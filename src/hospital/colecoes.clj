(ns hospital.colecoes
  (:use [clojure pprint]))

  ; VETOR
(defn testa-vetor []
  (let [espera [111 222]]
    (println "vetor")
    (println espera)
    (println (conj espera 333))
    (println (conj espera 444))
    (println (pop espera))
  ))

(testa-vetor)

; Para vetor:
; 'conj' adiciona elemento ao final do vetor, porém não altera o vetor original
; (Por isso, o 333 não é mantido na próxima chamada)
; 'pop' remove último elemento do vetor



; LISTA
(defn testa-lista []
  (let [espera '(111 222)]
    (println "lista")
    (println espera)
    (println (conj espera 333))
    (println (conj espera 444))
    (println (pop espera))
    ))

(testa-lista)

; Para lista:
; 'conj' adiciona elemento ao começo da lista, porém não altera o vetor original
; Por isso, o 333 não é mantido na próxima chamada
; 'pop' adiciona elemento ao começo da lista



; CONJUNTO
(defn testa-conjunto []
  (let [espera #{111 222}]
    (println "conjunto")
    (println espera)
    (println (conj espera 111))                             ; o 111 é ignorado neste caso, pois já existe no conjunto
    (println (conj espera 333))
    (println (conj espera 444))
    ;(println (pop espera))
    ))

(testa-conjunto)

; Como conjunto não tem uma ordem definida, o 'pop' não é implementado nesta estrutura de dados (set/conjunto)
; E o retorno acontece sem respeitar a ordem
; A estrutura de dados precisa implementar uma interface de pilha



; FILAS
(defn testa-fila []
  (let [espera (conj clojure.lang.PersistentQueue/EMPTY "111" "222")] ; os elementos são adicionados como str
    (println "fila")
    (println (seq espera))                                  ; Para imprimir, precisamos transformar em sequência (seq)
    (println (seq (conj espera "333")))
    (println (seq (pop espera)))                            ; 'pop' em fila retira o primeiro elemento
    (println (peek espera))                                 ; 'peek' retorna o primeiro elemento
    (pprint espera)                                         ; usando a lib 'pretty print'
    ))

(testa-fila)

; Para usar fila em Clojure, usamos a expressão "clojure.lang.PersistentQueue/EMPTY"

; Com a lib (pprint -> pretty print) que chamamos no início do arquivo, podemos melhorar a impressão
; saída: <-("111" "222")-< indica que é uma fila