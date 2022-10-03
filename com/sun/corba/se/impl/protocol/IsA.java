package com.sun.corba.se.impl.protocol;

import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.InputStream;
import com.sun.corba.se.spi.servicecontext.ServiceContexts;
import org.omg.CORBA.SystemException;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.oa.NullServant;
import com.sun.corba.se.spi.oa.ObjectAdapter;
import com.sun.corba.se.spi.protocol.CorbaMessageMediator;

class IsA extends SpecialMethod
{
    @Override
    public boolean isNonExistentMethod() {
        return false;
    }
    
    @Override
    public String getName() {
        return "_is_a";
    }
    
    @Override
    public CorbaMessageMediator invoke(final Object o, final CorbaMessageMediator corbaMessageMediator, final byte[] array, final ObjectAdapter objectAdapter) {
        if (o == null || o instanceof NullServant) {
            return corbaMessageMediator.getProtocolHandler().createSystemExceptionResponse(corbaMessageMediator, ORBUtilSystemException.get((ORB)corbaMessageMediator.getBroker(), "oa.invocation").badSkeleton(), null);
        }
        final String[] interfaces = objectAdapter.getInterfaces(o, array);
        final String read_string = ((InputStream)corbaMessageMediator.getInputObject()).read_string();
        boolean b = false;
        for (int i = 0; i < interfaces.length; ++i) {
            if (interfaces[i].equals(read_string)) {
                b = true;
                break;
            }
        }
        final CorbaMessageMediator response = corbaMessageMediator.getProtocolHandler().createResponse(corbaMessageMediator, null);
        ((OutputStream)response.getOutputObject()).write_boolean(b);
        return response;
    }
}
