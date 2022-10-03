package sun.text.normalizer;

public interface Replaceable
{
    int length();
    
    char charAt(final int p0);
    
    void getChars(final int p0, final int p1, final char[] p2, final int p3);
}
