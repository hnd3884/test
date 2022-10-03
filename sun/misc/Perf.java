package sun.misc;

import java.security.PrivilegedAction;
import java.io.UnsupportedEncodingException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.Permission;

public final class Perf
{
    private static Perf instance;
    private static final int PERF_MODE_RO = 0;
    private static final int PERF_MODE_RW = 1;
    
    private Perf() {
    }
    
    public static Perf getPerf() {
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            securityManager.checkPermission(new RuntimePermission("sun.misc.Perf.getPerf"));
        }
        return Perf.instance;
    }
    
    public ByteBuffer attach(final int n, final String s) throws IllegalArgumentException, IOException {
        if (s.compareTo("r") == 0) {
            return this.attachImpl(null, n, 0);
        }
        if (s.compareTo("rw") == 0) {
            return this.attachImpl(null, n, 1);
        }
        throw new IllegalArgumentException("unknown mode");
    }
    
    public ByteBuffer attach(final String s, final int n, final String s2) throws IllegalArgumentException, IOException {
        if (s2.compareTo("r") == 0) {
            return this.attachImpl(s, n, 0);
        }
        if (s2.compareTo("rw") == 0) {
            return this.attachImpl(s, n, 1);
        }
        throw new IllegalArgumentException("unknown mode");
    }
    
    private ByteBuffer attachImpl(final String s, final int n, final int n2) throws IllegalArgumentException, IOException {
        final ByteBuffer attach = this.attach(s, n, n2);
        if (n == 0) {
            return attach;
        }
        final ByteBuffer duplicate = attach.duplicate();
        Cleaner.create(duplicate, new Runnable() {
            @Override
            public void run() {
                try {
                    Perf.instance.detach(attach);
                }
                catch (final Throwable t) {
                    assert false : t.toString();
                }
            }
        });
        return duplicate;
    }
    
    private native ByteBuffer attach(final String p0, final int p1, final int p2) throws IllegalArgumentException, IOException;
    
    private native void detach(final ByteBuffer p0);
    
    public native ByteBuffer createLong(final String p0, final int p1, final int p2, final long p3);
    
    public ByteBuffer createString(final String s, final int n, final int n2, final String s2, final int n3) {
        final byte[] bytes = getBytes(s2);
        final byte[] array = new byte[bytes.length + 1];
        System.arraycopy(bytes, 0, array, 0, bytes.length);
        array[bytes.length] = 0;
        return this.createByteArray(s, n, n2, array, Math.max(array.length, n3));
    }
    
    public ByteBuffer createString(final String s, final int n, final int n2, final String s2) {
        final byte[] bytes = getBytes(s2);
        final byte[] array = new byte[bytes.length + 1];
        System.arraycopy(bytes, 0, array, 0, bytes.length);
        array[bytes.length] = 0;
        return this.createByteArray(s, n, n2, array, array.length);
    }
    
    public native ByteBuffer createByteArray(final String p0, final int p1, final int p2, final byte[] p3, final int p4);
    
    private static byte[] getBytes(final String s) {
        byte[] bytes = null;
        try {
            bytes = s.getBytes("UTF-8");
        }
        catch (final UnsupportedEncodingException ex) {}
        return bytes;
    }
    
    public native long highResCounter();
    
    public native long highResFrequency();
    
    private static native void registerNatives();
    
    static {
        registerNatives();
        Perf.instance = new Perf();
    }
    
    public static class GetPerfAction implements PrivilegedAction<Perf>
    {
        @Override
        public Perf run() {
            return Perf.getPerf();
        }
    }
}
