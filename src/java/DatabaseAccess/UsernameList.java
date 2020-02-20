package DatabaseAccess;

import DatabaseEntityClasses.UserProfileEntity;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author dapfel
 */
public class UsernameList extends ArrayList<String> {
    public UsernameList() {
        super();
    }
    public UsernameList(List<UserProfileEntity> c) {
        for (UserProfileEntity user : c)
            this.add(user.getUsername());
    }

    public List<String> getUsernames() {
        return this;
    }
    public void setUsernames(List<String> usernames) {
        this.addAll(usernames);
    }
}
