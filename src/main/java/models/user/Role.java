package models.user;

public enum Role {
    ADMIN,
    ANONIM,
    AUTH;

    public static Role fromString(String role) {
        try {
            return Role.valueOf(role.toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new IllegalArgumentException("Invalid role: " + role);
        }
    }
}
