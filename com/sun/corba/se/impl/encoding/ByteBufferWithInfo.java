package com.sun.corba.se.impl.encoding;

import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.pept.transport.ByteBufferPool;
import java.nio.ByteBuffer;
import com.sun.corba.se.spi.orb.ORB;

public class ByteBufferWithInfo
{
    private ORB orb;
    private boolean debug;
    private int index;
    public ByteBuffer byteBuffer;
    public int buflen;
    public int needed;
    public boolean fragmented;
    
    public ByteBufferWithInfo(final org.omg.CORBA.ORB orb, final ByteBuffer byteBuffer, final int n) {
        this.orb = (ORB)orb;
        this.debug = this.orb.transportDebugFlag;
        this.byteBuffer = byteBuffer;
        if (byteBuffer != null) {
            this.buflen = byteBuffer.limit();
        }
        this.position(n);
        this.needed = 0;
        this.fragmented = false;
    }
    
    public ByteBufferWithInfo(final org.omg.CORBA.ORB orb, final ByteBuffer byteBuffer) {
        this(orb, byteBuffer, 0);
    }
    
    public ByteBufferWithInfo(final org.omg.CORBA.ORB orb, final BufferManagerWrite bufferManagerWrite) {
        this(orb, bufferManagerWrite, true);
    }
    
    public ByteBufferWithInfo(final org.omg.CORBA.ORB orb, final BufferManagerWrite bufferManagerWrite, final boolean b) {
        this.orb = (ORB)orb;
        this.debug = this.orb.transportDebugFlag;
        final int bufferSize = bufferManagerWrite.getBufferSize();
        if (b) {
            this.byteBuffer = this.orb.getByteBufferPool().getByteBuffer(bufferSize);
            if (this.debug) {
                final int identityHashCode = System.identityHashCode(this.byteBuffer);
                final StringBuffer sb = new StringBuffer(80);
                sb.append("constructor (ORB, BufferManagerWrite) - got ").append("ByteBuffer id (").append(identityHashCode).append(") from ByteBufferPool.");
                this.dprint(sb.toString());
            }
        }
        else {
            this.byteBuffer = ByteBuffer.allocate(bufferSize);
        }
        this.position(0);
        this.buflen = bufferSize;
        this.byteBuffer.limit(this.buflen);
        this.needed = 0;
        this.fragmented = false;
    }
    
    public ByteBufferWithInfo(final ByteBufferWithInfo byteBufferWithInfo) {
        this.orb = byteBufferWithInfo.orb;
        this.debug = byteBufferWithInfo.debug;
        this.byteBuffer = byteBufferWithInfo.byteBuffer;
        this.buflen = byteBufferWithInfo.buflen;
        this.byteBuffer.limit(this.buflen);
        this.position(byteBufferWithInfo.position());
        this.needed = byteBufferWithInfo.needed;
        this.fragmented = byteBufferWithInfo.fragmented;
    }
    
    public int getSize() {
        return this.position();
    }
    
    public int getLength() {
        return this.buflen;
    }
    
    public int position() {
        return this.index;
    }
    
    public void position(final int index) {
        this.byteBuffer.position(index);
        this.index = index;
    }
    
    public void setLength(final int buflen) {
        this.buflen = buflen;
        this.byteBuffer.limit(this.buflen);
    }
    
    public void growBuffer(final ORB orb) {
        int buflen;
        for (buflen = this.byteBuffer.limit() * 2; this.position() + this.needed >= buflen; buflen *= 2) {}
        final ByteBufferPool byteBufferPool = orb.getByteBufferPool();
        final ByteBuffer byteBuffer = byteBufferPool.getByteBuffer(buflen);
        if (this.debug) {
            final int identityHashCode = System.identityHashCode(byteBuffer);
            final StringBuffer sb = new StringBuffer(80);
            sb.append("growBuffer() - got ByteBuffer id (");
            sb.append(identityHashCode).append(") from ByteBufferPool.");
            this.dprint(sb.toString());
        }
        this.byteBuffer.position(0);
        byteBuffer.put(this.byteBuffer);
        if (this.debug) {
            final int identityHashCode2 = System.identityHashCode(this.byteBuffer);
            final StringBuffer sb2 = new StringBuffer(80);
            sb2.append("growBuffer() - releasing ByteBuffer id (");
            sb2.append(identityHashCode2).append(") to ByteBufferPool.");
            this.dprint(sb2.toString());
        }
        byteBufferPool.releaseByteBuffer(this.byteBuffer);
        this.byteBuffer = byteBuffer;
        this.buflen = buflen;
        this.byteBuffer.limit(this.buflen);
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("ByteBufferWithInfo:");
        sb.append(" buflen = " + this.buflen);
        sb.append(" byteBuffer.limit = " + this.byteBuffer.limit());
        sb.append(" index = " + this.index);
        sb.append(" position = " + this.position());
        sb.append(" needed = " + this.needed);
        sb.append(" byteBuffer = " + ((this.byteBuffer == null) ? "null" : "not null"));
        sb.append(" fragmented = " + this.fragmented);
        return sb.toString();
    }
    
    protected void dprint(final String s) {
        ORBUtility.dprint("ByteBufferWithInfo", s);
    }
}
