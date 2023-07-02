#include "fatal.h"
#include "hash-linear.h"
#include <stdlib.h>
#include <string.h>

#define MinTableSize (10)


/*Return next prime; assume N>=10*/
static int NextPrime( int N )
{
    int i;

    for(i=N+1;;i++){
        int j;
        for (j=2;j*j<=i;j++) {
            if(i%j==0)
                break;
        }
        if(j*j>i)
            return i;
    }
}

/*Função Hash para strings*/
Index Hash( const char *Key, int TableSize )
{
    unsigned int HashVal = 0;

     while( *Key != '\0' )
        HashVal = ( HashVal << 5 ) + *Key++;

     return HashVal % TableSize;
}


HashTable InitializeTable( int TableSize )
{
    HashTable H;
    int i;

      if( TableSize < MinTableSize )
    {
          Error( "Table size too small" );
          return NULL;
    }


      H = malloc( sizeof( struct HashTbl ) );
     if( H == NULL )
         FatalError( "Out of space!!!" );

      H->TableSize = NextPrime( TableSize );


     H->TheCells = malloc( sizeof( Cell ) * H->TableSize );
     if( H->TheCells == NULL )
         FatalError( "Out of space!!!" );

     for( i = 0; i < H->TableSize; i++ )
         H->TheCells[ i ].Info = Empty;

     return H;
}


Position ProcPos( ElementType Key, HashTable H )
{
    Position CurrentPos;
    CurrentPos = Hash( Key, H->TableSize );

    while( H->TheCells[ CurrentPos ].Info != Empty && strcmp(H->TheCells[CurrentPos].Element, Key) != 0){
        CurrentPos++;
        if( CurrentPos >= H->TableSize ) {
            CurrentPos -= H->TableSize;
        }
    }
     return CurrentPos;
}


bool Find(ElementType Key, HashTable H){
    Position x=ProcPos(Key,H);
    if(H->TheCells[x].Info==Legitimate){
        return true;
    }
    return false;
}

void Insert( ElementType Key, HashTable H )
{
    Position Pos;

    Pos = ProcPos( Key, H );
    if( H->TheCells[ Pos ].Info != Legitimate )
    {
        H->TheCells[Pos].Element= malloc(strlen(Key)+1);
        strcpy(H->TheCells[Pos].Element,Key);
        H->TheCells[Pos].Info=Legitimate;
    }
}

HashTable Rehash( HashTable H )
{
    int i, OldSize;
    Cell *OldCells;

    OldCells = H->TheCells;
    OldSize  = H->TableSize;



    H = InitializeTable( 2 * OldSize );


    for( i = 0; i < OldSize; i++ )
        if( OldCells[ i ].Info == Legitimate )
            Insert( OldCells[ i ].Element, H );

    for(i=0;i<OldSize;i++){
        if(OldCells[i].Info==Legitimate)
            free(OldCells[i].Element);
    }
    free( OldCells );

    return H;
}

ElementType Retrieve( Position P, HashTable H )
{
    return H->TheCells[ P ].Element;
}

void DestroyTable( HashTable H )
{
    int i;
    for(i=0;i<H->TableSize;i++)
        if(H->TheCells[i].Info==Legitimate)
            free(H->TheCells[i].Element);
    free( H->TheCells );
    free( H );
}

void PrintTable(HashTable H) {
    int i;
    for (i = 0; i < H->TableSize; i++) {
        if (H->TheCells[i].Info == Legitimate)
            printf("Position: %d, Element: %s", i, H->TheCells[i].Element);
    }
}
