package ky.learnenglish;
import asyc.hwid.HWID;
import asyc.hwid.exception.UnsupportedOSException;
import ky.learnenglish.forms.MenuForm;
import ky.learnenglish.util.HWIDprotecc;

import javax.swing.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class App {

    public static void main(String[] args) {

        try {
            HWIDprotecc.checkHWID();
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        new MenuForm();

    }

}
