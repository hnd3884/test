package sun.nio.fs;

import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.nio.file.attribute.GroupPrincipal;
import java.nio.file.attribute.UserPrincipal;
import java.util.NoSuchElementException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Arrays;
import java.nio.file.WatchService;
import java.util.regex.Pattern;
import java.nio.file.PathMatcher;
import java.nio.file.attribute.UserPrincipalLookupService;
import java.util.Iterator;
import java.security.Permission;
import java.nio.file.FileStore;
import java.util.List;
import java.util.Collections;
import java.util.ArrayList;
import java.nio.file.Path;
import java.io.IOException;
import java.nio.file.spi.FileSystemProvider;
import java.security.PrivilegedAction;
import java.security.AccessController;
import sun.security.action.GetPropertyAction;
import java.util.Set;
import java.nio.file.FileSystem;

class WindowsFileSystem extends FileSystem
{
    private final WindowsFileSystemProvider provider;
    private final String defaultDirectory;
    private final String defaultRoot;
    private final boolean supportsLinks;
    private final boolean supportsStreamEnumeration;
    private static final Set<String> supportedFileAttributeViews;
    private static final String GLOB_SYNTAX = "glob";
    private static final String REGEX_SYNTAX = "regex";
    
    WindowsFileSystem(final WindowsFileSystemProvider provider, final String s) {
        this.provider = provider;
        final WindowsPathParser.Result parse = WindowsPathParser.parse(s);
        if (parse.type() != WindowsPathType.ABSOLUTE && parse.type() != WindowsPathType.UNC) {
            throw new AssertionError((Object)"Default directory is not an absolute path");
        }
        this.defaultDirectory = parse.path();
        this.defaultRoot = parse.root();
        final String[] split = Util.split(AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("os.version")), '.');
        final int int1 = Integer.parseInt(split[0]);
        final int int2 = Integer.parseInt(split[1]);
        this.supportsLinks = (int1 >= 6);
        this.supportsStreamEnumeration = (int1 >= 6 || (int1 == 5 && int2 >= 2));
    }
    
    String defaultDirectory() {
        return this.defaultDirectory;
    }
    
    String defaultRoot() {
        return this.defaultRoot;
    }
    
    boolean supportsLinks() {
        return this.supportsLinks;
    }
    
    boolean supportsStreamEnumeration() {
        return this.supportsStreamEnumeration;
    }
    
    @Override
    public FileSystemProvider provider() {
        return this.provider;
    }
    
    @Override
    public String getSeparator() {
        return "\\";
    }
    
    @Override
    public boolean isOpen() {
        return true;
    }
    
    @Override
    public boolean isReadOnly() {
        return false;
    }
    
    @Override
    public void close() throws IOException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public Iterable<Path> getRootDirectories() {
        int getLogicalDrives;
        try {
            getLogicalDrives = WindowsNativeDispatcher.GetLogicalDrives();
        }
        catch (final WindowsException ex) {
            throw new AssertionError((Object)ex.getMessage());
        }
        final ArrayList list = new ArrayList();
        final SecurityManager securityManager = System.getSecurityManager();
        for (int i = 0; i <= 25; ++i) {
            if ((getLogicalDrives & 1 << i) != 0x0) {
                final StringBuilder sb = new StringBuilder(3);
                sb.append((char)(65 + i));
                sb.append(":\\");
                final String string = sb.toString();
                if (securityManager != null) {
                    try {
                        securityManager.checkRead(string);
                    }
                    catch (final SecurityException ex2) {
                        continue;
                    }
                }
                list.add(WindowsPath.createFromNormalizedPath(this, string));
            }
        }
        return (Iterable<Path>)Collections.unmodifiableList((List<?>)list);
    }
    
    @Override
    public Iterable<FileStore> getFileStores() {
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            try {
                securityManager.checkPermission(new RuntimePermission("getFileStoreAttributes"));
            }
            catch (final SecurityException ex) {
                return (Iterable<FileStore>)Collections.emptyList();
            }
        }
        return new Iterable<FileStore>() {
            @Override
            public Iterator<FileStore> iterator() {
                return new FileStoreIterator();
            }
        };
    }
    
    @Override
    public Set<String> supportedFileAttributeViews() {
        return WindowsFileSystem.supportedFileAttributeViews;
    }
    
    @Override
    public final Path getPath(final String s, final String... array) {
        String string;
        if (array.length == 0) {
            string = s;
        }
        else {
            final StringBuilder sb = new StringBuilder();
            sb.append(s);
            for (final String s2 : array) {
                if (s2.length() > 0) {
                    if (sb.length() > 0) {
                        sb.append('\\');
                    }
                    sb.append(s2);
                }
            }
            string = sb.toString();
        }
        return WindowsPath.parse(this, string);
    }
    
    @Override
    public UserPrincipalLookupService getUserPrincipalLookupService() {
        return LookupService.instance;
    }
    
    @Override
    public PathMatcher getPathMatcher(final String s) {
        final int index = s.indexOf(58);
        if (index <= 0 || index == s.length()) {
            throw new IllegalArgumentException();
        }
        final String substring = s.substring(0, index);
        final String substring2 = s.substring(index + 1);
        String windowsRegexPattern;
        if (substring.equals("glob")) {
            windowsRegexPattern = Globs.toWindowsRegexPattern(substring2);
        }
        else {
            if (!substring.equals("regex")) {
                throw new UnsupportedOperationException("Syntax '" + substring + "' not recognized");
            }
            windowsRegexPattern = substring2;
        }
        return new PathMatcher() {
            final /* synthetic */ Pattern val$pattern = Pattern.compile(windowsRegexPattern, 66);
            
            @Override
            public boolean matches(final Path path) {
                return this.val$pattern.matcher(path.toString()).matches();
            }
        };
    }
    
    @Override
    public WatchService newWatchService() throws IOException {
        return new WindowsWatchService(this);
    }
    
    static {
        supportedFileAttributeViews = Collections.unmodifiableSet((Set<? extends String>)new HashSet<String>(Arrays.asList("basic", "dos", "acl", "owner", "user")));
    }
    
    private class FileStoreIterator implements Iterator<FileStore>
    {
        private final Iterator<Path> roots;
        private FileStore next;
        
        FileStoreIterator() {
            this.roots = WindowsFileSystem.this.getRootDirectories().iterator();
        }
        
        private FileStore readNext() {
            assert Thread.holdsLock(this);
            while (this.roots.hasNext()) {
                final WindowsPath windowsPath = this.roots.next();
                try {
                    windowsPath.checkRead();
                }
                catch (final SecurityException ex) {
                    continue;
                }
                try {
                    final WindowsFileStore create = WindowsFileStore.create(windowsPath.toString(), true);
                    if (create != null) {
                        return create;
                    }
                    continue;
                }
                catch (final IOException ex2) {}
            }
            return null;
        }
        
        @Override
        public synchronized boolean hasNext() {
            if (this.next != null) {
                return true;
            }
            this.next = this.readNext();
            return this.next != null;
        }
        
        @Override
        public synchronized FileStore next() {
            if (this.next == null) {
                this.next = this.readNext();
            }
            if (this.next == null) {
                throw new NoSuchElementException();
            }
            final FileStore next = this.next;
            this.next = null;
            return next;
        }
        
        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
    
    private static class LookupService
    {
        static final UserPrincipalLookupService instance;
        
        static {
            instance = new UserPrincipalLookupService() {
                @Override
                public UserPrincipal lookupPrincipalByName(final String s) throws IOException {
                    return WindowsUserPrincipals.lookup(s);
                }
                
                @Override
                public GroupPrincipal lookupPrincipalByGroupName(final String s) throws IOException {
                    final UserPrincipal lookup = WindowsUserPrincipals.lookup(s);
                    if (!(lookup instanceof GroupPrincipal)) {
                        throw new UserPrincipalNotFoundException(s);
                    }
                    return (GroupPrincipal)lookup;
                }
            };
        }
    }
}
