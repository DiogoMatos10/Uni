import java.io.*;
import java.net.*;
import java.time.*;
import java.util.*;


//Representa os diferentes estados de presenças de um aluno
enum Pres{PRESENT, LATE, MISS}



//Esta class representa um aluno, possui atributos como: username->l seguido numero de aluno; pass que é a sua password
class Aluno{
    public String username, pass;

    public Pres atendance;

    public Aluno(String username, String pass){
        this.username=username;
        this.pass=pass;

    }
    //Método que define a presença de um Aluno
    public void setAtendance(Pres atendance) {
        this.atendance = atendance;
    }
}

//Esta class representa um conjunto de perguntas e respostas, mais especificamente, dois ArrayLists e ainda um número total de respostas
class Perg_Resp{
    ArrayList<String> perguntas;
    ArrayList<Answer> respostas;

    int n_perguntas;

    public Perg_Resp(){
        n_perguntas=0;
        perguntas=new ArrayList<>();
        respostas=new ArrayList<>();
    }

    //Método que adiciona uma nova pergunta ao ArrayList "perguntas" e incrementa o número total de respostas
    public void perguntar(String str){
        perguntas.add(str);
        n_perguntas++;
    }
    //Método que adiciona uma nova resposta (objeto Answer) a uma certa questão ao ArrayList "respostas"
    public void responder(int Q, String username, String ans){
        respostas.add(new Answer(Q, username, ans));
    }

    //Método que remove uma reposta(objeto Answer) com base no número da pergunta e no nome do usuário que respondeu 
    public void remover(int q,String user){
        for(Answer answer:respostas){
            if(answer.Q==q && answer.username.equals(user)){
                respostas.remove(answer);
                break;
            }
        }
    }
}

//Esta class represeta um ficheiro, possui como argumentos o nome de um ficheiro e o tamanho desse
class Ficheiro{
    String name;
    int size;
    public Ficheiro(String name, int size) {
        this.name=name;
        this.size=size;
    }
}

//Esta class representa uma resposta, possui como argumentos o número da questão, o nome do aluno que respondeu e a resposta em si
class Answer{
    int Q;
    String username, ans;

    public Answer(int Q, String username, String ans) {
        this.Q = Q;
        this.username=username;
        this.ans=ans;
    }
}

//Esta class implementa a interface "Runnable" e é responsável por assegurar a interação entre um aluno e o server
class ClientHandler implements Runnable {
    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    public static ArrayList<Aluno> presencas = new ArrayList<>();
    public static Perg_Resp perg_resp = new Perg_Resp();
    public static ArrayList<Ficheiro> files=new ArrayList<>();
    private Socket socket;
    public BufferedReader bufferedReader;
    public BufferedWriter bufferedWriter;
    public String clientUsername, pass;
    public String[] input;
    public Pres ass;
    public Server server;

    //Este constructor é responsável por inicializar e autenticar um aluno que se conecta ao server
    public ClientHandler(Socket socket, Server server) {
        try {
            this.socket = socket;                    //Objeto socket que representa a conexão com um aluno
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())); //É usado para escrever dados no fluxo de "saída" do socket, envolve um OutputStreamWriter que converte caracteres em bytes e escrevê-los no fluxo de "saída", em sintese, é responsável por mandar mensagens para o aluno
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));   //É usado para ler dados do fluxo de "entrada" do socket, envolve um  InputStreamReader que converte bytes em caracteres e lê os caracteres do fluxo de "entrada", em sintese, é responsável por receber mensagens do aluno 
            this.server=server;                     //Objeto server que representa a conexão com o server

            //Loop infinito para autenticar o aluno
            while (true) {
                int num_aluno = 0;
                this.input = bufferedReader.readLine().split(" ");  //lê a mensagem enviada pelo aluno para verificação de autenticação
                this.clientUsername = input[1];                           //número de aluno armazenado na variável
                //É ocorrida a verificação da autenticação do aluno, tais como os parâmetros do comando para a mesma
                if (input.length == 4) {
                    if (input[0].equals("IAM") && input[1].charAt(0) == 'l' && input[2].equals("withpass") && input[1].length() <= 6 && num_aluno == 0) {
                        num_aluno = Integer.parseInt(input[1].substring(1));
                        this.pass = input[3];
                        this.ass = timeToPres(timeAfterStart());                            //Assinatura guardada, é obtida a partir da chamada do timeAfterStart e convertido o tempo em minutos para a presença correspondente
                        
                        //Verificação se o aluno já existe no ArrayList de presenças, se não existir, ele é adicionado à mesma com a sua password, criando-se um novo objeto Aluno
                        if (findAluno(this.clientUsername) == null) {
                            presencas.add(new Aluno(this.clientUsername, this.pass));
                            findAluno(this.clientUsername).setAtendance(this.ass);          //É armazenada a presença do aluno na aula ao server
                            break;
                        //Verificação se o aluno já está registado à aula, ou seja, se existe no ArrayList de presenças, faz ainda a verificação se a password inserida é a mesma que está associada ao seu numero de aluno com a chamada do método verificationPass e ainda se a sua presença já foi registada anteriormente
                        } else if (findAluno(this.clientUsername) != null && verificationPass(this.clientUsername, this.pass) && Objects.requireNonNull(findAluno(this.clientUsername)).atendance != null) {
                            break;
                        } else {
                            this.bufferedWriter.write("ERROR " + this.clientUsername + "\n");    //Escreve uma mensagem no buffer em que mostra ao aluno que a autenticação foi mal sucedida
                            this.bufferedWriter.newLine();                                       //Insere uma nova linha ao buffer
                            this.bufferedWriter.flush();                                         //Limpa o buffer e garante que a mensagem é enviada imediatamente
                        }
                    }
                }else{
                    this.bufferedWriter.write("ERROR " + this.clientUsername + "\n");            //Escreve uma mensagem no buffer em que mostra ao aluno que a autenticação foi mal sucedida
                    this.bufferedWriter.newLine();                                               //Insere uma nova linha ao buffer
                    this.bufferedWriter.flush();                                                 //Limpa o buffer e garante que a mensagem é enviada imediatamente
                }
            }
            clientHandlers.add(this);                                                            //O novo objeto criado é adicionado ao ArrayList de clientHandlers
            this.bufferedWriter.write("HELLO " + this.clientUsername + "\n");                    //Escreve uma mensagem no buffer em que mostra ao aluno que a autenticação foi bem sucedida
            this.bufferedWriter.newLine();                                                       //Insere uma nova linha ao buffer
            this.bufferedWriter.flush();                                                         //Limpa o buffer e garante que a mensagem é enviada imediatamente
            }catch(IOException e){
            closeEveryThing(socket, bufferedReader, bufferedWriter);
        }
    }

    //Método responsável por fazer a leitura das mensagens recebidas pelo aluno
    public void run() {
        String[] messageFromClient;
        while (socket.isConnected()) {
            try {
                messageFromClient = bufferedReader.readLine().split(" "); //Lê a mensagem recebida pelo aluno, e divide a palavra usando o espaço como limitador. O resultado é armazenado no array 
                
                //Se a primeira palavra for "ASK"
                if (messageFromClient[0].equals("ASK")) {
                    StringBuilder sb = new StringBuilder();
                    //Concatenação das palavras seguintes para formar uma pergunta
                    for (int i = 1; i < messageFromClient.length; i++) {
                        sb.append(messageFromClient[i]).append(" ");
                    }
                    String concat = sb.toString().trim();  //Passagem da concatenação para uma só String 
                    perg_resp.perguntar(concat);           //A pergunta é registada usando o método responder do objeto perg_resp, vai precisamente adicionar a pergunta ao ArrayList das perguntas
                    this.bufferedWriter.write("QUESTION " + perg_resp.n_perguntas + ": " + concat); //Escreve uma mensagem no buffer em que mostra ao aluno que a sua pergunta foi registada com sucesso
                    this.bufferedWriter.newLine();         //Insere uma nova linha ao buffer
                    this.bufferedWriter.flush();           //Limpa o buffer e garante que a mensagem é enviada imediatamente
                }

                //Se a primeira palavra for "ANSWER", faz ainda a verificação dos parametros do comando
                if (messageFromClient[0].equals("ANSWER") && messageFromClient.length>1 && messageFromClient.length>2 && isInteger(messageFromClient[1])) {
                    StringBuilder sb = new StringBuilder();
                    //Concatenação das palavras seguintes para formar uma resposta
                    for (int i = 2; i < messageFromClient.length; i++) {
                        sb.append(messageFromClient[i]).append(" ");
                    }
                    String concat = sb.toString().trim();   //Passagem da concatenação para uma só String 
                    perg_resp.responder(Integer.parseInt(messageFromClient[1]), clientUsername, concat);    //A resposta é registada usando o método responder do objeto perg_resp, vai precisamente, adicionar um novo objeto Answer ao ArrayList das respostas
                    this.bufferedWriter.write("REGISTERED " + messageFromClient[1]);                        //Escreve uma mensagem no buffer em que mostra ao aluno que a sua resposta foi registada com sucesso
                    this.bufferedWriter.newLine();          //Insere uma nova linha ao buffer
                    this.bufferedWriter.flush();            //Limpa o buffer e garante que a mensagem é enviada imediatamente
                }

                //Se a primeira e única palavra for "LISTQUESTIONS"
                if (messageFromClient[0].equals("LISTQUESTIONS") && messageFromClient.length==1) {
                    listQuestions();                       //É chamado o método listQuestions que irá mandar para o aluno (client) todas as questões e as suas respetivas respostas com o número do aluno que respondeu
                }
                
                //Se a primeira palavra for "ERASE", faz ainda a verificação dos parametros do comando
                if (messageFromClient[0].equals("ERASE") && messageFromClient.length==2) {
                    perg_resp.remover(Integer.parseInt(messageFromClient[1]), clientUsername);  //A resposta a uma certa pergunta é removida do ArrayList das respostas, ou seja, é removido um objeto Answer, chamando o método remover do objeto perg_resp
                    this.bufferedWriter.write("REMOVED " + messageFromClient[1]);               //Escreve uma mensagem no buffer em que mostra ao aluno que o seu pedido de retirar a sua resposta a uma pergunta foi registada e realizada com sucesso
                    this.bufferedWriter.newLine();          //Insere uma nova linha ao buffer
                    this.bufferedWriter.flush();            //Limpa o buffer e garante que a mensagem é enviada imediatamente
                }

                //Se a primeira palavra for "PUTFILE", faz ainda a verificação dos parametros do comando
                if(messageFromClient[0].equals("PUTFILE") && messageFromClient.length==3 && isInteger(messageFromClient[2])){
                   InputStream inputStream=this.socket.getInputStream();        //Criação de um InputStream usando fluxo de entrada do socket do aluno
                   String[] file_name=messageFromClient[1].split("/");      
                   FileOutputStream fileOutputStream=new FileOutputStream("srv/Files/"+file_name[file_name.length-1]);   //Criação de um FileOutputStream para guardar o ficheiro recebido na pasta "Files" do Server
                   BufferedOutputStream bufferedOutputStream=new BufferedOutputStream(fileOutputStream);                    //Criação de um BufferedOutputStream que envolve FileOutputStream, é responsável por escrever no ficheiro criado anteriormente os dados recebidos
                   byte[] buffer=new byte[2000000000];                          //Buffer criado com 2000000000 bytes, não foi possível criar apenas de 1024, porque assim os ficheiros não seriam recebidos completamente ou quase completamente
                   int file=inputStream.read(buffer,0,buffer.length);       //O código lê os dados recebidos pelo aluno para o buffer, o valor retornado, que é o número de bytes lidos, será guardado
                   bufferedOutputStream.write(buffer,0,file);               //Os dados lidos serão guardados no ficheiro de saída
                   files.add(new Ficheiro(messageFromClient[1],Integer.parseInt(messageFromClient[2])));                   //Um novo objeto Ficheiro, constituido pelo nome do fichieor e pelo tamanho do ficheiro, será guardado no ArrayList files
                   fileOutputStream.close();                                    //FileOutputStream é fechado
                   inputStream.close();                                         //InputStream é fechado
                   bufferedOutputStream.close();                                //BufferedOutputStream é fechado
                   this.bufferedWriter.write("UPLOADED "+messageFromClient[1]); //Escreve uma mensagem no buffer em que mostra ao aluno que o seu pedido de mandar um ficheiro para dentro do server foi realizado com sucesso
                   this.bufferedWriter.newLine();                               //Insere uma nova linha ao buffer       
                   this.bufferedWriter.flush();                                 //Limpa o buffer e garante que a mensagem é enviada imediatamente
                }
                
                //Se a primeira e única palavra for "LISTQUESTIONS"
                if(messageFromClient[0].equals("LISTFILES") && messageFromClient.length==1){
                    listFiles();                       //É chamado o método listFiles que irá mandar para o aluno (client) todos os ficheiros 
                }

                //Se a priemira palavra for "GETFILE", faz ainda a verificação dos parametros do comando
                if(messageFromClient[0].equals("GETFILE") && messageFromClient.length==2 && isInteger(messageFromClient[1])){
                    String file_name= files.get(Integer.parseInt(messageFromClient[1])-1).name; //Guarda em file_name, o nome do ficheiro segundo o número do ficheiro pedido pelo aluno
                    //Percorre todos os ficheiros dentro do ArrayList de Ficheiros e verifica se o nome de algum é igual ao nome do ficheiro pedido pelo aluno
                    for(Ficheiro ficheiro:files){                
                        if(ficheiro.name.equals(file_name)){
                            OutputStream outputStream=this.socket.getOutputStream();                //Criação de um OutputStream usando fluxo de saída do socket do aluno
                            FileInputStream fis=new FileInputStream("srv/Files/"+ficheiro.name); //Criação de um FileOutputStream para guardar o ficheiro recebido da pasta "Files" do Server
                            BufferedInputStream bufferedInputStream=new BufferedInputStream(fis);   //Criação de um BufferedInputStream para ler o ficheiro a ser enviado

                            byte[] buffer=new byte[2000000000];                                     //Buffer criado com 2000000000 bytes, não foi possível criar apenas de 1024, porque assim os ficheiros não seriam enviados completamente ou quase completamente
                            int bytesRead;

                            this.bufferedWriter.write("FILE "+ficheiro.name+" "+ficheiro.size);     //Escreve uma mensagem no buffer em que mostra ao aluno que o seu pedido de obter um ficheiro do server foi realizado com sucesso
                            this.bufferedWriter.newLine();                                          //Insere uma nova linha ao buffer   
                            this.bufferedWriter.flush();                                            //Limpa o buffer e garante que a mensagem é enviada imediatamente
                            
                            //Dentro do loop o código lê dados do ficheiro em blocos para o buffer e escreve no OutputStream criado. É feito até chegue ao final do ficheiro
                            while((bytesRead=bufferedInputStream.read(buffer))!=-1){
                                outputStream.write(buffer,0,bytesRead);
                            }
                            outputStream.flush();                                                   //Garante que a mensagem é enviada imediatamente
                            bufferedInputStream.close();                                            //BufferedInputStream é fechado
                            outputStream.close();                                                   //OutputStream é fechado
                        }
                    }
                }

            } catch (IOException e) {
                closeEveryThing(socket, bufferedReader, bufferedWriter);
            }
        }
    }

    //Método responsável por remover "clients" do ArrayList ClientHandlers 
    public void removeClientHandler() {
        clientHandlers.remove(this);
    }

    //Método responsável por fechar todos os recursos, tais como o socket do aluno, BufferedReader e BufferedWriter. Ele é chamado em caso de exceção ou quando a conexão é encerrada
    public void closeEveryThing(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        removeClientHandler();
        try {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Método responsável por mostrar uma lista de perguntas e respostas, mostra a questão e as respostas a essa questão
    public void listQuestions() {
        //Percorre todos os "client" dentro do ArrayList de ClientHandlers e verifica se o nome de algum é igual ao nome do aluno que fez o pedido da "LISTQUESTIONS", isto para mandar a informação apenas ao aluno que pediu
        for (ClientHandler clientHandler : clientHandlers) {
            try {
                if (clientHandler.clientUsername.equals(clientUsername)) {
                    int question = 1;
                    //Percorre cada pergunta do ArrayList "perguntas" do objeto "perg_resp", para cada pergunta mostra a mesma, seguida das respostas e com a informação de quem as fez
                    for (String pergunta : perg_resp.perguntas) {
                        clientHandler.bufferedWriter.write("(" + question + ") " + pergunta);   //Escreve o número da questão, e ela em si no buffer
                        clientHandler.bufferedWriter.newLine();                                 //Insere uma nova linha ao buffer
                        clientHandler.bufferedWriter.flush();                                   //Limpa o buffer e garante que a mensagem é enviada imediatamente
                        int count = 0;
                        for (Answer resposta : perg_resp.respostas) {
                            if (resposta.Q == question) {
                                count++;
                                clientHandler.bufferedWriter.write("    (" + resposta.username + ") " + resposta.ans);      //Escreve o número do aluno seguido da resposta que o mesmo mandou no buffer
                                clientHandler.bufferedWriter.newLine();                                                     //Insere uma nova linha ao buffer
                                clientHandler.bufferedWriter.flush();                                                       //Limpa o buffer e garante que a mensagem é enviada imediatamente
                            }
                        }
                        //Se uma pergunta não tiver qualquer resposta, é mostrado para essa pergunta que não tem respostas
                        if (count == 0) {
                            clientHandler.bufferedWriter.write("    NOTANSWERED");          //Escreve no buffer a informação que uma certa pergunta não tem respostas
                            clientHandler.bufferedWriter.newLine();                             //Insere uma nova linha ao buffer
                            clientHandler.bufferedWriter.flush();                               //Limpa o buffer e garante que a mensagem é enviada imediatamente
                        }
                        question++;
                    }
                    clientHandler.bufferedWriter.write("ENDQUESTIONS");                     //Escreve no buffer a informação do fim das questões
                    clientHandler.bufferedWriter.newLine();                                     //Insere uma nova linha ao buffer
                    clientHandler.bufferedWriter.flush();                                       //Limpa o buffer e garante que a mensagem é enviada imediatamente
                }
            } catch (IOException e) {
                closeEveryThing(socket, bufferedReader, bufferedWriter);
            }
        }
    }

    //Método responsável por mostrar uma lista de ficheiros presentes no server
    public void listFiles(){
        //Percorre todos os "client" dentro do ArrayList de ClientHandlers e verifica se o nome de algum é igual ao nome do aluno que fez o pedido da "LISTFILES", isto para mandar a informação apenas ao aluno que pediu
        for(ClientHandler clientHandler:clientHandlers){
            try{
                if(clientHandler.clientUsername.equals(clientUsername)){
                    //Percorre cada index do ArrayList files, mostra todos os nomes de ficheiros que foram metidos no server com um número para cada, para ser mais fácil para o aluno, fazer um pedido "GETFILE"
                    for(int i=1;i<=files.size();i++){
                        clientHandler.bufferedWriter.write("("+i+") "+files.get(i-1).name);     //Escreve o número do ficheiro, e o nome do mesmo no buffer
                        clientHandler.bufferedWriter.newLine();                                 //Insere uma nova linha ao buffer
                        clientHandler.bufferedWriter.flush();                                   //Limpa o buffer e garante que a mensagem é enviada imediatamente
                    }
                    clientHandler.bufferedWriter.write("ENDFILES");                         //Escreve no buffer a informação do fim dos ficheiros
                    clientHandler.bufferedWriter.newLine();                                     //Insere uma nova linha ao buffer//Insere uma nova linha ao buffer
                    clientHandler.bufferedWriter.flush();                                       //Limpa o buffer e garante que a mensagem é enviada imediatamente
                }
            } catch (IOException e) {
                closeEveryThing(socket, bufferedReader, bufferedWriter);
            }
        }
    }

    //Método responsável por converter o tempo do tipo inteiro, desde que o server deu start até que um certo aluno entrou, para o tipo Pres, consoante o tempo que já passou
    public static Pres timeToPres(int time) {
        Pres x;
        if (time < 20) {
            x = Pres.PRESENT;
        } else if (time > 45) {
            x = Pres.MISS;
        } else {
            x = Pres.LATE;
        }
        return x;
    }

    //Método responsável por converter a presença do tipo Pres para o valor no tipo String
    public static String presToString(Pres ass) {
        String pres = null;
        if (ass == Pres.PRESENT) {
            pres = "PRESENT";
        } else if (ass == Pres.LATE) {
            pres = "LATE";
        } else if (ass == Pres.MISS) {
            pres = "MISS";
        }
        return pres;
    }

    //Método responsável por retornar o valor da diferença entre a hora quando o server foi iniciado e a hora quando o aluno entrou no server
    public static int timeAfterStart() {
        LocalDateTime currentTime = LocalDateTime.now();                            //Criação de um LocalDateTime que representa a hora local do aluno quando o aluno entrou no server
        Duration duration = Duration.between(Server.serverStartTime, currentTime);  //Criação de um Duration que representa a diferença entre a hora quando o server foi iniciado e a hora quando o aluno entrou no server
        return (int) duration.toMinutes();                                          //Retorna o valor da diferança convertida para minutos como inteiro
    }

    //Método responsável por converter a presença do tipo String para o valor no tipo Pres
    public static Pres stringToPres(String ass){
        Pres pres = null;
        if (ass.equals("PRESENT")) {
            pres = Pres.PRESENT;
        } else if (ass.equals("LATE")) {
            pres = Pres.LATE;
        } else if (ass.equals("MISS")) {
            pres = Pres.MISS;
        }
        return pres;
    }

    //Método responsável por guardar os dados das perguntas e das respostas correspondentes presentes nos ArrayLists do objeto "perg_resp" em um ficheiro de texto dentro do Server
    public static void saveQuestionAnsData() throws IOException {
        try {
            BufferedWriter writer_file=new BufferedWriter(new FileWriter("Server/Files/data_question_ans.txt"));    //Criação de um BufferedWriter para escrever no ficheiro "data_question_ans.txt" usando um FileWriter
            int n=perg_resp.n_perguntas;                            
            writer_file.write(n+"\n");                                                                                       //Escreve o número total de perguntas no ficheiro
            if(n!=0){
                //Percorre cada pergunta do ArrayList "perguntas" do objeto "perg_resp" escrevendo cada uma no ficheiro
                for (String pergunta:perg_resp.perguntas){
                    writer_file.write(pergunta+"\n");
                }
                //Loop responsável por escrever para cada pergunta, seguindo a ordem pela qual foram escritas anteriormente, a quantidade de respostas seguido do número de aluno de quem fez uma resposta, essa que aparece logo depois
                for(int i=1;i<=n;i++){
                    int p=0;
                    for(Answer resposta:perg_resp.respostas){
                        if(resposta.Q==i){
                            p++;
                        }
                    }
                    writer_file.write(p+"\n");                                                                               //Escreve o número total de respostas a uma certa pergunta no ficheiro
                    if(p!=0){
                        for(Answer resposta:perg_resp.respostas){
                            if(resposta.Q==i){
                                writer_file.write(resposta.username+"\n");                                                   //Escreve o número de aluno de quem fez uma resposta a uma certa pergunta no ficheiro
                                writer_file.write(resposta.ans+"\n");                                                        //Escreve a resposta que esse aluno fez no ficheiro
                            }
                        }
                    }
                }
            }
            writer_file.close();                                                                                             //BufferedWriter é fechado
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    //Método responsável por ler o ficheiro "data_questions_ans.txt" e guardar dentro dos ArrayLists do objeto "perg_resp" os dados das questões e das respostas
    public static void loadQuestionAnsData() throws IOException {
        try {
            BufferedReader reader_file = new BufferedReader(new FileReader("Server/Files/data_question_ans.txt"));  //Criaçao de um BufferedReader para ler o ficheiro "data_question_ans.txt" usando um FileReader
            String str=reader_file.readLine();
            //Faz se a verificação se o ficheiro está vazio, se estiver vazio, atribui-se à variável n_perguntas do objeto perg_resp, o número total de perguntas igual a zero
            if(str==null){
                perg_resp.n_perguntas=0;
            }else{
                int n=Integer.parseInt(str);
                //Loop responsável por adicionar cada pergunta ao ArrayList "perguntas" do objeto perg_resp
                for(int i=1; i<=n; i++){
                    perg_resp.perguntas.add(reader_file.readLine());
                }
                perg_resp.n_perguntas=n;                                                                                      //Atribui-se à variável "n_perguntas" do objeto "perg_resp" o número total de perguntas
                //Loop responsável por adicionar a cada pergunta as suas respostas, adicionando ao ArrayList "respostas" um novo objeto Answer
                for(int i=1; i<=n; i++){
                    int p = Integer.parseInt(reader_file.readLine());
                    for(int j=0; j<p; j++){
                        String usr=reader_file.readLine();
                        String ans=reader_file.readLine();
                        perg_resp.respostas.add(new Answer(i,usr,ans));
                    }
                }
            }
            reader_file.close();                                                                                               //BufferedReader é fechado
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    //Método responsável por guardar os dados das presenças presentes no ArrayList de objetos "Aluno" 
    public static void saveUserData() throws IOException {
        try {
            BufferedWriter writer_file=new BufferedWriter(new FileWriter("Server/Files/data_user.txt"));              //Criação de um BufferedWriter para escrever no ficheiro "data_user.txt" usando um FileWriter
            int n=presencas.size();                                                                                            
            writer_file.write(n+"\n");                                                                                         //Escreve o número total de presenças no ficheiro, ou seja, o número de alunos
            if(n!=0){
                //Loop responsável por escrever para cada aluno, o seu número, a password e a sua presença, no ficheiro
                for (Aluno aluno:presencas){
                    writer_file.write(aluno.username+"\n");                                                                    //Escreve o número do aluno no ficheiro
                    writer_file.write(aluno.pass+"\n");                                                                        //Escreve a password do aluno no ficheiro
                    writer_file.write(presToString(aluno.atendance)+"\n");                                                     //Escreve a presença do aluno no ficheiro
                }
            }
            writer_file.close();                                                                                               //BufferedWriter é fechado
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    //Método responsável por ler o ficheiro "data_user.txt" e guardar dentro do ArrayList de objetos "Aluno" os dados das presenças e dos alunos
    public static void loadUserData() throws IOException {
        try {
            BufferedReader reader_file = new BufferedReader(new FileReader("Server/Files/data_user.txt"));            //Criaçao de um BufferedReader para ler o ficheiro "data_user.txt" usando um FileReader 
            String str=reader_file.readLine();
            //Faz se a verificação se o ficheiro está vazio, se estiver vazio, fecha se o BufferedReader
            if(str==null){
                reader_file.close();
            }else{
                int n=Integer.parseInt(str);
                //Loop responsável por ler de três em três linhas, ou seja, o número do aluno, a password e a presença, e adiciona ao ArrayList "presencas" um novo objeto "Aluno" com os dados lidos
                for(int i=0; i<n; i++){
                    String usr=reader_file.readLine();
                    String pass=reader_file.readLine();
                    String atendance=reader_file.readLine();
                    presencas.add(new Aluno(usr, pass));
                    findAluno(usr).atendance=stringToPres(atendance);
                }
                reader_file.close();                                                                                          //BufferedReader é fechado
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    //Método responsável por guardar os dados dos ficheiros presentes no ArrayList de objetos "Ficheiro" 
    public static void saveFilesData() throws IOException {
        try {
            BufferedWriter writer_file=new BufferedWriter(new FileWriter("Server/Files/data_files.txt"));           //Criação de um BufferedWriter para escrever no ficheiro "data_files.txt" usando um FileWriter
            int n=files.size();
            writer_file.write(n+"\n");                                                                                       //Escreve o número total de ficheiros no ficheiro
            if(n!=0){
                //Loop responsável por escrever para cada ficheiro, o seu nome e o seu tamanho
                for (Ficheiro file:files){
                    writer_file.write(file.name+"\n");                                                                       //Escreve o nome do ficheiro no ficheiro
                    writer_file.write(file.size+"\n");                                                                       //Escreve o tamanho do ficheiro no ficheiro
                }
            }
            writer_file.close();                                                                                             //BufferedWriter é fechado
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    //Método responsável por ler o ficheiro "data_files.txt" e guardar dentro do ArrayList de objetos "Ficheiro" os dados dos ficheiros
    public static void loadFilesData() throws IOException {
        try {
            BufferedReader reader_file = new BufferedReader(new FileReader("Server/Files/data_files.txt"));         //Criaçao de um BufferedReader para ler o ficheiro "data_files.txt" usando um FileReader
            String str=reader_file.readLine();
            //Faz se a verificação se o ficheiro está vazio, se estiver vazio, fecha se o BufferedReader
            if(str==null){
                reader_file.close();
            }else{
                int n=Integer.parseInt(str);
                //Loop responsável por ler de duas em duas linhas, ou seja, o nome do ficheiro e o seu tamanho, e adiciona ao ArrayList "files" um novo objeto "Ficheiro" com os dados lidos
                for(int i=0; i<n; i++){
                    String file_name= reader_file.readLine();
                    int size= Integer.parseInt(reader_file.readLine());
                    files.add(new Ficheiro(file_name,size));
                }
                reader_file.close();                                                                                          //BufferedReader é fechado
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    //Método responsável por verificar a autenticação do aluno
    private static boolean verificationPass(String user, String pass){
        //Percorre todos os "alunos" dentro do ArrayList das presenças e verifica se o número de aluno e a password inserida é igual aos dados armazenados para um certo aluno dentro do ArrayList
        for(Aluno aluno:presencas){
            if(aluno.username.equals(user) && aluno.pass.equals(pass)){
                return true;
            }
        }
        return false;
    }

    //Método responsável por verificar se o número de aluno inserido já existe dentro do ArrayList das presenças
    private static Aluno findAluno(String user){
        //Percorre todos os "alunos" dentro do ArrayList das presenças e verifica se o número de aluno inserido é igual a algum número de aluno armazenado dentro do ArrayList, retorna o objeto "Aluno" correspondente se for igual, senão retorna null
        for(Aluno aluno:presencas){
            if(aluno.username.equals(user)){
                return aluno;
            }
        }
        return null;
    }

    //Método responsável por verificar se um número dentro de uma String é um número, retorna True se sim, e False se não
    private static boolean isInteger(String number){
        boolean ctrl=false;
        try{
            Integer.parseInt(number);
            ctrl=true;
        }catch(NumberFormatException e){
            return ctrl;
        }
        return ctrl;
    }
}
