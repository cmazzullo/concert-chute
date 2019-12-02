(ns concert-chute.io
  (:require [clojure.string :as str])
  (:require clojure.xml))

;; Fetching raw concert data
;;
;; This is converted from XML to clojure data, but that is the ONLY
;; processing that has been done! This is pure I/O with the outside world.

;; Long-term definitions

(defn construct-querystring
  "Turn a map of format term_name: term_value into a URL querystring."
  [term-map]
  (str/join "&" (for [[term value] (seq term-map)]
                  (str term "=" value))))

(defn eventful-api-fn
  "Pulls data from the API at api.eventful.com"
  [search-terms]
  (let [base-url "https://api.eventful.com/rest/events/search?"
        app-key (str/trim (slurp "./app_key"))
        all-terms (assoc search-terms "app_key" app-key)
        querystring (construct-querystring all-terms)
        search-url (str base-url querystring)]
    (clojure.xml/parse search-url)))

(defn query-api
  "Queries an API using the function `api-fn` with `search-terms`."
  [search-terms]
  (eventful-api-fn search-terms))

(defn get-content-from-attr
  [attr]
  (first (:content attr)))

(defn pull-attr-from-list
  [tag attr-list]
  (first (filter #(= (:tag %) tag) attr-list)))

(defn get-page-count
  [raw-data]
  (let [attr-list (:content raw-data)]
    (Integer/parseInt
     (get-content-from-attr (pull-attr-from-list :page_count attr-list)))))

(defn download-page
  [search-terms page-number]
  (query-api (assoc search-terms "page_number" page-number)))

(defn download-all-data
  "Download all events matching search-terms from XML REST API."
  [search-terms]
  (let [first-page (query-api search-terms)
        page-count (get-page-count first-page)]
    (cons first-page
          (map #(download-page search-terms %) (range 2 (inc page-count))))))

(defn query-total-items
  [search-terms]
  (let [attr-list
        (:content (query-api (assoc search-terms "count_only" "true")))]
    (Integer/parseInt
     (get-content-from-attr (pull-attr-from-list :total_items attr-list)))))


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

(defn clean-raw-page
  "Produces a useful list of event data from a raw API data page.

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
  [raw-page]
  (let [event-list (extract-event-list raw-page)]
    (for [event event-list] (extract-event-content event))))

(defn clean-full-raw-data
  [full-raw-data]
  (flatten (map clean-raw-page full-raw-data)))

(defn download-events
  "Our user-friendly function for pulling in data from the Eventful API."
  [search-terms]
  (vec (clean-full-raw-data (download-all-data search-terms))))
