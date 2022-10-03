package com.sun.nio.zipfs;

import java.nio.file.FileSystem;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.StandardCopyOption;
import java.nio.file.Files;
import java.nio.file.CopyOption;
import java.io.OutputStream;
import java.nio.file.AccessDeniedException;
import java.nio.channels.FileChannel;
import java.nio.channels.SeekableByteChannel;
import java.util.Set;
import java.nio.file.FileStore;
import java.util.Map;
import java.nio.file.attribute.FileTime;
import java.nio.file.NoSuchFileException;
import java.nio.file.DirectoryStream;
import java.nio.file.StandardOpenOption;
import java.io.InputStream;
import java.nio.file.OpenOption;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.ReadOnlyFileSystemException;
import java.util.NoSuchElementException;
import java.util.Iterator;
import java.io.File;
import java.nio.file.WatchKey;
import java.nio.file.WatchEvent;
import java.nio.file.WatchService;
import java.nio.file.InvalidPathException;
import java.util.Arrays;
import java.nio.file.ProviderMismatchException;
import java.net.URI;
import java.io.IOException;
import java.nio.file.AccessMode;
import java.nio.file.LinkOption;
import java.nio.file.Path;

public class ZipPath implements Path
{
    private final ZipFileSystem zfs;
    private final byte[] path;
    private volatile int[] offsets;
    private int hashcode;
    private volatile byte[] resolved;
    
    ZipPath(final ZipFileSystem zipFileSystem, final byte[] array) {
        this(zipFileSystem, array, false);
    }
    
    ZipPath(final ZipFileSystem zfs, final byte[] path, final boolean b) {
        this.hashcode = 0;
        this.resolved = null;
        this.zfs = zfs;
        if (b) {
            this.path = path;
        }
        else {
            this.path = this.normalize(path);
        }
    }
    
    @Override
    public ZipPath getRoot() {
        if (this.isAbsolute()) {
            return new ZipPath(this.zfs, new byte[] { this.path[0] });
        }
        return null;
    }
    
    @Override
    public Path getFileName() {
        this.initOffsets();
        final int length = this.offsets.length;
        if (length == 0) {
            return null;
        }
        if (length == 1 && this.path[0] != 47) {
            return this;
        }
        final int n = this.offsets[length - 1];
        final int n2 = this.path.length - n;
        final byte[] array = new byte[n2];
        System.arraycopy(this.path, n, array, 0, n2);
        return new ZipPath(this.zfs, array);
    }
    
    @Override
    public ZipPath getParent() {
        this.initOffsets();
        final int length = this.offsets.length;
        if (length == 0) {
            return null;
        }
        final int n = this.offsets[length - 1] - 1;
        if (n <= 0) {
            return this.getRoot();
        }
        final byte[] array = new byte[n];
        System.arraycopy(this.path, 0, array, 0, n);
        return new ZipPath(this.zfs, array);
    }
    
    @Override
    public int getNameCount() {
        this.initOffsets();
        return this.offsets.length;
    }
    
    @Override
    public ZipPath getName(final int n) {
        this.initOffsets();
        if (n < 0 || n >= this.offsets.length) {
            throw new IllegalArgumentException();
        }
        final int n2 = this.offsets[n];
        int n3;
        if (n == this.offsets.length - 1) {
            n3 = this.path.length - n2;
        }
        else {
            n3 = this.offsets[n + 1] - n2 - 1;
        }
        final byte[] array = new byte[n3];
        System.arraycopy(this.path, n2, array, 0, n3);
        return new ZipPath(this.zfs, array);
    }
    
    @Override
    public ZipPath subpath(final int n, final int n2) {
        this.initOffsets();
        if (n < 0 || n >= this.offsets.length || n2 > this.offsets.length || n >= n2) {
            throw new IllegalArgumentException();
        }
        final int n3 = this.offsets[n];
        int n4;
        if (n2 == this.offsets.length) {
            n4 = this.path.length - n3;
        }
        else {
            n4 = this.offsets[n2] - n3 - 1;
        }
        final byte[] array = new byte[n4];
        System.arraycopy(this.path, n3, array, 0, n4);
        return new ZipPath(this.zfs, array);
    }
    
    @Override
    public ZipPath toRealPath(final LinkOption... array) throws IOException {
        final ZipPath absolutePath = new ZipPath(this.zfs, this.getResolvedPath()).toAbsolutePath();
        absolutePath.checkAccess(new AccessMode[0]);
        return absolutePath;
    }
    
    boolean isHidden() {
        return false;
    }
    
    @Override
    public ZipPath toAbsolutePath() {
        if (this.isAbsolute()) {
            return this;
        }
        final byte[] path = this.zfs.getDefaultDir().path;
        int length = path.length;
        final boolean b = path[length - 1] == 47;
        byte[] array;
        if (b) {
            array = new byte[length + this.path.length];
        }
        else {
            array = new byte[length + 1 + this.path.length];
        }
        System.arraycopy(path, 0, array, 0, length);
        if (!b) {
            array[length++] = 47;
        }
        System.arraycopy(this.path, 0, array, length, this.path.length);
        return new ZipPath(this.zfs, array, true);
    }
    
    @Override
    public URI toUri() {
        try {
            return new URI("jar", this.zfs.getZipFile().toUri() + "!" + this.zfs.getString(this.toAbsolutePath().path), null);
        }
        catch (final Exception ex) {
            throw new AssertionError((Object)ex);
        }
    }
    
    private boolean equalsNameAt(final ZipPath zipPath, final int n) {
        final int n2 = this.offsets[n];
        int n3;
        if (n == this.offsets.length - 1) {
            n3 = this.path.length - n2;
        }
        else {
            n3 = this.offsets[n + 1] - n2 - 1;
        }
        final int n4 = zipPath.offsets[n];
        int n5;
        if (n == zipPath.offsets.length - 1) {
            n5 = zipPath.path.length - n4;
        }
        else {
            n5 = zipPath.offsets[n + 1] - n4 - 1;
        }
        if (n3 != n5) {
            return false;
        }
        for (int i = 0; i < n3; ++i) {
            if (this.path[n2 + i] != zipPath.path[n4 + i]) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public Path relativize(final Path path) {
        final ZipPath checkPath = this.checkPath(path);
        if (checkPath.equals(this)) {
            return new ZipPath(this.getFileSystem(), new byte[0], true);
        }
        if (this.isAbsolute() != checkPath.isAbsolute()) {
            throw new IllegalArgumentException();
        }
        final int nameCount = this.getNameCount();
        final int nameCount2 = checkPath.getNameCount();
        int min;
        int n;
        for (min = Math.min(nameCount, nameCount2), n = 0; n < min && this.equalsNameAt(checkPath, n); ++n) {}
        int i = nameCount - n;
        int n2 = i * 3 - 1;
        if (n < nameCount2) {
            n2 += checkPath.path.length - checkPath.offsets[n] + 1;
        }
        final byte[] array = new byte[n2];
        int n3 = 0;
        while (i > 0) {
            array[n3++] = 46;
            array[n3++] = 46;
            if (n3 < n2) {
                array[n3++] = 47;
            }
            --i;
        }
        if (n < nameCount2) {
            System.arraycopy(checkPath.path, checkPath.offsets[n], array, n3, checkPath.path.length - checkPath.offsets[n]);
        }
        return new ZipPath(this.getFileSystem(), array);
    }
    
    @Override
    public ZipFileSystem getFileSystem() {
        return this.zfs;
    }
    
    @Override
    public boolean isAbsolute() {
        return this.path.length > 0 && this.path[0] == 47;
    }
    
    @Override
    public ZipPath resolve(final Path path) {
        final ZipPath checkPath = this.checkPath(path);
        if (checkPath.isAbsolute()) {
            return checkPath;
        }
        byte[] array;
        if (this.path[this.path.length - 1] == 47) {
            array = new byte[this.path.length + checkPath.path.length];
            System.arraycopy(this.path, 0, array, 0, this.path.length);
            System.arraycopy(checkPath.path, 0, array, this.path.length, checkPath.path.length);
        }
        else {
            array = new byte[this.path.length + 1 + checkPath.path.length];
            System.arraycopy(this.path, 0, array, 0, this.path.length);
            array[this.path.length] = 47;
            System.arraycopy(checkPath.path, 0, array, this.path.length + 1, checkPath.path.length);
        }
        return new ZipPath(this.zfs, array);
    }
    
    @Override
    public Path resolveSibling(final Path path) {
        if (path == null) {
            throw new NullPointerException();
        }
        final ZipPath parent = this.getParent();
        return (parent == null) ? path : parent.resolve(path);
    }
    
    @Override
    public boolean startsWith(final Path path) {
        final ZipPath checkPath = this.checkPath(path);
        if (checkPath.isAbsolute() != this.isAbsolute() || checkPath.path.length > this.path.length) {
            return false;
        }
        int length = checkPath.path.length;
        for (int i = 0; i < length; ++i) {
            if (checkPath.path[i] != this.path[i]) {
                return false;
            }
        }
        --length;
        return checkPath.path.length == this.path.length || checkPath.path[length] == 47 || this.path[length + 1] == 47;
    }
    
    @Override
    public boolean endsWith(final Path path) {
        final ZipPath checkPath = this.checkPath(path);
        int i = checkPath.path.length - 1;
        if (i > 0 && checkPath.path[i] == 47) {
            --i;
        }
        int n = this.path.length - 1;
        if (n > 0 && this.path[n] == 47) {
            --n;
        }
        if (i == -1) {
            return n == -1;
        }
        if ((checkPath.isAbsolute() && (!this.isAbsolute() || i != n)) || n < i) {
            return false;
        }
        while (i >= 0) {
            if (checkPath.path[i] != this.path[n]) {
                return false;
            }
            --i;
            --n;
        }
        return checkPath.path[i + 1] == 47 || n == -1 || this.path[n] == 47;
    }
    
    @Override
    public ZipPath resolve(final String s) {
        return this.resolve((Path)this.getFileSystem().getPath(s, new String[0]));
    }
    
    @Override
    public final Path resolveSibling(final String s) {
        return this.resolveSibling(this.getFileSystem().getPath(s, new String[0]));
    }
    
    @Override
    public final boolean startsWith(final String s) {
        return this.startsWith(this.getFileSystem().getPath(s, new String[0]));
    }
    
    @Override
    public final boolean endsWith(final String s) {
        return this.endsWith(this.getFileSystem().getPath(s, new String[0]));
    }
    
    @Override
    public Path normalize() {
        final byte[] resolved = this.getResolved();
        if (resolved == this.path) {
            return this;
        }
        return new ZipPath(this.zfs, resolved, true);
    }
    
    private ZipPath checkPath(final Path path) {
        if (path == null) {
            throw new NullPointerException();
        }
        if (!(path instanceof ZipPath)) {
            throw new ProviderMismatchException();
        }
        return (ZipPath)path;
    }
    
    private void initOffsets() {
        if (this.offsets == null) {
            int n = 0;
            int i = 0;
            while (i < this.path.length) {
                if (this.path[i++] != 47) {
                    ++n;
                    while (i < this.path.length && this.path[i] != 47) {
                        ++i;
                    }
                }
            }
            final int[] offsets = new int[n];
            int n2 = 0;
            for (int j = 0; j < this.path.length; ++j) {
                if (this.path[j] != 47) {
                    offsets[n2++] = j++;
                    while (j < this.path.length && this.path[j] != 47) {
                        ++j;
                    }
                }
            }
            synchronized (this) {
                if (this.offsets == null) {
                    this.offsets = offsets;
                }
            }
        }
    }
    
    byte[] getResolvedPath() {
        if (this.resolved == null) {
            byte[] resolved;
            if (this.isAbsolute()) {
                resolved = this.getResolved();
            }
            else {
                resolved = this.toAbsolutePath().getResolvedPath();
            }
            if (resolved[0] == 47) {
                resolved = Arrays.copyOfRange(resolved, 1, resolved.length);
            }
            this.resolved = resolved;
        }
        return this.resolved;
    }
    
    private byte[] normalize(final byte[] array) {
        if (array.length == 0) {
            return array;
        }
        int n = 0;
        for (int i = 0; i < array.length; ++i) {
            final byte b = array[i];
            if (b == 92) {
                return this.normalize(array, i);
            }
            if (b == 47 && n == 47) {
                return this.normalize(array, i - 1);
            }
            if (b == 0) {
                throw new InvalidPathException(this.zfs.getString(array), "Path: nul character not allowed");
            }
            n = b;
        }
        return array;
    }
    
    private byte[] normalize(final byte[] array, final int n) {
        final byte[] array2 = new byte[array.length];
        int i;
        for (i = 0; i < n; ++i) {
            array2[i] = array[i];
        }
        int n2 = i;
        byte b = 0;
        while (i < array.length) {
            byte b2 = array[i++];
            if (b2 == 92) {
                b2 = 47;
            }
            if (b2 == 47 && b == 47) {
                continue;
            }
            if (b2 == 0) {
                throw new InvalidPathException(this.zfs.getString(array), "Path: nul character not allowed");
            }
            array2[n2++] = b2;
            b = b2;
        }
        if (n2 > 1 && array2[n2 - 1] == 47) {
            --n2;
        }
        return (n2 == array2.length) ? array2 : Arrays.copyOf(array2, n2);
    }
    
    private byte[] getResolved() {
        if (this.path.length == 0) {
            return this.path;
        }
        for (int i = 0; i < this.path.length; ++i) {
            if (this.path[i] == 46) {
                return this.resolve0();
            }
        }
        return this.path;
    }
    
    private byte[] resolve0() {
        final byte[] array = new byte[this.path.length];
        final int nameCount = this.getNameCount();
        final int[] array2 = new int[nameCount];
        int n = -1;
        int n2 = 0;
        for (int i = 0; i < nameCount; ++i) {
            int n3 = this.offsets[i];
            int n4 = (i == this.offsets.length - 1) ? (this.path.length - n3) : (this.offsets[i + 1] - n3 - 1);
            if (n4 == 1 && this.path[n3] == 46) {
                if (n2 == 0 && this.path[0] == 47) {
                    array[n2++] = 47;
                }
            }
            else if (n4 == 2 && this.path[n3] == 46 && this.path[n3 + 1] == 46) {
                if (n >= 0) {
                    n2 = array2[n--];
                }
                else if (this.path[0] == 47) {
                    if (n2 == 0) {
                        array[n2++] = 47;
                    }
                }
                else {
                    if (n2 != 0 && array[n2 - 1] != 47) {
                        array[n2++] = 47;
                    }
                    while (n4-- > 0) {
                        array[n2++] = this.path[n3++];
                    }
                }
            }
            else {
                if ((n2 == 0 && this.path[0] == 47) || (n2 != 0 && array[n2 - 1] != 47)) {
                    array[n2++] = 47;
                }
                array2[++n] = n2;
                while (n4-- > 0) {
                    array[n2++] = this.path[n3++];
                }
            }
        }
        if (n2 > 1 && array[n2 - 1] == 47) {
            --n2;
        }
        return (n2 == array.length) ? array : Arrays.copyOf(array, n2);
    }
    
    @Override
    public String toString() {
        return this.zfs.getString(this.path);
    }
    
    @Override
    public int hashCode() {
        int hashcode = this.hashcode;
        if (hashcode == 0) {
            hashcode = (this.hashcode = Arrays.hashCode(this.path));
        }
        return hashcode;
    }
    
    @Override
    public boolean equals(final Object o) {
        return o != null && o instanceof ZipPath && this.zfs == ((ZipPath)o).zfs && this.compareTo((Path)o) == 0;
    }
    
    @Override
    public int compareTo(final Path path) {
        final ZipPath checkPath = this.checkPath(path);
        final int length = this.path.length;
        final int length2 = checkPath.path.length;
        final int min = Math.min(length, length2);
        final byte[] path2 = this.path;
        final byte[] path3 = checkPath.path;
        for (int i = 0; i < min; ++i) {
            final int n = path2[i] & 0xFF;
            final int n2 = path3[i] & 0xFF;
            if (n != n2) {
                return n - n2;
            }
        }
        return length - length2;
    }
    
    @Override
    public WatchKey register(final WatchService watchService, final WatchEvent.Kind<?>[] array, final WatchEvent.Modifier... array2) {
        if (watchService == null || array == null || array2 == null) {
            throw new NullPointerException();
        }
        throw new UnsupportedOperationException();
    }
    
    @Override
    public WatchKey register(final WatchService watchService, final WatchEvent.Kind<?>... array) {
        return this.register(watchService, array, new WatchEvent.Modifier[0]);
    }
    
    @Override
    public final File toFile() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public Iterator<Path> iterator() {
        return new Iterator<Path>() {
            private int i = 0;
            
            @Override
            public boolean hasNext() {
                return this.i < ZipPath.this.getNameCount();
            }
            
            @Override
            public Path next() {
                if (this.i < ZipPath.this.getNameCount()) {
                    final ZipPath name = ZipPath.this.getName(this.i);
                    ++this.i;
                    return name;
                }
                throw new NoSuchElementException();
            }
            
            @Override
            public void remove() {
                throw new ReadOnlyFileSystemException();
            }
        };
    }
    
    void createDirectory(final FileAttribute<?>... array) throws IOException {
        this.zfs.createDirectory(this.getResolvedPath(), array);
    }
    
    InputStream newInputStream(final OpenOption... array) throws IOException {
        if (array.length > 0) {
            for (final OpenOption openOption : array) {
                if (openOption != StandardOpenOption.READ) {
                    throw new UnsupportedOperationException("'" + openOption + "' not allowed");
                }
            }
        }
        return this.zfs.newInputStream(this.getResolvedPath());
    }
    
    DirectoryStream<Path> newDirectoryStream(final DirectoryStream.Filter<? super Path> filter) throws IOException {
        return new ZipDirectoryStream(this, filter);
    }
    
    void delete() throws IOException {
        this.zfs.deleteFile(this.getResolvedPath(), true);
    }
    
    void deleteIfExists() throws IOException {
        this.zfs.deleteFile(this.getResolvedPath(), false);
    }
    
    ZipFileAttributes getAttributes() throws IOException {
        final ZipFileAttributes fileAttributes = this.zfs.getFileAttributes(this.getResolvedPath());
        if (fileAttributes == null) {
            throw new NoSuchFileException(this.toString());
        }
        return fileAttributes;
    }
    
    void setAttribute(final String s, final Object o, final LinkOption... array) throws IOException {
        int index = s.indexOf(58);
        String substring;
        String substring2;
        if (index == -1) {
            substring = "basic";
            substring2 = s;
        }
        else {
            substring = s.substring(0, index++);
            substring2 = s.substring(index);
        }
        final ZipFileAttributeView value = ZipFileAttributeView.get(this, substring);
        if (value == null) {
            throw new UnsupportedOperationException("view <" + value + "> is not supported");
        }
        value.setAttribute(substring2, o);
    }
    
    void setTimes(final FileTime fileTime, final FileTime fileTime2, final FileTime fileTime3) throws IOException {
        this.zfs.setTimes(this.getResolvedPath(), fileTime, fileTime2, fileTime3);
    }
    
    Map<String, Object> readAttributes(final String s, final LinkOption... array) throws IOException {
        int index = s.indexOf(58);
        String substring;
        String substring2;
        if (index == -1) {
            substring = "basic";
            substring2 = s;
        }
        else {
            substring = s.substring(0, index++);
            substring2 = s.substring(index);
        }
        final ZipFileAttributeView value = ZipFileAttributeView.get(this, substring);
        if (value == null) {
            throw new UnsupportedOperationException("view not supported");
        }
        return value.readAttributes(substring2);
    }
    
    FileStore getFileStore() throws IOException {
        if (this.exists()) {
            return this.zfs.getFileStore(this);
        }
        throw new NoSuchFileException(this.zfs.getString(this.path));
    }
    
    boolean isSameFile(final Path path) throws IOException {
        if (this.equals(path)) {
            return true;
        }
        if (path == null || this.getFileSystem() != path.getFileSystem()) {
            return false;
        }
        this.checkAccess(new AccessMode[0]);
        ((ZipPath)path).checkAccess(new AccessMode[0]);
        return Arrays.equals(this.getResolvedPath(), ((ZipPath)path).getResolvedPath());
    }
    
    SeekableByteChannel newByteChannel(final Set<? extends OpenOption> set, final FileAttribute<?>... array) throws IOException {
        return this.zfs.newByteChannel(this.getResolvedPath(), set, array);
    }
    
    FileChannel newFileChannel(final Set<? extends OpenOption> set, final FileAttribute<?>... array) throws IOException {
        return this.zfs.newFileChannel(this.getResolvedPath(), set, array);
    }
    
    void checkAccess(final AccessMode... array) throws IOException {
        boolean b = false;
        boolean b2 = false;
        for (int length = array.length, i = 0; i < length; ++i) {
            switch (array[i]) {
                case READ: {
                    break;
                }
                case WRITE: {
                    b = true;
                    break;
                }
                case EXECUTE: {
                    b2 = true;
                    break;
                }
                default: {
                    throw new UnsupportedOperationException();
                }
            }
        }
        if (this.zfs.getFileAttributes(this.getResolvedPath()) == null && (this.path.length != 1 || this.path[0] != 47)) {
            throw new NoSuchFileException(this.toString());
        }
        if (b && this.zfs.isReadOnly()) {
            throw new AccessDeniedException(this.toString());
        }
        if (b2) {
            throw new AccessDeniedException(this.toString());
        }
    }
    
    boolean exists() {
        if (this.path.length == 1 && this.path[0] == 47) {
            return true;
        }
        try {
            return this.zfs.exists(this.getResolvedPath());
        }
        catch (final IOException ex) {
            return false;
        }
    }
    
    OutputStream newOutputStream(final OpenOption... array) throws IOException {
        if (array.length == 0) {
            return this.zfs.newOutputStream(this.getResolvedPath(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
        }
        return this.zfs.newOutputStream(this.getResolvedPath(), array);
    }
    
    void move(final ZipPath zipPath, final CopyOption... array) throws IOException {
        if (Files.isSameFile(this.zfs.getZipFile(), zipPath.zfs.getZipFile())) {
            this.zfs.copyFile(true, this.getResolvedPath(), zipPath.getResolvedPath(), array);
        }
        else {
            this.copyToTarget(zipPath, array);
            this.delete();
        }
    }
    
    void copy(final ZipPath zipPath, final CopyOption... array) throws IOException {
        if (Files.isSameFile(this.zfs.getZipFile(), zipPath.zfs.getZipFile())) {
            this.zfs.copyFile(false, this.getResolvedPath(), zipPath.getResolvedPath(), array);
        }
        else {
            this.copyToTarget(zipPath, array);
        }
    }
    
    private void copyToTarget(final ZipPath zipPath, final CopyOption... array) throws IOException {
        boolean b = false;
        boolean b2 = false;
        for (final CopyOption copyOption : array) {
            if (copyOption == StandardCopyOption.REPLACE_EXISTING) {
                b = true;
            }
            else if (copyOption == StandardCopyOption.COPY_ATTRIBUTES) {
                b2 = true;
            }
        }
        final ZipFileAttributes attributes = this.getAttributes();
        boolean exists;
        if (b) {
            try {
                zipPath.deleteIfExists();
                exists = false;
            }
            catch (final DirectoryNotEmptyException ex) {
                exists = true;
            }
        }
        else {
            exists = zipPath.exists();
        }
        if (exists) {
            throw new FileAlreadyExistsException(zipPath.toString());
        }
        if (attributes.isDirectory()) {
            zipPath.createDirectory((FileAttribute<?>[])new FileAttribute[0]);
        }
        else {
            final InputStream inputStream = this.zfs.newInputStream(this.getResolvedPath());
            try {
                final OutputStream outputStream = zipPath.newOutputStream(new OpenOption[0]);
                try {
                    final byte[] array2 = new byte[8192];
                    int read;
                    while ((read = inputStream.read(array2)) != -1) {
                        outputStream.write(array2, 0, read);
                    }
                }
                finally {
                    outputStream.close();
                }
            }
            finally {
                inputStream.close();
            }
        }
        if (b2) {
            final BasicFileAttributeView basicFileAttributeView = ZipFileAttributeView.get(zipPath, BasicFileAttributeView.class);
            try {
                basicFileAttributeView.setTimes(attributes.lastModifiedTime(), attributes.lastAccessTime(), attributes.creationTime());
            }
            catch (final IOException ex2) {
                try {
                    zipPath.delete();
                }
                catch (final IOException ex3) {}
                throw ex2;
            }
        }
    }
}
