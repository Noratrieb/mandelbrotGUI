package mandelbrotCalculator;

import ui.Controller;

import java.io.FileNotFoundException;

public class MandelbrotSet {

    private double[][] interestingPoints = {{-0.75, 0}, {-0.77568377, 0.13646737}, {-1.74995768370609350360221450607069970727110579726252077930242837820286008082972804887218672784431700831100544507655659531379747541999999995, 0.00000000000000000278793706563379402178294753790944364927085054500163081379043930650189386849765202169477470552201325772332454726999999995}};

    //constructor parameter variables
    private int pointNumber;
    private double zoomSpeed; // >1
    private int frames;

    //default values that can be changed
    private double pointX = 0;
    private double pointY = 0;
    private double zoom = 1;
    private int width = 1920;
    private int threshold = 1000;
    private float ratio = 2 / 3f;
    private int threadAmount = Runtime.getRuntime().availableProcessors() / 2;

    //only declared
    private int height;
    private int iterations;

    private boolean[] completeThreads;
    private boolean[] completeFrames;

    private long startTime = System.currentTimeMillis();

    private Controller controller;


    /**
     * Create a new Mandelbrot set manager
     * @param pointNumber The point number (-1 to set a custom point afterwards)
     * @param quality The quality (0-4, any other value sets the iterations to this value directly
     * @param zoomSpeed By how much the zoom value is multiplied every frame
     * @param frames The number of frames to be calculated
     */
    public MandelbrotSet(int pointNumber, int quality, double zoomSpeed, int frames, Controller controller) {
        this.pointNumber = pointNumber;
        this.zoomSpeed = zoomSpeed;
        this.frames = frames;
        this.controller = controller;

        height = (int) ((float) width * ratio);

        iterations = switch (quality){
            case 0 -> 50;
            case 1 -> 100;
            case 2 -> 500;
            case 3 -> 1000;
            case 4 -> 5000;
            default -> quality;
        };

        completeThreads = new boolean[threadAmount];
        completeFrames = new boolean[frames];
    }

    public MandelbrotSet(Controller c){
        this(2, 2, 1.2, 1, c);
    }

    public void startMandelbrot() {

        System.out.println("Started calculating " + frames + " frame/s");

        double forceCenterX;
        double forceCenterY;
        if(pointNumber == -1){
            forceCenterX = pointX;
            forceCenterY = pointY;
        } else {
            forceCenterX = interestingPoints[pointNumber][0];
            forceCenterY = interestingPoints[pointNumber][1];
        }

        double[][] zoomValues = zoomValues(frames, width, height, forceCenterX, forceCenterY, zoomSpeed, zoom);

        //create the threads
        CalculationThread[] threads = new CalculationThread[threadAmount];
        for (int i = 0; i < threadAmount; i++) {
            threads[i] = new CalculationThread(this, i, threadAmount, frames, width, height, iterations, threshold, zoomValues);
            threads[i].start();
        }
    }



    /**
     * Creates a double array of every corner position
     *
     * @param frames    the number of frames
     * @param width     width of the image
     * @param height    height of the image
     * @param centerX   the real coordinates of the center to be zoomed in
     * @param centerY   the imaginary coordinates of the center to be zoomed in
     * @param zoomSpeed speed of the zoom
     * @param zoom      the initial zoom
     * @return returns the corner coordinates
     */
    double[][] zoomValues(int frames, int width, int height, double centerX, double centerY, double zoomSpeed, double zoom) {

        //values, frames
        //values: 0 - stepSizeX, 1 - stepSizeY, 2 - offsetX, 3 - offsetY
        double[][] zoomValues = new double[4][frames];

        for (int frameCounter = 0; frameCounter < frames; frameCounter++) {

            zoomValues[0][frameCounter] = (3 / (float) width) / zoom;
            zoomValues[1][frameCounter] = (2 / (float) height) / zoom;

            zoomValues[2][frameCounter] = centerX - width / 2 * zoomValues[0][frameCounter];
            zoomValues[3][frameCounter] = -(centerY - height / 2 * zoomValues[1][frameCounter]);

            zoom *= zoomSpeed;
        }

        return zoomValues;
    }

    /**
     * Call when a new frame is finished
     * @param frameNumber The number of the frame
     */
    public void frameFinished(int frameNumber){
        completeFrames[frameNumber] = true;

        int latestFrame = 0;
        for(int i = 0; i < completeFrames.length; i++){
            if(completeFrames[i]){
                latestFrame = i;
            }
        }

        System.err.println(latestFrame);

        try {
            controller.nextImage(latestFrame);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Call when a new Thread is finished
     * @param threadNumber The Threadier
     */
    public void setFinished(int threadNumber){
        completeThreads[threadNumber] = true;

        boolean finished = true;
        for(boolean b : completeThreads){
            if (!b) {
                finished = false;
                break;
            }
        }

        if(finished){
            System.out.println("CALCULATION FINISHED");
            // TIME should probably not be here and serves no practical purpose but that doesn't stop me from keeping it here
            long endTime = System.currentTimeMillis();
            long completionTimeLong = endTime - startTime;
            double completionTimeSec = (double) completionTimeLong / 1000.0;
            System.out.println("Calculated " + frames + " frame/s in " + completionTimeSec + "s");
        }
    }

    //SETTER
    public void setZoom(double zoom) {
        this.zoom = zoom;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }

    public void setRatio(float ratio) {
        this.ratio = ratio;
    }

    public void setThreadAmount(int threadAmount) {
        this.threadAmount = threadAmount;
    }

    public void setPointX(double pointX) {
        this.pointX = pointX;
    }

    public void setPointY(double pointY) {
        this.pointY = pointY;
    }
}