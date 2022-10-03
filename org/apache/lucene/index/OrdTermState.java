package org.apache.lucene.index;

public class OrdTermState extends TermState
{
    public long ord;
    
    @Override
    public void copyFrom(final TermState other) {
        assert other instanceof OrdTermState : "can not copy from " + other.getClass().getName();
        this.ord = ((OrdTermState)other).ord;
    }
    
    @Override
    public String toString() {
        return "OrdTermState ord=" + this.ord;
    }
}
