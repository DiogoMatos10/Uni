/**
 * Class publica (Aerogerador).
 * Class constituida por um constructor e pelos métodos:
 * - setVelocidade
 * - getPt
 * - toArray
 *
 * @author Diogo de Matos e Miguel Gordicho
 */

public class Aerogerador {

    /**
     * Variáveis de instancia que armazenam os valores das caracteristicas de um aerogerador.
     * int tipo_gerador - tipo de gerador G1 ou G2 mas como a variável é do tipo integer então só pode armazenar valores inteiros 1 ou 2.
     * double raio - raio da zona de captação do vento, ou seja, o comprimento de cada pá do aerogerador.
     * double altitude - altitude onde foi instalado um aerogerador.
     * double alturaEixo - altura do eixo de rotação ao solo.
     * double longitude - longitude onde foi instalado um aerogerador.
     * double latitude - latitude onde foi instalado um aerogerador.
     * double vel - velocidade do vento num certo instante e localização.
     */

    int tipo_gerador;
    double raio;
    private double vel,altitude,alturaEixo,longitude,latitude;

    /**
     * Constructor da class Aerogerador que quarda nas variáveis de instância declaradas em cima os valores dos argumentos do parametro.
     * @param tipo_gerador - tipo de gerador
     * @param raio - raio da zona de captação do vento
     * @param altitude - altitude onde foi instalado um aerogerador
     * @param alturaEixo - altura do eixo de rotação ao solo
     * @param longitude - longitude onde foi instalado um aerogerador
     * @param latitude - velocidade do vento num certo instante e localização
     */

    public Aerogerador(int tipo_gerador,double raio,double altitude,double alturaEixo, double longitude, double latitude) {
        if(raio<=0 || altitude<=0 || alturaEixo<=0){
            System.out.println("Entrada inválida. Não há valores negativos. Apenas para a longitude e latitude");
        }else if(alturaEixo - raio < 4) {
            System.out.println("A diferença entre a altura do eixo até ao solo e o raio de envergadura das pás tem de ser maior ou igual a 4 metros.");
        }else if (tipo_gerador != 1 && tipo_gerador != 2) {
            System.out.println("O tipo de gerador só pode ser de tipo 1 ou tipo 2.");
        }else{
                this.tipo_gerador = tipo_gerador;
                this.alturaEixo = alturaEixo;
                this.raio = raio;
                this.altitude = altitude;
                this.longitude = longitude;
                this.latitude = latitude;
        }
    }

    /**
     * Método que recebe o valor da velocidade do vento num certo instante e o armazena.
     * Utilizado nos métodos getCalcPTNumInstante, getCalcPTVariosInstantes, getAllCalcPT da class Parque_Eolico.
     * @param vel (Velocidade do vento inserida pelo utilizador).
     */

    public void setVelocidade(double vel){
        try {
            if (vel < 0) {
                throw new Exception("O valor da velocidade deve ser positivo.");
            }else{
                this.vel=vel;
            }
        }catch (Exception e){
            System.out.println("Erro: "+ e.getMessage());
        }
    }

    /**
     * Método que retorna o valor da potência de energia gerada segundo um valor da velocidade do vento num certo instante e numa certa localização.
     * Utiliza o método getCalcDensidade da class Atmosfera e utiliza os métodos getCPA e getCPB das subclasses da class abstrata CP.
     * Utilizado nos métodos e class referenciados acima.
     * @return valor da Potência de Energia por um aerogerador.
     */

    public double getPt(){
        double area=Math.PI*Math.pow(this.raio,2);
        double Pv=0.50*Atmosfera.getCalcDensidade(this.altitude)*area*Math.pow(this.vel,3);
        if(this.tipo_gerador==1 && this.raio<8){
            return Pv*CenarioA.getCPA(this.vel);
        }
        else if(8>=this.raio || this.tipo_gerador==2) {
            return Pv*CenarioB.getCPB(this.vel);
        }
        return 0;
    }

    /**
     * Método responsável pelo retorno das caracteristicas de um aerogerador na forma de um array de doubles.
     * Utilizado nos métodos getAerogerador e getQuantAltitudeAcima da class Parque_Eolico.
     * @return Array de tamanho 6 com as caaracteristicas de um aerogerador, estando distribuido da seguinte forma:
     * 1ª posição - Tipo de gerador
     * 2ª posição - raio da zona de captação do vento
     * 3ª posição - altitude onde foi instalado um aerogerador
     * 4ª posição - altura do eixo de rotação ao solo
     * 5ª posição - longitude onde foi instalado um aerogerador
     * 6ª posição - latitude onde foi instalado um aerogerador
     */

    public double[] toArray(){
        double [] info=new double[6];
        info[0]=this.tipo_gerador;
        info[1]=this.raio;
        info[2]=this.altitude;
        info[3]=this.alturaEixo;
        info[4]=this.longitude;
        info[5]=this.latitude;
        return info;
    }
}
