package sun.java2d;

import java.lang.ref.Reference;

public class ReentrantContext
{
    byte usage;
    Reference<? extends ReentrantContext> reference;
    
    public ReentrantContext() {
        this.usage = 0;
        this.reference = null;
    }
}
