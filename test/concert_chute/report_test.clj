(ns concert-chute.report-test
  (:require [clojure.test :refer :all]
            [concert-chute.report :refer :all])
  (:require java-time))

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
