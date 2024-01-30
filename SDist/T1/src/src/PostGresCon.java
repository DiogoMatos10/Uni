package src;

import java.sql.*;

/*********************************
 * PostGresCon
 * @author José Saias
 *********************************/

public class PostGresCon {
    private String PG_HOST, PG_DB, USER, PWD;

    Connection con = null;
    Statement stmt = null;

    public PostGresCon(String host, String db, String user, String pwd) {
        PG_HOST = host;
        PG_DB = db;
        USER = user;
        PWD = pwd;
    }


    //Métodos retirados das aulas

    // Conexão
    public void connect() throws Exception {
        try {
            Class.forName("org.postgresql.Driver");
            con = DriverManager.getConnection("jdbc:postgresql://" + PG_HOST + ":5432/" + PG_DB, USER, PWD);

            stmt = con.createStatement();

            System.out.println("Successfully Connected to DataBase");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Problems setting the connection");
        }
    }

    // Fim da conexão
    public void disconnect() {
        try {
            stmt.close();
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Retorna o statment
    public Statement getStatement() {
        return stmt;
    }
}
