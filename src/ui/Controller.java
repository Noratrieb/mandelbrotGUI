package ui;

import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import mandelbrotCalculator.MandelbrotSet;
import util.Values;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class Controller {

    public ImageView setPreview;
    public TextArea output;

    public void nextImage(int frameNumber) throws FileNotFoundException {

        InputStream stream = new FileInputStream(Values.SAVE_IMAGE_PATH + frameNumber + ".png");
        Image image = new Image(stream);
        setPreview.setImage(image);
    }

    public void startCalculation() {
        MandelbrotSet m = new MandelbrotSet(2, -1, 2, 100, this);
        m.startMandelbrot();
    }

    public void printOutput(String s){
        output.appendText(String.format("%n%s", s));
    }
}
