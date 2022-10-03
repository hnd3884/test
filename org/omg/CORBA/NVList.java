package org.omg.CORBA;

public abstract class NVList
{
    public abstract int count();
    
    public abstract NamedValue add(final int p0);
    
    public abstract NamedValue add_item(final String p0, final int p1);
    
    public abstract NamedValue add_value(final String p0, final Any p1, final int p2);
    
    public abstract NamedValue item(final int p0) throws Bounds;
    
    public abstract void remove(final int p0) throws Bounds;
}
