(ns obelisk-exporter.core
  (:require [obelisk-ui.api :as api]
            [bidi.ring :refer [make-handler]]
            [ring.util.response :as res]
            [ring.adapter.jetty :refer [run-jetty]]
            [iapetos.core :as p]
            [iapetos.export :as e]
            [obelisk-exporter.metrics :as m]
            [obelisk-exporter.env :as env]))

;;;; --- Prometheus Metrics Registry --- ;;;;

(def registry
  (-> (p/collector-registry)
      (p/register
       ;; :hashrateData metrics
       (p/gauge :obelisk-exporter/hash-rate-gigahashes-per-second
                {:description "The total amount of hashrate each board has."
                 :labels [:board]})

       ;; :hashrateData metrics
       (p/gauge :obelisk-exporter/memory-free-megabytes
                {:description "The amount of free memory"})
       (p/gauge :obelisk-exporter/memory-total-megabytes
                {:description "The total amount of memory"})

       ;; :poolStatus metrics
       (p/gauge :obelisk-exporter/pool-last-share-time
                {:description "The last time a share was received"
                 :labels [:pool]})
       (p/gauge :obelisk-exporter/pool-share-count-total
                {:description "The amount of shares"
                 :labels [:pool :status]})

       ;; :hashboardStatus
       (p/gauge :obelisk-exporter/board-power-supply-temp-celsius
                {:description "The temperature of the board power supply"
                 :labels [:board]})
       (p/gauge :obelisk-exporter/board-num-cores-total
                {:description "The number of cores a board has available"
                 :labels [:board]})
       (p/gauge :obelisk-exporter/board-temp-celsius
                {:description "The temperature of the board"
                 :labels [:board]})
       (p/gauge :obelisk-exporter/board-chip-temp-celsius
                {:description "The temperature of the board chip"
                 :labels [:board]})
       (p/gauge :obelisk-exporter/board-hot-chip-temp-celsius
                {:description "The temperature of the board hot chip"
                 :labels [:board]})
       (p/gauge :obelisk-exporter/board-share-count-total
                {:description "The amount of shares a board has received"
                 :labels [:board :status]})
       (p/gauge :obelisk-exporter/board-num-chips-total
                {:description "The number of chips a board has available"
                 :labels [:board]})
       (p/gauge :obelisk-exporter/fan-speed-rmp
                {:description "The speed of the fans"
                 :labels [:fan]}))))


;;;; --- Handlers --- ;;;;

(defn index-handler
  [_]
  (res/response
   "<body>Metrics are available <a href=\"/metrics\">here</a>.<br>More information about this exporter is available <a href=\"https://github.com/akeboshiwind/obelisk-exporter\">here</a>.</body>"))

(defn- server-error
  [body]
  {:status  500
   :headers {}
   :body    body})

(defn- set-metrics
  [registry metric value-key data]
  (doseq [data-point data]
    (let [value (value-key data-point)]
      (p/set registry metric (dissoc data-point value-key) value)))
  registry)

(comment
  (def r
    (-> (p/collector-registry)
        (p/register
         (p/gauge :obelisk/test-total
                  {:labels [:label]}))))

  (set-metrics
   r
   :obelisk/test-total
   :value
   [{:value 1 :label 0}
    {:value 5 :label 1}
    {:value 1 :label 2}])

  (->> (e/text-format r)
       (print))

  [])

(defn metrics-handler
  [_]
  (let [opts (merge {:server-address env/obelisk-server-address}
                    (when env/basic-auth
                        {:basic-auth env/basic-auth}))]
    (if-let [cookie (api/login env/obelisk-panel-user
                               env/obelisk-panel-password
                               opts)]
      (let [opts (assoc opts :cookie cookie)
            d (api/dashboard opts)]
        (if-let [dashboard (api/dashboard opts)]
          (-> registry
              ;; :hashrateData metrics
              (set-metrics :obelisk-exporter/hash-rate-gigahashes-per-second
                           :hash-rate
                           (m/hash-rates dashboard))

              ;; :systemInfo metrics
              (p/set :obelisk-exporter/memory-free-megabytes (m/free-memory dashboard))
              (p/set :obelisk-exporter/memory-total-megabytes (m/total-memory dashboard))

              ;; :poolStatus metrics
              (set-metrics :obelisk-exporter/pool-last-share-time
                           :last-share-time
                           (m/last-share-times dashboard))
              (set-metrics :obelisk-exporter/pool-share-count-total
                           :value
                           (m/share-count dashboard))

              ;; :hashboardStatus metrics
              (set-metrics :obelisk-exporter/board-power-supply-temp-celsius
                           :power-supply-temp
                           (m/power-supply-temp dashboard))
              (set-metrics :obelisk-exporter/board-num-cores-total
                           :num-cores
                           (m/num-cores dashboard))
              (set-metrics :obelisk-exporter/board-temp-celsius
                           :board-temp
                           (m/board-temp dashboard))
              (set-metrics :obelisk-exporter/board-chip-temp-celsius
                           :chip-temp
                           (m/chip-temp dashboard))
              (set-metrics :obelisk-exporter/board-hot-chip-temp-celsius
                           :hot-chip-temp
                           (m/hot-chip-temp dashboard))
              (set-metrics :obelisk-exporter/board-share-count-total
                           :value
                           (m/board-share-count dashboard))
              (set-metrics :obelisk-exporter/board-num-chips-total
                           :num-chips
                           (m/num-chips dashboard))
              (set-metrics :obelisk-exporter/fan-speed-rmp
                           :fan-speed
                           (m/fan-speeds dashboard))

              ;; rendering
              e/text-format
              res/response)
          (server-error
           "Server Error: Failed to get a decent response from the obelisk ui.")))
      (server-error
       "Server Error: Misconfigured, please check configuration."))))

(defn not-found
  [_]
  (res/not-found "404 Not Found"))

(def routes
  ["" [["" index-handler]
       ["/" index-handler]
       ["/metrics" [["" metrics-handler]
                    ["/" metrics-handler]]]
       [true not-found]]])

(def handler
  (make-handler routes))


;;;; --- Main --- ;;;;

(defn -main
  [& args]
  (let [server (run-jetty handler {:port 3000
                                   :join? false})]
    (fn []
      (.stop server))))

(comment

  (def opts {:server-address "http://some-obelisk-address"
             :basic-auth ["username" "password"]})

  (def cookie (api/login "admin" "admin" opts))

  (def opts (assoc opts :cookie cookie))

  (def d (api/dashboard opts))

  (keys d)

  ;; The :hashrateData `is` sorted
  (->> (:hashrateData d)
       (map :time)
       (reduce (fn [a b]
                 (if (> b a)
                   b
                   (reduced false)))))

  ;; ::hash-rate-total
  (->> (:hashrateData d)
       (last)
       (clojure.pprint/pprint))

  (->> (:systemInfo d)
       (reduce (fn [acc {:keys [name value]}]
                 (assoc acc name value))
               {})
       (clojure.pprint/pprint))


  (->> (:poolStatus d)
       (clojure.pprint/pprint))

  (->> (:hashboardStatus d)
       (clojure.pprint/pprint))


  [])
