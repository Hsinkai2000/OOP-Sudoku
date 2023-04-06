package sudoku;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.FocusTraversalPolicy;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class SudokuFocusTraversalPolicy extends FocusTraversalPolicy {
    private Cell[][] cells;
    private int currRow;
    private int currCol;

    public SudokuFocusTraversalPolicy(Cell[][] cells) {
        this.cells = cells;
    }

    @Override
    public Component getComponentAfter(Container aContainer, Component aComponent) {
        int nextRow = -1, nextCol = -1;
        Cell prevCell = (Cell) aComponent;
        System.out.println("prevCell: " + prevCell.row + " " + prevCell.col);
        
        for (int row = 0; row < 9; row++){
            if(prevCell.row == row){//when same row, compare column
                if (prevCell.col == 8) {
                    continue;
                }
                else{
                    for (int col = prevCell.col+1; col < 9; col++) {
                        if (cells[row][col].status == CellStatus.TO_GUESS || cells[row][col].status == CellStatus.WRONG_GUESS) {
                            if (nextRow == -1 || nextCol == -1) {
                                nextRow=row;
                                nextCol=col;                                
                            }
                        }
                    }
                }
            }
            else if(row>prevCell.row){
                for (int col = 0; col < 9; col++) {
                    if (cells[row][col].status == CellStatus.TO_GUESS || cells[row][col].status == CellStatus.WRONG_GUESS) {
                        if (nextRow == -1 || nextCol == -1) {
                            nextRow=row;
                            nextCol=col;                                
                        }
                    }
                }
            }
        }

        // Return the next cell or wrap to the first one
        if (nextRow != -1 && nextCol != -1) {
            return cells[nextRow][nextCol];
        } else {
            return getFirstComponent(aContainer);
        }
    }

    @Override
    public Component getComponentBefore(Container aContainer, Component aComponent) {
        int nextRow = -1, nextCol = -1;
        Cell prevCell = (Cell) aComponent;
        System.out.println("prevCell: " + prevCell.row + " " + prevCell.col);
        
        for (int row = 8; row >= 0; row--){
            if (row == prevCell.row) {//when same row, compare column
                if (prevCell.col == 0) {
                    continue;
                }
                else{
                    for (int col = prevCell.col-1; col >= 0; col--) {
                        if (cells[row][col].status == CellStatus.TO_GUESS || cells[row][col].status == CellStatus.WRONG_GUESS) {
                            if (nextRow == -1 || nextCol == -1) {
                                nextRow=row;
                                nextCol=col;                                
                            }
                        }
                    }
                }
            }else if (row < prevCell.row){
                for (int col = 8; col >=0; col--) {
                    if (cells[row][col].status == CellStatus.TO_GUESS || cells[row][col].status == CellStatus.WRONG_GUESS) {
                        if (nextRow == -1 || nextCol == -1) {
                            nextRow=row;
                            nextCol=col;                                
                        }
                    }
                }
            }
        }
        // Return the next cell or wrap to the first one
        if (nextRow != -1 && nextCol != -1) {
            return cells[nextRow][nextCol];
        } else {
            return getLastComponent(aContainer);
        }
    }

    @Override
    public Component getFirstComponent(Container aContainer) {
        int nextRow = -1, nextCol = -1;

        // Find the next editable cell
        for (int row = 0; row < cells.length; row++) {
            if (nextRow == -1) {
                for (int col = 0; col < cells[row].length; col++) {
                    if (nextRow == -1) {
                        if (cells[row][col].status == CellStatus.TO_GUESS || cells[row][col].status == CellStatus.WRONG_GUESS) {
                            if (nextRow == -1 || nextCol == -1) {
                                nextRow = row;
                                nextCol = col;
                            }
                        }
                    }
                }
            }
        }

        return cells[nextRow][nextCol];
    }

    @Override
    public Component getLastComponent(Container aContainer) {
        int nextRow = -1, nextCol = -1;

        // Find the next editable cell
        for (int row = 8; row >= 0; row--) {
            for (int col = 8; col >= 0; col--) {
                if (cells[row][col].status == CellStatus.TO_GUESS || cells[row][col].status == CellStatus.WRONG_GUESS) {
                    if (nextRow == -1 || nextCol == -1) {
                        nextRow = row;
                        nextCol = col;
                    }
                }
            }
        }

        return cells[nextRow][nextCol];
    }

    @Override
    public Component getDefaultComponent(Container aContainer) {
        int nextRow = -1, nextCol = -1;

        // Find the next editable cell
        for (int row = 0; row < cells.length; row++) {
            if (nextRow == -1) {
                for (int col = 0; col < cells[row].length; col++) {
                    if (nextRow == -1) {
                        if (cells[row][col].status == CellStatus.TO_GUESS && cells[row][col].isEditable()) {
                            if (nextRow == -1 || nextCol == -1) {
                                nextRow = row;
                                nextCol = col;
                            }
                        }
                    }
                }
            }
        }

        return cells[nextRow][nextCol];
    }

    // Implement the other methods of the FocusTraversalPolicy interface
    // (getComponentBefore, getFirstComponent, getLastComponent,
    // getDefaultComponent)
    // to provide full support for focus traversal.
    // ...
}
