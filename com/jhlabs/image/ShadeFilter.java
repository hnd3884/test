package com.jhlabs.image;

import java.awt.image.Kernel;
import com.jhlabs.vecmath.Tuple3f;
import com.jhlabs.math.ImageFunction2D;
import java.awt.Rectangle;
import com.jhlabs.vecmath.Color4f;
import com.jhlabs.vecmath.Vector3f;
import java.awt.image.BufferedImage;
import com.jhlabs.math.Function2D;

public class ShadeFilter extends WholeImageFilter
{
    public static final int COLORS_FROM_IMAGE = 0;
    public static final int COLORS_CONSTANT = 1;
    public static final int BUMPS_FROM_IMAGE = 0;
    public static final int BUMPS_FROM_IMAGE_ALPHA = 1;
    public static final int BUMPS_FROM_MAP = 2;
    public static final int BUMPS_FROM_BEVEL = 3;
    private float bumpHeight;
    private float bumpSoftness;
    private float viewDistance;
    private int colorSource;
    private int bumpSource;
    private Function2D bumpFunction;
    private BufferedImage environmentMap;
    private int[] envPixels;
    private int envWidth;
    private int envHeight;
    private Vector3f l;
    private Vector3f v;
    private Vector3f n;
    private Color4f shadedColor;
    private Color4f diffuse_color;
    private Color4f specular_color;
    private Vector3f tmpv;
    private Vector3f tmpv2;
    protected static final float r255 = 0.003921569f;
    
    public ShadeFilter() {
        this.viewDistance = 10000.0f;
        this.colorSource = 0;
        this.bumpSource = 0;
        this.envWidth = 1;
        this.envHeight = 1;
        this.bumpHeight = 1.0f;
        this.bumpSoftness = 5.0f;
        this.l = new Vector3f();
        this.v = new Vector3f();
        this.n = new Vector3f();
        this.shadedColor = new Color4f();
        this.diffuse_color = new Color4f();
        this.specular_color = new Color4f();
        this.tmpv = new Vector3f();
        this.tmpv2 = new Vector3f();
    }
    
    public void setBumpFunction(final Function2D bumpFunction) {
        this.bumpFunction = bumpFunction;
    }
    
    public Function2D getBumpFunction() {
        return this.bumpFunction;
    }
    
    public void setBumpHeight(final float bumpHeight) {
        this.bumpHeight = bumpHeight;
    }
    
    public float getBumpHeight() {
        return this.bumpHeight;
    }
    
    public void setBumpSoftness(final float bumpSoftness) {
        this.bumpSoftness = bumpSoftness;
    }
    
    public float getBumpSoftness() {
        return this.bumpSoftness;
    }
    
    public void setEnvironmentMap(final BufferedImage environmentMap) {
        this.environmentMap = environmentMap;
        if (environmentMap != null) {
            this.envWidth = environmentMap.getWidth();
            this.envHeight = environmentMap.getHeight();
            this.envPixels = this.getRGB(environmentMap, 0, 0, this.envWidth, this.envHeight, null);
        }
        else {
            final int n = 1;
            this.envHeight = n;
            this.envWidth = n;
            this.envPixels = null;
        }
    }
    
    public BufferedImage getEnvironmentMap() {
        return this.environmentMap;
    }
    
    public void setBumpSource(final int bumpSource) {
        this.bumpSource = bumpSource;
    }
    
    public int getBumpSource() {
        return this.bumpSource;
    }
    
    protected void setFromRGB(final Color4f c, final int argb) {
        c.set((argb >> 16 & 0xFF) * 0.003921569f, (argb >> 8 & 0xFF) * 0.003921569f, (argb & 0xFF) * 0.003921569f, (argb >> 24 & 0xFF) * 0.003921569f);
    }
    
    @Override
    protected int[] filterPixels(final int width, final int height, final int[] inPixels, final Rectangle transformedSpace) {
        int index = 0;
        final int[] outPixels = new int[width * height];
        final float width2 = Math.abs(6.0f * this.bumpHeight);
        final boolean invertBumps = this.bumpHeight < 0.0f;
        final Vector3f position = new Vector3f(0.0f, 0.0f, 0.0f);
        final Vector3f viewpoint = new Vector3f(width / 2.0f, height / 2.0f, this.viewDistance);
        final Vector3f normal = new Vector3f();
        final Color4f c = new Color4f();
        Function2D bump = this.bumpFunction;
        if (this.bumpSource == 0 || this.bumpSource == 1 || this.bumpSource == 2 || bump == null) {
            if (this.bumpSoftness != 0.0f) {
                int bumpWidth = width;
                int bumpHeight = height;
                int[] bumpPixels = inPixels;
                if (this.bumpSource == 2 && this.bumpFunction instanceof ImageFunction2D) {
                    final ImageFunction2D if2d = (ImageFunction2D)this.bumpFunction;
                    bumpWidth = if2d.getWidth();
                    bumpHeight = if2d.getHeight();
                    bumpPixels = if2d.getPixels();
                }
                final Kernel kernel = GaussianFilter.makeKernel(this.bumpSoftness);
                final int[] tmpPixels = new int[bumpWidth * bumpHeight];
                final int[] softPixels = new int[bumpWidth * bumpHeight];
                GaussianFilter.convolveAndTranspose(kernel, bumpPixels, tmpPixels, bumpWidth, bumpHeight, true, false, false, ConvolveFilter.CLAMP_EDGES);
                GaussianFilter.convolveAndTranspose(kernel, tmpPixels, softPixels, bumpHeight, bumpWidth, true, false, false, ConvolveFilter.CLAMP_EDGES);
                bump = new ImageFunction2D(softPixels, bumpWidth, bumpHeight, 1, this.bumpSource == 1);
            }
            else {
                bump = new ImageFunction2D(inPixels, width, height, 1, this.bumpSource == 1);
            }
        }
        final Vector3f v1 = new Vector3f();
        final Vector3f v2 = new Vector3f();
        final Vector3f n = new Vector3f();
        for (int y = 0; y < height; ++y) {
            final float ny = (float)y;
            position.y = (float)y;
            for (int x = 0; x < width; ++x) {
                final float nx = (float)x;
                if (this.bumpSource != 3) {
                    int count = 0;
                    final Vector3f vector3f = normal;
                    final Vector3f vector3f2 = normal;
                    final Vector3f vector3f3 = normal;
                    final float x2 = 0.0f;
                    vector3f3.z = x2;
                    vector3f2.y = x2;
                    vector3f.x = x2;
                    final float m0 = width2 * bump.evaluate(nx, ny);
                    final float m2 = (x > 0) ? (width2 * bump.evaluate(nx - 1.0f, ny) - m0) : -2.0f;
                    final float m3 = (y > 0) ? (width2 * bump.evaluate(nx, ny - 1.0f) - m0) : -2.0f;
                    final float m4 = (x < width - 1) ? (width2 * bump.evaluate(nx + 1.0f, ny) - m0) : -2.0f;
                    final float m5 = (y < height - 1) ? (width2 * bump.evaluate(nx, ny + 1.0f) - m0) : -2.0f;
                    if (m2 != -2.0f && m5 != -2.0f) {
                        v1.x = -1.0f;
                        v1.y = 0.0f;
                        v1.z = m2;
                        v2.x = 0.0f;
                        v2.y = 1.0f;
                        v2.z = m5;
                        n.cross(v1, v2);
                        n.normalize();
                        if (n.z < 0.0) {
                            n.z = -n.z;
                        }
                        normal.add(n);
                        ++count;
                    }
                    if (m2 != -2.0f && m3 != -2.0f) {
                        v1.x = -1.0f;
                        v1.y = 0.0f;
                        v1.z = m2;
                        v2.x = 0.0f;
                        v2.y = -1.0f;
                        v2.z = m3;
                        n.cross(v1, v2);
                        n.normalize();
                        if (n.z < 0.0) {
                            n.z = -n.z;
                        }
                        normal.add(n);
                        ++count;
                    }
                    if (m3 != -2.0f && m4 != -2.0f) {
                        v1.x = 0.0f;
                        v1.y = -1.0f;
                        v1.z = m3;
                        v2.x = 1.0f;
                        v2.y = 0.0f;
                        v2.z = m4;
                        n.cross(v1, v2);
                        n.normalize();
                        if (n.z < 0.0) {
                            n.z = -n.z;
                        }
                        normal.add(n);
                        ++count;
                    }
                    if (m4 != -2.0f && m5 != -2.0f) {
                        v1.x = 1.0f;
                        v1.y = 0.0f;
                        v1.z = m4;
                        v2.x = 0.0f;
                        v2.y = 1.0f;
                        v2.z = m5;
                        n.cross(v1, v2);
                        n.normalize();
                        if (n.z < 0.0) {
                            n.z = -n.z;
                        }
                        normal.add(n);
                        ++count;
                    }
                    final Vector3f vector3f4 = normal;
                    vector3f4.x /= count;
                    final Vector3f vector3f5 = normal;
                    vector3f5.y /= count;
                    final Vector3f vector3f6 = normal;
                    vector3f6.z /= count;
                }
                if (invertBumps) {
                    normal.x = -normal.x;
                    normal.y = -normal.y;
                }
                position.x = (float)x;
                if (normal.z >= 0.0f) {
                    if (this.environmentMap != null) {
                        this.tmpv2.set(viewpoint);
                        this.tmpv2.sub(position);
                        this.tmpv2.normalize();
                        this.tmpv.set(normal);
                        this.tmpv.normalize();
                        this.tmpv.scale(2.0f * this.tmpv.dot(this.tmpv2));
                        this.tmpv.sub(this.v);
                        this.tmpv.normalize();
                        this.setFromRGB(c, this.getEnvironmentMapP(normal, inPixels, width, height));
                        final int alpha = inPixels[index] & 0xFF000000;
                        final int rgb = (int)(c.x * 255.0f) << 16 | (int)(c.y * 255.0f) << 8 | (int)(c.z * 255.0f);
                        outPixels[index++] = (alpha | rgb);
                    }
                    else {
                        outPixels[index++] = 0;
                    }
                }
                else {
                    outPixels[index++] = 0;
                }
            }
        }
        return outPixels;
    }
    
    private int getEnvironmentMapP(final Vector3f normal, final int[] inPixels, final int width, final int height) {
        if (this.environmentMap != null) {
            float x = 0.5f * (1.0f + normal.x);
            float y = 0.5f * (1.0f + normal.y);
            x = ImageMath.clamp(x * this.envWidth, 0.0f, (float)(this.envWidth - 1));
            y = ImageMath.clamp(y * this.envHeight, 0.0f, (float)(this.envHeight - 1));
            final int ix = (int)x;
            final int iy = (int)y;
            final float xWeight = x - ix;
            final float yWeight = y - iy;
            final int i = this.envWidth * iy + ix;
            final int dx = (ix != this.envWidth - 1) ? 1 : 0;
            final int dy = (iy == this.envHeight - 1) ? 0 : this.envWidth;
            return ImageMath.bilinearInterpolate(xWeight, yWeight, this.envPixels[i], this.envPixels[i + dx], this.envPixels[i + dy], this.envPixels[i + dx + dy]);
        }
        return 0;
    }
    
    @Override
    public String toString() {
        return "Stylize/Shade...";
    }
}
