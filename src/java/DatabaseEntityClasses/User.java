package DatabaseEntityClasses;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author dapfel
 */
@Entity
@Table(name = "users")
@NamedQueries({
    @NamedQuery(name = "User.findAll", query = "SELECT u FROM User u")
    , @NamedQuery(name = "User.findByUserID", query = "SELECT u FROM User u WHERE u.userID = :userID")
    , @NamedQuery(name = "User.findByUsername", query = "SELECT u FROM User u WHERE u.username = :username")
    , @NamedQuery(name = "User.findByPassword", query = "SELECT u FROM User u WHERE u.password = :password")
    , @NamedQuery(name = "User.findByWins", query = "SELECT u FROM User u WHERE u.wins = :wins")
    , @NamedQuery(name = "User.findByLosses", query = "SELECT u FROM User u WHERE u.losses = :losses")
    , @NamedQuery(name = "User.findByAvailable", query = "SELECT u FROM User u WHERE u.available = :available")
    , @NamedQuery(name = "User.findByDraws", query = "SELECT u FROM User u WHERE u.draws = :draws")})
@XmlRootElement
public class User implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "UserID")
    private Integer userID;
    @Size(max = 40)
    @Column(name = "username", unique = true)
    private String username;
    @Size(max = 30)
    @Column(name = "password")
    private String password;
    @Column(name = "wins")
    private Integer wins;
    @Column(name = "losses")
    private Integer losses;
    @Column(name = "draws")
    private Integer draws;
    @Column(name = "available")
    private Boolean available;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    private List<Gamerequest> gamerequestList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user1")
    private List<Gamerequest> gamerequestList1;

    public User() {
    }

    public User(Integer userID) {
        this.userID = userID;
    }

    public Integer getUserID() {
        return userID;
    }

    public void setUserID(Integer userID) {
        this.userID = userID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getWins() {
        return wins;
    }

    public void setWins(Integer wins) {
        this.wins = wins;
    }

    public Integer getLosses() {
        return losses;
    }

    public void setLosses(Integer losses) {
        this.losses = losses;
    }

    public Integer getDraws() {
        return draws;
    }

    public void setDraws(Integer draws) {
        this.draws = draws;
    }

    public Boolean getAvailable() {
        return available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }
    
    @XmlTransient
    public List<Gamerequest> getGamerequestList() {
        return gamerequestList;
    }

    public void setGamerequestList(List<Gamerequest> gamerequestList) {
        this.gamerequestList = gamerequestList;
    }

    @XmlTransient
    public List<Gamerequest> getGamerequestList1() {
        return gamerequestList1;
    }

    public void setGamerequestList1(List<Gamerequest> gamerequestList1) {
        this.gamerequestList1 = gamerequestList1;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (userID != null ? userID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof User)) {
            return false;
        }
        User other = (User) object;
        if ((this.userID == null && other.userID != null) || (this.userID != null && !this.userID.equals(other.userID))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "DatabaseEntityClasses.Users[ userID=" + userID + " ]";
    }
    
}
