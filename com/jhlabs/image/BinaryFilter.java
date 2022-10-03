package com.jhlabs.image;

import com.jhlabs.math.BlackFunction;
import com.jhlabs.math.BinaryFunction;

public abstract class BinaryFilter extends WholeImageFilter
{
    protected int newColor;
    protected BinaryFunction blackFunction;
    protected int iterations;
    protected Colormap colormap;
    
    public BinaryFilter() {
        this.newColor = -16777216;
        this.blackFunction = new BlackFunction();
        this.iterations = 1;
    }
    
    public void setIterations(final int iterations) {
        this.iterations = iterations;
    }
    
    public int getIterations() {
        return this.iterations;
    }
    
    public void setColormap(final Colormap colormap) {
        this.colormap = colormap;
    }
    
    public Colormap getColormap() {
        return this.colormap;
    }
    
    public void setNewColor(final int newColor) {
        this.newColor = newColor;
    }
    
    public int getNewColor() {
        return this.newColor;
    }
    
    public void setBlackFunction(final BinaryFunction blackFunction) {
        this.blackFunction = blackFunction;
    }
    
    public BinaryFunction getBlackFunction() {
        return this.blackFunction;
    }
}
