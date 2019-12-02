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

(deftest test-search-terms-from-json
  (testing "A flat map should return search terms"
    (is (= (search-terms-from-json "{\"title\": \"Necronomicon\", \"author\": \"Abdul\"}")
           {"title" "Necronomicon", "author" "Abdul"})))
  (testing "is-flat-map-of-strings utility function"
    (is (is-flat-map-of-strings {"a" "b", "c" "d"}))
    (is (not (is-flat-map-of-strings {"a" "b", "c" ["d" "e"]})))
    (is (not (is-flat-map-of-strings ["a" "b" "c" ["d" "e"]])))
    (is (not (is-flat-map-of-strings {"a" "b", ["c" "d"] "e"}))))
  (testing "Anything but a flat map should crash"
    (is (thrown? Exception
                 (search-terms-from-json
                  "{\"title\": \"Necronomicon\", \"author\": [\"Abdul\", \"A.\"]}")))))


(deftest test-handler
  (let [request {:server-port 12345
                 :server-name "examplename"
                 :remote-addr "192.168.0.1"
                 :uri "ex.com"
                 :scheme :http
                 :request-method :post
                 :headers {}
                 :body "{}"}]
    (testing "If 'debug' is one of the search terms, use `debug-api-fn`"
      (let [debug-request (assoc request :body "{\"debug\": \"true\"}")]
        (is (= (:body (handler debug-request)) "DEBUG"))))))
