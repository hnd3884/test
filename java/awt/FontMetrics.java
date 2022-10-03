package java.awt;

import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.text.CharacterIterator;
import java.awt.font.LineMetrics;
import java.awt.font.FontRenderContext;
import java.io.Serializable;

public abstract class FontMetrics implements Serializable
{
    private static final FontRenderContext DEFAULT_FRC;
    protected Font font;
    private static final long serialVersionUID = 1681126225205050147L;
    
    protected FontMetrics(final Font font) {
        this.font = font;
    }
    
    public Font getFont() {
        return this.font;
    }
    
    public FontRenderContext getFontRenderContext() {
        return FontMetrics.DEFAULT_FRC;
    }
    
    public int getLeading() {
        return 0;
    }
    
    public int getAscent() {
        return this.font.getSize();
    }
    
    public int getDescent() {
        return 0;
    }
    
    public int getHeight() {
        return this.getLeading() + this.getAscent() + this.getDescent();
    }
    
    public int getMaxAscent() {
        return this.getAscent();
    }
    
    public int getMaxDescent() {
        return this.getDescent();
    }
    
    @Deprecated
    public int getMaxDecent() {
        return this.getMaxDescent();
    }
    
    public int getMaxAdvance() {
        return -1;
    }
    
    public int charWidth(int n) {
        if (!Character.isValidCodePoint(n)) {
            n = 65535;
        }
        if (n < 256) {
            return this.getWidths()[n];
        }
        final char[] array = new char[2];
        return this.charsWidth(array, 0, Character.toChars(n, array, 0));
    }
    
    public int charWidth(final char c) {
        if (c < '\u0100') {
            return this.getWidths()[c];
        }
        return this.charsWidth(new char[] { c }, 0, 1);
    }
    
    public int stringWidth(final String s) {
        final int length = s.length();
        final char[] array = new char[length];
        s.getChars(0, length, array, 0);
        return this.charsWidth(array, 0, length);
    }
    
    public int charsWidth(final char[] array, final int n, final int n2) {
        return this.stringWidth(new String(array, n, n2));
    }
    
    public int bytesWidth(final byte[] array, final int n, final int n2) {
        return this.stringWidth(new String(array, 0, n, n2));
    }
    
    public int[] getWidths() {
        final int[] array = new int[256];
        for (char c = '\0'; c < '\u0100'; ++c) {
            array[c] = this.charWidth(c);
        }
        return array;
    }
    
    public boolean hasUniformLineMetrics() {
        return this.font.hasUniformLineMetrics();
    }
    
    public LineMetrics getLineMetrics(final String s, final Graphics graphics) {
        return this.font.getLineMetrics(s, this.myFRC(graphics));
    }
    
    public LineMetrics getLineMetrics(final String s, final int n, final int n2, final Graphics graphics) {
        return this.font.getLineMetrics(s, n, n2, this.myFRC(graphics));
    }
    
    public LineMetrics getLineMetrics(final char[] array, final int n, final int n2, final Graphics graphics) {
        return this.font.getLineMetrics(array, n, n2, this.myFRC(graphics));
    }
    
    public LineMetrics getLineMetrics(final CharacterIterator characterIterator, final int n, final int n2, final Graphics graphics) {
        return this.font.getLineMetrics(characterIterator, n, n2, this.myFRC(graphics));
    }
    
    public Rectangle2D getStringBounds(final String s, final Graphics graphics) {
        return this.font.getStringBounds(s, this.myFRC(graphics));
    }
    
    public Rectangle2D getStringBounds(final String s, final int n, final int n2, final Graphics graphics) {
        return this.font.getStringBounds(s, n, n2, this.myFRC(graphics));
    }
    
    public Rectangle2D getStringBounds(final char[] array, final int n, final int n2, final Graphics graphics) {
        return this.font.getStringBounds(array, n, n2, this.myFRC(graphics));
    }
    
    public Rectangle2D getStringBounds(final CharacterIterator characterIterator, final int n, final int n2, final Graphics graphics) {
        return this.font.getStringBounds(characterIterator, n, n2, this.myFRC(graphics));
    }
    
    public Rectangle2D getMaxCharBounds(final Graphics graphics) {
        return this.font.getMaxCharBounds(this.myFRC(graphics));
    }
    
    private FontRenderContext myFRC(final Graphics graphics) {
        if (graphics instanceof Graphics2D) {
            return ((Graphics2D)graphics).getFontRenderContext();
        }
        return FontMetrics.DEFAULT_FRC;
    }
    
    @Override
    public String toString() {
        return this.getClass().getName() + "[font=" + this.getFont() + "ascent=" + this.getAscent() + ", descent=" + this.getDescent() + ", height=" + this.getHeight() + "]";
    }
    
    private static native void initIDs();
    
    static {
        Toolkit.loadLibraries();
        if (!GraphicsEnvironment.isHeadless()) {
            initIDs();
        }
        DEFAULT_FRC = new FontRenderContext(null, false, false);
    }
}
