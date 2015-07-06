(ns ^:figwheel-always bulk-trader.cljs.globals)

(enable-console-print!)

;; define your app data so that it doesn't get over-written on reload
(defonce app-state (atom {:trader nil
                          :logged-in? false
                          :data []}))
(defonce overlay-state (atom {:data nil
                              :state false}))
(defonce error-state (atom {:data nil
                            :state false}))
(defonce login-state (atom {:state false
                            :trader nil
                            ;; Geojit
                            :usercode nil
                            :pass nil
                            :sessionkey nil}))

(defonce validateur {:exch {:nse {:segments ["EQ"]}
                        :bse {:segments ["EQ"]}}
                 :bs ["B" "S"]
                 :typ ["M" "L" "S"]
                 :p-token 0.05})

(defonce traders [{:i 0 :v "geojit" :n "Geojit BNP Paribas"
                   :login-fn "bulk_trader/cljs/geojit/login"
                   :login-url "/login"
                   :vr validateur}
                  {:i 1 :v "icici" :n "ICICI Direct"
                   :login-fl "bulk_trader/cljs/icici/login"}])
(defonce login-url "/login")
