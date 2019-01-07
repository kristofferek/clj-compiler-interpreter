(ns project.core)

(load "constCompiler")
(load "addSubCompiler")

(def addmult-compiling
  (reduce assoc-binary-op addsubmod-compiling [[:mult :multi][:div :divi]]))

(defmethod generate-instr :multi [mv [instr & args]]
  (doto mv
    (.visitInsn Opcodes/LMUL)))

(defmethod generate-instr :divi [mv [instr & args]]
  (doto mv
    (.visitInsn Opcodes/LDIV)))

(def addmult-compiler (dispatching-bytecode-generating-eval 0 "AddmultCompiler" addmult-compiling))
