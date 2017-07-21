package de.benedikt_werner.SudokuSolver;

import java.util.Set;

public class SudokuSolver {
    public boolean findSolution(SudokuBoard board) {
        return findSolution(board, 0);
    }

    private boolean findSolution(SudokuBoard board, int cellIndex) {
        if (cellIndex == SudokuBoard.SIZE * SudokuBoard.SIZE)
            return true;

        int row = cellIndex / SudokuBoard.SIZE;
        int column = cellIndex % SudokuBoard.SIZE;

        if (board.isCellFilled(row, column))
            return findSolution(board, cellIndex + 1);

        Set<Integer> optionsForCell = board.getOptionsForCell(row, column);
        for (int option : optionsForCell) {
            board.setCell(row, column, option);
            if (findSolution(board, cellIndex + 1))
                return true;
        }

        board.setCell(row, column, 0);
        return false;
    }
}
