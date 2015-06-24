(ns ^:figwheel-always bulk-trader.parse
    (:require [clojure.string :as str]

              [bulk-trader.globals :as g]))

(defn unparse [data]
  (str/join "\n" (mapv #(str/join "," %) data)))

(defn parse [s]
  (let [rows (str/split-lines s)]
    (mapv #(str/split % ",") rows)))
