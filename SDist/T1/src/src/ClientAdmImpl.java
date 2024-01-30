package src;

import java.io.*;
import java.rmi.server.UnicastRemoteObject;
import java.sql.*;
import java.util.Properties;

/*********************************
 * ClientAdmImpl
 * @author Henrique Rosa - l51923
 * @author Diogo Matos --- l54466
 *********************************/

public class ClientAdmImpl extends UnicastRemoteObject
        implements Remote_ClientAdm, java.io.Serializable {

    //construtor
    public ClientAdmImpl() throws java.rmi.RemoteException {
        super();
    }

    /******************************************************************************
     os seguintes métodos vão chamar o método opData com os argumentos necessários
     ao correto funcionamento das aplicações. Cada um deles tem uma mensagem
     especifica que vai ser mostrada na consola do servidor, para facilitar na
     identificação e correção de possíveis erros. Todos os métodos têm funcionamento
     análogo, logo, só foi comentado o primeiro.
     ********************************************************************************/

    public String getListArtist() throws java.rmi.RemoteException {
        System.out.println("Um user invocou getListArtist()"); //mensagem para o server
        String res = ""; //inicializa a String de retorno a zero
        try {
            res = opData(1, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    public String updateArtist(String id) throws java.rmi.RemoteException {
        System.out.println("Um user invocou updateArtist()"); //mensagem para o server
        String res = ""; //inicializa a String de retorno a zero
        try {
            res = opData(2, id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    public String opData(int op, String id) throws Exception {
        //Inicialização das Strings que vão conter as propriedades como Strings vazias
        String host = "", db = "", user = "", pwd = "";

        //Obtenção das propriedades a partir do ficheiro do tipo properties
        try (InputStream inputStream = new FileInputStream("resources/config.properties")) {

            Properties properties = new Properties();
            properties.load(inputStream);
            host = properties.getProperty("host");
            db = properties.getProperty("db");
            user = properties.getProperty("user");
            pwd = properties.getProperty("pwd");
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Criação da conecção com a base de dados postgres
        PostGresCon postGresCon = new PostGresCon(host, db, user, pwd);
        postGresCon.connect();
        Statement statement = postGresCon.getStatement();
        String ret = ""; //inicialização da String de retorno como vazia


        switch (op) {
            case 1: //listar artistas (ordenados por status e depois por Id de artista)
                try {
                    //result set que vai conter o nome, ID e status dos artistas, corretamente ordenados
                    ResultSet resultSet = statement.executeQuery("SELECT name,artistID, status FROM artists ORDER BY status,artistID");

                    String currentStatus = "";
                    ret = "\n#######Listas de artistas:#######\n"; //afetação da string de retorno para o cabeçalho da lista

                    while (resultSet.next()) { //repete enquanto houver valores no resultset
                        String name = resultSet.getString("name");
                        String status = resultSet.getString("status");
                        String id_ = resultSet.getString("artistID");

                        //o seguinte if serve para tornar a apresentação da lista mais legivel
                        if (!status.equals(currentStatus)) {
                            ret += "\n   ----" + status + ":----\n"; //separador por status da lista
                            currentStatus = status;
                        }
                        ret += "ID: " + id_ + " --- " + name + "\n"; //inserção dos dados dos artistas na lista
                    }

                    if (ret.length() == 0) { //failsafe caso não haja artistas.
                        ret = "Ainda não há artistas";
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;

            case 2://aprovar artista
                try {
                    //verificação de presença de valor no input id
                    if (id != null && !id.isEmpty()) {

                        //verificar se o id é um numero
                        int artistID;
                        try {
                            artistID = Integer.parseInt(id);
                        } catch (NumberFormatException e) {
                            ret = "Os ID de artista têm de ser número inteiros. Não foi inserido um numero.";
                            break;
                        }
                        //result set que vai ser utilizado para verificar se o artista com o id inserido já foi aprovado
                        ResultSet resultSet = statement.executeQuery("SELECT artistID,status FROM Artists WHERE artistID=" + id + "AND status='aprovado'");

                        if (resultSet.next()) {
                            System.out.println("kpapdmkam");
                            ret += "Este artista já foi aprovado.";
                            break;
                        }else{
                            //query para dar update ao valor status de um artirsta para 'aprovado'
                            String updateQuery = "UPDATE Artists SET status = 'aprovado' WHERE artistID = ?";
                            try (PreparedStatement preparedStatement = postGresCon.con.prepareStatement(updateQuery)) {
                                //definição do id do artista na query
                                preparedStatement.setInt(1, artistID);
                                //update dos valores
                                int update = preparedStatement.executeUpdate();
                                //mensagem de sucesso
                                if (update > 0) {
                                    ret = "O artista com o ID " + artistID + " foi aprovado.";
                                } else {
                                    ret = "Não foi encontrado nenhum artista com o ID " + artistID;
                                }
                            }
                        }
                    } else {
                        ret = "ID de artista inválido";
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }

        try {
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        postGresCon.disconnect();
        return ret;
    }
}