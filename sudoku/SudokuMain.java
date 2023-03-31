package sudoku;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

/**
 * The main Sudoku program
 */
public class SudokuMain extends JFrame {
    private static final long serialVersionUID = 1L; // to prevent serial warning
    /** App title */
    public static final String TITLE = "Sudoku";
    public static JFrame frame = new JFrame(TITLE);
    // private variables
    GameBoardPanel board = new GameBoardPanel();
    JMenuItem newGame = new JMenuItem("New Game");
    JMenuItem resetGame = new JMenuItem("Reset Game");
    static JMenuBar menuBar;

    // Constructor
    public SudokuMain() {
        Container cp = getContentPane();
        cp.setLayout(new BorderLayout());

        cp.add(board, BorderLayout.CENTER);

        generateMenuBar();
        this.setJMenuBar(menuBar);

        // Initialize the game board to start the game
        board.newGame();

        pack(); // Pack the UI components, instead of using setSize()
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // to handle window-closing
        setTitle("Sudoku");
        setVisible(true);
    }

    private void generateMenuBar() {
        menuBar = new JMenuBar();

        // File Menu
        JMenu fileMenu = new JMenu("File");
        JMenuItem exit = new JMenuItem("Exit");
        fileMenu.add(newGame);
        fileMenu.add(resetGame);
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
        newGame.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                board.newGame();
            }
        });

        resetGame.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                board.resetGame();
            }
        });
    }

    /** The entry main() entry method */
    public static void main(String[] args) {
        // Use the event-dispatcher thread to build the UI for thread-safety.
        SwingUtilities.invokeLater(new Runnable() {
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
