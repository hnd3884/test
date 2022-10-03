package javax.swing.border;

import java.awt.Insets;
import java.awt.Graphics;
import java.awt.Component;
import java.beans.ConstructorProperties;
import java.awt.Color;

public class BevelBorder extends AbstractBorder
{
    public static final int RAISED = 0;
    public static final int LOWERED = 1;
    protected int bevelType;
    protected Color highlightOuter;
    protected Color highlightInner;
    protected Color shadowInner;
    protected Color shadowOuter;
    
    public BevelBorder(final int bevelType) {
        this.bevelType = bevelType;
    }
    
    public BevelBorder(final int n, final Color color, final Color color2) {
        this(n, color.brighter(), color, color2, color2.brighter());
    }
    
    @ConstructorProperties({ "bevelType", "highlightOuterColor", "highlightInnerColor", "shadowOuterColor", "shadowInnerColor" })
    public BevelBorder(final int n, final Color highlightOuter, final Color highlightInner, final Color shadowOuter, final Color shadowInner) {
        this(n);
        this.highlightOuter = highlightOuter;
        this.highlightInner = highlightInner;
        this.shadowOuter = shadowOuter;
        this.shadowInner = shadowInner;
    }
    
    @Override
    public void paintBorder(final Component component, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        if (this.bevelType == 0) {
            this.paintRaisedBevel(component, graphics, n, n2, n3, n4);
        }
        else if (this.bevelType == 1) {
            this.paintLoweredBevel(component, graphics, n, n2, n3, n4);
        }
    }
    
    @Override
    public Insets getBorderInsets(final Component component, final Insets insets) {
        insets.set(2, 2, 2, 2);
        return insets;
    }
    
    public Color getHighlightOuterColor(final Component component) {
        final Color highlightOuterColor = this.getHighlightOuterColor();
        return (highlightOuterColor != null) ? highlightOuterColor : component.getBackground().brighter().brighter();
    }
    
    public Color getHighlightInnerColor(final Component component) {
        final Color highlightInnerColor = this.getHighlightInnerColor();
        return (highlightInnerColor != null) ? highlightInnerColor : component.getBackground().brighter();
    }
    
    public Color getShadowInnerColor(final Component component) {
        final Color shadowInnerColor = this.getShadowInnerColor();
        return (shadowInnerColor != null) ? shadowInnerColor : component.getBackground().darker();
    }
    
    public Color getShadowOuterColor(final Component component) {
        final Color shadowOuterColor = this.getShadowOuterColor();
        return (shadowOuterColor != null) ? shadowOuterColor : component.getBackground().darker().darker();
    }
    
    public Color getHighlightOuterColor() {
        return this.highlightOuter;
    }
    
    public Color getHighlightInnerColor() {
        return this.highlightInner;
    }
    
    public Color getShadowInnerColor() {
        return this.shadowInner;
    }
    
    public Color getShadowOuterColor() {
        return this.shadowOuter;
    }
    
    public int getBevelType() {
        return this.bevelType;
    }
    
    @Override
    public boolean isBorderOpaque() {
        return true;
    }
    
    protected void paintRaisedBevel(final Component component, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        final Color color = graphics.getColor();
        graphics.translate(n, n2);
        graphics.setColor(this.getHighlightOuterColor(component));
        graphics.drawLine(0, 0, 0, n4 - 2);
        graphics.drawLine(1, 0, n3 - 2, 0);
        graphics.setColor(this.getHighlightInnerColor(component));
        graphics.drawLine(1, 1, 1, n4 - 3);
        graphics.drawLine(2, 1, n3 - 3, 1);
        graphics.setColor(this.getShadowOuterColor(component));
        graphics.drawLine(0, n4 - 1, n3 - 1, n4 - 1);
        graphics.drawLine(n3 - 1, 0, n3 - 1, n4 - 2);
        graphics.setColor(this.getShadowInnerColor(component));
        graphics.drawLine(1, n4 - 2, n3 - 2, n4 - 2);
        graphics.drawLine(n3 - 2, 1, n3 - 2, n4 - 3);
        graphics.translate(-n, -n2);
        graphics.setColor(color);
    }
    
    protected void paintLoweredBevel(final Component component, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        final Color color = graphics.getColor();
        graphics.translate(n, n2);
        graphics.setColor(this.getShadowInnerColor(component));
        graphics.drawLine(0, 0, 0, n4 - 1);
        graphics.drawLine(1, 0, n3 - 1, 0);
        graphics.setColor(this.getShadowOuterColor(component));
        graphics.drawLine(1, 1, 1, n4 - 2);
        graphics.drawLine(2, 1, n3 - 2, 1);
        graphics.setColor(this.getHighlightOuterColor(component));
        graphics.drawLine(1, n4 - 1, n3 - 1, n4 - 1);
        graphics.drawLine(n3 - 1, 1, n3 - 1, n4 - 2);
        graphics.setColor(this.getHighlightInnerColor(component));
        graphics.drawLine(2, n4 - 2, n3 - 2, n4 - 2);
        graphics.drawLine(n3 - 2, 2, n3 - 2, n4 - 3);
        graphics.translate(-n, -n2);
        graphics.setColor(color);
    }
}
