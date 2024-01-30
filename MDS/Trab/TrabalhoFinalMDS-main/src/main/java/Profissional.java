import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;

public class Profissional extends Utilizador {

    public ArrayList<LocalDateTime> disponibilidade;
    public ArrayList<String> especializacao;
    public boolean func_clinica;
    public Clinica clinica;
    public ArrayList<String> seguros;
    public ArrayList<Ato_medico> atos_medicos;
    public boolean validado;


    public Profissional(String nome, String email, String password, int id, ArrayList<String> especializacao,  boolean func_clinica) {
        super(nome, email, password, id);
        this.disponibilidade = new ArrayList<>();
        this.especializacao=especializacao;
        this.func_clinica = func_clinica;
        this.seguros = new ArrayList<>();
        this.atos_medicos = new ArrayList<>();
        this.clinica = null;
        this.validado = false;
    }

    //Faz delete de todas as datas de disponibilidade que passem do dia atual.
    public void delete_disp_obsoletas(){
        for(int i=0; i<disponibilidade.size(); i++){
            if(disponibilidade.get(i).isBefore(LocalDateTime.now())){
                disponibilidade.remove(i);
            }
        }
    }

}
