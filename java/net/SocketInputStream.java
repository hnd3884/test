package java.net;

import sun.net.ConnectionResetException;
import java.io.FileDescriptor;
import java.nio.channels.FileChannel;
import java.io.IOException;
import java.io.FileInputStream;

class SocketInputStream extends FileInputStream
{
    private boolean eof;
    private AbstractPlainSocketImpl impl;
    private byte[] temp;
    private Socket socket;
    private boolean closing;
    
    SocketInputStream(final AbstractPlainSocketImpl impl) throws IOException {
        super(impl.getFileDescriptor());
        this.impl = null;
        this.socket = null;
        this.closing = false;
        this.impl = impl;
        this.socket = impl.getSocket();
    }
    
    @Override
    public final FileChannel getChannel() {
        return null;
    }
    
    private native int socketRead0(final FileDescriptor p0, final byte[] p1, final int p2, final int p3, final int p4) throws IOException;
    
    private int socketRead(final FileDescriptor fileDescriptor, final byte[] array, final int n, final int n2, final int n3) throws IOException {
        return this.socketRead0(fileDescriptor, array, n, n2, n3);
    }
    
    @Override
    public int read(final byte[] array) throws IOException {
        return this.read(array, 0, array.length);
    }
    
    @Override
    public int read(final byte[] array, final int n, final int n2) throws IOException {
        return this.read(array, n, n2, this.impl.getTimeout());
    }
    
    int read(final byte[] array, final int n, final int n2, final int n3) throws IOException {
        if (this.eof) {
            return -1;
        }
        if (this.impl.isConnectionReset()) {
            throw new SocketException("Connection reset");
        }
        if (n2 <= 0 || n < 0 || n2 > array.length - n) {
            if (n2 == 0) {
                return 0;
            }
            throw new ArrayIndexOutOfBoundsException("length == " + n2 + " off == " + n + " buffer length == " + array.length);
        }
        else {
            boolean b = false;
            final FileDescriptor acquireFD = this.impl.acquireFD();
            try {
                final int socketRead = this.socketRead(acquireFD, array, n, n2, n3);
                if (socketRead > 0) {
                    return socketRead;
                }
            }
            catch (final ConnectionResetException ex) {
                b = true;
            }
            finally {
                this.impl.releaseFD();
            }
            if (b) {
                this.impl.setConnectionResetPending();
                this.impl.acquireFD();
                try {
                    final int socketRead2 = this.socketRead(acquireFD, array, n, n2, n3);
                    if (socketRead2 > 0) {
                        return socketRead2;
                    }
                }
                catch (final ConnectionResetException ex2) {}
                finally {
                    this.impl.releaseFD();
                }
            }
            if (this.impl.isClosedOrPending()) {
                throw new SocketException("Socket closed");
            }
            if (this.impl.isConnectionResetPending()) {
                this.impl.setConnectionReset();
            }
            if (this.impl.isConnectionReset()) {
                throw new SocketException("Connection reset");
            }
            this.eof = true;
            return -1;
        }
    }
    
    @Override
    public int read() throws IOException {
        if (this.eof) {
            return -1;
        }
        this.temp = new byte[1];
        if (this.read(this.temp, 0, 1) <= 0) {
            return -1;
        }
        return this.temp[0] & 0xFF;
    }
    
    @Override
    public long skip(final long n) throws IOException {
        if (n <= 0L) {
            return 0L;
        }
        long n2 = n;
        final int n3 = (int)Math.min(1024L, n2);
        final byte[] array = new byte[n3];
        while (n2 > 0L) {
            final int read = this.read(array, 0, (int)Math.min(n3, n2));
            if (read < 0) {
                break;
            }
            n2 -= read;
        }
        return n - n2;
    }
    
    @Override
    public int available() throws IOException {
        return this.impl.available();
    }
    
    @Override
    public void close() throws IOException {
        if (this.closing) {
            return;
        }
        this.closing = true;
        if (this.socket != null) {
            if (!this.socket.isClosed()) {
                this.socket.close();
            }
        }
        else {
            this.impl.close();
        }
        this.closing = false;
    }
    
    void setEOF(final boolean eof) {
        this.eof = eof;
    }
    
    @Override
    protected void finalize() {
    }
    
    private static native void init();
    
    static {
        init();
    }
}
