package sun.font;

public final class FontRunIterator
{
    CompositeFont font;
    char[] text;
    int start;
    int limit;
    CompositeGlyphMapper mapper;
    int slot;
    int pos;
    static final int SURROGATE_START = 65536;
    static final int LEAD_START = 55296;
    static final int LEAD_LIMIT = 56320;
    static final int TAIL_START = 56320;
    static final int TAIL_LIMIT = 57344;
    static final int LEAD_SURROGATE_SHIFT = 10;
    static final int SURROGATE_OFFSET = -56613888;
    static final int DONE = -1;
    
    public FontRunIterator() {
        this.slot = -1;
    }
    
    public void init(final CompositeFont font, final char[] text, final int n, final int limit) {
        if (font == null || text == null || n < 0 || limit < n || limit > text.length) {
            throw new IllegalArgumentException();
        }
        this.font = font;
        this.text = text;
        this.start = n;
        this.limit = limit;
        this.mapper = (CompositeGlyphMapper)font.getMapper();
        this.slot = -1;
        this.pos = n;
    }
    
    public PhysicalFont getFont() {
        return (this.slot == -1) ? null : this.font.getSlotFont(this.slot);
    }
    
    public int getGlyphMask() {
        return this.slot << 24;
    }
    
    public int getPos() {
        return this.pos;
    }
    
    public boolean next(final int n, final int n2) {
        if (this.pos == n2) {
            return false;
        }
        final int n3 = this.mapper.charToGlyph(this.nextCodePoint(n2)) & 0xFF000000;
        this.slot = n3 >>> 24;
        int nextCodePoint;
        while ((nextCodePoint = this.nextCodePoint(n2)) != -1 && (this.mapper.charToGlyph(nextCodePoint) & 0xFF000000) == n3) {}
        this.pushback(nextCodePoint);
        return true;
    }
    
    public boolean next() {
        return this.next(0, this.limit);
    }
    
    final int nextCodePoint() {
        return this.nextCodePoint(this.limit);
    }
    
    final int nextCodePoint(final int n) {
        if (this.pos >= n) {
            return -1;
        }
        int n2 = this.text[this.pos++];
        if (n2 >= 55296 && n2 < 56320 && this.pos < n) {
            final char c = this.text[this.pos];
            if (c >= '\udc00' && c < '\ue000') {
                ++this.pos;
                n2 = (n2 << 10) + c - 56613888;
            }
        }
        return n2;
    }
    
    final void pushback(final int n) {
        if (n >= 0) {
            if (n >= 65536) {
                this.pos -= 2;
            }
            else {
                --this.pos;
            }
        }
    }
}
