(ns hospital.aula3
  (:use [clojure.pprint])
  (:require [hospital.logic :as h.logic]
            [hospital.model :as h.model]))

; ÁTOMO E SWAP

; ROOT BINDING
; Símbolo definido como global (DEF), podendo ser acessada por qualquer threading deste namespace
; Valor "maria" atribuído ao símbolo
(def nome "maria")
;(println nome)

; Redefinindo o símbolo (refazendo o binding)
(def nome 32567)
;(println nome)

; Utilizando LET
(let [nome "maria"]
  ; coisa 1
  ; coisa 2
  ;(println nome)
  ; Aqui não estamos refazendo o binding do símbolo local
  ; Estamos criando um símbolo local a este bloco e escondendo o anterior >>> SHADOWING
  (let [nome "joão"]
    ; coisa 3
    ; coisa 4
    ;(println nome)
    )
  ;(println nome)
  )

; ATOM -> 'wrap'(casca) que permite a mutabilidade do que estiver interno a ele

; Definindo um mapa mutável em nível atômico
(def hospital-silveira (atom {}))

(defn testa-atomao []
  (let [hospital-silveira (atom { :espera h.model/empty-queue})]
    (println hospital-silveira)
    (pprint hospital-silveira)

    ; DEREF -> tira a referência do mapa p/ acessar o que está lá dentro
    ; No exemplo: acesso à fila de espera dentro de um mapa
    ;(pprint (deref hospital-silveira))
    ; Pode ser indicado somente com '@' (atalho p/ deref)
    (pprint @hospital-silveira)

    ; ASSOC -> para associar ao mapa (mas não altera o conteúdo dentro do átomo, apenas altera o valor extraído)
    (pprint (assoc @hospital-silveira :laboratorio1 h.model/empty-queue))
    (pprint @hospital-silveira)

    ; Para alterar o valor dentro do átomo (uma das maneiras)
    ; SWAP -> altera o conteúdo dentro do átomo e evita o problema de concorrência

    ; SWAP! + ASSOC
    ; Acessa o valor do átomo e troca ele (efeito colateral)
    (swap! hospital-silveira assoc :laboratorio1 h.model/empty-queue)
    (pprint @hospital-silveira)

    (swap! hospital-silveira assoc :laboratorio2 h.model/empty-queue)
    (pprint @hospital-silveira)

    ; UPDATE tradicional imutável com dereferência (não trará efeito)
    (update @hospital-silveira :laboratorio1 conj "001")

    ; Isolando as atribuições dentro da função 'swap'
    (swap! hospital-silveira update :laboratorio1 conj "001")
    (swap! hospital-silveira update :laboratorio2 conj "002")
    (pprint hospital-silveira)
    ))

; O símbolo aponta p/ um átomo e esse átomo pode ser atualizado em uma função como 'swap

;(testa-atomao)

; Acrescentamos o ! no nome da função p/ deixar claro que a função altera os parâmetros (tem efeito colateral)

(defn chega-em-malvado! [hospital pessoa]
  (swap! hospital h.logic/chega-em-pausado-logando :espera pessoa) ; ordem: atom + function + parameters
  (println "apos inserir" pessoa))

; Problema de variável global (símbolo do namespace) compartilhado
(defn simula-um-dia-em-paralelo []

  ; Definindo hospital como um átomo que contém o resultado desta invocação
  ; Como não temos mais símbolo global, 'hospital' passa a ser parâmetro p/ a função

  (let [hospital (atom (h.model/novo-hospital))]
    (.start (Thread. (fn [] (chega-em-malvado! hospital "001"))))
    (.start (Thread. (fn [] (chega-em-malvado! hospital "002"))))
    (.start (Thread. (fn [] (chega-em-malvado! hospital "003"))))
    (.start (Thread. (fn [] (chega-em-malvado! hospital "004"))))
    (.start (Thread. (fn [] (chega-em-malvado! hospital "005"))))
    (.start (Thread. (fn [] (chega-em-malvado! hospital "006"))))
    (.start (Thread. (fn [] (Thread/sleep 8000)
                       (pprint hospital))))))

; Forçando situações de retry
;(simula-um-dia-em-paralelo)



(defn chega-sem-malvado! [hospital pessoa]
  (swap! hospital h.logic/chega-em :espera pessoa)
  (println "apos inserir" pessoa))

(defn simula-um-dia-em-paralelo []
  (let [hospital (atom (h.model/novo-hospital))]
    (.start (Thread. (fn [] (chega-sem-malvado! hospital "001"))))
    (.start (Thread. (fn [] (chega-sem-malvado! hospital "002"))))
    (.start (Thread. (fn [] (chega-sem-malvado! hospital "003"))))
    (.start (Thread. (fn [] (chega-sem-malvado! hospital "004"))))
    (.start (Thread. (fn [] (chega-sem-malvado! hospital "005"))))
    (.start (Thread. (fn [] (chega-sem-malvado! hospital "006"))))
    (.start (Thread. (fn [] (Thread/sleep 8000)
                       (pprint hospital))))))

; Sem forçar situação de retry (busy retry) -> pode acontecer, mas pode não acontecer
(simula-um-dia-em-paralelo)