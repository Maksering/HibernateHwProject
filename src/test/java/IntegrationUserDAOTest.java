import org.example.dao.UserDAO;
import org.example.user.User;
import org.example.util.HibernateUtils;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
public class IntegrationUserDAOTest {
    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:14.1-alpine")
            .withDatabaseName("testdb")
            .withUsername("testdb")
            .withPassword("testdb");

    private static UserDAO userDAO;
    private User testUser;

    @BeforeAll
    static void beforeAll(){
        postgres.start();

        System.out.println("DB_URL: " + postgres.getJdbcUrl());
        System.setProperty("hibernate.connection.url", postgres.getJdbcUrl());
        System.setProperty("hibernate.connection.username", postgres.getUsername());
        System.setProperty("hibernate.connection.password", postgres.getPassword());

        HibernateUtils.buildSessionFactoryForTest();
        userDAO = new UserDAO();
    }

    @BeforeEach
    void setup(){
        cleardb();
        testUser = new User("Test1","test@test.com",20);
        userDAO.create(testUser);
    }

   @Test
    void testCreateAndGetById(){

        User found = userDAO.findById(1);
        assertNotNull(found, "User should be found");
        assertEquals(testUser.getId(), found.getId());
        assertEquals(testUser.getName(), found.getName());
        assertEquals(testUser.getEmail(), found.getEmail());
        assertEquals(testUser.getAge(), found.getAge());
    }

    @Test
    void testFindByIdWhenUserNotFound(){
        User foundUser = userDAO.findById(-1);
        assertNull(foundUser, "User should not be found");
    }

    @Test
    void testFindAll(){
        User testUser2 = new User("Test2","Test2@test.com",21);
        userDAO.create(testUser2);

        List<User> users = userDAO.findAll();

        assertEquals(2,users.size());
        assertTrue(users.stream().anyMatch(user -> user.getName().equals(testUser.getName())));
        assertTrue(users.stream().anyMatch(user -> user.getName().equals(testUser2.getName())));
    }

    @Test
    void testFindAllWhenNoUsers(){
        cleardb();

        List<User> users = userDAO.findAll();

        assertTrue(users.isEmpty());
    }

    @Test
    void testUpdateUser(){
        testUser.setName("UpdateName");
        testUser.setEmail("UpdateEmail@test.com");
        testUser.setAge(22);

        userDAO.update(testUser);

        User findUpdatedUser = userDAO.findById(1);
        assertEquals("UpdateName", testUser.getName());
        assertEquals("UpdateEmail@test.com", testUser.getEmail());
        assertEquals(22, testUser.getAge());
    }

    @Test
    void deleteUser(){
        userDAO.delete(testUser);

        User deletedUser = userDAO.findById(1);
        assertNull(deletedUser,"Should be deleted");
    }

    private void cleardb(){
        try(Session session = HibernateUtils.getSessionFactory().openSession()){
            Transaction transaction = session.beginTransaction();
            session.createNativeQuery("TRUNCATE TABLE users RESTART IDENTITY CASCADE").executeUpdate();
            transaction.commit();
        }
    }
}