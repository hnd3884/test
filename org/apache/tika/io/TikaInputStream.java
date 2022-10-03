package org.apache.tika.io;

import java.nio.channels.FileChannel;
import java.nio.file.StandardCopyOption;
import java.nio.file.CopyOption;
import java.io.Closeable;
import java.net.URLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.LinkOption;
import java.nio.file.Paths;
import java.net.URI;
import java.sql.SQLException;
import java.sql.Blob;
import java.io.ByteArrayInputStream;
import org.apache.tika.metadata.Metadata;
import java.io.FileNotFoundException;
import java.io.FileInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import org.apache.commons.io.input.TaggedInputStream;

public class TikaInputStream extends TaggedInputStream
{
    private static final int MAX_CONSECUTIVE_EOFS = 1000;
    private static final int BLOB_SIZE_THRESHOLD = 1048576;
    private final TemporaryResources tmp;
    private InputStreamFactory streamFactory;
    private Path path;
    private long length;
    private long position;
    private long mark;
    private Object openContainer;
    private int consecutiveEOFs;
    private byte[] skipBuffer;
    
    private TikaInputStream(final Path path) throws IOException {
        super((InputStream)new BufferedInputStream(Files.newInputStream(path, new OpenOption[0])));
        this.position = 0L;
        this.mark = -1L;
        this.consecutiveEOFs = 0;
        this.path = path;
        this.tmp = new TemporaryResources();
        this.length = Files.size(path);
    }
    
    private TikaInputStream(final Path path, final TemporaryResources tmp, final long length) throws IOException {
        super((InputStream)new BufferedInputStream(Files.newInputStream(path, new OpenOption[0])));
        this.position = 0L;
        this.mark = -1L;
        this.consecutiveEOFs = 0;
        this.path = path;
        this.tmp = tmp;
        this.length = length;
    }
    
    @Deprecated
    private TikaInputStream(final File file) throws FileNotFoundException {
        super((InputStream)new BufferedInputStream(new FileInputStream(file)));
        this.position = 0L;
        this.mark = -1L;
        this.consecutiveEOFs = 0;
        this.path = file.toPath();
        this.tmp = new TemporaryResources();
        this.length = file.length();
    }
    
    private TikaInputStream(final InputStream stream, final TemporaryResources tmp, final long length) {
        super(stream);
        this.position = 0L;
        this.mark = -1L;
        this.consecutiveEOFs = 0;
        this.path = null;
        this.tmp = tmp;
        this.length = length;
    }
    
    public static boolean isTikaInputStream(final InputStream stream) {
        return stream instanceof TikaInputStream;
    }
    
    public static TikaInputStream get(InputStream stream, final TemporaryResources tmp) {
        if (stream == null) {
            throw new NullPointerException("The Stream must not be null");
        }
        if (stream instanceof TikaInputStream) {
            return (TikaInputStream)stream;
        }
        if (!stream.markSupported()) {
            stream = new BufferedInputStream(stream);
        }
        return new TikaInputStream(stream, tmp, -1L);
    }
    
    public static TikaInputStream get(final InputStream stream) {
        return get(stream, new TemporaryResources());
    }
    
    public static TikaInputStream cast(final InputStream stream) {
        if (stream instanceof TikaInputStream) {
            return (TikaInputStream)stream;
        }
        return null;
    }
    
    public static TikaInputStream get(final byte[] data) {
        return get(data, new Metadata());
    }
    
    public static TikaInputStream get(final byte[] data, final Metadata metadata) {
        metadata.set("Content-Length", Integer.toString(data.length));
        return new TikaInputStream(new ByteArrayInputStream(data), new TemporaryResources(), data.length);
    }
    
    public static TikaInputStream get(final Path path) throws IOException {
        return get(path, new Metadata());
    }
    
    public static TikaInputStream get(final Path path, final Metadata metadata) throws IOException {
        metadata.set("resourceName", path.getFileName().toString());
        metadata.set("Content-Length", Long.toString(Files.size(path)));
        return new TikaInputStream(path);
    }
    
    public static TikaInputStream get(final Path path, final Metadata metadata, final TemporaryResources tmp) throws IOException {
        final long length = Files.size(path);
        metadata.set("resourceName", path.getFileName().toString());
        metadata.set("Content-Length", Long.toString(length));
        return new TikaInputStream(path, tmp, length);
    }
    
    @Deprecated
    public static TikaInputStream get(final File file) throws FileNotFoundException {
        return get(file, new Metadata());
    }
    
    @Deprecated
    public static TikaInputStream get(final File file, final Metadata metadata) throws FileNotFoundException {
        metadata.set("resourceName", file.getName());
        metadata.set("Content-Length", Long.toString(file.length()));
        return new TikaInputStream(file);
    }
    
    public static TikaInputStream get(final InputStreamFactory factory) throws IOException {
        return get(factory, new TemporaryResources());
    }
    
    public static TikaInputStream get(final InputStreamFactory factory, final TemporaryResources tmp) throws IOException {
        final TikaInputStream stream = get(factory.getInputStream(), tmp);
        stream.streamFactory = factory;
        return stream;
    }
    
    public static TikaInputStream get(final Blob blob) throws SQLException {
        return get(blob, new Metadata());
    }
    
    public static TikaInputStream get(final Blob blob, final Metadata metadata) throws SQLException {
        long length = -1L;
        try {
            length = blob.length();
            metadata.set("Content-Length", Long.toString(length));
        }
        catch (final SQLException ex) {}
        if (0L <= length && length <= 1048576L) {
            return get(blob.getBytes(1L, (int)length), metadata);
        }
        return new TikaInputStream(new BufferedInputStream(blob.getBinaryStream()), new TemporaryResources(), length);
    }
    
    public static TikaInputStream get(final URI uri) throws IOException {
        return get(uri, new Metadata());
    }
    
    public static TikaInputStream get(final URI uri, final Metadata metadata) throws IOException {
        if ("file".equalsIgnoreCase(uri.getScheme())) {
            final Path path = Paths.get(uri);
            if (Files.isRegularFile(path, new LinkOption[0])) {
                return get(path, metadata);
            }
        }
        return get(uri.toURL(), metadata);
    }
    
    public static TikaInputStream get(final URL url) throws IOException {
        return get(url, new Metadata());
    }
    
    public static TikaInputStream get(final URL url, final Metadata metadata) throws IOException {
        if ("file".equalsIgnoreCase(url.getProtocol())) {
            try {
                final Path path = Paths.get(url.toURI());
                if (Files.isRegularFile(path, new LinkOption[0])) {
                    return get(path, metadata);
                }
            }
            catch (final URISyntaxException ex) {}
        }
        final URLConnection connection = url.openConnection();
        final String path2 = url.getPath();
        final int slash = path2.lastIndexOf(47);
        if (slash + 1 < path2.length()) {
            metadata.set("resourceName", path2.substring(slash + 1));
        }
        final String type = connection.getContentType();
        if (type != null) {
            metadata.set("Content-Type", type);
        }
        final String encoding = connection.getContentEncoding();
        if (encoding != null) {
            metadata.set("Content-Encoding", encoding);
        }
        final int length = connection.getContentLength();
        if (length >= 0) {
            metadata.set("Content-Length", Integer.toString(length));
        }
        return new TikaInputStream(new BufferedInputStream(connection.getInputStream()), new TemporaryResources(), length);
    }
    
    public int peek(final byte[] buffer) throws IOException {
        int n = 0;
        this.mark(buffer.length);
        int m = this.read(buffer);
        while (m != -1) {
            n += m;
            if (n < buffer.length) {
                m = this.read(buffer, n, buffer.length - n);
            }
            else {
                m = -1;
            }
        }
        this.reset();
        return n;
    }
    
    public Object getOpenContainer() {
        return this.openContainer;
    }
    
    public void setOpenContainer(final Object container) {
        this.openContainer = container;
        if (container instanceof Closeable) {
            this.tmp.addResource((Closeable)container);
        }
    }
    
    public boolean hasInputStreamFactory() {
        return this.streamFactory != null;
    }
    
    public InputStreamFactory getInputStreamFactory() {
        return this.streamFactory;
    }
    
    public boolean hasFile() {
        return this.path != null;
    }
    
    public Path getPath() throws IOException {
        return this.getPath(-1);
    }
    
    public Path getPath(final int maxBytes) throws IOException {
        if (this.path == null) {
            if (this.position > 0L) {
                throw new IOException("Stream is already being read");
            }
            final Path tmpFile = this.tmp.createTempFile();
            if (maxBytes > -1) {
                try (final InputStream lookAhead = new LookaheadInputStream((InputStream)this, maxBytes)) {
                    Files.copy(lookAhead, tmpFile, StandardCopyOption.REPLACE_EXISTING);
                    if (Files.size(tmpFile) >= maxBytes) {
                        return null;
                    }
                }
            }
            else {
                Files.copy((InputStream)this, tmpFile, StandardCopyOption.REPLACE_EXISTING);
            }
            this.path = tmpFile;
            final InputStream newStream = Files.newInputStream(this.path, new OpenOption[0]);
            this.tmp.addResource(newStream);
            final InputStream oldStream = this.in;
            this.in = new BufferedInputStream(newStream) {
                @Override
                public void close() throws IOException {
                    oldStream.close();
                }
            };
            this.length = Files.size(this.path);
            this.position = 0L;
            this.mark = -1L;
        }
        return this.path;
    }
    
    public File getFile() throws IOException {
        return this.getPath().toFile();
    }
    
    public FileChannel getFileChannel() throws IOException {
        final FileChannel channel = FileChannel.open(this.getPath(), new OpenOption[0]);
        this.tmp.addResource(channel);
        return channel;
    }
    
    public boolean hasLength() {
        return this.length != -1L;
    }
    
    public long getLength() throws IOException {
        if (this.length == -1L) {
            this.getPath();
        }
        return this.length;
    }
    
    public long getPosition() {
        return this.position;
    }
    
    public long skip(final long ln) throws IOException {
        if (this.skipBuffer == null) {
            this.skipBuffer = new byte[4096];
        }
        final long n = IOUtils.skip(super.in, ln, this.skipBuffer);
        this.position += n;
        return n;
    }
    
    public void mark(final int readlimit) {
        super.mark(readlimit);
        this.mark = this.position;
    }
    
    public boolean markSupported() {
        return true;
    }
    
    public void reset() throws IOException {
        super.reset();
        this.position = this.mark;
        this.mark = -1L;
        this.consecutiveEOFs = 0;
    }
    
    public void close() throws IOException {
        this.path = null;
        this.mark = -1L;
        this.tmp.addResource(this.in);
        this.tmp.close();
    }
    
    protected void afterRead(final int n) throws IOException {
        if (n != -1) {
            this.position += n;
        }
        else {
            ++this.consecutiveEOFs;
            if (this.consecutiveEOFs > 1000) {
                throw new IOException("Read too many -1 (EOFs); there could be an infinite loop.If you think your file is not corrupt, please open an issue on Tika's JIRA");
            }
        }
    }
    
    public String toString() {
        String str = "TikaInputStream of ";
        if (this.hasFile()) {
            str += this.path.toString();
        }
        else {
            str += this.in.toString();
        }
        if (this.openContainer != null) {
            str = str + " (in " + this.openContainer + ")";
        }
        return str;
    }
}
