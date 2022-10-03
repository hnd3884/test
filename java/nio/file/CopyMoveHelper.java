package java.nio.file;

import java.io.InputStream;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.FileAttribute;
import java.io.IOException;
import java.nio.file.attribute.BasicFileAttributes;

class CopyMoveHelper
{
    private CopyMoveHelper() {
    }
    
    private static CopyOption[] convertMoveToCopyOptions(final CopyOption... array) throws AtomicMoveNotSupportedException {
        final int length = array.length;
        final CopyOption[] array2 = new CopyOption[length + 2];
        for (int i = 0; i < length; ++i) {
            final CopyOption copyOption = array[i];
            if (copyOption == StandardCopyOption.ATOMIC_MOVE) {
                throw new AtomicMoveNotSupportedException(null, null, "Atomic move between providers is not supported");
            }
            array2[i] = copyOption;
        }
        array2[length] = LinkOption.NOFOLLOW_LINKS;
        array2[length + 1] = StandardCopyOption.COPY_ATTRIBUTES;
        return array2;
    }
    
    static void copyToForeignTarget(final Path path, final Path path2, final CopyOption... array) throws IOException {
        final CopyOptions parse = CopyOptions.parse(array);
        final BasicFileAttributes attributes = Files.readAttributes(path, BasicFileAttributes.class, parse.followLinks ? new LinkOption[0] : new LinkOption[] { LinkOption.NOFOLLOW_LINKS });
        if (attributes.isSymbolicLink()) {
            throw new IOException("Copying of symbolic links not supported");
        }
        if (parse.replaceExisting) {
            Files.deleteIfExists(path2);
        }
        else if (Files.exists(path2, new LinkOption[0])) {
            throw new FileAlreadyExistsException(path2.toString());
        }
        if (attributes.isDirectory()) {
            Files.createDirectory(path2, (FileAttribute<?>[])new FileAttribute[0]);
        }
        else {
            try (final InputStream inputStream = Files.newInputStream(path, new OpenOption[0])) {
                Files.copy(inputStream, path2, new CopyOption[0]);
            }
        }
        if (parse.copyAttributes) {
            final BasicFileAttributeView basicFileAttributeView = Files.getFileAttributeView(path2, BasicFileAttributeView.class, new LinkOption[0]);
            try {
                basicFileAttributeView.setTimes(attributes.lastModifiedTime(), attributes.lastAccessTime(), attributes.creationTime());
            }
            catch (final Throwable t4) {
                try {
                    Files.delete(path2);
                }
                catch (final Throwable t5) {
                    t4.addSuppressed(t5);
                }
                throw t4;
            }
        }
    }
    
    static void moveToForeignTarget(final Path path, final Path path2, final CopyOption... array) throws IOException {
        copyToForeignTarget(path, path2, convertMoveToCopyOptions(array));
        Files.delete(path);
    }
    
    private static class CopyOptions
    {
        boolean replaceExisting;
        boolean copyAttributes;
        boolean followLinks;
        
        private CopyOptions() {
            this.replaceExisting = false;
            this.copyAttributes = false;
            this.followLinks = true;
        }
        
        static CopyOptions parse(final CopyOption... array) {
            final CopyOptions copyOptions = new CopyOptions();
            for (final CopyOption copyOption : array) {
                if (copyOption == StandardCopyOption.REPLACE_EXISTING) {
                    copyOptions.replaceExisting = true;
                }
                else if (copyOption == LinkOption.NOFOLLOW_LINKS) {
                    copyOptions.followLinks = false;
                }
                else if (copyOption == StandardCopyOption.COPY_ATTRIBUTES) {
                    copyOptions.copyAttributes = true;
                }
                else {
                    if (copyOption == null) {
                        throw new NullPointerException();
                    }
                    throw new UnsupportedOperationException("'" + copyOption + "' is not a recognized copy option");
                }
            }
            return copyOptions;
        }
    }
}
