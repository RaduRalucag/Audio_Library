package currentUser;

import models.user.User;

public class CurrentUserSession {
    private static CurrentUserSession instance;
    private User user;

    private CurrentUserSession() {
    }

    public static synchronized CurrentUserSession getInstance() {
        if (instance == null) {
            instance = new CurrentUserSession();
        }
        return instance;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
