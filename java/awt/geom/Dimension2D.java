package java.awt.geom;

public abstract class Dimension2D implements Cloneable
{
    protected Dimension2D() {
    }
    
    public abstract double getWidth();
    
    public abstract double getHeight();
    
    public abstract void setSize(final double p0, final double p1);
    
    public void setSize(final Dimension2D dimension2D) {
        this.setSize(dimension2D.getWidth(), dimension2D.getHeight());
    }
    
    public Object clone() {
        try {
            return super.clone();
        }
        catch (final CloneNotSupportedException ex) {
            throw new InternalError(ex);
        }
    }
}
