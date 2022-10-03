package org.apache.lucene.store;

import java.io.OutputStream;
import java.io.FilterOutputStream;
import java.nio.file.StandardOpenOption;
import java.nio.file.OpenOption;
import org.apache.lucene.util.IOUtils;
import java.nio.file.StandardCopyOption;
import java.nio.file.CopyOption;
import java.util.Collection;
import java.util.Iterator;
import java.nio.file.DirectoryStream;
import java.util.List;
import java.util.ArrayList;
import org.apache.lucene.util.Constants;
import java.io.IOException;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;

public abstract class FSDirectory extends BaseDirectory
{
    protected final Path directory;
    
    protected FSDirectory(final Path path, final LockFactory lockFactory) throws IOException {
        super(lockFactory);
        if (!Files.isDirectory(path, new LinkOption[0])) {
            Files.createDirectories(path, (FileAttribute<?>[])new FileAttribute[0]);
        }
        this.directory = path.toRealPath(new LinkOption[0]);
    }
    
    public static FSDirectory open(final Path path) throws IOException {
        return open(path, FSLockFactory.getDefault());
    }
    
    public static FSDirectory open(final Path path, final LockFactory lockFactory) throws IOException {
        if (Constants.JRE_IS_64BIT && MMapDirectory.UNMAP_SUPPORTED) {
            return new MMapDirectory(path, lockFactory);
        }
        if (Constants.WINDOWS) {
            return new SimpleFSDirectory(path, lockFactory);
        }
        return new NIOFSDirectory(path, lockFactory);
    }
    
    public static String[] listAll(final Path dir) throws IOException {
        final List<String> entries = new ArrayList<String>();
        try (final DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
            for (final Path path : stream) {
                entries.add(path.getFileName().toString());
            }
        }
        return entries.toArray(new String[entries.size()]);
    }
    
    @Override
    public String[] listAll() throws IOException {
        this.ensureOpen();
        return listAll(this.directory);
    }
    
    @Override
    public long fileLength(final String name) throws IOException {
        this.ensureOpen();
        return Files.size(this.directory.resolve(name));
    }
    
    @Override
    public void deleteFile(final String name) throws IOException {
        this.ensureOpen();
        Files.delete(this.directory.resolve(name));
    }
    
    @Override
    public IndexOutput createOutput(final String name, final IOContext context) throws IOException {
        this.ensureOpen();
        return new FSIndexOutput(name);
    }
    
    @Override
    public void sync(final Collection<String> names) throws IOException {
        this.ensureOpen();
        for (final String name : names) {
            this.fsync(name);
        }
    }
    
    @Override
    public void renameFile(final String source, final String dest) throws IOException {
        this.ensureOpen();
        Files.move(this.directory.resolve(source), this.directory.resolve(dest), StandardCopyOption.ATOMIC_MOVE);
        IOUtils.fsync(this.directory, true);
    }
    
    @Override
    public synchronized void close() {
        this.isOpen = false;
    }
    
    public Path getDirectory() {
        this.ensureOpen();
        return this.directory;
    }
    
    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "@" + this.directory + " lockFactory=" + this.lockFactory;
    }
    
    protected void fsync(final String name) throws IOException {
        IOUtils.fsync(this.directory.resolve(name), false);
    }
    
    final class FSIndexOutput extends OutputStreamIndexOutput
    {
        static final int CHUNK_SIZE = 8192;
        
        public FSIndexOutput(final String name) throws IOException {
            super("FSIndexOutput(path=\"" + FSDirectory.this.directory.resolve(name) + "\")", new FilterOutputStream(Files.newOutputStream(FSDirectory.this.directory.resolve(name), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE)) {
                @Override
                public void write(final byte[] b, int offset, int length) throws IOException {
                    while (length > 0) {
                        final int chunk = Math.min(length, 8192);
                        this.out.write(b, offset, chunk);
                        length -= chunk;
                        offset += chunk;
                    }
                }
            }, 8192);
        }
    }
}
