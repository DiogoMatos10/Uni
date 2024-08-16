import numpy as np

#Todo o algoritmo implementado supõe que os dados inseridos são numericos, como é indicado no enunciado

#classe do algoritmo KNN
class KNeighborsClassUE:

    #construtor do objeto (modelo)
    # inputs :  k = valor do número de vizinhos mais próximos a ser considerado
    #           p = descriminador da métrica de minkowski ( p=2 -> distancia euclideana)
    def __init__(self, k, p):
        self.k = k
        self.p = p

############################################################################################################################
    
    # Método que guarda os elementos do array de treino dentro do objeto
    # inputs :  X = array de arrays com os conjuntos de treino com valores de atributos 
    #           y = etiquetas para os conjuntos de valores acima descritos
     
    def fit(self, X, y):
        self.X_train = X.to_numpy()
        self.y_train = y

############################################################################################################################

    #Método que retorna a métrica de minkowski entre x1 e x2 (arrays de valores numericos provenientes dos atributos dos dados de treino)
    # input :   x1 = array de atributos numéricos
    #           x2 = " "

    def Metric(self, x1, x2):
        sum = 0
        for i in range(len(x1)):
            sum = sum + ((x1[i] - x2[i])**self.p)
        ret = sum ** (1/self.p)
        return ret

############################################################################################################################
    
    #método que vai prever as etiquetas para os conjuntos de valores em X, com base no algoritmo KNN. retorna um array.
    # input :   input = array de array com os conjuntos de valores para os quais queremos as previsões.
    
    def predict(self, input):
        values=[]                                       #array onde vão ficar etiquetas dos KNN relativamente ao input

        for n in range(len(input)):
            distances= []                               #inicialização de arrays sem valores
            closest = []
            
            labels = []
            labels = arr_copy(self.y_train)                       #para não mudar o valor ás labels iniciais de treino, é criado um novo array identico
            
            for i in range(len(self.X_train)):
                distances.append(self.Metric(input[n], self.X_train[i]))   #distância euclideana

            for s in range(self.k):                     #ciclo para averiguar os k vizinhos mais próximos e guardar as suas labels (repete k vezes)
                i = distances.index(min(distances))
                closest.append(labels[i])                   
                labels.pop(i)                           #pop para retirar o minimo dos arrays (se não iria encontrar sempre o mesmo mínimo)
                distances.pop(i)                        #retira-se também a label correspondente, para manter a correspondencia. 
            
            closest_set = set(closest)                  #set sem os duplicados do array "closest", para desempenho do código

            ret="Error"                                 #valor return default (só será retornado se houver erro)                   
            hold=0                 

            for x in closest_set:                           #encontrar o valor que mais se repete nos K vizinhos mais próximos
                cnt=closest.count(x)
                if cnt>hold:
                    ret=x
                    hold=cnt
            
            values.append(ret)

        return values

############################################################################################################################

    # método que testa (e retorna) a precisão do modelo com o conjunto de atributos X (array de arrays) e etiquetas reais y
    # inputs :  X = array de arrays com valores de atributos de teste
    #           y = array com etiquetas reais

    def score(self, X, y):

        labels = self.predict(X)                            #array onde vão ser guardadas as previsões    
        length = len(y)
        sum=0

        for n in range(length):
            if labels[n]==y[n]:                             #testa a igualdade das labels
                sum+=1                                      #se as etiquetas forem igual, incrementa o somador

        res = sum/length                                    #retorna o valor do somador dividido pelo numero de etiquetas testadas
        return res
    
############################################################################################################################

# metodo auxiliar para copiar valores de um array para outro array sem fazer referenciação, Retorna um array
# input :   arr1=array com os valroes a copiar

def arr_copy(arr1):
    arr2=[]
    for i in range(len(arr1)):
        arr2.append(arr1[i])
    return arr2