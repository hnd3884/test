package sun.text.normalizer;

public class ReplaceableString implements Replaceable
{
    private StringBuffer buf;
    
    public ReplaceableString(final String s) {
        this.buf = new StringBuffer(s);
    }
    
    public ReplaceableString(final StringBuffer buf) {
        this.buf = buf;
    }
    
    @Override
    public int length() {
        return this.buf.length();
    }
    
    @Override
    public char charAt(final int n) {
        return this.buf.charAt(n);
    }
    
    @Override
    public void getChars(final int n, final int n2, final char[] array, final int n3) {
        Utility.getChars(this.buf, n, n2, array, n3);
    }
}
