<%@ page language="java" session="true"
         contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
    <title>Runit</title>
    <link rel="stylesheet" href="../static/css/style.css">
    <style>
        .page-content{
            align-items: center;
            justify-content: center;
            margin: 0;
            padding: 0;
            display: flex;
            flex-direction: column;
            height: 70vh;
        }
        .page-content h2 {
            margin-bottom: 5px;
        }
    </style>
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
                <h2>Não tem permissão para esta página! Volte ao menu inicial.</h2>
            </div>
        </div>
    </div>
</body>
</html>