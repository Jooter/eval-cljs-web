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

(def eval-env
  '(let [rect (fn rect
                ([c w h]
                 [:svg/svg
                  {:height h :width w}
                  [:svg/rect
                   {:height h :width w :fill (name c)}
                   ]])

                ([c w]
                 (rect c w w))

                ([c]
                 (rect c 20))

                ([]
                 (rect :yellow 20))
                )

         square (fn sqare
                  ([c w] (rect c w))
                  ([c]   (rect c))
                  ([]    (rect)))

         circle (fn circle
                  ([c r]
                   (let [d (* 2 r)]
                     [:svg/svg {:height d :width d}
                      [:svg/circle
                       {:cx r :cy r :r r :fill (name c)}
                       ]]))

                  ([c]
                   (circle c 50))

                  ([]
                   (circle :blue 50))
                  )
         ]

     ))

(defn combine-with-env [f]
  (seq
   (conj
    (vec eval-env)
    f)))

(defn eval-str-3 [s]
  (eval (empty-state)
        (combine-with-env (read-string s))
        {:eval       js-eval}
        identity
        ))

(def example-input-1
  (pp-str
  '(rect :blue 9 90)
  ))

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

(defn keyup [output]
  (fn [e]
    (prn :key (.-key e))
    (let [v (-> e .-target .-value)]
      (reset! output (eval-str-3 v)))))

(defn textarea1 [input output rows cols]
  [:textarea
   {:id "in1"
    :on-keyup (keyup output)
    :on-focus (keyup output)
    :rows rows :cols cols}
   @input])

(defn view []
  (let [input (atom example-input-1)
        output (atom nil)]
    [:div
     #_[:button {:on-click #(eval-str "(println \"hello world!\")")} "let's compile!"]
     #_[:button {:on-click #(reset! output (eval-str @input))} "let's compile!"]
     #_[:p]
     (textarea1 input output 2 50)
     (rx
      (let [out1 (:value @output)]
        [:div
         out1
         [:p #_(str out1)]]))


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
