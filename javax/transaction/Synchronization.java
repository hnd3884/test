package javax.transaction;

public interface Synchronization
{
    void beforeCompletion();
    
    void afterCompletion(final int p0);
}
