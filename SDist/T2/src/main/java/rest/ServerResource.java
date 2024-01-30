package rest;
import jakarta.ws.rs.*;

/*********************************
 * ServerResource
 * @author Henrique Rosa - l51923
 * @author Diogo Matos --- l54466
 *********************************/

@Path("/api")
public class ServerResource {

    private Management management;
    public ServerResource() {
        this.management = new Management();
    }

    @Path("/newUser")
    @POST
    @Consumes({"application/json"})
    public synchronized String newUser(Users users) throws Exception {
        System.out.println("Registando um novo user:" + users.getName());

        if (users.getName() != null && users.getEmail() != null && users.getPwd() != null && users.getRole() != null) {
            return this.management.newUser(users);
        }
        return "Os valores não podem ser nulos.";
    }

    @Path("/login")
    @GET
    @Produces({"application/json"})
    public synchronized String login(@QueryParam("name") String name, @QueryParam("password") String password){
        System.out.println("Tentativa de login.");

        if(name!=null && password!=null){
            return this.management.login(name,password);
        }
        return "";
    }

    @Path("/approveAdmin")
    @GET
    @Produces({"application/json"})
    public synchronized String approveAdmin(@QueryParam("username") String username) throws Exception {
        System.out.println("Tentativa de aprovação de admin.");
        return this.management.approveAdmin(username);
    }

    @Path("/insertArtist")
    @POST
    @Consumes({"application/json"})
    public synchronized String insertArtist(Artists artists) throws Exception {
        System.out.println("Registando um novo artista:"+artists.getName());

        if (artists.getName() != null && artists.getTypeArt() != null && artists.getLocation() != null) {
            return this.management.insertArtist(artists);
        }
        return "Os valores não podem ser nulos.";
    }

    @Path("/infoArtist")
    @GET
    @Produces({"application/json"})
    public synchronized String infoArtist(@QueryParam("artistName") String artistName) throws Exception {
        System.out.println("Tentativa de consulta de info de artista.");
        return this.management.infoArtist(artistName);
    }

    @Path("/updateArtist")
    @GET
    @Produces({"application/json"})
    public synchronized String updateArtist(@QueryParam("artistID") String artistID,@QueryParam("name") String name, @QueryParam("typeArt") String typeArt, @QueryParam("locationLatitude") String locationLatitude, @QueryParam("locationLongitude") String locationLongitude){
        System.out.println("Tentativa de update de artista.");
        return this.management.updateArtist(artistID,name,typeArt,locationLatitude,locationLongitude);
    }

    @Path("/listArtists")
    @GET
    @Produces({"application/json"})
    public synchronized String listArtists(@QueryParam("typeArt") String typeArt, @QueryParam("locationLatitude") String locationLatitude, @QueryParam("locationLongitude") String locationLongitude){
        System.out.println("Tentativa de consulta de artistas.");
        return this.management.listArtists(typeArt,locationLatitude,locationLongitude);
    }

    @Path("/listArtistsWithStatus")
    @GET
    @Produces({"application/json"})
    public synchronized String listArtistsWithStatus(){
        System.out.println("Tentativa de consulta de artistas com status.");
        return this.management.listArtistsWithStatus();
    }

    @Path("/approveArtist")
    @GET
    @Produces({"application/json"})
    public synchronized String approveArtist(@QueryParam("artistID") String artistID) throws Exception {
        System.out.println("Tentativa de aprovação de artista.");
        return this.management.approveArtist(artistID);
    }

    @Path("/listPerformancesLive")
    @GET
    @Produces({"application/json"})
    public synchronized String listPerformancesLive(){
        System.out.println("Tentativa de consulta de espetáculos a decorrer.");
        return this.management.listPerformancesLive();
    }

    @Path("/listPerformancesPrevious")
    @GET
    @Produces({"application/json"})
    public synchronized String listPerformancesPrevious(@QueryParam("name") String name) throws Exception {
        System.out.println("Tentativa de consulta de espetáculos já realizados por: "+name);
        return this.management.listPerformancesPrevious(name);
    }

    @Path("/listPerformancesFuture")
    @GET
    @Produces({"application/json"})
    public synchronized String listPerformancesFuture(@QueryParam("name") String name) throws Exception {
        System.out.println("Tentativa de consulta de próximos espetáculos de: "+name);
        return this.management.listPerformancesFuture(name);
    }

    @Path("/donateArtist")
    @POST
    @Consumes({"application/json"})
    public synchronized String donateArtist(Donations donations) throws Exception {
        System.out.println("Registando uma nova donation");

        if (donations.getUserID() != 0 && donations.getArtistName() != null && donations.getValue() != 0) {
            return this.management.donateArtist(donations);
        }
        return "Os valores não podem ser nulos.";
    }

    @Path("/listDonations")
    @GET
    @Produces({"application/json"})
    public synchronized String listDonations(@QueryParam("name") String name){
        System.out.println("Tentativa de consulta de doações recebidas pelo artista:"+name);
        return this.management.listDonations(name);
    }

    @Path("/ratingArtist")
    @POST
    @Consumes({"application/json"})
    public synchronized String ratingArtist(Ratings ratings) throws Exception {
        System.out.println("Registando uma nova classificação.");

        if (ratings.getName() != null && ratings.getRating() != 0) {
            return this.management.ratingArtist(ratings);
        }
        return "Os valores não podem ser nulos.";
    }

}