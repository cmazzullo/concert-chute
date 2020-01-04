(ns concert-chute.core-test
  (:require [concert-chute.report-test :refer [report-data-fixture expected-json-string]])
  (:require [clojure.test :refer :all])
  (:require [concert-chute.core :refer :all]))

(deftest test-report-http-response
  (let [response (json-response-report report-data-fixture)]
    (testing "Make sure that the response has the correct content type for JSON"
      (is (= "text/javascript" (get (:headers response) "Content-Type"))))
    (testing "Make sure that the content is correct"
      (is (= expected-json-string (:body response))))))
