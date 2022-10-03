package com.jhlabs.image;

public class ChannelMixFilter extends PointFilter
{
    private int blueGreen;
    private int redBlue;
    private int greenRed;
    private int intoR;
    private int intoG;
    private int intoB;
    
    public ChannelMixFilter() {
        this.canFilterIndexColorModel = true;
    }
    
    public void setBlueGreen(final int blueGreen) {
        this.blueGreen = blueGreen;
    }
    
    public int getBlueGreen() {
        return this.blueGreen;
    }
    
    public void setRedBlue(final int redBlue) {
        this.redBlue = redBlue;
    }
    
    public int getRedBlue() {
        return this.redBlue;
    }
    
    public void setGreenRed(final int greenRed) {
        this.greenRed = greenRed;
    }
    
    public int getGreenRed() {
        return this.greenRed;
    }
    
    public void setIntoR(final int intoR) {
        this.intoR = intoR;
    }
    
    public int getIntoR() {
        return this.intoR;
    }
    
    public void setIntoG(final int intoG) {
        this.intoG = intoG;
    }
    
    public int getIntoG() {
        return this.intoG;
    }
    
    public void setIntoB(final int intoB) {
        this.intoB = intoB;
    }
    
    public int getIntoB() {
        return this.intoB;
    }
    
    @Override
    public int filterRGB(final int x, final int y, final int rgb) {
        final int a = rgb & 0xFF000000;
        final int r = rgb >> 16 & 0xFF;
        final int g = rgb >> 8 & 0xFF;
        final int b = rgb & 0xFF;
        final int nr = PixelUtils.clamp((this.intoR * (this.blueGreen * g + (255 - this.blueGreen) * b) / 255 + (255 - this.intoR) * r) / 255);
        final int ng = PixelUtils.clamp((this.intoG * (this.redBlue * b + (255 - this.redBlue) * r) / 255 + (255 - this.intoG) * g) / 255);
        final int nb = PixelUtils.clamp((this.intoB * (this.greenRed * r + (255 - this.greenRed) * g) / 255 + (255 - this.intoB) * b) / 255);
        return a | nr << 16 | ng << 8 | nb;
    }
    
    @Override
    public String toString() {
        return "Colors/Mix Channels...";
    }
}
