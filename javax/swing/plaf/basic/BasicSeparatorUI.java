package javax.swing.plaf.basic;

import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.LookAndFeel;
import javax.swing.JSeparator;
import javax.swing.plaf.ComponentUI;
import javax.swing.JComponent;
import java.awt.Color;
import javax.swing.plaf.SeparatorUI;

public class BasicSeparatorUI extends SeparatorUI
{
    protected Color shadow;
    protected Color highlight;
    
    public static ComponentUI createUI(final JComponent component) {
        return new BasicSeparatorUI();
    }
    
    @Override
    public void installUI(final JComponent component) {
        this.installDefaults((JSeparator)component);
        this.installListeners((JSeparator)component);
    }
    
    @Override
    public void uninstallUI(final JComponent component) {
        this.uninstallDefaults((JSeparator)component);
        this.uninstallListeners((JSeparator)component);
    }
    
    protected void installDefaults(final JSeparator separator) {
        LookAndFeel.installColors(separator, "Separator.background", "Separator.foreground");
        LookAndFeel.installProperty(separator, "opaque", Boolean.FALSE);
    }
    
    protected void uninstallDefaults(final JSeparator separator) {
    }
    
    protected void installListeners(final JSeparator separator) {
    }
    
    protected void uninstallListeners(final JSeparator separator) {
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
    
    @Override
    public Dimension getMinimumSize(final JComponent component) {
        return null;
    }
    
    @Override
    public Dimension getMaximumSize(final JComponent component) {
        return null;
    }
}
