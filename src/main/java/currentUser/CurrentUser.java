package currentUser;

import models.user.User;
import service.AvailabilityService;

public abstract class CurrentUser {
    public static boolean checkLoggedOut() {
        User currentUser = CurrentUserSession.getInstance().getUser();
        return AvailabilityService.checkLoggedOut(currentUser);
    }

    public boolean checkLoggedIn() {
        User currentUser = CurrentUserSession.getInstance().getUser();
        return AvailabilityService.checkLoggedIn(currentUser);
    }

    public static boolean checkAdmin() {
        User currentUser = CurrentUserSession.getInstance().getUser();
        return AvailabilityService.checkAdmin(currentUser);
    }
}
