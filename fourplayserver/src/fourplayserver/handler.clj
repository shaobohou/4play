(ns fourplayserver.handler
  (:require [compojure.handler :as handler]
            [compojure.route :as route]
            [compojure.core :refer :all]
            [fourplayserver.tournament :as tournament]
            [cheshire.core     :refer [generate-string parse-string]])
  (:import [net.swiftkey.fourplay.bots Bots IdiotPlayer RandomPlayer MinimaxPlayer]))

(def available-bots (keys net.swiftkey.fourplay.bots.Bots/sBots))

(defn wrap-exceptions [app]
  (fn [req] (try (app req)
                (catch IllegalArgumentException err
                  {:status 400 :body (.getMessage err)})
                (catch Exception err
                  {:status 500 :body (.getMessage err)}))))

(defn wrap-headers [app]
  (fn [req] (let [response (app req)]
             (merge response {:headers {"Access-Control-Allow-Headers" "X-Requested-With, Content-Type"
                                        "Access-Control-Allow-Methods" "GET, POST, OPTIONS"
                                        "Access-Control-Allow-Origin"  "*"}}))))
(defn to-json [f]
  (fn [req] 
    (generate-string (f (-> req :params)))))

(defroutes app-routes
  (ANY "/bots" [] (to-json (fn [args] available-bots)))
  (context 
    "/tournament" []
    (ANY "/join" [] (to-json tournament/join))
    (ANY "/reset" [] (to-json tournament/reset))
    (ANY "/players" [] (to-json tournament/players))
    (ANY "/state" [] (to-json tournament/state))
    (ANY "/start" [] (to-json tournament/start))
    (ANY "/poll" [] (to-json tournament/poll))
    (ANY "/move" [] (to-json tournament/move)))
  (route/resources "/")
  (route/not-found "Not Found"))

(def app (->
           app-routes
           handler/api
           wrap-exceptions
           wrap-headers))

(defn on-socket-message [connection json-message]
  (println json-message)
  (let [message (get (parse-string json-message) "message")]
    (cond
      (= "reset" message)
      (do (println "RESET") (tournament/reset {}))
      (= "state" message)
      (do (println "STATE") (tournament/state {}))
      (= "start" message)
      (do (println "START") (tournament/start {}))
      (= "add-player" (get message "type"))
      (do (println "ADD PLAYER") (tournament/start-bot (get net.swiftkey.fourplay.bots.Bots/sBots (get message "bot")) (str (get message "bot") (rand-int 10000)))))))
