<%@ page language="java" session="true"
         contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
    <title>Runit</title>
    <link rel="stylesheet" href="../static/css/login.css">

</head>
<body>
<div id="login-box">
    <h2>Início de Sessão</h2>
    <c:if test="${not empty error}">
        <div class="error">${error}</div>
    </c:if>
    <c:if test="${not empty msg}">
        <div class="msg">${msg}</div>
    </c:if>
    <form name='loginForm'
          action="<c:url value='j_spring_security_check'/>" method='POST'>
        <input id="username" type='text' name='username' value='' placeholder="Nome de utilizador">
        <input id="password" type='password' name='password' placeholder="Palavra-passe"/>
        <br>
        <input type="hidden" name="${_csrf.parameterName}"
               value="${_csrf.token}"/>
        <div id="button_div">
            <button id="submit" type="submit">Entrar</button>
        </div>
    </form>
    <div id="line_div"></div>
    <form action="/newuser">
        <div id="new_account_div">
            <button id="new_account" type="submit"> Nova Conta</button>
        </div>
    </form>
</div>
</body>
</html>