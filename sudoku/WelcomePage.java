package sudoku;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import org.w3c.dom.NameList;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.Flow;

public class WelcomePage extends JFrame {
    
    private JPanel welcomePanel;
    private JLabel welcomeLabel;
    private static JTextField playerNameField;
    private JButton playButton;
    private JButton scoreboardButton;
    private static ArrayList<String>nameList = new ArrayList<String>();
    private static ArrayList<Integer>pointList = new ArrayList<Integer>();
    private JPanel difficultyPanel;
    private JRadioButton beginnerRadioButton;
    private JRadioButton intermediateRadioButton;
    private JRadioButton expertRadioButton;
    private ButtonGroup btnGroup;
    private Image img;

    private final Color BG_COLOR= new Color(253, 243, 212);
    private final Color darkerColor = new Color(98, 31, 31);
    

    public WelcomePage() {
        setTitle("Welcome to Sudoku");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
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
        playButton.setPreferredSize(new Dimension(200,30));
        scoreboardButton = new JButton("Scoreboard");
        scoreboardButton.setPreferredSize(new Dimension(200,30));
        scoreboardButton.setBorder(null);
        scoreboardButton.setOpaque(true);
        scoreboardButton.setBackground(darkerColor);
        scoreboardButton.setForeground(BG_COLOR);

        displayRadioButton();
        populateScoreboard();
        loadImage();
        JLabel imageLabel = new JLabel(new ImageIcon(img));
        scoreboardButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String message="Scoreboard is not implemented yet.";
                if(nameList.size()!=0){
                    message = "Name\t\tScore\n";
                    for (int i = 0; i < nameList.size(); i++) {
                        message += nameList.get(i) + "\t\t" + pointList.get(i) + "\n";
                    }   
                }
                
                JOptionPane.showMessageDialog(null, message, "Scoreboard", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        
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
        c.insets = new Insets(10,0,0,0);
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
        
        add(welcomePanel);
        setVisible(true);
        pack();

        setLocationRelativeTo(null);
    }

    private void populateScoreboard() {
        nameList.add("Jason");
        pointList.add(700);
        nameList.add("Jonathan");
        pointList.add(600);
        nameList.add("Stephanie");
        pointList.add(400);
        nameList.add("Rachel");
        pointList.add(300);
        
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
        beginnerRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                GameBoardPanel.setPuzzleDifficulty(1);
            }
        });
        
        intermediateRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                GameBoardPanel.setPuzzleDifficulty(2);
            }
        });

        expertRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                GameBoardPanel.setPuzzleDifficulty(3);
            }
        });
        
        playButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String playerName = playerNameField.getText();
                SudokuMain sudoku = new SudokuMain();
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

    public static void addToScoreboard(int points){
        nameList.add(playerNameField.getText());
        pointList.add(points);
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
    

    
}
