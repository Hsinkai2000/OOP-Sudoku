package sudoku;

public enum CellStatus {
   GIVEN,
   TO_GUESS,
   CORRECT_GUESS,
   WRONG_GUESS,
   PENDING,
   WRONG_HINT,
   HINT
}