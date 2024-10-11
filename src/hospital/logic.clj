(ns hospital.logic)

; Definindo a lógica de atendimento do hospital
; Funções de chegada e de atendimento

(defn chega-em
  [hospital departamento pessoa]
  (update hospital departamento conj pessoa))

(defn atende
  [hospital departamento]
  (let [fila (get hospital departamento)]
    (update hospital departamento pop)
    ))



; Atualizando a função 'chega-em' para aceitar somente até 5 pessoas
(defn chega-em
  [hospital departamento pessoa]
  (let [fila (get hospital departamento)
        tamanho-atual (count fila)
        cabe? (< tamanho-atual 5)]
    (if cabe?
      (update hospital departamento conj pessoa)
      (throw (ex-info "Fila já está cheia" {:tentando-adicionar pessoa}))))) ; lançando erro

(defn atende
  [hospital departamento]
  (let [fila (get hospital departamento)]
    (update hospital departamento pop)
    ))



; Separando as funções (single responsability) e usando threading first

(defn cabe-na-fila? [hospital departamento]
  (-> hospital
      (get,,, departamento)
      count,,,
      (<,,, 5)))


(defn chega-em
  [hospital departamento pessoa]
  (if (cabe-na-fila? hospital departamento)
    (update hospital departamento conj pessoa)
    (throw (ex-info "Fila já está cheia" {:tentando-adicionar pessoa}))))


(defn chega-em-pausado
  "Função 'chega-em-malvado' parece ser pura, mas usa random"
  [hospital departamento pessoa]
  (Thread/sleep (* (rand) 2000))
  (if (cabe-na-fila? hospital departamento)
    (do
      ; (Thread/sleep (* (rand) 2000))
      (update hospital departamento conj pessoa))
    (throw (ex-info "Fila já está cheia" {:tentando-adicionar pessoa}))))



(defn chega-em-pausado-logando
  "Função 'chega-em-malvado' parece ser pura, mas usa random, altera seu status e loga"
  [hospital departamento pessoa]
  (println "Tentando adicionar a pessoa" pessoa)
  (Thread/sleep (* (rand) 2000))
  (if (cabe-na-fila? hospital departamento)
    (do
      ; (Thread/sleep (* (rand) 2000))
      (println "Dando o update nessa pessoa" pessoa)
      (update hospital departamento conj pessoa))
    (throw (ex-info "Fila já está cheia" {:tentando-adicionar pessoa}))))


(defn atende
  [hospital departamento]
  (let [fila (get hospital departamento)]
    (update hospital departamento pop)
    ))


(defn transfere
  [hospital de para]
  (let [pessoa (peek (get hospital de))]
    (-> hospital
        (atende de)
        (chega-em para pessoa))))


; Refatorando 'transfere' e add 'proxima'

(defn proxima
  "Retorna o próximo paciente da fila"
  [hospital departamento]
  (-> hospital
      departamento
      peek))


(defn transfere
  "Transfere o próximo paciente da fila -de- para a fila -para-"
  [hospital de para]
  (let [pessoa (proxima hospital de)]
    (-> hospital
        (atende de)
        (chega-em para pessoa))))



(defn atende-completo
  "Retorno dos dois valores (paciente movido e a fila restante)"
  [hospital departamento]
  {:paciente (update hospital departamento peek)
   :hospital (update hospital departamento pop)})



; Utilizando 'juxt' >>> junta as duas funções (peek e pop)

(defn atende-completo-que-chama-ambos
  "Retorno dos dois valores (paciente movido e a fila restante)"
  [hospital departamento]
  (let [fila (get hospital departamento)
        peek-pop (juxt peek pop)
        [pessoa fila-atualizada] (peek-pop fila)
        hospital-atualizado (update hospital assoc departamento fila-atualizada)]
    {:paciente pessoa
     :hospital hospital-atualizado}))