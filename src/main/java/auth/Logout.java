package auth;

import models.audit.Audit;
import models.user.Role;
import models.user.User;

public class Logout {
    Audit audit = new Audit();
    public void logout(User user) {
        if (user != null) {
            user.setRole(Role.ANONIM);
            System.out.println("You have been logged out, " + user.getUsername() + ".");
            audit.logAction("User logged out", user.getUsername());
        } else {
            System.out.println("No user is currently logged in.");
            audit.logAction("Failed logout attempt", "UNKNOWN");
        }
    }
}
