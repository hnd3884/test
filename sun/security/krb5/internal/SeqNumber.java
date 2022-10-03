package sun.security.krb5.internal;

public interface SeqNumber
{
    void randInit();
    
    void init(final int p0);
    
    int current();
    
    int next();
    
    int step();
}
