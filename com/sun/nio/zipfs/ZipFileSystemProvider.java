package com.sun.nio.zipfs;

import java.io.OutputStream;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.nio.file.DirectoryStream;
import java.nio.channels.SeekableByteChannel;
import java.nio.channels.AsynchronousFileChannel;
import java.util.concurrent.ExecutorService;
import java.nio.file.OpenOption;
import java.util.Set;
import java.nio.file.FileStore;
import java.nio.file.attribute.FileAttributeView;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.CopyOption;
import java.nio.file.AccessMode;
import java.nio.file.ProviderMismatchException;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.FileSystems;
import java.util.zip.ZipError;
import java.nio.file.FileSystemAlreadyExistsException;
import java.nio.file.FileSystem;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.net.URI;
import java.util.HashMap;
import java.nio.file.Path;
import java.util.Map;
import java.nio.file.spi.FileSystemProvider;

public class ZipFileSystemProvider extends FileSystemProvider
{
    private final Map<Path, ZipFileSystem> filesystems;
    
    public ZipFileSystemProvider() {
        this.filesystems = new HashMap<Path, ZipFileSystem>();
    }
    
    @Override
    public String getScheme() {
        return "jar";
    }
    
    protected Path uriToPath(final URI uri) {
        final String scheme = uri.getScheme();
        if (scheme == null || !scheme.equalsIgnoreCase(this.getScheme())) {
            throw new IllegalArgumentException("URI scheme is not '" + this.getScheme() + "'");
        }
        try {
            String s = uri.getRawSchemeSpecificPart();
            final int index = s.indexOf("!/");
            if (index != -1) {
                s = s.substring(0, index);
            }
            return Paths.get(new URI(s)).toAbsolutePath();
        }
        catch (final URISyntaxException ex) {
            throw new IllegalArgumentException(ex.getMessage(), ex);
        }
    }
    
    private boolean ensureFile(final Path path) {
        try {
            if (!Files.readAttributes(path, BasicFileAttributes.class, new LinkOption[0]).isRegularFile()) {
                throw new UnsupportedOperationException();
            }
            return true;
        }
        catch (final IOException ex) {
            return false;
        }
    }
    
    @Override
    public FileSystem newFileSystem(final URI uri, final Map<String, ?> map) throws IOException {
        final Path uriToPath = this.uriToPath(uri);
        synchronized (this.filesystems) {
            Path realPath = null;
            if (this.ensureFile(uriToPath)) {
                realPath = uriToPath.toRealPath(new LinkOption[0]);
                if (this.filesystems.containsKey(realPath)) {
                    throw new FileSystemAlreadyExistsException();
                }
            }
            ZipFileSystem zipFileSystem;
            try {
                zipFileSystem = new ZipFileSystem(this, uriToPath, map);
            }
            catch (final ZipError zipError) {
                final String string = uriToPath.toString();
                if (string.endsWith(".zip") || string.endsWith(".jar")) {
                    throw zipError;
                }
                throw new UnsupportedOperationException();
            }
            this.filesystems.put(realPath, zipFileSystem);
            return zipFileSystem;
        }
    }
    
    @Override
    public FileSystem newFileSystem(final Path path, final Map<String, ?> map) throws IOException {
        if (path.getFileSystem() != FileSystems.getDefault()) {
            throw new UnsupportedOperationException();
        }
        this.ensureFile(path);
        try {
            return new ZipFileSystem(this, path, map);
        }
        catch (final ZipError zipError) {
            final String string = path.toString();
            if (string.endsWith(".zip") || string.endsWith(".jar")) {
                throw zipError;
            }
            throw new UnsupportedOperationException();
        }
    }
    
    @Override
    public Path getPath(final URI uri) {
        final String schemeSpecificPart = uri.getSchemeSpecificPart();
        final int index = schemeSpecificPart.indexOf("!/");
        if (index == -1) {
            throw new IllegalArgumentException("URI: " + uri + " does not contain path info ex. jar:file:/c:/foo.zip!/BAR");
        }
        return this.getFileSystem(uri).getPath(schemeSpecificPart.substring(index + 1), new String[0]);
    }
    
    @Override
    public FileSystem getFileSystem(final URI uri) {
        synchronized (this.filesystems) {
            FileSystem fileSystem = null;
            try {
                fileSystem = this.filesystems.get(this.uriToPath(uri).toRealPath(new LinkOption[0]));
            }
            catch (final IOException ex) {}
            if (fileSystem == null) {
                throw new FileSystemNotFoundException();
            }
            return fileSystem;
        }
    }
    
    static final ZipPath toZipPath(final Path path) {
        if (path == null) {
            throw new NullPointerException();
        }
        if (!(path instanceof ZipPath)) {
            throw new ProviderMismatchException();
        }
        return (ZipPath)path;
    }
    
    @Override
    public void checkAccess(final Path path, final AccessMode... array) throws IOException {
        toZipPath(path).checkAccess(array);
    }
    
    @Override
    public void copy(final Path path, final Path path2, final CopyOption... array) throws IOException {
        toZipPath(path).copy(toZipPath(path2), array);
    }
    
    @Override
    public void createDirectory(final Path path, final FileAttribute<?>... array) throws IOException {
        toZipPath(path).createDirectory(array);
    }
    
    @Override
    public final void delete(final Path path) throws IOException {
        toZipPath(path).delete();
    }
    
    @Override
    public <V extends FileAttributeView> V getFileAttributeView(final Path path, final Class<V> clazz, final LinkOption... array) {
        return ZipFileAttributeView.get(toZipPath(path), clazz);
    }
    
    @Override
    public FileStore getFileStore(final Path path) throws IOException {
        return toZipPath(path).getFileStore();
    }
    
    @Override
    public boolean isHidden(final Path path) {
        return toZipPath(path).isHidden();
    }
    
    @Override
    public boolean isSameFile(final Path path, final Path path2) throws IOException {
        return toZipPath(path).isSameFile(path2);
    }
    
    @Override
    public void move(final Path path, final Path path2, final CopyOption... array) throws IOException {
        toZipPath(path).move(toZipPath(path2), array);
    }
    
    @Override
    public AsynchronousFileChannel newAsynchronousFileChannel(final Path path, final Set<? extends OpenOption> set, final ExecutorService executorService, final FileAttribute<?>... array) throws IOException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public SeekableByteChannel newByteChannel(final Path path, final Set<? extends OpenOption> set, final FileAttribute<?>... array) throws IOException {
        return toZipPath(path).newByteChannel(set, array);
    }
    
    @Override
    public DirectoryStream<Path> newDirectoryStream(final Path path, final DirectoryStream.Filter<? super Path> filter) throws IOException {
        return toZipPath(path).newDirectoryStream(filter);
    }
    
    @Override
    public FileChannel newFileChannel(final Path path, final Set<? extends OpenOption> set, final FileAttribute<?>... array) throws IOException {
        return toZipPath(path).newFileChannel(set, array);
    }
    
    @Override
    public InputStream newInputStream(final Path path, final OpenOption... array) throws IOException {
        return toZipPath(path).newInputStream(array);
    }
    
    @Override
    public OutputStream newOutputStream(final Path path, final OpenOption... array) throws IOException {
        return toZipPath(path).newOutputStream(array);
    }
    
    @Override
    public <A extends BasicFileAttributes> A readAttributes(final Path path, final Class<A> clazz, final LinkOption... array) throws IOException {
        if (clazz == BasicFileAttributes.class || clazz == ZipFileAttributes.class) {
            return (A)toZipPath(path).getAttributes();
        }
        return null;
    }
    
    @Override
    public Map<String, Object> readAttributes(final Path path, final String s, final LinkOption... array) throws IOException {
        return toZipPath(path).readAttributes(s, array);
    }
    
    @Override
    public Path readSymbolicLink(final Path path) throws IOException {
        throw new UnsupportedOperationException("Not supported.");
    }
    
    @Override
    public void setAttribute(final Path path, final String s, final Object o, final LinkOption... array) throws IOException {
        toZipPath(path).setAttribute(s, o, array);
    }
    
    void removeFileSystem(Path realPath, final ZipFileSystem zipFileSystem) throws IOException {
        synchronized (this.filesystems) {
            realPath = realPath.toRealPath(new LinkOption[0]);
            if (this.filesystems.get(realPath) == zipFileSystem) {
                this.filesystems.remove(realPath);
            }
        }
    }
}
