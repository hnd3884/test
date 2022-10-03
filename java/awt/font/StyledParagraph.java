package java.awt.font;

import java.awt.Font;
import java.util.HashMap;
import java.awt.Toolkit;
import java.awt.im.InputMethodHighlight;
import java.text.Annotation;
import sun.text.CodePointIterator;
import sun.font.FontResolver;
import java.util.Map;
import java.text.AttributedCharacterIterator;
import java.util.Vector;
import sun.font.Decoration;

final class StyledParagraph
{
    private int length;
    private Decoration decoration;
    private Object font;
    private Vector<Decoration> decorations;
    int[] decorationStarts;
    private Vector<Object> fonts;
    int[] fontStarts;
    private static int INITIAL_SIZE;
    
    public StyledParagraph(final AttributedCharacterIterator attributedCharacterIterator, final char[] array) {
        final int beginIndex = attributedCharacterIterator.getBeginIndex();
        final int endIndex = attributedCharacterIterator.getEndIndex();
        this.length = endIndex - beginIndex;
        int i = beginIndex;
        attributedCharacterIterator.first();
        do {
            final int runLimit = attributedCharacterIterator.getRunLimit();
            final int n = i - beginIndex;
            final Map<? extends AttributedCharacterIterator.Attribute, ?> addInputMethodAttrs = addInputMethodAttrs(attributedCharacterIterator.getAttributes());
            this.addDecoration(Decoration.getDecoration(addInputMethodAttrs), n);
            final Object graphicOrFont = getGraphicOrFont(addInputMethodAttrs);
            if (graphicOrFont == null) {
                this.addFonts(array, addInputMethodAttrs, n, runLimit - beginIndex);
            }
            else {
                this.addFont(graphicOrFont, n);
            }
            attributedCharacterIterator.setIndex(runLimit);
            i = runLimit;
        } while (i < endIndex);
        if (this.decorations != null) {
            this.decorationStarts = addToVector(this, this.length, this.decorations, this.decorationStarts);
        }
        if (this.fonts != null) {
            this.fontStarts = addToVector(this, this.length, this.fonts, this.fontStarts);
        }
    }
    
    private static void insertInto(final int n, final int[] array, int n2) {
        while (array[--n2] > n) {
            final int n3 = n2;
            ++array[n3];
        }
    }
    
    public static StyledParagraph insertChar(final AttributedCharacterIterator attributedCharacterIterator, final char[] array, final int index, final StyledParagraph styledParagraph) {
        final char setIndex = attributedCharacterIterator.setIndex(index);
        final int max = Math.max(index - attributedCharacterIterator.getBeginIndex() - 1, 0);
        final Map<? extends AttributedCharacterIterator.Attribute, ?> addInputMethodAttrs = addInputMethodAttrs(attributedCharacterIterator.getAttributes());
        if (!styledParagraph.getDecorationAt(max).equals(Decoration.getDecoration(addInputMethodAttrs))) {
            return new StyledParagraph(attributedCharacterIterator, array);
        }
        Object o = getGraphicOrFont(addInputMethodAttrs);
        if (o == null) {
            final FontResolver instance = FontResolver.getInstance();
            o = instance.getFont(instance.getFontIndex(setIndex), addInputMethodAttrs);
        }
        if (!styledParagraph.getFontOrGraphicAt(max).equals(o)) {
            return new StyledParagraph(attributedCharacterIterator, array);
        }
        ++styledParagraph.length;
        if (styledParagraph.decorations != null) {
            insertInto(max, styledParagraph.decorationStarts, styledParagraph.decorations.size());
        }
        if (styledParagraph.fonts != null) {
            insertInto(max, styledParagraph.fontStarts, styledParagraph.fonts.size());
        }
        return styledParagraph;
    }
    
    private static void deleteFrom(final int n, final int[] array, int n2) {
        while (array[--n2] > n) {
            final int n3 = n2;
            --array[n3];
        }
    }
    
    public static StyledParagraph deleteChar(final AttributedCharacterIterator attributedCharacterIterator, final char[] array, int n, final StyledParagraph styledParagraph) {
        n -= attributedCharacterIterator.getBeginIndex();
        if (styledParagraph.decorations == null && styledParagraph.fonts == null) {
            --styledParagraph.length;
            return styledParagraph;
        }
        if (styledParagraph.getRunLimit(n) == n + 1 && (n == 0 || styledParagraph.getRunLimit(n - 1) == n)) {
            return new StyledParagraph(attributedCharacterIterator, array);
        }
        --styledParagraph.length;
        if (styledParagraph.decorations != null) {
            deleteFrom(n, styledParagraph.decorationStarts, styledParagraph.decorations.size());
        }
        if (styledParagraph.fonts != null) {
            deleteFrom(n, styledParagraph.fontStarts, styledParagraph.fonts.size());
        }
        return styledParagraph;
    }
    
    public int getRunLimit(final int n) {
        if (n < 0 || n >= this.length) {
            throw new IllegalArgumentException("index out of range");
        }
        int length = this.length;
        if (this.decorations != null) {
            length = this.decorationStarts[findRunContaining(n, this.decorationStarts) + 1];
        }
        int length2 = this.length;
        if (this.fonts != null) {
            length2 = this.fontStarts[findRunContaining(n, this.fontStarts) + 1];
        }
        return Math.min(length, length2);
    }
    
    public Decoration getDecorationAt(final int n) {
        if (n < 0 || n >= this.length) {
            throw new IllegalArgumentException("index out of range");
        }
        if (this.decorations == null) {
            return this.decoration;
        }
        return this.decorations.elementAt(findRunContaining(n, this.decorationStarts));
    }
    
    public Object getFontOrGraphicAt(final int n) {
        if (n < 0 || n >= this.length) {
            throw new IllegalArgumentException("index out of range");
        }
        if (this.fonts == null) {
            return this.font;
        }
        return this.fonts.elementAt(findRunContaining(n, this.fontStarts));
    }
    
    private static int findRunContaining(final int n, final int[] array) {
        int n2;
        for (n2 = 1; array[n2] <= n; ++n2) {}
        return n2 - 1;
    }
    
    private static int[] addToVector(final Object o, final int n, final Vector vector, int[] array) {
        if (!vector.lastElement().equals(o)) {
            vector.addElement(o);
            final int size = vector.size();
            if (array.length == size) {
                final int[] array2 = new int[array.length * 2];
                System.arraycopy(array, 0, array2, 0, array.length);
                array = array2;
            }
            array[size - 1] = n;
        }
        return array;
    }
    
    private void addDecoration(final Decoration decoration, final int n) {
        if (this.decorations != null) {
            this.decorationStarts = addToVector(decoration, n, this.decorations, this.decorationStarts);
        }
        else if (this.decoration == null) {
            this.decoration = decoration;
        }
        else if (!this.decoration.equals(decoration)) {
            (this.decorations = new Vector<Decoration>(StyledParagraph.INITIAL_SIZE)).addElement(this.decoration);
            this.decorations.addElement(decoration);
            (this.decorationStarts = new int[StyledParagraph.INITIAL_SIZE])[0] = 0;
            this.decorationStarts[1] = n;
        }
    }
    
    private void addFont(final Object font, final int n) {
        if (this.fonts != null) {
            this.fontStarts = addToVector(font, n, this.fonts, this.fontStarts);
        }
        else if (this.font == null) {
            this.font = font;
        }
        else if (!this.font.equals(font)) {
            (this.fonts = new Vector<Object>(StyledParagraph.INITIAL_SIZE)).addElement(this.font);
            this.fonts.addElement(font);
            (this.fontStarts = new int[StyledParagraph.INITIAL_SIZE])[0] = 0;
            this.fontStarts[1] = n;
        }
    }
    
    private void addFonts(final char[] array, final Map<? extends AttributedCharacterIterator.Attribute, ?> map, final int n, final int n2) {
        final FontResolver instance = FontResolver.getInstance();
        final CodePointIterator create = CodePointIterator.create(array, n, n2);
        for (int i = create.charIndex(); i < n2; i = create.charIndex()) {
            this.addFont(instance.getFont(instance.nextFontRunIndex(create), map), i);
        }
    }
    
    static Map<? extends AttributedCharacterIterator.Attribute, ?> addInputMethodAttrs(final Map<? extends AttributedCharacterIterator.Attribute, ?> map) {
        Object o = map.get(TextAttribute.INPUT_METHOD_HIGHLIGHT);
        try {
            if (o != null) {
                if (o instanceof Annotation) {
                    o = ((Annotation)o).getValue();
                }
                final InputMethodHighlight inputMethodHighlight = (InputMethodHighlight)o;
                Map<TextAttribute, ?> map2 = null;
                try {
                    map2 = inputMethodHighlight.getStyle();
                }
                catch (final NoSuchMethodError noSuchMethodError) {}
                if (map2 == null) {
                    map2 = Toolkit.getDefaultToolkit().mapInputMethodHighlight(inputMethodHighlight);
                }
                if (map2 != null) {
                    final HashMap hashMap = new HashMap(5, 0.9f);
                    hashMap.putAll(map);
                    hashMap.putAll(map2);
                    return hashMap;
                }
            }
        }
        catch (final ClassCastException ex) {}
        return map;
    }
    
    private static Object getGraphicOrFont(final Map<? extends AttributedCharacterIterator.Attribute, ?> map) {
        final Object value = map.get(TextAttribute.CHAR_REPLACEMENT);
        if (value != null) {
            return value;
        }
        final Object value2 = map.get(TextAttribute.FONT);
        if (value2 != null) {
            return value2;
        }
        if (map.get(TextAttribute.FAMILY) != null) {
            return Font.getFont(map);
        }
        return null;
    }
    
    static {
        StyledParagraph.INITIAL_SIZE = 8;
    }
}
