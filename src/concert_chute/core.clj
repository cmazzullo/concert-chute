(ns concert-chute.core
  (:require [concert-chute [io :as io] [report :as report]])
  (:require [clojure.string :as str])
  (:require [clojure.tools.cli :refer [parse-opts]])
  (:require [ring.util.response :refer [response content-type]])
  (:require [ring.adapter.jetty :as jetty])
  (:require [cheshire.core :as cheshire])
  (:gen-class))

;; Teporary definitions



(defn -main
  [& args]
  (let [search-terms {"location" "Washington+DC"
                       "category" "music"
                      "date" "2019112600-2019122600"}
        data (io/download-events search-terms)
        report-data (report/generate-report-data data)
        ;; report-data (read-string (slurp "report-data.txt"))
        sorted-report (report/sort-report-by-datetime report-data)
        html (report/html-report sorted-report)]
    ;; Note: don't use the "sort_order" term, it causes the response to be limited to 100
    ;; results (I assume this is a bug in the API)
    (report/pretty-print-report sorted-report)
    ;; (spit "report-data.txt" report-data)))
    ;; (spit "report.html" html)
    (println html)))

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
  (content-type (response "Hello world")) "text/javascript")

(defn -main [& args]
  (jetty/run-jetty handler {:port 3000}))
