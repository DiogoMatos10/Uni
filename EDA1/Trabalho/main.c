#include <stdio.h>
#include <stdlib.h>
#include <stdbool.h>
#include <string.h>
#include "hash-linear.h"

#define GRID_SIZE 4 //Define o tamanho do tabuleiro como 4x4
#define MAX_WORD_LENGTH 8  //Define o comprimento máximo de uma palavra como 8
#define MAX_DICTIONARY_SIZE 80000  //Define o tamanho máximo do dicionário como 80000

typedef struct {
    int row;
    int col;
} Coordinates;  //Define uma struct para representar coordenadas(linha,coluna)

typedef struct {
    char grid[GRID_SIZE][GRID_SIZE];    //Matriz de caracteres que representa o tabuleiro do Boggle
    bool visited[GRID_SIZE][GRID_SIZE]; //Matriz boolean para rastrear as posições visitadas no tabuleiro
} BoggleBoard;  //Define uma struct para representar o tabuleiro do Boggle

typedef struct {
    char word[MAX_WORD_LENGTH]; //Armazena a palavra encontrada
    Coordinates path[MAX_WORD_LENGTH];  //Armazena o caminho percorrido para formar a palavra no tabuleiro
    int length; //Armazena o comprimento da palavra
} WordResult;   //Define uma struct para representar a palavra formada no tabuleiro


//Função para ler o ficheiro do Boggle (board) e armazenar no formato de array bi-dimensional
void loadBoggleBoard(const char* fileName, BoggleBoard* board) {
    FILE* file = fopen(fileName, "r");  //Abre o ficheiro no modo de leitura
    if (file == NULL) { //Verifica se houve falha a abrir o ficheiro
        printf("Erro a abrir o ficheiro.\n");
        exit(1);
    }

    //Loop responsável por percorrer as linhas e colunas do tabuleiro do Boggle
    for (int row = 0; row < GRID_SIZE; row++) {
        for (int col = 0; col < GRID_SIZE; col++) {
            fscanf(file, " %c", &board->grid[row][col]);    //  Lê o caracter do ficheiro e armazena na posição correta da matriz do tabuleiro
            board->visited[row][col] = false;   //Inicializa essa posição da matriz como não visitada
        }
    }

    fclose(file);   //Fecha o ficheiro após a leitura
}

//Função para ler o ficheiro do dicionário e armazenar as palavras e os seus prefixos em HashTables, na HashTable de palavras completas e na HashTable de prefixos mais concretamente
void loadDictionary(const char* fileName, HashTable wordTable, HashTable prefixTable) {
    FILE* file = fopen(fileName, "r");  //Abre o ficheiro no modo de leitura
    if (file == NULL) { //Verifica se houve falha a abrir o ficheiro
        printf("Failed to open file: %s\n", fileName);
        exit(1);
    }

    char word[MAX_WORD_LENGTH]; //Variável para armazenar temporariamente cada palavra do ficheiro

    while (fscanf(file, " %s", word) != EOF) {  //Loop para ler cada palavra do ficheiro até ao final deste
        char read_word[MAX_WORD_LENGTH];
        strncpy(read_word,word, MAX_WORD_LENGTH-1); //Copia a palavra lida para uma variável de leitura temporária
        read_word[MAX_WORD_LENGTH-1]='\0';  //Garante que a string de leitura termine em caracter nulo '\0'
        Insert(read_word,wordTable);   //Insere a palavra completa na HashTable das palavras completas

        for (int i = 1; i <= strlen(read_word); i++) {  //Loop para gerar prefixos da palavra completa e inseri-los na HashTable de prefixos
            if(i<=MAX_WORD_LENGTH){ //Verifica se o comprimento do prefixo é válido
                char prefix[MAX_WORD_LENGTH];
                strncpy(prefix, read_word, i);  //Copia uma parte da palavra como prefixo
                prefix[i] = '\0';   //Garante que a string do prefixo termine em caractert nulo '\0'
                Insert(prefix,prefixTable);    //Insere o prefixo na HashTable de prefixos
            }
        }
    }
    fclose(file);   //Fecha o ficheiro após a leitura
}

//Função responsável por encontrar palavras completas no tabuleiro do Boggle a partir de uma posição especifica
void findWords(BoggleBoard* board, HashTable wordTable, HashTable prefixTable, int row, int col, WordResult* wordResult, int *count_word) {
    if (!(row >= 0 && row < GRID_SIZE && col >= 0 && col < GRID_SIZE) || board->visited[row][col])
        return; //Se a posição atual estiver fora do tabuleiro ou já tiver sido visitada, dá return

    board->visited[row][col] = true;    //Marca a posição atual como visitada

    int wordIndex = wordResult->length;
    wordResult->word[wordIndex] = board->grid[row][col];    //Adiciona a letra da posição atual à palavra em construção
    wordResult->path[wordIndex].row = row;  //Guarda a linha da posição atual no caminho percorrido
    wordResult->path[wordIndex].col = col;  //Guarda a coluna da posição atual no caminho percorrido
    wordResult->length++;   //Incrementa o tamanho

    wordResult->word[wordResult->length] = '\0';    //Garante que a palavra termine em caracter nulo '\0'

    //Condição que verifica se a palavra formada existe nas HashTables utilizando a função Find das HashTables (esta função que foi criada exclusivamante para verificar esta condição)
    if (Find(wordResult->word,wordTable) && Find(wordResult->word,prefixTable)) {
        (*count_word)++;    //Incrementa o contador de palavras encontradas
        printf("%s ",wordResult->word); //Print à palavra encontrada
        printf(" %c:(%d,%d)",wordResult->word[0], wordResult->path[0].row, wordResult->path[0].col);    //Print à primeira letra da palavra encontrada e a sua posição
        for (int j = 1; j < wordResult->length; j++) {
            printf("->%c:(%d,%d)",wordResult->word[j], wordResult->path[j].row, wordResult->path[j].col);   //Dá print de cada letra da palavra encontrada e a sua posição
        }
        printf("\n");
    }


    if (Find(wordResult->word,prefixTable)) {
        for (int dRow = -1; dRow <= 1; dRow++) {
            for (int dCol = -1; dCol <= 1; dCol++) {
                int newRow = row + dRow;
                int newCol = col + dCol;
                findWords(board, wordTable, prefixTable, newRow, newCol, wordResult, count_word);   //Chama recursivamente a função para as posições vizinhas
            }
        }
    }

    board->visited[row][col] = false;
    wordResult->length--;
}

//Função para resolver o Boggle a partir dos ficheiros de entrada
void solveBoggle(const char* boggleFile, const char* dictionaryFile) {
    BoggleBoard board;
    loadBoggleBoard(boggleFile, &board);    //Carrega o tabuleiro do ficheiro

    HashTable wordTable= InitializeTable(MAX_DICTIONARY_SIZE); //Inicializa a HashTable para as palavras completas
    HashTable prefixTable = InitializeTable(MAX_DICTIONARY_SIZE);  //Inicializa a HashTable para os prefixos

    loadDictionary(dictionaryFile, wordTable, prefixTable);   //Carrega o dicionário para as HashTables

    WordResult wordResult;
    memset(&wordResult, 0, sizeof(WordResult)); //Inicializa a struct da palavra resultante com zeros

    int count_word=0;   //Variável para contar o número de palavras encontradas

    //Loop que percorre cada caracter do boggle e verifica as palavras existentes a partir de cada caracter (posição do tabuleiro)
    for (int row = 0; row < GRID_SIZE; row++) {
        for (int col = 0; col < GRID_SIZE; col++) {
            findWords(&board, wordTable, prefixTable, row, col, &wordResult,&count_word);
        }
    }

    printf("\nNúmero total de palavras encontradas: %d\n", count_word); //Print ao numerom total de palavras encontradas no Boggle

    DestroyTable(wordTable); //Liberta a memória da HashTable das palavras completas
    DestroyTable(prefixTable);   //Liberta a memória da HashTable dos prefixos
}

int main() {
    const char* boggleFile = "boggle0.txt"; //Ficheiro de entrada com o tabuleiro Boggle
    const char* dictionaryFile = "corncob_caps_2023.txt";   //Ficheiro de entrada com o dicionário

    solveBoggle(boggleFile, dictionaryFile);    //Chamada da função para resolver o Boggle

    return 0;
}
