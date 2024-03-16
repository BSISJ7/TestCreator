package TestCreator.users;

import TestCreator.Test;
import TestCreator.options.Options;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.ArrayList;

public class User{
    private final ArrayList<Test> TESTS_CREATED = new ArrayList<>();

    private String username = "";
    private String hashedPassword = "";
    private String firstName = "";
    private String lastName = "";
    private String email = "";
    private String phoneNumber = "";
    private boolean emailVerified = false;
    private boolean readQuestions = false;
    private String theme = "primer-dark";
    private Options options = new Options();

    public User(String username, String hashedPassword, String firstName, String lastName, String email, String phoneNumber, boolean emailVerified) {
        this.username = username;
        this.hashedPassword = hashedPassword;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.emailVerified = emailVerified;
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

    public User(String username, String hashedPassword, String email) {
        this(username, hashedPassword);
        this.email = email;
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

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
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

    public Node getUserAsXMLNode(Document XMLDocument) {
        Node userNode = XMLDocument.createElement("User");
        Node username = XMLDocument.createElement("Username");
        username.setTextContent(getUsername());
        userNode.appendChild(username);

        return userNode;
    }

    public void loadFromXMLNode(Element userNode) {
        setUsername(userNode.getChildNodes().item(0).getTextContent());
    }

    @Override
    public String toString() {
        return getUsername();
    }

    public boolean canReadQuestions() {
        return readQuestions;
    }
    public void setReadQuestions(boolean readQuestions) {
        this.readQuestions = readQuestions;
    }

    public String getTheme() {
        return theme;
    }
    public void setTheme(String theme) {
        this.theme = theme;
    }

    public Options getOptions() {
        return options;
    }
}
