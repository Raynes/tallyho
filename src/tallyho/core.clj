(ns tallyho.core
  (:use seesaw.core
        [clojure.string :only [join]])
  (:import [javax.swing JTable JOptionPane]
           javax.swing.table.DefaultTableModel
           java.awt.event.MouseListener)
  (:gen-class))

(def table-model
     (proxy [DefaultTableModel] [(to-array-2d []) (object-array ["name" "score"])]
       (isCellEditable [row column] false)))

(def s-table (JTable. table-model))

(defn calculate-score [[calc & digits :as all] old]
  (let [old (Integer/parseInt (str old))
        digits (when digits (Integer/parseInt (join digits)))]
    (cond
     (= \+ calc) (+ old digits)
     (= \- calc) (- old digits)
     :else (+ old (Integer/parseInt (join all))))))

(defn validate [s]
  (when-not (empty? s) s))

(defn calc-new-score [old]
  (when-let [new-score (validate (JOptionPane/showInputDialog "Enter -number or +number."))]
    
    (if-let [result (try
                      (calculate-score new-score old)
                      (catch NumberFormatException e))]
      result
      (do
        (alert "Enter a real number, dude.")
        (recur old)))))

(def on-click
     (proxy [MouseListener] []
       (mouseClicked
        [e]
        (let [row (.rowAtPoint s-table (.getPoint e))
              score (.getValueAt s-table row 1)]
          (when-let [new-score (calc-new-score score)]
            (.setValueAt table-model new-score row 1))))
       (mouseEntered  [_])
       (mouseExited   [_])
       (mousePressed  [_])
       (mouseReleased [_])))

(def score-table
     (doto s-table
       (.setFillsViewportHeight true)
       (.addMouseListener on-click)))

(def selection-model (.getSelectionModel score-table))

(def scroll-pane (scrollable score-table))

(defn add-user [e]
  (.addRow
   table-model
   (object-array [(JOptionPane/showInputDialog "Enter the player's name.") "0"])))

(def menus
     (let [add-user (action :handler add-user :name "Add User")]
       (menubar :items [(menu :text "Tallyho" :items [add-user])])))

(def main-panel
     (mig-panel
      :constraints ["ins 0"]
      :items [[scroll-pane ""]]))

(defn -main [& args]
  (invoke-now
   (frame
    :title "TallyHOOOOOOO"
    :content main-panel
    :on-close :dispose
    :menubar menus)))