package dataHandlers;

import models.song.Song;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileHandler {
    public static List<Song> readFromFile(String filename) {
        List<Song> songs = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            String title = null;
            String artist = null;
            int year = 0;

            while ((line = br.readLine()) != null) {
                line = line.trim();

                if (line.isEmpty()) {
                    continue;
                }

                if (line.startsWith("-") && line.endsWith("-")) {
                    title = line.substring(1, line.length() - 1).trim();
                } else if (line.startsWith("*") && line.endsWith("*")) {
                    artist = line.substring(1, line.length() - 1).trim();
                } else if (line.startsWith("!") && line.endsWith("!")) {
                    year = Integer.parseInt(line.substring(1, line.length() - 1).trim());
                }

                if (title != null && artist != null && year != 0) {
                    songs.add(new Song(title, artist, year));
                    title = null;
                    artist = null;
                    year = 0;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return songs;
    }

    public static void saveToFile(String filename, List<Song> songs) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            for (Song song : songs) {
                writer.printf("-%s-\n", song.getTitle());
                writer.printf("*%s*\n", song.getArtist());
                writer.printf("!%d!\n", song.getYear());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
