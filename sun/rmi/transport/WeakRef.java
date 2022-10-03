package sun.rmi.transport;

import sun.rmi.runtime.Log;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;

class WeakRef extends WeakReference<Object>
{
    private int hashValue;
    private Object strongRef;
    
    public WeakRef(final Object hashValue) {
        super(hashValue);
        this.strongRef = null;
        this.setHashValue(hashValue);
    }
    
    public WeakRef(final Object hashValue, final ReferenceQueue<Object> referenceQueue) {
        super(hashValue, referenceQueue);
        this.strongRef = null;
        this.setHashValue(hashValue);
    }
    
    public synchronized void pin() {
        if (this.strongRef == null) {
            this.strongRef = this.get();
            if (DGCImpl.dgcLog.isLoggable(Log.VERBOSE)) {
                DGCImpl.dgcLog.log(Log.VERBOSE, "strongRef = " + this.strongRef);
            }
        }
    }
    
    public synchronized void unpin() {
        if (this.strongRef != null) {
            if (DGCImpl.dgcLog.isLoggable(Log.VERBOSE)) {
                DGCImpl.dgcLog.log(Log.VERBOSE, "strongRef = " + this.strongRef);
            }
            this.strongRef = null;
        }
    }
    
    private void setHashValue(final Object o) {
        if (o != null) {
            this.hashValue = System.identityHashCode(o);
        }
        else {
            this.hashValue = 0;
        }
    }
    
    @Override
    public int hashCode() {
        return this.hashValue;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof WeakRef)) {
            return false;
        }
        if (o == this) {
            return true;
        }
        final Object value = this.get();
        return value != null && value == ((WeakRef)o).get();
    }
}
