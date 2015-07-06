(ns ^:figwheel-always bulk-trader.globals)

(enable-console-print!)

;; define your app data so that it doesn't get over-written on reload
(defonce app-state (atom {:trader nil
                          :logged-in? false
                          :data []}))
(defonce overlay-state (atom {:data nil
                              :state false}))
(defonce error-state (atom {:data nil
                            :state false}))

(def validateur {:exch {:nse {:segments ["EQ"]}
                        :bse {:segments ["EQ"]}}
                 :bs ["B" "S"]
                 :typ ["M" "L" "S"]
                 :p-token 0.05})

(defonce traders [{:i 0 :v "geojit" :n "Geojit BNP Paribas" :l "bulk_trader/geojit/login"
                   :vr validateur}
                  {:i 1 :v "icici" :n "ICICI Direct" :l "bulk_trader/icici/login"}])
