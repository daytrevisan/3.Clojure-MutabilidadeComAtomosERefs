(ns hospital.core
  (:use [clojure.pprint])
  (:require [hospital.model :as h.model]))

; Ordem de atendimento
; Fila de espera
; Fila do laboratório 1
; Fila do laboratório 2
; Fila do laboratório 3

(let [hospital-boa-esperanca (h.model/novo-hospital)]
  (pprint hospital-boa-esperanca))

(pprint h.model/empty-queue)