package com.octo.captcha.component.image.deformation;

import com.jhlabs.image.RotateFilter;
import java.awt.image.ImageObserver;
import java.awt.Image;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.security.SecureRandom;
import java.util.Random;

public class PuzzleImageDeformation implements ImageDeformation
{
    private int colNum;
    private int rowNum;
    private double maxAngleRotation;
    private Random random;
    
    public PuzzleImageDeformation(final int colNum, final int rowNum, final double maxAngleRotation) {
        this.colNum = 6;
        this.rowNum = 4;
        this.maxAngleRotation = 0.3;
        this.random = new SecureRandom();
        this.colNum = colNum;
        this.rowNum = rowNum;
        this.maxAngleRotation = maxAngleRotation;
    }
    
    public BufferedImage deformImage(final BufferedImage bufferedImage) {
        final int height = bufferedImage.getHeight();
        final int width = bufferedImage.getWidth();
        final int n = width / this.colNum;
        final int n2 = height / this.rowNum;
        final Graphics2D graphics2D = (Graphics2D)new BufferedImage(width, height, bufferedImage.getType()).getGraphics();
        graphics2D.setColor(Color.white);
        graphics2D.setBackground(Color.white);
        graphics2D.fillRect(0, 0, width, height);
        graphics2D.dispose();
        final Graphics2D graphics2D2 = (Graphics2D)bufferedImage.getGraphics();
        graphics2D2.setBackground(Color.white);
        final BufferedImage bufferedImage2 = new BufferedImage(n, n2, bufferedImage.getType());
        final Graphics2D graphics = bufferedImage2.createGraphics();
        for (int i = 0; i < this.colNum; ++i) {
            for (int j = 0; j < this.rowNum; ++j) {
                graphics.drawImage(bufferedImage, 0, 0, n, n2, n * i, n2 * j, n * i + n, n2 * j + n2, null);
                final RotateFilter rotateFilter = new RotateFilter((float)this.maxAngleRotation * this.random.nextFloat() * (this.random.nextBoolean() ? -1 : 1));
                bufferedImage2.getGraphics().dispose();
                graphics2D2.drawImage(bufferedImage2, n * i, n2 * j, null, null);
            }
        }
        return bufferedImage;
    }
}
