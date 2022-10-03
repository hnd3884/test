package com.sun.corba.se.impl.encoding;

import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.pept.transport.ByteBufferPool;
import com.sun.corba.se.pept.transport.Connection;
import java.util.Iterator;
import com.sun.corba.se.pept.encoding.OutputObject;
import org.omg.CORBA.portable.OutputStream;
import com.sun.corba.se.impl.protocol.giopmsgheaders.MessageBase;
import com.sun.corba.se.spi.orb.ORB;

public class BufferManagerWriteCollect extends BufferManagerWrite
{
    private BufferQueue queue;
    private boolean sentFragment;
    private boolean debug;
    
    BufferManagerWriteCollect(final ORB orb) {
        super(orb);
        this.queue = new BufferQueue();
        this.sentFragment = false;
        this.debug = false;
        if (orb != null) {
            this.debug = orb.transportDebugFlag;
        }
    }
    
    @Override
    public boolean sentFragment() {
        return this.sentFragment;
    }
    
    @Override
    public int getBufferSize() {
        return this.orb.getORBData().getGIOPFragmentSize();
    }
    
    @Override
    public void overflow(final ByteBufferWithInfo byteBufferWithInfo) {
        MessageBase.setFlag(byteBufferWithInfo.byteBuffer, 2);
        this.queue.enqueue(byteBufferWithInfo);
        final ByteBufferWithInfo byteBufferWithInfo2 = new ByteBufferWithInfo(this.orb, this);
        byteBufferWithInfo2.fragmented = true;
        ((CDROutputObject)this.outputObject).setByteBufferWithInfo(byteBufferWithInfo2);
        ((CDROutputObject)this.outputObject).getMessageHeader().createFragmentMessage().write((OutputStream)this.outputObject);
    }
    
    @Override
    public void sendMessage() {
        this.queue.enqueue(((CDROutputObject)this.outputObject).getByteBufferWithInfo());
        final Iterator iterator = this.iterator();
        final Connection connection = ((OutputObject)this.outputObject).getMessageMediator().getConnection();
        connection.writeLock();
        try {
            final ByteBufferPool byteBufferPool = this.orb.getByteBufferPool();
            while (iterator.hasNext()) {
                final ByteBufferWithInfo byteBufferWithInfo = iterator.next();
                ((CDROutputObject)this.outputObject).setByteBufferWithInfo(byteBufferWithInfo);
                connection.sendWithoutLock((OutputObject)this.outputObject);
                this.sentFragment = true;
                if (this.debug) {
                    final int identityHashCode = System.identityHashCode(byteBufferWithInfo.byteBuffer);
                    final StringBuffer sb = new StringBuffer(80);
                    sb.append("sendMessage() - releasing ByteBuffer id (");
                    sb.append(identityHashCode).append(") to ByteBufferPool.");
                    this.dprint(sb.toString());
                }
                byteBufferPool.releaseByteBuffer(byteBufferWithInfo.byteBuffer);
                byteBufferWithInfo.byteBuffer = null;
            }
            this.sentFullMessage = true;
        }
        finally {
            connection.writeUnlock();
        }
    }
    
    @Override
    public void close() {
        final Iterator iterator = this.iterator();
        final ByteBufferPool byteBufferPool = this.orb.getByteBufferPool();
        while (iterator.hasNext()) {
            final ByteBufferWithInfo byteBufferWithInfo = iterator.next();
            if (byteBufferWithInfo != null && byteBufferWithInfo.byteBuffer != null) {
                if (this.debug) {
                    final int identityHashCode = System.identityHashCode(byteBufferWithInfo.byteBuffer);
                    final StringBuffer sb = new StringBuffer(80);
                    sb.append("close() - releasing ByteBuffer id (");
                    sb.append(identityHashCode).append(") to ByteBufferPool.");
                    this.dprint(sb.toString());
                }
                byteBufferPool.releaseByteBuffer(byteBufferWithInfo.byteBuffer);
                byteBufferWithInfo.byteBuffer = null;
            }
        }
    }
    
    private void dprint(final String s) {
        ORBUtility.dprint("BufferManagerWriteCollect", s);
    }
    
    private Iterator iterator() {
        return new BufferManagerWriteCollectIterator();
    }
    
    private class BufferManagerWriteCollectIterator implements Iterator
    {
        @Override
        public boolean hasNext() {
            return BufferManagerWriteCollect.this.queue.size() != 0;
        }
        
        @Override
        public Object next() {
            return BufferManagerWriteCollect.this.queue.dequeue();
        }
        
        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
