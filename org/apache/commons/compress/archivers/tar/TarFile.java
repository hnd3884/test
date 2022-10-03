package org.apache.commons.compress.archivers.tar;

import org.apache.commons.compress.utils.BoundedArchiveInputStream;
import org.apache.commons.compress.utils.ArchiveUtils;
import java.io.ByteArrayOutputStream;
import java.util.Iterator;
import org.apache.commons.compress.utils.BoundedSeekableByteChannelInputStream;
import org.apache.commons.compress.utils.BoundedInputStream;
import java.util.Collection;
import org.apache.commons.compress.archivers.zip.ZipEncodingHelper;
import java.util.HashMap;
import java.util.ArrayList;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.io.File;
import java.io.IOException;
import org.apache.commons.compress.utils.SeekableInMemoryByteChannel;
import java.io.InputStream;
import java.util.Map;
import java.util.List;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import org.apache.commons.compress.archivers.zip.ZipEncoding;
import java.nio.channels.SeekableByteChannel;
import java.io.Closeable;

public class TarFile implements Closeable
{
    private static final int SMALL_BUFFER_SIZE = 256;
    private final byte[] smallBuf;
    private final SeekableByteChannel archive;
    private final ZipEncoding zipEncoding;
    private final LinkedList<TarArchiveEntry> entries;
    private final int blockSize;
    private final boolean lenient;
    private final int recordSize;
    private final ByteBuffer recordBuffer;
    private final List<TarArchiveStructSparse> globalSparseHeaders;
    private boolean hasHitEOF;
    private TarArchiveEntry currEntry;
    private Map<String, String> globalPaxHeaders;
    private final Map<String, List<InputStream>> sparseInputStreams;
    
    public TarFile(final byte[] content) throws IOException {
        this(new SeekableInMemoryByteChannel(content));
    }
    
    public TarFile(final byte[] content, final String encoding) throws IOException {
        this(new SeekableInMemoryByteChannel(content), 10240, 512, encoding, false);
    }
    
    public TarFile(final byte[] content, final boolean lenient) throws IOException {
        this(new SeekableInMemoryByteChannel(content), 10240, 512, null, lenient);
    }
    
    public TarFile(final File archive) throws IOException {
        this(archive.toPath());
    }
    
    public TarFile(final File archive, final String encoding) throws IOException {
        this(archive.toPath(), encoding);
    }
    
    public TarFile(final File archive, final boolean lenient) throws IOException {
        this(archive.toPath(), lenient);
    }
    
    public TarFile(final Path archivePath) throws IOException {
        this(Files.newByteChannel(archivePath, new OpenOption[0]), 10240, 512, null, false);
    }
    
    public TarFile(final Path archivePath, final String encoding) throws IOException {
        this(Files.newByteChannel(archivePath, new OpenOption[0]), 10240, 512, encoding, false);
    }
    
    public TarFile(final Path archivePath, final boolean lenient) throws IOException {
        this(Files.newByteChannel(archivePath, new OpenOption[0]), 10240, 512, null, lenient);
    }
    
    public TarFile(final SeekableByteChannel content) throws IOException {
        this(content, 10240, 512, null, false);
    }
    
    public TarFile(final SeekableByteChannel archive, final int blockSize, final int recordSize, final String encoding, final boolean lenient) throws IOException {
        this.smallBuf = new byte[256];
        this.entries = new LinkedList<TarArchiveEntry>();
        this.globalSparseHeaders = new ArrayList<TarArchiveStructSparse>();
        this.globalPaxHeaders = new HashMap<String, String>();
        this.sparseInputStreams = new HashMap<String, List<InputStream>>();
        this.archive = archive;
        this.hasHitEOF = false;
        this.zipEncoding = ZipEncodingHelper.getZipEncoding(encoding);
        this.recordSize = recordSize;
        this.recordBuffer = ByteBuffer.allocate(this.recordSize);
        this.blockSize = blockSize;
        this.lenient = lenient;
        TarArchiveEntry entry;
        while ((entry = this.getNextTarEntry()) != null) {
            this.entries.add(entry);
        }
    }
    
    private TarArchiveEntry getNextTarEntry() throws IOException {
        if (this.isAtEOF()) {
            return null;
        }
        if (this.currEntry != null) {
            this.repositionForwardTo(this.currEntry.getDataOffset() + this.currEntry.getSize());
            this.throwExceptionIfPositionIsNotInArchive();
            this.skipRecordPadding();
        }
        final ByteBuffer headerBuf = this.getRecord();
        if (null == headerBuf) {
            return this.currEntry = null;
        }
        try {
            this.currEntry = new TarArchiveEntry(headerBuf.array(), this.zipEncoding, this.lenient, this.archive.position());
        }
        catch (final IllegalArgumentException e) {
            throw new IOException("Error detected parsing the header", e);
        }
        if (this.currEntry.isGNULongLinkEntry()) {
            final byte[] longLinkData = this.getLongNameData();
            if (longLinkData == null) {
                return null;
            }
            this.currEntry.setLinkName(this.zipEncoding.decode(longLinkData));
        }
        if (this.currEntry.isGNULongNameEntry()) {
            final byte[] longNameData = this.getLongNameData();
            if (longNameData == null) {
                return null;
            }
            final String name = this.zipEncoding.decode(longNameData);
            this.currEntry.setName(name);
            if (this.currEntry.isDirectory() && !name.endsWith("/")) {
                this.currEntry.setName(name + "/");
            }
        }
        if (this.currEntry.isGlobalPaxHeader()) {
            this.readGlobalPaxHeaders();
        }
        try {
            if (this.currEntry.isPaxHeader()) {
                this.paxHeaders();
            }
            else if (!this.globalPaxHeaders.isEmpty()) {
                this.applyPaxHeadersToCurrentEntry(this.globalPaxHeaders, this.globalSparseHeaders);
            }
        }
        catch (final NumberFormatException e2) {
            throw new IOException("Error detected parsing the pax header", e2);
        }
        if (this.currEntry.isOldGNUSparse()) {
            this.readOldGNUSparse();
        }
        return this.currEntry;
    }
    
    private void readOldGNUSparse() throws IOException {
        if (this.currEntry.isExtended()) {
            TarArchiveSparseEntry entry;
            do {
                final ByteBuffer headerBuf = this.getRecord();
                if (headerBuf == null) {
                    throw new IOException("premature end of tar archive. Didn't find extended_header after header with extended flag.");
                }
                entry = new TarArchiveSparseEntry(headerBuf.array());
                this.currEntry.getSparseHeaders().addAll(entry.getSparseHeaders());
                this.currEntry.setDataOffset(this.currEntry.getDataOffset() + this.recordSize);
            } while (entry.isExtended());
        }
        this.buildSparseInputStreams();
    }
    
    private void buildSparseInputStreams() throws IOException {
        final List<InputStream> streams = new ArrayList<InputStream>();
        final List<TarArchiveStructSparse> sparseHeaders = this.currEntry.getOrderedSparseHeaders();
        final InputStream zeroInputStream = new TarArchiveSparseZeroInputStream();
        long offset = 0L;
        long numberOfZeroBytesInSparseEntry = 0L;
        for (final TarArchiveStructSparse sparseHeader : sparseHeaders) {
            final long zeroBlockSize = sparseHeader.getOffset() - offset;
            if (zeroBlockSize < 0L) {
                throw new IOException("Corrupted struct sparse detected");
            }
            if (zeroBlockSize > 0L) {
                streams.add(new BoundedInputStream(zeroInputStream, zeroBlockSize));
                numberOfZeroBytesInSparseEntry += zeroBlockSize;
            }
            if (sparseHeader.getNumbytes() > 0L) {
                final long start = this.currEntry.getDataOffset() + sparseHeader.getOffset() - numberOfZeroBytesInSparseEntry;
                if (start + sparseHeader.getNumbytes() < start) {
                    throw new IOException("Unreadable TAR archive, sparse block offset or length too big");
                }
                streams.add(new BoundedSeekableByteChannelInputStream(start, sparseHeader.getNumbytes(), this.archive));
            }
            offset = sparseHeader.getOffset() + sparseHeader.getNumbytes();
        }
        this.sparseInputStreams.put(this.currEntry.getName(), streams);
    }
    
    private void applyPaxHeadersToCurrentEntry(final Map<String, String> headers, final List<TarArchiveStructSparse> sparseHeaders) throws IOException {
        this.currEntry.updateEntryFromPaxHeaders(headers);
        this.currEntry.setSparseHeaders(sparseHeaders);
    }
    
    private void paxHeaders() throws IOException {
        List<TarArchiveStructSparse> sparseHeaders = new ArrayList<TarArchiveStructSparse>();
        Map<String, String> headers;
        try (final InputStream input = this.getInputStream(this.currEntry)) {
            headers = TarUtils.parsePaxHeaders(input, sparseHeaders, this.globalPaxHeaders, this.currEntry.getSize());
        }
        if (headers.containsKey("GNU.sparse.map")) {
            sparseHeaders = new ArrayList<TarArchiveStructSparse>(TarUtils.parseFromPAX01SparseHeaders(headers.get("GNU.sparse.map")));
        }
        this.getNextTarEntry();
        if (this.currEntry == null) {
            throw new IOException("premature end of tar archive. Didn't find any entry after PAX header.");
        }
        this.applyPaxHeadersToCurrentEntry(headers, sparseHeaders);
        if (this.currEntry.isPaxGNU1XSparse()) {
            try (final InputStream input = this.getInputStream(this.currEntry)) {
                sparseHeaders = TarUtils.parsePAX1XSparseHeaders(input, this.recordSize);
            }
            this.currEntry.setSparseHeaders(sparseHeaders);
            this.currEntry.setDataOffset(this.currEntry.getDataOffset() + this.recordSize);
        }
        this.buildSparseInputStreams();
    }
    
    private void readGlobalPaxHeaders() throws IOException {
        try (final InputStream input = this.getInputStream(this.currEntry)) {
            this.globalPaxHeaders = TarUtils.parsePaxHeaders(input, this.globalSparseHeaders, this.globalPaxHeaders, this.currEntry.getSize());
        }
        this.getNextTarEntry();
        if (this.currEntry == null) {
            throw new IOException("Error detected parsing the pax header");
        }
    }
    
    private byte[] getLongNameData() throws IOException {
        final ByteArrayOutputStream longName = new ByteArrayOutputStream();
        try (final InputStream in = this.getInputStream(this.currEntry)) {
            int length;
            while ((length = in.read(this.smallBuf)) >= 0) {
                longName.write(this.smallBuf, 0, length);
            }
        }
        this.getNextTarEntry();
        if (this.currEntry == null) {
            return null;
        }
        int length;
        byte[] longNameData;
        for (longNameData = longName.toByteArray(), length = longNameData.length; length > 0 && longNameData[length - 1] == 0; --length) {}
        if (length != longNameData.length) {
            final byte[] l = new byte[length];
            System.arraycopy(longNameData, 0, l, 0, length);
            longNameData = l;
        }
        return longNameData;
    }
    
    private void skipRecordPadding() throws IOException {
        if (!this.isDirectory() && this.currEntry.getSize() > 0L && this.currEntry.getSize() % this.recordSize != 0L) {
            final long numRecords = this.currEntry.getSize() / this.recordSize + 1L;
            final long padding = numRecords * this.recordSize - this.currEntry.getSize();
            this.repositionForwardBy(padding);
            this.throwExceptionIfPositionIsNotInArchive();
        }
    }
    
    private void repositionForwardTo(final long newPosition) throws IOException {
        final long currPosition = this.archive.position();
        if (newPosition < currPosition) {
            throw new IOException("trying to move backwards inside of the archive");
        }
        this.archive.position(newPosition);
    }
    
    private void repositionForwardBy(final long offset) throws IOException {
        this.repositionForwardTo(this.archive.position() + offset);
    }
    
    private void throwExceptionIfPositionIsNotInArchive() throws IOException {
        if (this.archive.size() < this.archive.position()) {
            throw new IOException("Truncated TAR archive");
        }
    }
    
    private ByteBuffer getRecord() throws IOException {
        ByteBuffer headerBuf = this.readRecord();
        this.setAtEOF(this.isEOFRecord(headerBuf));
        if (this.isAtEOF() && headerBuf != null) {
            this.tryToConsumeSecondEOFRecord();
            this.consumeRemainderOfLastBlock();
            headerBuf = null;
        }
        return headerBuf;
    }
    
    private void tryToConsumeSecondEOFRecord() throws IOException {
        boolean shouldReset = true;
        try {
            shouldReset = !this.isEOFRecord(this.readRecord());
        }
        finally {
            if (shouldReset) {
                this.archive.position(this.archive.position() - this.recordSize);
            }
        }
    }
    
    private void consumeRemainderOfLastBlock() throws IOException {
        final long bytesReadOfLastBlock = this.archive.position() % this.blockSize;
        if (bytesReadOfLastBlock > 0L) {
            this.repositionForwardBy(this.blockSize - bytesReadOfLastBlock);
        }
    }
    
    private ByteBuffer readRecord() throws IOException {
        this.recordBuffer.rewind();
        final int readNow = this.archive.read(this.recordBuffer);
        if (readNow != this.recordSize) {
            return null;
        }
        return this.recordBuffer;
    }
    
    public List<TarArchiveEntry> getEntries() {
        return new ArrayList<TarArchiveEntry>(this.entries);
    }
    
    private boolean isEOFRecord(final ByteBuffer headerBuf) {
        return headerBuf == null || ArchiveUtils.isArrayZero(headerBuf.array(), this.recordSize);
    }
    
    protected final boolean isAtEOF() {
        return this.hasHitEOF;
    }
    
    protected final void setAtEOF(final boolean b) {
        this.hasHitEOF = b;
    }
    
    private boolean isDirectory() {
        return this.currEntry != null && this.currEntry.isDirectory();
    }
    
    public InputStream getInputStream(final TarArchiveEntry entry) throws IOException {
        try {
            return new BoundedTarEntryInputStream(entry, this.archive);
        }
        catch (final RuntimeException ex) {
            throw new IOException("Corrupted TAR archive. Can't read entry", ex);
        }
    }
    
    @Override
    public void close() throws IOException {
        this.archive.close();
    }
    
    private final class BoundedTarEntryInputStream extends BoundedArchiveInputStream
    {
        private final SeekableByteChannel channel;
        private final TarArchiveEntry entry;
        private long entryOffset;
        private int currentSparseInputStreamIndex;
        
        BoundedTarEntryInputStream(final TarArchiveEntry entry, final SeekableByteChannel channel) throws IOException {
            super(entry.getDataOffset(), entry.getRealSize());
            if (channel.size() - entry.getSize() < entry.getDataOffset()) {
                throw new IOException("entry size exceeds archive size");
            }
            this.entry = entry;
            this.channel = channel;
        }
        
        @Override
        protected int read(final long pos, final ByteBuffer buf) throws IOException {
            if (this.entryOffset >= this.entry.getRealSize()) {
                return -1;
            }
            int totalRead;
            if (this.entry.isSparse()) {
                totalRead = this.readSparse(this.entryOffset, buf, buf.limit());
            }
            else {
                totalRead = this.readArchive(pos, buf);
            }
            if (totalRead == -1) {
                if (buf.array().length > 0) {
                    throw new IOException("Truncated TAR archive");
                }
                TarFile.this.setAtEOF(true);
            }
            else {
                this.entryOffset += totalRead;
                buf.flip();
            }
            return totalRead;
        }
        
        private int readSparse(final long pos, final ByteBuffer buf, final int numToRead) throws IOException {
            final List<InputStream> entrySparseInputStreams = TarFile.this.sparseInputStreams.get(this.entry.getName());
            if (entrySparseInputStreams == null || entrySparseInputStreams.isEmpty()) {
                return this.readArchive(this.entry.getDataOffset() + pos, buf);
            }
            if (this.currentSparseInputStreamIndex >= entrySparseInputStreams.size()) {
                return -1;
            }
            final InputStream currentInputStream = entrySparseInputStreams.get(this.currentSparseInputStreamIndex);
            final byte[] bufArray = new byte[numToRead];
            final int readLen = currentInputStream.read(bufArray);
            if (readLen != -1) {
                buf.put(bufArray, 0, readLen);
            }
            if (this.currentSparseInputStreamIndex == entrySparseInputStreams.size() - 1) {
                return readLen;
            }
            if (readLen == -1) {
                ++this.currentSparseInputStreamIndex;
                return this.readSparse(pos, buf, numToRead);
            }
            if (readLen >= numToRead) {
                return readLen;
            }
            ++this.currentSparseInputStreamIndex;
            final int readLenOfNext = this.readSparse(pos + readLen, buf, numToRead - readLen);
            if (readLenOfNext == -1) {
                return readLen;
            }
            return readLen + readLenOfNext;
        }
        
        private int readArchive(final long pos, final ByteBuffer buf) throws IOException {
            this.channel.position(pos);
            return this.channel.read(buf);
        }
    }
}
