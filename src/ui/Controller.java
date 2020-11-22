package ui;

import javafx.event.ActionEvent;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
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
    public TextField pointNumberField;
    public TextField qualityField;
    public TextField zoomSpeedField;
    public TextField frameAmountField;

    private MandelbrotSet m;

    public void nextImage(int frameNumber) throws FileNotFoundException {

        InputStream stream = new FileInputStream(Values.SAVE_IMAGE_PATH + frameNumber + ".png");
        Image image = new Image(stream);
        setPreview.setImage(image);
    }
    public void startCalculation() {
        String pointNumber = pointNumberField.getText();
        String quality = qualityField.getText();
        String zoomSpeed = zoomSpeedField.getText();
        String frames = frameAmountField.getText();

        try{
            m = new MandelbrotSet(Integer.parseInt(pointNumber), Integer.parseInt(quality), Double.parseDouble(zoomSpeed), Integer.parseInt(frames), this);
            m.startMandelbrot();
        } catch (NumberFormatException ignored) {
        }

    }

    public void stop(){
        m.stop();
    }

    public void exitProgram() {
        System.exit(0);
    }

    public synchronized void printOutput(String s){
        output.appendText(String.format("%n%s", s));
    }

}
