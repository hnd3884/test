package com.jhlabs.image;

import java.awt.image.BufferedImage;
import com.jhlabs.math.CellularFunction2D;
import com.jhlabs.math.SCNoise;
import com.jhlabs.math.VLNoise;
import com.jhlabs.math.RidgedFBM;
import com.jhlabs.math.Noise;
import com.jhlabs.math.Function2D;
import java.util.Random;
import com.jhlabs.math.FBM;

public class FBMFilter extends PointFilter implements Cloneable
{
    public static final int NOISE = 0;
    public static final int RIDGED = 1;
    public static final int VLNOISE = 2;
    public static final int SCNOISE = 3;
    public static final int CELLULAR = 4;
    private float scale;
    private float stretch;
    private float angle;
    private float amount;
    private float H;
    private float octaves;
    private float lacunarity;
    private float gain;
    private float bias;
    private int operation;
    private float m00;
    private float m01;
    private float m10;
    private float m11;
    private float min;
    private float max;
    private Colormap colormap;
    private boolean ridged;
    private FBM fBm;
    protected Random random;
    private int basisType;
    private Function2D basis;
    
    public FBMFilter() {
        this.scale = 32.0f;
        this.stretch = 1.0f;
        this.angle = 0.0f;
        this.amount = 1.0f;
        this.H = 1.0f;
        this.octaves = 4.0f;
        this.lacunarity = 2.0f;
        this.gain = 0.5f;
        this.bias = 0.5f;
        this.m00 = 1.0f;
        this.m01 = 0.0f;
        this.m10 = 0.0f;
        this.m11 = 1.0f;
        this.colormap = new Gradient();
        this.random = new Random();
        this.setBasisType(this.basisType = 0);
    }
    
    public void setAmount(final float amount) {
        this.amount = amount;
    }
    
    public float getAmount() {
        return this.amount;
    }
    
    public void setOperation(final int operation) {
        this.operation = operation;
    }
    
    public int getOperation() {
        return this.operation;
    }
    
    public void setScale(final float scale) {
        this.scale = scale;
    }
    
    public float getScale() {
        return this.scale;
    }
    
    public void setStretch(final float stretch) {
        this.stretch = stretch;
    }
    
    public float getStretch() {
        return this.stretch;
    }
    
    public void setAngle(final float angle) {
        this.angle = angle;
        final float cos = (float)Math.cos(this.angle);
        final float sin = (float)Math.sin(this.angle);
        this.m00 = cos;
        this.m01 = sin;
        this.m10 = -sin;
        this.m11 = cos;
    }
    
    public float getAngle() {
        return this.angle;
    }
    
    public void setOctaves(final float octaves) {
        this.octaves = octaves;
    }
    
    public float getOctaves() {
        return this.octaves;
    }
    
    public void setH(final float H) {
        this.H = H;
    }
    
    public float getH() {
        return this.H;
    }
    
    public void setLacunarity(final float lacunarity) {
        this.lacunarity = lacunarity;
    }
    
    public float getLacunarity() {
        return this.lacunarity;
    }
    
    public void setGain(final float gain) {
        this.gain = gain;
    }
    
    public float getGain() {
        return this.gain;
    }
    
    public void setBias(final float bias) {
        this.bias = bias;
    }
    
    public float getBias() {
        return this.bias;
    }
    
    public void setColormap(final Colormap colormap) {
        this.colormap = colormap;
    }
    
    public Colormap getColormap() {
        return this.colormap;
    }
    
    public void setBasisType(final int basisType) {
        switch (this.basisType = basisType) {
            default: {
                this.basis = new Noise();
                break;
            }
            case 1: {
                this.basis = new RidgedFBM();
                break;
            }
            case 2: {
                this.basis = new VLNoise();
                break;
            }
            case 3: {
                this.basis = new SCNoise();
                break;
            }
            case 4: {
                this.basis = new CellularFunction2D();
                break;
            }
        }
    }
    
    public int getBasisType() {
        return this.basisType;
    }
    
    public void setBasis(final Function2D basis) {
        this.basis = basis;
    }
    
    public Function2D getBasis() {
        return this.basis;
    }
    
    protected FBM makeFBM(final float H, final float lacunarity, final float octaves) {
        final FBM fbm = new FBM(H, lacunarity, octaves, this.basis);
        final float[] minmax = Noise.findRange(fbm, null);
        this.min = minmax[0];
        this.max = minmax[1];
        return fbm;
    }
    
    @Override
    public BufferedImage filter(final BufferedImage src, final BufferedImage dst) {
        this.fBm = this.makeFBM(this.H, this.lacunarity, this.octaves);
        return super.filter(src, dst);
    }
    
    @Override
    public int filterRGB(final int x, final int y, final int rgb) {
        float nx = this.m00 * x + this.m01 * y;
        float ny = this.m10 * x + this.m11 * y;
        nx /= this.scale;
        ny /= this.scale * this.stretch;
        float f = this.fBm.evaluate(nx, ny);
        f = (f - this.min) / (this.max - this.min);
        f = ImageMath.gain(f, this.gain);
        f = ImageMath.bias(f, this.bias);
        f *= this.amount;
        final int a = rgb & 0xFF000000;
        int v;
        if (this.colormap != null) {
            v = this.colormap.getColor(f);
        }
        else {
            v = PixelUtils.clamp((int)(f * 255.0f));
            final int r = v << 16;
            final int g = v << 8;
            final int b = v;
            v = (a | r | g | b);
        }
        if (this.operation != 0) {
            v = PixelUtils.combinePixels(rgb, v, this.operation);
        }
        return v;
    }
    
    @Override
    public String toString() {
        return "Texture/Fractal Brownian Motion...";
    }
}
