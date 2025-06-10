package org.example.dao;

import org.example.util.HibernateUtils;
import org.example.Main;
import org.example.user.User;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.io.InputStream;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class UserDAO{

    private static Logger logger = initLogger();

    protected Logger getLogger() {
        return logger;
    }

    public static Logger initLogger(){
        Logger logger = Logger.getLogger(UserDAO.class.getName());
        try {
            InputStream config = UserDAO.class.getResourceAsStream("/logging.properties");
            if (config != null) {
                LogManager.getLogManager().readConfiguration(config);
            }
        } catch (Exception e) {
            logger.severe("Failed to initialize logger: " + e.getMessage());
        }
        return logger;
    }

    public User findById(int id) {
        try(Session session = HibernateUtils.getSessionFactory().openSession()){
            getLogger().log(Level.INFO, "Looking for user by id: " + id);
            User user = session.get(User.class, id);
            if(user!=null){
                getLogger().log(Level.INFO,"User was found: " + user);
            }
            else {
                getLogger().log(Level.INFO,"User not found");
            }
            return user;
        } catch (Exception e){
            getLogger().log(Level.SEVERE,"Error on looking for user by id: " + id + " Error: " + e);
            throw e;
        }
    }

    public List<User> findAll(){
        try(Session session = HibernateUtils.getSessionFactory().openSession()) {
            getLogger().log(Level.INFO, "Making list of all users");
            List<User> users = session.createQuery("FROM User", User.class).list();
            return users;
        } catch (Exception e){
            getLogger().log(Level.SEVERE,"Error on making list of all users. Error: " + e);
            throw e;
        }
    }

    public void create(User user){
        getLogger().log(Level.INFO, "Create user: " + user);
        Transaction tr = null;
        try(Session session = HibernateUtils.getSessionFactory().openSession()){
            tr = session.beginTransaction();
            session.save(user);
            tr.commit();
            getLogger().log(Level.INFO, "User was created: " + user);
        } catch (Exception e){
            if(tr != null) {
                getLogger().log(Level.WARNING,"Transaction rollback on user create: " + user);
                tr.rollback();
            }
            getLogger().log(Level.SEVERE, "Error on user create: " + user + " Error: " + e);
            throw e;
        }
    }

    public void update(User user){
        getLogger().log(Level.INFO, "Update user");
        Transaction tr = null;
        try(Session session = HibernateUtils.getSessionFactory().openSession()){
            tr = session.beginTransaction();
            session.update(user);
            tr.commit();
            getLogger().log(Level.INFO, "User was updated: " + user);
        } catch (Exception e){
            if(tr != null) {
                getLogger().log(Level.WARNING,"Transaction rollback on user update: " + user);
                tr.rollback();
            }
            getLogger().log(Level.SEVERE, "Error on user update: " + user + " Error: " + e);
            throw e;
        }
    }

    public void delete(User user){
        getLogger().log(Level.INFO, "Delete user: " + user);
        Transaction tr = null;
        try(Session session = HibernateUtils.getSessionFactory().openSession()){
            tr = session.beginTransaction();
            session.delete(user);
            tr.commit();
            getLogger().log(Level.INFO, "User was deleted: " + user);
        } catch (Exception e){
            if(tr != null) {
                getLogger().log(Level.WARNING,"Transaction rollback on user delete: " + user);
                tr.rollback();
            }
            getLogger().log(Level.SEVERE, "Error on user delete: " + user + " Error: " + e);
            throw e;
        }
    }
}
