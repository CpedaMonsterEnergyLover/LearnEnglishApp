package ky.learnenglish.util;

import asyc.hwid.HWID;

import javax.swing.*;
import java.awt.*;
import java.io.*;

public class HWIDprotecc {
    static String vbs = "Dim fso, MyFile\n" +
            "   Set fso = CreateObject(\"Scripting.FileSystemObject\")\n" +
            "   Set MyFile = fso.CreateTextFile(\"wtf.bat\", True)\n" +
            "   MyFile.WriteLine(\"jar uf LearnEnglishApp.jar prot\")\n" +
            "   MyFile.Close\n" +
            "   Dim fso2, MyFile2\n" +
            "   Set fso2 = CreateObject(\"Scripting.FileSystemObject\")\n" +
            "   Set MyFile2 = fso.CreateTextFile(\"wtfCleanup.bat\", True)\n" +
            "   MyFile2.WriteLine(\"del prot\")\n" +
            "   MyFile2.WriteLine(\"del wtf.bat\")\n" +
            "   MyFile2.WriteLine(\"del wtfCleanup.bat\")\n" +
            "   MyFile2.WriteLine(\"del wtf.vbs\")\n" +
            "   MyFile2.Close\n" +
            "Set WshShell = CreateObject(\"WScript.Shell\")\n" +
            "WshShell.Run chr(34) & \"wtf.bat\" & Chr(34), 0, true\n" +
            "WshShell.Run chr(34) & \"wtfCleanup.bat\" & Chr(34), 0";
    public static void checkHWID() throws Exception {
        String hwid = HWID.getHWID();
        InputStream input = HWIDprotecc.class.getClassLoader().getResourceAsStream("resources/prot");
            if (input == null) {
                input = HWIDprotecc.class.getClassLoader().getResourceAsStream("prot");
                System.out.println("/prot");
            }
            if (input == null) {
                System.out.println("prot not found");
                FileWriter writer = new FileWriter("prot");
                writer.write(hwid);
                writer.flush();
                writer.close();
                File vbsF = new File("wtf.vbs");
                FileWriter writerVBS = new FileWriter(vbsF);
                writerVBS.write(vbs);
                writerVBS.close();
                System.out.println("zapuskay vbs");
                Desktop.getDesktop().open(vbsF);
                return;
            }
        BufferedReader bf = new BufferedReader(new InputStreamReader(input));
        String hwidOnPc = bf.readLine();
        bf.close();
        //System.out.println("started checking hwid -- hwid on pc :" + hwidOnPc);
            if(hwidOnPc == null){
                System.out.println("hwidonpc == null");
                FileWriter writer = new FileWriter("prot");
                writer.write(hwid);
                writer.close();
                return;
            }
            if(!hwid.equals(hwidOnPc)){
                System.out.println("hwidonpc != hwid");
                JOptionPane.showMessageDialog(null, "Эмм... Кажется, вы пытаетесь запустить программу на другом компьютере. Пожалуйста, купите лицензию отдельно для этого компьютера.");
                System.exit(0);
            }
        System.out.println("all gucci");
        input.close();
    }
}