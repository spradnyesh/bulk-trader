(ns ^:figwheel-always bulk-trader.cljs.geojit
    (:require [om.core :as om :include-macros true]
              [sablono.core :refer-macros [html]]
              [ajax.core :refer [GET POST]]

              [bulk-trader.cljs.globals :as g]
              [bulk-trader.cljs.traders-common :as tc]))

(defonce trader-name "Geojit BNP Paribas")

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; ajax handlers

(defn handler [response]
  (println (str response)))

(defn error-handler [{:keys [status status-text]}]
  (println (str "something bad happened: " status ", " status-text)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; helpers

(defn do-login [e]
  (.preventDefault e)
  (GET "http://localhost:3000/login"
       {:params {:foo "foo"}
        :handler handler
        :error-handler error-handler}))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; components

(defn c-login [data owner]
  (om/component
   (html [:div {:className "z-div"}
          [:form {:method "post"
                  :action g/login-url
                  :encType "application/x-www-form-urlencoded"}
           [:div nil [:label nil
                      [:input {:type "text"
                               :name "username"}]
                      "User ID"]]
           [:div nil [:label nil
                      [:input {:type "password"
                               :name "passwd"}]
                      "Password"]]
           [:div nil [:label nil
                      [:input {:type "text"
                               :name "pan"}]
                      "PAN"]]
           [:div nil
            [:label nil
             [:input {:type "hidden"
                      :name "trader"
                      :value "geojit"}]]
            [:label nil
             [:input {:className "btn btn-default"
                      :type "submit"
                      :value "Login"
                      :onClick do-login
                      :name "submit"}]]
            [:label nil
             [:button {:className "btn btn-default"
                       :onClick tc/clear-login}
              "Cancel"]]]]])))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; main

(defn ^:export login [data owner]
  (if (:state data)
    (c-login data owner)
    (om/component (html [:div nil]))))

;; (let [logged-in? (do-login data owner)
;;         trader (if logged-in? trader-name nil)]
;;     (swap! g/app-state assoc
;;            :trader trader
;;            :logged-in? logged-in?)
;;     logged-in?)
