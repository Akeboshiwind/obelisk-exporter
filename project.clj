(defproject obelisk-exporter "0.1.0"
  :description ""
  :url "https://github.com/akeboshiwind/obelisk-exporter"
  :license {:name "MIT"}
  :dependencies [[org.clojure/clojure "1.10.0"]

                 ;; Metrics gathering
                 [obelisk-ui "0.1.1"]
                 [iapetos "0.1.8"]

                 ;; Configuration
                 [environ "1.1.0"]

                 ;; REST API
                 [bidi "2.1.5"]
                 [ring/ring-core "1.7.1"]
                 [ring/ring-jetty-adapter "1.7.1"]]
  :main ^:skip-aot obelisk-exporter.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
