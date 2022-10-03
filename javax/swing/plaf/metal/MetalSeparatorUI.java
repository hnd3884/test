package javax.swing.plaf.metal;

import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.LookAndFeel;
import javax.swing.JSeparator;
import javax.swing.plaf.ComponentUI;
import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicSeparatorUI;

public class MetalSeparatorUI extends BasicSeparatorUI
{
    public static ComponentUI createUI(final JComponent component) {
        return new MetalSeparatorUI();
    }
    
    @Override
    protected void installDefaults(final JSeparator separator) {
        LookAndFeel.installColors(separator, "Separator.background", "Separator.foreground");
    }
    
    @Override
    public void paint(final Graphics graphics, final JComponent component) {
        final Dimension size = component.getSize();
        if (((JSeparator)component).getOrientation() == 1) {
            graphics.setColor(component.getForeground());
            graphics.drawLine(0, 0, 0, size.height);
            graphics.setColor(component.getBackground());
            graphics.drawLine(1, 0, 1, size.height);
        }
        else {
            graphics.setColor(component.getForeground());
            graphics.drawLine(0, 0, size.width, 0);
            graphics.setColor(component.getBackground());
            graphics.drawLine(0, 1, size.width, 1);
        }
    }
    
    @Override
    public Dimension getPreferredSize(final JComponent component) {
        if (((JSeparator)component).getOrientation() == 1) {
            return new Dimension(2, 0);
        }
        return new Dimension(0, 2);
    }
}
