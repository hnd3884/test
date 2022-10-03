package sun.font;

import java.awt.Font;
import java.text.Bidi;
import java.awt.font.FontRenderContext;

public final class TextLabelFactory
{
    private final FontRenderContext frc;
    private final char[] text;
    private final Bidi bidi;
    private Bidi lineBidi;
    private final int flags;
    private int lineStart;
    private int lineLimit;
    
    public TextLabelFactory(final FontRenderContext frc, final char[] array, final Bidi bidi, final int flags) {
        this.frc = frc;
        this.text = array.clone();
        this.bidi = bidi;
        this.flags = flags;
        this.lineBidi = bidi;
        this.lineStart = 0;
        this.lineLimit = array.length;
    }
    
    public FontRenderContext getFontRenderContext() {
        return this.frc;
    }
    
    public Bidi getLineBidi() {
        return this.lineBidi;
    }
    
    public void setLineContext(final int lineStart, final int lineLimit) {
        this.lineStart = lineStart;
        this.lineLimit = lineLimit;
        if (this.bidi != null) {
            this.lineBidi = this.bidi.createLineBidi(lineStart, lineLimit);
        }
    }
    
    public ExtendedTextLabel createExtended(final Font font, final CoreMetrics coreMetrics, final Decoration decoration, final int n, final int n2) {
        if (n >= n2 || n < this.lineStart || n2 > this.lineLimit) {
            throw new IllegalArgumentException("bad start: " + n + " or limit: " + n2);
        }
        final int n3 = (this.lineBidi == null) ? 0 : this.lineBidi.getLevelAt(n - this.lineStart);
        final boolean b = this.lineBidi != null && !this.lineBidi.baseIsLeftToRight();
        int n4 = this.flags & 0xFFFFFFF6;
        if ((n3 & 0x1) != 0x0) {
            n4 |= 0x1;
        }
        if (b & true) {
            n4 |= 0x8;
        }
        return new ExtendedTextSourceLabel(new StandardTextSource(this.text, n, n2 - n, this.lineStart, this.lineLimit - this.lineStart, n3, n4, font, this.frc, coreMetrics), decoration);
    }
    
    public TextLabel createSimple(final Font font, final CoreMetrics coreMetrics, final int n, final int n2) {
        if (n >= n2 || n < this.lineStart || n2 > this.lineLimit) {
            throw new IllegalArgumentException("bad start: " + n + " or limit: " + n2);
        }
        final int n3 = (this.lineBidi == null) ? 0 : this.lineBidi.getLevelAt(n - this.lineStart);
        final boolean b = this.lineBidi != null && !this.lineBidi.baseIsLeftToRight();
        int n4 = this.flags & 0xFFFFFFF6;
        if ((n3 & 0x1) != 0x0) {
            n4 |= 0x1;
        }
        if (b & true) {
            n4 |= 0x8;
        }
        return new TextSourceLabel(new StandardTextSource(this.text, n, n2 - n, this.lineStart, this.lineLimit - this.lineStart, n3, n4, font, this.frc, coreMetrics));
    }
}
