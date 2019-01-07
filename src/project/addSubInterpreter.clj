(ns project.core)

(load "constInterpreter")
(load "addSubCompiler")

(use 'instaparse.core)

(def addsubmod-parser
  (instaparse.core/parser
   "prog= spaces add-sub-mod spaces
    <add-sub-mod>= number | add | sub | mod
    mod= add-sub-mod spaces <'%'> spaces number
    add= add-sub-mod spaces <'+'> spaces number
    sub= add-sub-mod spaces <'-'> spaces number
    number= #'-?[0-9]+'
    <spaces>= <#'\\s*'>"))

(def addsubmod-interpreting
  (assoc mod-interpreting :add + :sub -))

(def addsubmod-eval (dynamic-eval addsubmod-interpreting))

(defn interpreting-add-sub-mod [mod expression] (-> (str (mod-exp expression mod) "%" mod) addsubmod-parser addsubmod-eval))

(defn compiling-add-sub-mod [mod expression] (rem-sign (-> (str (mod-exp expression mod) "%" mod) addsubmod-parser addsubmod-compiler) mod))
