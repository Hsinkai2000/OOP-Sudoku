package sudoku;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.swing.JTextField;

/**
 * The Cell class model the cells of the Sudoku puzzle, by customizing
 * (subclass)
 * the javax.swing.JTextField to include row/column, puzzle number and status.
 */
public class Cell extends JTextField {
    private static final long serialVersionUID = 1L; // to prevent serial warning
    Color[] colors = new Color[]{
        new Color(227, 52, 47), 
        new Color(246, 153, 63), 
        new Color(195, 137, 200), 
        new Color(56, 193, 114), 
        new Color(77, 192, 181), 
        new Color(52, 144, 220), 
        new Color(101, 116, 205), 
        new Color(149, 97, 226), 
        new Color(246, 109, 155)
    };
    // Define named constants for JTextField's colors and fonts
    // to be chosen based on CellStatus
    public static final Color BG_GIVEN = new Color(215, 32, 35, 0); // RGB
    public static final Color FG_GIVEN = Color.red;
    public static final Color FG_NOT_GIVEN = Color.GREEN;
    public static final Color BG_TO_GUESS = new Color(215, 32, 35, 0);
    public static final Color BG_CORRECT_GUESS = new Color(0, 216, 0);
    public static final Color BG_WRONG_GUESS = new Color(255, 171, 171);
    public static final Color BG_PENDING = new Color(110, 180, 255);
    public static final Color BG_WRONG_HINT = new Color(246, 140, 112);
    public static final Font FONT_NUMBERS = new Font("OCR A Extended", Font.PLAIN, 28);
    public static final Color BG_HINT = new Color(149, 125, 173);

    // Define properties (package-visible)
    /** The row and column number [0-8] of this cell */
    int row, col;
    /** The puzzle number [1-9] for this cell */
    int number;
    /** The status of this cell defined in enum CellStatus */
    CellStatus status;

    /** Constructor */
    public Cell(int row, int col, CellStatus status) {
        super(); // JTextField
        this.row = row;
        this.col = col;
        this.status = status;
        // Inherited from JTextField: Beautify all the cells once for all
        // loadFont();
        
        // URL fontUrl = getClass().getResource("./Font/Alice-Smile.ttf");
        URL fontUrl = getClass().getResource("./Font/gemstone/Gemstone.ttf");
        Font customFont;
        try {
            customFont = Font.createFont(Font.PLAIN, fontUrl.openStream()).deriveFont(25f);
            
            super.setFont(customFont);
        } catch (FontFormatException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        super.setHorizontalAlignment(JTextField.CENTER);
        super.setOpaque(false);
    }

    /** Reset this cell for a new game, given the puzzle number and isGiven */
    public void newGame(int number, boolean isGiven) {
        this.number = number;
        status = isGiven ? CellStatus.GIVEN : CellStatus.TO_GUESS;

        this.setOpaque(false);
        setnumberColour();
        paint(); // paint itself
    }

    @Override
    public void setText(String text) {
        super.setText(text);
    }

    /** This Cell (JTextField) paints itself based on its status */
    public void paint() {
        if (status == CellStatus.GIVEN) {
            // Inherited from JTextField: Set display properties
            super.setText(number + "");
            super.setEditable(false);
            super.setBackground(BG_GIVEN);
            // super.setForeground(FG_GIVEN);
            super.setForeground(colors[number-1]);
        } else if (status == CellStatus.TO_GUESS) {
            // Inherited from JTextField: Set display properties
            super.setText("");
            super.setEditable(true);
            super.setBackground(BG_TO_GUESS);
            super.setForeground(FG_NOT_GIVEN);
        } else if (status == CellStatus.CORRECT_GUESS) { // from TO_GUESS
            super.setBackground(BG_CORRECT_GUESS);
            super.setForeground(colors[number-1]);
        } else if (status == CellStatus.WRONG_GUESS) { // from TO_GUESS
            super.setBackground(BG_WRONG_GUESS);
        } else if (status == CellStatus.PENDING){
            super.setBackground(BG_PENDING);
            super.setForeground(Color.WHITE);
        } else if (status == CellStatus.WRONG_HINT){
            super.setBackground(BG_WRONG_HINT);
            super.setForeground(Color.WHITE);
        }
        else if (status == CellStatus.HINT){
            super.setBackground(BG_HINT);
            super.setForeground(Color.WHITE);
        }
    }

    private void setnumberColour(){
        switch (this.number) {
            case 1:
                super.setForeground(colors[0]);
                break;
            case 2:
                super.setForeground(colors[1]);
                break;
            case 3:
                super.setForeground(colors[2]);
                break;
            case 4:
                super.setForeground(colors[3]);
                break;
            case 5:
                super.setForeground(colors[4]);
                break;
            case 6:
                super.setForeground(colors[5]);
                break;
            case 7:
                super.setForeground(colors[6]);
                break;
            case 8:
                super.setForeground(colors[7]);
                break;
            case 9:
                super.setForeground(colors[8]);
                break;
            default:
                break;
        }
        
    }

}