(ns fourplayserver.main
  (:require [fourplayserver.handler :refer [app]]
            [fourplayserver.websocket :as socket]
            [ring.adapter.jetty :refer [run-jetty]]))

(defn -main
  []
  (println "Starting server on port 3000..")
  (future (run-jetty app {:port 3000}))
  (println "Starting web socket on port 8080")
  (socket/start 8080))