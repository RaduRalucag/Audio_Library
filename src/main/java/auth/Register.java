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

public class Register {
    public boolean register(String username, String password, Role role) {
        Connection connection = null;
        PreparedStatement statement = null;
        Audit audit = new Audit();

        try {
            connection = DatabaseConnection.getConnection();

            // Check if the username already exists
            if (usernameExists(connection, username)) {
                audit.logAction("Failed to register, username already exists", username);
                System.out.println("User with given username already exists! Please try again!");
                return false;
            }

            statement = connection.prepareStatement("INSERT INTO User (username, password, role) VALUES (?, ?, ?)");
            statement.setString(1, username);
            statement.setString(2, password);
            statement.setString(3, role.name());
            int rowsInserted = statement.executeUpdate();

            if (rowsInserted > 0) {
                User user = new User(username, password, role);
                CurrentUserSession.getInstance().setUser(user);
                audit.logAction("Registered", username);
                System.out.println("Registered account with user name " + username);
                return true;
            } else {
                audit.logAction("Failed to register", username);
                System.out.println("Registration failed! Please try again!");
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            audit.logAction("Database error during registration attempt", username);
            return false;
        } finally {
            closeResources(statement, connection);
        }
    }

    private boolean usernameExists(Connection connection, String username) throws SQLException {
        String query = "SELECT 1 FROM User WHERE username = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    private void closeResources(PreparedStatement statement, Connection connection) {
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
