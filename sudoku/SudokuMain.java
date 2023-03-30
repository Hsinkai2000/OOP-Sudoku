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

    // == Define named constants for the game ==
    /** Number of rows of the game board (in cells) */
    public static final int ROWS = 40;
    /** Number of columns of the game board (in cells) */
    public static final int COLS = 40;
    /** Size of the body cell (in pixels) */
    public static final int CELL_SIZE = 16;

    /** App title */
    public static final String TITLE = "Sudoku";
    /** Width (in pixels) of the game board */
    public static final int PIT_WIDTH = COLS * CELL_SIZE;
    /** Height (in pixels) of the game board */
    public static final int PIT_HEIGHT = ROWS * CELL_SIZE;
    // private variables
    GameBoardPanel board = new GameBoardPanel();
    JButton btnNewGame = new JButton("New Game");

    // Constructor
    public SudokuMain() {
        Container cp = getContentPane();
        cp.setLayout(new BorderLayout());

        cp.add(board, BorderLayout.CENTER);

        // Add a button to the south to re-start the game via board.newGame()
        // ......
        cp.add(btnNewGame, BorderLayout.SOUTH);
        btnOnclick();

        // Initialize the game board to start the game
        board.newGame();

        pack(); // Pack the UI components, instead of using setSize()
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // to handle window-closing
        setTitle("Sudoku");
        setVisible(true);
    }

    private void btnOnclick() {
        btnNewGame.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                board.newGame();
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
                JFrame frame = new JFrame(TITLE);
                frame.setContentPane(main); // main JPanel as content pane
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.pack();
                frame.setLocationRelativeTo(null); // center the application window
                frame.setVisible(true); // show it
            }
        });
    }
}
