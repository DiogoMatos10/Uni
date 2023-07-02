import java.io.*;
import java.net.*;
import java.net.Socket;
import java.nio.file.*;
import java.time.*;
import java.util.concurrent.*;


public class Server {
    public final ServerSocket serverSocket;
    public static LocalDateTime serverStartTime;        
    private static final int intervalo_persistencia=5;  //Intervalo de tempo com que o server vai guardar as informações em .txt

    public Server(ServerSocket serverSocket){
        this.serverSocket=serverSocket;
    }

    //Inicia o server
    public void startServer(Server server){
        serverStartTime=LocalDateTime.now();                        //Hora inicial com que o server iniciou
        LocalDateTime endTime=serverStartTime.plusHours(2);   //Hora final com que o server se irá encerrar (2 horas a mais que a hora inicial)
        System.out.println("Server iniciado");
        try{
            while(!serverSocket.isClosed() && LocalDateTime.now().isBefore(endTime)){
                ScheduledExecutorService executorService= Executors.newSingleThreadScheduledExecutor(); //Criação de um ScheduledExecutorService que será usado para executar uma task de persistência
                executorService.scheduleAtFixedRate(new PersistanceTask(),intervalo_persistencia, intervalo_persistencia, TimeUnit.MINUTES);    //O método scheduleAtFixedRate do executorService é chamado para agendar a execução da task de persistência de 5 em 5 min, essa tarefa será representada class PersistanceTask.
                Socket socket=serverSocket.accept();               //Aguarda a conexão de um novo aluno. Quando a conexão é estabelecida, o código continua na próxima linha
                System.out.println("Novo aluno no server");
                ClientHandler clientHandler=new ClientHandler(socket, server);
                Thread thread=new Thread(clientHandler);
                thread.start();                                    //Inicia uma nova thread com o cliente que ingressou
            }
        } catch (IOException e) {
            closeServerSocket();
        }
    }
    //Task de persistência para salvar os dados na pasta "Files". Tem um método "run" para o funcionamento do qual é necessário implementar a interface "Runnable". O método "run" é executado quando a task de persistência é agendada para ser executada, ou seja, de 5 em 5 min
    private static class PersistanceTask implements Runnable{
        public void run() {
            try {
                ClientHandler.saveQuestionAnsData();    //Salva dados das perguntas e respostas da aula em .txt
                ClientHandler.saveUserData();           //Salva dados de usuários (alunos) e a sua respetiva presença na aula em .txt
                ClientHandler.saveFilesData();          //Salva dados de ficheiros, ou seja, ficheiros adicionados ao server durante a aula em .txt
                System.out.println("Dados salvos em disco em "+LocalDateTime.now());

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    //Fecha o socket do server
    public void closeServerSocket(){
        try {
            if(serverSocket!=null){
                serverSocket.close();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        //Verifica se existem ficheiros de dados salvos  
        if(Files.exists(Path.of("srv/Files/data_question_ans.txt")) || Files.exists(Path.of("srv/Files/data_files.txt")) || Files.exists(Path.of("srv/Files/data_user.txt"))) {
            ClientHandler.loadQuestionAnsData();        //Carrega dados de perguntas e respostas
            ClientHandler.loadUserData();               //Carrega dados de usuários (alunos) e respetivas presenças
            ClientHandler.loadFilesData();              //Carrega dados de ficheiros
        }
        ServerSocket serverSocket=new ServerSocket(5555);
        Server server=new Server(serverSocket);
        server.startServer(server);                     //Inicia o server
    }
}








