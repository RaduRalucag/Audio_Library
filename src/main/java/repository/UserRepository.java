package repository;

public interface UserRepository {
    boolean isUsersTableEmpty();
    void deleteAllUsers();
}
