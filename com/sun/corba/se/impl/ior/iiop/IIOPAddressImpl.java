package com.sun.corba.se.impl.ior.iiop;

import org.omg.CORBA_2_3.portable.OutputStream;
import org.omg.CORBA_2_3.portable.InputStream;
import com.sun.corba.se.impl.logging.IORSystemException;
import com.sun.corba.se.spi.orb.ORB;

public final class IIOPAddressImpl extends IIOPAddressBase
{
    private ORB orb;
    private IORSystemException wrapper;
    private String host;
    private int port;
    
    public IIOPAddressImpl(final ORB orb, final String host, final int port) {
        this.orb = orb;
        this.wrapper = IORSystemException.get(orb, "oa.ior");
        if (port < 0 || port > 65535) {
            throw this.wrapper.badIiopAddressPort(new Integer(port));
        }
        this.host = host;
        this.port = port;
    }
    
    public IIOPAddressImpl(final InputStream inputStream) {
        this.host = inputStream.read_string();
        this.port = this.shortToInt(inputStream.read_short());
    }
    
    @Override
    public String getHost() {
        return this.host;
    }
    
    @Override
    public int getPort() {
        return this.port;
    }
}
