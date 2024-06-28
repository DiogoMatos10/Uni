%%%%%%%%%%%%%%%%%%%%%%%%% INICIALIZAÇÃO %%%%%%%%%%%%%%%%%%%%%%%%%

%definiçao do estado inicial
/* 
estado_inicial(e(  [(1,7,v),(2,7,v),(3,7,v),(4,7,v),(5,7,v),(6,7,v),
                    (1,6,v),(2,6,v),(3,6,v),(4,6,v),(5,6,v),(6,6,v),
                    (1,5,v),(2,5,v),(3,5,v),(4,5,v),(5,5,v),(6,5,v),
      			    (1,4,v),(2,4,v),(3,4,v),(4,4,v),(5,4,v),(6,4,v),
      				(1,3,v),(2,3,v),(3,3,v),(4,3,v),(5,3,v),(6,3,v),
      				(1,2,v),(2,2,v),(3,2,v),(4,2,v),(5,2,v),(6,2,v),
      			    (1,1,v),(2,1,v),(3,1,v),(4,1,v),(5,1,v),(6,1,v)], b ) ).
*/

%exemplo 1
/*
estado_inicial(e(  [(1,7,b),(2,7,b),(3,7,b),(4,7,v),(5,7,b),(6,7,b),
                    (1,6,p),(2,6,p),(3,6,p),(4,6,v),(5,6,p),(6,6,p),
                    (1,5,b),(2,5,b),(3,5,b),(4,5,v),(5,5,b),(6,5,b),
      				(1,4,p),(2,4,p),(3,4,p),(4,4,v),(5,4,p),(6,4,p),
      				(1,3,b),(2,3,b),(3,3,b),(4,3,v),(5,3,b),(6,3,b),
      				(1,2,p),(2,2,p),(3,2,p),(4,2,v),(5,2,p),(6,2,p),
      				(1,1,b),(2,1,b),(3,1,b),(4,1,v),(5,1,b),(6,1,b)], b )).

*/
%exemplo 2
/*
estado_inicial(e(  [(1,7,b),(2,7,b),(3,7,p),(4,7,v),(5,7,b),(6,7,p),
                    (1,6,p),(2,6,p),(3,6,p),(4,6,v),(5,6,p),(6,6,p),
                    (1,5,b),(2,5,b),(3,5,p),(4,5,v),(5,5,b),(6,5,p),
                    (1,4,b),(2,4,v),(3,4,b),(4,4,p),(5,4,b),(6,4,b),
                    (1,3,b),(2,3,v),(3,3,p),(4,3,b),(5,3,b),(6,3,p),
                    (1,2,p),(2,2,p),(3,2,b),(4,2,p),(5,2,p),(6,2,p),
                    (1,1,b),(2,1,p),(3,1,b),(4,1,p),(5,1,p),(6,1,p)], b ) ).
*/

%exemplo 3

estado_inicial(e(  [(1,7,v),(2,7,v),(3,7,p),(4,7,p),(5,7,b),(6,7,b),
                    (1,6,p),(2,6,v),(3,6,p),(4,6,b),(5,6,p),(6,6,p),
                    (1,5,b),(2,5,b),(3,5,p),(4,5,p),(5,5,b),(6,5,p),
                    (1,4,b),(2,4,b),(3,4,b),(4,4,p),(5,4,b),(6,4,b),
                    (1,3,b),(2,3,b),(3,3,p),(4,3,b),(5,3,b),(6,3,p),
                    (1,2,p),(2,2,p),(3,2,b),(4,2,p),(5,2,p),(6,2,p),
                    (1,1,b),(2,1,p),(3,1,b),(4,1,p),(5,1,p),(6,1,p)], b ) ).


%dimensões do tabuleiro
tabuleiroX(6).
tabuleiroY(7).

%%%%%%%%%%%%%%%%%%%%%%%%% TERMINAL %%%%%%%%%%%%%%%%%%%%%%%%%

% um estado é terminal se houvere quatro peças de uma das duas cores.
% em linha, ou se não houverem mais casas vazias.
terminal(e(L,_)):- conta4pecas(L,b) ; conta4pecas(L,p).
terminal(e(L,_)):- \+member((_,_,v),L).


%%%%%%%%%%%%%%%%%%%%%%%%% AUXILIARES %%%%%%%%%%%%%%%%%%%%%%%%%

conta4pecas(L,P):- 	linhas(L,P,1) ; 
					colunas(L,P,1) ; 
					tabuleiroX(M), M1 is M+1 , diagonais(L,P,1,-M1); 
					tabuleiroX(S), S1 is S+1, antidiags(L,P,1,-S1). 
                   
%%%%%%%%%%%%% LINHAS 

%aumenta iterativamente N desde 1 até ao máximo Y do tabuleiro
% sucede se, para alguma das linhas com Y=N, o predicado linha suceder.
linhas(_,_,N):- tabuleiroY(N), false.
linhas(L,P,N):- linha(L,P,N) ; tabuleiroY(S), N1 is N+1, N1<S+1, linhas(L,P,N1).

% cria uma lista com todos os valores de uma linha Y e sucede se
% o predicado conta4 para essa lista suceder.
linha(L,P,Y):- findall(V,member((_,Y,V),L),S), conta4(S,P).

%%%%%%%%%%%%% COLUNAS

% predicados análogos a linhas\3 e linha\3
colunas(_,_,N):- tabuleiroX(N), false.
colunas(L,P,N):- coluna(L,P,N) ; tabuleiroX(S), N1 is N+1, N1<S+1, colunas(L,P,N1).
coluna(L,P,X):- findall(V,member((X,_,V),L),S), conta4(S,P).

%%%%%%%%%%%%% DIAGONAIS

% análogo a linhas e colunas, mas com um twist:
% todas as diagonais do tabuleiro podem ser definidas como y=x+k, onde k varia de -M até M, sendo M a dimensão X do tabuleiro
diagonais(_,_,Y,_):- tabuleiroY(Y), false.
diagonais(L,P,Y,K):- diagonal(L,P,Y,K,[]) ; tabuleiroY(S), K1 is K+1, K1<S+1, diagonais(L,P,Y,K1).

% este predicado itera sobre o valor do y, dando ao x o valor de y+k, construindo assim uma lista com os valores de
% todas as posições da diagonal representada por x=y+k. chama o predicado conta/2 a cada iteraçãono
diagonal(L,P,Y,K,H):- conta4(H,P);  diagonal2(L,Y,K,V), H1 = [V|H], tabuleiroY(S), Y1 is Y+1, Y1<S+2, diagonal(L,P,Y1,K,H1).
diagonal2(L,Y,K,V):- W is Y+K, member((W,Y,V),L).
diagonal2(L,Y,K,V):- V = v.

%%%%%%%%%%%%% ANTI-DIAGONAIS

% de forma análoga ás diagonais, este predicado verifica as anti-diagonais, definidas por y=-x+k
antidiags(_,_,Y,_):- tabuleiroY(Y), false.
antidiags(L,P,Y,K):- antidiag(L,P,Y,K,[]) ; tabuleiroY(S), K1 is K+1, K1<S+1, antidiags(L,P,Y,K1).
antidiag(L,P,Y,K,H):- conta4(H,P);  antidiag2(L,Y,K,V), H1 = [V|H], tabuleiroY(S), Y1 is Y+1, Y1<S+2, antidiag(L,P,Y1,K,H1).
antidiag2(L,Y,K,V):- K>(-1) , W is K-Y, member((W,Y,V),L) ; tabuleiroX(S), W is S+K-Y, member((W,Y,V),L).
antidiag2(L,Y,K,V):- V = v.

% sucede se houver uma sequencia de quatro peças P na lista.
% caso a lista tenha menos de 4 elementos, não sucede
conta4(L,_):- num_el(L,N), N<4, false.
conta4([A,B,C,D|T],P):- A = P, B = P, C = P, D = P ; conta4([B,C,D|T],P).

%%%%%%%%%%%% AUXILIARES DA FUNÇÃO AVALIAÇÃO

% predicados análogos aos acima, mas em vez de verificar se há quatro peças de seguida, retornam 
%o valor da maior sequencia de peças  
linhasA(_,_,N,0):- tabuleiroY(N).
linhasA(L,P,N,Val):- N1 is N+1, linhasA(L,P,N1,Val1), linhaA(L,P,N,Val2), (Val2>Val1 -> Val is Val2 ; Val is Val1).
linhaA(L,P,Y,Val):- findall(V,member((_,Y,V),L),S), contaN(S,P,Val).

colunasA(_,_,N,0):- tabuleiroX(N).
colunasA(L,P,N,Val):- N1 is N+1, colunasA(L,P,N1,Val1), colunaA(L,P,N,Val2), (Val2>Val1 -> Val is Val2 ; Val is Val1) .
colunaA(L,P,X,Val):- findall(V,member((X,_,V),L),S), contaN(S,P,Val).

diagonaisA(_,_,_,K,0):- tabuleiroX(K).
diagonaisA(L,P,Y,K,Val):- K1 is K+1, diagonaisA(L,P,Y,K1,Val1), diagonalA(L,P,Y,K,H), contaN(H,P,Val2), (Val2>Val1 -> Val is Val2 ; Val is Val1).
diagonalA(_,_,Y,_,[]):-  N is Y-1, tabuleiroY(N).
diagonalA(L,P,Y,K,H):- Y1 is Y+1, diagonalA(L,P,Y1,K,H1), diagonal2A(L,Y,K,V), H = [V|H1].
diagonal2A(L,Y,K,V):- W is Y+K, member((W,Y,V),L).
diagonal2A(L,Y,K,V):- V = v.

antidiagsA(_,_,_,K,0):- tabuleiroX(K).
antidiagsA(L,P,Y,K,Val):- K1 is K+1, antidiagsA(L,P,Y,K1,Val1), antidiagA(L,P,Y,K,H), contaN(H,P,Val2), (Val2>Val1 -> Val is Val2 ; Val is Val1). 
antidiagA(_,_,Y,_,[]):-  N is Y-1, tabuleiroY(N).
antidiagA(L,P,Y,K,H):- Y1 is Y+1, antidiagA(L,P,Y1,K,H1), antidiag2A(L,Y,K,V), H = [V|H1].
antidiag2A(L,Y,K,V):- K>(-1) , W is K-Y, member((W,Y,V),L); tabuleiroX(S), W is S+K-Y, member((W,Y,V),L).
antidiag2A(L,Y,K,V):- V = v.

%predicado que retorna o máximo de elementos Element em sequencia
contaN(L, E, MaxSeq) :-
    max_sequence(L, E, 0, 0, MaxSeq).
max_sequence([], _, CurrentSeq, TempMax, MaxSeq) :-
    MaxSeq is max(CurrentSeq, TempMax).
max_sequence([E|T], E, CurrentSeq, TempMax, MaxSeq) :-
    NewCurrentSeq is CurrentSeq + 1,
    max_sequence(T, E, NewCurrentSeq, TempMax, MaxSeq).
max_sequence([H|T], E, CurrentSeq, TempMax, MaxSeq) :-
    H \= E,
    NewTempMax is max(CurrentSeq, TempMax),
    max_sequence(T, E, 0, NewTempMax, MaxSeq).

% sucede se o segundo argumento for o numero de elementos no primeiro argumento
num_el([],0).
num_el([_|T], L) :- num_el(T, TL), L is TL + 1.
  
%%%%%%%%%%%%%%%%%%%%%%%%% FUNÇÃO UTILIDADE %%%%%%%%%%%%%%%%%%%%%%%%%

valor(e(L,_),0,P):-  \+ member(v,L),!.

valor(E,V,P):-terminal(E),
  	X is P mod 2, (X== 1,V=4;X==0,V= -4).

%%%%%%%%%%%%%%%%%%%%%%%%% FUNÇÃO AVALIAÇÃO %%%%%%%%%%%%%%%%%%%%%%%%%

% a função de avaliação consiste em retornar o valor da maior sequencia de peças, para ser utilizada como valor de avaliação
aval(e(L,P),Val):-	linhasA(L,P,1,VL) , 
                  	colunasA(L,P,1,VC) , 
					tabuleiroX(M), M1 is M+1 , diagonaisA(L,P,1,-M1,VD), 
					tabuleiroX(S), S1 is S+1, antidiagsA(L,P,1,-S1,VAD),!,
					H1 is max(VL,VC), H2 is max(H1, VD), Val is max(H2,VAD).

%%%%%%%%%%%%%%%%%%%%%%%%% OPERADOR JOGADA %%%%%%%%%%%%%%%%%%%%%%%%%

op1(e(L,J),(C),e(L1,J1)):- inv(J,J1), subs(v,J,L,L1,1,C).
inv(p,b).
inv(b,p).

subs(A,J, [(X,Y,A)|R], [(X,Y,J)|R],C,C).
subs(A,J, [(X,Y,B)|R], [(X,Y,B)|S],N,C):- M is N+1, subs(A,J,R,S,M,C).

