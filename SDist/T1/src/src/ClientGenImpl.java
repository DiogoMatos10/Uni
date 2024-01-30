package src;

import java.io.*;
import java.rmi.server.UnicastRemoteObject;
import java.sql.*;
import java.time.LocalDate;
import java.util.Properties;
import static java.lang.Boolean.*;

/*********************************
 * ClientGenImpl
 * @author Henrique Rosa - l51923
 * @author Diogo Matos --- l54466
 *********************************/

public class ClientGenImpl extends UnicastRemoteObject
        implements Remote_ClientGen, java.io.Serializable {

    //construtor
    public ClientGenImpl() throws java.rmi.RemoteException {
        super();
    }


    /******************************************************************************
     os seguintes métodos vão chamar o método opData com os argumentos necessários
     ao correto funcionamento das aplicações. Cada um deles tem uma mensagem
     especifica que vai ser mostrada na consola do servidor, para facilitar na
     identificação e correção de possíveis erros. Todos os métodos têm funcionamento
     análogo, logo, só foi comentado o primeiro.
     ********************************************************************************/

    public String insertArtist(String str) throws java.rmi.RemoteException{
        System.out.println("Um user invocou insertArtist()"); //mensagem para o server
        String res=""; //inicializa a String de retorno como vazia
        try {
            //chama o metodo opData com os argumentos necessarios ao seu correto funcionamento
            res=opData(1,str);
        }catch (Exception e){
            e.printStackTrace();
        }
        return res;
    }

    public String listArtists(String str) throws java.rmi.RemoteException{
        System.out.println("Um user invocou listArtists()");
        String res="";
        try {
            res=opData(2,str);
        }catch (Exception e){
            e.printStackTrace();
        }
        return res;
    }

    public String listPerformLocations() throws java.rmi.RemoteException{
        System.out.println("Um user invocou listPerformLocations()");
        String res="";
        try {
            res=opData(3,null);
        }catch (Exception e){
            e.printStackTrace();
        }
        return res;
    }

    public String listShow(String str) throws java.rmi.RemoteException{
        System.out.println("Um user invocou listShow()");
        String res="";
        try {
            res=opData(4,str);
        }catch (Exception e){
            e.printStackTrace();
        }
        return res;
    }

    public String donateArtist(String str) throws java.rmi.RemoteException{
        System.out.println("Um user invocou donateArtist()");
        String res="";
        try {
            res=opData(5,str);
        }catch (Exception e){
            e.printStackTrace();
        }
        return res;
    }

    public String listDonations(String str) throws java.rmi.RemoteException{
        System.out.println("Um user invocou listDonations()");
        String res="";
        try {
            res=opData(6,str);
        }catch (Exception e){
            e.printStackTrace();
        }
        return res;
    }




    public String opData(int op, String data) throws Exception {
        //Inicialização das Strings que vão conter as propriedades como Strings vazias
        String host="", db="", user="", pwd="";
        String str_return = "";//inicialização da String de retorno como vazia
        //Obtenção das propriedades a partir do ficheiro do tipo properties
        try(InputStream inputStream=new FileInputStream("resources/config.properties")){

            Properties properties=new Properties();

            properties.load(inputStream);
            host=properties.getProperty("host");
            db=properties.getProperty("db");
            user=properties.getProperty("user");
            pwd=properties.getProperty("pwd");
        }catch (Exception e){
            e.printStackTrace();
        }

        //Criação da conecção com a base de dados postgres
        PostGresCon postGresCon=new PostGresCon(host,db,user,pwd);
        postGresCon.connect();
        Statement statement=postGresCon.getStatement();


        switch (op){

            case 1: //Registar um novo artista

                String [] data1 = data.split(",");

                //caso a string inserida não esteja no formato correto para ser usado:
                if (data.equals("") || data1.length < 3){
                    str_return= "O input inserido não está no formato correto";
                    break;
                }
                //remoção dos espaços do input.
                data1[1] = data1[1].trim();
                data1[2] = data1[2].trim();

                //inserção de um artista na base de dados com os dados
                statement.executeUpdate("INSERT INTO artists VALUES(DEFAULT, '" + data1[0] + "', '" + data1[1] + "', '" + data1[2] + "', '" + parseBoolean(data1[3]) + "', '" +"não aprovado"+ "')");
                str_return="O artista "+data1[0]+" foi registado com sucesso";//mensagem de sucesso
                break;

            case 2: //Listar artistas com filtros por tipo de arte e localização

                String [] data2 = data.split(",");
                //check de formato
                if (data.equals("") || data2.length<2){
                    str_return= "O input inserido não está no formato correto";
                    break;
                }
                str_return="Lista de artistas:\n"; //cabeçalho da lista

                //remoção dos espaços do input.
                String type= data2[0].trim();
                String loc= data2[1].trim();


                //obter os artistas
                //dentro de cada uma das verificações seguintes temos código análogo, pelo que só foi comentada a primeira
                ResultSet artists_resset;
                if (type.equals("_") && loc.equals("_")){ //sem filtros
                    //obtenção do result set com os nomes e Ids de arrtistas que correspondam aos filtros
                    artists_resset= statement.executeQuery("SELECT name,artistID FROM artists ORDER BY artistID");
                    while (artists_resset.next()){ //repete enquanto houver valores para ler no result set com os artistas
                        //afetação da string de retorno para todos os artistas que correspondem ao filtro
                        str_return += "ID: "+artists_resset.getString("artistID")+" --- "+artists_resset.getString("name")+"\n";
                    }
                }else if(type.equals("_")){ //apenas com filtro por localização
                    artists_resset= statement.executeQuery("SELECT name, artistID FROM artists WHERE location='"+loc+"' ORDER BY artistID");
                    while (artists_resset.next()){
                        str_return += "ID: "+artists_resset.getString("artistID")+" --- "+artists_resset.getString("name")+"\n";
                    }
                }else if(loc.equals("_")){ //apenas com filtro por tipo de arte
                    artists_resset= statement.executeQuery("SELECT name,artistID FROM artists WHERE typeart='"+type+"' ORDER BY artistID");
                    while (artists_resset.next()){
                        str_return += "ID: "+artists_resset.getString("artistID")+" --- "+ artists_resset.getString("name")+"\n";
                    }
                }else{  //com ambos os filtros
                    artists_resset= statement.executeQuery("SELECT name FROM artists WHERE typeart='"+type+"' location='"+loc+"' ORDER BY artistID");
                    while (artists_resset.next()){
                        str_return += "ID: "+artists_resset.getString("artistID")+" --- "+ artists_resset.getString("name")+"\n";
                    }
                }

                break;

            case 3: //Listar locais de espetáculos
                //obtenção dos artistas que estão a atuar e a sual localização
                ResultSet performances = statement.executeQuery("SELECT name,location FROM Artists WHERE (acting=true)");
                while (performances.next()){
                    //afetação da string para as localizações
                    str_return += performances.getString("name")+"-----"+performances.getString("location")+"\n";
                }
                break;

            case 4: //Listar datas e localizações onde já atuou um dado artista, identificado pelo seu ID, como requerido no enunciado
                //verificar se o input é um nuermo para poder ser usado como id
                try {
                    Integer.parseInt(data);
                }catch(NumberFormatException e){
                    str_return = "Os ID de artista têm de ser número inteiros. Não foi inserido um numero.";
                    break;
                }
                //definição da data atual
                LocalDate d = LocalDate.now();
                String curr_date = d.toString();
                //obtenção de todas as performances que precedam a data atual
                ResultSet perfom_done = statement.executeQuery("SELECT date, location FROM performances WHERE( artistid="+data+" AND date<'"+curr_date+"')");


                int rowcount = 0;//contador de linhas para verificação de existenncia de atuações
                while(perfom_done.next()){ //repete enquanto houver valores no result set para ler
                    str_return += perfom_done.getString("date")+" ---- "+perfom_done.getString("location")+".\n";
                    rowcount++;
                }
                //se não houver pelo menos uma performance, então o artista ainda não atuou
                if (rowcount<1){
                    str_return = "O artista ainda não atuou.\n";
                }
                else{
                    str_return = "O artista já atuou nas seguintes datas e locais:\n"+str_return;
                }
                break;

            case 5: //Fazer doação a um artista
                String [] data5=data.split(",");

                //verificação de formato do input
                if (data.equals("") || data5.length < 2){
                    str_return= "O input inserido não está no formato correto";
                    break;
                }

                //tirar os espaços
                data5[0]=data5[0].trim();
                data5[1]=data5[1].trim();

                //verificar se o ID e se o valor inseridos são ambos numeros
                try{  //verificação do ID
                    Integer.parseInt(data5[0]);
                }catch (NumberFormatException e){
                    str_return = "O ID inserido não é um número";
                    break;
                }
                try{ //verifição do valor
                    Integer.parseInt(data5[1]);
                }catch (NumberFormatException e){
                    str_return = "O valor inserido inserido não é um número";
                    break;
                }

                //retorna o resultset com o nome do artista que tenha o id inserido
                ResultSet artist=statement.executeQuery("SELECT name FROM artists WHERE artistID="+data5[0]);

                while (artist.next()){//repete enquanto houver valores para ler no result set
                    //adiciona os valores inseridos á base de dados
                    statement.executeUpdate("INSERT INTO donations VALUES(DEFAULT, " + data5[0] + ",'" + data5[1]+"')");
                    str_return="Doação enviada com sucesso - artista:"+data5[0]+" | valor:"+data5[1];
                    break;
                }
                if(str_return.length()==0){
                    str_return = "Artista não encontrado";
                }

                break;
            case 6: //Listar doações por artista

                // verificar se data é um numero
                try {
                    Integer.parseInt(data);
                }catch(NumberFormatException e){
                    str_return = "Os ID de artista têm de ser número inteiros. Não foi inserido um numero.";
                    break;
                }

                //retorna o resultset com o valor da donation e o nome do artista que tenha o id (data) inserido
                ResultSet artist_donation=statement.executeQuery("SELECT donations.value, artists.name FROM donations, artists WHERE donations.artistID="+data+" AND artists.artistID=donations.artistID");

                //verificação de formato
                if (data.equals("") || data.length() > 1){
                    str_return = "O input inserido não está no formato correto";
                    break;
                }

                //repete enquanto houver valores para ler no result set
                while (artist_donation.next()){
                    //afetação da string de retorno para todas as donations para um determinado artista
                    str_return += "Valor: "+artist_donation.getString("value")+"\n";
                }
                if(str_return.length()>0) {
                    str_return = "\n   ----Lista de doações para o artista com o ID " + data + ":----\n" + str_return;
                }
                else{
                    str_return = "Ainda não há doações para este artista / O ID inserido não pertence a nenhum artista.";
                }
                break;
        }

        try {
            statement.close();
        }catch (Exception e){
            e.printStackTrace();
        }

        postGresCon.disconnect();
        return str_return;
    }
}
