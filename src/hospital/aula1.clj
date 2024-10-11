(ns hospital.aula1
  (:require [hospital.logic :as h.logic]
            [hospital.model :as h.model])
  (:use [clojure.pprint]))

(defn simula-um-dia []
  ; root binding -> valor do símbolo
  (def hospital (h.model/novo-hospital))
  (h.logic/chega-em hospital :espera "001")                 ; respectivamente: hospital, depto e pessoa
  (h.logic/chega-em hospital :espera "002")
  (h.logic/chega-em hospital :espera "003")
  (pprint hospital)                                         ; se imprimirmos dessa forma, o retorno será de fila vazia
  )

(simula-um-dia)
; É necessário redefinir a função toda vez que quiser atribuir um novo valor
; porém, ainda não é o melhor metodo, pois o root binding está sendo alterado a cada nova inclusão

(defn simula-um-dia []
  ; root binding -> valor do símbolo
  (def hospital (h.model/novo-hospital))
  (def hospital (h.logic/chega-em hospital :espera "001"))
  (def hospital (h.logic/chega-em hospital :espera "002"))
  (def hospital (h.logic/chega-em hospital :espera "003"))
  (pprint hospital)

  (def hospital (h.logic/chega-em hospital :laboratorio1 "004"))
  (def hospital (h.logic/chega-em hospital :laboratorio3 "005"))
  (pprint hospital)

  (pprint (h.logic/atende hospital :laboratorio1))
  (pprint (h.logic/atende hospital :espera))
  (pprint hospital)

  (def hospital (h.logic/chega-em hospital :espera "006"))
  (def hospital (h.logic/chega-em hospital :espera "007"))
  (def hospital (h.logic/chega-em hospital :espera "008"))
  (pprint hospital)

  )

;(simula-um-dia)



; Reformatando com duas threadings -> espaço de memória compartilhado

(defn simula-um-dia-em-paralelo []
  (def hospital (h.model/novo-hospital))
  (.start (Thread. (fn [] (h.logic/chega-em hospital :espera "001"))))
  (.start (Thread. (fn [] (h.logic/chega-em hospital :espera "002"))))
  (.start (Thread. (fn [] (h.logic/chega-em hospital :espera "003"))))
  (.start (Thread. (fn [] (h.logic/chega-em hospital :espera "004"))))
  (.start (Thread. (fn [] (h.logic/chega-em hospital :espera "005"))))
  (.start (Thread. (fn [] (h.logic/chega-em hospital :espera "006"))))
  (.start (Thread. (fn [] (Thread/sleep 4000)               ; 'sleep'-> Metodo estatico de Java (espera 4s)
                     (pprint hospital)))))

(simula-um-dia-em-paralelo)



; Redefinindo

(defn chega-em-malvado [pessoa]
  (def hospital (h.logic/chega-em-pausado hospital :espera pessoa))
  (println "apos inserir" pessoa))

; Problema de variável global (símbolo do namespace) compartilhado
(defn simula-um-dia-em-paralelo []
  (def hospital (h.model/novo-hospital))
  (.start (Thread. (fn [] (chega-em-malvado "001"))))
  (.start (Thread. (fn [] (chega-em-malvado "002"))))
  (.start (Thread. (fn [] (chega-em-malvado "003"))))
  (.start (Thread. (fn [] (chega-em-malvado "004"))))
  (.start (Thread. (fn [] (chega-em-malvado "005"))))
  (.start (Thread. (fn [] (chega-em-malvado "006"))))
  (.start (Thread. (fn [] (Thread/sleep 4000)
                     (pprint hospital)))))

(simula-um-dia-em-paralelo)

; Para várias execuções em paralelo -> através da classe Threading
; (.start) chama o metodo 'start'
; (Thread.) -> cria um objeto (que é uma threading), chamando o construtor 'new' da classe Threading em Java
; Em seguida, passamos como argumento algo que implementa 'runable' -> uma função que não retorna nada
; Em Clojure, as funções já implementam runable e podem ser passadas como parâmetro p/ uma threading

; Notamos problemas de -concorrência- quando trabalhamos com variáveis e espaço de memória compartilhado