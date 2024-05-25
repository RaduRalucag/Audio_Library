package repository;

import exceptions.DuplicateSongException;
import models.song.Song;

import java.sql.SQLException;
import java.util.List;

public interface SongRepository{
    boolean exists(String name, String author) throws SQLException;
    void addSong(Song song) throws SQLException, DuplicateSongException;
    List<Song> getAllSongs() throws SQLException;
    List<Song> findSongByTitle(String searchValue) throws SQLException;
    List<Song> findSongByArtist(String searchValue) throws SQLException;
    List<Song> findSongByTitleAndArtist(String title, String artist) throws SQLException;
    int getNumberSongs() throws SQLException;
}
