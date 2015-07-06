(ns ^:figwheel-always bulk-trader.utils
    (:require [clojure.string :as str]

              [bulk-trader.geojit :as geojit]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; http://stackoverflow.com/a/30892955/4329629

(defn ->js [var-name]
  (-> var-name
      (str/replace #"/" ".")
      (str/replace #"-" "_")))

(defn invoke [function-name & args]
  (when function-name
    (let [fun (js/eval (->js function-name))]
      (apply fun args))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
