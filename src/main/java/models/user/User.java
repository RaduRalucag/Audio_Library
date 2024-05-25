package models.user;

public class User {
    private String username;
    private String password;
    private Role role;
    boolean isLogged;

    public User(String username, String password, Role role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public Role getRole() {
        return role;
    }

    public boolean getIsLogged() {
        return isLogged;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public void setIsLogged(boolean isLogged) {
        this.isLogged = isLogged;
    }
}
