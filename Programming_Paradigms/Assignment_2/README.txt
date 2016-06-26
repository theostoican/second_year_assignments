/*
	Theodor Stoican
	323CA
*/

Tema2 mi s-a parut foarte interesanta, poate si din cauza ca aduce putin a Inteligenta
Artificiala :). Dar si din cauza ca Haskell e foarte elegant. Lasand la o parte 
impresiile, voi descrie in cele ce urmeaza cateva detalii de implementare pe care le 
consider relevante:

*Am incercat sa evit recursivitatea in multe locuri si am reusit, in mare, totusi mi-au
ramas cateva locuri (2-3), cum ar fi generarea aleatoare a cailor, unde am folosit 
recursivitate pe stiva.
*O functie care mi-a dat bataie de cap cu adevarat a fost showEstimations. Initial, mi-a
luat ceva sa inteleg ca printf returneaza un String (ma deruta ideea de String/IO) si ca 
il pot folosi in mod direct ca sa intorc ceva. Dupa aceea, am implementat o solutie 
nefericita (si hardcodata) care compunea sirul final din concatenari repetate intre 
concatMap-uri si printf (am folosit concatMap, deoarece prin simpla aplicare a lui map
as fi obtinut o lista de string-uri (tot liste), care ar fi trebuit concatenate - de aici 
necesitatea lui concatMap). In final, am gasit o solutie decenta, folosind o functie de
biblioteca (chunksOf), pe care o folosesc sa concatenez randurile efective ale matricii.
Finalmente, dupa mai multe incercari nefericite, a functionat.
*O alta functie la care am muncit un pic mai mult este updateEstimation. N-am inteles cum
functioneaza functia accum pana nu am cautat efectiv pe internet exemple, pe care le-am 
rulat ulterior in interpretor (parca totusi documentatia de Haskell e uneori prea saraca,
in sensul in care definitiile functiilor, cu exceptia semnaturilor lor, nu sunt bine 
conturate). In fine, dupa ce mi-am dat efectiv seama cum functioneaza, mi-am dat seama ca, 
pentru aplicarea algoritmului din tema, aveam nevoie sa stiu care e vecinul unei anumite 
stari. Prin urmare, lista de perechi data ca parametru lui accum, trebuia sa fie ceva mai
speciala, oferindu-ti indicii despre vecinul starii curente. Prin urmare, solutia pe care
am gasit-o a fost sa aplic un zip intre primele n-1 elemente ale listei si ultimele n-1
elemente ale aceleiasi liste, pentru a obtine perechi de vecini din calea respectiva.
De acolo, mi-am mai creat o functie ca sa accesez vecinul unei stari, si o functie 
getReinforcement care extrage utilitatea unei anumite stari. Remodelarea functiei dupa
ce am creat structura StateInfo a fost facila, avand cele de mai sus implementate.
*O ultima precizare ar fi legata de bonus si de ultimul task. Am folosit, in mare, structura 
de pe StackOverFlow in ceea ce priveste memoizarea, iar algoritmul de calcul al exponentialei
este cel eficient. In ceea ce priveste modelarea structurii de date, ea contine 2 campuri:
estimate si nrOfVisitors, iar cand parcurg array-ul de estimations folosesc sintaxa
(x, (SI es nr)), unde SI e constructorul de date, pentru a avea avea acces la campurile
structurii in mod direct.

In mare, cam acestea ar fi explicatiile. Sper ca am acoperit toate detaliile
relevante de implementare :).

Theodor 