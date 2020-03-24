package DatabaseEntityClasses;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.Size;

/**
 *
 * @author dapfel
 */
@Entity
@Table(name = "game")
@NamedQueries({
    @NamedQuery(name = "GameEntity.findAll", query = "SELECT g FROM GameEntity g")
    , @NamedQuery(name = "GameEntity.findByGameID", query = "SELECT g FROM GameEntity g WHERE g.gameID = :gameID")
    , @NamedQuery(name = "GameEntity.findByMove", query = "SELECT g FROM GameEntity g WHERE g.move = :move")})
public class GameEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "gameID")
    private Integer gameID;
    @Size(max = 20)
    @Column(name = "move")
    private String move;
    @Size(max = 40)
    @Column(name = "player1")
    private String player1;
    @Size(max = 40)
    @Column(name = "player2")
    private String player2;

    public GameEntity() {
    }
    
    public GameEntity(GameRequestPKEntity gameRequestPK) {
        player1 = gameRequestPK.getRequestingUser();
        player2 = gameRequestPK.getRequestedUser();
    }

    public Integer getGameID() {
        return gameID;
    }

    public void setGameID(Integer gameID) {
        this.gameID = gameID;
    }

    public String getMove() {
        return move;
    }

    public void setMove(String move) {
        this.move = move;
    }

    public String getPlayer1() {
        return player1;
    }

    public void setPlayer1(String player1) {
        this.player1 = player1;
    }

    public String getPlayer2() {
        return player2;
    }

    public void setPlayer2(String player2) {
        this.player2 = player2;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (gameID != null ? gameID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof GameEntity)) {
            return false;
        }
        GameEntity other = (GameEntity) object;
        return !((this.gameID == null && other.gameID != null) || (this.gameID != null && !this.gameID.equals(other.gameID)));
    }


}
