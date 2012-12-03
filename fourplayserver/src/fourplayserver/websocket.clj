(ns fourplayserver.websocket
  (:require [clojure.string :as s]
            [fourplayserver.handler :refer [on-socket-message]]
            [fourplayserver.models.socket-connections :refer [add-connection remove-connection]])
  (:import [org.webbitserver WebServer WebServers WebSocketHandler]
           [org.webbitserver.handler StaticFileHandler]))

(defn start [port]
  (doto (WebServers/createWebServer port)
    (.add "/websocket"
      (proxy [WebSocketHandler] []
        (onOpen [c] (add-connection c))
        (onClose [c] (remove-connection c))
        (onMessage [c j] (on-socket-message c j))))
    (.add (StaticFileHandler. "."))
    (.start)))