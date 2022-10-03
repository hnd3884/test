package org.apache.lucene.rangetree;

import java.io.IOException;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import org.apache.lucene.store.InputStreamDataInput;

final class OfflineSliceReader implements SliceReader
{
    final InputStreamDataInput in;
    long countLeft;
    private long value;
    private long ord;
    private int docID;
    
    OfflineSliceReader(final Path tempFile, final long start, final long count) throws IOException {
        final InputStream fis = Files.newInputStream(tempFile, new OpenOption[0]);
        final long seekFP = start * 20L;
        long skipped = 0L;
        while (skipped < seekFP) {
            final long inc = fis.skip(seekFP - skipped);
            skipped += inc;
            if (inc == 0L) {
                throw new RuntimeException("skip returned 0");
            }
        }
        this.in = new InputStreamDataInput((InputStream)new BufferedInputStream(fis));
        this.countLeft = count;
    }
    
    @Override
    public boolean next() throws IOException {
        if (this.countLeft == 0L) {
            return false;
        }
        --this.countLeft;
        this.value = this.in.readLong();
        this.ord = this.in.readLong();
        this.docID = this.in.readInt();
        return true;
    }
    
    @Override
    public long value() {
        return this.value;
    }
    
    @Override
    public long ord() {
        return this.ord;
    }
    
    @Override
    public int docID() {
        return this.docID;
    }
    
    @Override
    public void close() throws IOException {
        this.in.close();
    }
}
