package com.BSISJ7.TestCreator;

import java.util.ArrayList;

public class User {
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;

    private final ArrayList<Test> TESTS_CREATED = new ArrayList<>();
    public User(String username, String password, String firstName, String lastName, String email, String phoneNumber) {
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public User() {
        this("Guest", "");
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getFirstName(){
        return firstName;
    }

    public String getLastName(){
        return lastName;
    }

    public String getEmail() { return email; }

    public String getPhoneNumber() { return phoneNumber; }

    public void setUsername(String username) { this.username = username; }

    public void setPassword(String password) { this.password = password; }

    public void setFirstName(String firstName) { this.firstName = firstName; }

    public void setLastName(String lastName) { this.lastName = lastName;}

    public void setEmail(String email) { this.email = email; }

    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public void addTest(Test test) {
        TESTS_CREATED.add(test);
    }

    public void removeTest(Test test) {
        TESTS_CREATED.remove(test);
    }

    //Return a copy of the TESTS_CREATED ArrayList
    public ArrayList<Test> getTestsCreated() {
        return new ArrayList<>(TESTS_CREATED);
    }
}
