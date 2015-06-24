(ns ^:figwheel-always bulk-trader.core
    (:require [om.core :as om :include-macros true]
              [om.dom :as dom :include-macros true]

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

(defn trader-view [trader owner]
  (reify om/IRender
    (render [this]
      (dom/div "radio"
               (dom/label "form-group"
                          (dom/input #js {:type "radio" :name "optionRadios"
                                          :value (:v trader)}
                                     (:n trader)))))))

(defn data-view [data owner]
  (reify om/IRender
    (render [this]
      (let [[exch segment smbl bs qty typ price slprice] data]
        (dom/tr nil
                (dom/td nil exch)
                (dom/td nil segment)
                (dom/td nil smbl)
                (dom/td nil bs)
                (dom/td nil qty)
                (dom/td nil typ)
                (dom/td nil price)
                (dom/td nil slprice))))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; event-handlers and views

(defn v-clear-overlay []
  (om/root
   (fn [data owner]
     (reify om/IRender
       (render [_]
         (dom/div nil))))
   g/app-state
   {:target (. js/document (getElementById "overlay"))}))

(defn e-trade [e]
  (.log js/console "inside e-trade")

  (.preventDefault e))

;; pre-define
(def v-init-data nil)

(defn e-edit-data-save [e]
  (let [data (.-value (.-firstChild (.-parentNode (.-parentNode (.-target e)))))]
    (swap! g/app-state assoc :data (p/parse data)))

  (v-init-data)
  (v-clear-overlay)
  (.preventDefault e))

(defn e-edit-data-cancel [e]
  (v-clear-overlay)
  (.preventDefault e))

(defn v-edit-data []
  (om/root
   (fn [data owner]
     (reify om/IRender
       (render [_]
         (dom/div nil
                  (dom/textarea #js {:rows 20
                                     :cols 50
                                     :readOnly false}
                                (p/unparse (:data data)))
                  (dom/div nil
                           (dom/button #js {:className "btn btn-default"
                                            :onClick e-edit-data-save}
                                       "Save Changes")
                           (dom/button #js {:className "btn btn-default"
                                            :onClick e-edit-data-cancel}
                                       "Cancel"))))))
   g/app-state
   {:target (. js/document (getElementById "overlay"))}))

(defn e-edit-data [e]
  (v-edit-data)
  (.preventDefault e))

(defn e-upload-data [e]
  (.log js/console "inside e-upload-data")

  (.preventDefault e))

(defn v-init-data []
  (om/root
   (fn [data owner]
     (reify om/IRender
       (render [_]
         (dom/div nil
                  (dom/h3 nil "Logged in to trader: "
                          (dom/mark nil (:trader data)))
                  (if (nil? (:data data))
                    (dom/div nil
                             (dom/label nil
                                        (dom/button #js {:className "btn btn-default"
                                                         :onClick e-edit-data}
                                                    "Enter / Copy-Paste"))
                             (dom/label nil
                                        (dom/button #js {:className "btn btn-default"
                                                         :onClick e-upload-data}
                                                    "Upload")))
                    (dom/div nil
                             (dom/div nil
                                      (dom/label nil
                                                 (dom/button #js {:className "btn btn-default"
                                                                  :onClick e-edit-data}
                                                             "Edit Trading Data"))
                                      (dom/label nil
                                                 (dom/button #js {:className "btn btn-default"
                                                                  :onClick e-trade}
                                                             "Execute Trades!!!")))
                             (dom/h4 nil "Existing Trading Data:")
                             (apply dom/table #js {:className "table table-striped table-bordered"}
                                    (om/build-all data-view (:data data)))))))))
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

(defn v-login [traders owner]
  (reify om/IRender
    (render [this]
      (dom/form #js {:action "#"}
               (dom/h3 nil "Select Trader")
               (apply dom/div #js {:className "radio"}
                      (om/build-all trader-view traders))
               (dom/div nil
                        (dom/label nil
                                   (dom/button #js {:className "btn btn-default"
                                                    :onClick e-login}
                                               "Submit")))))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; init

(defn init []
  (om/root
   (fn [data owner]
     (if-not (:logged-in? data)
       (reify om/IRender
         (render [_] (om/build v-login g/traders)))
       (v-init-data)))
   g/app-state
   {:target (. js/document (getElementById "main"))}))

(defn on-js-reload []
  (init))

(init)
