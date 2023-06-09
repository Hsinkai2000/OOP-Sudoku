package sudoku;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.lang.*;
import java.net.URL;
import java.util.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Flow;
import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import org.w3c.dom.NameList;

public class WelcomePage extends JFrame {

    private JPanel welcomePanel;
    private JLabel welcomeLabel;
    private static JTextField playerNameField;
    private JButton playButton;
    private JButton scoreboardButton;
    public static HashMap<String, Integer> scores = new HashMap<String, Integer>();
    private JPanel difficultyPanel;
    private JRadioButton beginnerRadioButton;
    private JRadioButton intermediateRadioButton;
    private JRadioButton expertRadioButton;
    private ButtonGroup btnGroup;
    private Image img;
    private static Clip welcomeclip;

    private final Color BG_COLOR = new Color(253, 243, 212);
    private final Color darkerColor = new Color(98, 31, 31);

    public WelcomePage() {
        setTitle("Sudoku Samurai");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        playSound();
        setpanel();
        displayRadioButton();
        populateScoreboard();
        loadImage();
        scoreboardbtnOnClick();
        setupGrid();

        applyFont();
        add(welcomePanel);
        setVisible(true);
        pack();
        setLocationRelativeTo(null);
    }

    private void applyFont(){
        URL fontUrl = getClass().getResource("./Font/alice_smile/Alice Smile.ttf");
        Font customFont;
        try {
            customFont = Font.createFont(Font.PLAIN, fontUrl.openStream()).deriveFont(13f);
            welcomeLabel.setFont(customFont);
            playerNameField.setFont(customFont);
            playButton.setFont(customFont);
            scoreboardButton.setFont(customFont);
            beginnerRadioButton.setFont(customFont);
            intermediateRadioButton.setFont(customFont);
            expertRadioButton.setFont(customFont);
        } catch (FontFormatException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void setupGrid() {
        JLabel imageLabel = new JLabel(new ImageIcon(img));
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.weighty = 1.0;
        c.weightx = 1.0;
        c.fill = GridBagConstraints.BOTH;
        welcomePanel.add(imageLabel, c);

        c.gridx = 0;
        c.gridy = 1;
        c.weighty = 0.0;
        c.insets = new Insets(10, 0, 0, 0);
        welcomePanel.add(welcomeLabel, c);

        c.gridx = 0;
        c.gridy = 2;
        c.weighty = 0.0;
        welcomePanel.add(playerNameField, c);

        c.gridx = 0;
        c.gridy = 3;
        c.weighty = 0.0;
        difficultyPanel.setPreferredSize(new Dimension(400, 30));
        welcomePanel.add(difficultyPanel, c);

        c.gridx = 0;
        c.gridy = 4;
        c.weighty = 0.0;
        welcomePanel.add(playButton, c);

        c.gridx = 0;
        c.gridy = 5;
        c.weighty = 0.0;
        welcomePanel.add(scoreboardButton, c);
    }

    private void scoreboardbtnOnClick(){
        scoreboardButton.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        scoreboardDialog();
                    }
                });

    }

    private void setpanel(){
        welcomePanel = new JPanel();
        welcomePanel.setBackground(BG_COLOR);
        welcomePanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        welcomePanel.setLayout(new GridBagLayout());

        welcomeLabel = new JLabel("Welcome to Sudoku! Please enter your name:");
        welcomeLabel.setForeground(darkerColor);
        welcomeLabel.setPreferredSize(new Dimension(400, 30));
        playerNameField = new JTextField();
        playButton = new JButton("Play Sudoku");
        playButton.setBorder(null);
        playButton.setOpaque(true);
        playButton.setBackground(darkerColor);
        playButton.setForeground(BG_COLOR);
        playButton.setPreferredSize(new Dimension(200, 30));
        scoreboardButton = new JButton("Scoreboard");
        scoreboardButton.setPreferredSize(new Dimension(200, 30));
        scoreboardButton.setBorder(null);
        scoreboardButton.setOpaque(true);
        scoreboardButton.setBackground(darkerColor);
        scoreboardButton.setForeground(BG_COLOR);
    }

    private void populateScoreboard() {
        scores.put("Jason", 59 );
        scores.put("Jonathan", 33);
        scores.put("Stephanie", 23);
        scores.put("Rachel", 58);

        // scores.put("Jason", Jason);
        // scores.put("Jonathan", Jonathan);
        // scores.put("Stephanie", Stephanie);
        // scores.put("Rachel", Rachel);

        Map<String, Integer> hm1 = sortByValue(scores);

        // print the sorted hashmap
        // for (Map.Entry<String, Integer> en : hm1.entrySet()) {
        // System.out.println("Key = " + en.getKey() +
        // ", Value = " + en.getValue());
        // }

        for (Map.Entry<String, Integer> en : hm1.entrySet()) {
            System.out.println(en.getKey() + ", " + en.getValue());
        }
    }

    private void displayRadioButton() {
        btnGroup = new ButtonGroup();
        beginnerRadioButton = new JRadioButton("Beginner");
        beginnerRadioButton.setForeground(darkerColor);
        intermediateRadioButton = new JRadioButton("Intermediate");
        intermediateRadioButton.setForeground(darkerColor);
        expertRadioButton = new JRadioButton("Expert");
        expertRadioButton.setForeground(darkerColor);
        btnGroup.add(beginnerRadioButton);
        btnGroup.add(intermediateRadioButton);
        btnGroup.add(expertRadioButton);
        beginnerRadioButton.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        GameBoardPanel.setPuzzleDifficulty(1);
                    }
                });

        intermediateRadioButton.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        GameBoardPanel.setPuzzleDifficulty(2);
                    }
                });

        expertRadioButton.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        GameBoardPanel.setPuzzleDifficulty(3);
                    }
                });

        playButton.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        String playerName = playerNameField.getText();
                        SudokuMain sudoku = new SudokuMain();
                        welcomeclip.stop();
                        dispose();
                    }
                });

        difficultyPanel = new JPanel();
        difficultyPanel.setLayout(new FlowLayout());
        difficultyPanel.add(beginnerRadioButton);
        difficultyPanel.add(intermediateRadioButton);
        difficultyPanel.add(expertRadioButton);

        beginnerRadioButton.setSelected(true);
        difficultyPanel.setBackground(BG_COLOR);
    }

    public static void addToScoreboard(int points, int time) {
        if(scores.get(playerNameField.getText()) == null){
            scores.put(playerNameField.getText(), points/time);  
        }
        else{
            if (points/time > scores.get(playerNameField.getText())) {
                scores.put(playerNameField.getText(), points/time);            
            }
        }
        
    }

    public static void main(String[] args) {
        WelcomePage welcomePage = new WelcomePage();
    }

    public Image loadImage() {
        // URL imgUrl = getClass().getResource("./images/background-image-copy.png");
        URL imgUrl = getClass().getResource("./images/Sudoku samurai2.png");
        if (imgUrl == null) {
            System.err.println("Couldn't find file: ");
        } else {
            try {
                img = ImageIO.read(imgUrl);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return img;
    }

    public static synchronized void playSound() {
        new Thread(
                new Runnable() {
                    // The wrapper thread is unnecessary, unless it blocks on the
                    // Clip finishing; see comments.
                    public void run() {
                        try {
                            welcomeclip = AudioSystem.getClip();
                            AudioInputStream inputStream = AudioSystem.getAudioInputStream(
                                    SudokuMain.class.getResourceAsStream(
                                            "./Music/KonohaPeace.wav"));
                                            welcomeclip.open(inputStream);
                            // Set the volume
                            FloatControl gainControl = (FloatControl) welcomeclip.getControl(
                                    FloatControl.Type.MASTER_GAIN);
                            gainControl.setValue(-10.0f);

                            welcomeclip.start();
                        } catch (Exception e) {
                            System.err.println(e.getMessage());
                        }
                    }
                })
                .start();
    }
    
    public static HashMap<String, Integer> sortByValue(
            HashMap<String, Integer> hm) {
        // Create a list from elements of HashMap
        List<Map.Entry<String, Integer>> list = new LinkedList<Map.Entry<String, Integer>>(
                hm.entrySet());

        // Sort the list
        Collections.sort(
                list,
                new Comparator<Map.Entry<String, Integer>>() {
                    public int compare(
                            Map.Entry<String, Integer> o1,
                            Map.Entry<String, Integer> o2) {
                        return (o2.getValue()).compareTo(o1.getValue());
                    }
                });

        // put data from sorted list to hashmap
        HashMap<String, Integer> temp = new LinkedHashMap<String, Integer>();
        for (Map.Entry<String, Integer> aa : list) {
            temp.put(aa.getKey(), aa.getValue());
        }
        return temp;
    }

    public static String loadMessage() {
        String message = "";
        Map<String, Integer> hm1 = sortByValue(scores);
        for (Map.Entry<String, Integer> en : hm1.entrySet()) {
            System.out.println(en.getKey() + ", " + en.getValue());
            message += en.getKey() + "\t" + en.getValue() + "\n";
        }
        return message;
    }

    
    private void scoreboardDialog() {
        // Create a new dialog for the death animation
        JDialog dialog = new JDialog();

        dialog.setBackground(BG_COLOR);
        dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        dialog.setTitle("Leaderboard");

            JLabel label = new JLabel();
            String message ="Name\tScore\n"+ loadMessage();
            JPanel panel = new JPanel();
            panel = (JPanel) dialog.getContentPane();
            panel.setBorder(new EmptyBorder(10, 10, 10, 10));

            JTextArea textLabel = new JTextArea(message);
            label.add(textLabel);
            textLabel.setBackground(BG_COLOR);
            textLabel.setForeground(darkerColor);
            dialog.getContentPane().add(textLabel, BorderLayout.CENTER);
            dialog.getContentPane().setBackground(BG_COLOR);
            dialog.pack();
            dialog.setResizable(false);
            dialog.setLocationRelativeTo(null);
            dialog.setVisible(true);
       
    }
}


