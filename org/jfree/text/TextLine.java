package org.jfree.text;

import org.jfree.ui.Size2D;
import java.util.Iterator;
import org.jfree.ui.TextAnchor;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Font;
import java.util.ArrayList;
import org.jfree.util.Log;
import org.jfree.util.LogContext;
import java.util.List;
import java.io.Serializable;

public class TextLine implements Serializable
{
    private static final long serialVersionUID = 7100085690160465444L;
    private List fragments;
    protected static final LogContext logger;
    static /* synthetic */ Class class$org$jfree$text$TextLine;
    
    static {
        logger = Log.createContext((TextLine.class$org$jfree$text$TextLine != null) ? TextLine.class$org$jfree$text$TextLine : (TextLine.class$org$jfree$text$TextLine = class$("org.jfree.text.TextLine")));
    }
    
    public TextLine() {
        this.fragments = new ArrayList();
    }
    
    public TextLine(final String text) {
        this(text, TextFragment.DEFAULT_FONT);
    }
    
    public TextLine(final String text, final Font font) {
        this.fragments = new ArrayList();
        final TextFragment fragment = new TextFragment(text, font);
        this.fragments.add(fragment);
    }
    
    public TextLine(final String text, final Font font, final Paint paint) {
        if (text == null) {
            throw new IllegalArgumentException("Null 'text' argument.");
        }
        if (font == null) {
            throw new IllegalArgumentException("Null 'font' argument.");
        }
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");
        }
        this.fragments = new ArrayList();
        final TextFragment fragment = new TextFragment(text, font, paint);
        this.fragments.add(fragment);
    }
    
    public void addFragment(final TextFragment fragment) {
        this.fragments.add(fragment);
    }
    
    private float calculateBaselineOffset(final Graphics2D g2, final TextAnchor anchor) {
        float result = 0.0f;
        final Iterator iterator = this.fragments.iterator();
        while (iterator.hasNext()) {
            final TextFragment fragment = iterator.next();
            result = Math.max(result, fragment.calculateBaselineOffset(g2, anchor));
        }
        return result;
    }
    
    public Size2D calculateDimensions(final Graphics2D g2) {
        double width = 0.0;
        double height = 0.0;
        final Iterator iterator = this.fragments.iterator();
        while (iterator.hasNext()) {
            final TextFragment fragment = iterator.next();
            final Size2D dimension = fragment.calculateDimensions(g2);
            width += dimension.getWidth();
            height = Math.max(height, dimension.getHeight());
            if (TextLine.logger.isDebugEnabled()) {
                TextLine.logger.debug("width = " + width + ", height = " + height);
            }
        }
        return new Size2D(width, height);
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
        float x = anchorX;
        final float yOffset = this.calculateBaselineOffset(g2, anchor);
        final Iterator iterator = this.fragments.iterator();
        while (iterator.hasNext()) {
            final TextFragment fragment = iterator.next();
            final Size2D d = fragment.calculateDimensions(g2);
            fragment.draw(g2, x, anchorY + yOffset, TextAnchor.BASELINE_LEFT, rotateX, rotateY, angle);
            x += (float)d.getWidth();
        }
    }
    
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj instanceof TextLine) {
            final TextLine line = (TextLine)obj;
            return this.fragments.equals(line.fragments);
        }
        return false;
    }
    
    public TextFragment getFirstTextFragment() {
        TextFragment result = null;
        if (this.fragments.size() > 0) {
            result = this.fragments.get(0);
        }
        return result;
    }
    
    public TextFragment getLastTextFragment() {
        TextFragment result = null;
        if (this.fragments.size() > 0) {
            result = this.fragments.get(this.fragments.size() - 1);
        }
        return result;
    }
    
    public int hashCode() {
        return (this.fragments != null) ? this.fragments.hashCode() : 0;
    }
    
    public void removeFragment(final TextFragment fragment) {
        this.fragments.remove(fragment);
    }
}
