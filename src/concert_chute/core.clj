(ns concert-chute.core
  (:require [clojure.string :as str])
  (:require clojure.xml)
  (:require [clojure.tools.cli :refer [parse-opts]])
  (:gen-class))

(def base-url "https://api.eventful.com/rest/events/search?")
(def app-key (str/trim (slurp "./app_key")))

(defn construct-querystring
  "Turn a map of format term_name: term_value into a URL querystring."
  [term-map]
  (str/join "&" (for [[term value] (seq term-map)]
                  (str term "=" value))))

(defn query-concerts
  "Download all events matching search-terms from XML REST API."
  [search-terms]
  (let [all-terms (assoc search-terms "app_key" app-key)
        querystring (construct-querystring all-terms)
        search-url (str base-url querystring)]
    (clojure.xml/parse search-url)))


;; (let [search-terms {"location" "Washington+DC"
;;                     "date" "2019111400-2019111500"
;;                     "category" "music"}
;;       query-result (query-concerts search-terms)]
;;   (println (:content query-result)))

;; Need to fix this:
;; see https://github.com/clojure/tools.cli

(def cli-options
  [["-c" "--category CATEGORY" "Category of event to search"
    :default "music"]
   ["-l" "--location LOCATION" "Location to search"
    :default "Washington+DC"]])

(defn -main
  [& args]
  (parse-opts args cli-options))
