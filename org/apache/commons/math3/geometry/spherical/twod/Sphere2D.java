package org.apache.commons.math3.geometry.spherical.twod;

import org.apache.commons.math3.exception.MathUnsupportedOperationException;
import org.apache.commons.math3.geometry.spherical.oned.Sphere1D;
import org.apache.commons.math3.geometry.Space;
import java.io.Serializable;

public class Sphere2D implements Serializable, Space
{
    private static final long serialVersionUID = 20131218L;
    
    private Sphere2D() {
    }
    
    public static Sphere2D getInstance() {
        return LazyHolder.INSTANCE;
    }
    
    public int getDimension() {
        return 2;
    }
    
    public Sphere1D getSubSpace() {
        return Sphere1D.getInstance();
    }
    
    private Object readResolve() {
        return LazyHolder.INSTANCE;
    }
    
    private static class LazyHolder
    {
        private static final Sphere2D INSTANCE;
        
        static {
            INSTANCE = new Sphere2D(null);
        }
    }
}
