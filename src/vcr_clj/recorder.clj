(ns vcr-clj.recorder)

(declare ^:dynamic *request-store*)

(defprotocol VcrStore
  (record-request! [store key value] "")
  (get-request [store key] "")
  (get-all [store] "")
  (request-recorded? [store key] "")
  (clear-recorded! [store]))

(deftype VcrMemoryStore [store-map]
  VcrStore
  (record-request!
    [_ options response]
    (clojure.core/swap! store-map assoc options response))

  (get-request
    [_ key]
    (clojure.core/get @store-map key))

  (request-recorded?
    [_ options]
    (some #(= options %) (keys @store-map)))

  (clear-recorded!
    [_]
    (reset! store-map {}))

  (get-all
    [_]
    store-map))

(defn vcr-memory-store
  ([] (vcr-memory-store (atom {})))
  ([s] (VcrMemoryStore. s)))
