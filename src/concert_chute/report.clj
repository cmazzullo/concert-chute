(ns concert-chute.report
  (:require [clojure.string :as str])
  (:require java-time)
  (:require [cheshire.core :as cheshire])
  (:require [ring.util.response :refer [response content-type]]))

;; Report output
;;
;; This is where we create reports using the clean data.

(def output-fields [:title
                    ;; :url
                    :venue_name
                    :start_time])

(defn generate-report-data
  "Subset the data to the fields specified in `output-fields`"
  [clean-data]
  (mapv #(select-keys % output-fields) clean-data))

(defn api-timestamp-to-datetime
  [timestamp]
  (java-time/local-date-time "y-M-d k:m:s" timestamp))

(defn date-to-query-timestamp
  [date]
  (java-time/format "yyyyMMdd00" date))

(defn convert-report-datetimes
  [report-data]
  (mapv #(assoc % :start_time (api-timestamp-to-datetime (:start_time %))) report-data))

(defn sort-report-by-datetime
  [report-data]
  (sort-by #(:start_time %) report-data))


;; JSON output

(defn json-report
  [report-data]
  (cheshire/generate-string report-data))
