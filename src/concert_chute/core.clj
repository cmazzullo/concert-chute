(ns concert-chute.core
  (:require [clojure.string :as str])
  (:require [clojure.tools.cli :refer [parse-opts]])
  (:require concert-chute.io)
  (:gen-class))


;; Teporary definitions
(def search-terms {"location" "Washington+DC"
                   "when" "this+month"
                   "category" "music"
                   "sort_order" "date"})

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

(defn pretty-print-report-str2
  [report-data]
  (str/join "\n\n" ;; separate each event with a blank line
            (map #(:title %) report-data)))


(defn pretty-print-report
  [report-data]
  (println (str "event count: " (count report-data)))
  (println (pretty-print-report-str2 report-data)))


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

(defn -main
  [& args]
  (let [data (concert-chute.io/download-events search-terms)

        ;; data (read-string (slurp "output.txt"))
        report-data (generate-report-data data)]
    ;; (spit "output.txt" data)
    (pretty-print-report report-data)))
