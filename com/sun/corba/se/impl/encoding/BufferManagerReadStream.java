package com.sun.corba.se.impl.encoding;

import com.sun.corba.se.impl.orbutil.ORBUtility;
import java.util.ListIterator;
import com.sun.corba.se.impl.protocol.giopmsgheaders.Message;
import com.sun.corba.se.pept.transport.ByteBufferPool;
import com.sun.corba.se.impl.protocol.RequestCanceledException;
import com.sun.corba.se.impl.protocol.giopmsgheaders.FragmentMessage;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.spi.orb.ORB;

public class BufferManagerReadStream implements BufferManagerRead, MarkAndResetHandler
{
    private boolean receivedCancel;
    private int cancelReqId;
    private boolean endOfStream;
    private BufferQueue fragmentQueue;
    private long FRAGMENT_TIMEOUT;
    private ORB orb;
    private ORBUtilSystemException wrapper;
    private boolean debug;
    private boolean markEngaged;
    private LinkedList fragmentStack;
    private RestorableInputStream inputStream;
    private Object streamMemento;
    
    BufferManagerReadStream(final ORB orb) {
        this.receivedCancel = false;
        this.cancelReqId = 0;
        this.endOfStream = true;
        this.fragmentQueue = new BufferQueue();
        this.FRAGMENT_TIMEOUT = 60000L;
        this.debug = false;
        this.markEngaged = false;
        this.fragmentStack = null;
        this.inputStream = null;
        this.streamMemento = null;
        this.orb = orb;
        this.wrapper = ORBUtilSystemException.get(orb, "rpc.encoding");
        this.debug = orb.transportDebugFlag;
    }
    
    @Override
    public void cancelProcessing(final int cancelReqId) {
        synchronized (this.fragmentQueue) {
            this.receivedCancel = true;
            this.cancelReqId = cancelReqId;
            this.fragmentQueue.notify();
        }
    }
    
    @Override
    public void processFragment(final ByteBuffer byteBuffer, final FragmentMessage fragmentMessage) {
        final ByteBufferWithInfo byteBufferWithInfo = new ByteBufferWithInfo(this.orb, byteBuffer, fragmentMessage.getHeaderLength());
        synchronized (this.fragmentQueue) {
            if (this.debug) {
                final int identityHashCode = System.identityHashCode(byteBuffer);
                final StringBuffer sb = new StringBuffer(80);
                sb.append("processFragment() - queueing ByteBuffer id (");
                sb.append(identityHashCode).append(") to fragment queue.");
                this.dprint(sb.toString());
            }
            this.fragmentQueue.enqueue(byteBufferWithInfo);
            this.endOfStream = !fragmentMessage.moreFragmentsToFollow();
            this.fragmentQueue.notify();
        }
    }
    
    @Override
    public ByteBufferWithInfo underflow(final ByteBufferWithInfo byteBufferWithInfo) {
        ByteBufferWithInfo dequeue = null;
        synchronized (this.fragmentQueue) {
            if (this.receivedCancel) {
                throw new RequestCanceledException(this.cancelReqId);
            }
            while (this.fragmentQueue.size() == 0) {
                if (this.endOfStream) {
                    throw this.wrapper.endOfStream();
                }
                boolean b = false;
                try {
                    this.fragmentQueue.wait(this.FRAGMENT_TIMEOUT);
                }
                catch (final InterruptedException ex) {
                    b = true;
                }
                if (!b && this.fragmentQueue.size() == 0) {
                    throw this.wrapper.bufferReadManagerTimeout();
                }
                if (this.receivedCancel) {
                    throw new RequestCanceledException(this.cancelReqId);
                }
            }
            dequeue = this.fragmentQueue.dequeue();
            dequeue.fragmented = true;
            if (this.debug) {
                final int identityHashCode = System.identityHashCode(dequeue.byteBuffer);
                final StringBuffer sb = new StringBuffer(80);
                sb.append("underflow() - dequeued ByteBuffer id (");
                sb.append(identityHashCode).append(") from fragment queue.");
                this.dprint(sb.toString());
            }
            if (!this.markEngaged && byteBufferWithInfo != null && byteBufferWithInfo.byteBuffer != null) {
                final ByteBufferPool byteBufferPool = this.getByteBufferPool();
                if (this.debug) {
                    final int identityHashCode2 = System.identityHashCode(byteBufferWithInfo.byteBuffer);
                    final StringBuffer sb2 = new StringBuffer(80);
                    sb2.append("underflow() - releasing ByteBuffer id (");
                    sb2.append(identityHashCode2).append(") to ByteBufferPool.");
                    this.dprint(sb2.toString());
                }
                byteBufferPool.releaseByteBuffer(byteBufferWithInfo.byteBuffer);
                byteBufferWithInfo.byteBuffer = null;
            }
        }
        return dequeue;
    }
    
    @Override
    public void init(final Message message) {
        if (message != null) {
            this.endOfStream = !message.moreFragmentsToFollow();
        }
    }
    
    @Override
    public void close(final ByteBufferWithInfo byteBufferWithInfo) {
        int n = 0;
        if (this.fragmentQueue != null) {
            synchronized (this.fragmentQueue) {
                if (byteBufferWithInfo != null) {
                    n = System.identityHashCode(byteBufferWithInfo.byteBuffer);
                }
                final ByteBufferPool byteBufferPool = this.getByteBufferPool();
                while (this.fragmentQueue.size() != 0) {
                    final ByteBufferWithInfo dequeue = this.fragmentQueue.dequeue();
                    if (dequeue != null && dequeue.byteBuffer != null) {
                        final int identityHashCode = System.identityHashCode(dequeue.byteBuffer);
                        if (n != identityHashCode && this.debug) {
                            final StringBuffer sb = new StringBuffer(80);
                            sb.append("close() - fragmentQueue is ").append("releasing ByteBuffer id (").append(identityHashCode).append(") to ").append("ByteBufferPool.");
                            this.dprint(sb.toString());
                        }
                        byteBufferPool.releaseByteBuffer(dequeue.byteBuffer);
                    }
                }
            }
            this.fragmentQueue = null;
        }
        if (this.fragmentStack != null && this.fragmentStack.size() != 0) {
            if (byteBufferWithInfo != null) {
                n = System.identityHashCode(byteBufferWithInfo.byteBuffer);
            }
            final ByteBufferPool byteBufferPool2 = this.getByteBufferPool();
            final ListIterator listIterator = this.fragmentStack.listIterator();
            while (listIterator.hasNext()) {
                final ByteBufferWithInfo byteBufferWithInfo2 = (ByteBufferWithInfo)listIterator.next();
                if (byteBufferWithInfo2 != null && byteBufferWithInfo2.byteBuffer != null) {
                    final int identityHashCode2 = System.identityHashCode(byteBufferWithInfo2.byteBuffer);
                    if (n == identityHashCode2) {
                        continue;
                    }
                    if (this.debug) {
                        final StringBuffer sb2 = new StringBuffer(80);
                        sb2.append("close() - fragmentStack - releasing ").append("ByteBuffer id (" + identityHashCode2 + ") to ").append("ByteBufferPool.");
                        this.dprint(sb2.toString());
                    }
                    byteBufferPool2.releaseByteBuffer(byteBufferWithInfo2.byteBuffer);
                }
            }
            this.fragmentStack = null;
        }
    }
    
    protected ByteBufferPool getByteBufferPool() {
        return this.orb.getByteBufferPool();
    }
    
    private void dprint(final String s) {
        ORBUtility.dprint("BufferManagerReadStream", s);
    }
    
    @Override
    public void mark(final RestorableInputStream inputStream) {
        this.inputStream = inputStream;
        this.markEngaged = true;
        this.streamMemento = inputStream.createStreamMemento();
        if (this.fragmentStack != null) {
            this.fragmentStack.clear();
        }
    }
    
    @Override
    public void fragmentationOccured(final ByteBufferWithInfo byteBufferWithInfo) {
        if (!this.markEngaged) {
            return;
        }
        if (this.fragmentStack == null) {
            this.fragmentStack = new LinkedList();
        }
        this.fragmentStack.addFirst(new ByteBufferWithInfo(byteBufferWithInfo));
    }
    
    @Override
    public void reset() {
        if (!this.markEngaged) {
            return;
        }
        this.markEngaged = false;
        if (this.fragmentStack != null && this.fragmentStack.size() != 0) {
            final ListIterator listIterator = this.fragmentStack.listIterator();
            synchronized (this.fragmentQueue) {
                while (listIterator.hasNext()) {
                    this.fragmentQueue.push((ByteBufferWithInfo)listIterator.next());
                }
            }
            this.fragmentStack.clear();
        }
        this.inputStream.restoreInternalState(this.streamMemento);
    }
    
    @Override
    public MarkAndResetHandler getMarkAndResetHandler() {
        return this;
    }
}
