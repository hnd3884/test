package javax.swing.plaf;

import java.awt.Rectangle;
import javax.swing.JTabbedPane;

public abstract class TabbedPaneUI extends ComponentUI
{
    public abstract int tabForCoordinate(final JTabbedPane p0, final int p1, final int p2);
    
    public abstract Rectangle getTabBounds(final JTabbedPane p0, final int p1);
    
    public abstract int getTabRunCount(final JTabbedPane p0);
}
