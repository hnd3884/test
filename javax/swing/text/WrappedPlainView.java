package javax.swing.text;

import java.lang.ref.SoftReference;
import java.awt.Rectangle;
import java.awt.Container;
import java.awt.Shape;
import javax.swing.event.DocumentEvent;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.FontMetrics;

public class WrappedPlainView extends BoxView implements TabExpander
{
    FontMetrics metrics;
    Segment lineBuffer;
    boolean widthChanging;
    int tabBase;
    int tabSize;
    boolean wordWrap;
    int sel0;
    int sel1;
    Color unselected;
    Color selected;
    
    public WrappedPlainView(final Element element) {
        this(element, false);
    }
    
    public WrappedPlainView(final Element element, final boolean wordWrap) {
        super(element, 1);
        this.wordWrap = wordWrap;
    }
    
    protected int getTabSize() {
        final Integer n = (Integer)this.getDocument().getProperty("tabSize");
        return (n != null) ? n : 8;
    }
    
    protected void drawLine(final int n, final int n2, final Graphics graphics, int drawText, final int n3) {
        final Element element = this.getElement();
        final Element element2 = element.getElement(element.getElementIndex(n));
        try {
            if (element2.isLeaf()) {
                this.drawText(element2, n, n2, graphics, drawText, n3);
            }
            else {
                for (int i = element2.getElementIndex(n); i <= element2.getElementIndex(n2); ++i) {
                    final Element element3 = element2.getElement(i);
                    drawText = this.drawText(element3, Math.max(element3.getStartOffset(), n), Math.min(element3.getEndOffset(), n2), graphics, drawText, n3);
                }
            }
        }
        catch (final BadLocationException ex) {
            throw new StateInvariantError("Can't render: " + n + "," + n2);
        }
    }
    
    private int drawText(final Element element, final int n, int min, final Graphics graphics, int n2, final int n3) throws BadLocationException {
        min = Math.min(this.getDocument().getLength(), min);
        final AttributeSet attributes = element.getAttributes();
        if (Utilities.isComposedTextAttributeDefined(attributes)) {
            graphics.setColor(this.unselected);
            n2 = Utilities.drawComposedText(this, attributes, graphics, n2, n3, n - element.getStartOffset(), min - element.getStartOffset());
        }
        else if (this.sel0 == this.sel1 || this.selected == this.unselected) {
            n2 = this.drawUnselectedText(graphics, n2, n3, n, min);
        }
        else if (n >= this.sel0 && n <= this.sel1 && min >= this.sel0 && min <= this.sel1) {
            n2 = this.drawSelectedText(graphics, n2, n3, n, min);
        }
        else if (this.sel0 >= n && this.sel0 <= min) {
            if (this.sel1 >= n && this.sel1 <= min) {
                n2 = this.drawUnselectedText(graphics, n2, n3, n, this.sel0);
                n2 = this.drawSelectedText(graphics, n2, n3, this.sel0, this.sel1);
                n2 = this.drawUnselectedText(graphics, n2, n3, this.sel1, min);
            }
            else {
                n2 = this.drawUnselectedText(graphics, n2, n3, n, this.sel0);
                n2 = this.drawSelectedText(graphics, n2, n3, this.sel0, min);
            }
        }
        else if (this.sel1 >= n && this.sel1 <= min) {
            n2 = this.drawSelectedText(graphics, n2, n3, n, this.sel1);
            n2 = this.drawUnselectedText(graphics, n2, n3, this.sel1, min);
        }
        else {
            n2 = this.drawUnselectedText(graphics, n2, n3, n, min);
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
    
    protected int calculateBreakPosition(final int n, final int n2) {
        final Segment sharedSegment = SegmentCache.getSharedSegment();
        this.loadText(sharedSegment, n, n2);
        final int width = this.getWidth();
        int n3;
        if (this.wordWrap) {
            n3 = n + Utilities.getBreakLocation(sharedSegment, this.metrics, this.tabBase, this.tabBase + width, this, n);
        }
        else {
            n3 = n + Utilities.getTabbedTextOffset(sharedSegment, this.metrics, this.tabBase, this.tabBase + width, this, n, false);
        }
        SegmentCache.releaseSharedSegment(sharedSegment);
        return n3;
    }
    
    @Override
    protected void loadChildren(final ViewFactory viewFactory) {
        final Element element = this.getElement();
        final int elementCount = element.getElementCount();
        if (elementCount > 0) {
            final View[] array = new View[elementCount];
            for (int i = 0; i < elementCount; ++i) {
                array[i] = new WrappedLine(element.getElement(i));
            }
            this.replace(0, 0, array);
        }
    }
    
    void updateChildren(final DocumentEvent documentEvent, final Shape shape) {
        final DocumentEvent.ElementChange change = documentEvent.getChange(this.getElement());
        if (change != null) {
            final Element[] childrenRemoved = change.getChildrenRemoved();
            final Element[] childrenAdded = change.getChildrenAdded();
            final View[] array = new View[childrenAdded.length];
            for (int i = 0; i < childrenAdded.length; ++i) {
                array[i] = new WrappedLine(childrenAdded[i]);
            }
            this.replace(change.getIndex(), childrenRemoved.length, array);
            if (shape != null) {
                this.preferenceChanged(null, true, true);
                this.getContainer().repaint();
            }
        }
        this.updateMetrics();
    }
    
    final void loadText(final Segment segment, final int n, final int n2) {
        try {
            this.getDocument().getText(n, n2 - n, segment);
        }
        catch (final BadLocationException ex) {
            throw new StateInvariantError("Can't get line text");
        }
    }
    
    final void updateMetrics() {
        final Container container = this.getContainer();
        this.metrics = container.getFontMetrics(container.getFont());
        this.tabSize = this.getTabSize() * this.metrics.charWidth('m');
    }
    
    @Override
    public float nextTabStop(final float n, final int n2) {
        if (this.tabSize == 0) {
            return n;
        }
        return (float)(this.tabBase + (((int)n - this.tabBase) / this.tabSize + 1) * this.tabSize);
    }
    
    @Override
    public void paint(final Graphics graphics, final Shape shape) {
        this.tabBase = ((Rectangle)shape).x;
        final JTextComponent textComponent = (JTextComponent)this.getContainer();
        this.sel0 = textComponent.getSelectionStart();
        this.sel1 = textComponent.getSelectionEnd();
        this.unselected = (textComponent.isEnabled() ? textComponent.getForeground() : textComponent.getDisabledTextColor());
        this.selected = ((textComponent.getCaret().isSelectionVisible() && textComponent.getHighlighter() != null) ? textComponent.getSelectedTextColor() : this.unselected);
        graphics.setFont(textComponent.getFont());
        super.paint(graphics, shape);
    }
    
    @Override
    public void setSize(final float n, final float n2) {
        this.updateMetrics();
        if ((int)n != this.getWidth()) {
            this.preferenceChanged(null, true, true);
            this.widthChanging = true;
        }
        super.setSize(n, n2);
        this.widthChanging = false;
    }
    
    @Override
    public float getPreferredSpan(final int n) {
        this.updateMetrics();
        return super.getPreferredSpan(n);
    }
    
    @Override
    public float getMinimumSpan(final int n) {
        this.updateMetrics();
        return super.getMinimumSpan(n);
    }
    
    @Override
    public float getMaximumSpan(final int n) {
        this.updateMetrics();
        return super.getMaximumSpan(n);
    }
    
    @Override
    public void insertUpdate(final DocumentEvent documentEvent, final Shape shape, final ViewFactory viewFactory) {
        this.updateChildren(documentEvent, shape);
        final Rectangle rectangle = (shape != null && this.isAllocationValid()) ? this.getInsideAllocation(shape) : null;
        final View viewAtPosition = this.getViewAtPosition(documentEvent.getOffset(), rectangle);
        if (viewAtPosition != null) {
            viewAtPosition.insertUpdate(documentEvent, rectangle, viewFactory);
        }
    }
    
    @Override
    public void removeUpdate(final DocumentEvent documentEvent, final Shape shape, final ViewFactory viewFactory) {
        this.updateChildren(documentEvent, shape);
        final Rectangle rectangle = (shape != null && this.isAllocationValid()) ? this.getInsideAllocation(shape) : null;
        final View viewAtPosition = this.getViewAtPosition(documentEvent.getOffset(), rectangle);
        if (viewAtPosition != null) {
            viewAtPosition.removeUpdate(documentEvent, rectangle, viewFactory);
        }
    }
    
    @Override
    public void changedUpdate(final DocumentEvent documentEvent, final Shape shape, final ViewFactory viewFactory) {
        this.updateChildren(documentEvent, shape);
    }
    
    class WrappedLine extends View
    {
        int lineCount;
        SoftReference<int[]> lineCache;
        
        WrappedLine(final Element element) {
            super(element);
            this.lineCache = null;
            this.lineCount = -1;
        }
        
        @Override
        public float getPreferredSpan(final int n) {
            switch (n) {
                case 0: {
                    final float n2 = (float)WrappedPlainView.this.getWidth();
                    if (n2 == 2.14748365E9f) {
                        return 100.0f;
                    }
                    return n2;
                }
                case 1: {
                    if (this.lineCount < 0 || WrappedPlainView.this.widthChanging) {
                        this.breakLines(this.getStartOffset());
                    }
                    return (float)(this.lineCount * WrappedPlainView.this.metrics.getHeight());
                }
                default: {
                    throw new IllegalArgumentException("Invalid axis: " + n);
                }
            }
        }
        
        @Override
        public void paint(final Graphics graphics, final Shape shape) {
            final Rectangle rectangle = (Rectangle)shape;
            int n = rectangle.y + WrappedPlainView.this.metrics.getAscent();
            final int x = rectangle.x;
            final JTextComponent textComponent = (JTextComponent)this.getContainer();
            final Highlighter highlighter = textComponent.getHighlighter();
            final LayeredHighlighter layeredHighlighter = (highlighter instanceof LayeredHighlighter) ? ((LayeredHighlighter)highlighter) : null;
            final int startOffset = this.getStartOffset();
            final int endOffset = this.getEndOffset();
            int n2 = startOffset;
            final int[] lineEnds = this.getLineEnds();
            for (int i = 0; i < this.lineCount; ++i) {
                final int n3 = (lineEnds == null) ? endOffset : (startOffset + lineEnds[i]);
                if (layeredHighlighter != null) {
                    layeredHighlighter.paintLayeredHighlights(graphics, n2, (n3 == endOffset) ? (n3 - 1) : n3, shape, textComponent, this);
                }
                WrappedPlainView.this.drawLine(n2, n3, graphics, x, n);
                n2 = n3;
                n += WrappedPlainView.this.metrics.getHeight();
            }
        }
        
        @Override
        public Shape modelToView(final int n, final Shape shape, final Position.Bias bias) throws BadLocationException {
            final Rectangle bounds = shape.getBounds();
            bounds.height = WrappedPlainView.this.metrics.getHeight();
            bounds.width = 1;
            int startOffset = this.getStartOffset();
            if (n < startOffset || n > this.getEndOffset()) {
                throw new BadLocationException("Position out of range", n);
            }
            final int n2 = (bias == Position.Bias.Forward) ? n : Math.max(startOffset, n - 1);
            final int[] lineEnds = this.getLineEnds();
            if (lineEnds != null) {
                final int line = this.findLine(n2 - startOffset);
                if (line > 0) {
                    startOffset += lineEnds[line - 1];
                }
                final Rectangle rectangle = bounds;
                rectangle.y += bounds.height * line;
            }
            if (n > startOffset) {
                final Segment sharedSegment = SegmentCache.getSharedSegment();
                WrappedPlainView.this.loadText(sharedSegment, startOffset, n);
                final Rectangle rectangle2 = bounds;
                rectangle2.x += Utilities.getTabbedTextWidth(sharedSegment, WrappedPlainView.this.metrics, bounds.x, WrappedPlainView.this, startOffset);
                SegmentCache.releaseSharedSegment(sharedSegment);
            }
            return bounds;
        }
        
        @Override
        public int viewToModel(final float n, final float n2, final Shape shape, final Position.Bias[] array) {
            array[0] = Position.Bias.Forward;
            final Rectangle rectangle = (Rectangle)shape;
            final int n3 = (int)n;
            final int n4 = (int)n2;
            if (n4 < rectangle.y) {
                return this.getStartOffset();
            }
            if (n4 > rectangle.y + rectangle.height) {
                return this.getEndOffset() - 1;
            }
            rectangle.height = WrappedPlainView.this.metrics.getHeight();
            final int n5 = (rectangle.height > 0) ? ((n4 - rectangle.y) / rectangle.height) : (this.lineCount - 1);
            if (n5 >= this.lineCount) {
                return this.getEndOffset() - 1;
            }
            int startOffset = this.getStartOffset();
            int endOffset;
            if (this.lineCount == 1) {
                endOffset = this.getEndOffset();
            }
            else {
                final int[] lineEnds = this.getLineEnds();
                endOffset = startOffset + lineEnds[n5];
                if (n5 > 0) {
                    startOffset += lineEnds[n5 - 1];
                }
            }
            if (n3 < rectangle.x) {
                return startOffset;
            }
            if (n3 > rectangle.x + rectangle.width) {
                return endOffset - 1;
            }
            final Segment sharedSegment = SegmentCache.getSharedSegment();
            WrappedPlainView.this.loadText(sharedSegment, startOffset, endOffset);
            final int tabbedTextOffset = Utilities.getTabbedTextOffset(sharedSegment, WrappedPlainView.this.metrics, rectangle.x, n3, WrappedPlainView.this, startOffset);
            SegmentCache.releaseSharedSegment(sharedSegment);
            return Math.min(startOffset + tabbedTextOffset, endOffset - 1);
        }
        
        @Override
        public void insertUpdate(final DocumentEvent documentEvent, final Shape shape, final ViewFactory viewFactory) {
            this.update(documentEvent, shape);
        }
        
        @Override
        public void removeUpdate(final DocumentEvent documentEvent, final Shape shape, final ViewFactory viewFactory) {
            this.update(documentEvent, shape);
        }
        
        private void update(final DocumentEvent documentEvent, final Shape shape) {
            final int lineCount = this.lineCount;
            this.breakLines(documentEvent.getOffset());
            if (lineCount != this.lineCount) {
                WrappedPlainView.this.preferenceChanged(this, false, true);
                this.getContainer().repaint();
            }
            else if (shape != null) {
                final Container container = this.getContainer();
                final Rectangle rectangle = (Rectangle)shape;
                container.repaint(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
            }
        }
        
        final int[] getLineEnds() {
            if (this.lineCache == null) {
                return null;
            }
            final int[] array = this.lineCache.get();
            if (array == null) {
                return this.breakLines(this.getStartOffset());
            }
            return array;
        }
        
        final int[] breakLines(final int n) {
            final int[] array2;
            int[] array = array2 = (int[])((this.lineCache == null) ? null : ((int[])this.lineCache.get()));
            final int startOffset = this.getStartOffset();
            int line = 0;
            if (array != null) {
                line = this.findLine(n - startOffset);
                if (line > 0) {
                    --line;
                }
            }
            int i = (line == 0) ? startOffset : (startOffset + array[line - 1]);
            final int endOffset = this.getEndOffset();
            while (i < endOffset) {
                int calculateBreakPosition = WrappedPlainView.this.calculateBreakPosition(i, endOffset);
                i = ((calculateBreakPosition == i) ? (++calculateBreakPosition) : calculateBreakPosition);
                if (line == 0 && i >= endOffset) {
                    this.lineCache = null;
                    array = null;
                    line = 1;
                    break;
                }
                if (array == null || line >= array.length) {
                    final int[] array3 = new int[Math.max((int)Math.ceil((line + 1) * ((endOffset - startOffset) / (double)(i - startOffset))), line + 2)];
                    if (array != null) {
                        System.arraycopy(array, 0, array3, 0, line);
                    }
                    array = array3;
                }
                array[line++] = i - startOffset;
            }
            this.lineCount = line;
            if (this.lineCount > 1) {
                final int n2 = this.lineCount + this.lineCount / 3;
                if (array.length > n2) {
                    final int[] array4 = new int[n2];
                    System.arraycopy(array, 0, array4, 0, this.lineCount);
                    array = array4;
                }
            }
            if (array != null && array != array2) {
                this.lineCache = new SoftReference<int[]>(array);
            }
            return array;
        }
        
        private int findLine(final int n) {
            final int[] array = this.lineCache.get();
            if (n < array[0]) {
                return 0;
            }
            if (n > array[this.lineCount - 1]) {
                return this.lineCount;
            }
            return this.findLine(array, n, 0, this.lineCount - 1);
        }
        
        private int findLine(final int[] array, final int n, final int n2, final int n3) {
            if (n3 - n2 <= 1) {
                return n3;
            }
            final int n4 = (n3 + n2) / 2;
            return (n < array[n4]) ? this.findLine(array, n, n2, n4) : this.findLine(array, n, n4, n3);
        }
    }
}
