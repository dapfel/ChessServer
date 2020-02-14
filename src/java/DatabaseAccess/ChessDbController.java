package DatabaseAccess;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import DatabaseEntityClasses.*;
import java.util.List;
import javax.persistence.LockModeType;
import javax.persistence.TypedQuery;

public class ChessDbController {
    
    private final EntityManagerFactory entityManagerFactory;
    private final EntityManager entityManager;

    public ChessDbController() {
       entityManagerFactory = Persistence.createEntityManagerFactory("ChessServerPU");
       entityManager = entityManagerFactory.createEntityManager();
    }
    
    public User validateSignIn(String username, String password) {
        entityManager.getTransaction().begin();
        TypedQuery<User> query = entityManager.createNamedQuery("User.findByUsername", User.class);
        query.setParameter("username", username);
        List<User> results = query.getResultList();
        User user = results.get(0);
        if (user == null) {// no such user exists
            entityManager.getTransaction().commit();
            return null;
        }
        if (user.getPassword().equals(password)) {
           entityManager.getTransaction().commit();
           return user;
        }
        else { // wrong password
            entityManager.getTransaction().commit();
            return null;
        }
    }
    
    public User addUser(User user) throws Exception {
        if (user.getUsername().equals("") || user.getPassword().equals(""))
            return null;
        try {
            entityManager.getTransaction().begin(); 
            entityManager.persist(user);
            entityManager.getTransaction().commit();
        }
        catch (Exception e) {
            throw e;
        }
        
        return user;
    }
    
    public User updateUserProfile(int userID, User newUser) {
        entityManager.getTransaction().begin();
        User user = entityManager.find(User.class, userID, LockModeType.PESSIMISTIC_WRITE);
        if (user == null) {
            entityManager.getTransaction().commit();
            return null;
        }
        if (!(newUser.getUsername().equals("")))
            user.setUsername(newUser.getUsername());
        if (!(newUser.getPassword().equals("")))
            user.setPassword(newUser.getPassword());      
        entityManager.getTransaction().commit();
        
        return user;
    }
    
    public List<User> getAvailableUsers() {    
        TypedQuery<User> query = entityManager.createNamedQuery("User.findByAvailable", User.class);
        query.setParameter("available", true);
        List<User> results = query.getResultList();
        
        return results;
    }  
    
    public Gamerequest makeGameRequest(Gamerequest request) {
        User requestedUser;
        try {
            entityManager.getTransaction().begin();  
            TypedQuery<User> query = entityManager.createNamedQuery("User.findByUsername", User.class);
            query.setParameter("username", request.getGamerequestPK().getRequestedUser());
            List<User> results = query.getResultList();
            User user = results.get(0);
            requestedUser = entityManager.find(User.class, user.getUserID(), LockModeType.PESSIMISTIC_WRITE);
            if (requestedUser.getAvailable()) {
                request.setUser(requestedUser);
                entityManager.persist(request);
            }
            entityManager.getTransaction().commit();
        }
        catch (Exception e) {
            return null;
        }
        
        request.setUser(null); // so that client won't see other players private info
        return request;        
    }
    
    public Game acceptGameRequest(Gamerequest gameRequest) throws Exception {
        Game game = null;
        try {
            Gamerequest request = entityManager.find(Gamerequest.class, gameRequest.getGamerequestPK(), LockModeType.PESSIMISTIC_WRITE);
            if (request == null)
                return null;
            TypedQuery<User> query = entityManager.createNamedQuery("User.findByUsername", User.class);
            query.setParameter("username", request.getGamerequestPK().getRequestingUser());
            List<User> results = query.getResultList();
            User user = results.get(0);           
            if (user.getAvailable() && request.getGameID() == 0) { // the request still is valid and hasn't been asigned a game yet
                entityManager.getTransaction().begin();  
                game = new Game();
                entityManager.persist(game);
                entityManager.flush();
                int gameID = game.getGameID();
                request.setGameID(gameID);
                entityManager.getTransaction().commit();
            }
        }
        catch (Exception e) {
            throw e;
        }    
        return game;
    }
        
    public Gamerequest whiteStartGame(String username) {
        TypedQuery<Gamerequest> query = entityManager.createNamedQuery("Gamerequest.findByRequestingUser", Gamerequest.class);
        query.setParameter("username", username);
        List<Gamerequest> results = query.getResultList();
        
        Gamerequest resultRequest = new Gamerequest(null,null); 
        resultRequest.setGameID(0);// game request with invalid gameID is returned to indicate no requests have been accepted
        for (Gamerequest request: results) {
            if (request.getGameID() > 0) {
                entityManager.getTransaction().begin();
                request = entityManager.find(Gamerequest.class, request.getGamerequestPK(), LockModeType.PESSIMISTIC_WRITE);
                resultRequest.setGamerequestPK(request.getGamerequestPK());
                resultRequest.setGameID(request.getGameID());
                request.setGameID(-1);
                User user = entityManager.find(User.class, request.getUser1().getUserID(), LockModeType.PESSIMISTIC_WRITE);
                user.setAvailable(false);
                user.getGamerequestList().forEach((gameR) -> {// remove all requests for this user from other users
                    entityManager.remove(gameR);
                });
                user.getGamerequestList1().forEach((gameR) -> {// remove all of this users requests for other players
                    entityManager.remove(gameR);
                });
                entityManager.getTransaction().commit();
                break;
            }
        }
        return resultRequest;
    }
    
    public Game blackStartGame(Gamerequest gameRequest) {
        Game game = null;
        entityManager.getTransaction().begin(); 
        Gamerequest request = entityManager.find(Gamerequest.class, gameRequest.getGamerequestPK(), LockModeType.PESSIMISTIC_WRITE);
        if (request.getGameID() == -1) {
            game = entityManager.find(Game.class, gameRequest.getGameID());
            entityManager.remove(request);
            User user = entityManager.find(User.class, gameRequest.getUser1().getUserID(),LockModeType.PESSIMISTIC_WRITE);
            user.setAvailable(false);
            user.getGamerequestList().forEach((gameR) -> { // remove all requests for this user from other users
                entityManager.remove(gameR);
            });
            user.getGamerequestList1().forEach((gameR) -> {// remove all of this users requests for other players
                entityManager.remove(gameR);
            });           
        }
        entityManager.getTransaction().commit();
        return game;
    }
    
    public List<Gamerequest> getGameRequests(int userID) {
        User user = entityManager.find(User.class, userID);
        if (user == null) // no such user exists
            return null;
        else
            return user.getGamerequestList();
    }
        
    public String getLastMove(Integer gameID) {
        Game game = entityManager.find(Game.class, gameID);
        if (game == null)
            return null;
        else
            return game.getMove();
    }
    
    public String makeMove(Integer gameID, String move) {
        entityManager.getTransaction().begin();
        Game game = entityManager.find(Game.class, gameID, LockModeType.PESSIMISTIC_WRITE);
        if (game == null) {
            entityManager.getTransaction().commit();
            return null;
        }
        game.setMove(move);
        entityManager.getTransaction().commit();
        
        return move;
    }
    
    public Game endGame(int gameID) {
        entityManager.getTransaction().begin();
        Game game = entityManager.find(Game.class, gameID, LockModeType.PESSIMISTIC_WRITE);
        if (game == null) {
            entityManager.getTransaction().commit();
            return null;
        }
        entityManager.remove(game);
        entityManager.getTransaction().commit();
        
        return game;
    }
    
    public String reset(int userID, String availability) {
        entityManager.getTransaction().begin();
        User user = entityManager.find(User.class, userID, LockModeType.PESSIMISTIC_WRITE);
        if (user == null)
            return null;
        if (availability.equals("onlyAvailable")) {
            user.setAvailable(true);
            return "success";
        }
        if (availability.equals("available"))
            user.setAvailable(true);
        else // "unavailable"
            user.setAvailable(false);
        
        user.getGamerequestList().forEach((gameR) -> { // remove all requests for this user from other users
            entityManager.remove(gameR);
        });
        user.getGamerequestList1().forEach((gameR) -> {// remove all of this users requests for other players
            entityManager.remove(gameR);
        });         
        
        entityManager.getTransaction().commit();
        return "success";               
    }
    
    public void close() {
        entityManager.close();
        entityManagerFactory.close();
    }

    
}