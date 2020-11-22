package mandelbrotCalculator;

import ui.Controller;
import util.Values;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class CalculationThread implements Runnable {

    private Thread thread;

    int threadNumber;
    int threadAmount;
    int frameAmount;
    int width;
    int height;
    int iterations;
    int threshold;
    double[][] zoomValues;
    CNumber[][] samples;

    final MandelbrotSet set;
    final Controller controller;

    public void start() {
        if (thread == null) {
            synchronized (controller){
                controller.printOutput("Thread " + threadNumber + " started");
            }
            thread = new Thread(this, String.valueOf(threadNumber));
            thread.start();
        }
    }

    @Override
    public void run() {

        long totalStartTime = System.currentTimeMillis();

        samples = new CNumber[width][height];

        for (int frameCounter = threadNumber; frameCounter < frameAmount; frameCounter += threadAmount) {

            long startTime = System.currentTimeMillis();

            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

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

            synchronized (set) {
                set.frameFinished(frameCounter);
            }

            long frameTime = System.currentTimeMillis() - startTime;
            System.out.println("Frame " + frameCounter + " finished in " + ((double) frameTime / 1000) + "s");

            synchronized (controller) {
                controller.printOutput("Frame " + frameCounter + " finished in " + ((double) frameTime / 1000) + "s");
            }

        }

        long totalTime = System.currentTimeMillis() - totalStartTime;
        System.out.println("--Thread " + threadNumber + " completed. Process took " + ((double) totalTime / 1000) + "s");

        synchronized (controller) {
            controller.printOutput("--Thread " + threadNumber + " completed. Process took " + ((double) totalTime / 1000) + "s");
        }

        synchronized (set) {
            set.setFinished(threadNumber);
        }

    }

    public CalculationThread(Controller controller, MandelbrotSet set, int number, int threads, int frames, int widthC, int heightC, int iterationsC, int thresholdC, double[][] zoomValuesC) {
        this.set = set;
        this.controller = controller;
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
     *
     * @param image      the image to be used
     * @param counter    the frame number of the image
     * @param width      width of the image
     * @param height     height of the image
     * @param values     the values of every pixel
     * @param iterations the amount of iterations
     */
    void createImage(BufferedImage image, int counter, int width, int height, double[][] values, int iterations) {

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
            File f = new File(Values.SAVE_IMAGE_PATH + counter + ".png");
            ImageIO.write(image, "png", f);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Checks whether the number is in the Mandelbrot set
     *
     * @param number     The Complex Number to be checked
     * @param iterations The amount of iterations the program should do
     * @param threshold  The threshold for a number not being in the set
     * @return -1 if the number is in the set, else it returns the amount of interations before it reached the threshold
     */
    int checkMandelbrot(CNumber number, int iterations, double threshold) {

        CNumber n = new CNumber();
        int reached = -1;

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
