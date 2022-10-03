package com.jhlabs.image;

public class CrystallizeFilter extends CellularFilter
{
    private float edgeThickness;
    private boolean fadeEdges;
    private int edgeColor;
    
    public CrystallizeFilter() {
        this.edgeThickness = 0.4f;
        this.fadeEdges = false;
        this.edgeColor = -16777216;
        this.setScale(16.0f);
        this.setRandomness(0.0f);
    }
    
    public void setEdgeThickness(final float edgeThickness) {
        this.edgeThickness = edgeThickness;
    }
    
    public float getEdgeThickness() {
        return this.edgeThickness;
    }
    
    public void setFadeEdges(final boolean fadeEdges) {
        this.fadeEdges = fadeEdges;
    }
    
    public boolean getFadeEdges() {
        return this.fadeEdges;
    }
    
    public void setEdgeColor(final int edgeColor) {
        this.edgeColor = edgeColor;
    }
    
    public int getEdgeColor() {
        return this.edgeColor;
    }
    
    @Override
    public int getPixel(final int x, final int y, final int[] inPixels, final int width, final int height) {
        float nx = this.m00 * x + this.m01 * y;
        float ny = this.m10 * x + this.m11 * y;
        nx /= this.scale;
        ny /= this.scale * this.stretch;
        nx += 1000.0f;
        ny += 1000.0f;
        float f = this.evaluate(nx, ny);
        final float f2 = this.results[0].distance;
        final float f3 = this.results[1].distance;
        int srcx = ImageMath.clamp((int)((this.results[0].x - 1000.0f) * this.scale), 0, width - 1);
        int srcy = ImageMath.clamp((int)((this.results[0].y - 1000.0f) * this.scale), 0, height - 1);
        int v = inPixels[srcy * width + srcx];
        f = (f3 - f2) / this.edgeThickness;
        f = ImageMath.smoothStep(0.0f, this.edgeThickness, f);
        if (this.fadeEdges) {
            srcx = ImageMath.clamp((int)((this.results[1].x - 1000.0f) * this.scale), 0, width - 1);
            srcy = ImageMath.clamp((int)((this.results[1].y - 1000.0f) * this.scale), 0, height - 1);
            int v2 = inPixels[srcy * width + srcx];
            v2 = ImageMath.mixColors(0.5f, v2, v);
            v = ImageMath.mixColors(f, v2, v);
        }
        else {
            v = ImageMath.mixColors(f, this.edgeColor, v);
        }
        return v;
    }
    
    @Override
    public String toString() {
        return "Stylize/Crystallize...";
    }
}
