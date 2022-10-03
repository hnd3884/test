package sun.font;

public final class TextRecord
{
    public char[] text;
    public int start;
    public int limit;
    public int min;
    public int max;
    
    public void init(final char[] text, final int start, final int limit, final int min, final int max) {
        this.text = text;
        this.start = start;
        this.limit = limit;
        this.min = min;
        this.max = max;
    }
}
