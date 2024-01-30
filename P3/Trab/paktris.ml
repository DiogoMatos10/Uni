(*DEFINIÇÕES, PARAMETROS E PREPARAÇÃO*)
(* Definir os tipos peça e jogada *)
type peca = I | O | S | T ;;
type jogada = peca * int * int ;;

(*tipo auxiliar para as funções set_pieces*)
type orientation = N | S | E | W ;;

(* Inicializador das dimensões *)
let n = 8;; (*altura*)
let m = 5;; (*largura*)

(*criação do array*)
let board = Array.make_matrix m n 0 ;;

(*duas listas de jogadas para teste, uma que cabe e outra que não*)
let cabe = [(I,0,0);(I,0,0);(S,1,1);(S,1,1);(S,1,1);(O,0,3);(O,0,3);(O,0,3);(S,0,2)];;
let naocabe = [(I,0,0);(I,0,0);(I,0,0)];;

(*##############################################################################################################################################################################################################################*)
(*FUNÇÕES RELEVANTE À LÓGICA DE QUEDA DAS PEÇAS E COLISÃO*)

(*verifica se existe colisão da peça com outra peça ou com o fundo do tabuleiro*)
let check_colision = function board -> function (x,y) -> function 
  |[|a;b;c;d|]-> if y-a = -1 then true else if y-a >= n then false else if board.(x).(y-a)>0 || board.(x+1).(y-b)>0 || board.(x+2).(y-c)>0 ||board.(x+3).(y-d)>0 then true else false
  |[|a;b;c|] -> if y-a = -1||y-b = -1||y-c = -1 then true else if y-a >= n || y-b >= n || y-c >= n then false else if board.(x).(y-a)>0||board.(x+1).(y-b)>0 ||board.(x+2).(y-c)>0 then true else false
  |[|a;b|] -> if y-a = -1||y-b = -1  then true else if y-a >= n || y-b >= n then false else if board.(x).(y-a)>0 ||board.(x+1).(y-b)>0 then true else false
  |[|a|] -> if y-a = -1 then true else if y-a >= n then false else if board.(x).(y-a)>0 then true else false ;;

(*aplica a queda gradual da peça, verificando colisão com a função acima*)
let rec fall = function board -> function (x,y) -> function
  |[|a;b;c;d|]-> if (check_colision board (x,y) [|a;b;c;d|]) then (x,y) else fall board (x,y-1) [|a;b;c;d|]
  |[|a;b;c|] -> if (check_colision board (x,y) [|a;b;c|]) then (x,y) else fall board (x,y-1) [|a;b;c|]
  |[|a;b|] -> if (check_colision board (x,y) [|a;b|]) then (x,y) else fall board (x,y-1) [|a;b|] 
  |[|a|] -> if (check_colision board (x,y) [|a|]) then (x,y) else fall board (x,y-1) [|a|];;
  

(*##############################################################################################################################################################################################################################*)
(*FUNÇÕES PARA UPDATE DO TABULEIRO DO JOGO CONFORME A JOGADA*)
(*cada peça tem uma função respetiva que responde a todas as variações de orientação que essa peça pssui*)
(*cada peça coloca um valor diferente no array - I=1 | O=2 | S=3 | T=4 - vai ser util na apresentação*)

let set_piece_I board (x,y) dir = match dir with
  | N -> board.(x).(y) <- 1 ;  board.(x).(y-1) <- 1 ;  board.(x).(y-2) <- 1 ;  board.(x).(y-3) <- 1 ; true 
  | E -> board.(x).(y) <- 1 ;  board.(x+1).(y) <- 1 ;  board.(x+2).(y) <- 1 ;  board.(x+3).(y) <- 1 ; true;;

let set_piece_O board = function (x,y) -> board.(x).(y) <- 2 ;  board.(x).(y-1) <- 2 ;  board.(x+1).(y) <- 2 ;  board.(x+1).(y-1) <- 2 ; true;; 

let set_piece_S board (x,y) dir = match dir with
  | E -> board.(x).(y-1) <- 3 ;  board.(x+1).(y-1) <- 3 ;  board.(x+1).(y) <- 3 ;  board.(x+2).(y) <- 3 ; true 
  | N -> board.(x).(y) <- 3 ;  board.(x).(y-1) <- 3 ;  board.(x+1).(y-1) <- 3 ;  board.(x+1).(y-2) <- 3 ; true;;

let set_piece_T board (x,y) dir = match dir with
  | S -> board.(x).(y) <- 4 ;  board.(x+1).(y) <- 4 ;  board.(x+1).(y-1) <- 4 ;  board.(x+2).(y) <- 4 ; true 
  | W -> board.(x).(y-1) <- 4 ;  board.(x+1).(y) <- 4 ;  board.(x+1).(y-1) <- 4 ;  board.(x+1).(y-2) <- 4 ; true
  | N -> board.(x+1).(y) <- 4 ;  board.(x).(y-1) <- 4 ;  board.(x+1).(y-1) <- 4 ;  board.(x+2).(y-1) <- 4 ; true 
  | E -> board.(x).(y) <- 4 ;  board.(x).(y-1) <- 4 ;  board.(x).(y-2) <- 4 ;  board.(x+1).(y-1) <- 4 ; true;;

(*##############################################################################################################################################################################################################################*)
(*VERIFICAÇÃO DE PEÇA VERTICALMENTE FORA DO TABULEIRO *)
(*true se estiver fora, false se não*)  

let out_of_bounds = function (_,y) -> if y >= n then true else false;;

(*##############################################################################################################################################################################################################################*)
(*JOGADAS DAS DIFERENTES PEÇAS A CONTAR COM ROTAÇÃO*)

(*As jogadas das diferentes peças apenas diferem nos valores mandandos ás outras funçòes, fazendo assim uso das mesmas funções*)

(*As rotações das peças são um pouco peculiares, pelo que pedimos a consulta dos detalhes da implementação das rotações no relatório.*)
(*lá estarão representações visuais da rotação de cada uma das peças, e que condições são necesárias para a jogada das mesmas.*)

let rec make_play_I board nrot ndir = 
  match nrot with
  |0 -> if ndir > m-1 || ndir<0 then (print_string("jogada invalida\n"); false ) else if out_of_bounds (fall board (ndir,n+4) [|4|]) then false else set_piece_I board (fall board (ndir,n+4) [|4|] ) N 
  |1 -> if ndir > m-4 || ndir<0 then (print_string("jogada invalida\n"); false ) else if out_of_bounds (fall board (ndir,n+1) [|1;1;1;1|]) then false else set_piece_I board (fall board (ndir,n+1) [|1;1;1;1|] ) E 
  |2 -> if ndir > m-1 || ndir<0 then (print_string("jogada invalida\n"); false ) else if out_of_bounds (fall board (ndir,n+4) [|4|]) then false else set_piece_I board (fall board (ndir,n+4) [|4|] ) N 
  |3 -> if ndir > m-1 || ndir<3 then (print_string("jogada invalida\n"); false ) else if out_of_bounds (fall board (ndir-3,n+1) [|1;1;1;1|]) then false else set_piece_I board (fall board (ndir-3,n+1) [|1;1;1;1|] ) E ;;

let rec make_play_O board nrot ndir = 
  match nrot with
  |0 -> if ndir > m-2 || ndir<0 then (print_string("jogada invalida\n"); false ) else if out_of_bounds (fall board (ndir,n+2) [|2;2|]) then false else set_piece_O board (fall board (ndir,n+2) [|2;2|])  
  |1 -> if ndir > m-2 || ndir<0 then (print_string("jogada invalida\n"); false ) else if out_of_bounds (fall board (ndir,n+2) [|2;2|]) then false else set_piece_O board (fall board (ndir,n+2) [|2;2|]) 
  |2 -> if ndir > m-1 || ndir<1 then (print_string("jogada invalida\n"); false ) else if out_of_bounds (fall board (ndir-1,n+2) [|2;2|]) then false else set_piece_O board (fall board (ndir-1,n+2) [|2;2|]) 
  |3 -> if ndir > m-1 || ndir<1 then (print_string("jogada invalida\n"); false ) else if out_of_bounds (fall board (ndir-1,n+2) [|2;2|]) then false else set_piece_O board (fall board (ndir-1,n+2) [|2;2|]);;
   
let rec make_play_S board nrot ndir = 
  match nrot with
  |0 -> if ndir > m-3 || ndir<0 then (print_string("jogada invalida\n"); false ) else if out_of_bounds (fall board (ndir,n+2) [|2;2;1|]) then false else set_piece_S board (fall board (ndir,n+2) [|2;2;1|]) E 
  |1 -> if ndir > m-2 || ndir<0 then (print_string("jogada invalida\n"); false ) else if out_of_bounds (fall board (ndir,n+3) [|2;3|]) then false else set_piece_S board (fall board (ndir,n+3) [|2;3|]) N
  |2 -> if ndir > m-1 || ndir<2 then (print_string("jogada invalida\n"); false ) else if out_of_bounds (fall board (ndir-2,n+2) [|2;2;1|]) then false else set_piece_S board (fall board (ndir-2,n+2) [|2;2;1|]) E 
  |3 -> if ndir > m-1 || ndir<1 then (print_string("jogada invalida\n"); false ) else if out_of_bounds (fall board (ndir-1,n+3) [|2;3|]) then false else set_piece_S board (fall board (ndir-1,n+3) [|2;3|]) N ;;
  
let rec make_play_T board nrot ndir = 
  match nrot with
  |0 -> if ndir > m-3 || ndir<0 then (print_string("jogada invalida\n"); false ) else if out_of_bounds (fall board (ndir,n+2) [|1;2;1|]) then false else set_piece_T board (fall board (ndir,n+2) [|1;2;1|]) S 
  |1 -> if ndir > m-2 || ndir<0 then (print_string("jogada invalida\n"); false ) else if out_of_bounds (fall board (ndir,n+3) [|2;3|]) then false else set_piece_T board (fall board (ndir,n+3) [|2;3|]) W
  |2 -> if ndir > m-1 || ndir<2 then (print_string("jogada invalida\n"); false ) else if out_of_bounds (fall board (ndir-2,n+2) [|2;2;2|]) then false else set_piece_T board (fall board (ndir-2,n+2) [|2;2;2|]) N 
  |3 -> if ndir > m-1 || ndir<1 then (print_string("jogada invalida\n"); false ) else if out_of_bounds (fall board (ndir-1,n+3) [|3;2|]) then false else set_piece_T board (fall board (ndir-1,n+3) [|3;2|]) E ;;

(*##############################################################################################################################################################################################################################*)
(*INICIALIZADOR DA JOGADA*)

let make_play = function board -> function 
|(I, nrot, ndir) -> make_play_I board (nrot mod 4) ndir 
|(O, nrot, ndir) -> make_play_O board (nrot mod 4) ndir
|(S, nrot, ndir) -> make_play_S board (nrot mod 4) ndir
|(T, nrot, ndir) -> make_play_T board (nrot mod 4) ndir;;

(*##############################################################################################################################################################################################################################*)
(*MOSTRAR O TABULEIRO*)

let print_board arr =
  let rows = Array.length arr in
  let cols = Array.length arr.(0) in
  for j = cols - 1 downto 0 do
    for i = 0 to rows - 1 do
      if arr.(i).(j) = 1 then Printf.printf "I " 
      else if arr.(i).(j) = 2 then Printf.printf "O "
      else if arr.(i).(j) = 3 then Printf.printf "S "
      else if arr.(i).(j) = 4 then Printf.printf "T "  
      else Printf.printf ". ";
    done;
    print_newline ();
  done;;

(*##############################################################################################################################################################################################################################*)  
(*LIMPAR O TABULEIRO*)

  let clear_board arr =
    let rows = Array.length arr in
    let cols = Array.length arr.(0) in
    for j = cols - 1 downto 0 do
      for i = 0 to rows - 1 do
        arr.(i).(j)<-0;
      done;
    done; print_board arr;;

(*##############################################################################################################################################################################################################################*)  
(*FUNÇÃO INICIAL*)
(*como descrito no enunciado, aceita uma lista de jogadas e retorna true se as jogadas couberem TODAS, e false em caso contrário*)
(*muito simples, mas faz toda a magia acontecer :] *)

let rec paktris = function 
  |[] -> true ;
  |head::tail -> if make_play board head then paktris tail else false;;