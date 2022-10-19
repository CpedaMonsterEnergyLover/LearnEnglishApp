package ky.learnenglish.forms;

import javax.swing.*;
import java.awt.*;

public class JPanelWithBg extends JPanel {
    private Image image;

    JPanelWithBg() {
        ClassLoader cldr = this.getClass().getClassLoader();
        java.net.URL imageURL   = cldr.getResource("bg.gif");
        ImageIcon imageIcon = new ImageIcon(imageURL);
        this.image = imageIcon.getImage();
        GridLayout experimentLayout = new GridLayout(1,1);
        setLayout(experimentLayout);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(image,0,0,getWidth(),getHeight(),this);
    }

}
