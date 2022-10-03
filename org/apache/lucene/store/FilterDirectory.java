package org.apache.lucene.store;

import java.util.Collection;
import java.io.IOException;

public class FilterDirectory extends Directory
{
    protected final Directory in;
    
    public static Directory unwrap(Directory dir) {
        while (dir instanceof FilterDirectory) {
            dir = ((FilterDirectory)dir).in;
        }
        return dir;
    }
    
    protected FilterDirectory(final Directory in) {
        this.in = in;
    }
    
    public final Directory getDelegate() {
        return this.in;
    }
    
    @Override
    public String[] listAll() throws IOException {
        return this.in.listAll();
    }
    
    @Override
    public void deleteFile(final String name) throws IOException {
        this.in.deleteFile(name);
    }
    
    @Override
    public long fileLength(final String name) throws IOException {
        return this.in.fileLength(name);
    }
    
    @Override
    public IndexOutput createOutput(final String name, final IOContext context) throws IOException {
        return this.in.createOutput(name, context);
    }
    
    @Override
    public void sync(final Collection<String> names) throws IOException {
        this.in.sync(names);
    }
    
    @Override
    public void renameFile(final String source, final String dest) throws IOException {
        this.in.renameFile(source, dest);
    }
    
    @Override
    public IndexInput openInput(final String name, final IOContext context) throws IOException {
        return this.in.openInput(name, context);
    }
    
    @Override
    public Lock obtainLock(final String name) throws IOException {
        return this.in.obtainLock(name);
    }
    
    @Override
    public void close() throws IOException {
        this.in.close();
    }
    
    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "(" + this.in.toString() + ")";
    }
}
