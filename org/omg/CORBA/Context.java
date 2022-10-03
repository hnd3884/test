package org.omg.CORBA;

public abstract class Context
{
    public abstract String context_name();
    
    public abstract Context parent();
    
    public abstract Context create_child(final String p0);
    
    public abstract void set_one_value(final String p0, final Any p1);
    
    public abstract void set_values(final NVList p0);
    
    public abstract void delete_values(final String p0);
    
    public abstract NVList get_values(final String p0, final int p1, final String p2);
}
