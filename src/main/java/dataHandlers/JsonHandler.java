package dataHandlers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.song.Song;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class JsonHandler {
    private final ObjectMapper objectMapper;

    public JsonHandler() {
        this.objectMapper = new ObjectMapper();
    }

    public void writeToJson(List<Song> songs, String filename) throws IOException {
        objectMapper.writeValue(new File(filename), songs);
    }

    public List<Song> readFromJson(String filename) throws IOException {
        return objectMapper.readValue(new File(filename), new TypeReference<List<Song>>() {});
    }
}
