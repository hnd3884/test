package javax.swing.text;

import java.awt.geom.RectangularShape;
import java.awt.geom.Rectangle2D;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Graphics;
import java.awt.font.TextHitInfo;
import java.awt.font.TextLayout;

class GlyphPainter2 extends GlyphView.GlyphPainter
{
    TextLayout layout;
    
    public GlyphPainter2(final TextLayout layout) {
        this.layout = layout;
    }
    
    @Override
    public GlyphView.GlyphPainter getPainter(final GlyphView glyphView, final int n, final int n2) {
        return null;
    }
    
    @Override
    public float getSpan(final GlyphView glyphView, final int n, final int n2, final TabExpander tabExpander, final float n3) {
        if (n == glyphView.getStartOffset() && n2 == glyphView.getEndOffset()) {
            return this.layout.getAdvance();
        }
        final int startOffset = glyphView.getStartOffset();
        final int n4 = n - startOffset;
        final int n5 = n2 - startOffset;
        final TextHitInfo afterOffset = TextHitInfo.afterOffset(n4);
        final TextHitInfo beforeOffset = TextHitInfo.beforeOffset(n5);
        final float n6 = this.layout.getCaretInfo(afterOffset)[0];
        final float n7 = this.layout.getCaretInfo(beforeOffset)[0];
        return (n7 > n6) ? (n7 - n6) : (n6 - n7);
    }
    
    @Override
    public float getHeight(final GlyphView glyphView) {
        return this.layout.getAscent() + this.layout.getDescent() + this.layout.getLeading();
    }
    
    @Override
    public float getAscent(final GlyphView glyphView) {
        return this.layout.getAscent();
    }
    
    @Override
    public float getDescent(final GlyphView glyphView) {
        return this.layout.getDescent();
    }
    
    @Override
    public void paint(final GlyphView glyphView, final Graphics graphics, final Shape shape, final int n, final int n2) {
        if (graphics instanceof Graphics2D) {
            final Rectangle2D bounds2D = shape.getBounds2D();
            final Graphics2D graphics2D = (Graphics2D)graphics;
            final float n3 = (float)bounds2D.getY() + this.layout.getAscent() + this.layout.getLeading();
            final float n4 = (float)bounds2D.getX();
            if (n <= glyphView.getStartOffset()) {
                if (n2 >= glyphView.getEndOffset()) {
                    this.layout.draw(graphics2D, n4, n3);
                    return;
                }
            }
            try {
                final Shape modelToView = glyphView.modelToView(n, Position.Bias.Forward, n2, Position.Bias.Backward, shape);
                final Shape clip = graphics.getClip();
                graphics2D.clip(modelToView);
                this.layout.draw(graphics2D, n4, n3);
                graphics.setClip(clip);
            }
            catch (final BadLocationException ex) {}
        }
    }
    
    @Override
    public Shape modelToView(final GlyphView glyphView, final int n, final Position.Bias bias, final Shape shape) throws BadLocationException {
        final int n2 = n - glyphView.getStartOffset();
        final Rectangle2D bounds2D = shape.getBounds2D();
        bounds2D.setRect(bounds2D.getX() + this.layout.getCaretInfo((bias == Position.Bias.Forward) ? TextHitInfo.afterOffset(n2) : TextHitInfo.beforeOffset(n2))[0], bounds2D.getY(), 1.0, bounds2D.getHeight());
        return bounds2D;
    }
    
    @Override
    public int viewToModel(final GlyphView glyphView, final float n, final float n2, final Shape shape, final Position.Bias[] array) {
        final TextHitInfo hitTestChar = this.layout.hitTestChar(n - (float)((RectangularShape)((shape instanceof Rectangle2D) ? shape : shape.getBounds2D())).getX(), 0.0f);
        int insertionIndex = hitTestChar.getInsertionIndex();
        if (insertionIndex == glyphView.getEndOffset()) {
            --insertionIndex;
        }
        array[0] = (hitTestChar.isLeadingEdge() ? Position.Bias.Forward : Position.Bias.Backward);
        return insertionIndex + glyphView.getStartOffset();
    }
    
    @Override
    public int getBoundedPosition(final GlyphView glyphView, final int n, final float n2, final float n3) {
        if (n3 < 0.0f) {
            throw new IllegalArgumentException("Length must be >= 0.");
        }
        TextHitInfo textHitInfo;
        if (this.layout.isLeftToRight()) {
            textHitInfo = this.layout.hitTestChar(n3, 0.0f);
        }
        else {
            textHitInfo = this.layout.hitTestChar(this.layout.getAdvance() - n3, 0.0f);
        }
        return glyphView.getStartOffset() + textHitInfo.getCharIndex();
    }
    
    @Override
    public int getNextVisualPositionFrom(final GlyphView glyphView, int n, final Position.Bias bias, final Shape shape, final int n2, final Position.Bias[] array) throws BadLocationException {
        final Document document = glyphView.getDocument();
        final int startOffset = glyphView.getStartOffset();
        final int endOffset = glyphView.getEndOffset();
        switch (n2) {
            case 1: {
                break;
            }
            case 5: {
                break;
            }
            case 3: {
                final boolean leftToRight = AbstractDocument.isLeftToRight(document, startOffset, endOffset);
                if (startOffset == document.getLength()) {
                    if (n == -1) {
                        array[0] = Position.Bias.Forward;
                        return startOffset;
                    }
                    return -1;
                }
                else if (n == -1) {
                    if (leftToRight) {
                        array[0] = Position.Bias.Forward;
                        return startOffset;
                    }
                    final Segment text = glyphView.getText(endOffset - 1, endOffset);
                    final char c = text.array[text.offset];
                    SegmentCache.releaseSharedSegment(text);
                    if (c == '\n') {
                        array[0] = Position.Bias.Forward;
                        return endOffset - 1;
                    }
                    array[0] = Position.Bias.Backward;
                    return endOffset;
                }
                else {
                    TextHitInfo textHitInfo;
                    if (bias == Position.Bias.Forward) {
                        textHitInfo = TextHitInfo.afterOffset(n - startOffset);
                    }
                    else {
                        textHitInfo = TextHitInfo.beforeOffset(n - startOffset);
                    }
                    TextHitInfo textHitInfo2 = this.layout.getNextRightHit(textHitInfo);
                    if (textHitInfo2 == null) {
                        return -1;
                    }
                    if (leftToRight != this.layout.isLeftToRight()) {
                        textHitInfo2 = this.layout.getVisualOtherHit(textHitInfo2);
                    }
                    n = textHitInfo2.getInsertionIndex() + startOffset;
                    if (n == endOffset) {
                        final Segment text2 = glyphView.getText(endOffset - 1, endOffset);
                        final char c2 = text2.array[text2.offset];
                        SegmentCache.releaseSharedSegment(text2);
                        if (c2 == '\n') {
                            return -1;
                        }
                        array[0] = Position.Bias.Backward;
                    }
                    else {
                        array[0] = Position.Bias.Forward;
                    }
                    return n;
                }
                break;
            }
            case 7: {
                final boolean leftToRight2 = AbstractDocument.isLeftToRight(document, startOffset, endOffset);
                if (startOffset == document.getLength()) {
                    if (n == -1) {
                        array[0] = Position.Bias.Forward;
                        return startOffset;
                    }
                    return -1;
                }
                else if (n == -1) {
                    if (!leftToRight2) {
                        array[0] = Position.Bias.Forward;
                        return startOffset;
                    }
                    final Segment text3 = glyphView.getText(endOffset - 1, endOffset);
                    final char c3 = text3.array[text3.offset];
                    SegmentCache.releaseSharedSegment(text3);
                    if (c3 == '\n' || Character.isSpaceChar(c3)) {
                        array[0] = Position.Bias.Forward;
                        return endOffset - 1;
                    }
                    array[0] = Position.Bias.Backward;
                    return endOffset;
                }
                else {
                    TextHitInfo textHitInfo3;
                    if (bias == Position.Bias.Forward) {
                        textHitInfo3 = TextHitInfo.afterOffset(n - startOffset);
                    }
                    else {
                        textHitInfo3 = TextHitInfo.beforeOffset(n - startOffset);
                    }
                    TextHitInfo textHitInfo4 = this.layout.getNextLeftHit(textHitInfo3);
                    if (textHitInfo4 == null) {
                        return -1;
                    }
                    if (leftToRight2 != this.layout.isLeftToRight()) {
                        textHitInfo4 = this.layout.getVisualOtherHit(textHitInfo4);
                    }
                    n = textHitInfo4.getInsertionIndex() + startOffset;
                    if (n == endOffset) {
                        final Segment text4 = glyphView.getText(endOffset - 1, endOffset);
                        final char c4 = text4.array[text4.offset];
                        SegmentCache.releaseSharedSegment(text4);
                        if (c4 == '\n') {
                            return -1;
                        }
                        array[0] = Position.Bias.Backward;
                    }
                    else {
                        array[0] = Position.Bias.Forward;
                    }
                    return n;
                }
                break;
            }
            default: {
                throw new IllegalArgumentException("Bad direction: " + n2);
            }
        }
        return n;
    }
}
