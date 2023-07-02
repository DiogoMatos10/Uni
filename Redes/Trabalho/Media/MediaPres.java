import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

//!!!!!!!!!!!!!!!! Importante !!!!!!!!!!!!!!

//este código funciona com um ficherio de texto simples em que as presenças estrão marcadas com um formato identico ao
//Data_user.txt (ver relatório), mas com uma pequena diferença: todas as presenças a serem marcadas têm de estar no mesmo
//ficheiro, o código vai marcar a presença de um dado aluno conforme as vezes que aparece no ficheiro e o valor de presença 
//relativa a cada vez que o aluno aparece no ficheiro, tendo a primeira linha do ficheiro o numero de presenças a considerar
//neste caso,o numero total de alunos representados no ficheiro, incluindo os repetidos

//Certifique-se de que as presenças estão marcadas num ficheiro com o nome "Pres.txt", com o formato acima indicado

public class MediaPres {

    public static void main(String[] args) {
        int num_aulas=3; //introduza o numero de aulas
        ScoreBoard.startChain(num_aulas);
    }
}

class Aluno{
    String username;
    int totalScore;
    int percentageScore;

    public Aluno(String username, int totalScore){
        this.username = username;
        this.totalScore=totalScore;
        this.percentageScore=0;

    }
    public void addScore(int add){
        totalScore += add;
    }
}

class ScoreBoard{
    public static void startChain(int num_aulas){
        ArrayList<Aluno> lista = scanFile();
        lista=getScorePer(num_aulas, lista);
        presentScore(lista);
    }

    public static void presentScore(ArrayList<Aluno>lista){
        System.out.println("numero     percentagem");
        for(Aluno x: lista){
            if(x.percentageScore>=100) {
                System.out.println(x.username + "         "+ x.percentageScore);
            }
            else if(x.percentageScore<10){
                System.out.println(x.username +"           " + x.percentageScore);
            }
            else{
                System.out.println(x.username +"          " + x.percentageScore);
            }
        }
    }

    public static ArrayList<Aluno> getScorePer(int num_aulas, ArrayList<Aluno> lista){
        int possivel=num_aulas*2;
        for(Aluno x:lista){
            x.percentageScore= (x.totalScore * 100) / possivel;
        }
        return lista;

    }

    public static ArrayList<Aluno> scanFile() { //dá scan do ficheiro das presenças e transporta os dados para um ficheiro
        try {
            File f = new File("src/Pres.txt");
            Scanner scan = new Scanner(f);
            int n = Integer.parseInt(scan.nextLine());
            ArrayList<Aluno> lista = new ArrayList<>();

            for (int i = 0; i < n; i++) {
                String user = scan.nextLine();
                scan.nextLine();
                String value = scan.nextLine();
                boolean user_exists = false;

                for (Aluno x : lista) { //ver se o aluno já está na lista
                    if (x.username.equals(user)) {
                        x.addScore(checkValue(value));
                        user_exists = true;
                        break;
                    }
                }
                if (!user_exists) {
                    lista.add(new Aluno(user, checkValue(value)));
                }
            }
            return lista;
        }
        catch (FileNotFoundException e) {
            System.out.println("file not found");
            return new ArrayList<Aluno>();
        }
    }

    public static int checkValue(String value){
        if(value.equals("PRESENT")){
            return 2;
        }
        else if(value.equals("LATE")){
            return 1;
        }
        else{
            return 0;
        }
    }
}