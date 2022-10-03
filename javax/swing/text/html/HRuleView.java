package javax.swing.text.html;

import javax.swing.text.ViewFactory;
import javax.swing.event.DocumentEvent;
import javax.swing.text.BadLocationException;
import javax.swing.text.Position;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Graphics;
import javax.swing.text.StyleConstants;
import javax.swing.text.Element;
import javax.swing.text.AttributeSet;
import javax.swing.text.View;

class HRuleView extends View
{
    private float topMargin;
    private float bottomMargin;
    private float leftMargin;
    private float rightMargin;
    private int alignment;
    private String noshade;
    private int size;
    private CSS.LengthValue widthValue;
    private static final int SPACE_ABOVE = 3;
    private static final int SPACE_BELOW = 3;
    private AttributeSet attr;
    
    public HRuleView(final Element element) {
        super(element);
        this.alignment = 1;
        this.noshade = null;
        this.size = 0;
        this.setPropertiesFromAttributes();
    }
    
    protected void setPropertiesFromAttributes() {
        final StyleSheet styleSheet = ((HTMLDocument)this.getDocument()).getStyleSheet();
        final AttributeSet attributes = this.getElement().getAttributes();
        this.attr = styleSheet.getViewAttributes(this);
        this.alignment = 1;
        this.size = 0;
        this.noshade = null;
        this.widthValue = null;
        if (this.attr != null) {
            if (this.attr.getAttribute(StyleConstants.Alignment) != null) {
                this.alignment = StyleConstants.getAlignment(this.attr);
            }
            this.noshade = (String)attributes.getAttribute(HTML.Attribute.NOSHADE);
            final Object attribute = attributes.getAttribute(HTML.Attribute.SIZE);
            if (attribute != null && attribute instanceof String) {
                try {
                    this.size = Integer.parseInt((String)attribute);
                }
                catch (final NumberFormatException ex) {
                    this.size = 1;
                }
            }
            final Object attribute2 = this.attr.getAttribute(CSS.Attribute.WIDTH);
            if (attribute2 != null && attribute2 instanceof CSS.LengthValue) {
                this.widthValue = (CSS.LengthValue)attribute2;
            }
            this.topMargin = this.getLength(CSS.Attribute.MARGIN_TOP, this.attr);
            this.bottomMargin = this.getLength(CSS.Attribute.MARGIN_BOTTOM, this.attr);
            this.leftMargin = this.getLength(CSS.Attribute.MARGIN_LEFT, this.attr);
            this.rightMargin = this.getLength(CSS.Attribute.MARGIN_RIGHT, this.attr);
        }
        else {
            final float n = 0.0f;
            this.rightMargin = n;
            this.leftMargin = n;
            this.bottomMargin = n;
            this.topMargin = n;
        }
        this.size = Math.max(2, this.size);
    }
    
    private float getLength(final CSS.Attribute attribute, final AttributeSet set) {
        final CSS.LengthValue lengthValue = (CSS.LengthValue)set.getAttribute(attribute);
        return (lengthValue != null) ? lengthValue.getValue() : 0.0f;
    }
    
    @Override
    public void paint(final Graphics graphics, final Shape shape) {
        final Rectangle rectangle = (Rectangle)((shape instanceof Rectangle) ? shape : shape.getBounds());
        final int n = rectangle.y + 3 + (int)this.topMargin;
        int n2 = rectangle.width - (int)(this.leftMargin + this.rightMargin);
        if (this.widthValue != null) {
            n2 = (int)this.widthValue.getValue((float)n2);
        }
        int size = rectangle.height - (6 + (int)this.topMargin + (int)this.bottomMargin);
        if (this.size > 0) {
            size = this.size;
        }
        int n3 = 0;
        switch (this.alignment) {
            case 1: {
                n3 = rectangle.x + rectangle.width / 2 - n2 / 2;
                break;
            }
            case 2: {
                n3 = rectangle.x + rectangle.width - n2 - (int)this.rightMargin;
                break;
            }
            default: {
                n3 = rectangle.x + (int)this.leftMargin;
                break;
            }
        }
        if (this.noshade != null) {
            graphics.setColor(Color.black);
            graphics.fillRect(n3, n, n2, size);
        }
        else {
            final Color background = this.getContainer().getBackground();
            Color color;
            Color color2;
            if (background == null || background.equals(Color.white)) {
                color = Color.darkGray;
                color2 = Color.lightGray;
            }
            else {
                color = Color.darkGray;
                color2 = Color.white;
            }
            graphics.setColor(color2);
            graphics.drawLine(n3 + n2 - 1, n, n3 + n2 - 1, n + size - 1);
            graphics.drawLine(n3, n + size - 1, n3 + n2 - 1, n + size - 1);
            graphics.setColor(color);
            graphics.drawLine(n3, n, n3 + n2 - 1, n);
            graphics.drawLine(n3, n, n3, n + size - 1);
        }
    }
    
    @Override
    public float getPreferredSpan(final int n) {
        switch (n) {
            case 0: {
                return 1.0f;
            }
            case 1: {
                if (this.size > 0) {
                    return this.size + 3 + 3 + this.topMargin + this.bottomMargin;
                }
                if (this.noshade != null) {
                    return 8.0f + this.topMargin + this.bottomMargin;
                }
                return 6.0f + this.topMargin + this.bottomMargin;
            }
            default: {
                throw new IllegalArgumentException("Invalid axis: " + n);
            }
        }
    }
    
    @Override
    public int getResizeWeight(final int n) {
        if (n == 0) {
            return 1;
        }
        if (n == 1) {
            return 0;
        }
        return 0;
    }
    
    @Override
    public int getBreakWeight(final int n, final float n2, final float n3) {
        if (n == 0) {
            return 3000;
        }
        return 0;
    }
    
    @Override
    public View breakView(final int n, final int n2, final float n3, final float n4) {
        return null;
    }
    
    @Override
    public Shape modelToView(final int n, final Shape shape, final Position.Bias bias) throws BadLocationException {
        final int startOffset = this.getStartOffset();
        final int endOffset = this.getEndOffset();
        if (n >= startOffset && n <= endOffset) {
            final Rectangle bounds = shape.getBounds();
            if (n == endOffset) {
                final Rectangle rectangle = bounds;
                rectangle.x += bounds.width;
            }
            bounds.width = 0;
            return bounds;
        }
        return null;
    }
    
    @Override
    public int viewToModel(final float n, final float n2, final Shape shape, final Position.Bias[] array) {
        final Rectangle rectangle = (Rectangle)shape;
        if (n < rectangle.x + rectangle.width / 2) {
            array[0] = Position.Bias.Forward;
            return this.getStartOffset();
        }
        array[0] = Position.Bias.Backward;
        return this.getEndOffset();
    }
    
    @Override
    public AttributeSet getAttributes() {
        return this.attr;
    }
    
    @Override
    public void changedUpdate(final DocumentEvent documentEvent, final Shape shape, final ViewFactory viewFactory) {
        super.changedUpdate(documentEvent, shape, viewFactory);
        final int offset = documentEvent.getOffset();
        if (offset <= this.getStartOffset() && offset + documentEvent.getLength() >= this.getEndOffset()) {
            this.setPropertiesFromAttributes();
        }
    }
}
