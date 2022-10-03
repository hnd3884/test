package javax.swing.border;

import java.awt.Insets;
import java.awt.Graphics;
import java.awt.Component;

public interface Border
{
    void paintBorder(final Component p0, final Graphics p1, final int p2, final int p3, final int p4, final int p5);
    
    Insets getBorderInsets(final Component p0);
    
    boolean isBorderOpaque();
}
