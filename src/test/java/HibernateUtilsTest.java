import org.example.util.HibernateUtils;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.*;

@Testcontainers
public class HibernateUtilsTest {
    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:14.1-alpine")
            .withDatabaseName("testdb")
            .withUsername("testdb")
            .withPassword("testdb");

    private static SessionFactory sessionFactory;

    @BeforeAll
    static void beforeAll(){
        postgres.start();

        System.out.println("DB_URL: " + postgres.getJdbcUrl());
        System.setProperty("hibernate.connection.url", postgres.getJdbcUrl());
        System.setProperty("hibernate.connection.username", postgres.getUsername());
        System.setProperty("hibernate.connection.password", postgres.getPassword());
        HibernateUtils.buildSessionFactoryForTest();
    }

    @BeforeEach
    void beforeEach(){
        sessionFactory = HibernateUtils.getSessionFactory();
    }

    @Test
    void testSessionFactoryCreationWhenNotNull() {
        sessionFactory = HibernateUtils.getSessionFactory();
        SessionFactory secondFactory = sessionFactory;
        //sessionFactory != null; Reuse getSessionFactory();
        sessionFactory = HibernateUtils.getSessionFactory();
        assertSame(sessionFactory,secondFactory,"Should be the same");
    }

    @Test
    void testSessionFactoryCreationWhenNull() {
        sessionFactory = null;
        sessionFactory = HibernateUtils.getSessionFactory();
        assertNotNull(sessionFactory, "SessionFactory should not be null");
        assertFalse(sessionFactory.isClosed(), "SessionFactory should be open");
    }

    @Test
    void testSessionShutDownWhenNull_ShouldSetSessionFactoryNull() {
        SessionFactory current = HibernateUtils.getSessionFactory();
        try {
            HibernateUtils.setSessionFactory(null);
            HibernateUtils.shutdown();
            sessionFactory = HibernateUtils.getSessionFactoryForTest();
            assertNull(sessionFactory,"Should be null");
        } finally {
            HibernateUtils.setSessionFactory(current);
        }
    }

    @Test
    void testSessionShutdownWhenClosed_ShouldSetSessionFactoryNull() {
        SessionFactory current = HibernateUtils.getSessionFactory();
        try {
            sessionFactory = HibernateUtils.getSessionFactory();
            sessionFactory.close();
            HibernateUtils.shutdown();
            sessionFactory = HibernateUtils.getSessionFactoryForTest();
            assertNull(sessionFactory,"Should be null");
        } finally {
            HibernateUtils.setSessionFactory(current);
        }
    }

    @Test
    void testExceptionLoggingOnFailWithMockito() {
        PrintStream originalErr = System.err;
        try {
            PrintStream mockErr = mock(PrintStream.class);
            System.setErr(mockErr);

            HibernateUtils.shutdown();

            System.setProperty("hibernate.connection.url", "jdbc:postgresql://invalid:5432/fake");
            System.setProperty("hibernate.connection.username", "invalid");
            System.setProperty("hibernate.connection.password", "invalid");

            try {
                HibernateUtils.getSessionFactory();
            } catch (Exception ignore) {}

            verify(mockErr, timeout(1000)).println(contains("Error on create SessionFactory: "));
        } finally {
            System.setErr(originalErr);

            System.setProperty("hibernate.connection.url", postgres.getJdbcUrl());
            System.setProperty("hibernate.connection.username", postgres.getUsername());
            System.setProperty("hibernate.connection.password", postgres.getPassword());
            HibernateUtils.buildSessionFactoryForTest();
        }
    }
}
