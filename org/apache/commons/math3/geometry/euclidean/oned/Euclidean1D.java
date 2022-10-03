package org.apache.commons.math3.geometry.euclidean.oned;

import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.exception.MathUnsupportedOperationException;
import org.apache.commons.math3.geometry.Space;
import java.io.Serializable;

public class Euclidean1D implements Serializable, Space
{
    private static final long serialVersionUID = -1178039568877797126L;
    
    private Euclidean1D() {
    }
    
    public static Euclidean1D getInstance() {
        return LazyHolder.INSTANCE;
    }
    
    public int getDimension() {
        return 1;
    }
    
    public Space getSubSpace() throws NoSubSpaceException {
        throw new NoSubSpaceException();
    }
    
    private Object readResolve() {
        return LazyHolder.INSTANCE;
    }
    
    private static class LazyHolder
    {
        private static final Euclidean1D INSTANCE;
        
        static {
            INSTANCE = new Euclidean1D(null);
        }
    }
    
    public static class NoSubSpaceException extends MathUnsupportedOperationException
    {
        private static final long serialVersionUID = 20140225L;
        
        public NoSubSpaceException() {
            super(LocalizedFormats.NOT_SUPPORTED_IN_DIMENSION_N, new Object[] { 1 });
        }
    }
}
