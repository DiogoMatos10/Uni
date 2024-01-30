<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
    <title>Runit</title>
    <link rel="stylesheet" href="../static/css/style.css">
</head>
<body>
    <div id="content">
        <div class="top">
            <div class="bar">
                ${options}
            </div>
        </div>

        <div class="page-content">
            <form class="button-type" action="/" method=get>
                <button type="submit" name="type" value="previous">Previous</button>
                <button type="submit" name="type" value="live">Live</button>
                <button type="submit" name="type" value="future">Future</button>
            </form>

            <div class="tables-container">
                <div id="eventsContainer">
                    <h2 style="display: flex; justify-content: center">${type}</h2>
                    <div class="search search-participant">
                        <form action="/" method="get">
                            <label for="name">Nome</label>
                            <input type="text" id="name" name="name">
                            <label for="date">Data</label>
                            <input type="date" id="date" name="date">
                            <input type="hidden" name="type" value="${param.type}">
                            <button type="submit" class="button-2">Search</button>
                        </form>

                        <form action="/">
                            <button type="submit" class="button-2" name="type" value="${param.type}">Clear</button>
                        </form>
                    </div>
                    ${events}
                    ${pagination}
                </div>
                <div id="classificationsContainer">
                    <h2 style="display: flex; justify-content: center">${event}</h2>
                    <div class="search">
                        <form action="/" method="get">
                            <label for="name">Nome</label>
                            <input type="text" name="participant_name">
                            <select id="tier" name="tier">
                                <option value="" disabled selected>Escalão</option>
                                <option value="júnior">Júnior</option>
                                <option value="sénior">Sénior</option>
                                <option value="vet35">Vet35</option>
                                <option value="vet50">Vet50</option>
                                <option value="vet65">Vet65</option>
                            </select>
                            <input type="hidden" name="type" value="${param.type}">
                            <input type="hidden" name="eventName" value="${param.eventName}">
                            <button type="submit" class="button-2">Search</button>
                        </form>

                        <form action="/">
                            <input type="hidden" name="type" value="${param.type}">
                            <input type="hidden" name="eventName" value="${param.eventName}">
                            <button type="submit" class="button-2" >Clear</button>
                        </form>
                    </div>
                    ${participants}
                </div>
            </div>
        </div>

    <div class="footer">
        <div class="footer-content">
            <div class="sponsors">
                <p>Patrocinadores:</p>
                <img src="../static/img/adidas.png" alt="adidas" height="100%">
                <img src="../static/img/FPA.png" alt="FPA" height="100%">
                <img src="../static/img/continente.png" alt="continente" height="100%">
            </div>
            <div id="credits">
                <p>Criadores:</p>
                <p>Diogo Matos, l54466</p>
                <p>Henrique Rosa, l51923</p>
                <p>Website criado em parceria com a <a href="https://www.uevora.pt/" id="UE">Universidade de Évora</a>
                </p>
            </div>
        </div>
    </div>
    </div></div>
</body>
</html>
