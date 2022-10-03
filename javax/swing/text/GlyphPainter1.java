package javax.swing.text;

import java.awt.Container;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Graphics;
import java.awt.FontMetrics;

class GlyphPainter1 extends GlyphView.GlyphPainter
{
    FontMetrics metrics;
    
    @Override
    public float getSpan(final GlyphView glyphView, final int n, final int n2, final TabExpander tabExpander, final float n3) {
        this.sync(glyphView);
        final Segment text = glyphView.getText(n, n2);
        final int tabbedTextWidth = Utilities.getTabbedTextWidth(glyphView, text, this.metrics, (int)n3, tabExpander, n, this.getJustificationData(glyphView));
        SegmentCache.releaseSharedSegment(text);
        return (float)tabbedTextWidth;
    }
    
    @Override
    public float getHeight(final GlyphView glyphView) {
        this.sync(glyphView);
        return (float)this.metrics.getHeight();
    }
    
    @Override
    public float getAscent(final GlyphView glyphView) {
        this.sync(glyphView);
        return (float)this.metrics.getAscent();
    }
    
    @Override
    public float getDescent(final GlyphView glyphView) {
        this.sync(glyphView);
        return (float)this.metrics.getDescent();
    }
    
    @Override
    public void paint(final GlyphView glyphView, final Graphics graphics, final Shape shape, final int n, final int n2) {
        this.sync(glyphView);
        final TabExpander tabExpander = glyphView.getTabExpander();
        final Rectangle rectangle = (Rectangle)((shape instanceof Rectangle) ? shape : shape.getBounds());
        int x = rectangle.x;
        final int startOffset = glyphView.getStartOffset();
        final int[] justificationData = this.getJustificationData(glyphView);
        if (startOffset != n) {
            final Segment text = glyphView.getText(startOffset, n);
            x += Utilities.getTabbedTextWidth(glyphView, text, this.metrics, x, tabExpander, startOffset, justificationData);
            SegmentCache.releaseSharedSegment(text);
        }
        final int n3 = rectangle.y + this.metrics.getHeight() - this.metrics.getDescent();
        final Segment text2 = glyphView.getText(n, n2);
        graphics.setFont(this.metrics.getFont());
        Utilities.drawTabbedText(glyphView, text2, x, n3, graphics, tabExpander, n, justificationData);
        SegmentCache.releaseSharedSegment(text2);
    }
    
    @Override
    public Shape modelToView(final GlyphView glyphView, final int n, final Position.Bias bias, final Shape shape) throws BadLocationException {
        this.sync(glyphView);
        final Rectangle rectangle = (Rectangle)((shape instanceof Rectangle) ? shape : shape.getBounds());
        final int startOffset = glyphView.getStartOffset();
        final int endOffset = glyphView.getEndOffset();
        final TabExpander tabExpander = glyphView.getTabExpander();
        if (n == endOffset) {
            return new Rectangle(rectangle.x + rectangle.width, rectangle.y, 0, this.metrics.getHeight());
        }
        if (n >= startOffset && n <= endOffset) {
            final Segment text = glyphView.getText(startOffset, n);
            final int tabbedTextWidth = Utilities.getTabbedTextWidth(glyphView, text, this.metrics, rectangle.x, tabExpander, startOffset, this.getJustificationData(glyphView));
            SegmentCache.releaseSharedSegment(text);
            return new Rectangle(rectangle.x + tabbedTextWidth, rectangle.y, 0, this.metrics.getHeight());
        }
        throw new BadLocationException("modelToView - can't convert", endOffset);
    }
    
    @Override
    public int viewToModel(final GlyphView glyphView, final float n, final float n2, final Shape shape, final Position.Bias[] array) {
        this.sync(glyphView);
        final Rectangle rectangle = (Rectangle)((shape instanceof Rectangle) ? shape : shape.getBounds());
        final int startOffset = glyphView.getStartOffset();
        final int endOffset = glyphView.getEndOffset();
        final TabExpander tabExpander = glyphView.getTabExpander();
        final Segment text = glyphView.getText(startOffset, endOffset);
        final int tabbedTextOffset = Utilities.getTabbedTextOffset(glyphView, text, this.metrics, rectangle.x, (int)n, tabExpander, startOffset, this.getJustificationData(glyphView));
        SegmentCache.releaseSharedSegment(text);
        int n3 = startOffset + tabbedTextOffset;
        if (n3 == endOffset) {
            --n3;
        }
        array[0] = Position.Bias.Forward;
        return n3;
    }
    
    @Override
    public int getBoundedPosition(final GlyphView glyphView, final int n, final float n2, final float n3) {
        this.sync(glyphView);
        final TabExpander tabExpander = glyphView.getTabExpander();
        final Segment text = glyphView.getText(n, glyphView.getEndOffset());
        final int tabbedTextOffset = Utilities.getTabbedTextOffset(glyphView, text, this.metrics, (int)n2, (int)(n2 + n3), tabExpander, n, false, this.getJustificationData(glyphView));
        SegmentCache.releaseSharedSegment(text);
        return n + tabbedTextOffset;
    }
    
    void sync(final GlyphView glyphView) {
        final Font font = glyphView.getFont();
        if (this.metrics == null || !font.equals(this.metrics.getFont())) {
            final Container container = glyphView.getContainer();
            this.metrics = ((container != null) ? container.getFontMetrics(font) : Toolkit.getDefaultToolkit().getFontMetrics(font));
        }
    }
    
    private int[] getJustificationData(final GlyphView glyphView) {
        final View parent = glyphView.getParent();
        int[] justificationData = null;
        if (parent instanceof ParagraphView.Row) {
            justificationData = ((ParagraphView.Row)parent).justificationData;
        }
        return justificationData;
    }
}
