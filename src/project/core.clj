(ns project.core
  (:gen-class))

(load "constInterpreter")
(load "addSubInterpreter")
(load "addSubCompiler")
(load "addMulInterpreter")
(load "addMulCompiler")
(load "arithInterpreter")
(load "lang0Interpreter")
(load "lang0Compiler")
(load "lang1Interpreter")
(load "lang1Compiler")

(use 'instaparse.core)

; CONST TESTS
(println "CONST")
(def interpreted-const-mod (interpreting-const-mod 12345 "-123456012312312312"))
(println (str "Interpreter: " (interpreted-const-mod)))

(def compiled-const-mod (compiling-const-mod 12345 "-123456012312312312"))
(println (str "Compiler: " (compiled-const-mod)))
(println "")

; ADDSUB TESTS
(println "ADDSUB")
(def interpreted-add-sub-mod (interpreting-add-sub-mod 123456 "1234567890123456789 + 12345432124356 - 98765543210123456789"))
(println (str "Interpreter: " (interpreted-add-sub-mod)))

(def compiled-add-sub-mod (compiling-add-sub-mod 123456 "1234567890123456789 + 12345432124356 - 98765543210123456789"))
(println (str "Compiler: " (compiled-add-sub-mod))) ;; => 29053?
(println "")

; ADDMUL TESTS
(println "ADDMUL")
(def interpreted-add-mult-mod (interpreting-add-mult-mod 1234567 "1234567890123456789 + 12345432124356 * -98765543210123456789"))
(println (str "Interpreter: " (interpreted-add-mult-mod)))

(def compiled-add-mult-mod (compiling-add-mult-mod 1234567 "1234567890123456789 + 12345432124356 * -98765543210123456789"))
(println (str "Compiler: " (compiled-add-mult-mod))) ; 639208
(println "")

; ; ARITH TESTS
(println "ARITH")
(def interpreted-arith-mod (interpreting-arith-mod 123456 "(1234567890123456789 + 12345432124356) * -98765543210123456789"))
(println (str "Interpreter: " (interpreted-arith-mod))) ; 25395

(def compiled-arith-mod (compiling-arith-mod 123456 "(1234567890123456789 + 12345432124356) * -98765543210123456789"))
(println (str "Compiler: " (compiled-arith-mod))) ; 25395
(println "")

; ARITH EXP TESTS
(println "ARITH w EXP")
(def interpreted-arith-exp-mod (interpreting-arith-exp-mod 12345 "12345432124356012031023123 ^ 9876554321012345678912312312341240102102032023023123123999999999999999999999999999999"))
(println (str "Interpreter: " (interpreted-arith-exp-mod)))

(def compiled-arith-exp-mod (compiling-arith-exp-mod 12345 "12345432124356012031023123 ^ 9876554321012345678912312312341240102102032023023123123999999999999999999999999999999"))
(println (str "Compiler: " (compiled-arith-exp-mod)))
(println "")

; LANG0
(println "LANG0")
(def interpreted-lang0-mod (interpreting-lang0-mod 12345 "a= 1234567890123456788+1; b=9876543210123345680 - 2; a*b;"))
(println (str "Interpreter: " (interpreted-lang0-mod)))

(def compiled-lang0-mod (compiling-lang0-mod 12345 "a= 1234567890123456788+1; b=9876543210123345680 - 2; a*b;"))
(println (str "Compiler: " (compiled-lang0-mod)))
(println "")

; LANG1
(println "LANG1")
(def interpreted-lang1-mod (interpreting-lang1-mod 12345 "a=$0;a + $1 *3;"))
(println (str "Interpreter: " (interpreted-lang1-mod 12345 -7)))

(def compiled-lang1-mod (compiling-lang1-mod 12345 "a=$0;a + $1 *3;"))
(println (str "Compiler: " (compiled-lang1-mod 12345 -7)))
(println "")

(defn -main
  "Dummy main"
  [& args]
  (println "All tests passed"))
