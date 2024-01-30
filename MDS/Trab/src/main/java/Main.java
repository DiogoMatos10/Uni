import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        //Inicialização
        SaudeEmCasa_APP sys = new SaudeEmCasa_APP();
        Scanner scan = new Scanner(System.in);
        int opt = 1;

        //HARDCODED DATA

        ArrayList<String> especializations = new ArrayList<String>();
        especializations.add("Neurocirurgião");
        especializations.add("Cardiologista");

        ArrayList<String> dadosmedicus = new ArrayList<>();
        dadosmedicus.add("dado 1");
        dadosmedicus.add("dado 2");

        Ato_medico atono = new Ato_medico("consulta", "Descrição", 30.00);
        ArrayList<Ato_medico> sbf = new ArrayList<Ato_medico>();
        sbf.add(atono);
        LocalDateTime datasa = LocalDateTime.now().plusDays(30);

        //clinicas
        sys.clinicas.add(new Clinica(0, "MediÉvora", "localização"));

        //profissionais
        sys.lista_Profissionais.add(new Profissional("Paulo", "Paulo@gmail.com", "1234" ,0, especializations, true));
        sys.lista_Profissionais.get(0).clinica= sys.clinicas.get(0);
        sys.lista_Profissionais.get(0).validado=true;
        sys.lista_Profissionais.get(0).disponibilidade.add(LocalDateTime.now().plusDays(30));
        sys.lista_Profissionais.add(new Profissional("Alexandre", "Alexandre@gmail.com", "4567" ,1, especializations, false));
        sys.lista_Profissionais.get(0).atos_medicos = sbf;
        sys.lista_Profissionais.get(1).atos_medicos = sbf;

        //pacientes
        sys.lista_Pacientes.add(new Paciente("Olga", "Olga@gmail.com","5555", 0 , "Rua da República", dadosmedicus, 5655555, 555555));
        sys.lista_Pacientes.get(0).registo_clinico = new Registo_clinico();

        //Agendamentos
        sys.agendamentos.add(new Agendamento(0, sys.lista_Pacientes.get(0), sys.lista_Profissionais.get(0),datasa, "aqui" , "Informação" ,atono));

        //admins
        sys.lista_Admins.add(new Admin("Diogo", "Diogo@gmail.com", "abc", 0));

        System.out.println("\nBem vindo ao SaúdeEmCasa@Évora!");
        while(true){

            //User Interface
            //Escolha inicial
            System.out.println("Escolha uma das opções abaixo:\n");

            do{
                if(opt<1 || opt>5){
                    System.out.println("A opção inserida não é válida. Por favor Insira uma das opções abaixo");
                }
                System.out.println("Registar utilizador -------------------------- 1");
                System.out.println("Testar o sistema como Paciente --------------- 2");
                System.out.println("Testar o sistema como Profissional de Saúde -- 3");
                System.out.println("Testar o sistema como Administrador ---------- 4");
                System.out.println("Sair ----------------------------------------- 5");
                opt = scan.nextInt();

            }while(opt<1 || opt>5);

            //Registar utilizador
            if(opt == 1) {
                do {
                    if (opt < 1 || opt > 3) {
                        System.out.println("A opção inserida não é válida. Por favor Insira uma das opções abaixo");
                    }
                    System.out.println("Registar Paciente ---------------------------- 1");
                    System.out.println("Registar Profissional de saúde --------------- 2");
                    System.out.println("Voltar ao menu inicial ----------------------- 3");
                    opt = scan.nextInt();
                } while (opt < 1 || opt > 3);

                switch (opt) {
                    //Registar Paciente
                    case 1:
                        System.out.println("Insira os seguintes dados, no formato indicado (separado por virgulas), para ser registado no sistema:");
                        System.out.println("nome , email , password , morada , telemovel, NIF");
                        scan.nextLine();
                        String opd=scan.nextLine();
                        System.out.println(opd);
                        String[] inp = opd.split(",");

                        if (inp.length != 6) {
                            System.out.println("Foi inserido um numero de campos diferente do esperado. Voltando para a seleção inicial");
                            opt = 0;
                            break;
                        }

                        System.out.println("Insira os seus dados médicos, separados por virgulas");
                        String[] dados = scan.next().split(",");

                        for (int i = 0; i < inp.length; i++) {
                            inp[i] = inp[i].trim();
                        }

                        ArrayList<String> dados_medicos = new ArrayList<String>(Arrays.asList(dados));
                        String msg = sys.resgistoPaciente(inp[0], inp[1], inp[2], inp[3], dados_medicos, Long.parseLong(inp[4]), Long.parseLong(inp[5]));
                        System.out.println(msg);
                        break;

                    //Registar Profissional de saúde
                    case 2:
                        System.out.println("Insira os seguintes dados, no formato indicado (separado por virgulas), para ser registado no sistema:");
                        System.out.println("nome , email , password ");
                        String[] inp1 = scan.next().split(",");

                        if (inp1.length != 3) {
                            System.out.println("Foi inserido um numero de campos diferente do esperado. Voltando para a seleção inicial");
                            opt = 0;
                            break;
                        }

                        System.out.println("Insira as suas especializações, separadas por { ; } :");
                        String espec = scan.next();

                        System.out.println("Deseja ser registado como funcionários de clínica médica?");
                        System.out.println("Sim --- 1");
                        System.out.println("Não --- 2");
                        opt = scan.nextInt();

                        boolean func_clin;
                        Clinica clin;
                        if(opt == 1) {
                            func_clin = true;
                            System.out.println("Selecione uma das clinicas, ou alternativamente regite uma");
                            System.out.println("registar nova clínica -------- 0");
                            for (int i=0 ; i<sys.clinicas.size();i++){
                                System.out.println(sys.clinicas.get(i).nome + " -- " + (i+1));
                            }
                            opt = scan.nextInt();

                            if(opt == 0) {
                                System.out.println("Insira as seguintes informações separadas por virgulas:");
                                System.out.println("nome , local");
                                String[] data_clinic = scan.nextLine().split(",");
                                sys.registoClinica(data_clinic[0], data_clinic[1]);
                                clin = sys.clinicas.get(sys.clinicas.size()-1);
                            }
                            else {
                                clin = sys.clinicas.get(opt-1);
                            }
                        }
                        else {
                            func_clin = false;
                            clin=null;
                        }

                        for (int i = 0; i < inp1.length; i++) {
                            inp1[i] = inp1[i].trim();
                        }


                        String msg1 = sys.registoProfissional (inp1[0], inp1[1], inp1[2], espec, func_clin, clin);
                        System.out.println(msg1);
                        break;
                    case 3:
                        break;
                }
            }
            //Testar o sistema como Paciente
            else if (opt==2) {
                Paciente ut;
                System.out.println("Escolha um dos pacientes para utilizar:");
                for (int i = 0; i < sys.lista_Pacientes.size(); i++) {
                    Paciente u = sys.lista_Pacientes.get(i);
                    System.out.println( i + " - " + u.nome + "\n");
                }

                int id = scan.nextInt();
                ut = sys.lista_Pacientes.get(id);

                System.out.println();
                System.out.println("Bem Vindo " + ut.nome + "!");

                while(true) {
                    System.out.println();
                    System.out.println("Por favor selecione uma das opções abaixo:");

                    System.out.println("Visualizar o seu perfil ---------------------- 1");
                    System.out.println("Agendar ato médico --------------------------- 2");
                    System.out.println("Consultar os seus agendamentos --------------- 3");
                    System.out.println("Visualizar lista de Profissionais de saúde  -- 4");
                    System.out.println("Sair ----------------------------------------- 5");

                    scan.nextLine();
                    opt = scan.nextInt();
                    if(opt == 5){
                        break;
                    }
                    System.out.println();
                    switch (opt){
                        //visualiazr perfil e alterações no mesmo
                        case 1:
                            System.out.println(sys.visualizarPerfil(ut));
                            System.out.println();
                            System.out.println("Inserir dados médicos ---------------- 1");
                            System.out.println("Preencher registo Clínico ------------ 2");
                            System.out.println("Consultar registo clínico ------------ 3");
                            System.out.println("Voltar ao menu ----------------------- 4");
                            opt = scan.nextInt();

                            if(opt == 1){
                                System.out.println("Introduza os dados, separados por virgulas");
                                String[] str = scan.next().split(",");
                                ut.dados_medicos.addAll(new ArrayList<String>(Arrays.asList(str)));
                            } else if (opt == 2) {
                                File file=new File("file_test.txt");

                                System.out.println("Adicionar analise ----------------- 1");
                                System.out.println("Adicionar exame ------------------- 2");
                                System.out.println("Adicionar relatorio --------------- 3");
                                System.out.println("Adicionar informação -------------- 4");
                                System.out.println("Sair ------------------------------ 5");
                                opt = scan.nextInt();

                                if(opt==1){
                                    ut.registo_clinico.addAnalise(file);
                                    System.out.println("Ficheiro de teste adicionado");
                                } else if (opt == 2) {
                                    ut.registo_clinico.addExame(file);
                                    System.out.println("Ficheiro de teste adicionado");
                                } else if (opt == 3) {
                                    ut.registo_clinico.addRelatorio(file);
                                    System.out.println("Ficheiro de teste adicionado");
                                } else if (opt == 4) {
                                    System.out.println("Introduza a informação a adicionar");
                                    String str = scan.next();
                                    ut.registo_clinico.addInfo(str);
                                    System.out.println("Informação adicionada");
                                }
                            }else if (opt == 3) {
                                System.out.println("De momento só será possivel visualizar as informações do seu registo clínico");
                                for(String s : ut.registo_clinico.info){
                                    System.out.println(s);
                                }
                            }
                            opt=0;
                            break;

                        //Agendar
                        case 2:
                            System.out.println("Escolha um dos seguintes Profissionais");
                            for(int i = 0; i<sys.lista_Profissionais.size(); i++){
                                if(sys.lista_Profissionais.get(i).validado){
                                    System.out.println(sys.lista_Profissionais.get(i).nome + " --- "+i);
                                }
                            }
                            opt = scan.nextInt();
                            Profissional prof = sys.lista_Profissionais.get(opt);

                            System.out.println("Defina o ato médico que quer agendar (Os atos médicos apresentados são respectivos ao profissional de saúde selecionado)");
                            for (int i=0 ; i<prof.atos_medicos.size(); i++){
                                System.out.println(prof.atos_medicos.get(i).nome + " --- " + i);
                            }
                            int a = scan.nextInt();
                            Ato_medico ato = prof.atos_medicos.get(a);

                            System.out.println("Selecione a data em que quer agendar (As datas são disponibilizadas pelo profissional de saude selecionado, de acordo com a sua disponibilidade)");
                            for (int i = 0; i < prof.disponibilidade.size(); i++) {
                                System.out.println(prof.disponibilidade.get(i).toString() + " ---- " + i);
                            }
                            opt = scan.nextInt();
                            String data = prof.disponibilidade.get(opt).toString();

                            System.out.println("Defina o local do agendamento");
                            scan.nextLine();
                            String local = scan.nextLine();
                            System.out.println("tem alguma informação adicional a acrescentar?");
                            String info = scan.nextLine();

                            String msg = sys.novoAgendamento(prof, ut, data, local, info, ato);
                            System.out.println(msg);

                            opt=0;
                            break;

                        //Consultar agendamentos
                        case 3:
                            for(Agendamento ag : sys.agendamentos){
                                if(ag.paciente==ut){
                                    String str = ag.ID + " | " + ag.data + " | " + ag.local+ " | " + ag.profissional + " | " + ag.ato_medico + " | ";
                                    if (ag.confirm){
                                        str = str + "Confirmado";
                                    }else{
                                        str = str + "Não Confirmado";
                                    }
                                    System.out.println(str);
                                }
                            }
                            break;

                        //Visualizar lista de Profissionais de saúde
                        case 4:
                            System.out.println("sair -------------- 0");
                            for (int i = 0; i<sys.lista_Profissionais.size(); i++){
                                Profissional pr = sys.lista_Profissionais.get(i);
                                if(pr.validado) {
                                    System.out.println(pr.nome + " ------- " + (i+1));
                                }
                            }
                            opt= scan.nextInt();
                            if(opt==0){
                                break;
                            }

                            Profissional pr = sys.lista_Profissionais.get(opt-1);
                            System.out.println();
                            System.out.println(sys.visualizarPerfil(pr));
                            break;

                    }
                }
            }
            //Testar o sistema como Profissional
            else if (opt==3) {
                Profissional ut;
                System.out.println("Escolha um dos profissionais para utilizar:");
                for (int i = 0; i < sys.lista_Profissionais.size(); i++) {
                    Profissional u = sys.lista_Profissionais.get(i);
                    System.out.println( i + " - " + u.nome + " - " + u.validado);
                }

                int id = scan.nextInt();
                ut = sys.lista_Profissionais.get(id);

                System.out.println();
                System.out.println("Bem Vindo " + ut.nome + "!");

                while(true) {
                    if (!ut.validado) {
                        System.out.println("O SEU REGISTO AINDA NÃO FOI VALIDADO");
                    }
                    System.out.println("Selecione uma das opções abaixo");
                    System.out.println("Visualizar ou alterar dados do seu perfil -------- 1");
                    if (ut.validado) {
                        System.out.println("Visualizar Agendamentos (processar) ---------- 2");
                        System.out.println("Realizar ato médico (completar agendamento) -- 3");
                        System.out.println("Sair ----------------------------------------- 4");
                    }
                    opt = scan.nextInt();
                    if(opt == 4){
                        break;
                    }

                    switch (opt) {
                        //perfil
                        case 1:
                            System.out.println(sys.visualizarPerfil(ut));
                            System.out.println("Alterar disponibilidade ---------------------- 1");
                            System.out.println("Alterar especialização ----------------------- 2");
                            System.out.println("Alterar Seguros ------------------------------ 3");
                            System.out.println("Alterar Atos médicos ------------------------- 4");
                            System.out.println("Sair ----------------------------------------- 5");

                            opt = scan.nextInt();

                            if (opt == 1) {
                                System.out.println("Adicionar disponibilidade -------------- 0");
                                for (int i = 0; i < ut.disponibilidade.size(); i++) {
                                    System.out.println(ut.disponibilidade.get(i).toString() + " (remover -- " + (i+1) + ")");
                                }
                                opt = scan.nextInt();

                                //adicionar disponibilidade
                                if (opt == 0) {
                                    System.out.println("Insira a data e a hora da disponibilidade no seguinte formato: AAAA-MM-DDTHH:MM");
                                    String data = scan.next();
                                    data = data + ":00.00";
                                    ut.disponibilidade.add(LocalDateTime.parse(data));
                                } else {
                                    ut.disponibilidade.remove(opt - 1);
                                }
                                opt=0;
                            }
                            else if (opt == 2) {
                                System.out.println("Adicionar especialização -------------- 0");
                                for (int i = 0; i < ut.especializacao.size(); i++) {
                                    System.out.println(ut.especializacao.get(i) + " (remover -- " + (i+1) + ")");
                                }
                                opt = scan.nextInt();
                                //adicionar especialização
                                if (opt == 0) {
                                    System.out.println("Insira a especialização");
                                    String espec = scan.next();
                                    ut.especializacao.add(espec);
                                } else {
                                    ut.especializacao.remove(opt - 1);
                                }
                                opt=0;

                            }
                            else if (opt == 3) {
                                System.out.println("Adicionar Seguros -------------- 0");
                                for (int i = 0; i < ut.seguros.size(); i++) {
                                    System.out.println(ut.seguros.get(i) + " (remover -- " + (i+1) + ")");
                                }
                                opt = scan.nextInt();
                                //adicionar seguro
                                if (opt == 0) {
                                    System.out.println("Insira o nome do seguro");
                                    String seg = scan.next();
                                    ut.seguros.add(seg);
                                } else {
                                    ut.seguros.remove(opt - 1);
                                }
                                opt=0;
                            }
                            else if (opt == 4) {
                                System.out.println("Adicionar ato médico -------------- 0");
                                for (int i = 0; i < ut.atos_medicos.size(); i++) {
                                    System.out.println(ut.atos_medicos.get(i).nome + " (remover -- " + (i+1) + ")");
                                }
                                opt = scan.nextInt();
                                //adicionar ato medico
                                if (opt == 0) {
                                    System.out.println("Insira o nome do ato médico");
                                    String n = scan.next();
                                    System.out.println("Insira o preço do ato médico");
                                    double p = scan.nextDouble();
                                    System.out.println("Insira a descrição do ato médico");
                                    String d = scan.next();

                                    ut.atos_medicos.add(new Ato_medico(n,d,p));
                                } else {
                                    ut.atos_medicos.remove(opt - 1);
                                }
                                opt=0;
                            }


                        //Agendamentos
                        case 2:
                            for (int i = 0; i < sys.agendamentos.size(); i++) {
                                Agendamento ag = sys.agendamentos.get(i);
                                if(ag.profissional==ut){
                                    String str = i+" -- " + ag.paciente.nome + " | " + ag.local + " | " + ag.ato_medico + " | " + ag.data+ " | ";
                                    if (!ag.processado){
                                        str = str + "POR PROCESSAR!!";
                                    }
                                    else if (ag.confirm) {
                                        str = str + "Confirmado";
                                    }
                                    else {
                                        str = str + "Recusado";
                                    }
                                }
                            }

                            opt =scan.nextInt();
                            Agendamento A = sys.agendamentos.get(opt);

                            System.out.println("De momento só é possivel exibir as Informações do registo clínico do paciente");
                            System.out.println("Paciente: " + A.paciente.nome);
                            for (String s : A.paciente.registo_clinico.info){
                                System.out.println(s);
                            }
                            System.out.println("\n");
                            if (!A.processado){
                                System.out.println("confirmar --- 1");
                                System.out.println("recusar ----- 2");
                                opt= scan.nextInt();
                                if (opt==1){
                                    A.confirmar();
                                }else{
                                    System.out.println("Indique a razão do cancelamento");
                                    String r = scan.next();
                                    A.recusar(r);
                                }
                            }
                            else {
                                String str  = "Informação adicional: ";
                                for(String s : A.info){
                                    str = str + s;
                                }
                                System.out.println(str);
                            }
                            opt = 0;
                            break;
                        //ato médico
                        case 3:
                            System.out.println("Insira qual o agendamento do ato médico realizado");
                            for (int i = 0; i < sys.agendamentos.size(); i++) {
                                Agendamento ag = sys.agendamentos.get(i);
                                if(ag.profissional==ut){
                                    String str = i+" -- " + ag.paciente.nome + " | " + ag.local + " | " + ag.ato_medico + " | " + ag.data;
                                }
                            }
                            opt =scan.nextInt();
                            Paciente p = sys.agendamentos.get(opt).paciente;

                            System.out.println("Proceda à atualização do registo clinico do paciente:");

                            File file=new File("file_test.txt");

                            System.out.println("Adicionar analise ----------------- 1");
                            System.out.println("Adicionar exame ------------------- 2");
                            System.out.println("Adicionar relatorio --------------- 3");
                            System.out.println("Adicionar informação -------------- 4");
                            System.out.println("Sair ------------------------------ 5");
                            opt = scan.nextInt();

                            if(opt==1){
                                p.registo_clinico.addAnalise(file);
                                System.out.println("Ficheiro de teste adicionado");
                            } else if (opt == 2) {
                                p.registo_clinico.addExame(file);
                                System.out.println("Ficheiro de teste adicionado");
                            } else if (opt == 3) {
                                p.registo_clinico.addRelatorio(file);
                                System.out.println("Ficheiro de teste adicionado");
                            } else if (opt == 4) {
                                System.out.println("Introduza a informação a adicionar");
                                String str = scan.next();
                                p.registo_clinico.addInfo(str);
                                System.out.println("Informação adicionada");
                            }

                            sys.agendamentos.remove(opt);
                            opt = 0;
                            break;

                        //sair
                        case 4:
                            break;

                    }
                }

            }
            //Testar o sistema como Administrador
            else if (opt==4) {
                Admin ut;
                System.out.println("Escolha um dos Administradores para utilizar:");
                for (int i = 0; i < sys.lista_Admins.size(); i++) {
                    Admin u = sys.lista_Admins.get(i);
                    System.out.println("ID: " + u.id + " - " + u.nome + "\n");
                }

                int id = scan.nextInt();
                ut = sys.lista_Admins.get(id);

                System.out.println();
                System.out.println("Bem Vindo " + ut.nome + "!");

                while(true) {
                    System.out.println("Selecione uma das opções abaixo");
                    System.out.println("Executar manutenção diária do sistema -------- 1");
                    System.out.println("Aprovar registo de profissional de saude ----- 2");
                    System.out.println("Sair ----------------------------------------- 3");
                    if(opt == 3){
                        break;
                    }
                    opt = scan.nextInt();
                    switch(opt){
                        case 1:
                            sys.daily();
                            break;
                        case 2:
                            System.out.println("Escolha que registo de profissional deseja verificar");
                            System.out.println("Sair ------------------------- 0");
                            for (int i = 0; i < sys.lista_Profissionais.size(); i++) {
                                Profissional pr = sys.lista_Profissionais.get(i);
                                if (!pr.validado){
                                    System.out.println(pr.nome + " ------- " + (i+1));
                                }
                            }
                            opt = scan.nextInt();
                            if(opt == 0){
                                break;
                            }
                            Profissional pr = sys.lista_Profissionais.get(opt-1);
                            System.out.println(sys.visualizarPerfil(pr));
                            System.out.println("Deseja validar este registo?");
                            System.out.println("Sim ----------- 1");
                            System.out.println("Não ----------- 2");
                            opt = scan.nextInt();

                            if (opt == 1){
                                System.out.println(sys.validarRegisto( ut.id, pr));
                            }else{
                                System.out.println("Deseja eliminar este registo?");
                                System.out.println("Sim ----------- 1");
                                System.out.println("Não ----------- 2");
                                opt = scan.nextInt();
                                if (opt == 1){
                                    sys.lista_Profissionais.remove(pr);
                                }
                            }
                            opt = 0;
                            break;
                    }
                }

            }
            //Sair
            else if (opt==5) {
                System.exit(1);
            }
        }
    }
}
