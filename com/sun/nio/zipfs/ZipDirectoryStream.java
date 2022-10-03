package com.sun.nio.zipfs;

import java.util.NoSuchElementException;
import java.nio.file.ClosedDirectoryStreamException;
import java.io.IOException;
import java.nio.file.NotDirectoryException;
import java.util.Iterator;
import java.nio.file.Path;
import java.nio.file.DirectoryStream;

public class ZipDirectoryStream implements DirectoryStream<Path>
{
    private final ZipFileSystem zipfs;
    private final byte[] path;
    private final Filter<? super Path> filter;
    private volatile boolean isClosed;
    private volatile Iterator<Path> itr;
    
    ZipDirectoryStream(final ZipPath zipPath, final Filter<? super Path> filter) throws IOException {
        this.zipfs = zipPath.getFileSystem();
        this.path = zipPath.getResolvedPath();
        this.filter = filter;
        if (!this.zipfs.isDirectory(this.path)) {
            throw new NotDirectoryException(zipPath.toString());
        }
    }
    
    @Override
    public synchronized Iterator<Path> iterator() {
        if (this.isClosed) {
            throw new ClosedDirectoryStreamException();
        }
        if (this.itr != null) {
            throw new IllegalStateException("Iterator has already been returned");
        }
        try {
            this.itr = this.zipfs.iteratorOf(this.path, this.filter);
        }
        catch (final IOException ex) {
            throw new IllegalStateException(ex);
        }
        return new Iterator<Path>() {
            private Path next;
            
            @Override
            public boolean hasNext() {
                return !ZipDirectoryStream.this.isClosed && ZipDirectoryStream.this.itr.hasNext();
            }
            
            @Override
            public synchronized Path next() {
                if (ZipDirectoryStream.this.isClosed) {
                    throw new NoSuchElementException();
                }
                return ZipDirectoryStream.this.itr.next();
            }
            
            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }
    
    @Override
    public synchronized void close() throws IOException {
        this.isClosed = true;
    }
}
