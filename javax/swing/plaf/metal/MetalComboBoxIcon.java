package javax.swing.plaf.metal;

import java.awt.Color;
import javax.swing.JComponent;
import java.awt.Graphics;
import java.awt.Component;
import java.io.Serializable;
import javax.swing.Icon;

public class MetalComboBoxIcon implements Icon, Serializable
{
    @Override
    public void paintIcon(final Component component, final Graphics graphics, final int n, final int n2) {
        final JComponent component2 = (JComponent)component;
        final int iconWidth = this.getIconWidth();
        graphics.translate(n, n2);
        graphics.setColor(component2.isEnabled() ? MetalLookAndFeel.getControlInfo() : MetalLookAndFeel.getControlShadow());
        graphics.drawLine(0, 0, iconWidth - 1, 0);
        graphics.drawLine(1, 1, 1 + (iconWidth - 3), 1);
        graphics.drawLine(2, 2, 2 + (iconWidth - 5), 2);
        graphics.drawLine(3, 3, 3 + (iconWidth - 7), 3);
        graphics.drawLine(4, 4, 4 + (iconWidth - 9), 4);
        graphics.translate(-n, -n2);
    }
    
    @Override
    public int getIconWidth() {
        return 10;
    }
    
    @Override
    public int getIconHeight() {
        return 5;
    }
}
