(defproject concert-chute "0.1.0-SNAPSHOT"
  :description "Download concert data and email it to me."
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [org.clojure/tools.cli "0.4.2"]
                 [clojure.java-time "0.3.2"]]
  :main ^:skip-aot concert-chute.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
