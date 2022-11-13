package ky.learnenglish.forms;

import javafx.scene.paint.Stop;
import ky.learnenglish.util.ContentLoader;
import ky.learnenglish.util.Mathf;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.URL;
import java.util.Hashtable;
import java.util.List;
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
    private JTextArea credits;
    private final List<String> lines;

    private final int start;
    private final int amount;
    private volatile boolean paused = false;
    private volatile Thread currentThread = null;


    private static final Color darkTextColor = new Color(60, 63, 65);
    private static final Color lightTextColor = new Color(187, 187, 187);

//    private Clip clip;
    private final ClassLoader classLoader;
//    private AudioInputStream ais;

    private boolean lessonStarted;
    private final boolean finalWeek;
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

    public MainForm(int start, int amount, boolean finalWeek){
        removeBackgrounds();
        newPanel.add(mainPanel);
        this.start = start;
        this.finalWeek = finalWeek;
        this.amount = amount;
        lines = new ContentLoader().GetFile("vocabulary.txt");
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setSize(new Dimension(screenSize.width, screenSize.height));
        setLocation(0, 0);
        classLoader = getClass().getClassLoader();
        promoPanel.setVisible(false);
        setContentPane(newPanel);
        PrepareKeyListeners();
        setVisible(true);

        PrepareSlider();
        StartIntroThread();
    }


    private void PrepareSlider(){
        progressSlider.setVisible(false);
        progressSlider.setValue(0);
        Hashtable<Integer, JLabel> lableTable = new Hashtable<>();
        lableTable.put(0, new JLabel("Начало"));
        lableTable.put(100, new JLabel("Словарь"));
        lableTable.put(300, new JLabel("Озвучка"));
        if(!finalWeek) lableTable.put(900, new JLabel("Словарь"));
        lableTable.put(1000, new JLabel("Конец"));
        progressSlider.setLabelTable(lableTable);
        progressSlider.setPaintLabels(true);

        progressSlider.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if(currentThread.isAlive()){
                    currentThread.interrupt();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                SetPause(false);
                int newValue = progressSlider.getValue();
                if (newValue < 100) {
                    int calc = Mathf.Lerp(newValue / 100f, 0, 120);
                    StartMotivatorsThread(calc);
                } else if (newValue < 200){
                    int calc = Mathf.Lerp((newValue - 100) / 100f, 0, 2499);
                    StartLessonTread(calc, 1);
                } else if(newValue < 300){
                    int calc = Mathf.Lerp((newValue - 200) / 100f, 0, 2499);
                    StartLessonTread(calc, 2);
                } else if ((newValue < 900 && !finalWeek) || (finalWeek && newValue < 1000)  ) {
                    int calc = Mathf.Lerp((newValue - 300) / (finalWeek ? 700f : 600f), 0, amount);
                    StartRepeatThread(calc);
                } else {
                    int calc = Mathf.Lerp((newValue - 900) / 100f, 0, 2499);
                    StartLessonTread(calc, 3);
                }
            }
        });

    }

    private void PrepareMotivators(){
        StartMusic();
        newPanel.SetImage("spiral_2.gif");
        engLabel.setVisible(false);
        ruLabel.setVisible(false);
        wordNumberLabel.setVisible(false);
        ruLabel.setForeground(darkTextColor);
        kyrLabel.setForeground(darkTextColor);
        engLabel.setForeground(darkTextColor);
        kyrLabel.setIcon(null);
        credits.setVisible(false);
    }

    private void EndLesson(){
        StopMusic();
        wordNumberLabel.setVisible(false);
        engLabel.setVisible(false);
        ruLabel.setVisible(false);
        kyrLabel.setText("Сабак бүттү");
    }

    private void PrepareLesson(int loop){
        StartMusic();
        newPanel.SetImage(loop == 2 ? "spiral_2.gif" : "spiral_1.gif");
        engLabel.setVisible(true);
        ruLabel.setVisible(true);
        kyrLabel.setVisible(true);
        wordNumberLabel.setVisible(true);
    }

    private void PrepareRepeat(){
        StopMusic();
        newPanel.SetImage("clouds.jpg");
        ruLabel.setForeground(darkTextColor);
        kyrLabel.setForeground(darkTextColor);
        engLabel.setForeground(darkTextColor);
        engLabel.setVisible(true);
        ruLabel.setVisible(true);
        kyrLabel.setVisible(true);
        wordNumberLabel.setVisible(true);
    }

    private void PrepareIntro(){
        engLabel.setVisible(false);
        wordNumberLabel.setVisible(false);

        ruLabel.setVisible(true);
        ruLabel.setText("Англис жана Орус тилдерин 2 айда үйрөнүү");
        ruLabel.setForeground(Color.orange);

        kyrLabel.setText("");
        kyrLabel.setVisible(true);

        java.net.URL imageURL = classLoader.getResource("logo.png");
        ImageIcon imageIcon = new ImageIcon(imageURL);
        kyrLabel.setIcon(imageIcon);
    }

    private void SetPause(boolean isPaused){
        if(!lessonStarted) return;
        System.out.println("Pause");
        paused = isPaused;
        progressSlider.setVisible(isPaused);
        if(isPaused) PauseMusic();
        else StartMusic();
    }

    private void PrepareKeyListeners(){
        JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(newPanel);
        System.out.println("Prepare listeners");
        topFrame.setFocusable(true);
        topFrame.requestFocus();
        topFrame.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                int keyCode = e.getKeyCode();
                System.out.println(keyCode);
                if (keyCode == KeyEvent.VK_SPACE) {
                    SetPause(!paused);
                } else if (keyCode == KeyEvent.VK_ESCAPE && lessonStarted) {
                    dispose();
                    if(currentThread != null && currentThread.isAlive()) {
                        currentThread.interrupt();
                        StopMusic();
                    }
                    new MenuForm();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });
    }

    private void StartIntroThread(){
        PrepareIntro();
        currentThread = new Thread(() -> {
            try {
                Thread.sleep(12000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            lessonStarted = true;
            System.out.println("Lesson started");
            StartMotivatorsThread(0);
        });
        currentThread.start();
    }

    private void StartMotivatorsThread(int startpos) {
        PrepareMotivators();
        final List<String> lines = new ContentLoader().GetFile("motivators.txt");
        currentThread = new Thread(() -> {
            int counter = startpos;
            while (counter < 120 && !Thread.currentThread().isInterrupted()){
                if(paused) continue;
                int finalCounter = counter;
                lines.forEach(s -> {
                    if(Thread.currentThread().isInterrupted()) return;
                    kyrLabel.setText(s);
                    try {
                        UpdateProgress(finalCounter / 120f, 0, 100);
                        Thread.sleep(96);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                });
                counter++;
            }
            if(Thread.currentThread().isInterrupted()) return;
            try {
                kyrLabel.setText("Приготовьтесь к уроку");
                Thread.sleep(60000);
                StartLessonTread(0, 1);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        currentThread.start();
    }

    private void StartLessonTread(int word, int loop) {
        int stagemin = loop == 3 ? 900 : 100 * loop;
        int stagemax = loop == 3 ? 1000 : loop == 2 ? 300 : 200;
        PrepareLesson(loop);
        currentThread = new Thread(() -> {
                int counter = word;
                while(counter < 2500 && !Thread.currentThread().isInterrupted()){
                    if(paused) continue;
                    String[] words = lines.get(counter).split(" : ");
                    engLabel.setText(words[0]);
                    kyrLabel.setText(words[1]);
                    ruLabel.setText(words[2]);
                    wordNumberLabel.setText("" + counter);
                    counter++;

                    try {
                        Thread.sleep(100);
                        UpdateProgress(counter / 2500f, stagemin, stagemax);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
            if(Thread.currentThread().isInterrupted()) return;
            if(loop == 2){
                StartRepeatThread(0);
            } else if (loop == 1){
                StartLessonTread(0, 2);
            } else if (loop == 3){
                EndLesson();
            }
        });
        currentThread.start();
    }

    private void StartRepeatThread(int word){
        PrepareRepeat();
        int end = start + amount - 1;
        int startWith = start + word;
        AtomicInteger counter = new AtomicInteger(startWith);
        wordNumberLabel.setText("" + startWith);
        UpdateProgress(word / (float) amount, 300, finalWeek ? 1000 : 900);

        currentThread = new Thread(() -> {
            lines.subList(startWith - 1, Math.min(end, 2500)).forEach(s -> {
                if(Thread.currentThread().isInterrupted()) return;
                UpdateProgress(((counter.get()) - start) / (float) (end - start), 300, finalWeek ? 1000 : 900);
                String[] words = s.split(" : ");
                engLabel.setText(words[0]);
                kyrLabel.setText(words[1]);
                ruLabel.setText(words[2]);
                wordNumberLabel.setText("" + counter);
                counter.getAndIncrement();

                String filename = words[0].replace("?", "").replace(".", "");
                String path = "voice/" + filename + ".wav";
                Clip clip = null;
                AudioInputStream ais = null;

                try {
                    URL defaultSound = classLoader.getResource(path);
                    clip = AudioSystem.getClip();
                    ais = AudioSystem.getAudioInputStream(defaultSound);
                    clip.open(ais);
                } catch (LineUnavailableException | UnsupportedAudioFileException | IOException e) {
                    e.printStackTrace();
                }

                int i = 0;
                int repeatTime = finalWeek ? 2 : 3;
                while (i < repeatTime && !Thread.currentThread().isInterrupted()){
                    if(paused) continue;
                    try {
                        clip.setFramePosition(0);
                        clip.start();
                        try {
                            Thread.sleep((int) (clip.getMicrosecondLength() * 0.001f) + 500);
                            i++;
                        } catch (InterruptedException e) {
                            clip.close();
                            clip.stop();
                            clip.flush();
                            ais.close();
                            Thread.currentThread().interrupt();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            if(Thread.currentThread().isInterrupted()) return;
            if(finalWeek) {
                EndLesson();
            } else {
                StartLessonTread(0, 3);
            }
        });
        currentThread.start();
    }

    private boolean IsPlaying() {
        return currentMusicClip != null;
    }
    private Clip currentMusicClip = null;
    private long pausedClipTime = 0;

    private void StartMusic(){
        try {
            if(pausedClipTime > 0) {
                currentMusicClip.start();
            } else if (!IsPlaying()) {
                currentMusicClip = AudioSystem.getClip();
                URL path = classLoader.getResource("music.wav");
                AudioInputStream ais = AudioSystem.getAudioInputStream(path);
                currentMusicClip.open(ais);
                currentMusicClip.setFramePosition(0);
                currentMusicClip.loop(Clip.LOOP_CONTINUOUSLY);
                currentMusicClip.start();
            }
        } catch (LineUnavailableException | UnsupportedAudioFileException | IOException e) {
            e.printStackTrace();
        }
    }

    private void StopMusic(){
        if(!IsPlaying()) return;
        currentMusicClip.close();
        currentMusicClip.stop();
        currentMusicClip.flush();
        pausedClipTime = 0;
        currentMusicClip = null;
    }

    private void PauseMusic(){
        if(!IsPlaying()) return;
        pausedClipTime = currentMusicClip.getMicrosecondPosition();
        currentMusicClip.stop();
    }

    private void UpdateProgress(float current, int stagemin, int stagemax) {
        int value = Mathf.Lerp(current, stagemin, stagemax);
        progressSlider.setValue(stagemin + value);
    }

}