package sun.text.normalizer;

public interface RangeValueIterator
{
    boolean next(final Element p0);
    
    void reset();
    
    public static class Element
    {
        public int start;
        public int limit;
        public int value;
    }
}
