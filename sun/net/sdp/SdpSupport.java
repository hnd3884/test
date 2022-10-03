package sun.net.sdp;

import sun.misc.SharedSecrets;
import java.security.PrivilegedAction;
import java.security.AccessController;
import sun.security.action.GetPropertyAction;
import java.io.IOException;
import java.io.FileDescriptor;
import sun.misc.JavaIOFileDescriptorAccess;

public final class SdpSupport
{
    private static final String os;
    private static final boolean isSupported;
    private static final JavaIOFileDescriptorAccess fdAccess;
    
    private SdpSupport() {
    }
    
    public static FileDescriptor createSocket() throws IOException {
        if (!SdpSupport.isSupported) {
            throw new UnsupportedOperationException("SDP not supported on this platform");
        }
        final int create0 = create0();
        final FileDescriptor fileDescriptor = new FileDescriptor();
        SdpSupport.fdAccess.set(fileDescriptor, create0);
        return fileDescriptor;
    }
    
    public static void convertSocket(final FileDescriptor fileDescriptor) throws IOException {
        if (!SdpSupport.isSupported) {
            throw new UnsupportedOperationException("SDP not supported on this platform");
        }
        convert0(SdpSupport.fdAccess.get(fileDescriptor));
    }
    
    private static native int create0() throws IOException;
    
    private static native void convert0(final int p0) throws IOException;
    
    static {
        os = AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("os.name"));
        isSupported = (SdpSupport.os.equals("SunOS") || SdpSupport.os.equals("Linux"));
        fdAccess = SharedSecrets.getJavaIOFileDescriptorAccess();
        AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Void>() {
            @Override
            public Void run() {
                System.loadLibrary("net");
                return null;
            }
        });
    }
}
