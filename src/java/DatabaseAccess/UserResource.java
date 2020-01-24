
package DatabaseAccess;

import com.google.gson.Gson;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.POST;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;

/**
 * REST Web Service
 *
 * @author dapfe
 */
@Path("userProfile")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {

    private ChessDbController recipeDB;

    public UserResource() {
        recipeDB = new ChessDbController();
    }

    @GET
    @Path("signIn/{email}/{password}")
    public String validateSignIn(@PathParam("email") String email, @PathParam("password") String password) {
       String userJson = new Gson().toJson(recipeDB.validateSignIn(email, password));
       recipeDB.close();
       return userJson;
    }
    
    @POST
    @Path("add/{password}")
    public String addUser(String userJson, @PathParam("password") String password) {
       User user = new Gson().fromJson(userJson, User.class);
       try {
           user = recipeDB.addUser(user, password);
       }
       catch (Exception e) {
           user = null;
       }
       finally {
           recipeDB.close();
       }
       return new Gson().toJson(user);
    }
}