package com.sun.corba.se.impl.ior;

import com.sun.corba.se.spi.ior.Identifiable;
import org.omg.CORBA_2_3.portable.InputStream;
import com.sun.corba.se.spi.orb.ORB;

public class TaggedProfileTemplateFactoryFinderImpl extends IdentifiableFactoryFinderBase
{
    public TaggedProfileTemplateFactoryFinderImpl(final ORB orb) {
        super(orb);
    }
    
    @Override
    public Identifiable handleMissingFactory(final int n, final InputStream inputStream) {
        throw this.wrapper.taggedProfileTemplateFactoryNotFound(new Integer(n));
    }
}
