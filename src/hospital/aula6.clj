(ns hospital.aula6
  (:require [hospital.model :as h.model])
  (:use (clojure [pprint])))

(defn cabe-na-fila? [fila]
  (-> fila
      count
      (< 5)))

(defn chega-em
  [fila pessoa]
  (if (cabe-na-fila? fila)
    (conj fila pessoa)
    (throw (ex-info "Fila já está cheia" {:tentando-adicionar pessoa}))))

; 'ref-set' ou 'alter' >>> troca o valor de uma referência
; 'dosync' >>> para estabelecer um bloco de sincronizado de transação

(defn chega-em!
  "Troca de referência via ref-set"
  [hospital pessoa]
  (let [fila (get hospital :espera)]
    (ref-set fila (chega-em @fila pessoa))))                ; '@' é o mesmo que 'deref' ('chega-em' é uma função pura)

(defn chega-em!
  "Troca de referência via alter"
  [hospital pessoa]
  (let [fila (get hospital :espera)]
    (alter fila chega-em pessoa)))

(defn simula-um-dia []
  (let [hospital {:espera       (ref h.model/empty-queue)
                  :laboratorio1 (ref h.model/empty-queue)
                  :laboratorio2 (ref h.model/empty-queue)
                  :laboratorio3 (ref h.model/empty-queue)}]
    (dosync
      (chega-em! hospital "João")
      (chega-em! hospital "Maria")
      (chega-em! hospital "Paulo")
      (chega-em! hospital "Marta")
      (chega-em! hospital "Ana")
      ;(chega-em! hospital "Lúcia")
      )
    (pprint hospital)))

;(simula-um-dia)

(defn async-chega-em! [hospital pessoa]
  (future
    (Thread/sleep (rand 5000))
    (dosync
      (println "Tentando o código sincronizado" pessoa)
      (chega-em! hospital pessoa))))



(defn simula-um-dia-async []
  (let [hospital {:espera       (ref h.model/empty-queue)
                  :laboratorio1 (ref h.model/empty-queue)
                  :laboratorio2 (ref h.model/empty-queue)
                  :laboratorio3 (ref h.model/empty-queue)}]
    (dotimes [pessoa 10]
      (async-chega-em! hospital pessoa))
    (future
      (Thread/sleep 8000)
      (pprint hospital))
    ))

;(simula-um-dia-async)

;(println (future 15))
;(println (future ((Thread/sleep 1000) 15)))



(defn simula-um-dia-async-com-mapv []
  (let [hospital {:espera       (ref h.model/empty-queue)
                  :laboratorio1 (ref h.model/empty-queue)
                  :laboratorio2 (ref h.model/empty-queue)
                  :laboratorio3 (ref h.model/empty-queue)}]
    (def futures (mapv #(async-chega-em! hospital %) (range 10)))
    (future
      (dotimes [n 4]
        (Thread/sleep 2000)
        (pprint hospital)
        (pprint futures))
      )
    ))

(simula-um-dia-async-com-mapv)