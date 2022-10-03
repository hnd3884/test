package sun.nio.ch;

import java.nio.channels.SeekableByteChannel;
import java.io.IOException;
import java.nio.channels.IllegalBlockingModeException;
import java.nio.channels.SelectableChannel;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.io.InputStream;

public class ChannelInputStream extends InputStream
{
    protected final ReadableByteChannel ch;
    private ByteBuffer bb;
    private byte[] bs;
    private byte[] b1;
    
    public static int read(final ReadableByteChannel readableByteChannel, final ByteBuffer byteBuffer, final boolean b) throws IOException {
        if (readableByteChannel instanceof SelectableChannel) {
            final SelectableChannel selectableChannel = (SelectableChannel)readableByteChannel;
            synchronized (selectableChannel.blockingLock()) {
                final boolean blocking = selectableChannel.isBlocking();
                if (!blocking) {
                    throw new IllegalBlockingModeException();
                }
                if (blocking != b) {
                    selectableChannel.configureBlocking(b);
                }
                final int read = readableByteChannel.read(byteBuffer);
                if (blocking != b) {
                    selectableChannel.configureBlocking(blocking);
                }
                return read;
            }
        }
        return readableByteChannel.read(byteBuffer);
    }
    
    public ChannelInputStream(final ReadableByteChannel ch) {
        this.bb = null;
        this.bs = null;
        this.b1 = null;
        this.ch = ch;
    }
    
    @Override
    public synchronized int read() throws IOException {
        if (this.b1 == null) {
            this.b1 = new byte[1];
        }
        if (this.read(this.b1) == 1) {
            return this.b1[0] & 0xFF;
        }
        return -1;
    }
    
    @Override
    public synchronized int read(final byte[] bs, final int n, final int n2) throws IOException {
        if (n < 0 || n > bs.length || n2 < 0 || n + n2 > bs.length || n + n2 < 0) {
            throw new IndexOutOfBoundsException();
        }
        if (n2 == 0) {
            return 0;
        }
        final ByteBuffer bb = (this.bs == bs) ? this.bb : ByteBuffer.wrap(bs);
        bb.limit(Math.min(n + n2, bb.capacity()));
        bb.position(n);
        this.bb = bb;
        this.bs = bs;
        return this.read(bb);
    }
    
    protected int read(final ByteBuffer byteBuffer) throws IOException {
        return read(this.ch, byteBuffer, true);
    }
    
    @Override
    public int available() throws IOException {
        if (this.ch instanceof SeekableByteChannel) {
            final SeekableByteChannel seekableByteChannel = (SeekableByteChannel)this.ch;
            final long max = Math.max(0L, seekableByteChannel.size() - seekableByteChannel.position());
            return (max > 2147483647L) ? Integer.MAX_VALUE : ((int)max);
        }
        return 0;
    }
    
    @Override
    public void close() throws IOException {
        this.ch.close();
    }
}
