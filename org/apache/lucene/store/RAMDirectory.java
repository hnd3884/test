package org.apache.lucene.store;

import org.apache.lucene.util.Accountables;
import java.util.Collection;
import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.ArrayList;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.Map;
import org.apache.lucene.util.Accountable;

public class RAMDirectory extends BaseDirectory implements Accountable
{
    protected final Map<String, RAMFile> fileMap;
    protected final AtomicLong sizeInBytes;
    
    public RAMDirectory() {
        this(new SingleInstanceLockFactory());
    }
    
    public RAMDirectory(final LockFactory lockFactory) {
        super(lockFactory);
        this.fileMap = new ConcurrentHashMap<String, RAMFile>();
        this.sizeInBytes = new AtomicLong();
    }
    
    public RAMDirectory(final FSDirectory dir, final IOContext context) throws IOException {
        this(dir, false, context);
    }
    
    private RAMDirectory(final FSDirectory dir, final boolean closeDir, final IOContext context) throws IOException {
        this();
        for (final String file : dir.listAll()) {
            if (!Files.isDirectory(dir.getDirectory().resolve(file), new LinkOption[0])) {
                this.copyFrom(dir, file, file, context);
            }
        }
        if (closeDir) {
            dir.close();
        }
    }
    
    @Override
    public final String[] listAll() {
        this.ensureOpen();
        final Set<String> fileNames = this.fileMap.keySet();
        final List<String> names = new ArrayList<String>(fileNames.size());
        for (final String name : fileNames) {
            names.add(name);
        }
        return names.toArray(new String[names.size()]);
    }
    
    public final boolean fileNameExists(final String name) {
        this.ensureOpen();
        return this.fileMap.containsKey(name);
    }
    
    @Override
    public final long fileLength(final String name) throws IOException {
        this.ensureOpen();
        final RAMFile file = this.fileMap.get(name);
        if (file == null) {
            throw new FileNotFoundException(name);
        }
        return file.getLength();
    }
    
    @Override
    public final long ramBytesUsed() {
        this.ensureOpen();
        return this.sizeInBytes.get();
    }
    
    @Override
    public Collection<Accountable> getChildResources() {
        return Accountables.namedAccountables("file", this.fileMap);
    }
    
    @Override
    public void deleteFile(final String name) throws IOException {
        this.ensureOpen();
        final RAMFile file = this.fileMap.remove(name);
        if (file != null) {
            file.directory = null;
            this.sizeInBytes.addAndGet(-file.sizeInBytes);
            return;
        }
        throw new FileNotFoundException(name);
    }
    
    @Override
    public IndexOutput createOutput(final String name, final IOContext context) throws IOException {
        this.ensureOpen();
        final RAMFile file = this.newRAMFile();
        final RAMFile existing = this.fileMap.remove(name);
        if (existing != null) {
            this.sizeInBytes.addAndGet(-existing.sizeInBytes);
            existing.directory = null;
        }
        this.fileMap.put(name, file);
        return new RAMOutputStream(name, file, true);
    }
    
    protected RAMFile newRAMFile() {
        return new RAMFile(this);
    }
    
    @Override
    public void sync(final Collection<String> names) throws IOException {
    }
    
    @Override
    public void renameFile(final String source, final String dest) throws IOException {
        this.ensureOpen();
        final RAMFile file = this.fileMap.get(source);
        if (file == null) {
            throw new FileNotFoundException(source);
        }
        this.fileMap.put(dest, file);
        this.fileMap.remove(source);
    }
    
    @Override
    public IndexInput openInput(final String name, final IOContext context) throws IOException {
        this.ensureOpen();
        final RAMFile file = this.fileMap.get(name);
        if (file == null) {
            throw new FileNotFoundException(name);
        }
        return new RAMInputStream(name, file);
    }
    
    @Override
    public void close() {
        this.isOpen = false;
        this.fileMap.clear();
    }
}
