package service;

import currentUser.CurrentUserSession;
import database.DatabaseConnection;
import exceptions.DuplicateSongException;
import exceptions.InvalidSongException;
import models.audit.Audit;
import models.song.Song;
import paginated.PaginatedList;
import repository.SongRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SongService implements SongRepository {
    private Connection connection;
    Audit audit = new Audit();

    public SongService() throws SQLException {
        this.connection = DatabaseConnection.getConnection();
    }

    @Override
    public boolean exists(String title, String artist) throws SQLException {
        String query = "SELECT * FROM Song WHERE title = ? AND artist = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, title);
        preparedStatement.setString(2, artist);
        ResultSet resultSet = preparedStatement.executeQuery();
        return resultSet.next();
    }

    @Override
    public void addSong(Song song) throws SQLException, DuplicateSongException {
        if (exists(song.getTitle(), song.getArtist())) {
            throw new DuplicateSongException("Song already exists!");
        }
        String query = "INSERT INTO Song (title, artist, year) VALUES (?, ?, ?)";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, song.getTitle());
        preparedStatement.setString(2, song.getArtist());
        preparedStatement.setInt(3, song.getYear());
        preparedStatement.executeUpdate();
        audit.logAction("Added song", CurrentUserSession.getInstance().getUser().getUsername());
    }

    @Override
    public List<Song> getAllSongs() throws SQLException {
        String query = "SELECT * FROM Song";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        ResultSet resultSet = preparedStatement.executeQuery();
        List<Song> allSongs = new ArrayList<>();

        while (resultSet.next()) {
            String title = resultSet.getString("title");
            String artist = resultSet.getString("artist");
            int year = resultSet.getInt("year");

            Song song = new Song(title, artist, year);
            allSongs.add(song);
        }

        resultSet.close();
        preparedStatement.close();
        audit.logAction("Listed all songs", CurrentUserSession.getInstance().getUser().getUsername());

        return allSongs;
    }

    @Override
    public List<Song> findSongByTitle(String title) {
        List<Song> songs = new ArrayList<>();
        String query = "SELECT title, artist, year FROM Song WHERE title = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, title);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String songTitle = resultSet.getString("title");
                String artist = resultSet.getString("artist");
                int year = resultSet.getInt("year");
                songs.add(new Song(songTitle, artist, year));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        audit.logAction("Found song by title", CurrentUserSession.getInstance().getUser().getUsername());
        return songs;
    }

    @Override
    public List<Song> findSongByArtist(String searchValue) throws SQLException {
        String query = "SELECT * FROM Song WHERE artist = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, searchValue);
        ResultSet resultSet = preparedStatement.executeQuery();
        List<Song> songs = new ArrayList<>();

        while (resultSet.next()) {
            String title = resultSet.getString("title");
            String artist = resultSet.getString("artist");
            int year = resultSet.getInt("year");

            Song song = new Song(title, artist, year);

            songs.add(song);

            System.out.println("Title: " + title);
            System.out.println("Artist: " + artist);
            System.out.println("Year: " + year);
            System.out.println();
        }
        resultSet.close();
        preparedStatement.close();
        audit.logAction("Found song by artist", CurrentUserSession.getInstance().getUser().getUsername());

        return songs;
    }

    @Override
    public List<Song> findSongByTitleAndArtist(String title, String artist) throws SQLException {
        String query = "SELECT * FROM Song WHERE title = ? AND artist = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, title);
        preparedStatement.setString(2, artist);
        ResultSet resultSet = preparedStatement.executeQuery();
        List<Song> songs = new ArrayList<>();

        while (resultSet.next()) {
            String songTitle = resultSet.getString("title");
            String songArtist = resultSet.getString("artist");
            int year = resultSet.getInt("year");

            Song song = new Song(songTitle, songArtist, year);

            songs.add(song);

            System.out.println("Title: " + songTitle);
            System.out.println("Artist: " + songArtist);
            System.out.println("Year: " + year);
            System.out.println();
        }
        resultSet.close();
        preparedStatement.close();
        audit.logAction("Found song by title and artist", CurrentUserSession.getInstance().getUser().getUsername());

        return songs;
    }

    @Override
    public int getNumberSongs() throws SQLException {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        int count = 0;

        try {
            connection = DatabaseConnection.getConnection();
            String query = "SELECT COUNT(*) AS total FROM Song";
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