package com.sun.corba.se.impl.encoding;

import com.sun.corba.se.pept.transport.Connection;
import com.sun.corba.se.pept.encoding.OutputObject;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.SystemException;
import com.sun.corba.se.impl.protocol.giopmsgheaders.MessageBase;
import com.sun.corba.se.spi.orb.ORB;

public class BufferManagerWriteStream extends BufferManagerWrite
{
    private int fragmentCount;
    
    BufferManagerWriteStream(final ORB orb) {
        super(orb);
        this.fragmentCount = 0;
    }
    
    @Override
    public boolean sentFragment() {
        return this.fragmentCount > 0;
    }
    
    @Override
    public int getBufferSize() {
        return this.orb.getORBData().getGIOPFragmentSize();
    }
    
    @Override
    public void overflow(final ByteBufferWithInfo byteBufferWithInfo) {
        MessageBase.setFlag(byteBufferWithInfo.byteBuffer, 2);
        try {
            this.sendFragment(false);
        }
        catch (final SystemException ex) {
            this.orb.getPIHandler().invokeClientPIEndingPoint(2, ex);
            throw ex;
        }
        byteBufferWithInfo.position(0);
        byteBufferWithInfo.buflen = byteBufferWithInfo.byteBuffer.limit();
        byteBufferWithInfo.fragmented = true;
        ((CDROutputObject)this.outputObject).getMessageHeader().createFragmentMessage().write((OutputStream)this.outputObject);
    }
    
    private void sendFragment(final boolean b) {
        final Connection connection = ((OutputObject)this.outputObject).getMessageMediator().getConnection();
        connection.writeLock();
        try {
            connection.sendWithoutLock((OutputObject)this.outputObject);
            ++this.fragmentCount;
        }
        finally {
            connection.writeUnlock();
        }
    }
    
    @Override
    public void sendMessage() {
        this.sendFragment(true);
        this.sentFullMessage = true;
    }
    
    @Override
    public void close() {
    }
}
