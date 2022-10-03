package sun.awt;

public class CharsetString
{
    public char[] charsetChars;
    public int offset;
    public int length;
    public FontDescriptor fontDescriptor;
    
    public CharsetString(final char[] charsetChars, final int offset, final int length, final FontDescriptor fontDescriptor) {
        this.charsetChars = charsetChars;
        this.offset = offset;
        this.length = length;
        this.fontDescriptor = fontDescriptor;
    }
}
