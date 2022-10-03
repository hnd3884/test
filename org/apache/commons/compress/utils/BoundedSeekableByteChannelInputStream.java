package org.apache.commons.compress.utils;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;

public class BoundedSeekableByteChannelInputStream extends BoundedArchiveInputStream
{
    private final SeekableByteChannel channel;
    
    public BoundedSeekableByteChannelInputStream(final long start, final long remaining, final SeekableByteChannel channel) {
        super(start, remaining);
        this.channel = channel;
    }
    
    @Override
    protected int read(final long pos, final ByteBuffer buf) throws IOException {
        final int read;
        synchronized (this.channel) {
            this.channel.position(pos);
            read = this.channel.read(buf);
        }
        buf.flip();
        return read;
    }
}
