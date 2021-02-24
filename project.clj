(defproject clojure-googlesheets "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.1"]
				 [com.google.gdata/gdata-core "1.0"]
                 [com.google.gdata/gdata-spreadsheet "3.0"]
				 [com.google.api-client/google-api-client "1.28.0"]
				 [org.clojure/math.numeric-tower "0.0.4"] 
				 [org.xerial/sqlite-jdbc "3.20.0"] 
				 [org.clojure/java.jdbc "0.7.0"] 
				 [com.layerware/hugsql "0.4.7"] 
				 [mount "0.1.11"]]
  :main ^:skip-aot clojure-googlesheets.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}})
