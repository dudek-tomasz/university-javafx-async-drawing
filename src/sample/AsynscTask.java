package sample;

import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.canvas.GraphicsContext;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

import static sample.Equation.calc;

class AsyncTask extends Task {
    private Random random = new Random();
    private int numberOfPoints;
    private float result;
    private int amountOfGoodPoints=0;
    private GraphicsContext gc;
    boolean suspended = false;
    AsyncTask(int numberOfPoints, GraphicsContext gc){
        this.numberOfPoints=numberOfPoints;
        this.gc=gc;
    }
    @Override
    protected Object call() throws Exception {
        double x;
        double y;
        double tryX;
        double tryY;
        int a;
        int b;
        int i=0;
        BufferedImage bi= new BufferedImage(400, 300, BufferedImage.TYPE_INT_RGB);
        while(i<numberOfPoints) {
            try {
                tryX = -8 + (8 - (-8)) * random.nextDouble();
                tryY = -8 + (8 - (-8)) * random.nextDouble();
                x = (tryX)* gc.getCanvas().getWidth() / 16;
                y = (tryY) * gc.getCanvas().getHeight() / 16;
                a = (int) (gc.getCanvas().getWidth() / 2 - x);
                b = (int) (gc.getCanvas().getHeight() / 2 - y);
                if (calc(tryX, tryY)) {
                    amountOfGoodPoints++;
                    bi.setRGB(a, b, Color.YELLOW.getRGB());
                } else {
                    bi.setRGB(a, b, Color.BLACK.getRGB());
                }
                if (isCancelled()) {
                    break;
                }
                if (i % 1000 == 0) {
                    gc.drawImage(SwingFXUtils.toFXImage(bi, null), 0, 0);
                }
                i++;
                updateProgress(i, numberOfPoints);
                synchronized(this) {
                    while(suspended) {
                        wait();
                    }
                }
            } catch (InterruptedException e) {
            System.out.println("Thread interrupted.");
        }
        }
        result=(16*16)*(float)amountOfGoodPoints/(float)numberOfPoints;
        return result;
    }
    void suspend() {
        suspended = true;
    }
    synchronized void resume() {
        suspended = false;
        notify();
    }
}
