package com.adventnet.beans;

import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.Dimension;
import java.awt.Color;
import javax.swing.Timer;
import java.awt.event.ActionListener;
import javax.swing.JPanel;

public class LedPanel extends JPanel implements ActionListener
{
    private int indexOfColor;
    private int timeDelay;
    private Timer delay;
    private Color[] color;
    private Color[] blinkColor;
    private boolean selectFillFlag;
    private int nLed;
    private int radius;
    
    public void setDefaultColor() {
        (this.color = new Color[this.nLed])[0] = Color.white;
        this.color[1] = Color.white;
        this.color[2] = Color.white;
        (this.blinkColor = new Color[this.nLed])[0] = Color.red;
        this.blinkColor[1] = Color.green;
        this.blinkColor[2] = Color.blue;
        this.setBackground(Color.black);
    }
    
    public LedPanel() {
        this.timeDelay = 500;
        this.delay = new Timer(this.getTimeDelay(), this);
        this.color = null;
        this.blinkColor = null;
        this.selectFillFlag = false;
        this.nLed = 3;
        this.radius = 8;
        this.setDefaultColor();
        this.setPreferredSize(new Dimension(65, 20));
        this.delay.start();
    }
    
    public void setLedRadius(final int radius) {
        this.radius = radius;
    }
    
    public int getLedRadius() {
        return this.radius;
    }
    
    public void setTimeDelay(final int timeDelay) {
        this.timeDelay = timeDelay;
        this.delay.stop();
        (this.delay = new Timer(this.timeDelay, this)).start();
    }
    
    public int getTimeDelay() {
        return this.timeDelay;
    }
    
    public void setColorToBlink(final int indexOfColor) {
        this.indexOfColor = indexOfColor;
    }
    
    public int getColorToBlink() {
        return this.indexOfColor;
    }
    
    public void setLedColor1(final Color color) {
        this.color[0] = color;
        this.repaint();
    }
    
    public Color getLedColor1() {
        return this.color[0];
    }
    
    public void setLedColor2(final Color color) {
        this.color[1] = color;
        this.repaint();
    }
    
    public Color getLedColor2() {
        return this.color[1];
    }
    
    public void setLedColor3(final Color color) {
        this.color[2] = color;
        this.repaint();
    }
    
    public Color getLedColor3() {
        return this.color[2];
    }
    
    public void setLedBlinkColor1(final Color color) {
        this.blinkColor[0] = color;
    }
    
    public void setLedBlinkColor2(final Color color) {
        this.blinkColor[1] = color;
    }
    
    public void setLedBlinkColor3(final Color color) {
        this.blinkColor[2] = color;
    }
    
    public Color getLedBlinkColor1() {
        return this.blinkColor[0];
    }
    
    public Color getLedBlinkColor2() {
        return this.blinkColor[1];
    }
    
    public Color getLedBlinkColor3() {
        return this.blinkColor[2];
    }
    
    public void actionPerformed(final ActionEvent actionEvent) {
        this.repaint();
    }
    
    public void paintComponent(final Graphics graphics) {
        final Dimension size = this.getSize();
        final int n = (size.height - this.radius) / 2;
        graphics.setColor(this.getBackground());
        graphics.fillRect(0, 0, size.width, size.height);
        final int n2 = n;
        final int n3 = (size.width - this.nLed * this.radius) / (this.nLed + 1);
        final int n4 = (size.width + this.radius - (this.nLed * 2 * this.radius + (this.nLed + 1) * n3)) / 2;
        for (int i = 0; i < this.nLed; ++i) {
            final int n5 = n4 + (n3 + this.radius) * (i + 1);
            graphics.setColor(this.color[i]);
            graphics.fillArc(n5, n2, this.radius, this.radius, 0, 360);
        }
        final int n6 = n4 + (n3 + this.radius) * (this.indexOfColor + 1);
        final int n7 = n;
        if (!this.selectFillFlag) {
            graphics.setColor(this.blinkColor[this.indexOfColor]);
            graphics.fillArc(n6, n7, this.radius, this.radius, 0, 360);
        }
        if (this.selectFillFlag) {
            this.selectFillFlag = false;
        }
        else {
            this.selectFillFlag = true;
        }
    }
}
