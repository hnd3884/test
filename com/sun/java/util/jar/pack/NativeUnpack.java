package com.sun.java.util.jar.pack;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.zip.ZipEntry;
import java.io.FileInputStream;
import java.io.File;
import java.util.jar.JarOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.zip.CRC32;
import java.io.BufferedInputStream;

class NativeUnpack
{
    private long unpackerPtr;
    private BufferedInputStream in;
    private int _verbose;
    private long _byteCount;
    private int _segCount;
    private int _fileCount;
    private long _estByteLimit;
    private int _estSegLimit;
    private int _estFileLimit;
    private int _prevPercent;
    private final CRC32 _crc32;
    private byte[] _buf;
    private UnpackerImpl _p200;
    private PropMap _props;
    
    private static synchronized native void initIDs();
    
    private synchronized native long start(final ByteBuffer p0, final long p1);
    
    private synchronized native boolean getNextFile(final Object[] p0);
    
    private synchronized native ByteBuffer getUnusedInput();
    
    private synchronized native long finish();
    
    protected synchronized native boolean setOption(final String p0, final String p1);
    
    protected synchronized native String getOption(final String p0);
    
    NativeUnpack(final UnpackerImpl p) {
        this._prevPercent = -1;
        this._crc32 = new CRC32();
        this._buf = new byte[16384];
        this._p200 = p;
        this._props = p.props;
        p._nunp = this;
    }
    
    private static Object currentInstance() {
        final UnpackerImpl unpackerImpl = (UnpackerImpl)Utils.getTLGlobals();
        return (unpackerImpl == null) ? null : unpackerImpl._nunp;
    }
    
    private synchronized long getUnpackerPtr() {
        return this.unpackerPtr;
    }
    
    private long readInputFn(final ByteBuffer byteBuffer, final long n) throws IOException {
        if (this.in == null) {
            return 0L;
        }
        final long n2 = byteBuffer.capacity() - byteBuffer.position();
        assert n <= n2;
        long n3 = 0L;
        int n4 = 0;
        while (n3 < n) {
            ++n4;
            int length = this._buf.length;
            if (length > n2 - n3) {
                length = (int)(n2 - n3);
            }
            final int read = this.in.read(this._buf, 0, length);
            if (read <= 0) {
                break;
            }
            n3 += read;
            assert n3 <= n2;
            byteBuffer.put(this._buf, 0, read);
        }
        if (this._verbose > 1) {
            Utils.log.fine("readInputFn(" + n + "," + n2 + ") => " + n3 + " steps=" + n4);
        }
        if (n2 > 100L) {
            this._estByteLimit = this._byteCount + n2;
        }
        else {
            this._estByteLimit = (this._byteCount + n3) * 20L;
        }
        this._byteCount += n3;
        this.updateProgress();
        return n3;
    }
    
    private void updateProgress() {
        double n = this._segCount;
        if (this._estByteLimit > 0L && this._byteCount > 0L) {
            n += this._byteCount / (double)this._estByteLimit;
        }
        int prevPercent = (int)Math.round(100.0 * (0.33 * n / Math.max(this._estSegLimit, 1) + 0.67 * this._fileCount / Math.max(this._estFileLimit, 1)));
        if (prevPercent > 100) {
            prevPercent = 100;
        }
        if (prevPercent > this._prevPercent) {
            this._prevPercent = prevPercent;
            this._props.setInteger("unpack.progress", prevPercent);
            if (this._verbose > 0) {
                Utils.log.info("progress = " + prevPercent);
            }
        }
    }
    
    private void copyInOption(final String s) {
        final String property = this._props.getProperty(s);
        if (this._verbose > 0) {
            Utils.log.info("set " + s + "=" + property);
        }
        if (property != null && !this.setOption(s, property)) {
            Utils.log.warning("Invalid option " + s + "=" + property);
        }
    }
    
    void run(final InputStream inputStream, final JarOutputStream jarOutputStream, ByteBuffer unusedInput) throws IOException {
        final BufferedInputStream in = new BufferedInputStream(inputStream);
        this.in = in;
        this._verbose = this._props.getInteger("com.sun.java.util.jar.pack.verbose");
        final int n = "keep".equals(this._props.getProperty("com.sun.java.util.jar.pack.unpack.modification.time", "0")) ? 0 : this._props.getTime("com.sun.java.util.jar.pack.unpack.modification.time");
        this.copyInOption("com.sun.java.util.jar.pack.verbose");
        this.copyInOption("unpack.deflate.hint");
        if (n == 0) {
            this.copyInOption("com.sun.java.util.jar.pack.unpack.modification.time");
        }
        this.updateProgress();
        while (true) {
            final long start = this.start(unusedInput, 0L);
            final long n2 = 0L;
            this._estByteLimit = n2;
            this._byteCount = n2;
            ++this._segCount;
            final int n3 = (int)(start >>> 32);
            final int n4 = (int)(start >>> 0);
            this._estSegLimit = this._segCount + n3;
            this._estFileLimit = (int)((this._fileCount + n4) * (double)this._estSegLimit / this._segCount);
            final int[] array = { 0, 0, 0, 0 };
            final Object[] array2 = { array, null, null, null };
            while (this.getNextFile(array2)) {
                this.writeEntry(jarOutputStream, (String)array2[1], (n != 0) ? ((long)n) : ((long)array[2]), ((long)array[0] << 32) + ((long)array[1] << 32 >>> 32), array[3] != 0, (ByteBuffer)array2[2], (ByteBuffer)array2[3]);
                ++this._fileCount;
                this.updateProgress();
            }
            unusedInput = this.getUnusedInput();
            final long finish = this.finish();
            if (this._verbose > 0) {
                Utils.log.info("bytes consumed = " + finish);
            }
            if (unusedInput == null && !Utils.isPackMagic(Utils.readMagic(in))) {
                break;
            }
            if (this._verbose <= 0 || unusedInput == null) {
                continue;
            }
            Utils.log.info("unused input = " + unusedInput);
        }
    }
    
    void run(final InputStream inputStream, final JarOutputStream jarOutputStream) throws IOException {
        this.run(inputStream, jarOutputStream, null);
    }
    
    void run(final File file, final JarOutputStream jarOutputStream) throws IOException {
        final ByteBuffer byteBuffer = null;
        try (final FileInputStream fileInputStream = new FileInputStream(file)) {
            this.run(fileInputStream, jarOutputStream, byteBuffer);
        }
    }
    
    private void writeEntry(final JarOutputStream jarOutputStream, final String s, final long n, final long n2, final boolean b, final ByteBuffer byteBuffer, final ByteBuffer byteBuffer2) throws IOException {
        final int n3 = (int)n2;
        if (n3 != n2) {
            throw new IOException("file too large: " + n2);
        }
        final CRC32 crc32 = this._crc32;
        if (this._verbose > 1) {
            Utils.log.fine("Writing entry: " + s + " size=" + n3 + (b ? " deflated" : ""));
        }
        if (this._buf.length < n3) {
            int i = n3;
            while (i < this._buf.length) {
                i <<= 1;
                if (i <= 0) {
                    i = n3;
                    break;
                }
            }
            this._buf = new byte[i];
        }
        assert this._buf.length >= n3;
        int j = 0;
        if (byteBuffer != null) {
            final int capacity = byteBuffer.capacity();
            byteBuffer.get(this._buf, j, capacity);
            j += capacity;
        }
        if (byteBuffer2 != null) {
            final int capacity2 = byteBuffer2.capacity();
            byteBuffer2.get(this._buf, j, capacity2);
            j += capacity2;
        }
        while (j < n3) {
            final int read = this.in.read(this._buf, j, n3 - j);
            if (read <= 0) {
                throw new IOException("EOF at end of archive");
            }
            j += read;
        }
        final ZipEntry zipEntry = new ZipEntry(s);
        zipEntry.setTime(n * 1000L);
        if (n3 == 0) {
            zipEntry.setMethod(0);
            zipEntry.setSize(0L);
            zipEntry.setCrc(0L);
            zipEntry.setCompressedSize(0L);
        }
        else if (!b) {
            zipEntry.setMethod(0);
            zipEntry.setSize(n3);
            zipEntry.setCompressedSize(n3);
            crc32.reset();
            crc32.update(this._buf, 0, n3);
            zipEntry.setCrc(crc32.getValue());
        }
        else {
            zipEntry.setMethod(8);
            zipEntry.setSize(n3);
        }
        jarOutputStream.putNextEntry(zipEntry);
        if (n3 > 0) {
            jarOutputStream.write(this._buf, 0, n3);
        }
        jarOutputStream.closeEntry();
        if (this._verbose > 0) {
            Utils.log.info("Writing " + Utils.zeString(zipEntry));
        }
    }
    
    static {
        AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Void>() {
            @Override
            public Void run() {
                System.loadLibrary("unpack");
                return null;
            }
        });
        initIDs();
    }
}
