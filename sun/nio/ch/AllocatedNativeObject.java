package sun.nio.ch;

class AllocatedNativeObject extends NativeObject
{
    AllocatedNativeObject(final int n, final boolean b) {
        super(n, b);
    }
    
    synchronized void free() {
        if (this.allocationAddress != 0L) {
            AllocatedNativeObject.unsafe.freeMemory(this.allocationAddress);
            this.allocationAddress = 0L;
        }
    }
}
