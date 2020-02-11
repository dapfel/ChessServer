package DatabaseAccess;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import DatabaseEntityClasses.*;
import java.util.List;
import javax.persistence.LockModeType;
import javax.persistence.TypedQuery;

public class ChessDbController {
    
    private EntityManagerFactory entityManagerFactory;
    private final EntityManager entityManager;

    public ChessDbController() {
       entityManagerFactory = Persistence.createEntityManagerFactory("ChessServerPU");
       entityManager = entityManagerFactory.createEntityManager();
    }
    
    public User validateSignIn(String username, String password) {
        entityManager.getTransaction().begin();
        User user = entityManager.find(User.class, username);
        if (user == null) {// no such user exists
            entityManager.getTransaction().commit();
            return null;
        }
        if (user.getPassword().equals(password)) {
           user.setAvailable(true);
           entityManager.getTransaction().commit();
           return user;
        }
        else { // wrong password
            entityManager.getTransaction().commit();
            return null;
        }
    }
    
    public User addUser(User user) throws Exception {
        try {
            entityManager.getTransaction().begin(); 
            user.setAvailable(true);
            entityManager.persist(user);
            entityManager.getTransaction().commit();
        }
        catch (Exception e) {
            throw e;
        }
        
        return user;
    }
    
    public User updateUserProfile(String username, User newUser) {
        entityManager.getTransaction().begin();
        User user = entityManager.find(User.class, username, LockModeType.PESSIMISTIC_WRITE);
        if (user == null) {
            entityManager.getTransaction().commit();
            return null;
        }
        if (!(newUser.getUsername() == null))
            user.setUsername(newUser.getUsername());
        if (!(newUser.getPassword() == null))
            user.setPassword(newUser.getPassword());      
        entityManager.getTransaction().commit();
        
        return user;
    }
    
    public List<User> getAvailableUsers() {    
        TypedQuery<User> query = entityManager.createNamedQuery("User.findByAvailable", User.class);
        List<User> results = query.getResultList();
        
        return results;
    }  
    
    public Gamerequest makeGameRequest(Gamerequest request) {
        User requestedUser;
        try {
            entityManager.getTransaction().begin();   
            requestedUser = entityManager.find(User.class, request.getGamerequestPK().getRequestedUser(), LockModeType.PESSIMISTIC_WRITE);
            if (requestedUser.getAvailable())
                entityManager.persist(request);
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
            if (request != null && request.getGameID() == 0) { // the request still exists and hasen't been asigned a game yet
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
                User user = entityManager.find(User.class, request.getUser1(),LockModeType.PESSIMISTIC_WRITE);
                user.setAvailable(false);
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
            User user = entityManager.find(User.class, gameRequest.getUser1(),LockModeType.PESSIMISTIC_WRITE);
            user.setAvailable(false);
        }
        entityManager.getTransaction().commit();
        return game;
    }
    
    public List<Gamerequest> getGameRequests(String username) {
        User user = entityManager.find(User.class, username);
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
    
    public String reset(String username) {
        entityManager.getTransaction().begin();
        User user = entityManager.find(User.class, username, LockModeType.PESSIMISTIC_WRITE);
        if (user == null)
            return null;
        user.setAvailable(true);
        
        TypedQuery<Gamerequest> query = entityManager.createNamedQuery("Gamerequest.findByRequestingUser", Gamerequest.class);
        List<Gamerequest> results = query.getResultList();
        for (Gamerequest request : results)
            entityManager.remove(request);
        
        query = entityManager.createNamedQuery("Gamerequest.findByRequestedUser", Gamerequest.class);
        results = query.getResultList();
        for (Gamerequest request : results)
            entityManager.remove(request);
        
        entityManager.getTransaction().commit();
        return "success";               
    }
    
    public void close() {
        entityManager.close();
        entityManagerFactory.close();
    }

    
}