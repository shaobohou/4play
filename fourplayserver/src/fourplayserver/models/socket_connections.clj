(ns fourplayserver.models.socket-connections)

(defonce connections (atom []))

(defn add-connection
  [c]
  (println "Opened: " c)
  (swap! connections conj c)
  (println @connections))

(defn remove-connection
  [c]
  (println "Closed: " c)
  (swap! connections (fn [cs] (remove #(= % c) cs))))