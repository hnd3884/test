package sun.nio.fs;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.io.IOException;
import java.nio.file.Path;

public class RegistryFileTypeDetector extends AbstractFileTypeDetector
{
    public String implProbeContentType(final Path path) throws IOException {
        if (!(path instanceof Path)) {
            return null;
        }
        final Path fileName = path.getFileName();
        if (fileName == null) {
            return null;
        }
        final String string = fileName.toString();
        final int lastIndex = string.lastIndexOf(46);
        if (lastIndex < 0 || lastIndex == string.length() - 1) {
            return null;
        }
        final String substring = string.substring(lastIndex);
        NativeBuffer nativeBuffer = null;
        NativeBuffer nativeBuffer2 = null;
        try {
            nativeBuffer = WindowsNativeDispatcher.asNativeBuffer(substring);
            nativeBuffer2 = WindowsNativeDispatcher.asNativeBuffer("Content Type");
            return queryStringValue(nativeBuffer.address(), nativeBuffer2.address());
        }
        catch (final WindowsException ex) {
            ex.rethrowAsIOException(path.toString());
            return null;
        }
        finally {
            nativeBuffer2.release();
            nativeBuffer.release();
        }
    }
    
    private static native String queryStringValue(final long p0, final long p1);
    
    static {
        AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Void>() {
            @Override
            public Void run() {
                System.loadLibrary("net");
                System.loadLibrary("nio");
                return null;
            }
        });
    }
}
