package com.jhlabs.image;

import java.awt.Rectangle;
import com.jhlabs.math.Noise;
import java.util.Random;
import com.jhlabs.math.Function2D;

public class CellularFilter extends WholeImageFilter implements Function2D, Cloneable
{
    protected float scale;
    protected float stretch;
    protected float angle;
    public float amount;
    public float turbulence;
    public float gain;
    public float bias;
    public float distancePower;
    public boolean useColor;
    protected Colormap colormap;
    protected float[] coefficients;
    protected float angleCoefficient;
    protected Random random;
    protected float m00;
    protected float m01;
    protected float m10;
    protected float m11;
    protected Point[] results;
    protected float randomness;
    protected int gridType;
    private float min;
    private float max;
    private static byte[] probabilities;
    private float gradientCoefficient;
    public static final int RANDOM = 0;
    public static final int SQUARE = 1;
    public static final int HEXAGONAL = 2;
    public static final int OCTAGONAL = 3;
    public static final int TRIANGULAR = 4;
    
    public CellularFilter() {
        this.scale = 32.0f;
        this.stretch = 1.0f;
        this.angle = 0.0f;
        this.amount = 1.0f;
        this.turbulence = 1.0f;
        this.gain = 0.5f;
        this.bias = 0.5f;
        this.distancePower = 2.0f;
        this.useColor = false;
        this.colormap = new Gradient();
        this.coefficients = new float[] { 1.0f, 0.0f, 0.0f, 0.0f };
        this.random = new Random();
        this.m00 = 1.0f;
        this.m01 = 0.0f;
        this.m10 = 0.0f;
        this.m11 = 1.0f;
        this.results = null;
        this.randomness = 0.0f;
        this.gridType = 2;
        this.results = new Point[3];
        for (int j = 0; j < this.results.length; ++j) {
            this.results[j] = new Point();
        }
        if (CellularFilter.probabilities == null) {
            CellularFilter.probabilities = new byte[8192];
            float factorial = 1.0f;
            float total = 0.0f;
            final float mean = 2.5f;
            for (int i = 0; i < 10; ++i) {
                if (i > 1) {
                    factorial *= i;
                }
                final float probability = (float)Math.pow(mean, i) * (float)Math.exp(-mean) / factorial;
                final int start = (int)(total * 8192.0f);
                total += probability;
                for (int end = (int)(total * 8192.0f), k = start; k < end; ++k) {
                    CellularFilter.probabilities[k] = (byte)i;
                }
            }
        }
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
        final float cos = (float)Math.cos(angle);
        final float sin = (float)Math.sin(angle);
        this.m00 = cos;
        this.m01 = sin;
        this.m10 = -sin;
        this.m11 = cos;
    }
    
    public float getAngle() {
        return this.angle;
    }
    
    public void setCoefficient(final int i, final float v) {
        this.coefficients[i] = v;
    }
    
    public float getCoefficient(final int i) {
        return this.coefficients[i];
    }
    
    public void setAngleCoefficient(final float angleCoefficient) {
        this.angleCoefficient = angleCoefficient;
    }
    
    public float getAngleCoefficient() {
        return this.angleCoefficient;
    }
    
    public void setGradientCoefficient(final float gradientCoefficient) {
        this.gradientCoefficient = gradientCoefficient;
    }
    
    public float getGradientCoefficient() {
        return this.gradientCoefficient;
    }
    
    public void setF1(final float v) {
        this.coefficients[0] = v;
    }
    
    public float getF1() {
        return this.coefficients[0];
    }
    
    public void setF2(final float v) {
        this.coefficients[1] = v;
    }
    
    public float getF2() {
        return this.coefficients[1];
    }
    
    public void setF3(final float v) {
        this.coefficients[2] = v;
    }
    
    public float getF3() {
        return this.coefficients[2];
    }
    
    public void setF4(final float v) {
        this.coefficients[3] = v;
    }
    
    public float getF4() {
        return this.coefficients[3];
    }
    
    public void setColormap(final Colormap colormap) {
        this.colormap = colormap;
    }
    
    public Colormap getColormap() {
        return this.colormap;
    }
    
    public void setRandomness(final float randomness) {
        this.randomness = randomness;
    }
    
    public float getRandomness() {
        return this.randomness;
    }
    
    public void setGridType(final int gridType) {
        this.gridType = gridType;
    }
    
    public int getGridType() {
        return this.gridType;
    }
    
    public void setDistancePower(final float distancePower) {
        this.distancePower = distancePower;
    }
    
    public float getDistancePower() {
        return this.distancePower;
    }
    
    public void setTurbulence(final float turbulence) {
        this.turbulence = turbulence;
    }
    
    public float getTurbulence() {
        return this.turbulence;
    }
    
    public void setAmount(final float amount) {
        this.amount = amount;
    }
    
    public float getAmount() {
        return this.amount;
    }
    
    private float checkCube(final float x, final float y, final int cubeX, final int cubeY, final Point[] results) {
        this.random.setSeed(571 * cubeX + 23 * cubeY);
        int numPoints = 0;
        switch (this.gridType) {
            default: {
                numPoints = CellularFilter.probabilities[this.random.nextInt() & 0x1FFF];
                break;
            }
            case 1: {
                numPoints = 1;
                break;
            }
            case 2: {
                numPoints = 1;
                break;
            }
            case 3: {
                numPoints = 2;
                break;
            }
            case 4: {
                numPoints = 2;
                break;
            }
        }
        for (int i = 0; i < numPoints; ++i) {
            float px = 0.0f;
            float py = 0.0f;
            float weight = 1.0f;
            switch (this.gridType) {
                case 0: {
                    px = this.random.nextFloat();
                    py = this.random.nextFloat();
                    break;
                }
                case 1: {
                    py = (px = 0.5f);
                    if (this.randomness != 0.0f) {
                        px += (float)(this.randomness * (this.random.nextFloat() - 0.5));
                        py += (float)(this.randomness * (this.random.nextFloat() - 0.5));
                        break;
                    }
                    break;
                }
                case 2: {
                    if ((cubeX & 0x1) == 0x0) {
                        px = 0.75f;
                        py = 0.0f;
                    }
                    else {
                        px = 0.75f;
                        py = 0.5f;
                    }
                    if (this.randomness != 0.0f) {
                        px += this.randomness * Noise.noise2(271.0f * (cubeX + px), 271.0f * (cubeY + py));
                        py += this.randomness * Noise.noise2(271.0f * (cubeX + px) + 89.0f, 271.0f * (cubeY + py) + 137.0f);
                        break;
                    }
                    break;
                }
                case 3: {
                    switch (i) {
                        case 0: {
                            px = 0.207f;
                            py = 0.207f;
                            break;
                        }
                        case 1: {
                            px = 0.707f;
                            py = 0.707f;
                            weight = 1.6f;
                            break;
                        }
                    }
                    if (this.randomness != 0.0f) {
                        px += this.randomness * Noise.noise2(271.0f * (cubeX + px), 271.0f * (cubeY + py));
                        py += this.randomness * Noise.noise2(271.0f * (cubeX + px) + 89.0f, 271.0f * (cubeY + py) + 137.0f);
                        break;
                    }
                    break;
                }
                case 4: {
                    if ((cubeY & 0x1) == 0x0) {
                        if (i == 0) {
                            px = 0.25f;
                            py = 0.35f;
                        }
                        else {
                            px = 0.75f;
                            py = 0.65f;
                        }
                    }
                    else if (i == 0) {
                        px = 0.75f;
                        py = 0.35f;
                    }
                    else {
                        px = 0.25f;
                        py = 0.65f;
                    }
                    if (this.randomness != 0.0f) {
                        px += this.randomness * Noise.noise2(271.0f * (cubeX + px), 271.0f * (cubeY + py));
                        py += this.randomness * Noise.noise2(271.0f * (cubeX + px) + 89.0f, 271.0f * (cubeY + py) + 137.0f);
                        break;
                    }
                    break;
                }
            }
            float dx = Math.abs(x - px);
            float dy = Math.abs(y - py);
            dx *= weight;
            dy *= weight;
            float d;
            if (this.distancePower == 1.0f) {
                d = dx + dy;
            }
            else if (this.distancePower == 2.0f) {
                d = (float)Math.sqrt(dx * dx + dy * dy);
            }
            else {
                d = (float)Math.pow((float)Math.pow(dx, this.distancePower) + (float)Math.pow(dy, this.distancePower), 1.0f / this.distancePower);
            }
            if (d < results[0].distance) {
                final Point p = results[2];
                results[2] = results[1];
                results[1] = results[0];
                results[0] = p;
                p.distance = d;
                p.dx = dx;
                p.dy = dy;
                p.x = cubeX + px;
                p.y = cubeY + py;
            }
            else if (d < results[1].distance) {
                final Point p = results[2];
                results[2] = results[1];
                results[1] = p;
                p.distance = d;
                p.dx = dx;
                p.dy = dy;
                p.x = cubeX + px;
                p.y = cubeY + py;
            }
            else if (d < results[2].distance) {
                final Point p = results[2];
                p.distance = d;
                p.dx = dx;
                p.dy = dy;
                p.x = cubeX + px;
                p.y = cubeY + py;
            }
        }
        return results[2].distance;
    }
    
    public float evaluate(final float x, final float y) {
        for (int j = 0; j < this.results.length; ++j) {
            this.results[j].distance = Float.POSITIVE_INFINITY;
        }
        final int ix = (int)x;
        final int iy = (int)y;
        final float fx = x - ix;
        final float fy = y - iy;
        float d = this.checkCube(fx, fy, ix, iy, this.results);
        if (d > fy) {
            d = this.checkCube(fx, fy + 1.0f, ix, iy - 1, this.results);
        }
        if (d > 1.0f - fy) {
            d = this.checkCube(fx, fy - 1.0f, ix, iy + 1, this.results);
        }
        if (d > fx) {
            this.checkCube(fx + 1.0f, fy, ix - 1, iy, this.results);
            if (d > fy) {
                d = this.checkCube(fx + 1.0f, fy + 1.0f, ix - 1, iy - 1, this.results);
            }
            if (d > 1.0f - fy) {
                d = this.checkCube(fx + 1.0f, fy - 1.0f, ix - 1, iy + 1, this.results);
            }
        }
        if (d > 1.0f - fx) {
            d = this.checkCube(fx - 1.0f, fy, ix + 1, iy, this.results);
            if (d > fy) {
                d = this.checkCube(fx - 1.0f, fy + 1.0f, ix + 1, iy - 1, this.results);
            }
            if (d > 1.0f - fy) {
                d = this.checkCube(fx - 1.0f, fy - 1.0f, ix + 1, iy + 1, this.results);
            }
        }
        float t = 0.0f;
        for (int i = 0; i < 3; ++i) {
            t += this.coefficients[i] * this.results[i].distance;
        }
        if (this.angleCoefficient != 0.0f) {
            float angle = (float)Math.atan2(y - this.results[0].y, x - this.results[0].x);
            if (angle < 0.0f) {
                angle += 6.2831855f;
            }
            angle /= 12.566371f;
            t += this.angleCoefficient * angle;
        }
        if (this.gradientCoefficient != 0.0f) {
            final float a = 1.0f / (this.results[0].dy + this.results[0].dx);
            t += this.gradientCoefficient * a;
        }
        return t;
    }
    
    public float turbulence2(final float x, final float y, final float freq) {
        float t = 0.0f;
        for (float f = 1.0f; f <= freq; f *= 2.0f) {
            t += this.evaluate(f * x, f * y) / f;
        }
        return t;
    }
    
    public int getPixel(final int x, final int y, final int[] inPixels, final int width, final int height) {
        float nx = this.m00 * x + this.m01 * y;
        float ny = this.m10 * x + this.m11 * y;
        nx /= this.scale;
        ny /= this.scale * this.stretch;
        nx += 1000.0f;
        ny += 1000.0f;
        float f = (this.turbulence == 1.0f) ? this.evaluate(nx, ny) : this.turbulence2(nx, ny, this.turbulence);
        f *= 2.0f;
        f *= this.amount;
        final int a = -16777216;
        if (this.colormap != null) {
            int v = this.colormap.getColor(f);
            if (this.useColor) {
                final int srcx = ImageMath.clamp((int)((this.results[0].x - 1000.0f) * this.scale), 0, width - 1);
                final int srcy = ImageMath.clamp((int)((this.results[0].y - 1000.0f) * this.scale), 0, height - 1);
                v = inPixels[srcy * width + srcx];
                f = (this.results[1].distance - this.results[0].distance) / (this.results[1].distance + this.results[0].distance);
                f = ImageMath.smoothStep(this.coefficients[1], this.coefficients[0], f);
                v = ImageMath.mixColors(f, -16777216, v);
            }
            return v;
        }
        int v = PixelUtils.clamp((int)(f * 255.0f));
        final int r = v << 16;
        final int g = v << 8;
        final int b = v;
        return a | r | g | b;
    }
    
    @Override
    protected int[] filterPixels(final int width, final int height, final int[] inPixels, final Rectangle transformedSpace) {
        int index = 0;
        final int[] outPixels = new int[width * height];
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                outPixels[index++] = this.getPixel(x, y, inPixels, width, height);
            }
        }
        return outPixels;
    }
    
    @Override
    public Object clone() {
        final CellularFilter f = (CellularFilter)super.clone();
        f.coefficients = this.coefficients.clone();
        f.results = this.results.clone();
        f.random = new Random();
        return f;
    }
    
    @Override
    public String toString() {
        return "Texture/Cellular...";
    }
    
    public class Point
    {
        public int index;
        public float x;
        public float y;
        public float dx;
        public float dy;
        public float cubeX;
        public float cubeY;
        public float distance;
    }
}
