(ns concert-chute.core
  (:require [concert-chute io report])
  (:require [clojure.string :as str])
  (:require [clojure.tools.cli :refer [parse-opts]])
  (:gen-class))

;; Teporary definitions



(defn -main
  [& args]
  (let [data (concert-chute.io/download-events search-terms)
        report-data (concert-chute.report/generate-report-data data)
        ;; report-data (read-string (slurp "report-data.txt"))
        sorted-report (concert-chute.report/sort-report-by-datetime report-data)
        html (concert-chute.report/html-report sorted-report)]
    ;; Note: don't use the "sort_order" term, it causes the response to be limited to 100
    ;; results (I assume this is a bug in the API)
    (def search-terms {"location" "Washington+DC"
                       "category" "music"
                       "date" "2019112600-2019122600"})
    (concert-chute.report/pretty-print-report sorted-report)
    ;; (spit "report-data.txt" report-data)))
    ;; (spit "report.html" html)
    (println html)))
