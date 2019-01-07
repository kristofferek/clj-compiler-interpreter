(ns project.core)

(load "lang1Compiler")

(use 'instaparse.core)

(def lang1-parser
  (instaparse.core/parser
   "prog = (spaces expr spaces <';'> spaces)*
      <expr> = assig | add-sub-mod
      assig = varname spaces <'='> spaces expr
      <add-sub-mod> = mult-div | add | sub | mod
      add = add-sub-mod spaces <'+'> spaces mult-div
      sub = add-sub-mod spaces <'-'> spaces mult-div
      mod = add-sub-mod spaces <'%'> spaces mult-div
      <mult-div> = factor | mult |div
      mult = mult-div spaces <'*'> spaces factor
      div = mult-div spaces <'/'> spaces factor
      <factor> = number | <'('> spaces expr spaces <')'> | varget |assig
      <spaces> = <#'\\s*'>
      number = #'-?[0-9]+'
      varget = varname | argument
      varname = #'[a-zA-Z]\\w*'
      argument= <'$'>#'[0-9]+'"))

(defn args-to-env[args]
  (into {} (map-indexed #(vector (keyword (str "$" %1)) %2) args)))

(defn dynamic-eval-args [make-interpreter]
  (fn[ast]
    (fn[& args]
      (instaparse.core/transform (make-interpreting make-interpreter
                                          (assoc (args-to-env args)
                                                 :_ret 0))
                       ast))))

(defn make-lang1-instr-interpreting [env]
  (assoc (make-lang0-instr-interpreting env)
        :argument #(assoc env :_ret (keyword (str "$" %)))))

(def lang1-interpreter (dynamic-eval-args make-lang1-instr-interpreting))

(defn interpreting-lang1-mod [mod expression] (-> (outer-mod (mod-exp (exponent expression mod) mod) mod) lang1-parser lang1-interpreter))

(defn compiling-lang1-mod [mod expression] (rem-sign (->> (outer-mod (mod-exp (exponent expression mod) mod) mod) lang1-parser (lang1-compiler-chain "Lang1Compiler")) mod))
