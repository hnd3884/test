package javax.swing.plaf;

import java.awt.Graphics;
import javax.swing.JSplitPane;

public abstract class SplitPaneUI extends ComponentUI
{
    public abstract void resetToPreferredSizes(final JSplitPane p0);
    
    public abstract void setDividerLocation(final JSplitPane p0, final int p1);
    
    public abstract int getDividerLocation(final JSplitPane p0);
    
    public abstract int getMinimumDividerLocation(final JSplitPane p0);
    
    public abstract int getMaximumDividerLocation(final JSplitPane p0);
    
    public abstract void finishedPaintingChildren(final JSplitPane p0, final Graphics p1);
}
