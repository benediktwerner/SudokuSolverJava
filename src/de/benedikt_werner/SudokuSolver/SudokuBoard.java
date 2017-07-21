package de.benedikt_werner.SudokuSolver;

import java.util.HashSet;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SudokuBoard {
    public static final int SIZE = 9;

    private int[][] board;

    public SudokuBoard(int[][] board) {
        this.board = board;
    }

    public HashSet<Integer> getOptionsForCell(int row, int column) {
        HashSet<Integer> options = IntStream
                .rangeClosed(1, SIZE)
                .boxed()
                .collect(Collectors.toCollection(HashSet::new));
        removeOptionsUsedInRow(options, row);
        removeOptionsUsedInColumn(options, column);
        removeOptionsUsedInBox(options, row, column);
        return options;
    }

    private void removeOptionsUsedInBox(HashSet<Integer> options, int row, int column) {
        int boxRow = row - row % 3;
        int boxColumn = column - column % 3;
        for (int rowIndex = 0; rowIndex < 3; rowIndex++)
            for (int columnIndex = 0; columnIndex < 3; columnIndex++)
                options.remove(board[boxRow + rowIndex][boxColumn + columnIndex]);
    }

    private void removeOptionsUsedInColumn(HashSet<Integer> options, int column) {
        for (int row = 0; row < SIZE; row++)
            options.remove(board[row][column]);
    }

    private void removeOptionsUsedInRow(HashSet<Integer> options, int row) {
        for (int column = 0; column < SIZE; column++)
            options.remove(board[row][column]);
    }

    public boolean isCellFilled(int row, int column) {
        return board[row][column] != 0;
    }

    public void setCell(int row, int column, int value) {
        this.board[row][column] = value;
    }

    public int getCell(int row, int column) {
        return board[row][column];
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (int y = 0; y < board.length; y++) {
            for (int x = 0; x < board[0].length; x++) {
                result.append(board[y][x] != 0 ? String.valueOf(board[y][x]) : " ");
                if (x == 2 || x == 5) result.append(" | ");
            }

            result.append("\n");
            if (y == 2 || y == 5) result.append("---------------\n");
        }
        return result.toString();
    }
}
