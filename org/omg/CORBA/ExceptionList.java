package org.omg.CORBA;

public abstract class ExceptionList
{
    public abstract int count();
    
    public abstract void add(final TypeCode p0);
    
    public abstract TypeCode item(final int p0) throws Bounds;
    
    public abstract void remove(final int p0) throws Bounds;
}
