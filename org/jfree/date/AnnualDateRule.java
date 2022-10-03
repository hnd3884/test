package org.jfree.date;

public abstract class AnnualDateRule implements Cloneable
{
    protected AnnualDateRule() {
    }
    
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
    
    public abstract SerialDate getDate(final int p0);
}
