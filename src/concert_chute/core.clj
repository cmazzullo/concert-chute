(ns concert-chute.core
  (:require [concert-chute [io :as io] [report :as report]])
  (:require [ring.util.response :refer [response content-type]])
  (:require [ring.adapter.jetty :as jetty])
  (:require [ring.middleware.reload :refer [wrap-reload]])
  (:gen-class))

(defn json-response-report
  [report-data]
  (-> report-data
      (report/json-report)
      (response)
      (content-type "text/javascript")))

(def search-terms {"date" "2019112600-2019122600"
                   "category" "music"
                   "location" "washington+dc"})

(defn handler [request]
  (let [data (io/download-events search-terms)
          report-data (report/generate-report-data data)
          json-data (report/json-report report-data)]
      (content-type (response json-data) "text/javascript")))

(defn -main [& args]
  (jetty/run-jetty (wrap-reload #'handler) {:host "localhost" :port 3000}))
