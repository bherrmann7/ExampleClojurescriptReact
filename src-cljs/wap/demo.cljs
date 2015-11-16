(ns ^:figwheel-always wap.demo
  (:require
    [reagent.core :as reagent :refer [atom]]))
(enable-console-print!)

(defonce app-state (atom {
                          :colors {:left "white" :right "white"}
                          :people []
                          }))

; -- state changes
(defn add-person []
  (swap! app-state assoc :people (conj (:people @app-state) {:name "(unnamed)"}))
  )
(defn edit-person [i]
  (swap! app-state assoc :editor :person-editor :editor-person-name (:name (get (:people @app-state) i)) :editor-person-index i)
  )
(defn change-name [new-name]
  (swap! app-state assoc :editor-person-name new-name)
  )
(defn save-person []
  (let [peeps (assoc (:people @app-state) (:editor-person-index @app-state) {:name (:editor-person-name @app-state)})]
    (swap! app-state assoc :people peeps)
    (swap! app-state assoc :editor nil)
    ))
(defn edit-color [panel-key]
  (swap! app-state assoc :editor :color-editor :editor-color "white" :editor-color-panel panel-key)
  )
(defn change-color [new-color]
  (swap! app-state assoc :editor-color new-color)
  )
(defn save-color []
  (let [panel-key (:editor-color-panel @app-state)
        color (:editor-color @app-state)]
    (swap! app-state assoc-in [:colors panel-key] color)
    (swap! app-state assoc :editor nil)
    ))
; ---

(defn person-panel [people background]
  [:div {:style {:flex 1 :backgroundColor background :padding "10px"}}
   [:h4 "People"]
   (map-indexed (fn [i p] (vector :div (:name p) " " [:a {:href "#" :on-click #(edit-person i)} "(edit)"])) people)
   [:button {:on-click #(add-person)} "Add person"]
   ]
  )

(defn color-panel [colors background]
  [:div {:style {:flex 1 :backgroundColor background :padding "10px"}}
   [:h4 "Colors"]
   (map (fn [lr] (vector :div (name lr) ": " (lr colors) " " [:a {:href "#" :on-click #(edit-color lr)} "(edit)"])) [:left :right])
   ])

(defn person-editor [name]
  [:div
   [:input {:value name :on-change #(change-name (-> % .-target .-value))}]
   [:button {:on-click save-person} "Save"]
   ])

(defn color-editor [color]
  [:div
   [:select {:value color :on-change #(change-color (-> % .-target .-value))}
    (map #(vector :option {:value %} %) ["white", "plum", "gold"])
    ]
   [:button {:on-click save-color} "Save"]
   ])

(defn demo-page []
  [:div {:style {:padding "10px"}}
   [:h1 (:text @app-state)]
   [:div {:style {:display "flex" :margin "20px"}}
    [person-panel (:people @app-state) (:left (:colors @app-state))]
    [color-panel (:colors @app-state) (:right (:colors @app-state))]
    ]
   [:hr]
   [:div
    (if (= :person-editor (:editor @app-state)) (person-editor (:editor-person-name @app-state)))
    (if (= :color-editor (:editor @app-state)) (color-editor (:editor-color @app-state)))
    ]
   [:br]
   [:br]
   [:br]
   [:br]
   [:a {:href "https://github.com/bherrmann7/ExampleClojurescriptReact/blob/master/src-cljs/wap/demo.cljs"} "Clojurescript gist"]
   ]
  )

