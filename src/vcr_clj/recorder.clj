(ns vcr-clj.recorder
  (:refer-clojure :exclude [list]))

(declare ^:dynamic *request-store*)

(defprotocol VcrStore
  (record! [store key value] "")
  (get [store key] "")
  (get-all [store] "")
  (recorded? [store key] "")
  (clear-recorded! [store]))

(deftype VcrMemoryStore [store-map]
  VcrStore
  (record!
    [_ options response]
    (clojure.core/swap! store-map assoc options response))

  (get [_ key]
    (clojure.core/get @store-map key))

  (recorded? [_ options]
    (some #(= options %) (keys @store-map)))

  (clear-recorded! [_]
    (reset! store-map {}))

  (get-all
    [_]
    store-map)

  (dump-tp-disk! [_ options]
    (some #(= options %) (keys @store-map))))

(defn vcr-memory-store
  ([] (vcr-memory-store (atom {})))
  ([s] (VcrMemoryStore. s)))
