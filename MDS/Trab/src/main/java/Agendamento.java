import java.sql.Time;
import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDateTime;
import java.util.ArrayList;
import java.util.Date;

public class Agendamento {
    public int ID;
    public LocalDateTime data;
    public boolean confirm;
    public String local;
    public ArrayList<String> info;
    public Profissional profissional;
    public Ato_medico ato_medico;
    public Paciente paciente;
    public String razao_cancelamento;
    public LocalDateTime data_de_criacao;
    public boolean processado;


    public Agendamento(int ID, Paciente paciente, Profissional profissional, LocalDateTime data, String local, String info, Ato_medico ato) {
        this.ID = ID;
        this.paciente=paciente;
        this.profissional = profissional;
        this.data = data;
        this.confirm = false;
        this.local = local;
        this.info = new ArrayList<String>();
        this.info.add(info);
        this.confirm = false;
        this.razao_cancelamento=null;
        this.data_de_criacao=LocalDateTime.now();
        this.processado=false;
        this.ato_medico = ato;
    }

    public void confirmar(){
        confirm=true;
        processado=true;
    }
    public void recusar(String razao){
        razao_cancelamento = razao;
        confirm=false;
        processado=true;
    }

}