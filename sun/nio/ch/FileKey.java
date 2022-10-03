package sun.nio.ch;

import java.io.IOException;
import java.io.FileDescriptor;

public class FileKey
{
    private long dwVolumeSerialNumber;
    private long nFileIndexHigh;
    private long nFileIndexLow;
    
    private FileKey() {
    }
    
    public static FileKey create(final FileDescriptor fileDescriptor) {
        final FileKey fileKey = new FileKey();
        try {
            fileKey.init(fileDescriptor);
        }
        catch (final IOException ex) {
            throw new Error(ex);
        }
        return fileKey;
    }
    
    @Override
    public int hashCode() {
        return (int)(this.dwVolumeSerialNumber ^ this.dwVolumeSerialNumber >>> 32) + (int)(this.nFileIndexHigh ^ this.nFileIndexHigh >>> 32) + (int)(this.nFileIndexLow ^ this.nFileIndexHigh >>> 32);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof FileKey)) {
            return false;
        }
        final FileKey fileKey = (FileKey)o;
        return this.dwVolumeSerialNumber == fileKey.dwVolumeSerialNumber && this.nFileIndexHigh == fileKey.nFileIndexHigh && this.nFileIndexLow == fileKey.nFileIndexLow;
    }
    
    private native void init(final FileDescriptor p0) throws IOException;
    
    private static native void initIDs();
    
    static {
        IOUtil.load();
        initIDs();
    }
}
