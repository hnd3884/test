package org.omg.CORBA;

public abstract class ContextList
{
    public abstract int count();
    
    public abstract void add(final String p0);
    
    public abstract String item(final int p0) throws Bounds;
    
    public abstract void remove(final int p0) throws Bounds;
}
