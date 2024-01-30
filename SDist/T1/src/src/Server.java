package src;

import java.io.*;
import java.util.Properties;

/*********************************
 * Server
 * @author Henrique Rosa - l51923
 * @author Diogo Matos --- l54466
 *********************************/

//esta class representa o servidor do serviço para lidar com RMI.
public class Server {
    public static void main( String[] args ){
        //port padrão do registo
        int regPort=1099;

        //lê o ficheiro de propriedades ("config.properties")
        try(InputStream propfile = new FileInputStream("resources/config.properties")){

            Properties properties = new Properties();

            //carrega as propriedades e define a regPort apartir do ficheiro das propriedades
            properties.load(propfile);
            regPort = Integer.parseInt(properties.getProperty("regPortServer"));
        } catch( Exception e){
            e.printStackTrace();
        }

        try{
            //criação de instâncias dos objetos remotos
            Remote_ClientAdm remote_clientAdm =new ClientAdmImpl();
            Remote_ClientGen remote_clientGen =new ClientGenImpl();

            //obtenção do registo RMI usando o port
            java.rmi.registry.Registry registry = java.rmi.registry.LocateRegistry.getRegistry(regPort);

            //associação dos objetos remotos ao registo RMI
            registry.rebind("clientAdmin", remote_clientAdm);
            registry.rebind("clientGeneral", remote_clientGen);

            System.out.println("Servidor On"); //mensagem para o server
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
