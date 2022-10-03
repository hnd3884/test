package org.apache.commons.math3.geometry.euclidean.threed;

import org.apache.commons.math3.exception.MathUnsupportedOperationException;
import org.apache.commons.math3.geometry.euclidean.twod.Euclidean2D;
import org.apache.commons.math3.geometry.Space;
import java.io.Serializable;

public class Euclidean3D implements Serializable, Space
{
    private static final long serialVersionUID = 6249091865814886817L;
    
    private Euclidean3D() {
    }
    
    public static Euclidean3D getInstance() {
        return LazyHolder.INSTANCE;
    }
    
    public int getDimension() {
        return 3;
    }
    
    public Euclidean2D getSubSpace() {
        return Euclidean2D.getInstance();
    }
    
    private Object readResolve() {
        return LazyHolder.INSTANCE;
    }
    
    private static class LazyHolder
    {
        private static final Euclidean3D INSTANCE;
        
        static {
            INSTANCE = new Euclidean3D(null);
        }
    }
}
