(ns clojure-googlesheets.core
  (:gen-class)
  (:require [mount.core :as mount]
            [hugsql.core :as hugsql]
            [clojure.java.jdbc :as jdbc]
            [clojure.java.jdbc :as sql]
            [clojure.string :as string]
            [clojure.java.io :as io])
  (:import com.google.gdata.client.spreadsheet.SpreadsheetService
           com.google.gdata.data.spreadsheet.SpreadsheetFeed
           com.google.gdata.data.spreadsheet.WorksheetFeed
           com.google.gdata.data.spreadsheet.CellFeed
           com.google.api.client.googleapis.auth.oauth2.GoogleCredential
           com.google.api.client.json.jackson2.JacksonFactory
           com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
           java.net.URL
           java.util.Collections))


(def application-name "Playcards")

;change filename to yours for oauth google api
(def credentials-resource (io/resource "gmavenproject-931d7910422a.json"))

(def oauth-scope "https://spreadsheets.google.com/feeds")
;(def oauth-scope "https://www.googleapis.com/auth/spreadsheets")


(def spreadsheet-feed-url (URL. "https://spreadsheets.google.com/feeds/spreadsheets/private/full"))

(defn get-credential
  []
  (with-open [in (io/input-stream credentials-resource)]
    (let [credential (GoogleCredential/fromStream in)]
      (.createScoped credential (Collections/singleton oauth-scope)))))

(defn init-service
  []
  (let [credential (get-credential)
        service (SpreadsheetService. application-name)]
    (.setOAuth2Credentials service credential)
    service))

(defn list-spreadsheets
  [service]
  (.getEntries (.getFeed service spreadsheet-feed-url SpreadsheetFeed)))

(defn find-spreadsheet-by-title
  [service title]
  (let [spreadsheets (filter (fn [sheet] (= (.getPlainText (.getTitle sheet)) title))
                             (list-spreadsheets service))]
    (if (= (count spreadsheets) 1)
      (first spreadsheets)
      (throw (Exception. (format "Found %d spreadsheets with name %s"
                                 (count spreadsheets)
                                 title))))))

(defn list-worksheets
  [service spreadsheet]
  (.getEntries (.getFeed service (.getWorksheetFeedUrl spreadsheet) WorksheetFeed)))

(defn find-worksheet-by-title
  [service spreadsheet title]
  (let [worksheets (filter (fn [ws] (= (.getPlainText (.getTitle ws)) title))
                           (list-worksheets service spreadsheet))]
    (if (= (count worksheets) 1)
      (first worksheets)
      (throw (Exception. (format "Found %d worksheets in %s with name %s"
                                 (count worksheets)
                                 spreadsheet
                                 title))))))

(defn get-cells
  [service worksheet]
  (map (memfn getCell) (.getEntries (.getFeed service (.getCellFeedUrl worksheet) CellFeed))))

(defn to-nested-vec
  [cells]
  (mapv (partial mapv (memfn getValue)) (partition-by (memfn getRow) cells)))

(defn fetch-worksheet
  [service {spreadsheet-title :spreadsheet worksheet-title :worksheet}]
  (if-let [spreadsheet (find-spreadsheet-by-title service spreadsheet-title)]
    (if-let [worksheet (find-worksheet-by-title service spreadsheet worksheet-title)]
      (to-nested-vec (get-cells service worksheet))
      (throw (Exception. (format "Spreadsheet '%s' has no worksheet '%s'"
                                 spreadsheet-title worksheet-title))))
    (throw (Exception. (format "Spreadsheet '%s' not found" spreadsheet-title)))))

(def gservice (init-service))

(def sheet (fetch-worksheet gservice {:spreadsheet "Playcards" :worksheet "Sheet1"}))

(def spec
  {:classname   "org.sqlite.JDBC"
   :subprotocol "sqlite"
   :subname     ":memory:"})

(def db-uri "jdbc:sqlite::memory:")

(declare db)

(defn on-start []
  (let [spec {:connection-uri db-uri}
        conn (jdbc/get-connection spec)]
    (assoc spec :connection conn)))

(defn on-stop []
  (-> db :connection .close)
  nil)

(mount/defstate
  ^{:on-reload :noop}
  db
  :start (on-start)
  :stop (on-stop))

(mount/start #'db)

;(mount/stop #'db)

(jdbc/execute! db "create table playingcards (id integer,matchnumber integer,picktimes integer,pickedcards text,remainingcards text)")


(defn getMaxMatchNumber
[]
(or (:matchnumber (first (sql/query db ["select max(matchnumber) as matchnumber from playingcards"]))) 0)
)


(def MaxMatchNumber
#(+ (getMaxMatchNumber) %)
)

;(MaxMatchNumber)
;(getMaxMatchNumber)
;(MaxMatchNumber 1)

(defn getMaxID
[]
(or (:id (first (sql/query db ["select max(id) as id from playingcards"]))) 0)
)

;(getMaxID)
;(+ (getMaxID) 1)

(defn insertDB
[matchnumber pickedCards remainingCards picktimes]
(jdbc/insert! db :playingcards {:id (+ (getMaxID) 1) :matchnumber matchnumber :picktimes picktimes :pickedcards pickedCards :remainingcards remainingCards})
(println "Inserted 1 record to database successfully !")
)

(defrecord StampedData
  [matchnumber
   picktimes
   pickedcards
   remainingcards
   ])

(defn ^StampedData stamped-data
  [[matchnumber picktimes pickedcards remainingcards]]
  (StampedData. matchnumber picktimes pickedcards remainingcards))

(defn add-database
[record]
  (let [data (stamped-data record)]
    (insertDB (:matchnumber data) (:pickedcards data) (:remainingcards data) (:picktimes data)
    )
  )
)


(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (let [sheetdata (drop 1 sheet)]
    (map #(add-database %) sheetdata)
   )
)

;(sql/query db ["select * from playingcards where matchnumber=1"])
