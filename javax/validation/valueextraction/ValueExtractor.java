package javax.validation.valueextraction;

public interface ValueExtractor<T>
{
    void extractValues(final T p0, final ValueReceiver p1);
    
    public interface ValueReceiver
    {
        void value(final String p0, final Object p1);
        
        void iterableValue(final String p0, final Object p1);
        
        void indexedValue(final String p0, final int p1, final Object p2);
        
        void keyedValue(final String p0, final Object p1, final Object p2);
    }
}
