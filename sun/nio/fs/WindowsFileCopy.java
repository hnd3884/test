package sun.nio.fs;

import java.nio.file.AtomicMoveNotSupportedException;
import java.util.concurrent.ExecutionException;
import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.security.Permission;
import java.nio.file.LinkPermission;
import java.nio.file.FileAlreadyExistsException;
import com.sun.nio.file.ExtendedCopyOption;
import java.nio.file.LinkOption;
import java.nio.file.StandardCopyOption;
import java.nio.file.CopyOption;

class WindowsFileCopy
{
    private WindowsFileCopy() {
    }
    
    static void copy(final WindowsPath windowsPath, final WindowsPath windowsPath2, final CopyOption... array) throws IOException {
        boolean b = false;
        boolean b2 = false;
        boolean b3 = true;
        boolean b4 = false;
        for (final CopyOption copyOption : array) {
            if (copyOption == StandardCopyOption.REPLACE_EXISTING) {
                b = true;
            }
            else if (copyOption == LinkOption.NOFOLLOW_LINKS) {
                b3 = false;
            }
            else if (copyOption == StandardCopyOption.COPY_ATTRIBUTES) {
                b2 = true;
            }
            else if (copyOption == ExtendedCopyOption.INTERRUPTIBLE) {
                b4 = true;
            }
            else {
                if (copyOption == null) {
                    throw new NullPointerException();
                }
                throw new UnsupportedOperationException("Unsupported copy option");
            }
        }
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            windowsPath.checkRead();
            windowsPath2.checkWrite();
        }
        WindowsFileAttributes attributes = null;
        WindowsFileAttributes attributes2 = null;
        long openForReadAttributeAccess = 0L;
        try {
            openForReadAttributeAccess = windowsPath.openForReadAttributeAccess(b3);
        }
        catch (final WindowsException ex) {
            ex.rethrowAsIOException(windowsPath);
        }
        try {
            try {
                attributes = WindowsFileAttributes.readAttributes(openForReadAttributeAccess);
            }
            catch (final WindowsException ex2) {
                ex2.rethrowAsIOException(windowsPath);
            }
            try {
                final long openForReadAttributeAccess2 = windowsPath2.openForReadAttributeAccess(false);
                try {
                    attributes2 = WindowsFileAttributes.readAttributes(openForReadAttributeAccess2);
                    if (WindowsFileAttributes.isSameFile(attributes, attributes2)) {
                        return;
                    }
                    if (!b) {
                        throw new FileAlreadyExistsException(windowsPath2.getPathForExceptionMessage());
                    }
                }
                finally {
                    WindowsNativeDispatcher.CloseHandle(openForReadAttributeAccess2);
                }
            }
            catch (final WindowsException ex3) {}
        }
        finally {
            WindowsNativeDispatcher.CloseHandle(openForReadAttributeAccess);
        }
        if (securityManager != null && attributes.isSymbolicLink()) {
            securityManager.checkPermission(new LinkPermission("symbolic"));
        }
        final String win32Path = asWin32Path(windowsPath);
        final String win32Path2 = asWin32Path(windowsPath2);
        if (attributes2 != null) {
            try {
                if (attributes2.isDirectory() || attributes2.isDirectoryLink()) {
                    WindowsNativeDispatcher.RemoveDirectory(win32Path2);
                }
                else {
                    WindowsNativeDispatcher.DeleteFile(win32Path2);
                }
            }
            catch (final WindowsException ex4) {
                if (attributes2.isDirectory() && (ex4.lastError() == 145 || ex4.lastError() == 183)) {
                    throw new DirectoryNotEmptyException(windowsPath2.getPathForExceptionMessage());
                }
                ex4.rethrowAsIOException(windowsPath2);
            }
        }
        if (!attributes.isDirectory() && !attributes.isDirectoryLink()) {
            final int n = (windowsPath.getFileSystem().supportsLinks() && !b3) ? 2048 : 0;
            if (b4) {
                final Cancellable cancellable = new Cancellable() {
                    public int cancelValue() {
                        return 1;
                    }
                    
                    public void implRun() throws IOException {
                        try {
                            WindowsNativeDispatcher.CopyFileEx(win32Path, win32Path2, n, this.addressToPollForCancel());
                        }
                        catch (final WindowsException ex) {
                            ex.rethrowAsIOException(windowsPath, windowsPath2);
                        }
                    }
                };
                try {
                    Cancellable.runInterruptibly(cancellable);
                }
                catch (final ExecutionException ex5) {
                    final Throwable cause = ex5.getCause();
                    if (cause instanceof IOException) {
                        throw (IOException)cause;
                    }
                    throw new IOException(cause);
                }
            }
            else {
                try {
                    WindowsNativeDispatcher.CopyFileEx(win32Path, win32Path2, n, 0L);
                }
                catch (final WindowsException ex6) {
                    ex6.rethrowAsIOException(windowsPath, windowsPath2);
                }
            }
            if (b2) {
                try {
                    copySecurityAttributes(windowsPath, windowsPath2, b3);
                }
                catch (final IOException ex7) {}
            }
            return;
        }
        try {
            if (attributes.isDirectory()) {
                WindowsNativeDispatcher.CreateDirectory(win32Path2, 0L);
            }
            else {
                WindowsNativeDispatcher.CreateSymbolicLink(win32Path2, WindowsPath.addPrefixIfNeeded(WindowsLinkSupport.readLink(windowsPath)), 1);
            }
        }
        catch (final WindowsException ex8) {
            ex8.rethrowAsIOException(windowsPath2);
        }
        if (b2) {
            final WindowsFileAttributeViews.Dos dosView = WindowsFileAttributeViews.createDosView(windowsPath2, false);
            try {
                dosView.setAttributes(attributes);
            }
            catch (final IOException ex9) {
                if (attributes.isDirectory()) {
                    try {
                        WindowsNativeDispatcher.RemoveDirectory(win32Path2);
                    }
                    catch (final WindowsException ex10) {}
                }
            }
            try {
                copySecurityAttributes(windowsPath, windowsPath2, b3);
            }
            catch (final IOException ex11) {}
        }
    }
    
    static void move(final WindowsPath windowsPath, final WindowsPath windowsPath2, final CopyOption... array) throws IOException {
        boolean b = false;
        boolean b2 = false;
        for (final CopyOption copyOption : array) {
            if (copyOption == StandardCopyOption.ATOMIC_MOVE) {
                b = true;
            }
            else if (copyOption == StandardCopyOption.REPLACE_EXISTING) {
                b2 = true;
            }
            else if (copyOption != LinkOption.NOFOLLOW_LINKS) {
                if (copyOption == null) {
                    throw new NullPointerException();
                }
                throw new UnsupportedOperationException("Unsupported copy option");
            }
        }
        if (System.getSecurityManager() != null) {
            windowsPath.checkWrite();
            windowsPath2.checkWrite();
        }
        final String win32Path = asWin32Path(windowsPath);
        final String win32Path2 = asWin32Path(windowsPath2);
        if (b) {
            try {
                WindowsNativeDispatcher.MoveFileEx(win32Path, win32Path2, 1);
            }
            catch (final WindowsException ex) {
                if (ex.lastError() == 17) {
                    throw new AtomicMoveNotSupportedException(windowsPath.getPathForExceptionMessage(), windowsPath2.getPathForExceptionMessage(), ex.errorString());
                }
                ex.rethrowAsIOException(windowsPath, windowsPath2);
            }
            return;
        }
        WindowsFileAttributes attributes = null;
        WindowsFileAttributes attributes2 = null;
        long openForReadAttributeAccess = 0L;
        try {
            openForReadAttributeAccess = windowsPath.openForReadAttributeAccess(false);
        }
        catch (final WindowsException ex2) {
            ex2.rethrowAsIOException(windowsPath);
        }
        try {
            try {
                attributes = WindowsFileAttributes.readAttributes(openForReadAttributeAccess);
            }
            catch (final WindowsException ex3) {
                ex3.rethrowAsIOException(windowsPath);
            }
            try {
                final long openForReadAttributeAccess2 = windowsPath2.openForReadAttributeAccess(false);
                try {
                    attributes2 = WindowsFileAttributes.readAttributes(openForReadAttributeAccess2);
                    if (WindowsFileAttributes.isSameFile(attributes, attributes2)) {
                        return;
                    }
                    if (!b2) {
                        throw new FileAlreadyExistsException(windowsPath2.getPathForExceptionMessage());
                    }
                }
                finally {
                    WindowsNativeDispatcher.CloseHandle(openForReadAttributeAccess2);
                }
            }
            catch (final WindowsException ex4) {}
        }
        finally {
            WindowsNativeDispatcher.CloseHandle(openForReadAttributeAccess);
        }
        if (attributes2 != null) {
            try {
                if (attributes2.isDirectory() || attributes2.isDirectoryLink()) {
                    WindowsNativeDispatcher.RemoveDirectory(win32Path2);
                }
                else {
                    WindowsNativeDispatcher.DeleteFile(win32Path2);
                }
            }
            catch (final WindowsException ex5) {
                if (attributes2.isDirectory() && (ex5.lastError() == 145 || ex5.lastError() == 183)) {
                    throw new DirectoryNotEmptyException(windowsPath2.getPathForExceptionMessage());
                }
                ex5.rethrowAsIOException(windowsPath2);
            }
        }
        try {
            WindowsNativeDispatcher.MoveFileEx(win32Path, win32Path2, 0);
        }
        catch (final WindowsException ex6) {
            if (ex6.lastError() != 17) {
                ex6.rethrowAsIOException(windowsPath, windowsPath2);
            }
            if (!attributes.isDirectory() && !attributes.isDirectoryLink()) {
                try {
                    WindowsNativeDispatcher.MoveFileEx(win32Path, win32Path2, 2);
                }
                catch (final WindowsException ex7) {
                    ex7.rethrowAsIOException(windowsPath, windowsPath2);
                }
                try {
                    copySecurityAttributes(windowsPath, windowsPath2, false);
                }
                catch (final IOException ex8) {}
                return;
            }
            assert attributes.isDirectory() || attributes.isDirectoryLink();
            try {
                if (attributes.isDirectory()) {
                    WindowsNativeDispatcher.CreateDirectory(win32Path2, 0L);
                }
                else {
                    WindowsNativeDispatcher.CreateSymbolicLink(win32Path2, WindowsPath.addPrefixIfNeeded(WindowsLinkSupport.readLink(windowsPath)), 1);
                }
            }
            catch (final WindowsException ex9) {
                ex9.rethrowAsIOException(windowsPath2);
            }
            final WindowsFileAttributeViews.Dos dosView = WindowsFileAttributeViews.createDosView(windowsPath2, false);
            try {
                dosView.setAttributes(attributes);
            }
            catch (final IOException ex10) {
                try {
                    WindowsNativeDispatcher.RemoveDirectory(win32Path2);
                }
                catch (final WindowsException ex11) {}
                throw ex10;
            }
            try {
                copySecurityAttributes(windowsPath, windowsPath2, false);
            }
            catch (final IOException ex12) {}
            try {
                WindowsNativeDispatcher.RemoveDirectory(win32Path);
            }
            catch (final WindowsException ex13) {
                try {
                    WindowsNativeDispatcher.RemoveDirectory(win32Path2);
                }
                catch (final WindowsException ex14) {}
                if (ex13.lastError() == 145 || ex13.lastError() == 183) {
                    throw new DirectoryNotEmptyException(windowsPath2.getPathForExceptionMessage());
                }
                ex13.rethrowAsIOException(windowsPath);
            }
        }
    }
    
    private static String asWin32Path(final WindowsPath windowsPath) throws IOException {
        try {
            return windowsPath.getPathForWin32Calls();
        }
        catch (final WindowsException ex) {
            ex.rethrowAsIOException(windowsPath);
            return null;
        }
    }
    
    private static void copySecurityAttributes(final WindowsPath windowsPath, final WindowsPath windowsPath2, final boolean b) throws IOException {
        final String finalPath = WindowsLinkSupport.getFinalPath(windowsPath, b);
        final WindowsSecurity.Privilege enablePrivilege = WindowsSecurity.enablePrivilege("SeRestorePrivilege");
        try {
            final int n = 7;
            final NativeBuffer fileSecurity = WindowsAclFileAttributeView.getFileSecurity(finalPath, n);
            try {
                WindowsNativeDispatcher.SetFileSecurity(windowsPath2.getPathForWin32Calls(), n, fileSecurity.address());
            }
            catch (final WindowsException ex) {
                ex.rethrowAsIOException(windowsPath2);
            }
            finally {
                fileSecurity.release();
            }
        }
        finally {
            enablePrivilege.drop();
        }
    }
}
