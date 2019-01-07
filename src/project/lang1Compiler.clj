(ns project.core)

(use 'instaparse.core)

(defn nb-args[ast]
  (inc (instaparse.core/transform (assoc (replace-vals
                           lang0-compiling (fn[& args]
                                             (apply max (conj (filter number? args)
                                                              -1))))
                          :argument #(Long/parseLong %))
                   ast)))

(defn args->varnum[ast]
  (instaparse.core/transform {:argument #(* 2 (Long/parseLong %))} ast))

(defn lang1-compiler-chain[class-name ast]
  (let[n-args (nb-args ast)
       compiler (dispatching-bytecode-generating-eval n-args class-name lang0-compiling)]
    (->> ast args->varnum (to-numeric-vars n-args) compiler)))
