package rest;

import javax.xml.bind.annotation.*;

/*********************************
 * Ratings
 * @author Henrique Rosa - l51923
 * @author Diogo Matos --- l54466
 *********************************/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "ratings")
public class Ratings {
    @XmlElement(required = true)
    private String name;
    @XmlElement(required = true)
    private Integer rating;

    @Override
    public String toString() {
        return "Ratings{" +
                "name='" + name + '\'' +
                ", rating='" + rating + '\'' +
                '}';
    }

    public  Ratings(){
        this.name="";
        this.rating=0;
    }
    public Ratings(String name,Integer rating) {
        this.name=name;
        this.rating=rating;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }
}
