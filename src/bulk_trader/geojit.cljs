(ns ^:figwheel-always bulk-trader.geojit
    (:require [bulk-trader.globals :as g]))

(defonce trader-name "Geojit BNP Paribas")

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; helpers

;; XXX remove hardcoding
(defn- do-login []
  true)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn login []
  (let [logged-in? (do-login)
        trader (if logged-in? trader-name nil)]
    (swap! g/app-state assoc
           :trader trader
           :logged-in? logged-in?)
    logged-in?))
