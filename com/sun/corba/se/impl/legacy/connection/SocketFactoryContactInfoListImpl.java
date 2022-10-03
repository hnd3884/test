package com.sun.corba.se.impl.legacy.connection;

import com.sun.corba.se.spi.transport.CorbaContactInfoList;
import java.util.Iterator;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.impl.transport.CorbaContactInfoListImpl;

public class SocketFactoryContactInfoListImpl extends CorbaContactInfoListImpl
{
    public SocketFactoryContactInfoListImpl(final ORB orb) {
        super(orb);
    }
    
    public SocketFactoryContactInfoListImpl(final ORB orb, final IOR ior) {
        super(orb, ior);
    }
    
    @Override
    public Iterator iterator() {
        return new SocketFactoryContactInfoListIteratorImpl(this.orb, this);
    }
}
