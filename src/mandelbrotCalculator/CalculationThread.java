package mandelbrotCalculator;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class CalculationThread extends Thread {

    int threadNumber;
    int threadAmount;
    int frameAmount;
    int width;
    int height;
    int iterations;
    int threshold;
    double[][] zoomValues;
    CNumber[][] samples;

    public void run() {

        long totalStartTime = System.currentTimeMillis();

        samples = new CNumber[width][height];

        for (int frameCounter = threadNumber; frameCounter < frameAmount; frameCounter += threadAmount) {

            long startTime = System.currentTimeMillis();

            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            File f = null;

            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    samples[i][j] = new CNumber(); // fill the array
                    samples[i][j].setReal(zoomValues[2][frameCounter] + zoomValues[0][frameCounter] * i); // calculate the position on the real numberline
                    samples[i][j].setImag(zoomValues[3][frameCounter] - zoomValues[1][frameCounter] * j); // calculate the position on the imaginary numberline
                }
            }

            // calculate values
            double[][] values = new double[width][height]; // new array of booleans for the drawing
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    values[i][j] = checkMandelbrot(samples[i][j], iterations, threshold); // check if the number is inside of th set
                }
            }

            createImage(image, frameCounter, width, height, values, iterations);

            long frameTime = System.currentTimeMillis() - startTime;
            System.out.println("------------------------Frame " + frameCounter + " finished in " + ((double) frameTime / 1000) + "s------------------------");

        }

        long totalTime = System.currentTimeMillis() - totalStartTime;
        System.out.println("Thread " + threadNumber + " completed. Process took " + ((double) totalTime / 1000) + "s");

    }

    public CalculationThread(int number, int threads, int frames, int widthC, int heightC, int iterationsC, int thresholdC, double[][] zoomValuesC) {
        this.threadNumber = number;
        this.threadAmount = threads;
        this.frameAmount = frames;
        this.width = widthC;
        this.height = heightC;
        this.iterations = iterationsC;
        this.threshold = thresholdC;
        this.zoomValues = zoomValuesC;
    }

    /**
     * Creates an image from the calculated values
     *  @param image      the image to be used
     * @param counter    the frame number of the image
     * @param width      width of the image
     * @param height     height of the image
     * @param values     the values of every pixel
     * @param iterations the amount of interations
     */
    void createImage(BufferedImage image, int counter, int width, int height, double[][] values, int iterations) {

        //System.out.println("Frame: " + counter + " | Started creating image...");

        int p0 = getColorAsInt(0, 0, 0, 0);
        int t0 = -1;

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                //for every frame:
                double n = values[i][j];

                float hue = (float) n / iterations;
                Color color = Color.getHSBColor(hue, 1, 1);
                image.setRGB(i, j, color.getRGB());

                if (n == t0) {
                    image.setRGB(i, j, p0);
                }
            }
        }
        try {
            File f = new File("C:\\Users\\nilsh\\Desktop\\testordner/sterbi" + counter + ".png");
            ImageIO.write(image, "png", f);
            System.out.println(f.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    int checkMandelbrot(CNumber number, int iterations, double threshold) {
        //returns -1 if the number is in the set, returns the number of iterations before it reached the threshold if it is not in the set

        // start
        CNumber n = new CNumber();
        int reached = -1;

        // first
        n = CNumber.add(n, number);

        for (int i = 0; i < iterations; i++) {
            n = CNumber.add(CNumber.multiply(n, n), number);        // CNumber.multiply(n, n)
            if ((n.getReal() + n.getImag()) > threshold) {
                reached = i;
                break;
            }
        }

        return reached;
    }

    public int getColorAsInt(int a, int r, int g, int b) {

        return (a << 24) | (r << 16) | (g << 8) | b;
    }

}
