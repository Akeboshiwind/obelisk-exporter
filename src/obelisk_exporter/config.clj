(ns obelisk-exporter.config
  (:require [environ.core :as e]
            [yaml.core :as yaml]))

(def default-cfg
  {:general {:port 3000
             :server-address "0.0.0.0"}
   :obelisk-ui {:panel {:user "admin"
                        :password "admin"}}})

(def ^{:private true} cfg
  (atom default-cfg))

(defn config
  ([& ks]
   (get-in @cfg ks)))

(defn env->config
  [env]
  {:general {:port (env :port)
             :server-address (env :server-address)}
   :obelisk-ui {:server-address (env :obelisk-server-address)
                :panel {:user (env :obelisk-panel-user)
                        :password (env :obelisk-panel-password)}
                :basic-auth {:user (env :basic-auth-user)
                             :password (env :basic-auth-password)}}})

(defn remove-nils
  "remove pairs of key-value that has nil value from a (possibly nested) map. also transform map to nil if all of its value are nil"
  [nm]
  (clojure.walk/postwalk
   (fn [el]
     (if (map? el)
       (let [m (into {} (remove (comp nil? second) el))]
         (when (seq m)
           m))
       el))
   nm))

(defn deep-merge [v & vs]
  (letfn [(rec-merge [v1 v2]
            (if (and (map? v1) (map? v2))
              (merge-with deep-merge v1 v2)
              v2))]
    (if (some identity vs)
      (reduce #(rec-merge %1 %2) v vs)
      (last vs))))

(defn load!
  ([]
   (swap! cfg
          deep-merge
          (remove-nils (env->config e/env))))
  ([config-file]
   (swap! cfg
          deep-merge
          (remove-nils (yaml/from-file config-file))
          (remove-nils (env->config e/env)))))

(comment

  (->
   (yaml/from-file "obelisk-exporter.yml")
   (config :obelisk-ui :panel :user)
   (clojure.pprint/pprint))

  default-cfg

  (deep-merge
   default-cfg
   (remove-nils
    {:general {:port nil,
               :server-address nil},
     :obelisk-ui {:server-address "test"
                  :panel {:user nil,
                          :password nil},
                  :basic-auth {:user nil,
                               :password nil}}}))

  [])
