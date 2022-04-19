package ru.netology.graphics.image;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;

public class Converter implements TextGraphicsConverter{
    private int width;
    private int height;
    private double maxRatio;
    private TextColorSchema schema;

    public Converter(){
        schema = new Schema();
    }

    public Converter(int width, int height, double maxRatio){
        this.width = width;
        this.height = height;
        this.maxRatio = maxRatio;
        schema = new Schema();
    }

    @Override
    public String convert(String url) throws IOException, BadImageSizeException {
        BufferedImage img = ImageIO.read(new URL(url));
        double imgWidth = img.getWidth();
        double imgHeight = img.getHeight();
        double imgRatio = (double) imgWidth / (double) imgHeight;

        if(imgRatio > maxRatio){
            throw new BadImageSizeException(imgRatio, maxRatio);
        }
        int newWidth;
        int newHeight;
        Image scaledImage;

        if(imgWidth > width || imgHeight > height){
            double scaleRatio = Math.max(imgWidth / width, imgHeight / height);
            newWidth = (int) (imgWidth / scaleRatio);
            newHeight =(int) (imgHeight / scaleRatio);
            scaledImage = img.getScaledInstance(newWidth, newHeight, BufferedImage.SCALE_SMOOTH);
            }else {
            newWidth = (int) imgWidth;
            newHeight = (int) imgHeight;
            scaledImage = img;
        }

        BufferedImage bwImg = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_BYTE_GRAY);
        Graphics2D graphics = bwImg.createGraphics();
        graphics.drawImage(scaledImage, 0, 0, null);
        ImageIO.write(bwImg, "png", new File("out png"));
        WritableRaster bwRaster = bwImg.getRaster();

        String[] textGraphics = new String[newHeight];
        for(int h = 0; h< newHeight; h++) {
            char[] textLine = new char[newWidth * 2];
            for (int w = 0; w < newWidth; w++) {
                int color = bwRaster.getPixel(w, h, new int[3])[0];
                char c = schema.convert(color);  //TODO convert
                textLine[w * 2] = c;
                textLine[w * 2 + 1] = c; //вертик
            }

            textGraphics[h] = new String((textLine)) + "/n";
        }
        return Arrays.toString(textGraphics);
        }

    @Override
    public void setMaxWidth(int width) {
        this.width = width;
    }

    @Override
    public void setMaxHeight(int height) {
        this.height = height;
    }

    @Override
    public void setMaxRatio(double maxRatio) {
        this.maxRatio = maxRatio;
    }

    @Override
    public void setTextColorSchema(TextColorSchema schema) {
        this.schema = schema;
    }
}

