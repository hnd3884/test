package sun.management;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.io.IOException;
import java.io.File;

public class FileSystemImpl extends FileSystem
{
    @Override
    public boolean supportsFileSecurity(final File file) throws IOException {
        return isSecuritySupported0(file.getAbsolutePath());
    }
    
    @Override
    public boolean isAccessUserOnly(final File file) throws IOException {
        final String absolutePath = file.getAbsolutePath();
        if (!isSecuritySupported0(absolutePath)) {
            throw new UnsupportedOperationException("File system does not support file security");
        }
        return isAccessUserOnly0(absolutePath);
    }
    
    static native void init0();
    
    static native boolean isSecuritySupported0(final String p0) throws IOException;
    
    static native boolean isAccessUserOnly0(final String p0) throws IOException;
    
    static {
        AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Void>() {
            @Override
            public Void run() {
                System.loadLibrary("management");
                return null;
            }
        });
        init0();
    }
}
