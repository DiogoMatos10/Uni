package rest;

import javax.xml.bind.annotation.*;

/*********************************
 * Donations
 * @author Henrique Rosa - l51923
 * @author Diogo Matos --- l54466
 *********************************/

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "donations")
public class Donations {
    @XmlElement(required = true)
    private int userID;
    @XmlElement(required = true)
    private String artistName;
    @XmlElement(required = true)
    private int value;

    @Override
    public String toString() {
        return "Donations{" +
                "userID='" + userID + '\'' +
                ", artistName='" + artistName + '\'' +
                ", value='" + value + '\'' +
                '}';
    }

    public Donations() {
        this.userID = 0;
        this.artistName="";
        this.value =0;
    }

    public Donations(int userID, String artistName, int value) {
        this.userID = userID;
        this.artistName=artistName;
        this.value = value;
    }


    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
