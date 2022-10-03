package com.sun.corba.se.impl.ior;

import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.spi.ior.IORFactories;
import org.omg.CORBA.Object;
import com.sun.corba.se.spi.ior.IORTemplateList;
import com.sun.corba.se.spi.ior.IORFactory;
import com.sun.corba.se.spi.orb.ORB;

public abstract class ObjectReferenceProducerBase
{
    protected transient ORB orb;
    
    public abstract IORFactory getIORFactory();
    
    public abstract IORTemplateList getIORTemplateList();
    
    public ObjectReferenceProducerBase(final ORB orb) {
        this.orb = orb;
    }
    
    public org.omg.CORBA.Object make_object(final String s, final byte[] array) {
        return ORBUtility.makeObjectReference(this.getIORFactory().makeIOR(this.orb, s, IORFactories.makeObjectId(array)));
    }
}
