package ky.learnenglish.forms;

import javax.swing.*;
import java.awt.*;

public abstract class BaseForm extends JFrame {

    private static final String WINDOW_TITLE = "Unique abilities - Англис жана Орус тилдерин 2 айда үйрөнүү";
/*    private static Image WINDOW_ICON;

    static {
        try {
            WINDOW_ICON = ImageIO.read(BaseForm.class.getClassLoader().getResource("icon.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    public BaseForm (){
        setTitle(WINDOW_TITLE);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

//        setIconImage(WINDOW_ICON);
    }

    protected void SetSizeAndCenter(int width, int height){
        setSize(new Dimension(width, height));
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((screenSize.width - width) / 2, (screenSize.height - height) / 2);
    }
}
