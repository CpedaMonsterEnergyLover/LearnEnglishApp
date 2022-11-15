package ky.learnenglish.forms;

import javax.swing.*;
import java.awt.*;

public class JPanelWithBg extends JPanel {
    private Image image;
    private static ClassLoader classLoader;

    private static ImageIcon firstBg;
    private static ImageIcon secondBg;
    private static ImageIcon skyBg;
    private static ImageIcon blueBg;

    enum Background {
        SKY,
        FIRST,
        SECOND,
        BLUE
    }

    public void SetImage(Background image){
        switch (image) {
            case SKY:
                this.image = skyBg.getImage();
                break;
            case FIRST:
                this.image = firstBg.getImage();
                break;
            case SECOND:
                this.image = secondBg.getImage();
                break;
            case BLUE:
                this.image = blueBg.getImage();
                break;
        }

    };

    JPanelWithBg() {
        classLoader = this.getClass().getClassLoader();

        java.net.URL imageURL3 = classLoader.getResource("royal_blue.jpg");
        blueBg = new ImageIcon(imageURL3);
        java.net.URL imageURL = classLoader.getResource("spiral_1.gif");
        firstBg = new ImageIcon(imageURL);
        java.net.URL imageURL1 = classLoader.getResource("spiral_2.gif");
        secondBg = new ImageIcon(imageURL1);
        java.net.URL imageURL2 = classLoader.getResource("clouds.jpg");
        skyBg = new ImageIcon(imageURL2);
        GridLayout experimentLayout = new GridLayout(1,1);
        setLayout(experimentLayout);
        SetImage(Background.BLUE);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(image,0,0,getWidth(),getHeight(),this);
    }

}
