package java.awt;

import java.util.Arrays;
import sun.java2d.pipe.RenderingEngine;
import java.beans.ConstructorProperties;

public class BasicStroke implements Stroke
{
    public static final int JOIN_MITER = 0;
    public static final int JOIN_ROUND = 1;
    public static final int JOIN_BEVEL = 2;
    public static final int CAP_BUTT = 0;
    public static final int CAP_ROUND = 1;
    public static final int CAP_SQUARE = 2;
    float width;
    int join;
    int cap;
    float miterlimit;
    float[] dash;
    float dash_phase;
    
    @ConstructorProperties({ "lineWidth", "endCap", "lineJoin", "miterLimit", "dashArray", "dashPhase" })
    public BasicStroke(final float width, final int cap, final int join, final float miterlimit, final float[] array, final float dash_phase) {
        if (width < 0.0f) {
            throw new IllegalArgumentException("negative width");
        }
        if (cap != 0 && cap != 1 && cap != 2) {
            throw new IllegalArgumentException("illegal end cap value");
        }
        if (join == 0) {
            if (miterlimit < 1.0f) {
                throw new IllegalArgumentException("miter limit < 1");
            }
        }
        else if (join != 1 && join != 2) {
            throw new IllegalArgumentException("illegal line join value");
        }
        if (array != null) {
            if (dash_phase < 0.0f) {
                throw new IllegalArgumentException("negative dash phase");
            }
            boolean b = true;
            for (int i = 0; i < array.length; ++i) {
                final float n = array[i];
                if (n > 0.0) {
                    b = false;
                }
                else if (n < 0.0) {
                    throw new IllegalArgumentException("negative dash length");
                }
            }
            if (b) {
                throw new IllegalArgumentException("dash lengths all zero");
            }
        }
        this.width = width;
        this.cap = cap;
        this.join = join;
        this.miterlimit = miterlimit;
        if (array != null) {
            this.dash = array.clone();
        }
        this.dash_phase = dash_phase;
    }
    
    public BasicStroke(final float n, final int n2, final int n3, final float n4) {
        this(n, n2, n3, n4, null, 0.0f);
    }
    
    public BasicStroke(final float n, final int n2, final int n3) {
        this(n, n2, n3, 10.0f, null, 0.0f);
    }
    
    public BasicStroke(final float n) {
        this(n, 2, 0, 10.0f, null, 0.0f);
    }
    
    public BasicStroke() {
        this(1.0f, 2, 0, 10.0f, null, 0.0f);
    }
    
    @Override
    public Shape createStrokedShape(final Shape shape) {
        return RenderingEngine.getInstance().createStrokedShape(shape, this.width, this.cap, this.join, this.miterlimit, this.dash, this.dash_phase);
    }
    
    public float getLineWidth() {
        return this.width;
    }
    
    public int getEndCap() {
        return this.cap;
    }
    
    public int getLineJoin() {
        return this.join;
    }
    
    public float getMiterLimit() {
        return this.miterlimit;
    }
    
    public float[] getDashArray() {
        if (this.dash == null) {
            return null;
        }
        return this.dash.clone();
    }
    
    public float getDashPhase() {
        return this.dash_phase;
    }
    
    @Override
    public int hashCode() {
        int n = ((Float.floatToIntBits(this.width) * 31 + this.join) * 31 + this.cap) * 31 + Float.floatToIntBits(this.miterlimit);
        if (this.dash != null) {
            n = n * 31 + Float.floatToIntBits(this.dash_phase);
            for (int i = 0; i < this.dash.length; ++i) {
                n = n * 31 + Float.floatToIntBits(this.dash[i]);
            }
        }
        return n;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof BasicStroke)) {
            return false;
        }
        final BasicStroke basicStroke = (BasicStroke)o;
        if (this.width != basicStroke.width) {
            return false;
        }
        if (this.join != basicStroke.join) {
            return false;
        }
        if (this.cap != basicStroke.cap) {
            return false;
        }
        if (this.miterlimit != basicStroke.miterlimit) {
            return false;
        }
        if (this.dash != null) {
            if (this.dash_phase != basicStroke.dash_phase) {
                return false;
            }
            if (!Arrays.equals(this.dash, basicStroke.dash)) {
                return false;
            }
        }
        else if (basicStroke.dash != null) {
            return false;
        }
        return true;
    }
}
