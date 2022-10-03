package javax.swing.plaf.metal;

import javax.swing.ButtonModel;
import java.awt.Color;
import javax.swing.JCheckBox;
import java.awt.Graphics;
import java.awt.Component;
import java.io.Serializable;
import javax.swing.plaf.UIResource;
import javax.swing.Icon;

public class MetalCheckBoxIcon implements Icon, UIResource, Serializable
{
    protected int getControlSize() {
        return 13;
    }
    
    @Override
    public void paintIcon(final Component component, final Graphics graphics, int n, final int n2) {
        final JCheckBox checkBox = (JCheckBox)component;
        final ButtonModel model = checkBox.getModel();
        final int controlSize = this.getControlSize();
        final boolean selected = model.isSelected();
        if (model.isEnabled()) {
            if (checkBox.isBorderPaintedFlat()) {
                graphics.setColor(MetalLookAndFeel.getControlDarkShadow());
                graphics.drawRect(n + 1, n2, controlSize - 1, controlSize - 1);
            }
            if (model.isPressed() && model.isArmed()) {
                if (checkBox.isBorderPaintedFlat()) {
                    graphics.setColor(MetalLookAndFeel.getControlShadow());
                    graphics.fillRect(n + 2, n2 + 1, controlSize - 2, controlSize - 2);
                }
                else {
                    graphics.setColor(MetalLookAndFeel.getControlShadow());
                    graphics.fillRect(n, n2, controlSize - 1, controlSize - 1);
                    MetalUtils.drawPressed3DBorder(graphics, n, n2, controlSize, controlSize);
                }
            }
            else if (!checkBox.isBorderPaintedFlat()) {
                MetalUtils.drawFlush3DBorder(graphics, n, n2, controlSize, controlSize);
            }
            graphics.setColor(MetalLookAndFeel.getControlInfo());
        }
        else {
            graphics.setColor(MetalLookAndFeel.getControlShadow());
            graphics.drawRect(n, n2, controlSize - 1, controlSize - 1);
        }
        if (selected) {
            if (checkBox.isBorderPaintedFlat()) {
                ++n;
            }
            this.drawCheck(component, graphics, n, n2);
        }
    }
    
    protected void drawCheck(final Component component, final Graphics graphics, final int n, final int n2) {
        final int controlSize = this.getControlSize();
        graphics.fillRect(n + 3, n2 + 5, 2, controlSize - 8);
        graphics.drawLine(n + (controlSize - 4), n2 + 3, n + 5, n2 + (controlSize - 6));
        graphics.drawLine(n + (controlSize - 4), n2 + 4, n + 5, n2 + (controlSize - 5));
    }
    
    @Override
    public int getIconWidth() {
        return this.getControlSize();
    }
    
    @Override
    public int getIconHeight() {
        return this.getControlSize();
    }
}
