package ky.learnenglish.forms;

import javax.swing.*;
import java.awt.*;

public class JPanelWithBg extends JPanel {
    private Image image;
    private static ClassLoader classLoader;

    public void SetImage(String name){
        java.net.URL imageURL = classLoader.getResource(name);
        ImageIcon imageIcon = new ImageIcon(imageURL);
        this.image = imageIcon.getImage();
    };

    JPanelWithBg() {
        classLoader = this.getClass().getClassLoader();
        GridLayout experimentLayout = new GridLayout(1,1);
        setLayout(experimentLayout);
        SetImage("royal_blue.jpg");
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(image,0,0,getWidth(),getHeight(),this);
    }

}
