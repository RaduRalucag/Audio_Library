package org.example;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

import auth.Login;
import auth.Logout;
import auth.Promote;
import auth.Register;
import currentUser.CurrentUser;
import currentUser.CurrentUserSession;
import dataHandlers.JsonHandler;
import exceptions.DuplicatePlaylistException;
import exceptions.DuplicateSongException;
import exceptions.UserNotFoundException;
import models.audit.Audit;
import models.song.Song;
import models.playlist.Playlist;
import models.user.Role;
import paginated.PaginatedList;
import service.PlaylistService;
import service.SongService;
import dataHandlers.CsvHandler;
import dataHandlers.FileHandler;

public class Main {
    public static void main(String[] args){
        System.out.println("Welcome to the audio library! Please select an option:");
        menu();
    }

    public void readJson() throws SQLException {
        JsonHandler jsonHandler = new JsonHandler();
        List<Song> songs;
        try {
            songs = jsonHandler.readFromJson("C:\\Users\\raluc\\IdeaProjects\\PAO_Audio_Library\\src\\main\\java\\songs.json");
        } catch (IOException e) {
            System.err.println("Error reading from JSON file: " + e.getMessage());
            return;
        }

        SongService songService = new SongService();
        for (Song song : songs) {
            try {
                songService.addSong(song);
                System.out.println("Added song: " + song.getTitle());
            } catch (SQLException | DuplicateSongException e) {
                System.err.println("Error adding song: " + e.getMessage());
            }
        }
    }

    public void readCsv() throws SQLException {
        CsvHandler csvHandler = new CsvHandler();
        List<Song> songs;
        try {
            songs = csvHandler.readFromCsv("C:\\Users\\raluc\\IdeaProjects\\PAO_Audio_Library\\src\\main\\java\\songs.csv");
        } catch (IOException e) {
            System.err.println("Error reading from CSV file: " + e.getMessage());
            return;
        }

        SongService songService = new SongService();
        for (Song song : songs) {
            try {
                songService.addSong(song);
                System.out.println("Added song: " + song.getTitle());
            } catch (SQLException | DuplicateSongException e) {
                System.err.println("Error adding song: " + e.getMessage());
            }
        }
    }

    public void readFile() throws SQLException {
        String filename = "C:\\Users\\raluc\\IdeaProjects\\PAO_Audio_Library\\src\\main\\java\\songs.txt";

        List<Song> songs = FileHandler.readFromFile(filename);

        SongService songService = new SongService();
        try {
            for (Song song : songs) {
                songService.addSong(song);
            }
            System.out.println("Songs have been saved to the database successfully.");
        } catch (SQLException | DuplicateSongException e) {
            System.out.println("Error: Failed to save songs to the database.");
            e.printStackTrace();
        }
    }

    public static void menu(){
        System.out.println("1. Login");
        System.out.println("2. Register");
        System.out.println("3. Exit");

        Scanner scanner = new Scanner(System.in);
        boolean isOptionValid = false;

        while (!isOptionValid) {
            try {
                int option = Integer.parseInt(scanner.nextLine().trim());

                switch (option) {
                    case 1:
                        System.out.println("Please enter your username:");
                        String logUsername = scanner.nextLine().trim();
                        System.out.println("Please enter your password:");
                        String logPassword = scanner.nextLine().trim();
                        Audit audit = new Audit();
                        Login login = new Login(audit);
                        if (login.login(logUsername, logPassword)) {
                            System.out.println("Login successful!");
                            loggedCommands();
                        } else {
                            System.out.println("Login failed. Please try again.");
                        }
                        break;

                    case 2:
                        System.out.println("Please enter your username:");
                        String regUsername = scanner.nextLine().trim();
                        System.out.println("Please enter your password:");
                        String regPassword = scanner.nextLine().trim();
                        Register register = new Register();
                        if (register.register(regUsername, regPassword, Role.AUTH)) {
                            System.out.println("Registration successful!");
                            loggedCommands();
                        } else {
                            System.out.println("Failed to register. Please try again.");
                        }
                        break;

                    case 3:
                        System.out.println("Exiting the system.");
                        System.exit(0);

                    default:
                        System.out.println("Invalid option. Please try again.");
                }
                menu();
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            } catch (SQLException e) {
                throw new RuntimeException(e);
            } catch (DuplicateSongException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void loggedCommands() throws SQLException, DuplicateSongException {
        System.out.println("Please select an option:");
        System.out.println("1. Songs");
        System.out.println("2. Playlists");
        System.out.println("3. Add song to playlist");
        System.out.println("4. Promote");
        System.out.println("5. Logout");
        Scanner scanner = new Scanner(System.in);
        boolean isOptionValid = false;

        while (!isOptionValid) {
            try {
                int option2 = Integer.parseInt(scanner.nextLine().trim());

                switch (option2) {
                    case 1:
                        songCommands();
                        break;

                    case 2:
                        playlistCommands();
                        break;

                    case 3:
                        System.out.println("Please enter the song title:");
                        String title = scanner.nextLine().trim();
                        System.out.println("Please enter the playlist name:");
                        String playlistName = scanner.nextLine().trim();
                        PlaylistService playlistService = new PlaylistService();
                        playlistService.addSongToPlaylist(playlistName, title);
                        break;

                    case 4:
                        if(CurrentUserSession.getInstance().getUser().getRole().equals(Role.AUTH)){
                            System.out.println("You do not have permission to execute this command.");
                            break;
                        }
                        Promote promote = new Promote();
                        System.out.println("Please enter the username to promote:");
                        String username = scanner.nextLine().trim();
                        try {
                            promote.promote(username);
                            System.out.println("User promoted successfully.");
                        } catch (UserNotFoundException e) {
                            System.out.println("User not found: " + e.getMessage());
                        }
                        break;

                    case 5:
                        Logout logout = new Logout();
                        logout.logout(CurrentUserSession.getInstance().getUser());
                        return;

                    default:
                        System.out.println("Invalid option. Please try again.");
                }
                loggedCommands();
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }

    public static void songCommands() throws SQLException, DuplicateSongException {
        Scanner scanner = new Scanner(System.in);
        SongService songService = new SongService();

        System.out.println("Please select an option:");
        System.out.println("1. Add song");
        System.out.println("2. List all songs");
        System.out.println("3. Find song by title");
        System.out.println("4. Find song by artist");
        System.out.println("5. Find song by title and artist");
        System.out.println("6. Exit");

        int option = scanner.nextInt();
        boolean isOptionValid = false;
        while (!isOptionValid) {
            switch (option) {
                case 1:
                    System.out.println("Please enter the song title:");
                    String title = scanner.next();
                    System.out.println("Please enter the song artist:");
                    String artist = scanner.next();
                    System.out.println("Please enter the release year:");
                    int year = scanner.nextInt();
                    Song newSong = new Song(title, artist, year);
                    songService.addSong(newSong);
                    System.out.println("Song added successfully!");
                    break;
                case 2:
                    int totalSongs = songService.getNumberSongs();
                    int totalPages = (int) Math.ceil((double) totalSongs / 10);
                    System.out.println("Enter page number between 1 and " + totalPages + ":");
                    int page = scanner.nextInt();
                    if (page < 1 || page > totalPages) {
                        System.out.println("Invalid page number. Please try again.");
                        break;
                    }
                    List<Song> allSongs = songService.getAllSongs();
                    List<Song> songs = PaginatedList.getPageItems(allSongs, page, 10);
                    for (Song song : songs) {
                        System.out.println(song.getTitle() + " by " + song.getArtist() + " (" + song.getYear() + ")");
                    }
                    break;
                case 3:
                    System.out.println("Please enter the song title:");
                    String searchTitle = scanner.next();
                    List<Song> songsByTitle = songService.findSongByTitle(searchTitle);
                    if (songsByTitle.isEmpty()) {
                        System.out.println("No songs found with the title: " + searchTitle);
                    } else {
                        for (Song song : songsByTitle) {
                            System.out.println(song.getTitle() + " by " + song.getArtist() + " (" + song.getYear() + ")");
                        }
                    }
                    break;
                case 4:
                    System.out.println("Please enter the song artist:");
                    String searchArtist = scanner.next();
                    List<Song> songsByArtist = songService.findSongByArtist(searchArtist);
                    if (songsByArtist.isEmpty()) {
                        System.out.println("No songs found by artist: " + searchArtist);
                    } else {
                        for (Song song : songsByArtist) {
                            System.out.println(song.getTitle() + " by " + song.getArtist() + " (" + song.getYear() + ")");
                        }
                    }
                    break;
                case 5:
                    System.out.println("Please enter the song title:");
                    String searchTitleArtist = scanner.next();
                    System.out.println("Please enter the song artist:");
                    String searchArtistTitle = scanner.next();
                    List<Song> songsByTitleArtist = songService.findSongByTitleAndArtist(searchTitleArtist, searchArtistTitle);
                    if (songsByTitleArtist.isEmpty()) {
                        System.out.println("No songs found with title: " + searchTitleArtist + " and artist: " + searchArtistTitle);
                    } else {
                        for (Song song : songsByTitleArtist) {
                            System.out.println(song.getTitle() + " by " + song.getArtist() + " (" + song.getYear() + ")");
                        }
                    }
                    break;
                case 6:
                    System.out.println("Exiting the system.");
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
                    break;
            }
            songCommands();
        }
    }

    public static void playlistCommands() {
        System.out.println("Please select an option:");
        System.out.println("1. Create playlist");
        System.out.println("2. List playlists");
        System.out.println("3. Find playlist by name");
        System.out.println("4. Find playlist by user");
        System.out.println("5. Find playlist by name and user");
        System.out.println("6. List songs from playlist");
        System.out.println("7. Exit");

        Scanner scanner = new Scanner(System.in);
        boolean isOptionValid = false;

        while (!isOptionValid) {
            try {
                int option = Integer.parseInt(scanner.nextLine().trim());

                switch (option) {
                    case 1:
                        System.out.println("Please enter the playlist name:");
                        String name = scanner.nextLine().trim();
                        Playlist playlist = new Playlist(name, CurrentUserSession.getInstance().getUser().getUsername());
                        PlaylistService playlistService = new PlaylistService();
                        playlistService.addPlaylist(playlist);
                        System.out.println("Playlist created successfully!");
                        break;

                    case 2:
                        PlaylistService playlistService2 = new PlaylistService();
                        List<Playlist> allPlaylists = playlistService2.getAllPlaylists();
                        if (allPlaylists.isEmpty()) {
                            System.out.println("No playlists found.");
                        } else {
                            int totalPlaylists = playlistService2.getNumberPlaylists();
                            int totalPages = (int) Math.ceil((double) totalPlaylists / 10);
                            System.out.println("Enter page number between 1 and " + totalPages + ":");
                            int page = scanner.nextInt();
                            if (page < 1 || page > totalPages) {
                                System.out.println("Invalid page number. Please try again.");
                                break;
                            }
                            List<Playlist> playlists = PaginatedList.getPageItems(allPlaylists, page, 10);
                            for (Playlist p : playlists) {
                                System.out.println(p.getName() + " by " + p.getUser());
                            }
                        }
                        break;

                    case 3:
                        System.out.println("Please enter the playlist name:");
                        String playlistName = scanner.nextLine().trim();
                        PlaylistService playlistService3 = new PlaylistService();
                        List<Playlist> playlistsByName = playlistService3.findPlaylistByName(playlistName);
                        if (playlistsByName.isEmpty()) {
                            System.out.println("No playlists found with the name: " + playlistName);
                        } else {
                            System.out.println("Playlists with the name '" + playlistName + "':");
                            for (Playlist p : playlistsByName) {
                                System.out.println(p.getName() + " by " + p.getUser());
                            }
                        }
                        break;

                    case 4:
                        System.out.println("Please enter the username:");
                        String username = scanner.nextLine().trim();
                        PlaylistService playlistService4 = new PlaylistService();
                        List<Playlist> playlistsByUser = playlistService4.findPlaylistByUser(username);
                        if (playlistsByUser.isEmpty()) {
                            System.out.println("No playlists found for user: " + username);
                        } else {
                            System.out.println("Playlists by user '" + username + "':");
                            for (Playlist p : playlistsByUser) {
                                System.out.println(p.getName() + " by " + p.getUser());
                            }
                        }
                        break;

                    case 5:
                        System.out.println("Please enter the playlist name:");
                        String playlistName2 = scanner.nextLine().trim();
                        System.out.println("Please enter the username:");
                        String username2 = scanner.nextLine().trim();
                        PlaylistService playlistService5 = new PlaylistService();
                        List<Playlist> playlistsByNameAndUser = playlistService5.findPlaylistByNameAndUser(playlistName2, username2);
                        if (playlistsByNameAndUser.isEmpty()) {
                            System.out.println("No playlists found with the name '" + playlistName2 + "' for user '" + username2 + "'");
                        } else {
                            System.out.println("Playlists with the name '" + playlistName2 + "' by user '" + username2 + "':");
                            for (Playlist p : playlistsByNameAndUser) {
                                System.out.println(p.getName() + " by " + p.getUser());
                            }
                        }
                        break;
                    case 6:
                        System.out.println("Please enter the playlist name:");
                        String playlistName3 = scanner.nextLine().trim();
                        PlaylistService playlistService6 = new PlaylistService();
                        List<Song> songs = playlistService6.listSongsFromPlaylist(playlistName3);
                        if (songs.isEmpty()) {
                            System.out.println("No songs found in playlist: " + playlistName3);
                        } else {
                            System.out.println("Songs in playlist '" + playlistName3 + "':");
                            for (Song song : songs) {
                                System.out.println(song.getTitle() + " by " + song.getArtist() + " (" + song.getYear() + ")");
                            }
                        }
                        break;

                    case 7:
                        System.out.println("Exiting the system.");
                        return;

                    default:
                        System.out.println("Invalid option. Please try again.");
                }
                playlistCommands();
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            } catch (SQLException e) {
                System.out.println("Error occurred: " + e.getMessage());
            } catch (DuplicatePlaylistException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }
}
