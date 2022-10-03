package javax.lang.model.element;

public interface Name extends CharSequence
{
    boolean equals(final Object p0);
    
    int hashCode();
    
    boolean contentEquals(final CharSequence p0);
}
