package java.awt;

import java.io.Serializable;

public class GridBagLayoutInfo implements Serializable
{
    private static final long serialVersionUID = -4899416460737170217L;
    int width;
    int height;
    int startx;
    int starty;
    int[] minWidth;
    int[] minHeight;
    double[] weightX;
    double[] weightY;
    boolean hasBaseline;
    short[] baselineType;
    int[] maxAscent;
    int[] maxDescent;
    
    GridBagLayoutInfo(final int width, final int height) {
        this.width = width;
        this.height = height;
    }
    
    boolean hasConstantDescent(final int n) {
        return (this.baselineType[n] & 1 << Component.BaselineResizeBehavior.CONSTANT_DESCENT.ordinal()) != 0x0;
    }
    
    boolean hasBaseline(final int n) {
        return this.hasBaseline && this.baselineType[n] != 0;
    }
}
