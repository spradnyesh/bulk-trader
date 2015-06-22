(ns ^:figwheel-always bulk-trader.core
    (:require[om.core :as om :include-macros true]
             [om.dom :as dom :include-macros true]

             [bulk-trader.globals :as g]))

(enable-console-print!)

;; define your app data so that it doesn't get over-written on reload
(defonce app-state (atom {:trader nil
                          :logged-in? false
                          :data nil}))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; event-handlers

;; TODO
(defn e-select-trader [e]
  (.log js/console e)
  nil)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; views

(defn v-trader-view [trader owner]
  (reify om/IRender
    (render [this]
      (dom/div "radio"
               (dom/label "form-group"
                          (dom/input #js {:type "radio" :name "optionRadios"
                                          :value (:v trader)}
                                     (:n trader)))))))

(defn v-select-traders [traders owner]
  (reify om/IRender
    (render [this]
      (dom/form #js {:action "#"}
               (dom/h3 nil "Select Trader")
               (apply dom/div "radio"
                      (om/build-all v-trader-view traders))
               (dom/div nil
                        (dom/label nil
                                   (dom/button #js {:className "btn btn-default"
                                                    :type "submit"
                                                    :onClick e-select-trader}
                                               "Submit")))))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; init

(defn init []
  (om/root
   (fn [data owner]
     (if-not (:logged-in? data)
       (reify om/IRender
         (render [_] (om/build v-select-traders g/traders)))
       ;; TODO
       (reify om/IRender
         (render [_] (dom/p nil "TODO")))))
   app-state
   {:target (. js/document (getElementById "main"))}))

(defn on-js-reload []
  (init))

(init)
