package org.apache.commons.math3.geometry.euclidean.twod;

import org.apache.commons.math3.exception.MathUnsupportedOperationException;
import org.apache.commons.math3.geometry.euclidean.oned.Euclidean1D;
import org.apache.commons.math3.geometry.Space;
import java.io.Serializable;

public class Euclidean2D implements Serializable, Space
{
    private static final long serialVersionUID = 4793432849757649566L;
    
    private Euclidean2D() {
    }
    
    public static Euclidean2D getInstance() {
        return LazyHolder.INSTANCE;
    }
    
    public int getDimension() {
        return 2;
    }
    
    public Euclidean1D getSubSpace() {
        return Euclidean1D.getInstance();
    }
    
    private Object readResolve() {
        return LazyHolder.INSTANCE;
    }
    
    private static class LazyHolder
    {
        private static final Euclidean2D INSTANCE;
        
        static {
            INSTANCE = new Euclidean2D(null);
        }
    }
}
