(ns vcr-clj.core-test
  (:require [clj-http.client :as client]
            [vcr-clj.recorder :as r])
  (:use clojure.test
        vcr-clj.core))

(deftest wrap-fn-test
  (testing "Replace function (arity 0) with a different implementation"
    (defn wrap-fn-original
      []
      "wrap-fn-original-return-value")

    (is (= "wrap-fn-original-return-value" (wrap-fn-original)))


    (wrap-fn #'wrap-fn-original
             (fn [caller-arguments original-fn]
               "wrap-fn-replaced-return-value"))

    (is (= "wrap-fn-replaced-return-value" (wrap-fn-original)))

    (unwrap-fn #'wrap-fn-original)

    (is (= "wrap-fn-original-return-value" (wrap-fn-original))))

  (testing "Replace function (arity 1) with a different implementation"
    (defn wrap-fn-original-arity-1
      [a]
      (format "original-value-is-%s" a))

    (is (= "original-value-is-aaa" (wrap-fn-original-arity-1 "aaa")))

    (wrap-fn #'wrap-fn-original-arity-1
             (fn [caller-arguments original-fn]
               (format "changed-value-is-%s" (first caller-arguments))))

    (is (= "changed-value-is-aaa" (wrap-fn-original-arity-1 "aaa"))))

  (testing "Replace function (arity 1 and optional keys) with a different implementation"
    (defn wrap-fn-original-arity-1-and-optional-keys
      [a &{:keys [b c d]}]
      (format "original-values-for-a-%s-b-%d-c-%d-d-%d" a b c d))

    (is (= "original-values-for-a-aaa-b-1-c-2-d-3" (wrap-fn-original-arity-1-and-optional-keys "aaa" :b 1 :c 2 :d 3)))

    (wrap-fn #'wrap-fn-original-arity-1-and-optional-keys
             (fn [[a & {:keys [b c d]}] original-fn]
               (format "changed-values-for-a-%s-b-%d-c-%d-d-%d" a b c d)))

    (is (= "changed-values-for-a-aaa-b-1-c-2-d-3" (wrap-fn-original-arity-1-and-optional-keys "aaa" :b 1 :c 2 :d 3))))



  (testing "Chain function (arity 0) with a different implementation"
    (defn wrap-fn-original
      []
      "wrap-fn-original-return-value")

    (is (= "wrap-fn-original-return-value" (wrap-fn-original)))

    (wrap-fn #'wrap-fn-original
             (fn [caller-arguments original-fn]
               (let [original-result (apply original-fn caller-arguments)]
                 (str original-result " " "wrap-fn-chained-return-value"))))

    (is (= "wrap-fn-original-return-value wrap-fn-chained-return-value" (wrap-fn-original))))

  (testing "Chain function (arity 1) with a different implementation"
    (defn wrap-fn-original-arity-1
      [a]
      (format "original-value-is-%s" a))

    (is (= "original-value-is-aaa" (wrap-fn-original-arity-1 "aaa")))

    (wrap-fn #'wrap-fn-original-arity-1
             (fn [caller-arguments original-fn]
               (let [original-result (apply original-fn caller-arguments)]
                 (str original-result " " (format "chained-value-is-%s" (first caller-arguments))))))

    (is (= "original-value-is-aaa chained-value-is-aaa" (wrap-fn-original-arity-1 "aaa"))))

  (testing "Chain function (arity 1 and optional keys) with a different implementation"
    (defn wrap-fn-original-arity-1-and-optional-keys
      [a &{:keys [b c d]}]
      (format "original-values-for-a-%s-b-%d-c-%d-d-%d" a b c d))

    (is (= "original-values-for-a-aaa-b-1-c-2-d-3" (wrap-fn-original-arity-1-and-optional-keys "aaa" :b 1 :c 2 :d 3)))

    (wrap-fn #'wrap-fn-original-arity-1-and-optional-keys
             (fn [[a & {:keys [b c d]} :as caller-arguments] original-fn]
               (let [original-result (apply original-fn caller-arguments)]
                 (str original-result " " (format "chained-values-for-a-%s-b-%d-c-%d-d-%d" a b c d)))))

    (is (= "original-values-for-a-aaa-b-1-c-2-d-3 chained-values-for-a-aaa-b-1-c-2-d-3" (wrap-fn-original-arity-1-and-optional-keys "aaa" :b 1 :c 2 :d 3)))))

(deftest ^{:network-dependent true :unpredictable true} asd
         (mock-clj-http!)
         (binding [r/*request-store* (r/vcr-memory-store)]
           (r/record! r/*request-store* {:url "http://google.com" :method :get} "result")
           (is (= "result" (client/get "http://google.com"))))
         (unmock-clj-http!)
         ;; (r/get r/*request-store* {:url "http://google.com" :method :get})
  )