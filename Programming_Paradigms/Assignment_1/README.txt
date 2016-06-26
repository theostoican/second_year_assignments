/* 
THEODOR STOICAN
323 CA
*/

README

Tema1 a fost foarte faina, fiind prima data cand am implementat un AVL. Lucrand
in paradigma functionala, a fost cu atat mai bine. Pe scurt, voi rezuma ideea implementarii
in cele ce urmeaza:

* arborele este implementat sub forma unei liste de tipul '(left_son value right_son),
unde left_son si right_son sunt si ele, la randul lor alte liste cu acelasi format.
* La task-ul 0, cred ca unica precizare relevanta de facut este implementarea 
functiilor successor si predecessor. In cazul functiei successor, m-am folosit de
o functie ajutatoare, care sa-mi mentina arborele initial ca parametru, pentru a 
putea determina succesorul nodului a carui valoare este data ca parametru, in cazul
in care, spre exemplu, nodul al carui successor trebuie sa il determinam este cel
mai din dreapta nod din subarborele stang al radacinei. De asemenea, functia
predecessor este implementata similar.
* La task-ul 1, functia balance este, practic, cheia, intrucat de functionalitatea 
ei depind mare parte din celelalte operatii. In cadrul balansarii AVL-ului, exista 
4 cazuri in momentul in care arborele nu mai este echilibrat. De aici existenta 
celor 4 cazuri in cond-ul aferent functiei. Ea se foloseste de cele 2 functii, 
leftRotate si rightRotate, care implementeaza efectiv rotirile.
* La operatiile pe multimi, parcurg arborele AVL si creez un nou arbore sau aplic
insert/remove pe unul dintre arborii existenti, daca e cazul.
* La task-ul al doilea, pentru a genera submultimile de cardinal k, am generat toate
submultimile de dimensiune mai mare ca 0(stiu, ineficient), dintre care le-am 
selectionat pe cele care au dimensiunea k.
* Pentru a genera submultimile zigzag, am generat toate permutarile multimii reprezentate
ca arbore, dimensiunea maximala fiind exact numarul de elemente din arbore 
* La partea de bonus, am generat arborele sintactic prost de la bun inceput, tratand
doar cazurile cu 2 operatori binari. Vazand ca pica ultimul test, am modificat functia
parser astfel incat sa tratez si celelalte cazuri (a trebuit sa rescriu din nou functia :) ). 
In evaluate, totul e transparent, parcurg arborele si tratez cele 4 cazuri ale operatiilor.

Per total, a fost o tema faina :).

Cheers,
Theo