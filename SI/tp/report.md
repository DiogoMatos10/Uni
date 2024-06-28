# Relatório do Trabalho Prático de Segurança Informática

**Trabalho realizado por:**
- Henrique Rosa, nº 51923
- Diogo Matos, nº 54466

## Introdução

O presente trabalho prático foi desenvolvido com base no tema abordado no trabalho teórico sobre o ransomware WannaCry. 
Este vírus de computador tornou-se notório por encriptar os ficheiros dos utilizadores e exigir o pagamento de resgate para a sua desencriptação. 
Com base nesse contexto, o objetivo deste trabalho prático foi simular a atuação do WannaCry, encripta os ficheiros de uma pasta específica no computador do utilizador e fornecendo uma interface para realizar o pagamento ao atacante. 
Caso o pagamento fosse realizado, a chave de desencriptação seria fornecida para permitir a restauração dos ficheiros.

Este relatório documenta o desenvolvimento do projeto, descrevendo a implementação e as tecnologias utilizadas.
Além disso, são discutidos os desafios encontrados e as lições aprendidas ao longo do desenvolvimento. 

## Implementação e Tecnologias Utilizadas 

Para o desenvolvimento deste trabalho, utilizamos conhecimentos adquiridos na unidade curricular de Redes e Computadores, explorando a linguagem de programação Java para implementar o `Client` (vitima) e o `Server` (atacante).

<br>

### Client

A classe `Client`, representa a vítima, é responsável por encriptar uma pasta específica no seu sistema e enviar a chave de encriptação para o `Server` (atacante). Utilizamos as seguintes classes Java para implementar:
- `Cipher`, `DirectoryStream` e `SecretKey`: Para a encripta AES dos arquivos na pasta;
- `Socket`, `BufferedReader` e `PrintWritter`: Para comunicação com o server.

#### Método Main

Método responsável por coordenar as principais operações do cliente, como encriptar a pasta, enviar a chave de encriptação e interagir com o utilizador para simular o pagamento e desencriptar a pasta.
Com um pequeno pormenor, caso a pasta já esteja encriptada, a pasta não será encriptada e uma nova chave não irá ser enviada para o `server`.

1. **Encripta a Pasta**: Chama o método `encryptFolder` para encriptar os ficheiros na pasta especificada;
2. **Envia a Chave de Encriptação**: Chama o método `sendEncryptionKey` para enviar a chave de encriptação para o `server`;
3. **Interage com o Utilizador**: Mostra opções para o utilizador, como pagar para desencriptar a pasta ou sair do programa.


#### Método encryptFolder

Método responsável por encriptar todos os ficheiros na pasta especificada. 


1. **Gera a Chave de Encriptação**: Utiliza o método `generateSecretKey` para gerar uma chave AES.
2. **Encripta Ficheiros**: Utiliza a chave gerada para encriptar todos os ficheiros na pasta especificada usando a classe `Cipher`. 
A iteração sobre os ficheiros é feita utilizando `DirectoryStream` para uma abordagem mais simples e direta.


#### Método isFolderEncrypted

Método responsável por verificar se a pasta já está encriptada.

1. **Leitura dos Ficheiros**: Utiliza `DirectoryStream` para iterar sobre os ficheiros na pasta.
2. **Verificação dos Ficheiros**: Para cada ficheiro, verifica se é um ficheiro regular e lê os seus bytes.
3. **Validação dos Bytes**: Verifica se os primeiros bytes do ficheiro são legíveis ou correspondem a um padrão esperado.
4. **Retorno**: Retorna `true` se a pasta estiver encriptada, ou `false` caso contrário.

#### Método isLegit

Método responsável por verificar se os bytes de um ficheiro provavelmente estão encriptados.

1. **Iteração sobre os Bytes**: Percorre todos os bytes do ficheiro recebido como parâmetro.
2. **Verificação de Legitimidade**: Verifica se cada byte não é um caracter ASCII legível, ou seja, se estiver fora do intervalo 32-126.
3. **Retorno**: Retorna `true` se os bytes provavelmente estiverem encriptados, ou `false` caso contrário.


#### Método sendEncryptKey

Método responsável por enviar a chave de encriptação para o `Server` usando sockets. 

1. **Estabelece Conexão**: Cria um socket e estabelece uma conexão com o `Server`.
2. **Envia a Chave de Encriptação**: Utiliza um `PrintWriter` para enviar a chave de encriptação para o `Server`.



#### Método simulatePaymentAndDecrypt

Método responsável por simular o pagamento e desencriptar a pasta.

1. **Solicita a Chave de Desencriptação**: Utiliza o método `requestDecryptionKey` para solicitar a chave de desencriptação ao `server`.
2. **Desencripta a Pasta**: Utiliza a chave recebida para desencriptar os ficheiros na pasta, chamando o método `decryptFolder`.



#### Método requestDecryptionKey

Método responsável por solicitar a chave de desencriptação ao `Server`.

1. **Estabelece Conexão**: Cria um socket e estabelece uma conexão com o `Server`.
2. **Solicita a Chave**: Envia um pedido de pagamento ao `Server` e recebe a chave de desencriptação.



#### Método decryptFolder

Método responsável por desencriptar todos os ficheiros na pasta especificada.

1. **Inicializa o Cipher**: Configura o `Cipher` para o modo de desencriptação usando a chave fornecida.
2. **Desencripta Ficheiros**: Percorre todos os ficheiros na pasta utilizando `DirectoryStream` e desencripta-os utilizando a chave de desencriptação.



#### Método generateSecretKey

Método responsável por gerar uma chave de encriptação AES.

1. **Gera a Chave AES**: Utiliza o `KeyGenerator` para criar uma nova chave AES de 128 bits.


<br>

### Server

A classe `Server` representa o atacante, responsável por receber a chave de encriptação do `Client` e enviar a chave de desencriptação quando solicitado.



#### Método Main

Método responsável por coordenar as principais operações do `Server`, como receber a chave de encriptação e esperar pelo pagamento para enviar a chave de desencriptação.

1. **Aguarda Conexões**: O método principal (main) aguarda por conexões de vitimas. 
Quando uma vitima se conecta, cria uma nova instância de ClientHandler para lidar com as operações.



#### Classe ClientHandler
Esta classe lida com as operações de cada cliente/vítima.


##### Método Run

Método responsável por esperar pelo pedido de pagamento do `Client` e enviar a chave de desencriptação. Método responsável por executar as operações para cada client conectado.

1. **Lê o Comando do Client**: Lê o ID e o comando enviado.
2. **Processa o Comando**: Se o comando for "ENCRYPT", o servidor lê a chave de encriptação e a armazena associada ao IP. 
Se for "PAGAR", o servidor envia a chave de desencriptação associada ao IP.


## Instruções de Utilização

1. **Iniciar a class `Server`**: Deve alterar a variável port para aquilo que pretende;

2. **Alterar os valores das variáveis da class `Client`**:
    - Deve alterar a variável port e `SERVER_PORT` e `SERVER_IP` para os valores corretos do server;
    - Deve alterar a variável `FOLDER_PATH` para o path da pasta que pretende encriptar.
3. **Iniciar a class `Client`**:
    - O `Client` apresentará opções para o utilizador.
    - Para desencriptar os ficheiros, o utilizador deverá selecionar a opção de pagamento, caso selecione sair os ficheiros mantém se encriptados.

4. **Desencriptação de Ficheiros**:
    - Após simular o pagamento, o `Client` solicitará a chave de desencriptação ao `Server`, e este envia a chave de encriptação.
    - Os ficheiros serão desencriptados e restaurados à sua forma original.

## Conclusão e Desafios encontrados

Como já foi mencionado, apesar de termos conhecimento sobre a comunicação entre um cliente e um servidor em Java, obtido na unidade curricular de Redes de Computadores, tivemos de pesquisar sobre encriptação nesta linguagem de programação, enfrentando várias dificuldades durante a sua implementação. 
Temos consciência de que esta é uma simulação bastante simples, mas eficaz, que demonstra um pouco do funcionamento do vírus WannaCry.

Tínhamos ainda a intenção de criar um executável para o Windows utilizando um ficheiro .jar do `Client`, onde a "vítima" abriria o executável e seria atacada. 
Pesquisámos métodos para o fazer, mas sem sucesso, pois aparecia sempre o aviso "This application requires a Java Runtime Environment". 
Investigámos bastante sobre como resolver este problema, testando várias versões de Java e em diversos sistemas operativos, mas sem êxito.

Foi bastante gratificante realizar este trabalho, principalmente por nos recordar os conteúdos de Redes de Computadores e Sistemas Distribuídos, abrangendo ainda mais o nosso conhecimento. 
Este trabalho permitiu nos aplicar e expandir as nossas competências neste âmbito.