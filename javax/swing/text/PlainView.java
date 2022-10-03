package javax.swing.text;

import java.awt.Container;
import java.awt.Component;
import javax.swing.event.DocumentEvent;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;

public class PlainView extends View implements TabExpander
{
    protected FontMetrics metrics;
    Element longLine;
    Font font;
    Segment lineBuffer;
    int tabSize;
    int tabBase;
    int sel0;
    int sel1;
    Color unselected;
    Color selected;
    int firstLineOffset;
    
    public PlainView(final Element element) {
        super(element);
    }
    
    protected int getTabSize() {
        final Integer n = (Integer)this.getDocument().getProperty("tabSize");
        return (n != null) ? n : 8;
    }
    
    protected void drawLine(final int n, final Graphics graphics, int drawElement, final int n2) {
        final Element element = this.getElement().getElement(n);
        try {
            if (element.isLeaf()) {
                this.drawElement(n, element, graphics, drawElement, n2);
            }
            else {
                for (int elementCount = element.getElementCount(), i = 0; i < elementCount; ++i) {
                    drawElement = this.drawElement(n, element.getElement(i), graphics, drawElement, n2);
                }
            }
        }
        catch (final BadLocationException ex) {
            throw new StateInvariantError("Can't render line: " + n);
        }
    }
    
    private int drawElement(final int n, final Element element, final Graphics graphics, int n2, final int n3) throws BadLocationException {
        final int startOffset = element.getStartOffset();
        final int min = Math.min(this.getDocument().getLength(), element.getEndOffset());
        if (n == 0) {
            n2 += this.firstLineOffset;
        }
        final AttributeSet attributes = element.getAttributes();
        if (Utilities.isComposedTextAttributeDefined(attributes)) {
            graphics.setColor(this.unselected);
            n2 = Utilities.drawComposedText(this, attributes, graphics, n2, n3, startOffset - element.getStartOffset(), min - element.getStartOffset());
        }
        else if (this.sel0 == this.sel1 || this.selected == this.unselected) {
            n2 = this.drawUnselectedText(graphics, n2, n3, startOffset, min);
        }
        else if (startOffset >= this.sel0 && startOffset <= this.sel1 && min >= this.sel0 && min <= this.sel1) {
            n2 = this.drawSelectedText(graphics, n2, n3, startOffset, min);
        }
        else if (this.sel0 >= startOffset && this.sel0 <= min) {
            if (this.sel1 >= startOffset && this.sel1 <= min) {
                n2 = this.drawUnselectedText(graphics, n2, n3, startOffset, this.sel0);
                n2 = this.drawSelectedText(graphics, n2, n3, this.sel0, this.sel1);
                n2 = this.drawUnselectedText(graphics, n2, n3, this.sel1, min);
            }
            else {
                n2 = this.drawUnselectedText(graphics, n2, n3, startOffset, this.sel0);
                n2 = this.drawSelectedText(graphics, n2, n3, this.sel0, min);
            }
        }
        else if (this.sel1 >= startOffset && this.sel1 <= min) {
            n2 = this.drawSelectedText(graphics, n2, n3, startOffset, this.sel1);
            n2 = this.drawUnselectedText(graphics, n2, n3, this.sel1, min);
        }
        else {
            n2 = this.drawUnselectedText(graphics, n2, n3, startOffset, min);
        }
        return n2;
    }
    
    protected int drawUnselectedText(final Graphics graphics, final int n, final int n2, final int n3, final int n4) throws BadLocationException {
        graphics.setColor(this.unselected);
        final Document document = this.getDocument();
        final Segment sharedSegment = SegmentCache.getSharedSegment();
        document.getText(n3, n4 - n3, sharedSegment);
        final int drawTabbedText = Utilities.drawTabbedText(this, sharedSegment, n, n2, graphics, this, n3);
        SegmentCache.releaseSharedSegment(sharedSegment);
        return drawTabbedText;
    }
    
    protected int drawSelectedText(final Graphics graphics, final int n, final int n2, final int n3, final int n4) throws BadLocationException {
        graphics.setColor(this.selected);
        final Document document = this.getDocument();
        final Segment sharedSegment = SegmentCache.getSharedSegment();
        document.getText(n3, n4 - n3, sharedSegment);
        final int drawTabbedText = Utilities.drawTabbedText(this, sharedSegment, n, n2, graphics, this, n3);
        SegmentCache.releaseSharedSegment(sharedSegment);
        return drawTabbedText;
    }
    
    protected final Segment getLineBuffer() {
        if (this.lineBuffer == null) {
            this.lineBuffer = new Segment();
        }
        return this.lineBuffer;
    }
    
    protected void updateMetrics() {
        if (this.font != this.getContainer().getFont()) {
            this.calculateLongestLine();
            this.tabSize = this.getTabSize() * this.metrics.charWidth('m');
        }
    }
    
    @Override
    public float getPreferredSpan(final int n) {
        this.updateMetrics();
        switch (n) {
            case 0: {
                return (float)this.getLineWidth(this.longLine);
            }
            case 1: {
                return (float)(this.getElement().getElementCount() * this.metrics.getHeight());
            }
            default: {
                throw new IllegalArgumentException("Invalid axis: " + n);
            }
        }
    }
    
    @Override
    public void paint(final Graphics graphics, Shape adjustPaintRegion) {
        final Shape shape = adjustPaintRegion;
        adjustPaintRegion = this.adjustPaintRegion(adjustPaintRegion);
        final Rectangle rectangle = (Rectangle)adjustPaintRegion;
        this.tabBase = rectangle.x;
        final JTextComponent textComponent = (JTextComponent)this.getContainer();
        final Highlighter highlighter = textComponent.getHighlighter();
        graphics.setFont(textComponent.getFont());
        this.sel0 = textComponent.getSelectionStart();
        this.sel1 = textComponent.getSelectionEnd();
        this.unselected = (textComponent.isEnabled() ? textComponent.getForeground() : textComponent.getDisabledTextColor());
        this.selected = ((textComponent.getCaret().isSelectionVisible() && highlighter != null) ? textComponent.getSelectedTextColor() : this.unselected);
        this.updateMetrics();
        final Rectangle clipBounds = graphics.getClipBounds();
        final int height = this.metrics.getHeight();
        final int n = rectangle.y + rectangle.height - (clipBounds.y + clipBounds.height);
        final int n2 = clipBounds.y - rectangle.y;
        int max;
        int max2;
        int n3;
        if (height > 0) {
            max = Math.max(0, n / height);
            max2 = Math.max(0, n2 / height);
            n3 = rectangle.height / height;
            if (rectangle.height % height != 0) {
                ++n3;
            }
        }
        else {
            max2 = (max = (n3 = 0));
        }
        final Rectangle lineToRect = this.lineToRect(adjustPaintRegion, max2);
        int n4 = lineToRect.y + this.metrics.getAscent();
        int x = lineToRect.x;
        final Element element = this.getElement();
        int elementCount = element.getElementCount();
        final int min = Math.min(elementCount, n3 - max);
        --elementCount;
        final LayeredHighlighter layeredHighlighter = (highlighter instanceof LayeredHighlighter) ? ((LayeredHighlighter)highlighter) : null;
        for (int i = max2; i < min; ++i) {
            if (layeredHighlighter != null) {
                final Element element2 = element.getElement(i);
                if (i == elementCount) {
                    layeredHighlighter.paintLayeredHighlights(graphics, element2.getStartOffset(), element2.getEndOffset(), shape, textComponent, this);
                }
                else {
                    layeredHighlighter.paintLayeredHighlights(graphics, element2.getStartOffset(), element2.getEndOffset() - 1, shape, textComponent, this);
                }
            }
            this.drawLine(i, graphics, x, n4);
            n4 += height;
            if (i == 0) {
                x -= this.firstLineOffset;
            }
        }
    }
    
    Shape adjustPaintRegion(final Shape shape) {
        return shape;
    }
    
    @Override
    public Shape modelToView(final int n, final Shape shape, final Position.Bias bias) throws BadLocationException {
        final Document document = this.getDocument();
        final Element element = this.getElement();
        final int elementIndex = element.getElementIndex(n);
        if (elementIndex < 0) {
            return this.lineToRect(shape, 0);
        }
        final Rectangle lineToRect = this.lineToRect(shape, elementIndex);
        this.tabBase = lineToRect.x;
        final int startOffset = element.getElement(elementIndex).getStartOffset();
        final Segment sharedSegment = SegmentCache.getSharedSegment();
        document.getText(startOffset, n - startOffset, sharedSegment);
        final int tabbedTextWidth = Utilities.getTabbedTextWidth(sharedSegment, this.metrics, this.tabBase, this, startOffset);
        SegmentCache.releaseSharedSegment(sharedSegment);
        final Rectangle rectangle = lineToRect;
        rectangle.x += tabbedTextWidth;
        lineToRect.width = 1;
        lineToRect.height = this.metrics.getHeight();
        return lineToRect;
    }
    
    @Override
    public int viewToModel(final float n, final float n2, final Shape shape, final Position.Bias[] array) {
        array[0] = Position.Bias.Forward;
        final Rectangle bounds = shape.getBounds();
        final Document document = this.getDocument();
        final int n3 = (int)n;
        final int n4 = (int)n2;
        if (n4 < bounds.y) {
            return this.getStartOffset();
        }
        if (n4 > bounds.y + bounds.height) {
            return this.getEndOffset() - 1;
        }
        final Element defaultRootElement = document.getDefaultRootElement();
        final int height = this.metrics.getHeight();
        final int n5 = (height > 0) ? Math.abs((n4 - bounds.y) / height) : (defaultRootElement.getElementCount() - 1);
        if (n5 >= defaultRootElement.getElementCount()) {
            return this.getEndOffset() - 1;
        }
        final Element element = defaultRootElement.getElement(n5);
        if (n5 == 0) {
            final Rectangle rectangle = bounds;
            rectangle.x += this.firstLineOffset;
            final Rectangle rectangle2 = bounds;
            rectangle2.width -= this.firstLineOffset;
        }
        if (n3 < bounds.x) {
            return element.getStartOffset();
        }
        if (n3 > bounds.x + bounds.width) {
            return element.getEndOffset() - 1;
        }
        try {
            final int startOffset = element.getStartOffset();
            final int n6 = element.getEndOffset() - 1;
            final Segment sharedSegment = SegmentCache.getSharedSegment();
            document.getText(startOffset, n6 - startOffset, sharedSegment);
            this.tabBase = bounds.x;
            final int n7 = startOffset + Utilities.getTabbedTextOffset(sharedSegment, this.metrics, this.tabBase, n3, this, startOffset);
            SegmentCache.releaseSharedSegment(sharedSegment);
            return n7;
        }
        catch (final BadLocationException ex) {
            return -1;
        }
    }
    
    @Override
    public void insertUpdate(final DocumentEvent documentEvent, final Shape shape, final ViewFactory viewFactory) {
        this.updateDamage(documentEvent, shape, viewFactory);
    }
    
    @Override
    public void removeUpdate(final DocumentEvent documentEvent, final Shape shape, final ViewFactory viewFactory) {
        this.updateDamage(documentEvent, shape, viewFactory);
    }
    
    @Override
    public void changedUpdate(final DocumentEvent documentEvent, final Shape shape, final ViewFactory viewFactory) {
        this.updateDamage(documentEvent, shape, viewFactory);
    }
    
    @Override
    public void setSize(final float n, final float n2) {
        super.setSize(n, n2);
        this.updateMetrics();
    }
    
    @Override
    public float nextTabStop(final float n, final int n2) {
        if (this.tabSize == 0) {
            return n;
        }
        return (float)(this.tabBase + (((int)n - this.tabBase) / this.tabSize + 1) * this.tabSize);
    }
    
    protected void updateDamage(final DocumentEvent documentEvent, final Shape shape, final ViewFactory viewFactory) {
        final Container container = this.getContainer();
        this.updateMetrics();
        final DocumentEvent.ElementChange change = documentEvent.getChange(this.getElement());
        final Element[] array = (Element[])((change != null) ? change.getChildrenAdded() : null);
        final Element[] array2 = (Element[])((change != null) ? change.getChildrenRemoved() : null);
        if ((array != null && array.length > 0) || (array2 != null && array2.length > 0)) {
            if (array != null) {
                int lineWidth = this.getLineWidth(this.longLine);
                for (int i = 0; i < array.length; ++i) {
                    final int lineWidth2 = this.getLineWidth(array[i]);
                    if (lineWidth2 > lineWidth) {
                        lineWidth = lineWidth2;
                        this.longLine = array[i];
                    }
                }
            }
            if (array2 != null) {
                for (int j = 0; j < array2.length; ++j) {
                    if (array2[j] == this.longLine) {
                        this.calculateLongestLine();
                        break;
                    }
                }
            }
            this.preferenceChanged(null, true, true);
            container.repaint();
        }
        else {
            final Element element = this.getElement();
            final int elementIndex = element.getElementIndex(documentEvent.getOffset());
            this.damageLineRange(elementIndex, elementIndex, shape, container);
            if (documentEvent.getType() == DocumentEvent.EventType.INSERT) {
                final int lineWidth3 = this.getLineWidth(this.longLine);
                final Element element2 = element.getElement(elementIndex);
                if (element2 == this.longLine) {
                    this.preferenceChanged(null, true, false);
                }
                else if (this.getLineWidth(element2) > lineWidth3) {
                    this.longLine = element2;
                    this.preferenceChanged(null, true, false);
                }
            }
            else if (documentEvent.getType() == DocumentEvent.EventType.REMOVE && element.getElement(elementIndex) == this.longLine) {
                this.calculateLongestLine();
                this.preferenceChanged(null, true, false);
            }
        }
    }
    
    protected void damageLineRange(final int n, final int n2, final Shape shape, final Component component) {
        if (shape != null) {
            final Rectangle lineToRect = this.lineToRect(shape, n);
            final Rectangle lineToRect2 = this.lineToRect(shape, n2);
            if (lineToRect != null && lineToRect2 != null) {
                final Rectangle union = lineToRect.union(lineToRect2);
                component.repaint(union.x, union.y, union.width, union.height);
            }
            else {
                component.repaint();
            }
        }
    }
    
    protected Rectangle lineToRect(final Shape shape, final int n) {
        Rectangle rectangle = null;
        this.updateMetrics();
        if (this.metrics != null) {
            final Rectangle bounds = shape.getBounds();
            if (n == 0) {
                final Rectangle rectangle2 = bounds;
                rectangle2.x += this.firstLineOffset;
                final Rectangle rectangle3 = bounds;
                rectangle3.width -= this.firstLineOffset;
            }
            rectangle = new Rectangle(bounds.x, bounds.y + n * this.metrics.getHeight(), bounds.width, this.metrics.getHeight());
        }
        return rectangle;
    }
    
    private void calculateLongestLine() {
        final Container container = this.getContainer();
        this.font = container.getFont();
        this.metrics = container.getFontMetrics(this.font);
        this.getDocument();
        final Element element = this.getElement();
        final int elementCount = element.getElementCount();
        int n = -1;
        for (int i = 0; i < elementCount; ++i) {
            final Element element2 = element.getElement(i);
            final int lineWidth = this.getLineWidth(element2);
            if (lineWidth > n) {
                n = lineWidth;
                this.longLine = element2;
            }
        }
    }
    
    private int getLineWidth(final Element element) {
        if (element == null) {
            return 0;
        }
        final int startOffset = element.getStartOffset();
        final int endOffset = element.getEndOffset();
        final Segment sharedSegment = SegmentCache.getSharedSegment();
        int tabbedTextWidth;
        try {
            element.getDocument().getText(startOffset, endOffset - startOffset, sharedSegment);
            tabbedTextWidth = Utilities.getTabbedTextWidth(sharedSegment, this.metrics, this.tabBase, this, startOffset);
        }
        catch (final BadLocationException ex) {
            tabbedTextWidth = 0;
        }
        SegmentCache.releaseSharedSegment(sharedSegment);
        return tabbedTextWidth;
    }
}
