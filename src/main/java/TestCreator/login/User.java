package TestCreator.login;

import TestCreator.Test;

import java.util.ArrayList;

public class User{
    private final ArrayList<Test> TESTS_CREATED = new ArrayList<>();

    private String username = "";
    private String hashedPassword = "";
    private String firstName = "";
    private String lastName = "";
    private String email = "";
    private String phoneNumber = "";

    public User(String username, String hashedPassword, String firstName, String lastName, String email, String phoneNumber) {
        this.username = username;
        this.hashedPassword = hashedPassword;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    public User(String username, String hashedPassword) {
        this.username = username;
        this.hashedPassword = hashedPassword;
    }

    public User(String username) {
        this(username, "");
    }
    public User() {
        this("Guest", "");
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

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

    @Override
    public String toString() {
        return getUsername();
    }
}
