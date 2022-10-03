package java.awt.peer;

public interface ScrollbarPeer extends ComponentPeer
{
    void setValues(final int p0, final int p1, final int p2, final int p3);
    
    void setLineIncrement(final int p0);
    
    void setPageIncrement(final int p0);
}
