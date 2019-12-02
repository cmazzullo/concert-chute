(ns concert-chute.core
  (:require [concert-chute [io :as io] [report :as report]])
  (:require [ring.util.response :refer [response content-type]])
  (:require [ring.adapter.jetty :as jetty])
  (:require [cheshire.core :as cheshire])
  (:gen-class))

(defn all-type?
  [atype alist]
  (every? #(= (type %) atype) alist))

(defn is-flat-map-of-strings
  [object]
  (and (= (type object) clojure.lang.PersistentArrayMap) ; the object must be a map
       (all-type? java.lang.String (keys object))
       (all-type? java.lang.String (vals object))))

(defn search-terms-from-json
  [json-string]
  (let [parsed-json (cheshire/parse-string json-string)]
    (if (is-flat-map-of-strings parsed-json)
      parsed-json
      (throw
       (Exception. "Search term input JSON not formatted correctly - must be a flat map")))))

(defn json-response-report
  [report-data]
  (-> report-data
      (report/json-report)
      (response)
      (content-type "text/javascript")))

(defn handler [request]
  (if (= (:request-method request) :post)
    (let [search-terms (search-terms-from-json (:body request))]
      (if (get search-terms "debug") (response "DEBUG")
          (let [data (io/download-events search-terms)
                report-data (report/generate-report-data data)
                json-data (report/json-report report-data)]
            (content-type (response "Hello world") "text/javascript"))))
    (response "Please use POST")))

(defn -main [& args]
  (jetty/run-jetty handler {:port 3000}))
