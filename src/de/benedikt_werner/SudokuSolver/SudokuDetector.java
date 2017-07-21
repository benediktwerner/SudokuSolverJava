package de.benedikt_werner.SudokuSolver;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class SudokuDetector {
    public static SudokuBoard detectSudoku(String imagePath) {
        Mat sudoku = Imgcodecs.imread(imagePath, Imgcodecs.IMREAD_GRAYSCALE);
        if (sudoku.empty())
            return null;

        threshold(sudoku);
        removeBorder(sudoku);
        showImage(sudoku);

        return null;
    }

    private static void threshold(Mat sudoku) {
        Imgproc.GaussianBlur(sudoku, sudoku, new Size(11, 11), 0);
        Imgproc.adaptiveThreshold(sudoku, sudoku, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY_INV, 11, 2);
    }

    private static void removeBorder(Mat sudoku) {
        MatOfPoint2f contour = findContours(sudoku);
        sudoku = extractSudoku(sudoku, contour);
        showImage(sudoku);
    }

    private static MatOfPoint2f findContours(Mat sudoku) {
        List<MatOfPoint> contours = new ArrayList<>();
        Imgproc.findContours(sudoku.clone(), contours, new Mat(), Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);

        // Assume board covers at least one quarter of the screen
        double maxArea = Math.pow(Math.min(sudoku.width(), sudoku.height()) / 2, 2);
        MatOfPoint2f maxContour = null;

        for (MatOfPoint contour : contours) {
            double area = Imgproc.contourArea(contour);
            if (area > maxArea) {
                MatOfPoint2f approxCurve = new MatOfPoint2f();
                MatOfPoint2f contour2f = new MatOfPoint2f(contour.toArray());
                double arcLength = Imgproc.arcLength(contour2f, true);
                Imgproc.approxPolyDP(contour2f, approxCurve, 0.02 * arcLength, true);

                if (approxCurve.height() == 4) { // If contour is rectangle
                    maxArea = area;
                    maxContour = approxCurve;
                }
            }
        }
        return maxContour;
    }

    private static Mat extractSudoku(Mat sudoku, MatOfPoint2f contour) {
        MatOfPoint2f contourRectified = rectifyContour(contour);
        MatOfPoint2f h = new MatOfPoint2f(
                new Point(0, 0),
                new Point(449, 0),
                new Point(449, 449),
                new Point(0, 449));
        Mat transform = Imgproc.getPerspectiveTransform(contourRectified, h);
        Mat result = new Mat();
        Imgproc.warpPerspective(sudoku, result, transform, new Size(450, 450));
        return result;
    }

    /**
     * Order points in the contour matrix to represent a rectangle. Order: topLeft topRight bottomRight bottomLeft (= clockwise)
     */
    private static MatOfPoint2f rectifyContour(MatOfPoint2f contour) {
        contour.reshape(4, 2);
        List<Point> contourList = Arrays.asList(contour.toArray());

        Point[] points = new Point[4];
        points[0] = Collections.min(contourList, (a, b) -> Double.compare(a.x + a.y, b.x + b.y));
        points[2] = Collections.max(contourList, (a, b) -> Double.compare(a.x + a.y, b.x + b.y));

        points[1] = Collections.max(contourList, (a, b) -> Double.compare(a.x - a.y, b.x - b.y));
        points[3] = Collections.min(contourList, (a, b) -> Double.compare(a.x - a.y, b.x - b.y));

        return new MatOfPoint2f(points);
    }

    private static void showImage(Mat m) {
        int type = BufferedImage.TYPE_BYTE_GRAY;
        if (m.channels() > 1)
            type = BufferedImage.TYPE_3BYTE_BGR;

        int bufferSize = m.channels() * m.cols() * m.rows();
        byte[] b = new byte[bufferSize];
        m.get(0, 0, b); // get all the pixels
        BufferedImage image = new BufferedImage(m.cols(), m.rows(), type);
        final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        System.arraycopy(b, 0, targetPixels, 0, b.length);

        JFrame frame = new JFrame();
        frame.getContentPane().add(new JLabel(new ImageIcon(image)));
        frame.pack();
        frame.setVisible(true);
    }
}
