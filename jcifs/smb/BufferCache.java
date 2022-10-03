package jcifs.smb;

import jcifs.Config;

public class BufferCache
{
    private static final int MAX_BUFFERS;
    static Object[] cache;
    private static int numBuffers;
    private static int freeBuffers;
    
    private static byte[] getBuffer0() {
        if (BufferCache.freeBuffers > 0) {
            for (int i = 0; i < BufferCache.MAX_BUFFERS; ++i) {
                if (BufferCache.cache[i] != null) {
                    final byte[] buf = (byte[])BufferCache.cache[i];
                    BufferCache.cache[i] = null;
                    --BufferCache.freeBuffers;
                    return buf;
                }
            }
        }
        final byte[] buf = new byte[65535];
        ++BufferCache.numBuffers;
        return buf;
    }
    
    static void getBuffers(final SmbComTransaction req, final SmbComTransactionResponse rsp) {
        synchronized (BufferCache.cache) {
            try {
                while (BufferCache.freeBuffers + (BufferCache.MAX_BUFFERS - BufferCache.numBuffers) < 2) {
                    BufferCache.cache.wait();
                }
                req.txn_buf = getBuffer0();
                rsp.txn_buf = getBuffer0();
            }
            catch (final InterruptedException ie) {
                ie.printStackTrace();
            }
        }
    }
    
    public static byte[] getBuffer() {
        synchronized (BufferCache.cache) {
            while (BufferCache.freeBuffers + (BufferCache.MAX_BUFFERS - BufferCache.numBuffers) < 1) {
                try {
                    BufferCache.cache.wait();
                }
                catch (final InterruptedException ie) {}
            }
            return getBuffer0();
        }
    }
    
    public static void releaseBuffer(final byte[] buf) {
        synchronized (BufferCache.cache) {
            for (int i = 0; i < BufferCache.MAX_BUFFERS; ++i) {
                if (BufferCache.cache[i] == null) {
                    BufferCache.cache[i] = buf;
                    ++BufferCache.freeBuffers;
                    BufferCache.cache.notify();
                    return;
                }
            }
        }
    }
    
    static {
        MAX_BUFFERS = Config.getInt("jcifs.smb.maxBuffers", 16);
        BufferCache.cache = new Object[BufferCache.MAX_BUFFERS];
        BufferCache.numBuffers = 0;
        BufferCache.freeBuffers = 0;
    }
}
