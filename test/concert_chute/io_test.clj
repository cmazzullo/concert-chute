(ns concert-chute.io-test
  (:require [clojure.test :refer :all]
            [concert-chute.io :refer :all]))

(defn read-fixture
  "Read in fixture data as a string from a file in resources/fixtures/."
  [fixture-filename]
  (slurp (clojure.java.io/resource (str "fixtures/" fixture-filename))))

(defn read-raw-api-xml-fixture
  "Read in raw XML data pulled from the Eventful API to use as a fixture."
  [test]
  (def fixture-data-page-1 (read-fixture "data-page-1.xml"))
  (def fixture-data-page-2 (read-fixture "data-page-2.xml"))
  (test))

(use-fixtures :once read-raw-api-xml-fixture)


(deftest test-event-data-munging
  (testing "Test the cleaning of the full raw data output (i.e. from download-all-data)"
    (let [raw-data [{:tag :search,
                     :content [{:tag :metadata1, :content [0]}
                               {:tag :events,
                                :content [{:tag :event, :content [{:tag :title, :content ["e1"]}]},
                                          {:tag :event, :content [{:tag :title, :content ["e2"]},
                                                                  {:tag :url, :content ["x.y"]}]}]}]}
                    {:tag :search,
                     :content [{:tag :metadata1, :content [1]}
                               {:tag :events,
                                :content [{:tag :event, :content [{:tag :title, :content ["e3"]}]}]}]}
                    ]
          raw-page (first raw-data)]
      (is (= (clean-raw-page raw-page)
             [{:title "e1"}
              {:title "e2", :url "x.y"}]))
    (is (= (clean-full-raw-data raw-data)
           [{:title "e1"}
            {:title "e2", :url "x.y"}
            {:title "e3"}]))))

  (testing "helper function extracts event data into a map"
    (let [input [{:tag :url, :attrs nil, :content ["example.com"]},
                 {:tag :title, :attrs nil, :content ["My Title"]}]]
      (is (= (extract-event-content input)
             {:url "example.com", :title "My Title"}))))

  (testing "fixtures are loaded correctly"
    (is (not= fixture-data-page-1 ""))))
