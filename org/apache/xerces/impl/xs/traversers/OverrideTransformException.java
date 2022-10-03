package org.apache.xerces.impl.xs.traversers;

public class OverrideTransformException extends Exception
{
    private static final long serialVersionUID = 5800328170981546685L;
    
    public OverrideTransformException() {
    }
    
    public OverrideTransformException(final String s) {
        super(s);
    }
    
    public OverrideTransformException(final Throwable t) {
        super(t);
    }
    
    public OverrideTransformException(final String s, final Throwable t) {
        super(s, t);
    }
}
