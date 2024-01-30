import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.InputMismatchException;


public class SaudeEmCasa_APP {
    ArrayList<Profissional> lista_Profissionais;
    ArrayList<Paciente> lista_Pacientes;
    ArrayList<Admin> lista_Admins;
    ArrayList<Agendamento> agendamentos;

    ArrayList<Clinica> clinicas;


    public SaudeEmCasa_APP (){
        lista_Pacientes=new ArrayList<>();
        lista_Admins=new ArrayList<>();
        lista_Profissionais=new ArrayList<>();
        agendamentos=new ArrayList<>();
        clinicas = new ArrayList<>();
    }

    public void daily (){
        for(Profissional p : lista_Profissionais){
            p.delete_disp_obsoletas();
        }
        LocalDateTime time = LocalDateTime.now().minusDays(1);

        //punição do profissional de saude que não tenha processado um agendamento
        for (Agendamento a : agendamentos){
            if((!a.processado) && a.data_de_criacao.isBefore(time)){
                a.profissional.validado=false;
            }
        }
        //remove agendamento depois de 24 horas desde criação se não tiver sido confirmado ou se tiver sido rejeitado
        agendamentos.removeIf(a -> (!a.confirm) && a.data_de_criacao.isBefore(time));
    }


    public String resgistoPaciente(String nome, String email, String password, String morada , ArrayList<String> dados_medicos, long telemovel , long NIF){
        try {
            //garantir que nenhum Paciente tem um id igual
            int id = 0;
            if (!lista_Pacientes.isEmpty()) {
                Paciente h = lista_Pacientes.get(lista_Pacientes.size() - 1);
                id = h.id + 1;
            }
            Paciente p = new Paciente(nome, email, password, id , morada , dados_medicos, telemovel , NIF);
            lista_Pacientes.add(p);
            return "Paciente registado com sucesso - ID:" + id + ".";
        }catch(InputMismatchException e){
            return "ERRO - inputs fora do formato correto";
        }
    }

    public String registoProfissional(String nome, String email, String password, String especializacao, boolean func_clinica, Clinica clinica){
        //garantir que nenhum profissional tem um id igual
        try {
            int id = 0;
            if (!lista_Profissionais.isEmpty()) {
                Profissional h = lista_Profissionais.get(lista_Profissionais.size() - 1);
                id = h.id + 1;
            }
            String[] espec = especializacao.split(";");
            Profissional p = new Profissional(nome, email, password, id, new ArrayList<String>(Arrays.asList(espec)) , func_clinica);
            if(func_clinica) {
                p.clinica = clinica;
            }
            lista_Profissionais.add(p);
            return "Profissional registado com sucesso. O seu registo está pendente para validação - ID:" + id + ".";
        }catch(InputMismatchException e){
            return "ERRO - inputs fora do formato correto";
        }
    }

    public String registoClinica(String nome, String local){
        //garantir que nenhuma clinica tem um id igual
        try {
            int id = 0;
            if (!clinicas.isEmpty()) {
                Clinica h = clinicas.get(clinicas.size() - 1);
                id = h.ID + 1;
            }

            Clinica p = new Clinica(id, nome, local);
            clinicas.add(p);
            return "Clinica registada com sucesso.";
        }catch(InputMismatchException e){
            return "ERRO - inputs fora do formato correto";
        }
    }

    public String novoAgendamento (Profissional prof, Paciente pac,String data, String local, String info, Ato_medico ato){

        try {
            LocalDateTime n_data;
            try{
                 n_data = LocalDateTime.parse(data);
            }catch(DateTimeParseException e){
                return "ERRO - A data inserida não está no formato correto";
            }
            int id = 0;
            if (!agendamentos.isEmpty()) {
                Agendamento h = agendamentos.get(agendamentos.size() - 1);
                id = h.ID + 1;
            }
            Agendamento p = new Agendamento(id, pac ,prof, n_data, local, info, ato);
            return "Agendamento registado com sucesso - ID:" + id + ".";
        }catch(InputMismatchException e){
            return "ERRO - inputs fora do formato correto";
        }
    }

    public String validarRegisto (int ID, Profissional profissional){
        if(lista_Admins.stream().anyMatch(p->p.id == ID)){
            if(!profissional.validado){
                int i = lista_Profissionais.indexOf(profissional);
                lista_Profissionais.get(i).validado = true;
                return "O profissional foi validado com sucesso";
            }else{
                return "O profissional já foi validado.";
            }
        }else{
            return "ERRO - O utilizador não consta na lista de administradores.";
        }
    }

    public String visualizarPerfil (Utilizador u){

        //Paciente
        if(u.getClass() == Paciente.class){
            String str =    "Nome: "+ u.nome+ "| ID: " + u.id + "\n" +
                            "Contactos - Email: " + u.email + " | Telemovel: " + ((Paciente) u).telemovel + "\n" +
                            "Morada: " + ((Paciente) u).morada + "\n" +
                            "NIF: " + ((Paciente) u).NIF+"\nDados médicos:\n";

            for(String dado : ((Paciente) u).dados_medicos){
                str = str + dado + "\n";
            }
            return str;
        }

        //Profissional
        else if(u.getClass() == Profissional.class){
            String str =    "Nome: "+ u.nome+ "| ID: " + u.id + "\n" +
                            "Contactos - Email: " + u.email + "\n" +
                            "Especiazação: " +  ((Profissional) u).especializacao + "\n"+
                            "Clínica médica: ";

            if(((Profissional) u).clinica==null){
                str = str + "Este profissional não está associado a nenhuma clínica\n";
            }else{
                str = str + ((Profissional) u).clinica.nome + ", localiazada em " + ((Profissional) u).clinica.local+"\n";
            }
            str = str + "Seguros: ";
            if (((Profissional) u).seguros.isEmpty()){
                str = str + "Este profissional não tem asssociação com nenhum seguro";
            }else{
                for(String seguro : ((Profissional) u).seguros) {
                    str = str + seguro + " | ";
                }
            }
            str = str + "\nDisponibilidade: \n";
            for(LocalDateTime disp : ((Profissional) u).disponibilidade) {
                str = str + disp.toString() + "\n";
            }
            return str;
        }

        else{
            return "ERRO - Informação sobre o utilizador inserido não está disponível";
        }
    }

    public String processarAgendamento(boolean confirmar, Agendamento agendamento, String razao){
        if (agendamento.processado){
            return "O agendamento já foi processado";
        }
        if(confirmar){
            agendamento.confirm=true;
            return "Agendamento confirmado com sucesso";
        }
        else{
            agendamento.razao_cancelamento = razao;
            agendamento.processado=true;
            return "Agendamento cancelado com sucesso";
        }
    }

}
