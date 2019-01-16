(defproject obelisk-exporter "0.1.0"
  :description "A prometheus exporter for the obelisk ui."
  :url "https://github.com/akeboshiwind/obelisk-exporter"
  :license {:name "MIT"}
  :dependencies [[org.clojure/clojure "1.10.0"]

                 ;; Command line args
                 [org.clojure/tools.cli "0.4.1"]

                 ;; Metrics gathering
                 [obelisk-ui "0.1.1"]
                 [iapetos "0.1.8"]

                 ;; Configuration
                 [environ "1.1.0"]
                 [io.forward/yaml "1.0.9"]

                 ;; REST API
                 [bidi "2.1.5"]
                 [ring/ring-core "1.7.1"]
                 [ring/ring-jetty-adapter "1.7.1"]

                 ;; Logging
                 [org.clojure/tools.logging "0.4.1"]
                 [org.slf4j/slf4j-log4j12 "1.7.25"]]
  :main ^:skip-aot obelisk-exporter.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
