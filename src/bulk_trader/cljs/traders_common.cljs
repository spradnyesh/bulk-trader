(ns ^:figwheel-always bulk-trader.cljs.traders-common
    (:require [bulk-trader.cljs.globals :as g]))

(defn clear-login [e]
  (when e (.preventDefault e))
  (swap! g/login-state assoc :state nil))
