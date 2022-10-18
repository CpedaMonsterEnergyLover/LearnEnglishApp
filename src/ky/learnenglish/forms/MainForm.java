package ky.learnenglish.forms;

import ky.learnenglish.util.ContentLoader;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.net.URL;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class MainForm extends BaseForm {
    private JPanel mainPanel;
    private JLabel engLabel;
    private JLabel kyrLabel;
    private JLabel ruLabel;
    private JLabel wordNumberLabel;
    private JLabel leftPromoLabel;
    private JLabel rightPromoLabel;
    private List<String> lines;

    private int start;
    private int amount; //пц пшц
    private volatile boolean paused = false;
    private volatile Thread currentThread = null;

    public MainForm(int start, int amount){
        this.start = start;
        this.amount = amount;
        lines = new ContentLoader().GetFile("vocabulary.txt");
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setSize(new Dimension(screenSize.width, screenSize.height));
        setLocation(0, 0);

        setContentPane(mainPanel);
        StartKeyListeners();

        setVisible(true);
        StartMotivatorsTread();
//        StartLessonTread();
//        PrepareLesson();
//        StartRepeatThread();
    }

    private void PrepareMotivators(){
        engLabel.setVisible(false);
        ruLabel.setVisible(false);
        wordNumberLabel.setVisible(false);
    }

    private void PrepareLesson(){
        engLabel.setVisible(true);
        ruLabel.setVisible(true);
        wordNumberLabel.setVisible(true);
    }

    private void PrepareRepeat(){
        wordNumberLabel.setText("" + start);
    }

    private void StartKeyListeners(){
        JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(mainPanel);

        topFrame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int keyCode = e.getKeyCode();
                System.out.println(keyCode);
                if (keyCode == KeyEvent.VK_SPACE) {
                    paused = !paused;
                    System.out.println(paused);
                    currentThread.stop();
                } else if (keyCode == KeyEvent.VK_ESCAPE) {
                    dispose();
                    new MenuForm();
                }
            }
        });
    }

    private void StartRepeatThread(){
        PrepareRepeat();
        currentThread = new Thread(() -> {
            int end = start + amount - 1;
            AtomicInteger counter = new AtomicInteger(start);
            lines.subList(start - 1, Math.min(end, 2500)).forEach(s -> {
                String[] words = s.split(" : ");
                engLabel.setText(words[0]);
                kyrLabel.setText(words[1]);
                ruLabel.setText(words[2]);
                wordNumberLabel.setText("" + counter);
                counter.getAndIncrement();

                try {
                    String path = "voice/" + words[0] + ".wav";
                    ClassLoader classLoader = getClass().getClassLoader();
                    URL defaultSound = classLoader.getResource(path);

                    int i = 0;
                    while (i < 3){
                        if(paused) continue;
                        AudioInputStream ais = AudioSystem.getAudioInputStream(defaultSound);
                        Clip clip = AudioSystem.getClip();
                        clip.open(ais);
                        clip.start();
                        Thread.sleep((int) (clip.getMicrosecondLength() * 0.001f) + 1000);
                        i++;
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
        });
        currentThread.start();
    }

    private void StartLessonTread() {
        PrepareLesson();
        currentThread = new Thread(() -> {
                int counter = 0;
                while(counter < 2500){
                    if(paused) continue;

                    String[] words = lines.get(counter).split(" : ");
                    engLabel.setText(words[0]);
                    kyrLabel.setText(words[1]);
                    ruLabel.setText(words[2]);
                    wordNumberLabel.setText("" + counter);
                    counter++;

                    try {
                        java.lang.Thread.sleep(100);
                    }
                    catch(Exception ignored) { }
                }
            StartRepeatThread();
            });
        currentThread.start();
    }

    private void StartMotivatorsTread() {
        PrepareMotivators();
        final List<String> lines = new ContentLoader().GetFile("motivators.txt");
        currentThread = new Thread(() -> {
            int counter = 120;
            while (counter > 0){
                if(paused) continue;

                lines.forEach(s -> {
                    kyrLabel.setText(s);
                    try {
                        java.lang.Thread.sleep(100);
                    }
                    catch(Exception ignored) { }
                });
                counter--;
            }

            kyrLabel.setText("Приготовьтесь к уроку");
            try {
                java.lang.Thread.sleep(5000);
            }
            catch(Exception ignored) { }
            StartLessonTread();
        });
        currentThread.start();
    }
}
