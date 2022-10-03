package com.sun.corba.se.impl.encoding;

import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.pept.transport.Connection;
import com.sun.corba.se.pept.encoding.OutputObject;
import com.sun.corba.se.spi.orb.ORBData;
import com.sun.corba.se.spi.orb.ORB;

public class BufferManagerWriteGrow extends BufferManagerWrite
{
    BufferManagerWriteGrow(final ORB orb) {
        super(orb);
    }
    
    @Override
    public boolean sentFragment() {
        return false;
    }
    
    @Override
    public int getBufferSize() {
        int giopBufferSize = 1024;
        if (this.orb != null) {
            final ORBData orbData = this.orb.getORBData();
            if (orbData != null) {
                giopBufferSize = orbData.getGIOPBufferSize();
                this.dprint("BufferManagerWriteGrow.getBufferSize: bufferSize == " + giopBufferSize);
            }
            else {
                this.dprint("BufferManagerWriteGrow.getBufferSize: orbData reference is NULL");
            }
        }
        else {
            this.dprint("BufferManagerWriteGrow.getBufferSize: orb reference is NULL");
        }
        return giopBufferSize;
    }
    
    @Override
    public void overflow(final ByteBufferWithInfo byteBufferWithInfo) {
        byteBufferWithInfo.growBuffer(this.orb);
        byteBufferWithInfo.fragmented = false;
    }
    
    @Override
    public void sendMessage() {
        final Connection connection = ((OutputObject)this.outputObject).getMessageMediator().getConnection();
        connection.writeLock();
        try {
            connection.sendWithoutLock((OutputObject)this.outputObject);
            this.sentFullMessage = true;
        }
        finally {
            connection.writeUnlock();
        }
    }
    
    @Override
    public void close() {
    }
    
    private void dprint(final String s) {
        if (this.orb.transportDebugFlag) {
            ORBUtility.dprint(this, s);
        }
    }
}
