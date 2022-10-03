package sun.font;

import java.text.AttributedCharacterIterator;
import java.util.Map;
import sun.text.CodePointIterator;
import java.util.ArrayList;
import java.awt.GraphicsEnvironment;
import java.awt.Font;

public final class FontResolver
{
    private Font[] allFonts;
    private Font[] supplementaryFonts;
    private int[] supplementaryIndices;
    private static final int DEFAULT_SIZE = 12;
    private Font defaultFont;
    private static final int SHIFT = 9;
    private static final int BLOCKSIZE = 128;
    private static final int MASK = 127;
    private int[][] blocks;
    private static FontResolver INSTANCE;
    
    private FontResolver() {
        this.defaultFont = new Font("Dialog", 0, 12);
        this.blocks = new int[512][];
    }
    
    private Font[] getAllFonts() {
        if (this.allFonts == null) {
            this.allFonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts();
            for (int i = 0; i < this.allFonts.length; ++i) {
                this.allFonts[i] = this.allFonts[i].deriveFont(12.0f);
            }
        }
        return this.allFonts;
    }
    
    private int getIndexFor(final char c) {
        if (this.defaultFont.canDisplay(c)) {
            return 1;
        }
        for (int i = 0; i < this.getAllFonts().length; ++i) {
            if (this.allFonts[i].canDisplay(c)) {
                return i + 2;
            }
        }
        return 1;
    }
    
    private Font[] getAllSCFonts() {
        if (this.supplementaryFonts == null) {
            final ArrayList list = new ArrayList();
            final ArrayList list2 = new ArrayList();
            for (int i = 0; i < this.getAllFonts().length; ++i) {
                final Font font = this.allFonts[i];
                if (FontUtilities.getFont2D(font).hasSupplementaryChars()) {
                    list.add(font);
                    list2.add(i);
                }
            }
            final int size = list.size();
            this.supplementaryIndices = new int[size];
            for (int j = 0; j < size; ++j) {
                this.supplementaryIndices[j] = (int)list2.get(j);
            }
            this.supplementaryFonts = list.toArray(new Font[size]);
        }
        return this.supplementaryFonts;
    }
    
    private int getIndexFor(final int n) {
        if (this.defaultFont.canDisplay(n)) {
            return 1;
        }
        for (int i = 0; i < this.getAllSCFonts().length; ++i) {
            if (this.supplementaryFonts[i].canDisplay(n)) {
                return this.supplementaryIndices[i] + 2;
            }
        }
        return 1;
    }
    
    public int getFontIndex(final char c) {
        final int n = c >> 9;
        int[] array = this.blocks[n];
        if (array == null) {
            array = new int[128];
            this.blocks[n] = array;
        }
        final int n2 = c & '\u007f';
        if (array[n2] == 0) {
            array[n2] = this.getIndexFor(c);
        }
        return array[n2];
    }
    
    public int getFontIndex(final int n) {
        if (n < 65536) {
            return this.getFontIndex((char)n);
        }
        return this.getIndexFor(n);
    }
    
    public int nextFontRunIndex(final CodePointIterator codePointIterator) {
        final int next = codePointIterator.next();
        int fontIndex = 1;
        if (next != -1) {
            fontIndex = this.getFontIndex(next);
            int next2;
            while ((next2 = codePointIterator.next()) != -1) {
                if (this.getFontIndex(next2) != fontIndex) {
                    codePointIterator.prev();
                    break;
                }
            }
        }
        return fontIndex;
    }
    
    public Font getFont(final int n, final Map map) {
        Font defaultFont = this.defaultFont;
        if (n >= 2) {
            defaultFont = this.allFonts[n - 2];
        }
        return defaultFont.deriveFont(map);
    }
    
    public static FontResolver getInstance() {
        if (FontResolver.INSTANCE == null) {
            FontResolver.INSTANCE = new FontResolver();
        }
        return FontResolver.INSTANCE;
    }
}
