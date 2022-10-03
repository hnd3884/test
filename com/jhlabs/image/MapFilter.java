package com.jhlabs.image;

import com.jhlabs.math.Function2D;

public class MapFilter extends TransformFilter
{
    private Function2D xMapFunction;
    private Function2D yMapFunction;
    
    public void setXMapFunction(final Function2D xMapFunction) {
        this.xMapFunction = xMapFunction;
    }
    
    public Function2D getXMapFunction() {
        return this.xMapFunction;
    }
    
    public void setYMapFunction(final Function2D yMapFunction) {
        this.yMapFunction = yMapFunction;
    }
    
    public Function2D getYMapFunction() {
        return this.yMapFunction;
    }
    
    @Override
    protected void transformInverse(final int x, final int y, final float[] out) {
        final float xMap = this.xMapFunction.evaluate((float)x, (float)y);
        final float yMap = this.yMapFunction.evaluate((float)x, (float)y);
        out[0] = xMap * this.transformedSpace.width;
        out[1] = yMap * this.transformedSpace.height;
    }
    
    @Override
    public String toString() {
        return "Distort/Map Coordinates...";
    }
}
