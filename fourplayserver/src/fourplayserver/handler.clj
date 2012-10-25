(ns fourplayserver.handler
  (:require [compojure.handler :as handler]
            [compojure.route :as route]
            [compojure.core :refer :all]
            [fourplayserver.session :as session]
            [cheshire.core     :refer [generate-string parse-string]]))

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
  (ANY "/new-game" [] (to-json session/new-game))
  (ANY "/poll"     [] (to-json session/poll))
  (ANY "/move"     [] (to-json session/move))
  (route/resources "/")
  (route/not-found "Not Found"))

(def app (-> 
           app-routes
           handler/api
           wrap-exceptions
           wrap-headers))
