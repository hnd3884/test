package com.jhlabs.image;

import java.awt.Point;
import java.awt.Rectangle;

public class RotateFilter extends TransformFilter
{
    private float angle;
    private float cos;
    private float sin;
    private boolean resize;
    
    public RotateFilter() {
        this(3.1415927f);
    }
    
    public RotateFilter(final float angle) {
        this(angle, true);
    }
    
    public RotateFilter(final float angle, final boolean resize) {
        this.resize = true;
        this.setAngle(angle);
        this.resize = resize;
    }
    
    public void setAngle(final float angle) {
        this.angle = angle;
        this.cos = (float)Math.cos(this.angle);
        this.sin = (float)Math.sin(this.angle);
    }
    
    public float getAngle() {
        return this.angle;
    }
    
    @Override
    protected void transformSpace(final Rectangle rect) {
        if (this.resize) {
            final Point out = new Point(0, 0);
            int minx = Integer.MAX_VALUE;
            int miny = Integer.MAX_VALUE;
            int maxx = Integer.MIN_VALUE;
            int maxy = Integer.MIN_VALUE;
            final int w = rect.width;
            final int h = rect.height;
            final int x = rect.x;
            final int y = rect.y;
            for (int i = 0; i < 4; ++i) {
                switch (i) {
                    case 0: {
                        this.transform(x, y, out);
                        break;
                    }
                    case 1: {
                        this.transform(x + w, y, out);
                        break;
                    }
                    case 2: {
                        this.transform(x, y + h, out);
                        break;
                    }
                    case 3: {
                        this.transform(x + w, y + h, out);
                        break;
                    }
                }
                minx = Math.min(minx, out.x);
                miny = Math.min(miny, out.y);
                maxx = Math.max(maxx, out.x);
                maxy = Math.max(maxy, out.y);
            }
            rect.x = minx;
            rect.y = miny;
            rect.width = maxx - rect.x;
            rect.height = maxy - rect.y;
        }
    }
    
    private void transform(final int x, final int y, final Point out) {
        out.x = (int)(x * this.cos + y * this.sin);
        out.y = (int)(y * this.cos - x * this.sin);
    }
    
    @Override
    protected void transformInverse(final int x, final int y, final float[] out) {
        out[0] = x * this.cos - y * this.sin;
        out[1] = y * this.cos + x * this.sin;
    }
    
    @Override
    public String toString() {
        return "Rotate " + (int)(this.angle * 180.0f / 3.141592653589793);
    }
}
