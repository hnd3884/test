package org.omg.CORBA;

public abstract class Environment
{
    public abstract Exception exception();
    
    public abstract void exception(final Exception p0);
    
    public abstract void clear();
}
