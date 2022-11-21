package ky.learnenglish.forms;

import ky.learnenglish.util.ContentLoader;
import ky.learnenglish.util.Mathf;
import ky.learnenglish.util.Settings;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URL;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class MainForm extends BaseForm {
    public static MainForm Instance;

    private final JPanelWithBg newPanel = new JPanelWithBg();
    private JPanel mainPanel;
    private JLabel engLabel;
    private JLabel kyrLabel;
    private JLabel ruLabel;
    private JLabel wordNumberLabel;
    private JLabel leftPromoLabel;
    private JLabel rightPromoLabel;
    private JLabel creditsLine1;
    private JLabel creditsLine2;
    private JLabel creditsLine3;
    private JLabel creditsLine4;
    private JLabel creditsLine5;
    private JPanel labelPanel;
    private JPanel promoPanel;
    private JSlider progressSlider;
    private JPanel creditsPanel;
    private final List<String> lines;

    private final int start;
    private final int amount;
    public static volatile boolean paused = false;
    private volatile Thread currentThread = null;

    private static final Color darkTextColor = new Color(60, 63, 65);

    private final ClassLoader classLoader;

    private boolean lessonStarted;
    private final boolean finalWeek;

    private boolean hidePromo;

    private void UpdateUISize(){
        float newSize = Settings.UISize;
        engLabel.setFont(engLabel.getFont().deriveFont(Math.max(Settings.DEFAULT_ENG_SIZE + Settings.DEFAULT_ENG_SIZE * newSize, Settings.MIN_SIZE)));
        kyrLabel.setFont(kyrLabel.getFont().deriveFont(Math.max(Settings.DEFAULT_KYR_SIZE + Settings.DEFAULT_KYR_SIZE * newSize, Settings.MIN_SIZE)));
        ruLabel.setFont(ruLabel.getFont().deriveFont(Math.max(Settings.DEFAULT_RU_SIZE + Settings.DEFAULT_RU_SIZE * newSize, Settings.MIN_SIZE)));
        float min = Math.max(Settings.DEFAULT_CREDITS_SIZE + Settings.DEFAULT_CREDITS_SIZE * newSize, Settings.MIN_SIZE);
        creditsLine1.setFont(creditsLine1.getFont().deriveFont(min));
        creditsLine2.setFont(creditsLine2.getFont().deriveFont(min));
        creditsLine3.setFont(creditsLine3.getFont().deriveFont(min));
        creditsLine4.setFont(creditsLine4.getFont().deriveFont(min));
        creditsLine5.setFont(creditsLine5.getFont().deriveFont(min));
    }

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
        creditsPanel.setBackground(new Color(Color.TRANSLUCENT));
        creditsPanel.setOpaque(false);
        creditsPanel.repaint();
        wordNumberLabel.setBackground(new Color(Color.TRANSLUCENT));
        wordNumberLabel.setOpaque(false);
        wordNumberLabel.repaint();
        labelPanel.setBackground(new Color(Color.TRANSLUCENT));
        labelPanel.setOpaque(false);
        labelPanel.repaint();
        promoPanel.setBackground(new Color(Color.TRANSLUCENT));
        promoPanel.setOpaque(false);
        promoPanel.repaint();
        leftPromoLabel.setBackground(new Color(Color.TRANSLUCENT));
        leftPromoLabel.setOpaque(false);
        leftPromoLabel.repaint();
        rightPromoLabel.setBackground(new Color(Color.TRANSLUCENT));
        rightPromoLabel.setOpaque(false);
        rightPromoLabel.repaint();
        progressSlider.setBackground(new Color(Color.TRANSLUCENT));
        progressSlider.setOpaque(false);
        progressSlider.repaint();

    }


    static GraphicsDevice device[] = GraphicsEnvironment
            .getLocalGraphicsEnvironment().getScreenDevices();

    public MainForm(int start, int amount, boolean finalWeek){
        paused = false;
        lessonStarted = false;
        System.out.println("MOnitor " + Settings.monitor);
        setAlwaysOnTop(true);
        device[Settings.monitor].setFullScreenWindow(this);
/*        DisplayMode dm = device[Settings.monitor].getDisplayMode();
        int screenWidth = dm.getWidth();
        int screenHeight = dm.getHeight();
        SetSizeAndCenter(screenWidth, screenHeight);*/
        classLoader = getClass().getClassLoader();
        Instance = this;
        removeBackgrounds();
        PrepareMusicClip();
        newPanel.add(mainPanel);
        this.start = start;
        this.finalWeek = finalWeek;
        this.amount = amount;
        lines = new ContentLoader().GetFile("vocabulary.txt");
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setSize(new Dimension(screenSize.width, screenSize.height));
        setLocation(0, 0);
        setContentPane(newPanel);
        promoPanel.setVisible(false);
        creditsPanel.setVisible(false);
        UpdateUISize();
        setVisible(true);
        PrepareSlider();
        StartIntroThread();
    }

    private void PrepareMusicClip(){
        try {
            currentMusicClip = AudioSystem.getClip();
            URL path = classLoader.getResource("music.wav");
            AudioInputStream ais = AudioSystem.getAudioInputStream(path);
            currentMusicClip.open(ais);
            currentMusicClip.setFramePosition(0);
            currentMusicClip.loop(Clip.LOOP_CONTINUOUSLY);
            currentMusicClip.stop();
        } catch (LineUnavailableException | UnsupportedAudioFileException | IOException e) {
            e.printStackTrace();
        }

    }


    private void PrepareSlider(){
        progressSlider.setMinimum(0);
        progressSlider.setMaximum(1000);
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
        hidePromo = true;
        HidePromo();
        StartMusic();
        newPanel.SetImage(JPanelWithBg.Background.SECOND);
        engLabel.setVisible(false);
        ruLabel.setVisible(false);
        kyrLabel.setVisible(true);
        wordNumberLabel.setVisible(false);
        ruLabel.setForeground(darkTextColor);
        kyrLabel.setForeground(darkTextColor);
        engLabel.setForeground(darkTextColor);
        kyrLabel.setIcon(null);
        creditsPanel.setVisible(false);
    }

    private void EndLesson(){
        StopMusic();
        wordNumberLabel.setVisible(false);
        engLabel.setVisible(false);
        ruLabel.setVisible(false);
        kyrLabel.setText("Урок окончен");
//        kyrLabel.setText("Сабак бүттү");
    }

    private void PrepareLesson(int loop){
        hidePromo = true;
        HidePromo();
        StartMusic();
        newPanel.SetImage(loop == 2 ? JPanelWithBg.Background.SECOND : JPanelWithBg.Background.FIRST);
        engLabel.setVisible(true);
        ruLabel.setVisible(true);
        kyrLabel.setVisible(true);
        wordNumberLabel.setVisible(true);
    }

    private void PrepareRepeat(){
        hidePromo = false;
        ShowPromo();
        StopMusic();
        newPanel.SetImage(JPanelWithBg.Background.SKY);
        ruLabel.setForeground(darkTextColor);
        kyrLabel.setForeground(darkTextColor);
        engLabel.setForeground(darkTextColor);
        engLabel.setVisible(true);
        ruLabel.setVisible(true);
        kyrLabel.setVisible(true);
        wordNumberLabel.setVisible(true);
    }

    private void ShowPromo(){
        promoPanel.setVisible(true);
    }

    private void HidePromo(){
        promoPanel.setVisible(false);
    }

    private void PrepareIntro(){
        engLabel.setVisible(false);
        wordNumberLabel.setVisible(false);
        HidePromo();

        ruLabel.setVisible(true);
        ruLabel.setText("Англис жана Орус тилдерин 2 айда үйрөнүү");
        ruLabel.setForeground(Color.orange);

        kyrLabel.setText("");
        kyrLabel.setVisible(true);

        java.net.URL imageURL = classLoader.getResource("logo.png");
        ImageIcon imageIcon = new ImageIcon(imageURL);
        kyrLabel.setIcon(imageIcon);
    }

    public void SetPause(boolean isPaused){
        if(lessonStarted) {
            if(isPaused){
                ShowPromo();
                paused = true;
                PauseMusic();
                progressSlider.setVisible(true);
                progressSlider.requestFocus();
            } else {
                if(hidePromo) HidePromo();
                progressSlider.setVisible(false);
                StartMusic();
                paused = false;
            }
        } else {
            if(currentThread != null && currentThread.isAlive())
                currentThread.interrupt();
        }
    }

    public void Close(){
        if(!lessonStarted) return;
        dispose();
        if(currentThread != null && currentThread.isAlive()) {
            StopMusic();
            currentThread.interrupt();
        }
/*        try {
            GlobalScreen.unregisterNativeHook();
        } catch (NativeHookException nativeHookException) {
            nativeHookException.printStackTrace();
        }*/
        Instance = null;
        new MenuForm();
    }

    private void StartIntroThread(){
        System.out.println("Starting intro");
        PrepareIntro();
        currentThread = new Thread(() -> {
            try {
                Thread.sleep(6000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                StartSecondIntroThread();
            }
            if(!Thread.currentThread().isInterrupted()) StartSecondIntroThread();
        });
        currentThread.start();
    }

    private void StartSecondIntroThread(){
        System.out.println("Starting second intro");
        currentThread = new Thread(() -> {
            try {
                creditsPanel.setVisible(true);
                kyrLabel.setVisible(false);
                ruLabel.setVisible(false);
                engLabel.setVisible(false);
                Thread.sleep(6000);
            } catch (InterruptedException e) {
                lessonStarted = true;
                StartMotivatorsThread(0);
                Thread.currentThread().interrupt();
            }
            if(Thread.currentThread().isInterrupted()) return;
            lessonStarted = true;
            StartMotivatorsThread(0);
        });
        currentThread.start();
    }

    private void StartMotivatorsThread(int startpos) {
        PrepareMotivators();
        final List<String> lines = new ContentLoader().GetFile("motivators_ru.txt");
        int length = lines.toArray().length;
        currentThread = new Thread(() -> {
            int counter = startpos;
            while (counter < 120 && !Thread.currentThread().isInterrupted()){
                if(paused) continue;
                int item = 0;
                while(item < length && !Thread.currentThread().isInterrupted()){
                    if(paused) continue;
                    kyrLabel.setText(lines.get(item));
                    try {
                        Thread.sleep(125);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    item++;
                }

                UpdateProgress(counter / 120f, 0, 100);
                counter++;
            }
            if(Thread.currentThread().isInterrupted()) return;
            try {
                kyrLabel.setText("Приготовьтесь к уроку");
                Thread.sleep(20000);
                StartLessonTread(0, 0);
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
                try {
                    engLabel.setVisible(false);
                    ruLabel.setVisible(false);
                    kyrLabel.setText("Приготовьтесь слушать");
                    wordNumberLabel.setVisible(false);
                    Thread.sleep(20000);
                    StartRepeatThread(0);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
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
                try {
                    engLabel.setVisible(false);
                    ruLabel.setVisible(false);
                    wordNumberLabel.setVisible(false);
                    kyrLabel.setText("Приготовьтесь к уроку");
                    Thread.sleep(20000);
                    StartLessonTread(0, 3);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }

            }
        });
        currentThread.start();
    }

    private boolean isPlaying;
    private Clip currentMusicClip = null;
    private long pausedClipTime = 0;
    private boolean musicPaused;

    private void StartMusic(){

        if(musicPaused && pausedClipTime > -1) {
            currentMusicClip.setMicrosecondPosition(pausedClipTime);
        } else if(isPlaying) return;
        else currentMusicClip.setMicrosecondPosition(0);
        isPlaying = true;
        currentMusicClip.start();
        musicPaused = false;
    }

    private void StopMusic(){
        if(!isPlaying) return;
        currentMusicClip.stop();
        pausedClipTime = 0;
        isPlaying = false;
    }

    private void PauseMusic(){
        if(!isPlaying) return;
        musicPaused = true;
        pausedClipTime = currentMusicClip.getMicrosecondPosition();
        currentMusicClip.stop();
    }

    private void UpdateProgress(float current, int stagemin, int stagemax) {
        int value = Mathf.Lerp(current, stagemin, stagemax);
        progressSlider.setValue(stagemin + value);
    }

}