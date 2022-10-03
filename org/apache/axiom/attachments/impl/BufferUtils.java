package org.apache.axiom.attachments.impl;

import org.apache.commons.logging.LogFactory;
import org.apache.axiom.util.activation.DataSourceUtils;
import javax.activation.DataHandler;
import org.apache.axiom.attachments.utils.BAAOutputStream;
import java.nio.channels.FileLock;
import java.nio.channels.FileChannel;
import java.io.IOException;
import org.apache.axiom.ext.io.ReadFromSupport;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import org.apache.commons.logging.Log;

public class BufferUtils
{
    private static final Log log;
    public static final int BUFFER_LEN = 4096;
    static boolean ENABLE_FILE_CHANNEL;
    static boolean ENABLE_BAAOS_OPT;
    private static byte[] _cacheBuffer;
    private static boolean _cacheBufferInUse;
    private static ByteBuffer _cacheByteBuffer;
    private static boolean _cacheByteBufferInUse;
    
    public static void inputStream2OutputStream(final InputStream is, final OutputStream os) throws IOException {
        if (BufferUtils.ENABLE_FILE_CHANNEL && os instanceof FileOutputStream && inputStream2FileOutputStream(is, (FileOutputStream)os)) {
            return;
        }
        if (BufferUtils.ENABLE_BAAOS_OPT && os instanceof ReadFromSupport) {
            ((ReadFromSupport)os).readFrom(is, Long.MAX_VALUE);
            return;
        }
        final byte[] buffer = getTempBuffer();
        try {
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
        }
        finally {
            releaseTempBuffer(buffer);
        }
    }
    
    public static int inputStream2OutputStream(final InputStream is, final OutputStream os, final int limit) throws IOException {
        if (BufferUtils.ENABLE_BAAOS_OPT && os instanceof ReadFromSupport) {
            return (int)((ReadFromSupport)os).readFrom(is, limit);
        }
        final byte[] buffer = getTempBuffer();
        int totalWritten = 0;
        int bytesRead = 0;
        try {
            do {
                final int len = (limit - totalWritten > 4096) ? 4096 : (limit - totalWritten);
                bytesRead = is.read(buffer, 0, len);
                if (bytesRead > 0) {
                    os.write(buffer, 0, bytesRead);
                    if (bytesRead <= 0) {
                        continue;
                    }
                    totalWritten += bytesRead;
                }
            } while (totalWritten < limit && (bytesRead > 0 || is.available() > 0));
            return totalWritten;
        }
        finally {
            releaseTempBuffer(buffer);
        }
    }
    
    public static boolean inputStream2FileOutputStream(final InputStream is, final FileOutputStream fos) throws IOException {
        FileChannel channel = null;
        FileLock lock = null;
        ByteBuffer bb = null;
        try {
            channel = fos.getChannel();
            if (channel != null) {
                lock = channel.tryLock();
            }
            bb = getTempByteBuffer();
        }
        catch (final Throwable t) {}
        if (lock == null || bb == null || !bb.hasArray()) {
            releaseTempByteBuffer(bb);
            return false;
        }
        try {
            for (int bytesRead = is.read(bb.array()); bytesRead != -1; bytesRead = is.read(bb.array())) {
                int written = 0;
                if (bytesRead < 4096) {
                    final ByteBuffer temp = ByteBuffer.allocate(bytesRead);
                    temp.put(bb.array(), 0, bytesRead);
                    temp.position(0);
                    written = channel.write(temp);
                }
                else {
                    bb.position(0);
                    written = channel.write(bb);
                    bb.clear();
                }
            }
        }
        finally {
            lock.release();
            releaseTempByteBuffer(bb);
        }
        return true;
    }
    
    public static long inputStream2BAAOutputStream(final InputStream is, final BAAOutputStream baaos, final long limit) throws IOException {
        return baaos.receive(is, limit);
    }
    
    public static int doesDataHandlerExceedLimit(final DataHandler dh, final int limit) {
        if (limit == 0) {
            return -1;
        }
        final long size = DataSourceUtils.getSize(dh.getDataSource());
        if (size != -1L) {
            return (size > limit) ? 1 : 0;
        }
        try {
            dh.writeTo(new SizeLimitedOutputStream(limit));
        }
        catch (final SizeLimitExceededException ex) {
            return 1;
        }
        catch (final IOException ex2) {
            BufferUtils.log.warn((Object)ex2.getMessage());
            return -1;
        }
        return 0;
    }
    
    private static synchronized byte[] getTempBuffer() {
        synchronized (BufferUtils._cacheBuffer) {
            if (!BufferUtils._cacheBufferInUse) {
                BufferUtils._cacheBufferInUse = true;
                return BufferUtils._cacheBuffer;
            }
        }
        return new byte[4096];
    }
    
    private static void releaseTempBuffer(final byte[] buffer) {
        synchronized (BufferUtils._cacheBuffer) {
            if (buffer == BufferUtils._cacheBuffer) {
                BufferUtils._cacheBufferInUse = false;
            }
        }
    }
    
    private static synchronized ByteBuffer getTempByteBuffer() {
        synchronized (BufferUtils._cacheByteBuffer) {
            if (!BufferUtils._cacheByteBufferInUse) {
                BufferUtils._cacheByteBufferInUse = true;
                return BufferUtils._cacheByteBuffer;
            }
        }
        return ByteBuffer.allocate(4096);
    }
    
    private static void releaseTempByteBuffer(final ByteBuffer buffer) {
        synchronized (BufferUtils._cacheByteBuffer) {
            if (buffer == BufferUtils._cacheByteBuffer) {
                BufferUtils._cacheByteBufferInUse = false;
            }
        }
    }
    
    static {
        log = LogFactory.getLog((Class)BufferUtils.class);
        BufferUtils.ENABLE_FILE_CHANNEL = true;
        BufferUtils.ENABLE_BAAOS_OPT = true;
        BufferUtils._cacheBuffer = new byte[4096];
        BufferUtils._cacheBufferInUse = false;
        BufferUtils._cacheByteBuffer = ByteBuffer.allocate(4096);
        BufferUtils._cacheByteBufferInUse = false;
    }
    
    private static class SizeLimitExceededException extends IOException
    {
        private static final long serialVersionUID = -6644887187061182165L;
    }
    
    private static class SizeLimitedOutputStream extends OutputStream
    {
        private final int maxSize;
        private int size;
        
        public SizeLimitedOutputStream(final int maxSize) {
            this.maxSize = maxSize;
        }
        
        @Override
        public void write(final byte[] b, final int off, final int len) throws IOException {
            this.size += len;
            this.checkSize();
        }
        
        @Override
        public void write(final byte[] b) throws IOException {
            this.size += b.length;
            this.checkSize();
        }
        
        @Override
        public void write(final int b) throws IOException {
            ++this.size;
            this.checkSize();
        }
        
        private void checkSize() throws SizeLimitExceededException {
            if (this.size > this.maxSize) {
                throw new SizeLimitExceededException();
            }
        }
    }
}
