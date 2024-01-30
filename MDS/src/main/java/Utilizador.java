public abstract class Utilizador {
    public String nome, email;
    private String password;
    public int id;


    public Utilizador(String nome, String email, String password, int id) {
        this.nome = nome;
        this.email = email;
        this.password = password;
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
