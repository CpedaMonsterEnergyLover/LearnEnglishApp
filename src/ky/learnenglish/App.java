package ky.learnenglish;
import asyc.hwid.HWID;
import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import ky.learnenglish.forms.MenuForm;
import ky.learnenglish.util.HWIDprotecc;

import javax.swing.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class App {

    public static void main(String[] args) {

        try {
            GlobalScreen.registerNativeHook();
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
            URL url = null;
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
