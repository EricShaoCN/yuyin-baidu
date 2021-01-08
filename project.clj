(defproject yuyin-baidu "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [clj-http "3.9.1"]
                 [ring/ring-codec "1.1.1"]
                 ]
  :resource-paths ["resources" "lib/aip-java-sdk-4.1.1.jar" "lib/json-20160810.jar" "lib/log4j-1.2.17.jar"]
  :repl-options {:init-ns yuyin-baidu.core})
