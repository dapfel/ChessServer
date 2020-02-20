package DatabaseEntityClasses;

import DatabaseAccess.User;
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

/**
 *
 * @author dapfel
 */
@Entity
@Table(name = "userprofile")
@NamedQueries({
    @NamedQuery(name = "UserProfileEntity.findAll", query = "SELECT u FROM UserProfileEntity u")
    , @NamedQuery(name = "UserProfileEntity.findByUserID", query = "SELECT u FROM UserProfileEntity u WHERE u.userID = :userID")
    , @NamedQuery(name = "UserProfileEntity.findByUsername", query = "SELECT u FROM UserProfileEntity u WHERE u.username = :username")
    , @NamedQuery(name = "UserProfileEntity.findByPassword", query = "SELECT u FROM UserProfileEntity u WHERE u.password = :password")
    , @NamedQuery(name = "UserProfileEntity.findByWins", query = "SELECT u FROM UserProfileEntity u WHERE u.wins = :wins")
    , @NamedQuery(name = "UserProfileEntity.findByLosses", query = "SELECT u FROM UserProfileEntity u WHERE u.losses = :losses")
    , @NamedQuery(name = "UserProfileEntity.findByAvailable", query = "SELECT u FROM UserProfileEntity u WHERE u.available = :available")
    , @NamedQuery(name = "UserProfileEntity.findByDraws", query = "SELECT u FROM UserProfileEntity u WHERE u.draws = :draws")})
public class UserProfileEntity implements Serializable {

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
    private List<GameRequestEntity> requestedGameList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user1")
    private List<GameRequestEntity> requesterGameList;

    public UserProfileEntity() {
    }

    public UserProfileEntity(User user, String password) {
        this.userID = user.getUserID();
        this.username = user.getUsername();
        this.password = password;
        this.wins = user.getWins();
        this.losses = user.getLosses();
        this.draws = user.getDraws();
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

    public List<GameRequestEntity> getRequestedGameList() {
        return requestedGameList;
    }

    public void setRequestedGameList(List<GameRequestEntity> requestedGameList) {
        this.requestedGameList = requestedGameList;
    }

    public List<GameRequestEntity> getRequesterGameList() {
        return requesterGameList;
    }

    public void setRequesterGameList(List<GameRequestEntity> requesterGameList) {
        this.requesterGameList = requesterGameList;
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
        if (!(object instanceof UserProfileEntity)) {
            return false;
        }
        UserProfileEntity other = (UserProfileEntity) object;
        if ((this.userID == null && other.userID != null) || (this.userID != null && !this.userID.equals(other.userID))) {
            return false;
        }
        return true;
    }
    
}
