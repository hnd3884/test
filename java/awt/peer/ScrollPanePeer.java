package java.awt.peer;

import java.awt.Adjustable;

public interface ScrollPanePeer extends ContainerPeer
{
    int getHScrollbarHeight();
    
    int getVScrollbarWidth();
    
    void setScrollPosition(final int p0, final int p1);
    
    void childResized(final int p0, final int p1);
    
    void setUnitIncrement(final Adjustable p0, final int p1);
    
    void setValue(final Adjustable p0, final int p1);
}
