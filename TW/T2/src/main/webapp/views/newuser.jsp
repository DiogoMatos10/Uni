<%@ page language="java" session="true" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
    <title>Runit</title>
    <link rel="stylesheet" href="../static/css/newuser.css">

</head>
<body>
<div id="register-box">
    <form id="form1" method="GET" action="/register">
        <h2>Criar nova conta</h2>
        <input id="username" type='text' name='username' placeholder="Nome de utilizador">
        <input id="password" type='password' name='password' placeholder="Palavra-passe"/> <br>
        <select id="role" name="role" >
            <option value="ATLETA">Atleta</option>
            <option value="STAFF">Staff</option>
        </select>
        <div id="button_div">
            <button id="submit" type="submit">Criar conta</button>
        </div>
    </form>
</div>
</body>
</html>