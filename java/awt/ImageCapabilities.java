package java.awt;

public class ImageCapabilities implements Cloneable
{
    private boolean accelerated;
    
    public ImageCapabilities(final boolean accelerated) {
        this.accelerated = false;
        this.accelerated = accelerated;
    }
    
    public boolean isAccelerated() {
        return this.accelerated;
    }
    
    public boolean isTrueVolatile() {
        return false;
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
