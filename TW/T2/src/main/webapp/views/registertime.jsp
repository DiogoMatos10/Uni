<%@ page language="java" session="true" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
    <title>Runit</title>
    <link rel="stylesheet" href="../static/css/style.css">

<body>
<div id="wrapper">
    <div id="content">
        <div class="top">
            <div class="bar">
                ${options}
            </div>
        </div>
        <div class="register-time-table page-content">
            <div class="events-container">
                <h2 style="justify-content: center; text-align: center">Registar tempo</h2>
                ${events}
            </div>
        </div>
    </div>
</div>
</body>
</html>