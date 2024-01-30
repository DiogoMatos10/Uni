package src;

import java.io.*;
import java.util.*;

/*********************************
 * ClientAdmin
 * @author Henrique Rosa - l51923
 * @author Diogo Matos --- l54466
 *********************************/

//esta class representa a aplicação cliente administrativo que interage com o server
public class ClientAdmin {
    public static void main(String[] args) {
        String host = "", port = "";

        //lê o ficheiro de propriedades ("config.properties")
        try (InputStream inputStream = new FileInputStream("resources/config.properties")) {
            Properties properties = new Properties();

            //carrega as propriedades e define o regHost e o regPort apartir do ficheiro das propriedades
            properties.load(inputStream);
            host = properties.getProperty("regHost");
            port = properties.getProperty("regPort");

        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            // objeto remoto
            Remote_ClientAdm clientAdmin = (Remote_ClientAdm) java.rmi.Naming.lookup("rmi://" + host + ":" + port + "/clientAdmin");
            System.out.println("Sucesso!\n"); //mensagem para o client
            Scanner scan = new Scanner(System.in);

            // ciclo infinito para interação contínua
            while (true) {
                // prints de opções disponiveis para o cliente administrativo
                System.out.println("Ações - Cliente Administrativo: ");
                System.out.println("Listar artistas por aprovação -- 1");
                System.out.println("Aprovar artista --------------- 2");

                int adminAction = scan.nextInt();
                scan.nextLine();

                switch (adminAction) {
                    case 1:
                        // print da chamada do metodo remoto getListArtist() que retorna o resultado do pedido da lista de artistas por status
                        System.out.println(clientAdmin.getListArtist());
                        break;
                    case 2:
                        System.out.println("Insira um artistID para aprovar: ");
                        String id = scan.next();
                        // print da chamada do metodo remoto updateArtistID() que retorna o resultado do pedido de alteração de status de um artistID especifico para "aprovado"
                        System.out.println(clientAdmin.updateArtist(id));
                        break;
                    default:
                        System.out.println("Ação inválida.");
                }
                System.out.println("\n");
            }

        } catch (Exception b) {
            b.printStackTrace();
        }
    }
}
