package org.apache.lucene.index;

import java.io.IOException;
import java.util.List;

public abstract class FilterDirectoryReader extends DirectoryReader
{
    protected final DirectoryReader in;
    
    public static DirectoryReader unwrap(DirectoryReader reader) {
        while (reader instanceof FilterDirectoryReader) {
            reader = ((FilterDirectoryReader)reader).in;
        }
        return reader;
    }
    
    public FilterDirectoryReader(final DirectoryReader in, final SubReaderWrapper wrapper) throws IOException {
        super(in.directory(), wrapper.wrap(in.getSequentialSubReaders()));
        this.in = in;
    }
    
    protected abstract DirectoryReader doWrapDirectoryReader(final DirectoryReader p0) throws IOException;
    
    private final DirectoryReader wrapDirectoryReader(final DirectoryReader in) throws IOException {
        return (in == null) ? null : this.doWrapDirectoryReader(in);
    }
    
    @Override
    protected final DirectoryReader doOpenIfChanged() throws IOException {
        return this.wrapDirectoryReader(this.in.doOpenIfChanged());
    }
    
    @Override
    protected final DirectoryReader doOpenIfChanged(final IndexCommit commit) throws IOException {
        return this.wrapDirectoryReader(this.in.doOpenIfChanged(commit));
    }
    
    @Override
    protected final DirectoryReader doOpenIfChanged(final IndexWriter writer, final boolean applyAllDeletes) throws IOException {
        return this.wrapDirectoryReader(this.in.doOpenIfChanged(writer, applyAllDeletes));
    }
    
    @Override
    public long getVersion() {
        return this.in.getVersion();
    }
    
    @Override
    public boolean isCurrent() throws IOException {
        return this.in.isCurrent();
    }
    
    @Override
    public IndexCommit getIndexCommit() throws IOException {
        return this.in.getIndexCommit();
    }
    
    @Override
    protected void doClose() throws IOException {
        this.in.close();
    }
    
    public DirectoryReader getDelegate() {
        return this.in;
    }
    
    public abstract static class SubReaderWrapper
    {
        private LeafReader[] wrap(final List<? extends LeafReader> readers) {
            final LeafReader[] wrapped = new LeafReader[readers.size()];
            for (int i = 0; i < readers.size(); ++i) {
                wrapped[i] = this.wrap((LeafReader)readers.get(i));
            }
            return wrapped;
        }
        
        public abstract LeafReader wrap(final LeafReader p0);
    }
}
