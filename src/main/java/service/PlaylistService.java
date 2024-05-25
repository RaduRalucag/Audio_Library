package service;

import currentUser.CurrentUserSession;
import database.DatabaseConnection;
import exceptions.DuplicatePlaylistException;
import models.audit.Audit;
import models.playlist.Playlist;
import models.song.Song;
import paginated.PaginatedList;
import repository.PlaylistRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PlaylistService implements PlaylistRepository{
    private Connection connection;
    Audit audit = new Audit();

    public PlaylistService() throws SQLException {
        this.connection = DatabaseConnection.getConnection();
    }

    @Override
    public boolean exists(String name) throws SQLException {
        String query = "SELECT * FROM Playlist WHERE name = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, name);
        return preparedStatement.executeQuery().next();
    }

    @Override
    public void addPlaylist(Playlist playlist) throws SQLException, DuplicatePlaylistException {
        if (exists(playlist.getName())) {
            throw new DuplicatePlaylistException("Playlist already exists!");
        }
        String query = "INSERT INTO Playlist (name, username) VALUES (?, ?)";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, playlist.getName());
        preparedStatement.setString(2, playlist.getUser());
        preparedStatement.executeUpdate();
        audit.logAction("Added playlist", CurrentUserSession.getInstance().getUser().getUsername());
    }

    @Override
    public List<Playlist> getAllPlaylists() throws SQLException {
        String query = "SELECT * FROM Playlist";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        ResultSet resultSet = preparedStatement.executeQuery();
        List<Playlist> allPlaylists = new ArrayList<>();

        while (resultSet.next()) {
            String name = resultSet.getString("name");
            String user = resultSet.getString("username");

            Playlist playlist = new Playlist(name, user);
            allPlaylists.add(playlist);
        }

        resultSet.close();
        preparedStatement.close();
        audit.logAction("Listed all playlists", CurrentUserSession.getInstance().getUser().getUsername());

        return allPlaylists;
    }

    @Override
    public List<Playlist> findPlaylistByName(String searchValue) throws SQLException {
        String query = "SELECT * FROM Playlist WHERE name = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, searchValue);
        ResultSet resultSet = preparedStatement.executeQuery();
        List<Playlist> playlists = new ArrayList<>();

        while (resultSet.next()) {
            String name = resultSet.getString("name");
            String user = resultSet.getString("username");

            Playlist playlist = new Playlist(name, user);
            playlists.add(playlist);
        }

        resultSet.close();
        preparedStatement.close();
        audit.logAction("Found playlist by name", CurrentUserSession.getInstance().getUser().getUsername());

        return playlists;
    }

    @Override
public List<Playlist> findPlaylistByUser(String searchValue) throws SQLException {
        String query = "SELECT * FROM Playlist WHERE username = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, searchValue);
        ResultSet resultSet = preparedStatement.executeQuery();
        List<Playlist> playlists = new ArrayList<>();

        while (resultSet.next()) {
            String name = resultSet.getString("name");
            String user = resultSet.getString("username");

            Playlist playlist = new Playlist(name, user);
            playlists.add(playlist);
        }

        resultSet.close();
        preparedStatement.close();
        audit.logAction("Found playlist by user", CurrentUserSession.getInstance().getUser().getUsername());

        return playlists;
    }

    @Override
    public List<Playlist> findPlaylistByNameAndUser(String name, String user) throws SQLException {
        String query = "SELECT * FROM Playlist WHERE name = ? AND username = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, name);
        preparedStatement.setString(2, user);
        ResultSet resultSet = preparedStatement.executeQuery();
        List<Playlist> playlists = new ArrayList<>();

        while (resultSet.next()) {
            String playlistName = resultSet.getString("name");
            String playlistUser = resultSet.getString("username");

            Playlist playlist = new Playlist(playlistName, playlistUser);
            playlists.add(playlist);
        }

        resultSet.close();
        preparedStatement.close();
        audit.logAction("Found playlist by name and user", CurrentUserSession.getInstance().getUser().getUsername());

        return playlists;
    }

    @Override
    public void addSongToPlaylist(String playlistName, String songName) throws SQLException {
        int playlist_id = getPlaylistId(playlistName);
        int song_id = getSongId(songName);
        String query = "INSERT INTO SongPlaylist (song_id, playlist_id) VALUES (?, ?)";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setInt(1, song_id);
        preparedStatement.setInt(2, playlist_id);
        preparedStatement.executeUpdate();
        audit.logAction("Added song to playlist", CurrentUserSession.getInstance().getUser().getUsername());
    }

    public int getSongId(String title) throws SQLException {
        String query = "SELECT id FROM Song WHERE title = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, title);
        ResultSet resultSet = preparedStatement.executeQuery();
        int id = -1;
        if (resultSet.next()) {
            id = resultSet.getInt("id");
        }
        return id;
    }

    public int getPlaylistId(String name) throws SQLException {
        String query = "SELECT id FROM Playlist WHERE name = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, name);
        ResultSet resultSet = preparedStatement.executeQuery();
        int id = -1;
        if (resultSet.next()) {
            id = resultSet.getInt("id");
        }
        return id;
    }

    public List<Song> listSongsFromPlaylist(String playlistName) throws SQLException {
        List<Song> songs = new ArrayList<>();
        int playlistId = getPlaylistId(playlistName);

        if (playlistId == -1) {
            System.out.println("Playlist not found.");
            return songs;
        }

        String query = "SELECT s.id, s.title, s.artist, s.year " +
                "FROM Song s " +
                "INNER JOIN SongPlaylist sp ON s.id = sp.song_id " +
                "WHERE sp.playlist_id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, playlistId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    int songId = resultSet.getInt("id");
                    String title = resultSet.getString("title");
                    String artist = resultSet.getString("artist");
                    int year = resultSet.getInt("year");

                    Song song = new Song(title, artist, year);
                    songs.add(song);
                }
            }
        }

        return songs;
    }

    public int getNumberPlaylists() throws SQLException {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        int count = 0;

        try {
            connection = DatabaseConnection.getConnection();
            String query = "SELECT COUNT(*) AS total FROM Playlist";
            statement = connection.prepareStatement(query);
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                count = resultSet.getInt("total");
            }
        } finally {
            if (resultSet != null) {
                resultSet.close();
            }
            if (statement != null) {
                statement.close();
            }
            if (connection != null) {
                connection.close();
            }
        }

        return count;
    }


}
