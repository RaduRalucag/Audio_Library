package auth;

import database.DatabaseConnection;
import models.audit.Audit;
import models.user.Role;
import models.user.User;
import currentUser.CurrentUserSession;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Login {
    private final Audit audit;

    public Login(Audit audit) {
        this.audit = audit;
    }

    public boolean login(String username, String password) {
        try (Connection connection = DatabaseConnection.getConnection()) {

            if (isUserTableEmpty(connection)) {
                User user = new User(username, password, Role.ADMIN);
                audit.logAction("New admin user created", username);
                System.out.println("You are now authenticated as " + user.getUsername() + " with role " + user.getRole());
                CurrentUserSession.getInstance().setUser(user);
                return true;
            }

            try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM User WHERE username = ? AND password = ?")) {
                statement.setString(1, username);
                statement.setString(2, password);

                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        String roleString = resultSet.getString("role");
                        Role role = Role.fromString(roleString);
                        User user = new User(resultSet.getString("username"), resultSet.getString("password"), role);
                        CurrentUserSession.getInstance().setUser(user);
                        audit.logAction("User logged in", username);
                        System.out.println("You are now authenticated as " + user.getUsername() + " with role " + user.getRole());
                        return true;
                    } else {
                        System.out.println("Username or password is invalid.");
                        audit.logAction("Failed login attempt", username);
                        return false;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            audit.logAction("Database error during login attempt", username);
            return false;
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            audit.logAction("Invalid role found in database for user", username);
            return false;
        }
    }

    private boolean isUserTableEmpty(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) FROM User")) {
            resultSet.next();
            int count = resultSet.getInt(1);
            return count == 0;
        }
    }
}
