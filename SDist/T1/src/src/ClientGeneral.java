package src;

import java.io.*;
import java.util.*;


/*********************************
 * ClientGeneral
 * @author Henrique Rosa - l51923
 * @author Diogo Matos --- l54466
 *********************************/

// esta class representa a aplicação cliente geral que interage com o server
public class ClientGeneral {
    public static void main(String[] args) {
        String host = "", port = "";

        // lê o ficheiro de propriedades ("config.properties")
        try (InputStream inputStream = new FileInputStream("resources/config.properties")) {
            Properties properties = new Properties();

            // carrega as propriedades e define o regHost e o regPort apartir do ficheiro das propriedades
            properties.load(inputStream);
            host = properties.getProperty("regHost");
            port = properties.getProperty("regPort");

        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            // objeto remoto
            Remote_ClientGen clientGeneral = (Remote_ClientGen) java.rmi.Naming.lookup("rmi://" + host + ":" + port + "/clientGeneral");
            System.out.println("Sucesso!"); //mensagem para o client
            Scanner scan = new Scanner(System.in);

            // ciclo infinito para interação contínua
            while (true) {
                // prints de opções disponiveis para o cliente geral
                System.out.println("Ações - Cliente Geral:");
                System.out.println("Registar Novo artista ------------------- 1");
                System.out.println("Listar artistas ------------------------- 2");
                System.out.println("Listar locais de espetáculo a decorrer -- 3");
                System.out.println("Listar espétaculos por artista ---------- 4");
                System.out.println("Doar a um artista ----------------------- 5");
                System.out.println("Listar doações por artista -------------- 6");

                int generalAction = scan.nextInt();
                scan.nextLine();
                switch (generalAction) {
                    case 1:
                        System.out.println("Insira a informação do artista a inserir, no formato [nome , tipo , localização , acting]: ");
                        String artist = scan.nextLine();
                        // print da chamada do metodo remoto insertArtist() que retorna o resultado do pedido de inserção de um artista com o input do user
                        System.out.println("\n" + clientGeneral.insertArtist(artist.toLowerCase()));
                        break;
                    case 2:
                        System.out.println("Inserir termos para a filtragem da lista, no formato [ tipo de arte , localização ]\nCaso não seja necessario algum (ou ambos) dos filtros, por favor digite [\"_\"] no seu lugar: ");
                        String filters = scan.nextLine();
                        // print da chamada do metodo remoto listArtist() que retorna o resultado do pedido da lista de artistas com filtros
                        System.out.println(clientGeneral.listArtists(filters.toLowerCase()));
                        break;
                    case 3:
                        System.out.println("Lista de localizações:");
                        // print da chamada do metodo remoto listPerformLocations() que retorna o resultado do pedido da lista de localizações onde existem artistas a atuar
                        System.out.println(clientGeneral.listPerformLocations());
                        break;
                    case 4:
                        System.out.println("Insira o ID do Artista:");
                        String id = scan.next().trim();
                        // print da chamada do metodo remoto listShow() que retorna o resulta do pedido da lista das datas e localizações onde já atuou um determinado artistID
                        System.out.println(clientGeneral.listShow(id));
                        break;
                    case 5:
                        System.out.println("Insira o artistID a quem quer doar e o seu valor, no formato [ artistID , valor ]: ");
                        String donation = scan.nextLine();
                        // print da chamada do metodo remoto donateArtist() que retorna o resultado do pedido de doação de um certo valor a um determinado artistID
                        System.out.println(clientGeneral.donateArtist(donation));
                        break;
                    case 6:
                        System.out.println("Insira o ID do Artista:");
                        String idArtistDonation = scan.next().trim();
                        // print da chamada do metodo remoto listDonations() que retorna o resultado do pedido da lista das doações recebidas por um determinado artistID
                        System.out.println(clientGeneral.listDonations(idArtistDonation));
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
