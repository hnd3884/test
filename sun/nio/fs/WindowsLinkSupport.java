package sun.nio.fs;

import java.nio.file.NotLinkException;
import java.io.IOError;
import java.nio.file.FileSystemException;
import java.nio.file.Path;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.io.IOException;
import sun.misc.Unsafe;

class WindowsLinkSupport
{
    private static final Unsafe unsafe;
    
    private WindowsLinkSupport() {
    }
    
    static String readLink(final WindowsPath windowsPath) throws IOException {
        long openForReadAttributeAccess = 0L;
        try {
            openForReadAttributeAccess = windowsPath.openForReadAttributeAccess(false);
        }
        catch (final WindowsException ex) {
            ex.rethrowAsIOException(windowsPath);
        }
        try {
            return readLinkImpl(openForReadAttributeAccess);
        }
        finally {
            WindowsNativeDispatcher.CloseHandle(openForReadAttributeAccess);
        }
    }
    
    static String getFinalPath(final WindowsPath windowsPath) throws IOException {
        long openForReadAttributeAccess = 0L;
        try {
            openForReadAttributeAccess = windowsPath.openForReadAttributeAccess(true);
        }
        catch (final WindowsException ex) {
            ex.rethrowAsIOException(windowsPath);
        }
        try {
            return stripPrefix(WindowsNativeDispatcher.GetFinalPathNameByHandle(openForReadAttributeAccess));
        }
        catch (final WindowsException ex2) {
            if (ex2.lastError() != 124) {
                ex2.rethrowAsIOException(windowsPath);
            }
        }
        finally {
            WindowsNativeDispatcher.CloseHandle(openForReadAttributeAccess);
        }
        return null;
    }
    
    static String getFinalPath(final WindowsPath windowsPath, final boolean b) throws IOException {
        final WindowsFileSystem fileSystem = windowsPath.getFileSystem();
        try {
            if (!b || !fileSystem.supportsLinks()) {
                return windowsPath.getPathForWin32Calls();
            }
            if (!WindowsFileAttributes.get(windowsPath, false).isSymbolicLink()) {
                return windowsPath.getPathForWin32Calls();
            }
        }
        catch (final WindowsException ex) {
            ex.rethrowAsIOException(windowsPath);
        }
        final String finalPath = getFinalPath(windowsPath);
        if (finalPath != null) {
            return finalPath;
        }
        WindowsPath resolve = windowsPath;
        int n = 0;
        do {
            try {
                if (!WindowsFileAttributes.get(resolve, false).isSymbolicLink()) {
                    return resolve.getPathForWin32Calls();
                }
            }
            catch (final WindowsException ex2) {
                ex2.rethrowAsIOException(resolve);
            }
            final WindowsPath fromNormalizedPath = WindowsPath.createFromNormalizedPath(fileSystem, readLink(resolve));
            WindowsPath windowsPath2 = resolve.getParent();
            if (windowsPath2 == null) {
                windowsPath2 = AccessController.doPrivileged((PrivilegedAction<WindowsPath>)new PrivilegedAction<WindowsPath>() {
                    @Override
                    public WindowsPath run() {
                        return resolve.toAbsolutePath();
                    }
                }).getParent();
            }
            resolve = windowsPath2.resolve(fromNormalizedPath);
        } while (++n < 32);
        throw new FileSystemException(windowsPath.getPathForExceptionMessage(), null, "Too many links");
    }
    
    static String getRealPath(final WindowsPath windowsPath, boolean b) throws IOException {
        final WindowsFileSystem fileSystem = windowsPath.getFileSystem();
        if (b && !fileSystem.supportsLinks()) {
            b = false;
        }
        String s;
        try {
            s = windowsPath.toAbsolutePath().toString();
        }
        catch (final IOError ioError) {
            throw (IOException)ioError.getCause();
        }
        if (s.indexOf(46) >= 0) {
            try {
                s = WindowsNativeDispatcher.GetFullPathName(s);
            }
            catch (final WindowsException ex) {
                ex.rethrowAsIOException(windowsPath);
            }
        }
        final StringBuilder sb = new StringBuilder(s.length());
        final char char1 = s.charAt(0);
        final char char2 = s.charAt(1);
        int n;
        if (((char1 <= 'z' && char1 >= 'a') || (char1 <= 'Z' && char1 >= 'A')) && char2 == ':' && s.charAt(2) == '\\') {
            sb.append(Character.toUpperCase(char1));
            sb.append(":\\");
            n = 3;
        }
        else {
            if (char1 != '\\' || char2 != '\\') {
                throw new AssertionError((Object)"path type not recognized");
            }
            final int n2 = s.length() - 1;
            final int index = s.indexOf(92, 2);
            if (index == -1 || index == n2) {
                throw new FileSystemException(windowsPath.getPathForExceptionMessage(), null, "UNC has invalid share");
            }
            int index2 = s.indexOf(92, index + 1);
            if (index2 < 0) {
                index2 = n2;
                sb.append(s).append("\\");
            }
            else {
                sb.append(s, 0, index2 + 1);
            }
            n = index2 + 1;
        }
        if (n >= s.length()) {
            final String string = sb.toString();
            try {
                WindowsNativeDispatcher.GetFileAttributes(string);
            }
            catch (final WindowsException ex2) {
                ex2.rethrowAsIOException(s);
            }
            return string;
        }
        int n3;
        for (int i = n; i < s.length(); i = n3 + 1) {
            final int index3 = s.indexOf(92, i);
            n3 = ((index3 == -1) ? s.length() : index3);
            final String string2 = sb.toString() + s.substring(i, n3);
            try {
                final WindowsNativeDispatcher.FirstFile findFirstFile = WindowsNativeDispatcher.FindFirstFile(WindowsPath.addPrefixIfNeeded(string2));
                WindowsNativeDispatcher.FindClose(findFirstFile.handle());
                if (b && WindowsFileAttributes.isReparsePoint(findFirstFile.attributes())) {
                    String s2 = getFinalPath(windowsPath);
                    if (s2 == null) {
                        s2 = getRealPath(resolveAllLinks(WindowsPath.createFromNormalizedPath(fileSystem, s)), false);
                    }
                    return s2;
                }
                sb.append(findFirstFile.name());
                if (index3 != -1) {
                    sb.append('\\');
                }
            }
            catch (final WindowsException ex3) {
                ex3.rethrowAsIOException(s);
            }
        }
        return sb.toString();
    }
    
    private static String readLinkImpl(final long n) throws IOException {
        final int n2 = 16384;
        final NativeBuffer nativeBuffer = NativeBuffers.getNativeBuffer(n2);
        try {
            try {
                WindowsNativeDispatcher.DeviceIoControlGetReparsePoint(n, nativeBuffer.address(), n2);
            }
            catch (final WindowsException ex) {
                if (ex.lastError() == 4390) {
                    throw new NotLinkException(null, null, ex.errorString());
                }
                ex.rethrowAsIOException((String)null);
            }
            if ((int)WindowsLinkSupport.unsafe.getLong(nativeBuffer.address() + 0L) != -1610612724) {
                throw new NotLinkException(null, null, "Reparse point is not a symbolic link");
            }
            final short short1 = WindowsLinkSupport.unsafe.getShort(nativeBuffer.address() + 8L);
            final short short2 = WindowsLinkSupport.unsafe.getShort(nativeBuffer.address() + 10L);
            if (short2 % 2 != 0) {
                throw new FileSystemException(null, null, "Symbolic link corrupted");
            }
            final char[] array = new char[short2 / 2];
            WindowsLinkSupport.unsafe.copyMemory(null, nativeBuffer.address() + 20L + short1, array, Unsafe.ARRAY_CHAR_BASE_OFFSET, short2);
            final String stripPrefix = stripPrefix(new String(array));
            if (stripPrefix.length() == 0) {
                throw new IOException("Symbolic link target is invalid");
            }
            return stripPrefix;
        }
        finally {
            nativeBuffer.release();
        }
    }
    
    private static WindowsPath resolveAllLinks(WindowsPath windowsPath) throws IOException {
        assert windowsPath.isAbsolute();
        final WindowsFileSystem fileSystem = windowsPath.getFileSystem();
        int n = 0;
        int i = 0;
        while (i < windowsPath.getNameCount()) {
            final WindowsPath resolve = windowsPath.getRoot().resolve(windowsPath.subpath(0, i + 1));
            WindowsFileAttributes value = null;
            try {
                value = WindowsFileAttributes.get(resolve, false);
            }
            catch (final WindowsException ex) {
                ex.rethrowAsIOException(resolve);
            }
            if (value.isSymbolicLink()) {
                if (++n > 32) {
                    throw new IOException("Too many links");
                }
                final WindowsPath fromNormalizedPath = WindowsPath.createFromNormalizedPath(fileSystem, readLink(resolve));
                Path subpath = null;
                final int nameCount = windowsPath.getNameCount();
                if (i + 1 < nameCount) {
                    subpath = windowsPath.subpath(i + 1, nameCount);
                }
                windowsPath = resolve.getParent().resolve(fromNormalizedPath);
                try {
                    final String getFullPathName = WindowsNativeDispatcher.GetFullPathName(windowsPath.toString());
                    if (!getFullPathName.equals(windowsPath.toString())) {
                        windowsPath = WindowsPath.createFromNormalizedPath(fileSystem, getFullPathName);
                    }
                }
                catch (final WindowsException ex2) {
                    ex2.rethrowAsIOException(windowsPath);
                }
                if (subpath != null) {
                    windowsPath = windowsPath.resolve(subpath);
                }
                i = 0;
            }
            else {
                ++i;
            }
        }
        return windowsPath;
    }
    
    private static String stripPrefix(String s) {
        if (s.startsWith("\\\\?\\")) {
            if (s.startsWith("\\\\?\\UNC\\")) {
                s = "\\" + s.substring(7);
            }
            else {
                s = s.substring(4);
            }
            return s;
        }
        if (s.startsWith("\\??\\")) {
            if (s.startsWith("\\??\\UNC\\")) {
                s = "\\" + s.substring(7);
            }
            else {
                s = s.substring(4);
            }
            return s;
        }
        return s;
    }
    
    static {
        unsafe = Unsafe.getUnsafe();
    }
}
