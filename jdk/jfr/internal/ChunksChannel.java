package jdk.jfr.internal;

import java.nio.channels.FileChannel;
import java.nio.ByteBuffer;
import java.io.IOException;
import java.util.ArrayList;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Iterator;
import java.nio.channels.ReadableByteChannel;

final class ChunksChannel implements ReadableByteChannel
{
    private final Iterator<RepositoryChunk> chunks;
    private RepositoryChunk current;
    private ReadableByteChannel channel;
    
    public ChunksChannel(final List<RepositoryChunk> list) throws IOException {
        if (list.isEmpty()) {
            throw new FileNotFoundException("No chunks");
        }
        final ArrayList list2 = new ArrayList(list.size());
        for (final RepositoryChunk repositoryChunk : list) {
            repositoryChunk.use();
            list2.add(repositoryChunk);
        }
        this.chunks = list2.iterator();
        this.nextChannel();
    }
    
    private boolean nextChunk() {
        if (!this.chunks.hasNext()) {
            return false;
        }
        this.current = this.chunks.next();
        return true;
    }
    
    private boolean nextChannel() throws IOException {
        if (!this.nextChunk()) {
            return false;
        }
        this.channel = this.current.newChannel();
        return true;
    }
    
    @Override
    public int read(final ByteBuffer byteBuffer) throws IOException {
        do {
            if (this.channel != null) {
                assert this.current != null;
                final int read = this.channel.read(byteBuffer);
                if (read != -1) {
                    return read;
                }
                this.channel.close();
                this.current.release();
                this.channel = null;
                this.current = null;
            }
        } while (this.nextChannel());
        return -1;
    }
    
    public long transferTo(final FileChannel fileChannel) throws IOException {
        long n = 0L;
        do {
            if (this.channel != null) {
                assert this.current != null;
                long transfer;
                for (long size = this.current.getSize(); size > 0L; size -= transfer) {
                    transfer = fileChannel.transferFrom(this.channel, n, Math.min(size, 1048576L));
                    n += transfer;
                }
                this.channel.close();
                this.current.release();
                this.channel = null;
                this.current = null;
            }
        } while (this.nextChannel());
        return n;
    }
    
    @Override
    public void close() throws IOException {
        if (this.channel != null) {
            this.channel.close();
            this.channel = null;
        }
        while (this.current != null) {
            this.current.release();
            this.current = null;
            if (!this.nextChunk()) {
                return;
            }
        }
    }
    
    @Override
    public boolean isOpen() {
        return this.channel != null;
    }
    
    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        this.close();
    }
}
