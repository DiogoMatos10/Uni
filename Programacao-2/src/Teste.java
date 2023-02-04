import java.util.Arrays;

/**
 * Class publica (Teste).
 * Class que demonstra e testa as classes criadas e os seus respetivos métodos.
 * De recordar que a forma como está disposta um array com as caracteristicas é [tipo_gerador,raio,altitude,altura do eixo,longitude,latitude].
 * Requer a biblioeteca java.util.Arrays para passar os valores retornados em forma de array para string.
 *
 * @author Diogo de Matos e Miguel Gordicho.
 */

public class Teste {
    public static void main(String[] args) {
        Parque_Eolico parque_eolico=new Parque_Eolico();


        //Declaração dos aerogeradores com as caracteristicasa
        Aerogerador aerogerador1=new Aerogerador(1,2,4,8,5,10);
        Aerogerador aerogerador2=new Aerogerador(2,10,50,15,100,120);
        Aerogerador aerogerador3=new Aerogerador(1,4,10,10,66,360);
        //Demonstração quando um aerogerador tem caracteristicas que não cumprem os requesitos:
        Aerogerador aerogerador4=new Aerogerador(5,2,3,15,200,220);
        System.out.println("\n");


        //Adicionar aerogeradores ao Parque Eólico
        parque_eolico.addAerogerador(aerogerador1);
        parque_eolico.addAerogerador(aerogerador2);
        parque_eolico.addAerogerador(aerogerador3);
        //Demonstração quando um aerogerador tem caracteristicas que não cumprem os requesitos e queremos adicionar:
        parque_eolico.addAerogerador(aerogerador4);


        //Remover um aerogerador do Parque Eólico
        parque_eolico.removeAerogerador(1);
        //Demonstração em como foi removido
        System.out.println("\nTeste em como foi removido: "+Arrays.toString(parque_eolico.getAerogerador(1)));


        //Quantidade de aerogeradores existentes cuja turbina está instalada acima de determinada altura ao solo
        System.out.println("\nQuantidade de aerogeradores existentes: "+parque_eolico.getQuantAero());


        //Quantidade de aerogeradores existentes com altura acima ou igual
        System.out.println("\nQuantidade de aerogeradores existentes com altura acima ou igual: "+parque_eolico.getQuantAlturaAcima(15)+"\n");


        //Manuntenção do rotor e do gerador
        parque_eolico.manutencaoAmbos(1,3,2);
        //Demonstração do aerogerador com as novas caracteristicas
        System.out.println("Teste de manuntenção de ambos: "+Arrays.toString(parque_eolico.getAerogerador(1))+ "\n");

        //Manuntenção do rotor
        parque_eolico.manutencaoRotor(2,2);
        //Demonstração do aerogerador com as novas caracteristicas
        System.out.println("Teste de manuntenção do rotor: "+Arrays.toString(parque_eolico.getAerogerador(2))+"\n");


        //Manuntenção do gerador
        parque_eolico.manutencaoGerador(2,2);
        System.out.println("Teste de manuntenção do gerador: "+Arrays.toString(parque_eolico.getAerogerador(2))+"\n");


        //Cálculo da energia/potência gerado por um aerogerador num instante, ou seja, segundo uma velocidade
        System.out.println("Teste do calculo da energia/potência para um instante: "+parque_eolico.getCalcPTNumInstante(1,7.5)+"\n");


        //Cálculo da energia/potência total gerada por todos os aerogeradores existentes
        System.out.println("Teste do calculo da energia/potência total para um instante: "+parque_eolico.getAllCalcPT(7.5)+"\n");


        //Cálculo da energia/potência gerada por um aerogerador para várias instantes/dias, ou seja, várias velocidades
        parque_eolico.addVelocidades(2);
        parque_eolico.addVelocidades(4);
        parque_eolico.addVelocidades(4);
        System.out.println("Primeiro teste para o cálculo da energia/potência para varios instantes/dias: "+Arrays.toString(parque_eolico.getCalcPTVariosInstantes(1))+"\n");
        parque_eolico.clearVelocidades(); //Clear à coleção de velocidades
        parque_eolico.addVelocidades(4);
        parque_eolico.addVelocidades(6);
        parque_eolico.addVelocidades(8);
        System.out.println("Segundo teste para o cálculo da energia/potência para varios instantes/dias: "+Arrays.toString(parque_eolico.getCalcPTVariosInstantes(2))+"\n");

    }
}

