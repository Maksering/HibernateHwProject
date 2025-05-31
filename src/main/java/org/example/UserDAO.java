package org.example;

import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class UserDAO {

    public User findById(int id) {
        try(Session session = HibernateUtils.getSessionFactory().openSession()){
            return session.get(User.class, id);
        }
    }

    public List<User> findAll(){
        try(Session session = HibernateUtils.getSessionFactory().openSession()) {
            return session.createQuery("FROM User", User.class).list();
        }
    }

    void create(User user){
        Transaction tr = null;
        try(Session session = HibernateUtils.getSessionFactory().openSession()){
            tr = session.beginTransaction();
            session.save(user);
            tr.commit();
        } catch (Exception e){
            if(tr != null) tr.rollback();
            throw e;
        }
    }

    void update(User user){
        Transaction tr = null;
        try(Session session = HibernateUtils.getSessionFactory().openSession()){
            tr = session.beginTransaction();
            session.update(user);
            tr.commit();
        } catch (Exception e){
            if(tr != null) tr.rollback();
            throw e;
        }
    }

    void delete(User user){
        Transaction tr = null;
        try(Session session = HibernateUtils.getSessionFactory().openSession()){
            tr = session.beginTransaction();
            session.delete(user);
            tr.commit();
        } catch (Exception e){
            if(tr != null) tr.rollback();
            throw e;
        }
    }

}
