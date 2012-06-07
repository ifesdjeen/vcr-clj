(ns vcr-clj.core
  (:require [clj-yaml.core :as yaml]
            [vcr-clj.recorder :as recorder]))

(defn wrap-fn
  "Wrap or replace some function with your own function"
  [qualifier wrapper]
  (alter-var-root
   qualifier
   (fn [original-fn]
     (fn [& caller-arguments]
       (wrapper caller-arguments original-fn)))))

;; (defn persist-recorded!
;;   []
;;   (io!
;;    (spit "cassete.yml" (yaml/generate-string @recorded-requests))))

(defn recorder
  [caller-arguments original-fn]
  (let [original-result (apply original-fn caller-arguments)]
    ;; (recorder/record-request! (first caller-arguments) original-result)
    original-result))