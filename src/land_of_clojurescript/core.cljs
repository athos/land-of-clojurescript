(ns land-of-clojurescript.core
  (:require [reagent.core :as reagent]
            [re-frame.core :as r]))

(enable-console-print!)

(defn app []
  [:svg {:view-box "0 0 100 100"
         :style {:width 400 :height 400}}
   [:linearGradient#gradient
    [:stop {:offset "0%" :style {:stop-color :yellow}}]
    [:stop {:offset "100%" :style {:stop-color :green}}]]
   [:rect {:x 0 :y 0 :width 100 :height 100 :style {:fill "url(#gradient)"}}]
   [:circle {:cx 50 :cy 50 :r 30 :style {:fill "url(#gradient)"}}]])

(defn main []
  (reagent/render [app] (.getElementById js/document "app")))

(.addEventListener js/window "load" main)
