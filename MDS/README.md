# Relatório da Segunda Parte do Trabalho de Metodologias e Desenvolvimento de Software

---

* Henrique Rosa, nº 51923
* Diogo Matos, nº 54466

---

## Introdução
Neste relatório iremos abordar todas as decisões tomadas ao longo do desenvolvimento, 
onde foram alteradas as classes já pensadas no diagrama de classes da primeira parte do trabalho, entre outras decisões.
Todas as alterações ao projeto foram registadas por commits, onde em cada commit se menciona o issue resolvido em questão.
</br>
O projeto foi feito no IntelliJ IDEA com auxilio das ferramentas já incorporadas para a gestão do projeto.
## Classes alteradas

### Paciente:
  - O telémovel não pode ser **inteiro**, mudado para **long**;
  - O NIF não pode ser **inteiro**, mudado para **long**;
### Profissionais:
  - Adicionou-se uma lista de atos médicos que representa os atos que este disponibiliza;
  - Não há avaliação positiva e negativa dos profissionais como estava no diagrama de classes, visto que está explicito no enunciado que não era necessário implementar;
### Agendamento:
  - No inicio do desenvolvimento a data e hora estavam separados mas acabou por ser modificado para um só objeto do tipo **LocalDateTime** em vez de dois objetos do tipo **Date** e do tipo **Time**;
  - Avaliação não implementada por instrução do enunciado;
  - Adicionada razão do cancelamento;
  - Adicionada data de criação;
  - Adicionado campo "processado" que marca se um argumento já foi processado (cancelamento ou aprovado por um profissional de saúde);
  - Agendamentos vão poder ser vistos apenas pelo paciente e pelo Profissional de Saúde com ele relacionados, ambos através da UI da main;
### Ato Médico:
  - Adicionado nome do ato médico;
  - Adicionado descrição do ato médico;
  - Retirado o ID;
### Registo Clinico:
  - Retirado o atribudo "visivel", pois só os profissionais de saúde podem ter acesso ao registo clinico dos pacientes, por instrução do enunciado;
  - Não faz sentido os atributos serem privados, pelo que foram deixados todos publicos;
### SaudeEmCasa_APP:
  Classe onde se implementaram os use cases em si. Dá-se a inicialização das listas necessárias ao funcionamento da aplicação.
#### Métodos:
  - **daily** - Método que apaga as disponibilidades obseletas para todos os profissionais e remove os agendamentos não confirmados criados há mais de um dia (representa a manutenção automática diária do sistema);
  - **registoPaciente** - Regista um novo paciente;
  - **registoProfissional** - Regista um novo profissional de saúde e a clinica em que ele trabalha (caso trabalhe em alguma);
  - **registoClinica** - Regista uma nova clinica;
  - **novoAgendamento** - Cria um novo agendamento;
  - **validarRegisto** - Valida o registo de um profissional por um admin;
  - **visualizarPerfil** - Gera uma string com as informações de um user(profissional ou paciente);
  - **processarAgendamento** - Confirma ou cancela um agendamento;
</br>
</br>
Todos os métodos à exceção do daily retornam uma string que contem informação sobre o sucesso do método.
### Main:
Classe onde é representada a UI para interagir com a aplicação "SaudeEmCasa_APP".


## Testes unitários
Os testes unitários são realizados na classe "SaudeEmCasa_APPTest" onde se efetua testes a todos os métodos da classe "SaudeEmCasa_APP" mencionados já neste relatório.
Realizam se testes de erro e testes de sucesso onde todos foram bem sucedidos.

## Considerações Finais
Ao longo do desenvolvimento do trabalho houve bastantes alterações, mesmo já no fim do mesmo o que provocou bastantas commits, muitas das vezes seguidos uns aos outros. Por exemplo, tinhamos até ao fim do projeto um método "alterarRegisto" que consistia na adição de ficheiros no registo clinico do Paciente (o qual acabamos de retirar por não ser usado).
Tivemos também problemas do lado de um dos membros do grupo, onde o aluno Diogo não conseguia fazer commits e pushs para o Git e a solução encontrada foi o envio dos códigos feitos por este para o Henrique onde ele posteriormente dá commit. O problema acabou por se resolver e acabou por haver commits de ambas as partes.
