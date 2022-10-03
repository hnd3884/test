package com.sun.corba.se.impl.protocol;

import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.spi.servicecontext.ServiceContexts;
import com.sun.corba.se.spi.protocol.CorbaMessageMediator;
import com.sun.corba.se.pept.protocol.MessageMediator;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.ior.ObjectKey;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.protocol.CorbaServerRequestDispatcher;

public class INSServerRequestDispatcher implements CorbaServerRequestDispatcher
{
    private ORB orb;
    private ORBUtilSystemException wrapper;
    
    public INSServerRequestDispatcher(final ORB orb) {
        this.orb = null;
        this.orb = orb;
        this.wrapper = ORBUtilSystemException.get(orb, "rpc.protocol");
    }
    
    @Override
    public IOR locate(final ObjectKey objectKey) {
        return this.getINSReference(new String(objectKey.getBytes(this.orb)));
    }
    
    @Override
    public void dispatch(final MessageMediator messageMediator) {
        final CorbaMessageMediator corbaMessageMediator = (CorbaMessageMediator)messageMediator;
        corbaMessageMediator.getProtocolHandler().createLocationForward(corbaMessageMediator, this.getINSReference(new String(corbaMessageMediator.getObjectKey().getBytes(this.orb))), null);
    }
    
    private IOR getINSReference(final String s) {
        final IOR ior = ORBUtility.getIOR(this.orb.getLocalResolver().resolve(s));
        if (ior != null) {
            return ior;
        }
        throw this.wrapper.servantNotFound();
    }
}
