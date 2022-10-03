package sun.font;

import java.awt.font.FontRenderContext;
import java.awt.Font;

final class StandardTextSource extends TextSource
{
    private final char[] chars;
    private final int start;
    private final int len;
    private final int cstart;
    private final int clen;
    private final int level;
    private final int flags;
    private final Font font;
    private final FontRenderContext frc;
    private final CoreMetrics cm;
    
    StandardTextSource(final char[] chars, final int start, final int len, final int cstart, final int clen, final int level, final int flags, final Font font, final FontRenderContext frc, final CoreMetrics cm) {
        if (chars == null) {
            throw new IllegalArgumentException("bad chars: null");
        }
        if (cstart < 0) {
            throw new IllegalArgumentException("bad cstart: " + cstart);
        }
        if (start < cstart) {
            throw new IllegalArgumentException("bad start: " + start + " for cstart: " + cstart);
        }
        if (clen < 0) {
            throw new IllegalArgumentException("bad clen: " + clen);
        }
        if (cstart + clen > chars.length) {
            throw new IllegalArgumentException("bad clen: " + clen + " cstart: " + cstart + " for array len: " + chars.length);
        }
        if (len < 0) {
            throw new IllegalArgumentException("bad len: " + len);
        }
        if (start + len > cstart + clen) {
            throw new IllegalArgumentException("bad len: " + len + " start: " + start + " for cstart: " + cstart + " clen: " + clen);
        }
        if (font == null) {
            throw new IllegalArgumentException("bad font: null");
        }
        if (frc == null) {
            throw new IllegalArgumentException("bad frc: null");
        }
        this.chars = chars;
        this.start = start;
        this.len = len;
        this.cstart = cstart;
        this.clen = clen;
        this.level = level;
        this.flags = flags;
        this.font = font;
        this.frc = frc;
        if (cm != null) {
            this.cm = cm;
        }
        else {
            this.cm = ((FontLineMetrics)font.getLineMetrics(chars, cstart, clen, frc)).cm;
        }
    }
    
    @Override
    public char[] getChars() {
        return this.chars;
    }
    
    @Override
    public int getStart() {
        return this.start;
    }
    
    @Override
    public int getLength() {
        return this.len;
    }
    
    @Override
    public int getContextStart() {
        return this.cstart;
    }
    
    @Override
    public int getContextLength() {
        return this.clen;
    }
    
    @Override
    public int getLayoutFlags() {
        return this.flags;
    }
    
    @Override
    public int getBidiLevel() {
        return this.level;
    }
    
    @Override
    public Font getFont() {
        return this.font;
    }
    
    @Override
    public FontRenderContext getFRC() {
        return this.frc;
    }
    
    @Override
    public CoreMetrics getCoreMetrics() {
        return this.cm;
    }
    
    @Override
    public TextSource getSubSource(final int n, final int n2, final int n3) {
        if (n < 0 || n2 < 0 || n + n2 > this.len) {
            throw new IllegalArgumentException("bad start (" + n + ") or length (" + n2 + ")");
        }
        int level = this.level;
        if (n3 != 2) {
            final boolean b = (this.flags & 0x8) == 0x0;
            if ((n3 != 0 || !b) && (n3 != 1 || b)) {
                throw new IllegalArgumentException("direction flag is invalid");
            }
            level = (b ? 0 : 1);
        }
        return new StandardTextSource(this.chars, this.start + n, n2, this.cstart, this.clen, level, this.flags, this.font, this.frc, this.cm);
    }
    
    @Override
    public String toString() {
        return this.toString(true);
    }
    
    @Override
    public String toString(final boolean b) {
        final StringBuffer sb = new StringBuffer(super.toString());
        sb.append("[start:");
        sb.append(this.start);
        sb.append(", len:");
        sb.append(this.len);
        sb.append(", cstart:");
        sb.append(this.cstart);
        sb.append(", clen:");
        sb.append(this.clen);
        sb.append(", chars:\"");
        int n;
        int n2;
        if (b) {
            n = this.cstart;
            n2 = this.cstart + this.clen;
        }
        else {
            n = this.start;
            n2 = this.start + this.len;
        }
        for (int i = n; i < n2; ++i) {
            if (i > n) {
                sb.append(" ");
            }
            sb.append(Integer.toHexString(this.chars[i]));
        }
        sb.append("\"");
        sb.append(", level:");
        sb.append(this.level);
        sb.append(", flags:");
        sb.append(this.flags);
        sb.append(", font:");
        sb.append(this.font);
        sb.append(", frc:");
        sb.append(this.frc);
        sb.append(", cm:");
        sb.append(this.cm);
        sb.append("]");
        return sb.toString();
    }
}
