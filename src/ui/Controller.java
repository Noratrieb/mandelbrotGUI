package ui;

import javafx.event.ActionEvent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import mandelbrotCalculator.MandelbrotSet;
import util.Values;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class Controller {

    public ImageView setPreview;

    public void nextImage(int frameNumber) throws FileNotFoundException {

        InputStream stream = new FileInputStream(Values.SAVE_IMAGE_PATH + frameNumber + ".png");
        Image image = new Image(stream);
        setPreview.setImage(image);
    }

    public void startCalculation(ActionEvent actionEvent) {
        MandelbrotSet m = new MandelbrotSet(2, 1, 3, 100, this);
        m.startMandelbrot();
    }
}
