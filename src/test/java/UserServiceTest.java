import org.example.dao.UserDAO;
import org.example.service.UserService;
import org.example.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserDAO userDAO;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setup(){
        testUser = new User("Test1", "test@email.com", 20);
        testUser.setId(1);
        testUser.setCreated_at(LocalDateTime.now());
    }

    @Test
    void testCreateUser(){
        doNothing().when(userDAO).create(any(User.class));

        userService.createUser("TestUser","test@test.com",20);
        verify(userDAO).create(any(User.class));
    }

    @Test
    void testFindUserById() {
        User expectUser = testUser;
        when(userDAO.findById(1)).thenReturn(expectUser);

        userService.findUserByid(1);
        verify(userDAO, times(1)).findById(1);
    }

    @Test
    void testFindUserByIdWhenNotExists() {
        when(userDAO.findById(-1)).thenReturn(null);

        userService.findUserByid(-1);
        verify(userDAO, times(1)).findById(-1);
    }

    @Test
    void testShowAllUsers() {
        User user1 = testUser;
        User user2 = new User("Test2", "test2@email.com", 21);;
        List<User> users = List.of(user1, user2);
        when(userDAO.findAll()).thenReturn(users);

        userService.showAllUsers();
        verify(userDAO, times(1)).findAll();
    }

    @Test
    void testShowAllUsersWhenEmpty() {
        List<User> emptyList = List.of();
        when(userDAO.findAll()).thenReturn(emptyList);

        userService.showAllUsers();
        verify(userDAO, times(1)).findAll();
    }

    @Test
    void testUpdateUser() {
        when(userDAO.findById(1)).thenReturn(testUser);
        doNothing().when(userDAO).update(any(User.class));

        userService.updateUser(1, "UpdateName", "UpdateName@test.com", "21");
        verify(userDAO).update(testUser);
    }

    @Test
    void testUpdateUserWhenUserNotFound() {
        when(userDAO.findById(1)).thenReturn(null);

        userService.updateUser(1, "UpdateName", "UpdateName@test.com", "21");
        verify(userDAO, never()).update(any(User.class));
    }

    @Test
    void testUpdateUserWhenNameEmailAgeIsEmpty() {

        User mockUser = mock(User.class);
        when(userDAO.findById(1)).thenReturn(mockUser);

        userService.updateUser(1, "", "", "");

        verify(mockUser, never()).setName(anyString());
        verify(mockUser, never()).setEmail(anyString());
        verify(mockUser, never()).setAge(anyInt());

        verify(userDAO).update(mockUser);
    }

    @Test
    void testDeleteUser() {
        when(userDAO.findById(1)).thenReturn(testUser);
        doNothing().when(userDAO).delete(testUser);

        userService.deleteUser(1);
        verify(userDAO).delete(testUser);
    }
    @Test
    void testDeleteWhenUserNull(){
        when(userDAO.findById(1)).thenReturn(null);

        userService.deleteUser(1);
        verify(userDAO, never()).delete(any(User.class));
    }


}
