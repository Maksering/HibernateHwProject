import org.example.dao.UserDAO;
import org.example.user.User;
import org.example.util.HibernateUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserDAOExceptionsMockitoTest {

    @Mock
    private SessionFactory sessionFactory;
    @Mock
    private Session session;
    @Mock
    private Transaction transaction;
    @Mock
    private Logger logger;
    @Mock
    private Query<User> query;

    private MockedStatic<HibernateUtils> hibernateUtilsMockedStatic;

    @InjectMocks
    private UserDAO userDAO;

    @BeforeEach
    void setup(){
       MockitoAnnotations.openMocks(this);

        hibernateUtilsMockedStatic = mockStatic(HibernateUtils.class);
        hibernateUtilsMockedStatic.when(HibernateUtils::getSessionFactory).thenReturn(sessionFactory);

        when(sessionFactory.openSession()).thenReturn(session);
        when(session.beginTransaction()).thenReturn(transaction);

        userDAO = new UserDAO() {
            @Override
            protected Logger getLogger(){
                return logger;
            }
        };
    }

    @AfterEach
    void shutdown(){
        hibernateUtilsMockedStatic.close();
    }

    @Test
    void findById_ThrowsException_LogsError() {
        RuntimeException expectedException = new RuntimeException("DB error");
        when(session.get(User.class, 1)).thenThrow(expectedException);

        assertThrows(RuntimeException.class, () -> userDAO.findById(1));
        verify(logger).log(Level.SEVERE, "Error on looking for user by id: 1 Error: " + expectedException);
    }

    @Test
    void findAll_ThrowsException_LogsError() {
        RuntimeException expectedException = new RuntimeException("DB error");
        when(session.createQuery(anyString(),eq(User.class))).thenThrow(expectedException);

        assertThrows(RuntimeException.class, () -> userDAO.findAll());
        verify(logger).log(Level.SEVERE, "Error on making list of all users. Error: " + expectedException);
    }

    @Test
    void create_Failure_RollsBackTransaction() {
        User user = new User("test", "test@test.com", 25);
        when(sessionFactory.openSession()).thenReturn(session);
        doThrow(new RuntimeException("DB error")).when(session).save(user);

        assertThrows(RuntimeException.class, () -> userDAO.create(user));
        verify(transaction).rollback();
        verify(logger).log(Level.WARNING, "Transaction rollback on user create: " + user);
        verify(logger).log(Level.SEVERE, "Error on user create: " + user + " Error: java.lang.RuntimeException: DB error");
    }

    @Test
    void create_Failure_NoRollsBack(){
        User user = new User("test", "test@test.com", 25);
        when(session.beginTransaction()).thenReturn(null);
        doThrow(new RuntimeException("Transaction equals null exception")).when(session).save(user);
        assertThrows(RuntimeException.class, () -> userDAO.create(user));
        verify(transaction, never()).rollback();
        verify(logger, never()).log(Level.WARNING, "Transaction rollback on user create: " + user);
        verify(logger).log(Level.SEVERE, "Error on user create: " + user + " Error: java.lang.RuntimeException: Transaction equals null exception");
    }


    @Test
    void update_Failure_RollsBackTransaction() {
        User user = new User("test", "test@test.com", 25);
        doThrow(new RuntimeException("DB error")).when(session).update(user);

        assertThrows(RuntimeException.class, () -> userDAO.update(user));
        verify(transaction).rollback();
        verify(logger).log(Level.WARNING, "Transaction rollback on user update: " + user);
        verify(logger).log(Level.SEVERE, "Error on user update: " + user + " Error: java.lang.RuntimeException: DB error");
    }


    @Test
    void update_Failure_NoRollsBack(){
        User user = new User("test", "test@test.com", 25);
        when(session.beginTransaction()).thenReturn(null);
        doThrow(new RuntimeException("Transaction equals null exception")).when(session).update(user);
        assertThrows(RuntimeException.class, () -> userDAO.update(user));
        verify(transaction, never()).rollback();
        verify(logger, never()).log(Level.WARNING, "Transaction rollback on user update: " + user);
        verify(logger).log(Level.SEVERE, "Error on user update: " + user + " Error: java.lang.RuntimeException: Transaction equals null exception");
    }

    @Test
    void delete_Failure_RollsBackTransaction() {
        User user = new User("test", "test@test.com", 25);
        doThrow(new RuntimeException("DB error")).when(session).delete(user);

        assertThrows(RuntimeException.class, () -> userDAO.delete(user));
        verify(transaction).rollback();
        verify(logger).log(Level.WARNING, "Transaction rollback on user delete: " + user);
        verify(logger).log(Level.SEVERE, "Error on user delete: " + user + " Error: java.lang.RuntimeException: DB error");
    }

    @Test
    void delete_Failure_NoRollsBack(){
        User user = new User("test", "test@test.com", 25);
        when(session.beginTransaction()).thenReturn(null);
        doThrow(new RuntimeException("Transaction equals null exception")).when(session).delete(user);
        assertThrows(RuntimeException.class, () -> userDAO.delete(user));
        verify(transaction, never()).rollback();
        verify(logger, never()).log(Level.WARNING, "Transaction rollback on user delete: " + user);
        verify(logger).log(Level.SEVERE, "Error on user delete: " + user + " Error: java.lang.RuntimeException: Transaction equals null exception");
    }
}
