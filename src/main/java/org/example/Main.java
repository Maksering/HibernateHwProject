package org.example;

import org.example.service.UserService;
import org.example.util.HibernateUtils;

import java.util.Scanner;

public class Main {
    private static UserService userService = new UserService();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        try{
            boolean working = true;
            while (working){
                showMenu();
                int choice=scanner.nextInt();
                scanner.nextLine();

                switch (choice){
                    case 1 -> create();
                    case 2 -> findById();
                    case 3 -> userService.showAllUsers();
                    case 4 -> update();
                    case 5 -> delete();
                    case 6 -> working = false;
                    default -> System.out.println("Wrong action");
                }
            }
        } finally {
            HibernateUtils.shutdown();
            scanner.close();
        }
    }

    private static void showMenu(){
        System.out.println("Hibernate project");
        System.out.println("1. Create User");
        System.out.println("2. Find user by id");
        System.out.println("3. Show all");
        System.out.println("4. Update user");
        System.out.println("5. Delete user");
        System.out.println("6. Exit");
        System.out.print("Action: ");
    }

    private static void create(){
        System.out.println("Enter name: ");
        String name = scanner.nextLine();

        System.out.println("Enter email: ");
        String email = scanner.nextLine();

        System.out.println("Enter age: ");
        int age = scanner.nextInt();
        scanner.nextLine();

        userService.createUser(name,email,age);
    }

    private static void findById(){
        System.out.println("Enter id: ");
        int id = scanner.nextInt();
        scanner.nextLine();

        userService.findUserByid(id);
    }

    private static void update(){
        System.out.println("Enter id: ");
        int id = scanner.nextInt();
        scanner.nextLine();

        System.out.println("Enter new name: ");
        String name = scanner.nextLine();

        System.out.println("Enter new email: ");
        String email = scanner.nextLine();

        System.out.println("Enter new age: ");
        String  age = scanner.nextLine();

        userService.updateUser(id, name, email, age);
    }

    private static void delete(){
        System.out.println("Enter id: ");
        int id = scanner.nextInt();
        scanner.nextLine();

        userService.deleteUser(id);
    }
}