(defproject fourplayserver "0.1.0-SNAPSHOT"
  
  :description "FIXME: write description"
  
  :url "http://example.com/FIXME"
  
  :dependencies [[ring/ring-jetty-adapter "1.1.6"]
                 [org.clojure/clojure "1.4.0"]
                 [compojure "1.1.1"]
                 [net.swiftkey.fourplay/fourplayclient "1.0-SNAPSHOT"]
                 [cheshire "4.0.1"]
                 [com.google.code.gson/gson "2.2.2"]
                 [org.apache.httpcomponents/httpclient-cache "4.2.2"]
                 [org.apache.httpcomponents/httpmime "4.2.2"]
                 [org.apache.httpcomponents/httpcore "4.2.2"]
                 [org.apache.httpcomponents/httpclient "4.2.2"]
                 [org.webbitserver/webbit "0.4.3"]
                 [org.clojure/data.json "0.1.2"]]
  
  :main fourplayserver.main
  
  :plugins [[lein-ring "0.7.1"]]
  
  :ring {:handler fourplayserver.handler/app}
  
  :java-source-paths ["../java/fourplayclient/src/main"]
  
  :profiles {:dev {:dependencies [[midje "1.4.0"]]
                   :plugins [[lein-midje "1.0.8"]
                             [com.github.robertrolandorg/lein-eclipse "1.1.0"]]}})
