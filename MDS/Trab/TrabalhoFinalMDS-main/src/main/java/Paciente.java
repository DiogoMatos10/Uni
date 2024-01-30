import java.util.ArrayList;

public class Paciente extends Utilizador {
    public String morada;
    public ArrayList<String> dados_medicos;
    public long telemovel;
    public long NIF;

    public  Registo_clinico registo_clinico;

    public Paciente(String nome, String email, String password, int id , String morada , ArrayList<String> dados_medicos, long telemovel , long NIF) {
        super(nome, email, password, id);
        this.morada = morada;
        this.dados_medicos = dados_medicos;
        this.telemovel = telemovel;
        this.NIF = NIF;
        this.registo_clinico=new Registo_clinico();
    }



}
