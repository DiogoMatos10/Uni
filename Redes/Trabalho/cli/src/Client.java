import java.io.*;
import java.net.Socket;



public class Client {
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;


    public Client(Socket socket){
        try{
            this.socket = socket;
            this.bufferedWriter=new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader=new BufferedReader(new InputStreamReader(socket.getInputStream()));

        }catch (IOException e){
            closeEveryhing(socket, bufferedReader, bufferedWriter);
        }
    }

    //Este método é responsável por enviar mensagens para o server
    public void sendMessage(Client client) throws IOException {

        BufferedReader reader=new BufferedReader(new InputStreamReader(System.in));     //Responsável por ler as mensagens impressas pelo aluno no terminal

        try{
            System.out.println("WELCOME TO THE CLASS. PLEASE STATE YOUR STUDENT NUMBER TO ATTEND CLASS AND STATE YOUR ATTENDANCE:\n" +
                    "TO STATE YOUR STUDENT NUMBER, PLEASE TYPE  \" IAM \" FOLLOWED BY YOUR STUDENT NUMBER (WITH THE l)");
            String in=reader.readLine();
            

            //As próximas linhas são responsáveis por mandar para o server a autenticação do aluno
            client.bufferedWriter.write(in);                 //Escreve uma mensagem no buffer                   
            client.bufferedWriter.newLine();                 //Insere uma nova linha ao buffer
            client.bufferedWriter.flush();                   //Limpa o buffer e garante que a mensagem é enviada imediatamente

            //Loop responsável por manter o aluno enquanto o socket está conectado, ele vai recebendo inputs no terminal do aluno, analisa e executa a lógica com base no comando fornecido
            while(socket.isConnected()){
                String messageToSend=reader.readLine();
                String[] message_cut=messageToSend.split(" ");
                
                //Se o comando começar por PUTFILE e se cumprir os restantes parâmetros ele irá mandar o comando para o server e será tratado o envio do conteúdo do ficheiro especificado 
                if (message_cut[0].equals("PUTFILE") && message_cut.length==3 && isInteger(message_cut[2])) {
                    bufferedWriter.write("PUTFILE cli/Files/"+message_cut[1]);               //Escreve uma mensagem no buffer
                    bufferedWriter.newLine();                                                   //Insere uma nova linha ao buffer
                    bufferedWriter.flush();                                                     //Limpa o buffer e garante que a mensagem é enviada imediatamente
                    OutputStream outputStream=client.socket.getOutputStream();                  //Criação de um OutputStream usando fluxo de saída do socket do aluno
                    BufferedInputStream bufferedInputStream=new BufferedInputStream(new FileInputStream(message_cut[1]));    //Criação de um BufferedInputStream para ler o ficheiro a ser enviado
                    byte[] buffer=new byte[2000000000];                                         //Buffer criado com 2000000000 bytes, não foi possível criar apenas de 1024, porque assim os ficheiros não seriam mandados completamente ou quase completamente
                    int byteRead;
                    
                    //Dentro do loop o código lê dados do ficheiro em blocos para o buffer e escreve no OutputStream criado. É feito até chegue ao final do ficheiro
                    while((byteRead=bufferedInputStream.read(buffer))!=-1){                                     
                        outputStream.write(buffer,0,byteRead);                                        
                    }
                    outputStream.flush();                                                       //Garante que a mensagem é enviada imediatamente
                    bufferedInputStream.close();                                                //BufferedInputStream é fechado
                    outputStream.close();                                                       //OutputStream é fechado
                //Se o comando começar por GETFILE e se cumprir os restantes parâmetros ele irá mandar o comando para o server e será tratado o envio do conteúdo do ficheiro especificado por parte do server para o aluno
                }else if(message_cut[0].equals("GETFILE") && message_cut.length==3 && isInteger(message_cut[1])){
                    bufferedWriter.write("GETFILE "+message_cut[1]);                            //Escreve uma mensagem no buffer
                    bufferedWriter.newLine();                                                   //Insere uma nova linha ao buffer
                    bufferedWriter.flush();                                                     //Limpa o buffer e garante que a mensagem é enviada imediatamente
                    InputStream inputStream=socket.getInputStream();                            //Criação de um InputStream usando fluxo de entrada do socket do aluno
                   
                    FileOutputStream fileOutputStream=new FileOutputStream("cli/Files/ficheiro_recebido."+message_cut[2]);   //Criação de um FileOutputStream para guardar o ficheiro recebido na pasta "Files" do Client

                    byte[] buffer=new byte[2000000000];                                         //Buffer criado com 2000000000 bytes, não foi possível criar apenas de 1024, porque assim os ficheiros não seriam recebidos completamente ou quase completamente

                    int bytesRead=inputStream.read(buffer,0,buffer.length);                 //O código lê os dados recebidos pelo server para o buffer, o valor retornado, que é o número de bytes lidos, será guardado
                    fileOutputStream.write(buffer,0,bytesRead);                             //Os dados lidos serão guardados no ficheiro de saída
                    fileOutputStream.close();                                                   //FileOutputStream é fechado
                    inputStream.close();                                                        //InputStream é fechado
                }
                else{
                    bufferedWriter.write(messageToSend);                                        //Escreve uma mensagem no buffer
                    bufferedWriter.newLine();                                                   //Insere uma nova linha ao buffer
                    bufferedWriter.flush();                                                     //Limpa o buffer e garante que a mensagem é enviada imediatamente
                }
            }
        }catch (IOException e){
            System.out.println("ERROR\n");
            closeEveryhing(socket, bufferedReader, bufferedWriter);
        }
    }

    //Método responsável por receber as mensagens do server. Ele é executado numa nova thread para que se possa enviar e receber mensagens simultaneamente. Tem um método "run" para o funcionamento do qual é necessário implementar a interface "Runnable"
    public void messageFromServer(){
        new Thread(new Runnable() {
            public void run() {
                String msgServer;
                 
                //Dentro do loop, lê se as mensagens do server, usando um BufferedReader associado ao aluno e dá print à mensagem no terminal, isto enquanto o socket do aluno estiver conectado
                while(socket.isConnected()){       
                    try{
                        msgServer=bufferedReader.readLine();
                        System.out.println(msgServer);
                    }catch (IOException e){
                        closeEveryhing(socket, bufferedReader, bufferedWriter);
                    }
                }
            }
        }).start();
    }

    //Método responsável por fechar todos os recursos, tais como o socket do aluno, BufferedReader e BufferedWriter. Ele é chamado em caso de exceção ou quando a conexão é encerrada
    public void closeEveryhing(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter){
        try{
            if(bufferedReader!=null){
                bufferedReader.close();
            }
            if(bufferedWriter!=null){
                bufferedWriter.close();
            }
            if(socket!=null){
                socket.close();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
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

    public static void main(String[] args) throws IOException {
        Socket socket=new Socket("localhost", 5555);   
        Client client=new Client(socket);                           //Criação de um novo objeto "Client", com um socket conectado ao localhost na porta 5555

        client.messageFromServer();                                 //messageFromServer é chamado em uma nova thread para receber as mensagens do server e dar print a elas
        client.sendMessage(client);                                 //sendMessage é chamado para permitir ao aluno enviar mensagens para o server
    }
}
