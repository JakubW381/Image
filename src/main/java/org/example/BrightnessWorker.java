package org.example;

import java.awt.image.BufferedImage;

import static java.lang.Math.clamp;

public class BrightnessWorker implements Runnable{

    public int start;
    public int end;
    public int value;
    public BufferedImage bufferedImage;

    public BrightnessWorker(int start, int end, BufferedImage bufferedImage, int value) {
        this.start = start;
        this.end = end;
        this.bufferedImage = bufferedImage;
        this.value = value;
    }
    @Override
    public void run() {
        int width = bufferedImage.getWidth();
        int blue;
        int green;
        int red;
        for (int y = start; y < end; y++){
            for (int x = 0; x < width; x++){
                int rgb = bufferedImage.getRGB(x,y);

                blue = rgb & 0xff;
                green = (rgb >> 8) & 0xff;
                red = (rgb >> 16) & 0xff;

                blue = clamp(blue+value,0,255);
                green = clamp(green+value,0,255);
                red = clamp(red+value,0,255);

                int newRgb = blue | (green << 8) | (red << 16);
                bufferedImage.setRGB(x,y,newRgb);
            }
        }
    }
}
