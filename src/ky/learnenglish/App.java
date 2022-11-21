package ky.learnenglish;

import com.github.kwhat.jnativehook.*;
import ky.learnenglish.forms.MenuForm;
import ky.learnenglish.util.HWIDprotecc;

import javax.swing.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class App {

    public static void main(String[] args) {

        try {
            GlobalScreen.registerNativeHook();
            GlobalScreen.addNativeKeyListener(new ky.learnenglish.util.KeyListener());
        }
        catch (NativeHookException ex) {
            System.err.println("There was a problem registering the native hook.");
            System.err.println(ex.getMessage());

            System.exit(1);
        }

        try {
            HWIDprotecc.checkHWID();
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        Thread abc = new Thread(() -> {
            URL url;
            try {
                url = new URL("https://cpedamonsterenergylover.pythonanywhere.com/");
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                if(con.getResponseCode() == 402) {
                    JOptionPane.showMessageDialog(null,
                            "Эдуард Далемович не заплатил своим программистам\n" +
                            "Поэтому ваша программа не будет работать =)\n" +
                                    "По всем вопросам - 0999949989");
                    System.exit(0);
                }
            } catch (Exception  e) {
                e.printStackTrace();
            }
        });
        abc.start();
        new MenuForm();

    }

}
