package src;

/*********************************
 * Remote_ClientGen
 * @author Henrique Rosa - l51923
 * @author Diogo Matos --- l54466
 *********************************/

//interface remota dos métodos da aplicação do cliente geral
public interface Remote_ClientGen extends java.rmi.Remote{
    public String insertArtist(String str) throws java.rmi.RemoteException;
    public String listArtists(String str) throws java.rmi.RemoteException;
    public String listPerformLocations() throws java.rmi.RemoteException;
    public String donateArtist(String str) throws java.rmi.RemoteException;
    public String listShow(String str) throws java.rmi.RemoteException;
    public String listDonations(String str) throws java.rmi.RemoteException;
}
