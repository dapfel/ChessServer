package DatabaseAccess;

import DatabaseEntityClasses.Game;
import DatabaseEntityClasses.Gamerequest;
import com.google.gson.Gson;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * REST Web Service
 *
 * @author dapfel
 */
@Path("game")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class GameResource {

    private ChessDbController chessDB;

    public GameResource() {
        chessDB = new ChessDbController();
    }
    
    @GET
    @Path("lastMove/{gameID}")
    public String getLastMove(@PathParam("gameID") Integer gameID) {
        String move = chessDB.getLastMove(gameID);
        chessDB.close();
        
        return new Gson().toJson(move);
    }
    
    @PUT
    @Path("makeMove/{gameID}")
    public String makeMove(@PathParam("gameID") Integer gameID, String moveJson) {
        String move = new Gson().fromJson(moveJson, String.class);
        try {
            move = chessDB.makeMove(gameID, move);
        }
        catch (Exception e) {
            move = null;
        }
        finally {
        chessDB.close();
        }
        return new Gson().toJson(move);
    }
    
    @DELETE
    @Path("{gameID}")
    public String endGame(@PathParam("gameID") Integer gameID) {
        Game game = chessDB.endGame(gameID);
        chessDB.close();
        
        return new Gson().toJson(game);
    }
}
