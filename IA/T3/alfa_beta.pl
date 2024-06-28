:- dynamic(estados_visitados/1).

% Função principal para iniciar o jogo
g(Jogo) :-Jogo=game, [Jogo], estado_inicial(Ei), retractall(estados_visitados(_)), asserta(estados_visitados(0)),minimax_decidir(Ei, Op),
    casa_pos(Ei, H, Op, 1), write(H), nl,estados_visitados(Count), write('Total de estados visitados: '), write(Count), nl.

g(Jogo):- Jogo=nim, [Jogo], retractall(estados_visitados(_)), assertz(estados_visitados(0)) ,estado_inicial(Ei), minimax_decidir(Ei,Op),
write(Op),nl,estados_visitados(Count), write('Total de estados visitados: '), write(Count), nl.



% decide qual é a melhor jogada num estado do jogo
% minimax_decidir(Estado, MelhorJogada)

% se é estado terminal não há jogada 
minimax_decidir(Ei, terminou) :- 
    terminal(Ei).

% Para cada estado sucessor de Ei calcula o valor minimax do estado
% Opf é o operador (jogada) que tem maior valor
minimax_decidir(Ei, Opf) :- 
    findall(Es-Op, op1(Ei, Op, Es), L),
    Alpha is -50, 
    Beta is 50,
    best_move(L, Alpha, Beta, Opf).

best_move([Es-Op], Alpha, Beta, Opf) :-
    min_valor(Es, V, 1, Alpha, Beta),
    Opf = Op.
best_move([Es-Op|L], Alpha, Beta, Opf) :-
    min_valor(Es, V, 1, Alpha, Beta),
    best_move_aux(L, V, Op, Alpha, Beta, Opf).

best_move_aux([], _, BestOp, _, _, BestOp).
best_move_aux([Es-Op|L], BestV, BestOp, Alpha, Beta, Opf) :-
    min_valor(Es, V, 1, Alpha, Beta),
    (V > BestV -> 
        NewBestV = V, 
        NewBestOp = Op 
    ; 
        NewBestV = BestV, 
        NewBestOp = BestOp
    ),
    Alpha1 is max(Alpha, NewBestV),
    best_move_aux(L, NewBestV, NewBestOp, Alpha1, Beta, Opf).

% Função max_valor com cortes Alpha-Beta
max_valor(Ei, Val, P, Alpha, Beta) :-
    terminal(Ei), 
    valor(Ei, Val, P).
max_valor(Ei, Val, P, Alpha, Beta) :-
    incrementa_estados_visitados,
    findall(Es, op1(Ei, _, Es), L),
    P1 is P + 1,
    max_valor_aux(L, P1, Alpha, Beta, -50, Val).

max_valor_aux([], _, _, _, V, V).
max_valor_aux([E|Es], P, Alpha, Beta, V0, V) :-
    min_valor(E, V1, P, Alpha, Beta),
    V2 is max(V0, V1),
    (V2 >= Beta -> 
        V = V2 
    ; 
        Alpha1 is max(Alpha, V2), 
        max_valor_aux(Es, P, Alpha1, Beta, V2, V)
    ).

% Função min_valor com cortes Alpha-Beta
min_valor(Ei, Val, P, Alpha, Beta) :-
    terminal(Ei), 
    valor(Ei, Val, P).
min_valor(Ei, Val, P, Alpha, Beta) :-
    incrementa_estados_visitados,
    findall(Es, op1(Ei, _, Es), L),
    P1 is P + 1,
    min_valor_aux(L, P1, Alpha, Beta, 50, Val).

min_valor_aux([], _, _, _, V, V).
min_valor_aux([E|Es], P, Alpha, Beta, V0, V) :-
    max_valor(E, V1, P, Alpha, Beta),
    V2 is min(V0, V1),
    (V2 =< Alpha -> 
        V = V2 
    ; 
        Beta1 is min(Beta, V2), 
        min_valor_aux(Es, P, Alpha, Beta1, V2, V)
    ).

% Predicados auxiliares de máximo e mínimo (não são usados diretamente mas podem ser úteis)
maximo([A|R], Val) :- maximo(R, A, Val).
maximo([], A, A).
maximo([A|R], X, Val) :- 
    A < X, !, 
    maximo(R, X, Val).
maximo([A|R], _, Val) :- 
    maximo(R, A, Val).

minimo([A|R], Val) :- minimo(R, A, Val).
minimo([], A, A).
minimo([A|R], X, Val) :- 
    A > X, !, 
    minimo(R, X, Val).
minimo([A|R], _, Val) :- 
    minimo(R, A, Val).

% Predicado para converter entre número de casa e coordenadas da casa, no tabuleiro
casa_pos(e([(X,Y,_)|_], _), (X,Y), F, F).  
casa_pos(e([_|T], _), (RX,RY), F, I) :- 
    I1 is I + 1, 
    casa_pos(e(T, _), (RX, RY), F, I1).

% Incrementa o contador de estados visitados
incrementa_estados_visitados :-
    estados_visitados(Count),
    NewCount is Count + 1,
    retract(estados_visitados(Count)),
    asserta(estados_visitados(NewCount)).
