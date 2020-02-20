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
    @NamedQuery(name = "GameRequestEntity.findAll", query = "SELECT g FROM GameRequestEntity g")
    , @NamedQuery(name = "GameRequestEntity.findByRequestingUser", query = "SELECT g FROM GameRequestEntity g WHERE g.gamerequestPK.requestingUser = :requestingUser")
    , @NamedQuery(name = "GameRequestEntity.findByRequestedUser", query = "SELECT g FROM GameRequestEntity g WHERE g.gamerequestPK.requestedUser = :requestedUser")
    , @NamedQuery(name = "GameRequestEntity.findByGameID", query = "SELECT g FROM GameRequestEntity g WHERE g.gameID = :gameID")})
public class GameRequestEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected GameRequestPKEntity gamerequestPK;
    @Column(name = "gameID")
    private Integer gameID;
    @JoinColumn(name = "requestedUser", referencedColumnName = "username", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private UserProfileEntity user;
    @JoinColumn(name = "requestingUser", referencedColumnName = "username", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private UserProfileEntity user1;

    public GameRequestEntity() {
    }

    public GameRequestEntity(GameRequestPKEntity gamerequestPK) {
        this.gamerequestPK = gamerequestPK;
    }

    public GameRequestEntity(String requestingUser, String requestedUser) {
        this.gamerequestPK = new GameRequestPKEntity(requestingUser, requestedUser);
    }

    public GameRequestPKEntity getGamerequestPK() {
        return gamerequestPK;
    }

    public void setGamerequestPK(GameRequestPKEntity gamerequestPK) {
        this.gamerequestPK = gamerequestPK;
    }

    public Integer getGameID() {
        return gameID;
    }

    public void setGameID(Integer gameID) {
        this.gameID = gameID;
    }

    public UserProfileEntity getUser() {
        return user;
    }

    public void setUser(UserProfileEntity user) {
        this.user = user;
    }

    public UserProfileEntity getUser1() {
        return user1;
    }

    public void setUser1(UserProfileEntity user1) {
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
        if (!(object instanceof GameRequestEntity)) {
            return false;
        }
        GameRequestEntity other = (GameRequestEntity) object;
        if ((this.gamerequestPK == null && other.gamerequestPK != null) || (this.gamerequestPK != null && !this.gamerequestPK.equals(other.gamerequestPK))) {
            return false;
        }
        return true;
    }

    
}
