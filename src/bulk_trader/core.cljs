(ns ^:figwheel-always bulk-trader.core
    (:require [om.core :as om :include-macros true]
              [om.dom :as dom :include-macros true]
              [sablono.core :as html :refer-macros [html]]

              [bulk-trader.globals :as g]
              [bulk-trader.parse :as p]
              [bulk-trader.geojit :as geojit]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; helpers

;; find "index" (from g/traders) of selected trader
(defn find-selected-trader [e]
  (let [children (.-children (aget (.-children (.-form (.-target e))) 1))]
    ;; this "loop" is needed coz children is HTMLElements which is *not* iterable
    (loop [i (dec (.-length children))]
      (if (= -1 i)
        -1 ; none selected
        (if-let [checked (.-checked (aget (.-children (aget (.-children (aget children i)) 0)) 0))]
          i (recur (dec i)))))))

(defn c-trader [trader owner]
  (om/component (html [:div {:className "radio"}
                       [:label {:className "form-group"}
                        [:input {:type "radio" :name "optionRadios"
                                 :value (:v trader)}
                         (:n trader)]]])))

(defn c-data [data owner]
  (let [[exch segment smbl bs qty typ price slprice] data]
    (om/component (html [:tr nil
                         [:td nil exch]
                         [:td nil segment]
                         [:td nil smbl]
                         [:td nil bs]
                         [:td nil qty]
                         [:td nil typ]
                         [:td nil price]
                         [:td nil slprice]]))))

(defn clear-overlay [e]
  (.preventDefault e)
  (swap! g/overlay-state assoc
         :state false
         :data nil))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; event-handlers and views

(defn e-trade [e]
  (.preventDefault e)

  (.log js/console "inside e-trade"))

(defn e-edit-data-save [e]
  (let [data (.-value (.-firstChild (.-parentNode (.-parentNode (.-target e)))))]
    (swap! g/app-state assoc :data (p/parse data)))
  (clear-overlay e))

(defn e-edit-data-cancel [e]
  (clear-overlay e))

(defn c-overlay [data owner]
  (if (:state data)
    (om/component (html [:div nil
                         [:textarea {:rows 20
                                     :cols 50
                                     :defaultValue (:data data)}]
                         [:div nil
                          [:button {:className "btn btn-default"
                                    :onClick e-edit-data-save}
                           "Save Changes"]
                          [:button {:className "btn btn-default"
                                    :onClick e-edit-data-cancel}
                           "Cancel"]]]))
    (om/component (html [:div nil]))))

(defn e-edit-data [e]
  (.preventDefault e)
  (swap! g/overlay-state assoc
         :state true
         :data (p/unparse (:data @g/app-state))))

(defn e-upload-data [e]
  (.preventDefault e)

  (.log js/console "inside e-upload-data"))

(defn e-login [e]
  (.preventDefault e)

  (let [trader (find-selected-trader e)] ; trader is index from g/traders
    (when-not (cond (= 0 trader) (geojit/login)
                    ;; (= 1 trader) (icici/login)
                    :else nil)
      (js/alert "Login failed! Please try again."))))

(defn c-login [data owner]
  (om/component (html [:form nil
                       [:h3 nil "Select Trader"]
                       [:div nil
                        (map #(om/build c-trader %) data)]
                       [:div nil
                        [:label nil
                         [:button {:className "btn btn-default"
                                   :onClick e-login}
                          "Submit"]]]])))

(defn c-app [data owner]
  (if-not (:logged-in? data)
    (c-login g/traders owner)
    (om/component (html [:div nil
                         [:h3 nil "Logged in to trader: "
                          [:mark nil (:trader data)]]
                         (if (empty? (:data data))
                           [:div nil [:label nil
                                      [:button {:className "btn btn-default"
                                                :onClick e-edit-data}
                                       "Enter / Copy-Paste"]
                                      [:label nil
                                       [:button {:className "btn btn-default"
                                                 :onClick e-upload-data}
                                        "Upload"]]]]
                           [:div nil
                            [:div nil [:label nil
                                       [:button {:className "btn btn-default"
                                                 :onClick e-edit-data}
                                        "Edit Trading Data"]
                                       [:label nil
                                        [:button {:className "btn btn-default"
                                                  :onClick e-trade}
                                         "Execute Trades!!!"]]]]
                            [:h4 nil "Existing Trading Data:"]
                            [:table {:className "table table-striped table-bordered"}
                             [:tbody (map #(om/build c-data %) (:data data))]]])]))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; roots

(om/root c-overlay
         g/overlay-state
         {:target (. js/document (getElementById "overlay"))})

;; init "root" is inside defn, coz need to call it below and on-js-reload
(defn init []
  (om/root c-app
           g/app-state
           {:target (. js/document (getElementById "main"))}))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; init

(defn on-js-reload []
  (init))

(init)
