(ns chipee.gates
  (:require-macros [chipee.core :as cc]))

(cc/defprimitive nand* [a b] => [out]
  (if (= 2 (+ a b)) [0] [1]))

(cc/defgate not* [in] => [out]
  (nand* [in in] => [out]))

(cc/defgate and* [a b] => [out]
  (nand* [a b] => [w])
  (not* [w] => [out]))

(cc/defgate or* [a b] => [out]
   (not* [a] => [na])
   (not* [b] => [nb])
   (and* [na nb] => [w])
   (not* [w] => [out]))

(cc/defgate xor* [a b] => [out]
  (not* [a] =>[na])
  (not* [b] => [nb])
  (and* [a nb] => [anb])
  (and* [b na] => [bna])
  (or* [anb bna] => [out]))

(cc/defgate mux* [a b sel] => [out]
  (not* [sel] => [nsel])
  (and* [a nsel] => [w1])
  (and* [b sel] => [w2])
  (or* [w1 w2] => [out]))

(cc/defgate dmux* [in sel] => [a b]
  (not* [sel] => [nsel])
  (and* [in nsel] => [a])
  (and* [in sel] => [b]))

; extend - binadd

(cc/defgate binadd* [a b] => [carry sum]
  (xor* [a b] => [sum])
  (and* [a b] => [carry]))


; event gates

; intermediate
(cc/defprimitive event* [prior prob] => [out]
  [(* prior prob)])

(cc/defgate top* [prior] => [out]
  (event* [prior 1.0] => [out]))

(cc/defgate basic* [prob] => [out]
  (event* [1.0 prob] => [out]))

(cc/defgate undeveloped* [prob] => [out]
  (event* [1.0 prob] => [out]))


