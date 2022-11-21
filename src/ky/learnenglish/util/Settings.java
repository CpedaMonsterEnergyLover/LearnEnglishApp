package ky.learnenglish.util;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class Settings {
    public static Settings Instance;
    public static float UISize;
    public static int monitor;

    public static final int DEFAULT_ENG_SIZE = 94;
    public static final int DEFAULT_KYR_SIZE = 94;
    public static final int DEFAULT_RU_SIZE = 70;
    public static final int DEFAULT_CREDITS_SIZE = 69;
    public static final int MIN_SIZE = 10;

    private static final String configFileName = "settings.txt";

    public Settings() {
        if(Instance != null) return;
        Instance = this;
        File configFile = new File(configFileName);
        if (!configFile.exists()) {
            Save(0f, 0);
        }
        Load();
    }

    public void Load(){
        Path filePath = Paths.get(configFileName);
        Charset charset = StandardCharsets.UTF_8;
        try {
            List<String> content = Files.readAllLines(filePath, charset);
            UISize = Float.parseFloat(content.get(0).split(":")[0]);
            monitor = Integer.parseInt(content.get(0).split(":")[1]);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void Save(float newSize, int newMonitor){
        try {
            FileWriter myWriter = new FileWriter(configFileName);
            myWriter.write(String.valueOf(newSize));
            myWriter.write(":"+ newMonitor);
            myWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
