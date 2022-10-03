package javax.swing.text;

import java.awt.Shape;
import java.awt.Component;
import java.text.AttributedCharacterIterator;
import java.awt.font.TextAttribute;
import java.text.AttributedString;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.text.CharacterIterator;
import java.text.BreakIterator;
import java.awt.FontMetrics;
import sun.swing.SwingUtilities2;
import java.awt.Graphics;
import java.awt.Container;
import javax.swing.JComponent;

public class Utilities
{
    static JComponent getJComponent(final View view) {
        if (view != null) {
            final Container container = view.getContainer();
            if (container instanceof JComponent) {
                return (JComponent)container;
            }
        }
        return null;
    }
    
    public static final int drawTabbedText(final Segment segment, final int n, final int n2, final Graphics graphics, final TabExpander tabExpander, final int n3) {
        return drawTabbedText(null, segment, n, n2, graphics, tabExpander, n3);
    }
    
    static final int drawTabbedText(final View view, final Segment segment, final int n, final int n2, final Graphics graphics, final TabExpander tabExpander, final int n3) {
        return drawTabbedText(view, segment, n, n2, graphics, tabExpander, n3, null);
    }
    
    static final int drawTabbedText(final View view, final Segment segment, int n, final int n2, final Graphics graphics, final TabExpander tabExpander, final int n3, final int[] array) {
        final JComponent jComponent = getJComponent(view);
        final FontMetrics fontMetrics = SwingUtilities2.getFontMetrics(jComponent, graphics);
        int n4 = n;
        final char[] array2 = segment.array;
        final int offset = segment.offset;
        int n5 = 0;
        int offset2 = segment.offset;
        int n6 = 0;
        int n7 = -1;
        int n8 = 0;
        int n9 = 0;
        if (array != null) {
            int n10 = -n3 + offset;
            final View parent;
            if (view != null && (parent = view.getParent()) != null) {
                n10 += parent.getStartOffset();
            }
            n6 = array[0];
            n7 = array[1] + n10;
            n8 = array[2] + n10;
            n9 = array[3] + n10;
        }
        for (int n11 = segment.offset + segment.count, i = offset; i < n11; ++i) {
            if (array2[i] == '\t' || ((n6 != 0 || i <= n7) && array2[i] == ' ' && n8 <= i && i <= n9)) {
                if (n5 > 0) {
                    n4 = SwingUtilities2.drawChars(jComponent, graphics, array2, offset2, n5, n, n2);
                    n5 = 0;
                }
                offset2 = i + 1;
                if (array2[i] == '\t') {
                    if (tabExpander != null) {
                        n4 = (int)tabExpander.nextTabStop((float)n4, n3 + i - offset);
                    }
                    else {
                        n4 += fontMetrics.charWidth(' ');
                    }
                }
                else if (array2[i] == ' ') {
                    n4 += fontMetrics.charWidth(' ') + n6;
                    if (i <= n7) {
                        ++n4;
                    }
                }
                n = n4;
            }
            else if (array2[i] == '\n' || array2[i] == '\r') {
                if (n5 > 0) {
                    n4 = SwingUtilities2.drawChars(jComponent, graphics, array2, offset2, n5, n, n2);
                    n5 = 0;
                }
                offset2 = i + 1;
                n = n4;
            }
            else {
                ++n5;
            }
        }
        if (n5 > 0) {
            n4 = SwingUtilities2.drawChars(jComponent, graphics, array2, offset2, n5, n, n2);
        }
        return n4;
    }
    
    public static final int getTabbedTextWidth(final Segment segment, final FontMetrics fontMetrics, final int n, final TabExpander tabExpander, final int n2) {
        return getTabbedTextWidth(null, segment, fontMetrics, n, tabExpander, n2, null);
    }
    
    static final int getTabbedTextWidth(final View view, final Segment segment, final FontMetrics fontMetrics, final int n, final TabExpander tabExpander, final int n2, final int[] array) {
        int n3 = n;
        final char[] array2 = segment.array;
        final int offset = segment.offset;
        final int n4 = segment.offset + segment.count;
        int n5 = 0;
        int n6 = 0;
        int n7 = -1;
        int n8 = 0;
        int n9 = 0;
        if (array != null) {
            int n10 = -n2 + offset;
            final View parent;
            if (view != null && (parent = view.getParent()) != null) {
                n10 += parent.getStartOffset();
            }
            n6 = array[0];
            n7 = array[1] + n10;
            n8 = array[2] + n10;
            n9 = array[3] + n10;
        }
        for (int i = offset; i < n4; ++i) {
            if (array2[i] == '\t' || ((n6 != 0 || i <= n7) && array2[i] == ' ' && n8 <= i && i <= n9)) {
                n3 += fontMetrics.charsWidth(array2, i - n5, n5);
                n5 = 0;
                if (array2[i] == '\t') {
                    if (tabExpander != null) {
                        n3 = (int)tabExpander.nextTabStop((float)n3, n2 + i - offset);
                    }
                    else {
                        n3 += fontMetrics.charWidth(' ');
                    }
                }
                else if (array2[i] == ' ') {
                    n3 += fontMetrics.charWidth(' ') + n6;
                    if (i <= n7) {
                        ++n3;
                    }
                }
            }
            else if (array2[i] == '\n') {
                n3 += fontMetrics.charsWidth(array2, i - n5, n5);
                n5 = 0;
            }
            else {
                ++n5;
            }
        }
        return n3 + fontMetrics.charsWidth(array2, n4 - n5, n5) - n;
    }
    
    public static final int getTabbedTextOffset(final Segment segment, final FontMetrics fontMetrics, final int n, final int n2, final TabExpander tabExpander, final int n3) {
        return getTabbedTextOffset(segment, fontMetrics, n, n2, tabExpander, n3, true);
    }
    
    static final int getTabbedTextOffset(final View view, final Segment segment, final FontMetrics fontMetrics, final int n, final int n2, final TabExpander tabExpander, final int n3, final int[] array) {
        return getTabbedTextOffset(view, segment, fontMetrics, n, n2, tabExpander, n3, true, array);
    }
    
    public static final int getTabbedTextOffset(final Segment segment, final FontMetrics fontMetrics, final int n, final int n2, final TabExpander tabExpander, final int n3, final boolean b) {
        return getTabbedTextOffset(null, segment, fontMetrics, n, n2, tabExpander, n3, b, null);
    }
    
    static final int getTabbedTextOffset(final View view, final Segment segment, final FontMetrics fontMetrics, final int n, final int n2, final TabExpander tabExpander, final int n3, final boolean b, final int[] array) {
        if (n >= n2) {
            return 0;
        }
        int n4 = n;
        final char[] array2 = segment.array;
        final int offset = segment.offset;
        final int count = segment.count;
        int n5 = 0;
        int n6 = -1;
        int n7 = 0;
        int n8 = 0;
        if (array != null) {
            int n9 = -n3 + offset;
            final View parent;
            if (view != null && (parent = view.getParent()) != null) {
                n9 += parent.getStartOffset();
            }
            n5 = array[0];
            n6 = array[1] + n9;
            n7 = array[2] + n9;
            n8 = array[3] + n9;
        }
        for (int n10 = segment.offset + segment.count, i = segment.offset; i < n10; ++i) {
            if (array2[i] == '\t' || ((n5 != 0 || i <= n6) && array2[i] == ' ' && n7 <= i && i <= n8)) {
                if (array2[i] == '\t') {
                    if (tabExpander != null) {
                        n4 = (int)tabExpander.nextTabStop((float)n4, n3 + i - offset);
                    }
                    else {
                        n4 += fontMetrics.charWidth(' ');
                    }
                }
                else if (array2[i] == ' ') {
                    n4 += fontMetrics.charWidth(' ') + n5;
                    if (i <= n6) {
                        ++n4;
                    }
                }
            }
            else {
                n4 += fontMetrics.charWidth(array2[i]);
            }
            if (n2 < n4) {
                int j;
                if (b) {
                    j = i + 1 - offset;
                    int charsWidth = fontMetrics.charsWidth(array2, offset, j);
                    final int n11 = n2 - n;
                    if (n11 < charsWidth) {
                        while (j > 0) {
                            final int n12 = (j > 1) ? fontMetrics.charsWidth(array2, offset, j - 1) : 0;
                            if (n11 >= n12) {
                                if (n11 - n12 < charsWidth - n11) {
                                    --j;
                                    break;
                                }
                                break;
                            }
                            else {
                                charsWidth = n12;
                                --j;
                            }
                        }
                    }
                }
                else {
                    for (j = i - offset; j > 0 && fontMetrics.charsWidth(array2, offset, j) > n2 - n; --j) {}
                }
                return j;
            }
        }
        return count;
    }
    
    public static final int getBreakLocation(final Segment text, final FontMetrics fontMetrics, final int n, final int n2, final TabExpander tabExpander, final int n3) {
        final char[] array = text.array;
        final int offset = text.offset;
        final int count = text.count;
        int tabbedTextOffset = getTabbedTextOffset(text, fontMetrics, n, n2, tabExpander, n3, false);
        if (tabbedTextOffset >= count - 1) {
            return count;
        }
        int i = offset + tabbedTextOffset;
        while (i >= offset) {
            final char c = array[i];
            if (c < '\u0100') {
                if (Character.isWhitespace(c)) {
                    tabbedTextOffset = i - offset + 1;
                    break;
                }
                --i;
            }
            else {
                final BreakIterator lineInstance = BreakIterator.getLineInstance();
                lineInstance.setText(text);
                final int preceding = lineInstance.preceding(i + 1);
                if (preceding > offset) {
                    tabbedTextOffset = preceding - offset;
                    break;
                }
                break;
            }
        }
        return tabbedTextOffset;
    }
    
    public static final int getRowStart(final JTextComponent textComponent, int n) throws BadLocationException {
        Rectangle modelToView = textComponent.modelToView(n);
        if (modelToView == null) {
            return -1;
        }
        for (int n2 = n, y = modelToView.y; modelToView != null && y == modelToView.y; modelToView = ((--n2 >= 0) ? textComponent.modelToView(n2) : null)) {
            if (modelToView.height != 0) {
                n = n2;
            }
        }
        return n;
    }
    
    public static final int getRowEnd(final JTextComponent textComponent, int n) throws BadLocationException {
        Rectangle modelToView = textComponent.modelToView(n);
        if (modelToView == null) {
            return -1;
        }
        for (int length = textComponent.getDocument().getLength(), n2 = n, y = modelToView.y; modelToView != null && y == modelToView.y; modelToView = ((++n2 <= length) ? textComponent.modelToView(n2) : null)) {
            if (modelToView.height != 0) {
                n = n2;
            }
        }
        return n;
    }
    
    public static final int getPositionAbove(final JTextComponent textComponent, int n, final int n2) throws BadLocationException {
        int n3 = getRowStart(textComponent, n) - 1;
        if (n3 < 0) {
            return -1;
        }
        int n4 = Integer.MAX_VALUE;
        int y = 0;
        Rectangle modelToView = null;
        if (n3 >= 0) {
            modelToView = textComponent.modelToView(n3);
            y = modelToView.y;
        }
        while (modelToView != null && y == modelToView.y) {
            final int abs = Math.abs(modelToView.x - n2);
            if (abs < n4) {
                n = n3;
                n4 = abs;
            }
            modelToView = ((--n3 >= 0) ? textComponent.modelToView(n3) : null);
        }
        return n;
    }
    
    public static final int getPositionBelow(final JTextComponent textComponent, int n, final int n2) throws BadLocationException {
        int n3 = getRowEnd(textComponent, n) + 1;
        if (n3 <= 0) {
            return -1;
        }
        int n4 = Integer.MAX_VALUE;
        final int length = textComponent.getDocument().getLength();
        int y = 0;
        Rectangle modelToView = null;
        if (n3 <= length) {
            modelToView = textComponent.modelToView(n3);
            y = modelToView.y;
        }
        while (modelToView != null && y == modelToView.y) {
            final int abs = Math.abs(n2 - modelToView.x);
            if (abs < n4) {
                n = n3;
                n4 = abs;
            }
            modelToView = ((++n3 <= length) ? textComponent.modelToView(n3) : null);
        }
        return n;
    }
    
    public static final int getWordStart(final JTextComponent textComponent, int n) throws BadLocationException {
        final Document document = textComponent.getDocument();
        final Element paragraphElement = getParagraphElement(textComponent, n);
        if (paragraphElement == null) {
            throw new BadLocationException("No word at " + n, n);
        }
        final int startOffset = paragraphElement.getStartOffset();
        final int min = Math.min(paragraphElement.getEndOffset(), document.getLength());
        final Segment sharedSegment = SegmentCache.getSharedSegment();
        document.getText(startOffset, min - startOffset, sharedSegment);
        if (sharedSegment.count > 0) {
            final BreakIterator wordInstance = BreakIterator.getWordInstance(textComponent.getLocale());
            wordInstance.setText(sharedSegment);
            int n2 = sharedSegment.offset + n - startOffset;
            if (n2 >= wordInstance.last()) {
                n2 = wordInstance.last() - 1;
            }
            wordInstance.following(n2);
            n = startOffset + wordInstance.previous() - sharedSegment.offset;
        }
        SegmentCache.releaseSharedSegment(sharedSegment);
        return n;
    }
    
    public static final int getWordEnd(final JTextComponent textComponent, int n) throws BadLocationException {
        final Document document = textComponent.getDocument();
        final Element paragraphElement = getParagraphElement(textComponent, n);
        if (paragraphElement == null) {
            throw new BadLocationException("No word at " + n, n);
        }
        final int startOffset = paragraphElement.getStartOffset();
        final int min = Math.min(paragraphElement.getEndOffset(), document.getLength());
        final Segment sharedSegment = SegmentCache.getSharedSegment();
        document.getText(startOffset, min - startOffset, sharedSegment);
        if (sharedSegment.count > 0) {
            final BreakIterator wordInstance = BreakIterator.getWordInstance(textComponent.getLocale());
            wordInstance.setText(sharedSegment);
            int n2 = n - startOffset + sharedSegment.offset;
            if (n2 >= wordInstance.last()) {
                n2 = wordInstance.last() - 1;
            }
            n = startOffset + wordInstance.following(n2) - sharedSegment.offset;
        }
        SegmentCache.releaseSharedSegment(sharedSegment);
        return n;
    }
    
    public static final int getNextWord(final JTextComponent textComponent, int endOffset) throws BadLocationException {
        Element element;
        int i;
        for (element = getParagraphElement(textComponent, endOffset), i = getNextWordInParagraph(textComponent, element, endOffset, false); i == -1; i = getNextWordInParagraph(textComponent, element, endOffset, true)) {
            endOffset = element.getEndOffset();
            element = getParagraphElement(textComponent, endOffset);
        }
        return i;
    }
    
    static int getNextWordInParagraph(final JTextComponent textComponent, final Element element, int n, final boolean b) throws BadLocationException {
        if (element == null) {
            throw new BadLocationException("No more words", n);
        }
        final Document document = element.getDocument();
        final int startOffset = element.getStartOffset();
        final int min = Math.min(element.getEndOffset(), document.getLength());
        if (n >= min || n < startOffset) {
            throw new BadLocationException("No more words", n);
        }
        final Segment sharedSegment = SegmentCache.getSharedSegment();
        document.getText(startOffset, min - startOffset, sharedSegment);
        final BreakIterator wordInstance = BreakIterator.getWordInstance(textComponent.getLocale());
        wordInstance.setText(sharedSegment);
        if (b && wordInstance.first() == sharedSegment.offset + n - startOffset && !Character.isWhitespace(sharedSegment.array[wordInstance.first()])) {
            return n;
        }
        final int following = wordInstance.following(sharedSegment.offset + n - startOffset);
        if (following == -1 || following >= sharedSegment.offset + sharedSegment.count) {
            return -1;
        }
        if (!Character.isWhitespace(sharedSegment.array[following])) {
            return startOffset + following - sharedSegment.offset;
        }
        final int next = wordInstance.next();
        if (next != -1) {
            n = startOffset + next - sharedSegment.offset;
            if (n != min) {
                return n;
            }
        }
        SegmentCache.releaseSharedSegment(sharedSegment);
        return -1;
    }
    
    public static final int getPreviousWord(final JTextComponent textComponent, int n) throws BadLocationException {
        Element element;
        int i;
        for (element = getParagraphElement(textComponent, n), i = getPrevWordInParagraph(textComponent, element, n); i == -1; i = getPrevWordInParagraph(textComponent, element, n)) {
            n = element.getStartOffset() - 1;
            element = getParagraphElement(textComponent, n);
        }
        return i;
    }
    
    static int getPrevWordInParagraph(final JTextComponent textComponent, final Element element, final int n) throws BadLocationException {
        if (element == null) {
            throw new BadLocationException("No more words", n);
        }
        final Document document = element.getDocument();
        final int startOffset = element.getStartOffset();
        final int endOffset = element.getEndOffset();
        if (n > endOffset || n < startOffset) {
            throw new BadLocationException("No more words", n);
        }
        final Segment sharedSegment = SegmentCache.getSharedSegment();
        document.getText(startOffset, endOffset - startOffset, sharedSegment);
        final BreakIterator wordInstance = BreakIterator.getWordInstance(textComponent.getLocale());
        wordInstance.setText(sharedSegment);
        if (wordInstance.following(sharedSegment.offset + n - startOffset) == -1) {
            wordInstance.last();
        }
        int n2 = wordInstance.previous();
        if (n2 == sharedSegment.offset + n - startOffset) {
            n2 = wordInstance.previous();
        }
        if (n2 == -1) {
            return -1;
        }
        if (!Character.isWhitespace(sharedSegment.array[n2])) {
            return startOffset + n2 - sharedSegment.offset;
        }
        final int previous = wordInstance.previous();
        if (previous != -1) {
            return startOffset + previous - sharedSegment.offset;
        }
        SegmentCache.releaseSharedSegment(sharedSegment);
        return -1;
    }
    
    public static final Element getParagraphElement(final JTextComponent textComponent, final int n) {
        final Document document = textComponent.getDocument();
        if (document instanceof StyledDocument) {
            return ((StyledDocument)document).getParagraphElement(n);
        }
        final Element defaultRootElement = document.getDefaultRootElement();
        final Element element = defaultRootElement.getElement(defaultRootElement.getElementIndex(n));
        if (n >= element.getStartOffset() && n < element.getEndOffset()) {
            return element;
        }
        return null;
    }
    
    static boolean isComposedTextElement(final Document document, final int n) {
        Element element;
        for (element = document.getDefaultRootElement(); !element.isLeaf(); element = element.getElement(element.getElementIndex(n))) {}
        return isComposedTextElement(element);
    }
    
    static boolean isComposedTextElement(final Element element) {
        return isComposedTextAttributeDefined(element.getAttributes());
    }
    
    static boolean isComposedTextAttributeDefined(final AttributeSet set) {
        return set != null && set.isDefined(StyleConstants.ComposedTextAttribute);
    }
    
    static int drawComposedText(final View view, final AttributeSet set, final Graphics graphics, final int n, final int n2, final int n3, final int n4) throws BadLocationException {
        final Graphics2D graphics2D = (Graphics2D)graphics;
        final AttributedString attributedString = (AttributedString)set.getAttribute(StyleConstants.ComposedTextAttribute);
        attributedString.addAttribute(TextAttribute.FONT, graphics.getFont());
        if (n3 >= n4) {
            return n;
        }
        return n + (int)SwingUtilities2.drawString(getJComponent(view), graphics2D, attributedString.getIterator(null, n3, n4), n, n2);
    }
    
    static void paintComposedText(final Graphics graphics, final Rectangle rectangle, final GlyphView glyphView) {
        if (graphics instanceof Graphics2D) {
            final Graphics2D graphics2D = (Graphics2D)graphics;
            final int startOffset = glyphView.getStartOffset();
            final int endOffset = glyphView.getEndOffset();
            final AttributedString attributedString = (AttributedString)glyphView.getElement().getAttributes().getAttribute(StyleConstants.ComposedTextAttribute);
            final int startOffset2 = glyphView.getElement().getStartOffset();
            final int n = rectangle.y + rectangle.height - (int)glyphView.getGlyphPainter().getDescent(glyphView);
            final int x = rectangle.x;
            attributedString.addAttribute(TextAttribute.FONT, glyphView.getFont());
            attributedString.addAttribute(TextAttribute.FOREGROUND, glyphView.getForeground());
            if (StyleConstants.isBold(glyphView.getAttributes())) {
                attributedString.addAttribute(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD);
            }
            if (StyleConstants.isItalic(glyphView.getAttributes())) {
                attributedString.addAttribute(TextAttribute.POSTURE, TextAttribute.POSTURE_OBLIQUE);
            }
            if (glyphView.isUnderline()) {
                attributedString.addAttribute(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
            }
            if (glyphView.isStrikeThrough()) {
                attributedString.addAttribute(TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON);
            }
            if (glyphView.isSuperscript()) {
                attributedString.addAttribute(TextAttribute.SUPERSCRIPT, TextAttribute.SUPERSCRIPT_SUPER);
            }
            if (glyphView.isSubscript()) {
                attributedString.addAttribute(TextAttribute.SUPERSCRIPT, TextAttribute.SUPERSCRIPT_SUB);
            }
            SwingUtilities2.drawString(getJComponent(glyphView), graphics2D, attributedString.getIterator(null, startOffset - startOffset2, endOffset - startOffset2), x, n);
        }
    }
    
    static boolean isLeftToRight(final Component component) {
        return component.getComponentOrientation().isLeftToRight();
    }
    
    static int getNextVisualPositionFrom(final View view, final int n, final Position.Bias bias, final Shape shape, final int n2, final Position.Bias[] array) throws BadLocationException {
        if (view.getViewCount() == 0) {
            return n;
        }
        final boolean b = n2 == 1 || n2 == 7;
        int n4;
        if (n == -1) {
            final int n3 = b ? (view.getViewCount() - 1) : 0;
            n4 = view.getView(n3).getNextVisualPositionFrom(n, bias, view.getChildAllocation(n3, shape), n2, array);
            if (n4 == -1 && !b && view.getViewCount() > 1) {
                n4 = view.getView(1).getNextVisualPositionFrom(-1, array[0], view.getChildAllocation(1, shape), n2, array);
            }
        }
        else {
            int n5 = b ? -1 : 1;
            int n6;
            if (bias == Position.Bias.Backward && n > 0) {
                n6 = view.getViewIndex(n - 1, Position.Bias.Forward);
            }
            else {
                n6 = view.getViewIndex(n, Position.Bias.Forward);
            }
            final View view2 = view.getView(n6);
            n4 = view2.getNextVisualPositionFrom(n, bias, view.getChildAllocation(n6, shape), n2, array);
            if ((n2 == 3 || n2 == 7) && view instanceof CompositeView && ((CompositeView)view).flipEastAndWestAtEnds(n, bias)) {
                n5 *= -1;
            }
            final int n7 = n6 + n5;
            if (n4 == -1 && n7 >= 0 && n7 < view.getViewCount()) {
                n4 = view.getView(n7).getNextVisualPositionFrom(-1, bias, view.getChildAllocation(n7, shape), n2, array);
                if (n4 == n && array[0] != bias) {
                    return getNextVisualPositionFrom(view, n, array[0], shape, n2, array);
                }
            }
            else if (n4 != -1 && array[0] != bias && ((n5 == 1 && view2.getEndOffset() == n4) || (n5 == -1 && view2.getStartOffset() == n4)) && n7 >= 0 && n7 < view.getViewCount()) {
                final View view3 = view.getView(n7);
                final Shape childAllocation = view.getChildAllocation(n7, shape);
                final Position.Bias bias2 = array[0];
                final int nextVisualPosition = view3.getNextVisualPositionFrom(-1, bias, childAllocation, n2, array);
                if (array[0] == bias) {
                    n4 = nextVisualPosition;
                }
                else {
                    array[0] = bias2;
                }
            }
        }
        return n4;
    }
}
