(ns project.core)

(load "addMulInterpreter")
(load "addMulCompiler")

(use 'instaparse.core)

(def arithmod-parser
  (instaparse.core/parser
   "prog = spaces add-sub-mod spaces
    <add-sub-mod> = mult-div | add | sub | mod
    add = add-sub-mod spaces <'+'> spaces mult-div
    sub = add-sub-mod spaces <'-'> spaces mult-div
    mod = add-sub-mod spaces <'%'> spaces mult-div
    <mult-div> = term | mult | div
    mult = mult-div spaces <'*'> spaces term
    div = mult-div spaces <'/'> spaces term
    <term> = number | <'('> add-sub-mod <')'>
    <spaces> = <#'\\s'*>
    number = #'-?[0-9]+'"))

(defn interpreting-arith-mod [mod expression] (-> (str (mod-exp expression mod) "%" mod) arithmod-parser addmult-eval))

(defn compiling-arith-mod [mod expression] (rem-sign (-> (str (mod-exp expression mod) "%" mod) arithmod-parser addmult-compiler) mod))

; ARITH MOD WITH EXPONENTS
(defn modpow [b e m]
  (.modPow (biginteger b) (biginteger e) (biginteger m)))

(defn exponent[expression m]
  (clojure.string/replace expression #"(\d+)[ ]*\^[ ]*(\d+)"
     (fn [[_ n exp ]]
       (str (modpow n exp m)))))

(defn interpreting-arith-exp-mod [mod expression] (-> (str (mod-exp (exponent expression mod) mod) "%" mod) arithmod-parser addmult-eval))

(defn compiling-arith-exp-mod [mod expression] (rem-sign (-> (str (mod-exp (exponent expression mod) mod) "%" mod) arithmod-parser addmult-compiler) mod))
