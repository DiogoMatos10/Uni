import java.util.ArrayList;

/**
 * Class publica (Parque_Eolico).
 * Class constituida por um constructor sem parametros e pelos métodos:
 * - addAerogerador
 * - getAerogerador
 * - removeAerogerador
 * - getQuantAero
 * - getQuantAltitudeAcima
 * - manutencaoGerador
 * - manutencaoRotor
 * - manutencaoAmbos
 * - getCalcPTNumInstante
 * - getCalcPTVariosInstantes
 * - getAllCalcPT
 * Utiliza a biblioteca java.util.ArrayList para a criação do ArrayList.
 *
 * @author Diogo de Matos e Miguel Gordicho
 */

public class Parque_Eolico{
    public static ArrayList<Aerogerador> lista= new ArrayList<>();
    public static ArrayList<Double> colecaoVel= new ArrayList<>();
    /**
     * Constructor da class Parque_Eolico sem parametros.
     */
    public Parque_Eolico() {
    }

    /**
     * Método que recebe um aerogerador e adiciona-o ao ArrayList lista, mas só o adiciona se os valores do seu array não forem nulos, isto é, se tiver passado nos requisitos das caracteristicas de um aerogerador. Esta verificação faz se com o método toArray da class Aerogerador.
     * @param aerogerador (aerogerador declarado pelo utilizador com a class Aerogerador).
     */

    public void addAerogerador(Aerogerador aerogerador){
        double[] array=aerogerador.toArray();
        if(array[0]==0 && array[1]==0 && array[2]==0 && array[3]==0 && array[4]==0 && array[5]==0){
            System.out.println("Não foi possível adicionar o aerogerador ao parque eólico visto que as suas caracteristicas não cumprem os requesitos.");
        }else{
            lista.add(aerogerador);
            System.out.println("Aerogerador adicionado com sucesso.");
        }
    }

    /**
     * Método responsável por retornar as caracteristicas de um certo aerogerador em forma de array chamando o método toArray da class Aerogerador.
     * @param n (Número do aerogerador que o utilizador ou método quer retornar, sendo este apenas possível com valores de n a partir de 1).
     * @return caracteristicas do aerogerador pretendido em forma de array do tipo double.
     */

    public double[] getAerogerador(int n){
        Aerogerador aerogerador=lista.get(n-1);
        return aerogerador.toArray();
    }

    /**
     * Método responsável por remover um aerogerador ao ArrayList lista.
     * @param n (Número do aerogerador que o utilizador ou método quer retornar, sendo este apenas possível com valores de n a partir de 1).
     */

    public void removeAerogerador(int n){
        lista.remove(n-1);
    }

    /**
     * Método que retorna a quantidade de aerogeradores existentes no ArrayList lista.
     * O método vai correr cada posição do ArrayList lista com o for-each loop e se o aerogerador da posição pertencer à class Aerogerador, soma mais 1 ao count declarado antes do ciclo com valor inicial igual a 0.
     * @return valor do count depois de ter efetuado o for-each loop até ao fim.
     */

    public int getQuantAero(){
        int count=0;
        for (Aerogerador aerogerador : lista) {
            if (aerogerador instanceof Aerogerador) {
                count++;
            }
        }
        return count;
    }

    /**
     * Método que retorna a quantidade de aerogeradores que têm a turbina instalada acima de certa altura em relação ao solo.
     * O método vai correr cada posição do ArrayList lista com o for-each loop e vai declarar um array do tipo double e igualar ao método que retorna as caracteristicas de um aerogerador em forma de array (toArray da class Aerogerador),
     * se o valor da posição 3 do array declarado for maior ou igual à altura_ao_solo recebida pelo utilizador, soma mais 1 ao count declarado antes do ciclo com valor inicial igual a 0.
     * @param altura_ao_solo (Valor da altura da turbina instalada em relação ao solo pretendida pelo utilizador)
     * @return valor do count depois de ter efetuado o for-each loop até ao fim.
     */

    public int getQuantAlturaAcima(double altura_ao_solo){
        int count=0;
        for (Aerogerador aerogerador : lista) {
            double[] array = aerogerador.toArray();
            if (array[3] >= altura_ao_solo) {
                count++;
            }
        }
        return count;
    }

    /**
     * Método responsável pela manuntenção do Gerador.
     * Este método inicia se com a declaração de um array do tipo double sendo atribuido a este os valores do método getAerogerador de um certo aerogerador n dado pelo utilizador.
     * Se o valor do tipo de gerador (tipo_gerador) for igual a 1 ou 2, irá haver uma atribuição do valor recebido à posição 0 do array declarado no inicio do método,
     * de seguida, é declarado um novo aerogerador com o valor do tipo de gerador recebido adicionando este mesmo novo aerogerador à posição onde se encontra o aerogerador onde se pretendia fazer a alteração. Para o utilizador ter a certeza que a manuntenção foi bem sucedida, é mostrada ao utilizador a mensagem a dizer que a manuntenção foi realizada com sucesso.
     * Se o tipo de gerador for diferente de 1 ou 2, não ocorrerá alteração nenhuma ao aerogerador e é mostrada ao utilizador a mensagem a dizer que o tipo de gerador só pode ser de tipo 1 ou 2.
     * @param n (Número do aerogerador onde o utilizador quer fazer a manuntenção, sendo este apenas possível com valores de n a partir de 1).
     * @param tipo_gerador (Novo tipo de gerador para o aerogerador escolhido pelo utilizador).
     */

    public void manutencaoGerador(int n, int tipo_gerador){
        double[] array=getAerogerador(n);
        if(tipo_gerador==1 || tipo_gerador==2){
            array[0]=tipo_gerador;
            Aerogerador newAerogerador=new Aerogerador((int) array[0],array[1],array[2],array[3], array[4],array[5]);
            lista.set(n-1, newAerogerador);
            System.out.println("Manuntenção do gerador realizada com sucesso.");
        }else{
            System.out.println("Não se alterou o tipo de gerador. O tipo de gerador só pode ser de tipo 1 ou 2.");
        }
    }

    /**
     * Método responsável pela manuntenção do Rotor.
     * Este método inicia se com a declaração de um array do tipo double sendo atribuido a este os valores do método getAerogerador de um certo aerogerador n dado pelo utilizador.
     * Se o valor da diferença entre o valor da posição 3 (altura do eixo) e o raio dado pelo utilizador for maior ou igual a 4, será atribuido o valor do raio dado pelo utilizador à posição 1 do array declarado,
     * de seguida, é declarado um novo aerogerador com o valor do raio recebido adicionando este mesmo novo aerogerador à posição onde se encontra o aerogerador onde se pretendia fazer a alteração. Para o utilizador ter a certeza que a manuntenção foi bem sucedida, é mostrada ao utilizador a mensagem a dizer que a manuntenção foi realizada com sucesso.
     * Se o valor da diferença entre o valor da posição 3 (altura do eixo) e o raio dado pelo utilizador for menor que 4, não ocorrerá alteração nenhuma ao aerogerador e é mostrada ao utilizador a mensagem a dizer que a diferença entre a altura do eixo e o raio de envergadura das pás tem de ser maior ou igual a 4 metros.
     * @param n (Número do aerogerador onde o utilizador quer fazer a manuntenção, sendo este apenas possível com valores de n a partir de 1).
     * @param raio (Novo valor do raio de envergadura das pás para o rotot do aerogerador escolhido pelo utilizador).
     */

    public void manutencaoRotor(int n, double raio){
        double[] array=getAerogerador(n);
        if(array[3]-raio>=4){
            array[1]=raio;
            Aerogerador newAerogerador=new Aerogerador((int) array[0],array[1],array[2],array[3], array[4],array[5]);
            lista.set(n-1, newAerogerador);
            System.out.println("Manuntenção do rotor realizada com sucesso.");
        }else{
            System.out.println("Não se alterou o valor do raio do Rotor. A diferença entre a altura do eixo até ao solo e o raio de envergadura das pás tem de ser maior ou igual a 4 metros.");
        }
    }

    /**
     * Método responsável pela manuntenção do Gerador e do Rotor.
     * Este método utiliza os métodos manuntencaoGerador e manuntencaoRotor para fazer a alteração de ambos os componentes.
     * @param n (Número do aerogerador onde o utilizador quer fazer a manuntenção, sendo este apenas possível com valores de n a partir de 1).
     * @param raio (Novo valor do raio de envergadura das pás para o rotot do aerogerador escolhido pelo utilizador).
     * @param tipo_gerador (Novo tipo de gerador para o aerogerador escolhido pelo utilizador).
     */

    public void manutencaoAmbos(int n,double raio, int tipo_gerador){
        manutencaoGerador(n,tipo_gerador);
        manutencaoRotor(n,raio);
    }

    /**
     * Método que retorna o valor da energia/potência produzida por um aerogerador segundo uma velocidade num instante.
     * O método inicialmente declara um novo aerogerador sendo este o aerogerador pretendido pelo utilizador utilizando a class Aerogerador,
     * em seguida utiliza o método setVelocidade da class Aerogerador para o aerogerador declarado inicialmente atribuindo no parametro do método a velocidade do vento dada pelo utilizador num certo instante.
     * @param n (Número do aerogerador onde o utilizador quer calcular a energia/potência gerada pelo aerogerador, sendo este apenas possível com valores de n a partir de 1).
     * @param vel (Valor da velocidade do vento num certo instante e numa certa localização).
     * @return valor da energia/potência gerada pelo aerogerador utilizando o método getPt da class Aerogerador.
     */

    public double getCalcPTNumInstante(int n, double vel){
        Aerogerador aerogerador=lista.get(n-1);
        aerogerador.setVelocidade(vel);
        return aerogerador.getPt();
    }

    /**
     * Método responsável por adicionar velocidades ao ArrayList colecaoVel.
     * @param vel (Valor da velocidade do vento num certo instante e numa certa localização).
     */

    public void addVelocidades(double vel){
        colecaoVel.add(vel);
    }

    /**
     * Método responsável por limpar o ArrayList colecaoVel.
     */

    public void clearVelocidades(){
        colecaoVel.clear();
    }

    /**
     * Método que retorna uma coleção dos valores da energia/potência produzida por um aerogerador segundo certas velocidades em vários instantes/dias.
     * O método inicialmente declara um array do tipo double (PTs) com o mesmo tamanho do ArrayList colecaoVel e declara ainda um novo aerogerador sendo este o aerogerador pretendido pelo utilizador a class Aerogerador,
     * em seguida ocorre um ciclo for que irá correr todas as posições da colecaoVel e utilizar o método setVelocidade da class Aerogerador, para o aerogerador declarado inicialmente, atribuindo no parametro do método a velocidade do vento da posição em concreto para,
     * posteriormente atribuir o valor da potência gerada calculada no método getPt ,da class Aerogerador para certa velocidade, à posição em concreto do array PTs.
     * @param n (Número do aerogerador onde o utilizador quer calcular a energia/potência total gerada pelo aerogerador, sendo este apenas possível com valores de n a partir de 1).
     * @return coleção dos valores da energia/potência na forma de array.
     */

    public double[] getCalcPTVariosInstantes(int n){
        Aerogerador aerogerador=lista.get(n-1);
        double[] PTs=new double[colecaoVel.size()];
        for(int i = 0; i< colecaoVel.size(); i++){
            aerogerador.setVelocidade(colecaoVel.get(i));
            PTs[i]=aerogerador.getPt();
        }
        return PTs;
    }

    /**
     * Método que retorna o valor total da energia/potência gerada por todos os aerogeradores existentes no ArrayList lista para um dado instante, ou seja, velocidade do vento.
     * O método inicialmente declara uma variável do tipo double (res) com o valor inicial 0,
     * em seguida ocorre um for-each loop que vai correr todos os aerogeradores da lista e para cada aerogerador vai utilizar o método setVelocidade da class Aerogerador atribuindo no parametro do método a velocidade do vento para,
     * posteriormente calcular a potência gerada por um aerogerador com o método getPt da class Aerogerador e somar esse valor à variável res.
     * @param vel (Valor da velocidade do vento num certo instante e numa certa localização).
     * @return valor final da variável res quando o ciclo for termina sendo este valor o valor total total da energia/potência gerado por todos os aerogeradores.
     */

    public double getAllCalcPT(double vel){
        double res=0;
        for (Aerogerador aerogerador : lista) {
            aerogerador.setVelocidade(vel);
            res += aerogerador.getPt();
        }
        return res;
    }
}
