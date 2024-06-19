package org.example;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.lang.Math.clamp;


public class ImageEditor{
    BufferedImage bufferedImage;

    public void loadImage(String path) throws IOException {
            File inputFile = new File(path);
            bufferedImage = ImageIO.read(inputFile);
    }

    public void saveImage(String path,String format) throws IOException {
        if(bufferedImage != null){
            File outputFile = new File(path);
            ImageIO.write(bufferedImage,format,outputFile);
        }
        else {
            throw new IllegalArgumentException("Missing Image");
        }
    }

    public void brightness(int value){
        if (bufferedImage != null){
            for (int y = 0; y < bufferedImage.getHeight(); y++){
                for (int x = 0; x < bufferedImage.getWidth(); x++){
                    int rgb = bufferedImage.getRGB(x,y);

                    int blue = rgb & 0xff;
                    int green = (rgb >> 8) & 0xff;
                    int red = (rgb >> 16) & 0xff;

                    blue = clamp(blue+value,0,255);
                    green = clamp(green+value,0,255);
                    red = clamp(red+value,0,255);

                    int newRgb = blue | (green << 8) | (red << 16);
                    bufferedImage.setRGB(x,y,newRgb);
                }
            }
        }
        else{
            throw new IllegalCallerException("Missing Image");
        }
    }

    public void brightnessMultiThread(int value) throws InterruptedException {
        if (bufferedImage != null) {
            int height = bufferedImage.getHeight();
            int numberOfThreads = Runtime.getRuntime().availableProcessors();
            Thread[] threads = new Thread[numberOfThreads];
            int chunkSize = height / numberOfThreads;

            for (int i = 0; i < numberOfThreads; i++) {
                int start = i * chunkSize;
                int end = (i == numberOfThreads - 1) ? height : (i + 1) * chunkSize;
                threads[i] = new Thread(new BrightnessWorker(start, end, bufferedImage, value));
                threads[i].start();
            }
            for (Thread thread : threads) thread.join();
        }
        else {
            throw new InterruptedException();
        }
    }

    public int[][] computeHistogram() {
        if (bufferedImage == null) {
            throw new IllegalStateException("Image has not been loaded.");
        }

        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();

        int[] redHistogram = new int[256];
        int[] greenHistogram = new int[256];
        int[] blueHistogram = new int[256];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = bufferedImage.getRGB(x, y);

                int red = (rgb >> 16) & 0xff;
                int green = (rgb >> 8) & 0xff;
                int blue = rgb & 0xff;

                redHistogram[red]++;
                greenHistogram[green]++;
                blueHistogram[blue]++;
            }
        }

        int[][] histograms = new int[3][256]; // 0 - Red, 1 - Green, 2 - Blue
        histograms[0] = redHistogram;
        histograms[1] = greenHistogram;
        histograms[2] = blueHistogram;

        return histograms;
    }

    public void drawHistogram(String path,String format) throws IOException {
        int[][] histograms = this.computeHistogram();
        int numOfChannels = histograms.length;
        int numBins = histograms[0].length;
        int width = numBins;
        int height = 200;

        int maxValue = 0;
        for (int[] histogram : histograms) {
            for (int value : histogram) {
                if (value > maxValue) {
                    maxValue = value;
                }
            }
        }

        BufferedImage histogramImage = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
        Graphics g = histogramImage.getGraphics();

        g.setColor(Color.white);
        g.fillRect(0,0,width,height);

        Color[] channelColors = {Color.red,Color.green,Color.blue};
        double scaleFactor = (double) (height - 20) / maxValue;
        for (int channel = 0; channel < numOfChannels ; channel++){
            g.setColor(channelColors[channel]);

            for (int bin = 0; bin < numBins ; bin++){
                int barHeight = (int) (histograms[channel][bin] * scaleFactor);
                g.drawLine(bin,height - 1, bin, height - barHeight - 1);
            }
        }
        File outputImage = new File(path);
        ImageIO.write(histogramImage,format, outputImage);
        g.dispose();
    }
}
