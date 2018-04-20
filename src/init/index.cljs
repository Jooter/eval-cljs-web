(ns init.index
  (:refer-clojure :exclude [atom])
  (:require [freactive.core :refer [atom]]
            [freactive.dom :as dom]
            #_[clojure.string :as s]

            [cljs.reader :refer [read-string]]
            [cljs.js :refer [empty-state eval js-eval]]
            #_[cljs.env :refer [*compiler*]]
            #_[cljs.pprint :refer [pprint]]

            #_[cljs.core.async :refer [put! chan <! >!]])
  (:require-macros #_[cljs.core.async.macros :refer [go go-loop]]
                   [freactive.macros :refer [rx]]))

(defn eval-str [s]
  (eval (empty-state)
        (read-string s)
        {:eval       js-eval
         :source-map true
         :context    :expr}
        (fn [result] result)))

#_(defn pp-str [form1]
  (with-out-str
    (pprint form1)))

(def example-input-1 "(do (js/alert 9) [:h1 \"Hi\"])")

#_(def example-input-1
  (pp-str
   '(do
      (js/alert 9)
      [:h1 "Hi"])
   ))

(defn view []
  (let [input (atom example-input-1)
        output (atom nil)]
    [:div
     #_[:button {:on-click #(eval-str "(println \"hello world!\")")} "let's compile!"]
     #_[:button {:on-click #(reset! output (eval-str @input))} "let's compile!"]
     #_[:p]
     [:h3 "Enter to evalue cljs input"]
     [:input
      {:id "in1"
       :on-keypress
       (fn [e]
         (let [v (-> e .-target .-value)]
           (prn :code (.-key e))
           (when (= "Enter" (.-key e)) 
             (reset! output (eval-str v)))))

       :value @input :type "text"}]
     [:p] [:h3 (rx (str "" @output))]
     [:p] (rx (:value @output))
     ]))

(set!
 (.-onload js/window)
 (fn []
   (let [root
         (dom/append-child! (.-body js/document) [:div#root])]

     (aset js/document "title" "Eval cljs in cljs")

     (dom/mount! root (view))

     (.focus (js/document.getElementById "in1"))
     )))
