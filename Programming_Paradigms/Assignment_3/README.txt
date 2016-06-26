/*
	Theodor Stoican
	323CA
*/

README

Tema3 a fost foarte ok, ideea de a implementa un joc in Prolog fiind foarte
atractiva.Voi reda in cele de mai jos cateva detalii scurte despre implementare:
*Am inceput cu greu tema, dat fiind faptul ca nu am stiut exact cum as vrea sa arate
rezultatul parsarii mele si ce m-ar interesa in legatura cu fiecare tip de nod din joc.
Prin urmare, am inceput prin a schita un next_state, bazat pe scheletul de laborator, 
care sa ma ajute sa-mi dau seama cum as vrea sa arate nodurile. Asadar, am ajuns
sa stochez, in cadrul structurilor sources, receivers, star_transceivers,
source_transceivers, perechi de tipul (nod, latime)- mi-am creat eu functii care realizeaza
conversia. Pentru noduri de tipul transceivers pastrez (nod, out, in) cu semnificatia ca
out pachete de informatie pot iesi din nod si in pachete mai trebuie sa intre pana la final. 
De asemenea, structura collinears a fost si ea transformata intr-o lista de muchii
interzise, pentru a-mi fi mai usor sa stiu daca o muchie (a,b) e valida sau nu.
*In ceea ce priveste cautarea in spatiul starilor, folosesc 4 functii next_state,
cautand combinatii (transceiver-receiver, source-transceiver, source-receiver) si
incercand sa adaug muchii. Pentru simularea buclelor imperative, utilizez 2 member 
consecutive, pentru a itera prin 2 structuri in acelasi timp. 
*Ma opresc din cautare in spatiul starilor in momentul in care indeplinesc diverse conditii
referitoare la noduri, alese in functie de reprezentarea acestora. Spre exemplu, la receivers
sau sources, vreau ca al doilea membru al perechii sa fie nul, iar la transceivers-
reprezentare:(nod,out,in)-vrea ca in sa fie 0.
Cu alte cuvinte, nu mai poate intra nimic in transceiver. De asemenea, vreau ca nodurile
tip transceiver ce sunt si star_transceivers sa contina pachetele de informatie cerute la 
final, astfel ca voi cauta in cele 2 liste, voi identifica nodurile si voi verifica ca out 
va fi egal cu membrul 2 al perechii din lista de star_transceiver. Daca ambele conditii
sunt indeplinite, atunci ma voi opri, intrucat am gasit o solutie. 

In mare si, pe scurt, cam acestea ar fi cele mai relevante detalii referitoare 
la implementare.

Theodor