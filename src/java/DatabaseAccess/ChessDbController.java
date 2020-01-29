package DatabaseAccess;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import DatabaseEntityClasses.*;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.LockModeType;
import javax.persistence.TypedQuery;

public class ChessDbController {
    
    EntityManagerFactory entityManagerFactory;
    private final EntityManager entityManager;

    public ChessDbController() {
       entityManagerFactory = Persistence.createEntityManagerFactory("ChessServerPU");
       entityManager = entityManagerFactory.createEntityManager();
    }
    
    public User validateSignIn(String username, String password) {
        User user = entityManager.find(User.class, username);
        if (user == null) // no such user exists
            return null;
        if (user.getPassword().equals(password))
           return user;
        else // wrong password
            return null;
    }
    
    public User addUser(User user) throws Exception {
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
        try {
            entityManager.getTransaction().begin();       
            entityManager.persist(request);
            entityManager.getTransaction().commit();
        }
        catch (Exception e) {
            throw e;
        }
        
        return request;        
    }
    
    public Game whiteStartGame(String username) {
        TypedQuery<Gamerequest> query = entityManager.createNamedQuery("Gamerequest.findByRequestingUser", Gamerequest.class);
        List<Gamerequest> results = query.getResultList();
        
        Game game = null;
        for (Gamerequest request: results) {
            if (request.getGameID() > 0) {
                entityManager.getTransaction().begin();
                request = entityManager.find(Gamerequest.class, request.getGamerequestPK(), LockModeType.PESSIMISTIC_WRITE);
                game = entityManager.find(Game.class, request.getGameID());
                request.setGameID(-1);
                entityManager.getTransaction().commit();
                break;
            }
        }
        return game;
    }
    
    public Game acceptRequest(Gamerequest gameRequest) throws Exception {
        Game game = null;
        try {
            Gamerequest request = entityManager.find(Gamerequest.class, gameRequest.getGamerequestPK(), LockModeType.PESSIMISTIC_WRITE);
            if (request.getGameID() == 0) { // the request hasen't been asigned a game yet
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
    
    public Game blackStartGame(Gamerequest gameRequest) {
        Game game = null;
        entityManager.getTransaction().begin(); 
        Gamerequest request = entityManager.find(Gamerequest.class, gameRequest.getGamerequestPK(), LockModeType.PESSIMISTIC_WRITE);
        if (request.getGameID() == -1) {
            game = entityManager.find(Game.class, gameRequest.getGameID());
            entityManager.remove(request);
        }
        entityManager.getTransaction().commit();
        return game;
    }
    
    public Game endGame (Integer gameID) {
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
    
    public void close() {
        entityManager.close();
        entityManagerFactory.close();
    }

    
}