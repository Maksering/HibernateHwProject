package org.example;

import javax.persistence.*;
import java.time.LocalDateTime;


@Entity
@Table(name="users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "serial")
    private int id;

    @Column
    private String name;

    @Column
    private String email;

    @Column
    private int age;

    @Column
    private LocalDateTime created_at;

    public  User(){}

    public User(String name, String email, int age){
        this.name = name;
        this.email = email;
        this.age = age;
        this.created_at = LocalDateTime.now();
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public LocalDateTime getCreated_at() {
        return created_at;
    }

    @Override
    public String toString() {
        return String.format("User id = %d | name=%s | email=%s | age=%d | created_at=%s",
                id, name, email, age, created_at);
    }
}
