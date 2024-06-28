%%%%%%%%%%%%%%%%%%%%%%%%%%%%% DEFINIÇÃO DO PROBLEMA %%%%%%%%%%%%%%%%%%%%%%%%%%%%%

estado_inicial([tamanho(a,2), tamanho(b,1), tamanho(c,1), tamanho(d,2), tamanho(e, 1),                                                                                                                       
                tamanho(chao,2), topo(chao), 
                topo(e), topo(c),
                sobre(e,a), sobre(a,d), sobre(d,chao),
                sobre(c,b),sobre(b,chao),
                livre(1), livre(2) ]).

estado_final([  tamanho(a,2), tamanho(b,1), tamanho(c,1), tamanho(d,2), tamanho(e, 1),                                                                                                                      
                tamanho(chao,2), topo(chao), 
                topo(e), topo(a),
                sobre(c,chao), sobre(d,chao),
                sobre(e,b), sobre(b,c),sobre(a,d),
                livre(1), livre(2) ]). 

%%%%%%%%%%%%%%%%%%%%%%%%%%%%% AÇÕES %%%%%%%%%%%%%%%%%%%%%%%%%%%%%

accao(agarra(B,M),  [tamanho(B,1),topo(B),livre(M),sobre(B,BS)],
                    [namao(B,M),topo(BS)],
                    [sobre(B,BS),topo(B),livre(M)]):-   member(B,[a,b,c,d,e]), 
                                                        member(BS,[a,b,c,d,e]),
                                                        B\=BS, 
                                                        member(M,[1,2]).

accao(agarraG(B),   [tamanho(B,2),topo(B),livre(1),livre(2),sobre(B,BS)],
                    [namao(B,1),namao(B,2),topo(BS)],
                    [sobre(B,BS),topo(B),livre(1), livre(2)]):- member(B,[a,b,c,d,e]), 
                                                                member(BS,[a,b,c,d,e]),
                                                                B\=BS.

accao(larga(B,M,BS),[topo(BS),namao(B,M),tamanho(B,1)],
                    [topo(B),sobre(B,BS),livre(M)],
                    [topo(BS),namao(B,M)]):-member(B,[a,b,c,d,e]), 
                                            member(BS,[a,b,c,d,e]),
                                            B\=BS,
                                            member(M,[1,2]). 

accao(largaG(B,BS) ,[topo(BS),namao(B,1),namao(B,2),tamanho(B,2),tamanho(BS,2)],
                    [topo(B),sobre(B,BS),livre(1),livre(2)],
                    [topo(BS),namao(B,1),namao(B,2)]):- member(B,[a,b,c,d,e]), 
                                                        member(BS,[a,b,c,d,e]),
                                                        B\=BS.