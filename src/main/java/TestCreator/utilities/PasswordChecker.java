package TestCreator.utilities;

public class PasswordChecker {

    public static final int MIN_LENGTH = 8;
    public static final int MAX_LENGTH = 50;
    public static final boolean REQUIRE_UPPER_CASE = true;
    public static final boolean REQUIRE_LOWER_CASE = true;
    public static final boolean REQUIRE_DIGIT = true;
    public static final boolean REQUIRE_SPECIAL_CHAR = false;

    public static boolean isPasswordCorrect(String password) {
        return checkMinLength(password) && checkMaxLength(password) && checkUpperCase(password)
                && checkLowerCase(password) && checkDigit(password) && checkSpecialChar(password);
    }

    public static boolean checkMinLength(String password) {
        return password.length() >= MIN_LENGTH;
    }

    public static boolean checkMaxLength(String password) {
        return password.length() <= MAX_LENGTH;
    }

    public static boolean checkUpperCase(String password) {
        return password.matches(".*[A-Z].*") || !REQUIRE_UPPER_CASE;
    }

    public static boolean checkLowerCase(String password) {
        return password.matches(".*[a-z].*") || !REQUIRE_LOWER_CASE;
    }

    public static boolean checkDigit(String password) {
        return password.matches(".*[0-9].*") || !REQUIRE_DIGIT;
    }

    public static boolean checkSpecialChar(String password) {
        return password.matches(".*[@#$%^&+=!].*") || !REQUIRE_SPECIAL_CHAR;
    }
}
