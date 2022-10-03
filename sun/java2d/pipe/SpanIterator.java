package sun.java2d.pipe;

public interface SpanIterator
{
    void getPathBox(final int[] p0);
    
    void intersectClipBox(final int p0, final int p1, final int p2, final int p3);
    
    boolean nextSpan(final int[] p0);
    
    void skipDownTo(final int p0);
    
    long getNativeIterator();
}
