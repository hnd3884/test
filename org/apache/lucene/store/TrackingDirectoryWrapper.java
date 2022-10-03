package org.apache.lucene.store;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public final class TrackingDirectoryWrapper extends FilterDirectory
{
    private final Set<String> createdFileNames;
    
    public TrackingDirectoryWrapper(final Directory in) {
        super(in);
        this.createdFileNames = Collections.synchronizedSet(new HashSet<String>());
    }
    
    @Override
    public void deleteFile(final String name) throws IOException {
        this.in.deleteFile(name);
        this.createdFileNames.remove(name);
    }
    
    @Override
    public IndexOutput createOutput(final String name, final IOContext context) throws IOException {
        final IndexOutput output = this.in.createOutput(name, context);
        this.createdFileNames.add(name);
        return output;
    }
    
    @Override
    public void copyFrom(final Directory from, final String src, final String dest, final IOContext context) throws IOException {
        this.in.copyFrom(from, src, dest, context);
        this.createdFileNames.add(dest);
    }
    
    @Override
    public void renameFile(final String source, final String dest) throws IOException {
        this.in.renameFile(source, dest);
        synchronized (this.createdFileNames) {
            this.createdFileNames.add(dest);
            this.createdFileNames.remove(source);
        }
    }
    
    public Set<String> getCreatedFiles() {
        return this.createdFileNames;
    }
}
