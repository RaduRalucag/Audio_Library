package service;

import models.user.Role;
import models.user.User;

public class AvailabilityService {

    public static boolean checkLoggedOut(User currentUser) {
        return currentUser == null || !currentUser.getIsLogged();
    }

    public static boolean checkLoggedIn(User currentUser) {
        return currentUser != null && currentUser.getIsLogged();
    }

    public static boolean checkAdmin(User currentUser) {
        return currentUser != null && currentUser.getIsLogged() && currentUser.getRole().equals(Role.ADMIN);
    }

}