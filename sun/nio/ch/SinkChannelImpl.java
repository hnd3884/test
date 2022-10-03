package sun.nio.ch;

import java.nio.channels.AsynchronousCloseException;
import java.nio.ByteBuffer;
import java.io.IOException;
import java.nio.channels.spi.SelectorProvider;
import java.io.FileDescriptor;
import java.nio.channels.SocketChannel;
import java.nio.channels.Pipe;

class SinkChannelImpl extends Pipe.SinkChannel implements SelChImpl
{
    SocketChannel sc;
    
    @Override
    public FileDescriptor getFD() {
        return ((SocketChannelImpl)this.sc).getFD();
    }
    
    @Override
    public int getFDVal() {
        return ((SocketChannelImpl)this.sc).getFDVal();
    }
    
    SinkChannelImpl(final SelectorProvider selectorProvider, final SocketChannel sc) {
        super(selectorProvider);
        this.sc = sc;
    }
    
    @Override
    protected void implCloseSelectableChannel() throws IOException {
        if (!this.isRegistered()) {
            this.kill();
        }
    }
    
    @Override
    public void kill() throws IOException {
        this.sc.close();
    }
    
    @Override
    protected void implConfigureBlocking(final boolean b) throws IOException {
        this.sc.configureBlocking(b);
    }
    
    public boolean translateReadyOps(final int n, final int n2, final SelectionKeyImpl selectionKeyImpl) {
        final int nioInterestOps = selectionKeyImpl.nioInterestOps();
        final int nioReadyOps = selectionKeyImpl.nioReadyOps();
        int n3 = n2;
        if ((n & Net.POLLNVAL) != 0x0) {
            throw new Error("POLLNVAL detected");
        }
        if ((n & (Net.POLLERR | Net.POLLHUP)) != 0x0) {
            final int n4 = nioInterestOps;
            selectionKeyImpl.nioReadyOps(n4);
            return (n4 & ~nioReadyOps) != 0x0;
        }
        if ((n & Net.POLLOUT) != 0x0 && (nioInterestOps & 0x4) != 0x0) {
            n3 |= 0x4;
        }
        selectionKeyImpl.nioReadyOps(n3);
        return (n3 & ~nioReadyOps) != 0x0;
    }
    
    @Override
    public boolean translateAndUpdateReadyOps(final int n, final SelectionKeyImpl selectionKeyImpl) {
        return this.translateReadyOps(n, selectionKeyImpl.nioReadyOps(), selectionKeyImpl);
    }
    
    @Override
    public boolean translateAndSetReadyOps(final int n, final SelectionKeyImpl selectionKeyImpl) {
        return this.translateReadyOps(n, 0, selectionKeyImpl);
    }
    
    @Override
    public void translateAndSetInterestOps(int pollout, final SelectionKeyImpl selectionKeyImpl) {
        if ((pollout & 0x4) != 0x0) {
            pollout = Net.POLLOUT;
        }
        selectionKeyImpl.selector.putEventOps(selectionKeyImpl, pollout);
    }
    
    @Override
    public int write(final ByteBuffer byteBuffer) throws IOException {
        try {
            return this.sc.write(byteBuffer);
        }
        catch (final AsynchronousCloseException ex) {
            this.close();
            throw ex;
        }
    }
    
    @Override
    public long write(final ByteBuffer[] array) throws IOException {
        try {
            return this.sc.write(array);
        }
        catch (final AsynchronousCloseException ex) {
            this.close();
            throw ex;
        }
    }
    
    @Override
    public long write(final ByteBuffer[] array, final int n, final int n2) throws IOException {
        if (n < 0 || n2 < 0 || n > array.length - n2) {
            throw new IndexOutOfBoundsException();
        }
        try {
            return this.write(Util.subsequence(array, n, n2));
        }
        catch (final AsynchronousCloseException ex) {
            this.close();
            throw ex;
        }
    }
}
