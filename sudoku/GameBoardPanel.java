package sudoku;

import java.awt.*;
import java.awt.event.*;
import java.time.Duration;
import java.time.Instant;

import java.awt.geom.Line2D;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.*;

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
                cells[row][col].newGame(puzzle.numbers[row][col], puzzle.isGiven[row][col]);
            }
        }

        CellKeyListener listener = new CellKeyListener();
        int count = 1;
        for (int row = 0; row < GRID_SIZE; ++row) {
            for (int col = 0; col < GRID_SIZE; ++col) {
                if (cells[row][col].status == CellStatus.TO_GUESS && cells[row][col].isEditable()) {
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

        Instant instantStart = Instant.now();
        stepTimer = new Timer(STEP_IN_MSEC, e -> stepGame(instantStart));
        stepTimer.start();
    }

    private void stepGame(Instant instantStart) {
        Instant instantStop = Instant.now();
        ElapsedTime = Duration.between(instantStart, instantStop);
        SudokuMain.time.setText("Time: " + ElapsedTime.toSeconds());

    }

    public void resetGame() {
        // Initialize all the 9x9 cells, based on the puzzle.
        for (int row = 0; row < GRID_SIZE; ++row) {
            for (int col = 0; col < GRID_SIZE; ++col) {
                cells[row][col].newGame(puzzle.numbers[row][col], puzzle.isGiven[row][col]);
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
                if (cells[row][col].status == CellStatus.TO_GUESS || cells[row][col].status == CellStatus.WRONG_GUESS) {
                    return false;
                }
            }
        }
        return true;
    }

    public void reverseHighlight(){
        for (int row = 0; row < GRID_SIZE; ++row) {
            for (int col = 0; col < GRID_SIZE; ++col) {
                if (cells[row][col].status == CellStatus.WRONG_HINT || cells[row][col].status == CellStatus.PENDING){
                    cells[row][col].status = CellStatus.GIVEN;
                    cells[row][col].paint();
                }
            }
        }
    }

    public void applyPending(int numberIn){
        for (int row = 0; row < GRID_SIZE; ++row) {
            for (int col = 0; col < GRID_SIZE; ++col) {
                if(puzzle.numbers[row][col] == numberIn){
                    if(cells[row][col].status == CellStatus.GIVEN){
                        cells[row][col].status = CellStatus.PENDING;
                        cells[row][col].paint();
                    }
                }
            }
        }
    }

    // Check for same row, column and box
    public void applyWrongHint(int numberIn, int cellRow, int cellCol){
        for (int row = 0; row < GRID_SIZE; ++row) {
            if(puzzle.numbers[row][cellCol] == numberIn && cells[row][cellCol].status == CellStatus.GIVEN){
            // if(puzzle.numbers[row][cellCol] == numberIn && cells[row][cellCol].status == CellStatus.GIVEN){
                    cells[row][cellCol].status = CellStatus.WRONG_HINT;
                cells[row][cellCol].paint();
            }
        }
        for (int col = 0; col < GRID_SIZE; ++col) {
            if(puzzle.numbers[cellRow][col] == numberIn && cells[cellRow][col].status == CellStatus.GIVEN){
                cells[cellRow][col].status = CellStatus.WRONG_HINT;
                cells[cellRow][col].paint();
            }
        }

        // For col 0,3,6
        if (cellCol % 3 == 0){
            if (cellRow % 3 == 0){
                if(puzzle.numbers[cellRow+1][cellCol+1] == numberIn && cells[cellRow+1][cellCol+1].status == CellStatus.GIVEN){
                    cells[cellRow+1][cellCol+1].status = CellStatus.WRONG_HINT;
                    cells[cellRow+1][cellCol+1].paint();
                }
                if(puzzle.numbers[cellRow+2][cellCol+1] == numberIn && cells[cellRow+2][cellCol+1].status == CellStatus.GIVEN){
                    cells[cellRow+2][cellCol+1].status = CellStatus.WRONG_HINT;
                    cells[cellRow+2][cellCol+1].paint();
                }
                if(puzzle.numbers[cellRow+1][cellCol+2] == numberIn && cells[cellRow+1][cellCol+2].status == CellStatus.GIVEN){
                    cells[cellRow+1][cellCol+2].status = CellStatus.WRONG_HINT;
                    cells[cellRow+1][cellCol+2].paint();
                }
                if(puzzle.numbers[cellRow+2][cellCol+2] == numberIn && cells[cellRow+2][cellCol+2].status == CellStatus.GIVEN){
                    cells[cellRow+2][cellCol+2].status = CellStatus.WRONG_HINT;
                    cells[cellRow+2][cellCol+2].paint();
                }
            } else if (cellRow % 3 == 1){
                if(puzzle.numbers[cellRow+1][cellCol+1] == numberIn && cells[cellRow+1][cellCol+1].status == CellStatus.GIVEN){
                    cells[cellRow+1][cellCol+1].status = CellStatus.WRONG_HINT;
                    cells[cellRow+1][cellCol+1].paint();
                }
                if(puzzle.numbers[cellRow-1][cellCol+1] == numberIn && cells[cellRow-1][cellCol+1].status == CellStatus.GIVEN){
                    cells[cellRow-1][cellCol+1].status = CellStatus.WRONG_HINT;
                    cells[cellRow-1][cellCol+1].paint();
                }
                if(puzzle.numbers[cellRow+1][cellCol+2] == numberIn && cells[cellRow+1][cellCol+2].status == CellStatus.GIVEN){
                    cells[cellRow+1][cellCol+2].status = CellStatus.WRONG_HINT;
                    cells[cellRow+1][cellCol+2].paint();
                }
                if(puzzle.numbers[cellRow-1][cellCol+2] == numberIn && cells[cellRow-1][cellCol+2].status == CellStatus.GIVEN){
                    cells[cellRow-1][cellCol+2].status = CellStatus.WRONG_HINT;
                    cells[cellRow-1][cellCol+2].paint();
                }
            } else {
                if(puzzle.numbers[cellRow-2][cellCol+1] == numberIn && cells[cellRow-2][cellCol+1].status == CellStatus.GIVEN){
                    cells[cellRow-2][cellCol+1].status = CellStatus.WRONG_HINT;
                    cells[cellRow-2][cellCol+1].paint();
                }
                if(puzzle.numbers[cellRow-1][cellCol+1] == numberIn && cells[cellRow-1][cellCol+1].status == CellStatus.GIVEN){
                    cells[cellRow-1][cellCol+1].status = CellStatus.WRONG_HINT;
                    cells[cellRow-1][cellCol+1].paint();
                }
                if(puzzle.numbers[cellRow-2][cellCol+2] == numberIn && cells[cellRow-2][cellCol+2].status == CellStatus.GIVEN){
                    cells[cellRow-2][cellCol+2].status = CellStatus.WRONG_HINT;
                    cells[cellRow-2][cellCol+2].paint();
                }
                if(puzzle.numbers[cellRow-1][cellCol+2] == numberIn && cells[cellRow-1][cellCol+2].status == CellStatus.GIVEN){
                    cells[cellRow-1][cellCol+2].status = CellStatus.WRONG_HINT;
                    cells[cellRow-1][cellCol+2].paint();
                }
            }
        } else if (cellCol % 3 == 1){
            // For col 1,4,7
            if (cellRow % 3 == 0){
                if(puzzle.numbers[cellRow+1][cellCol+1] == numberIn && cells[cellRow+1][cellCol+1].status == CellStatus.GIVEN){
                    cells[cellRow+1][cellCol+1].status = CellStatus.WRONG_HINT;
                    cells[cellRow+1][cellCol+1].paint();
                }
                if(puzzle.numbers[cellRow+2][cellCol+1] == numberIn && cells[cellRow+2][cellCol+1].status == CellStatus.GIVEN){
                    cells[cellRow+2][cellCol+1].status = CellStatus.WRONG_HINT;
                    cells[cellRow+2][cellCol+1].paint();
                }
                if(puzzle.numbers[cellRow+1][cellCol-1] == numberIn && cells[cellRow+1][cellCol-1].status == CellStatus.GIVEN){
                    cells[cellRow+1][cellCol-1].status = CellStatus.WRONG_HINT;
                    cells[cellRow+1][cellCol-1].paint();
                }
                if(puzzle.numbers[cellRow+2][cellCol-1] == numberIn && cells[cellRow+2][cellCol-1].status == CellStatus.GIVEN){
                    cells[cellRow+2][cellCol-1].status = CellStatus.WRONG_HINT;
                    cells[cellRow+2][cellCol-1].paint();
                }
            } else if (cellRow % 3 == 1) {
                if(puzzle.numbers[cellRow+1][cellCol+1] == numberIn && cells[cellRow+1][cellCol+1].status == CellStatus.GIVEN){
                    cells[cellRow+1][cellCol+1].status = CellStatus.WRONG_HINT;
                    cells[cellRow+1][cellCol+1].paint();
                }
                if(puzzle.numbers[cellRow-1][cellCol+1] == numberIn && cells[cellRow-1][cellCol+1].status == CellStatus.GIVEN){
                    cells[cellRow-1][cellCol+1].status = CellStatus.WRONG_HINT;
                    cells[cellRow-1][cellCol+1].paint();
                }
                if(puzzle.numbers[cellRow+1][cellCol-1] == numberIn && cells[cellRow+1][cellCol-1].status == CellStatus.GIVEN){
                    cells[cellRow+1][cellCol-1].status = CellStatus.WRONG_HINT;
                    cells[cellRow+1][cellCol-1].paint();
                }
                if(puzzle.numbers[cellRow-1][cellCol-1] == numberIn && cells[cellRow-1][cellCol-1].status == CellStatus.GIVEN){
                    cells[cellRow-1][cellCol-1].status = CellStatus.WRONG_HINT;
                    cells[cellRow-1][cellCol-1].paint();
                }
            } else {
                if(puzzle.numbers[cellRow-2][cellCol+1] == numberIn && cells[cellRow-2][cellCol+1].status == CellStatus.GIVEN){
                    cells[cellRow-2][cellCol+1].status = CellStatus.WRONG_HINT;
                    cells[cellRow-2][cellCol+1].paint();
                }
                if(puzzle.numbers[cellRow-1][cellCol+1] == numberIn && cells[cellRow-1][cellCol+1].status == CellStatus.GIVEN){
                    cells[cellRow-1][cellCol+1].status = CellStatus.WRONG_HINT;
                    cells[cellRow-1][cellCol+1].paint();
                }
                if(puzzle.numbers[cellRow-2][cellCol-1] == numberIn && cells[cellRow-2][cellCol-1].status == CellStatus.GIVEN){
                    cells[cellRow-2][cellCol-1].status = CellStatus.WRONG_HINT;
                    cells[cellRow-2][cellCol-1].paint();
                }
                if(puzzle.numbers[cellRow-1][cellCol-1] == numberIn && cells[cellRow-1][cellCol-1].status == CellStatus.GIVEN){
                    cells[cellRow-1][cellCol-1].status = CellStatus.WRONG_HINT;
                    cells[cellRow-1][cellCol-1].paint();
                }
            }
        } else {
            // For col 2,5,8
            if (cellRow % 3 == 0){
                if(puzzle.numbers[cellRow+1][cellCol-2] == numberIn && cells[cellRow+1][cellCol-2].status == CellStatus.GIVEN){
                    cells[cellRow+1][cellCol-2].status = CellStatus.WRONG_HINT;
                    cells[cellRow+1][cellCol-2].paint();
                }
                if(puzzle.numbers[cellRow+2][cellCol-2] == numberIn && cells[cellRow+2][cellCol-2].status == CellStatus.GIVEN){
                    cells[cellRow+2][cellCol-2].status = CellStatus.WRONG_HINT;
                    cells[cellRow+2][cellCol-2].paint();
                }
                if(puzzle.numbers[cellRow+1][cellCol-1] == numberIn && cells[cellRow+1][cellCol-1].status == CellStatus.GIVEN){
                    cells[cellRow+1][cellCol-1].status = CellStatus.WRONG_HINT;
                    cells[cellRow+1][cellCol-1].paint();
                }
                if(puzzle.numbers[cellRow+2][cellCol-1] == numberIn && cells[cellRow+2][cellCol-1].status == CellStatus.GIVEN){
                    cells[cellRow+2][cellCol-1].status = CellStatus.WRONG_HINT;
                    cells[cellRow+2][cellCol-1].paint();
                }
            } else if (cellRow % 3 == 1){
                if(puzzle.numbers[cellRow+1][cellCol-2] == numberIn && cells[cellRow+1][cellCol-2].status == CellStatus.GIVEN){
                    cells[cellRow+1][cellCol-2].status = CellStatus.WRONG_HINT;
                    cells[cellRow+1][cellCol-2].paint();
                }
                if(puzzle.numbers[cellRow-1][cellCol-2] == numberIn && cells[cellRow-1][cellCol-2].status == CellStatus.GIVEN){
                    cells[cellRow-1][cellCol-2].status = CellStatus.WRONG_HINT;
                    cells[cellRow-1][cellCol-2].paint();
                }
                if(puzzle.numbers[cellRow+1][cellCol-1] == numberIn && cells[cellRow+1][cellCol-1].status == CellStatus.GIVEN){
                    cells[cellRow+1][cellCol-1].status = CellStatus.WRONG_HINT;
                    cells[cellRow+1][cellCol-1].paint();
                }
                if(puzzle.numbers[cellRow-1][cellCol-1] == numberIn && cells[cellRow-1][cellCol-1].status == CellStatus.GIVEN){
                    cells[cellRow-1][cellCol-1].status = CellStatus.WRONG_HINT;
                    cells[cellRow-1][cellCol-1].paint();
                }
            } else {
                if(puzzle.numbers[cellRow-2][cellCol-2] == numberIn && cells[cellRow-2][cellCol-2].status == CellStatus.GIVEN){
                    cells[cellRow-2][cellCol-2].status = CellStatus.WRONG_HINT;
                    cells[cellRow-2][cellCol-2].paint();
                }
                if(puzzle.numbers[cellRow-1][cellCol-2] == numberIn && cells[cellRow-1][cellCol-2].status == CellStatus.GIVEN){
                    cells[cellRow-1][cellCol-2].status = CellStatus.WRONG_HINT;
                    cells[cellRow-1][cellCol-2].paint();
                }
                if(puzzle.numbers[cellRow-2][cellCol-1] == numberIn && cells[cellRow-2][cellCol-1].status == CellStatus.GIVEN){
                    cells[cellRow-2][cellCol-1].status = CellStatus.WRONG_HINT;
                    cells[cellRow-2][cellCol-1].paint();
                }
                if(puzzle.numbers[cellRow-1][cellCol-1] == numberIn && cells[cellRow-1][cellCol-1].status == CellStatus.GIVEN){
                    cells[cellRow-1][cellCol-1].status = CellStatus.WRONG_HINT;
                    cells[cellRow-1][cellCol-1].paint();
                }
            }
        }
    }

    private class CellKeyListener implements KeyListener {
        @Override
        public void keyTyped(KeyEvent e) {
            // Get a reference of the JTextField that triggers this action event
            sourceCell = (Cell) e.getSource();
            char in = e.getKeyChar();
            if (Character.isDigit(in)
                    || Character.getNumericValue(in) == -1) {
                // Retrieve the int entered
                int numberIn = Character.getNumericValue(in);

                // For debugging
                System.out.println("You entered " + numberIn);
                System.out.println("cellnum: " + sourceCell.number);
                reverseHighlight();
                if (sourceCell.status != CellStatus.GIVEN) {
                    // check if number entered is correct or wrong
                    if (numberIn == 0){
                        haveAnime = true;
                        System.out.println("0 has been pressed!");

                    }
                    if (numberIn == sourceCell.number) {
                        sourceCell.status = CellStatus.CORRECT_GUESS;
                        System.out.println(isSolved());
                        score += 100;
                        blanksLeft--;
                        SudokuMain.remaining.setText("Remaining: " + blanksLeft);
                        applyPending(numberIn);
                        // System.out.println("Column is: " + sourceCell.col);
                    } else {
                        sourceCell.status = CellStatus.WRONG_GUESS;
                        applyWrongHint(numberIn, sourceCell.row, sourceCell.col);
                    }
                    sourceCell.paint(); // re-paint this cell based on its status
                    SudokuMain.score.setText("Score: " + score);

                    if (isSolved()) {
                        System.out.println("solved");
                        WelcomePage.addToScoreboard(score);
                        stepTimer.stop();
                        displayPopup("Congratulation! You WON!\nScore: " + score + "\nTime: " + ElapsedTime.toSeconds()
                                + " seconds\n\nLeaderboard:\n");

                    }
                }

            } else {
                displayPopup("Please enter a number");
            }

        }

        private void displayPopup(String message) {
            // JOptionPane.showMessageDialog(null, message);
            // JFrame frame = SudokuMain.frame;
            // JLabel label = new JLabel("Congrats");
            // frame.add(label, BorderLayout.CENTER);
            // System.out.println("Supposed to show");
            // SwingUtilities.updateComponentTreeUI(frame);

            for (int i = 0; i < WelcomePage.nameList.size(); i++) {
                message += WelcomePage.nameList.get(i) + "\t\t" + WelcomePage.pointList.get(i) + "\n";
            }   
            JOptionPane.showMessageDialog(null, message, "Scoreboard", JOptionPane.INFORMATION_MESSAGE);

        }

        @Override
        public void keyPressed(KeyEvent e) {
        }

        @Override
        public void keyReleased(KeyEvent e) {
        }
    }

    public static void setPuzzleDifficulty(int level) {
        difficulty = level;
    }

    // @Override
    // public void paintComponent(Graphics g) {
    //     super.paintComponent(g);

    //     img = loadImage();
    //     if (img != null) {
    //         g.drawImage(img, 0, 0, getWidth(), getHeight(), this);
    //     }
    // }

    @Override
    public void paintComponent(Graphics g){
        // super.paintComponent(g);
        super.paintComponent(g);

        if (haveAnime){
            img =loadAnimeImage();
        } else {
            img =loadImage();
        }
        // img = loadAnimeImage();
        if (img != null) {
            g.drawImage(img, 0, 0, getWidth(), getHeight(), this);
        }
        System.out.println("Reached paint");
    }

    public Image loadImage() {
        // URL imgUrl = getClass().getResource("./images/background-image-copy.png");
        URL imgUrl = getClass().getResource("./images/background-image2.png");
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

    // public void callAnime(){
    //     paintComponent(g);
    //     img = loadAnimeImage();
    
    // }

    // public void repaintComponent(Graphics g){
    //     super.paintComponent(g);

    //     img = loadAnimeImage();
    //     if (img != null) {
    //         g.drawImage(img, 0, 0, getWidth(), getHeight(), this);
    //     }
    // }
}
