package auth;

import currentUser.CurrentUser;
import currentUser.CurrentUserSession;
import exceptions.UserNotFoundException;
import models.audit.Audit;
import models.user.Role;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import database.DatabaseConnection;

public class Promote {
    Audit audit = new Audit();

    public void promote(String username) {
        if (!CurrentUserSession.getInstance().getUser().getRole().equals(Role.ADMIN)){
            audit.logAction("Failed to promote user to admin", username);
            throw new SecurityException("You do not have permission to execute this command.");
        }

        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = DatabaseConnection.getConnection();
            statement = connection.prepareStatement("UPDATE User SET role = ? WHERE username = ?");
            statement.setString(1, Role.ADMIN.name());
            statement.setString(2, username);
            int rowsUpdated = statement.executeUpdate();

            if (rowsUpdated > 0) {
                System.out.println("User " + username + " has been promoted to admin.");
                audit.logAction("User promoted to admin", username);
            } else {
                audit.logAction("Failed to promote user to admin", username);
                throw new UserNotFoundException("Failed to promote user to admin. User not found.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(statement, connection);
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

