package java.net;

import sun.net.ConnectionResetException;
import java.io.FileDescriptor;
import java.nio.channels.FileChannel;
import java.io.IOException;
import java.io.FileOutputStream;

class SocketOutputStream extends FileOutputStream
{
    private AbstractPlainSocketImpl impl;
    private byte[] temp;
    private Socket socket;
    private boolean closing;
    
    SocketOutputStream(final AbstractPlainSocketImpl impl) throws IOException {
        super(impl.getFileDescriptor());
        this.impl = null;
        this.temp = new byte[1];
        this.socket = null;
        this.closing = false;
        this.impl = impl;
        this.socket = impl.getSocket();
    }
    
    @Override
    public final FileChannel getChannel() {
        return null;
    }
    
    private native void socketWrite0(final FileDescriptor p0, final byte[] p1, final int p2, final int p3) throws IOException;
    
    private void socketWrite(final byte[] array, final int n, final int n2) throws IOException {
        if (n2 > 0 && n >= 0 && n2 <= array.length - n) {
            final FileDescriptor acquireFD = this.impl.acquireFD();
            try {
                this.socketWrite0(acquireFD, array, n, n2);
            }
            catch (final SocketException ex) {
                if (ex instanceof ConnectionResetException) {
                    this.impl.setConnectionResetPending();
                    ex = new SocketException("Connection reset");
                }
                if (this.impl.isClosedOrPending()) {
                    throw new SocketException("Socket closed");
                }
                throw ex;
            }
            finally {
                this.impl.releaseFD();
            }
            return;
        }
        if (n2 == 0) {
            return;
        }
        throw new ArrayIndexOutOfBoundsException("len == " + n2 + " off == " + n + " buffer length == " + array.length);
    }
    
    @Override
    public void write(final int n) throws IOException {
        this.temp[0] = (byte)n;
        this.socketWrite(this.temp, 0, 1);
    }
    
    @Override
    public void write(final byte[] array) throws IOException {
        this.socketWrite(array, 0, array.length);
    }
    
    @Override
    public void write(final byte[] array, final int n, final int n2) throws IOException {
        this.socketWrite(array, n, n2);
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
    
    @Override
    protected void finalize() {
    }
    
    private static native void init();
    
    static {
        init();
    }
}
