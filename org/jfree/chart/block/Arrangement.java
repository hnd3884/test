package org.jfree.chart.block;

import org.jfree.ui.Size2D;
import java.awt.Graphics2D;

public interface Arrangement
{
    void add(final Block p0, final Object p1);
    
    Size2D arrange(final BlockContainer p0, final Graphics2D p1, final RectangleConstraint p2);
    
    void clear();
}
