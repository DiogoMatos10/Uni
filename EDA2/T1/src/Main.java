import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// Class que representa um grupo de jogos
class GameGroup {
    String name;
    int players;
    int enthusiasm;

    public GameGroup(String name, int players, int enthusiasm) {
        this.name = name;
        this.players = players;
        this.enthusiasm = enthusiasm;
    }
}

public class Main {
    static List<GameGroup> groups; // Lista para armazenar os grupos de jogos
    static int[][] dp; // Array para memorização de resultados
    static int[][] selected; // Array para marcar os grupos selecionados

    public static void main(String[] args) throws IOException {
        BufferedReader bf = new BufferedReader(new InputStreamReader(System.in)); // Uso de BufferedReader para a leitura de valores de entrada
        int availableSpace = Integer.parseInt(bf.readLine()); // Leitura do espaço válido
        int numGroups = Integer.parseInt(bf.readLine()); // Leitura da quantidade de grupos de jogos

        groups = new ArrayList<>(); // Inicialização da lista de grupos
        // Leitura de valores e inserção na lista de grupos de jogos
        for (int i = 0; i < numGroups; i++) {
            String[] parts = bf.readLine().split(" ");
            groups.add(new GameGroup(parts[0], Integer.parseInt(parts[1]), Integer.parseInt(parts[2])));
        }

        //Inicialização das matrizes
        dp = new int[numGroups + 1][availableSpace + 1];
        selected = new int[numGroups + 1][availableSpace + 1];

        // Chamada do método selectGames para selecionar os grupos de jogos e ainda o retorno do máximo de entusiasmo obtido
        int maxEnthusiasm = selectGames(numGroups, availableSpace);

        // Lista para adicionar os grupos de jogos selecionados
        List<String> selectedGames = new ArrayList<>();

        int i = numGroups, j = availableSpace;
        // loop que percorre todas as possições da matriz selected e caso uma posição seja igual a 1, indica que o grupo foi selecionado
        while (i > 0 && j > 0) {
            if (selected[i][j] == 1) {
                // Adiciona o nome do grupo à lista de jogos selecionados
                selectedGames.add(groups.get(i - 1).name);
                // Redução do valor j, pelo número de jogadores do grupo selecionado. Isso é feito para percorrer todos os grupos de jogos que foram marcados como selecionados
                j -= groups.get(i - 1).players;
            }
            // decrementação de i para mover para a próxima linha da matriz. É feito para percorrer todos os grupos de jogos selecionados
            i--;
        }

        // Inverte a ordem dos elementos do "selectedGames", necessário porque os jogos foram adicionados à lista na ordem inversa da sua solicitação
        Collections.reverse(selectedGames);

        // Print do output
        System.out.println(selectedGames.size() + " " + (availableSpace - j) + " " + maxEnthusiasm);
        for (String game : selectedGames) {
            System.out.println(game);
        }
    }

    static int selectGames(int numGroups, int availableSpace) {
        // Loop externo que percorre cada grupo de jogos
        for (int i = 1; i <= numGroups; i++) {
            // Loop interno que percorre cada valor possivel de espaço disponivel
            for (int j = 1; j <= availableSpace; j++) {
                // Verifica se o número de jogadores do jogo atual excede o espaço disponivel. Se for o caso, não é possivel incluir o jogo no espaço disponivel atual
                if (groups.get(i - 1).players > j) {
                    // Atribuição do valor do jogo anterior a dp[i][j]
                    dp[i][j] = dp[i - 1][j];
                } else {
                    // Representa o entusiasmo atual se incluirmos o jogo atual
                    int includeCurrent = groups.get(i - 1).enthusiasm + dp[i - 1][j - groups.get(i - 1).players];
                    // Representa o entusiasmo total se excluirmos atual. O que representa total dos jogos anteriores sem incluir o jogo atual
                    int excludeCurrent = dp[i - 1][j];
                    // Se o valor do entusiamo do jogo atual for maior que o valor só com o jogo anterior, então atribuimos a dp[i][j] o valor de incluideCurrent e definimos o jogo atual como selecionado
                    if (includeCurrent > excludeCurrent) {
                        dp[i][j] = includeCurrent;
                        selected[i][j] = 1;
                    } else { // Senão atribuimos o valor do entusiasmo sem o atual a dp[i][j] e definimos o jogo atual como não selecionado
                        dp[i][j] = excludeCurrent;
                        selected[i][j] = 0;
                    }
                }
            }
        }
        // Retorno do valor máximo de entusiasmo obtido
        return dp[numGroups][availableSpace];
    }
}