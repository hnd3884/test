package javax.swing.plaf.metal;

import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.plaf.ComponentUI;
import javax.swing.JComponent;

public class MetalPopupMenuSeparatorUI extends MetalSeparatorUI
{
    public static ComponentUI createUI(final JComponent component) {
        return new MetalPopupMenuSeparatorUI();
    }
    
    @Override
    public void paint(final Graphics graphics, final JComponent component) {
        final Dimension size = component.getSize();
        graphics.setColor(component.getForeground());
        graphics.drawLine(0, 1, size.width, 1);
        graphics.setColor(component.getBackground());
        graphics.drawLine(0, 2, size.width, 2);
        graphics.drawLine(0, 0, 0, 0);
        graphics.drawLine(0, 3, 0, 3);
    }
    
    @Override
    public Dimension getPreferredSize(final JComponent component) {
        return new Dimension(0, 4);
    }
}
