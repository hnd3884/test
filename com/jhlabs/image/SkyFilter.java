package com.jhlabs.image;

import com.jhlabs.math.Noise;
import java.awt.Graphics2D;
import java.awt.image.ImageObserver;
import java.awt.Image;
import java.awt.image.ColorModel;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import com.jhlabs.math.Function2D;
import java.util.Random;
import com.jhlabs.math.FBM;

public class SkyFilter extends PointFilter
{
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
    private float min;
    private float max;
    private boolean ridged;
    private FBM fBm;
    protected Random random;
    private Function2D basis;
    private float cloudCover;
    private float cloudSharpness;
    private float time;
    private float glow;
    private float glowFalloff;
    private float haziness;
    private float t;
    private float sunRadius;
    private int sunColor;
    private float sunR;
    private float sunG;
    private float sunB;
    private float sunAzimuth;
    private float sunElevation;
    private float windSpeed;
    private float cameraAzimuth;
    private float cameraElevation;
    private float fov;
    private float[] exponents;
    private float[] tan;
    private BufferedImage skyColors;
    private int[] skyPixels;
    private static final float r255 = 0.003921569f;
    private float width;
    private float height;
    float mn;
    float mx;
    
    public SkyFilter() {
        this.scale = 0.1f;
        this.stretch = 1.0f;
        this.angle = 0.0f;
        this.amount = 1.0f;
        this.H = 1.0f;
        this.octaves = 8.0f;
        this.lacunarity = 2.0f;
        this.gain = 1.0f;
        this.bias = 0.6f;
        this.random = new Random();
        this.cloudCover = 0.5f;
        this.cloudSharpness = 0.5f;
        this.time = 0.3f;
        this.glow = 0.5f;
        this.glowFalloff = 0.5f;
        this.haziness = 0.96f;
        this.t = 0.0f;
        this.sunRadius = 10.0f;
        this.sunColor = -1;
        this.sunAzimuth = 0.5f;
        this.sunElevation = 0.5f;
        this.windSpeed = 0.0f;
        this.cameraAzimuth = 0.0f;
        this.cameraElevation = 0.0f;
        this.fov = 1.0f;
        if (this.skyColors == null) {
            this.skyColors = ImageUtils.createImage(Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("SkyColors.png")).getSource());
        }
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
    
    public void setT(final float t) {
        this.t = t;
    }
    
    public float getT() {
        return this.t;
    }
    
    public void setFOV(final float fov) {
        this.fov = fov;
    }
    
    public float getFOV() {
        return this.fov;
    }
    
    public void setCloudCover(final float cloudCover) {
        this.cloudCover = cloudCover;
    }
    
    public float getCloudCover() {
        return this.cloudCover;
    }
    
    public void setCloudSharpness(final float cloudSharpness) {
        this.cloudSharpness = cloudSharpness;
    }
    
    public float getCloudSharpness() {
        return this.cloudSharpness;
    }
    
    public void setTime(final float time) {
        this.time = time;
    }
    
    public float getTime() {
        return this.time;
    }
    
    public void setGlow(final float glow) {
        this.glow = glow;
    }
    
    public float getGlow() {
        return this.glow;
    }
    
    public void setGlowFalloff(final float glowFalloff) {
        this.glowFalloff = glowFalloff;
    }
    
    public float getGlowFalloff() {
        return this.glowFalloff;
    }
    
    public void setAngle(final float angle) {
        this.angle = angle;
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
    
    public void setHaziness(final float haziness) {
        this.haziness = haziness;
    }
    
    public float getHaziness() {
        return this.haziness;
    }
    
    public void setSunElevation(final float sunElevation) {
        this.sunElevation = sunElevation;
    }
    
    public float getSunElevation() {
        return this.sunElevation;
    }
    
    public void setSunAzimuth(final float sunAzimuth) {
        this.sunAzimuth = sunAzimuth;
    }
    
    public float getSunAzimuth() {
        return this.sunAzimuth;
    }
    
    public void setSunColor(final int sunColor) {
        this.sunColor = sunColor;
    }
    
    public int getSunColor() {
        return this.sunColor;
    }
    
    public void setCameraElevation(final float cameraElevation) {
        this.cameraElevation = cameraElevation;
    }
    
    public float getCameraElevation() {
        return this.cameraElevation;
    }
    
    public void setCameraAzimuth(final float cameraAzimuth) {
        this.cameraAzimuth = cameraAzimuth;
    }
    
    public float getCameraAzimuth() {
        return this.cameraAzimuth;
    }
    
    public void setWindSpeed(final float windSpeed) {
        this.windSpeed = windSpeed;
    }
    
    public float getWindSpeed() {
        return this.windSpeed;
    }
    
    @Override
    public BufferedImage filter(final BufferedImage src, BufferedImage dst) {
        final long start = System.currentTimeMillis();
        this.sunR = (this.sunColor >> 16 & 0xFF) * 0.003921569f;
        this.sunG = (this.sunColor >> 8 & 0xFF) * 0.003921569f;
        this.sunB = (this.sunColor & 0xFF) * 0.003921569f;
        this.mn = 10000.0f;
        this.mx = -10000.0f;
        this.exponents = new float[(int)this.octaves + 1];
        float frequency = 1.0f;
        for (int i = 0; i <= (int)this.octaves; ++i) {
            this.exponents[i] = (float)Math.pow(2.0, -i);
            frequency *= this.lacunarity;
        }
        this.min = -1.0f;
        this.max = 1.0f;
        this.width = (float)src.getWidth();
        this.height = (float)src.getHeight();
        final int h = src.getHeight();
        this.tan = new float[h];
        for (int j = 0; j < h; ++j) {
            this.tan[j] = (float)Math.tan(this.fov * j / h * 3.141592653589793 * 0.5);
        }
        if (dst == null) {
            dst = this.createCompatibleDestImage(src, null);
        }
        final int t = (int)(63.0f * this.time);
        final Graphics2D g = dst.createGraphics();
        g.drawImage(this.skyColors, 0, 0, dst.getWidth(), dst.getHeight(), t, 0, t + 1, 64, null);
        g.dispose();
        final BufferedImage clouds = super.filter(dst, dst);
        final long finish = System.currentTimeMillis();
        System.out.println(this.mn + " " + this.mx + " " + (finish - start) * 0.001f);
        this.exponents = null;
        this.tan = null;
        return dst;
    }
    
    public float evaluate(float x, float y) {
        float value = 0.0f;
        x += 371.0f;
        y += 529.0f;
        int i;
        for (i = 0; i < (int)this.octaves; ++i) {
            value += Noise.noise3(x, y, this.t) * this.exponents[i];
            x *= this.lacunarity;
            y *= this.lacunarity;
        }
        final float remainder = this.octaves - (int)this.octaves;
        if (remainder != 0.0f) {
            value += remainder * Noise.noise3(x, y, this.t) * this.exponents[i];
        }
        return value;
    }
    
    @Override
    public int filterRGB(final int x, final int y, final int rgb) {
        final float fx = x / this.width;
        final float fy = y / this.height;
        final float haze = (float)Math.pow(this.haziness, 100.0f * fy * fy);
        float r = (rgb >> 16 & 0xFF) * 0.003921569f;
        float g = (rgb >> 8 & 0xFF) * 0.003921569f;
        float b = (rgb & 0xFF) * 0.003921569f;
        final float cx = this.width * 0.5f;
        float nx = x - cx;
        float ny = (float)y;
        ny = this.tan[y];
        nx = (fx - 0.5f) * (1.0f + ny);
        ny += this.t * this.windSpeed;
        nx /= this.scale;
        ny /= this.scale * this.stretch;
        final float fg;
        float f = fg = this.evaluate(nx, ny);
        f = (f + 1.23f) / 2.46f;
        final int a = rgb & 0xFF000000;
        float c = f - this.cloudCover;
        if (c < 0.0f) {
            c = 0.0f;
        }
        float cloudAlpha = 1.0f - (float)Math.pow(this.cloudSharpness, c);
        this.mn = Math.min(this.mn, cloudAlpha);
        this.mx = Math.max(this.mx, cloudAlpha);
        final float centreX = this.width * this.sunAzimuth;
        final float centreY = this.height * this.sunElevation;
        final float dx = x - centreX;
        final float dy = y - centreY;
        float distance2 = dx * dx + dy * dy;
        distance2 = (float)Math.pow(distance2, this.glowFalloff);
        final float sun = 10.0f * (float)Math.exp(-distance2 * this.glow * 0.1f);
        r += sun * this.sunR;
        g += sun * this.sunG;
        b += sun * this.sunB;
        final float ca = (1.0f - cloudAlpha * cloudAlpha * cloudAlpha * cloudAlpha) * this.amount;
        final float cloudR = this.sunR * ca;
        final float cloudG = this.sunG * ca;
        final float cloudB = this.sunB * ca;
        cloudAlpha *= haze;
        final float iCloudAlpha = 1.0f - cloudAlpha;
        r = iCloudAlpha * r + cloudAlpha * cloudR;
        g = iCloudAlpha * g + cloudAlpha * cloudG;
        b = iCloudAlpha * b + cloudAlpha * cloudB;
        final float exposure = this.gain;
        r = 1.0f - (float)Math.exp(-r * exposure);
        g = 1.0f - (float)Math.exp(-g * exposure);
        b = 1.0f - (float)Math.exp(-b * exposure);
        final int ir = (int)(255.0f * r) << 16;
        final int ig = (int)(255.0f * g) << 8;
        final int ib = (int)(255.0f * b);
        final int v = 0xFF000000 | ir | ig | ib;
        return v;
    }
    
    @Override
    public String toString() {
        return "Texture/Sky...";
    }
}
