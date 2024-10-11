(ns hospital.aula4
  (:use [clojure.pprint])
  (:require [hospital.model :as h.model]
            [hospital.logic :as h.logic]))

(defn chega-sem-malvado! [hospital pessoa]
  (swap! hospital h.logic/chega-em :espera pessoa)
  (println "apos inserir" pessoa))

(defn simula-um-dia-em-paralelo []

  (let [hospital (atom (h.model/novo-hospital))
        pessoas ["111", "222", "333", "444", "555", "666"]]

    ; Utilizando 'map' para rodar o vetor
    ; Modificamos o código anterior, tranformando o (.start (.Thread)) em lambda, add o # na frente e % p/ parâmetros
    (map #(.start (Thread. (fn [] (chega-sem-malvado! hospital %)))) pessoas)

    (.start (Thread. (fn [] (Thread/sleep 8000)
                       (pprint hospital))))))

; Retorna somente hospital vazio (mapa vazio) >>> comportamento -lazy-
;(simula-um-dia-em-paralelo)



(defn simula-um-dia-em-paralelo-com-mapv []
  ; Simulação utilizando um 'mapv' p/ forçar a execução do que era lazy (somente 'map')

  (let [hospital (atom (h.model/novo-hospital))
        pessoas ["111", "222", "333", "444", "555", "666"]]

    ; Modificamos o código anterior, tranformando o (.start (.Thread)) em lambda, add o # na frente e % p/ parâmetros
    (mapv #(.start (Thread. (fn [] (chega-sem-malvado! hospital %)))) pessoas)

    (.start (Thread. (fn [] (Thread/sleep 8000)
                       (pprint hospital))))))

; Map por padrão tem comportamento lazy, por isso não retorna com 'map' -> apenas execução
; Por isso alteramos p/ 'mapv' -> força a execução, gerando um vetor
;(simula-um-dia-em-paralelo-com-mapv)



; Definindo em 'let' a função lambda (melhora a legibilidade)
(defn simula-um-dia-em-paralelo-com-mapv-refatorada []
  (let [hospital (atom (h.model/novo-hospital))
        pessoas ["111", "222", "333", "444", "555", "666"]
        starta-thread-de-chegada #(.start (Thread. (fn [] (chega-sem-malvado! hospital %))))]

    (mapv starta-thread-de-chegada pessoas)

    (.start (Thread. (fn [] (Thread/sleep 8000)
                       (pprint hospital))))))

;(simula-um-dia-em-paralelo-com-mapv-refatorada)



; Refatorando...
; Mapv recebe apenas -um- parâmetro, porém temos -dois-
; Para isso, deixamos a fn 'starta-threading-de-chegada' preparada p/ receber -um- ou -dois- argumentos

; Separando a função starta-thread-de-chegada
; Função fica preparada p/ receber um ou dois parâmetros
(defn starta-thread-de-chegada
  ([hospital]
   (fn [pessoa] (starta-thread-de-chegada hospital pessoa)))
  ([hospital pessoa]
   (.start (Thread. (fn [] (chega-sem-malvado! hospital pessoa))))))

(defn simula-um-dia-em-paralelo-com-mapv-extraida []
  (let [hospital (atom (h.model/novo-hospital))
        pessoas ["111", "222", "333", "444", "555", "666"]
        starta (starta-thread-de-chegada hospital)]

    (mapv starta pessoas)

    (.start (Thread. (fn [] (Thread/sleep 8000)
                       (pprint hospital))))))

;(simula-um-dia-em-paralelo-com-mapv-extraida)



; Implementando uma 'partial' a uma função
; Com isso, a fn 'starta-thread-de-chegada' não precisa ter duas possibilidades

(defn starta-thread-de-chegada
  [hospital pessoa]
  (.start (Thread. (fn [] (chega-sem-malvado! hospital pessoa)))))

(defn simula-um-dia-em-paralelo-com-partial []
  (let [hospital (atom (h.model/novo-hospital))
        pessoas ["111", "222", "333", "444", "555", "666"]
        starta (partial starta-thread-de-chegada hospital)]

    (mapv starta pessoas)

    (.start (Thread. (fn [] (Thread/sleep 8000)
                       (pprint hospital))))))

;(simula-um-dia-em-paralelo-com-partial)



; Executando uma tarefa com número determinado de vezes
; Utilizando 'doseq' >>> execução para os elementos da sequência

(defn starta-thread-de-chegada
  [hospital pessoa]
  (.start (Thread. (fn [] (chega-sem-malvado! hospital pessoa)))))

(defn simula-um-dia-em-paralelo-com-doseq []
  (let [hospital (atom (h.model/novo-hospital))
        pessoas ["111", "222", "333", "444", "555", "666"]]

    (doseq [pessoa pessoas]
      (starta-thread-de-chegada hospital pessoa))

    (.start (Thread. (fn [] (Thread/sleep 8000)
                       (pprint hospital))))))

;(simula-um-dia-em-paralelo-com-doseq)



; Trocando o vetor com elementos por 'range'
(defn simula-um-dia-em-paralelo-com-doseq-e-range []
  (let [hospital (atom (h.model/novo-hospital))
        pessoas (range 6)]

    (doseq [pessoa pessoas]
      (starta-thread-de-chegada hospital pessoa))

    (.start (Thread. (fn [] (Thread/sleep 8000)
                       (pprint hospital))))))

;(simula-um-dia-em-paralelo-com-doseq-e-range)



; Utilizando 'dotimes' >>> execução em N vezes
(defn simula-um-dia-em-paralelo-com-dotimes []
  (let [hospital (atom (h.model/novo-hospital))]

    (dotimes [pessoa 6]
      (starta-thread-de-chegada hospital pessoa))

    (.start (Thread. (fn [] (Thread/sleep 8000)
                       (pprint hospital))))))

(simula-um-dia-em-paralelo-com-dotimes)