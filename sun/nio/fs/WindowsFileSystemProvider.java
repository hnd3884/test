package sun.nio.fs;

import java.io.FilePermission;
import java.nio.file.LinkPermission;
import java.nio.file.FileAlreadyExistsException;
import java.security.Permission;
import java.nio.file.FileStore;
import java.nio.file.AccessDeniedException;
import java.nio.file.AccessMode;
import java.nio.file.DirectoryStream;
import java.util.Collections;
import java.nio.file.CopyOption;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.channels.SeekableByteChannel;
import java.io.Serializable;
import java.nio.file.attribute.DosFileAttributes;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.UserDefinedFileAttributeView;
import java.nio.file.attribute.FileOwnerAttributeView;
import java.nio.file.attribute.AclFileAttributeView;
import java.nio.file.attribute.DosFileAttributeView;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.FileAttributeView;
import java.nio.file.LinkOption;
import sun.nio.ch.ThreadPool;
import java.nio.channels.AsynchronousFileChannel;
import java.util.concurrent.ExecutorService;
import java.nio.file.ProviderMismatchException;
import java.nio.channels.FileChannel;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.OpenOption;
import java.util.Set;
import java.nio.file.Path;
import java.io.IOException;
import java.nio.file.FileSystemAlreadyExistsException;
import java.nio.file.FileSystem;
import java.util.Map;
import java.net.URI;
import sun.misc.Unsafe;

public class WindowsFileSystemProvider extends AbstractFileSystemProvider
{
    private static final Unsafe unsafe;
    private static final String USER_DIR = "user.dir";
    private final WindowsFileSystem theFileSystem;
    
    public WindowsFileSystemProvider() {
        this.theFileSystem = new WindowsFileSystem(this, System.getProperty("user.dir"));
    }
    
    @Override
    public String getScheme() {
        return "file";
    }
    
    private void checkUri(final URI uri) {
        if (!uri.getScheme().equalsIgnoreCase(this.getScheme())) {
            throw new IllegalArgumentException("URI does not match this provider");
        }
        if (uri.getAuthority() != null) {
            throw new IllegalArgumentException("Authority component present");
        }
        if (uri.getPath() == null) {
            throw new IllegalArgumentException("Path component is undefined");
        }
        if (!uri.getPath().equals("/")) {
            throw new IllegalArgumentException("Path component should be '/'");
        }
        if (uri.getQuery() != null) {
            throw new IllegalArgumentException("Query component present");
        }
        if (uri.getFragment() != null) {
            throw new IllegalArgumentException("Fragment component present");
        }
    }
    
    @Override
    public FileSystem newFileSystem(final URI uri, final Map<String, ?> map) throws IOException {
        this.checkUri(uri);
        throw new FileSystemAlreadyExistsException();
    }
    
    @Override
    public final FileSystem getFileSystem(final URI uri) {
        this.checkUri(uri);
        return this.theFileSystem;
    }
    
    @Override
    public Path getPath(final URI uri) {
        return WindowsUriSupport.fromUri(this.theFileSystem, uri);
    }
    
    @Override
    public FileChannel newFileChannel(final Path path, final Set<? extends OpenOption> set, final FileAttribute<?>... array) throws IOException {
        if (path == null) {
            throw new NullPointerException();
        }
        if (!(path instanceof WindowsPath)) {
            throw new ProviderMismatchException();
        }
        final WindowsPath windowsPath = (WindowsPath)path;
        final WindowsSecurityDescriptor fromAttribute = WindowsSecurityDescriptor.fromAttribute(array);
        try {
            return WindowsChannelFactory.newFileChannel(windowsPath.getPathForWin32Calls(), windowsPath.getPathForPermissionCheck(), set, fromAttribute.address());
        }
        catch (final WindowsException ex) {
            ex.rethrowAsIOException(windowsPath);
            return null;
        }
        finally {
            if (fromAttribute != null) {
                fromAttribute.release();
            }
        }
    }
    
    @Override
    public AsynchronousFileChannel newAsynchronousFileChannel(final Path path, final Set<? extends OpenOption> set, final ExecutorService executorService, final FileAttribute<?>... array) throws IOException {
        if (path == null) {
            throw new NullPointerException();
        }
        if (!(path instanceof WindowsPath)) {
            throw new ProviderMismatchException();
        }
        final WindowsPath windowsPath = (WindowsPath)path;
        final ThreadPool threadPool = (executorService == null) ? null : ThreadPool.wrap(executorService, 0);
        final WindowsSecurityDescriptor fromAttribute = WindowsSecurityDescriptor.fromAttribute(array);
        try {
            return WindowsChannelFactory.newAsynchronousFileChannel(windowsPath.getPathForWin32Calls(), windowsPath.getPathForPermissionCheck(), set, fromAttribute.address(), threadPool);
        }
        catch (final WindowsException ex) {
            ex.rethrowAsIOException(windowsPath);
            return null;
        }
        finally {
            if (fromAttribute != null) {
                fromAttribute.release();
            }
        }
    }
    
    @Override
    public <V extends FileAttributeView> V getFileAttributeView(final Path path, final Class<V> clazz, final LinkOption... array) {
        final WindowsPath windowsPath = WindowsPath.toWindowsPath(path);
        if (clazz == null) {
            throw new NullPointerException();
        }
        final boolean followLinks = Util.followLinks(array);
        if (clazz == BasicFileAttributeView.class) {
            return (V)WindowsFileAttributeViews.createBasicView(windowsPath, followLinks);
        }
        if (clazz == DosFileAttributeView.class) {
            return (V)WindowsFileAttributeViews.createDosView(windowsPath, followLinks);
        }
        if (clazz == AclFileAttributeView.class) {
            return (V)new WindowsAclFileAttributeView(windowsPath, followLinks);
        }
        if (clazz == FileOwnerAttributeView.class) {
            return (V)new FileOwnerAttributeViewImpl(new WindowsAclFileAttributeView(windowsPath, followLinks));
        }
        if (clazz == UserDefinedFileAttributeView.class) {
            return (V)new WindowsUserDefinedFileAttributeView(windowsPath, followLinks);
        }
        return null;
    }
    
    @Override
    public <A extends BasicFileAttributes> A readAttributes(final Path path, final Class<A> clazz, final LinkOption... array) throws IOException {
        Serializable s;
        if (clazz == BasicFileAttributes.class) {
            s = BasicFileAttributeView.class;
        }
        else if (clazz == DosFileAttributes.class) {
            s = DosFileAttributeView.class;
        }
        else {
            if (clazz == null) {
                throw new NullPointerException();
            }
            throw new UnsupportedOperationException();
        }
        return (A)this.getFileAttributeView(path, (Class<BasicFileAttributeView>)s, array).readAttributes();
    }
    
    public DynamicFileAttributeView getFileAttributeView(final Path path, final String s, final LinkOption... array) {
        final WindowsPath windowsPath = WindowsPath.toWindowsPath(path);
        final boolean followLinks = Util.followLinks(array);
        if (s.equals("basic")) {
            return WindowsFileAttributeViews.createBasicView(windowsPath, followLinks);
        }
        if (s.equals("dos")) {
            return WindowsFileAttributeViews.createDosView(windowsPath, followLinks);
        }
        if (s.equals("acl")) {
            return new WindowsAclFileAttributeView(windowsPath, followLinks);
        }
        if (s.equals("owner")) {
            return new FileOwnerAttributeViewImpl(new WindowsAclFileAttributeView(windowsPath, followLinks));
        }
        if (s.equals("user")) {
            return new WindowsUserDefinedFileAttributeView(windowsPath, followLinks);
        }
        return null;
    }
    
    @Override
    public SeekableByteChannel newByteChannel(final Path path, final Set<? extends OpenOption> set, final FileAttribute<?>... array) throws IOException {
        final WindowsPath windowsPath = WindowsPath.toWindowsPath(path);
        final WindowsSecurityDescriptor fromAttribute = WindowsSecurityDescriptor.fromAttribute(array);
        try {
            return WindowsChannelFactory.newFileChannel(windowsPath.getPathForWin32Calls(), windowsPath.getPathForPermissionCheck(), set, fromAttribute.address());
        }
        catch (final WindowsException ex) {
            ex.rethrowAsIOException(windowsPath);
            return null;
        }
        finally {
            fromAttribute.release();
        }
    }
    
    @Override
    boolean implDelete(final Path path, final boolean b) throws IOException {
        final WindowsPath windowsPath = WindowsPath.toWindowsPath(path);
        windowsPath.checkDelete();
        WindowsFileAttributes value = null;
        try {
            value = WindowsFileAttributes.get(windowsPath, false);
            if (value.isDirectory() || value.isDirectoryLink()) {
                WindowsNativeDispatcher.RemoveDirectory(windowsPath.getPathForWin32Calls());
            }
            else {
                WindowsNativeDispatcher.DeleteFile(windowsPath.getPathForWin32Calls());
            }
            return true;
        }
        catch (final WindowsException ex) {
            if (!b && (ex.lastError() == 2 || ex.lastError() == 3)) {
                return false;
            }
            if (value != null && value.isDirectory() && (ex.lastError() == 145 || ex.lastError() == 183)) {
                throw new DirectoryNotEmptyException(windowsPath.getPathForExceptionMessage());
            }
            ex.rethrowAsIOException(windowsPath);
            return false;
        }
    }
    
    @Override
    public void copy(final Path path, final Path path2, final CopyOption... array) throws IOException {
        WindowsFileCopy.copy(WindowsPath.toWindowsPath(path), WindowsPath.toWindowsPath(path2), array);
    }
    
    @Override
    public void move(final Path path, final Path path2, final CopyOption... array) throws IOException {
        WindowsFileCopy.move(WindowsPath.toWindowsPath(path), WindowsPath.toWindowsPath(path2), array);
    }
    
    private static boolean hasDesiredAccess(final WindowsPath windowsPath, final int n) throws IOException {
        boolean checkAccessMask = false;
        final NativeBuffer fileSecurity = WindowsAclFileAttributeView.getFileSecurity(WindowsLinkSupport.getFinalPath(windowsPath, true), 7);
        try {
            checkAccessMask = WindowsSecurity.checkAccessMask(fileSecurity.address(), n, 1179785, 1179926, 1179808, 2032127);
        }
        catch (final WindowsException ex) {
            ex.rethrowAsIOException(windowsPath);
        }
        finally {
            fileSecurity.release();
        }
        return checkAccessMask;
    }
    
    private void checkReadAccess(final WindowsPath windowsPath) throws IOException {
        try {
            WindowsChannelFactory.newFileChannel(windowsPath.getPathForWin32Calls(), windowsPath.getPathForPermissionCheck(), Collections.emptySet(), 0L).close();
        }
        catch (final WindowsException ex) {
            try {
                new WindowsDirectoryStream(windowsPath, null).close();
            }
            catch (final IOException ex2) {
                ex.rethrowAsIOException(windowsPath);
            }
        }
    }
    
    @Override
    public void checkAccess(final Path path, final AccessMode... array) throws IOException {
        final WindowsPath windowsPath = WindowsPath.toWindowsPath(path);
        boolean b = false;
        boolean b2 = false;
        boolean b3 = false;
        for (int length = array.length, i = 0; i < length; ++i) {
            switch (array[i]) {
                case READ: {
                    b = true;
                    break;
                }
                case WRITE: {
                    b2 = true;
                    break;
                }
                case EXECUTE: {
                    b3 = true;
                    break;
                }
                default: {
                    throw new AssertionError((Object)"Should not get here");
                }
            }
        }
        if (!b2 && !b3) {
            this.checkReadAccess(windowsPath);
            return;
        }
        int n = 0;
        if (b) {
            windowsPath.checkRead();
            n |= 0x1;
        }
        if (b2) {
            windowsPath.checkWrite();
            n |= 0x2;
        }
        if (b3) {
            final SecurityManager securityManager = System.getSecurityManager();
            if (securityManager != null) {
                securityManager.checkExec(windowsPath.getPathForPermissionCheck());
            }
            n |= 0x20;
        }
        if (!hasDesiredAccess(windowsPath, n)) {
            throw new AccessDeniedException(windowsPath.getPathForExceptionMessage(), null, "Permissions does not allow requested access");
        }
        if (b2) {
            try {
                final WindowsFileAttributes value = WindowsFileAttributes.get(windowsPath, true);
                if (!value.isDirectory() && value.isReadOnly()) {
                    throw new AccessDeniedException(windowsPath.getPathForExceptionMessage(), null, "DOS readonly attribute is set");
                }
            }
            catch (final WindowsException ex) {
                ex.rethrowAsIOException(windowsPath);
            }
            if (WindowsFileStore.create(windowsPath).isReadOnly()) {
                throw new AccessDeniedException(windowsPath.getPathForExceptionMessage(), null, "Read-only file system");
            }
        }
    }
    
    @Override
    public boolean isSameFile(final Path path, final Path path2) throws IOException {
        final WindowsPath windowsPath = WindowsPath.toWindowsPath(path);
        if (windowsPath.equals(path2)) {
            return true;
        }
        if (path2 == null) {
            throw new NullPointerException();
        }
        if (!(path2 instanceof WindowsPath)) {
            return false;
        }
        final WindowsPath windowsPath2 = (WindowsPath)path2;
        windowsPath.checkRead();
        windowsPath2.checkRead();
        long openForReadAttributeAccess = 0L;
        try {
            openForReadAttributeAccess = windowsPath.openForReadAttributeAccess(true);
        }
        catch (final WindowsException ex) {
            ex.rethrowAsIOException(windowsPath);
        }
        try {
            WindowsFileAttributes attributes = null;
            try {
                attributes = WindowsFileAttributes.readAttributes(openForReadAttributeAccess);
            }
            catch (final WindowsException ex2) {
                ex2.rethrowAsIOException(windowsPath);
            }
            long openForReadAttributeAccess2 = 0L;
            try {
                openForReadAttributeAccess2 = windowsPath2.openForReadAttributeAccess(true);
            }
            catch (final WindowsException ex3) {
                ex3.rethrowAsIOException(windowsPath2);
            }
            try {
                WindowsFileAttributes attributes2 = null;
                try {
                    attributes2 = WindowsFileAttributes.readAttributes(openForReadAttributeAccess2);
                }
                catch (final WindowsException ex4) {
                    ex4.rethrowAsIOException(windowsPath2);
                }
                return WindowsFileAttributes.isSameFile(attributes, attributes2);
            }
            finally {
                WindowsNativeDispatcher.CloseHandle(openForReadAttributeAccess2);
            }
        }
        finally {
            WindowsNativeDispatcher.CloseHandle(openForReadAttributeAccess);
        }
    }
    
    @Override
    public boolean isHidden(final Path path) throws IOException {
        final WindowsPath windowsPath = WindowsPath.toWindowsPath(path);
        windowsPath.checkRead();
        WindowsFileAttributes value = null;
        try {
            value = WindowsFileAttributes.get(windowsPath, true);
        }
        catch (final WindowsException ex) {
            ex.rethrowAsIOException(windowsPath);
        }
        return !value.isDirectory() && value.isHidden();
    }
    
    @Override
    public FileStore getFileStore(final Path path) throws IOException {
        final WindowsPath windowsPath = WindowsPath.toWindowsPath(path);
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            securityManager.checkPermission(new RuntimePermission("getFileStoreAttributes"));
            windowsPath.checkRead();
        }
        return WindowsFileStore.create(windowsPath);
    }
    
    @Override
    public void createDirectory(final Path path, final FileAttribute<?>... array) throws IOException {
        final WindowsPath windowsPath = WindowsPath.toWindowsPath(path);
        windowsPath.checkWrite();
        final WindowsSecurityDescriptor fromAttribute = WindowsSecurityDescriptor.fromAttribute(array);
        try {
            WindowsNativeDispatcher.CreateDirectory(windowsPath.getPathForWin32Calls(), fromAttribute.address());
        }
        catch (final WindowsException ex) {
            if (ex.lastError() == 5) {
                try {
                    if (WindowsFileAttributes.get(windowsPath, false).isDirectory()) {
                        throw new FileAlreadyExistsException(windowsPath.toString());
                    }
                }
                catch (final WindowsException ex2) {}
            }
            ex.rethrowAsIOException(windowsPath);
        }
        finally {
            fromAttribute.release();
        }
    }
    
    @Override
    public DirectoryStream<Path> newDirectoryStream(final Path path, final DirectoryStream.Filter<? super Path> filter) throws IOException {
        final WindowsPath windowsPath = WindowsPath.toWindowsPath(path);
        windowsPath.checkRead();
        if (filter == null) {
            throw new NullPointerException();
        }
        return new WindowsDirectoryStream(windowsPath, filter);
    }
    
    @Override
    public void createSymbolicLink(final Path path, final Path path2, final FileAttribute<?>... array) throws IOException {
        final WindowsPath windowsPath = WindowsPath.toWindowsPath(path);
        final WindowsPath windowsPath2 = WindowsPath.toWindowsPath(path2);
        if (!windowsPath.getFileSystem().supportsLinks()) {
            throw new UnsupportedOperationException("Symbolic links not supported on this operating system");
        }
        if (array.length > 0) {
            WindowsSecurityDescriptor.fromAttribute(array);
            throw new UnsupportedOperationException("Initial file attributesnot supported when creating symbolic link");
        }
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            securityManager.checkPermission(new LinkPermission("symbolic"));
            windowsPath.checkWrite();
        }
        if (windowsPath2.type() == WindowsPathType.DRIVE_RELATIVE) {
            throw new IOException("Cannot create symbolic link to working directory relative target");
        }
        WindowsPath resolve;
        if (windowsPath2.type() == WindowsPathType.RELATIVE) {
            final WindowsPath parent = windowsPath.getParent();
            resolve = ((parent == null) ? windowsPath2 : parent.resolve(windowsPath2));
        }
        else {
            resolve = windowsPath.resolve(windowsPath2);
        }
        int n = 0;
        try {
            final WindowsFileAttributes value = WindowsFileAttributes.get(resolve, false);
            if (value.isDirectory() || value.isDirectoryLink()) {
                n |= 0x1;
            }
        }
        catch (final WindowsException ex) {}
        try {
            WindowsNativeDispatcher.CreateSymbolicLink(windowsPath.getPathForWin32Calls(), WindowsPath.addPrefixIfNeeded(windowsPath2.toString()), n);
        }
        catch (final WindowsException ex2) {
            if (ex2.lastError() == 4392) {
                ex2.rethrowAsIOException(windowsPath, windowsPath2);
            }
            else {
                ex2.rethrowAsIOException(windowsPath);
            }
        }
    }
    
    @Override
    public void createLink(final Path path, final Path path2) throws IOException {
        final WindowsPath windowsPath = WindowsPath.toWindowsPath(path);
        final WindowsPath windowsPath2 = WindowsPath.toWindowsPath(path2);
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            securityManager.checkPermission(new LinkPermission("hard"));
            windowsPath.checkWrite();
            windowsPath2.checkWrite();
        }
        try {
            WindowsNativeDispatcher.CreateHardLink(windowsPath.getPathForWin32Calls(), windowsPath2.getPathForWin32Calls());
        }
        catch (final WindowsException ex) {
            ex.rethrowAsIOException(windowsPath, windowsPath2);
        }
    }
    
    @Override
    public Path readSymbolicLink(final Path path) throws IOException {
        final WindowsPath windowsPath = WindowsPath.toWindowsPath(path);
        final WindowsFileSystem fileSystem = windowsPath.getFileSystem();
        if (!fileSystem.supportsLinks()) {
            throw new UnsupportedOperationException("symbolic links not supported");
        }
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            securityManager.checkPermission(new FilePermission(windowsPath.getPathForPermissionCheck(), "readlink"));
        }
        return WindowsPath.createFromNormalizedPath(fileSystem, WindowsLinkSupport.readLink(windowsPath));
    }
    
    static {
        unsafe = Unsafe.getUnsafe();
    }
}
