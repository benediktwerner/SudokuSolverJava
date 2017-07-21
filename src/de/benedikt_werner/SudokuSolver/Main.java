package de.benedikt_werner.SudokuSolver;

import org.opencv.core.Core;

public class Main {
    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        SudokuDetector.detectSudoku("sudoku.jpg");
    }
}
