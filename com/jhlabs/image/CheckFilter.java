package com.jhlabs.image;

public class CheckFilter extends PointFilter
{
    private int xScale;
    private int yScale;
    private int foreground;
    private int background;
    private int fuzziness;
    private float angle;
    private float m00;
    private float m01;
    private float m10;
    private float m11;
    
    public CheckFilter() {
        this.xScale = 8;
        this.yScale = 8;
        this.foreground = -1;
        this.background = -16777216;
        this.fuzziness = 0;
        this.angle = 0.0f;
        this.m00 = 1.0f;
        this.m01 = 0.0f;
        this.m10 = 0.0f;
        this.m11 = 1.0f;
    }
    
    public void setForeground(final int foreground) {
        this.foreground = foreground;
    }
    
    public int getForeground() {
        return this.foreground;
    }
    
    public void setBackground(final int background) {
        this.background = background;
    }
    
    public int getBackground() {
        return this.background;
    }
    
    public void setXScale(final int xScale) {
        this.xScale = xScale;
    }
    
    public int getXScale() {
        return this.xScale;
    }
    
    public void setYScale(final int yScale) {
        this.yScale = yScale;
    }
    
    public int getYScale() {
        return this.yScale;
    }
    
    public void setFuzziness(final int fuzziness) {
        this.fuzziness = fuzziness;
    }
    
    public int getFuzziness() {
        return this.fuzziness;
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
    
    @Override
    public int filterRGB(final int x, final int y, final int rgb) {
        final float nx = (this.m00 * x + this.m01 * y) / this.xScale;
        final float ny = (this.m10 * x + this.m11 * y) / this.yScale;
        float f = ((int)(nx + 100000.0f) % 2 != (int)(ny + 100000.0f) % 2) ? 1.0f : 0.0f;
        if (this.fuzziness != 0) {
            final float fuzz = this.fuzziness / 100.0f;
            final float fx = ImageMath.smoothPulse(0.0f, fuzz, 1.0f - fuzz, 1.0f, ImageMath.mod(nx, 1.0f));
            final float fy = ImageMath.smoothPulse(0.0f, fuzz, 1.0f - fuzz, 1.0f, ImageMath.mod(ny, 1.0f));
            f *= fx * fy;
        }
        return ImageMath.mixColors(f, this.foreground, this.background);
    }
    
    @Override
    public String toString() {
        return "Texture/Checkerboard...";
    }
}
