(ns land-of-clojurescript.core
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [reagent.core :as reagent]
            [re-frame.core :as r]))

(enable-console-print!)

(def width 100)
(def height 100)
(def box-size 10)

(r/reg-sub-raw
 :running?
 (fn [db _]
   (reaction (:running? @db))))

(r/reg-sub-raw
 :dragging?
 (fn [db _]
   (reaction (:dragging? @db))))

(r/reg-sub-raw
 :position
 (fn [db _]
   (reaction (:position @db))))

(r/reg-event-db
 :init
 (fn [db _]
   (assoc db
          :running? false
          :position {:x (/ width 2) :y (/ height 2)}
          :velocity {:vx 2 :vy 1})))

(r/reg-event-db
 :toggle-running
 (fn [db _]
   (update db :running? not)))

(r/reg-event-db
 :set-dragging
 [r/trim-v]
 (fn [db [dragging?]]
   (assoc db :dragging? dragging?)))

(r/reg-event-db
 :set-position
 [r/trim-v]
 (fn [db [position]]
   (assoc db :position position)))

(r/reg-event-db
 :update-box
 [r/trim-v]
 (fn [db [coord]]
   (assoc db :position coord)))

(r/reg-event-db
 :tick
 (fn [db _]
   (if-not (:running? db)
     db
     (let [{:keys [x y]} (:position db)
           {:keys [vx vy]} (:velocity db)
           vx' (let [x' (+ x vx)]
                 (if (< 0 x' (- width box-size))
                   vx
                   (- vx)))
           vy' (let [y' (+ y vy)]
                 (if (< 0 y' (- height box-size))
                   vy
                   (- vy)))]
       (assoc db
              :position {:x (+ x vx) :y (+ y vy)}
              :velocity {:vx vx' :vy vy'})))))

(r/reg-event-db
 :drag-box
 [r/trim-v]
 (fn [db [position]]
   (if-not (:dragging? db)
     db
     (assoc db :position position))))

(defn app []
  (let [position (r/subscribe [:position])
        running? (r/subscribe [:running?])]
    [:div
     [:div
      [:svg {:view-box (str "0 0 " width " " height)
             :style {:width 400 :height 400 :border "1px solid"}
             :on-mouse-move (fn [e]
                              (let [pos {:x (- (int (/ (.-clientX e) 4))
                                               (/ box-size 2))
                                         :y (- (int (/ (.-clientY e) 4))
                                               (/ box-size 2))}]
                                (r/dispatch [:drag-box pos])))}
       [:rect {:style {:fill :red}
               :width box-size
               :height box-size
               :x (:x @position)
               :y (:y @position)
               :on-mouse-down #(r/dispatch [:set-dragging true])
               :on-mouse-up #(r/dispatch [:set-dragging false])}]]]
     [:div
      [:button {:on-click #(r/dispatch [:toggle-running])}
       (if @running? "stop" "start")]]]))

(defn main []
  (r/dispatch-sync [:init])
  (js/setInterval #(r/dispatch [:tick]) 50)
  (reagent/render [app] (.getElementById js/document "app")))

(.addEventListener js/window "load" main)
