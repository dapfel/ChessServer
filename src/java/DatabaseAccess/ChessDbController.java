package DatabaseAccess;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.List;
import java.util.ArrayList;
import javax.persistence.Query;
import javax.persistence.LockModeType;

public class ChessDbController {
    
    EntityManagerFactory entityManagerFactory;
    private final EntityManager entityManager;

    public ChessDbController() {
       entityManagerFactory = Persistence.createEntityManagerFactory("RecipeAppDatabaseServicePU");
       entityManager = entityManagerFactory.createEntityManager();
    }
    
    public User validateSignIn(String email, String password) {
        User profile = entityManager.find(User.class, email);
        if (profile == null) // no such email exists
            return null;
        if (profile.getPassword().equals(password))
           return convertToUserProfile(profile);
        else // wrong password
            return null;
    }
    
    public User addUser(User user, String password) throws Exception {
        User userP = convertToUserprofilesEntity(user, password);
        
        try {
            entityManager.getTransaction().begin();       
            entityManager.persist(userP);
            entityManager.getTransaction().commit();
        }
        catch (Exception e) {
            throw e;
        }
        
        return getUser(user.getEmail());
    }
    
    public void close() {
        entityManager.close();
        entityManagerFactory.close();
    }
    
}