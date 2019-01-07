(ns project.core)

(use 'instaparse.core)

(import '(clojure.asm Opcodes Type ClassWriter))
(import '(clojure.asm.commons Method GeneratorAdapter))

(defn compiled [n-args class-name bytecode-generator]
    (let [cw (ClassWriter. (+ ClassWriter/COMPUTE_FRAMES ClassWriter/COMPUTE_MAXS ))
          init (Method/getMethod "void <init>()")
          meth-name "run"
          meth-sig (str "(" (apply str (repeat n-args "J")) ")J")]
      (.visit cw Opcodes/V1_6 Opcodes/ACC_PUBLIC (.replace class-name \. \/) nil "java/lang/Object" nil)
      (doto (GeneratorAdapter. Opcodes/ACC_PUBLIC init nil nil cw)
        (.visitCode)
        (.loadThis)
        (.invokeConstructor (Type/getType Object) init)
        (.returnValue)
        (.endMethod))
      (doto (.visitMethod cw (+ Opcodes/ACC_PUBLIC Opcodes/ACC_STATIC) meth-name meth-sig nil nil )
        (.visitCode)
        (bytecode-generator)
        (.visitMaxs 0 0 )
        (.visitEnd))
      (.visitEnd cw)
      (let [b (.toByteArray cw)
            cl (clojure.lang.DynamicClassLoader.)]
        (.defineClass cl class-name b nil))
      (fn [& args] (clojure.lang.Reflector/invokeStaticMethod class-name meth-name (into-array args))))
    )

(defn bytecode-generating-eval [n-args class-name compiling instr-generating]
  (fn[ast]
  (let[instrs (instaparse.core/transform compiling ast)
       generate-prog (fn[mv] (reduce instr-generating mv instrs))]
    (compiled n-args class-name generate-prog))))

(defn assoc-binary-op [m [op instr]]
  (let[binary-op-compiling (fn[op]
                             (fn[instrs-v0 instrs-v1]
         (conj (into instrs-v0 instrs-v1) [op])))]
    (assoc m op (binary-op-compiling instr))))

(def const-compiling
  {:prog (fn[& instrs](conj (reduce into [[:loadi 0]] instrs)[:reti]))
   :number #(vector [:loadi (Long/parseLong %)])})

(def mod-compiling
  (reduce assoc-binary-op const-compiling [[:mod :modi]]))

(defmulti generate-instr (fn [mv [instr & args]] instr))
(defn dispatching-bytecode-generating-eval [n-args class-name compiling]
  (fn[ast]
    (let[instrs (instaparse.core/transform compiling ast)
         generate-prog (fn[mv] (reduce generate-instr mv instrs))]
      (compiled n-args class-name generate-prog))))

(defmethod generate-instr :loadi [mv [instr & args]]
  (doto mv
    (.visitLdcInsn (first args))))

(defmethod generate-instr :reti [mv [instr & args]]
  (doto mv
    (.visitInsn Opcodes/LRETURN)))

(defmethod generate-instr :modi [mv [instr & args]]
  (doto mv
    (.visitInsn Opcodes/LREM)))

(def const-compiler  (dispatching-bytecode-generating-eval 0 "ConstCompiler" const-compiling))

(def mod-compiler (dispatching-bytecode-generating-eval 0 "ModCompiler" mod-compiling))
