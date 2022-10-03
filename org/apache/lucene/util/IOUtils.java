package org.apache.lucene.util;

import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.FileStore;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.store.FileSwitchDirectory;
import org.apache.lucene.store.FilterDirectory;
import java.nio.channels.FileChannel;
import java.nio.file.StandardOpenOption;
import java.nio.file.OpenOption;
import java.nio.file.FileVisitResult;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.FileVisitor;
import java.nio.file.LinkOption;
import java.util.Map;
import java.util.LinkedHashMap;
import java.nio.file.Files;
import java.util.Collection;
import java.nio.file.Path;
import org.apache.lucene.store.Directory;
import java.nio.charset.CharsetDecoder;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.CodingErrorAction;
import java.io.Reader;
import java.io.InputStream;
import java.util.Iterator;
import java.io.IOException;
import java.util.Arrays;
import java.io.Closeable;
import java.nio.charset.Charset;

public final class IOUtils
{
    @Deprecated
    public static final Charset CHARSET_UTF_8;
    public static final String UTF_8;
    
    private IOUtils() {
    }
    
    public static void close(final Closeable... objects) throws IOException {
        close(Arrays.asList(objects));
    }
    
    public static void close(final Iterable<? extends Closeable> objects) throws IOException {
        Throwable th = null;
        for (final Closeable object : objects) {
            try {
                if (object == null) {
                    continue;
                }
                object.close();
            }
            catch (final Throwable t) {
                addSuppressed(th, t);
                if (th != null) {
                    continue;
                }
                th = t;
            }
        }
        reThrow(th);
    }
    
    public static void closeWhileHandlingException(final Closeable... objects) {
        closeWhileHandlingException(Arrays.asList(objects));
    }
    
    public static void closeWhileHandlingException(final Iterable<? extends Closeable> objects) {
        for (final Closeable object : objects) {
            try {
                if (object == null) {
                    continue;
                }
                object.close();
            }
            catch (final Throwable t) {}
        }
    }
    
    private static void addSuppressed(final Throwable exception, final Throwable suppressed) {
        if (exception != null && suppressed != null) {
            exception.addSuppressed(suppressed);
        }
    }
    
    public static Reader getDecodingReader(final InputStream stream, final Charset charSet) {
        final CharsetDecoder charSetDecoder = charSet.newDecoder().onMalformedInput(CodingErrorAction.REPORT).onUnmappableCharacter(CodingErrorAction.REPORT);
        return new BufferedReader(new InputStreamReader(stream, charSetDecoder));
    }
    
    public static Reader getDecodingReader(final Class<?> clazz, final String resource, final Charset charSet) throws IOException {
        InputStream stream = null;
        boolean success = false;
        try {
            stream = clazz.getResourceAsStream(resource);
            final Reader reader = getDecodingReader(stream, charSet);
            success = true;
            return reader;
        }
        finally {
            if (!success) {
                close(stream);
            }
        }
    }
    
    public static void deleteFilesIgnoringExceptions(final Directory dir, final String... files) {
        for (final String name : files) {
            try {
                dir.deleteFile(name);
            }
            catch (final Throwable t) {}
        }
    }
    
    public static void deleteFilesIgnoringExceptions(final Path... files) {
        deleteFilesIgnoringExceptions(Arrays.asList(files));
    }
    
    public static void deleteFilesIgnoringExceptions(final Collection<? extends Path> files) {
        for (final Path name : files) {
            if (name != null) {
                try {
                    Files.delete(name);
                }
                catch (final Throwable t) {}
            }
        }
    }
    
    public static void deleteFilesIfExist(final Path... files) throws IOException {
        deleteFilesIfExist(Arrays.asList(files));
    }
    
    public static void deleteFilesIfExist(final Collection<? extends Path> files) throws IOException {
        Throwable th = null;
        for (final Path file : files) {
            try {
                if (file == null) {
                    continue;
                }
                Files.deleteIfExists(file);
            }
            catch (final Throwable t) {
                addSuppressed(th, t);
                if (th != null) {
                    continue;
                }
                th = t;
            }
        }
        reThrow(th);
    }
    
    public static void rm(final Path... locations) throws IOException {
        final LinkedHashMap<Path, Throwable> unremoved = rm(new LinkedHashMap<Path, Throwable>(), locations);
        if (!unremoved.isEmpty()) {
            final StringBuilder b = new StringBuilder("Could not remove the following files (in the order of attempts):\n");
            for (final Map.Entry<Path, Throwable> kv : unremoved.entrySet()) {
                b.append("   ").append(kv.getKey().toAbsolutePath()).append(": ").append(kv.getValue()).append("\n");
            }
            throw new IOException(b.toString());
        }
    }
    
    private static LinkedHashMap<Path, Throwable> rm(final LinkedHashMap<Path, Throwable> unremoved, final Path... locations) {
        if (locations != null) {
            for (final Path location : locations) {
                if (location != null && Files.exists(location, new LinkOption[0])) {
                    try {
                        Files.walkFileTree(location, new FileVisitor<Path>() {
                            @Override
                            public FileVisitResult preVisitDirectory(final Path dir, final BasicFileAttributes attrs) throws IOException {
                                return FileVisitResult.CONTINUE;
                            }
                            
                            @Override
                            public FileVisitResult postVisitDirectory(final Path dir, final IOException impossible) throws IOException {
                                assert impossible == null;
                                try {
                                    Files.delete(dir);
                                }
                                catch (final IOException e) {
                                    unremoved.put(dir, e);
                                }
                                return FileVisitResult.CONTINUE;
                            }
                            
                            @Override
                            public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException {
                                try {
                                    Files.delete(file);
                                }
                                catch (final IOException exc) {
                                    unremoved.put(file, exc);
                                }
                                return FileVisitResult.CONTINUE;
                            }
                            
                            @Override
                            public FileVisitResult visitFileFailed(final Path file, final IOException exc) throws IOException {
                                if (exc != null) {
                                    unremoved.put(file, exc);
                                }
                                return FileVisitResult.CONTINUE;
                            }
                        });
                    }
                    catch (final IOException impossible) {
                        throw new AssertionError("visitor threw exception", impossible);
                    }
                }
            }
        }
        return unremoved;
    }
    
    public static void reThrow(final Throwable th) throws IOException {
        if (th != null) {
            if (th instanceof IOException) {
                throw (IOException)th;
            }
            reThrowUnchecked(th);
        }
    }
    
    public static void reThrowUnchecked(final Throwable th) {
        if (th == null) {
            return;
        }
        if (th instanceof RuntimeException) {
            throw (RuntimeException)th;
        }
        if (th instanceof Error) {
            throw (Error)th;
        }
        throw new RuntimeException(th);
    }
    
    public static void fsync(final Path fileToSync, final boolean isDir) throws IOException {
        try (final FileChannel file = FileChannel.open(fileToSync, isDir ? StandardOpenOption.READ : StandardOpenOption.WRITE)) {
            file.force(true);
        }
        catch (final IOException ioe) {
            if (!isDir) {
                throw ioe;
            }
            assert !Constants.LINUX && !Constants.MAC_OS_X : "On Linux and MacOSX fsyncing a directory should not throw IOException, we just don't want to rely on that in production (undocumented). Got: " + ioe;
        }
    }
    
    public static boolean spins(Directory dir) throws IOException {
        dir = FilterDirectory.unwrap(dir);
        if (dir instanceof FileSwitchDirectory) {
            final FileSwitchDirectory fsd = (FileSwitchDirectory)dir;
            return spins(fsd.getPrimaryDir()) || spins(fsd.getSecondaryDir());
        }
        return !(dir instanceof RAMDirectory) && (!(dir instanceof FSDirectory) || spins(((FSDirectory)dir).getDirectory()));
    }
    
    public static boolean spins(Path path) throws IOException {
        path = path.toRealPath(new LinkOption[0]);
        if (!Constants.LINUX) {
            return true;
        }
        try {
            return spinsLinux(path);
        }
        catch (final Exception exc) {
            return true;
        }
    }
    
    static boolean spinsLinux(final Path path) throws IOException {
        final FileStore store = getFileStore(path);
        if ("tmpfs".equals(store.type())) {
            return false;
        }
        String devName = store.name();
        if (!devName.startsWith("/")) {
            return true;
        }
        devName = path.getRoot().resolve(devName).toRealPath(new LinkOption[0]).getFileName().toString();
        final Path sysinfo = path.getRoot().resolve("sys").resolve("block");
        Path devsysinfo = null;
        int matchlen = 0;
        try (final DirectoryStream<Path> stream = Files.newDirectoryStream(sysinfo)) {
            for (final Path device : stream) {
                final String name = device.getFileName().toString();
                if (name.length() > matchlen && devName.startsWith(name)) {
                    devsysinfo = device;
                    matchlen = name.length();
                }
            }
        }
        if (devsysinfo == null) {
            return true;
        }
        final Path rotational = devsysinfo.resolve("queue").resolve("rotational");
        try (final InputStream stream2 = Files.newInputStream(rotational, new OpenOption[0])) {
            return stream2.read() == 49;
        }
    }
    
    static FileStore getFileStore(final Path path) throws IOException {
        final FileStore store = Files.getFileStore(path);
        final String mount = getMountPoint(store);
        FileStore sameMountPoint = null;
        for (final FileStore fs : path.getFileSystem().getFileStores()) {
            if (mount.equals(getMountPoint(fs))) {
                if (sameMountPoint != null) {
                    return store;
                }
                sameMountPoint = fs;
            }
        }
        if (sameMountPoint != null) {
            return sameMountPoint;
        }
        return store;
    }
    
    static String getMountPoint(final FileStore store) {
        final String desc = store.toString();
        final int index = desc.lastIndexOf(" (");
        if (index != -1) {
            return desc.substring(0, index);
        }
        return desc;
    }
    
    static {
        CHARSET_UTF_8 = StandardCharsets.UTF_8;
        UTF_8 = StandardCharsets.UTF_8.name();
    }
}
