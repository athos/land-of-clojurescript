(ns land-of-clojurescript.test
  (:require [doo.runner :refer-macros [doo-tests]]
            land-of-clojurescript.core-test))

(doo-tests 'land-of-clojurescript.core-test)
