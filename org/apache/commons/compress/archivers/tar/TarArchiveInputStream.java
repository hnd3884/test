package org.apache.commons.compress.archivers.tar;

import org.apache.commons.compress.utils.BoundedInputStream;
import org.apache.commons.compress.archivers.ArchiveEntry;
import java.util.Collection;
import org.apache.commons.compress.utils.ArchiveUtils;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import org.apache.commons.compress.utils.IOUtils;
import java.io.IOException;
import java.util.Iterator;
import org.apache.commons.compress.archivers.zip.ZipEncodingHelper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.compress.archivers.zip.ZipEncoding;
import java.util.List;
import java.io.InputStream;
import org.apache.commons.compress.archivers.ArchiveInputStream;

public class TarArchiveInputStream extends ArchiveInputStream
{
    private static final int SMALL_BUFFER_SIZE = 256;
    private final byte[] smallBuf;
    private final int recordSize;
    private final byte[] recordBuffer;
    private final int blockSize;
    private boolean hasHitEOF;
    private long entrySize;
    private long entryOffset;
    private final InputStream inputStream;
    private List<InputStream> sparseInputStreams;
    private int currentSparseInputStreamIndex;
    private TarArchiveEntry currEntry;
    private final ZipEncoding zipEncoding;
    final String encoding;
    private Map<String, String> globalPaxHeaders;
    private final List<TarArchiveStructSparse> globalSparseHeaders;
    private final boolean lenient;
    
    public TarArchiveInputStream(final InputStream is) {
        this(is, 10240, 512);
    }
    
    public TarArchiveInputStream(final InputStream is, final boolean lenient) {
        this(is, 10240, 512, null, lenient);
    }
    
    public TarArchiveInputStream(final InputStream is, final String encoding) {
        this(is, 10240, 512, encoding);
    }
    
    public TarArchiveInputStream(final InputStream is, final int blockSize) {
        this(is, blockSize, 512);
    }
    
    public TarArchiveInputStream(final InputStream is, final int blockSize, final String encoding) {
        this(is, blockSize, 512, encoding);
    }
    
    public TarArchiveInputStream(final InputStream is, final int blockSize, final int recordSize) {
        this(is, blockSize, recordSize, null);
    }
    
    public TarArchiveInputStream(final InputStream is, final int blockSize, final int recordSize, final String encoding) {
        this(is, blockSize, recordSize, encoding, false);
    }
    
    public TarArchiveInputStream(final InputStream is, final int blockSize, final int recordSize, final String encoding, final boolean lenient) {
        this.smallBuf = new byte[256];
        this.globalPaxHeaders = new HashMap<String, String>();
        this.globalSparseHeaders = new ArrayList<TarArchiveStructSparse>();
        this.inputStream = is;
        this.hasHitEOF = false;
        this.encoding = encoding;
        this.zipEncoding = ZipEncodingHelper.getZipEncoding(encoding);
        this.recordSize = recordSize;
        this.recordBuffer = new byte[recordSize];
        this.blockSize = blockSize;
        this.lenient = lenient;
    }
    
    @Override
    public void close() throws IOException {
        if (this.sparseInputStreams != null) {
            for (final InputStream inputStream : this.sparseInputStreams) {
                inputStream.close();
            }
        }
        this.inputStream.close();
    }
    
    public int getRecordSize() {
        return this.recordSize;
    }
    
    @Override
    public int available() throws IOException {
        if (this.isDirectory()) {
            return 0;
        }
        if (this.currEntry.getRealSize() - this.entryOffset > 2147483647L) {
            return Integer.MAX_VALUE;
        }
        return (int)(this.currEntry.getRealSize() - this.entryOffset);
    }
    
    @Override
    public long skip(final long n) throws IOException {
        if (n <= 0L || this.isDirectory()) {
            return 0L;
        }
        final long availableOfInputStream = this.inputStream.available();
        final long available = this.currEntry.getRealSize() - this.entryOffset;
        final long numToSkip = Math.min(n, available);
        long skipped;
        if (!this.currEntry.isSparse()) {
            skipped = IOUtils.skip(this.inputStream, numToSkip);
            skipped = this.getActuallySkipped(availableOfInputStream, skipped, numToSkip);
        }
        else {
            skipped = this.skipSparse(numToSkip);
        }
        this.count(skipped);
        this.entryOffset += skipped;
        return skipped;
    }
    
    private long skipSparse(final long n) throws IOException {
        if (this.sparseInputStreams == null || this.sparseInputStreams.isEmpty()) {
            return this.inputStream.skip(n);
        }
        long bytesSkipped = 0L;
        while (bytesSkipped < n && this.currentSparseInputStreamIndex < this.sparseInputStreams.size()) {
            final InputStream currentInputStream = this.sparseInputStreams.get(this.currentSparseInputStreamIndex);
            bytesSkipped += currentInputStream.skip(n - bytesSkipped);
            if (bytesSkipped < n) {
                ++this.currentSparseInputStreamIndex;
            }
        }
        return bytesSkipped;
    }
    
    @Override
    public boolean markSupported() {
        return false;
    }
    
    @Override
    public synchronized void mark(final int markLimit) {
    }
    
    @Override
    public synchronized void reset() {
    }
    
    public TarArchiveEntry getNextTarEntry() throws IOException {
        if (this.isAtEOF()) {
            return null;
        }
        if (this.currEntry != null) {
            IOUtils.skip(this, Long.MAX_VALUE);
            this.skipRecordPadding();
        }
        final byte[] headerBuf = this.getRecord();
        if (headerBuf == null) {
            return this.currEntry = null;
        }
        try {
            this.currEntry = new TarArchiveEntry(headerBuf, this.zipEncoding, this.lenient);
        }
        catch (final IllegalArgumentException e) {
            throw new IOException("Error detected parsing the header", e);
        }
        this.entryOffset = 0L;
        this.entrySize = this.currEntry.getSize();
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
        this.entrySize = this.currEntry.getSize();
        return this.currEntry;
    }
    
    private void skipRecordPadding() throws IOException {
        if (!this.isDirectory() && this.entrySize > 0L && this.entrySize % this.recordSize != 0L) {
            final long available = this.inputStream.available();
            final long numRecords = this.entrySize / this.recordSize + 1L;
            final long padding = numRecords * this.recordSize - this.entrySize;
            long skipped = IOUtils.skip(this.inputStream, padding);
            skipped = this.getActuallySkipped(available, skipped, padding);
            this.count(skipped);
        }
    }
    
    private long getActuallySkipped(final long available, final long skipped, final long expected) throws IOException {
        long actuallySkipped = skipped;
        if (this.inputStream instanceof FileInputStream) {
            actuallySkipped = Math.min(skipped, available);
        }
        if (actuallySkipped != expected) {
            throw new IOException("Truncated TAR archive");
        }
        return actuallySkipped;
    }
    
    protected byte[] getLongNameData() throws IOException {
        final ByteArrayOutputStream longName = new ByteArrayOutputStream();
        int length = 0;
        while ((length = this.read(this.smallBuf)) >= 0) {
            longName.write(this.smallBuf, 0, length);
        }
        this.getNextEntry();
        if (this.currEntry == null) {
            return null;
        }
        byte[] longNameData;
        for (longNameData = longName.toByteArray(), length = longNameData.length; length > 0 && longNameData[length - 1] == 0; --length) {}
        if (length != longNameData.length) {
            final byte[] l = new byte[length];
            System.arraycopy(longNameData, 0, l, 0, length);
            longNameData = l;
        }
        return longNameData;
    }
    
    private byte[] getRecord() throws IOException {
        byte[] headerBuf = this.readRecord();
        this.setAtEOF(this.isEOFRecord(headerBuf));
        if (this.isAtEOF() && headerBuf != null) {
            this.tryToConsumeSecondEOFRecord();
            this.consumeRemainderOfLastBlock();
            headerBuf = null;
        }
        return headerBuf;
    }
    
    protected boolean isEOFRecord(final byte[] record) {
        return record == null || ArchiveUtils.isArrayZero(record, this.recordSize);
    }
    
    protected byte[] readRecord() throws IOException {
        final int readNow = IOUtils.readFully(this.inputStream, this.recordBuffer);
        this.count(readNow);
        if (readNow != this.recordSize) {
            return null;
        }
        return this.recordBuffer;
    }
    
    private void readGlobalPaxHeaders() throws IOException {
        this.globalPaxHeaders = TarUtils.parsePaxHeaders(this, this.globalSparseHeaders, this.globalPaxHeaders, this.entrySize);
        this.getNextEntry();
        if (this.currEntry == null) {
            throw new IOException("Error detected parsing the pax header");
        }
    }
    
    private void paxHeaders() throws IOException {
        List<TarArchiveStructSparse> sparseHeaders = new ArrayList<TarArchiveStructSparse>();
        final Map<String, String> headers = TarUtils.parsePaxHeaders(this, sparseHeaders, this.globalPaxHeaders, this.entrySize);
        if (headers.containsKey("GNU.sparse.map")) {
            sparseHeaders = new ArrayList<TarArchiveStructSparse>(TarUtils.parseFromPAX01SparseHeaders(headers.get("GNU.sparse.map")));
        }
        this.getNextEntry();
        if (this.currEntry == null) {
            throw new IOException("premature end of tar archive. Didn't find any entry after PAX header.");
        }
        this.applyPaxHeadersToCurrentEntry(headers, sparseHeaders);
        if (this.currEntry.isPaxGNU1XSparse()) {
            sparseHeaders = TarUtils.parsePAX1XSparseHeaders(this.inputStream, this.recordSize);
            this.currEntry.setSparseHeaders(sparseHeaders);
        }
        this.buildSparseInputStreams();
    }
    
    private void applyPaxHeadersToCurrentEntry(final Map<String, String> headers, final List<TarArchiveStructSparse> sparseHeaders) throws IOException {
        this.currEntry.updateEntryFromPaxHeaders(headers);
        this.currEntry.setSparseHeaders(sparseHeaders);
    }
    
    private void readOldGNUSparse() throws IOException {
        if (this.currEntry.isExtended()) {
            TarArchiveSparseEntry entry;
            do {
                final byte[] headerBuf = this.getRecord();
                if (headerBuf == null) {
                    throw new IOException("premature end of tar archive. Didn't find extended_header after header with extended flag.");
                }
                entry = new TarArchiveSparseEntry(headerBuf);
                this.currEntry.getSparseHeaders().addAll(entry.getSparseHeaders());
            } while (entry.isExtended());
        }
        this.buildSparseInputStreams();
    }
    
    private boolean isDirectory() {
        return this.currEntry != null && this.currEntry.isDirectory();
    }
    
    @Override
    public ArchiveEntry getNextEntry() throws IOException {
        return this.getNextTarEntry();
    }
    
    private void tryToConsumeSecondEOFRecord() throws IOException {
        boolean shouldReset = true;
        final boolean marked = this.inputStream.markSupported();
        if (marked) {
            this.inputStream.mark(this.recordSize);
        }
        try {
            shouldReset = !this.isEOFRecord(this.readRecord());
        }
        finally {
            if (shouldReset && marked) {
                this.pushedBackBytes(this.recordSize);
                this.inputStream.reset();
            }
        }
    }
    
    @Override
    public int read(final byte[] buf, final int offset, int numToRead) throws IOException {
        if (numToRead == 0) {
            return 0;
        }
        int totalRead = 0;
        if (this.isAtEOF() || this.isDirectory()) {
            return -1;
        }
        if (this.currEntry == null) {
            throw new IllegalStateException("No current tar entry");
        }
        if (this.entryOffset >= this.currEntry.getRealSize()) {
            return -1;
        }
        numToRead = Math.min(numToRead, this.available());
        if (this.currEntry.isSparse()) {
            totalRead = this.readSparse(buf, offset, numToRead);
        }
        else {
            totalRead = this.inputStream.read(buf, offset, numToRead);
        }
        if (totalRead == -1) {
            if (numToRead > 0) {
                throw new IOException("Truncated TAR archive");
            }
            this.setAtEOF(true);
        }
        else {
            this.count(totalRead);
            this.entryOffset += totalRead;
        }
        return totalRead;
    }
    
    private int readSparse(final byte[] buf, final int offset, final int numToRead) throws IOException {
        if (this.sparseInputStreams == null || this.sparseInputStreams.isEmpty()) {
            return this.inputStream.read(buf, offset, numToRead);
        }
        if (this.currentSparseInputStreamIndex >= this.sparseInputStreams.size()) {
            return -1;
        }
        final InputStream currentInputStream = this.sparseInputStreams.get(this.currentSparseInputStreamIndex);
        final int readLen = currentInputStream.read(buf, offset, numToRead);
        if (this.currentSparseInputStreamIndex == this.sparseInputStreams.size() - 1) {
            return readLen;
        }
        if (readLen == -1) {
            ++this.currentSparseInputStreamIndex;
            return this.readSparse(buf, offset, numToRead);
        }
        if (readLen >= numToRead) {
            return readLen;
        }
        ++this.currentSparseInputStreamIndex;
        final int readLenOfNext = this.readSparse(buf, offset + readLen, numToRead - readLen);
        if (readLenOfNext == -1) {
            return readLen;
        }
        return readLen + readLenOfNext;
    }
    
    @Override
    public boolean canReadEntryData(final ArchiveEntry ae) {
        return ae instanceof TarArchiveEntry;
    }
    
    public TarArchiveEntry getCurrentEntry() {
        return this.currEntry;
    }
    
    protected final void setCurrentEntry(final TarArchiveEntry e) {
        this.currEntry = e;
    }
    
    protected final boolean isAtEOF() {
        return this.hasHitEOF;
    }
    
    protected final void setAtEOF(final boolean b) {
        this.hasHitEOF = b;
    }
    
    private void consumeRemainderOfLastBlock() throws IOException {
        final long bytesReadOfLastBlock = this.getBytesRead() % this.blockSize;
        if (bytesReadOfLastBlock > 0L) {
            final long skipped = IOUtils.skip(this.inputStream, this.blockSize - bytesReadOfLastBlock);
            this.count(skipped);
        }
    }
    
    public static boolean matches(final byte[] signature, final int length) {
        return length >= 265 && ((ArchiveUtils.matchAsciiBuffer("ustar\u0000", signature, 257, 6) && ArchiveUtils.matchAsciiBuffer("00", signature, 263, 2)) || (ArchiveUtils.matchAsciiBuffer("ustar ", signature, 257, 6) && (ArchiveUtils.matchAsciiBuffer(" \u0000", signature, 263, 2) || ArchiveUtils.matchAsciiBuffer("0\u0000", signature, 263, 2))) || (ArchiveUtils.matchAsciiBuffer("ustar\u0000", signature, 257, 6) && ArchiveUtils.matchAsciiBuffer("\u0000\u0000", signature, 263, 2)));
    }
    
    private void buildSparseInputStreams() throws IOException {
        this.currentSparseInputStreamIndex = -1;
        this.sparseInputStreams = new ArrayList<InputStream>();
        final List<TarArchiveStructSparse> sparseHeaders = this.currEntry.getOrderedSparseHeaders();
        final InputStream zeroInputStream = new TarArchiveSparseZeroInputStream();
        long offset = 0L;
        for (final TarArchiveStructSparse sparseHeader : sparseHeaders) {
            final long zeroBlockSize = sparseHeader.getOffset() - offset;
            if (zeroBlockSize < 0L) {
                throw new IOException("Corrupted struct sparse detected");
            }
            if (zeroBlockSize > 0L) {
                this.sparseInputStreams.add(new BoundedInputStream(zeroInputStream, sparseHeader.getOffset() - offset));
            }
            if (sparseHeader.getNumbytes() > 0L) {
                this.sparseInputStreams.add(new BoundedInputStream(this.inputStream, sparseHeader.getNumbytes()));
            }
            offset = sparseHeader.getOffset() + sparseHeader.getNumbytes();
        }
        if (!this.sparseInputStreams.isEmpty()) {
            this.currentSparseInputStreamIndex = 0;
        }
    }
}
