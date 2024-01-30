package com.roytuts.spring.boot.security.form.based.jdbc.userdetailsservice.auth.dao;

import com.roytuts.spring.boot.security.form.based.jdbc.userdetailsservice.auth.controller.PostGresConnect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;
import org.json.*;

import com.roytuts.spring.boot.security.form.based.jdbc.userdetailsservice.auth.model.User;
import com.roytuts.spring.boot.security.form.based.jdbc.userdetailsservice.auth.model.Event;

import com.roytuts.spring.boot.security.form.based.jdbc.userdetailsservice.auth.rowmapper.UserRowMapper;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.*;
import java.net.*;
import java.io.OutputStream;

import java.text.SimpleDateFormat;


@Repository
public class Handler {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public User getUser(final String username) {
        return jdbcTemplate.queryForObject(
                "select u.user_name user_name, u.user_pass user_pass, ur.user_role user_role from users u, user_roles ur where u.user_name = ? and u.user_name = ur.user_name",
                new String[]{username}, new UserRowMapper());
    }

    public int saveUser(final User u) throws Exception {
        BCryptPasswordEncoder benc = new BCryptPasswordEncoder();
        String passwordEncoder = benc.encode(u.getPassword().subSequence(0, u.getPassword().length()));

        PostGresConnect connect = new PostGresConnect();
        connect.connect();
        Statement statement = connect.getStatement();

        ResultSet resultSet = statement.executeQuery("SELECT user_name from users where user_name = '" + u.getUsername() + "'");
        if (resultSet.next()) {
            connect.disconnect();
            return -1;
        }

        statement.executeUpdate("INSERT INTO users VALUES ('" + u.getUsername() + "', '" + passwordEncoder + "', 1)");
        statement.executeUpdate("INSERT INTO user_roles VALUES ('" + u.getUsername() + "', '" + u.getRole() + "')");

        connect.disconnect();

        return 1;

    }

    public String getRole(String username) throws Exception {
        PostGresConnect connect = new PostGresConnect();
        connect.connect();
        Statement statement = connect.getStatement();
        ResultSet resultSet = statement.executeQuery("SELECT user_role from user_roles where user_name = '" + username + "'");
        resultSet.next();
        return resultSet.getString("user_role");
    }

    public int saveEvent(final Event e) throws Exception {
        PostGresConnect connect = new PostGresConnect();
        connect.connect();
        Statement statement = connect.getStatement();

        ResultSet resultSet = statement.executeQuery("SELECT name from events where name = '" + e.getName() + "'");
        if (resultSet.next()) {
            connect.disconnect();
            return -1;
        }
        statement.executeUpdate("INSERT INTO events VALUES ('" + e.getName() + "', '" + e.getDescription() + "', '" + e.getDate() + "', " + e.getValue() + ")");
        connect.disconnect();
        return 1;
    }

    public JSONObject saveInscricao(String eventName,String userName,String participant_name,String gender,String tier) throws Exception {
        PostGresConnect connect = new PostGresConnect();
        connect.connect();
        Statement statement = connect.getStatement();

        ResultSet resultSet=statement.executeQuery("SELECT name from events where name='"+eventName+"'");
        if(!resultSet.next()){
            connect.disconnect();
            return null;
        }

        resultSet=statement.executeQuery("SELECT participant_name from registrations where participant_name='"+participant_name+"' and event_name='"+eventName+"'");
        if(resultSet.next()){
            connect.disconnect();
            return null;
        }

        Double value=null;
        JSONObject jsonObject=new JSONObject();

        resultSet = statement.executeQuery("SELECT name,value FROM events WHERE name = '" + eventName + "'");

        if(resultSet.next()){
            value=resultSet.getDouble("value");
            jsonObject=getReferenceAndEntity(String.valueOf(value));
        }
         
        statement.executeUpdate("INSERT INTO registrations VALUES ('" + eventName + "', '" + userName + "', '" +participant_name+"', '"+ gender + "', '" + tier + "', '" + jsonObject.getString("mb_reference") +"', '"+jsonObject.getString("mb_entity")+ "' , 'pending')");

        JSONObject json=new JSONObject();
        json.put("reference", jsonObject.getString("mb_reference"));
        json.put("entity",jsonObject.getString("mb_entity"));
        json.put("value", Double.toString(value));

        connect.disconnect();
        return json;
    }


    public JSONObject getReferenceAndEntity(String amount) throws Exception {
        try {
            URL url = new URL("https://magno.di.uevora.pt/tweb/t2/mbref4payment");
            HttpsURLConnection connect = (HttpsURLConnection) url.openConnection();
            connect.setRequestMethod("POST");
            connect.setDoOutput(true);

            String postData = "amount=" + URLEncoder.encode(amount, "UTF-8");
    
            try (OutputStream os = connect.getOutputStream()) {
                byte[] input = postData.getBytes("UTF-8");
                os.write(input, 0, input.length);
            }

            StringBuilder res = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connect.getInputStream()))) {
                for (String line; (line = reader.readLine()) != null; ) {
                    res.append(line);
                }
            }

            JSONObject json = new JSONObject(res.toString());
            JSONObject jsonObject=new JSONObject();
            jsonObject.put("mb_reference",json.getString("mb_reference"));
            jsonObject.put("mb_entity",json.getString("mb_entity"));

            connect.disconnect();
            return jsonObject;
        } catch (Exception e) {
            return null;
        }
    }
    
    public JSONObject getPaymentInfo(String eventName, String participantName) throws Exception{
        PostGresConnect connect = new PostGresConnect();
        connect.connect();
        Statement statement = connect.getStatement();

       String sql = "SELECT r.payment_reference, r.payment_entity, e.value " +
               "FROM registrations r INNER JOIN events e ON r.event_name = e.name "+
               "WHERE r.participant_name='"+participantName+"' AND r.event_name='"+eventName+"'";

        ResultSet resultSet = statement.executeQuery(sql);
        JSONObject info=new JSONObject();
        while (resultSet.next()){
            info=new JSONObject();
            info.put("mb_reference",resultSet.getString("payment_reference"));
            info.put("mb_entity",resultSet.getString("payment_entity"));
            info.put("value",resultSet.getString("value"));
        }

        connect.disconnect();
        return info;
    }

    public void confirmPay(String eventName,String participantName)throws Exception{
        PostGresConnect connect = new PostGresConnect();
        connect.connect();
        Statement statement = connect.getStatement();

        String sql="UPDATE registrations SET status='confirm' WHERE participant_name='"+participantName +"' AND event_name='"+eventName+"'";
        statement.executeUpdate(sql);

        connect.disconnect();
    }

    public JSONArray getEvents(String eventType, String inputName, String inputDate) throws Exception {
        PostGresConnect connect = new PostGresConnect();
        connect.connect();
        Statement statement = connect.getStatement();

        Date currentDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        String sql = "";

        switch (eventType.toLowerCase()) {
            case "live":
                sql = "SELECT name, date, value, description FROM events WHERE DATE(date)='" + dateFormat.format(currentDate) + "'";
                break;
            case "previous":
                sql = "SELECT name, date, value, description FROM events WHERE date<'" + dateFormat.format(currentDate) + "'";
                break;
            case "future":
                sql = "SELECT name, date, value, description FROM events WHERE date>'" + dateFormat.format(currentDate) + "'";
                break;
            case "tojoin":
                sql = "SELECT name, date, value, description FROM events WHERE date>='" + dateFormat.format(currentDate) + "'";
                break;
            case "all":
                sql="SELECT name, date, value, description FROM events";
                break;
            default:
        }

        ResultSet resultSet = statement.executeQuery(sql);

        JSONArray events = new JSONArray();

        while (resultSet.next()) {
            JSONObject event = new JSONObject();
            event.put("event", resultSet.getString("name"));
            event.put("price", resultSet.getString("value"));
            event.put("date", resultSet.getString("date"));
            event.put("description", resultSet.getString("description"));
            events.put(event);
        }

        JSONArray filteredEvents = new JSONArray();
        for (int i = 0; i < events.length(); i++) {
            JSONObject event = events.getJSONObject(i);

            if ((inputName == null || event.getString("event").toLowerCase().contains(inputName.toLowerCase())) &&
                    (inputDate == null || event.getString("date").split(" ")[0].equals(inputDate))) {
                filteredEvents.put(event);
            }
        }

        connect.disconnect();
        return filteredEvents;
    }

    public JSONArray getInscricoes(String userName) throws Exception {
        PostGresConnect connect = new PostGresConnect();
        connect.connect();
        Statement statement = connect.getStatement();

        ResultSet resultSet=statement.executeQuery("SELECT r.user_name, r.event_name, r.participant_name,r.payment_reference,r.payment_entity, r.status, e.value, e.date " +
        "FROM registrations r " +
        "INNER JOIN events e ON r.event_name = e.name " +
        "WHERE r.user_name = '" + userName + "'");

        JSONArray inscricoes = new JSONArray();

        while (resultSet.next()) {
            JSONObject inscricao = new JSONObject();
            inscricao.put("participant_name", resultSet.getString("participant_name"));
            inscricao.put("event_name", resultSet.getString("event_name"));
            inscricao.put("payment_reference", resultSet.getString("payment_reference"));
            inscricao.put("payment_entity", resultSet.getString("payment_entity"));
            inscricao.put("value", resultSet.getString("value"));
            inscricao.put("date", resultSet.getString("date"));
            inscricao.put("status", resultSet.getString("status"));
            inscricoes.put(inscricao);
        }

        connect.disconnect();
        return inscricoes;
    }


    public int saveTime(String eventName, String name, String number, String checkpoint, Timestamp timestamp) throws Exception {
        PostGresConnect connect=new PostGresConnect();
        connect.connect();
        Statement statement=connect.getStatement();


        ResultSet resultSet = statement.executeQuery("SELECT date FROM events WHERE name='"+eventName+"'");
        if(resultSet.next()){
            Timestamp eventDate=resultSet.getTimestamp("date");
            if(timestamp.before(eventDate)){
                connect.disconnect();
                return -2; // o timestamp inserido é menor que o date de começo do evento
            }
        }

        resultSet=statement.executeQuery("SELECT participant_name FROM registrations WHERE participant_name='"+name+"' AND event_name='"+eventName+"'");
        if (!resultSet.next()) {
            connect.disconnect();
            return -1; // o nome do participante não existe nas inscrições do evento
        }

        resultSet = statement.executeQuery("SELECT participant_name FROM participant_times WHERE participant_name='"+eventName+"' AND participant_number='"+number+ "'");
        if(resultSet.next()){
            if(!resultSet.getString("participant_name").equals("name") && resultSet.getString("participant_name")!=null){
                connect.disconnect();
                return -3; // o nº de participante inserido não condiz com o nome do participante
            }
        }

        //Update do tempo de um participante
        resultSet = statement.executeQuery("SELECT participant_name FROM participant_times WHERE participant_name='"+name+"'");
        if(resultSet.next()){
            String sql="UPDATE participant_times SET checkpoint='"+checkpoint+"' , timestamp='"+timestamp+"' WHERE participant_name='"+name +"' AND event_name='"+eventName+"'";
            statement.executeUpdate(sql);
            return 2;
        }

        statement.executeUpdate("INSERT INTO participant_times VALUES ('"+eventName+"', '"+name+"', '"+number+"', '"+checkpoint+"', '"+timestamp+"')");

        connect.disconnect();
        return 1;
    }

    public JSONArray getRegistrationsAndClassifications(String eventName,String participant_name, String tier) throws Exception {
        PostGresConnect connect = new PostGresConnect();
        connect.connect();
        Statement statement = connect.getStatement();


        ResultSet resultSet = statement.executeQuery(
                "SELECT r.participant_name,r.tier,r.gender, pt.participant_number, pt.checkpoint, pt.timestamp, e.date, r.status " +
                        "FROM registrations r " +
                        "LEFT JOIN events e ON r.event_name = e.name " +
                        "LEFT JOIN participant_times pt ON r.participant_name = pt.participant_name AND r.event_name = pt.event_name " +
                        "WHERE r.event_name = '" + eventName + "'");


        JSONArray participants = new JSONArray();
        while (resultSet.next()){
            JSONObject participant = new JSONObject();
            participant.put("participant_name", resultSet.getString("participant_name"));
            if(resultSet.getString("participant_number")!=null){
                participant.put("participant_number", resultSet.getString("participant_number"));
            }
            if(resultSet.getString("checkpoint")!=null){
                participant.put("checkpoint", resultSet.getString("checkpoint"));
            }
            if(resultSet.getString("timestamp")!=null){
                participant.put("timestampFinal", resultSet.getString("timestamp"));
            }
            participant.put("timestampStart",resultSet.getString("date"));
            participant.put("status",resultSet.getString("status"));
            participant.put("tier",resultSet.getString("tier"));
            participant.put("gender",resultSet.getString("gender"));
            participants.put(participant);
        }

        JSONArray filteredParticipants = new JSONArray();
        for (int i = 0; i < participants.length(); i++) {
            JSONObject participantsJSONObject = participants.getJSONObject(i);

            if ((participant_name == null || participantsJSONObject.getString("participant_name").toLowerCase().contains(participant_name.toLowerCase())) &&
                    (tier == null || participantsJSONObject.getString("tier").equals(tier))) {
                filteredParticipants.put(participantsJSONObject);
            }
        }

        connect.disconnect();
        return filteredParticipants;
    }

}
