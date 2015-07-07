(ns ^:figwheel-always bulk-trader.cljs.geojit
    (:require [om.core :as om :include-macros true]
              [sablono.core :refer-macros [html]]
              [ajax.core :refer [GET POST]]

              [bulk-trader.cljs.globals :as g]
              [bulk-trader.cljs.traders-common :as tc]))

(defonce trader "geojit")
(defonce trader-name "Geojit BNP Paribas")

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; ajax handlers

(defn handler [response]
  ;; (println (str response))
  (if-let [logged-in? (:logged-in? response)]
    (do (tc/clear-login nil)
        (swap! g/app-state assoc
               :trader trader
               :trader-name trader-name
               :sessionid (:sessionid response)
               :logged-in? logged-in?))
    (js/alert (str "Login Failed! Error is:\n\n" (:error response) "\n\nPlease try again..."))))

(defn error-handler [{:keys [status status-text]}]
  (println (str "something bad happened: " status ", " status-text)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; helpers

(defn do-login [e]
  (.preventDefault e)
  (POST (str g/login-domain g/login-url)
        {:params {:trader "geojit"
                  :username "foo"
                  :passwd "bar"
                  :pan "baz"}
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
