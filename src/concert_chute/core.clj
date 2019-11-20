(ns concert-chute.core
  (:require [clojure.string :as str])
  (:require [clojure.tools.cli :refer [parse-opts]])
  (:require concert-chute.io)
  (:require java-time)
  (:gen-class))


;; Teporary definitions

;; Note: don't use the "sort_order" term, it causes the response to be limited to 100
;; results (I assume this is a bug in the API)
(def search-terms {"location" "Washington+DC"
                   "when" "this+month"
                   "category" "music"})

;; Report output
;;
;; This is where we create reports using the clean data.

(def output-fields [:title
                    ;; :url
                    :venue_name
                    :start_time
                    ])

(defn generate-report-data
  "Subset the data to the fields specified in `output-fields`"
  [clean-data]
  (mapv #(select-keys % output-fields) clean-data))

(defn pretty-print-event-str
  [event]
  (str/join \newline
            (cons
             ((first output-fields) event) ;; pull out first (primary) field
             (map #(str \tab (% event)) (rest output-fields)))))

(defn pretty-print-report-str
  [report-data]
   (str/join "\n\n" ;; separate each event with a blank line
             (map pretty-print-event-str report-data)))

(defn pretty-print-report
  [report-data]
  (println (str "event count: " (count report-data)))
  (println (pretty-print-report-str report-data)))


;; Need to fix this:
;; see https://github.com/clojure/tools.cli

;; (def cli-options
;;   [["-c" "--category CATEGORY" "Category of event to search"
;;     :default "music"]
;;    ["-l" "--location LOCATION" "Location to search"
;;     :default "Washington+DC"]])

;; (defn -main
;;   [& args]
;;   (parse-opts args cli-options))

(defn api-timestamp-to-datetime
  [timestamp]
  (java-time/local-date-time "y-M-d k:m:s" timestamp))

(defn convert-report-datetimes
  [report-data]
  (mapv #(assoc % :start_time (api-timestamp-to-datetime (:start_time %))) report-data))

(defn sort-report-by-datetime
  [report-data]
  (sort-by #(:start_time %) report-data))


(defn -main
  [& args]
  (let [;; data (concert-chute.io/download-events search-terms)
        ;; report-data (generate-report-data data)]

        report-data (read-string (slurp "report-data.txt"))
        sorted-report (sort-report-by-datetime report-data)]
    (pretty-print-report sorted-report)
    ;; (spit "report-data.txt" report-data)

    ;;(pretty-print-report report-data))))
))
