package org.apache.lucene.store;

import java.util.Collections;
import org.apache.lucene.util.Accountables;
import org.apache.lucene.util.IOUtils;
import java.io.Closeable;
import java.util.Iterator;
import java.util.Collection;
import java.io.IOException;
import java.util.Set;
import java.util.HashSet;
import org.apache.lucene.util.Accountable;

public class NRTCachingDirectory extends FilterDirectory implements Accountable
{
    private final RAMDirectory cache;
    private final long maxMergeSizeBytes;
    private final long maxCachedBytes;
    private static final boolean VERBOSE = false;
    private final Object uncacheLock;
    
    public NRTCachingDirectory(final Directory delegate, final double maxMergeSizeMB, final double maxCachedMB) {
        super(delegate);
        this.cache = new RAMDirectory();
        this.uncacheLock = new Object();
        this.maxMergeSizeBytes = (long)(maxMergeSizeMB * 1024.0 * 1024.0);
        this.maxCachedBytes = (long)(maxCachedMB * 1024.0 * 1024.0);
    }
    
    @Override
    public String toString() {
        return "NRTCachingDirectory(" + this.in + "; maxCacheMB=" + this.maxCachedBytes / 1024L / 1024.0 + " maxMergeSizeMB=" + this.maxMergeSizeBytes / 1024L / 1024.0 + ")";
    }
    
    @Override
    public synchronized String[] listAll() throws IOException {
        final Set<String> files = new HashSet<String>();
        for (final String f : this.cache.listAll()) {
            files.add(f);
        }
        for (final String f : this.in.listAll()) {
            files.add(f);
        }
        return files.toArray(new String[files.size()]);
    }
    
    @Override
    public synchronized void deleteFile(final String name) throws IOException {
        if (this.cache.fileNameExists(name)) {
            this.cache.deleteFile(name);
        }
        else {
            this.in.deleteFile(name);
        }
    }
    
    @Override
    public synchronized long fileLength(final String name) throws IOException {
        if (this.cache.fileNameExists(name)) {
            return this.cache.fileLength(name);
        }
        return this.in.fileLength(name);
    }
    
    public String[] listCachedFiles() {
        return this.cache.listAll();
    }
    
    @Override
    public IndexOutput createOutput(final String name, final IOContext context) throws IOException {
        if (this.doCacheWrite(name, context)) {
            try {
                this.in.deleteFile(name);
            }
            catch (final IOException ex) {}
            return this.cache.createOutput(name, context);
        }
        try {
            this.cache.deleteFile(name);
        }
        catch (final IOException ex2) {}
        return this.in.createOutput(name, context);
    }
    
    @Override
    public void sync(final Collection<String> fileNames) throws IOException {
        for (final String fileName : fileNames) {
            this.unCache(fileName);
        }
        this.in.sync(fileNames);
    }
    
    @Override
    public void renameFile(final String source, final String dest) throws IOException {
        this.unCache(source);
        this.in.renameFile(source, dest);
    }
    
    @Override
    public synchronized IndexInput openInput(final String name, final IOContext context) throws IOException {
        if (this.cache.fileNameExists(name)) {
            return this.cache.openInput(name, context);
        }
        return this.in.openInput(name, context);
    }
    
    @Override
    public void close() throws IOException {
        boolean success = false;
        try {
            if (this.cache.isOpen) {
                for (final String fileName : this.cache.listAll()) {
                    this.unCache(fileName);
                }
            }
            success = true;
        }
        finally {
            if (success) {
                IOUtils.close(this.cache, this.in);
            }
            else {
                IOUtils.closeWhileHandlingException(this.cache, this.in);
            }
        }
    }
    
    protected boolean doCacheWrite(final String name, final IOContext context) {
        long bytes = 0L;
        if (context.mergeInfo != null) {
            bytes = context.mergeInfo.estimatedMergeBytes;
        }
        else if (context.flushInfo != null) {
            bytes = context.flushInfo.estimatedSegmentSize;
        }
        return bytes <= this.maxMergeSizeBytes && bytes + this.cache.ramBytesUsed() <= this.maxCachedBytes;
    }
    
    private void unCache(final String fileName) throws IOException {
        synchronized (this.uncacheLock) {
            if (!this.cache.fileNameExists(fileName)) {
                return;
            }
            final IOContext context = IOContext.DEFAULT;
            final IndexOutput out = this.in.createOutput(fileName, context);
            IndexInput in = null;
            try {
                in = this.cache.openInput(fileName, context);
                out.copyBytes(in, in.length());
            }
            finally {
                IOUtils.close(in, out);
            }
            synchronized (this) {
                this.cache.deleteFile(fileName);
            }
        }
    }
    
    @Override
    public long ramBytesUsed() {
        return this.cache.ramBytesUsed();
    }
    
    @Override
    public Collection<Accountable> getChildResources() {
        return Collections.singleton(Accountables.namedAccountable("cache", this.cache));
    }
}
