package org.apache.lucene.store;

import java.util.Collection;
import java.io.IOException;

public final class LockValidatingDirectoryWrapper extends FilterDirectory
{
    private final Lock writeLock;
    
    public LockValidatingDirectoryWrapper(final Directory in, final Lock writeLock) {
        super(in);
        this.writeLock = writeLock;
    }
    
    @Override
    public void deleteFile(final String name) throws IOException {
        this.writeLock.ensureValid();
        this.in.deleteFile(name);
    }
    
    @Override
    public IndexOutput createOutput(final String name, final IOContext context) throws IOException {
        this.writeLock.ensureValid();
        return this.in.createOutput(name, context);
    }
    
    @Override
    public void copyFrom(final Directory from, final String src, final String dest, final IOContext context) throws IOException {
        this.writeLock.ensureValid();
        this.in.copyFrom(from, src, dest, context);
    }
    
    @Override
    public void renameFile(final String source, final String dest) throws IOException {
        this.writeLock.ensureValid();
        this.in.renameFile(source, dest);
    }
    
    @Override
    public void sync(final Collection<String> names) throws IOException {
        this.writeLock.ensureValid();
        this.in.sync(names);
    }
}
