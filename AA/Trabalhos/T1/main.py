from KNeighborsClassUE import KNeighborsClassUE
from NBayesClassUE import NBayesClassUE
import pandas

#################################################   TESTE DO KNN    ##########################################################

#variável que contém a localização (PATH) do ficheiro CSV a utilizar
CSV = r"C:\Users\Henrique\Desktop\uni\AA\trabalho\dados\iris.csv"

#o código assum que todas as linhas presentes no ficheiro CSV sejam válidas para aplicar os algoritmos implementados

#colunas do ficheiro a utilizar para os valores dos atributos:
x1 = 0                  #inicio 
x2 = 4                  #fim 

#coluna a utilizar para os valores das classes
y1=4

#definir os parametros para o treino a partir da dataframe obtida do ficheiro CSV | X = atributos, y=classes
df = pandas.read_csv(CSV)
X = df.iloc[:,x1:x2]
y = df.iloc[:,y1]

#cria o objeto do modelo
model = KNeighborsClassUE()

#treino do modelo
model.fit(X,y)

teste = [[5,3.6,1.4,0.2],[5.5,2.4,3.7,1],[5,4,3,1]] #atributos de teste do modelo para o algoritmo KNN:
etiquetas = ["Iris-setosa","Iris-setosa","errado"]  #etiquetas para teste do método score (uma delas está errada, obviamente, para testar)

#print
print("--------------   TESTE KNN   --------------\n")
print("Etiquetas reais:     ",etiquetas)
print("Etiquetas previstas: ",model.predict(teste))
print("Score:               ",model.score(teste,etiquetas),"\n")


##################################################  TESTE DO NBAYES ############################################################

CSV = r"C:\Users\Henrique\Desktop\uni\AA\trabalho\dados\weather-nominal.csv"

#o código assume que todas as linhas presentes no ficheiro CSV sejam válidas para aplicar os algoritmos implementados

#colunas do ficheiro a utilizar para os valores dos atributos:
x1 = 0                  #inicio 
x2 = 4                  #fim 

#coluna a utilizar para os valores das classes
y1=4

#definir os parametros para o treino a partir da dataframe obtida do ficheiro CSV | X = atributos, y=classes
df = pandas.read_csv(CSV)
X = df.iloc[:,x1:x2]
y = df.iloc[:,y1]

model = NBayesClassUE()
model.fit(X,y)
     
#IMPORTANTE: notar que, os valores do ficheiro CSV, quando convertidos para array numpy para
#poderem ser lidos e indexados pelo código, todas as células onde o valor "FALSE" esteja presente 
#não vai ser convertido para string,mas sim para o valor booleano "False", por favor manter em mente ao testar! 

omega = [["sunny","hot","high",False], ["sunny","hot","high",False], ["sunny","hot","high",True]]
sigma = ["no", "yes" , "no"]

#testes      
print("--------------  TESTE NBAYES --------------\n")
print("etiquetas reais:   ", sigma )
print("etiqueta prevista: ", model.predict(omega))
print("score:             ", model.score(omega,sigma), "\n") 