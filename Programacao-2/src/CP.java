/**
 * Class abstrata publica (CP).
 * Constituida por duas subclasses (CenarioA e Cenario B).
 *
 * @author Diogo de Matos e Miguel Gordicho.
 */

public abstract class CP {
    /**
     * Variável de instância do tipo array double contendo esta os valores tabelados e constantes da velocidade.
     */

    public static final double[] val_tabeladosVel = {0, 2, 4, 6, 8, 10, 12, 14, 16, 18};
}

/**
 * Subclass (CenarioA) que é constituida pelo método getGPA que é responsável pelo retorno do valor do Coeficiente de Potência de um aerogerador segundo uma velocidade do vento inserida pelo utilizador.
 */

class CenarioA extends CP{

    /**
     * Método que retorna o valor do tipo double do Coefiente de Potência, utilizado no método getPt() da class Aerogerador.
     * O método recebe um valor da velocidade, se esse valor da velocidade for igual a algum dos valores tabelados da velocidade declarados no array de doubles na class abstracta CP o método retorna o valor de CP correspondente a essa velocidade do array de doubles val_tabeladosCPa.
     * Se o valor da velocidade não for igual a nenhum dos valores tabelados da velocidade, irá retornar o valor do cp para essa velocidade por interpolação linear.
     * @param vel (Velocidade do ar inserida pelo utilizador).
     * @return valor do Coefiente de Potência para o Cenário A segundo uma velociade do ar.
     */

    public static double getCPA(double vel){
        double[] val_tabeladosCPa = {0,0,0.02, 0.30, 0.40, 0.44, 0.41, 0.35, 0.26, 0.18};
        if(vel>=18){
            return 0.18;
        }else {
            for (int i = 0; i < val_tabeladosVel.length; i++) {
                if (vel == val_tabeladosVel[i]) {
                    return val_tabeladosCPa[i];
                }
                if (val_tabeladosVel[i] < vel && vel < val_tabeladosVel[i + 1]) {
                    return val_tabeladosCPa[i] + ((val_tabeladosCPa[i + 1] - val_tabeladosCPa[i]) / (val_tabeladosVel[i + 1] - val_tabeladosVel[i])) * (vel - val_tabeladosVel[i]);
                }
            }
        }
        return 0;
    }
}

/**
 * Subclass (CenarioB) que é constituida pelo método getGPB que é responsável pelo retorno do valor do Coeficiente de Potência de um aerogerador segundo uma velocidade do vento inserida pelo utilizador.
 */

class CenarioB extends CP{

    /**
     * Método que retorna o valor do tipo double do Coefiente de Potência, utilizado no método getPt() da class Aerogerador.
     * O método recebe um valor da velocidade, se esse valor da velocidade for igual a algum dos valores tabelados da velocidade declarados no array de doubles na class abstracta CP o método retorna o valor de CP correspondente a essa velocidade do array de doubles val_tabeladosCPb.
     * Se o valor da velocidade não for igual a nenhum dos valores tabelados da velocidade, irá retornar o valor do cp para essa velocidade por interpolação linear.
     * @param vel (Velocidade do vento inserida pelo utilizador).
     * @return valor do Coefiente de Potência para o Cenário B segundo uma velociade do ar.
     */

    public static double getCPB(double vel){
        double[] val_tabeladosCPb = {0,0,0.04, 0.18, 0.26, 0.36, 0.48, 0.52, 0.40, 0.28};
        if(vel>=18){
            return 0.28;
        }else {
            for (int i = 0; i < val_tabeladosVel.length; i++) {
                if (vel == val_tabeladosVel[i]) {
                    return val_tabeladosCPb[i];
                }
                if (val_tabeladosVel[i] < vel && vel < val_tabeladosVel[i + 1]) {
                    return val_tabeladosCPb[i] + ((val_tabeladosCPb[i + 1] - val_tabeladosCPb[i]) / (val_tabeladosVel[i + 1] - val_tabeladosVel[i])) * (vel - val_tabeladosVel[i]);
                }
            }
        }
        return 0;
    }
}
