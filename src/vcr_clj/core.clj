(ns vcr-clj.core
  (:require [clj-http.client :as client]
            [clj-yaml.core :as yaml]
            [vcr-clj.recorder :as r]))

(defn wrap-fn
  "Wrap or replace some function with your own function"
  [qualifier wrapper]
  (alter-var-root
   qualifier
   (fn [original-fn]
     (with-meta
       (fn [& caller-arguments]
         (wrapper caller-arguments original-fn))
       (assoc (meta original-fn)
         :original original-fn)))))

(defn unwrap-fn
  [qualifier]
  (let [original (-> @qualifier meta :original)]
    (alter-var-root qualifier (constantly original))))

;; (defn persist-recorded!
;;   []
;;   (io!
;;    (spit "cassete.yml" (yaml/generate-string @recorded-requests))))

(defn- recorder
  [caller-arguments original-fn]
  (if-let [res (r/get r/*request-store* (first caller-arguments))]
    res
    (let [original-result (apply original-fn caller-arguments)]
      (r/record! r/*request-store* (first caller-arguments) original-result)
      original-result)))

(defn mock-clj-http!
  []
  (wrap-fn #'client/request recorder))

(defn unmock-clj-http!
  []
  (unwrap-fn #'client/request))