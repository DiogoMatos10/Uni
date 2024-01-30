package client;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/*********************************
 * Client
 * @author Henrique Rosa - l51923
 * @author Diogo Matos --- l54466
 *********************************/

public class Client {
    private static final String API_URL="http://localhost:8080/api";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String role;
        String op="";
        String [] split;
        while (true) {
            System.out.println("SeekArtist - Login");
            System.out.println("Registo de conta --- 1");
            System.out.println("Iniciar sessão ----- 2");
            System.out.println("Exit --------------- 0");
            int auth = scanner.nextInt();

            if (auth == 1 || auth == 2) {
                split=menuAuth(auth).split(",");
                break;
            } else if (auth==0) {
                System.out.println("Adeus!");
                System.exit(0);
            } else {
                System.out.println("Ação inválida.");
            }
        }
        if(split[0].equals("administrador") && split[2].equals("aprovado")){
            while (true){
                System.out.println("SEEK ARTIST - Olá "+split[3]+"!");
                System.out.println("\nMenu Administrador");
                System.out.println("Listar artistas por aprovação ----------- a");
                System.out.println("Aprovar artista ------------------------- b");
                System.out.println("Dar permissão de Admin ------------------ c");
                System.out.println("Consultar informação de um artista ------ d");
                System.out.println("Alterar informação de um artista -------- e");
                System.out.println("\nMenu normal");
                System.out.println("Registar Novo artista ------------------- 1");
                System.out.println("Listar artistas ------------------------- 2");
                System.out.println("Listar locais de espetáculo a decorrer -- 3");
                System.out.println("Listar espétaculos já feitos por artista  4");
                System.out.println("Listar próximos espétaculos por artista - 5");
                System.out.println("Doar a um artista ----------------------- 6");
                System.out.println("Listar doações por artista -------------- 7");
                System.out.println("Dar classificação a artista ------------- 8");
                System.out.println("Exit ------------------------------------ .");
                op=scanner.next();
                if(op.equals(".")){
                    System.out.println("Adeus!");
                    System.exit(0);
                }
                opMenu(op,split[1]);
            }
        }else{
            while (true){
                System.out.println("SEEK ARTIST - Olá "+split[3]+"!");
                if(split[0].equals("administrador")){
                    System.out.println("ADMIN AINDA NÃO APROVADO");
                }
                System.out.println("\nMenu Normal");
                System.out.println("Registar Novo artista ------------------- 1");
                System.out.println("Listar artistas ------------------------- 2");
                System.out.println("Listar locais de espetáculo a decorrer -- 3");
                System.out.println("Listar espétaculos já feitos por artista  4");
                System.out.println("Listar próximos espétaculos por artista - 5");
                System.out.println("Doar a um artista ----------------------- 6");
                System.out.println("Listar doações por artista -------------- 7");
                System.out.println("Dar classificação a artista ------------- 8");
                System.out.println("Exit ------------------------------------ .");
                op=scanner.next();
                if(op.equals(".")){
                    System.out.println("Adeus!");
                    System.exit(0);
                }
                opMenu(op,split[1]);
            }
        }
    }
    public static String menuAuth(int auth){
        Scanner scanner =new Scanner(System.in);
        String username,email,password, role;
        switch (auth){
            case 1:
                while (true){
                    System.out.println("Registo de conta");
                    System.out.println("Username:");
                    username=scanner.nextLine().trim();
                    if(!username.equals("")){
                        System.out.println("Email:");
                        email=scanner.nextLine().trim();
                        if(!email.equals("")){
                            System.out.println("Password:");
                            password=scanner.nextLine().trim();
                            if(!password.equals("")){
                                while (true){
                                    System.out.println("Role:");
                                    role=scanner.nextLine().trim();
                                    if(role.equals("administrador") || role.equals("normal")){
                                        return newUser(username,email,password,role);
                                    }
                                    System.out.println("Insira \"administrador\" ou \"normal\".");
                                }
                            }
                        }
                    }
                }
            case 2:
                while (true){
                    System.out.println("Login de conta");
                    System.out.println("Username:");
                    username=scanner.nextLine().trim();
                    System.out.println("Password:");
                    password=scanner.nextLine().trim();

                    String login=login(username,password);
                    if(login!=null){
                        return login;
                    }
                    System.out.println("Credenciais incorretas.");
                }
            default:
                System.out.println("Ação inválida.");
        }
        return "";
    }
    public static void opMenu(String op,String userID){
        Scanner scan=new Scanner(System.in);
        String name;
        switch (op){
            case "a":
                System.out.println(listArtistsWithStatus());
                break;
            case "b":
                System.out.println("Insira um artistID para aprovar");
                String id = scan.next();
                System.out.println(approveArtist(id));
                break;
            case "c":
                System.out.println("Insira um username de admin para aprovar");
                name=scan.nextLine();
                System.out.println(approveAdmin(name));
                break;
            case "d":
                System.out.println("Insira o nome do artista para visualizar informação");
                name=scan.nextLine();
                System.out.println(infoArtist(name));
                break;
            case "e":
                System.out.println("Insira os valores no seguinte formato para atualizar o artista, [id,nome, tipo de arte, latitude, longitude]\nCaso não seja necessario algum (ou ambos) dos parametros, por favor digite [\"_\"] no seu lugar. (AVISO: insira sempre o id do artista)");
                String update=scan.nextLine();
                System.out.println(updateArtist(update));
                break;
            case "1":
                System.out.println("Insira a informação do artista a inserir, no formato [nome , tipo de arte, latitude ,longitude]");
                String artist = scan.nextLine();
                System.out.println("\n"+insertArtist(artist.toLowerCase()));
                break;
            case "2":
                System.out.println("Inserir termos para a filtragem da lista, no formato [tipo de arte , latitude, longitude]\nCaso não seja necessario algum (ou ambos) dos filtros, por favor digite [\"_\"] no seu lugar");
                String filters = scan.nextLine();
                System.out.println("\n"+listArtists(filters.toLowerCase()));
                break;
            case "3":
                System.out.println("\n"+listPerformancesLive());
                break;
            case "4":
                System.out.println("Insira um nome de artista para visualizar espétaculos já feitos");
                name=scan.nextLine();
                System.out.println(listPerformancesPrevious(name));
                break;
            case "5":
                System.out.println("Insira um nome de artista para visualizar espétaculos já feitos");
                name=scan.nextLine();
                System.out.println(listPerformancesFuture(name));
                break;
            case "6":
                System.out.println("Insira o nome do artista a quem quer doar e o seu valor, no formato [ nome , valor ]");
                String donation=scan.nextLine();
                System.out.println(donateArtist(donation,userID));
                break;
            case "7":
                System.out.println("Insira o nome do artista");
                name=scan.nextLine();
                System.out.println(listDonations(name));
                break;
            case "8":
                System.out.println("Insira o nome do artista a quem quer dar uma classificação e a sua classificação de 0 a 10, no formato [ nome , classificação ]");
                String rating=scan.nextLine();
                System.out.println(ratingArtist(rating));
                break;
            default:
                System.out.println("Ação inválida");
        }
    }
    public static String newUser(String name, String email, String pwd, String role) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(API_URL + "/newUser").openConnection();

            connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);


            String jsonInputString = "{\"name\":\"" + name.trim() + "\",\"email\":\"" + email.trim() + "\",\"pwd\":\"" + pwd.trim() + "\",\"role\":\"" + role.trim() + "\"}";

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            StringBuilder response = new StringBuilder();
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                return response.toString();
            } finally {
                connection.disconnect();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
    public static String login(String name, String password){
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(API_URL + "/login?name=" + replaceSpaces(name.trim())+"&&password="+replaceSpaces(password.trim())).openConnection();
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String response = in.readLine();

            in.close();
            return response;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
    public static String insertArtist(String artist){
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(API_URL + "/insertArtist").openConnection();

            connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);

            String [] data =artist.split(",");
            if (artist.equals("") || data.length < 4){
                return "O input inserido não está no formato correto.";
            }

            String jsonInputString = "{\"name\":\"" + data[0].trim() + "\",\"typeArt\":\"" + data[1].toString().trim() + "\",\"locationLatitude\":\"" + data[2].toString().trim() + "\",\"locationLongitude\":\"" + data[3].toString().trim() +"\"}";

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            StringBuilder response = new StringBuilder();
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                return response.toString();
            } finally {
                connection.disconnect();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String infoArtist(String artist){
        try {
            if(artist==null){
                return "Nome inválido.";
            }

            HttpURLConnection connection = (HttpURLConnection) new URL(API_URL + "/infoArtist?artistName="+replaceSpaces(artist.trim())).openConnection();
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String response = in.readLine();

            in.close();
            return response;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
    public static String updateArtist(String update){
        try {
            String [] data=update.split(",");

            if (update.equals("") || data.length<5 || data[0].trim().equals("_") || data[0]==null){
                return "O input inserido não está no formato correto";
            }
            HttpURLConnection connection = (HttpURLConnection) new URL(API_URL + "/updateArtist?artistID="+data[0].trim()+"&&name="+replaceSpaces(data[1].trim())+"&&typeArt=" + replaceSpaces(data[2].trim())+"&&locationLatitude="+replaceSpaces(data[3].trim())+"&&locationLongitude="+replaceSpaces(data[4].trim())).openConnection();
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestMethod("GET");

            StringBuilder response = new StringBuilder();
            try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                String line;
                while ((line = in.readLine()) != null) {
                    response.append(line).append("\n");
                }
            }

            return response.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
    public static String listArtists(String filters){
        try {
            String [] data=filters.split(",");

            if (filters.equals("") || data.length<3){
                return "O input inserido não está no formato correto";
            }
            HttpURLConnection connection = (HttpURLConnection) new URL(API_URL + "/listArtists?typeArt="+replaceSpaces(data[0].trim())+"&&locationLatitude="+replaceSpaces(data[1].trim())+"&&locationLongitude="+replaceSpaces(data[2].trim())).openConnection();
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestMethod("GET");

            StringBuilder response = new StringBuilder();
            try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                String line;
                while ((line = in.readLine()) != null) {
                    response.append(line).append("\n");
                }
            }

            return response.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
    public static String listArtistsWithStatus(){
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(API_URL + "/listArtistsWithStatus").openConnection();
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestMethod("GET");

            StringBuilder response = new StringBuilder();
            try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                String line;
                while ((line = in.readLine()) != null) {
                    response.append(line).append("\n");
                }
            }

            return response.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
    public static boolean isInteger(String s){
        try {
            Integer.parseInt(s);
            return true;
        }catch (NumberFormatException e){
            return false;
        }
    }
    public static String replaceSpaces(String input){
        return input.replaceAll(" ","%20");
    }

    public static String approveArtist(String id){
        try {
            if(id==null || id.isEmpty() || !isInteger(id)){
                return "ID de artista inválido";
            }

            HttpURLConnection connection = (HttpURLConnection) new URL(API_URL + "/approveArtist?artistID="+id.trim()).openConnection();
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String response = in.readLine();

            in.close();
            return response;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
    public static String approveAdmin(String username){
        try {
            if(username==null){
                return "Nome de artista inválido";
            }

            HttpURLConnection connection = (HttpURLConnection) new URL(API_URL + "/approveAdmin?username="+replaceSpaces(username.trim())).openConnection();
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String response = in.readLine();

            in.close();
            return response;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
    public static String listPerformancesLive(){
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(API_URL + "/listPerformancesLive").openConnection();
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestMethod("GET");

            StringBuilder response = new StringBuilder();
            try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                String line;
                while ((line = in.readLine()) != null) {
                    response.append(line).append("\n");
                }
            }

            if(response.toString().equals("Lista de espéculos a decorrer")){
                return "Não há espétaculos a decorrer";
            }

            return response.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
    public static String listPerformancesPrevious(String name){
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(API_URL + "/listPerformancesPrevious?name="+replaceSpaces(name.trim())).openConnection();
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestMethod("GET");

            if(name==null || name.isEmpty()){
                return "Nome de artista inválido";
            }

            StringBuilder response = new StringBuilder();
            try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                String line;
                while ((line = in.readLine()) != null) {
                    response.append(line).append("\n");
                }
            }
            if(response.toString().equals("Lista de espéculos já feitos por "+name)){
                return "Não há espétaculos a decorrer";
            }

            return response.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
    public static String listPerformancesFuture(String name){
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(API_URL + "/listPerformancesFuture?name="+replaceSpaces(name.trim())).openConnection();
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestMethod("GET");

            if(name==null || name.isEmpty()){
                return "Nome de artista inválido";
            }

            StringBuilder response = new StringBuilder();
            try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                String line;
                while ((line = in.readLine()) != null) {
                    response.append(line).append("\n");
                }
            }
            if(response.toString().equals("Lista de próximos espetáculos de "+name)){
                return "Não há espétaculos marcados.";
            }

            return response.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
    public static String donateArtist(String donation,String userID){
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(API_URL + "/donateArtist").openConnection();
            connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);

            String [] data=donation.split(",");

            if(data.length<2 || !isInteger(data[1].trim())){
                return "O input inserido não está no formato correto";
            }

            String jsonInputString = "{\"userID\":\"" + userID.trim() + "\",\"artistName\":\"" + data[0].trim() + "\",\"value\":\"" + data[1].trim() + "\"}";

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            StringBuilder response = new StringBuilder();
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                return response.toString();
            } finally {
                connection.disconnect();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
    public static String listDonations(String name){
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(API_URL + "/listDonations?name="+replaceSpaces(name.trim())).openConnection();
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestMethod("GET");

            if(name==null || name.isEmpty()){
                return "Nome de artista inválido";
            }

            StringBuilder response = new StringBuilder();
            try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                String line;
                while ((line = in.readLine()) != null) {
                    response.append(line).append("\n");
                }
            }

            return response.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
    public static String ratingArtist(String rating){
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(API_URL + "/ratingArtist").openConnection();
            connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);

            String [] data=rating.split(",");

            if(rating==null || Integer.parseInt(data[1].trim())<0 || Integer.parseInt(data[1].trim())>10 || data.length<2){
                return "Erro. A classificação deve ser de 0 a 10.";
            }

            String jsonInputString = "{\"name\":\"" + data[0].trim() + "\",\"rating\":\"" + data[1].toString().trim() + "\"}";

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            StringBuilder response = new StringBuilder();
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                return response.toString();
            } finally {
                connection.disconnect();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}