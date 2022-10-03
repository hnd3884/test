package com.jhlabs.image;

public class Histogram
{
    public static final int RED = 0;
    public static final int GREEN = 1;
    public static final int BLUE = 2;
    public static final int GRAY = 3;
    protected int[][] histogram;
    protected int numSamples;
    protected int[] minValue;
    protected int[] maxValue;
    protected int[] minFrequency;
    protected int[] maxFrequency;
    protected float[] mean;
    protected boolean isGray;
    
    public Histogram() {
        this.histogram = null;
        this.numSamples = 0;
        this.isGray = true;
        this.minValue = null;
        this.maxValue = null;
        this.minFrequency = null;
        this.maxFrequency = null;
        this.mean = null;
    }
    
    public Histogram(final int[] pixels, final int w, final int h, final int offset, final int stride) {
        this.histogram = new int[3][256];
        this.minValue = new int[4];
        this.maxValue = new int[4];
        this.minFrequency = new int[3];
        this.maxFrequency = new int[3];
        this.mean = new float[3];
        this.numSamples = w * h;
        this.isGray = true;
        int index = 0;
        for (int y = 0; y < h; ++y) {
            index = offset + y * stride;
            for (int x = 0; x < w; ++x) {
                final int rgb = pixels[index++];
                final int r = rgb >> 16 & 0xFF;
                final int g = rgb >> 8 & 0xFF;
                final int b = rgb & 0xFF;
                final int[] array = this.histogram[0];
                final int n = r;
                ++array[n];
                final int[] array2 = this.histogram[1];
                final int n2 = g;
                ++array2[n2];
                final int[] array3 = this.histogram[2];
                final int n3 = b;
                ++array3[n3];
            }
        }
        for (int i = 0; i < 256; ++i) {
            if (this.histogram[0][i] != this.histogram[1][i] || this.histogram[1][i] != this.histogram[2][i]) {
                this.isGray = false;
                break;
            }
        }
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 256; ++j) {
                if (this.histogram[i][j] > 0) {
                    this.minValue[i] = j;
                    break;
                }
            }
            for (int j = 255; j >= 0; --j) {
                if (this.histogram[i][j] > 0) {
                    this.maxValue[i] = j;
                    break;
                }
            }
            this.minFrequency[i] = Integer.MAX_VALUE;
            this.maxFrequency[i] = 0;
            for (int j = 0; j < 256; ++j) {
                this.minFrequency[i] = Math.min(this.minFrequency[i], this.histogram[i][j]);
                this.maxFrequency[i] = Math.max(this.maxFrequency[i], this.histogram[i][j]);
                final float[] mean = this.mean;
                final int n4 = i;
                mean[n4] += j * this.histogram[i][j];
            }
            final float[] mean2 = this.mean;
            final int n5 = i;
            mean2[n5] /= this.numSamples;
        }
        this.minValue[3] = Math.min(Math.min(this.minValue[0], this.minValue[1]), this.minValue[2]);
        this.maxValue[3] = Math.max(Math.max(this.maxValue[0], this.maxValue[1]), this.maxValue[2]);
    }
    
    public boolean isGray() {
        return this.isGray;
    }
    
    public int getNumSamples() {
        return this.numSamples;
    }
    
    public int getFrequency(final int value) {
        if (this.numSamples > 0 && this.isGray && value >= 0 && value <= 255) {
            return this.histogram[0][value];
        }
        return -1;
    }
    
    public int getFrequency(final int channel, final int value) {
        if (this.numSamples < 1 || channel < 0 || channel > 2 || value < 0 || value > 255) {
            return -1;
        }
        return this.histogram[channel][value];
    }
    
    public int getMinFrequency() {
        if (this.numSamples > 0 && this.isGray) {
            return this.minFrequency[0];
        }
        return -1;
    }
    
    public int getMinFrequency(final int channel) {
        if (this.numSamples < 1 || channel < 0 || channel > 2) {
            return -1;
        }
        return this.minFrequency[channel];
    }
    
    public int getMaxFrequency() {
        if (this.numSamples > 0 && this.isGray) {
            return this.maxFrequency[0];
        }
        return -1;
    }
    
    public int getMaxFrequency(final int channel) {
        if (this.numSamples < 1 || channel < 0 || channel > 2) {
            return -1;
        }
        return this.maxFrequency[channel];
    }
    
    public int getMinValue() {
        if (this.numSamples > 0 && this.isGray) {
            return this.minValue[0];
        }
        return -1;
    }
    
    public int getMinValue(final int channel) {
        return this.minValue[channel];
    }
    
    public int getMaxValue() {
        if (this.numSamples > 0 && this.isGray) {
            return this.maxValue[0];
        }
        return -1;
    }
    
    public int getMaxValue(final int channel) {
        return this.maxValue[channel];
    }
    
    public float getMeanValue() {
        if (this.numSamples > 0 && this.isGray) {
            return this.mean[0];
        }
        return -1.0f;
    }
    
    public float getMeanValue(final int channel) {
        if (this.numSamples > 0 && 0 <= channel && channel <= 2) {
            return this.mean[channel];
        }
        return -1.0f;
    }
}
