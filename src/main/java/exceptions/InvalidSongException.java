package exceptions;

public class InvalidSongException extends Exception {
    public InvalidSongException(String message) {
        super(message);
    }
}
