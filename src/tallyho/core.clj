(ns tallyho.core
  (:use seesaw.core
        [clojure.string :only [join]])
  (:import [javax.swing JOptionPane JTable]
           javax.swing.table.DefaultTableModel
           java.awt.event.MouseEvent)
  (:gen-class))

(def table-model
     (proxy [DefaultTableModel] [(to-array-2d []) (object-array ["name" "score"])]
       (isCellEditable [row column] false)))

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
  (when-let [new-score
             (validate
              (JOptionPane/showInputDialog
               "Enter -number to decrease score.\nEnter either +number or number to increase score."))]
    
    (if-let [result (try
                      (calculate-score new-score old)
                      (catch NumberFormatException e))]
      result
      (do
        (alert "Enter a real number, dude.")
        (recur old)))))

(defn on-table-click
  [e]
  (when (and (= (.getButton e) MouseEvent/BUTTON1) (= 2 (.getClickCount e)))
    (let [s-table (to-widget e)
          row (.rowAtPoint s-table (.getPoint e))]
      (when (>= row 0)
        (when-let [new-score (calc-new-score (.getValueAt s-table row 1))]
          (.setValueAt table-model new-score row 1))))))

(declare score-table)

(defn add-user [e]
  (when-let [user (validate (JOptionPane/showInputDialog "Enter the player's name."))]
    (.addRow
     table-model
     (object-array [user "0"]))))

(defn delete-user [e]
  (when-let [row (selection score-table)]
    (.removeRow table-model row)))

(defn confirm []
  (= JOptionPane/YES_OPTION
     (JOptionPane/showConfirmDialog
      score-table
      "Are you sure?" "Seriously?"
      JOptionPane/YES_NO_OPTION)))

(defn reset-scores [e]
  (when (confirm)
    (doseq [row (range 0 (.getRowCount score-table))]
      (.setValueAt table-model 0 row 1))))

(defn reset-game [e]
  (when (confirm)
    (.setRowCount table-model 0)))

(def add-user-action (action :handler add-user :name "Add Player"))

(def delete-user-action (action :handler delete-user :name "Delete Player"))

(def reset-scores-action (action :handler reset-scores :name "Reset Scores"))

(def reset-game-action (action :handler reset-game :name "Remove All Players"))

(def score-table
     (doto
         (table :model table-model 
                :listen [:mouse-clicked on-table-click]
                :popup (fn [e] [add-user-action delete-user-action
                                reset-scores-action reset-game-action])
                :font "ARIAL-PLAIN-14")
       (.setFillsViewportHeight true)
       (.setRowHeight 20)))

(def scroll-pane (scrollable score-table))

(def menus (menubar :items
                    [(menu :text "Tallyho"
                           :items [add-user-action delete-user-action
                                   reset-scores-action reset-game-action])]))

(def main-panel
     (mig-panel
      :constraints ["fill, ins 0"]
      :items [[scroll-pane "grow"]]))

(defn -main [& args]
  (invoke-now
   (frame
    :title "TallyHOOOOOOO"
    :content main-panel
    :on-close :dispose
    :menubar menus)))
