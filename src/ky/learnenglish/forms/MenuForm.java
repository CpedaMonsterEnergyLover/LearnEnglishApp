package ky.learnenglish.forms;

import ky.learnenglish.util.VideoPlayer;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MenuForm extends BaseForm
{

    private JPanel mainPanel;
    private JPanel buttonsPanel;
    private JButton week1Button;
    private JButton week2Button;
    private JButton week3Button;
    private JButton week4Button;
    private JButton week5Button;
    private JButton week6Button;
    private JButton week7Button;
    private JButton week8Button;

    private List<JButton> buttons = new ArrayList<>();

    public MenuForm(){
        SetSizeAndCenter(800, 800);
        BuildButtons();
        setContentPane(mainPanel);
        setVisible(true);
    }

    private void BuildButtons(){
        buttons.add(week1Button);
        buttons.add(week2Button);
        buttons.add(week3Button);
        buttons.add(week4Button);
        buttons.add(week5Button);
        buttons.add(week6Button);
        buttons.add(week7Button);
        buttons.add(week8Button);

        for(int i = 0; i < 8; i++){
            int finalI = i;
            buttons.get(i).addActionListener(e -> {
                dispose();
                new MainForm(finalI * 300 + 1, 300);
            });
        }
    }

}
