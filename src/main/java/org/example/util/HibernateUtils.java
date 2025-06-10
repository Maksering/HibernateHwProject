package org.example.util;

import org.example.user.User;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

public class HibernateUtils {
    private static SessionFactory sessionFactory;

    public static void setSessionFactory(SessionFactory factory) {
        sessionFactory = factory;
    }
    public static SessionFactory getSessionFactoryForTest(){
        return sessionFactory;
    }

    private HibernateUtils() {}

    public static SessionFactory getSessionFactory(){
        if(sessionFactory==null){
            try{
                Configuration configuration = new Configuration().configure();
                configuration.addAnnotatedClass(User.class);
                StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder()
                        .applySettings(configuration.getProperties());
                sessionFactory = configuration.buildSessionFactory(builder.build());
            } catch (Exception e){
                System.err.println("Error on create SessionFactory: " + e);
                throw new RuntimeException("Failed to create SessionFactory: " + e);
            }
        }
        return sessionFactory;
    }

    public static void buildSessionFactoryForTest() {

        Configuration configuration = new Configuration()
                .setProperty("hibernate.connection.driver_class", "org.postgresql.Driver")
                .setProperty("hibernate.connection.url", System.getProperty("hibernate.connection.url"))
                .setProperty("hibernate.connection.username", System.getProperty("hibernate.connection.username"))
                .setProperty("hibernate.connection.password", System.getProperty("hibernate.connection.password"))
                .setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect")
                .setProperty("hibernate.hbm2ddl.auto", "create-drop");

        configuration.addAnnotatedClass(User.class);
        sessionFactory = configuration.buildSessionFactory();
    }

    public static void shutdown() {
        if (sessionFactory != null && !sessionFactory.isClosed()) {
            sessionFactory.close();
        }
        sessionFactory = null;
    }
}