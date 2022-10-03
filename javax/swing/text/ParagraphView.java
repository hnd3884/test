package javax.swing.text;

import java.util.Arrays;
import javax.swing.event.DocumentEvent;
import javax.swing.SizeRequirements;
import java.awt.geom.Rectangle2D;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Point;
import java.awt.Shape;
import java.awt.font.TextAttribute;

public class ParagraphView extends FlowView implements TabExpander
{
    private int justification;
    private float lineSpacing;
    protected int firstLineIndent;
    private int tabBase;
    static Class i18nStrategy;
    static char[] tabChars;
    static char[] tabDecimalChars;
    
    public ParagraphView(final Element element) {
        super(element, 1);
        this.firstLineIndent = 0;
        this.setPropertiesFromAttributes();
        final Object property = element.getDocument().getProperty("i18n");
        if (property != null && property.equals(Boolean.TRUE)) {
            try {
                if (ParagraphView.i18nStrategy == null) {
                    final String s = "javax.swing.text.TextLayoutStrategy";
                    final ClassLoader classLoader = this.getClass().getClassLoader();
                    if (classLoader != null) {
                        ParagraphView.i18nStrategy = classLoader.loadClass(s);
                    }
                    else {
                        ParagraphView.i18nStrategy = Class.forName(s);
                    }
                }
                final FlowStrategy instance = ParagraphView.i18nStrategy.newInstance();
                if (instance instanceof FlowStrategy) {
                    this.strategy = instance;
                }
            }
            catch (final Throwable t) {
                throw new StateInvariantError("ParagraphView: Can't create i18n strategy: " + t.getMessage());
            }
        }
    }
    
    protected void setJustification(final int justification) {
        this.justification = justification;
    }
    
    protected void setLineSpacing(final float lineSpacing) {
        this.lineSpacing = lineSpacing;
    }
    
    protected void setFirstLineIndent(final float n) {
        this.firstLineIndent = (int)n;
    }
    
    protected void setPropertiesFromAttributes() {
        final AttributeSet attributes = this.getAttributes();
        if (attributes != null) {
            this.setParagraphInsets(attributes);
            final Integer n = (Integer)attributes.getAttribute(StyleConstants.Alignment);
            int intValue;
            if (n == null) {
                final Object property = this.getElement().getDocument().getProperty(TextAttribute.RUN_DIRECTION);
                if (property != null && property.equals(TextAttribute.RUN_DIRECTION_RTL)) {
                    intValue = 2;
                }
                else {
                    intValue = 0;
                }
            }
            else {
                intValue = n;
            }
            this.setJustification(intValue);
            this.setLineSpacing(StyleConstants.getLineSpacing(attributes));
            this.setFirstLineIndent(StyleConstants.getFirstLineIndent(attributes));
        }
    }
    
    protected int getLayoutViewCount() {
        return this.layoutPool.getViewCount();
    }
    
    protected View getLayoutView(final int n) {
        return this.layoutPool.getView(n);
    }
    
    @Override
    protected int getNextNorthSouthVisualPositionFrom(final int n, final Position.Bias bias, final Shape shape, final int n2, final Position.Bias[] array) throws BadLocationException {
        int n3;
        if (n == -1) {
            n3 = ((n2 == 1) ? (this.getViewCount() - 1) : 0);
        }
        else {
            if (bias == Position.Bias.Backward && n > 0) {
                n3 = this.getViewIndexAtPosition(n - 1);
            }
            else {
                n3 = this.getViewIndexAtPosition(n);
            }
            if (n2 == 1) {
                if (n3 == 0) {
                    return -1;
                }
                --n3;
            }
            else if (++n3 >= this.getViewCount()) {
                return -1;
            }
        }
        final JTextComponent textComponent = (JTextComponent)this.getContainer();
        final Caret caret = textComponent.getCaret();
        final Point point = (caret != null) ? caret.getMagicCaretPosition() : null;
        int n4;
        if (point == null) {
            Rectangle modelToView;
            try {
                modelToView = textComponent.getUI().modelToView(textComponent, n, bias);
            }
            catch (final BadLocationException ex) {
                modelToView = null;
            }
            if (modelToView == null) {
                n4 = 0;
            }
            else {
                n4 = modelToView.getBounds().x;
            }
        }
        else {
            n4 = point.x;
        }
        return this.getClosestPositionTo(n, bias, shape, n2, array, n3, n4);
    }
    
    protected int getClosestPositionTo(final int n, final Position.Bias bias, final Shape shape, final int n2, final Position.Bias[] array, final int n3, final int n4) throws BadLocationException {
        final JTextComponent textComponent = (JTextComponent)this.getContainer();
        final Document document = this.getDocument();
        final View view = this.getView(n3);
        int i = -1;
        array[0] = Position.Bias.Forward;
        for (int j = 0; j < view.getViewCount(); ++j) {
            final View view2 = view.getView(j);
            final int startOffset = view2.getStartOffset();
            if (AbstractDocument.isLeftToRight(document, startOffset, startOffset + 1)) {
                i = startOffset;
                for (int endOffset = view2.getEndOffset(); i < endOffset; ++i) {
                    final float n5 = (float)textComponent.modelToView(i).getBounds().x;
                    if (n5 >= n4) {
                        while (++i < endOffset && textComponent.modelToView(i).getBounds().x == n5) {}
                        return --i;
                    }
                }
                --i;
            }
            else {
                for (i = view2.getEndOffset() - 1; i >= startOffset; --i) {
                    final float n6 = (float)textComponent.modelToView(i).getBounds().x;
                    if (n6 >= n4) {
                        while (--i >= startOffset && textComponent.modelToView(i).getBounds().x == n6) {}
                        return ++i;
                    }
                }
                ++i;
            }
        }
        if (i == -1) {
            return this.getStartOffset();
        }
        return i;
    }
    
    @Override
    protected boolean flipEastAndWestAtEnds(int startOffset, final Position.Bias bias) {
        final Document document = this.getDocument();
        startOffset = this.getStartOffset();
        return !AbstractDocument.isLeftToRight(document, startOffset, startOffset + 1);
    }
    
    @Override
    public int getFlowSpan(final int n) {
        final View view = this.getView(n);
        int n2 = 0;
        if (view instanceof Row) {
            final Row row = (Row)view;
            n2 = row.getLeftInset() + row.getRightInset();
        }
        return (this.layoutSpan == Integer.MAX_VALUE) ? this.layoutSpan : (this.layoutSpan - n2);
    }
    
    @Override
    public int getFlowStart(final int n) {
        final View view = this.getView(n);
        int leftInset = 0;
        if (view instanceof Row) {
            leftInset = ((Row)view).getLeftInset();
        }
        return this.tabBase + leftInset;
    }
    
    @Override
    protected View createRow() {
        return new Row(this.getElement());
    }
    
    @Override
    public float nextTabStop(float n, final int n2) {
        if (this.justification != 0) {
            return n + 10.0f;
        }
        n -= this.tabBase;
        final TabSet tabSet = this.getTabSet();
        if (tabSet == null) {
            return (float)(this.tabBase + ((int)n / 72 + 1) * 72);
        }
        final TabStop tabAfter = tabSet.getTabAfter(n + 0.01f);
        if (tabAfter == null) {
            return this.tabBase + n + 5.0f;
        }
        final int alignment = tabAfter.getAlignment();
        int n3 = 0;
        switch (alignment) {
            default: {
                return this.tabBase + tabAfter.getPosition();
            }
            case 5: {
                return this.tabBase + tabAfter.getPosition();
            }
            case 1:
            case 2: {
                n3 = this.findOffsetToCharactersInString(ParagraphView.tabChars, n2 + 1);
                break;
            }
            case 4: {
                n3 = this.findOffsetToCharactersInString(ParagraphView.tabDecimalChars, n2 + 1);
                break;
            }
        }
        if (n3 == -1) {
            n3 = this.getEndOffset();
        }
        final float partialSize = this.getPartialSize(n2 + 1, n3);
        switch (alignment) {
            case 1:
            case 4: {
                return this.tabBase + Math.max(n, tabAfter.getPosition() - partialSize);
            }
            case 2: {
                return this.tabBase + Math.max(n, tabAfter.getPosition() - partialSize / 2.0f);
            }
            default: {
                return n;
            }
        }
    }
    
    protected TabSet getTabSet() {
        return StyleConstants.getTabSet(this.getElement().getAttributes());
    }
    
    protected float getPartialSize(int n, final int n2) {
        float n3 = 0.0f;
        this.getViewCount();
        int endOffset;
        for (int elementIndex = this.getElement().getElementIndex(n), viewCount = this.layoutPool.getViewCount(); n < n2 && elementIndex < viewCount; n = endOffset) {
            final View view = this.layoutPool.getView(elementIndex++);
            endOffset = view.getEndOffset();
            final int min = Math.min(n2, endOffset);
            if (view instanceof TabableView) {
                n3 += ((TabableView)view).getPartialSpan(n, min);
            }
            else {
                if (n != view.getStartOffset() || min != view.getEndOffset()) {
                    return 0.0f;
                }
                n3 += view.getPreferredSpan(0);
            }
        }
        return n3;
    }
    
    protected int findOffsetToCharactersInString(final char[] array, final int n) {
        final int length = array.length;
        final int endOffset = this.getEndOffset();
        final Segment segment = new Segment();
        try {
            this.getDocument().getText(n, endOffset - n, segment);
        }
        catch (final BadLocationException ex) {
            return -1;
        }
        for (int i = segment.offset; i < segment.offset + segment.count; ++i) {
            final char c = segment.array[i];
            for (int j = 0; j < length; ++j) {
                if (c == array[j]) {
                    return i - segment.offset + n;
                }
            }
        }
        return -1;
    }
    
    protected float getTabBase() {
        return (float)this.tabBase;
    }
    
    @Override
    public void paint(final Graphics graphics, final Shape shape) {
        final Rectangle rectangle = (Rectangle)((shape instanceof Rectangle) ? shape : shape.getBounds());
        this.tabBase = rectangle.x + this.getLeftInset();
        super.paint(graphics, shape);
        if (this.firstLineIndent < 0) {
            final Shape childAllocation = this.getChildAllocation(0, shape);
            if (childAllocation != null && childAllocation.intersects(rectangle)) {
                final int n = rectangle.x + this.getLeftInset() + this.firstLineIndent;
                final int n2 = rectangle.y + this.getTopInset();
                final Rectangle clipBounds = graphics.getClipBounds();
                this.tempRect.x = n + this.getOffset(0, 0);
                this.tempRect.y = n2 + this.getOffset(1, 0);
                this.tempRect.width = this.getSpan(0, 0) - this.firstLineIndent;
                this.tempRect.height = this.getSpan(1, 0);
                if (this.tempRect.intersects(clipBounds)) {
                    this.tempRect.x -= this.firstLineIndent;
                    this.paintChild(graphics, this.tempRect, 0);
                }
            }
        }
    }
    
    @Override
    public float getAlignment(final int n) {
        switch (n) {
            case 1: {
                float n2 = 0.5f;
                if (this.getViewCount() != 0) {
                    final int n3 = (int)this.getPreferredSpan(1);
                    final int n4 = (int)this.getView(0).getPreferredSpan(1);
                    n2 = ((n3 != 0) ? (n4 / 2 / (float)n3) : 0.0f);
                }
                return n2;
            }
            case 0: {
                return 0.5f;
            }
            default: {
                throw new IllegalArgumentException("Invalid axis: " + n);
            }
        }
    }
    
    public View breakView(final int n, final float n2, final Shape shape) {
        if (n == 1) {
            if (shape != null) {
                final Rectangle bounds = shape.getBounds();
                this.setSize((float)bounds.width, (float)bounds.height);
            }
            return this;
        }
        return this;
    }
    
    public int getBreakWeight(final int n, final float n2) {
        if (n == 1) {
            return 0;
        }
        return 0;
    }
    
    @Override
    protected SizeRequirements calculateMinorAxisRequirements(final int n, SizeRequirements calculateMinorAxisRequirements) {
        calculateMinorAxisRequirements = super.calculateMinorAxisRequirements(n, calculateMinorAxisRequirements);
        float n2 = 0.0f;
        float n3 = 0.0f;
        for (int layoutViewCount = this.getLayoutViewCount(), i = 0; i < layoutViewCount; ++i) {
            final View layoutView = this.getLayoutView(i);
            final float minimumSpan = layoutView.getMinimumSpan(n);
            if (layoutView.getBreakWeight(n, 0.0f, layoutView.getMaximumSpan(n)) > 0) {
                final int startOffset = layoutView.getStartOffset();
                final int endOffset = layoutView.getEndOffset();
                final float edgeSpan = this.findEdgeSpan(layoutView, n, startOffset, startOffset, endOffset);
                final float edgeSpan2 = this.findEdgeSpan(layoutView, n, endOffset, startOffset, endOffset);
                n2 = Math.max(n2, Math.max(minimumSpan, n3 + edgeSpan));
                n3 = edgeSpan2;
            }
            else {
                n3 += minimumSpan;
                n2 = Math.max(n2, n3);
            }
        }
        calculateMinorAxisRequirements.minimum = Math.max(calculateMinorAxisRequirements.minimum, (int)n2);
        calculateMinorAxisRequirements.preferred = Math.max(calculateMinorAxisRequirements.minimum, calculateMinorAxisRequirements.preferred);
        calculateMinorAxisRequirements.maximum = Math.max(calculateMinorAxisRequirements.preferred, calculateMinorAxisRequirements.maximum);
        return calculateMinorAxisRequirements;
    }
    
    private float findEdgeSpan(final View view, final int n, final int n2, int n3, int n4) {
        final int n5 = n4 - n3;
        if (n5 <= 1) {
            return view.getMinimumSpan(n);
        }
        final int n6 = n3 + n5 / 2;
        final boolean b = n6 > n2;
        final View view2 = b ? view.createFragment(n2, n6) : view.createFragment(n6, n2);
        if (view2.getBreakWeight(n, 0.0f, view2.getMaximumSpan(n)) > 0 == b) {
            n4 = n6;
        }
        else {
            n3 = n6;
        }
        return this.findEdgeSpan(view2, n, n2, n3, n4);
    }
    
    @Override
    public void changedUpdate(final DocumentEvent documentEvent, final Shape shape, final ViewFactory viewFactory) {
        this.setPropertiesFromAttributes();
        this.layoutChanged(0);
        this.layoutChanged(1);
        super.changedUpdate(documentEvent, shape, viewFactory);
    }
    
    static {
        (ParagraphView.tabChars = new char[1])[0] = '\t';
        (ParagraphView.tabDecimalChars = new char[2])[0] = '\t';
        ParagraphView.tabDecimalChars[1] = '.';
    }
    
    class Row extends BoxView
    {
        static final int SPACE_ADDON = 0;
        static final int SPACE_ADDON_LEFTOVER_END = 1;
        static final int START_JUSTIFIABLE = 2;
        static final int END_JUSTIFIABLE = 3;
        int[] justificationData;
        
        Row(final Element element) {
            super(element, 0);
            this.justificationData = null;
        }
        
        @Override
        protected void loadChildren(final ViewFactory viewFactory) {
        }
        
        @Override
        public AttributeSet getAttributes() {
            final View parent = this.getParent();
            return (parent != null) ? parent.getAttributes() : null;
        }
        
        @Override
        public float getAlignment(final int n) {
            if (n == 0) {
                switch (ParagraphView.this.justification) {
                    case 0: {
                        return 0.0f;
                    }
                    case 2: {
                        return 1.0f;
                    }
                    case 1: {
                        return 0.5f;
                    }
                    case 3: {
                        float n2 = 0.5f;
                        if (this.isJustifiableDocument()) {
                            n2 = 0.0f;
                        }
                        return n2;
                    }
                }
            }
            return super.getAlignment(n);
        }
        
        @Override
        public Shape modelToView(final int n, final Shape shape, final Position.Bias bias) throws BadLocationException {
            final View viewAtPosition = this.getViewAtPosition(n, shape.getBounds());
            if (viewAtPosition != null && !viewAtPosition.getElement().isLeaf()) {
                return super.modelToView(n, shape, bias);
            }
            final Rectangle bounds = shape.getBounds();
            final int height = bounds.height;
            final int y = bounds.y;
            final Rectangle bounds2 = super.modelToView(n, shape, bias).getBounds();
            bounds2.height = height;
            bounds2.y = y;
            return bounds2;
        }
        
        @Override
        public int getStartOffset() {
            int min = Integer.MAX_VALUE;
            for (int viewCount = this.getViewCount(), i = 0; i < viewCount; ++i) {
                min = Math.min(min, this.getView(i).getStartOffset());
            }
            return min;
        }
        
        @Override
        public int getEndOffset() {
            int max = 0;
            for (int viewCount = this.getViewCount(), i = 0; i < viewCount; ++i) {
                max = Math.max(max, this.getView(i).getEndOffset());
            }
            return max;
        }
        
        @Override
        protected void layoutMinorAxis(final int n, final int n2, final int[] array, final int[] array2) {
            this.baselineLayout(n, n2, array, array2);
        }
        
        @Override
        protected SizeRequirements calculateMinorAxisRequirements(final int n, final SizeRequirements sizeRequirements) {
            return this.baselineRequirements(n, sizeRequirements);
        }
        
        private boolean isLastRow() {
            final View parent;
            return (parent = this.getParent()) == null || this == parent.getView(parent.getViewCount() - 1);
        }
        
        private boolean isBrokenRow() {
            boolean b = false;
            final int viewCount = this.getViewCount();
            if (viewCount > 0 && this.getView(viewCount - 1).getBreakWeight(0, 0.0f, 0.0f) >= 3000) {
                b = true;
            }
            return b;
        }
        
        private boolean isJustifiableDocument() {
            return !Boolean.TRUE.equals(this.getDocument().getProperty("i18n"));
        }
        
        private boolean isJustifyEnabled() {
            return ParagraphView.this.justification == 3 && this.isJustifiableDocument() && !this.isLastRow() && !this.isBrokenRow();
        }
        
        @Override
        protected SizeRequirements calculateMajorAxisRequirements(final int n, final SizeRequirements sizeRequirements) {
            final int[] justificationData = this.justificationData;
            this.justificationData = null;
            final SizeRequirements calculateMajorAxisRequirements = super.calculateMajorAxisRequirements(n, sizeRequirements);
            if (this.isJustifyEnabled()) {
                this.justificationData = justificationData;
            }
            return calculateMajorAxisRequirements;
        }
        
        @Override
        protected void layoutMajorAxis(final int n, final int n2, final int[] array, final int[] array2) {
            final int[] justificationData = this.justificationData;
            this.justificationData = null;
            super.layoutMajorAxis(n, n2, array, array2);
            if (!this.isJustifyEnabled()) {
                return;
            }
            int n3 = 0;
            for (int length = array2.length, i = 0; i < length; ++i) {
                n3 += array2[i];
            }
            if (n3 == n) {
                return;
            }
            int n4 = 0;
            int n5 = -1;
            int n6 = -1;
            int leadingSpaces = 0;
            final int startOffset = this.getStartOffset();
            final int[] array3 = new int[this.getEndOffset() - startOffset];
            Arrays.fill(array3, 0);
            for (int j = this.getViewCount() - 1; j >= 0; --j) {
                final View view = this.getView(j);
                if (view instanceof GlyphView) {
                    final GlyphView.JustificationInfo justificationInfo = ((GlyphView)view).getJustificationInfo(startOffset);
                    final int startOffset2 = view.getStartOffset();
                    final int n7 = startOffset2 - startOffset;
                    for (int k = 0; k < justificationInfo.spaceMap.length(); ++k) {
                        if (justificationInfo.spaceMap.get(k)) {
                            array3[k + n7] = 1;
                        }
                    }
                    if (n5 > 0) {
                        if (justificationInfo.end >= 0) {
                            n4 += justificationInfo.trailingSpaces;
                        }
                        else {
                            leadingSpaces += justificationInfo.trailingSpaces;
                        }
                    }
                    if (justificationInfo.start >= 0) {
                        n5 = justificationInfo.start + startOffset2;
                        n4 += leadingSpaces;
                    }
                    if (justificationInfo.end >= 0 && n6 < 0) {
                        n6 = justificationInfo.end + startOffset2;
                    }
                    n4 += justificationInfo.contentSpaces;
                    leadingSpaces = justificationInfo.leadingSpaces;
                    if (justificationInfo.hasTab) {
                        break;
                    }
                }
            }
            if (n4 <= 0) {
                return;
            }
            final int n8 = n - n3;
            final int n9 = (n4 > 0) ? (n8 / n4) : 0;
            int n10 = -1;
            for (int n11 = n5 - startOffset, l = n8 - n9 * n4; l > 0; l -= array3[n11], ++n11) {
                n10 = n11;
            }
            if (n9 > 0 || n10 >= 0) {
                (this.justificationData = ((justificationData != null) ? justificationData : new int[4]))[0] = n9;
                this.justificationData[1] = n10;
                this.justificationData[2] = n5 - startOffset;
                this.justificationData[3] = n6 - startOffset;
                super.layoutMajorAxis(n, n2, array, array2);
            }
        }
        
        @Override
        public float getMaximumSpan(final int n) {
            float maximumSpan;
            if (0 == n && this.isJustifyEnabled()) {
                maximumSpan = Float.MAX_VALUE;
            }
            else {
                maximumSpan = super.getMaximumSpan(n);
            }
            return maximumSpan;
        }
        
        @Override
        protected int getViewIndexAtPosition(final int n) {
            if (n < this.getStartOffset() || n >= this.getEndOffset()) {
                return -1;
            }
            for (int i = this.getViewCount() - 1; i >= 0; --i) {
                final View view = this.getView(i);
                if (n >= view.getStartOffset() && n < view.getEndOffset()) {
                    return i;
                }
            }
            return -1;
        }
        
        @Override
        protected short getLeftInset() {
            int firstLineIndent = 0;
            final View parent;
            if ((parent = this.getParent()) != null && this == parent.getView(0)) {
                firstLineIndent = ParagraphView.this.firstLineIndent;
            }
            return (short)(super.getLeftInset() + firstLineIndent);
        }
        
        @Override
        protected short getBottomInset() {
            return (short)(super.getBottomInset() + ((this.minorRequest != null) ? this.minorRequest.preferred : 0) * ParagraphView.this.lineSpacing);
        }
    }
}
