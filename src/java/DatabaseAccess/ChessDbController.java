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
        TypedQuery<UserProfileEntity> query = entityManager.createNamedQuery("UserProfileEntity.findByUsername", UserProfileEntity.class);
        query.setParameter("username", username);
        List<UserProfileEntity> results = query.getResultList();
        if (results.isEmpty()) {// no such user exists
            entityManager.getTransaction().commit();
            return null;
        }
        UserProfileEntity user = results.get(0);
        if (user.getPassword().equals(password)) {
           entityManager.getTransaction().commit();
           return new User(user);
        }
        else { // its wrong password
            entityManager.getTransaction().commit();
            return null;
        }
    }
    
    public User addUser(User user, String password) throws Exception {
        if (user.getUsername().equals("") || password.equals(""))
            return null;
        try {
            entityManager.getTransaction().begin(); 
            UserProfileEntity newUser = new UserProfileEntity(user, password);
            user.setWins(0);
            user.setLosses(0);
            user.setDraws(0);
            entityManager.persist(newUser);
            entityManager.getTransaction().commit();
        }
        catch (Exception e) {
            throw e;
        }
        
        return user;
    }
    
    public User updateUserProfile(int userID, String newPassword, User newUser) {
        entityManager.getTransaction().begin();
        UserProfileEntity user = entityManager.find(UserProfileEntity.class, userID, LockModeType.PESSIMISTIC_WRITE);
        if (user == null) {
            entityManager.getTransaction().commit();
            return null;
        }
        if (!(newUser.getUsername().equals("")))
            user.setUsername(newUser.getUsername());
        if (!(newPassword.equals("")))
            user.setPassword(newPassword);      
        entityManager.getTransaction().commit();
        
        return new User(user);
    }
    
    public UsernameList getAvailableUsers() {    
        TypedQuery<UserProfileEntity> query = entityManager.createNamedQuery("UserProfileEntity.findByAvailable", UserProfileEntity.class);
        query.setParameter("available", true);
        List<UserProfileEntity> results = query.getResultList();
        
        return new UsernameList(results);
    }  
    
    public GameRequest makeGameRequest(GameRequest request) {
        UserProfileEntity requestedUser;
        try {
            entityManager.getTransaction().begin();  
            TypedQuery<UserProfileEntity> query = entityManager.createNamedQuery("UserProfileEntity.findByUsername", UserProfileEntity.class);
            query.setParameter("username", request.getRequestedUser());
            List<UserProfileEntity> results = query.getResultList();
            UserProfileEntity user = results.get(0);
            requestedUser = entityManager.find(UserProfileEntity.class, user.getUserID());
            if (requestedUser.getAvailable()) {
                GameRequestEntity newRequest = new GameRequestEntity(request.getRequestingUser(), request.getRequestedUser());
                newRequest.setGameID(0); // set to invalid gameID to indicate that request has not yet been accepted
                entityManager.persist(newRequest);
            }
            entityManager.getTransaction().commit();
        }
        catch (Exception e) {
            return null;
        }
        
        return request;        
    }
    
    public Game acceptGameRequest(GameRequest gameRequest) throws Exception {
        Game game = null;
        try {
            entityManager.getTransaction().begin();
            GameRequestPKEntity gameRequestPK = new GameRequestPKEntity(gameRequest.getRequestingUser(), gameRequest.getRequestedUser());
            GameRequestEntity request = entityManager.find(GameRequestEntity.class, gameRequestPK, LockModeType.PESSIMISTIC_WRITE);
            if (request == null) {
                entityManager.getTransaction().commit();               
                return null;
            }
            TypedQuery<UserProfileEntity> query = entityManager.createNamedQuery("UserProfileEntity.findByUsername", UserProfileEntity.class);
            query.setParameter("username", request.getGamerequestPK().getRequestingUser());
            List<UserProfileEntity> results = query.getResultList();
            UserProfileEntity user = results.get(0);           
            if (user.getAvailable() && request.getGameID() == 0) { // the request still is valid and hasn't been asigned a game yet
                GameEntity gameEntity = new GameEntity(gameRequestPK);
                entityManager.persist(gameEntity);
                entityManager.flush();
                int gameID = gameEntity.getGameID();
                request.setGameID(gameID);
                game = new Game(gameEntity.getGameID());
            }
            entityManager.getTransaction().commit();
        }
        catch (Exception e) {
            throw e;
        }    
        return game;
    }
        
    public GameRequest whiteStartGame(int userID) {
        UserProfileEntity user = entityManager.find(UserProfileEntity.class, userID);
        List<GameRequestEntity> requesterList = user.getRequesterGameList();
        
        GameRequestEntity resultRequest = new GameRequestEntity(null,null); 
        resultRequest.setGameID(0);// game request with invalid gameID is returned to indicate no requests have been accepted
        for (GameRequestEntity request: requesterList) {
            if (request.getGameID() > 0) {
                entityManager.getTransaction().begin();
                entityManager.lock(request, LockModeType.PESSIMISTIC_WRITE);
                resultRequest.setGamerequestPK(request.getGamerequestPK());
                resultRequest.setGameID(request.getGameID());
                request.setGameID(-1);
                entityManager.lock(user, LockModeType.PESSIMISTIC_WRITE);
                user.setAvailable(false);
                user.getRequestedGameList().forEach((gameR) -> {// remove all requests for this user from other users
                    entityManager.remove(gameR);
                });
                user.getRequesterGameList().forEach((gameR) -> {// remove all of this users requests for other players
                    entityManager.remove(gameR);
                });
                entityManager.getTransaction().commit();
                break;
            }
        }
        return new GameRequest(resultRequest);
    }
    
    public Game blackStartGame(GameRequest gameRequest) {
        GameEntity gameEntity;
        GameRequestPKEntity gameRequestPK = new GameRequestPKEntity(gameRequest.getRequestingUser(), gameRequest.getRequestedUser());
        entityManager.getTransaction().begin(); 
        GameRequestEntity request = entityManager.find(GameRequestEntity.class, gameRequestPK, LockModeType.PESSIMISTIC_WRITE);
        if (request.getGameID() == -1) {
            gameEntity = entityManager.find(GameEntity.class, gameRequest.getGameID());
            entityManager.remove(request);
            
            TypedQuery<UserProfileEntity> query = entityManager.createNamedQuery("UserProfileEntity.findByUsername", UserProfileEntity.class);
            query.setParameter("username", gameRequest.getRequestedUser());
            List<UserProfileEntity> results = query.getResultList();
            UserProfileEntity user = results.get(0);
            entityManager.lock(user, LockModeType.PESSIMISTIC_WRITE);
            user.setAvailable(false);
            user.getRequestedGameList().forEach((gameR) -> { // remove all requests for this user from other users
                entityManager.remove(gameR);
            });
            user.getRequesterGameList().forEach((gameR) -> {// remove all of this users requests for other players
                entityManager.remove(gameR);
            });           
        }
        else 
            return null;
        entityManager.getTransaction().commit();
        return new Game(gameEntity.getGameID());
    }
    
    public UsernameList getGameRequests(int userID) {
        UserProfileEntity user = entityManager.find(UserProfileEntity.class, userID);       
        if (user == null) // no such user exists
            return null;
        else {
            UsernameList requestingUsers = new UsernameList();
            user.getRequestedGameList().forEach((request) -> {
            requestingUsers.add(request.getGamerequestPK().getRequestingUser());
            });
            return requestingUsers;
        }
    }
        
    public String getLastMove(Integer gameID) {
        GameEntity game = entityManager.find(GameEntity.class, gameID);
        if (game == null)
            return null;
        else
            return game.getMove();
    }
    
    public String makeMove(Integer gameID, String move) {
        entityManager.getTransaction().begin();
        GameEntity game = entityManager.find(GameEntity.class, gameID, LockModeType.PESSIMISTIC_WRITE);
        if (game == null) {
            entityManager.getTransaction().commit();
            return null;
        }
        game.setMove(move);
        entityManager.getTransaction().commit();
        
        return move;
    }
    
    public Game endGame(int gameID, String winner) {
        entityManager.getTransaction().begin();
        GameEntity game = entityManager.find(GameEntity.class, gameID, LockModeType.PESSIMISTIC_WRITE);
        if (game == null) {
            entityManager.getTransaction().commit();
            return null;
        }
        
        TypedQuery<UserProfileEntity> query = entityManager.createNamedQuery("UserProfileEntity.findByUsername", UserProfileEntity.class);
        query.setParameter("username", game.getPlayer1());
        List<UserProfileEntity> results = query.getResultList();
        UserProfileEntity player1 = results.get(0);
        System.out.println(game.getPlayer1() + "  " + player1.getUsername() + player1.getWins());
        entityManager.lock(player1, LockModeType.PESSIMISTIC_WRITE);
        query.setParameter("username", game.getPlayer2());
        results = query.getResultList();
        UserProfileEntity player2 = results.get(0);
        entityManager.lock(player2, LockModeType.PESSIMISTIC_WRITE);
        if (player1.getUsername().equals(winner)) {
            player1.setWins(player1.getWins() + 1);
            player2.setLosses(player2.getLosses() + 1);
        }
        else if (player2.getUsername().equals(winner)) {
            player2.setWins(player2.getWins() + 1);
            player1.setLosses(player1.getLosses() + 1);
        }
        else {
            player1.setDraws(player1.getWins() + 1);
            player2.setDraws(player2.getLosses() + 1);
        }       
        
        Game endedGame = new Game(game.getGameID());
        entityManager.remove(game);
        
        entityManager.getTransaction().commit();
        
        return endedGame;
    }
    
    public String reset(int userID, String availability) {
        entityManager.getTransaction().begin();
        UserProfileEntity user = entityManager.find(UserProfileEntity.class, userID, LockModeType.PESSIMISTIC_WRITE);
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
        
        user.getRequestedGameList().forEach((gameR) -> { // remove all requests for this user from other users
            entityManager.remove(gameR);
        });
        user.getRequesterGameList().forEach((gameR) -> {// remove all of this users requests for other players
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