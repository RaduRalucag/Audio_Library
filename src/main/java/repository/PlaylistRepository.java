package repository;

import exceptions.DuplicatePlaylistException;
import models.playlist.Playlist;
import models.song.Song;

import java.sql.SQLException;
import java.util.List;

public interface PlaylistRepository {
    boolean exists(String name) throws SQLException;
    void addPlaylist(Playlist playlist) throws SQLException, DuplicatePlaylistException;
    List<Playlist> getAllPlaylists() throws SQLException;
    List<Playlist> findPlaylistByName(String searchValue) throws SQLException;
    List<Playlist> findPlaylistByUser(String searchValue) throws SQLException;
    List<Playlist> findPlaylistByNameAndUser(String name, String user) throws SQLException;
    void addSongToPlaylist(String playlistName, String songName) throws SQLException;
    List<Song> listSongsFromPlaylist(String playlistName) throws SQLException;
}
