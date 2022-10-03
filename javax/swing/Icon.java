package javax.swing;

import java.awt.Graphics;
import java.awt.Component;

public interface Icon
{
    void paintIcon(final Component p0, final Graphics p1, final int p2, final int p3);
    
    int getIconWidth();
    
    int getIconHeight();
}
