(ns project.core)

(load "constCompiler")

(use 'instaparse.core)

(def const-parser
  (instaparse.core/parser
   "prog= spaces number spaces
    number=#'-?[0-9]+'
    <spaces> = <#'[ ]'*>"))

(def mod-parser
  (instaparse.core/parser
   "prog= spaces mod spaces
    mod= const | const <'%'> const
    <const>= spaces number spaces
    number=#'-?[0-9]+'
    <spaces> = <#'[ ]'*>"))

(defn mod-exp [exp val]
  (clojure.string/replace exp #"\d+" #(str (mod (biginteger %1) val))  ))

(defn rem-sign [m b]
  (fn [& a2](str (if (< (long (apply m a2)) 0) (if (< b 0) (- (long (apply m a2)) b) (+ (long (apply m a2)) b)) (long (apply m a2))))))


(defn dynamic-eval [interpreter]
    (fn[ast]
      (fn[]
        (instaparse.core/transform interpreter ast))))

(def const-interpreting
  {:prog identity
   :number #(Long/parseLong %)})

(def mod-interpreting
  (assoc const-interpreting :mod mod))

(def const-eval (dynamic-eval const-interpreting))

(def mod-eval (dynamic-eval mod-interpreting))

(defn interpreting-const-mod [mod nbr] (-> (str (mod-exp nbr mod) "%" mod) mod-parser mod-eval))

(defn compiling-const-mod [mod nbr] (rem-sign (-> (str (mod-exp nbr mod) "%" mod) mod-parser mod-compiler) mod))
