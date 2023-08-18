#include "queue.h"
#include "fatal.h"
#include <stdio.h>
#include <stdlib.h>

// nomeclaturas dos estados
#define new 0   // new
#define rdy 1   // ready
#define run 2   // running
#define blk 3   // blocked
#define ext 4   // exit
#define unblk 5 //unblock
#define nos -1  // no state
#define bnos -2 // antes de entrar

//Função que soma todos os instantes de todos os programas
int soma_instantes(int num_programas, int max_mudancas, int programas[num_programas][max_mudancas])
{
    int res = 0;
    for (int i = 0; i < num_programas; i++)
    {
        for (int j = 0; j < max_mudancas; j++)
        {
            res += programas[i][j];
        }
    }
    return res;
}

//Função responsável pelo output dos estados de cada programa por instante
void output(int instante, int num_programas, int state[num_programas])
{
    if (instante < 10)
    {
        printf("    %d    |", instante);
    }
    else
    {
        printf("    %d   |", instante);
    }
    for (int i = 0; i < num_programas; i++)
    {
        if (state[i] == nos)
        {
            printf("       |");
        }
        else if (state[i] == new)
        {
            printf("  new  |");
        }
        else if (state[i] == rdy)
        {
            printf(" ready |");
        }
        else if (state[i] == run)
        {
            printf("  run  |");
        }
        else if (state[i] == blk)
        {
            printf(" block |");
        }
        else if (state[i] == ext)
        {
            printf("  exit |");
        }
        else if (state[i] == bnos)
        {
            printf("       |");
        }
        else
        {
            printf("  err  |");
        }
    }
}

//Função responsável 
int all_exit(int state[], int num_programas)
{
    for (int i = 0; i < num_programas; i++)
    {
        if (state[i] != nos)
        {
            return 0;
        }
    }
    return 1;
}

int main()
{
    int num_programas, max_mudancas;
        FILE *file = fopen("proc.txt", "r");
        if(file==NULL){
            printf("ERRO AO ABRIR O FICHEIRO");
            exit(1);
        }
        fscanf(file, "%d %d", &num_programas, &max_mudancas);
        int programas[num_programas][max_mudancas];
        for (int i=0;i<num_programas;i++) {
            for(int j=0;j<max_mudancas;j++) {
                fscanf(file, "%d ", &programas[i][j]);
            }
        } 

    Queue ready = CreateQueue(40);   // queue do estado ready
    int instante = 0;                // marca o instante presente
    int iterator[num_programas];     // iterador que correspnde ao local do array de programas (por programa) que estamos a utilizar
    int state[num_programas];        // guarda os states dos programas para poderem ser mostrados no output
    int blocked_time[num_programas]; // por cada indíce, guarda o numero de instantes pelo que o respetivo programa tem de esperar no blocked
    int running_time;               
    int is_running = -1;             // -1 se não está nenhum processo no run e os outros diferentes valores serão o número do processo que está a correr

    // inicializador do state
    for (int i = 0; i < num_programas; i++)
    {
        state[i] = bnos;
        iterator[i] = 0;
    }

    // primeira linha de output
    printf("\nInstante | ");
    for (int i = 0; i < num_programas; i++)
    {
        printf("Proc%d | ", i);
    }
    printf("\n");

    // inicializar os processos que são new no instante zero
    for (int i = 0; i < num_programas; i++)
    {
        if (programas[i][0] == instante)
        {
            state[i] = 0;
        }
    }

    while (instante < soma_instantes(num_programas, max_mudancas, programas) && !all_exit(state, num_programas))
    {

        
        //guarda o processo a ser desbloqueado
        int to_unblock=-1;

        // output dos estados
        output(instante, num_programas, state);
        

        for (int i = 0; i < num_programas; i++)
        {
            if (state[i] == ext)
            {
                state[i] = nos;
            }
        }

        // run->exit
        if (is_running != -1 && running_time == 0 )
        {
            if ( programas[is_running][iterator[is_running]+2]==0 && programas[is_running][iterator[is_running]+3]==0)
            {
                state[is_running] = ext;    
                is_running = -1;
            }
        }

        // run->blocked
        if (is_running != -1 && running_time == 0 && programas[is_running][iterator[is_running]] != 0)
        {
            state[is_running] = blk;
            iterator[is_running]++;
            if(state[iterator[is_running]]!=-1){
                to_unblock=programas[is_running][iterator[is_running]]-1;
            }
            iterator[is_running]++;
            blocked_time[is_running] = programas[is_running][iterator[is_running]];
            is_running = -1;
        }

        // blocked->ready
        for (int proc = 0; proc < num_programas; proc++)
        {
            if (state[proc] == blk && blocked_time[proc] == 0)
            {
                Enqueue(proc, ready);
                iterator[proc]++;
                state[proc] = rdy;
            }

        }

        // new->ready
        for (int proc = 0; proc < num_programas; proc++)
        {
            if (state[proc] == new && !isInQueue(ready, proc))
            {
                Enqueue(proc, ready);
                iterator[proc]++;
                state[proc] = rdy;
            }
        }

        // ready->run
        if (!IsEmptyQueue(ready) && is_running == -1)
        {
            int proc = Dequeue(ready);
            is_running = proc;
            running_time = programas[proc][iterator[proc]];
            state[proc] = run;
        }

        // new
        for (int i = 0; i < num_programas; i++)
        {
            if (iterator[i] == 0 && programas[i][0] == instante + 1)
            {
                state[i] = new;
            }
        }

        //unblock process
        if(to_unblock>=0 && state[to_unblock]==blk){   
            state[to_unblock]=rdy;
            iterator[to_unblock]++; 
            Enqueue(to_unblock, ready);
            printf(" process %d is unblocked", to_unblock);
        }

        if (running_time > 0)
        {
            running_time--;
        }

        for (int i = 0; i < num_programas; i++)
        {
            if (blocked_time[i] > 0)
            {
                blocked_time[i]--;
            }
        }

        instante++;
        
        printf("\n");
    }
    printf("\n");

    return 0;
}

/* 

Testes

3 10
0 3 -1 2 2 3 4 2 2 0
1 4 1 4 5 1 4 3 3 0
2 2 2 6 2 -1 4 2 0 0

8 11
0 2 2 4 5 1 1 1 -1 0 0
0 1 -1 2 2 -1 3 1 -1 0 0
1 2 -1 1 6 -1 4 1 -1 0 0
2 2 2 1 5 2 4 1 -1 0 0
2 3 3 1 2 1 1 1 -1 0 0
3 4 4 2 7 4 2 1 -1 0 0
4 4 4 2 4 3 4 1 -1 0 0
5 5 3 6 5 1 3 1 -1 0 0
*/