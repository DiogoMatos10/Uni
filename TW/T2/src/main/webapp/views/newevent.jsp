<%@ page language="java" session="true" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
    <title>Runit</title>
    <link rel="stylesheet" href="../static/css/style_register.css">

</head>
<body>
<div id="wrapper">
    <div id="content">
        <div class="top">
            <div class="bar">
                ${options}
            </div>
        </div>
        <div class="page-content">
            <div class="form">
                <h2>New Event</h2>

                <form class="form" id="form1" method="GET" action="/registerevent">
                    <input type="text" name="name" placeholder="Nome de evento">
                    <input type="datetime-local" name="date" placeholder="Data e Hora">
                    <textarea name="description" placeholder="Descrição"></textarea>
                    <input type="text" name="value" placeholder="Preço"><br>
                    <button type="submit">Criar evento</button>
                </form>
            </div>
        </div>
    </div>
</div>
</body>
</html>