package com.sun.nio.zipfs;

import java.util.zip.CRC32;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.ZipError;
import java.io.EOFException;
import java.util.zip.InflaterInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.attribute.PosixFileAttributeView;
import java.nio.file.attribute.PosixFileAttributes;
import java.io.BufferedOutputStream;
import java.nio.file.ClosedFileSystemException;
import java.util.zip.ZipException;
import java.nio.channels.FileLock;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.NonWritableChannelException;
import java.nio.channels.ReadableByteChannel;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
import java.nio.channels.Channels;
import java.nio.file.FileSystemException;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.nio.file.CopyOption;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.NotDirectoryException;
import java.nio.file.DirectoryStream;
import java.nio.file.NoSuchFileException;
import java.nio.file.attribute.FileTime;
import java.util.Iterator;
import java.util.Collection;
import java.util.regex.Pattern;
import java.nio.file.PathMatcher;
import java.nio.file.FileStore;
import java.nio.file.WatchService;
import java.nio.file.attribute.UserPrincipalLookupService;
import java.nio.file.ReadOnlyFileSystemException;
import java.nio.file.spi.FileSystemProvider;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.AccessMode;
import java.nio.file.StandardOpenOption;
import java.nio.file.OpenOption;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.zip.Deflater;
import java.util.zip.Inflater;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.nio.channels.SeekableByteChannel;
import java.io.InputStream;
import java.util.Set;
import java.nio.file.Path;
import java.nio.file.FileSystem;

public class ZipFileSystem extends FileSystem
{
    private final ZipFileSystemProvider provider;
    private final ZipPath defaultdir;
    private boolean readOnly;
    private final Path zfpath;
    private final ZipCoder zc;
    private final String defaultDir;
    private final String nameEncoding;
    private final boolean useTempFile;
    private final boolean createNew;
    private static final boolean isWindows;
    private static final Set<String> supportedFileAttributeViews;
    private static final String GLOB_SYNTAX = "glob";
    private static final String REGEX_SYNTAX = "regex";
    private Set<InputStream> streams;
    private Set<ExChannelCloser> exChClosers;
    private Set<Path> tmppaths;
    private static byte[] ROOTPATH;
    private volatile boolean isOpen;
    private final SeekableByteChannel ch;
    final byte[] cen;
    private END end;
    private long locpos;
    private final ReadWriteLock rwlock;
    private LinkedHashMap<IndexNode, IndexNode> inodes;
    private boolean hasUpdate;
    private final IndexNode LOOKUPKEY;
    private final int MAX_FLATER = 20;
    private final List<Inflater> inflaters;
    private final List<Deflater> deflaters;
    private IndexNode root;
    
    ZipFileSystem(final ZipFileSystemProvider provider, final Path zfpath, final Map<String, ?> map) throws IOException {
        this.readOnly = false;
        this.streams = Collections.synchronizedSet(new HashSet<InputStream>());
        this.exChClosers = new HashSet<ExChannelCloser>();
        this.tmppaths = Collections.synchronizedSet(new HashSet<Path>());
        this.isOpen = true;
        this.rwlock = new ReentrantReadWriteLock();
        this.hasUpdate = false;
        this.LOOKUPKEY = IndexNode.keyOf(null);
        this.inflaters = new ArrayList<Inflater>();
        this.deflaters = new ArrayList<Deflater>();
        this.createNew = "true".equals(map.get("create"));
        this.nameEncoding = (map.containsKey("encoding") ? map.get("encoding") : "UTF-8");
        this.useTempFile = Boolean.TRUE.equals(map.get("useTempFile"));
        this.defaultDir = (map.containsKey("default.dir") ? map.get("default.dir") : "/");
        if (this.defaultDir.charAt(0) != '/') {
            throw new IllegalArgumentException("default dir should be absolute");
        }
        this.provider = provider;
        this.zfpath = zfpath;
        if (Files.notExists(zfpath, new LinkOption[0])) {
            if (!this.createNew) {
                throw new FileSystemNotFoundException(zfpath.toString());
            }
            try (final OutputStream outputStream = Files.newOutputStream(zfpath, StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE)) {
                new END().write(outputStream, 0L);
            }
        }
        zfpath.getFileSystem().provider().checkAccess(zfpath, AccessMode.READ);
        if (!Files.isWritable(zfpath)) {
            this.readOnly = true;
        }
        this.zc = ZipCoder.get(this.nameEncoding);
        this.defaultdir = new ZipPath(this, this.getBytes(this.defaultDir));
        this.ch = Files.newByteChannel(zfpath, StandardOpenOption.READ);
        this.cen = this.initCEN();
    }
    
    @Override
    public FileSystemProvider provider() {
        return this.provider;
    }
    
    @Override
    public String getSeparator() {
        return "/";
    }
    
    @Override
    public boolean isOpen() {
        return this.isOpen;
    }
    
    @Override
    public boolean isReadOnly() {
        return this.readOnly;
    }
    
    private void checkWritable() throws IOException {
        if (this.readOnly) {
            throw new ReadOnlyFileSystemException();
        }
    }
    
    @Override
    public Iterable<Path> getRootDirectories() {
        final ArrayList list = new ArrayList();
        list.add(new ZipPath(this, new byte[] { 47 }));
        return list;
    }
    
    ZipPath getDefaultDir() {
        return this.defaultdir;
    }
    
    @Override
    public ZipPath getPath(final String s, final String... array) {
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
                        sb.append('/');
                    }
                    sb.append(s2);
                }
            }
            string = sb.toString();
        }
        return new ZipPath(this, this.getBytes(string));
    }
    
    @Override
    public UserPrincipalLookupService getUserPrincipalLookupService() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public WatchService newWatchService() {
        throw new UnsupportedOperationException();
    }
    
    FileStore getFileStore(final ZipPath zipPath) {
        return new ZipFileStore(zipPath);
    }
    
    @Override
    public Iterable<FileStore> getFileStores() {
        final ArrayList list = new ArrayList(1);
        list.add(new ZipFileStore(new ZipPath(this, new byte[] { 47 })));
        return list;
    }
    
    @Override
    public Set<String> supportedFileAttributeViews() {
        return ZipFileSystem.supportedFileAttributeViews;
    }
    
    @Override
    public String toString() {
        return this.zfpath.toString();
    }
    
    Path getZipFile() {
        return this.zfpath;
    }
    
    @Override
    public PathMatcher getPathMatcher(final String s) {
        final int index = s.indexOf(58);
        if (index <= 0 || index == s.length()) {
            throw new IllegalArgumentException();
        }
        final String substring = s.substring(0, index);
        final String substring2 = s.substring(index + 1);
        String regexPattern;
        if (substring.equals("glob")) {
            regexPattern = ZipUtils.toRegexPattern(substring2);
        }
        else {
            if (!substring.equals("regex")) {
                throw new UnsupportedOperationException("Syntax '" + substring + "' not recognized");
            }
            regexPattern = substring2;
        }
        return new PathMatcher() {
            final /* synthetic */ Pattern val$pattern = Pattern.compile(regexPattern);
            
            @Override
            public boolean matches(final Path path) {
                return this.val$pattern.matcher(path.toString()).matches();
            }
        };
    }
    
    @Override
    public void close() throws IOException {
        this.beginWrite();
        try {
            if (!this.isOpen) {
                return;
            }
            this.isOpen = false;
        }
        finally {
            this.endWrite();
        }
        if (!this.streams.isEmpty()) {
            final Iterator iterator = new HashSet(this.streams).iterator();
            while (iterator.hasNext()) {
                ((InputStream)iterator.next()).close();
            }
        }
        this.beginWrite();
        try {
            this.sync();
            this.ch.close();
        }
        finally {
            this.endWrite();
        }
        synchronized (this.inflaters) {
            final Iterator<Inflater> iterator2 = this.inflaters.iterator();
            while (iterator2.hasNext()) {
                iterator2.next().end();
            }
        }
        synchronized (this.deflaters) {
            final Iterator<Deflater> iterator3 = this.deflaters.iterator();
            while (iterator3.hasNext()) {
                iterator3.next().end();
            }
        }
        this.beginWrite();
        try {
            this.inodes = null;
        }
        finally {
            this.endWrite();
        }
        Throwable t = null;
        synchronized (this.tmppaths) {
            for (final Path path : this.tmppaths) {
                try {
                    Files.deleteIfExists(path);
                }
                catch (final IOException ex) {
                    if (t == null) {
                        t = ex;
                    }
                    else {
                        t.addSuppressed(ex);
                    }
                }
            }
        }
        this.provider.removeFileSystem(this.zfpath, this);
        if (t != null) {
            throw t;
        }
    }
    
    ZipFileAttributes getFileAttributes(final byte[] array) throws IOException {
        this.beginRead();
        Entry entry0;
        try {
            this.ensureOpen();
            entry0 = this.getEntry0(array);
            if (entry0 == null) {
                final IndexNode inode = this.getInode(array);
                if (inode == null) {
                    return null;
                }
                entry0 = new Entry(inode.name);
                entry0.method = 0;
                final Entry entry2 = entry0;
                final Entry entry3 = entry0;
                final Entry entry4 = entry0;
                final long mtime = -1L;
                entry4.ctime = mtime;
                entry3.atime = mtime;
                entry2.mtime = mtime;
            }
        }
        finally {
            this.endRead();
        }
        return new ZipFileAttributes(entry0);
    }
    
    void setTimes(final byte[] array, final FileTime fileTime, final FileTime fileTime2, final FileTime fileTime3) throws IOException {
        this.checkWritable();
        this.beginWrite();
        try {
            this.ensureOpen();
            final Entry entry0 = this.getEntry0(array);
            if (entry0 == null) {
                throw new NoSuchFileException(this.getString(array));
            }
            if (entry0.type == 1) {
                entry0.type = 4;
            }
            if (fileTime != null) {
                entry0.mtime = fileTime.toMillis();
            }
            if (fileTime2 != null) {
                entry0.atime = fileTime2.toMillis();
            }
            if (fileTime3 != null) {
                entry0.ctime = fileTime3.toMillis();
            }
            this.update(entry0);
        }
        finally {
            this.endWrite();
        }
    }
    
    boolean exists(final byte[] array) throws IOException {
        this.beginRead();
        try {
            this.ensureOpen();
            return this.getInode(array) != null;
        }
        finally {
            this.endRead();
        }
    }
    
    boolean isDirectory(final byte[] array) throws IOException {
        this.beginRead();
        try {
            final IndexNode inode = this.getInode(array);
            return inode != null && inode.isDir();
        }
        finally {
            this.endRead();
        }
    }
    
    private ZipPath toZipPath(final byte[] array) {
        final byte[] array2 = new byte[array.length + 1];
        array2[0] = 47;
        System.arraycopy(array, 0, array2, 1, array.length);
        return new ZipPath(this, array2);
    }
    
    Iterator<Path> iteratorOf(final byte[] array, final DirectoryStream.Filter<? super Path> filter) throws IOException {
        this.beginWrite();
        try {
            this.ensureOpen();
            final IndexNode inode = this.getInode(array);
            if (inode == null) {
                throw new NotDirectoryException(this.getString(array));
            }
            final ArrayList list = new ArrayList();
            for (IndexNode indexNode = inode.child; indexNode != null; indexNode = indexNode.sibling) {
                final ZipPath zipPath = this.toZipPath(indexNode.name);
                if (filter == null || filter.accept(zipPath)) {
                    list.add(zipPath);
                }
            }
            return list.iterator();
        }
        finally {
            this.endWrite();
        }
    }
    
    void createDirectory(byte[] directoryPath, final FileAttribute<?>... array) throws IOException {
        this.checkWritable();
        directoryPath = ZipUtils.toDirectoryPath(directoryPath);
        this.beginWrite();
        try {
            this.ensureOpen();
            if (directoryPath.length == 0 || this.exists(directoryPath)) {
                throw new FileAlreadyExistsException(this.getString(directoryPath));
            }
            this.checkParents(directoryPath);
            final Entry entry = new Entry(directoryPath, 2);
            entry.method = 0;
            this.update(entry);
        }
        finally {
            this.endWrite();
        }
    }
    
    void copyFile(final boolean b, final byte[] array, final byte[] array2, final CopyOption... array3) throws IOException {
        this.checkWritable();
        if (Arrays.equals(array, array2)) {
            return;
        }
        this.beginWrite();
        try {
            this.ensureOpen();
            final Entry entry0 = this.getEntry0(array);
            if (entry0 == null) {
                throw new NoSuchFileException(this.getString(array));
            }
            if (entry0.isDir()) {
                this.createDirectory(array2, (FileAttribute<?>[])new FileAttribute[0]);
                return;
            }
            boolean b2 = false;
            boolean b3 = false;
            for (final CopyOption copyOption : array3) {
                if (copyOption == StandardCopyOption.REPLACE_EXISTING) {
                    b2 = true;
                }
                else if (copyOption == StandardCopyOption.COPY_ATTRIBUTES) {
                    b3 = true;
                }
            }
            if (this.getEntry0(array2) != null) {
                if (!b2) {
                    throw new FileAlreadyExistsException(this.getString(array2));
                }
            }
            else {
                this.checkParents(array2);
            }
            final Entry entry2 = new Entry(entry0, 4);
            entry2.name(array2);
            if (entry0.type == 2 || entry0.type == 3) {
                entry2.type = entry0.type;
                if (b) {
                    entry2.bytes = entry0.bytes;
                    entry2.file = entry0.file;
                }
                else if (entry0.bytes != null) {
                    entry2.bytes = Arrays.copyOf(entry0.bytes, entry0.bytes.length);
                }
                else if (entry0.file != null) {
                    entry2.file = this.getTempPathForEntry(null);
                    Files.copy(entry0.file, entry2.file, StandardCopyOption.REPLACE_EXISTING);
                }
            }
            if (!b3) {
                final Entry entry3 = entry2;
                final Entry entry4 = entry2;
                final Entry entry5 = entry2;
                final long currentTimeMillis = System.currentTimeMillis();
                entry5.ctime = currentTimeMillis;
                entry4.atime = currentTimeMillis;
                entry3.mtime = currentTimeMillis;
            }
            this.update(entry2);
            if (b) {
                this.updateDelete(entry0);
            }
        }
        finally {
            this.endWrite();
        }
    }
    
    OutputStream newOutputStream(final byte[] array, final OpenOption... array2) throws IOException {
        this.checkWritable();
        boolean b = false;
        boolean b2 = false;
        boolean b3 = false;
        boolean b4 = false;
        for (final OpenOption openOption : array2) {
            if (openOption == StandardOpenOption.READ) {
                throw new IllegalArgumentException("READ not allowed");
            }
            if (openOption == StandardOpenOption.CREATE_NEW) {
                b = true;
            }
            if (openOption == StandardOpenOption.CREATE) {
                b2 = true;
            }
            if (openOption == StandardOpenOption.APPEND) {
                b3 = true;
            }
            if (openOption == StandardOpenOption.TRUNCATE_EXISTING) {
                b4 = true;
            }
        }
        if (b3 && b4) {
            throw new IllegalArgumentException("APPEND + TRUNCATE_EXISTING not allowed");
        }
        this.beginRead();
        try {
            this.ensureOpen();
            final Entry entry0 = this.getEntry0(array);
            if (entry0 != null) {
                if (entry0.isDir() || b) {
                    throw new FileAlreadyExistsException(this.getString(array));
                }
                if (b3) {
                    final InputStream inputStream = this.getInputStream(entry0);
                    final OutputStream outputStream = this.getOutputStream(new Entry(entry0, 2));
                    copyStream(inputStream, outputStream);
                    inputStream.close();
                    return outputStream;
                }
                return this.getOutputStream(new Entry(entry0, 2));
            }
            else {
                if (!b2 && !b) {
                    throw new NoSuchFileException(this.getString(array));
                }
                this.checkParents(array);
                return this.getOutputStream(new Entry(array, 2));
            }
        }
        finally {
            this.endRead();
        }
    }
    
    InputStream newInputStream(final byte[] array) throws IOException {
        this.beginRead();
        try {
            this.ensureOpen();
            final Entry entry0 = this.getEntry0(array);
            if (entry0 == null) {
                throw new NoSuchFileException(this.getString(array));
            }
            if (entry0.isDir()) {
                throw new FileSystemException(this.getString(array), "is a directory", null);
            }
            return this.getInputStream(entry0);
        }
        finally {
            this.endRead();
        }
    }
    
    private void checkOptions(final Set<? extends OpenOption> set) {
        for (final OpenOption openOption : set) {
            if (openOption == null) {
                throw new NullPointerException();
            }
            if (!(openOption instanceof StandardOpenOption)) {
                throw new IllegalArgumentException();
            }
        }
        if (set.contains(StandardOpenOption.APPEND) && set.contains(StandardOpenOption.TRUNCATE_EXISTING)) {
            throw new IllegalArgumentException("APPEND + TRUNCATE_EXISTING not allowed");
        }
    }
    
    SeekableByteChannel newByteChannel(final byte[] array, final Set<? extends OpenOption> set, final FileAttribute<?>... array2) throws IOException {
        this.checkOptions(set);
        if (set.contains(StandardOpenOption.WRITE) || set.contains(StandardOpenOption.APPEND)) {
            this.checkWritable();
            this.beginRead();
            try {
                final WritableByteChannel channel = Channels.newChannel(this.newOutputStream(array, (OpenOption[])set.toArray(new OpenOption[0])));
                long size = 0L;
                if (set.contains(StandardOpenOption.APPEND)) {
                    final Entry entry0 = this.getEntry0(array);
                    if (entry0 != null && entry0.size >= 0L) {
                        size = entry0.size;
                    }
                }
                return new SeekableByteChannel() {
                    long written = size;
                    
                    @Override
                    public boolean isOpen() {
                        return channel.isOpen();
                    }
                    
                    @Override
                    public long position() throws IOException {
                        return this.written;
                    }
                    
                    @Override
                    public SeekableByteChannel position(final long n) throws IOException {
                        throw new UnsupportedOperationException();
                    }
                    
                    @Override
                    public int read(final ByteBuffer byteBuffer) throws IOException {
                        throw new UnsupportedOperationException();
                    }
                    
                    @Override
                    public SeekableByteChannel truncate(final long n) throws IOException {
                        throw new UnsupportedOperationException();
                    }
                    
                    @Override
                    public int write(final ByteBuffer byteBuffer) throws IOException {
                        final int write = channel.write(byteBuffer);
                        this.written += write;
                        return write;
                    }
                    
                    @Override
                    public long size() throws IOException {
                        return this.written;
                    }
                    
                    @Override
                    public void close() throws IOException {
                        channel.close();
                    }
                };
            }
            finally {
                this.endRead();
            }
        }
        this.beginRead();
        try {
            this.ensureOpen();
            final Entry entry2 = this.getEntry0(array);
            if (entry2 == null || entry2.isDir()) {
                throw new NoSuchFileException(this.getString(array));
            }
            return new SeekableByteChannel() {
                long read = 0L;
                final /* synthetic */ ReadableByteChannel val$rbc = Channels.newChannel(ZipFileSystem.this.getInputStream(entry2));
                final /* synthetic */ long val$size = entry2.size;
                
                @Override
                public boolean isOpen() {
                    return this.val$rbc.isOpen();
                }
                
                @Override
                public long position() throws IOException {
                    return this.read;
                }
                
                @Override
                public SeekableByteChannel position(final long n) throws IOException {
                    throw new UnsupportedOperationException();
                }
                
                @Override
                public int read(final ByteBuffer byteBuffer) throws IOException {
                    final int read = this.val$rbc.read(byteBuffer);
                    if (read > 0) {
                        this.read += read;
                    }
                    return read;
                }
                
                @Override
                public SeekableByteChannel truncate(final long n) throws IOException {
                    throw new NonWritableChannelException();
                }
                
                @Override
                public int write(final ByteBuffer byteBuffer) throws IOException {
                    throw new NonWritableChannelException();
                }
                
                @Override
                public long size() throws IOException {
                    return this.val$size;
                }
                
                @Override
                public void close() throws IOException {
                    this.val$rbc.close();
                }
            };
        }
        finally {
            this.endRead();
        }
    }
    
    FileChannel newFileChannel(final byte[] array, Set<? extends OpenOption> set, final FileAttribute<?>... array2) throws IOException {
        this.checkOptions(set);
        final boolean b = set.contains(StandardOpenOption.WRITE) || set.contains(StandardOpenOption.APPEND);
        this.beginRead();
        try {
            this.ensureOpen();
            final Entry entry0 = this.getEntry0(array);
            if (b) {
                this.checkWritable();
                if (entry0 == null) {
                    if (!set.contains(StandardOpenOption.CREATE) && !set.contains(StandardOpenOption.CREATE_NEW)) {
                        throw new NoSuchFileException(this.getString(array));
                    }
                }
                else {
                    if (set.contains(StandardOpenOption.CREATE_NEW)) {
                        throw new FileAlreadyExistsException(this.getString(array));
                    }
                    if (entry0.isDir()) {
                        throw new FileAlreadyExistsException("directory <" + this.getString(array) + "> exists");
                    }
                }
                set = new HashSet(set);
                set.remove(StandardOpenOption.CREATE_NEW);
            }
            else if (entry0 == null || entry0.isDir()) {
                throw new NoSuchFileException(this.getString(array));
            }
            final boolean b2 = entry0 != null && entry0.type == 3;
            final Path path = b2 ? entry0.file : this.getTempPathForEntry(array);
            final FileChannel fileChannel = path.getFileSystem().provider().newFileChannel(path, set, array2);
            final Entry entry2 = b2 ? entry0 : new Entry(array, path, 3);
            if (b) {
                entry2.flag = 8;
                entry2.method = 8;
            }
            return new FileChannel() {
                @Override
                public int write(final ByteBuffer byteBuffer) throws IOException {
                    return fileChannel.write(byteBuffer);
                }
                
                @Override
                public long write(final ByteBuffer[] array, final int n, final int n2) throws IOException {
                    return fileChannel.write(array, n, n2);
                }
                
                @Override
                public long position() throws IOException {
                    return fileChannel.position();
                }
                
                @Override
                public FileChannel position(final long n) throws IOException {
                    fileChannel.position(n);
                    return this;
                }
                
                @Override
                public long size() throws IOException {
                    return fileChannel.size();
                }
                
                @Override
                public FileChannel truncate(final long n) throws IOException {
                    fileChannel.truncate(n);
                    return this;
                }
                
                @Override
                public void force(final boolean b) throws IOException {
                    fileChannel.force(b);
                }
                
                @Override
                public long transferTo(final long n, final long n2, final WritableByteChannel writableByteChannel) throws IOException {
                    return fileChannel.transferTo(n, n2, writableByteChannel);
                }
                
                @Override
                public long transferFrom(final ReadableByteChannel readableByteChannel, final long n, final long n2) throws IOException {
                    return fileChannel.transferFrom(readableByteChannel, n, n2);
                }
                
                @Override
                public int read(final ByteBuffer byteBuffer) throws IOException {
                    return fileChannel.read(byteBuffer);
                }
                
                @Override
                public int read(final ByteBuffer byteBuffer, final long n) throws IOException {
                    return fileChannel.read(byteBuffer, n);
                }
                
                @Override
                public long read(final ByteBuffer[] array, final int n, final int n2) throws IOException {
                    return fileChannel.read(array, n, n2);
                }
                
                @Override
                public int write(final ByteBuffer byteBuffer, final long n) throws IOException {
                    return fileChannel.write(byteBuffer, n);
                }
                
                @Override
                public MappedByteBuffer map(final MapMode mapMode, final long n, final long n2) throws IOException {
                    throw new UnsupportedOperationException();
                }
                
                @Override
                public FileLock lock(final long n, final long n2, final boolean b) throws IOException {
                    return fileChannel.lock(n, n2, b);
                }
                
                @Override
                public FileLock tryLock(final long n, final long n2, final boolean b) throws IOException {
                    return fileChannel.tryLock(n, n2, b);
                }
                
                @Override
                protected void implCloseChannel() throws IOException {
                    fileChannel.close();
                    if (b) {
                        entry2.mtime = System.currentTimeMillis();
                        entry2.size = Files.size(entry2.file);
                        ZipFileSystem.this.update(entry2);
                    }
                    else if (!b2) {
                        ZipFileSystem.this.removeTempPathForEntry(path);
                    }
                }
            };
        }
        finally {
            this.endRead();
        }
    }
    
    private Path getTempPathForEntry(final byte[] array) throws IOException {
        final Path tempFileInSameDirectoryAs = this.createTempFileInSameDirectoryAs(this.zfpath);
        if (array != null && this.getEntry0(array) != null) {
            try (final InputStream inputStream = this.newInputStream(array)) {
                Files.copy(inputStream, tempFileInSameDirectoryAs, StandardCopyOption.REPLACE_EXISTING);
            }
        }
        return tempFileInSameDirectoryAs;
    }
    
    private void removeTempPathForEntry(final Path path) throws IOException {
        Files.delete(path);
        this.tmppaths.remove(path);
    }
    
    private void checkParents(byte[] parent) throws IOException {
        this.beginRead();
        try {
            while ((parent = getParent(parent)) != null && parent.length != 0) {
                if (!this.inodes.containsKey(IndexNode.keyOf(parent))) {
                    throw new NoSuchFileException(this.getString(parent));
                }
            }
        }
        finally {
            this.endRead();
        }
    }
    
    private static byte[] getParent(final byte[] array) {
        int n = array.length - 1;
        if (n > 0 && array[n] == 47) {
            --n;
        }
        while (n > 0 && array[n] != 47) {
            --n;
        }
        if (n <= 0) {
            return ZipFileSystem.ROOTPATH;
        }
        return Arrays.copyOf(array, n + 1);
    }
    
    private final void beginWrite() {
        this.rwlock.writeLock().lock();
    }
    
    private final void endWrite() {
        this.rwlock.writeLock().unlock();
    }
    
    private final void beginRead() {
        this.rwlock.readLock().lock();
    }
    
    private final void endRead() {
        this.rwlock.readLock().unlock();
    }
    
    final byte[] getBytes(final String s) {
        return this.zc.getBytes(s);
    }
    
    final String getString(final byte[] array) {
        return this.zc.toString(array);
    }
    
    @Override
    protected void finalize() throws IOException {
        this.close();
    }
    
    private long getDataPos(final Entry entry) throws IOException {
        if (entry.locoff == -1L) {
            final Entry entry2 = this.getEntry0(entry.name);
            if (entry2 == null) {
                throw new ZipException("invalid loc for entry <" + entry.name + ">");
            }
            entry.locoff = entry2.locoff;
        }
        final byte[] array = new byte[30];
        if (this.readFullyAt(array, 0, array.length, entry.locoff) != array.length) {
            throw new ZipException("invalid loc for entry <" + entry.name + ">");
        }
        return this.locpos + entry.locoff + 30L + ZipConstants.LOCNAM(array) + ZipConstants.LOCEXT(array);
    }
    
    final long readFullyAt(final byte[] array, final int n, final long n2, final long n3) throws IOException {
        final ByteBuffer wrap = ByteBuffer.wrap(array);
        wrap.position(n);
        wrap.limit((int)(n + n2));
        return this.readFullyAt(wrap, n3);
    }
    
    private final long readFullyAt(final ByteBuffer byteBuffer, final long n) throws IOException {
        synchronized (this.ch) {
            return this.ch.position(n).read(byteBuffer);
        }
    }
    
    private END findEND() throws IOException {
        final byte[] array = new byte[128];
        final long size = this.ch.size();
        for (long n = ((size - 65557L > 0L) ? (size - 65557L) : 0L) - (array.length - 22), n2 = size - array.length; n2 >= n; n2 -= array.length - 22) {
            int n3 = 0;
            if (n2 < 0L) {
                n3 = (int)(-n2);
                Arrays.fill(array, 0, n3, (byte)0);
            }
            final int n4 = array.length - n3;
            if (this.readFullyAt(array, n3, n4, n2 + n3) != n4) {
                zerror("zip END header not found");
            }
            for (int i = array.length - 22; i >= 0; --i) {
                if (array[i + 0] == 80 && array[i + 1] == 75 && array[i + 2] == 5 && array[i + 3] == 6 && n2 + i + 22L + ZipConstants.ENDCOM(array, i) == size) {
                    final byte[] copyOfRange = Arrays.copyOfRange(array, i, i + 22);
                    final END end = new END();
                    end.endsub = ZipConstants.ENDSUB(copyOfRange);
                    end.centot = ZipConstants.ENDTOT(copyOfRange);
                    end.cenlen = ZipConstants.ENDSIZ(copyOfRange);
                    end.cenoff = ZipConstants.ENDOFF(copyOfRange);
                    end.comlen = ZipConstants.ENDCOM(copyOfRange);
                    end.endpos = n2 + i;
                    if (end.cenlen == 4294967295L || end.cenoff == 4294967295L || end.centot == 65535) {
                        final byte[] array2 = new byte[20];
                        if (this.readFullyAt(array2, 0, array2.length, end.endpos - 20L) != array2.length) {
                            return end;
                        }
                        final long zip64_LOCOFF = ZipConstants.ZIP64_LOCOFF(array2);
                        final byte[] array3 = new byte[56];
                        if (this.readFullyAt(array3, 0, array3.length, zip64_LOCOFF) != array3.length) {
                            return end;
                        }
                        end.cenlen = ZipConstants.ZIP64_ENDSIZ(array3);
                        end.cenoff = ZipConstants.ZIP64_ENDOFF(array3);
                        end.centot = (int)ZipConstants.ZIP64_ENDTOT(array3);
                        end.endpos = zip64_LOCOFF;
                    }
                    return end;
                }
            }
        }
        zerror("zip END header not found");
        return null;
    }
    
    private byte[] initCEN() throws IOException {
        this.end = this.findEND();
        if (this.end.endpos == 0L) {
            this.inodes = new LinkedHashMap<IndexNode, IndexNode>(10);
            this.locpos = 0L;
            this.buildNodeTree();
            return null;
        }
        if (this.end.cenlen > this.end.endpos) {
            zerror("invalid END header (bad central directory size)");
        }
        final long n = this.end.endpos - this.end.cenlen;
        this.locpos = n - this.end.cenoff;
        if (this.locpos < 0L) {
            zerror("invalid END header (bad central directory offset)");
        }
        final byte[] array = new byte[(int)(this.end.cenlen + 22L)];
        if (this.readFullyAt(array, 0, array.length, n) != this.end.cenlen + 22L) {
            zerror("read CEN tables failed");
        }
        this.inodes = new LinkedHashMap<IndexNode, IndexNode>(this.end.centot + 1);
        int i = 0;
        int cennam;
        int cenext;
        int cencom;
        for (int n2 = array.length - 22; i < n2; i += 46 + cennam + cenext + cencom) {
            if (!ZipConstants.cenSigAt(array, i)) {
                zerror("invalid CEN header (bad signature)");
            }
            final int cenhow = ZipConstants.CENHOW(array, i);
            cennam = ZipConstants.CENNAM(array, i);
            cenext = ZipConstants.CENEXT(array, i);
            cencom = ZipConstants.CENCOM(array, i);
            if ((ZipConstants.CENFLG(array, i) & 0x1) != 0x0) {
                zerror("invalid CEN header (encrypted entry)");
            }
            if (cenhow != 0 && cenhow != 8) {
                zerror("invalid CEN header (unsupported compression method: " + cenhow + ")");
            }
            if (i + 46 + cennam > n2) {
                zerror("invalid CEN header (bad header size)");
            }
            final IndexNode indexNode = new IndexNode(Arrays.copyOfRange(array, i + 46, i + 46 + cennam), i);
            this.inodes.put(indexNode, indexNode);
        }
        if (i + 22 != array.length) {
            zerror("invalid CEN header (bad header size)");
        }
        this.buildNodeTree();
        return array;
    }
    
    private void ensureOpen() throws IOException {
        if (!this.isOpen) {
            throw new ClosedFileSystemException();
        }
    }
    
    private Path createTempFileInSameDirectoryAs(final Path path) throws IOException {
        final Path parent = path.toAbsolutePath().getParent();
        final Path tempFile = Files.createTempFile((parent == null) ? path.getFileSystem().getPath(".", new String[0]) : parent, "zipfstmp", null, (FileAttribute<?>[])new FileAttribute[0]);
        this.tmppaths.add(tempFile);
        return tempFile;
    }
    
    private void updateDelete(final IndexNode indexNode) {
        this.beginWrite();
        try {
            this.removeFromTree(indexNode);
            this.inodes.remove(indexNode);
            this.hasUpdate = true;
        }
        finally {
            this.endWrite();
        }
    }
    
    private void update(final Entry child) {
        this.beginWrite();
        try {
            final IndexNode indexNode = this.inodes.put(child, child);
            if (indexNode != null) {
                this.removeFromTree(indexNode);
            }
            if (child.type == 2 || child.type == 3 || child.type == 4) {
                final IndexNode indexNode2 = this.inodes.get(this.LOOKUPKEY.as(getParent(child.name)));
                child.sibling = indexNode2.child;
                indexNode2.child = child;
            }
            this.hasUpdate = true;
        }
        finally {
            this.endWrite();
        }
    }
    
    private long copyLOCEntry(final Entry entry, final boolean b, final OutputStream outputStream, long locoff, final byte[] array) throws IOException {
        final long locoff2 = entry.locoff;
        entry.locoff = locoff;
        long n = 0L;
        if ((entry.flag & 0x8) != 0x0) {
            if (entry.size >= 4294967295L || entry.csize >= 4294967295L) {
                n = 24L;
            }
            else {
                n = 16L;
            }
        }
        if (this.readFullyAt(array, 0, 30L, locoff2) != 30L) {
            throw new ZipException("loc: reading failed");
        }
        long n2;
        long n3;
        if (b) {
            n2 = locoff2 + (30 + ZipConstants.LOCNAM(array) + ZipConstants.LOCEXT(array));
            n3 = n + entry.csize;
            locoff = entry.writeLOC(outputStream) + n3;
        }
        else {
            outputStream.write(array, 0, 30);
            n2 = locoff2 + 30L;
            n3 = n + (ZipConstants.LOCNAM(array) + ZipConstants.LOCEXT(array) + entry.csize);
            locoff = 30L + n3;
        }
        int n4;
        while (n3 > 0L && (n4 = (int)this.readFullyAt(array, 0, array.length, n2)) != -1) {
            if (n3 < n4) {
                n4 = (int)n3;
            }
            outputStream.write(array, 0, n4);
            n3 -= n4;
            n2 += n4;
        }
        return locoff;
    }
    
    private void sync() throws IOException {
        if (!this.exChClosers.isEmpty()) {
            for (final ExChannelCloser exChannelCloser : this.exChClosers) {
                if (exChannelCloser.streams.isEmpty()) {
                    exChannelCloser.ch.close();
                    Files.delete(exChannelCloser.path);
                    this.exChClosers.remove(exChannelCloser);
                }
            }
        }
        if (!this.hasUpdate) {
            return;
        }
        final PosixFileAttributes posixAttributes = this.getPosixAttributes(this.zfpath);
        final Path tempFileInSameDirectoryAs = this.createTempFileInSameDirectoryAs(this.zfpath);
        try (final BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(Files.newOutputStream(tempFileInSameDirectoryAs, StandardOpenOption.WRITE))) {
            final ArrayList list = new ArrayList(this.inodes.size());
            long n = 0L;
            final byte[] array = new byte[8192];
            for (final IndexNode indexNode : this.inodes.values()) {
                if (indexNode instanceof Entry) {
                    final Entry entry = (Entry)indexNode;
                    try {
                        if (entry.type == 4) {
                            n += this.copyLOCEntry(entry, true, bufferedOutputStream, n, array);
                        }
                        else {
                            entry.locoff = n;
                            n += entry.writeLOC(bufferedOutputStream);
                            if (entry.bytes != null) {
                                bufferedOutputStream.write(entry.bytes);
                                n += entry.bytes.length;
                            }
                            else if (entry.file != null) {
                                try (final InputStream inputStream = Files.newInputStream(entry.file, new OpenOption[0])) {
                                    if (entry.type == 2) {
                                        int read;
                                        while ((read = inputStream.read(array)) != -1) {
                                            bufferedOutputStream.write(array, 0, read);
                                            n += read;
                                        }
                                    }
                                    else if (entry.type == 3) {
                                        try (final EntryOutputStream entryOutputStream = new EntryOutputStream(entry, bufferedOutputStream)) {
                                            int read2;
                                            while ((read2 = inputStream.read(array)) != -1) {
                                                entryOutputStream.write(array, 0, read2);
                                            }
                                        }
                                        n += entry.csize;
                                        if ((entry.flag & 0x8) != 0x0) {
                                            n += entry.writeEXT(bufferedOutputStream);
                                        }
                                    }
                                }
                                Files.delete(entry.file);
                                this.tmppaths.remove(entry.file);
                            }
                        }
                        list.add(entry);
                    }
                    catch (final IOException ex) {
                        ex.printStackTrace();
                    }
                }
                else {
                    if (indexNode.pos == -1) {
                        continue;
                    }
                    final Entry cen = Entry.readCEN(this, indexNode.pos);
                    try {
                        n += this.copyLOCEntry(cen, false, bufferedOutputStream, n, array);
                        list.add(cen);
                    }
                    catch (final IOException ex2) {
                        ex2.printStackTrace();
                    }
                }
            }
            this.end.cenoff = n;
            final Iterator iterator3 = list.iterator();
            while (iterator3.hasNext()) {
                n += ((Entry)iterator3.next()).writeCEN(bufferedOutputStream);
            }
            this.end.centot = list.size();
            this.end.cenlen = n - this.end.cenoff;
            this.end.write(bufferedOutputStream, n);
        }
        if (!this.streams.isEmpty()) {
            final ExChannelCloser exChannelCloser2 = new ExChannelCloser(this.createTempFileInSameDirectoryAs(this.zfpath), this.ch, this.streams);
            Files.move(this.zfpath, exChannelCloser2.path, StandardCopyOption.REPLACE_EXISTING);
            this.exChClosers.add(exChannelCloser2);
            this.streams = Collections.synchronizedSet(new HashSet<InputStream>());
        }
        else {
            this.ch.close();
            Files.delete(this.zfpath);
        }
        if (posixAttributes != null) {
            Files.setPosixFilePermissions(tempFileInSameDirectoryAs, posixAttributes.permissions());
        }
        Files.move(tempFileInSameDirectoryAs, this.zfpath, StandardCopyOption.REPLACE_EXISTING);
        this.hasUpdate = false;
    }
    
    private PosixFileAttributes getPosixAttributes(final Path path) throws IOException {
        try {
            final PosixFileAttributeView posixFileAttributeView = Files.getFileAttributeView(path, PosixFileAttributeView.class, new LinkOption[0]);
            if (posixFileAttributeView == null) {
                return null;
            }
            return posixFileAttributeView.readAttributes();
        }
        catch (final UnsupportedOperationException ex) {
            return null;
        }
    }
    
    private IndexNode getInode(byte[] copy) {
        if (copy == null) {
            throw new NullPointerException("path");
        }
        final IndexNode key = IndexNode.keyOf(copy);
        IndexNode indexNode = this.inodes.get(key);
        if (indexNode == null && (copy.length == 0 || copy[copy.length - 1] != 47)) {
            copy = Arrays.copyOf(copy, copy.length + 1);
            copy[copy.length - 1] = 47;
            indexNode = this.inodes.get(key.as(copy));
        }
        return indexNode;
    }
    
    private Entry getEntry0(final byte[] array) throws IOException {
        final IndexNode inode = this.getInode(array);
        if (inode instanceof Entry) {
            return (Entry)inode;
        }
        if (inode == null || inode.pos == -1) {
            return null;
        }
        return Entry.readCEN(this, inode.pos);
    }
    
    public void deleteFile(final byte[] array, final boolean b) throws IOException {
        this.checkWritable();
        final IndexNode inode = this.getInode(array);
        if (inode == null) {
            if (array != null && array.length == 0) {
                throw new ZipException("root directory </> can't not be delete");
            }
            if (b) {
                throw new NoSuchFileException(this.getString(array));
            }
        }
        else {
            if (inode.isDir() && inode.child != null) {
                throw new DirectoryNotEmptyException(this.getString(array));
            }
            this.updateDelete(inode);
        }
    }
    
    private static void copyStream(final InputStream inputStream, final OutputStream outputStream) throws IOException {
        final byte[] array = new byte[8192];
        int read;
        while ((read = inputStream.read(array)) != -1) {
            outputStream.write(array, 0, read);
        }
    }
    
    private OutputStream getOutputStream(final Entry entry) throws IOException {
        if (entry.mtime == -1L) {
            entry.mtime = System.currentTimeMillis();
        }
        if (entry.method == -1) {
            entry.method = 8;
        }
        entry.flag = 0;
        if (this.zc.isUTF8()) {
            entry.flag |= 0x800;
        }
        OutputStream outputStream;
        if (this.useTempFile) {
            entry.file = this.getTempPathForEntry(null);
            outputStream = Files.newOutputStream(entry.file, StandardOpenOption.WRITE);
        }
        else {
            outputStream = new ByteArrayOutputStream((entry.size > 0L) ? ((int)entry.size) : 8192);
        }
        return new EntryOutputStream(entry, outputStream);
    }
    
    private InputStream getInputStream(final Entry entry) throws IOException {
        InputStream inputStream;
        if (entry.type == 2) {
            if (entry.bytes != null) {
                inputStream = new ByteArrayInputStream(entry.bytes);
            }
            else {
                if (entry.file == null) {
                    throw new ZipException("update entry data is missing");
                }
                inputStream = Files.newInputStream(entry.file, new OpenOption[0]);
            }
        }
        else {
            if (entry.type == 3) {
                return Files.newInputStream(entry.file, new OpenOption[0]);
            }
            inputStream = new EntryInputStream(entry, this.ch);
        }
        if (entry.method == 8) {
            long n = entry.size + 2L;
            if (n > 65536L) {
                n = 8192L;
            }
            inputStream = new InflaterInputStream(inputStream, this.getInflater(), (int)n) {
                private boolean isClosed = false;
                private boolean eof;
                final /* synthetic */ long val$size = entry.size;
                
                @Override
                public void close() throws IOException {
                    if (!this.isClosed) {
                        ZipFileSystem.this.releaseInflater(this.inf);
                        this.in.close();
                        this.isClosed = true;
                        ZipFileSystem.this.streams.remove(this);
                    }
                }
                
                @Override
                protected void fill() throws IOException {
                    if (this.eof) {
                        throw new EOFException("Unexpected end of ZLIB input stream");
                    }
                    this.len = this.in.read(this.buf, 0, this.buf.length);
                    if (this.len == -1) {
                        this.buf[0] = 0;
                        this.len = 1;
                        this.eof = true;
                    }
                    this.inf.setInput(this.buf, 0, this.len);
                }
                
                @Override
                public int available() throws IOException {
                    if (this.isClosed) {
                        return 0;
                    }
                    final long n = this.val$size - this.inf.getBytesWritten();
                    return (n > 2147483647L) ? Integer.MAX_VALUE : ((int)n);
                }
            };
        }
        else if (entry.method != 0) {
            throw new ZipException("invalid compression method");
        }
        this.streams.add(inputStream);
        return inputStream;
    }
    
    static void zerror(final String s) {
        throw new ZipError(s);
    }
    
    private Inflater getInflater() {
        synchronized (this.inflaters) {
            final int size = this.inflaters.size();
            if (size > 0) {
                return this.inflaters.remove(size - 1);
            }
            return new Inflater(true);
        }
    }
    
    private void releaseInflater(final Inflater inflater) {
        synchronized (this.inflaters) {
            if (this.inflaters.size() < 20) {
                inflater.reset();
                this.inflaters.add(inflater);
            }
            else {
                inflater.end();
            }
        }
    }
    
    private Deflater getDeflater() {
        synchronized (this.deflaters) {
            final int size = this.deflaters.size();
            if (size > 0) {
                return this.deflaters.remove(size - 1);
            }
            return new Deflater(-1, true);
        }
    }
    
    private void releaseDeflater(final Deflater deflater) {
        synchronized (this.deflaters) {
            if (this.inflaters.size() < 20) {
                deflater.reset();
                this.deflaters.add(deflater);
            }
            else {
                deflater.end();
            }
        }
    }
    
    private void addToTree(final IndexNode child, final HashSet<IndexNode> set) {
        if (set.contains(child)) {
            return;
        }
        final byte[] name = child.name;
        final byte[] parent = getParent(name);
        IndexNode indexNode;
        if (this.inodes.containsKey(this.LOOKUPKEY.as(parent))) {
            indexNode = this.inodes.get(this.LOOKUPKEY);
        }
        else {
            indexNode = new IndexNode(parent, -1);
            this.inodes.put(indexNode, indexNode);
        }
        this.addToTree(indexNode, set);
        child.sibling = indexNode.child;
        indexNode.child = child;
        if (name[name.length - 1] == 47) {
            set.add(child);
        }
    }
    
    private void removeFromTree(final IndexNode indexNode) {
        final IndexNode indexNode2 = this.inodes.get(this.LOOKUPKEY.as(getParent(indexNode.name)));
        IndexNode indexNode3 = indexNode2.child;
        if (indexNode3.equals(indexNode)) {
            indexNode2.child = indexNode3.sibling;
        }
        else {
            IndexNode indexNode4 = indexNode3;
            while ((indexNode3 = indexNode3.sibling) != null) {
                if (indexNode3.equals(indexNode)) {
                    indexNode4.sibling = indexNode3.sibling;
                    break;
                }
                indexNode4 = indexNode3;
            }
        }
    }
    
    private void buildNodeTree() throws IOException {
        this.beginWrite();
        try {
            final HashSet set = new HashSet();
            final IndexNode indexNode = new IndexNode(ZipFileSystem.ROOTPATH, -1);
            this.inodes.put(indexNode, indexNode);
            set.add(indexNode);
            final IndexNode[] array = this.inodes.keySet().toArray(new IndexNode[0]);
            for (int length = array.length, i = 0; i < length; ++i) {
                this.addToTree(array[i], set);
            }
        }
        finally {
            this.endWrite();
        }
    }
    
    static {
        isWindows = System.getProperty("os.name").startsWith("Windows");
        supportedFileAttributeViews = Collections.unmodifiableSet((Set<? extends String>)new HashSet<String>(Arrays.asList("basic", "zip")));
        ZipFileSystem.ROOTPATH = new byte[0];
    }
    
    private class EntryInputStream extends InputStream
    {
        private final SeekableByteChannel zfch;
        private long pos;
        protected long rem;
        protected final long size;
        
        EntryInputStream(final Entry entry, final SeekableByteChannel zfch) throws IOException {
            this.zfch = zfch;
            this.rem = entry.csize;
            this.size = entry.size;
            this.pos = ZipFileSystem.this.getDataPos(entry);
        }
        
        @Override
        public int read(final byte[] array, final int n, int n2) throws IOException {
            ZipFileSystem.this.ensureOpen();
            if (this.rem == 0L) {
                return -1;
            }
            if (n2 <= 0) {
                return 0;
            }
            if (n2 > this.rem) {
                n2 = (int)this.rem;
            }
            long n3 = 0L;
            final ByteBuffer wrap = ByteBuffer.wrap(array);
            wrap.position(n);
            wrap.limit(n + n2);
            synchronized (this.zfch) {
                n3 = this.zfch.position(this.pos).read(wrap);
            }
            if (n3 > 0L) {
                this.pos += n3;
                this.rem -= n3;
            }
            if (this.rem == 0L) {
                this.close();
            }
            return (int)n3;
        }
        
        @Override
        public int read() throws IOException {
            final byte[] array = { 0 };
            if (this.read(array, 0, 1) == 1) {
                return array[0] & 0xFF;
            }
            return -1;
        }
        
        @Override
        public long skip(long rem) throws IOException {
            ZipFileSystem.this.ensureOpen();
            if (rem > this.rem) {
                rem = this.rem;
            }
            this.pos += rem;
            this.rem -= rem;
            if (this.rem == 0L) {
                this.close();
            }
            return rem;
        }
        
        @Override
        public int available() {
            return (this.rem > 2147483647L) ? Integer.MAX_VALUE : ((int)this.rem);
        }
        
        public long size() {
            return this.size;
        }
        
        @Override
        public void close() {
            this.rem = 0L;
            ZipFileSystem.this.streams.remove(this);
        }
    }
    
    class EntryOutputStream extends DeflaterOutputStream
    {
        private CRC32 crc;
        private Entry e;
        private long written;
        private boolean isClosed;
        
        EntryOutputStream(final Entry e, final OutputStream outputStream) throws IOException {
            super(outputStream, ZipFileSystem.this.getDeflater());
            this.isClosed = false;
            if (e == null) {
                throw new NullPointerException("Zip entry is null");
            }
            this.e = e;
            this.crc = new CRC32();
        }
        
        @Override
        public synchronized void write(final byte[] array, final int n, final int n2) throws IOException {
            if (this.e.type != 3) {
                ZipFileSystem.this.ensureOpen();
            }
            if (this.isClosed) {
                throw new IOException("Stream closed");
            }
            if (n < 0 || n2 < 0 || n > array.length - n2) {
                throw new IndexOutOfBoundsException();
            }
            if (n2 == 0) {
                return;
            }
            switch (this.e.method) {
                case 8: {
                    super.write(array, n, n2);
                    break;
                }
                case 0: {
                    this.written += n2;
                    this.out.write(array, n, n2);
                    break;
                }
                default: {
                    throw new ZipException("invalid compression method");
                }
            }
            this.crc.update(array, n, n2);
        }
        
        @Override
        public synchronized void close() throws IOException {
            if (this.isClosed) {
                return;
            }
            this.isClosed = true;
            switch (this.e.method) {
                case 8: {
                    this.finish();
                    this.e.size = this.def.getBytesRead();
                    this.e.csize = this.def.getBytesWritten();
                    this.e.crc = this.crc.getValue();
                    break;
                }
                case 0: {
                    final Entry e = this.e;
                    final Entry e2 = this.e;
                    final long written = this.written;
                    e2.csize = written;
                    e.size = written;
                    this.e.crc = this.crc.getValue();
                    break;
                }
                default: {
                    throw new ZipException("invalid compression method");
                }
            }
            if (this.out instanceof ByteArrayOutputStream) {
                this.e.bytes = ((ByteArrayOutputStream)this.out).toByteArray();
            }
            if (this.e.type == 3) {
                ZipFileSystem.this.releaseDeflater(this.def);
                return;
            }
            super.close();
            ZipFileSystem.this.releaseDeflater(this.def);
            ZipFileSystem.this.update(this.e);
        }
    }
    
    static class END
    {
        int disknum;
        int sdisknum;
        int endsub;
        int centot;
        long cenlen;
        long cenoff;
        int comlen;
        byte[] comment;
        int diskNum;
        long endpos;
        int disktot;
        
        void write(final OutputStream outputStream, final long n) throws IOException {
            boolean b = false;
            long cenlen = this.cenlen;
            long cenoff = this.cenoff;
            if (cenlen >= 4294967295L) {
                cenlen = 4294967295L;
                b = true;
            }
            if (cenoff >= 4294967295L) {
                cenoff = 4294967295L;
                b = true;
            }
            int centot = this.centot;
            if (centot >= 65535) {
                centot = 65535;
                b = true;
            }
            if (b) {
                ZipUtils.writeInt(outputStream, 101075792L);
                ZipUtils.writeLong(outputStream, 44L);
                ZipUtils.writeShort(outputStream, 45);
                ZipUtils.writeShort(outputStream, 45);
                ZipUtils.writeInt(outputStream, 0L);
                ZipUtils.writeInt(outputStream, 0L);
                ZipUtils.writeLong(outputStream, this.centot);
                ZipUtils.writeLong(outputStream, this.centot);
                ZipUtils.writeLong(outputStream, this.cenlen);
                ZipUtils.writeLong(outputStream, this.cenoff);
                ZipUtils.writeInt(outputStream, 117853008L);
                ZipUtils.writeInt(outputStream, 0L);
                ZipUtils.writeLong(outputStream, n);
                ZipUtils.writeInt(outputStream, 1L);
            }
            ZipUtils.writeInt(outputStream, ZipConstants.ENDSIG);
            ZipUtils.writeShort(outputStream, 0);
            ZipUtils.writeShort(outputStream, 0);
            ZipUtils.writeShort(outputStream, centot);
            ZipUtils.writeShort(outputStream, centot);
            ZipUtils.writeInt(outputStream, cenlen);
            ZipUtils.writeInt(outputStream, cenoff);
            if (this.comment != null) {
                ZipUtils.writeShort(outputStream, this.comment.length);
                ZipUtils.writeBytes(outputStream, this.comment);
            }
            else {
                ZipUtils.writeShort(outputStream, 0);
            }
        }
    }
    
    static class IndexNode
    {
        byte[] name;
        int hashcode;
        int pos;
        IndexNode sibling;
        IndexNode child;
        
        IndexNode(final byte[] array, final int pos) {
            this.pos = -1;
            this.name(array);
            this.pos = pos;
        }
        
        static final IndexNode keyOf(final byte[] array) {
            return new IndexNode(array, -1);
        }
        
        final void name(final byte[] name) {
            this.name = name;
            this.hashcode = Arrays.hashCode(name);
        }
        
        final IndexNode as(final byte[] array) {
            this.name(array);
            return this;
        }
        
        boolean isDir() {
            return this.name != null && (this.name.length == 0 || this.name[this.name.length - 1] == 47);
        }
        
        @Override
        public boolean equals(final Object o) {
            return o instanceof IndexNode && Arrays.equals(this.name, ((IndexNode)o).name);
        }
        
        @Override
        public int hashCode() {
            return this.hashcode;
        }
        
        IndexNode() {
            this.pos = -1;
        }
    }
    
    static class Entry extends IndexNode
    {
        static final int CEN = 1;
        static final int NEW = 2;
        static final int FILECH = 3;
        static final int COPY = 4;
        byte[] bytes;
        Path file;
        int type;
        int version;
        int flag;
        int method;
        long mtime;
        long atime;
        long ctime;
        long crc;
        long csize;
        long size;
        byte[] extra;
        int versionMade;
        int disk;
        int attrs;
        long attrsEx;
        long locoff;
        byte[] comment;
        
        Entry() {
            this.type = 1;
            this.method = -1;
            this.mtime = -1L;
            this.atime = -1L;
            this.ctime = -1L;
            this.crc = -1L;
            this.csize = -1L;
            this.size = -1L;
        }
        
        Entry(final byte[] array) {
            this.type = 1;
            this.method = -1;
            this.mtime = -1L;
            this.atime = -1L;
            this.ctime = -1L;
            this.crc = -1L;
            this.csize = -1L;
            this.size = -1L;
            this.name(array);
            final long currentTimeMillis = System.currentTimeMillis();
            this.atime = currentTimeMillis;
            this.ctime = currentTimeMillis;
            this.mtime = currentTimeMillis;
            this.crc = 0L;
            this.size = 0L;
            this.csize = 0L;
            this.method = 8;
        }
        
        Entry(final byte[] array, final int type) {
            this(array);
            this.type = type;
        }
        
        Entry(final Entry entry, final int type) {
            this.type = 1;
            this.method = -1;
            this.mtime = -1L;
            this.atime = -1L;
            this.ctime = -1L;
            this.crc = -1L;
            this.csize = -1L;
            this.size = -1L;
            this.name(entry.name);
            this.version = entry.version;
            this.ctime = entry.ctime;
            this.atime = entry.atime;
            this.mtime = entry.mtime;
            this.crc = entry.crc;
            this.size = entry.size;
            this.csize = entry.csize;
            this.method = entry.method;
            this.extra = entry.extra;
            this.versionMade = entry.versionMade;
            this.disk = entry.disk;
            this.attrs = entry.attrs;
            this.attrsEx = entry.attrsEx;
            this.locoff = entry.locoff;
            this.comment = entry.comment;
            this.type = type;
        }
        
        Entry(final byte[] array, final Path file, final int n) {
            this(array, n);
            this.file = file;
            this.method = 0;
        }
        
        int version() throws ZipException {
            if (this.method == 8) {
                return 20;
            }
            if (this.method == 0) {
                return 10;
            }
            throw new ZipException("unsupported compression method");
        }
        
        static Entry readCEN(final ZipFileSystem zipFileSystem, final int n) throws IOException {
            return new Entry().cen(zipFileSystem, n);
        }
        
        private Entry cen(final ZipFileSystem zipFileSystem, int n) throws IOException {
            final byte[] cen = zipFileSystem.cen;
            if (!ZipConstants.cenSigAt(cen, n)) {
                ZipFileSystem.zerror("invalid CEN header (bad signature)");
            }
            this.versionMade = ZipConstants.CENVEM(cen, n);
            this.version = ZipConstants.CENVER(cen, n);
            this.flag = ZipConstants.CENFLG(cen, n);
            this.method = ZipConstants.CENHOW(cen, n);
            this.mtime = ZipUtils.dosToJavaTime(ZipConstants.CENTIM(cen, n));
            this.crc = ZipConstants.CENCRC(cen, n);
            this.csize = ZipConstants.CENSIZ(cen, n);
            this.size = ZipConstants.CENLEN(cen, n);
            final int cennam = ZipConstants.CENNAM(cen, n);
            final int cenext = ZipConstants.CENEXT(cen, n);
            final int cencom = ZipConstants.CENCOM(cen, n);
            this.disk = ZipConstants.CENDSK(cen, n);
            this.attrs = ZipConstants.CENATT(cen, n);
            this.attrsEx = ZipConstants.CENATX(cen, n);
            this.locoff = ZipConstants.CENOFF(cen, n);
            n += 46;
            this.name(Arrays.copyOfRange(cen, n, n + cennam));
            n += cennam;
            if (cenext > 0) {
                this.extra = Arrays.copyOfRange(cen, n, n + cenext);
                n += cenext;
                this.readExtra(zipFileSystem);
            }
            if (cencom > 0) {
                this.comment = Arrays.copyOfRange(cen, n, n + cencom);
            }
            return this;
        }
        
        int writeCEN(final OutputStream outputStream) throws IOException {
            final int version = this.version();
            long csize = this.csize;
            long size = this.size;
            long locoff = this.locoff;
            int n = 0;
            int n2 = 0;
            int n3 = 0;
            boolean b = false;
            final int n4 = (this.name != null) ? this.name.length : 0;
            final int n5 = (this.extra != null) ? this.extra.length : 0;
            int n6 = 0;
            final int n7 = (this.comment != null) ? this.comment.length : 0;
            if (this.csize >= 4294967295L) {
                csize = 4294967295L;
                n += 8;
            }
            if (this.size >= 4294967295L) {
                size = 4294967295L;
                n += 8;
            }
            if (this.locoff >= 4294967295L) {
                locoff = 4294967295L;
                n += 8;
            }
            if (n != 0) {
                n += 4;
            }
            while (n6 + 4 < n5) {
                final int sh = ZipConstants.SH(this.extra, n6);
                final int sh2 = ZipConstants.SH(this.extra, n6 + 2);
                if (sh == 21589 || sh == 10) {
                    b = true;
                }
                n6 += 4 + sh2;
            }
            if (!b) {
                if (ZipFileSystem.isWindows) {
                    n2 = 36;
                }
                else {
                    n3 = 9;
                }
            }
            ZipUtils.writeInt(outputStream, ZipConstants.CENSIG);
            if (n != 0) {
                ZipUtils.writeShort(outputStream, 45);
                ZipUtils.writeShort(outputStream, 45);
            }
            else {
                ZipUtils.writeShort(outputStream, version);
                ZipUtils.writeShort(outputStream, version);
            }
            ZipUtils.writeShort(outputStream, this.flag);
            ZipUtils.writeShort(outputStream, this.method);
            ZipUtils.writeInt(outputStream, (int)ZipUtils.javaToDosTime(this.mtime));
            ZipUtils.writeInt(outputStream, this.crc);
            ZipUtils.writeInt(outputStream, csize);
            ZipUtils.writeInt(outputStream, size);
            ZipUtils.writeShort(outputStream, this.name.length);
            ZipUtils.writeShort(outputStream, n5 + n + n2 + n3);
            if (this.comment != null) {
                ZipUtils.writeShort(outputStream, Math.min(n7, 65535));
            }
            else {
                ZipUtils.writeShort(outputStream, 0);
            }
            ZipUtils.writeShort(outputStream, 0);
            ZipUtils.writeShort(outputStream, 0);
            ZipUtils.writeInt(outputStream, 0L);
            ZipUtils.writeInt(outputStream, locoff);
            ZipUtils.writeBytes(outputStream, this.name);
            if (n != 0) {
                ZipUtils.writeShort(outputStream, 1);
                ZipUtils.writeShort(outputStream, n - 4);
                if (size == 4294967295L) {
                    ZipUtils.writeLong(outputStream, this.size);
                }
                if (csize == 4294967295L) {
                    ZipUtils.writeLong(outputStream, this.csize);
                }
                if (locoff == 4294967295L) {
                    ZipUtils.writeLong(outputStream, this.locoff);
                }
            }
            if (n2 != 0) {
                ZipUtils.writeShort(outputStream, 10);
                ZipUtils.writeShort(outputStream, n2 - 4);
                ZipUtils.writeInt(outputStream, 0L);
                ZipUtils.writeShort(outputStream, 1);
                ZipUtils.writeShort(outputStream, 24);
                ZipUtils.writeLong(outputStream, ZipUtils.javaToWinTime(this.mtime));
                ZipUtils.writeLong(outputStream, ZipUtils.javaToWinTime(this.atime));
                ZipUtils.writeLong(outputStream, ZipUtils.javaToWinTime(this.ctime));
            }
            if (n3 != 0) {
                ZipUtils.writeShort(outputStream, 21589);
                ZipUtils.writeShort(outputStream, n3 - 4);
                if (this.ctime == -1L) {
                    outputStream.write(3);
                }
                else {
                    outputStream.write(7);
                }
                ZipUtils.writeInt(outputStream, ZipUtils.javaToUnixTime(this.mtime));
            }
            if (this.extra != null) {
                ZipUtils.writeBytes(outputStream, this.extra);
            }
            if (this.comment != null) {
                ZipUtils.writeBytes(outputStream, this.comment);
            }
            return 46 + n4 + n5 + n7 + n + n2 + n3;
        }
        
        static Entry readLOC(final ZipFileSystem zipFileSystem, final long n) throws IOException {
            return readLOC(zipFileSystem, n, new byte[1024]);
        }
        
        static Entry readLOC(final ZipFileSystem zipFileSystem, final long n, final byte[] array) throws IOException {
            return new Entry().loc(zipFileSystem, n, array);
        }
        
        Entry loc(final ZipFileSystem zipFileSystem, long n, final byte[] array) throws IOException {
            assert array.length >= 30;
            if (zipFileSystem.readFullyAt(array, 0, 30L, n) != 30L) {
                throw new ZipException("loc: reading failed");
            }
            if (!ZipConstants.locSigAt(array, 0)) {
                throw new ZipException("loc: wrong sig ->" + Long.toString(ZipConstants.getSig(array, 0), 16));
            }
            this.version = ZipConstants.LOCVER(array);
            this.flag = ZipConstants.LOCFLG(array);
            this.method = ZipConstants.LOCHOW(array);
            this.mtime = ZipUtils.dosToJavaTime(ZipConstants.LOCTIM(array));
            this.crc = ZipConstants.LOCCRC(array);
            this.csize = ZipConstants.LOCSIZ(array);
            this.size = ZipConstants.LOCLEN(array);
            final int locnam = ZipConstants.LOCNAM(array);
            final int locext = ZipConstants.LOCEXT(array);
            this.name = new byte[locnam];
            if (zipFileSystem.readFullyAt(this.name, 0, locnam, n + 30L) != locnam) {
                throw new ZipException("loc: name reading failed");
            }
            if (locext > 0) {
                this.extra = new byte[locext];
                if (zipFileSystem.readFullyAt(this.extra, 0, locext, n + 30L + locnam) != locext) {
                    throw new ZipException("loc: ext reading failed");
                }
            }
            n += 30 + locnam + locext;
            if ((this.flag & 0x8) != 0x0) {
                final Entry access$900 = zipFileSystem.getEntry0(this.name);
                if (access$900 == null) {
                    throw new ZipException("loc: name not found in cen");
                }
                this.size = access$900.size;
                this.csize = access$900.csize;
                n += ((this.method == 0) ? this.size : this.csize);
                if (this.size >= 4294967295L || this.csize >= 4294967295L) {
                    n += 24L;
                }
                else {
                    n += 16L;
                }
            }
            else {
                if (this.extra != null && (this.size == 4294967295L || this.csize == 4294967295L)) {
                    int sh;
                    for (int n2 = 0; n2 + 20 < locext; n2 += sh + 4) {
                        sh = ZipConstants.SH(this.extra, n2 + 2);
                        if (ZipConstants.SH(this.extra, n2) == 1 && sh == 16) {
                            this.size = ZipConstants.LL(this.extra, n2 + 4);
                            this.csize = ZipConstants.LL(this.extra, n2 + 12);
                            break;
                        }
                    }
                }
                n += ((this.method == 0) ? this.size : this.csize);
            }
            return this;
        }
        
        int writeLOC(final OutputStream outputStream) throws IOException {
            ZipUtils.writeInt(outputStream, ZipConstants.LOCSIG);
            this.version();
            final int n = (this.name != null) ? this.name.length : 0;
            final int n2 = (this.extra != null) ? this.extra.length : 0;
            boolean b = false;
            int n3 = 0;
            int n4 = 0;
            int n5 = 0;
            int n6 = 0;
            if ((this.flag & 0x8) != 0x0) {
                ZipUtils.writeShort(outputStream, this.version());
                ZipUtils.writeShort(outputStream, this.flag);
                ZipUtils.writeShort(outputStream, this.method);
                ZipUtils.writeInt(outputStream, (int)ZipUtils.javaToDosTime(this.mtime));
                ZipUtils.writeInt(outputStream, 0L);
                ZipUtils.writeInt(outputStream, 0L);
                ZipUtils.writeInt(outputStream, 0L);
            }
            else {
                if (this.csize >= 4294967295L || this.size >= 4294967295L) {
                    n4 = 20;
                    ZipUtils.writeShort(outputStream, 45);
                }
                else {
                    ZipUtils.writeShort(outputStream, this.version());
                }
                ZipUtils.writeShort(outputStream, this.flag);
                ZipUtils.writeShort(outputStream, this.method);
                ZipUtils.writeInt(outputStream, (int)ZipUtils.javaToDosTime(this.mtime));
                ZipUtils.writeInt(outputStream, this.crc);
                if (n4 != 0) {
                    ZipUtils.writeInt(outputStream, 4294967295L);
                    ZipUtils.writeInt(outputStream, 4294967295L);
                }
                else {
                    ZipUtils.writeInt(outputStream, this.csize);
                    ZipUtils.writeInt(outputStream, this.size);
                }
            }
            while (n3 + 4 < n2) {
                final int sh = ZipConstants.SH(this.extra, n3);
                final int sh2 = ZipConstants.SH(this.extra, n3 + 2);
                if (sh == 21589 || sh == 10) {
                    b = true;
                }
                n3 += 4 + sh2;
            }
            if (!b) {
                if (ZipFileSystem.isWindows) {
                    n6 = 36;
                }
                else {
                    n5 = 9;
                    if (this.atime != -1L) {
                        n5 += 4;
                    }
                    if (this.ctime != -1L) {
                        n5 += 4;
                    }
                }
            }
            ZipUtils.writeShort(outputStream, this.name.length);
            ZipUtils.writeShort(outputStream, n2 + n4 + n6 + n5);
            ZipUtils.writeBytes(outputStream, this.name);
            if (n4 != 0) {
                ZipUtils.writeShort(outputStream, 1);
                ZipUtils.writeShort(outputStream, 16);
                ZipUtils.writeLong(outputStream, this.size);
                ZipUtils.writeLong(outputStream, this.csize);
            }
            if (n6 != 0) {
                ZipUtils.writeShort(outputStream, 10);
                ZipUtils.writeShort(outputStream, n6 - 4);
                ZipUtils.writeInt(outputStream, 0L);
                ZipUtils.writeShort(outputStream, 1);
                ZipUtils.writeShort(outputStream, 24);
                ZipUtils.writeLong(outputStream, ZipUtils.javaToWinTime(this.mtime));
                ZipUtils.writeLong(outputStream, ZipUtils.javaToWinTime(this.atime));
                ZipUtils.writeLong(outputStream, ZipUtils.javaToWinTime(this.ctime));
            }
            if (n5 != 0) {
                ZipUtils.writeShort(outputStream, 21589);
                ZipUtils.writeShort(outputStream, n5 - 4);
                int n7 = 1;
                if (this.atime != -1L) {
                    n7 |= 0x2;
                }
                if (this.ctime != -1L) {
                    n7 |= 0x4;
                }
                outputStream.write(n7);
                ZipUtils.writeInt(outputStream, ZipUtils.javaToUnixTime(this.mtime));
                if (this.atime != -1L) {
                    ZipUtils.writeInt(outputStream, ZipUtils.javaToUnixTime(this.atime));
                }
                if (this.ctime != -1L) {
                    ZipUtils.writeInt(outputStream, ZipUtils.javaToUnixTime(this.ctime));
                }
            }
            if (this.extra != null) {
                ZipUtils.writeBytes(outputStream, this.extra);
            }
            return 30 + this.name.length + n2 + n4 + n6 + n5;
        }
        
        int writeEXT(final OutputStream outputStream) throws IOException {
            ZipUtils.writeInt(outputStream, ZipConstants.EXTSIG);
            ZipUtils.writeInt(outputStream, this.crc);
            if (this.csize >= 4294967295L || this.size >= 4294967295L) {
                ZipUtils.writeLong(outputStream, this.csize);
                ZipUtils.writeLong(outputStream, this.size);
                return 24;
            }
            ZipUtils.writeInt(outputStream, this.csize);
            ZipUtils.writeInt(outputStream, this.size);
            return 16;
        }
        
        void readExtra(final ZipFileSystem zipFileSystem) throws IOException {
            if (this.extra == null) {
                return;
            }
            final int length = this.extra.length;
            int n = 0;
            int n2 = 0;
            while (n + 4 < length) {
                int n3 = n;
                final int sh = ZipConstants.SH(this.extra, n3);
                final int sh2 = ZipConstants.SH(this.extra, n3 + 2);
                n3 += 4;
                if (n3 + sh2 > length) {
                    break;
                }
                switch (sh) {
                    case 1: {
                        if (this.size == 4294967295L) {
                            if (n3 + 8 > length) {
                                break;
                            }
                            this.size = ZipConstants.LL(this.extra, n3);
                            n3 += 8;
                        }
                        if (this.csize == 4294967295L) {
                            if (n3 + 8 > length) {
                                break;
                            }
                            this.csize = ZipConstants.LL(this.extra, n3);
                            n3 += 8;
                        }
                        if (this.locoff != 4294967295L) {
                            break;
                        }
                        if (n3 + 8 > length) {
                            break;
                        }
                        this.locoff = ZipConstants.LL(this.extra, n3);
                        n3 += 8;
                        break;
                    }
                    case 10: {
                        if (sh2 < 32) {
                            break;
                        }
                        n3 += 4;
                        if (ZipConstants.SH(this.extra, n3) != 1) {
                            break;
                        }
                        if (ZipConstants.SH(this.extra, n3 + 2) != 24) {
                            break;
                        }
                        this.mtime = ZipUtils.winToJavaTime(ZipConstants.LL(this.extra, n3 + 4));
                        this.atime = ZipUtils.winToJavaTime(ZipConstants.LL(this.extra, n3 + 12));
                        this.ctime = ZipUtils.winToJavaTime(ZipConstants.LL(this.extra, n3 + 20));
                        break;
                    }
                    case 21589: {
                        final byte[] array = new byte[30];
                        if (zipFileSystem.readFullyAt(array, 0, array.length, this.locoff) != array.length) {
                            throw new ZipException("loc: reading failed");
                        }
                        if (!ZipConstants.locSigAt(array, 0)) {
                            throw new ZipException("loc: wrong sig ->" + Long.toString(ZipConstants.getSig(array, 0), 16));
                        }
                        final int locext = ZipConstants.LOCEXT(array);
                        if (locext < 9) {
                            break;
                        }
                        final int locnam = ZipConstants.LOCNAM(array);
                        final byte[] array2 = new byte[locext];
                        if (zipFileSystem.readFullyAt(array2, 0, array2.length, this.locoff + 30L + locnam) != array2.length) {
                            throw new ZipException("loc extra: reading failed");
                        }
                        int n4 = 0;
                        while (n4 + 4 < array2.length) {
                            final int sh3 = ZipConstants.SH(array2, n4);
                            final int sh4 = ZipConstants.SH(array2, n4 + 2);
                            n4 += 4;
                            if (sh3 != 21589) {
                                n4 += sh4;
                            }
                            else {
                                final int ch = ZipConstants.CH(array2, n4++);
                                if ((ch & 0x1) != 0x0) {
                                    this.mtime = ZipUtils.unixToJavaTime(ZipConstants.LG(array2, n4));
                                    n4 += 4;
                                }
                                if ((ch & 0x2) != 0x0) {
                                    this.atime = ZipUtils.unixToJavaTime(ZipConstants.LG(array2, n4));
                                    n4 += 4;
                                }
                                if ((ch & 0x4) != 0x0) {
                                    this.ctime = ZipUtils.unixToJavaTime(ZipConstants.LG(array2, n4));
                                    n4 += 4;
                                    break;
                                }
                                break;
                            }
                        }
                        break;
                    }
                    default: {
                        System.arraycopy(this.extra, n, this.extra, n2, sh2 + 4);
                        n2 += sh2 + 4;
                        break;
                    }
                }
                n += sh2 + 4;
            }
            if (n2 != 0 && n2 != this.extra.length) {
                this.extra = Arrays.copyOf(this.extra, n2);
            }
            else {
                this.extra = null;
            }
        }
    }
    
    private static class ExChannelCloser
    {
        Path path;
        SeekableByteChannel ch;
        Set<InputStream> streams;
        
        ExChannelCloser(final Path path, final SeekableByteChannel ch, final Set<InputStream> streams) {
            this.path = path;
            this.ch = ch;
            this.streams = streams;
        }
    }
}
