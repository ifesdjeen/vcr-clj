(ns vcr-clj.recorder-test
  (:use clojure.test
        vcr-clj.recorder))

(deftest record-request-test
  (let [store (vcr-memory-store)]
    (record-request! store :k "value")
    (is (= (get-request store :k) "value"))))

(deftest request-recorded-test
  (let [store (vcr-memory-store)]
    (record-request! store :k "value")
    (is (request-recorded? store :k))))

(deftest request-recorded-test
  (let [store (vcr-memory-store)]
    (record-request! store :k "value")
    (clear-recorded! store)
    (is (= @(get-all store) {}))))
