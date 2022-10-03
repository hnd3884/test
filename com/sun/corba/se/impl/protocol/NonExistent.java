package com.sun.corba.se.impl.protocol;

import org.omg.CORBA.portable.OutputStream;
import com.sun.corba.se.spi.servicecontext.ServiceContexts;
import com.sun.corba.se.spi.oa.NullServant;
import com.sun.corba.se.spi.oa.ObjectAdapter;
import com.sun.corba.se.spi.protocol.CorbaMessageMediator;

class NonExistent extends SpecialMethod
{
    @Override
    public boolean isNonExistentMethod() {
        return true;
    }
    
    @Override
    public String getName() {
        return "_non_existent";
    }
    
    @Override
    public CorbaMessageMediator invoke(final Object o, final CorbaMessageMediator corbaMessageMediator, final byte[] array, final ObjectAdapter objectAdapter) {
        final boolean b = o == null || o instanceof NullServant;
        final CorbaMessageMediator response = corbaMessageMediator.getProtocolHandler().createResponse(corbaMessageMediator, null);
        ((OutputStream)response.getOutputObject()).write_boolean(b);
        return response;
    }
}
