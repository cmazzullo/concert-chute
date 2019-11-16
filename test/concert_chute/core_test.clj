(ns concert-chute.core-test
  (:require [clojure.test :refer :all]
            [concert-chute.core :refer :all]))

(deftest test-event-data-munging
  (testing "helper function extracts event data into a map"
    (let [input [{:tag :url, :attrs nil, :content ["example.com"]},
                 {:tag :title, :attrs nil, :content ["My Title"]}]]
      (is (= (extract-event-content input)
             {:url "example.com", :title "My Title"}))))

  (testing "clean-event-data processes raw data correctly"
    (let [raw-data  {:tag :search,
                     :content [{:tag :metadata1, :content [0]}
                               {:tag :events, :content [{:tag :event, :content [{:tag :title, :content ["e1"]}]},
                                                        {:tag :event, :content [{:tag :title, :content ["e2"]},
                                                                                {:tag :url, :content ["x.y"]}]}]}]}]
      (is (= (clean-event-data raw-data)
             [{:title "e1"}
              {:title "e2", :url "x.y"}])))))
