#include <stdbool.h>
typedef char* ElementType;

#ifndef _HashLin_H
#define _HashLin_H

typedef int Index;
typedef Index Position;

enum KindOfEntry{Legitimate, Empty, Deleted};

typedef struct HashEntry{
    ElementType Element;
    enum KindOfEntry Info;
}Cell;

struct HashTbl{
    int TableSize;
    Cell* TheCells;
};

typedef struct HashTbl *HashTable;

HashTable InitializeTable( int TableSize );
void DestroyTable( HashTable H );
Position ProcPos( ElementType Key, HashTable H );
void Insert( ElementType Key, HashTable H );
ElementType Retrieve( Position P, HashTable H );
HashTable Rehash( HashTable H );
void PrintTable(HashTable H);
bool Find(ElementType Key, HashTable H);

#endif 


