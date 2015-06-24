(ns ^:figwheel-always bulk-trader.globals)

(enable-console-print!)

;; define your app data so that it doesn't get over-written on reload
(defonce app-state (atom {:trader nil
                          :logged-in? false
                          :data [["NSE" "EQ" "NIFTYBEES" "B" 1 "S" 1000.10 995.10]
                                 ["NSE" "EQ" "NIFTYBEES" "S" 1 "S" 990.90 995.90]]}))

(defonce traders [{:v "geojit" :n "Geojit BNP Paribas"}
                  {:v "icici" :n "ICICI Direct"}])
