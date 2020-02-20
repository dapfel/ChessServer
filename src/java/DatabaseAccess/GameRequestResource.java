package DatabaseAccess;

import com.google.gson.Gson;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
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

    private final ChessDbController chessDB;

    public GameRequestResource() {
        chessDB = new ChessDbController();
    }
    
    @GET
    @Path("{userID}")
    public String getGameRequests(@PathParam("userID") int userID) {
        
        UsernameList requestingUsers = chessDB.getGameRequests(userID);
        chessDB.close();
        return new Gson().toJson(requestingUsers);
    }
    
    @POST
    @Path("")
    public String makeGameRequest(String requestJson) {
       GameRequest request = new Gson().fromJson(requestJson, GameRequest.class);
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
    
    @POST
    @Path("accept")
    public String acceptRequest(String gameRequestJson) {
       Game game;
       GameRequest gameRequest = new Gson().fromJson(gameRequestJson, GameRequest.class);
       try {
           game = chessDB.acceptGameRequest(gameRequest);
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
    @Path("whiteStartGame/{userID}")
    public String whiteStartGame(@PathParam("userID") int userID){         
        GameRequest request = chessDB.whiteStartGame(userID);
        chessDB.close();
        return new Gson().toJson(request);
    }
    
    @POST
    @Path("blackStartGame")
        public String blackStartGame(String gameRequestJson){         
        Game game = chessDB.blackStartGame(new Gson().fromJson(gameRequestJson, GameRequest.class));
        chessDB.close();
        return new Gson().toJson(game);
        }
        
    @GET
    @Path("reset/{userID}/{availability}")
    public String reset(@PathParam("userID") int userID, @PathParam("availability") String availability) {
        String response = chessDB.reset(userID, availability);
        chessDB.close();
        
        return new Gson().toJson(response);
    }
}
