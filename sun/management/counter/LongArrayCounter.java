package sun.management.counter;

public interface LongArrayCounter extends Counter
{
    long[] longArrayValue();
    
    long longAt(final int p0);
}
