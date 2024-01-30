package rest;

import javax.xml.bind.annotation.*;

/*********************************
 * Artists
 * @author Henrique Rosa - l51923
 * @author Diogo Matos --- l54466
 *********************************/

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "artists")
public class Artists {
    @XmlElement(required = true)
    private String name;
    @XmlElement(required = true)
    private String typeArt;
    @XmlElement(required = true)
    private String locationLatitude;
    @XmlElement(required = true)
    private String locationLongitude;


    @Override
    public String toString() {
        return "Artists{" +
                "name='" + name + '\'' +
                ", typeArt='" + typeArt + '\'' +
                ", locationLatitude='" + locationLatitude + '\'' +
                ", locationLongitude='" + locationLongitude + '\'' +
                '}';
    }

    public Artists(){
        this.name="";
        this.typeArt="";
        this.locationLatitude="";
        this.locationLongitude="";
    }

    public Artists(String name, String typeArt, String locationLatitude, String locationLongitude) {
        this.name = name;
        this.typeArt = typeArt;
        this.locationLatitude = locationLatitude;
        this.locationLongitude = locationLongitude;
    }

    public String getName() {
        return name;
    }

    public String getTypeArt() {
        return typeArt;
    }

    public String getLocation() {
        return locationLatitude+","+locationLongitude;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTypeArt(String typeArt) {
        this.typeArt = typeArt;
    }

    public void setLocationLatitude(String locationLatitude) {
        this.locationLatitude = locationLatitude;
    }

    public void setLocationLongitude(String locationLongitude) {
        this.locationLongitude = locationLongitude;
    }
}

