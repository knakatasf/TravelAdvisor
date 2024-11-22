package model;

public class PreparedStatements {
    public static final String CREATE_USER_TABLE_SQL = """
            CREATE TABLE users (
                userid INTEGER AUTO_INCREMENT PRIMARY KEY,
                username VARCHAR(32) NOT NULL UNIQUE,
                password CHAR(64) NOT NULL,
                usersalt CHAR(32) NOT NULL);
            """;

    public static final String REGISTER_USER_SQL = """
            INSERT INTO users (username, password, usersalt)
            VALUES (?, ?, ?);
            """;

    public static final String SALT_SQL =
            "SELECT usersalt FROM users WHERE username = ?";

    public static final String AUTH_SQL =
            "SELECT username FROM users WHERE username = ? AND password = ?";

}
