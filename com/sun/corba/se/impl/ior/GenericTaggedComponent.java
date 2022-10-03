package com.sun.corba.se.impl.ior;

import org.omg.CORBA.ORB;
import org.omg.CORBA_2_3.portable.InputStream;
import com.sun.corba.se.spi.ior.TaggedComponent;

public class GenericTaggedComponent extends GenericIdentifiable implements TaggedComponent
{
    public GenericTaggedComponent(final int n, final InputStream inputStream) {
        super(n, inputStream);
    }
    
    public GenericTaggedComponent(final int n, final byte[] array) {
        super(n, array);
    }
    
    @Override
    public org.omg.IOP.TaggedComponent getIOPComponent(final ORB orb) {
        return new org.omg.IOP.TaggedComponent(this.getId(), this.getData());
    }
}
