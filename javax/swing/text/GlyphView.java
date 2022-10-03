package javax.swing.text;

import java.awt.Point;
import java.util.BitSet;
import javax.swing.event.DocumentEvent;
import java.util.Locale;
import java.text.BreakIterator;
import java.text.CharacterIterator;
import sun.swing.SwingUtilities2;
import javax.swing.UIManager;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Graphics;
import java.awt.Font;
import java.awt.Container;
import java.awt.Color;

public class GlyphView extends View implements TabableView, Cloneable
{
    private byte[] selections;
    int offset;
    int length;
    boolean impliedCR;
    boolean skipWidth;
    TabExpander expander;
    private float minimumSpan;
    private int[] breakSpots;
    int x;
    GlyphPainter painter;
    static GlyphPainter defaultPainter;
    private JustificationInfo justificationInfo;
    
    public GlyphView(final Element element) {
        super(element);
        this.selections = null;
        this.minimumSpan = -1.0f;
        this.breakSpots = null;
        this.justificationInfo = null;
        this.offset = 0;
        this.length = 0;
        final Element parentElement = element.getParentElement();
        final AttributeSet attributes = element.getAttributes();
        this.impliedCR = (attributes != null && attributes.getAttribute("CR") != null && parentElement != null && parentElement.getElementCount() > 1);
        this.skipWidth = element.getName().equals("br");
    }
    
    @Override
    protected final Object clone() {
        Object clone;
        try {
            clone = super.clone();
        }
        catch (final CloneNotSupportedException ex) {
            clone = null;
        }
        return clone;
    }
    
    public GlyphPainter getGlyphPainter() {
        return this.painter;
    }
    
    public void setGlyphPainter(final GlyphPainter painter) {
        this.painter = painter;
    }
    
    public Segment getText(final int n, final int n2) {
        final Segment sharedSegment = SegmentCache.getSharedSegment();
        try {
            this.getDocument().getText(n, n2 - n, sharedSegment);
        }
        catch (final BadLocationException ex) {
            throw new StateInvariantError("GlyphView: Stale view: " + ex);
        }
        return sharedSegment;
    }
    
    public Color getBackground() {
        final Document document = this.getDocument();
        if (document instanceof StyledDocument) {
            final AttributeSet attributes = this.getAttributes();
            if (attributes.isDefined(StyleConstants.Background)) {
                return ((StyledDocument)document).getBackground(attributes);
            }
        }
        return null;
    }
    
    public Color getForeground() {
        final Document document = this.getDocument();
        if (document instanceof StyledDocument) {
            return ((StyledDocument)document).getForeground(this.getAttributes());
        }
        final Container container = this.getContainer();
        if (container != null) {
            return container.getForeground();
        }
        return null;
    }
    
    public Font getFont() {
        final Document document = this.getDocument();
        if (document instanceof StyledDocument) {
            return ((StyledDocument)document).getFont(this.getAttributes());
        }
        final Container container = this.getContainer();
        if (container != null) {
            return container.getFont();
        }
        return null;
    }
    
    public boolean isUnderline() {
        return StyleConstants.isUnderline(this.getAttributes());
    }
    
    public boolean isStrikeThrough() {
        return StyleConstants.isStrikeThrough(this.getAttributes());
    }
    
    public boolean isSubscript() {
        return StyleConstants.isSubscript(this.getAttributes());
    }
    
    public boolean isSuperscript() {
        return StyleConstants.isSuperscript(this.getAttributes());
    }
    
    public TabExpander getTabExpander() {
        return this.expander;
    }
    
    protected void checkPainter() {
        if (this.painter == null) {
            if (GlyphView.defaultPainter == null) {
                final String s = "javax.swing.text.GlyphPainter1";
                try {
                    final ClassLoader classLoader = this.getClass().getClassLoader();
                    Class<?> clazz;
                    if (classLoader != null) {
                        clazz = classLoader.loadClass(s);
                    }
                    else {
                        clazz = Class.forName(s);
                    }
                    final Object instance = clazz.newInstance();
                    if (instance instanceof GlyphPainter) {
                        GlyphView.defaultPainter = (GlyphPainter)instance;
                    }
                }
                catch (final Throwable t) {
                    throw new StateInvariantError("GlyphView: Can't load glyph painter: " + s);
                }
            }
            this.setGlyphPainter(GlyphView.defaultPainter.getPainter(this, this.getStartOffset(), this.getEndOffset()));
        }
    }
    
    @Override
    public float getTabbedSpan(final float n, final TabExpander expander) {
        this.checkPainter();
        final TabExpander expander2 = this.expander;
        this.expander = expander;
        if (this.expander != expander2) {
            this.preferenceChanged(null, true, false);
        }
        this.x = (int)n;
        return this.painter.getSpan(this, this.getStartOffset(), this.getEndOffset(), this.expander, n);
    }
    
    @Override
    public float getPartialSpan(final int n, final int n2) {
        this.checkPainter();
        return this.painter.getSpan(this, n, n2, this.expander, (float)this.x);
    }
    
    @Override
    public int getStartOffset() {
        final Element element = this.getElement();
        return (this.length > 0) ? (element.getStartOffset() + this.offset) : element.getStartOffset();
    }
    
    @Override
    public int getEndOffset() {
        final Element element = this.getElement();
        return (this.length > 0) ? (element.getStartOffset() + this.offset + this.length) : element.getEndOffset();
    }
    
    private void initSelections(final int n, final int n2) {
        final int n3 = n2 - n + 1;
        if (this.selections == null || n3 > this.selections.length) {
            this.selections = new byte[n3];
            return;
        }
        for (int i = 0; i < n3; this.selections[i++] = 0) {}
    }
    
    @Override
    public void paint(final Graphics graphics, final Shape shape) {
        this.checkPainter();
        int n = 0;
        final Container container = this.getContainer();
        final int startOffset = this.getStartOffset();
        final int endOffset = this.getEndOffset();
        final Rectangle rectangle = (Rectangle)((shape instanceof Rectangle) ? shape : shape.getBounds());
        final Color background = this.getBackground();
        Color foreground = this.getForeground();
        if (container != null && !container.isEnabled()) {
            foreground = ((container instanceof JTextComponent) ? ((JTextComponent)container).getDisabledTextColor() : UIManager.getColor("textInactiveText"));
        }
        if (background != null) {
            graphics.setColor(background);
            graphics.fillRect(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
        }
        if (container instanceof JTextComponent) {
            final JTextComponent textComponent = (JTextComponent)container;
            final Highlighter highlighter = textComponent.getHighlighter();
            if (highlighter instanceof LayeredHighlighter) {
                ((LayeredHighlighter)highlighter).paintLayeredHighlights(graphics, startOffset, endOffset, shape, textComponent, this);
            }
        }
        if (Utilities.isComposedTextElement(this.getElement())) {
            Utilities.paintComposedText(graphics, shape.getBounds(), this);
            n = 1;
        }
        else if (container instanceof JTextComponent) {
            final JTextComponent textComponent2 = (JTextComponent)container;
            final Color selectedTextColor = textComponent2.getSelectedTextColor();
            if (textComponent2.getHighlighter() != null && selectedTextColor != null && !selectedTextColor.equals(foreground)) {
                final Highlighter.Highlight[] highlights = textComponent2.getHighlighter().getHighlights();
                if (highlights.length != 0) {
                    int n2 = 0;
                    int n3 = 0;
                    for (int i = 0; i < highlights.length; ++i) {
                        final Highlighter.Highlight highlight = highlights[i];
                        final int startOffset2 = highlight.getStartOffset();
                        final int endOffset2 = highlight.getEndOffset();
                        if (startOffset2 <= endOffset) {
                            if (endOffset2 >= startOffset) {
                                if (SwingUtilities2.useSelectedTextColor(highlight, textComponent2)) {
                                    if (startOffset2 <= startOffset && endOffset2 >= endOffset) {
                                        this.paintTextUsingColor(graphics, shape, selectedTextColor, startOffset, endOffset);
                                        n = 1;
                                        break;
                                    }
                                    if (n2 == 0) {
                                        this.initSelections(startOffset, endOffset);
                                        n2 = 1;
                                    }
                                    final int max = Math.max(startOffset, startOffset2);
                                    final int min = Math.min(endOffset, endOffset2);
                                    this.paintTextUsingColor(graphics, shape, selectedTextColor, max, min);
                                    final byte[] selections = this.selections;
                                    final int n4 = max - startOffset;
                                    ++selections[n4];
                                    final byte[] selections2 = this.selections;
                                    final int n5 = min - startOffset;
                                    --selections2[n5];
                                    ++n3;
                                }
                            }
                        }
                    }
                    if (n == 0 && n3 > 0) {
                        int n6 = -1;
                        int n7 = 0;
                        final int n8 = endOffset - startOffset;
                        while (n6++ < n8) {
                            while (n6 < n8 && this.selections[n6] == 0) {
                                ++n6;
                            }
                            if (n7 != n6) {
                                this.paintTextUsingColor(graphics, shape, foreground, startOffset + n7, startOffset + n6);
                            }
                            for (int n9 = 0; n6 < n8 && (n9 += this.selections[n6]) != 0; ++n6) {}
                            n7 = n6;
                        }
                        n = 1;
                    }
                }
            }
        }
        if (n == 0) {
            this.paintTextUsingColor(graphics, shape, foreground, startOffset, endOffset);
        }
    }
    
    final void paintTextUsingColor(final Graphics graphics, final Shape shape, final Color color, final int n, int n2) {
        graphics.setColor(color);
        this.painter.paint(this, graphics, shape, n, n2);
        final boolean underline = this.isUnderline();
        final boolean strikeThrough = this.isStrikeThrough();
        if (underline || strikeThrough) {
            final Rectangle rectangle = (Rectangle)((shape instanceof Rectangle) ? shape : shape.getBounds());
            final View parent = this.getParent();
            if (parent != null && parent.getEndOffset() == n2) {
                final Segment text = this.getText(n, n2);
                while (Character.isWhitespace(text.last())) {
                    --n2;
                    final Segment segment = text;
                    --segment.count;
                }
                SegmentCache.releaseSharedSegment(text);
            }
            int x = rectangle.x;
            final int startOffset = this.getStartOffset();
            if (startOffset != n) {
                x += (int)this.painter.getSpan(this, startOffset, n, this.getTabExpander(), (float)x);
            }
            final int n3 = x + (int)this.painter.getSpan(this, n, n2, this.getTabExpander(), (float)x);
            final int n4 = rectangle.y + (int)(this.painter.getHeight(this) - this.painter.getDescent(this));
            if (underline) {
                final int n5 = n4 + 1;
                graphics.drawLine(x, n5, n3, n5);
            }
            if (strikeThrough) {
                final int n6 = n4 - (int)(this.painter.getAscent(this) * 0.3f);
                graphics.drawLine(x, n6, n3, n6);
            }
        }
    }
    
    @Override
    public float getMinimumSpan(final int n) {
        switch (n) {
            case 0: {
                if (this.minimumSpan < 0.0f) {
                    this.minimumSpan = 0.0f;
                    int breakSpot;
                    for (int startOffset = this.getStartOffset(), i = this.getEndOffset(); i > startOffset; i = breakSpot - 1) {
                        breakSpot = this.getBreakSpot(startOffset, i);
                        if (breakSpot == -1) {
                            breakSpot = startOffset;
                        }
                        this.minimumSpan = Math.max(this.minimumSpan, this.getPartialSpan(breakSpot, i));
                    }
                }
                return this.minimumSpan;
            }
            case 1: {
                return super.getMinimumSpan(n);
            }
            default: {
                throw new IllegalArgumentException("Invalid axis: " + n);
            }
        }
    }
    
    @Override
    public float getPreferredSpan(final int n) {
        if (this.impliedCR) {
            return 0.0f;
        }
        this.checkPainter();
        final int startOffset = this.getStartOffset();
        final int endOffset = this.getEndOffset();
        switch (n) {
            case 0: {
                if (this.skipWidth) {
                    return 0.0f;
                }
                return this.painter.getSpan(this, startOffset, endOffset, this.expander, (float)this.x);
            }
            case 1: {
                float height = this.painter.getHeight(this);
                if (this.isSuperscript()) {
                    height += height / 3.0f;
                }
                return height;
            }
            default: {
                throw new IllegalArgumentException("Invalid axis: " + n);
            }
        }
    }
    
    @Override
    public float getAlignment(final int n) {
        this.checkPainter();
        if (n == 1) {
            final boolean superscript = this.isSuperscript();
            final boolean subscript = this.isSubscript();
            final float height = this.painter.getHeight(this);
            final float descent = this.painter.getDescent(this);
            final float ascent = this.painter.getAscent(this);
            float n2;
            if (superscript) {
                n2 = 1.0f;
            }
            else if (subscript) {
                n2 = ((height > 0.0f) ? ((height - (descent + ascent / 2.0f)) / height) : 0.0f);
            }
            else {
                n2 = ((height > 0.0f) ? ((height - descent) / height) : 0.0f);
            }
            return n2;
        }
        return super.getAlignment(n);
    }
    
    @Override
    public Shape modelToView(final int n, final Shape shape, final Position.Bias bias) throws BadLocationException {
        this.checkPainter();
        return this.painter.modelToView(this, n, bias, shape);
    }
    
    @Override
    public int viewToModel(final float n, final float n2, final Shape shape, final Position.Bias[] array) {
        this.checkPainter();
        return this.painter.viewToModel(this, n, n2, shape, array);
    }
    
    @Override
    public int getBreakWeight(final int n, final float n2, final float n3) {
        if (n == 0) {
            this.checkPainter();
            final int startOffset = this.getStartOffset();
            final int boundedPosition = this.painter.getBoundedPosition(this, startOffset, n2, n3);
            return (boundedPosition == startOffset) ? 0 : ((this.getBreakSpot(startOffset, boundedPosition) != -1) ? 2000 : 1000);
        }
        return super.getBreakWeight(n, n2, n3);
    }
    
    @Override
    public View breakView(final int n, final int n2, final float n3, final float n4) {
        if (n != 0) {
            return this;
        }
        this.checkPainter();
        int boundedPosition = this.painter.getBoundedPosition(this, n2, n3, n4);
        final int breakSpot = this.getBreakSpot(n2, boundedPosition);
        if (breakSpot != -1) {
            boundedPosition = breakSpot;
        }
        if (n2 == this.getStartOffset() && boundedPosition == this.getEndOffset()) {
            return this;
        }
        final GlyphView glyphView = (GlyphView)this.createFragment(n2, boundedPosition);
        glyphView.x = (int)n3;
        return glyphView;
    }
    
    private int getBreakSpot(final int n, final int n2) {
        if (this.breakSpots == null) {
            final int startOffset = this.getStartOffset();
            final int endOffset = this.getEndOffset();
            final int[] array = new int[endOffset + 1 - startOffset];
            int n3 = 0;
            final Element parentElement = this.getElement().getParentElement();
            final int n4 = (parentElement == null) ? startOffset : parentElement.getStartOffset();
            final int n5 = (parentElement == null) ? endOffset : parentElement.getEndOffset();
            final Segment text = this.getText(n4, n5);
            text.first();
            final BreakIterator breaker = this.getBreaker();
            breaker.setText(text);
            int n6 = endOffset + ((n5 > endOffset) ? 1 : 0);
            while (true) {
                n6 = breaker.preceding(text.offset + (n6 - n4)) + (n4 - text.offset);
                if (n6 <= startOffset) {
                    break;
                }
                array[n3++] = n6;
            }
            SegmentCache.releaseSharedSegment(text);
            System.arraycopy(array, 0, this.breakSpots = new int[n3], 0, n3);
        }
        int n7 = -1;
        int i = 0;
        while (i < this.breakSpots.length) {
            final int n8 = this.breakSpots[i];
            if (n8 <= n2) {
                if (n8 > n) {
                    n7 = n8;
                    break;
                }
                break;
            }
            else {
                ++i;
            }
        }
        return n7;
    }
    
    private BreakIterator getBreaker() {
        final Document document = this.getDocument();
        if (document != null && Boolean.TRUE.equals(document.getProperty(AbstractDocument.MultiByteProperty))) {
            final Container container = this.getContainer();
            return BreakIterator.getLineInstance((container == null) ? Locale.getDefault() : container.getLocale());
        }
        return new WhitespaceBasedBreakIterator();
    }
    
    @Override
    public View createFragment(final int n, final int n2) {
        this.checkPainter();
        final Element element = this.getElement();
        final GlyphView glyphView = (GlyphView)this.clone();
        glyphView.offset = n - element.getStartOffset();
        glyphView.length = n2 - n;
        glyphView.painter = this.painter.getPainter(glyphView, n, n2);
        glyphView.justificationInfo = null;
        return glyphView;
    }
    
    @Override
    public int getNextVisualPositionFrom(final int n, final Position.Bias bias, final Shape shape, final int n2, final Position.Bias[] array) throws BadLocationException {
        if (n < -1) {
            throw new BadLocationException("invalid position", n);
        }
        return this.painter.getNextVisualPositionFrom(this, n, bias, shape, n2, array);
    }
    
    @Override
    public void insertUpdate(final DocumentEvent documentEvent, final Shape shape, final ViewFactory viewFactory) {
        this.justificationInfo = null;
        this.breakSpots = null;
        this.minimumSpan = -1.0f;
        this.syncCR();
        this.preferenceChanged(null, true, false);
    }
    
    @Override
    public void removeUpdate(final DocumentEvent documentEvent, final Shape shape, final ViewFactory viewFactory) {
        this.justificationInfo = null;
        this.breakSpots = null;
        this.minimumSpan = -1.0f;
        this.syncCR();
        this.preferenceChanged(null, true, false);
    }
    
    @Override
    public void changedUpdate(final DocumentEvent documentEvent, final Shape shape, final ViewFactory viewFactory) {
        this.minimumSpan = -1.0f;
        this.syncCR();
        this.preferenceChanged(null, true, true);
    }
    
    private void syncCR() {
        if (this.impliedCR) {
            final Element parentElement = this.getElement().getParentElement();
            this.impliedCR = (parentElement != null && parentElement.getElementCount() > 1);
        }
    }
    
    @Override
    void updateAfterChange() {
        this.breakSpots = null;
    }
    
    JustificationInfo getJustificationInfo(final int n) {
        if (this.justificationInfo != null) {
            return this.justificationInfo;
        }
        final int startOffset = this.getStartOffset();
        final int endOffset = this.getEndOffset();
        final Segment text = this.getText(startOffset, endOffset);
        final int offset = text.offset;
        final int n2 = text.offset + text.count - 1;
        int n3 = n2 + 1;
        int n4 = offset - 1;
        int n5 = 0;
        int n6 = 0;
        int n7 = 0;
        boolean b = false;
        final BitSet set = new BitSet(endOffset - startOffset + 1);
        int i = n2;
        int n8 = 0;
        while (i >= offset) {
            if (' ' == text.array[i]) {
                set.set(i - offset);
                if (n8 == 0) {
                    ++n5;
                }
                else if (n8 == 1) {
                    n8 = 2;
                    n7 = 1;
                }
                else if (n8 == 2) {
                    ++n7;
                }
            }
            else {
                if ('\t' == text.array[i]) {
                    b = true;
                    break;
                }
                if (n8 == 0) {
                    if ('\n' != text.array[i] && '\r' != text.array[i]) {
                        n8 = 1;
                        n4 = i;
                    }
                }
                else if (n8 != 1) {
                    if (n8 == 2) {
                        n6 += n7;
                        n7 = 0;
                    }
                }
                n3 = i;
            }
            --i;
        }
        SegmentCache.releaseSharedSegment(text);
        int n9 = -1;
        if (n3 < n2) {
            n9 = n3 - offset;
        }
        int n10 = -1;
        if (n4 > offset) {
            n10 = n4 - offset;
        }
        return this.justificationInfo = new JustificationInfo(n9, n10, n7, n6, n5, b, set);
    }
    
    static class JustificationInfo
    {
        final int start;
        final int end;
        final int leadingSpaces;
        final int contentSpaces;
        final int trailingSpaces;
        final boolean hasTab;
        final BitSet spaceMap;
        
        JustificationInfo(final int start, final int end, final int leadingSpaces, final int contentSpaces, final int trailingSpaces, final boolean hasTab, final BitSet spaceMap) {
            this.start = start;
            this.end = end;
            this.leadingSpaces = leadingSpaces;
            this.contentSpaces = contentSpaces;
            this.trailingSpaces = trailingSpaces;
            this.hasTab = hasTab;
            this.spaceMap = spaceMap;
        }
    }
    
    public abstract static class GlyphPainter
    {
        public abstract float getSpan(final GlyphView p0, final int p1, final int p2, final TabExpander p3, final float p4);
        
        public abstract float getHeight(final GlyphView p0);
        
        public abstract float getAscent(final GlyphView p0);
        
        public abstract float getDescent(final GlyphView p0);
        
        public abstract void paint(final GlyphView p0, final Graphics p1, final Shape p2, final int p3, final int p4);
        
        public abstract Shape modelToView(final GlyphView p0, final int p1, final Position.Bias p2, final Shape p3) throws BadLocationException;
        
        public abstract int viewToModel(final GlyphView p0, final float p1, final float p2, final Shape p3, final Position.Bias[] p4);
        
        public abstract int getBoundedPosition(final GlyphView p0, final int p1, final float p2, final float p3);
        
        public GlyphPainter getPainter(final GlyphView glyphView, final int n, final int n2) {
            return this;
        }
        
        public int getNextVisualPositionFrom(final GlyphView glyphView, int n, final Position.Bias bias, final Shape shape, final int n2, final Position.Bias[] array) throws BadLocationException {
            final int startOffset = glyphView.getStartOffset();
            final int endOffset = glyphView.getEndOffset();
            switch (n2) {
                case 1:
                case 5: {
                    if (n != -1) {
                        return -1;
                    }
                    final Container container = glyphView.getContainer();
                    if (!(container instanceof JTextComponent)) {
                        return n;
                    }
                    final Caret caret = ((JTextComponent)container).getCaret();
                    final Point point = (caret != null) ? caret.getMagicCaretPosition() : null;
                    if (point == null) {
                        array[0] = Position.Bias.Forward;
                        return startOffset;
                    }
                    return glyphView.viewToModel((float)point.x, 0.0f, shape, array);
                }
                case 3: {
                    if (startOffset == glyphView.getDocument().getLength()) {
                        if (n == -1) {
                            array[0] = Position.Bias.Forward;
                            return startOffset;
                        }
                        return -1;
                    }
                    else {
                        if (n == -1) {
                            array[0] = Position.Bias.Forward;
                            return startOffset;
                        }
                        if (n == endOffset) {
                            return -1;
                        }
                        if (++n == endOffset) {
                            return -1;
                        }
                        array[0] = Position.Bias.Forward;
                        return n;
                    }
                    break;
                }
                case 7: {
                    if (startOffset == glyphView.getDocument().getLength()) {
                        if (n == -1) {
                            array[0] = Position.Bias.Forward;
                            return startOffset;
                        }
                        return -1;
                    }
                    else {
                        if (n == -1) {
                            array[0] = Position.Bias.Forward;
                            return endOffset - 1;
                        }
                        if (n == startOffset) {
                            return -1;
                        }
                        array[0] = Position.Bias.Forward;
                        return n - 1;
                    }
                    break;
                }
                default: {
                    throw new IllegalArgumentException("Bad direction: " + n2);
                }
            }
        }
    }
}
