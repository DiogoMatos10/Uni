typedef int ElementType;

#ifndef _Queue_h
#define _Queue_h

struct QueueRecord;
typedef struct QueueRecord *Queue;

Queue CreateQueue( int MaxElements );
void DisposeQueue( Queue Q );

int IsEmptyQueue( Queue Q );
int IsFullQueue( Queue Q );
void MakeEmptyQueue( Queue Q );

ElementType Front( Queue Q );
void Enqueue( ElementType X, Queue Q );
ElementType Dequeue( Queue Q );
void display(Queue Q);
int size(Queue Q);
int isInQueue(Queue Q, int X);
void inicialize(Queue Q);
#endif
