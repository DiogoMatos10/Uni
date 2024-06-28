%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%|| Contagem de Nós ||%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% !!! ESTE TROÇO DE CÓDIGO FOI RETIRADO DO FICHEIRO pni.pl DADO NAS AULAS !!!

:- dynamic(nos/1).
nos(0).

limpa:-retractall(maxNL(A)),

    retractall(nos(A)), asserta(maxNL(0)),

    asserta(nos(0)).

inc:- retract(nos(N)), N1 is N+1, asserta(nos(N1)).

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%|| Operador Sucessor ||%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

sucessor(e([v(N,D,_)|R],E),e(R,[v(N,D,V)|E])):-
    member(V,D).

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%|| Inicialização ||%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

estado_inicial(e([  v(pos(1,1),[1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16],_),
                    v(pos(2,1),[1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16],_),
                    v(pos(3,1),[1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16],_),
                    v(pos(4,1),[1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16],_),
                    v(pos(1,2),[1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16],_),
                    v(pos(2,2),[1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16],_),
                    v(pos(3,2),[1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16],_),
                    v(pos(4,2),[1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16],_),
                    v(pos(1,3),[1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16],_),
                    v(pos(2,3),[1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16],_),
                    v(pos(3,3),[1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16],_),
                    v(pos(4,3),[1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16],_),
                    v(pos(1,4),[1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16],_),
                    v(pos(2,4),[1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16],_),
                    v(pos(3,4),[1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16],_),
                    v(pos(4,4),[1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16],_)   ], [])).

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%|| Restrições ||%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

ve_restricoes(e([],LI)):-
    nenhum_igual(LI),       %nenhuma variável tem o valor igual.
    linhas(LI,VL),      %todas as linhas têm o mesmo valor.
    colunas(LI,VC),     %todas as colunas têm o mesmo valor.
    diagonais(LI,VD),   %todas as diagonais têm o mesmo valor.
    VL == VC , VC == VD.    %todas as anteriores têm o mesmo valor.

ve_restricoes(e(LNI,LI)):-
    LNI\==[] , nenhum_igual(LI).

nenhum_igual([]).
nenhum_igual([v(N1,_,V)|R]):-
    \+(member(v(N2,_,V),R)), nenhum_igual(R).
    

linhas(LI,VL):-
    findall(A,member(v(pos(_,1),_,A),LI), L1),
    sum(L1,V1), %primeira linha
    findall(A,member(v(pos(_,2),_,A),LI), L2),
    sum(L2,V2), %segunda linha
    findall(A,member(v(pos(_,3),_,A),LI), L3),
    sum(L3,V3), %terceira linha
    findall(A,member(v(pos(_,4),_,A),LI), L4),
    sum(L4,V4), %quarta linha
    V1==V2, V2==V3, V3==V4,
    VL is V1.
    
colunas(LI,VC):-
    findall(A,member(v(pos(1,_),_,A),LI), C1),
    sum(C1,V1), %primeira coluna
    findall(A,member(v(pos(2,_),_,A),LI), C2),
    sum(C2,V2), %segunda coluna
    findall(A,member(v(pos(3,_),_,A),LI), C3),
    sum(C3,V3), %terceira coluna
    findall(A,member(v(pos(4,_),_,A),LI), C4),
    sum(C4,V4), %quarta coluna
    V1==V2, V2==V3, V3==V4,
    VC is V1.

diagonais(LI,VD):-
    findall(A,member(v(pos(S,S),_,A),LI), DP),
    sum(DP, DPV), %diagonal principal
    
    member(v(pos(4,1),_,Val1),LI),
    member(v(pos(3,2),_,Val2),LI),
    member(v(pos(2,3),_,Val3),LI),
    member(v(pos(1,4),_,Val4),LI),
    sum([Val1,Val2,Val3,Val4],DSV), %diagonal secundária

    DPV==DSV,
    VD is DPV.

sum([],0).
sum([H|R],Res):-
    sum(R,A), Res is A+H.

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%|| Pesquisa Backtracking ||%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

pb:- estado_inicial(E0), back(E0,A), esc(A).

back(e([],A),A).
back(E,Sol):- sucessor(E,E1),
    inc, 
    ve_restricoes(E1),
    back(E1,Sol).

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%|| Backtracking com ForwardCheck ||%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

pbfc:-estado_inicial(E0), back_fc(E0,A),  esc(A).

back_fc(e([],A),A).
    back_fc(E,Sol):- sucessor(E,E1), inc, ve_restricoes(E1),
    forCheck(E1,E2),
    back_fc(E2,Sol).

forCheck(e(Lni,[v(N,D,V)|Li]), e(Lnii,[v(N,D,V)|Li])):-corta(V,Lni,Lnii).
  
corta(_,[],[]).
corta(V,[v(N,D,_)|Li], [v(N,D1,_)|Lii]):- delete(D,V,D1), corta(V,Li,Lii).

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%|| Ordenar ||%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

order(e(Lni,Li),e(LniO,Li)):-
    member(v(pos(2,2),D,V),Lni),
    List = [v(pos(2,2),D,V),
            v(pos(1,3),D,V),
            v(pos(3,1),D,V),
            v(pos(1,1),D,V),
            v(pos(3,3),D,V)],
    remove_list(Lni,List,Aux),
    append(List,Aux,LniO).


remove_list([], _, []).
remove_list([X|Tail], L2, Result):- member(X, L2), !, remove_list(Tail, L2, Result). 
remove_list([X|Tail], L2, [X|Result]):- remove_list(Tail, L2, Result).

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%|| Escrever  ||%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

esc([v(N,_,V)|R]):- esc([v(N,_,V)|R],1,4).
esc([v(N,_,V)|R],S,S):- R==[], write(V) , S1 is 1 ,nl, esc(R,S1,S).
esc([v(N,_,V)|R],S,S):- R\==[], write(V), S1 is 1 ,nl, write('---------'),nl, esc(R,S1,S).
esc([v(N,_,V)|R],S,S1):- S<S1, write(V), write(' | '), S2 is S+1 ,esc(R,S2,S1).
esc([],_,_):- nl, nos(A), write('nós visitados:  '), write(A).
