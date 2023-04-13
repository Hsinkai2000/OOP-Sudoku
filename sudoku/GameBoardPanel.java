package sudoku;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Line2D;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Time;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class GameBoardPanel extends JPanel {

    private static final long serialVersionUID = 1L; // to prevent serial warning

    // Define named constants for the game board properties
    public static final int GRID_SIZE = 9; // Size of the board
    public static final int SUBGRID_SIZE = 3; // Size of the sub-grid
    // Define named constants for UI sizes
    public static final int CELL_SIZE = 60; // Cell width/height in pixels
    public static final int BOARD_WIDTH = CELL_SIZE * GRID_SIZE;
    public static final int BOARD_HEIGHT = CELL_SIZE * GRID_SIZE;
    // Board width/height in pixels

    public static final Color COLOR_PIT = Color.LIGHT_GRAY;
    public static final Color COLOR_GAMEOVER = Color.GREEN;
    public static final Font FONT_GAMEOVER = new Font("Verdana", Font.BOLD, 30);

    public static int STEPS_PER_SEC = 6;
    public static int STEP_IN_MSEC = 1000 / STEPS_PER_SEC;
    private Cell sourceCell;

    private Line2D testline = new Line2D.Double(10, 10, 10, 30);

    private static final Color BG_COLOR = new Color(253, 243, 212);
    private final Color darkerColor = new Color(98, 31, 31);
    // Define properties
    /** The game board composes of 9x9 Cells (customized JTextFields) */
    public Cell[][] cells = new Cell[GRID_SIZE][GRID_SIZE];
    /** It also contains a Puzzle with array numbers and isGiven */
    private Puzzle puzzle = new Puzzle();
    private int score = 0;
    private static int difficulty = 1;
    private int blanksLeft;
    private Duration ElapsedTime;
    private Timer stepTimer;
    private Image img;
    public boolean haveAnime = false;
    private Image gif;
    public static ArrayList<Instant> instantList = new ArrayList<Instant>();

    /** Constructor */
    public GameBoardPanel() {
        super.setLayout(new GridLayout(GRID_SIZE, GRID_SIZE)); // JPanel
        // Allocate the 2D array of Cell, and added into JPanel.
        for (int row = 0; row < GRID_SIZE; ++row) {
            for (int col = 0; col < GRID_SIZE; ++col) {
                if (puzzle.isGiven[row][col] == true) {
                    cells[row][col] = new Cell(row, col, CellStatus.GIVEN);
                } else {
                    cells[row][col] = new Cell(row, col, CellStatus.TO_GUESS);
                }
                super.add(cells[row][col]); // JPanel
            }
        }

        super.setPreferredSize(new Dimension(BOARD_WIDTH, BOARD_HEIGHT));
    }

    /**
     * Generate a new puzzle; and reset the gameboard of cells based on the puzzle.
     * You can call this method to start a new game.
     */
    public void newGame(Container cp) {
        // Generate a new puzzle
        blanksLeft = 3 * difficulty;
        SudokuMain.remaining.setText("Remaining: " + blanksLeft);
        puzzle.newPuzzle(blanksLeft);

        cp.setFocusTraversalPolicy(new SudokuFocusTraversalPolicy(cells));
        // Initialize all the 9x9 cells, based on the puzzle.
        for (int row = 0; row < GRID_SIZE; ++row) {
            for (int col = 0; col < GRID_SIZE; ++col) {
                cells[row][col].newGame(
                        puzzle.numbers[row][col],
                        puzzle.isGiven[row][col]);
            }
        }

        CellKeyListener listener = new CellKeyListener();
        int count = 1;
        for (int row = 0; row < GRID_SIZE; ++row) {
            for (int col = 0; col < GRID_SIZE; ++col) {
                if (cells[row][col].status == CellStatus.TO_GUESS &&
                        cells[row][col].isEditable()) {
                    cells[row][col].addKeyListener(listener); // For all editable rows and cols
                    System.out.println("to guess: " + cells[row][col].isEditable());

                    if (count == 1) {
                        sourceCell = cells[row][col];
                        System.out.println("sourceCell: " + sourceCell.number);
                        count++;
                    }
                }
            }
        }
        this.setFocusCycleRoot(true);
        this.setFocusTraversalPolicy(new SudokuFocusTraversalPolicy(cells));

        instantList.add(Instant.now());
        stepTimer = new Timer(STEP_IN_MSEC, e -> stepGame(instantList.get(instantList.size() - 1)));
        stepTimer.start();
    }

    private void stepGame(Instant instantStart) {
        Instant instantStop = Instant.now();
        ElapsedTime = Duration.between(instantStart, instantStop);
        SudokuMain.time.setText("Time: " + ElapsedTime.toSeconds());
        float minus = ((float) getRemaining() / ((float) difficulty * 3)) * 100;
        SudokuMain.progressBar.setValue(100 - (int) minus);
    }

    public void resetGame() {
        // Initialize all the 9x9 cells, based on the puzzle.
        for (int row = 0; row < GRID_SIZE; ++row) {
            for (int col = 0; col < GRID_SIZE; ++col) {
                cells[row][col].newGame(
                        puzzle.numbers[row][col],
                        puzzle.isGiven[row][col]);
            }
        }
    }

    /**
     * Return true if the puzzle is solved
     * i.e., none of the cell have status of TO_GUESS or WRONG_GUESS
     */
    public boolean isSolved() {
        for (int row = 0; row < GRID_SIZE; ++row) {
            for (int col = 0; col < GRID_SIZE; ++col) {
                if (cells[row][col].status == CellStatus.TO_GUESS ||
                        cells[row][col].status == CellStatus.WRONG_GUESS) {
                    return false;
                }
            }
        }
        return true;
    }

    public void reverseHighlight() {
        for (int row = 0; row < GRID_SIZE; ++row) {
            for (int col = 0; col < GRID_SIZE; ++col) {
                if (cells[row][col].status == CellStatus.WRONG_HINT ||
                        cells[row][col].status == CellStatus.PENDING) {
                    cells[row][col].status = CellStatus.GIVEN;
                    cells[row][col].paint();
                }
            }
        }
    }

    public void applyPending(int numberIn) {
        for (int row = 0; row < GRID_SIZE; ++row) {
            for (int col = 0; col < GRID_SIZE; ++col) {
                if (puzzle.numbers[row][col] == numberIn) {
                    if (cells[row][col].status == CellStatus.GIVEN) {
                        cells[row][col].status = CellStatus.PENDING;
                        cells[row][col].paint();
                    }
                }
            }
        }
    }

    // Check for same row, column and box
    public void applyWrongHint(int numberIn, int cellRow, int cellCol) {
        for (int row = 0; row < GRID_SIZE; ++row) {
            if (puzzle.numbers[row][cellCol] == numberIn &&
                    cells[row][cellCol].status == CellStatus.GIVEN) {
                // if(puzzle.numbers[row][cellCol] == numberIn && cells[row][cellCol].status ==
                // CellStatus.GIVEN){
                cells[row][cellCol].status = CellStatus.WRONG_HINT;
                cells[row][cellCol].paint();
            }
        }
        for (int col = 0; col < GRID_SIZE; ++col) {
            if (puzzle.numbers[cellRow][col] == numberIn &&
                    cells[cellRow][col].status == CellStatus.GIVEN) {
                cells[cellRow][col].status = CellStatus.WRONG_HINT;
                cells[cellRow][col].paint();
            }
        }

        // For col 0,3,6
        if (cellCol % 3 == 0) {
            if (cellRow % 3 == 0) {
                if (puzzle.numbers[cellRow + 1][cellCol + 1] == numberIn &&
                        cells[cellRow + 1][cellCol + 1].status == CellStatus.GIVEN) {
                    cells[cellRow + 1][cellCol + 1].status = CellStatus.WRONG_HINT;
                    cells[cellRow + 1][cellCol + 1].paint();
                }
                if (puzzle.numbers[cellRow + 2][cellCol + 1] == numberIn &&
                        cells[cellRow + 2][cellCol + 1].status == CellStatus.GIVEN) {
                    cells[cellRow + 2][cellCol + 1].status = CellStatus.WRONG_HINT;
                    cells[cellRow + 2][cellCol + 1].paint();
                }
                if (puzzle.numbers[cellRow + 1][cellCol + 2] == numberIn &&
                        cells[cellRow + 1][cellCol + 2].status == CellStatus.GIVEN) {
                    cells[cellRow + 1][cellCol + 2].status = CellStatus.WRONG_HINT;
                    cells[cellRow + 1][cellCol + 2].paint();
                }
                if (puzzle.numbers[cellRow + 2][cellCol + 2] == numberIn &&
                        cells[cellRow + 2][cellCol + 2].status == CellStatus.GIVEN) {
                    cells[cellRow + 2][cellCol + 2].status = CellStatus.WRONG_HINT;
                    cells[cellRow + 2][cellCol + 2].paint();
                }
            } else if (cellRow % 3 == 1) {
                if (puzzle.numbers[cellRow + 1][cellCol + 1] == numberIn &&
                        cells[cellRow + 1][cellCol + 1].status == CellStatus.GIVEN) {
                    cells[cellRow + 1][cellCol + 1].status = CellStatus.WRONG_HINT;
                    cells[cellRow + 1][cellCol + 1].paint();
                }
                if (puzzle.numbers[cellRow - 1][cellCol + 1] == numberIn &&
                        cells[cellRow - 1][cellCol + 1].status == CellStatus.GIVEN) {
                    cells[cellRow - 1][cellCol + 1].status = CellStatus.WRONG_HINT;
                    cells[cellRow - 1][cellCol + 1].paint();
                }
                if (puzzle.numbers[cellRow + 1][cellCol + 2] == numberIn &&
                        cells[cellRow + 1][cellCol + 2].status == CellStatus.GIVEN) {
                    cells[cellRow + 1][cellCol + 2].status = CellStatus.WRONG_HINT;
                    cells[cellRow + 1][cellCol + 2].paint();
                }
                if (puzzle.numbers[cellRow - 1][cellCol + 2] == numberIn &&
                        cells[cellRow - 1][cellCol + 2].status == CellStatus.GIVEN) {
                    cells[cellRow - 1][cellCol + 2].status = CellStatus.WRONG_HINT;
                    cells[cellRow - 1][cellCol + 2].paint();
                }
            } else {
                if (puzzle.numbers[cellRow - 2][cellCol + 1] == numberIn &&
                        cells[cellRow - 2][cellCol + 1].status == CellStatus.GIVEN) {
                    cells[cellRow - 2][cellCol + 1].status = CellStatus.WRONG_HINT;
                    cells[cellRow - 2][cellCol + 1].paint();
                }
                if (puzzle.numbers[cellRow - 1][cellCol + 1] == numberIn &&
                        cells[cellRow - 1][cellCol + 1].status == CellStatus.GIVEN) {
                    cells[cellRow - 1][cellCol + 1].status = CellStatus.WRONG_HINT;
                    cells[cellRow - 1][cellCol + 1].paint();
                }
                if (puzzle.numbers[cellRow - 2][cellCol + 2] == numberIn &&
                        cells[cellRow - 2][cellCol + 2].status == CellStatus.GIVEN) {
                    cells[cellRow - 2][cellCol + 2].status = CellStatus.WRONG_HINT;
                    cells[cellRow - 2][cellCol + 2].paint();
                }
                if (puzzle.numbers[cellRow - 1][cellCol + 2] == numberIn &&
                        cells[cellRow - 1][cellCol + 2].status == CellStatus.GIVEN) {
                    cells[cellRow - 1][cellCol + 2].status = CellStatus.WRONG_HINT;
                    cells[cellRow - 1][cellCol + 2].paint();
                }
            }
        } else if (cellCol % 3 == 1) {
            // For col 1,4,7
            if (cellRow % 3 == 0) {
                if (puzzle.numbers[cellRow + 1][cellCol + 1] == numberIn &&
                        cells[cellRow + 1][cellCol + 1].status == CellStatus.GIVEN) {
                    cells[cellRow + 1][cellCol + 1].status = CellStatus.WRONG_HINT;
                    cells[cellRow + 1][cellCol + 1].paint();
                }
                if (puzzle.numbers[cellRow + 2][cellCol + 1] == numberIn &&
                        cells[cellRow + 2][cellCol + 1].status == CellStatus.GIVEN) {
                    cells[cellRow + 2][cellCol + 1].status = CellStatus.WRONG_HINT;
                    cells[cellRow + 2][cellCol + 1].paint();
                }
                if (puzzle.numbers[cellRow + 1][cellCol - 1] == numberIn &&
                        cells[cellRow + 1][cellCol - 1].status == CellStatus.GIVEN) {
                    cells[cellRow + 1][cellCol - 1].status = CellStatus.WRONG_HINT;
                    cells[cellRow + 1][cellCol - 1].paint();
                }
                if (puzzle.numbers[cellRow + 2][cellCol - 1] == numberIn &&
                        cells[cellRow + 2][cellCol - 1].status == CellStatus.GIVEN) {
                    cells[cellRow + 2][cellCol - 1].status = CellStatus.WRONG_HINT;
                    cells[cellRow + 2][cellCol - 1].paint();
                }
            } else if (cellRow % 3 == 1) {
                if (puzzle.numbers[cellRow + 1][cellCol + 1] == numberIn &&
                        cells[cellRow + 1][cellCol + 1].status == CellStatus.GIVEN) {
                    cells[cellRow + 1][cellCol + 1].status = CellStatus.WRONG_HINT;
                    cells[cellRow + 1][cellCol + 1].paint();
                }
                if (puzzle.numbers[cellRow - 1][cellCol + 1] == numberIn &&
                        cells[cellRow - 1][cellCol + 1].status == CellStatus.GIVEN) {
                    cells[cellRow - 1][cellCol + 1].status = CellStatus.WRONG_HINT;
                    cells[cellRow - 1][cellCol + 1].paint();
                }
                if (puzzle.numbers[cellRow + 1][cellCol - 1] == numberIn &&
                        cells[cellRow + 1][cellCol - 1].status == CellStatus.GIVEN) {
                    cells[cellRow + 1][cellCol - 1].status = CellStatus.WRONG_HINT;
                    cells[cellRow + 1][cellCol - 1].paint();
                }
                if (puzzle.numbers[cellRow - 1][cellCol - 1] == numberIn &&
                        cells[cellRow - 1][cellCol - 1].status == CellStatus.GIVEN) {
                    cells[cellRow - 1][cellCol - 1].status = CellStatus.WRONG_HINT;
                    cells[cellRow - 1][cellCol - 1].paint();
                }
            } else {
                if (puzzle.numbers[cellRow - 2][cellCol + 1] == numberIn &&
                        cells[cellRow - 2][cellCol + 1].status == CellStatus.GIVEN) {
                    cells[cellRow - 2][cellCol + 1].status = CellStatus.WRONG_HINT;
                    cells[cellRow - 2][cellCol + 1].paint();
                }
                if (puzzle.numbers[cellRow - 1][cellCol + 1] == numberIn &&
                        cells[cellRow - 1][cellCol + 1].status == CellStatus.GIVEN) {
                    cells[cellRow - 1][cellCol + 1].status = CellStatus.WRONG_HINT;
                    cells[cellRow - 1][cellCol + 1].paint();
                }
                if (puzzle.numbers[cellRow - 2][cellCol - 1] == numberIn &&
                        cells[cellRow - 2][cellCol - 1].status == CellStatus.GIVEN) {
                    cells[cellRow - 2][cellCol - 1].status = CellStatus.WRONG_HINT;
                    cells[cellRow - 2][cellCol - 1].paint();
                }
                if (puzzle.numbers[cellRow - 1][cellCol - 1] == numberIn &&
                        cells[cellRow - 1][cellCol - 1].status == CellStatus.GIVEN) {
                    cells[cellRow - 1][cellCol - 1].status = CellStatus.WRONG_HINT;
                    cells[cellRow - 1][cellCol - 1].paint();
                }
            }
        } else {
            // For col 2,5,8
            if (cellRow % 3 == 0) {
                if (puzzle.numbers[cellRow + 1][cellCol - 2] == numberIn &&
                        cells[cellRow + 1][cellCol - 2].status == CellStatus.GIVEN) {
                    cells[cellRow + 1][cellCol - 2].status = CellStatus.WRONG_HINT;
                    cells[cellRow + 1][cellCol - 2].paint();
                }
                if (puzzle.numbers[cellRow + 2][cellCol - 2] == numberIn &&
                        cells[cellRow + 2][cellCol - 2].status == CellStatus.GIVEN) {
                    cells[cellRow + 2][cellCol - 2].status = CellStatus.WRONG_HINT;
                    cells[cellRow + 2][cellCol - 2].paint();
                }
                if (puzzle.numbers[cellRow + 1][cellCol - 1] == numberIn &&
                        cells[cellRow + 1][cellCol - 1].status == CellStatus.GIVEN) {
                    cells[cellRow + 1][cellCol - 1].status = CellStatus.WRONG_HINT;
                    cells[cellRow + 1][cellCol - 1].paint();
                }
                if (puzzle.numbers[cellRow + 2][cellCol - 1] == numberIn &&
                        cells[cellRow + 2][cellCol - 1].status == CellStatus.GIVEN) {
                    cells[cellRow + 2][cellCol - 1].status = CellStatus.WRONG_HINT;
                    cells[cellRow + 2][cellCol - 1].paint();
                }
            } else if (cellRow % 3 == 1) {
                if (puzzle.numbers[cellRow + 1][cellCol - 2] == numberIn &&
                        cells[cellRow + 1][cellCol - 2].status == CellStatus.GIVEN) {
                    cells[cellRow + 1][cellCol - 2].status = CellStatus.WRONG_HINT;
                    cells[cellRow + 1][cellCol - 2].paint();
                }
                if (puzzle.numbers[cellRow - 1][cellCol - 2] == numberIn &&
                        cells[cellRow - 1][cellCol - 2].status == CellStatus.GIVEN) {
                    cells[cellRow - 1][cellCol - 2].status = CellStatus.WRONG_HINT;
                    cells[cellRow - 1][cellCol - 2].paint();
                }
                if (puzzle.numbers[cellRow + 1][cellCol - 1] == numberIn &&
                        cells[cellRow + 1][cellCol - 1].status == CellStatus.GIVEN) {
                    cells[cellRow + 1][cellCol - 1].status = CellStatus.WRONG_HINT;
                    cells[cellRow + 1][cellCol - 1].paint();
                }
                if (puzzle.numbers[cellRow - 1][cellCol - 1] == numberIn &&
                        cells[cellRow - 1][cellCol - 1].status == CellStatus.GIVEN) {
                    cells[cellRow - 1][cellCol - 1].status = CellStatus.WRONG_HINT;
                    cells[cellRow - 1][cellCol - 1].paint();
                }
            } else {
                if (puzzle.numbers[cellRow - 2][cellCol - 2] == numberIn &&
                        cells[cellRow - 2][cellCol - 2].status == CellStatus.GIVEN) {
                    cells[cellRow - 2][cellCol - 2].status = CellStatus.WRONG_HINT;
                    cells[cellRow - 2][cellCol - 2].paint();
                }
                if (puzzle.numbers[cellRow - 1][cellCol - 2] == numberIn &&
                        cells[cellRow - 1][cellCol - 2].status == CellStatus.GIVEN) {
                    cells[cellRow - 1][cellCol - 2].status = CellStatus.WRONG_HINT;
                    cells[cellRow - 1][cellCol - 2].paint();
                }
                if (puzzle.numbers[cellRow - 2][cellCol - 1] == numberIn &&
                        cells[cellRow - 2][cellCol - 1].status == CellStatus.GIVEN) {
                    cells[cellRow - 2][cellCol - 1].status = CellStatus.WRONG_HINT;
                    cells[cellRow - 2][cellCol - 1].paint();
                }
                if (puzzle.numbers[cellRow - 1][cellCol - 1] == numberIn &&
                        cells[cellRow - 1][cellCol - 1].status == CellStatus.GIVEN) {
                    cells[cellRow - 1][cellCol - 1].status = CellStatus.WRONG_HINT;
                    cells[cellRow - 1][cellCol - 1].paint();
                }
            }
        }
    }

    private class CellKeyListener implements KeyListener {
        
        private boolean hintMode = false;
        @Override
        public void keyTyped(KeyEvent e) {
            // Get a reference of the JTextField that triggers this action event
            sourceCell = (Cell) e.getSource();
            char in = e.getKeyChar();
            if (in == 'h') {
            }
            else if (Character.isDigit(in) || Character.getNumericValue(in) == -1) {

                if(hintMode){
                    System.out.println("waiting key in hint mode");
                    displayHint(in);
                }
                else{// Retrieve the int entered
                    int numberIn = Character.getNumericValue(in);

                    // For debugging
                    System.out.println("You entered " + numberIn);
                    System.out.println("cellnum: " + sourceCell.number);
                    reverseHighlight();
                    if (sourceCell.status != CellStatus.GIVEN) {
                        // check if number entered is correct or wrong
                        if (numberIn == 0) {
                            haveAnime = true;
                        }
                        if(haveAnime){
                            if (numberIn == sourceCell.number) {
                                correctGuess(numberIn);
                            } else {
                                sourceCell.status = CellStatus.WRONG_GUESS;
                                applyPendingAnime(in);
                            }
                            sourceCell.paint(); // re-paint this cell based on its status
                            SudokuMain.score.setText("Score: " + score);
    
                            if (isSolved()) {
                                puzzleSolved();
                            }
                        }
                        else{
                            if (numberIn == sourceCell.number) {
                                correctGuess(numberIn);
                            } else {
                                sourceCell.status = CellStatus.WRONG_GUESS;
                                applyWrongHint(numberIn, sourceCell.row, sourceCell.col);
                            }
                            sourceCell.paint(); // re-paint this cell based on its status
                            SudokuMain.score.setText("Score: " + score);
    
                            if (isSolved()) {
                                puzzleSolved();
                            }
                        }                        
                    }
                }
            } 
            else {
                displayPopup("Error", "Please enter a number");
            }
        }

        private void displayPopup(String title, String message) {
            JOptionPane.showMessageDialog(
                    null,
                    message,
                    title,
                    JOptionPane.INFORMATION_MESSAGE);
        }

        @Override
        public void keyPressed(KeyEvent e) {
            sourceCell = (Cell) e.getSource();
            char in = e.getKeyChar();
            if (in == 'h') {                
                hintMode=true;
                System.out.println("waiting key");
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
            sourceCell = (Cell) e.getSource();
            char in = e.getKeyChar();
            if (in == 'h') {
                sourceCell.setText("");
                removeHint();
                hintMode=false;
                System.out.println("waiting ended");
            }
        }

        private void removeHint(){
            for (int i = 0; i < cells.length; i++) {
                for (int j = 0; j < cells[i].length; j++) {
                    if (cells[i][j].status == CellStatus.HINT) {
                        if(puzzle.isGiven[i][j]){
                            cells[i][j].status=CellStatus.GIVEN;
                        }
                        else{
                            cells[i][j].status=CellStatus.CORRECT_GUESS;
                        }
                        cells[i][j].paint();
                    }
                }
            }
        }
        
        private void displayHint(char input) {
            for (int i = 0; i < cells.length; i++) {
                for (int j = 0; j < cells[i].length; j++) {
                    System.out.println("looped into " + cells[i][j].number);                    
                    System.out.println("looped into " + cells[i][j].status);
                    if (cells[i][j].number == Character.getNumericValue(input)){
                        System.out.println("input " + input);    
                        if(cells[i][j].status == CellStatus.GIVEN || cells[i][j].status == CellStatus.CORRECT_GUESS) {
                                      
                            System.out.println("inside condition" + cells[i][j].status);
                            cells[i][j].status=CellStatus.HINT;
                            cells[i][j].paint();
                        }
                    }
                }
            }
        }
        
        private void applyPendingAnime(char input){
            for (int i = 0; i < cells.length; i++) {
                for (int j = 0; j < cells[i].length; j++) {
                    if (cells[i][j].number == Character.getNumericValue(input)){
                        if(cells[i][j].status == CellStatus.GIVEN || cells[i][j].status == CellStatus.CORRECT_GUESS) {
                                      
                            cells[i][j].status=CellStatus.PENDING;
                            cells[i][j].paint();
                        }
                    }
                }
            }
        }
    }


    private void correctGuess(int numberIn) {
        sourceCell.status = CellStatus.CORRECT_GUESS;
        System.out.println(isSolved());
        score += 100;
        blanksLeft--;
        SudokuMain.remaining.setText("Remaining: " + blanksLeft);
        applyPending(numberIn);
    }

    private void puzzleSolved() {
        System.out.println("solved");
        WelcomePage.addToScoreboard(score, (int) ElapsedTime.toSeconds());
        stepTimer.stop();
        WinAnimationDialog();
    }

    public static void setPuzzleDifficulty(int level) {
        difficulty = level;
    }

    public int getRemaining() {
        return blanksLeft;
    }

    @Override
    public void paintComponent(Graphics g) {
        // super.paintComponent(g);
        super.paintComponent(g);

        if (haveAnime) {
            img = loadAnimeImage();
        } else {
            img = loadImage();
        }
        // img = loadAnimeImage();
        if (img != null) {
            g.drawImage(img, 0, 0, getWidth(), getHeight(), this);
        }
        // System.out.println("Reached paint");
    }

    public Image loadImage() {
        // URL imgUrl = getClass().getResource("./images/background-image-copy.png");
        URL imgUrl = getClass().getResource("./images/background-image.png");
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

    public Image loadAnimeImage() {
        URL imgUrl = getClass().getResource("./images/background-image-copy.png");

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

    private void WinAnimationDialog() {
        // Create a new dialog for the death animation
        JDialog dialog = new JDialog();

        dialog.setBackground(BG_COLOR);
        dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        dialog.setTitle("Congratulations!");

        // Tries to play the death sound and open the death animation gif in a new
        // window
        try {
            ImageIcon icon = new ImageIcon(
                    getClass().getResource("./images/slash.gif"));
            JLabel label = new JLabel(icon);
            String message = "Congratulation! You WON!\nScore: " +
                    score +
                    "\nTime: " +
                    ElapsedTime.toSeconds() +
                    " seconds\n\nLeaderboard:\n";
            // for (int i = 0; i < WelcomePage.nameList.size(); i++) {
            // message += WelcomePage.nameList.get(i) + "\t\t" +
            // WelcomePage.pointList.get(i) + "\t\t" + WelcomePage.timeList.get(i) + "\n";
            // }

            message += WelcomePage.loadMessage();

            // // Hashmap
            // Map<String, Integer> hm1 = sortByValue(WelcomePage.scores);
            // for (Map.Entry<String, Integer> en : hm1.entrySet()) {
            // System.out.println(en.getKey() +
            // ", " + en.getValue());
            // message += en.getKey() + "\t\t" + en.getValue() +"s\n";
            // }

            JTextArea textLabel = new JTextArea(message);
            textLabel.setBackground(BG_COLOR);
            textLabel.setForeground(darkerColor);
            JButton newGamebtn = new JButton("New Game");
            newGamebtn.setBorder(null);
            newGamebtn.setOpaque(true);
            newGamebtn.setBackground(darkerColor);
            newGamebtn.setForeground(BG_COLOR);
            newGamebtn.addActionListener(
                    new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            newGame(SudokuMain.frame);
                            dialog.dispose();
                        }
                    });
            dialog.getContentPane().add(label, BorderLayout.CENTER);
            dialog.setPreferredSize(
                    new Dimension(icon.getIconWidth(), icon.getIconHeight()));
            dialog.pack();
            dialog.setResizable(false);
            dialog.setLocationRelativeTo(null);
            dialog.setVisible(true);

            // Closes the dialog when player clicks outside the dialog window
            dialog.addWindowFocusListener(
                    new WindowFocusListener() {
                        @Override
                        public void windowGainedFocus(WindowEvent e) {
                            dialog.addKeyListener(
                                    new KeyListener() {
                                        public void keyPressed(KeyEvent e) {
                                            JPanel panel = (JPanel) dialog.getContentPane();

                                            panel.setBorder(new EmptyBorder(10, 10, 10, 10));
                                            panel.setBackground(BG_COLOR);
                                            panel.remove(label);
                                            panel.add(textLabel, BorderLayout.CENTER);
                                            panel.add(newGamebtn, BorderLayout.SOUTH);
                                            panel.revalidate();
                                            panel.repaint();
                                        }

                                        @Override
                                        public void keyTyped(KeyEvent e) {
                                        }

                                        @Override
                                        public void keyReleased(KeyEvent e) {
                                        }
                                    });
                        }

                        @Override
                        public void windowLostFocus(WindowEvent e) {
                            dialog.dispose();
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Game Over!");
        }
    }


    // public void callAnime(){
    // paintComponent(g);
    // img = loadAnimeImage();

    // }

    // public void repaintComponent(Graphics g){
    // super.paintComponent(g);

    // img = loadAnimeImage();
    // if (img != null) {
    // g.drawImage(img, 0, 0, getWidth(), getHeight(), this);
    // }
    // }
}
