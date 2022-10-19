package ky.learnenglish.forms;

import ky.learnenglish.util.ContentLoader;
import ky.learnenglish.util.Mathf;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.URL;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class MainForm extends BaseForm {
    private final JPanelWithBg newPanel = new JPanelWithBg();
    private JPanel mainPanel;
    private JLabel engLabel;
    private JLabel kyrLabel;
    private JLabel ruLabel;
    private JLabel wordNumberLabel;
    private JLabel leftPromoLabel;
    private JLabel rightPromoLabel;
    private JPanel labelPanel;
    private JPanel promoPanel;
    private JSlider progressSlider;
    private final List<String> lines;

    private final int start;
    private final int amount; //пц пшц
    private volatile boolean paused = false;
    private volatile Thread currentThread = null;


    private void removeBackgrounds(){
        mainPanel.setBackground(new Color(Color.TRANSLUCENT));
        mainPanel.setOpaque(false);
        mainPanel.repaint();
        engLabel.setBackground(new Color(Color.TRANSLUCENT));
        engLabel.setOpaque(false);
        engLabel.repaint();
        kyrLabel.setBackground(new Color(Color.TRANSLUCENT));
        kyrLabel.setOpaque(false);
        kyrLabel.repaint();
        ruLabel.setBackground(new Color(Color.TRANSLUCENT));
        ruLabel.setOpaque(false);
        ruLabel.repaint();
        wordNumberLabel.setBackground(new Color(Color.TRANSLUCENT));
        wordNumberLabel.setOpaque(false);
        wordNumberLabel.repaint();
        labelPanel.setBackground(new Color(Color.TRANSLUCENT));
        labelPanel.setOpaque(false);
        labelPanel.repaint();
        //promoPanel.setBackground(new Color(Color.TRANSLUCENT));
        //promoPanel.setOpaque(false);
        //promoPanel.repaint();
    }

    public MainForm(int start, int amount){
        removeBackgrounds();
        newPanel.add(mainPanel);
        this.start = start;
        this.amount = amount;
        lines = new ContentLoader().GetFile("vocabulary.txt");
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setSize(new Dimension(screenSize.width, screenSize.height));
        setLocation(0, 0);

        setContentPane(newPanel);
        PrepareKeyListeners();
        setVisible(true);

        PrepareSlider();
        StartMotivatorsTread(0);
//        StartLessonTread();
//        PrepareLesson();
//        StartRepeatThread();
    }

    // Components initiation

    private void PrepareSlider(){
        progressSlider.setValue(0);
        Hashtable<Integer, JLabel> lableTable = new Hashtable<>();
        lableTable.put(0, new JLabel("Начало"));
        lableTable.put(100, new JLabel("Словарь"));
        lableTable.put(500, new JLabel("Озвучка"));
        lableTable.put(1000, new JLabel("Конец"));
        progressSlider.setLabelTable(lableTable);
        progressSlider.setPaintLabels(true);

        progressSlider.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                paused = true;
                if(currentThread.isAlive()){
                    currentThread.interrupt();
                }

            }

            @Override
            public void mouseReleased(MouseEvent e) {
                paused = false;
                int newValue = progressSlider.getValue();
                if (newValue < 100) {
                    int calc = Mathf.Lerp(newValue / 100f, 0, 120);
                    StartMotivatorsTread(calc);
                } else if (newValue < 500) {
                    int calc = Mathf.Lerp((newValue - 100) / 400f, 0, 2499);
                    StartLessonTread(calc);
                } else {
                    int calc = Mathf.Lerp((newValue - 500) / 500f, 0, 300);
                    StartRepeatThread(calc);
                }
            }
        });

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

    private void PrepareKeyListeners(){
        JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(mainPanel);

        topFrame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                int keyCode = e.getKeyCode();
                System.out.println(keyCode);
                if (keyCode == KeyEvent.VK_SPACE) {
                    if(paused) paused = false;
                    else paused = true;
                } else if (keyCode == KeyEvent.VK_ESCAPE) {
                    dispose();
                    new MenuForm();
                }
            }

        });
    }

    // Learning threads

    private void StartMotivatorsTread(int startpos) {
        PrepareMotivators();
        AtomicBoolean stopped = new AtomicBoolean(false);
        final List<String> lines = new ContentLoader().GetFile("motivators.txt");
        currentThread = new Thread(() -> {
            int counter = startpos;
            while (counter < 120){
                if(paused) continue;

                lines.forEach(s -> {
                    kyrLabel.setText(s);
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        stopped.set(true);
                    }
                });
                UpdateProgress(counter / 120f, 0, 100);
                counter++;
            }
            if(stopped.get()) return;
            try {
                kyrLabel.setText("Приготовьтесь к уроку");
                Thread.sleep(5000);
                StartLessonTread(0);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                stopped.set(true);
            }
        });
        currentThread.start();
    }

    private void StartLessonTread(int word) {
        PrepareLesson();
        AtomicBoolean stopped = new AtomicBoolean(false);
        currentThread = new Thread(() -> {
                int counter = word;
                while(counter < 2500){
                    if(paused) continue;

                    String[] words = lines.get(counter).split(" : ");
                    engLabel.setText(words[0]);
                    kyrLabel.setText(words[1]);
                    ruLabel.setText(words[2]);
                    wordNumberLabel.setText("" + counter);
                    counter++;

                    try {
                        Thread.sleep(100);
                        UpdateProgress(counter / 2500f, 100, 500);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        stopped.set(true);
                    }
                }
            if(stopped.get()) return;
            StartRepeatThread(0);
        });
        currentThread.start();
    }

    private void StartRepeatThread(int word){
        currentThread = new Thread(() -> {
            int end = start + amount - 1;
            int startWith = start + word;
            AtomicInteger counter = new AtomicInteger(startWith);
            wordNumberLabel.setText("" + startWith);

            lines.subList(startWith - 1, Math.min(end, 2500)).forEach(s -> {
                String[] words = s.split(" : ");
                engLabel.setText(words[0]);
                kyrLabel.setText(words[1]);
                ruLabel.setText(words[2]);
                wordNumberLabel.setText("" + counter);
                counter.getAndIncrement();

                String path = "voice/" + words[0] + ".wav";
                ClassLoader classLoader = getClass().getClassLoader();
                URL defaultSound = classLoader.getResource(path);

                int i = 0;
                while (i < 3){
                    if(paused) continue;
                    try {
                        AudioInputStream ais = AudioSystem.getAudioInputStream(defaultSound);
                        Clip clip = AudioSystem.getClip();
                        clip.open(ais);
                        clip.start();
                        try {
                            Thread.sleep((int) (clip.getMicrosecondLength() * 0.001f) + 1000);
                            i++;
                        } catch (InterruptedException e) {
                            clip.stop();
                            Thread.currentThread().interrupt();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
                UpdateProgress(counter.get() / (float) end, 500, 1000);
            });
        });
        currentThread.start();
    }

    // Progress

    private void UpdateProgress(float current, int stagemin, int stagemax) {
        int value = Mathf.Lerp(current, stagemin, stagemax);
        progressSlider.setValue(stagemin + value);
    }

}