(ns concert-chute.report-test
  (:require [clojure.test :refer :all]
            [concert-chute.report :refer :all])
  (:require java-time)
  (:require [cheshire.core :as cheshire]))

(deftest test-datetime-sort
  (testing "Test our datetime-sorting method"
    (let [report-data [{:start_time (java-time/local-date-time "2019-11-20T14:01")},
                       {:start_time (java-time/local-date-time "2019-10-20T14:01")},
                       {:start_time (java-time/local-date-time "2019-09-20T01:00")},
                       {:start_time (java-time/local-date-time "2019-11-20T01:01")}]]
      (is (= (sort-report-by-datetime report-data)
             [{:start_time (java-time/local-date-time "2019-09-20T01:00")},
              {:start_time (java-time/local-date-time "2019-10-20T14:01")},
              {:start_time (java-time/local-date-time "2019-11-20T01:01")},
              {:start_time (java-time/local-date-time "2019-11-20T14:01")}])))))


(def report-data-fixture [{:title "Fuzion Fridays OPEN BAR",
                           :venue_name "Cafe Asia DC",
                           :start_time "2019-11-29 21:00:00"}
                          {:title "Light Orchestra",
                           :venue_name "Blues Alley",
                           :start_time "2019-12-23 22:00:00"}])

(def expected-json-string (str "[{\"title\" \"Fuzion Fridays OPEN BAR\", \"venue_name\" "
                               "\"Cafe Asia DC\", \"start_time\" \"2019-11-29 21:00:00\"}, "
                               "{\"title\" \"Light Orchestra\", \"venue_name\" \"Blues Alley\", "
                               "\"start_time\" \"2019-12-23 22:00:00\"}]"))

(deftest test-report-output-formatting
  (let [report-data report-data-fixture]
    (testing "test our report's JSON formatting"
      (is (= (json-report expected-json-string))))))
