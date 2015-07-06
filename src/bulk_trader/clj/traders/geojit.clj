(ns bulk-trader.clj.traders.geojit
  (:require [bulk-trader.clj.globals :as g]
            [cognitect.transit :as t]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; definitions

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; helpers

(defn login-1 [username passwd pan]
  {:status nil
   :session-key "abcdefghi"
   :error "wrong id"})

(defn login-2 [username passwd pan]
  )

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; main

(defn login [{:keys [username passwd pan] :as params}]
  (let [rslt-1 (login-1 username passwd pan)]
    (if (:status rslt-1)
      (let [rslt-2 (login-1 username passwd pan)]
        (if (:status rslt-2)
          {:logged-in? true :error nil}
          {:logged-in? false :error (:error rslt-2)}))
      {:logged-in? false :error (:error rslt-1)})))
