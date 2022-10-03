package java.nio.file;

import java.nio.file.attribute.UserPrincipalLookupService;
import java.util.Set;
import java.io.IOException;
import java.nio.file.spi.FileSystemProvider;
import java.io.Closeable;

public abstract class FileSystem implements Closeable
{
    protected FileSystem() {
    }
    
    public abstract FileSystemProvider provider();
    
    @Override
    public abstract void close() throws IOException;
    
    public abstract boolean isOpen();
    
    public abstract boolean isReadOnly();
    
    public abstract String getSeparator();
    
    public abstract Iterable<Path> getRootDirectories();
    
    public abstract Iterable<FileStore> getFileStores();
    
    public abstract Set<String> supportedFileAttributeViews();
    
    public abstract Path getPath(final String p0, final String... p1);
    
    public abstract PathMatcher getPathMatcher(final String p0);
    
    public abstract UserPrincipalLookupService getUserPrincipalLookupService();
    
    public abstract WatchService newWatchService() throws IOException;
}
