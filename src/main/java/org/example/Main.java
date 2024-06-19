package org.example;

import java.io.*;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        ImageEditor myImage = new ImageEditor();
        try {
            myImage.loadImage("WUWUWU.jpg");
        } catch (IOException e) {
            e.printStackTrace();
        }

        myImage.brightness(-100);
        myImage.saveImage("WUWU.jpg", "jpg");
        myImage.brightnessMultiThread(200);
        myImage.drawHistogram("WUWUHistogram.png", "png");
        myImage.saveImage("WUWU2.jpg", "jpg");
        myImage.drawHistogram("WUWU2Histogram.png", "png");
    }
    //nie jestem pewny czy dobrze rozumiem te histogrtamy, nie wiem jak je poprawnie narysować
}