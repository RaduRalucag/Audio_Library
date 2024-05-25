package models.playlist;

import currentUser.CurrentUserSession;
import models.song.Song;

import java.util.List;

public class Playlist {
    private String name;
    String username;
    List<Song> songs;

    public Playlist(String name, String user) {
        this.name = name;
        this.username = user;
    }

    public String getName() {
        return name;
    }

    public String getUser() {
        return CurrentUserSession.getInstance().getUser().getUsername();
    }

}
