package com.sun.corba.se.impl.encoding;

import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.spi.orb.ORB;

public abstract class BufferManagerWrite
{
    protected ORB orb;
    protected ORBUtilSystemException wrapper;
    protected Object outputObject;
    protected boolean sentFullMessage;
    
    BufferManagerWrite(final ORB orb) {
        this.sentFullMessage = false;
        this.orb = orb;
        this.wrapper = ORBUtilSystemException.get(orb, "rpc.encoding");
    }
    
    public abstract boolean sentFragment();
    
    public boolean sentFullMessage() {
        return this.sentFullMessage;
    }
    
    public abstract int getBufferSize();
    
    public abstract void overflow(final ByteBufferWithInfo p0);
    
    public abstract void sendMessage();
    
    public void setOutputObject(final Object outputObject) {
        this.outputObject = outputObject;
    }
    
    public abstract void close();
}
