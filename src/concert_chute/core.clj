(ns concert-chute.core
  (:require [clojure.string :as str])
  (:require clojure.xml)
  (:require [clojure.tools.cli :refer [parse-opts]])
  (:gen-class))

;; Long-term definitions
(def base-url "https://api.eventful.com/rest/events/search?")
(def app-key (str/trim (slurp "./app_key")))


;; Teporary definitions
(def search-terms {"location" "Washington+DC"
                   "date" "2019111600-2019121600"
                   "category" "music"})


(defn construct-querystring
  "Turn a map of format term_name: term_value into a URL querystring."
  [term-map]
  (str/join "&" (for [[term value] (seq term-map)]
                  (str term "=" value))))


;; Fetching raw concert data
;;
;; This is converted from XML to clojure data, but that is the ONLY
;; processing that has been done! This is pure I/O with the outside world.
(defn query-concerts
  "Download all events matching search-terms from XML REST API."
  [search-terms]
  (let [all-terms (assoc search-terms "app_key" app-key)
        querystring (construct-querystring all-terms)
        search-url (str base-url querystring)]
    (clojure.xml/parse search-url)))

(def output-fname "output.txt")

(defn dump-concerts
  "Dump concert data to a file."
  [concert-data]
  (spit output-fname concert-data))

(defn load-concerts
  "Read concert data from a file."
  []
  (read-string (slurp output-fname)))

;; Munging concert data
;;
;; This takes raw data from methods like query-concerts and load-concerts
;; and processes it to be a simple list of maps. This is the format that we
;; use exclusively in the rest of the program!!!

(defn extract-event-list
  "Pull out event data from the deeply-nested raw API output."
  [raw-data]
  (map #(:content %)
       (:content
        (first
         (filter #(= (:tag %) :events) (:content raw-data))))))

(defn extract-event-content
  "Extract the content of an event from a piece of input data.

  Data comes in in a pretty ugly format - a list of attributes in the format:
  [{:tag :url, :attrs nil, :content [\"example.com\"]},
   {:tag :title, :attrs nil, :content [\"My Title\"]}]

  We want to turn this into a nice clean map like this:
  {:url \"example.com\", :title \"My Title\"}
  "
  [event-data]
    (zipmap
     (map #(:tag %) event-data)
     (map #(first (:content %)) event-data)))

(defn clean-event-data
  "Produces a useful list of event data from the raw API data.

  Input format:
  {:tag :search,
   :content [{:tag :metadata1, :content [0]}
     {:tag :events, :content [
        {:tag :event, :content [{:tag :title, :content [\"e1\"]}]},
        {:tag :event, :content [{:tag :title, :content [\"e2\"]},
                                {:tag :url, :content [\"x.y\"]}]}]}]}

  Output format:
  [{:title \"e1\"}
   {:title \"e2\", :url \"x.y\"}]
  "
  [raw-data]
  (let [event-list (extract-event-list raw-data)]
    (for [event event-list] (extract-event-content event))))


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
  (map #(select-keys % output-fields) clean-data))

(defn pretty-print-event
  [event]
  (str/join \newline
            (cons
             ((first output-fields) event) ;; pull out first (primary) field
             (map #(str \tab (% event)) (rest output-fields)))))

(defn pretty-print-report
  [report-data]
  (println
   (str/join "\n\n" ;; separate each event with a blank line
             (map pretty-print-event report-data))))
             ;; (for [event report-data]
             ;;   (pretty-print-event event)))))

;; Need to fix this:
;; see https://github.com/clojure/tools.cli

(def cli-options
  [["-c" "--category CATEGORY" "Category of event to search"
    :default "music"]
   ["-l" "--location LOCATION" "Location to search"
    :default "Washington+DC"]])

;; (defn -main
;;   [& args]
;;   (parse-opts args cli-options))


(defn -main
  [& args]
  (let [;; concert-data (load-concerts)
        concert-data (query-concerts search-terms)
        clean-data (clean-event-data concert-data)
        report-data (generate-report-data clean-data)]
    (dump-concerts concert-data)
    (pretty-print-report report-data)))
