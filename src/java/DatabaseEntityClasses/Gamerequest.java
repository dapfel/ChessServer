package DatabaseEntityClasses;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 *
 * @author user
 */
@Entity
@Table(name = "gamerequest")
@NamedQueries({
    @NamedQuery(name = "Gamerequest.findAll", query = "SELECT g FROM Gamerequest g")
    , @NamedQuery(name = "Gamerequest.findByRequestingUser", query = "SELECT g FROM Gamerequest g WHERE g.gamerequestPK.requestingUser = :requestingUser")
    , @NamedQuery(name = "Gamerequest.findByRequestedUser", query = "SELECT g FROM Gamerequest g WHERE g.gamerequestPK.requestedUser = :requestedUser")
    , @NamedQuery(name = "Gamerequest.findByGameID", query = "SELECT g FROM Gamerequest g WHERE g.gameID = :gameID")})
public class Gamerequest implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected GamerequestPK gamerequestPK;
    @Column(name = "gameID")
    private Integer gameID;
    @JoinColumn(name = "requestedUser", referencedColumnName = "username", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private User user;
    @JoinColumn(name = "requestingUser", referencedColumnName = "username", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private User user1;

    public Gamerequest() {
    }

    public Gamerequest(GamerequestPK gamerequestPK) {
        this.gamerequestPK = gamerequestPK;
    }

    public Gamerequest(String requestingUser, String requestedUser) {
        this.gamerequestPK = new GamerequestPK(requestingUser, requestedUser);
    }

    public GamerequestPK getGamerequestPK() {
        return gamerequestPK;
    }

    public void setGamerequestPK(GamerequestPK gamerequestPK) {
        this.gamerequestPK = gamerequestPK;
    }

    public Integer getGameID() {
        return gameID;
    }

    public void setGameID(Integer gameID) {
        this.gameID = gameID;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser1() {
        return user1;
    }

    public void setUser1(User user1) {
        this.user1 = user1;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (gamerequestPK != null ? gamerequestPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Gamerequest)) {
            return false;
        }
        Gamerequest other = (Gamerequest) object;
        if ((this.gamerequestPK == null && other.gamerequestPK != null) || (this.gamerequestPK != null && !this.gamerequestPK.equals(other.gamerequestPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "DatabaseEntityClasses.Gamerequest[ gamerequestPK=" + gamerequestPK + " ]";
    }
    
}
