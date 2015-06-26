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
        (let [checked (.-checked (aget (.-children (aget (.-children (aget children i)) 0)) 0))]
          (if checked i (recur (dec i))))))))

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

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; event-handlers and views

(defn v-clear-overlay []
  (om/root
   (fn [data owner] (om/component (html [:div nil])))
   g/overlay-state
   {:target (. js/document (getElementById "overlay"))}))

(defn e-trade [e]
  (.log js/console "inside e-trade")

  (.preventDefault e))

(defn e-edit-data-save [e]
  (let [data (.-value (.-firstChild (.-parentNode (.-parentNode (.-target e)))))]
    (swap! g/app-state assoc :data (p/parse data))) ; will call v-init-data automatically
  (v-clear-overlay)
  (.preventDefault e))

(defn e-edit-data-cancel [e]
  (v-clear-overlay)
  (.preventDefault e))

(defn c-edit-data [data owner]
  (om/component (html [:div nil
                       [:textarea {:rows 20
                                   :cols 50
                                   :readOnly false
                                   :defaultValue (p/unparse (:data data))}]
                       [:div nil
                        [:button {:className "btn btn-default"
                                  :onClick e-edit-data-save}
                         "Save Changes"]
                        [:button {:className "btn btn-default"
                                  :onClick e-edit-data-cancel}
                         "Cancel"]]])))

(defn v-edit-data []
  (om/root
   c-edit-data
   g/app-state
   {:target (. js/document (getElementById "overlay"))}))

(defn e-edit-data [e]
  (v-edit-data)
  (.preventDefault e))

(defn e-upload-data [e]
  (.log js/console "inside e-upload-data")

  (.preventDefault e))

(defn c-init-data [data owner]
  (om/component (html [:div nil
                       [:h3 nil "Logged in to trader: "
                        [:mark nil (:trader data)]]
                       (if (nil? (:data data))
                         [:div nil [:label nil
                                    [:button {:className "btn btn-default"
                                              :onClick e-edit-data}
                                     "Enter / Copy-Paste"]
                                    [:label nil
                                     [:button {:className "btn btn-default"
                                               :onClick e-upload-data}
                                      "Upload"]]]]
                         [:div nil [:div nil [:label nil
                                              [:button {:className "btn btn-default"
                                                        :onClick e-edit-data}
                                               "Edit Trading Data"]
                                              [:label nil
                                               [:button {:className "btn btn-default"
                                                         :onClick e-trade}
                                                "Execute Trades!!!"]]]]
                          [:h4 nil "Existing Trading Data:"]
                          [:table {:className "table table-striped table-bordered"}
                           (map #(om/build c-data %) (:data data))]])])))

(defn v-init-data []
  (om/root
   c-init-data
   g/app-state
   {:target (. js/document (getElementById "main"))}))



(defn e-login [e]
  (let [trader (find-selected-trader e)] ; trader is index from g/traders
    (if (cond (= 0 trader) (geojit/login)
              ;; (= 1 trader) (icici/login)
              :else nil)
      (v-init-data)
      (js/alert "Login failed! Please try again."))

    (.preventDefault e)))

(defn c-login [traders]
  (om/component (html [:form nil
                       [:h3 nil "Select Trader"]
                       [:div {:className "radio"}
                        (map #(om/build c-trader %) traders)]
                       [:div nil
                        [:label nil
                         [:button {:className "btn btn-default"
                                   :onClick e-login}
                          "Submit"]]]])))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; init

(defn init []
  (om/root
   (fn [data owner]
     (if-not (:logged-in? data)
       (c-login g/traders)
       (v-init-data)))
   g/app-state
   {:target (. js/document (getElementById "main"))}))

(defn on-js-reload []
  (init))

(init)
