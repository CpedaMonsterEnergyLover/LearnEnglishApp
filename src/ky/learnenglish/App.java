package ky.learnenglish;
import ky.learnenglish.forms.MenuForm;
import javax.swing.*;

public class App {

    public static void main(String[] args) {

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        new MenuForm();

    }

}
