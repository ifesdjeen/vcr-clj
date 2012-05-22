(ns vcr-clj.core)

(defn wrap-fn
  "Wrap or replace some function with your own function"
  [qualifier wrapper]
  (alter-var-root
   qualifier
   (fn [original-fn]
     (fn [& caller-arguments]
       (wrapper caller-arguments original-fn)))))
