package DatabaseAccess;

import com.google.gson.Gson;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import DatabaseEntityClasses.User;
import java.util.List;


/**
 * REST Web Service
 *
 * @author dapfe
 */
@Path("user")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {

    private ChessDbController chessDB;

    public UserResource() {
        chessDB = new ChessDbController();
    }

    @GET
    @Path("signIn/{username}/{password}")
    public String validateSignIn(@PathParam("username") String username, @PathParam("password") String password) {
       String userJson = new Gson().toJson(chessDB.validateSignIn(username, password));
       chessDB.close();
       return userJson;
    }
    
    @POST
    @Path("")
    public String addUser(String userJson) {
       User user = new Gson().fromJson(userJson, User.class);
       try {
           user = chessDB.addUser(user);
       }
       catch (Exception e) {
           user = null;
       }
       finally {
           chessDB.close();
       }
       return new Gson().toJson(user);
    }
    
    @PUT
    @Path("update/{userID}")
    public String updateUserProfile(@PathParam("userID") int userID, String newUserJson) {
        User newUser = new Gson().fromJson(newUserJson, User.class);
        try {
            newUser = chessDB.updateUserProfile(userID, newUser);
        }
        catch (Exception e) {
            newUser = null;
        }
        finally {
            chessDB.close();
        }
        return new Gson().toJson(newUser);
    }
    
    @GET
    @Path("availableUsers")
    public String getAvailableUsers() {
        
        List<User> results = chessDB.getAvailableUsers();
        chessDB.close();
        return new Gson().toJson(new UsernameList(results));
    }
    
}