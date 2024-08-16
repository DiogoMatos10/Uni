from sklearn.naive_bayes import CategoricalNB
import numpy as np

class NBayesClassUE:
    
    # construtor do objeto (modelo)
    # inputs :  alpha=valor decimal de alfa para o estimador de lindstone (alfa = 1 -> estimador de laplace)

    def __init__(self, alpha):
        self.alpha = alpha

############################################################################################################################

    # Método que guarda os elementos dos arrays de treino dentro do objeto
    # inputs :   X = array que contem os valores de atributos de treino
    #            y = etiquetas de treino para os valores de X 
    #            é necessário que X e y tenham o mesmo numero de linhas (tantos conjuntos de atributos quanto etiquetas) 

    def fit(self, X, y):
        if not (len(X) == len(y)):                              #Verificação da igualidade das dimensões dos inputs    
            print("ERRO (NBAYES-FIT) : Os inputs não têm a mesma dimensão.")
            return np.NAN                               

        self.X_train = X.to_numpy()
        self.y_train = y

############################################################################################################################

    #método que prevê e retorna a classe de um conjunto de valores de atributos, com base no funcionamento do método de naive bayes dado nas aulas teóricas
    # input :   X = array que contêm um conjunto de valores de atributos, para o qual vai ser previsto a etiqueta
    #           é necessário que X tenha o mesmo numero de valore que cada linha de X_train (valores de atributo de treino)

    def predict(self, X):
        ret_arr = []                               #array de retorno, que vai conter as etiquetas previstas

        for X_line in X:                            #repete por cada conjunto de valores do input
            
            final_probs=[]                          #probabilidades finais, em relação ás classes que existem
            dif_classes_set = set(self.y_train)     #conjunto das classes que existem, sem duplicados
            dif_classes = []                        #array que vai conter os valores do conjunto acima (sets não são indexáveis em python)
            
            for x in dif_classes_set:               #população do array
                dif_classes.append(x)
            
            totals = []                             #array onde vão ser guardadas as probabilidades simples das classes
            probs_per_class=[]                      #array que vai guardar as probabilidades a multiplicar no fim, por classe (array de arrays)

            probs_ini=[]                             #array que vai guardar as probabilidades iniciais  

            for cl in range(len(dif_classes)):       #repete por cada classe que existe
                
                #calcular os valores para inserir na variavel "totals" no estimador e as probabilidades simples das classes#
                sum = 0
                for j in range(len(self.y_train)):              #repete para todas as linhas do array de etiquetas de treino
                    if self.y_train[j] == dif_classes[cl]: 
                        sum+=1
                totals.append(sum)                              #totals para usar no estimador
                probs_ini.append(sum/len(self.y_train))         #probabilidades simples das classes (por ex: P(yes))
                

            for i in range(len(dif_classes)):                   #vai pôr arrays com o valor da probabilidade simples da classe
                arr = []                                        #array vazio
                arr.append(probs_ini[i])                        #coloca o valor da probablididade simples da classe no array vazio
                probs_per_class.append(arr)                     #coloca o array na probs_per_class
                        
            
            ######## probabilidade composta atributo/classe, usando estimador de lindstone (por ex: P(sunny/yes) )#############
                                                
            for cl in range(len(dif_classes)):              #repete por cada classe que existe
                
                for x_pos in range(len(X_line)):                 #repete para todos os elementos de X
                    
                    ###### nvals e nx ######
                    nval_=[]                                #array auxiliar que vai guardar todos os valores presentes na posição x_pos de todas as linhas de X.train
                    nx=0                                    #contador para determinar nx
                    for i in range(len(self.X_train)):      #repete por quantas linhas houver em X.train
                        aux = self.X_train[i]               #array auxiliar, guarda o array presente na linha de X.train
                        nval_.append(aux[x_pos])            #guarda o valor do atributo x_pos dessa linha

                        if aux[x_pos]==X_line[x_pos] and self.y_train[i] == dif_classes[cl]:  #se o valor do atributo for igual e a classe for igual também, incrementa nx
                            nx+=1
                        
                    nvals=len(set(nval_))                   #nvals vai ser a contagem de valores diferentes que há em nval_

                    total = totals[cl]                      #total da classe
                    
                    prob=self.lidstone(nx,total, nvals)     #calcular a probabilidade composta com o estimador de lidstone
                    probs_per_class[cl].append(prob)        #insere a porbabilidade composta no array das probabilidades por classe, na posição respectiva á classe
        
            #calcular o valor das probabilidase por classe (multiplicação)
            for cl in range(len(dif_classes)):              #repete por cada classe que existe
                value=1                                     #elemento neutro da mulltiplicação
                for x in probs_per_class[cl]:               #repete por cada elemento do array de probabilidades respetivo á classe atula
                    value = value*x                                
                final_probs.append(value)                   #Põe o valor no array de saida

            #retornar a classe que tem a maior probabilidade
            for cl in range(len(dif_classes)):              #acha o máximo e adiciona a classe desse máximo ao array de retorno
                if final_probs[cl] == max(final_probs):     
                    ret_arr.append(dif_classes[cl])

        return ret_arr
           
############################################################################################################################

    #método que retorna o valor da Probabilidade estmiada pelo estimador de lidstone, com o alfa já indicado na criação do modelo
    #inputs:    inteiros nx, total, e nvals, necessários para a fórmula do estimador, como indicado no enunciado

    def lidstone(self, nx, total, nvals):
        alpha = self.alpha
        return ((nx+alpha)/(total+(alpha*nvals)))

############################################################################################################################

    # método que retorna o valor decimal da porcentagem de etiquetascorretamente previstas.
    # inputs :   X = array de arrays, cada array tem um conjunto de valores(dos atributos) a testar
    #            y= etiquetas corretas para comparar ás etiquetas previstas
    #            é necessário que X e y tenham o mesmo numero de linhas (tantos conjuntos de atributos quanto etiquetas reais)

    def score(self, X, y):

        if not (len(X) == len(y)):                                              #Verificação da igualidade das dimensões dos inputs,    
            print("ERRO (NBAYES-SCORE) : Os inputs não têm a mesma dimensão.")  #caso não sejam iguais dá mensagem de erro e retorna NAN
            return np.NAN                               

        values = self.predict(X)
        sum = 0                                 
        for i in range(len(values)):                                 #repete por cada valor de y     
            if(values[i] == y[i]):                                  #caso a etiqueta esteja correta, incrementa o somador
                sum+=1
        return (sum/len(y))                                     #retorna o numero de etiquetas previstas corretamente dividido pelo numero de previsões feitas