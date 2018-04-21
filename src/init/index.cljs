(ns init.index
  (:refer-clojure :exclude [atom])
  (:require [freactive.core :refer [atom]]
            [freactive.dom :as dom]
            #_[clojure.string :as s]
            [cljs.pprint :refer [pprint]]

            #_[cljs.env :as env]
            #_[cljs.analyzer :as ana]
            #_[cljs.env :refer [*compiler*]]
            [cljs.reader :refer [read-string]]
            [cljs.js :refer [empty-state eval #_eval-str js-eval]]

            #_[cljs.analyzer :as ana :include-macros true]
            #_[cljs.core.async :refer [put! chan <! >!]])
  (:require-macros #_[cljs.core.async.macros :refer [go go-loop]]
                   [freactive.macros :refer [rx]]))

(defonce mousexy (atom nil))

#_(defn eval-str-1
    "This does not work"
    [s]
    (eval-str (empty-state) s "input"
              {:eval js-eval}
              identity
              ))

(defn eval-str-2 [s]
  (eval (empty-state)
        (read-string s)
        {:eval       js-eval
         ; :source-map true
         ; :ns 'cljs.core
         ; :ns 'init.index
         ; :verbose true
         ; :load (fn [ a b ] (prn :load a b))
         ; :load-macros true
         ; :macros true
         ; :context    :expr
         }
        #_(fn [result] result)
        identity
        ))

(defn pp-str [form1]
  (with-out-str
    (pprint form1)))

#_(def example-input-1 "(do (js/alert 9) [:h1 \"Hi\"])")

#_(def example-input-1
  (pp-str
   '(do
      (js/alert 9)
      [:h1 "Hi"])
   ))

#_(def example-input-1
    (pp-str
     [:svg/svg
      [:svg/circle
       {:cx 50 :cy 50 :r 50 :fill "green"}
       ]]
     ))

#_(def example-input-1
  (pp-str
   '(let [s 50 c "green"]
      [:svg/svg
       {:height (* 2 s)
        :width  (* 2 s)}
       [:svg/circle
        {:cx s :cy s :r s :fill c}
        ]]
      )))

(def example-input-1
  (pp-str
   '(let [w 50 c "green"]
      [:svg/svg
       {:height w :width w}
       [:svg/rect
        {:height w :width w :fill c}
        ]]
      )))

#_(def example-input-1
  (pp-str
   '[:input
     {:on-keypress
      (fn [e]
        (let [v (-> e .-target .-value)]
          (prn :v v)))
      }
     ]
   ))

(defn view []
  (let [input (atom example-input-1)
        output (atom nil)]
    [:div
     #_[:button {:on-click #(eval-str "(println \"hello world!\")")} "let's compile!"]
     #_[:button {:on-click #(reset! output (eval-str @input))} "let's compile!"]
     #_[:p]
     (rx
      (let [out1 (:value @output)]
        [:div
         out1
         [:p #_(str out1)]]))

     [:h3 "Press F8 to play"]
     [:textarea
      {:id "in1"
       :on-keypress
       (fn [e]
         (let [v (-> e .-target .-value)]
           (when (= "F8" (.-key e)) 
             (reset! output
                     (eval-str-2 v)))))

       :rows 10 :cols 60}

      @input]

     ]))

(set!
 (.-onload js/window)
 (fn []
   (let [root
         (dom/append-child! (.-body js/document) [:div#root])]

     (aset js/document "title" "Eval cljs in cljs")

     (dom/mount! root (view))


     (dom/listen!
      js/window "mousemove"
      (fn [e]
        (reset! mousexy
         {:x (.-clientX e)
          :y (.-clientY e)})))

     (.focus (js/document.getElementById "in1"))
     )))
