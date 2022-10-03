package org.apache.lucene.search.suggest.fst;

import java.util.Comparator;
import org.apache.lucene.util.IOUtils;
import org.apache.lucene.util.BytesRefIterator;
import org.apache.lucene.util.BytesRef;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.Path;
import org.apache.lucene.util.OfflineSorter;
import java.io.Closeable;

public class ExternalRefSorter implements BytesRefSorter, Closeable
{
    private final OfflineSorter sort;
    private OfflineSorter.ByteSequencesWriter writer;
    private Path input;
    private Path sorted;
    
    public ExternalRefSorter(final OfflineSorter sort) throws IOException {
        this.sort = sort;
        this.input = Files.createTempFile(OfflineSorter.getDefaultTempDir(), "RefSorter-", ".raw", (FileAttribute<?>[])new FileAttribute[0]);
        this.writer = new OfflineSorter.ByteSequencesWriter(this.input);
    }
    
    @Override
    public void add(final BytesRef utf8) throws IOException {
        if (this.writer == null) {
            throw new IllegalStateException();
        }
        this.writer.write(utf8);
    }
    
    @Override
    public BytesRefIterator iterator() throws IOException {
        if (this.sorted == null) {
            this.closeWriter();
            this.sorted = Files.createTempFile(OfflineSorter.getDefaultTempDir(), "RefSorter-", ".sorted", (FileAttribute<?>[])new FileAttribute[0]);
            boolean success = false;
            try {
                this.sort.sort(this.input, this.sorted);
                success = true;
            }
            finally {
                if (success) {
                    Files.delete(this.input);
                }
                else {
                    IOUtils.deleteFilesIgnoringExceptions(new Path[] { this.input });
                }
            }
            this.input = null;
        }
        return (BytesRefIterator)new ByteSequenceIterator(new OfflineSorter.ByteSequencesReader(this.sorted));
    }
    
    private void closeWriter() throws IOException {
        if (this.writer != null) {
            this.writer.close();
            this.writer = null;
        }
    }
    
    @Override
    public void close() throws IOException {
        boolean success = false;
        try {
            this.closeWriter();
            success = true;
        }
        finally {
            if (success) {
                IOUtils.deleteFilesIfExist(new Path[] { this.input, this.sorted });
            }
            else {
                IOUtils.deleteFilesIgnoringExceptions(new Path[] { this.input, this.sorted });
            }
        }
    }
    
    @Override
    public Comparator<BytesRef> getComparator() {
        return this.sort.getComparator();
    }
    
    class ByteSequenceIterator implements BytesRefIterator
    {
        private final OfflineSorter.ByteSequencesReader reader;
        private BytesRef scratch;
        
        public ByteSequenceIterator(final OfflineSorter.ByteSequencesReader reader) {
            this.scratch = new BytesRef();
            this.reader = reader;
        }
        
        public BytesRef next() throws IOException {
            if (this.scratch == null) {
                return null;
            }
            boolean success = false;
            try {
                final byte[] next = this.reader.read();
                if (next != null) {
                    this.scratch.bytes = next;
                    this.scratch.length = next.length;
                    this.scratch.offset = 0;
                }
                else {
                    IOUtils.close(new Closeable[] { (Closeable)this.reader });
                    this.scratch = null;
                }
                success = true;
                return this.scratch;
            }
            finally {
                if (!success) {
                    IOUtils.closeWhileHandlingException(new Closeable[] { (Closeable)this.reader });
                }
            }
        }
    }
}
