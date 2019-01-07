(ns project.core)

(load "constCompiler")

(use 'instaparse.core)

(def addsubmod-compiling
  (reduce assoc-binary-op mod-compiling [[:add :addi][:sub :subi]]))

(defmethod generate-instr :addi [mv [instr & args]]
  (doto mv
    (.visitInsn Opcodes/LADD)))

(defmethod generate-instr :subi [mv [instr & args]]
  (doto mv
    (.visitInsn Opcodes/LSUB)))

(def addsubmod-compiler (dispatching-bytecode-generating-eval 0 "AddsubmodCompiler" addsubmod-compiling))
