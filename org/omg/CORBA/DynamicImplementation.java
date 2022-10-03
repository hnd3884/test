package org.omg.CORBA;

import org.omg.CORBA.portable.ObjectImpl;

@Deprecated
public class DynamicImplementation extends ObjectImpl
{
    @Deprecated
    public void invoke(final ServerRequest serverRequest) {
        throw new NO_IMPLEMENT();
    }
    
    @Override
    public String[] _ids() {
        throw new NO_IMPLEMENT();
    }
}
