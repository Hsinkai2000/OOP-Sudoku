package sudoku;

import javax.swing.*;

import org.w3c.dom.NameList;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class WelcomePage extends JFrame {
    
    private JPanel welcomePanel;
    private JLabel welcomeLabel;
    private static JTextField playerNameField;
    private JButton playButton;
    private JButton scoreboardButton;
    private static ArrayList<String>nameList = new ArrayList<String>();
    private static ArrayList<Integer>pointList = new ArrayList<Integer>();

    public WelcomePage() {
        setTitle("Welcome to Sudoku");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        welcomePanel = new JPanel();
        welcomePanel.setLayout(new GridLayout(4, 1));
        
        welcomeLabel = new JLabel("Welcome to Sudoku! Please enter your name:");
        playerNameField = new JTextField();
        playButton = new JButton("Play Sudoku");
        scoreboardButton = new JButton("Scoreboard");
        
        playButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String playerName = playerNameField.getText();
                SudokuMain sudoku = new SudokuMain();
                dispose();
            }
        });
        
        nameList.add("jonathan");
        pointList.add(22);
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
        
        welcomePanel.add(welcomeLabel);
        welcomePanel.add(playerNameField);
        welcomePanel.add(playButton);
        welcomePanel.add(scoreboardButton);
        
        add(welcomePanel);
        setVisible(true);
        pack();
    }

    public static void addToScoreboard(int points){
        nameList.add(playerNameField.getText());
        pointList.add(points);
    }
    
    public static void main(String[] args) {
        WelcomePage welcomePage = new WelcomePage();
    }
}
