package com.roytuts.spring.boot.security.form.based.jdbc.userdetailsservice.auth.controller;

import com.roytuts.spring.boot.security.form.based.jdbc.userdetailsservice.auth.dao.Handler;
import com.roytuts.spring.boot.security.form.based.jdbc.userdetailsservice.auth.model.User;
import com.roytuts.spring.boot.security.form.based.jdbc.userdetailsservice.auth.model.Event;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.json.JSONArray;
import org.json.JSONObject;


import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;


@Controller
public class SpringSecurityController {

    @Autowired
    private Handler handler;

    @GetMapping("/")
    public String defaultPage(String type,String name,@DateTimeFormat(pattern = "yyyy-MM-dd") String date,String participant_name, String tier,@RequestParam(defaultValue = "1") int page,
                              @RequestParam(defaultValue = "4") int pageSize, String eventName,Model model, HttpServletRequest request) throws Exception {
        if (request.getUserPrincipal() == null) {
            model.addAttribute("options","<div class=\"top-start\">" +
                                    "<a href=\"/\" class=\"title\">Runit</a>"+
                                "</div>"+
                                "<div class=\"top-end\">" +
                                    "<ul class=\"nav-wrapper\">"+
                                        "<li class=\"nav-item\"><a href=\"/login\">Entrar</a></li>"+
                                        "<li class=\"nav-item\"><a href=\"/newuser\">Registar</a></li>"+
                                    "</ul>"+
                                "</div>");

            fetchAndDisplayEvents(type, name, date, model,page,pageSize);
            fetchAndDisplayParticipants(tier,participant_name,eventName, model);


        } else {
            String username = request.getRemoteUser();
            String role = handler.getRole(username);

            if (role.equals("STAFF")) {
                model.addAttribute("options","<div class=\"top-start\">" +
                        "<a href=\"/\" class=\"title\">Runit</a>"+
                        "</div>"+
                        "<div class=\"top-mid\">" +
                        "<ul class=\"nav-wrapper\">"+
                        "<li class=\"nav-item\"><a href=\"/newevent\">Registar evento</a></li>"+
                        "<li> | </li>"+
                        "<li class=\"nav-item\"><a href=\"/registertime\">Registar tempos</a></li>"+
                        "</ul>"+
                        "</div>"+
                        "<div class=\"top-end\">" +
                        "Olá "+username+", <a href=\"/logout\" class=\"nav-wrapper nav-item\">Sair</a>"+
                        "</div>");


                fetchAndDisplayEvents(type, name, date, model,page, pageSize);
                fetchAndDisplayParticipants(tier,participant_name,eventName, model);

            } else {
                model.addAttribute("options","<div class=\"top-start\">" +
                        "<a href=\"/\" class=\"title\">Runit</a>"+
                        "</div>"+
                        "<div class=\"top-mid\">" +
                        "<ul class=\"nav-wrapper\">"+
                        "<li class=\"nav-item\"><a href=\"/newinscricao\">Inscrever num evento</a></li>"+
                        "<li> | </li>"+
                        "<li class=\"nav-item\"><a href=\"/inscricoes\">Inscrições</a></li>"+
                        "</ul>"+
                        "</div>"+
                        "<div class=\"top-end\">" +
                        "Olá "+username+", <a href=\"/logout\" class=\"nav-wrapper nav-item\">Sair</a>"+
                        "</div>");

                fetchAndDisplayEvents(type, name, date, model,page,pageSize);
                fetchAndDisplayParticipants(tier,participant_name,eventName, model);
            }
        }
        return "index";
    }


    public void fetchAndDisplayEvents(String type, String name, @DateTimeFormat(pattern = "yyyy-MM-dd") String date, Model model,Integer page,Integer pageSize) throws Exception {

        if(type==null){
            type="live";
        }
        if(date!=null && date.isEmpty()){
            date=null;
        }
        if(name!=null && name.isEmpty()){
            name=null;
        }


        JSONArray events;

        if(name!=null || date!=null){
            events=handler.getEvents(type,name,date);
        }else{
            events=handler.getEvents(type,null,null);
        }

        StringBuilder builder = new StringBuilder();

        if(type.equals("live")){
            model.addAttribute("type","Live Events");
        } else if (type.equals("previous")) {
            model.addAttribute("type","Previous Events");
        } else if (type.equals("future")) {
            model.addAttribute("type","Future Events");
        }

        if (events.isEmpty()) {
            builder.append("<p class=\"empty\">Não há eventos disponiveis</p>");
        } else {
            builder.append("<div class=\"table\">");
            builder.append("<table>"
                    + "<tr class=\"thead\">"
                    + "<th>Nome do evento</th>"
                    + "<th>Descrição</th>"
                    + "<th>Data</th>"
                    + "<th>Preço</th>"
                    + "<th></th>"
                    + "</tr>");

            int startIndex = (page - 1) * pageSize;
            int endIndex = Math.min(startIndex + pageSize, events.length());
            for (int i = startIndex; i < endIndex; i++) {
                JSONObject event = events.getJSONObject(i);

                String dateWithoutHour = event.getString("date").substring(0, event.getString("date").indexOf(" "));

                builder.append("<tr class=\"tbody\">"
                                + "<td>").append(event.getString("event")).append("</td>"
                                + "<td>").append(event.getString("description")).append("</td>"
                                + "<td>").append(dateWithoutHour).append("</td>"
                                + "<td>").append(event.getString("price")).append("&euro;").append("</td>"
                                + "<td>").append("<form class=\"table-button\" action=\"/\" method=\"GET\">"
                                + "<input type=\"hidden\" name=\"type\" value=\"").append(type).append("\">"
                                + "<input type=\"hidden\" name=\"eventName\" value=\"").append(event.getString("event"))
                        .append("\"><button class=\"button-2\">Ver classificações</button></form>").append("</td>"
                                + "</tr>");

            }
            builder.append("</table></div>");
            int totalPages = (int) Math.ceil((double) events.length() / pageSize);
            if(totalPages>1){
                model.addAttribute("pagination", buildPagination(name,date,page, totalPages, type));
            }
        }
        model.addAttribute("events", builder.toString());
    }

    public String buildPagination(String eventName,String date,int currentPage, int totalPages, String type) {
        StringBuilder pagination= new StringBuilder();


        pagination.append("<div class=\"pagination-events\">");
        for (int i = 1; i <= totalPages; i++) {
            if (i == currentPage) {
                pagination.append("<p class=\"current\">").append(i).append("</p>");
            } else {
                pagination.append("<a href=\"/?page=").append(i).append("&type=").append(type).append("&name=").append(eventName).append("&date=").append(date).append("\">").append(i).append("</a>");
            }
        }
        pagination.append("</div>");

        return pagination.toString();
    }

    private String calcDuration(String timestampStart, String timestampFinal){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        LocalDateTime startDateTime = LocalDateTime.parse(timestampStart, formatter);
        LocalDateTime finalDateTime = LocalDateTime.parse(timestampFinal, formatter);

        Duration duration = Duration.between(startDateTime, finalDateTime);

        long days = duration.toDays();
        long hours = duration.toHoursPart();
        long minutes = duration.toMinutesPart();
        long seconds = duration.toSecondsPart();

        return String.format("%d-%02d:%02d:%02d", days, hours, minutes, seconds);
    }

    public void fetchAndDisplayParticipants(String tier,String participant_name,String eventName,Model model) throws Exception {

        StringBuilder builder=new StringBuilder();
        JSONArray participants=new JSONArray();

        if(tier!=null || participant_name!=null){
            participants=handler.getRegistrationsAndClassifications(eventName,participant_name,tier);
        }else{
            participants=handler.getRegistrationsAndClassifications(eventName,null,null);
        }

        if(eventName=="" || eventName==null){
            model.addAttribute("event","Classificação de um evento");
            builder.append("<p id=\"empty\">Clique em \"Ver classificações\" para visualizar as classificações de um evento</p>");
        }else{
            model.addAttribute("event","Classificações em "+eventName);
            if (participants.isEmpty()) {
                builder.append("<p id=\"empty\">Não há atletas inscritos neste evento</p>");
            } else {
                builder.append("<div class=\"participants_list\">");
                builder.append("<table>"
                        + "<tr class=\"thead\">"
                        + "<th>Nome do Participante</th>"
                        + "<th>Género</th>"
                        + "<th>Tier</th>"
                        + "<th>Status</th>"
                        + "<th>Dorsal</th>"
                        + "<th>Timestamp de Start</th>"
                        + "<th>Timestamp de Fim</th>"
                        + "<th>Tempo feito</th>"
                        + "<th>Checkpoint</th>"
                        + "<th></th>"
                        + "</tr>");


                for (int i = 0; i < participants.length(); i++) {
                    JSONObject participant = participants.getJSONObject(i);
                    String duration="X";
                    if (participant.has("timestampFinal") && !participant.isNull("timestampFinal")) {
                        duration = calcDuration(participant.getString("timestampStart"), participant.getString("timestampFinal"));
                    }
                    builder.append("<tr class=\"tbody\">");
                    builder.append("<td>").append(participant.getString("participant_name"))
                            .append("</td>");
                    builder.append("<td>").append(participant.getString("gender")).append("</td>");
                    builder.append("<td>").append(participant.getString("tier")).append("</td>");
                    builder.append("<td>").append(participant.getString("status")).append("</td>");

                    if (participant.has("participant_number") && !participant.isNull("participant_number")) {
                        builder.append("<td>").append(participant.getString("participant_number")).append("</td>");
                    } else {
                        builder.append("<td>X</td>");
                    }

                    builder.append("<td>").append(participant.getString("timestampStart")).append("</td>");

                    if (participant.has("timestampFinal") && !participant.isNull("timestampFinal")) {
                        builder.append("<td>").append(participant.getString("timestampFinal")).append("</td>");
                    } else {
                        builder.append("<td>X</td>");
                    }

                    builder.append("<td>").append(duration).append("</td>");

                    if (participant.has("checkpoint") && !participant.isNull("checkpoint")) {
                        builder.append("<td>").append(participant.getString("checkpoint")).append("</td>");
                    } else {
                        builder.append("<td>X</td>");
                    }

                    builder.append("</tr>");



                }
                builder.append("</table></div>");
            }
        }
        model.addAttribute("participants", builder.toString());
    }



    @GetMapping("/registertime")
    public String registerTime(Model model,HttpServletRequest request) throws Exception {
        String username = request.getRemoteUser();
        String role = handler.getRole(username);
        if (role.equals("STAFF")){
            model.addAttribute("options","<div class=\"top-start\">" +
                        "<a href=\"/\" class=\"title\">Runit</a>"+
                        "</div>"+
                        "<div class=\"top-mid\">" +
                        "<ul class=\"nav-wrapper\">"+
                        "<li class=\"nav-item\"><a href=\"/newevent\">Registar evento</a></li>"+
                        "<li> | </li>"+
                        "<li class=\"nav-item\"><a href=\"/registertime\">Registar tempos</a></li>"+
                        "</ul>"+
                        "</div>"+
                        "<div class=\"top-end\">" +
                        "Olá "+username+", <a href=\"/logout\" class=\"nav-wrapper nav-item\">Sair</a>"+
                        "</div>");
        }else {
            model.addAttribute("options","<div class=\"top-start\">" +
                    "<a href=\"/\" class=\"title\">Runit</a>"+
                    "</div>"+
                    "<div class=\"top-mid\">" +
                    "<ul class=\"nav-wrapper\">"+
                    "<li class=\"nav-item\"><a href=\"/newinscricao\">Inscrever num evento</a></li>"+
                    "<li> | </li>"+
                    "<li class=\"nav-item\"><a href=\"/inscricoes\">Inscrições</a></li>"+
                    "</ul>"+
                    "</div>"+
                    "<div class=\"top-end\">" +
                    "Olá "+username+", <a href=\"/logout\" class=\"nav-wrapper nav-item\">Sair</a>"+
                    "</div>");
        }

        JSONArray events=handler.getEvents("all",null,null);

        StringBuilder builder = new StringBuilder();

        if (events.isEmpty()) {
            builder.append("<p>Não há eventos disponiveis</p>");
        } else {
            builder.append("<div class=\"table\">");
            builder.append("<table>"
                    + "<tr class=\"thead\">"
                    + "<th>Nome do evento</th>"
                    + "<th>Data</th>"
                    + "<th></th>"
                    + "</tr>");

            for (int i = 0; i < events.length(); i++) {
                JSONObject event = events.getJSONObject(i);

                String dateWithoutHour = event.getString("date").substring(0, event.getString("date").indexOf(" "));

                builder.append("<tr class=\"tbody\">"
                                + "<td>").append(event.getString("event")).append("</td>"
                                + "<td>").append(dateWithoutHour).append("</td>"
                                + "<td>").append("<form class=\"table-button\" action=\"/newtime\" method=\"GET\">"
                                + "<input type=\"hidden\" name=\"eventName\" value=\"").append(event.getString("event"))
                        .append("\"><button class=\"button-2\">Registar Tempo</button></form>").append("</td>"
                                + "</tr>");
            }
            builder.append("</div>");
        }


        model.addAttribute("events", builder.toString());
        return "registertime";
    }

    @GetMapping("/newtime")
    public String newTime(@RequestParam String eventName, Model model,HttpServletRequest request) throws Exception {
        String username = request.getRemoteUser();
        String role = handler.getRole(username);
        if (role.equals("STAFF")){
            model.addAttribute("options","<div class=\"top-start\">" +
                    "<a href=\"/\" class=\"title\">Runit</a>"+
                    "</div>"+
                    "<div class=\"top-mid\">" +
                    "<ul class=\"nav-wrapper\">"+
                    "<li class=\"nav-item\"><a href=\"/newevent\">Registar evento</a></li>"+
                    "<li> | </li>"+
                    "<li class=\"nav-item\"><a href=\"/registertime\">Registar tempos</a></li>"+
                    "</ul>"+
                    "</div>"+
                    "<div class=\"top-end\">" +
                    "Olá "+username+", <a href=\"/logout\" class=\"nav-wrapper nav-item\">Sair</a>"+
                    "</div>");
        }else {
            model.addAttribute("options","<div class=\"top-start\">" +
                    "<a href=\"/\" class=\"title\">Runit</a>"+
                    "</div>"+
                    "<div class=\"top-mid\">" +
                    "<ul class=\"nav-wrapper\">"+
                    "<li class=\"nav-item\"><a href=\"/newinscricao\">Inscrever num evento</a></li>"+
                    "<li> | </li>"+
                    "<li class=\"nav-item\"><a href=\"/inscricoes\">Inscrições</a></li>"+
                    "</ul>"+
                    "</div>"+
                    "<div class=\"top-end\">" +
                    "Olá "+username+", <a href=\"/logout\" class=\"nav-wrapper nav-item\">Sair</a>"+
                    "</div>");
        }


        model.addAttribute("event", eventName);
        StringBuilder builder = new StringBuilder();
        builder.append("<form class=\"form\" method=\"GET\" action=\"/submittime\">"
                        + "<input type=\"text\" name=\"name\" placeholder=\"Nome\"><br>"
                        + "<input type=\"text\" name=\"number\" placeholder=\"Nº do participante\"><br>"
                        + "<select name=\"checkpoint\">"
                        + "<option value='' disabled selected>Ponto da prova</option>"
                        + "<option value=\"start\">Start</option>"
                        + "<option value=\"p1\">P1</option>"
                        + "<option value=\"p2\">P2</option>"
                        + "<option value=\"p3\">P3</option>"
                        + "<option value=\"finish\">Finish</option>"
                        + "</select><br>"
                        + "<input type=\"datetime-local\" name=\"checkpointDateTime\" placeholder=\"Data e Hora do Checkpoint\"><br>"
                        + "<input type=\"hidden\" name=\"eventName\" value=\"").append(eventName)
                        .append("\"><button class=\"button-2\" type=\"submit\">Submeter</button>")
                        .append("</form>");
        model.addAttribute("form", builder.toString());

        return "newtime";
    }

    @GetMapping("/submittime")
    public String submitTime(@RequestParam String eventName,
                             @RequestParam String name,
                             @RequestParam String number,
                             @RequestParam String checkpoint,
                             @RequestParam String  checkpointDateTime, Model model,HttpServletRequest request) throws Exception {

        String username = request.getRemoteUser();
        String role = handler.getRole(username);
        if (role.equals("STAFF")){
            model.addAttribute("options","<div class=\"top-start\">" +
                    "<a href=\"/\" class=\"title\">Runit</a>"+
                    "</div>"+
                    "<div class=\"top-mid\">" +
                    "<ul class=\"nav-wrapper\">"+
                    "<li class=\"nav-item\"><a href=\"/newevent\">Registar evento</a></li>"+
                    "<li> | </li>"+
                    "<li class=\"nav-item\"><a href=\"/registertime\">Registar tempos</a></li>"+
                    "</ul>"+
                    "</div>"+
                    "<div class=\"top-end\">" +
                    "Olá "+username+", <a href=\"/logout\" class=\"nav-wrapper nav-item\">Sair</a>"+
                    "</div>");
        }else {
            model.addAttribute("options","<div class=\"top-start\">" +
                    "<a href=\"/\" class=\"title\">Runit</a>"+
                    "</div>"+
                    "<div class=\"top-mid\">" +
                    "<ul class=\"nav-wrapper\">"+
                    "<li class=\"nav-item\"><a href=\"/newinscricao\">Inscrever num evento</a></li>"+
                    "<li> | </li>"+
                    "<li class=\"nav-item\"><a href=\"/inscricoes\">Inscrições</a></li>"+
                    "</ul>"+
                    "</div>"+
                    "<div class=\"top-end\">" +
                    "Olá "+username+", <a href=\"/logout\" class=\"nav-wrapper nav-item\">Sair</a>"+
                    "</div>");
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");

        if(eventName=="" || name=="" || number=="" || checkpoint=="" || checkpointDateTime==""){
            model.addAttribute("op","Erro no registo de tempo");
            model.addAttribute("reason","Valores inseridos inválidos");

            return "confirmations";
        }

        Date parsedDate = dateFormat.parse(checkpointDateTime);


        Timestamp time = new Timestamp(parsedDate.getTime());

        int res = handler.saveTime(eventName, name, number, checkpoint, time);
        if(res==1){
            model.addAttribute("op","Sucesso no Registo de Tempo");
        }
        if(res==2){
            model.addAttribute("op","Update do checkpoint e de timestamp com sucesso");
        }
        else if(res==-1){
            model.addAttribute("op","Erro no registo de tempo");
            model.addAttribute("reason","O nome do participante não existe nas inscrições do evento");
        }
        else if(res==-3){
            model.addAttribute("op","Erro no registo ou no update de tempo");
            model.addAttribute("reason","O nº de participante inserido não condiz com o nome do participante");
        }
        else if(res==-2){
            model.addAttribute("op","Erro no registo de tempo");
            model.addAttribute("reason","O timestamp inserido é menor que a data e a hora de inicio do evento");
        }

        return "confirmations";
    }



    @GetMapping("/newinscricao")
    public String newInscricao(Model model,HttpServletRequest request) throws Exception {
        String username = request.getRemoteUser();
        String role = handler.getRole(username);
        if (role.equals("STAFF")){
            model.addAttribute("options","<div class=\"top-start\">" +
                    "<a href=\"/\" class=\"title\">Runit</a>"+
                    "</div>"+
                    "<div class=\"top-mid\">" +
                    "<ul class=\"nav-wrapper\">"+
                    "<li class=\"nav-item\"><a href=\"/newevent\">Registar evento</a></li>"+
                    "<li> | </li>"+
                    "<li class=\"nav-item\"><a href=\"/registertime\">Registar tempos</a></li>"+
                    "</ul>"+
                    "</div>"+
                    "<div class=\"top-end\">" +
                    "Olá "+username+", <a href=\"/logout\" class=\"nav-wrapper nav-item\">Sair</a>"+
                    "</div>");
        }else {
            model.addAttribute("options","<div class=\"top-start\">" +
                    "<a href=\"/\" class=\"title\">Runit</a>"+
                    "</div>"+
                    "<div class=\"top-mid\">" +
                    "<ul class=\"nav-wrapper\">"+
                    "<li class=\"nav-item\"><a href=\"/newinscricao\">Inscrever num evento</a></li>"+
                    "<li> | </li>"+
                    "<li class=\"nav-item\"><a href=\"/inscricoes\">Inscrições</a></li>"+
                    "</ul>"+
                    "</div>"+
                    "<div class=\"top-end\">" +
                    "Olá "+username+", <a href=\"/logout\" class=\"nav-wrapper nav-item\">Sair</a>"+
                    "</div>");
        }

        JSONArray events = handler.getEvents("tojoin",null,null);
        StringBuilder builder = new StringBuilder();

        if (events.isEmpty()) {
            builder.append("<p>Não eventos disponiveis</p>");
        } else {
            builder.append("<div class=\"table\">");
            builder.append("<table >"
                    + "<tr class=\"thead\">"
                    + "<th>Nome do evento</th>"
                    + "<th>Descrição</th>"
                    + "<th>Data</th>"
                    + "<th>Preço</th>"
                    + "<th></th>"
                    + "</tr>");

            for (int i = 0; i < events.length(); i++) {
                JSONObject event = events.getJSONObject(i);

                String dateWithoutHour = event.getString("date").substring(0, event.getString("date").indexOf(" "));

                builder.append("<tr class=\"tbody\">"
                                + "<td>").append(event.getString("event")).append("</td>"
                                + "<td>").append(event.getString("description")).append("</td>"
                                + "<td>").append(dateWithoutHour).append("</td>"
                                + "<td>").append(event.getString("price")).append("&euro;").append("</td>"
                                + "<td>").append("<form class=\"table-button\" action=\"/joinevent\" method=\"GET\">"
                                + "<input type=\"hidden\" name=\"eventName\" value=\"").append(event.getString("event"))
                        .append("\"><button class=\"button-2\">Inscrever</button></form>").append("</td>"
                                + "</tr>");
            }
            builder.append("</div>");
        }
        model.addAttribute("events", builder.toString());
        return "newinscricao";
    }


    @GetMapping("/inscricoes")
    public String getInscricoes(Model model,HttpServletRequest request) throws Exception {
        String username = request.getRemoteUser();
        String role = handler.getRole(username);
        if (role.equals("STAFF")){
            model.addAttribute("options","<div class=\"top-start\">" +
                    "<a href=\"/\" class=\"title\">Runit</a>"+
                    "</div>"+
                    "<div class=\"top-mid\">" +
                    "<ul class=\"nav-wrapper\">"+
                    "<li class=\"nav-item\"><a href=\"/newevent\">Registar evento</a></li>"+
                    "<li> | </li>"+
                    "<li class=\"nav-item\"><a href=\"/registertime\">Registar tempos</a></li>"+
                    "</ul>"+
                    "</div>"+
                    "<div class=\"top-end\">" +
                    "Olá "+username+", <a href=\"/logout\" class=\"nav-wrapper nav-item\">Sair</a>"+
                    "</div>");
        }else {
            model.addAttribute("options","<div class=\"top-start\">" +
                    "<a href=\"/\" class=\"title\">Runit</a>"+
                    "</div>"+
                    "<div class=\"top-mid\">" +
                    "<ul class=\"nav-wrapper\">"+
                    "<li class=\"nav-item\"><a href=\"/newinscricao\">Inscrever num evento</a></li>"+
                    "<li> | </li>"+
                    "<li class=\"nav-item\"><a href=\"/inscricoes\">Inscrições</a></li>"+
                    "</ul>"+
                    "</div>"+
                    "<div class=\"top-end\">" +
                    "Olá "+username+", <a href=\"/logout\" class=\"nav-wrapper nav-item\">Sair</a>"+
                    "</div>");
        }


        JSONArray events = handler.getInscricoes(username);
        StringBuilder builder = new StringBuilder();

        if (events.isEmpty()) {
            builder.append("<p>Não há inscrições em eventos</p>");
        } else {
            builder.append("<div class=\"table\">");
        builder.append("<table>"
                + "<tr class=\"thead\">"
                + "<th>Nome do atleta     </th>"
                + "<th>Nome do evento     </th>"
                + "<th>Data do evento     </th>"
                + "<th>Estado do pagamento</th>"
                + "<th></th>"
                + "</tr>");

        for (int i = 0; i < events.length(); i++) {
            JSONObject event = events.getJSONObject(i);

            String dateWithoutHour = event.getString("date").substring(0, event.getString("date").indexOf(" "));

            builder.append("<tr class=\"tbody\">"
                    + "<td>").append(event.getString("participant_name")).append("    </td>"
                    + "<td>").append(event.getString("event_name")).append("    </td>"
                    + "<td>").append(dateWithoutHour).append("    </td>"
                    + "<td>").append(event.getString("status")).append("    </td>"
                    + "<td>").append("<form class=\"table-button\" action=\"/paymentevent\" method=\"GET\">"
                    + "<input type=\"hidden\" name=\"eventName\" value=\"").append(event.getString("event_name")).append("\">"
                    + "<input type=\"hidden\" name=\"participant_name\" value=\"").append(event.getString("participant_name")).append("\">"
                    + "<button type=\"submit\" class=\"button-2\">Pagar</button></form>").append("</td>");
            }
            builder.append("</div>");
        }
        model.addAttribute("events", builder.toString());
        return "inscricoes";
    }

    @GetMapping("/paymentevent")
    public String paymentEvent(@RequestParam String eventName,
                               @RequestParam String participant_name,
                               Model model,HttpServletRequest request) throws Exception {
        String username = request.getRemoteUser();
        String role = handler.getRole(username);
        if (role.equals("STAFF")){
            model.addAttribute("options","<div class=\"top-start\">" +
                    "<a href=\"/\" class=\"title\">Runit</a>"+
                    "</div>"+
                    "<div class=\"top-mid\">" +
                    "<ul class=\"nav-wrapper\">"+
                    "<li class=\"nav-item\"><a href=\"/newevent\">Registar evento</a></li>"+
                    "<li> | </li>"+
                    "<li class=\"nav-item\"><a href=\"/registertime\">Registar tempos</a></li>"+
                    "</ul>"+
                    "</div>"+
                    "<div class=\"top-end\">" +
                    "Olá "+username+", <a href=\"/logout\" class=\"nav-wrapper nav-item\">Sair</a>"+
                    "</div>");
        }else {
            model.addAttribute("options","<div class=\"top-start\">" +
                    "<a href=\"/\" class=\"title\">Runit</a>"+
                    "</div>"+
                    "<div class=\"top-mid\">" +
                    "<ul class=\"nav-wrapper\">"+
                    "<li class=\"nav-item\"><a href=\"/newinscricao\">Inscrever num evento</a></li>"+
                    "<li> | </li>"+
                    "<li class=\"nav-item\"><a href=\"/inscricoes\">Inscrições</a></li>"+
                    "</ul>"+
                    "</div>"+
                    "<div class=\"top-end\">" +
                    "Olá "+username+", <a href=\"/logout\" class=\"nav-wrapper nav-item\">Sair</a>"+
                    "</div>");
        }


        model.addAttribute("event",eventName);
        JSONObject res=handler.getPaymentInfo(eventName,participant_name);
        model.addAttribute("payment", "<p>" + "<strong>Referência Multibanco:</strong> " + res.getString("mb_reference") + "  |  <strong>Entidade:</strong> "+res.getString("mb_entity")+ "  |  <strong>Valor:</strong> " + res.getString("value").toString()+"&euro;</p>");
        model.addAttribute("button","<form class=\"pay-button\" action=\"/confirmpayment\" method=\"GET\">"
                + "<input type=\"hidden\" name=\"eventName\" value=\""+eventName+"\">"
                + "<input type=\"hidden\" name=\"participant_name\" value=\""+participant_name+"\">"
                + "<button type=\"submit\" class=\"button-2\">Pagar</button></form>");
        return "paymentevent";
    }

    @GetMapping("/confirmpayment")
    public String confirmPayment(@RequestParam String eventName,
                                 @RequestParam String participant_name,
                                 Model model,HttpServletRequest request) throws Exception {
        String username = request.getRemoteUser();
        String role = handler.getRole(username);
        if (role.equals("STAFF")){
            model.addAttribute("options","<div class=\"top-start\">" +
                    "<a href=\"/\" class=\"title\">Runit</a>"+
                    "</div>"+
                    "<div class=\"top-mid\">" +
                    "<ul class=\"nav-wrapper\">"+
                    "<li class=\"nav-item\"><a href=\"/newevent\">Registar evento</a></li>"+
                    "<li> | </li>"+
                    "<li class=\"nav-item\"><a href=\"/registertime\">Registar tempos</a></li>"+
                    "</ul>"+
                    "</div>"+
                    "<div class=\"top-end\">" +
                    "Olá "+username+", <a href=\"/logout\" class=\"nav-wrapper nav-item\">Sair</a>"+
                    "</div>");
        }else {
            model.addAttribute("options","<div class=\"top-start\">" +
                    "<a href=\"/\" class=\"title\">Runit</a>"+
                    "</div>"+
                    "<div class=\"top-mid\">" +
                    "<ul class=\"nav-wrapper\">"+
                    "<li class=\"nav-item\"><a href=\"/newinscricao\">Inscrever num evento</a></li>"+
                    "<li> | </li>"+
                    "<li class=\"nav-item\"><a href=\"/inscricoes\">Inscrições</a></li>"+
                    "</ul>"+
                    "</div>"+
                    "<div class=\"top-end\">" +
                    "Olá "+username+", <a href=\"/logout\" class=\"nav-wrapper nav-item\">Sair</a>"+
                    "</div>");
        }

        model.addAttribute("op","Pagamento Confirmado");
        handler.confirmPay(eventName,participant_name);
        return "confirmations";
    }

    @GetMapping("/joinevent")
    public String joinEvent(@RequestParam String eventName, Model model,HttpServletRequest request)throws Exception {
        String username = request.getRemoteUser();
        String role = handler.getRole(username);
        if (role.equals("STAFF")){
            model.addAttribute("options","<div class=\"top-start\">" +
                    "<a href=\"/\" class=\"title\">Runit</a>"+
                    "</div>"+
                    "<div class=\"top-mid\">" +
                    "<ul class=\"nav-wrapper\">"+
                    "<li class=\"nav-item\"><a href=\"/newevent\">Registar evento</a></li>"+
                    "<li> | </li>"+
                    "<li class=\"nav-item\"><a href=\"/registertime\">Registar tempos</a></li>"+
                    "</ul>"+
                    "</div>"+
                    "<div class=\"top-end\">" +
                    "Olá "+username+", <a href=\"/logout\" class=\"nav-wrapper nav-item\">Sair</a>"+
                    "</div>");
        }else {
            model.addAttribute("options","<div class=\"top-start\">" +
                    "<a href=\"/\" class=\"title\">Runit</a>"+
                    "</div>"+
                    "<div class=\"top-mid\">" +
                    "<ul class=\"nav-wrapper\">"+
                    "<li class=\"nav-item\"><a href=\"/newinscricao\">Inscrever num evento</a></li>"+
                    "<li> | </li>"+
                    "<li class=\"nav-item\"><a href=\"/inscricoes\">Inscrições</a></li>"+
                    "</ul>"+
                    "</div>"+
                    "<div class=\"top-end\">" +
                    "Olá "+username+", <a href=\"/logout\" class=\"nav-wrapper nav-item\">Sair</a>"+
                    "</div>");
        }


        model.addAttribute("event", eventName);
        StringBuilder builder = new StringBuilder();
        builder.append("<form class=\"form\" method=\"GET\" action=\"/submitinscricao\">"
                        + "<input type=\"text\" name=\"name\" placeholder=\"Nome\"><br>"
                        + "<select name=\"gender\">"
                        + "<option value='' disabled selected>Género</option>"
                        + "<option value=\"M\">M</option>"
                        + "<option value=\"F\">F</option>"
                        + "</select><br>"
                        + "<select name=\"tier\">"
                        + "<option value='' disabled selected>Escalão</option>"
                        + "<option value=\"júnior\">Júnior</option>"
                        + "<option value=\"vet35\">vet35</option>"
                        + "<option value=\"vet50\">vet50</option>"
                        + "<option value=\"vet35\">vet65</option>"
                        + "</select><br>"
                        + "<input type=\"hidden\" name=\"eventName\" value=\"").append(eventName)
                .append("\"><button type=\"submit\" class=\"button-2\">Submeter</button>")
                .append("</form>");
        model.addAttribute("form", builder.toString());

        return "joinevent";
    }

    @GetMapping("/submitinscricao")
    public String submitInscricao(@RequestParam String eventName,
                                  @RequestParam String name,
                                  @RequestParam String gender,
                                  @RequestParam String tier,
                                  Model model,HttpServletRequest request) throws Exception {
        String username = request.getRemoteUser();
        String role = handler.getRole(username);


        if (role.equals("STAFF")){
            model.addAttribute("options","<div class=\"top-start\">" +
                    "<a href=\"/\" class=\"title\">Runit</a>"+
                    "</div>"+
                    "<div class=\"top-mid\">" +
                    "<ul class=\"nav-wrapper\">"+
                    "<li class=\"nav-item\"><a href=\"/newevent\">Registar evento</a></li>"+
                    "<li> | </li>"+
                    "<li class=\"nav-item\"><a href=\"/registertime\">Registar tempos</a></li>"+
                    "</ul>"+
                    "</div>"+
                    "<div class=\"top-end\">" +
                    "Olá "+username+", <a href=\"/logout\" class=\"nav-wrapper nav-item\">Sair</a>"+
                    "</div>");
        }else {
            model.addAttribute("options","<div class=\"top-start\">" +
                    "<a href=\"/\" class=\"title\">Runit</a>"+
                    "</div>"+
                    "<div class=\"top-mid\">" +
                    "<ul class=\"nav-wrapper\">"+
                    "<li class=\"nav-item\"><a href=\"/newinscricao\">Inscrever num evento</a></li>"+
                    "<li> | </li>"+
                    "<li class=\"nav-item\"><a href=\"/inscricoes\">Inscrições</a></li>"+
                    "</ul>"+
                    "</div>"+
                    "<div class=\"top-end\">" +
                    "Olá "+username+", <a href=\"/logout\" class=\"nav-wrapper nav-item\">Sair</a>"+
                    "</div>");
        }

        JSONObject res = handler.saveInscricao(eventName, username,name, gender, tier);

        if (res != null) {
            model.addAttribute("event",eventName);
            model.addAttribute("op", "<p>Sucesso na inscrição no evento</p>");
            model.addAttribute("payment", "<p>" + "<strong>Referência Multibanco:</strong> " + res.getString("reference") + "  |  <strong>Entidade:</strong> "+res.getString("entity")+ "  |  <strong>Valor:</strong> " + res.getString("value").toString()+"&euro;"+"</p>");
        } else {
            model.addAttribute("op", "<p>Erro na inscrição</p>");
            model.addAttribute("reason", "<p>Este atleta já está inscrito neste evento</p>");
        }

        return "submitinscricao";
    }


    @GetMapping("/newevent")
    public String newEvent(Model model,HttpServletRequest request)throws Exception {
        String username = request.getRemoteUser();
        String role = handler.getRole(username);
        if (role.equals("STAFF")){
            model.addAttribute("options","<div class=\"top-start\">" +
                    "<a href=\"/\" class=\"title\">Runit</a>"+
                    "</div>"+
                    "<div class=\"top-mid\">" +
                    "<ul class=\"nav-wrapper\">"+
                    "<li class=\"nav-item\"><a href=\"/newevent\">Registar evento</a></li>"+
                    "<li> | </li>"+
                    "<li class=\"nav-item\"><a href=\"/registertime\">Registar tempos</a></li>"+
                    "</ul>"+
                    "</div>"+
                    "<div class=\"top-end\">" +
                    "Olá "+username+", <a href=\"/logout\" class=\"nav-wrapper nav-item\">Sair</a>"+
                    "</div>");
        }else {
            model.addAttribute("options","<div class=\"top-start\">" +
                    "<a href=\"/\" class=\"title\">Runit</a>"+
                    "</div>"+
                    "<div class=\"top-mid\">" +
                    "<ul class=\"nav-wrapper\">"+
                    "<li class=\"nav-item\"><a href=\"/newinscricao\">Inscrever num evento</a></li>"+
                    "<li> | </li>"+
                    "<li class=\"nav-item\"><a href=\"/inscricoes\">Inscrições</a></li>"+
                    "</ul>"+
                    "</div>"+
                    "<div class=\"top-end\">" +
                    "Olá "+username+", <a href=\"/logout\" class=\"nav-wrapper nav-item\">Sair</a>"+
                    "</div>");
        }

        return "newevent";
    }


    @GetMapping("/registerevent")
    public String registerEvent(@RequestParam String name,
                                @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime date,
                                @RequestParam String description,
                                @RequestParam String value,
                                Model model,HttpServletRequest request) throws Exception {
        String username = request.getRemoteUser();
        String role = handler.getRole(username);
        if (role.equals("STAFF")){
            model.addAttribute("options","<div class=\"top-start\">" +
                    "<a href=\"/\" class=\"title\">Runit</a>"+
                    "</div>"+
                    "<div class=\"top-mid\">" +
                    "<ul class=\"nav-wrapper\">"+
                    "<li class=\"nav-item\"><a href=\"/newevent\">Registar evento</a></li>"+
                    "<li> | </li>"+
                    "<li class=\"nav-item\"><a href=\"/registertime\">Registar tempos</a></li>"+
                    "</ul>"+
                    "</div>"+
                    "<div class=\"top-end\">" +
                    "Olá "+username+", <a href=\"/logout\" class=\"nav-wrapper nav-item\">Sair</a>"+
                    "</div>");
        }else {
            model.addAttribute("options","<div class=\"top-start\">" +
                    "<a href=\"/\" class=\"title\">Runit</a>"+
                    "</div>"+
                    "<div class=\"top-mid\">" +
                    "<ul class=\"nav-wrapper\">"+
                    "<li class=\"nav-item\"><a href=\"/newinscricao\">Inscrever num evento</a></li>"+
                    "<li> | </li>"+
                    "<li class=\"nav-item\"><a href=\"/inscricoes\">Inscrições</a></li>"+
                    "</ul>"+
                    "</div>"+
                    "<div class=\"top-end\">" +
                    "Olá "+username+", <a href=\"/logout\" class=\"nav-wrapper nav-item\">Sair</a>"+
                    "</div>");
        }

        if(name=="" || description=="" || value=="" || date==null){
            model.addAttribute("op","Erro no registo de tempo");
            model.addAttribute("reason","Valores inseridos inválidos");

            return "confirmations";
        }

        Event e = new Event(name, description, Date.from(date.atZone(ZoneId.systemDefault()).toInstant()), Double.parseDouble(value));
        int res = handler.saveEvent(e);

        if (res == 1) {
            model.addAttribute("op", "Evento registado com sucesso");
        } else {
            model.addAttribute("op", "Erro no registo do evento");
            model.addAttribute("reason", "<p>Já existe um evento com o mesmo nome</p>");
        }

        return "confirmations";
    }

    @GetMapping("/login")
    public String loginPage(Model model, @RequestParam(value = "error", required = false) String error,
                            @RequestParam(value = "logout", required = false) String logout) {
        if (error != null) {
            model.addAttribute("error", "Credenciais inválidas");
        }
        if (logout != null) {
            model.addAttribute("msg", "Logged out bem sucedido");
        }
        return "login";
    }

    @GetMapping("/logout")
    public String logoutPage(Model model, HttpServletRequest request) {
        request.getSession().invalidate();
        return "redirect:/";
    }


    @GetMapping("/newuser")
    public String newuser(Model model) {
        return "newuser";
    }

    @GetMapping("/register")
    public String register(@RequestParam String username,
                           @RequestParam String password,
                           @RequestParam String role,
                           Model model,HttpServletRequest request) throws Exception {


        User u = new User(username, password, role);
        int res = handler.saveUser(u);
        if (res == 1) {
            model.addAttribute("op", "Conta criada com sucesso, volte para o menu inicial");
            model.addAttribute("options","<div class=\"top-mid\" style=\"text-align: center; \">" +
                        "<a href=\"/\" class=\"title\" >Runit</a>"+
                        "</div>");


        } else {
            model.addAttribute("op", "Erro na criação de conta");
            model.addAttribute("reason", "<p>O username inserido já está em uso</p>");

            model.addAttribute("options","<div class=\"top-start\">" +
                    "<a href=\"/\" class=\"title\" >Runit</a>"+
                    "</div>"+
                    "<div class=\"top-end\">" +
                    "<ul class=\"nav-wrapper\">"+
                    "<li class=\"nav-item\"><a href=\"/login\">Entrar</a></li>"+
                    "<li class=\"nav-item\"><a href=\"/newuser\">Registar</a></li>"+
                    "</ul>"+
                    "</div>");
        }

        return "confirmations";
    }

    @GetMapping("/error")
    public String error(Model model, HttpServletRequest request) throws Exception{
        String username = request.getRemoteUser();
        String role = handler.getRole(username);
        if (role.equals("STAFF")){
            model.addAttribute("options","<div class=\"top-start\">" +
                    "<a href=\"/\" class=\"title\">Runit</a>"+
                    "</div>"+
                    "<div class=\"top-mid\">" +
                    "<ul class=\"nav-wrapper\">"+
                    "<li class=\"nav-item\"><a href=\"/newevent\">Registar evento</a></li>"+
                    "<li> | </li>"+
                    "<li class=\"nav-item\"><a href=\"/registertime\">Registar tempos</a></li>"+
                    "</ul>"+
                    "</div>"+
                    "<div class=\"top-end\">" +
                    "Olá "+username+", <a href=\"/logout\" class=\"nav-wrapper nav-item\">Sair</a>"+
                    "</div>");
        }else{
            model.addAttribute("options","<div class=\"top-start\">" +
                    "<a href=\"/\" class=\"title\">Runit</a>"+
                    "</div>"+
                    "<div class=\"top-mid\">" +
                    "<ul class=\"nav-wrapper\">"+
                    "<li class=\"nav-item\"><a href=\"/newinscricao\">Inscrever num evento</a></li>"+
                    "<li> | </li>"+
                    "<li class=\"nav-item\"><a href=\"/inscricoes\">Inscrições</a></li>"+
                    "</ul>"+
                    "</div>"+
                    "<div class=\"top-end\">" +
                    "Olá "+username+", <a href=\"/logout\" class=\"nav-wrapper nav-item\">Sair</a>"+
                    "</div>");
        }
        return "confirmations";
    }
}
