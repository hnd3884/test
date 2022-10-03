package javax.swing.border;

import java.awt.Insets;
import java.awt.Graphics;
import java.awt.Component;
import java.beans.ConstructorProperties;
import java.awt.Color;

public class SoftBevelBorder extends BevelBorder
{
    public SoftBevelBorder(final int n) {
        super(n);
    }
    
    public SoftBevelBorder(final int n, final Color color, final Color color2) {
        super(n, color, color2);
    }
    
    @ConstructorProperties({ "bevelType", "highlightOuterColor", "highlightInnerColor", "shadowOuterColor", "shadowInnerColor" })
    public SoftBevelBorder(final int n, final Color color, final Color color2, final Color color3, final Color color4) {
        super(n, color, color2, color3, color4);
    }
    
    @Override
    public void paintBorder(final Component component, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        final Color color = graphics.getColor();
        graphics.translate(n, n2);
        if (this.bevelType == 0) {
            graphics.setColor(this.getHighlightOuterColor(component));
            graphics.drawLine(0, 0, n3 - 2, 0);
            graphics.drawLine(0, 0, 0, n4 - 2);
            graphics.drawLine(1, 1, 1, 1);
            graphics.setColor(this.getHighlightInnerColor(component));
            graphics.drawLine(2, 1, n3 - 2, 1);
            graphics.drawLine(1, 2, 1, n4 - 2);
            graphics.drawLine(2, 2, 2, 2);
            graphics.drawLine(0, n4 - 1, 0, n4 - 2);
            graphics.drawLine(n3 - 1, 0, n3 - 1, 0);
            graphics.setColor(this.getShadowOuterColor(component));
            graphics.drawLine(2, n4 - 1, n3 - 1, n4 - 1);
            graphics.drawLine(n3 - 1, 2, n3 - 1, n4 - 1);
            graphics.setColor(this.getShadowInnerColor(component));
            graphics.drawLine(n3 - 2, n4 - 2, n3 - 2, n4 - 2);
        }
        else if (this.bevelType == 1) {
            graphics.setColor(this.getShadowOuterColor(component));
            graphics.drawLine(0, 0, n3 - 2, 0);
            graphics.drawLine(0, 0, 0, n4 - 2);
            graphics.drawLine(1, 1, 1, 1);
            graphics.setColor(this.getShadowInnerColor(component));
            graphics.drawLine(2, 1, n3 - 2, 1);
            graphics.drawLine(1, 2, 1, n4 - 2);
            graphics.drawLine(2, 2, 2, 2);
            graphics.drawLine(0, n4 - 1, 0, n4 - 2);
            graphics.drawLine(n3 - 1, 0, n3 - 1, 0);
            graphics.setColor(this.getHighlightOuterColor(component));
            graphics.drawLine(2, n4 - 1, n3 - 1, n4 - 1);
            graphics.drawLine(n3 - 1, 2, n3 - 1, n4 - 1);
            graphics.setColor(this.getHighlightInnerColor(component));
            graphics.drawLine(n3 - 2, n4 - 2, n3 - 2, n4 - 2);
        }
        graphics.translate(-n, -n2);
        graphics.setColor(color);
    }
    
    @Override
    public Insets getBorderInsets(final Component component, final Insets insets) {
        insets.set(3, 3, 3, 3);
        return insets;
    }
    
    @Override
    public boolean isBorderOpaque() {
        return false;
    }
}
