package javax.print.attribute;

public interface AttributeSet
{
    Attribute get(final Class<?> p0);
    
    boolean add(final Attribute p0);
    
    boolean remove(final Class<?> p0);
    
    boolean remove(final Attribute p0);
    
    boolean containsKey(final Class<?> p0);
    
    boolean containsValue(final Attribute p0);
    
    boolean addAll(final AttributeSet p0);
    
    int size();
    
    Attribute[] toArray();
    
    void clear();
    
    boolean isEmpty();
    
    boolean equals(final Object p0);
    
    int hashCode();
}
