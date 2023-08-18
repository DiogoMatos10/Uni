#include <stdlib.h>
#include "queue.h"
#include "fatal.h"


#define MinQueueSize ( 0 )

struct QueueRecord{
    int Capacity;
    int Front;
    int Rear;
    ElementType *Array;
};

int size( Queue Q){
    return Q->Rear - Q->Front ;
}

/* indice do proximo elemento  */
int successor( int i, Queue Q ){
    return (i+1)%Q->Capacity;
}

void inicialize(Queue Q){
    for(int i=0; i<=Q->Capacity; i++){
        Q->Array[i]=9;
    }
}

Queue CreateQueue( int MaxElements ){
    Queue Q;

    if( MaxElements < MinQueueSize )
        Error( "Queue size is too small" );

    Q = malloc( sizeof( struct QueueRecord ) );
    if( Q == NULL )
        FatalError( "Out of space!!!" );

    Q->Array = malloc( sizeof( ElementType ) * MaxElements );
    if( Q->Array == NULL )
        FatalError( "Out of space!!!" );

    Q->Capacity = MaxElements+1;
    MakeEmptyQueue( Q );
    inicialize(Q);

    return Q;
}


void DisposeQueue( Queue Q ){
    if( Q != NULL ){
        free( Q->Array );
        free( Q );
    }
}


int IsEmptyQueue( Queue Q ){
    return Q->Front==Q->Rear;
}


int IsFullQueue( Queue Q ){
    return size(Q)==Q->Capacity;
}


void MakeEmptyQueue( Queue Q ){
    Q->Front=0;
    Q->Rear=0;
    
}


void Enqueue( ElementType X, Queue Q ){
   if( IsFullQueue(Q) )
        Error( "Full queue" );
    else{
        Q->Rear = successor(Q->Rear, Q);
        Q->Array[Q->Rear-1] = X;
    }
}


ElementType Front( Queue Q ){
    if(IsEmptyQueue(Q))
        Error("Empty Queue");
    else
        return Q->Array[Q->Front];
}


ElementType Dequeue( Queue Q ){
    ElementType X;
    if(IsEmptyQueue(Q))
        Error("Empty queue");
    else{
        X=Q->Array[Q->Front];
        Q->Front=successor(Q->Front, Q);
    }
    return X;
}

void display(Queue Q){
    for(int i = Q->Front; i<=Q->Capacity; i++){
        if(!(Q->Array[i]==9)){ 
            printf("%d", Q->Array[i]);
        }
    }
}

int isInQueue(Queue Q, int X){
    for(int i=0; i<size(Q); i++){
        if(Q->Array[i]==X){
            return 1;
        }
    }
    return 0;
}