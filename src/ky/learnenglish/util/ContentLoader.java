package ky.learnenglish.util;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

public class ContentLoader {

    public List<String> GetFile(String filename){
        InputStream inputStream = GetFileFromResourceAsStream(filename);

        List<String> motivators = new ArrayList<>();

        try (InputStreamReader streamReader =
                new InputStreamReader(inputStream, StandardCharsets.UTF_8);
             BufferedReader reader = new BufferedReader(streamReader)) {

            String line;
            while ((line = reader.readLine()) != null) {
                motivators.add(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return motivators;
    }

    private InputStream GetFileFromResourceAsStream(String fileName) {
        ClassLoader classLoader = getClass().getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(fileName);
        if (inputStream == null) {
            throw new IllegalArgumentException("file not found! " + fileName);
        } else {
            return inputStream;
        }
    }

}
