package sun.java2d;

public class DefaultDisposerRecord implements DisposerRecord
{
    private long dataPointer;
    private long disposerMethodPointer;
    
    public DefaultDisposerRecord(final long disposerMethodPointer, final long dataPointer) {
        this.disposerMethodPointer = disposerMethodPointer;
        this.dataPointer = dataPointer;
    }
    
    @Override
    public void dispose() {
        invokeNativeDispose(this.disposerMethodPointer, this.dataPointer);
    }
    
    public long getDataPointer() {
        return this.dataPointer;
    }
    
    public long getDisposerMethodPointer() {
        return this.disposerMethodPointer;
    }
    
    public static native void invokeNativeDispose(final long p0, final long p1);
}
