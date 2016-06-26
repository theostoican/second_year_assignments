
:- ensure_loaded('t3t.pl').

% decomentati aceasta regula pentru a primi testele pentru bonus.
% AmFacutBonus.

% parse(+Raw, -Problem)

isHeadNumber([H|_]):-number(H).

getHead([H|_], H).

convert([],[]).
convert([H|T],R):-convert(T,R2), \+number(H), isHeadNumber(T), getHead(T,H2), append([(H,H2)],R2,R).
convert([H|T],R):-convert(T,R2), \+number(H), \+isHeadNumber(T), append([(H,1)],R2,R).
convert([H|T],R):-convert(T,R), number(H), \+isHeadNumber(T).

convertTransreceiver([],[]).
convertTransreceiver([(A,B)|T],R):-convertTransreceiver(T,R2), append([(A,0,B)], R2, R).

makePairs(_,[],[]).
makePairs(A,[_|T],R):-getHead(T,R1),append([(A,R1)],R2,R),makePairs(A,T,R2).
makePairs(_,[_|T],R):- \+getHead(T,_),append([],[],R).

convertCollinear([H|T],R):-makePairs(H,T,R1),convertCollinear(T,R2),append(R1,R2,R).
convertCollinear([],[]).

processCollinear([],[]).
processCollinear([H|T],R):-convertCollinear(H,R1), processCollinear(T,R2), append(R1,R2,R).

parse([], [edges([])]).
parse([sources(X)|L], [sources(R)|U]):-parse(L,U), convert(X,R).
parse([transceivers(X)|L], [transceivers(R)|U]):-parse(L,U), convert(X,R2), convertTransreceiver(R2,R).
parse([receivers(X)|L], [receivers(R)|U]):-parse(L,U), convert(X,R).
parse([source_transceivers(X)|L], [source_transceivers(R)|U]):-parse(L,U), convert(X,R).
parse([star_transceivers(X)|L], [star_transceivers(R)|U]):-parse(L,U), convert(X,R).
parse([collinear(X)|L], [collinear(R)|U]):-processCollinear(X,R), parse(L,U).

convertSourceTransceivers([],_,[]).
convertSourceTransceivers([(A1,N1,N2)|L],S,R):-(bandwidthSourceTransceiver(A1,S,N3),Nnew1 is N1+N3, Nnew2 is N2,
convertSourceTransceivers(L,S,R2),!,append([(A1,Nnew1,Nnew2)],R2,R));(convertSourceTransceivers(L,S,R2),!, append(R2,[(A1,N1,N2)],R)).

bandwidthSourceTransceiver(A,[(A1,N1)|L],N):-(A=A1,N is N1);(bandwidthSourceTransceiver(A,L,N)).

% describe(+Problem)
describe([]).
describe([sources(X)|L]) :- write("Nodurile sursa, impreuna cu latimile acestora: "), writeln(X), describe(L).
describe([receivers(X)|L]):-write("Nodurile destinatie, impreuna cu latimile acestora: "),writeln(X), describe(L).
describe([transceivers(X)|L]):-write("Nodurile de tip transceiver, impreuna cu informatia care poate iesi care trebuie sa intre ale acestora: "),
 writeln(X), describe(L).
describe([source_transceivers(X)|L]):-write("Nodurile de tip transceiver care contin informatie: "), writeln(X), describe(L).
describe([star_transceivers(X)|L]):-write("Nodurile de tip transceiver in care trebuie sa ramana informatie: "),writeln(X),describe(L).
describe([collinear(X)|L]):-write("Perechile de muchii interzise, datorina coliniaritatii: "), writeln(X),describe(L).
describe([_|L]):-describe(L).

search([CurrentState|_],Solution,_):-
        final_state(CurrentState),
		final_Star_Transceivers(CurrentState),
        extractEdges(CurrentState,Solution).

		
search([CurrentState|Other], Solution,I):-
        next_state(CurrentState, NextState, _),
        \+ member(NextState, Other),
        search([NextState,CurrentState|Other],Solution, I+1),
		append([],_, Solution).
		

sourceTransParse(Raw, Solution):-(member(transceivers(X),Raw), member(source_transceivers(Y),Raw),!,
convertSourceTransceivers(X,Y,R), select(transceivers(X), Raw, transceivers(R), Solution));(Solution=Raw).

% solve(+Problem, -Sol)
solve(Raw, Solution) :- sourceTransParse(Raw,R), search([R],Solution,0).
		
extractEdges(CurrentState,Z):-member(edges(Z),CurrentState).		
		
checkIfFinished([]).
checkIfFinished([(_,H2)|L]):-H2=:=0, checkIfFinished(L).		
		
checkIfFinishedTrans([]).
checkIfFinishedTrans([(_,_,N2)|L]):-N2=:=0, checkIfFinishedTrans(L).	
		
final_Star_Transceivers(L1):-(member(transceivers(X),L1), member(star_transceivers(Y),L1), iterate_trans(X,Y));
 (\+member(star_transceivers(_),L1)).

iterate_trans([],_).
iterate_trans([(A1,N1,_)|T1],L2):-(\+findStarTransceiver(A1,N1,L2),iterate_trans(T1,L2))
;(findStarTransceiver(A1,N1,L2),checkStarTransceiver(A1,N1,L2), iterate_trans(T1,L2)).

checkStarTransceiver(A1,N1,[(A2,N2)|L]):-(A1==A2, N1=:=N2);checkStarTransceiver(A1,N1,L).
findStarTransceiver(A1,N1,[(A2,_)|L]):-(A1==A2);findStarTransceiver(A1,N1,L).
		
final_state([]).
final_state([sources(X)|L]):-checkIfFinished(X), final_state(L).
final_state([receivers(X)|L]):-checkIfFinished(X), final_state(L).
final_state([transceivers(X)|L]):-checkIfFinishedTrans(X), final_state(L).
final_state([collinear(_)|L]):-final_state(L).
final_state([edges(_)|L]):-final_state(L).
final_state([source_transceivers(_)|L]):-final_state(L).
final_state([star_transceivers(_)|L]):-final_state(L).

next_state(L1,L2, Solution):-member(transceivers(X),L1),member(edges(Z), L1), member((A1,N1, N2),X), 
member((A2,N3,N4),X),\+A1==A2,N1>0,N4>0,\+checkInverseEdge((A1,A2),Z), areAdjacent(A1,A2,L1), 
areAdjacent(A2,A1,L1),select((A1,N1,N2),X,(A1,N1-1,N2),X2), select((A2,N3,N4), X2, (A2,N3+1,N4-1),Y2), 
select(transceivers(X),L1,transceivers(Y2),L3),append([],[[A1,A2,1]],Solution), findAndModifyEdge((A1,A2),Z,Z2),
select(edges(Z),L3,edges(Z2),L2).



next_state(L1, L2, Solution):-member(sources(X),L1), member(transceivers(Y),L1),member(edges(Z), L1), member((A1,N1),X),
member((A2,N2,N3),Y), N1>0,N3>0, areAdjacent(A1,A2,L1),areAdjacent(A2,A1,L1), select((A1,N1),X,(A1,N1-1),X2),
select((A2,N2,N3),Y,(A2,N2+1,N3-1),Y2),select(sources(X),L1,sources(X2),I),select(transceivers(Y),I,transceivers(Y2),L3),
append([],[[A1,A2,1]],Solution), findAndModifyEdge((A1,A2),Z,Z2),select(edges(Z),L3,edges(Z2),L2).


next_state(L1,L2,Solution):-member(transceivers(X),L1),member(receivers(Y),L1),member(edges(Z), L1),
member((A1,N1,N2),X), member((A2,N3), Y),N1>0, N3>0, areAdjacent(A1,A2,L1),areAdjacent(A2,A1,L1),select((A1,N1,N2),X, (A1,N1-1,N2),X2), 
select((A2,N3),Y,(A2,N3-1),Y2), select(transceivers(X),L1, transceivers(X2),I),
select(receivers(Y),I,receivers(Y2),L3), append([],[[A1,A2,1]],Solution), findAndModifyEdge((A1,A2),Z,Z2),
 select(edges(Z),L3,edges(Z2),L2).


next_state(L1, L2 ,Solution):-member(sources(X),L1), member(receivers(Y),L1),member(edges(Z), L1), member((A1,N1),X), 
member((A2,N2),Y), N1>0,N2>0,areAdjacent(A1,A2,L1),areAdjacent(A2,A1,L1), select((A1,N1), X, (A1,N1-1), X2),
select((A2,N2),Y, (A2,N2-1), Y2), select(sources(X),L1,sources(X2),I),select(receivers(Y),I,receivers(Y2),L3), 
append([],[[A1,A2,1]], Solution),findAndModifyEdge((A1,A2),Z,Z2),select(edges(Z),L3,edges(Z2),L2).



checkInverseEdge((S,D),[[A,B,_]|L]):-(D=A,S=B);checkInverseEdge((S,D),L).

findAndModifyEdge((S,D),[],L):-append([[S,D,1]],[],L).
findAndModifyEdge((S,D),[[A,B, N]|L], R):- S=A, D=B, append([[A,B,N+1]],L,R).
findAndModifyEdge((S,D),[[A,B,N]|L], R):- (\+ S=A;\+ D=B),!, findAndModifyEdge((S,D),L, R1), append([[A,B,N]], R1, R).
	
areAdjacent(_,_,[]).
areAdjacent(M,N,[collinear(X)|_]):- \+member((M,N), X).
areAdjacent(M,N,[sources(_)|L]):-areAdjacent(M,N,L).
areAdjacent(M,N,[receivers(_)|L]):-areAdjacent(M,N,L).
areAdjacent(M,N,[transceivers(_)|L]):-areAdjacent(M,N,L).
areAdjacent(M,N,[source_transceivers(_)|L]):-areAdjacent(M,N,L).
areAdjacent(M,N,[star_transceivers(_)|L]):-areAdjacent(M,N,L).
areAdjacent(M,N,[edges(_)|L]):-areAdjacent(M,N,L).
		
		