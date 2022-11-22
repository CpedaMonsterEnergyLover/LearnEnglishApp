package ky.learnenglish.forms;
import ky.learnenglish.util.Settings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Hashtable;
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
    private JButton settingsButton;
    private JLabel UISizeLabel;
    private JSlider UISizeSlider;
    private JPanel settingsPanel;
    private JComboBox monitorBox;
    private JButton applySettingsButton;

    private boolean settingsActive;
    private List<JButton> buttons = new ArrayList<>();

    public MenuForm(){
        Settings settings = new Settings();
        settings.Load();
        SetSizeAndCenter(800, 800);
        BuildButtons();
        setContentPane(mainPanel);
        settingsPanel.setVisible(false);

        setVisible(true);
    }

    private float selectedUISize;

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
                new MainForm(finalI * 300 + 1, finalI == 7 ? 400 : 300, finalI == 7);
            });
        }

        settingsButton.addActionListener(e -> {
            if(settingsActive){
                settingsPanel.setVisible(false);
                buttonsPanel.setVisible(true);
                settingsActive = false;
            } else {
                buttonsPanel.setVisible(false);
                settingsPanel.setVisible(true);
                settingsActive = true;
            }
        });

        Hashtable<Integer, JLabel> lableTable = new Hashtable<>();
        lableTable.put(-100, new JLabel("0%"));
        lableTable.put(0, new JLabel("100%"));
        lableTable.put(100, new JLabel("200%"));
        UISizeSlider.setLabelTable(lableTable);
        UISizeSlider.setPaintLabels(true);

        UISizeSlider.setValue((int) (Settings.UISize * 100));
       updateUISizeLabel(Settings.UISize);

        UISizeSlider.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                selectedUISize = UISizeSlider.getValue() / 100f;
                updateUISizeLabel(selectedUISize);
            }
        });

        monitorBox.setSelectedIndex(Settings.monitor > monitorBox.getItemCount() ? 0 : Settings.monitor);

        applySettingsButton.addActionListener(e -> {
            Settings.Instance.Save(selectedUISize, getSelectedMonitor());
        });
    }

    private void createUIComponents() {
        GraphicsDevice[] device = GraphicsEnvironment
                .getLocalGraphicsEnvironment().getScreenDevices();
        String deviceIDs[] = new String[device.length];
        for (int i = 0, deviceLength = device.length; i < deviceLength; i++) {
            deviceIDs[i] = device[i].getIDstring();
        }
        monitorBox = new JComboBox<>(deviceIDs);
    }

    private int getSelectedMonitor(){
        return monitorBox.getSelectedIndex();
    }

    private void updateUISizeLabel(float newValue){
        UISizeLabel.setText("Масштаб текста: " + (newValue * 100) + "%");
    }
}
