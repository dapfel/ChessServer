package DatabaseAccess;

import DatabaseEntityClasses.*;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Produces;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;

/**
 * REST Web Service
 *
 * @author dapfel
 */
@Path("gameRequest")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class GameRequestResource {

    private ChessDbController chessDB;

    public GameRequestResource() {
        chessDB = new ChessDbController();
    }
    
    @GET
    @Path("{username}")
    public String getGameRequests(@PathParam("username") String username) {
        
        List<Gamerequest> requests = chessDB.getGameRequests(username);
        chessDB.close();
        List<User> requestUsers = new ArrayList<>();
        for (Gamerequest request : requests ) {
            requestUsers.add(request.getUser1());
        }
        return new Gson().toJson(new UsernameList(requestUsers));
    }
    
    @POST
    @Path("")
    public String makeGameRequest(String requestJson) {
       Gamerequest request = new Gson().fromJson(requestJson, Gamerequest.class);
       try {
           request = chessDB.makeGameRequest(request);
       }
       catch (Exception e) {
           request = null;
       }
       finally {
           chessDB.close();
       }
       return new Gson().toJson(request);
    }
    
    @PUT
    @Path("accept")
    public String acceptRequest(String gameRequestJson) {
       Game game;
       Gamerequest gameRequest = new Gson().fromJson(gameRequestJson, Gamerequest.class);
       try {
           game = chessDB.acceptRequest(gameRequest);
       }
       catch (Exception e) {
           game = null;
       }
       finally {
           chessDB.close();
       }
       return new Gson().toJson(game);
    }
    
    @GET
    @Path("whiteStartGame/{username}")
    public String whiteStartGame(@PathParam("username") String username){         
        Game game = chessDB.whiteStartGame(username);
        chessDB.close();
        return new Gson().toJson(game);
    }
    
    @PUT
    @Path("blackStartGame")
        public String blackStartGame(String gameRequestJson){         
        Game game = chessDB.blackStartGame(new Gson().fromJson(gameRequestJson, Gamerequest.class));
        chessDB.close();
        return new Gson().toJson(game);
        }
}
