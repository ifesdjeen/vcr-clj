(ns vcr-clj.core
  (:require [clj-yaml.core :as yaml]))

(defn wrap-fn
  "Wrap or replace some function with your own function"
  [qualifier wrapper]
  (alter-var-root
   qualifier
   (fn [original-fn]
     (fn [& caller-arguments]
       (wrapper caller-arguments original-fn)))))

(defonce recorded-requests (atom {}))

(defn record-request
  ""
  [options response]
  (swap! recorded-requests assoc options response))

(defn request-recorded?
  [options]
  (some #(= options %) (keys @recorded-requests)))

(defn persist-recorded!
  []
  (io!
   (spit "/Users/alexp/p/vcr-clj/cassete.yml" (yaml/generate-string @recorded-requests))))

(defn recorder
  [caller-arguments original-fn]
  (let [original-result (apply original-fn caller-arguments)]
    (record-request (first caller-arguments) original-result)
    original-result))