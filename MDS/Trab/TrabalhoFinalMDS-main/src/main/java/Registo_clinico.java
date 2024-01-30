import java.io.File;
import java.util.ArrayList;

public class Registo_clinico {
    public ArrayList<File> analises;
    public ArrayList<File> exames;
    public ArrayList<File> relatorios;
    public ArrayList<String> info;

    public Registo_clinico(){
        this.analises = new ArrayList<>();
        this.exames = new ArrayList<>();
        this.relatorios = new ArrayList<>();
        this.info = new ArrayList<>();
    }

    public void addAnalise(File f){
        analises.add(f);
    }

    public void addExame(File f){
        exames.add(f);
    }

    public void addRelatorio(File f){
        relatorios.add(f);
    }

    public void addInfo(String f){
        info.add(f);
    }
}
