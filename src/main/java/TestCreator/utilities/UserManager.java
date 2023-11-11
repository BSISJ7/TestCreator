package TestCreator.utilities;

import TestCreator.Main;
import TestCreator.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class UserManager {

    private static final List<User> USER_LIST = new ArrayList<>();

    private static User currentUser;
    private static final UserManager USER_MANAGER = new UserManager();

    private UserManager() {
        if(Main.TESTING_MODE) GenerateUsers();
    }

    public static int size() {
        return USER_LIST.size();
    }

    public void GenerateUsers(){
        for (int x = 0; x < 4; x++) {
            USER_LIST.add(new User(NamesManager.getNamesList().getRandomName()));
        }
        currentUser = USER_LIST.get(new Random().nextInt(USER_LIST.size()));
    }

    public static List<User> getUserList() {
        return USER_LIST;
    }

    public static void addUser(String username, String password) {
        USER_LIST.add(new User(username, password));
    }

    public static void addUser(String username) {
        USER_LIST.add(new User(username));
    }

    public static boolean userExists(String username) {
        for (User user : USER_LIST) {
            if (user.getUsername().equals(username)) return true;
        }
        return false;
    }

    public static User getUser(String username) {
        for (User user : USER_LIST) {
            if (user.getUsername().equals(username)) return user;
        }
        return null;
    }

    public static void setCurrentUser(String username) {
        try {
            currentUser = getUser(username);
        } catch (NullPointerException e) {
            System.out.println("User does not exist");
        }
    }

    public static String getCurrentUsername() {
        return currentUser.getUsername();
    }
}
