package sudoku;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Console;
import java.io.IOException;
import java.net.URL;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicProgressBarUI;

/**
 * The main Sudoku program
 */
public class SudokuMain extends JFrame {

    private static final long serialVersionUID = 1L; // to prevent serial warning
    /** App title */
    public static final String TITLE = "Sudoku Samurai";
    public static JFrame frame = new JFrame(TITLE);

    GameBoardPanel board = new GameBoardPanel();
    JMenuItem newGame = new JMenuItem("New Game");
    JMenuItem resetGame = new JMenuItem("Reset Game");
    JMenuItem musicToggle = new JMenuItem("Music Toggle");
    // JMenuItem animeToggle = new JMenuItem("Anime Toggle");
    JMenuItem beginner = new JMenuItem("beginner");
    JMenuItem intermediate = new JMenuItem("intermediate");
    JMenuItem expert = new JMenuItem("expert");
    static JMenuBar menuBar;
    JPanel bottomBar = new JPanel(new GridLayout(2, 0));
    public static JLabel score = new JLabel("Score: 0");
    public static JLabel time = new JLabel("Time: 0");
    public static JLabel remaining = new JLabel("Remaining: 0");
    private static Clip clip;
    private static Container cp;
    static JProgressBar progressBar;
    private static JPanel bottomPanel = new JPanel(new GridLayout(1, 0));

    private static final Color BG_COLOR = new Color(253, 243, 212);
    private final Color darkerColor = new Color(98, 31, 31);

    // Constructor
    public SudokuMain() {
        cp = getContentPane();
        cp.setLayout(new BorderLayout());
        cp.add(board, BorderLayout.CENTER);
        generateBottomBar();
        generateMenuBar();
        this.setJMenuBar(menuBar);
        // Initialize the game board to start the game
        board.newGame(cp);
        playSound();
        board.setBackground(BG_COLOR);
        applyFont();
        pack(); // Pack the UI components, instead of using setSize()
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // to handle window-closing
        setTitle(TITLE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void applyFont(){
        URL fontUrl = getClass().getResource("./Font/gemstone/Gemstone.ttf");
        Font customFont;
        try {
            customFont = Font.createFont(Font.PLAIN, fontUrl.openStream()).deriveFont(20f);
            
            score.setFont(customFont);
            time.setFont(customFont);
            remaining.setFont(customFont);
            progressBar.setFont(customFont);
            
        } catch (FontFormatException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void generateBottomBar(){        
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setValue(0);
        progressBar.setForeground(darkerColor);
        progressBar.setBackground(BG_COLOR);
        bottomBar.add(progressBar);
        bottomPanel.add(time);
        bottomPanel.add(score);
        bottomPanel.add(remaining);
        bottomBar.add(bottomPanel);
        bottomBar.setBorder(new EmptyBorder(10, 10, 10, 10));
        this.add(bottomBar, BorderLayout.SOUTH);
    }

    private void generateMenuBar() {
        menuBar = new JMenuBar();
        menuBar.setForeground(darkerColor);
        // File Menu
        JMenu fileMenu = new JMenu("File");
        JMenuItem exit = new JMenuItem("Exit");
        JMenu level = new JMenu("Level");
        level.add(beginner);
        level.add(intermediate);
        level.add(expert);
        fileMenu.add(newGame);
        fileMenu.add(resetGame);
        fileMenu.add(level);
        fileMenu.add(musicToggle);
        // fileMenu.add(animeToggle);
        fileMenu.add(exit);
        menuBar.add(fileMenu);

        // Options Menu
        JMenu optionsMenu = new JMenu("Options");
        menuBar.add(optionsMenu);

        // Help Menu
        JMenu helpMenu = new JMenu("Help");
        menuBar.add(helpMenu);

        menuOnclick();
    }

    private void menuOnclick() {
        newGame.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        board.newGame(cp);
                    }
                });

        resetGame.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        board.resetGame();
                    }
                });

        beginner.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        changeDifficulty(1);
                    }
                });

        intermediate.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        changeDifficulty(2);
                    }
                });

        expert.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        changeDifficulty(3);
                    }
                });

        musicToggle.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        if (clip.isRunning()) {
                            clip.stop();
                        } else {
                            clip.start();
                        }
                    }
                });

        // animeToggle.addActionListener(
        //         new ActionListener() {
        //             public void actionPerformed(ActionEvent e) {
        //                 activateAnime();
        //             }
        //         });
    }

    // public void activateAnime() {
    //     GameBoardPanel.setPuzzleDifficulty(1);
    //     board.newGame(cp);
    //     // System.out.println("level difficulty changed to: " + level);
    // }

    private void changeDifficulty(int level) {
        GameBoardPanel.setPuzzleDifficulty(level);
        board.newGame(cp);
        System.out.println("level difficulty changed to: " + level);
    }

    public static synchronized void playSound() {
        new Thread(
                new Runnable() {
                    // The wrapper thread is unnecessary, unless it blocks on the
                    // Clip finishing; see comments.
                    public void run() {
                        try {
                            clip = AudioSystem.getClip();
                            AudioInputStream inputStream = AudioSystem.getAudioInputStream(
                                    SudokuMain.class.getResourceAsStream(
                                            "./Music/love-mellow-piano-143300.wav"));
                            clip.open(inputStream);
                            // Set the volume
                            FloatControl gainControl = (FloatControl) clip.getControl(
                                    FloatControl.Type.MASTER_GAIN);
                            gainControl.setValue(-10.0f);

                            clip.start();
                        } catch (Exception e) {
                            System.err.println(e.getMessage());
                        }
                    }
                })
                .start();
    }

    public static void main(String[] args) {
        // Use the event-dispatcher thread to build the UI for thread-safety.
        SwingUtilities.invokeLater(
                new Runnable() {
                    @Override
                    public void run() {
                        SudokuMain main = new SudokuMain();
                        frame.setContentPane(main); // main JPanel as content pane
                        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                        frame.pack();
                        frame.setLocationRelativeTo(null); // center the application window
                        frame.setVisible(true); // show it
                    }
                });
    }

}
