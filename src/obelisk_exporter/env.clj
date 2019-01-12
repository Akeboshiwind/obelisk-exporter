(ns obelisk-exporter.env
  (:require [environ.core :refer [env]]))

(def obelisk-server-address (env :obelisk-server-address))
(assert obelisk-server-address "OBELISK_SERVER_ADDRESS must be supplied")

(def basic-auth-user (env :basic-auth-user))
(def basic-auth-password (env :basic-auth-password))

(def basic-auth
  (when (and basic-auth-user basic-auth-password)
    [basic-auth-user basic-auth-password]))

(def obelisk-panel-user (or (env :obelisk-panel-user) "admin"))
(def obelisk-panel-password (or (env :obelisk-panel-password) "admin"))
