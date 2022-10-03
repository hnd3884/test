package javax.swing.text.html;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Graphics;
import java.awt.Container;
import javax.swing.text.JTextComponent;
import javax.swing.SizeRequirements;
import javax.swing.text.View;
import javax.swing.text.Element;
import javax.swing.text.AttributeSet;

public class ParagraphView extends javax.swing.text.ParagraphView
{
    private AttributeSet attr;
    private StyleSheet.BoxPainter painter;
    private CSS.LengthValue cssWidth;
    private CSS.LengthValue cssHeight;
    
    public ParagraphView(final Element element) {
        super(element);
    }
    
    @Override
    public void setParent(final View parent) {
        super.setParent(parent);
        if (parent != null) {
            this.setPropertiesFromAttributes();
        }
    }
    
    @Override
    public AttributeSet getAttributes() {
        if (this.attr == null) {
            this.attr = this.getStyleSheet().getViewAttributes(this);
        }
        return this.attr;
    }
    
    @Override
    protected void setPropertiesFromAttributes() {
        final StyleSheet styleSheet = this.getStyleSheet();
        this.attr = styleSheet.getViewAttributes(this);
        this.painter = styleSheet.getBoxPainter(this.attr);
        if (this.attr != null) {
            super.setPropertiesFromAttributes();
            this.setInsets((short)this.painter.getInset(1, this), (short)this.painter.getInset(2, this), (short)this.painter.getInset(3, this), (short)this.painter.getInset(4, this));
            final Object attribute = this.attr.getAttribute(CSS.Attribute.TEXT_ALIGN);
            if (attribute != null) {
                final String string = attribute.toString();
                if (string.equals("left")) {
                    this.setJustification(0);
                }
                else if (string.equals("center")) {
                    this.setJustification(1);
                }
                else if (string.equals("right")) {
                    this.setJustification(2);
                }
                else if (string.equals("justify")) {
                    this.setJustification(3);
                }
            }
            this.cssWidth = (CSS.LengthValue)this.attr.getAttribute(CSS.Attribute.WIDTH);
            this.cssHeight = (CSS.LengthValue)this.attr.getAttribute(CSS.Attribute.HEIGHT);
        }
    }
    
    protected StyleSheet getStyleSheet() {
        return ((HTMLDocument)this.getDocument()).getStyleSheet();
    }
    
    @Override
    protected SizeRequirements calculateMinorAxisRequirements(final int n, SizeRequirements calculateMinorAxisRequirements) {
        calculateMinorAxisRequirements = super.calculateMinorAxisRequirements(n, calculateMinorAxisRequirements);
        if (BlockView.spanSetFromAttributes(n, calculateMinorAxisRequirements, this.cssWidth, this.cssHeight)) {
            final int n2 = (n == 0) ? (this.getLeftInset() + this.getRightInset()) : (this.getTopInset() + this.getBottomInset());
            final SizeRequirements sizeRequirements = calculateMinorAxisRequirements;
            sizeRequirements.minimum -= n2;
            final SizeRequirements sizeRequirements2 = calculateMinorAxisRequirements;
            sizeRequirements2.preferred -= n2;
            final SizeRequirements sizeRequirements3 = calculateMinorAxisRequirements;
            sizeRequirements3.maximum -= n2;
        }
        return calculateMinorAxisRequirements;
    }
    
    @Override
    public boolean isVisible() {
        final int n = this.getLayoutViewCount() - 1;
        for (int i = 0; i < n; ++i) {
            if (this.getLayoutView(i).isVisible()) {
                return true;
            }
        }
        if (n > 0) {
            final View layoutView = this.getLayoutView(n);
            if (layoutView.getEndOffset() - layoutView.getStartOffset() == 1) {
                return false;
            }
        }
        if (this.getStartOffset() == this.getDocument().getLength()) {
            boolean editable = false;
            final Container container = this.getContainer();
            if (container instanceof JTextComponent) {
                editable = ((JTextComponent)container).isEditable();
            }
            if (!editable) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public void paint(final Graphics graphics, final Shape shape) {
        if (shape == null) {
            return;
        }
        Rectangle bounds;
        if (shape instanceof Rectangle) {
            bounds = (Rectangle)shape;
        }
        else {
            bounds = shape.getBounds();
        }
        this.painter.paint(graphics, (float)bounds.x, (float)bounds.y, (float)bounds.width, (float)bounds.height, this);
        super.paint(graphics, shape);
    }
    
    @Override
    public float getPreferredSpan(final int n) {
        if (!this.isVisible()) {
            return 0.0f;
        }
        return super.getPreferredSpan(n);
    }
    
    @Override
    public float getMinimumSpan(final int n) {
        if (!this.isVisible()) {
            return 0.0f;
        }
        return super.getMinimumSpan(n);
    }
    
    @Override
    public float getMaximumSpan(final int n) {
        if (!this.isVisible()) {
            return 0.0f;
        }
        return super.getMaximumSpan(n);
    }
}
