%limitadores de espaço
bounds(X, Y) :- 
    X =< 7,
    X >= 1,
    Y =< 7,
    Y >= 1.

iguais(A, A).


% operadores de estado -> op(estado_atual, operador, estado_seguinte, custo)


op(e(X,Y,P,Q,N), baixo, e(X, Y1, P, Q1,N), 1) :-
    Y1 is Y - 1,
    (iguais(e(X, Y1), e(P, Q)) -> 
        (
            Q1 is Q - 1,
            bounds(P, Y1),
            \+ x(P, Q1)
        );
        (
            Q1 is Q,
            bounds(X, Y1),
            \+ x(X, Y1)
        )
    ).
op(e(X,Y,P,Q,N),direita,e(X1,Y,P1,Q,N),1) :-
    X1 is X+1,
    (iguais(e(X1,Y),e(P,Q)) ->
        (
            P1 is P+1,
            bounds(P1,Q),
            bounds(X1,Y),
            \+ x(P1,Q)
        );
        (
            P1 is P,
            bounds(X1,Y),
            \+ x(X1,Y)
        )
    ).
    
op(e(X,Y,P,Q,N),cima,e(X,Y1,P,Q1,N),1) :-
    Y1 is Y+1,
    (iguais(e(X,Y1),e(P,Q)) ->
        (
            Q1 is Q+1,
            bounds(P,Q1),
            bounds(X,Y1),
            \+ x(P,Q1)
        );
        (
            Q1 is Q,
            bounds(X,Y1),
            \+ x(X,Y1)
        )
    ).
op(e(X,Y,P,Q,N),esquerda,e(X1,Y,P1,Q,N),1) :-
    X1 is X-1,
    (iguais(e(X1,Y),e(P,Q)) ->
        (
            P1 is P-1,
            bounds(P1,Q),
            bounds(X1,Y),
            \+ x(P1,Q)
        );
        (
            P1 is P,
            bounds(X1,Y),
            \+ x(X1,Y)
        )
    ).

op(e(X,Y,P,Q,N),apanha,e(X,Y,P,Q,N1),1) :- 
        (abs(X-P) + abs(Y-Q))<2, %adjacência do agente á máquina.
        member((P,Q),N),
        delete(N,(P,Q),N1).
    

%%%%%%%%%%%%%%%%%%%%%%%%%%

% heurísticas
manhattan(e(_,_,P,Q,_),C):-
	estado_final(e(_,_,P1,Q1,_)),
	A is abs(P - P1), 
 	B is abs(Q - Q1),
	C is A+B.

euclidiana(e(_,_,P,Q,_),SOMA):-
	estado_final(e(_,_,P1,Q1,_)),
	SOMA is round(sqrt(abs(P - P1) ** 2 + abs(Q - Q1) ** 2)).

h1(A, B) :- manhattan(A, B).
h2(A, B) :- euclidiana(A, B).
