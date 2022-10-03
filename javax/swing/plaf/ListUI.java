package javax.swing.plaf;

import java.awt.Rectangle;
import java.awt.Point;
import javax.swing.JList;

public abstract class ListUI extends ComponentUI
{
    public abstract int locationToIndex(final JList p0, final Point p1);
    
    public abstract Point indexToLocation(final JList p0, final int p1);
    
    public abstract Rectangle getCellBounds(final JList p0, final int p1, final int p2);
}
