#include <stdio.h>
#include <stdlib.h>
#include <time.h>
#include <ctype.h>

//declaração dos tipos compostos (structs)
struct jogada{
  int col;
  int line;
};

struct jogada_char{
  char col;
  char line;
};

//##############################################################################################################################################################################################
//# finish - Esta função é executada quando o jogo acaba (seja porque o tabuleiro está cheio ou porque já não há jogadas válidas para nenhuma das cores), contas as peças e determina quem ganhou,
//# mostrando as mensagens de fim de jogo;
//#
//# argumentos:
//# board - tabuleiro do jogo(array de caracteres);
//# modo - o modo de jogo selecionado no inicio do jogo (um jogador ou dois jogadores);
//# num - número de jogadas realizadas no jogo;
//# comcolor - cor das peças do computador (caso seja necessário);
//#
//# valor de retorno:
//# nenhum(void).
//##############################################################################################################################################################################################
void finish(char board[8][8], int modo, int num, char comcolor){
  int i, j;
	int count_brancas=0; 
	int count_pretas=0;

  printf("\n\nO jogo acabou!");
  
  //para o modo de dois jogadores
  if(modo==2){
    //conta-se o número de peças brancas e pretas:
    for(i=0;i<8;i++){
      for(j=0;j<8;j++){
        if(board[i][j]=='O'){
          count_brancas++;
        }
        else if(board[i][j]=='X'){
          count_pretas++;
        }
      } 
    }
    //pelo número de peças determina-se o vencedor: 
    if(count_brancas>count_pretas){
      printf("\n\nO vencedor é o jogador das peças brancas. Parabéns!\nO jogo acabou com %d jogadas.\nPeças brancas:%d;\nPeças pretas:%d\n\n", num, count_brancas, count_pretas);
    }
    else if(count_brancas<count_pretas){
      printf("\n\nO vencedor é o jogador das peças brancas. Parabéns!\n\nO jogo acabou com %d jogadas.\nPeças brancas:%d;\nPeças pretas:%d\n\n", num, count_brancas, count_pretas);
    }
    else{
      printf("\n\nEmpate!\nO jogo acabou com %d jogadas.\nPeças brancas:%d;\nPeças pretas:%d\n\n", num, count_brancas, count_pretas);
    }
  }

  //para o modo de um jogador(contra o computador)
  else if(modo==1){
    //conta-se o numero de peças brancas e pretas:
    for(i=0;i<8;i++){
      for(j=0;j<8;j++){
        if(board[i][j]=='O'){
          count_brancas++;
        }
        if(board[i][j]=='X'){
          count_pretas++;
        }
      } 
    }
    if(count_brancas>count_pretas){
      if(comcolor=='O'){ 
        printf("\n\nO jogador perdeu!\nO jogo acabou com %d jogadas.\nPeças brancas:%d;\nPeças pretas:%d", num, count_brancas, count_pretas);
      }
      else{
        printf("\n\nO jogador Ganhou!!\nO jogo acabou com %d jogadas.\nPeças brancas:%d;\nPeças pretas:%d", num, count_brancas, count_pretas);
      }
    }
    else if(count_brancas<count_pretas){
      if(comcolor=='X'){ 
        printf("\n\nO jogador perdeu!\nO jogo acabou com %d jogadas.\nPeças brancas:%d;\nPeças pretas:%d", num, count_brancas, count_pretas);
      }
      else{
        printf("\n\nO jogador Ganhou!!\nO jogo acabou com %d jogadas.\nPeças brancas:%d;\nPeças pretas:%d", num, count_brancas, count_pretas);
      }
    }
    else{
      printf("\n\nEmpate!\nO jogo acabou com %d jogadas.\nPeças brancas:%d;\nPeças pretas:%d", num, count_brancas, count_pretas);
    }
  }
  exit(1);
}

//##############################################################################################################################################################################################
//# num_char_to_int - Esta função retorna o número correspondente ao caractere numérico (usada para converter as jogas obtidas do ficheiro de caractere para inteiro)
//#
//# argumentos;
//# var - aquilo que se quer converter de charactere (númerico) para inteiro;
//#
//# valor de retorno:
//# valor convertido, inteiro.
//##############################################################################################################################################################################################
int num_char_to_int(char var){
  if(var=='1'){
    return 0;
  }
  else if(var=='2'){
    return 1;
  }
  else if(var=='3'){
    return 2;
  }
  else if(var=='4'){
    return 3;
  }
  else if(var=='5'){
    return 4;
  }
  else if(var=='6'){
    return 5;
  }
  else if(var=='7'){
    return 6;
  }
  else if(var=='8'){
    return 7;
  }
}

//##############################################################################################################################################################################################
//# let_to_num - Esta função serve para converter caracteres(letras) para números(inteiro)
//#
//# argumentos;
//# var - aquilo que se quer converter de charactere (letra) para o seu inteiro correspondente(no array do tabuleiro);
//#
//# valor de retorno:
//# valor convertido, inteiro.
//##############################################################################################################################################################################################
int let_to_num(char var){
  var=toupper(var); //garante que a letra da coluna vai ser sempre maiúscula.
  if(var=='A'){
    return 0;
  }
  else if(var=='B'){
    return 1;
  }
  else if(var=='C'){
    return 2;
  }
  else if(var=='D'){
    return 3;
  }
  else if(var=='E'){
    return 4;
  }
  else if(var=='F'){
    return 5;
  }
  else if(var=='G'){
    return 6;
  }
  else if(var=='H'){
    return 7;
  }
}

//##############################################################################################################################################################################################
//# num_to_let - Esta função serve para converter números inteiros para letras (usado para conversão entre os números das colunas no array "board" e as suas respetivas letras.)
//#
//# argumentos;
//# var - aquilo que se quer converter de inteiro para o seu caractere (letra) correspondente(no array do tabuleiro);
//#
//# valor de retorno:
//# valor convertido, caracter.
//##############################################################################################################################################################################################
char num_to_let(int var){
  if(var==0){
    return 'A';
  }
  else if(var==1){
    return 'B';
  }
  else if(var==2){
    return 'C';
  }
  else if(var==3){
    return 'D';
  }
  else if(var==4){
    return 'E';
  }
  else if(var==5){
    return 'F';
  }
  else if(var==6){
    return 'G';
  }
  else if(var==7){
    return 'H';
  }
}

//##############################################################################################################################################################################################
//# verify_full - Esta função vai percorrer o tabuleiro em busca de espaços "vazios", retornando 0 se houver espaços e 1 se o tabuleiro estiver cheio.
//#
//# argumentos:
//# board - tabuleiro do jogo(array de caracteres);
//#
//# valor de retorno:
//# valor correspondente ao acima discriminado, inteiro.
//##############################################################################################################################################################################################
int verify_full(char board[8][8]){
  int i, j;
  for(i=0;i<8;i++){
    for(j=0;j<8;j++){
      if(board[i][j]=='.'){
        return 0;
      }
    }
  }
  return 1;
}

//##############################################################################################################################################################################################
//# verify - Esta função vai verificar se há jogadas válidas para o jogador atual, retornando 1 se houver jogadas válidas e 0 se não.
//#
//# argumentos:
//# board - tabuleiro do jogo(array de caracteres);
//# color - cor das peças do jogador atual.
//#
//# valor de retorno:
//# valor correspondente ao acima discriminado, inteiro.
//##############################################################################################################################################################################################
int verify(char board[8][8], char color){

  int i,j,i1,j1;
  char color2;

  //color2 vai ser a cor oposta à cor que está a jogar de momento, quando a função é executada.
  if(color=='X'){
    color2='O';
  }
  else if(color=='O'){
    color2='X';
  }

  for(i=0; i<8; i++){
    for(j=0; j<8; j++){
      if(board[i][j]==color){
        //verificar para a esquerda se é válido:
        if(board[i][j-1]==color2){
          for(j1=j-2; j>=0; j1--){
            if(board[i][j1]=='.'){
              return 1;
            }
            if(board[i][j1]==color){
              break;
            }
          }
        }
        //verificar para a direita se é válido:
        if(board[i][j+1]==color2){
          for(j1=j+2; j1<8; j1++){
            if(board[i][j1]=='.'){
              return 1;
            }
            if(board[i][j1]==color){
              break;
            }
          }
        }
        //verificar para cima se é válido:
        if(board[i-1][j]==color2){
          for(i1=i-2; i1>=0; i1--){
            if(board[i1][1]=='.'){
              return 1;
            }
            if(board[i1][j]==color){
              break;
            }
          }
        }

        //verificar para baixo se é válido:
        if(board[i+1][j]==color2){
          for(i1=i+2; i1<8; i1++){
            if(board[i1][j]=='.'){
              return 1;
            }
            if(board[i1][j]==color){
              break;
            }
          }
        }

        //verificar para cima-esquerda se é válido
        if(board[i-1][j-1]==color2){
          i1=i-2;
          j1=j-2;
          while(i1>=0 && j1>=0){
            if(board[i1][j1]=='.'){
              return 1;
            }
            if(board[i1][j1]==color){
              break;
            }
            i1--;
            j1--;
          }
        }

        //verificar para cima-direita se é válido
        if(board[i-1][j+1]==color2){
          i1=i-2;
          j1=j+2;
          while(i1>=0 && j1<8){
            if(board[i1][j1]=='.'){
              return 1;
            }
            if(board[i1][j1]==color){
              break;
            }
            i1--;
            j1++;
          }
        }

        //verificar para baixo-esquerda se é válido
        if(board[i+1][j-1]==color2){
          i1=i+2;
          j1=j-2;
          while(i1>=0 && j1<8){
            if(board[i1][j1]=='.'){
              return 1;
            }
            if(board[i1][j1]==color){
              break;
            }
            i1++;
            j1--;
          }
        }

        //verificar para baixo direita se é válido
        if(board[i+1][j+1]==color2){
          i1=i+2;
          j1=j+2;
          while(i1>=0 && j1<8){
            if(board[i1][j1]=='.'){
              return 1;
            }
            if(board[i1][j1]==color){
              break;
            }
            i1++;
            j1++;
          }
        }

      }  
    }
  }
  return 0;
}  

//##############################################################################################################################################################################################
//# count_flips_dir - Esta função é a parte "operacional" da contagem das peças viradas na jogada atual. Conta as peças numa certa direção do tabuleiro. Aliada à função flanked (abaixo), conta
//# as peças viradas na jogada atual.
//#
//# argumentos:
//# board - tabuleiro do jogo(array de caracteres);
//# line - linhas(do array) da jogada atual;
//# col - coluna(do array) da jogada atual;
//# delta_line - direção referente ás linhas, na qual se quer que a função conte as peças viradas;
//# delta_col - direção referente ás colunas, na qual se quer que a função conte as peças viradas;
//# color - cor das peças do jogador atual.
//#
//# valor de retorno:
//# O número de peças viradas na direção especificada, inteiro.
//##############################################################################################################################################################################################
int count_flips_dir(char board[8][8], int line, int col, int delta_line, int delta_col, char color){
  char color2; //esta variável guarda a cor contrária à do jogador atual
  int count=0;
  if(color=='X'){
    color2='O';
  }
  else if(color=='O'){
    color2='X';
  }
  while(board[line+delta_line][col+delta_col]==color2 && line+delta_line<=7 && line+delta_line>=0 &&col+delta_col<=7 && col+delta_col>=0){
    count++;
    line=line+delta_line;
    col=col+delta_col;
    if(line+delta_line<=7 && line+delta_line>=0 &&col+delta_col<=7 && col+delta_col>=0){ 
      if(board[line+delta_line][col+delta_col]==color){
        return count;
      }
    }
  }
  return 0;
}

//##############################################################################################################################################################################################
//# flanked - Esta função é a parte "logística" da contagem das peças viradas na jogada atual. Depois de mandar todas as direções possíveis para a função count_flips_dir (acima), soma os valores
//# por ela retornados. Aliada à função count_flips_dir(acima), conta as peças viradas na jogada atual.
//#
//# argumentos:
//# board - tabuleiro do jogo(array de caracteres);
//# line - linhas(do array) da jogada atual;
//# col - coluna(do array) da jogada atual;
//# color - cor das peças do jogador atual.
//#
//# valor de retorno:
//# número de peças viradas na jogada atual, inteiro.
//##############################################################################################################################################################################################
int flanked(char board[8][8],int line, int col, char color){
  int flips;
  flips=count_flips_dir(board,line,col,-1,0, color); //para cima
  flips=flips+count_flips_dir(board,line,col,1,0, color);//para baixo
  flips=flips+count_flips_dir(board,line,col,0,-1, color);//para esquerda
  flips=flips+count_flips_dir(board,line,col,0,1, color); //para direita
  flips=flips+count_flips_dir(board,line,col,-1,-1, color);//diagonal cima-esquerda
  flips=flips+count_flips_dir(board,line,col,-1,1, color);//diagonal cima-direita
  flips=flips+count_flips_dir(board,line,col,1,-1, color);//diagonal baixo-esquerda
  flips=flips+count_flips_dir(board,line,col,1,1, color);//diagonal baixo-direita
  return flips;
}

//##############################################################################################################################################################################################
//# delta_play - Esta função é a parte "operacional" da execução das jogadas. Percorre todas as direções, virando as peças de acordo com as regras do jogo. Aliada à função play (abaixo), executa
//# as jogadas.
//#
//# argumentos:
//# board - tabuleiro do jogo(array de caracteres);
//# line - linhas(do array) da jogada atual;
//# col - coluna(do array) da jogada atual;
//# delta_line - direção relativamente às linhas, na qual se quer que a função vire as peças;
//# delta_col - direção relativamente às colunas, na qual se quer que a função vire as peças;
//# color - cor das peças do jogador atual;
//# color2 - cor das peças adversárias ao jogador atual
//#
//# valor de retorno:
//# nenhum (void).
//##############################################################################################################################################################################################
void delta_play(char board[8][8], int line, int col, int delta_line, int delta_col, char color, char color2){
  int i,j;
  i=line;
  j=col;
  while(board[line+delta_line][col+delta_col]==color2){
    line=line+delta_line;
    col=col+delta_col;
    if(line+delta_line>7 || col+delta_col>7 || line+delta_line<0 || col+delta_col<0){
      return;
    }
    if(board[line+delta_line][col+delta_col]==color){
      while(line!=i || col!=j){
        board[line][col]=color;
        line=line-delta_line;
        col=col-delta_col;
      }
      return;
    }
  }
}

//##############################################################################################################################################################################################
//# play - Esta função é a parte "logística" da execução das jogadas. Coloca uma peça da cor do jogador atual na posição(do array) designada e manda todas as direções possíveis para a função
//# delta_play (acima). Aliada à função delta_play (acima), executa as jogadas.
//#
//# argumentos:
//# board - tabuleiro do jogo(array de caracteres);
//# line - linhas(do array) da jogada atual;
//# col - coluna(do array) da jogada atual;
//# color - cor das peças do jogador atual.
//#
//# valor de retorno:
//# nenhum (void).
//##############################################################################################################################################################################################
void play(char board[8][8], int line, int col, char color){ 
  char color2;      //esta variável guarda a cor contrária à do jogador atual
  if(color=='X'){
    color2='O';
  }
  else if(color=='O'){
    color2='X';
  }
  
  board[line][col]=color;//coloca uma peça da cor que jogou no sitio especificado na jogada.

  delta_play(board,line,col,0,-1,color,color2);//para a esquerda:
  delta_play(board,line,col,0,1,color,color2);//para a direita
  delta_play(board,line,col,-1,0,color,color2);//para cima
  delta_play(board,line,col,1,0,color,color2);//para baixo
  delta_play(board,line,col,-1,-1,color,color2);//esquerda-cima
  delta_play(board,line,col,1,-1,color,color2);//esquerda-baixo
  delta_play(board,line,col,-1,1,color,color2);//direita-cima
  delta_play(board,line,col,1,1,color,color2);//direita-baixo
}

//##############################################################################################################################################################################################
//# computer_play_easy - Esta função  escolhe randomicamente uma das possíveis jogadas que o computador pode fazer e aplica-a ao tabuleiro.
//#
//# argumentos:
//# board - tabuleiro do jogo(array de caracteres);
//# color - cor das peças do computador.
//#
//# valor de retorno:
//# jogada do computador, tipo composto struct jogada (especificado no inicio do código).
//##############################################################################################################################################################################################
struct jogada computer_play_easy(char board[8][8], char color){
  srand(time(NULL));
  struct jogada jogada_pc;
  jogada_pc.line=-1;
  jogada_pc.col=-1;
  int pass=rand()%5;
  int i,j;
  while(jogada_pc.line==-1 || jogada_pc.col==-1){  
    for(i=0;i<8;i++){
      for(j=0;j<8;j++){
        if(board[i][j]=='.' && flanked(board,i,j,color)!=0){
          jogada_pc.line=i;
          jogada_pc.col=j;
          pass--;
        }
        if(pass<=0 && jogada_pc.line!=-1 && jogada_pc.col!=-1){
          play(board,i,j,color);
          return jogada_pc;
        }
      }
    }
  }
}

//##############################################################################################################################################################################################
//# computer_play_normal - Esta função escolhe, das possíveis jogadas que o computador pode fazer, aquela que vira mais peças, e aplica-a ao tabuleiro.
//#
//# argumentos:
//# board - tabuleiro do jogo(array de caracteres);
//# color - cor das peças do computador.
//#
//# valor de retorno:
//# jogada do computador, tipo composto struct jogada (especificado no inicio do código).
//############################################################################################################################################################################################## 
struct jogada computer_play_normal(char board[8][8], char color){
  struct jogada bestplay;
  bestplay.col=0;
  bestplay.line=0;
  int i,j;
  for(i=0;i<8;i++){
    for(j=0;j<8;j++){ 
      if(board[i][j]=='.' && flanked(board,i,j, color)>flanked(board,bestplay.line,bestplay.col,color)){
        bestplay.line=i;
        bestplay.col=j;
      }
    }
  }
  play(board,bestplay.line,bestplay.col, color);
  return bestplay;
}

//##############################################################################################################################################################################################
//# init_board - Inicializa o tabuleiro na posição inicial, com as quatro peças alternadas no centro do tabuleiro.
//#
//# argumentos:
//# board - tabuleiro do jogo(array de caracteres);
//#
//# valor de retorno:
//# nenhum (void).
//##############################################################################################################################################################################################
void init_board(char board[8][8]){
  int i,j;
  //substitui todas as posições tabuleiro todo com "."(limpa o tabuleiro).
  for(i=0;i<8;i++){
    for(j=0;j<8;j++){
      board[i][j]='.';
    }
  }
    
  //inicializa a posição inicial no meio do tabuleiro.
  board[3][3]='O';
  board[3][4]='X';
  board[4][3]='X';
  board[4][4]='O';
}

//##############################################################################################################################################################################################
//# print_board - Mostra o tabuleiro atual.
//#
//# argumentos:
//# board - tabuleiro do jogo(array de caracteres);
//#
//# valor de retorno:
//# nenhum (void).
//##############################################################################################################################################################################################
void print_board(char board[8][8]){
  char linhas[8]={'1','2','3','4','5','6','7','8'};
  int i,j;
  
  printf("\n");
  printf("  A B C D E F G H\n"); //cria uma linha com a numeração das colunas.

  for(i=0;i<8;i++){ //o primeiro for atravessa as linhas do tabuleiro.
    printf("%c ",linhas[i]);
    for(j=0;j<8;j++){ //o segundo for atravessa as colunas do tabuleiro.
      printf("%c ",board[i][j]);
    }
    printf("\n");
  }
  printf("\n");
}

//##############################################################################################################################################################################################
//# main - Esta função usa todas as outras apresentadas anteriormente para fazer o jogo funcionar.
//#
//# argumentos:
//# argn - número de "palavras"(separadas por espaços) presentes quando o jogo é inicializado(./othelo);
//# args - array de strings que guarda as strings acima referidas.
//#
//# valor de retorno:
//# nenhum (void).
//##############################################################################################################################################################################################
void main(int argn, char* args[]){
  char board[8][8];
  char plcolor, comcolor, col;
	char color='X';
	int inicio, i, j, line, count_fim;
	int vez=0;
  int play_count=0;
	int ficheiro_ver=0;
  struct jogada_char jogadas_char[60];
	FILE *fp;

	if(argn>2){
		printf("ERRO : Formato Inválido. Se está a tentar iniciar o jogo com um ficheiro, considere corrigir a sua escrita. Inicio abortado!\n");
		exit(1);
	}
	
	//se forem detectadas duas strings como argumentos do main(no caso, o nome do ficheiro depois do "./othello")
	if(argn==2){
		fp=fopen(args[1], "r");
		if(fp==NULL){
			printf("ERRO : O ficheiro especificado não existe. Considere corrigir a sua escrita. Inicio abortado!\n");
			exit(1);
		}
		ficheiro_ver=1;
		init_board(board);
		//aqui transfere-se todas as jogadas do ficheiro para o array jogadas_char
		for(i=0;feof(fp)==0;i++){
    	jogadas_char[i].line=fgetc(fp);
    	jogadas_char[i].col=fgetc(fp);
    	fgetc(fp);
    	fgetc(fp);
  	}
		for(j=0;j<i;j++){
			if(flanked(board,num_char_to_int(jogadas_char[j].line),let_to_num(jogadas_char[j].col),color)==0){
				printf("ERRO : Foi detectada como inválida pelo menos uma da jogadas presentes no ficheiro. Inicio Abortado!\n");
				exit(1);
			}
			play(board,num_char_to_int(jogadas_char[j].line),let_to_num(jogadas_char[j].col),color);
			if(color=='X'){
				color='O';
				vez=1;
			}
			else if(color=='O'){
				color='X';
				vez=0;
			}
		}
	}
  
  srand(time(NULL));

  //menu inicial do jogo.
	if(ficheiro_ver==0){ 
  	printf("Bem vindo!!\n\nEscolha o modo de jogo:\n\n1 - Um jogador \n2 - Dois jogadores\n3 - Sair\n\n");
		init_board(board);
	}
	if(ficheiro_ver==1){
		printf("Bem vindo, as jogadas presentes no ficheiro indicado foram executadas com sucesso no tabuleiro.\n\nEscolha o modo de jogo:\n\n1 - Um jogador \n2 - Dois jogadores\n3 - Sair\n\n");
	}
	scanf("%d", &inicio);  
  printf("\n");
  while(inicio<0 || inicio>3){ 
  printf("Valor invalido, introduza outro valor.\n\n");
    scanf("%d", &inicio);
  }    
  if(inicio==3){
    exit(1);
  }
  
  //modo de dois jogadores:
  if(inicio==2){
    print_board(board);
    printf("\n\n");
    for(i=0;i<=60;i++){ //só podem ser feitas no máximo 60 jogadas, daí o limite de i<60.
      
      //a variável count_fim vai contar as vezes que não se acham jogadas válidas;
      //serve para acabar o jogo se alguma vez não se encontrarem jogas válidas para as duas cores (count_fim==2)
      count_fim=0;

      //vez das peças pretas.
			if(vez==0){ 
				if(verify(board,'X')==0){ //vai verificar se há jogadas válidas a fazer pelas pretas, e se não houver passa a vez ás brancas.
					printf("As peças pretas não têm jogadas válidas\n\n");
					count_fim++;
				}
				else if(verify(board, 'X')==1){
					printf("É a vez das peças pretas");
					int flk=0;
					while(flk==0){
						printf("\n\nEscreva a sua jogada: ");
						scanf(" %d%c", &line, &col);
						flk=flanked(board,line-1,let_to_num(col),'X');
						if(flk==0){
							printf("\n\nJogada inválida\n\n");
						}
					}
					play(board,line-1,let_to_num(col),'X');
					printf("\n\n");

					//incrementa o número de jogadas.
					play_count++;
				
					//depois da jogada, o tabuleiro é exibido novamente
					print_board(board);
				}
				vez=1;
			}

      //vez das peças brancas.
			if(vez==1){ 
				if(verify(board, 'O')==0){ //vai verificar se há jogadas válidas a fazer pelas brncas, e se não houver passa a vez ás pretas.
					printf("As peças brancas não têm jogadas válidas. Vez das pretas\n\n");
					count_fim++;
				}
				else if(verify(board, 'O')==1){
					printf("É a vez das peças brancas");
					int flk=0;
					while(flk==0){
						printf("\n\nEscreva a sua jogada: ");
						scanf(" %d%c", &line, &col);
						flk=flanked(board,line-1,let_to_num(col),'O');
						if(flk==0){
							printf("\n\nJogada inválida\n\n");
						}
					}
					play(board,line-1,let_to_num(col),'O');
					printf("\n\n");

					//incrementa o número de jogadas.
					play_count++;
				
					//depois da jogada, o tabuleiro é exibido novamente
					print_board(board);
				}
				vez=0;
			}
      if(count_fim==2 || verify_full(board)==1){
        finish(board, 2, play_count, 'n'); //o argumento 'n' serve apenas para eliminar o warning de compilação que dá quando há um número de argumentos diferente do que a função pede;
      }                                    //sendo assim o argumento 'n' não tem qualquer peso no resto do código.
    }
  }

  //modo de um jogador (contra computador):
  if(inicio==1){ 
    int dific;
    struct jogada jogada_pc; 
    
    //ecolha da dificuldade:
    printf("Escolha a dificuldade do jogo:\n\n1 - Fácil\n2 - Normal\n\n");
    scanf("%d",&dific);
    printf("\n\n");
    while(dific!=1 && dific!=2){
      printf("Escolha invalida. Introduza outra escolha;\n\n");
      printf("Escolha a dificuldade do jogo:\n\n1 - Fácil\n2 - Normal\n\n");
      scanf("%d",&dific);
    }

    //escolha das peças
    int ran=rand()%2;
    if(ran==0){
      //se o jogador ficar com as peças brancas.
      printf("O jogador ficou com as peças brancas\n\n");
      plcolor='O';
      comcolor='X';
      vez=0; 
    }  
    else{
      //se o jogador ficar com as peças pretas.
      printf("O jogador ficou com as peças pretas\n\n");
      plcolor='X';
      comcolor='O';
      vez=1;
    }
   
		if(ficheiro_ver==1){
			if(plcolor==color){
				vez=1;
			}
			else{
				vez=0;
			}
		}

    //display do tabuleiro inicial.
    if(ficheiro_ver==0){ 
			init_board(board);
		}
    print_board(board);
    printf("\n\n");

    //Loop das jogadas:
    while(verify_full(board)!=1){ 
      //vez do jogador
      if(vez==1){ 
        if(verify(board,plcolor)==0){ //vai verificar se há jogadas válidas a fazer pelo jogador. se não, passa automáticamente a jogada ao computador.
        printf("Não há jogadas válidas para a sua cor. Vez do computador\n\n");
        count_fim++;
        }
        else if(verify(board, plcolor)==1){
          printf("É a vez do jogador:");
          int flk=0;
          while(flk==0){
            printf("\n\nEscreva a sua jogada: ");
            scanf(" %d%c", &line, &col);
            flk=flanked(board,line-1,let_to_num(col),plcolor);
            if(flk==0){
            printf("\n\nJogada inválida\n\n");
            }
          }
          play(board,line-1,let_to_num(col),plcolor);
          vez=0;
          printf("\n\n");
          print_board(board);
          printf("\n\n");
					play_count++;
        }
      }
      
      //vez do computador:  
      if(vez==0){
        if(verify(board,comcolor)==1){ 
          if(dific==1){ 
            jogada_pc=computer_play_easy(board,comcolor);
          }
          else if(dific==2){
            jogada_pc=computer_play_normal(board,comcolor);
          }
          vez=1;
          print_board(board);
          printf("\n\n");
          printf("Jogada do computador:%d%c\n\n",jogada_pc.line+1, num_to_let(jogada_pc.col));
					play_count++;
        }
        else{
          vez=1;
          count_fim++;
          printf("Não há jogadas válidas para o computador.\n\n");
        }
				if(verify(board,plcolor)==0){
					printf("É a sua vez:");
				}      
      }
      if(count_fim==2){
        printf("Não há jogadas válidas para nenhuma das cores.\n\n");
        finish(board,inicio,play_count,comcolor);
        break;
      }
    }  
    if(verify_full(board)==1){
      printf("O tabuleiro está cheio. O jogo acabou.\n\n");
      finish(board,inicio,play_count,comcolor);
    }
  }
}