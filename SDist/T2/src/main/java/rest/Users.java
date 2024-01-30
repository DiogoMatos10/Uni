package rest;

import javax.xml.bind.annotation.*;

/*********************************
 * Users
 * @author Henrique Rosa - l51923
 * @author Diogo Matos --- l54466
 *********************************/

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name ="users")
public class Users {

    @XmlElement(required = true)
    private String name;
    @XmlElement(required = true)
    private String email;
    @XmlElement(required = true)
    private String pwd;
    @XmlElement(required = true)
    private String role;


    @Override
    public String toString() {
        return "Users{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", pwd='" + pwd + '\'' +
                ", role='" + role + '\'' +
                '}';
    }

    public Users() {
        this.name="";
        this.email="";
        this.pwd="";
        this.role="";
    }

    public Users(String name,
                 String email,
                 String pwd,
                 String role) {
        this.name = name;
        this.email = email;
        this.pwd = pwd;
        this.role = role;
    }

    public String getName() {
        return name;
    }
    public String getEmail() {
        return email;
    }
    public String getPwd() {
        return pwd;
    }
    public String getRole(){
        return role;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public void setRole(String role) {
        this.role = role;
    }
}