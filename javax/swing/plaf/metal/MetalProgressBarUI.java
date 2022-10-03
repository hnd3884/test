package javax.swing.plaf.metal;

import java.awt.Insets;
import java.awt.Color;
import java.awt.Stroke;
import java.awt.BasicStroke;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Graphics;
import javax.swing.plaf.ComponentUI;
import javax.swing.JComponent;
import java.awt.Rectangle;
import javax.swing.plaf.basic.BasicProgressBarUI;

public class MetalProgressBarUI extends BasicProgressBarUI
{
    private Rectangle innards;
    private Rectangle box;
    
    public static ComponentUI createUI(final JComponent component) {
        return new MetalProgressBarUI();
    }
    
    public void paintDeterminate(final Graphics graphics, final JComponent component) {
        super.paintDeterminate(graphics, component);
        if (!(graphics instanceof Graphics2D)) {
            return;
        }
        if (this.progressBar.isBorderPainted()) {
            final Insets insets = this.progressBar.getInsets();
            final int n = this.progressBar.getWidth() - (insets.left + insets.right);
            final int n2 = this.progressBar.getHeight() - (insets.top + insets.bottom);
            final int amountFull = this.getAmountFull(insets, n, n2);
            final boolean leftToRight = MetalUtils.isLeftToRight(component);
            final int left = insets.left;
            final int top = insets.top;
            final int n3 = insets.left + n - 1;
            final int n4 = insets.top + n2 - 1;
            final Graphics2D graphics2D = (Graphics2D)graphics;
            graphics2D.setStroke(new BasicStroke(1.0f));
            if (this.progressBar.getOrientation() == 0) {
                graphics2D.setColor(MetalLookAndFeel.getControlShadow());
                graphics2D.drawLine(left, top, n3, top);
                if (amountFull > 0) {
                    graphics2D.setColor(MetalLookAndFeel.getPrimaryControlDarkShadow());
                    if (leftToRight) {
                        graphics2D.drawLine(left, top, left + amountFull - 1, top);
                    }
                    else {
                        graphics2D.drawLine(n3, top, n3 - amountFull + 1, top);
                        if (this.progressBar.getPercentComplete() != 1.0) {
                            graphics2D.setColor(MetalLookAndFeel.getControlShadow());
                        }
                    }
                }
                graphics2D.drawLine(left, top, left, n4);
            }
            else {
                graphics2D.setColor(MetalLookAndFeel.getControlShadow());
                graphics2D.drawLine(left, top, left, n4);
                if (amountFull > 0) {
                    graphics2D.setColor(MetalLookAndFeel.getPrimaryControlDarkShadow());
                    graphics2D.drawLine(left, n4, left, n4 - amountFull + 1);
                }
                graphics2D.setColor(MetalLookAndFeel.getControlShadow());
                if (this.progressBar.getPercentComplete() == 1.0) {
                    graphics2D.setColor(MetalLookAndFeel.getPrimaryControlDarkShadow());
                }
                graphics2D.drawLine(left, top, n3, top);
            }
        }
    }
    
    public void paintIndeterminate(final Graphics graphics, final JComponent component) {
        super.paintIndeterminate(graphics, component);
        if (!this.progressBar.isBorderPainted() || !(graphics instanceof Graphics2D)) {
            return;
        }
        final Insets insets = this.progressBar.getInsets();
        final int n = this.progressBar.getWidth() - (insets.left + insets.right);
        final int n2 = this.progressBar.getHeight() - (insets.top + insets.bottom);
        this.getAmountFull(insets, n, n2);
        MetalUtils.isLeftToRight(component);
        final Rectangle box = this.getBox(null);
        final int left = insets.left;
        final int top = insets.top;
        final int n3 = insets.left + n - 1;
        final int n4 = insets.top + n2 - 1;
        final Graphics2D graphics2D = (Graphics2D)graphics;
        graphics2D.setStroke(new BasicStroke(1.0f));
        if (this.progressBar.getOrientation() == 0) {
            graphics2D.setColor(MetalLookAndFeel.getControlShadow());
            graphics2D.drawLine(left, top, n3, top);
            graphics2D.drawLine(left, top, left, n4);
            graphics2D.setColor(MetalLookAndFeel.getPrimaryControlDarkShadow());
            graphics2D.drawLine(box.x, top, box.x + box.width - 1, top);
        }
        else {
            graphics2D.setColor(MetalLookAndFeel.getControlShadow());
            graphics2D.drawLine(left, top, left, n4);
            graphics2D.drawLine(left, top, n3, top);
            graphics2D.setColor(MetalLookAndFeel.getPrimaryControlDarkShadow());
            graphics2D.drawLine(left, box.y, left, box.y + box.height - 1);
        }
    }
}
