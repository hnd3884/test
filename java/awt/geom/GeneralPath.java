package java.awt.geom;

import java.awt.Shape;

public final class GeneralPath extends Float
{
    private static final long serialVersionUID = -8327096662768731142L;
    
    public GeneralPath() {
        super(1, 20);
    }
    
    public GeneralPath(final int n) {
        super(n, 20);
    }
    
    public GeneralPath(final int n, final int n2) {
        super(n, n2);
    }
    
    public GeneralPath(final Shape shape) {
        super(shape, null);
    }
    
    GeneralPath(final int windingRule, final byte[] pointTypes, final int numTypes, final float[] floatCoords, final int numCoords) {
        this.windingRule = windingRule;
        this.pointTypes = pointTypes;
        this.numTypes = numTypes;
        this.floatCoords = floatCoords;
        this.numCoords = numCoords;
    }
}
