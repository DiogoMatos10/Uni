package rest;

import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.sql.Statement;
import java.util.Properties;

/*********************************
 * Management
 * @author Henrique Rosa - l51923
 * @author Diogo Matos --- l54466
 *********************************/
public class Management {
    public Management() {
    }
    PostGresCon dataBase() throws Exception {
        String host = "", db = "", user = "", pwd = "";
        try (InputStream input = new FileInputStream("src/main/resources/config.properties")) {
            Properties prop = new Properties();
            prop.load(input);
            host = prop.getProperty("host");
            db = prop.getProperty("db");
            user = prop.getProperty("user");
            pwd = prop.getProperty("pwd");
        }
        return new PostGresCon(host, db, user, pwd);
    }

    protected String newUser(Users users) {
        try {
            PostGresCon postGresCon = dataBase();
            postGresCon.connect();

            Statement statement = postGresCon.getStatement();
            ResultSet resultSet = statement.executeQuery("SELECT username, email FROM users WHERE username='" + users.getName() + "'");
            if (resultSet.next()) {
                postGresCon.disconnect();
                return "O nome inserido ou email já estão em uso.";
            }
            if(users.getRole().equals("administrador")){
                statement.executeUpdate("INSERT INTO users VALUES (DEFAULT, '" + users.getName() + "', '" + users.getEmail() + "', '" + users.getPwd() + "', '" + users.getRole() + "','nao aprovado')");
            }else {
                statement.executeUpdate("INSERT INTO users VALUES (DEFAULT, '" + users.getName() + "', '" + users.getEmail() + "', '" + users.getPwd() + "', '" + users.getRole() + "','aprovado')");

            }
            postGresCon.disconnect();
            return getUserInfo(users.getName());
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Erro interno do servidor ao processar o pedido.");
        }
        return "";
    }

    protected String getUserInfo(String username) {
        try {
            PostGresCon postGresCon = dataBase();
            postGresCon.connect();

            Statement statement = postGresCon.getStatement();
            ResultSet resultSet = statement.executeQuery("SELECT role,userID,status,username FROM users WHERE username='" + username + "'");
            if (resultSet.next()) {
                return resultSet.getString("role") + "," + resultSet.getString("userID")+","+resultSet.getString("status")+","+resultSet.getString("username");
            }
            postGresCon.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Erro interno do servidor ao processar o pedido.");
        }
        return "";
    }

    protected String login(String name, String password) {
        try {
            PostGresCon postGresCon = dataBase();
            postGresCon.connect();

            Statement statement = postGresCon.getStatement();
            ResultSet resultSet = statement.executeQuery("SELECT role,userID,status,username FROM users WHERE username='" + name + "' AND password='" + password + "'");
            if (resultSet.next()) {
                return resultSet.getString("role") + "," + resultSet.getString("userID")+","+resultSet.getString("status")+","+resultSet.getString("username");
            }
            postGresCon.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Erro interno do servidor ao processar o pedido.");
        }
        return "";
    }

    protected String approveAdmin(String username) throws Exception {
        PostGresCon postGresCon = dataBase();
        postGresCon.connect();
        Statement statement = postGresCon.getStatement();
        //result set que vai ser utilizado para verificar se o admin com o username inserido já foi aprovado
        ResultSet resultSet = statement.executeQuery("SELECT username,status FROM Users WHERE username='" + username + "' AND status='aprovado'");

        if (resultSet.next()) {
            return "Este admin já foi aprovado.";
        } else {
            //query para dar update ao valor status de um admin para 'aprovado'
            String updateQuery = "UPDATE Users SET status = 'aprovado' WHERE username = '"+username+"'";
            PreparedStatement preparedStatement = postGresCon.con.prepareStatement(updateQuery);
            int update = preparedStatement.executeUpdate();
            //mensagem de sucesso
            if (update > 0) {
                return "O admin com o username " + username + " foi aprovado.";
            }
            return "Não foi encontrado nenhum admin com o username " + username;
        }
    }

    protected String insertArtist(Artists artists) {
        try {
            PostGresCon postGresCon = dataBase();
            postGresCon.connect();

            Statement statement = postGresCon.getStatement();
            ResultSet resultSet = statement.executeQuery("SELECT name FROM artists WHERE name='" + artists.getName() + "'");

            if (resultSet.next()) {
                postGresCon.disconnect();
                return "Este artista já foi inserido.";
            }

            String[] location = artists.getLocation().split(",");
            statement.executeUpdate("INSERT INTO artists VALUES(DEFAULT, '" + artists.getName() + "', '" + artists.getTypeArt() + "', '" + location[0] + "', '" + location[1] + "', '" + "não aprovado" + "')");
            postGresCon.disconnect();
            return "Artista adicionado com sucesso.";
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Erro interno do servidor ao processar o pedido.");
        }
        return "";
    }

    protected String listArtists(String typeArt, String locationLatitude, String locationLongitude) {
        try {
            String str_return = "";
            PostGresCon postGresCon = dataBase();
            postGresCon.connect();
            Statement statement = postGresCon.getStatement();
            //obter os artistas
            //dentro de cada uma das verificações seguintes temos código análogo, pelo que só foi comentada a primeira
            ResultSet artists_resset;
            if (typeArt.equals("_") && locationLatitude.equals("_") && locationLongitude.equals("_")) { //sem filtros
                //obtenção do result set com os nomes e Ids de arrtistas que correspondam aos filtros
                artists_resset = statement.executeQuery("SELECT name,artistID FROM artists ORDER BY artistID");

                str_return += "Lista de artistas:\n";
                while (artists_resset.next()) { //repete enquanto houver valores para ler no result set com os artistas
                    //afetação da string de retorno para todos os artistas que correspondem ao filtro
                    str_return += "ID: " + artists_resset.getString("artistID") + " --- Nome: " + artists_resset.getString("name") + " --- Classificação: " + getFinalRating(artists_resset.getString("artistID")) + "\n";
                }
            } else if (typeArt.equals("_")) { //apenas com filtro por localização
                artists_resset = statement.executeQuery("SELECT name, artistID FROM artists  WHERE locationLatitude='" + locationLatitude + "' AND locationLongitude='" + locationLongitude + "' ORDER BY artistID");
                str_return += "Lista de artistas:\n";
                while (artists_resset.next()) {
                    str_return += "ID: " + artists_resset.getString("artistID") + " --- Nome: " + artists_resset.getString("name") + " --- Classificação: " + getFinalRating(artists_resset.getString("artistID")) + "\n";
                }
            } else if (locationLatitude.equals("_") && locationLongitude.equals("_")) { //apenas com filtro por tipo de arte
                artists_resset = statement.executeQuery("SELECT name,artistID FROM artists WHERE typeart='" + typeArt + "' ORDER BY artistID");
                str_return += "Lista de artistas:\n";
                while (artists_resset.next()) {
                    str_return += "ID: " + artists_resset.getString("artistID") + " --- Nome: " + artists_resset.getString("name") + " --- Classificação: " + getFinalRating(artists_resset.getString("artistID")) + "\n";
                }
            } else {  //com ambos os filtros
                artists_resset = statement.executeQuery("SELECT name, artistID FROM artists WHERE typeart='" + typeArt + "' locationLatitude='" + locationLatitude + "' AND locationLongitude='" + locationLongitude + "' ORDER BY artistID");
                str_return += "Lista de artistas:\n";
                while (artists_resset.next()) {
                    str_return += "ID: " + artists_resset.getString("artistID") + " --- Nome: " + artists_resset.getString("name") + " --- Classificação: " + getFinalRating(artists_resset.getString("artistID")) + "\n";
                }
            }
            if(str_return.equals("Lista de artistas:\n")){
                str_return+="Lista vazia";
            }
            return str_return;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Erro interno do servidor ao processar o pedido.");
        }
        return "";
    }

    private String getFinalRating(String artistID){
            try {
                PostGresCon postGresCon = dataBase();
                postGresCon.connect();

                float sum=0;
                int count=0;
                Statement statement = postGresCon.getStatement();
                ResultSet resultSet = statement.executeQuery("SELECT rating FROM ratings WHERE artistID='" + artistID + "'");

                while (resultSet.next()){
                    sum += resultSet.getInt("rating");
                    count++;
                }
                float res=sum/count;
                if(count==0){
                    return "X";
                }
                postGresCon.disconnect();
                return String.valueOf(res);
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Erro interno do servidor ao processar o pedido.");
            }
            return "";
    }

    protected String listArtistsWithStatus() {
        try {
            PostGresCon postGresCon = dataBase();
            postGresCon.connect();
            Statement statement = postGresCon.getStatement();


            ResultSet resultSet = statement.executeQuery("SELECT name,artistID, status FROM artists ORDER BY status,artistID");

            String currentStatus = "";
            String ret = "\n#######Listas de artistas:#######\n"; //afetação da string de retorno para o cabeçalho da lista

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

            if (ret.equals("\n#######Listas de artistas:#######\n")) { //failsafe caso não haja artistas.
                return  "Ainda não há artistas";
            }
            return ret;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Erro interno do servidor ao processar o pedido.");
        }
        return "";
    }

    protected String approveArtist(String id) throws Exception {
            PostGresCon postGresCon = dataBase();
            postGresCon.connect();
            Statement statement = postGresCon.getStatement();
            //result set que vai ser utilizado para verificar se o artista com o id inserido já foi aprovado
            ResultSet resultSet = statement.executeQuery("SELECT artistID,status FROM Artists WHERE artistID=" + id + "AND status='aprovado'");

            if (resultSet.next()) {
                return "Este artista já foi aprovado.";
            } else {
                //query para dar update ao valor status de um artista para 'aprovado'
                String updateQuery = "UPDATE Artists SET status = 'aprovado' WHERE artistID = '"+id+"'";
                PreparedStatement preparedStatement = postGresCon.con.prepareStatement(updateQuery);
                int update = preparedStatement.executeUpdate();
                //mensagem de sucesso
                if (update > 0) {
                    return "O artista com o ID " + id + " foi aprovado.";
                }
                return "Não foi encontrado nenhum artista com o ID " + id;
            }
    }

    protected String updateArtist(String artistID,String name,String typeArt, String locationLatitude, String locationLongitude){
        try {
            PostGresCon postGresCon = dataBase();
            postGresCon.connect();
            Statement statement = postGresCon.getStatement();


            ResultSet resultSet= statement.executeQuery("SELECT name,artistID, locationLongitude,locationLatitude,typeArt,status FROM artists WHERE artistID='"+artistID+"'");
            while (resultSet.next()){
                if (typeArt.equals("_") && locationLatitude.equals("_") && locationLongitude.equals("_")) { //mudança do nome
                    String updateQuery = "UPDATE Artists SET name ='"+name+"' WHERE artistID ='"+resultSet.getString("artistID")+"'";
                    PreparedStatement preparedStatement=postGresCon.con.prepareStatement(updateQuery);
                    int update=preparedStatement.executeUpdate();
                    if(update>0){
                        return "Artista atualizado.";
                    }
                } else if (typeArt.equals("_") && locationLongitude.equals("_")) { //apenas mudando a latitude
                    String updateQuery = "UPDATE Artists SET locationLatitude ='"+locationLatitude+"' WHERE artistID ='"+resultSet.getString("artistID")+"'";
                    PreparedStatement preparedStatement=postGresCon.con.prepareStatement(updateQuery);
                    int update=preparedStatement.executeUpdate();
                    if(update>0){
                        return "Artista atualizado.";
                    }
                } else if (typeArt.equals("_") && locationLongitude.equals("_")) { //apenas mudando a longitude
                    String updateQuery = "UPDATE Artists SET locationLongitude ='"+locationLongitude+"' WHERE artistID ='"+resultSet.getString("artistID")+"'";
                    PreparedStatement preparedStatement=postGresCon.con.prepareStatement(updateQuery);
                    int update=preparedStatement.executeUpdate();
                    if(update>0){
                        return "Artista atualizado.";
                    }
                } else if (locationLatitude.equals("_") && locationLongitude.equals("_")) { //apenas mudando o tipo de arte
                    String updateQuery = "UPDATE Artists SET typeArt ='"+typeArt+"' WHERE artistID ='"+resultSet.getString("artistID")+"'";
                    PreparedStatement preparedStatement=postGresCon.con.prepareStatement(updateQuery);
                    int update=preparedStatement.executeUpdate();
                    if(update>0){
                        return "Artista atualizado.";
                    }
                } else {  //com ambas as mudanças
                    String updateQuery = "UPDATE Artists SET typeArt ='"+typeArt+"' AND locationLatitude='"+locationLatitude+"' AND locationLongitude='"+locationLongitude+"' WHERE artistID ='"+resultSet.getString("artistID")+"'";
                    PreparedStatement preparedStatement=postGresCon.con.prepareStatement(updateQuery);
                    int update=preparedStatement.executeUpdate();
                    if(update>0){
                        return "Artista atualizado.";
                    }
                }
            }
            return "";
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Erro interno do servidor ao processar o pedido.");
        }
        return "";
    }

    protected String infoArtist(String name){
        try {
            PostGresCon postGresCon = dataBase();
            postGresCon.connect();
            Statement statement = postGresCon.getStatement();

            ResultSet resultSet = statement.executeQuery("SELECT artistID,status,typeart,locationlatitude,locationlongitude FROM artists WHERE name='" + name +"'");

            while (resultSet.next()){
                return "Nome: "+name+" --- ID: "+resultSet.getString("artistID")+" --- Tipo de Arte: "+resultSet.getString("typeart")+" --- Localização (latitude,longitude): "+resultSet.getString("locationlatitude")+","+resultSet.getString("locationlongitude")+" --- Status: "+resultSet.getString("status");
            }
            return "O nome de artista inserido não existe.";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    protected String listPerformancesLive() {
        try {
            PostGresCon postGresCon = dataBase();
            postGresCon.connect();
            Statement statement = postGresCon.getStatement();
            java.sql.Date currentDate = new java.sql.Date(System.currentTimeMillis());

            ResultSet performances = statement.executeQuery("SELECT a.name, p.locationLatitude, p.locationLongitude " +
                    "FROM performances p, artists a " +
                    "WHERE p.artistID = a.artistID " +
                    "AND p.date = '" + currentDate + "'");
            String str = "Lista de espéculos a decorrer";
            while (performances.next()) {
                //afetação da string para as localizações
                str += "\n" + performances.getString("name") + "----- Latitude:" + performances.getString("locationLatitude") + "----- Longitude:" + performances.getString("locationLongitude") + "\n";
            }
            if(str.equals("Lista de espéculos a decorrer")){
                return "Lista vazia.";
            }
            return str;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Erro interno do servidor ao processar o pedido.");
        }
        return "";
    }

    protected String listPerformancesFuture(String name) {
        try {
            PostGresCon postGresCon = dataBase();
            postGresCon.connect();
            Statement statement = postGresCon.getStatement();
            java.sql.Date currentDate = new java.sql.Date(System.currentTimeMillis());

            ResultSet performances = statement.executeQuery("SELECT p.date, p.locationLatitude, p.locationLongitude " +
                    "FROM performances p, artists a " +
                    "WHERE p.artistID = a.artistID " +
                    "AND p.date > '" + currentDate +
                    "'AND a.name='" + name + "'");
            String str = "Lista de próximos espetáculos de "+name;
            while (performances.next()) {
                //afetação da string para as localizações
                str += "\n" + performances.getString("date") + "----- Latitude:" + performances.getString("locationLatitude") + "----- Longitude:" + performances.getString("locationLongitude") + "\n";
            }
            if(str.equals("Lista de espéculos a decorrer de "+name)){
                return "Lista vazia.";
            }
            return str;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Erro interno do servidor ao processar o pedido.");
        }
        return "";
    }

    protected String listPerformancesPrevious(String name) {
        try {
            PostGresCon postGresCon = dataBase();
            postGresCon.connect();
            Statement statement = postGresCon.getStatement();
            java.sql.Date currentDate = new java.sql.Date(System.currentTimeMillis());

            ResultSet performances = statement.executeQuery("SELECT p.date, p.locationLatitude, p.locationLongitude " +
                    "FROM performances p, artists a " +
                    "WHERE p.artistID = a.artistID " +
                    "AND p.date < '" + currentDate +
                    "'AND a.name='" + name + "'");
            String str = "Lista de espéculos já feitos por " + name;
            while (performances.next()) {
                //afetação da string para as localizações
                str += "\n" + performances.getString("date") + "----- Latitude:" + performances.getString("locationLatitude") + "----- Longitude:" + performances.getString("locationLongitude") + "\n";
            }
            if(str.equals("Lista de espéculos já feitos por " + name)){
                return "Lista vazia.";
            }
            return str;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Erro interno do servidor ao processar o pedido.");
        }
        return "";
    }

    protected String donateArtist(Donations donations) {
        try {
            PostGresCon postGresCon = dataBase();
            postGresCon.connect();
            Statement statement = postGresCon.getStatement();
            java.sql.Date currentDate = new java.sql.Date(System.currentTimeMillis());
            String str_return = "";
            //retorna o resultset com o id que tenha o nome do artista inserido
            ResultSet artist = statement.executeQuery("SELECT artistID FROM artists WHERE name='" + donations.getArtistName() + "'");


            while (artist.next()) {//repete enquanto houver valores para ler no result set
                //adiciona os valores inseridos á base de dados
                statement.executeUpdate("INSERT INTO donations VALUES (DEFAULT,'" + donations.getUserID() + "', '" + artist.getInt("artistID") + "', '" + donations.getValue() + "', '" + currentDate + "')");
                return "Doação enviada com sucesso - artista:" + donations.getArtistName() + " | valor:" + donations.getValue();
            }
            if (str_return.length() == 0) {
                return "Artista não encontrado";
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Erro interno do servidor ao processar o pedido.");
        }
        return "";
    }

    protected String listDonations(String name) {
        try {
            PostGresCon postGresCon = dataBase();
            postGresCon.connect();
            Statement statement = postGresCon.getStatement();

            String str_return = "";
            ResultSet artist_donation = statement.executeQuery("SELECT d.value , d.date FROM donations d, artists a WHERE d.artistID=a.artistID AND a.name='" + name + "'");

            while (artist_donation.next()) {
                //afetação da string de retorno para todas as donations para um determinado artista
                str_return += "Valor: " + artist_donation.getString("value") + " | Data: " + artist_donation.getString("date") + "\n";
            }
            if (str_return.length() > 0) {
                return  "\n   ----Lista de doações para o artista " + name + ":----\n" + str_return;
            } else {
                return  "\n Ainda não há doações para este artista / O ID inserido não pertence a nenhum artista.";
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Erro interno do servidor ao processar o pedido.");
        }
        return "";
    }

    protected String ratingArtist(Ratings ratings){
        try {
            PostGresCon postGresCon = dataBase();
            postGresCon.connect();
            Statement statement = postGresCon.getStatement();
            String str_return = "";
            //retorna o resultset com o id que tenha o nome do artista inserido
            ResultSet artist = statement.executeQuery("SELECT artistID FROM artists WHERE name='" + ratings.getName() + "'");


            while (artist.next()) {//repete enquanto houver valores para ler no result set
                //adiciona os valores inseridos á base de dados
                statement.executeUpdate("INSERT INTO ratings VALUES (DEFAULT,'" + artist.getString("artistID") + "', '" + ratings.getRating() +  "')");
                return "Classificação realizada com sucesso - artista:" + ratings.getName() + " | classificação:" + ratings.getRating();
            }
            if (str_return.length() == 0) {
                return "Artista não encontrado";
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Erro interno do servidor ao processar o pedido.");
        }
        return "";
    }
}
