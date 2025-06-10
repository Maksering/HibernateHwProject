package org.example.service;

import org.example.dao.UserDAO;
import org.example.user.User;

import java.util.List;

public class UserService {

    private UserDAO userDAO = new UserDAO();

    public void createUser(String name, String email, int age) {
        User user = new User(name, email, age);
        userDAO.create(user);
        System.out.println("User created: " + user);
    }

    public void findUserByid(int id){
        User user = userDAO.findById(id);
        if(user==null){
            System.out.println("User not found");
        } else {
            System.out.println("Founded user: " + user);
        }
    }

    public void showAllUsers(){
        List<User> users = userDAO.findAll();
        if (users.isEmpty()) {
            System.out.println("No users found");
        } else {
            System.out.println("\nList of all users:");
            users.forEach(System.out::println);
        }
    }

    public void updateUser(int id, String name, String email, String ageInput) {
        User user = userDAO.findById(id);
        if (user == null) {
            System.out.println("User not found");
            return;
        }

        if (!name.isEmpty()) {
            user.setName(name);
        }
        if (!email.isEmpty()){
            user.setEmail(email);
        }
        if (!ageInput.isEmpty()) {
            user.setAge(Integer.parseInt(ageInput));
        }

        userDAO.update(user);
        System.out.println("User updated: " + user);
    }

    public void deleteUser(int id) {
        User user = userDAO.findById(id);
        if (user != null) {
            userDAO.delete(user);
            System.out.println("User deleted");
        } else {
            System.out.println("User not found ");
        }
    }
}
