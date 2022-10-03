package javax.swing.text.html;

import javax.swing.text.ViewFactory;
import javax.swing.event.DocumentEvent;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Graphics;
import javax.swing.SizeRequirements;
import javax.swing.text.View;
import javax.swing.text.Element;
import javax.swing.text.AttributeSet;
import javax.swing.text.BoxView;

public class BlockView extends BoxView
{
    private AttributeSet attr;
    private StyleSheet.BoxPainter painter;
    private CSS.LengthValue cssWidth;
    private CSS.LengthValue cssHeight;
    
    public BlockView(final Element element, final int n) {
        super(element, n);
    }
    
    @Override
    public void setParent(final View parent) {
        super.setParent(parent);
        if (parent != null) {
            this.setPropertiesFromAttributes();
        }
    }
    
    @Override
    protected SizeRequirements calculateMajorAxisRequirements(final int n, SizeRequirements calculateMajorAxisRequirements) {
        if (calculateMajorAxisRequirements == null) {
            calculateMajorAxisRequirements = new SizeRequirements();
        }
        if (!spanSetFromAttributes(n, calculateMajorAxisRequirements, this.cssWidth, this.cssHeight)) {
            calculateMajorAxisRequirements = super.calculateMajorAxisRequirements(n, calculateMajorAxisRequirements);
        }
        else {
            final SizeRequirements calculateMajorAxisRequirements2 = super.calculateMajorAxisRequirements(n, null);
            final int n2 = (n == 0) ? (this.getLeftInset() + this.getRightInset()) : (this.getTopInset() + this.getBottomInset());
            final SizeRequirements sizeRequirements = calculateMajorAxisRequirements;
            sizeRequirements.minimum -= n2;
            final SizeRequirements sizeRequirements2 = calculateMajorAxisRequirements;
            sizeRequirements2.preferred -= n2;
            final SizeRequirements sizeRequirements3 = calculateMajorAxisRequirements;
            sizeRequirements3.maximum -= n2;
            this.constrainSize(n, calculateMajorAxisRequirements, calculateMajorAxisRequirements2);
        }
        return calculateMajorAxisRequirements;
    }
    
    @Override
    protected SizeRequirements calculateMinorAxisRequirements(final int n, SizeRequirements calculateMinorAxisRequirements) {
        if (calculateMinorAxisRequirements == null) {
            calculateMinorAxisRequirements = new SizeRequirements();
        }
        if (!spanSetFromAttributes(n, calculateMinorAxisRequirements, this.cssWidth, this.cssHeight)) {
            calculateMinorAxisRequirements = super.calculateMinorAxisRequirements(n, calculateMinorAxisRequirements);
        }
        else {
            final SizeRequirements calculateMinorAxisRequirements2 = super.calculateMinorAxisRequirements(n, null);
            final int n2 = (n == 0) ? (this.getLeftInset() + this.getRightInset()) : (this.getTopInset() + this.getBottomInset());
            final SizeRequirements sizeRequirements = calculateMinorAxisRequirements;
            sizeRequirements.minimum -= n2;
            final SizeRequirements sizeRequirements2 = calculateMinorAxisRequirements;
            sizeRequirements2.preferred -= n2;
            final SizeRequirements sizeRequirements3 = calculateMinorAxisRequirements;
            sizeRequirements3.maximum -= n2;
            this.constrainSize(n, calculateMinorAxisRequirements, calculateMinorAxisRequirements2);
        }
        if (n == 0) {
            final Object attribute = this.getAttributes().getAttribute(CSS.Attribute.TEXT_ALIGN);
            if (attribute != null) {
                final String string = attribute.toString();
                if (string.equals("center")) {
                    calculateMinorAxisRequirements.alignment = 0.5f;
                }
                else if (string.equals("right")) {
                    calculateMinorAxisRequirements.alignment = 1.0f;
                }
                else {
                    calculateMinorAxisRequirements.alignment = 0.0f;
                }
            }
        }
        return calculateMinorAxisRequirements;
    }
    
    boolean isPercentage(final int n, final AttributeSet set) {
        if (n == 0) {
            if (this.cssWidth != null) {
                return this.cssWidth.isPercentage();
            }
        }
        else if (this.cssHeight != null) {
            return this.cssHeight.isPercentage();
        }
        return false;
    }
    
    static boolean spanSetFromAttributes(final int n, final SizeRequirements sizeRequirements, final CSS.LengthValue lengthValue, final CSS.LengthValue lengthValue2) {
        if (n == 0) {
            if (lengthValue != null && !lengthValue.isPercentage()) {
                final int minimum = (int)lengthValue.getValue();
                sizeRequirements.maximum = minimum;
                sizeRequirements.preferred = minimum;
                sizeRequirements.minimum = minimum;
                return true;
            }
        }
        else if (lengthValue2 != null && !lengthValue2.isPercentage()) {
            final int minimum2 = (int)lengthValue2.getValue();
            sizeRequirements.maximum = minimum2;
            sizeRequirements.preferred = minimum2;
            sizeRequirements.minimum = minimum2;
            return true;
        }
        return false;
    }
    
    @Override
    protected void layoutMinorAxis(final int n, final int n2, final int[] array, final int[] array2) {
        final int viewCount = this.getViewCount();
        final CSS.Attribute attribute = (n2 == 0) ? CSS.Attribute.WIDTH : CSS.Attribute.HEIGHT;
        for (int i = 0; i < viewCount; ++i) {
            final View view = this.getView(i);
            int n3 = (int)view.getMinimumSpan(n2);
            final CSS.LengthValue lengthValue = (CSS.LengthValue)view.getAttributes().getAttribute(attribute);
            int max;
            if (lengthValue != null && lengthValue.isPercentage()) {
                n3 = (max = Math.max((int)lengthValue.getValue((float)n), n3));
            }
            else {
                max = (int)view.getMaximumSpan(n2);
            }
            if (max < n) {
                array[i] = (int)((n - max) * view.getAlignment(n2));
                array2[i] = max;
            }
            else {
                array[i] = 0;
                array2[i] = Math.max(n3, n);
            }
        }
    }
    
    @Override
    public void paint(final Graphics graphics, final Shape shape) {
        final Rectangle rectangle = (Rectangle)shape;
        this.painter.paint(graphics, (float)rectangle.x, (float)rectangle.y, (float)rectangle.width, (float)rectangle.height, this);
        super.paint(graphics, rectangle);
    }
    
    @Override
    public AttributeSet getAttributes() {
        if (this.attr == null) {
            this.attr = this.getStyleSheet().getViewAttributes(this);
        }
        return this.attr;
    }
    
    @Override
    public int getResizeWeight(final int n) {
        switch (n) {
            case 0: {
                return 1;
            }
            case 1: {
                return 0;
            }
            default: {
                throw new IllegalArgumentException("Invalid axis: " + n);
            }
        }
    }
    
    @Override
    public float getAlignment(final int n) {
        switch (n) {
            case 0: {
                return 0.0f;
            }
            case 1: {
                if (this.getViewCount() == 0) {
                    return 0.0f;
                }
                final float preferredSpan = this.getPreferredSpan(1);
                final View view = this.getView(0);
                final float preferredSpan2 = view.getPreferredSpan(1);
                return ((int)preferredSpan != 0) ? (preferredSpan2 * view.getAlignment(1) / preferredSpan) : 0.0f;
            }
            default: {
                throw new IllegalArgumentException("Invalid axis: " + n);
            }
        }
    }
    
    @Override
    public void changedUpdate(final DocumentEvent documentEvent, final Shape shape, final ViewFactory viewFactory) {
        super.changedUpdate(documentEvent, shape, viewFactory);
        final int offset = documentEvent.getOffset();
        if (offset <= this.getStartOffset() && offset + documentEvent.getLength() >= this.getEndOffset()) {
            this.setPropertiesFromAttributes();
        }
    }
    
    @Override
    public float getPreferredSpan(final int n) {
        return super.getPreferredSpan(n);
    }
    
    @Override
    public float getMinimumSpan(final int n) {
        return super.getMinimumSpan(n);
    }
    
    @Override
    public float getMaximumSpan(final int n) {
        return super.getMaximumSpan(n);
    }
    
    protected void setPropertiesFromAttributes() {
        final StyleSheet styleSheet = this.getStyleSheet();
        this.attr = styleSheet.getViewAttributes(this);
        this.painter = styleSheet.getBoxPainter(this.attr);
        if (this.attr != null) {
            this.setInsets((short)this.painter.getInset(1, this), (short)this.painter.getInset(2, this), (short)this.painter.getInset(3, this), (short)this.painter.getInset(4, this));
        }
        this.cssWidth = (CSS.LengthValue)this.attr.getAttribute(CSS.Attribute.WIDTH);
        this.cssHeight = (CSS.LengthValue)this.attr.getAttribute(CSS.Attribute.HEIGHT);
    }
    
    protected StyleSheet getStyleSheet() {
        return ((HTMLDocument)this.getDocument()).getStyleSheet();
    }
    
    private void constrainSize(final int n, final SizeRequirements sizeRequirements, final SizeRequirements sizeRequirements2) {
        if (sizeRequirements2.minimum > sizeRequirements.minimum) {
            final int minimum = sizeRequirements2.minimum;
            sizeRequirements.preferred = minimum;
            sizeRequirements.minimum = minimum;
            sizeRequirements.maximum = Math.max(sizeRequirements.maximum, sizeRequirements2.maximum);
        }
    }
}
