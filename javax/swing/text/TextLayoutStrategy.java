package javax.swing.text;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.awt.Font;
import java.util.Set;
import java.awt.Container;
import java.awt.font.FontRenderContext;
import java.text.AttributedCharacterIterator;
import java.awt.font.TextAttribute;
import javax.swing.JComponent;
import java.text.BreakIterator;
import java.awt.Component;
import sun.swing.SwingUtilities2;
import java.awt.font.TextLayout;
import sun.font.BidiUtils;
import java.awt.Rectangle;
import javax.swing.event.DocumentEvent;
import java.awt.font.LineBreakMeasurer;

class TextLayoutStrategy extends FlowView.FlowStrategy
{
    private LineBreakMeasurer measurer;
    private AttributedSegment text;
    
    public TextLayoutStrategy() {
        this.text = new AttributedSegment();
    }
    
    @Override
    public void insertUpdate(final FlowView flowView, final DocumentEvent documentEvent, final Rectangle rectangle) {
        this.sync(flowView);
        super.insertUpdate(flowView, documentEvent, rectangle);
    }
    
    @Override
    public void removeUpdate(final FlowView flowView, final DocumentEvent documentEvent, final Rectangle rectangle) {
        this.sync(flowView);
        super.removeUpdate(flowView, documentEvent, rectangle);
    }
    
    @Override
    public void changedUpdate(final FlowView flowView, final DocumentEvent documentEvent, final Rectangle rectangle) {
        this.sync(flowView);
        super.changedUpdate(flowView, documentEvent, rectangle);
    }
    
    @Override
    public void layout(final FlowView flowView) {
        super.layout(flowView);
    }
    
    @Override
    protected int layoutRow(final FlowView flowView, final int n, final int n2) {
        final int layoutRow = super.layoutRow(flowView, n, n2);
        final View view = flowView.getView(n);
        final Object property = flowView.getDocument().getProperty("i18n");
        if (property != null && property.equals(Boolean.TRUE)) {
            final int viewCount = view.getViewCount();
            if (viewCount > 1) {
                final Element bidiRootElement = ((AbstractDocument)flowView.getDocument()).getBidiRootElement();
                final byte[] array = new byte[viewCount];
                final View[] array2 = new View[viewCount];
                for (int i = 0; i < viewCount; ++i) {
                    final View view2 = view.getView(i);
                    array[i] = (byte)StyleConstants.getBidiLevel(bidiRootElement.getElement(bidiRootElement.getElementIndex(view2.getStartOffset())).getAttributes());
                    array2[i] = view2;
                }
                BidiUtils.reorderVisually(array, array2);
                view.replace(0, viewCount, array2);
            }
        }
        return layoutRow;
    }
    
    @Override
    protected void adjustRow(final FlowView flowView, final int n, final int n2, final int n3) {
    }
    
    @Override
    protected View createView(final FlowView flowView, final int n, final int n2, final int n3) {
        final View logicalView = this.getLogicalView(flowView);
        flowView.getView(n3);
        final boolean b = this.viewBuffer.size() != 0;
        final View view = logicalView.getView(logicalView.getViewIndex(n, Position.Bias.Forward));
        final int limitingOffset = this.getLimitingOffset(view, n, n2, b);
        if (limitingOffset == n) {
            return null;
        }
        View fragment;
        if (n == view.getStartOffset() && limitingOffset == view.getEndOffset()) {
            fragment = view;
        }
        else {
            fragment = view.createFragment(n, limitingOffset);
        }
        if (fragment instanceof GlyphView && this.measurer != null) {
            boolean b2 = false;
            final int startOffset = fragment.getStartOffset();
            final int endOffset = fragment.getEndOffset();
            if (endOffset - startOffset == 1 && ((GlyphView)fragment).getText(startOffset, endOffset).first() == '\t') {
                b2 = true;
            }
            final TextLayout textLayout = b2 ? null : this.measurer.nextLayout((float)n2, this.text.toIteratorIndex(limitingOffset), b);
            if (textLayout != null) {
                ((GlyphView)fragment).setGlyphPainter(new GlyphPainter2(textLayout));
            }
        }
        return fragment;
    }
    
    int getLimitingOffset(final View view, final int n, final int n2, final boolean b) {
        int n3 = view.getEndOffset();
        final Document document = view.getDocument();
        if (document instanceof AbstractDocument) {
            final Element bidiRootElement = ((AbstractDocument)document).getBidiRootElement();
            if (bidiRootElement.getElementCount() > 1) {
                n3 = Math.min(bidiRootElement.getElement(bidiRootElement.getElementIndex(n)).getEndOffset(), n3);
            }
        }
        if (view instanceof GlyphView) {
            final Segment text = ((GlyphView)view).getText(n, n3);
            if (text.first() == '\t') {
                n3 = n + 1;
            }
            else {
                for (char c = text.next(); c != '\uffff'; c = text.next()) {
                    if (c == '\t') {
                        n3 = n + text.getIndex() - text.getBeginIndex();
                        break;
                    }
                }
            }
        }
        int n4 = this.text.toIteratorIndex(n3);
        if (this.measurer != null) {
            final int iteratorIndex = this.text.toIteratorIndex(n);
            if (this.measurer.getPosition() != iteratorIndex) {
                this.measurer.setPosition(iteratorIndex);
            }
            n4 = this.measurer.nextOffset((float)n2, n4, b);
        }
        return this.text.toModelPosition(n4);
    }
    
    void sync(final FlowView flowView) {
        final View logicalView = this.getLogicalView(flowView);
        this.text.setView(logicalView);
        final FontRenderContext fontRenderContext = SwingUtilities2.getFontRenderContext(flowView.getContainer());
        final Container container = flowView.getContainer();
        BreakIterator breakIterator;
        if (container != null) {
            breakIterator = BreakIterator.getLineInstance(container.getLocale());
        }
        else {
            breakIterator = BreakIterator.getLineInstance();
        }
        Object clientProperty = null;
        if (container instanceof JComponent) {
            clientProperty = ((JComponent)container).getClientProperty(TextAttribute.NUMERIC_SHAPING);
        }
        this.text.setShaper(clientProperty);
        this.measurer = new LineBreakMeasurer(this.text, breakIterator, fontRenderContext);
        for (int viewCount = logicalView.getViewCount(), i = 0; i < viewCount; ++i) {
            final View view = logicalView.getView(i);
            if (view instanceof GlyphView) {
                final int startOffset = view.getStartOffset();
                final int endOffset = view.getEndOffset();
                this.measurer.setPosition(this.text.toIteratorIndex(startOffset));
                ((GlyphView)view).setGlyphPainter(new GlyphPainter2(this.measurer.nextLayout(Float.MAX_VALUE, this.text.toIteratorIndex(endOffset), false)));
            }
        }
        this.measurer.setPosition(this.text.getBeginIndex());
    }
    
    static class AttributedSegment extends Segment implements AttributedCharacterIterator
    {
        View v;
        static Set<Attribute> keys;
        private Object shaper;
        
        AttributedSegment() {
            this.shaper = null;
        }
        
        View getView() {
            return this.v;
        }
        
        void setView(final View v) {
            this.v = v;
            final Document document = v.getDocument();
            final int startOffset = v.getStartOffset();
            final int endOffset = v.getEndOffset();
            try {
                document.getText(startOffset, endOffset - startOffset, this);
            }
            catch (final BadLocationException ex) {
                throw new IllegalArgumentException("Invalid view");
            }
            this.first();
        }
        
        int getFontBoundary(int n, final int n2) {
            View view = this.v.getView(n);
            Font font;
            for (font = this.getFont(n), n += n2; n >= 0 && n < this.v.getViewCount() && this.getFont(n) == font; n += n2) {
                view = this.v.getView(n);
            }
            return (n2 < 0) ? view.getStartOffset() : view.getEndOffset();
        }
        
        Font getFont(final int n) {
            final View view = this.v.getView(n);
            if (view instanceof GlyphView) {
                return ((GlyphView)view).getFont();
            }
            return null;
        }
        
        int toModelPosition(final int n) {
            return this.v.getStartOffset() + (n - this.getBeginIndex());
        }
        
        int toIteratorIndex(final int n) {
            return n - this.v.getStartOffset() + this.getBeginIndex();
        }
        
        private void setShaper(final Object shaper) {
            this.shaper = shaper;
        }
        
        @Override
        public int getRunStart() {
            return this.toIteratorIndex(this.v.getView(this.v.getViewIndex(this.toModelPosition(this.getIndex()), Position.Bias.Forward)).getStartOffset());
        }
        
        @Override
        public int getRunStart(final Attribute attribute) {
            if (attribute instanceof TextAttribute) {
                final int viewIndex = this.v.getViewIndex(this.toModelPosition(this.getIndex()), Position.Bias.Forward);
                if (attribute == TextAttribute.FONT) {
                    return this.toIteratorIndex(this.getFontBoundary(viewIndex, -1));
                }
            }
            return this.getBeginIndex();
        }
        
        @Override
        public int getRunStart(final Set<? extends Attribute> set) {
            int n = this.getBeginIndex();
            final Object[] array = set.toArray();
            for (int i = 0; i < array.length; ++i) {
                n = Math.max(this.getRunStart((Attribute)array[i]), n);
            }
            return Math.min(this.getIndex(), n);
        }
        
        @Override
        public int getRunLimit() {
            return this.toIteratorIndex(this.v.getView(this.v.getViewIndex(this.toModelPosition(this.getIndex()), Position.Bias.Forward)).getEndOffset());
        }
        
        @Override
        public int getRunLimit(final Attribute attribute) {
            if (attribute instanceof TextAttribute) {
                final int viewIndex = this.v.getViewIndex(this.toModelPosition(this.getIndex()), Position.Bias.Forward);
                if (attribute == TextAttribute.FONT) {
                    return this.toIteratorIndex(this.getFontBoundary(viewIndex, 1));
                }
            }
            return this.getEndIndex();
        }
        
        @Override
        public int getRunLimit(final Set<? extends Attribute> set) {
            int n = this.getEndIndex();
            final Object[] array = set.toArray();
            for (int i = 0; i < array.length; ++i) {
                n = Math.min(this.getRunLimit((Attribute)array[i]), n);
            }
            return Math.max(this.getIndex(), n);
        }
        
        @Override
        public Map<Attribute, Object> getAttributes() {
            final Object[] array = AttributedSegment.keys.toArray();
            final Hashtable hashtable = new Hashtable();
            for (int i = 0; i < array.length; ++i) {
                final TextAttribute textAttribute = (TextAttribute)array[i];
                final Object attribute = this.getAttribute(textAttribute);
                if (attribute != null) {
                    hashtable.put(textAttribute, attribute);
                }
            }
            return hashtable;
        }
        
        @Override
        public Object getAttribute(final Attribute attribute) {
            final int viewIndex = this.v.getViewIndex(this.toModelPosition(this.getIndex()), Position.Bias.Forward);
            if (attribute == TextAttribute.FONT) {
                return this.getFont(viewIndex);
            }
            if (attribute == TextAttribute.RUN_DIRECTION) {
                return this.v.getDocument().getProperty(TextAttribute.RUN_DIRECTION);
            }
            if (attribute == TextAttribute.NUMERIC_SHAPING) {
                return this.shaper;
            }
            return null;
        }
        
        @Override
        public Set<Attribute> getAllAttributeKeys() {
            return AttributedSegment.keys;
        }
        
        static {
            (AttributedSegment.keys = new HashSet<Attribute>()).add(TextAttribute.FONT);
            AttributedSegment.keys.add(TextAttribute.RUN_DIRECTION);
            AttributedSegment.keys.add(TextAttribute.NUMERIC_SHAPING);
        }
    }
}
