package sun.java2d.pipe;

import sun.awt.SunToolkit;
import java.util.HashSet;
import java.util.Set;

public abstract class RenderQueue
{
    private static final int BUFFER_SIZE = 32000;
    protected RenderBuffer buf;
    protected Set refSet;
    
    protected RenderQueue() {
        this.refSet = new HashSet();
        this.buf = RenderBuffer.allocate(32000);
    }
    
    public final void lock() {
        SunToolkit.awtLock();
    }
    
    public final boolean tryLock() {
        return SunToolkit.awtTryLock();
    }
    
    public final void unlock() {
        SunToolkit.awtUnlock();
    }
    
    public final void addReference(final Object o) {
        this.refSet.add(o);
    }
    
    public final RenderBuffer getBuffer() {
        return this.buf;
    }
    
    public final void ensureCapacity(final int n) {
        if (this.buf.remaining() < n) {
            this.flushNow();
        }
    }
    
    public final void ensureCapacityAndAlignment(final int n, final int n2) {
        this.ensureCapacity(n + 4);
        this.ensureAlignment(n2);
    }
    
    public final void ensureAlignment(final int n) {
        if ((this.buf.position() + n & 0x7) != 0x0) {
            this.buf.putInt(90);
        }
    }
    
    public abstract void flushNow();
    
    public abstract void flushAndInvokeNow(final Runnable p0);
    
    public void flushNow(final int n) {
        this.buf.position(n);
        this.flushNow();
    }
}
