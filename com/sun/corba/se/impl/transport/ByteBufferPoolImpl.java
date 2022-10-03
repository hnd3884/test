package com.sun.corba.se.impl.transport;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.pept.transport.ByteBufferPool;

public class ByteBufferPoolImpl implements ByteBufferPool
{
    private ORB itsOrb;
    private int itsByteBufferSize;
    private ArrayList itsPool;
    private int itsObjectCounter;
    private boolean debug;
    
    public ByteBufferPoolImpl(final ORB itsOrb) {
        this.itsObjectCounter = 0;
        this.itsByteBufferSize = itsOrb.getORBData().getGIOPFragmentSize();
        this.itsPool = new ArrayList();
        this.itsOrb = itsOrb;
        this.debug = itsOrb.transportDebugFlag;
    }
    
    @Override
    public ByteBuffer getByteBuffer(final int n) {
        Buffer buffer = null;
        if (n <= this.itsByteBufferSize && !this.itsOrb.getORBData().disableDirectByteBufferUse()) {
            final int size;
            synchronized (this.itsPool) {
                size = this.itsPool.size();
                if (size > 0) {
                    buffer = (ByteBuffer)this.itsPool.remove(size - 1);
                    buffer.clear();
                }
            }
            if (size <= 0) {
                buffer = ByteBuffer.allocateDirect(this.itsByteBufferSize);
            }
            ++this.itsObjectCounter;
        }
        else {
            buffer = ByteBuffer.allocate(n);
        }
        return (ByteBuffer)buffer;
    }
    
    @Override
    public void releaseByteBuffer(final ByteBuffer byteBuffer) {
        if (byteBuffer.isDirect()) {
            synchronized (this.itsPool) {
                int n = 0;
                int identityHashCode = 0;
                if (this.debug) {
                    for (int n2 = 0; n2 < this.itsPool.size() && n == 0; ++n2) {
                        if (byteBuffer == this.itsPool.get(n2)) {
                            n = 1;
                            identityHashCode = System.identityHashCode(byteBuffer);
                        }
                    }
                }
                if (n == 0 || !this.debug) {
                    this.itsPool.add(byteBuffer);
                }
                else {
                    new Throwable(Thread.currentThread().getName() + ": Duplicate ByteBuffer reference (" + identityHashCode + ")").printStackTrace(System.out);
                }
            }
            --this.itsObjectCounter;
        }
    }
    
    @Override
    public int activeCount() {
        return this.itsObjectCounter;
    }
}
