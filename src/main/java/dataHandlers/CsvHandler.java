package dataHandlers;

import models.song.Song;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.csv.CSVPrinter;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CsvHandler {
    public void writeToCsv(List<Song> songs, String filename) throws IOException {
        try (Writer writer = new FileWriter(filename);
             CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader("Title", "Artist", "Year"))) {
            for (Song song : songs) {
                csvPrinter.printRecord(song.getTitle(), song.getArtist(), song.getYear());
            }
            csvPrinter.flush();
        }
    }

    public List<Song> readFromCsv(String filename) throws IOException {
        List<Song> songs = new ArrayList<>();
        try (Reader reader = new FileReader(filename);
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader())) {
            for (CSVRecord csvRecord : csvParser) {
                String title = csvRecord.get("Title");
                String artist = csvRecord.get("Artist");
                int year = Integer.parseInt(csvRecord.get("Year"));
                Song song = new Song(title, artist, year);
                songs.add(song);
            }
        }
        return songs;
    }
}
