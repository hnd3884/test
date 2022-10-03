package com.jhlabs.image;

public class ThresholdFilter extends PointFilter
{
    private int lowerThreshold;
    private int lowerThreshold3;
    private int upperThreshold;
    private int upperThreshold3;
    private int white;
    private int black;
    
    public ThresholdFilter() {
        this(127);
    }
    
    public ThresholdFilter(final int t) {
        this.white = 16777215;
        this.black = 0;
        this.setLowerThreshold(t);
        this.setUpperThreshold(t);
    }
    
    public void setLowerThreshold(final int lowerThreshold) {
        this.lowerThreshold = lowerThreshold;
        this.lowerThreshold3 = lowerThreshold * 3;
    }
    
    public int getLowerThreshold() {
        return this.lowerThreshold;
    }
    
    public void setUpperThreshold(final int upperThreshold) {
        this.upperThreshold = upperThreshold;
        this.upperThreshold3 = upperThreshold * 3;
    }
    
    public int getUpperThreshold() {
        return this.upperThreshold;
    }
    
    public void setWhite(final int white) {
        this.white = white;
    }
    
    public int getWhite() {
        return this.white;
    }
    
    public void setBlack(final int black) {
        this.black = black;
    }
    
    public int getBlack() {
        return this.black;
    }
    
    @Override
    public int filterRGB(final int x, final int y, final int rgb) {
        final int a = rgb & 0xFF000000;
        final int r = rgb >> 16 & 0xFF;
        final int g = rgb >> 8 & 0xFF;
        final int b = rgb & 0xFF;
        final int l = r + g + b;
        if (l < this.lowerThreshold3) {
            return a | this.black;
        }
        if (l > this.upperThreshold3) {
            return a | this.white;
        }
        return rgb;
    }
    
    @Override
    public String toString() {
        return "Stylize/Threshold...";
    }
}
