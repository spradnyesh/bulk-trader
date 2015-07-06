(ns ^:figwheel-always bulk-trader.cljs.core
    (:require-macros [cljs.core.async.macros :refer [go]])
    (:require [om.core :as om :include-macros true]
              [sablono.core :as html :refer-macros [html]]
              [cljs.core.async :refer [put! chan <!]]

              [bulk-trader.cljs.globals :as g]
              [bulk-trader.cljs.parse :as p]
              [bulk-trader.cljs.utils :as u]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; helpers

;; find "index" (from g/traders) of selected trader
(defn find-selected-trader [e]
  (let [children (.-children (aget (.. e -target -form -children) 1))]
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
         :state false))

(defn clear-error [e]
  (.preventDefault e)
  (swap! g/error-state assoc
         :state false))

(defn validation-errors-markup [data]
  (html [:table {:className "table table-striped table-bordered"}
         [:tbody
          [:tr [:th "#Row"] [:th "Issue"]]
          (map (fn [[r is]]
                 (map (fn [i] [:tr [:td r] [:td i]]) is))
               data)]]))

(defn save-data [data e]
  (let [parsed (p/parse data)]
    (if (first parsed)
      (do (clear-overlay e) ; close existing (in case of "upload another file") overlay
          (swap! g/app-state assoc
                 :data (second parsed)))
      (swap! g/error-state assoc
             :state true
             :data (validation-errors-markup (second parsed))))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; event-handlers and views

(defn e-trade [e]
  (.preventDefault e)

  (.log js/console "inside e-trade"))

(defn e-edit-data-save [e]
  (let [data (.. e -target -parentNode -parentNode -parentNode -firstChild -value)]
    (save-data data e)))

(defn e-edit-data-cancel [e]
  (clear-overlay e))

(defn e-edit-data [e]
  (.preventDefault e)
  (swap! g/overlay-state assoc
         :state true
         :data (p/unparse (:data @g/app-state))))

(defn e-upload-data [e]
  (.preventDefault e)
  (let [file (aget (.. e -target -parentNode -firstChild -files) 0)
        reader (js/FileReader.)]
    (if (or (= "text/csv" (.-type file)) (= "application/ms-excel" (.-type file)))
      (set! (.-onload reader) ; set "onload" event handler for reader
            (fn [event]
              (save-data (.. event -target -result) event)))
      (js/alert "Please upload a .csv file"))
    ;; fire "read" event
    (.readAsText reader file)))

(defn c-error [data owner]
  (if (:state data)
    (om/component (html [:div {:className "z-div"}
                         [:div nil (:data data)]
                         [:div nil [:label nil
                                    [:button {:className "btn btn-default"
                                              :onClick clear-error}
                                     "Close"]]]]))
    (om/component (html [:div {:className "z-div"}]))))

(defn c-overlay [data owner]
  (if (:state data)
    (om/component (html [:div {:className "z-div"}
                         [:textarea {:rows 20
                                     :cols 65
                                     :defaultValue (:data data)}]
                         [:div nil
                          [:label nil
                           [:button {:className "btn btn-default"
                                     :onClick e-edit-data-save}
                            "Save Changes"]]
                          [:label nil
                           [:button {:className "btn btn-default"
                                     :onClick e-edit-data-cancel}
                            "Cancel"]]]
                         [:div nil
                           [:label nil
                            [:input {:type "file"
                                     :className "fileinput"}]
                            [:button {:className "btn btn-default"
                                      :onClick e-upload-data}
                             "Upload Another File"]]]]))
    (om/component (html [:div {:className "z-div"}]))))

(defn c-login [data owner]
  (if-let [trader (:trader data)]
    (u/invoke (:login-fn trader) data owner)
    (om/component (html [:div nil]))))

(defn e-login [e]
  (.preventDefault e)
  (let [trader (first (filter #(= (find-selected-trader e) (:i %)) g/traders))]
    (swap! g/login-state assoc
           :state :login
           :trader trader)))

(defn c-traders [data owner]
  (om/component (html [:form nil
                       [:h3 nil "Select Trader"]
                       [:div nil (map #(om/build c-trader %) data)]
                       [:div nil
                        [:label nil
                         [:button {:className "btn btn-default"
                                   :onClick e-login}
                          "Submit"]]]])))

(defn c-app [data owner]
  (if-not (:logged-in? data)
    (c-traders g/traders owner)
    (om/component (html [:div nil
                         [:h3 nil "Logged in to trader: "
                          [:mark nil (:trader data)]]
                         (if (empty? (:data data))
                           [:div nil
                            [:label nil
                             [:button {:className "btn btn-default"
                                       :onClick e-edit-data}
                              "Enter / Copy-Paste"]]
                            [:label nil
                             [:input {:type "file"
                                      :className "fileinput"}]
                             [:button {:className "btn btn-default"
                                       :onClick e-upload-data}
                              "Upload"]]]
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

(om/root c-error
         g/error-state
         {:target (. js/document (getElementById "error"))})

(om/root c-overlay
         g/overlay-state
         {:target (. js/document (getElementById "overlay"))})

(om/root c-login
         g/login-state
         {:target (. js/document (getElementById "login"))})

(om/root c-app
         g/app-state
         {:target (. js/document (getElementById "main"))})

(defn on-js-reload [])
