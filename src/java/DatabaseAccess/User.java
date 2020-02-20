package DatabaseAccess;

import DatabaseEntityClasses.UserProfileEntity;
import java.io.Serializable;

/**
 *
 * @author dapfel
 */

public class User implements Serializable {

    private static final long serialVersionUID = 1L;
    private Integer userID;
    private String username;
    private Integer wins;
    private Integer losses;
    private Integer draws;

    public User() {
    }

    public User(UserProfileEntity user) {
        this.userID = user.getUserID();
        this.username = user.getUsername();
        this.wins = user.getWins();
        this.losses = user.getLosses();
        this.draws = user.getDraws();
    }
    
    public User(String username) {
        this.username = username;
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
}
