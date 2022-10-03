package org.jfree.text;

import java.util.Collections;
import org.jfree.ui.TextAnchor;
import java.util.Iterator;
import org.jfree.ui.Size2D;
import org.jfree.util.ShapeUtilities;
import java.awt.geom.Rectangle2D;
import java.awt.Shape;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Font;
import java.util.ArrayList;
import org.jfree.util.Log;
import org.jfree.util.LogContext;
import org.jfree.ui.HorizontalAlignment;
import java.util.List;
import java.io.Serializable;

public class TextBlock implements Serializable
{
    private static final long serialVersionUID = -4333175719424385526L;
    private List lines;
    private HorizontalAlignment lineAlignment;
    protected static final LogContext logger;
    static /* synthetic */ Class class$org$jfree$text$TextBlock;
    
    static {
        logger = Log.createContext((TextBlock.class$org$jfree$text$TextBlock != null) ? TextBlock.class$org$jfree$text$TextBlock : (TextBlock.class$org$jfree$text$TextBlock = class$("org.jfree.text.TextBlock")));
    }
    
    public TextBlock() {
        this.lines = new ArrayList();
        this.lineAlignment = HorizontalAlignment.CENTER;
    }
    
    public void addLine(final String text, final Font font, final Paint paint) {
        this.addLine(new TextLine(text, font, paint));
    }
    
    public void addLine(final TextLine line) {
        this.lines.add(line);
    }
    
    public Shape calculateBounds(final Graphics2D g2, final float anchorX, final float anchorY, final TextBlockAnchor anchor, final float rotateX, final float rotateY, final double angle) {
        final Size2D d = this.calculateDimensions(g2);
        final float[] offsets = this.calculateOffsets(anchor, d.getWidth(), d.getHeight());
        final Rectangle2D bounds = new Rectangle2D.Double(anchorX + offsets[0], anchorY + offsets[1], d.getWidth(), d.getHeight());
        final Shape rotatedBounds = ShapeUtilities.rotateShape(bounds, angle, rotateX, rotateY);
        return rotatedBounds;
    }
    
    public Size2D calculateDimensions(final Graphics2D g2) {
        double width = 0.0;
        double height = 0.0;
        final Iterator iterator = this.lines.iterator();
        while (iterator.hasNext()) {
            final TextLine line = iterator.next();
            final Size2D dimension = line.calculateDimensions(g2);
            width = Math.max(width, dimension.getWidth());
            height += dimension.getHeight();
        }
        if (TextBlock.logger.isDebugEnabled()) {
            TextBlock.logger.debug("width = " + width + ", height = " + height);
        }
        return new Size2D(width, height);
    }
    
    private float[] calculateOffsets(final TextBlockAnchor anchor, final double width, final double height) {
        final float[] result = new float[2];
        float xAdj = 0.0f;
        float yAdj = 0.0f;
        if (anchor == TextBlockAnchor.TOP_CENTER || anchor == TextBlockAnchor.CENTER || anchor == TextBlockAnchor.BOTTOM_CENTER) {
            xAdj = (float)(-width) / 2.0f;
        }
        else if (anchor == TextBlockAnchor.TOP_RIGHT || anchor == TextBlockAnchor.CENTER_RIGHT || anchor == TextBlockAnchor.BOTTOM_RIGHT) {
            xAdj = (float)(-width);
        }
        if (anchor == TextBlockAnchor.TOP_LEFT || anchor == TextBlockAnchor.TOP_CENTER || anchor == TextBlockAnchor.TOP_RIGHT) {
            yAdj = 0.0f;
        }
        else if (anchor == TextBlockAnchor.CENTER_LEFT || anchor == TextBlockAnchor.CENTER || anchor == TextBlockAnchor.CENTER_RIGHT) {
            yAdj = (float)(-height) / 2.0f;
        }
        else if (anchor == TextBlockAnchor.BOTTOM_LEFT || anchor == TextBlockAnchor.BOTTOM_CENTER || anchor == TextBlockAnchor.BOTTOM_RIGHT) {
            yAdj = (float)(-height);
        }
        result[0] = xAdj;
        result[1] = yAdj;
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
    
    public void draw(final Graphics2D g2, final float x, final float y, final TextBlockAnchor anchor) {
        this.draw(g2, x, y, anchor, 0.0f, 0.0f, 0.0);
    }
    
    public void draw(final Graphics2D g2, final float anchorX, final float anchorY, final TextBlockAnchor anchor, final float rotateX, final float rotateY, final double angle) {
        final Size2D d = this.calculateDimensions(g2);
        final float[] offsets = this.calculateOffsets(anchor, d.getWidth(), d.getHeight());
        final Iterator iterator = this.lines.iterator();
        float yCursor = 0.0f;
        while (iterator.hasNext()) {
            final TextLine line = iterator.next();
            final Size2D dimension = line.calculateDimensions(g2);
            float lineOffset = 0.0f;
            if (this.lineAlignment == HorizontalAlignment.CENTER) {
                lineOffset = (float)(d.getWidth() - dimension.getWidth()) / 2.0f;
            }
            else if (this.lineAlignment == HorizontalAlignment.RIGHT) {
                lineOffset = (float)(d.getWidth() - dimension.getWidth());
            }
            line.draw(g2, anchorX + offsets[0] + lineOffset, anchorY + offsets[1] + yCursor, TextAnchor.TOP_LEFT, rotateX, rotateY, angle);
            yCursor += (float)dimension.getHeight();
        }
    }
    
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof TextBlock) {
            final TextBlock block = (TextBlock)obj;
            return this.lines.equals(block.lines);
        }
        return false;
    }
    
    public TextLine getLastLine() {
        TextLine last = null;
        final int index = this.lines.size() - 1;
        if (index >= 0) {
            last = this.lines.get(index);
        }
        return last;
    }
    
    public HorizontalAlignment getLineAlignment() {
        return this.lineAlignment;
    }
    
    public List getLines() {
        return Collections.unmodifiableList((List<?>)this.lines);
    }
    
    public int hashCode() {
        return (this.lines != null) ? this.lines.hashCode() : 0;
    }
    
    public void setLineAlignment(final HorizontalAlignment alignment) {
        if (alignment == null) {
            throw new IllegalArgumentException("Null 'alignment' argument.");
        }
        this.lineAlignment = alignment;
    }
}
