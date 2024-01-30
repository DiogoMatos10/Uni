/*Funções aplicáveis à listagem de farmácias e um agendamento de vacinação*/
getAllPharmacies();
cityDropDown();


//Função responsável por obter os dados de uma farmácia a partir do seu entity_id com ligação ao servidor de apoio
function getPharmacyData(entity_id) {
    //URL para fazer a requisição de dados da farmácia com base no entity_id
    const url = "https://magno.di.uevora.pt/tweb/t1/farmacia/get/" + entity_id;

    //Cria uma instância XMLHttpRequest para fazer a requisição assíncrona
    const xhr = new XMLHttpRequest();
    xhr.open("GET", url, true);

    xhr.onreadystatechange = function () {
        if (xhr.readyState === 4 && xhr.status === 200){
            const data = JSON.parse(xhr.responseText);

            //Procedimento para adicionar aos elements do detalhe da farmácia a sua informação
            //Nome, Email, Telefone e Director
            document.getElementById("pharmacyName").innerHTML = data.farmacia.name;
            document.getElementById("pharmacyEmail").innerHTML = "<strong>Email:</strong> " + data.farmacia.email;
            document.getElementById("pharmacyTelephone").innerHTML = "<strong>Telefone: </strong>" + data.farmacia.telephone;

            //Verificação se a farmácia tem director
            if(data.farmacia.director!== undefined){
                document.getElementById("pharmacyDirector").innerHTML = "<strong>Diretor: </strong>" + data.farmacia.director;
            }else{
                document.getElementById("pharmacyDirector").innerHTML = "<strong>Diretor: </strong>" + "Indefinido";
            }

            //Vacinas Disponíveis
            //Procedimento para retirar as vacinas disponiveis do array data.farmacia.services
            let availableVaccines = [];
            for (let i = 0; i < data.farmacia.services.length; i++) {
                if (typeof data.farmacia.services[i] === "string") {
                    availableVaccines.push(data.farmacia.services[i]);
                }
            }
            document.getElementById("pharmacyService").innerHTML = "<strong>Vacinas Disponíveis: </strong>" + availableVaccines.join(", ");

            //Informações de Localização
            document.getElementById("h4").innerHTML = "<strong>Localização</strong>"
            document.getElementById("pharmacyLongitude_Latitude").innerHTML = "<strong>Longitude: </strong>" + data.farmacia.longitude + " <strong>Latitude: </strong>" + data.farmacia.latitude;
            document.getElementById("pharmacyStreet").innerHTML = "<strong>Rua: </strong>" + data.farmacia.street_name;
            document.getElementById("pharmacyPostalCode").innerHTML = "<strong>Código Postal:  </strong>" + data.farmacia.postal_code_zone + "-" + data.farmacia.postal_code_sufix;
            document.getElementById("pharmacyLocality_Region").innerHTML = "<strong>Localidade: </strong>" + data.farmacia.postal_code_locality + ", " + data.farmacia.postal_code_region;


            //Criação do form para agender uma vacinação para uma certa farmácia
            const schedulingVaccination = document.getElementById("schedulingVacination");

            const form = document.createElement("form");
            form.id = "vaccinationForm";

            //Elementos do formulário (Número de utente, Data, Tipo de vacina)
            const numberLabel = document.createElement("label");
            numberLabel.textContent = "Número de utente: ";
            const numberInput = document.createElement("input");
            numberInput.type = "text";
            numberInput.name = "number";
            form.appendChild(numberLabel);
            form.appendChild(numberInput);

            const dateLabel = document.createElement("label");
            dateLabel.textContent = "Data: ";
            const dateInput = document.createElement("input");
            dateInput.type = "date";
            dateInput.name = "date";
            form.appendChild(dateLabel);
            form.appendChild(dateInput);

            //Dropdown de seleção do tipo de vacina
            const dropdownLabel = document.createElement("label");
            dropdownLabel.textContent = "Tipo de vacina: ";
            const dropdown = document.createElement("select");
            dropdown.name = "vacina";

            //Verificação das vacinas disponiveis na farmácia selecionada e criação das opções segundo o que há disponivel
            data.farmacia.services.forEach(function (service){
                if(service==="gripe"){
                    const optionGripe = document.createElement("option");
                    optionGripe.value = "gripe";
                    optionGripe.text = "Gripe";
                    dropdown.appendChild(optionGripe);
                }
                if(service==="covid-19"){
                    const optionCovid = document.createElement("option");
                    optionCovid.value = "covid-19";
                    optionCovid.text = "Covid 19";
                    dropdown.appendChild(optionCovid);
                }
            })
            form.appendChild(dropdownLabel);
            form.appendChild(dropdown);


            //Botão para submeter o agendamento
            const button = document.createElement("button");
            button.textContent = "Agendar Vacinação";
            button.addEventListener("click", function () {
                submitVaccination(entity_id);   //Chamada da função submitVaccination que faz adiciona o agendamento com ligação ao servidor de apoio
            });

            //Limpa o conteúdo anterior e adiciona o formulário e o botão
            schedulingVaccination.innerHTML = "";
            schedulingVaccination.appendChild(form);
            schedulingVaccination.appendChild(button);
        }
    };
    xhr.send();
}

//Função responsável por submeter um agendamento de vacinação para uma farmácia específica
function submitVaccination(entity_id) {
    //Obtém o formulário e os dados do formulário
    const formElement = document.getElementById("vaccinationForm");
    const formData = new FormData(formElement);

    //Cria uma instância XMLHttpRequest para fazer a requisição assíncrona
    const xhr = new XMLHttpRequest();

    //URL para enviar o agendamento de vacinação
    const url = "https://magno.di.uevora.pt/tweb/t1/schedule/add";

    //Configura a requisição como POST e o tipo de conteúdo
    xhr.open("POST", url, true);
    xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");

    xhr.onreadystatechange = function () {
        if (xhr.readyState === 4 && xhr.status === 200){
            const data = JSON.parse(xhr.responseText);

            //Alerta com a mensagem de agendamento e o código de agendamento
            alert(data.schedule_msg + "\n" + "Schedule Code: " + data.schedule_code);
        }
    };

    // Validação dos dados do formulário antes de enviar a requisição
    if(isNaN(String(formData.get("number")))){
        alert("O número de utente inserido é inválido");
    }else if(!formData.get("date")){
        alert("A data inserida é inválida");
    }else{
        const userId = "user_id=" + String(formData.get("number"));
        const vaccine = "vaccine=" + String(formData.get("vacina"));
        const entity_id_ = "entity_id=" + entity_id;
        const schedule_date = "schedule_date=" + formData.get("date");

        //Envio da requisição com os parâmetros necessários
        xhr.send(userId + "&" + vaccine + "&" + entity_id_ + "&" + schedule_date);
    }
}

//Função responsável por obter a lista de farmácias com base em filtros e paginá-las
function getAllPharmacies(page = 1, itemsPerPage = 5, name, city, vaccine) {
    //URL para obter a lista de farmácias
    const url = "https://magno.di.uevora.pt/tweb/t1/farmacia/list";

    //Cria uma instância XMLHttpRequest para fazer a requisição assíncrona
    const xhr = new XMLHttpRequest();
    xhr.open("GET", url, true);

    xhr.onreadystatechange = function () {
        if (xhr.readyState === 4 && xhr.status === 200){
            //Chama a função getServicesPharmacy para obter os serviços de farmácia com base na vacina
            getServicesPharmacy(vaccine, function (data_service) {
                const data = JSON.parse(xhr.responseText);

                //Obtém elementos HTML para exibição da lista de farmácias e paginação
                const pharmacyList = document.getElementById("pharmacyList");
                const pagination = document.getElementById("pagination");

                //Limpa o conteúdo anterior da lista de farmácias
                pharmacyList.innerHTML = "";

                //Cria um título para a lista de farmácias
                const h2 = document.createElement("h2");
                h2.textContent = "Listagem de Farmácias";
                pharmacyList.appendChild(h2);

                //Calcula os índices de início e fim para a página atual
                const startIndex = (page - 1) * itemsPerPage;
                let endIndex = startIndex + itemsPerPage;
                let pharmaciesToDisplay = [];
                let totalPages;

                //Verifica se há filtros aplicados
                if (city || name || vaccine) {
                    //Itera sobre as farmácias e aplica os filtros
                    data.farmacias.forEach(function (pharmacy) {
                        //Filtra farmácias com base na vacina disponível
                        const matchingServicePharmacies=[];

                        //Itera sobre as farmácias que aderem a certo serviço para encontrar correspondências com a vacina
                        data_service.farmacias.forEach(function (pharmacy_){
                            //Adiciona à lista de correspondências se não houver filtro de vacina ou a entidade coincidir
                            if(!vaccine || pharmacy_.entity_id === pharmacy.entity_id){
                                matchingServicePharmacies.push(pharmacy_);
                            }
                        })

                        //Condições para os filtros de cidade, nome e vacina
                        const cityCondition = !city || pharmacy.postal_code_locality === city;
                        const nameCondition = !name || pharmacy.name.toLowerCase().includes(name.toLowerCase());
                        const vaccineCondition = matchingServicePharmacies.length > 0 || vaccine==="";

                        //Se todas as condições forem atendidas, adiciona a farmácia à lista de exibição
                        if (cityCondition && nameCondition && vaccineCondition) {
                            pharmaciesToDisplay.push(pharmacy);
                        }
                    })

                    //Calcula o número total de páginas com base nas farmácias filtradas
                    totalPages = Math.ceil(pharmaciesToDisplay.length / itemsPerPage);
                    pharmaciesToDisplay = pharmaciesToDisplay.slice(startIndex, endIndex);

                } else {
                    //Se não houver filtros, exibe todas as farmácias
                    data.farmacias.forEach(function (pharmacy) {
                        if ((!name || pharmacy.name.toLowerCase().includes(name.toLowerCase()))) {
                            pharmaciesToDisplay.push(pharmacy);
                        }
                    })

                    //Calcula o número total de páginas com base em todas as farmácias
                    totalPages = Math.ceil(data.farmacias.length / itemsPerPage);
                    pharmaciesToDisplay = pharmaciesToDisplay.slice(startIndex, endIndex);
                }

                //Exibe as farmácias na lista
                pharmaciesToDisplay.forEach(function (pharmacy) {
                    const listItem = document.createElement("li");
                    const link = document.createElement("a");

                    //Adiciona um link clicável para obter detalhes da farmácia
                    link.textContent = pharmacy.name;
                    link.href = "#";
                    link.addEventListener("click", function () {
                        getPharmacyData(pharmacy.entity_id);
                    });

                    //Adiciona o link à lista de farmácias
                    listItem.appendChild(link);
                    pharmacyList.appendChild(listItem);
                });

                //Limpa o conteúdo anterior da paginação
                pagination.innerHTML = "";

                //Cria botões de página com base no número total de páginas
                if (totalPages > 1) {
                    for (let i = 1; i <= totalPages; i++) {
                        const pageButton = document.createElement("button");
                        pageButton.textContent = String(i);
                        pageButton.addEventListener("click", function () {
                            //Ao clicar, aplica os filtros para a página correspondente e exibe as farmácias
                            applyFilters(i, itemsPerPage);
                        });
                        pagination.appendChild(pageButton);
                    }
                }
            });
        }
    }
    xhr.send();
}

//Função responsável por obter a lista de farmácias que aderem a certa vacina, dada pelo argumento (vaccine)
function getServicesPharmacy(vaccine, callback) {
    //URL para buscar a lista de farmácias associadas a uma vacina
    const url = "https://magno.di.uevora.pt/tweb/t1/farmacia/searchvaccine";

    //Cria uma instância XMLHttpRequest para fazer a requisição assíncrona
    const xhr = new XMLHttpRequest();
    xhr.open("POST", url, true);
    xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");

    xhr.onreadystatechange = function () {
        if (xhr.readyState === 4 && xhr.status === 200) {
            const data = JSON.parse(xhr.responseText);

            //Chama o callback com os dados recebidos
            callback(data);
        }
    };
    xhr.send("vaccine=" + vaccine);
}


//Função responsável por criar o dropdown de cidade com base nas farmácias disponíveis
function cityDropDown() {
    //Obtém a referência do elemento dropdown no HTML
    const dropdown = document.getElementById("cityFilter");

    // URL para obter a lista de farmácias
    const url = "https://magno.di.uevora.pt/tweb/t1/farmacia/list";

    //Cria uma instância XMLHttpRequest para fazer a requisição assíncrona
    const xhr = new XMLHttpRequest();
    xhr.open("GET", url, true);

    xhr.onreadystatechange = function () {
        if (xhr.readyState === 4 && xhr.status === 200){
            const data = JSON.parse(xhr.responseText);

            // Array para armazenar cidades únicas
            let cities = [];

            // Itera sobre as farmácias para extrair cidades únicas
            data.farmacias.forEach(function (pharmacy) {
                const city = pharmacy.postal_code_locality;
                if (!cities.includes(city)) {
                    cities.push(city);
                }
            });

            //Cria o dropdown com as opções de cidades
            cities.forEach(function (city) {
                const option = document.createElement("option");
                option.value = city;
                option.textContent = city;
                dropdown.appendChild(option);
            });
        }
    }
    xhr.send();
}


//Função responsável por aplicar os filtros de nome da farmácia, cidade e vacina e chamar a função getAllPharmacies para obter e exibir as farmácias
function applyFilters(i = 1, itemsPerPage = 5) {
    // Obtém os valores dos campos de filtro do HTML
    const pharmacyName = document.getElementById("pharmacyNameFilter").value;
    const city = document.getElementById("cityFilter").value;
    const vaccine = document.getElementById("vaccinationFilter").value;

    //Chama a função para obter e exibir as famácias com base nos filtros aplicados
    getAllPharmacies(i, itemsPerPage, pharmacyName, city, vaccine);
}

//---------------------------------------------------------------------------------------------------------------------------------------//
/*Funções aplicáveis à listagem dos agendamentos*/


//Função responsável por obter todos os agendamentos de vacinação para um determinado usuário
function getAllVaccinations(user_id, url, callback) {
    //Cria uma instância XMLHttpRequest para fazer a requisição assíncrona
    const xhr = new XMLHttpRequest();
    xhr.open("POST", url, true);

    xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");

    xhr.onreadystatechange = function () {
        if (xhr.readyState === 4 && xhr.status === 200){
            const data = JSON.parse(xhr.responseText);

            // Verifica se o número de utente inserido é válido
            if(isNaN(String(user_id))===true){
                alert("O Número de Utente inserido é inválido")
            }else if(data.schedule_list.length===0){
                alert("O Número de Utente inserido não tem agendamentos")
            }else{
                //Se um callback foi fornecido, chama-o com os dados obtidos
                if (callback) {
                    callback(data);
                }
            }
        }
    };

    //Parâmetros e envio da requisição
    const params = "user_id=" + user_id;
    xhr.send(params);
}


//Função responsável por obter o nome de uma farmácia com base no seu entity_id
function getNamePharmacy(entity_id, callback) {
    //URL para obter os dados da farmácia com base no entity_id
    const url = "https://magno.di.uevora.pt/tweb/t1/farmacia/get/" + entity_id;

    //Cria uma instância XMLHttpRequest para fazer a requisição assíncrona
    const xhr = new XMLHttpRequest();
    xhr.open("GET", url, true);

    xhr.onreadystatechange = function () {
        if (xhr.readyState === 4 && xhr.status === 200) {
            const data = JSON.parse(xhr.responseText);

            //Obtém o nome da farmácia a partir dos dados recebidos
            const name = data.farmacia.name;

            // Chama o callback passando o nome da farmácia
            callback(name);
        }
    };
    xhr.send();
}


//Função responsável por aplicar uma pesquisa de agendamentos com base no número de utente
function applySearch(page = 1, itemsPerPage = 5) {
    //Obtém elementos HTML para exibição da lista de agendamentos e paginação
    const vaccinationsList = document.getElementById("scheduleList");
    const pagination = document.getElementById("pagination");

    //Obtém o número de utente a ser pesquisado
    const user_id = document.getElementById("userIDSearch").value;

    //URL para obter a lista de agendamentos
    const url = "https://magno.di.uevora.pt/tweb/t1/schedule/list";

    //Chama a função para obter todos os agendamentos para o número de utente especificado
    getAllVaccinations(user_id, url, function (data) {
        console.log(user_id);
        console.log(data);

        //Limpa o conteúdo anterior da lista de agendamentos
        vaccinationsList.innerHTML = "";

        //Cria um título para a lista de agendamentos
        const h2 = document.createElement("h2");

        //Calcula os índices de início e fim para a página atual
        const startIndex = (page - 1) * itemsPerPage;
        let endIndex = startIndex + itemsPerPage;
        let scheduleToDisplay = [];
        let totalPages;

        //Verifica se há um número de utente inserido
        if (user_id) {
            h2.textContent = "Listagem dos Agendamentos do Utente: " + user_id;
            //Filtra os agendamentos para o número de utente especificado
            for (let i = 0; i < data.schedule_list.length; i++) {
                if (data.schedule_list[i][0] === parseInt(user_id)) {
                    scheduleToDisplay.push(data.schedule_list[i]);
                }
            }

            //Calcula o número total de páginas e limita os agendamentos a serem exibidos na página atual
            totalPages = Math.ceil(scheduleToDisplay.length / itemsPerPage);
            scheduleToDisplay = scheduleToDisplay.slice(startIndex, endIndex);

            //Itera sobre os agendamentos a serem exibidos
            scheduleToDisplay.forEach(function (schedule) {
                //Obtém o nome da farmácia associada ao agendamento
                getNamePharmacy(schedule[1], function (name) {
                    const listItem = document.createElement("li");
                    const link = document.createElement("a");

                    //Configurações do link de remoção
                    link.href = "#";
                    link.textContent = "  Remover";
                    link.style = "text-decoration:none";
                    link.addEventListener("click", function () {
                        //Chama a função para confirmar a remoção do agendamento
                        confirmRemoveSchedule(schedule[4], user_id);
                    });

                    // Adiciona informações do agendamento ao item da lista
                    listItem.innerHTML = "<strong>Farmácia:</strong> " + name + "    <strong>Data:</strong> " + schedule[3] + "    <strong>Vacina:</strong> " + schedule[2] + "    <strong>Código:</strong> " + schedule[4];
                    listItem.appendChild(link);
                    vaccinationsList.appendChild(listItem);
                })
            });

            // Limpa a seção de paginação
            pagination.innerHTML = "";

            //Adiciona botões de página com base no número total de páginas
            if (totalPages > 1) {
                for (let i = 1; i <= totalPages; i++) {
                    const pageButton = document.createElement("button");
                    pageButton.textContent = String(i);
                    pageButton.addEventListener("click", function () {
                        // Aplica a pesquisa para a página clicada
                        applySearch(i, itemsPerPage);
                    });
                    pagination.appendChild(pageButton);
                }
            }
        }
        vaccinationsList.appendChild(h2);
    });
}


//Função para confirmar a remoção de um agendamento antes de realizar a ação
function confirmRemoveSchedule(schedule_code, user_id) {
    //Exibe uma janela de confirmação
    const confirmation = window.confirm("Tem a certeza que deseja remover este agendamento (code: " + schedule_code + ") ?");

    //Verifica se a remoção foi confirmada
    if (confirmation) {
        //Chama a função para remover o agendamento com base no código e número de utente
        removeSchedule(schedule_code, user_id);
    }
}


//Função responsável por remover um agendamento com base no código e número de utente
function removeSchedule(schedule_code, user_id) {
    //URL para a remoção de agendamentos
    const url = "https://magno.di.uevora.pt/tweb/t1/schedule/remove";

    // Cria uma instância XMLHttpRequest para fazer a requisição assíncrona
    const xhr = new XMLHttpRequest();
    xhr.open("POST", url, true);
    xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");

    xhr.onreadystatechange = function () {
        if (xhr.readyState === 4 && xhr.status === 200){
            const data = JSON.parse(xhr.responseText);
        }
    }

    //Exibe um alerta informando sobre a remoção do agendamento
    alert("Agendamento removido: "+ schedule_code);

    // Envia a requisição para remover o agendamento com base no código e número de utente
    xhr.send("schedule_code=" + schedule_code + "&" + "user_id=" + user_id);

    // Atualiza a lista de agendamentos após a remoção
    applySearch();
}


//----------------------------------------------------------------------------------------------------------------------------------------//
/*Funções aplicáveis à administração*/
getAllPharmacyAdmin();

// Função genérica para buscar dados dos URLs, exibir as listas de Gripe e Covid-19 e atualizar um gráfico para cada lista
function fetchAndDisplayPharmacyData(url, listId, graphId, title) {
    // Cria uma instância XMLHttpRequest para fazer a requisição assíncrona
    const xhr = new XMLHttpRequest();
    xhr.open("GET", url, true);

    xhr.onreadystatechange = function () {
        if (xhr.readyState === 4 && xhr.status === 200){
            const data = JSON.parse(xhr.responseText);
            const pharmacyList = document.getElementById(listId);

            //Limpa o conteúdo anterior da lista de farmácias
            pharmacyList.innerHTML = "";

            // Cria um título para a lista de farmácias
            const h2 = document.createElement("h2");
            h2.textContent = title;
            pharmacyList.appendChild(h2);

            //Itera sobre as farmácias e cria itens de lista com links de remoção
            data.farmacias.forEach(function (pharmacy) {
                const listItem = document.createElement("li");
                const link = document.createElement("a");

                link.href = "#";
                link.textContent = "  Remover";
                link.style = "text-decoration:none";
                link.addEventListener("click", function () {
                    // Chama a função para confirmar a remoção da farmácia
                    confirmRemovePharmacy(pharmacy.entity_id);
                });

                listItem.innerHTML = pharmacy.name;
                listItem.appendChild(link);
                pharmacyList.appendChild(listItem);
            });

            // Atualiza o gráfico com base nos dados obtidos
            updateGraph(listId, graphId, data);
        }
    };
    xhr.send();
}


//Função para confirmar a remoção de uma farmácia
function confirmRemovePharmacy(entity_id){
    const confirmation = window.confirm("Tem a certeza que deseja remover esta farmácia (Entity ID: " + entity_id + ") ?");
    if (confirmation) {
        //Chama a função para remover a farmácia
        removePharmacy(entity_id);
    }
}


//Função para remover uma farmácia
function removePharmacy(entity_id) {
    //URL para remover a farmácia
    const url = "https://magno.di.uevora.pt/tweb/t1/program/remove";

    //Cria uma instância XMLHttpRequest para fazer a requisição assíncrona
    const xhr = new XMLHttpRequest();
    xhr.open("POST", url, true);
    xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");

    xhr.onreadystatechange = function () {
        if (xhr.readyState === 4 && xhr.status === 200){
            const data = JSON.parse(xhr.responseText);
            console.log(data);
        }
    }

    //Envio dos dados para remover a farmácia
    xhr.send("entity_id=" + entity_id);
    console.log("Farmácia Removida:", entity_id);
    getAllPharmacyAdmin();
}


//Função para obter todas as farmácias com base nas suas vacinas
function getAllPharmacyAdmin() {
    //URLs para obter listas de farmácias por programa (Gripe e Covid19)
    const url_gripe = "https://magno.di.uevora.pt/tweb/t1/program/gripe/list";
    const url_covid = "https://magno.di.uevora.pt/tweb/t1/program/covid19/list";

    //Chamada da função para exibir a lista e gráfico de Gripe
    fetchAndDisplayPharmacyData(url_gripe, "gripeList", "gripeGraph", "Listagem Gripe");

    //Chamada da função para exibir a lista e gráfico de Covid19
    fetchAndDisplayPharmacyData(url_covid, "covidList", "covidGraph", "Listagem Covid19");
}

//Função para criar um form e adicionar uma nova farmácia
function addPharmacy() {
    // Obtém o formulário de farmácia por ID
    const formPharmacy = document.getElementById("formPharmacy");

    //Cria um novo elemento de formulário
    const form = document.createElement("form");
    form.id = "pharmacyForm";

    //Cria elementos de rótulo e entrada para o nome
    const nameLabel = document.createElement("label");
    nameLabel.textContent = "Nome: ";
    const nameInput = document.createElement("input");
    nameInput.type = "text";
    nameInput.name = "number";
    form.appendChild(nameLabel);
    form.appendChild(nameInput);

    //Cria elementos de rótulo e entrada para o Entity ID
    const numberLabel = document.createElement("label");
    numberLabel.textContent = "Entity ID: ";
    const numberInput = document.createElement("input");
    numberInput.type = "text";
    numberInput.name = "entity_id";
    form.appendChild(numberLabel);
    form.appendChild(numberInput);

    //Cria elementos de rótulo e dropdown para o tipo de vacina
    const dropdownLabel = document.createElement("label");
    dropdownLabel.textContent = "Tipo de vacina: ";
    const dropdownPh = document.createElement("select");
    dropdownPh.name = "vacina";
    const optionCovid = document.createElement("option");
    optionCovid.value = "covid-19";
    optionCovid.text = "Covid 19";
    const optionGripe = document.createElement("option");
    optionGripe.value = "gripe";
    optionGripe.text = "Gripe";
    const optionBoth = document.createElement("option");
    optionBoth.value ="gripe&covid-19";
    optionBoth.text = "Ambos";
    dropdownPh.appendChild(optionCovid);
    dropdownPh.appendChild(optionGripe);
    dropdownPh.appendChild(optionBoth);
    form.appendChild(dropdownLabel);
    form.appendChild(dropdownPh);


    // Cria um botão para adicionar a farmácia
    const button = document.createElement("button");
    button.textContent = "Adicionar Farmácia";
    button.addEventListener("click", function () {
        //Chama a função para enviar o formulário da farmácia
        submitPharmacy();
    });

    formPharmacy.innerHTML = "";
    formPharmacy.appendChild(form);
    formPharmacy.appendChild(button);
}

//Função para enviar o formulário de farmácia
function submitPharmacy(){
    const formElement = document.getElementById("pharmacyForm");
    const formData = new FormData(formElement);

    const xhr = new XMLHttpRequest();
    const url = "https://magno.di.uevora.pt/tweb/t1/program/add";
    xhr.open("POST", url, true);
    xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");

    xhr.onreadystatechange = function () {
        if (xhr.readyState === 4 && xhr.status === 200){
            const data = JSON.parse(xhr.responseText);
            console.log(data);
        }
    };

    //Obtém os serviços selecionados
    const service=formData.get("vacina").split("&");

    const name="name="+formData.get("name");
    const services="services="+service;
    const entity_id_="entity_id="+Number(formData.get("entity_id"));

    // Envia os dados do formulário para o servidor
    xhr.send( name + "&" + services + "&" + entity_id_);

    //Chama a função para atualizar a lista
    getAllPharmacyAdmin();
}
submitPharmacy();

//Função para atualizar o gráfico com base nos dados de farmácias
function updateGraph(listId, graphId, data) {
    //Obtém as cidades com mais farmácias
    const citiesWithMostPharmacies = getCitiesWithMostPharmacies(data.farmacias);

    //Inicializa um array para armazenar a contagem de farmácias por cidade
    const countPerCity = [];

    //Itera sobre as cidades com mais farmácias
    for (let i = 0; i < citiesWithMostPharmacies.length; i++) {
        //Para cada cidade, inicializa a contagem como zero
        const city = citiesWithMostPharmacies[i];
        let count = 0;

        //Itera sobre todas as farmácias para contar aquelas na cidade atual
        for (let j = 0; j < data.farmacias.length; j++) {
            if (data.farmacias[j].postal_code_locality === city) {
                count++;
            }
        }

        //Armazena a contagem no array
        countPerCity.push(count);
    }

    //Formata os dados para o gráfico de barras
    const graphData = [{
        x: citiesWithMostPharmacies,
        y: countPerCity,
        type: 'bar',
    }];

    //Configuração do layout do gráfico
    const graphLayout = {
        title: 'Farmácias por Cidade', yaxis: { title: 'Quantidade de Farmácias', tickformat: 'd' },
    };

    //Cria um gráfico Plotly com os dados formatados e o layout configurado
    Plotly.newPlot(graphId, graphData, graphLayout);
}

//Função para obter as cidades com mais farmácias a partir de uma lista de farmácias
function getCitiesWithMostPharmacies(farmacias) {
    //Contador de farmácias por cidade
    const citiesCount = {};

    //Itera sobre as farmácias e conta a quantidade por cidade
    farmacias.forEach(function (pharmacy) {
        const city = pharmacy.postal_code_locality;
        if (citiesCount[city] === undefined || citiesCount[city] === null) {
            citiesCount[city] = 0;
        }
        citiesCount[city] = citiesCount[city] + 1;
    });

    //Obtém as cidades ordenadas pelo número de farmácias em ordem decrescente
    const sortedCities = Object.keys(citiesCount).sort((a, b) => citiesCount[b] - citiesCount[a]);

    // Retorna as cinco primeiras cidades
    return sortedCities.slice(0, 5);
}