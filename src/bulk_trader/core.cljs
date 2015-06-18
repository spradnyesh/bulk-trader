(ns ^:figwheel-always bulk-trader.core
    (:require[om.core :as om :include-macros true]
             [om.dom :as dom :include-macros true]))

(enable-console-print!)

;; define your app data so that it doesn't get over-written on reload
(defonce app-state (atom {:trader nil
                          :logged-in? false
                          :data nil}))

(defonce traders ["geojit"])

(defn on-js-reload [])
