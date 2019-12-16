(ns city-bikes.core
  (:require [quil.core :as q]
            [clojure.data.json :as json]))

(defn keep-interesting [b]
  (select-keys b
               [:start_station_latitude
                :start_station_longitude
                :end_station_latitude
                :end_station_longitude
                :duration]))

(def bysykler
  (let [parsed (-> "bysykler.json"
                   slurp
                   (json/read-str :key-fn keyword))]
    (map keep-interesting parsed)))

(def max-start-lat (apply max (map :start_station_latitude bysykler)))
(def max-start-lon (apply max (map :start_station_longitude bysykler)))
(def min-start-lat (apply min (map :start_station_latitude bysykler)))
(def min-start-lon (apply min (map :start_station_longitude bysykler)))

(defn lat-long-to-xy [lat lon]
  [
   (q/map-range lat min-start-lat max-start-lat 10 (- 1000 10))
   (q/map-range lon min-start-lon max-start-lon 10 (- 1000 10))
   ])



(def min-dur (apply min (map :duration bysykler)))
(def max-dur (apply max (map :duration bysykler)))

(defn normalize-duration [dur]
  (q/map-range dur min-dur 500 0 10))


(defn setup []
  (q/color-mode :hsb 100 100 100)
  (q/background 0.)
  (q/stroke 100. 10)
  (q/stroke-weight 1))

(defn draw []
  (println (first bysykler))
  (doseq [b bysykler]
    (let [[x y] (lat-long-to-xy (:start_station_latitude b) (:start_station_longitude b))
          [aa bb] (lat-long-to-xy (:end_station_latitude b) (:end_station_longitude b))
          dur (normalize-duration (:duration b))
          ]
      (q/stroke dur 100 80 10)
      (q/line x y aa bb)))
  (q/no-loop))

(q/defsketch quil-drawings
  :title "Bysykler"
  :size [1000 1000]
  :setup setup
  :draw draw
  :features [:keep-on-top :no-bind-output])

(defn -main
  [& args])
