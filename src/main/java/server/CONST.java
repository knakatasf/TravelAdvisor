package server;

import java.util.Map;

public class CONST {
    final static public int USERNAME_MIN_LENGTH = 4;
    final static public int PASSWORD_MIN_LENGTH = 8;

    final static public Map<String, String> ERROR_MAP = Map.of(
            "conn", "Database connection error",
            "user", "Invalid username",
            "userTaken", "Username already exists",
            "pass", "Invalid password",
            "userAndPass", "Invalid username and password",
            "userOrPass", "Username and/or password is incorrect"
    );
}
