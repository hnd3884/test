package javax.swing;

import java.awt.Rectangle;
import java.awt.Dimension;

public interface Scrollable
{
    Dimension getPreferredScrollableViewportSize();
    
    int getScrollableUnitIncrement(final Rectangle p0, final int p1, final int p2);
    
    int getScrollableBlockIncrement(final Rectangle p0, final int p1, final int p2);
    
    boolean getScrollableTracksViewportWidth();
    
    boolean getScrollableTracksViewportHeight();
}
