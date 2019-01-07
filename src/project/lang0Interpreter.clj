(ns project.core)

(load "lang0Compiler")
(load "arithInterpreter")

(use 'instaparse.core)

(def lang0-parser
  (instaparse.core/parser
   "prog = (spaces expr spaces <';'> spaces)*
    <expr> = assig | add-sub
    assig = varname spaces <'='> spaces expr
    <add-sub> = mult-div | add | sub | mod
    add = add-sub spaces <'+'> spaces mult-div
    sub = add-sub spaces <'-'> spaces mult-div
    mod = add-sub spaces <'%'> spaces mult-div
    <mult-div> = factor | mult |div
    mult = mult-div spaces <'*'> spaces factor
    div = mult-div spaces <'/'> spaces factor
    <factor> = number | <'('> spaces expr spaces <')'> | varget |assig
    <spaces> = <#'\\s*'>
    number = #'-?[0-9]+'
    varget = varname
    varname = #'[a-zA-Z]\\w*'"))

(defn outer-mod [exp m]
  (clojure.string/replace exp #";$" (str "%" m ";")))

(defn make-interpreting [make-instr-interpreting init-env]
  {:prog (fn [& instrs] (:_ret (reduce
                                       (fn[env instr]
                                         (instaparse.core/transform (make-instr-interpreting env) instr))
                                       init-env
                                       instrs)))})

(defn make-lang0-instr-interpreting [env]
  { :assig (fn[{varname :_ret :as env1} {value :_ret :as env2}]
            (assoc (merge env1 env2) varname value :_ret value))
    :add (fn[{v1 :_ret :as env1} {v2 :_ret :as env2}]
            (assoc (merge env1 env2) :_ret (+ v1 v2)))
    :sub (fn[{v1 :_ret :as env1} {v2 :_ret :as env2}]
            (assoc (merge env1 env2) :_ret (- v1 v2)))
    :mod (fn[{v1 :_ret :as env1} {v2 :_ret :as env2}]
            (assoc (merge env1 env2) :_ret (mod v1 v2)))
    :mult (fn[{v1 :_ret :as env1} {v2 :_ret :as env2}]
            (assoc (merge env1 env2) :_ret (* v1 v2)))
    :div (fn[{v1 :_ret :as env1} {v2 :_ret :as env2}]
            (assoc (merge env1 env2) :_ret (quot v1 v2)))
    :number #(assoc env :_ret (Long/parseLong %))
    :varname #(assoc env :_ret (keyword %))
    :varget (fn [{varname :_ret :as env1}]
              (assoc env1 :_ret (varname env1)))})

(def lang0-interpreter (dynamic-eval (make-interpreting make-lang0-instr-interpreting {:_ret 0})))

(defn interpreting-lang0-mod [mod expression] (-> (outer-mod (mod-exp (exponent expression mod) mod) mod) lang0-parser lang0-interpreter))

(defn compiling-lang0-mod [mod expression] (rem-sign (->> (outer-mod (mod-exp (exponent expression mod) mod) mod) lang0-parser (to-numeric-vars 0) lang0-compiler) mod))
