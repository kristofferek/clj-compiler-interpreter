(ns project.core)

(use 'instaparse.core)
(use 'clojure.set)

(def lang0-compiling
  (assoc addmult-compiling
         :varget #(vector [:load %])
         :assig (fn[var instrs](conj instrs [:store var]))))


;; helper function that replaces all the values in map m with the given value v
(defn replace-vals [m v]
 (into {} (map vector (keys m) (repeat v ))))

(defn to-numeric-vars [nb-args ast]
  (let[varnames
      (instaparse.core/transform
        (assoc (replace-vals
                lang0-compiling
                (fn[& instrs] (apply clojure.set/union (filter set? instrs))))
               :varname (fn[varname]#{varname}))
       ast)
      name->num (into {} (map vector varnames (iterate (comp inc inc) (* 2 nb-args))))]
  (instaparse.core/transform {:varname #(get name->num %)} ast)))

(defmethod generate-instr :load [mv [instr & args]]
  (doto mv
    (.visitVarInsn Opcodes/LLOAD (first args))))

(defmethod generate-instr :store [mv [instr & args]]
  (doto mv
    (.visitInsn Opcodes/DUP2)
    (.visitVarInsn Opcodes/LSTORE (first args))))

(def lang0-compiler (dispatching-bytecode-generating-eval 0 "Lang0Compiler" lang0-compiling))
