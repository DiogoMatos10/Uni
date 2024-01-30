package src;

/*********************************
 * Remote_ClientAdm
 * @author Henrique Rosa - l51923
 * @author Diogo Matos --- l54466
 *********************************/

//interface remota dos métodos da aplicação do cliente administrador
public interface Remote_ClientAdm extends java.rmi.Remote {
    public String getListArtist() throws java.rmi.RemoteException;
    public String updateArtist(String id) throws java.rmi.RemoteException;
}
