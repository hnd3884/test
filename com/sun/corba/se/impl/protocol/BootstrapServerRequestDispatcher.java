package com.sun.corba.se.impl.protocol;

import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.ior.ObjectKey;
import java.util.Iterator;
import java.util.Set;
import org.omg.CORBA.SystemException;
import com.sun.corba.se.spi.servicecontext.ServiceContexts;
import com.sun.corba.se.impl.encoding.MarshalOutputStream;
import com.sun.corba.se.impl.encoding.MarshalInputStream;
import com.sun.corba.se.spi.protocol.CorbaMessageMediator;
import com.sun.corba.se.pept.protocol.MessageMediator;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.protocol.CorbaServerRequestDispatcher;

public class BootstrapServerRequestDispatcher implements CorbaServerRequestDispatcher
{
    private ORB orb;
    ORBUtilSystemException wrapper;
    private static final boolean debug = false;
    
    public BootstrapServerRequestDispatcher(final ORB orb) {
        this.orb = orb;
        this.wrapper = ORBUtilSystemException.get(orb, "rpc.protocol");
    }
    
    @Override
    public void dispatch(final MessageMediator messageMediator) {
        final CorbaMessageMediator corbaMessageMediator = (CorbaMessageMediator)messageMediator;
        try {
            final MarshalInputStream marshalInputStream = (MarshalInputStream)corbaMessageMediator.getInputObject();
            final String operationName = corbaMessageMediator.getOperationName();
            final MarshalOutputStream marshalOutputStream = (MarshalOutputStream)corbaMessageMediator.getProtocolHandler().createResponse(corbaMessageMediator, null).getOutputObject();
            if (operationName.equals("get")) {
                marshalOutputStream.write_Object(this.orb.getLocalResolver().resolve(marshalInputStream.read_string()));
            }
            else {
                if (!operationName.equals("list")) {
                    throw this.wrapper.illegalBootstrapOperation(operationName);
                }
                final Set list = this.orb.getLocalResolver().list();
                marshalOutputStream.write_long(list.size());
                final Iterator iterator = list.iterator();
                while (iterator.hasNext()) {
                    marshalOutputStream.write_string((String)iterator.next());
                }
            }
        }
        catch (final SystemException ex) {
            corbaMessageMediator.getProtocolHandler().createSystemExceptionResponse(corbaMessageMediator, ex, null);
        }
        catch (final RuntimeException ex2) {
            corbaMessageMediator.getProtocolHandler().createSystemExceptionResponse(corbaMessageMediator, this.wrapper.bootstrapRuntimeException(ex2), null);
        }
        catch (final Exception ex3) {
            corbaMessageMediator.getProtocolHandler().createSystemExceptionResponse(corbaMessageMediator, this.wrapper.bootstrapException(ex3), null);
        }
    }
    
    @Override
    public IOR locate(final ObjectKey objectKey) {
        return null;
    }
    
    public int getId() {
        throw this.wrapper.genericNoImpl();
    }
}
