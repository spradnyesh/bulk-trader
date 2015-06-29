(ns ^:figwheel-always bulk-trader.parse
    (:require [clojure.string :as str]

              [bulk-trader.globals :as g]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; validation helpers

(defn valid-exch? [exch]
  (first (filter #(= % (keyword (str/lower-case exch)))
                 (keys (:exch g/validateur)))))

(defn valid-segment? [exch segment]
  (when-let [exchange (valid-exch? exch)]
    (first (filter #(= % segment)
                   (:segments (exchange (:exch g/validateur)))))))

(defn valid-bs? [bs]
  (first (filter #(= % bs) (:bs g/validateur))))

(defn valid-typ? [typ]
  (first (filter #(= % typ) (:typ g/validateur))))

(defn valid-price? [price]
  (integer? (/ price (:p-token g/validateur))))

;; (valid? "NSE" "EQ" "NIFTYBEES" "B" 1 "M" 1.1 1.2)
;; (valid? "NS" "E" "NIFTYBEES" "BS" 1.1 "A" 1.13 1.23)
(defn valid? [& args]
  (let [data (first args)
        errors (atom [])]
    (if-not (= 8 (count data))
      (swap! errors conj "missing values")
      (let [[exch segment smbl bs qty typ price slprice] data]
        (when-not (valid-exch? exch)
          (swap! errors conj "Exchange is invalid"))
        (when-not (valid-segment? exch segment)
          (swap! errors conj "Segment is invalid"))
        (when-not (valid-bs? bs)
          (swap! errors conj "Buy/Sell is invalid"))
        (when-not (integer? (js/parseFloat qty))
          (swap! errors conj "Qty is invalid"))
        (when-not (valid-typ? typ)
          (swap! errors conj "Type is invalid"))
        (when-not (valid-price? (js/parseFloat price))
          (swap! errors conj "Price is invalid"))
        (when-not (valid-price? (js/parseFloat slprice))
          (swap! errors conj "S-L-Price is invalid"))))
    (if (empty? @errors)
      [true (first args)]
      [nil @errors])))

(defn filter-invalids [validated]
  (map (fn [[a [b c]]] [(inc a) c])
       (filter #(= nil (first (second %)))
               (partition 2 (interleave (range (count validated)) validated)))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn parse [s]
  (let [rows (str/split-lines s)
        validated (map valid? (map #(str/split % ",") rows))
        valid (filter #(= true (first %)) validated)]
    (if (= (count rows) (count valid))
      [true (map second valid)]
      [nil (filter-invalids validated)])))

(defn unparse [data]
  (str/join "\n" (map #(str/join "," %) data)))
