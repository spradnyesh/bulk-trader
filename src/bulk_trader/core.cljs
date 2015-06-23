(ns ^:figwheel-always bulk-trader.core
    (:require [om.core :as om :include-macros true]
              [om.dom :as dom :include-macros true]

              [bulk-trader.globals :as g]
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

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; event-handlers and views

(defn e-enter-data [e]
  (.log js/console "inside e-enter-data")

  (.preventDefault e))

(defn e-upload-data [e]
  (.log js/console "inside e-upload-data")

  (.preventDefault e))

(defn v-data-init []
  (om/root
   (fn [data owner]
     (reify om/IRender
       (render [_]
         (dom/div nil
                  (dom/h3 nil "Logged in to trader: "
                          (dom/mark nil (:trader data)))
                  (dom/form #js {:action "#"}
                            (dom/div nil
                                     (dom/label nil
                                                (dom/button #js {:className "btn btn-default"
                                                                 :onClick e-enter-data}
                                                            "Enter / Copy-Paste"))
                                     (dom/label nil
                                                (dom/button #js {:className "btn btn-default"
                                                                 :onClick e-upload-data}
                                                            "Upload"))))))))
   g/app-state
   {:target (. js/document (getElementById "main"))}))

(defn e-login [e]
  (let [trader (find-selected-trader e)] ; trader is index from g/traders
    (if (cond (= 0 trader) (geojit/login)
              ;; (= 1 trader) (icici/login)
              :else nil)
      (v-data-init)
      (js/alert "Login failed! Please try again."))

    (.preventDefault e)))

(defn v-login [traders owner]
  (reify om/IRender
    (render [this]
      (dom/form #js {:action "#"}
               (dom/h3 nil "Select Trader")
               (apply dom/div "radio"
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
       (v-data-init)))
   g/app-state
   {:target (. js/document (getElementById "main"))}))

(defn on-js-reload []
  (init))

(init)
