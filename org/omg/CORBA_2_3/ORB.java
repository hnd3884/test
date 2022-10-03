package org.omg.CORBA_2_3;

import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.Object;
import org.omg.CORBA.NO_IMPLEMENT;
import org.omg.CORBA.portable.ValueFactory;

public abstract class ORB extends org.omg.CORBA.ORB
{
    public ValueFactory register_value_factory(final String s, final ValueFactory valueFactory) {
        throw new NO_IMPLEMENT();
    }
    
    public void unregister_value_factory(final String s) {
        throw new NO_IMPLEMENT();
    }
    
    public ValueFactory lookup_value_factory(final String s) {
        throw new NO_IMPLEMENT();
    }
    
    public org.omg.CORBA.Object get_value_def(final String s) throws BAD_PARAM {
        throw new NO_IMPLEMENT();
    }
    
    public void set_delegate(final Object o) {
        throw new NO_IMPLEMENT();
    }
}
