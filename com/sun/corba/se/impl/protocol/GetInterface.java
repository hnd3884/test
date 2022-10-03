package com.sun.corba.se.impl.protocol;

import com.sun.corba.se.spi.servicecontext.ServiceContexts;
import org.omg.CORBA.SystemException;
import com.sun.corba.se.spi.oa.NullServant;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.oa.ObjectAdapter;
import com.sun.corba.se.spi.protocol.CorbaMessageMediator;

class GetInterface extends SpecialMethod
{
    @Override
    public boolean isNonExistentMethod() {
        return false;
    }
    
    @Override
    public String getName() {
        return "_interface";
    }
    
    @Override
    public CorbaMessageMediator invoke(final Object o, final CorbaMessageMediator corbaMessageMediator, final byte[] array, final ObjectAdapter objectAdapter) {
        final ORBUtilSystemException value = ORBUtilSystemException.get((ORB)corbaMessageMediator.getBroker(), "oa.invocation");
        if (o == null || o instanceof NullServant) {
            return corbaMessageMediator.getProtocolHandler().createSystemExceptionResponse(corbaMessageMediator, value.badSkeleton(), null);
        }
        return corbaMessageMediator.getProtocolHandler().createSystemExceptionResponse(corbaMessageMediator, value.getinterfaceNotImplemented(), null);
    }
}
