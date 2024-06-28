import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;


/**
 * Classe que representa um vertice no grafo que será utilizado para encontrar o caminho de volta.
 * @author Diogo Matos & Ivo Rego*/

class Vertex {
    char color; //Cor do vértice para marcar durante a pesquisa
    int d; //Distãncia do vértice ao ponto de partida (onde começaram os movimentos)
    int x; //Coordenada x do vértice
    int y; //Coordenada y do vértice
    Vertex parent; //Vértice pai
    String name; //Identificador do vertice (N, S, W, E)
    List<Vertex> adjacents; //Lista de vértices adjacentes

    public Vertex() {
        this.adjacents = new ArrayList<>();
    }
}


/**
 * Classe principal do programa onde é lido os ‘inputs’ no terminal pelo método main e onde é feito a pesquisa em largura para encontrar o percurso mais curto pelo método minMovements.
 * @author Diogo Matos & Ivo Rego*/

public class Main {

    private static final char WHITE = 'W'; //Representa o vértice ainda não visitado
    private static final char GREY = 'G'; //Representa o vértice está na lista de processamento
    private static final char BLACK = 'B'; //Representa o vértice já processado
    private static final HashMap<String, List<Vertex>> vertexMap = new HashMap<>(); //HashMap para armazenar vértices por coordenada

    /**
     * Método responsável por ler o ‘input’, que consiste na sequência de movimentos feitos. Esses movimentos são armazenados numa ‘string’ e passado para o método minMoviments que calula o número mínimo de movimentos para voltar ao ponto de partida.
     * @author Diogo Matos & Ivo Rego
     */

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        int L = Integer.parseInt(br.readLine().trim()); //Leitura do número de linhas de movimentos
        StringBuilder concatenatedMovements = new StringBuilder();
        for (int i = 0; i < L; i++) {
            concatenatedMovements.append(br.readLine().trim()); //Leitura linha a linha e armazenamento no StringBuilder
        }

        String[] concatenatedString = new String[]{concatenatedMovements.toString()}; //Conversão para array de strings

        int minMovements = minMovements(concatenatedString); //Chamada do método para calcular o número mínimo de movimentos

        System.out.println(minMovements); //Print do resultado
    }

    /**
     * Método responsável por retornar o número mínimo de movimentos para voltar ao ponto de partida.
     * @param movements Array de string que contei o conjunto de movimentos feitos
     * @return Número mínimo de movimentos para voltar ao inicio do percurso
     * @author Diogo Matos & Ivo Rego
     */

    private static int minMovements(String[] movements) {
        int n = movements[0].length() + 1; //Número máximo de movimentos possíveis incluindo o ponto de partida

        Vertex[] vertices = new Vertex[n]; //Array de vértices para representar o grafo
        vertices[0] = new Vertex(); //Inicialização do ponto de partida
        vertices[0].d = Integer.MAX_VALUE; //Distância inicial infinita
        vertices[0].parent = vertices[0]; //Vértice inicial é o seu próprio vértice pai
        vertices[0].color = WHITE; //Cor inicial branca (não visitado)
        vertices[0].x = 0; //Coordenada x do ponto de partida
        vertices[0].y = 0; //Coordenada y do ponto de partida
        addVertexToMap(vertices[0]); //Adição do vértice inicial ao hashMap pelo método addVertexToMap

        String[] names = movements[0].split(""); //Divisão da primeira linha de movimentos em caracteres individuais
        int x = 0; //Coordenada x inicial
        int y = 0; //Coordenada y inicial

        for (int j = 1; j < n; j++) {
            vertices[j] = new Vertex(); //Criação de um novo vértice
            vertices[j].color = WHITE; //Definição da cor como branca (vértice não visitado)
            vertices[j].name = names[j - 1]; //Definição do nome do vértice como a direção do movimento
            vertices[j].parent = vertices[j - 1]; //Definição do vértice pai é o vértice anterior
            Vertex parent = vertices[j].parent;
            parent.adjacents.add(vertices[j]); //Adição do vértice atual como adjacente ao seu vértice pai
            vertices[j].adjacents.add(parent); //Adição do vértice pai como adjacente ao vértice atual

            //Atualização das coordenadas baseado no movimento
            switch (names[j - 1]) {
                case "N":
                    vertices[j].y = y + 1;
                    vertices[j].x = x;
                    y++;
                    break;
                case "E":
                    vertices[j].x = x + 1;
                    vertices[j].y = y;
                    x++;
                    break;
                case "S":
                    vertices[j].y = y - 1;
                    vertices[j].x = x;
                    y--;
                    break;
                case "W":
                    vertices[j].x = x - 1;
                    vertices[j].y = y;
                    x--;
                    break;
            }

            addVertexToMap(vertices[j]); //Adição do vértice atual ao hashMap pelo método addVertexToMap
        }

        //Pesquisa em largura para encontrar o número mínimo de movimentos, esta mesma começa pelo vertice "end" que é o último vertice do percurso

        Queue<Vertex> queue = new LinkedList<>(); //Queue que vai guardando os vértices durante a pesquisa
        Vertex end = vertices[movements[0].length()]; //Definição do vértice inicial da pesquisa como o último vértice do percurso
        Vertex start = vertices[0]; //Definição do vertice final da pesquisa como o primeiro vértice do percurso

        end.color = GREY; //Marca o vértice como cinza (em processamento)
        queue.add(end); //Adição do vértice à queue

        while (!queue.isEmpty()) {
            Vertex u = queue.remove(); //Remoção do vértice da queue

            if (u.x == start.x && u.y == start.y) { //Caso se chegue ao primeiro vértice do percurso (start), retorna-se o número minimo de movimentos
                return u.d;
            }

            int[] dx = {0, 1, 0, -1}; //Variações de coordenada x para cada direção
            int[] dy = {-1, 0, 1, 0}; //Variações de coordenada y para cada direção

            for (int k = 0; k < 4; k++) {
                int newX = u.x + dx[k]; //Calculo da nova coordenada x
                int newY = u.y + dy[k]; //Calculo da nova coordenada y

                String key = newX + "," + newY; //Chave para procurar no hashMap
                if (vertexMap.containsKey(key)) { //Verifica se a coordenada existe no hashMap
                    List<Vertex> verticesAtPosition = vertexMap.get(key); //Obtém os vértices que tem na coordenada
                    for (Vertex v : verticesAtPosition) { //Percorre os vértices na coordenada
                        for (Vertex vi : v.adjacents) {
                            if ((vi != null) && ((vi.x == u.x && vi.y == u.y) || (v.x == u.x && v.y == u.y))) {
                                if (v.color == WHITE) { //Se o vértice ainda não foi visitado
                                    v.color = GREY; //Marca o vértice como cinza (em processamento)
                                    v.d = u.d + 1; //Atualiza a distância
                                    queue.add(v); //Adição do vértice à queue
                                }
                            }
                        }
                    }
                }
            }
            u.color = BLACK;  //Marcação do vértice como preto (visitado)
        }
        return 1;
    }


    /**
     * Método responsável por adicionar um vértice ao hashMap baseado na sua coordenada
     * @param vertex Vértice a adiconar
     * @author Diogo Matos & Ivo Rego
     */

    private static void addVertexToMap(Vertex vertex) {
        String key = vertex.x + "," + vertex.y; //Chave para a coordenada do vértice
        List<Vertex> verticesAtPosition = vertexMap.get(key); //Obtém a lista de vértices na coordenada
        if (verticesAtPosition == null) { //Se não houver vértices na coordenada
            verticesAtPosition = new ArrayList<>();
            vertexMap.put(key, verticesAtPosition); //Adição de uma nova lista para a coordenada no hashMap
        }
        verticesAtPosition.add(vertex); //Adição do vértice à lista de vértices na coordenada
    }
}
