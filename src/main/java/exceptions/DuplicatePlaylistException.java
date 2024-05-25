package exceptions;

public class DuplicatePlaylistException extends Exception{
    public DuplicatePlaylistException(String message) {
        super(message);
    }
}
