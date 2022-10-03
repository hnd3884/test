package com.sun.media.sound;

public final class ModelStandardTransform implements ModelTransform
{
    public static final boolean DIRECTION_MIN2MAX = false;
    public static final boolean DIRECTION_MAX2MIN = true;
    public static final boolean POLARITY_UNIPOLAR = false;
    public static final boolean POLARITY_BIPOLAR = true;
    public static final int TRANSFORM_LINEAR = 0;
    public static final int TRANSFORM_CONCAVE = 1;
    public static final int TRANSFORM_CONVEX = 2;
    public static final int TRANSFORM_SWITCH = 3;
    public static final int TRANSFORM_ABSOLUTE = 4;
    private boolean direction;
    private boolean polarity;
    private int transform;
    
    public ModelStandardTransform() {
        this.direction = false;
        this.polarity = false;
        this.transform = 0;
    }
    
    public ModelStandardTransform(final boolean direction) {
        this.direction = false;
        this.polarity = false;
        this.transform = 0;
        this.direction = direction;
    }
    
    public ModelStandardTransform(final boolean direction, final boolean polarity) {
        this.direction = false;
        this.polarity = false;
        this.transform = 0;
        this.direction = direction;
        this.polarity = polarity;
    }
    
    public ModelStandardTransform(final boolean direction, final boolean polarity, final int transform) {
        this.direction = false;
        this.polarity = false;
        this.transform = 0;
        this.direction = direction;
        this.polarity = polarity;
        this.transform = transform;
    }
    
    @Override
    public double transform(double n) {
        if (this.direction) {
            n = 1.0 - n;
        }
        if (this.polarity) {
            n = n * 2.0 - 1.0;
        }
        switch (this.transform) {
            case 1: {
                final double signum = Math.signum(n);
                double n2 = -(0.4166666666666667 / Math.log(10.0)) * Math.log(1.0 - Math.abs(n));
                if (n2 < 0.0) {
                    n2 = 0.0;
                }
                else if (n2 > 1.0) {
                    n2 = 1.0;
                }
                return signum * n2;
            }
            case 2: {
                final double signum2 = Math.signum(n);
                double n3 = 1.0 + 0.4166666666666667 / Math.log(10.0) * Math.log(Math.abs(n));
                if (n3 < 0.0) {
                    n3 = 0.0;
                }
                else if (n3 > 1.0) {
                    n3 = 1.0;
                }
                return signum2 * n3;
            }
            case 3: {
                if (this.polarity) {
                    return (n > 0.0) ? 1.0 : -1.0;
                }
                return (n > 0.5) ? 1.0 : 0.0;
            }
            case 4: {
                return Math.abs(n);
            }
            default: {
                return n;
            }
        }
    }
    
    public boolean getDirection() {
        return this.direction;
    }
    
    public void setDirection(final boolean direction) {
        this.direction = direction;
    }
    
    public boolean getPolarity() {
        return this.polarity;
    }
    
    public void setPolarity(final boolean polarity) {
        this.polarity = polarity;
    }
    
    public int getTransform() {
        return this.transform;
    }
    
    public void setTransform(final int transform) {
        this.transform = transform;
    }
}
