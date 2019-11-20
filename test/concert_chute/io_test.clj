(ns concert-chute.io-test
  (:require [clojure.test :refer :all]
            [concert-chute.io :refer :all]))

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
             {:url "example.com", :title "My Title"})))))
