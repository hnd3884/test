package sun.management;

import java.io.IOException;
import java.io.File;

public abstract class FileSystem
{
    private static final Object lock;
    private static FileSystem fs;
    
    protected FileSystem() {
    }
    
    public static FileSystem open() {
        synchronized (FileSystem.lock) {
            if (FileSystem.fs == null) {
                FileSystem.fs = new FileSystemImpl();
            }
            return FileSystem.fs;
        }
    }
    
    public abstract boolean supportsFileSecurity(final File p0) throws IOException;
    
    public abstract boolean isAccessUserOnly(final File p0) throws IOException;
    
    static {
        lock = new Object();
    }
}
