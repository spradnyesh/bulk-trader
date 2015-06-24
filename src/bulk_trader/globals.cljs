(ns ^:figwheel-always bulk-trader.globals)

(enable-console-print!)

;; define your app data so that it doesn't get over-written on reload
(defonce app-state (atom {:trader nil
                          :logged-in? false
                          :data nil}))

(defonce traders [{:v "geojit" :n "Geojit BNP Paribas"}
                  {:v "icici" :n "ICICI Direct"}])
