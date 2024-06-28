estado_inicial(e(9,2)).

terminal(e(A,_)):- A<1.

valor(E,V,P):-terminal(E),
  	X is P mod 2, (X== 1,V=1;X==0,V= -1).

op1(e(A,J),(X),e(A1,J1)):-  member(X,[1,2]), X=<A, A1 is A-X, A1>(-1), inv(J,J1).

inv(1,2).
inv(2,1).

playnim:-estado_inicial(E),playnim(E).

playnim(e(N,1)):-N<0, nl,write('ganhou o computador').
playnim(e(N,2)):-N<0, nl,write('ganhou o jogador').
playnim(e(N,2)):-expor(N),nl, read(Op), N1 is N-Op, playnim(e(N1,1)).
playnim(e(N,1)):-expor(N),nl, minimax_decidir(e(N,1),Op), (Op==terminou-> Op is 1; Op is Op),
					write(Op),nl, N1 is N-Op, playnim(e(N1,2)) ; 
					Op=1, write(Op), nl, N1 is N-Op, playnim(e(N1,2)).

expor(0):-write(0),!.
expor(N):- S is N-1, expor(S), write(0).

