package com.sun.corba.se.impl.ior.iiop;

import org.omg.CORBA_2_3.portable.OutputStream;
import com.sun.corba.se.spi.orbutil.closure.Closure;

public final class IIOPAddressClosureImpl extends IIOPAddressBase
{
    private Closure host;
    private Closure port;
    
    public IIOPAddressClosureImpl(final Closure host, final Closure port) {
        this.host = host;
        this.port = port;
    }
    
    @Override
    public String getHost() {
        return (String)this.host.evaluate();
    }
    
    @Override
    public int getPort() {
        return (int)this.port.evaluate();
    }
}
