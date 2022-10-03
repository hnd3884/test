package org.jfree.text;

import java.io.ObjectOutputStream;
import java.io.IOException;
import org.jfree.io.SerialUtilities;
import java.io.ObjectInputStream;
import java.awt.geom.Rectangle2D;
import org.jfree.ui.Size2D;
import java.awt.font.LineMetrics;
import java.awt.FontMetrics;
import java.awt.Graphics;
import org.jfree.ui.TextAnchor;
import java.awt.Graphics2D;
import org.jfree.util.Log;
import java.awt.Color;
import org.jfree.util.LogContext;
import java.awt.Paint;
import java.awt.Font;
import java.io.Serializable;

public class TextFragment implements Serializable
{
    private static final long serialVersionUID = 4465945952903143262L;
    public static final Font DEFAULT_FONT;
    public static final Paint DEFAULT_PAINT;
    private String text;
    private Font font;
    private Paint paint;
    private float baselineOffset;
    protected static final LogContext logger;
    static /* synthetic */ Class class$org$jfree$text$TextFragment;
    
    static {
        DEFAULT_FONT = new Font("Serif", 0, 12);
        DEFAULT_PAINT = Color.black;
        logger = Log.createContext((TextFragment.class$org$jfree$text$TextFragment != null) ? TextFragment.class$org$jfree$text$TextFragment : (TextFragment.class$org$jfree$text$TextFragment = class$("org.jfree.text.TextFragment")));
    }
    
    public TextFragment(final String text) {
        this(text, TextFragment.DEFAULT_FONT, TextFragment.DEFAULT_PAINT);
    }
    
    public TextFragment(final String text, final Font font) {
        this(text, font, TextFragment.DEFAULT_PAINT);
    }
    
    public TextFragment(final String text, final Font font, final Paint paint) {
        this(text, font, paint, 0.0f);
    }
    
    public TextFragment(final String text, final Font font, final Paint paint, final float baselineOffset) {
        if (text == null) {
            throw new IllegalArgumentException("Null 'text' argument.");
        }
        if (font == null) {
            throw new IllegalArgumentException("Null 'font' argument.");
        }
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");
        }
        this.text = text;
        this.font = font;
        this.paint = paint;
        this.baselineOffset = baselineOffset;
    }
    
    public float calculateBaselineOffset(final Graphics2D g2, final TextAnchor anchor) {
        float result = 0.0f;
        final FontMetrics fm = g2.getFontMetrics(this.font);
        final LineMetrics lm = fm.getLineMetrics("ABCxyz", g2);
        if (anchor == TextAnchor.TOP_LEFT || anchor == TextAnchor.TOP_CENTER || anchor == TextAnchor.TOP_RIGHT) {
            result = lm.getAscent();
        }
        else if (anchor == TextAnchor.BOTTOM_LEFT || anchor == TextAnchor.BOTTOM_CENTER || anchor == TextAnchor.BOTTOM_RIGHT) {
            result = -lm.getDescent() - lm.getLeading();
        }
        return result;
    }
    
    public Size2D calculateDimensions(final Graphics2D g2) {
        final FontMetrics fm = g2.getFontMetrics(this.font);
        final Rectangle2D bounds = TextUtilities.getTextBounds(this.text, g2, fm);
        final Size2D result = new Size2D(bounds.getWidth(), bounds.getHeight());
        return result;
    }
    
    static /* synthetic */ Class class$(final String class$) {
        try {
            return Class.forName(class$);
        }
        catch (final ClassNotFoundException forName) {
            throw new NoClassDefFoundError(forName.getMessage());
        }
    }
    
    public void draw(final Graphics2D g2, final float anchorX, final float anchorY, final TextAnchor anchor, final float rotateX, final float rotateY, final double angle) {
        g2.setFont(this.font);
        g2.setPaint(this.paint);
        TextUtilities.drawRotatedString(this.text, g2, anchorX, anchorY + this.baselineOffset, anchor, angle, rotateX, rotateY);
    }
    
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj instanceof TextFragment) {
            final TextFragment tf = (TextFragment)obj;
            return this.text.equals(tf.text) && this.font.equals(tf.font) && this.paint.equals(tf.paint);
        }
        return false;
    }
    
    public float getBaselineOffset() {
        return this.baselineOffset;
    }
    
    public Font getFont() {
        return this.font;
    }
    
    public Paint getPaint() {
        return this.paint;
    }
    
    public String getText() {
        return this.text;
    }
    
    public int hashCode() {
        int result = (this.text != null) ? this.text.hashCode() : 0;
        result = 29 * result + ((this.font != null) ? this.font.hashCode() : 0);
        result = 29 * result + ((this.paint != null) ? this.paint.hashCode() : 0);
        return result;
    }
    
    private void readObject(final ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.paint = SerialUtilities.readPaint(stream);
    }
    
    private void writeObject(final ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtilities.writePaint(this.paint, stream);
    }
}
