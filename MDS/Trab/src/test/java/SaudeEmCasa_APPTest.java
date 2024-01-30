import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class SaudeEmCasa_APPTest {
    private SaudeEmCasa_APP app;
    @Before
    public void setUp(){
        app=new SaudeEmCasa_APP();
    }

    @Test
    public void testResgistoPaciente() {
        String result=app.resgistoPaciente("Antonio Manel","antonio@gmail.com","1234","Rua de Avis,Évora",new ArrayList<>(),9277777,1222133);
        assertTrue(result.contains("registado com sucesso"));
    }

    @Test
    public void testRegistoProfissional() {
        Clinica clinica=new Clinica(1,"Clinica A","Evora");
        String result=app.registoProfissional("Dr. Jose","jose@gmail.com","1234","Ginecologista",true,clinica);
        assertTrue(result.contains("registado com sucesso"));
    }

    @Test
    public void testAgendamento() {
        Paciente paciente=new Paciente("Antonio Manel","antonio@gmail.com","1234",1,"Rua de Avis,Évora",new ArrayList<>(),9277777,1222133);
        Profissional profissional=new Profissional("Dr. Jose","jose@gmail.com","1234",1,new ArrayList<>(),true);
        Ato_medico atoMedico=new Ato_medico("Rotina","Consulta de rotina para ver se está tudo bem",333);
        String result=app.novoAgendamento(profissional,paciente,"2024-01-30T12:00","Clinica A","Consulta de Rotina",atoMedico);
        assertTrue(result.contains("registado com sucesso"));
    }

    @Test
    public void testNovoAgendamentoInvalidDate() {
        // Tentativa de agendamento com data inválida
        Paciente paciente = new Paciente("Antonio Manel", "antonio@gmail.com", "1234", 1, "Rua de Avis,Évora", new ArrayList<>(), 9277777, 1222133);
        Profissional profissional = new Profissional("Dr. Jose", "jose@gmail.com", "1234", 1, new ArrayList<>(), true);
        Ato_medico atoMedico=new Ato_medico("Rotina","Consulta de rotina para ver se está tudo bem",333);
        String result = app.novoAgendamento(profissional, paciente, "2024-01-30 12:00", "Clinica A", "Consulta de Rotina",atoMedico);
        assertTrue(result.contains("ERRO - A data inserida não está no formato correto"));
    }

    @Test
    public void testValidarRegisto() {
        Admin admin =new Admin("Admin","admin@gmail.com","1234",1);
        app.lista_Admins.add(admin);
        Profissional profissional=new Profissional("Dr. Jose","jose@gmail.com","1234",1,new ArrayList<>(),true);
        app.lista_Profissionais.add(profissional);

        String result= app.validarRegisto(admin.id,profissional);
        assertTrue(result.contains("validado com sucesso"));

        //Teste caso o profissional já tenha sido validado
        result=app.validarRegisto(admin.id,profissional);
        assertTrue(result.contains("já foi validado"));
    }

    @Test
    public void testValidarRegistoInvalidAdmin() {
        // Tentativa de validação de profissional por admin inválido, pois não consta na lista de admins
        Admin admin = new Admin("Admin", "admin@gmail.com", "1234", 1);
        Profissional profissional = new Profissional("Dr. Jose", "jose@gmail.com", "1234", 1, new ArrayList<>(), true);
        app.lista_Profissionais.add(profissional);
        String result = app.validarRegisto(admin.id, profissional);
        assertTrue(result.contains("ERRO - O utilizador não consta na lista de administradores."));
    }

    @Test
    public void testVisualizarPerfilPaciente() {
        Paciente paciente=new Paciente("Antonio Manel","antonio@gmail.com","1234",1,"Rua de Avis,Évora",new ArrayList<>(),9277777,1222133);

        String result=app.visualizarPerfil(paciente);
        assertTrue(result.contains("Nome: Antonio Manel| ID: 1"));
    }

    @Test
    public void testVisualizarPerfilProfissional() {
        Profissional profissional=new Profissional("Dr. Jose","jose@gmail.com","1234",1,new ArrayList<>(),true);
        String result=app.visualizarPerfil(profissional);
        assertTrue(result.contains("Nome: Dr. Jose| ID: 1"));
    }

    @Test
    public void testProcessarAgendamento() {
        Paciente paciente=new Paciente("Antonio Manel","antonio@gmail.com","1234",1,"Rua de Avis,Évora",new ArrayList<>(),9277777,1222133);
        Profissional profissional=new Profissional("Dr. Jose","jose@gmail.com","1234",1,new ArrayList<>(),true);
        Ato_medico atoMedico=new Ato_medico("Rotina","Consulta de rotina para ver se está tudo bem",333);
        Agendamento agendamento=new Agendamento(1,paciente,profissional, LocalDateTime.now(),"Clinica A","Consulta de Rotina",atoMedico);

        String result=app.processarAgendamento(true,agendamento,"");
        assertTrue(result.contains("confirmado com sucesso"));

        //Teste caso o agendamento seja cancelado
        result=app.processarAgendamento(false,agendamento,"");
        assertTrue(result.contains("cancelado com sucesso"));

        //Teste caso o agendamento já tenha sido processado
        result=app.processarAgendamento(true,agendamento,"");
        assertTrue(result.contains("O agendamento já foi processado"));
    }


    @Test
    public void testRegistoClinico(){
        String result=app.registoClinica("Clinica A","Evora");
        assertTrue(result.contains("registada com sucesso"));
    }
}