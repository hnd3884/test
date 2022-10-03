package sun.java2d.cmm;

import java.awt.color.CMMException;

public class Profile
{
    private final long nativePtr;
    
    protected Profile(final long nativePtr) {
        this.nativePtr = nativePtr;
    }
    
    protected final long getNativePtr() {
        if (this.nativePtr == 0L) {
            throw new CMMException("Invalid profile: ptr is null");
        }
        return this.nativePtr;
    }
}
