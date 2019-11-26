(ns concert-chute.report
  (:require [clojure.string :as str])
  (:require java-time))

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


;; HTML output

(defn html-cell
  [cell-data html-tag]
  (str "    <" html-tag ">" cell-data "</" html-tag ">"))

(defn html-data-cell [cell-data] (html-cell cell-data "td"))
(defn html-header-cell [col-name] (html-cell col-name "th"))

(defn html-report-row
  [report-row]
  (str
   "  <tr>\n"
   (str/join \newline (map #(html-data-cell (% report-row)) output-fields))
   "\n  </tr>"))

(defn html-header-row
  [output-fields]
  (str
   "  <tr>\n"
   (str/join \newline (map html-header-cell output-fields))
   "\n  </tr>\n"))

(defn html-report
  [report-data]
  (str
   "<table>\n"
   (html-header-row output-fields)
   (str/join \newline (map html-report-row report-data))
   "\n</table>"))


;; Text-based output

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
