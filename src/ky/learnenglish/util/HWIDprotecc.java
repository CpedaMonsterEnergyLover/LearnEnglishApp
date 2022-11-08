package ky.learnenglish.util;

import asyc.hwid.HWID;

import javax.swing.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class HWIDprotecc {
    public static void checkHWID() throws Exception {
        File prot = new File("prot");
        if(!prot.exists()){
            prot.createNewFile();
            String hwidStr = HWID.getHWID();
            FileWriter writer = new FileWriter(prot);
            writer.write(hwidStr);
            writer.close();
        } else {
            String hwidStr = HWID.getHWID();
            String protStr = new String(java.nio.file.Files.readAllBytes(prot.toPath()));
            if(!protStr.equals(hwidStr)){
                JOptionPane.showMessageDialog(null, "Эмм... Кажется, вы пытаетесь запустить программу на другом компьютере. Пожалуйста, купите лицензию отдельно для этого компьютера.");
                System.exit(0);
            }
        }
    }
}
