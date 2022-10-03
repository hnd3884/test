package jdk.jfr.internal;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.io.InputStream;

final class ChunkInputStream extends InputStream
{
    private final Iterator<RepositoryChunk> chunks;
    private RepositoryChunk currentChunk;
    private InputStream stream;
    
    ChunkInputStream(final List<RepositoryChunk> list) throws IOException {
        final ArrayList list2 = new ArrayList(list.size());
        for (final RepositoryChunk repositoryChunk : list) {
            repositoryChunk.use();
            list2.add(repositoryChunk);
        }
        this.chunks = list2.iterator();
        this.nextStream();
    }
    
    @Override
    public int available() throws IOException {
        if (this.stream != null) {
            return this.stream.available();
        }
        return 0;
    }
    
    private boolean nextStream() throws IOException {
        if (!this.nextChunk()) {
            return false;
        }
        this.stream = new BufferedInputStream(SecuritySupport.newFileInputStream(this.currentChunk.getFile()));
        return true;
    }
    
    private boolean nextChunk() {
        if (!this.chunks.hasNext()) {
            return false;
        }
        this.currentChunk = this.chunks.next();
        return true;
    }
    
    @Override
    public int read() throws IOException {
        do {
            if (this.stream != null) {
                final int read = this.stream.read();
                if (read != -1) {
                    return read;
                }
                this.stream.close();
                this.currentChunk.release();
                this.stream = null;
                this.currentChunk = null;
            }
        } while (this.nextStream());
        return -1;
    }
    
    @Override
    public void close() throws IOException {
        if (this.stream != null) {
            this.stream.close();
            this.stream = null;
        }
        while (this.currentChunk != null) {
            this.currentChunk.release();
            this.currentChunk = null;
            if (!this.nextChunk()) {
                return;
            }
        }
    }
    
    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        this.close();
    }
}
