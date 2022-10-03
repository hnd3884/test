package org.apache.lucene.index;

public abstract class TermState implements Cloneable
{
    protected TermState() {
    }
    
    public abstract void copyFrom(final TermState p0);
    
    public TermState clone() {
        try {
            return (TermState)super.clone();
        }
        catch (final CloneNotSupportedException cnse) {
            throw new RuntimeException(cnse);
        }
    }
    
    public boolean isRealTerm() {
        return true;
    }
    
    @Override
    public String toString() {
        return "TermState";
    }
}
