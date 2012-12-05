(ns fourplayserver.main
  (:require [fourplayserver.handler :refer [app]]
            [ring.adapter.jetty :refer [run-jetty]]
            [clojure.string :as s]
            [fourplayserver.handler :refer [on-socket-message]]
            [fourplayserver.models.socket-connections :refer [add-connection remove-connection]])
  (:import [org.webbitserver WebServer WebServers WebSocketHandler]
           [org.webbitserver.handler StaticFileHandler]))

(defn socket-start [port]
  (doto (WebServers/createWebServer port)
    (.add "/websocket"
      (proxy [WebSocketHandler] []
        (onOpen [c] (add-connection c))
        (onClose [c] (remove-connection c))
        (onMessage [c j] (on-socket-message c j))))
    (.add (StaticFileHandler. "."))
    (.start)))

(defn -main
  []
  (println "Starting server on port 3000..")
  (future (run-jetty app {:port 3000}))
  (println "Starting web socket on port 8080")
  (socket-start 8080))