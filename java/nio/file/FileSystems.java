package java.nio.file;

import sun.nio.fs.DefaultFileSystemProvider;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Collections;
import java.util.ServiceLoader;
import java.io.IOException;
import java.util.Map;
import java.util.Iterator;
import java.nio.file.spi.FileSystemProvider;
import java.net.URI;

public final class FileSystems
{
    private FileSystems() {
    }
    
    public static FileSystem getDefault() {
        return DefaultFileSystemHolder.defaultFileSystem;
    }
    
    public static FileSystem getFileSystem(final URI uri) {
        final String scheme = uri.getScheme();
        for (final FileSystemProvider fileSystemProvider : FileSystemProvider.installedProviders()) {
            if (scheme.equalsIgnoreCase(fileSystemProvider.getScheme())) {
                return fileSystemProvider.getFileSystem(uri);
            }
        }
        throw new ProviderNotFoundException("Provider \"" + scheme + "\" not found");
    }
    
    public static FileSystem newFileSystem(final URI uri, final Map<String, ?> map) throws IOException {
        return newFileSystem(uri, map, null);
    }
    
    public static FileSystem newFileSystem(final URI uri, final Map<String, ?> map, final ClassLoader classLoader) throws IOException {
        final String scheme = uri.getScheme();
        for (final FileSystemProvider fileSystemProvider : FileSystemProvider.installedProviders()) {
            if (scheme.equalsIgnoreCase(fileSystemProvider.getScheme())) {
                return fileSystemProvider.newFileSystem(uri, map);
            }
        }
        if (classLoader != null) {
            for (final FileSystemProvider fileSystemProvider2 : ServiceLoader.load(FileSystemProvider.class, classLoader)) {
                if (scheme.equalsIgnoreCase(fileSystemProvider2.getScheme())) {
                    return fileSystemProvider2.newFileSystem(uri, map);
                }
            }
        }
        throw new ProviderNotFoundException("Provider \"" + scheme + "\" not found");
    }
    
    public static FileSystem newFileSystem(final Path path, final ClassLoader classLoader) throws IOException {
        if (path == null) {
            throw new NullPointerException();
        }
        final Map<Object, Object> emptyMap = (Map<Object, Object>)Collections.emptyMap();
        for (final FileSystemProvider fileSystemProvider : FileSystemProvider.installedProviders()) {
            try {
                return fileSystemProvider.newFileSystem(path, (Map<String, ?>)emptyMap);
            }
            catch (final UnsupportedOperationException ex) {
                continue;
            }
            break;
        }
        if (classLoader != null) {
            for (final FileSystemProvider fileSystemProvider2 : ServiceLoader.load(FileSystemProvider.class, classLoader)) {
                try {
                    return fileSystemProvider2.newFileSystem(path, (Map<String, ?>)emptyMap);
                }
                catch (final UnsupportedOperationException ex2) {
                    continue;
                }
                break;
            }
        }
        throw new ProviderNotFoundException("Provider not found");
    }
    
    private static class DefaultFileSystemHolder
    {
        static final FileSystem defaultFileSystem;
        
        private static FileSystem defaultFileSystem() {
            return AccessController.doPrivileged((PrivilegedAction<FileSystemProvider>)new PrivilegedAction<FileSystemProvider>() {
                @Override
                public FileSystemProvider run() {
                    return getDefaultProvider();
                }
            }).getFileSystem(URI.create("file:///"));
        }
        
        private static FileSystemProvider getDefaultProvider() {
            FileSystemProvider create = DefaultFileSystemProvider.create();
            final String property = System.getProperty("java.nio.file.spi.DefaultFileSystemProvider");
            if (property != null) {
                for (final String s : property.split(",")) {
                    try {
                        create = (FileSystemProvider)Class.forName(s, true, ClassLoader.getSystemClassLoader()).getDeclaredConstructor(FileSystemProvider.class).newInstance(create);
                        if (!create.getScheme().equals("file")) {
                            throw new Error("Default provider must use scheme 'file'");
                        }
                    }
                    catch (final Exception ex) {
                        throw new Error(ex);
                    }
                }
            }
            return create;
        }
        
        static {
            defaultFileSystem = defaultFileSystem();
        }
    }
}
