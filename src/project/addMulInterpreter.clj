(ns project.core)

(load "addMulCompiler")
(load "constInterpreter")

(def addmult-parser
  (instaparse.core/parser
   "prog= spaces add-sub-mod spaces
    <add-sub-mod>= mult-div | add | sub | mod
    add= add-sub-mod spaces <'+'> spaces mult-div
    sub= add-sub-mod spaces <'-'> spaces mult-div
    mod= add-sub-mod spaces <'%'> spaces mult-div
    <mult-div>= number | mult | div
    mult= mult-div spaces <'*'> spaces number
    div= mult-div spaces <'/'> spaces number
    number= #'-?[0-9]+'
    <spaces>= <#'\\s*'>"))

(def addmult-interpreting (assoc addsubmod-interpreting :mult * :div /))

(def addmult-eval (dynamic-eval addmult-interpreting ))

(defn interpreting-add-mult-mod [mod expression] (-> (str (mod-exp expression mod) "%" mod) addmult-parser addmult-eval))

(defn compiling-add-mult-mod [mod expression] (rem-sign (-> (str (mod-exp expression mod) "%" mod) addmult-parser addmult-compiler) mod))
