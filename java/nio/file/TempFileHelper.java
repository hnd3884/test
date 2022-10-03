package java.nio.file;

import java.nio.file.attribute.PosixFilePermissions;
import java.util.EnumSet;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Set;
import java.security.PrivilegedAction;
import java.security.AccessController;
import sun.security.action.GetPropertyAction;
import java.io.IOException;
import java.nio.file.attribute.FileAttribute;
import java.security.SecureRandom;

class TempFileHelper
{
    private static final Path tmpdir;
    private static final boolean isPosix;
    private static final SecureRandom random;
    
    private TempFileHelper() {
    }
    
    private static Path generatePath(final String s, final String s2, final Path path) {
        final long nextLong = TempFileHelper.random.nextLong();
        final Path path2 = path.getFileSystem().getPath(s + Long.toString((nextLong == Long.MIN_VALUE) ? 0L : Math.abs(nextLong)) + s2, new String[0]);
        if (path2.getParent() != null) {
            throw new IllegalArgumentException("Invalid prefix or suffix");
        }
        return path.resolve(path2);
    }
    
    private static Path create(Path tmpdir, String s, String s2, final boolean b, FileAttribute<?>[] array) throws IOException {
        if (s == null) {
            s = "";
        }
        if (s2 == null) {
            s2 = (b ? "" : ".tmp");
        }
        if (tmpdir == null) {
            tmpdir = TempFileHelper.tmpdir;
        }
        if (TempFileHelper.isPosix && tmpdir.getFileSystem() == FileSystems.getDefault()) {
            if (array.length == 0) {
                array = new FileAttribute[] { b ? PosixPermissions.dirPermissions : PosixPermissions.filePermissions };
            }
            else {
                boolean b2 = false;
                for (int i = 0; i < array.length; ++i) {
                    if (array[i].name().equals("posix:permissions")) {
                        b2 = true;
                        break;
                    }
                }
                if (!b2) {
                    final FileAttribute[] array2 = new FileAttribute[array.length + 1];
                    System.arraycopy(array, 0, array2, 0, array.length);
                    array = array2;
                    array[array.length - 1] = (b ? PosixPermissions.dirPermissions : PosixPermissions.filePermissions);
                }
            }
        }
        final SecurityManager securityManager = System.getSecurityManager();
        while (true) {
            Path generatePath;
            try {
                generatePath = generatePath(s, s2, tmpdir);
            }
            catch (final InvalidPathException ex) {
                if (securityManager != null) {
                    throw new IllegalArgumentException("Invalid prefix or suffix");
                }
                throw ex;
            }
            try {
                if (b) {
                    return Files.createDirectory(generatePath, (FileAttribute<?>[])array);
                }
                return Files.createFile(generatePath, (FileAttribute<?>[])array);
            }
            catch (final SecurityException ex2) {
                if (tmpdir == TempFileHelper.tmpdir && securityManager != null) {
                    throw new SecurityException("Unable to create temporary file or directory");
                }
                throw ex2;
            }
            catch (final FileAlreadyExistsException ex3) {}
        }
    }
    
    static Path createTempFile(final Path path, final String s, final String s2, final FileAttribute<?>[] array) throws IOException {
        return create(path, s, s2, false, array);
    }
    
    static Path createTempDirectory(final Path path, final String s, final FileAttribute<?>[] array) throws IOException {
        return create(path, s, null, true, array);
    }
    
    static {
        tmpdir = Paths.get(AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("java.io.tmpdir")), new String[0]);
        isPosix = FileSystems.getDefault().supportedFileAttributeViews().contains("posix");
        random = new SecureRandom();
    }
    
    private static class PosixPermissions
    {
        static final FileAttribute<Set<PosixFilePermission>> filePermissions;
        static final FileAttribute<Set<PosixFilePermission>> dirPermissions;
        
        static {
            filePermissions = PosixFilePermissions.asFileAttribute(EnumSet.of(PosixFilePermission.OWNER_READ, PosixFilePermission.OWNER_WRITE));
            dirPermissions = PosixFilePermissions.asFileAttribute(EnumSet.of(PosixFilePermission.OWNER_READ, PosixFilePermission.OWNER_WRITE, PosixFilePermission.OWNER_EXECUTE));
        }
    }
}
