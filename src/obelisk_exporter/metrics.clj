(ns obelisk-exporter.metrics
  (:require [clojure.set :refer [rename-keys]]))


;;;; --- :hashrateData metrics --- ;;;;

(defn hash-rates
  [dashboard]
  (->> dashboard
       :hashrateData
       last
       (reduce (fn [acc [k v]]
                 (if-let [match (re-find #"Board (.*)" (name k))]
                   (let [board-id (Integer/parseInt (last match))]
                     (conj acc {:board board-id :hash-rate v}))
                   acc))
               [])))


;;;; --- :systemInfo metrics --- ;;;;

(defn- system-info
  [dashboard]
  (->> dashboard
       :systemInfo
       (reduce (fn [acc {:keys [name value]}]
                 (assoc acc name value))
               {})))

(defn free-memory
  [dashboard]
  (-> dashboard
      system-info
      (get "Free Memory")
      Float/parseFloat))

(defn total-memory
  [dashboard]
  (-> dashboard
      system-info
      (get "Total Memory")
      Float/parseFloat))


;;;; --- :poolStatus metrics --- ;;;;

(defn last-share-times
  [dashboard]
  (->> dashboard
       :poolStatus
       (map #(rename-keys % {:lastShareTime :last-share-time}))
       (map #(select-keys % [:pool :last-share-time]))))

(defn share-count
  [dashboard]
  (->> dashboard
       :poolStatus
       (map #(select-keys % [:pool :accepted :rejected]))
       (map (fn [pool]
              (let [{:keys [accepted rejected]} pool
                    pool (dissoc pool :accepted :rejected)]
                [(-> pool
                     (assoc :status "accepted")
                     (assoc :value accepted))
                 (-> pool
                     (assoc :status "rejected")
                     (assoc :value rejected))])))
       flatten))


;;;; --- :hashboardStatus metrics --- ;;;;

(defn- hash-board-status
  [dashboard]
  (->> dashboard
       :hashboardStatus
       (map #(assoc %2 :board %1) (drop 1 (range)))))

(defn power-supply-temp
  [dashboard]
  (->> dashboard
       hash-board-status
       (map #(rename-keys % {:powerSupplyTemp :power-supply-temp}))
       (map #(select-keys % [:board :power-supply-temp]))))

(defn num-cores
  [dashboard]
  (->> dashboard
       hash-board-status
       (map #(rename-keys % {:numCores :num-cores}))
       (map #(select-keys % [:board :num-cores]))))

(defn board-temp
  [dashboard]
  (->> dashboard
       hash-board-status
       (map #(rename-keys % {:boardTemp :board-temp}))
       (map #(select-keys % [:board :board-temp]))))

(defn chip-temp
  [dashboard]
  (->> dashboard
       hash-board-status
       (map #(rename-keys % {:chipTemp :chip-temp}))
       (map #(select-keys % [:board :chip-temp]))))

(defn hot-chip-temp
  [dashboard]
  (->> dashboard
       hash-board-status
       (map #(rename-keys % {:hotChipTemp :hot-chip-temp}))
       (map #(select-keys % [:board :hot-chip-temp]))))

(defn board-share-count
  [dashboard]
  (->> dashboard
       hash-board-status
       (map #(select-keys % [:board :accepted :rejected]))
       (map (fn [board]
              (let [{:keys [accepted rejected]} board
                    board (dissoc board :accepted :rejected)]
                [(-> board
                     (assoc :status "accepted")
                     (assoc :value accepted))
                 (-> board
                     (assoc :status "rejected")
                     (assoc :value rejected))])))
       flatten))

(defn num-chips
  [dashboard]
  (->> dashboard
       hash-board-status
       (map #(rename-keys % {:numChips :num-chips}))
       (map #(select-keys % [:board :num-chips]))))

(defn fan-speeds
  [dashboard]
  (->> dashboard
       hash-board-status
       first
       (reduce (fn [acc [k v]]
                 (if-let [match (re-find #"fanSpeed(.*)" (name k))]
                   (let [fan-id (Integer/parseInt (last match))]
                     (conj acc {:fan fan-id :fan-speed v}))
                   acc))
               [])))
