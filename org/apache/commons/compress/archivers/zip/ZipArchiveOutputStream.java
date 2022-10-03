package org.apache.commons.compress.archivers.zip;

import java.nio.file.LinkOption;
import java.util.zip.ZipException;
import org.apache.commons.compress.archivers.ArchiveEntry;
import java.io.InputStream;
import org.apache.commons.compress.utils.ByteUtils;
import java.util.Iterator;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.io.Closeable;
import org.apache.commons.compress.utils.IOUtils;
import java.util.Set;
import java.nio.file.Files;
import java.nio.file.attribute.FileAttribute;
import java.util.EnumSet;
import java.nio.file.StandardOpenOption;
import java.nio.file.Path;
import java.io.IOException;
import java.nio.file.OpenOption;
import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Calendar;
import java.io.OutputStream;
import java.nio.channels.SeekableByteChannel;
import java.util.zip.Deflater;
import java.util.Map;
import java.util.List;
import org.apache.commons.compress.archivers.ArchiveOutputStream;

public class ZipArchiveOutputStream extends ArchiveOutputStream
{
    static final int BUFFER_SIZE = 512;
    private static final int LFH_SIG_OFFSET = 0;
    private static final int LFH_VERSION_NEEDED_OFFSET = 4;
    private static final int LFH_GPB_OFFSET = 6;
    private static final int LFH_METHOD_OFFSET = 8;
    private static final int LFH_TIME_OFFSET = 10;
    private static final int LFH_CRC_OFFSET = 14;
    private static final int LFH_COMPRESSED_SIZE_OFFSET = 18;
    private static final int LFH_ORIGINAL_SIZE_OFFSET = 22;
    private static final int LFH_FILENAME_LENGTH_OFFSET = 26;
    private static final int LFH_EXTRA_LENGTH_OFFSET = 28;
    private static final int LFH_FILENAME_OFFSET = 30;
    private static final int CFH_SIG_OFFSET = 0;
    private static final int CFH_VERSION_MADE_BY_OFFSET = 4;
    private static final int CFH_VERSION_NEEDED_OFFSET = 6;
    private static final int CFH_GPB_OFFSET = 8;
    private static final int CFH_METHOD_OFFSET = 10;
    private static final int CFH_TIME_OFFSET = 12;
    private static final int CFH_CRC_OFFSET = 16;
    private static final int CFH_COMPRESSED_SIZE_OFFSET = 20;
    private static final int CFH_ORIGINAL_SIZE_OFFSET = 24;
    private static final int CFH_FILENAME_LENGTH_OFFSET = 28;
    private static final int CFH_EXTRA_LENGTH_OFFSET = 30;
    private static final int CFH_COMMENT_LENGTH_OFFSET = 32;
    private static final int CFH_DISK_NUMBER_OFFSET = 34;
    private static final int CFH_INTERNAL_ATTRIBUTES_OFFSET = 36;
    private static final int CFH_EXTERNAL_ATTRIBUTES_OFFSET = 38;
    private static final int CFH_LFH_OFFSET = 42;
    private static final int CFH_FILENAME_OFFSET = 46;
    protected boolean finished;
    public static final int DEFLATED = 8;
    public static final int DEFAULT_COMPRESSION = -1;
    public static final int STORED = 0;
    static final String DEFAULT_ENCODING = "UTF8";
    @Deprecated
    public static final int EFS_FLAG = 2048;
    private CurrentEntry entry;
    private String comment;
    private int level;
    private boolean hasCompressionLevelChanged;
    private int method;
    private final List<ZipArchiveEntry> entries;
    private final StreamCompressor streamCompressor;
    private long cdOffset;
    private long cdLength;
    private long cdDiskNumberStart;
    private long eocdLength;
    private static final byte[] ZERO;
    private static final byte[] LZERO;
    private static final byte[] ONE;
    private final Map<ZipArchiveEntry, EntryMetaData> metaData;
    private String encoding;
    private ZipEncoding zipEncoding;
    protected final Deflater def;
    private final SeekableByteChannel channel;
    private final OutputStream out;
    private boolean useUTF8Flag;
    private boolean fallbackToUTF8;
    private UnicodeExtraFieldPolicy createUnicodeExtraFields;
    private boolean hasUsedZip64;
    private Zip64Mode zip64Mode;
    private final byte[] copyBuffer;
    private final Calendar calendarInstance;
    private final boolean isSplitZip;
    private final Map<Integer, Integer> numberOfCDInDiskData;
    static final byte[] LFH_SIG;
    static final byte[] DD_SIG;
    static final byte[] CFH_SIG;
    static final byte[] EOCD_SIG;
    static final byte[] ZIP64_EOCD_SIG;
    static final byte[] ZIP64_EOCD_LOC_SIG;
    
    public ZipArchiveOutputStream(final OutputStream out) {
        this.comment = "";
        this.level = -1;
        this.method = 8;
        this.entries = new LinkedList<ZipArchiveEntry>();
        this.metaData = new HashMap<ZipArchiveEntry, EntryMetaData>();
        this.encoding = "UTF8";
        this.zipEncoding = ZipEncodingHelper.getZipEncoding("UTF8");
        this.useUTF8Flag = true;
        this.createUnicodeExtraFields = UnicodeExtraFieldPolicy.NEVER;
        this.zip64Mode = Zip64Mode.AsNeeded;
        this.copyBuffer = new byte[32768];
        this.calendarInstance = Calendar.getInstance();
        this.numberOfCDInDiskData = new HashMap<Integer, Integer>();
        this.out = out;
        this.channel = null;
        this.def = new Deflater(this.level, true);
        this.streamCompressor = StreamCompressor.create(out, this.def);
        this.isSplitZip = false;
    }
    
    public ZipArchiveOutputStream(final File file) throws IOException {
        this(file.toPath(), new OpenOption[0]);
    }
    
    public ZipArchiveOutputStream(final Path file, final OpenOption... options) throws IOException {
        this.comment = "";
        this.level = -1;
        this.method = 8;
        this.entries = new LinkedList<ZipArchiveEntry>();
        this.metaData = new HashMap<ZipArchiveEntry, EntryMetaData>();
        this.encoding = "UTF8";
        this.zipEncoding = ZipEncodingHelper.getZipEncoding("UTF8");
        this.useUTF8Flag = true;
        this.createUnicodeExtraFields = UnicodeExtraFieldPolicy.NEVER;
        this.zip64Mode = Zip64Mode.AsNeeded;
        this.copyBuffer = new byte[32768];
        this.calendarInstance = Calendar.getInstance();
        this.numberOfCDInDiskData = new HashMap<Integer, Integer>();
        this.def = new Deflater(this.level, true);
        OutputStream o = null;
        SeekableByteChannel _channel = null;
        StreamCompressor _streamCompressor = null;
        try {
            _channel = Files.newByteChannel(file, EnumSet.of(StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.READ, StandardOpenOption.TRUNCATE_EXISTING), (FileAttribute<?>[])new FileAttribute[0]);
            _streamCompressor = StreamCompressor.create(_channel, this.def);
        }
        catch (final IOException e) {
            IOUtils.closeQuietly(_channel);
            _channel = null;
            o = Files.newOutputStream(file, options);
            _streamCompressor = StreamCompressor.create(o, this.def);
        }
        this.out = o;
        this.channel = _channel;
        this.streamCompressor = _streamCompressor;
        this.isSplitZip = false;
    }
    
    public ZipArchiveOutputStream(final File file, final long zipSplitSize) throws IOException {
        this.comment = "";
        this.level = -1;
        this.method = 8;
        this.entries = new LinkedList<ZipArchiveEntry>();
        this.metaData = new HashMap<ZipArchiveEntry, EntryMetaData>();
        this.encoding = "UTF8";
        this.zipEncoding = ZipEncodingHelper.getZipEncoding("UTF8");
        this.useUTF8Flag = true;
        this.createUnicodeExtraFields = UnicodeExtraFieldPolicy.NEVER;
        this.zip64Mode = Zip64Mode.AsNeeded;
        this.copyBuffer = new byte[32768];
        this.calendarInstance = Calendar.getInstance();
        this.numberOfCDInDiskData = new HashMap<Integer, Integer>();
        this.def = new Deflater(this.level, true);
        this.out = new ZipSplitOutputStream(file, zipSplitSize);
        this.streamCompressor = StreamCompressor.create(this.out, this.def);
        this.channel = null;
        this.isSplitZip = true;
    }
    
    public ZipArchiveOutputStream(final SeekableByteChannel channel) throws IOException {
        this.comment = "";
        this.level = -1;
        this.method = 8;
        this.entries = new LinkedList<ZipArchiveEntry>();
        this.metaData = new HashMap<ZipArchiveEntry, EntryMetaData>();
        this.encoding = "UTF8";
        this.zipEncoding = ZipEncodingHelper.getZipEncoding("UTF8");
        this.useUTF8Flag = true;
        this.createUnicodeExtraFields = UnicodeExtraFieldPolicy.NEVER;
        this.zip64Mode = Zip64Mode.AsNeeded;
        this.copyBuffer = new byte[32768];
        this.calendarInstance = Calendar.getInstance();
        this.numberOfCDInDiskData = new HashMap<Integer, Integer>();
        this.channel = channel;
        this.def = new Deflater(this.level, true);
        this.streamCompressor = StreamCompressor.create(channel, this.def);
        this.out = null;
        this.isSplitZip = false;
    }
    
    public boolean isSeekable() {
        return this.channel != null;
    }
    
    public void setEncoding(final String encoding) {
        this.encoding = encoding;
        this.zipEncoding = ZipEncodingHelper.getZipEncoding(encoding);
        if (this.useUTF8Flag && !ZipEncodingHelper.isUTF8(encoding)) {
            this.useUTF8Flag = false;
        }
    }
    
    public String getEncoding() {
        return this.encoding;
    }
    
    public void setUseLanguageEncodingFlag(final boolean b) {
        this.useUTF8Flag = (b && ZipEncodingHelper.isUTF8(this.encoding));
    }
    
    public void setCreateUnicodeExtraFields(final UnicodeExtraFieldPolicy b) {
        this.createUnicodeExtraFields = b;
    }
    
    public void setFallbackToUTF8(final boolean b) {
        this.fallbackToUTF8 = b;
    }
    
    public void setUseZip64(final Zip64Mode mode) {
        this.zip64Mode = mode;
    }
    
    @Override
    public void finish() throws IOException {
        if (this.finished) {
            throw new IOException("This archive has already been finished");
        }
        if (this.entry != null) {
            throw new IOException("This archive contains unclosed entries.");
        }
        final long cdOverallOffset = this.streamCompressor.getTotalBytesWritten();
        this.cdOffset = cdOverallOffset;
        if (this.isSplitZip) {
            final ZipSplitOutputStream zipSplitOutputStream = (ZipSplitOutputStream)this.out;
            this.cdOffset = zipSplitOutputStream.getCurrentSplitSegmentBytesWritten();
            this.cdDiskNumberStart = zipSplitOutputStream.getCurrentSplitSegmentIndex();
        }
        this.writeCentralDirectoryInChunks();
        this.cdLength = this.streamCompressor.getTotalBytesWritten() - cdOverallOffset;
        final ByteBuffer commentData = this.zipEncoding.encode(this.comment);
        final long commentLength = commentData.limit() - (long)commentData.position();
        this.eocdLength = 22L + commentLength;
        this.writeZip64CentralDirectory();
        this.writeCentralDirectoryEnd();
        this.metaData.clear();
        this.entries.clear();
        this.streamCompressor.close();
        if (this.isSplitZip) {
            this.out.close();
        }
        this.finished = true;
    }
    
    private void writeCentralDirectoryInChunks() throws IOException {
        final int NUM_PER_WRITE = 1000;
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(70000);
        int count = 0;
        for (final ZipArchiveEntry ze : this.entries) {
            byteArrayOutputStream.write(this.createCentralFileHeader(ze));
            if (++count > 1000) {
                this.writeCounted(byteArrayOutputStream.toByteArray());
                byteArrayOutputStream.reset();
                count = 0;
            }
        }
        this.writeCounted(byteArrayOutputStream.toByteArray());
    }
    
    @Override
    public void closeArchiveEntry() throws IOException {
        this.preClose();
        this.flushDeflater();
        final long bytesWritten = this.streamCompressor.getTotalBytesWritten() - this.entry.dataStart;
        final long realCrc = this.streamCompressor.getCrc32();
        this.entry.bytesRead = this.streamCompressor.getBytesRead();
        final Zip64Mode effectiveMode = this.getEffectiveZip64Mode(this.entry.entry);
        final boolean actuallyNeedsZip64 = this.handleSizesAndCrc(bytesWritten, realCrc, effectiveMode);
        this.closeEntry(actuallyNeedsZip64, false);
        this.streamCompressor.reset();
    }
    
    private void closeCopiedEntry(final boolean phased) throws IOException {
        this.preClose();
        this.entry.bytesRead = this.entry.entry.getSize();
        final Zip64Mode effectiveMode = this.getEffectiveZip64Mode(this.entry.entry);
        final boolean actuallyNeedsZip64 = this.checkIfNeedsZip64(effectiveMode);
        this.closeEntry(actuallyNeedsZip64, phased);
    }
    
    private void closeEntry(final boolean actuallyNeedsZip64, final boolean phased) throws IOException {
        if (!phased && this.channel != null) {
            this.rewriteSizesAndCrc(actuallyNeedsZip64);
        }
        if (!phased) {
            this.writeDataDescriptor(this.entry.entry);
        }
        this.entry = null;
    }
    
    private void preClose() throws IOException {
        if (this.finished) {
            throw new IOException("Stream has already been finished");
        }
        if (this.entry == null) {
            throw new IOException("No current entry to close");
        }
        if (!this.entry.hasWritten) {
            this.write(ByteUtils.EMPTY_BYTE_ARRAY, 0, 0);
        }
    }
    
    public void addRawArchiveEntry(final ZipArchiveEntry entry, final InputStream rawStream) throws IOException {
        final ZipArchiveEntry ae = new ZipArchiveEntry(entry);
        if (this.hasZip64Extra(ae)) {
            ae.removeExtraField(Zip64ExtendedInformationExtraField.HEADER_ID);
        }
        final boolean is2PhaseSource = ae.getCrc() != -1L && ae.getSize() != -1L && ae.getCompressedSize() != -1L;
        this.putArchiveEntry(ae, is2PhaseSource);
        this.copyFromZipInputStream(rawStream);
        this.closeCopiedEntry(is2PhaseSource);
    }
    
    private void flushDeflater() throws IOException {
        if (this.entry.entry.getMethod() == 8) {
            this.streamCompressor.flushDeflater();
        }
    }
    
    private boolean handleSizesAndCrc(final long bytesWritten, final long crc, final Zip64Mode effectiveMode) throws ZipException {
        if (this.entry.entry.getMethod() == 8) {
            this.entry.entry.setSize(this.entry.bytesRead);
            this.entry.entry.setCompressedSize(bytesWritten);
            this.entry.entry.setCrc(crc);
        }
        else if (this.channel == null) {
            if (this.entry.entry.getCrc() != crc) {
                throw new ZipException("Bad CRC checksum for entry " + this.entry.entry.getName() + ": " + Long.toHexString(this.entry.entry.getCrc()) + " instead of " + Long.toHexString(crc));
            }
            if (this.entry.entry.getSize() != bytesWritten) {
                throw new ZipException("Bad size for entry " + this.entry.entry.getName() + ": " + this.entry.entry.getSize() + " instead of " + bytesWritten);
            }
        }
        else {
            this.entry.entry.setSize(bytesWritten);
            this.entry.entry.setCompressedSize(bytesWritten);
            this.entry.entry.setCrc(crc);
        }
        return this.checkIfNeedsZip64(effectiveMode);
    }
    
    private boolean checkIfNeedsZip64(final Zip64Mode effectiveMode) throws ZipException {
        final boolean actuallyNeedsZip64 = this.isZip64Required(this.entry.entry, effectiveMode);
        if (actuallyNeedsZip64 && effectiveMode == Zip64Mode.Never) {
            throw new Zip64RequiredException(Zip64RequiredException.getEntryTooBigMessage(this.entry.entry));
        }
        return actuallyNeedsZip64;
    }
    
    private boolean isZip64Required(final ZipArchiveEntry entry1, final Zip64Mode requestedMode) {
        return requestedMode == Zip64Mode.Always || requestedMode == Zip64Mode.AlwaysWithCompatibility || this.isTooLargeForZip32(entry1);
    }
    
    private boolean isTooLargeForZip32(final ZipArchiveEntry zipArchiveEntry) {
        return zipArchiveEntry.getSize() >= 4294967295L || zipArchiveEntry.getCompressedSize() >= 4294967295L;
    }
    
    private void rewriteSizesAndCrc(final boolean actuallyNeedsZip64) throws IOException {
        final long save = this.channel.position();
        this.channel.position(this.entry.localDataStart);
        this.writeOut(ZipLong.getBytes(this.entry.entry.getCrc()));
        if (!this.hasZip64Extra(this.entry.entry) || !actuallyNeedsZip64) {
            this.writeOut(ZipLong.getBytes(this.entry.entry.getCompressedSize()));
            this.writeOut(ZipLong.getBytes(this.entry.entry.getSize()));
        }
        else {
            this.writeOut(ZipLong.ZIP64_MAGIC.getBytes());
            this.writeOut(ZipLong.ZIP64_MAGIC.getBytes());
        }
        if (this.hasZip64Extra(this.entry.entry)) {
            final ByteBuffer name = this.getName(this.entry.entry);
            final int nameLen = name.limit() - name.position();
            this.channel.position(this.entry.localDataStart + 12L + 4L + nameLen + 4L);
            this.writeOut(ZipEightByteInteger.getBytes(this.entry.entry.getSize()));
            this.writeOut(ZipEightByteInteger.getBytes(this.entry.entry.getCompressedSize()));
            if (!actuallyNeedsZip64) {
                this.channel.position(this.entry.localDataStart - 10L);
                this.writeOut(ZipShort.getBytes(this.versionNeededToExtract(this.entry.entry.getMethod(), false, false)));
                this.entry.entry.removeExtraField(Zip64ExtendedInformationExtraField.HEADER_ID);
                this.entry.entry.setExtra();
                if (this.entry.causedUseOfZip64) {
                    this.hasUsedZip64 = false;
                }
            }
        }
        this.channel.position(save);
    }
    
    @Override
    public void putArchiveEntry(final ArchiveEntry archiveEntry) throws IOException {
        this.putArchiveEntry(archiveEntry, false);
    }
    
    private void putArchiveEntry(final ArchiveEntry archiveEntry, final boolean phased) throws IOException {
        if (this.finished) {
            throw new IOException("Stream has already been finished");
        }
        if (this.entry != null) {
            this.closeArchiveEntry();
        }
        this.entry = new CurrentEntry((ZipArchiveEntry)archiveEntry);
        this.entries.add(this.entry.entry);
        this.setDefaults(this.entry.entry);
        final Zip64Mode effectiveMode = this.getEffectiveZip64Mode(this.entry.entry);
        this.validateSizeInformation(effectiveMode);
        if (this.shouldAddZip64Extra(this.entry.entry, effectiveMode)) {
            final Zip64ExtendedInformationExtraField z64 = this.getZip64Extra(this.entry.entry);
            ZipEightByteInteger size;
            ZipEightByteInteger compressedSize;
            if (phased) {
                size = new ZipEightByteInteger(this.entry.entry.getSize());
                compressedSize = new ZipEightByteInteger(this.entry.entry.getCompressedSize());
            }
            else if (this.entry.entry.getMethod() == 0 && this.entry.entry.getSize() != -1L) {
                size = (compressedSize = new ZipEightByteInteger(this.entry.entry.getSize()));
            }
            else {
                size = (compressedSize = ZipEightByteInteger.ZERO);
            }
            z64.setSize(size);
            z64.setCompressedSize(compressedSize);
            this.entry.entry.setExtra();
        }
        if (this.entry.entry.getMethod() == 8 && this.hasCompressionLevelChanged) {
            this.def.setLevel(this.level);
            this.hasCompressionLevelChanged = false;
        }
        this.writeLocalFileHeader((ZipArchiveEntry)archiveEntry, phased);
    }
    
    private void setDefaults(final ZipArchiveEntry entry) {
        if (entry.getMethod() == -1) {
            entry.setMethod(this.method);
        }
        if (entry.getTime() == -1L) {
            entry.setTime(System.currentTimeMillis());
        }
    }
    
    private void validateSizeInformation(final Zip64Mode effectiveMode) throws ZipException {
        if (this.entry.entry.getMethod() == 0 && this.channel == null) {
            if (this.entry.entry.getSize() == -1L) {
                throw new ZipException("Uncompressed size is required for STORED method when not writing to a file");
            }
            if (this.entry.entry.getCrc() == -1L) {
                throw new ZipException("CRC checksum is required for STORED method when not writing to a file");
            }
            this.entry.entry.setCompressedSize(this.entry.entry.getSize());
        }
        if ((this.entry.entry.getSize() >= 4294967295L || this.entry.entry.getCompressedSize() >= 4294967295L) && effectiveMode == Zip64Mode.Never) {
            throw new Zip64RequiredException(Zip64RequiredException.getEntryTooBigMessage(this.entry.entry));
        }
    }
    
    private boolean shouldAddZip64Extra(final ZipArchiveEntry entry, final Zip64Mode mode) {
        return mode == Zip64Mode.Always || mode == Zip64Mode.AlwaysWithCompatibility || entry.getSize() >= 4294967295L || entry.getCompressedSize() >= 4294967295L || (entry.getSize() == -1L && this.channel != null && mode != Zip64Mode.Never);
    }
    
    public void setComment(final String comment) {
        this.comment = comment;
    }
    
    public void setLevel(final int level) {
        if (level < -1 || level > 9) {
            throw new IllegalArgumentException("Invalid compression level: " + level);
        }
        if (this.level == level) {
            return;
        }
        this.hasCompressionLevelChanged = true;
        this.level = level;
    }
    
    public void setMethod(final int method) {
        this.method = method;
    }
    
    @Override
    public boolean canWriteEntryData(final ArchiveEntry ae) {
        if (ae instanceof ZipArchiveEntry) {
            final ZipArchiveEntry zae = (ZipArchiveEntry)ae;
            return zae.getMethod() != ZipMethod.IMPLODING.getCode() && zae.getMethod() != ZipMethod.UNSHRINKING.getCode() && ZipUtil.canHandleEntryData(zae);
        }
        return false;
    }
    
    public void writePreamble(final byte[] preamble) throws IOException {
        this.writePreamble(preamble, 0, preamble.length);
    }
    
    public void writePreamble(final byte[] preamble, final int offset, final int length) throws IOException {
        if (this.entry != null) {
            throw new IllegalStateException("Preamble must be written before creating an entry");
        }
        this.streamCompressor.writeCounted(preamble, offset, length);
    }
    
    @Override
    public void write(final byte[] b, final int offset, final int length) throws IOException {
        if (this.entry == null) {
            throw new IllegalStateException("No current entry");
        }
        ZipUtil.checkRequestedFeatures(this.entry.entry);
        final long writtenThisTime = this.streamCompressor.write(b, offset, length, this.entry.entry.getMethod());
        this.count(writtenThisTime);
    }
    
    private void writeCounted(final byte[] data) throws IOException {
        this.streamCompressor.writeCounted(data);
    }
    
    private void copyFromZipInputStream(final InputStream src) throws IOException {
        if (this.entry == null) {
            throw new IllegalStateException("No current entry");
        }
        ZipUtil.checkRequestedFeatures(this.entry.entry);
        this.entry.hasWritten = true;
        int length;
        while ((length = src.read(this.copyBuffer)) >= 0) {
            this.streamCompressor.writeCounted(this.copyBuffer, 0, length);
            this.count(length);
        }
    }
    
    @Override
    public void close() throws IOException {
        try {
            if (!this.finished) {
                this.finish();
            }
        }
        finally {
            this.destroy();
        }
    }
    
    @Override
    public void flush() throws IOException {
        if (this.out != null) {
            this.out.flush();
        }
    }
    
    protected final void deflate() throws IOException {
        this.streamCompressor.deflate();
    }
    
    protected void writeLocalFileHeader(final ZipArchiveEntry ze) throws IOException {
        this.writeLocalFileHeader(ze, false);
    }
    
    private void writeLocalFileHeader(final ZipArchiveEntry ze, final boolean phased) throws IOException {
        final boolean encodable = this.zipEncoding.canEncode(ze.getName());
        final ByteBuffer name = this.getName(ze);
        if (this.createUnicodeExtraFields != UnicodeExtraFieldPolicy.NEVER) {
            this.addUnicodeExtraFields(ze, encodable, name);
        }
        long localHeaderStart = this.streamCompressor.getTotalBytesWritten();
        if (this.isSplitZip) {
            final ZipSplitOutputStream splitOutputStream = (ZipSplitOutputStream)this.out;
            ze.setDiskNumberStart(splitOutputStream.getCurrentSplitSegmentIndex());
            localHeaderStart = splitOutputStream.getCurrentSplitSegmentBytesWritten();
        }
        final byte[] localHeader = this.createLocalFileHeader(ze, name, encodable, phased, localHeaderStart);
        this.metaData.put(ze, new EntryMetaData(localHeaderStart, this.usesDataDescriptor(ze.getMethod(), phased)));
        this.entry.localDataStart = localHeaderStart + 14L;
        this.writeCounted(localHeader);
        this.entry.dataStart = this.streamCompressor.getTotalBytesWritten();
    }
    
    private byte[] createLocalFileHeader(final ZipArchiveEntry ze, final ByteBuffer name, final boolean encodable, final boolean phased, final long archiveOffset) {
        final ZipExtraField oldEx = ze.getExtraField(ResourceAlignmentExtraField.ID);
        if (oldEx != null) {
            ze.removeExtraField(ResourceAlignmentExtraField.ID);
        }
        final ResourceAlignmentExtraField oldAlignmentEx = (oldEx instanceof ResourceAlignmentExtraField) ? ((ResourceAlignmentExtraField)oldEx) : null;
        int alignment = ze.getAlignment();
        if (alignment <= 0 && oldAlignmentEx != null) {
            alignment = oldAlignmentEx.getAlignment();
        }
        if (alignment > 1 || (oldAlignmentEx != null && !oldAlignmentEx.allowMethodChange())) {
            final int oldLength = 30 + name.limit() - name.position() + ze.getLocalFileDataExtra().length;
            final int padding = (int)(-archiveOffset - oldLength - 4L - 2L & (long)(alignment - 1));
            ze.addExtraField(new ResourceAlignmentExtraField(alignment, oldAlignmentEx != null && oldAlignmentEx.allowMethodChange(), padding));
        }
        final byte[] extra = ze.getLocalFileDataExtra();
        final int nameLen = name.limit() - name.position();
        final int len = 30 + nameLen + extra.length;
        final byte[] buf = new byte[len];
        System.arraycopy(ZipArchiveOutputStream.LFH_SIG, 0, buf, 0, 4);
        final int zipMethod = ze.getMethod();
        final boolean dataDescriptor = this.usesDataDescriptor(zipMethod, phased);
        ZipShort.putShort(this.versionNeededToExtract(zipMethod, this.hasZip64Extra(ze), dataDescriptor), buf, 4);
        final GeneralPurposeBit generalPurposeBit = this.getGeneralPurposeBits(!encodable && this.fallbackToUTF8, dataDescriptor);
        generalPurposeBit.encode(buf, 6);
        ZipShort.putShort(zipMethod, buf, 8);
        ZipUtil.toDosTime(this.calendarInstance, ze.getTime(), buf, 10);
        if (phased || (zipMethod != 8 && this.channel == null)) {
            ZipLong.putLong(ze.getCrc(), buf, 14);
        }
        else {
            System.arraycopy(ZipArchiveOutputStream.LZERO, 0, buf, 14, 4);
        }
        if (this.hasZip64Extra(this.entry.entry)) {
            ZipLong.ZIP64_MAGIC.putLong(buf, 18);
            ZipLong.ZIP64_MAGIC.putLong(buf, 22);
        }
        else if (phased) {
            ZipLong.putLong(ze.getCompressedSize(), buf, 18);
            ZipLong.putLong(ze.getSize(), buf, 22);
        }
        else if (zipMethod == 8 || this.channel != null) {
            System.arraycopy(ZipArchiveOutputStream.LZERO, 0, buf, 18, 4);
            System.arraycopy(ZipArchiveOutputStream.LZERO, 0, buf, 22, 4);
        }
        else {
            ZipLong.putLong(ze.getSize(), buf, 18);
            ZipLong.putLong(ze.getSize(), buf, 22);
        }
        ZipShort.putShort(nameLen, buf, 26);
        ZipShort.putShort(extra.length, buf, 28);
        System.arraycopy(name.array(), name.arrayOffset(), buf, 30, nameLen);
        System.arraycopy(extra, 0, buf, 30 + nameLen, extra.length);
        return buf;
    }
    
    private void addUnicodeExtraFields(final ZipArchiveEntry ze, final boolean encodable, final ByteBuffer name) throws IOException {
        if (this.createUnicodeExtraFields == UnicodeExtraFieldPolicy.ALWAYS || !encodable) {
            ze.addExtraField(new UnicodePathExtraField(ze.getName(), name.array(), name.arrayOffset(), name.limit() - name.position()));
        }
        final String comm = ze.getComment();
        if (comm != null && !"".equals(comm)) {
            final boolean commentEncodable = this.zipEncoding.canEncode(comm);
            if (this.createUnicodeExtraFields == UnicodeExtraFieldPolicy.ALWAYS || !commentEncodable) {
                final ByteBuffer commentB = this.getEntryEncoding(ze).encode(comm);
                ze.addExtraField(new UnicodeCommentExtraField(comm, commentB.array(), commentB.arrayOffset(), commentB.limit() - commentB.position()));
            }
        }
    }
    
    protected void writeDataDescriptor(final ZipArchiveEntry ze) throws IOException {
        if (!this.usesDataDescriptor(ze.getMethod(), false)) {
            return;
        }
        this.writeCounted(ZipArchiveOutputStream.DD_SIG);
        this.writeCounted(ZipLong.getBytes(ze.getCrc()));
        if (!this.hasZip64Extra(ze)) {
            this.writeCounted(ZipLong.getBytes(ze.getCompressedSize()));
            this.writeCounted(ZipLong.getBytes(ze.getSize()));
        }
        else {
            this.writeCounted(ZipEightByteInteger.getBytes(ze.getCompressedSize()));
            this.writeCounted(ZipEightByteInteger.getBytes(ze.getSize()));
        }
    }
    
    protected void writeCentralFileHeader(final ZipArchiveEntry ze) throws IOException {
        final byte[] centralFileHeader = this.createCentralFileHeader(ze);
        this.writeCounted(centralFileHeader);
    }
    
    private byte[] createCentralFileHeader(final ZipArchiveEntry ze) throws IOException {
        final EntryMetaData entryMetaData = this.metaData.get(ze);
        final boolean needsZip64Extra = this.hasZip64Extra(ze) || ze.getCompressedSize() >= 4294967295L || ze.getSize() >= 4294967295L || entryMetaData.offset >= 4294967295L || ze.getDiskNumberStart() >= 65535L || this.zip64Mode == Zip64Mode.Always || this.zip64Mode == Zip64Mode.AlwaysWithCompatibility;
        if (needsZip64Extra && this.zip64Mode == Zip64Mode.Never) {
            throw new Zip64RequiredException("Archive's size exceeds the limit of 4GByte.");
        }
        this.handleZip64Extra(ze, entryMetaData.offset, needsZip64Extra);
        return this.createCentralFileHeader(ze, this.getName(ze), entryMetaData, needsZip64Extra);
    }
    
    private byte[] createCentralFileHeader(final ZipArchiveEntry ze, final ByteBuffer name, final EntryMetaData entryMetaData, final boolean needsZip64Extra) throws IOException {
        if (this.isSplitZip) {
            final int currentSplitSegment = ((ZipSplitOutputStream)this.out).getCurrentSplitSegmentIndex();
            if (this.numberOfCDInDiskData.get(currentSplitSegment) == null) {
                this.numberOfCDInDiskData.put(currentSplitSegment, 1);
            }
            else {
                final int originalNumberOfCD = this.numberOfCDInDiskData.get(currentSplitSegment);
                this.numberOfCDInDiskData.put(currentSplitSegment, originalNumberOfCD + 1);
            }
        }
        final byte[] extra = ze.getCentralDirectoryExtra();
        final int extraLength = extra.length;
        String comm = ze.getComment();
        if (comm == null) {
            comm = "";
        }
        final ByteBuffer commentB = this.getEntryEncoding(ze).encode(comm);
        final int nameLen = name.limit() - name.position();
        final int commentLen = commentB.limit() - commentB.position();
        final int len = 46 + nameLen + extraLength + commentLen;
        final byte[] buf = new byte[len];
        System.arraycopy(ZipArchiveOutputStream.CFH_SIG, 0, buf, 0, 4);
        ZipShort.putShort(ze.getPlatform() << 8 | (this.hasUsedZip64 ? 45 : 20), buf, 4);
        final int zipMethod = ze.getMethod();
        final boolean encodable = this.zipEncoding.canEncode(ze.getName());
        ZipShort.putShort(this.versionNeededToExtract(zipMethod, needsZip64Extra, entryMetaData.usesDataDescriptor), buf, 6);
        this.getGeneralPurposeBits(!encodable && this.fallbackToUTF8, entryMetaData.usesDataDescriptor).encode(buf, 8);
        ZipShort.putShort(zipMethod, buf, 10);
        ZipUtil.toDosTime(this.calendarInstance, ze.getTime(), buf, 12);
        ZipLong.putLong(ze.getCrc(), buf, 16);
        if (ze.getCompressedSize() >= 4294967295L || ze.getSize() >= 4294967295L || this.zip64Mode == Zip64Mode.Always || this.zip64Mode == Zip64Mode.AlwaysWithCompatibility) {
            ZipLong.ZIP64_MAGIC.putLong(buf, 20);
            ZipLong.ZIP64_MAGIC.putLong(buf, 24);
        }
        else {
            ZipLong.putLong(ze.getCompressedSize(), buf, 20);
            ZipLong.putLong(ze.getSize(), buf, 24);
        }
        ZipShort.putShort(nameLen, buf, 28);
        ZipShort.putShort(extraLength, buf, 30);
        ZipShort.putShort(commentLen, buf, 32);
        if (this.isSplitZip) {
            if (ze.getDiskNumberStart() >= 65535L || this.zip64Mode == Zip64Mode.Always) {
                ZipShort.putShort(65535, buf, 34);
            }
            else {
                ZipShort.putShort((int)ze.getDiskNumberStart(), buf, 34);
            }
        }
        else {
            System.arraycopy(ZipArchiveOutputStream.ZERO, 0, buf, 34, 2);
        }
        ZipShort.putShort(ze.getInternalAttributes(), buf, 36);
        ZipLong.putLong(ze.getExternalAttributes(), buf, 38);
        if (entryMetaData.offset >= 4294967295L || this.zip64Mode == Zip64Mode.Always) {
            ZipLong.putLong(4294967295L, buf, 42);
        }
        else {
            ZipLong.putLong(Math.min(entryMetaData.offset, 4294967295L), buf, 42);
        }
        System.arraycopy(name.array(), name.arrayOffset(), buf, 46, nameLen);
        final int extraStart = 46 + nameLen;
        System.arraycopy(extra, 0, buf, extraStart, extraLength);
        final int commentStart = extraStart + extraLength;
        System.arraycopy(commentB.array(), commentB.arrayOffset(), buf, commentStart, commentLen);
        return buf;
    }
    
    private void handleZip64Extra(final ZipArchiveEntry ze, final long lfhOffset, final boolean needsZip64Extra) {
        if (needsZip64Extra) {
            final Zip64ExtendedInformationExtraField z64 = this.getZip64Extra(ze);
            if (ze.getCompressedSize() >= 4294967295L || ze.getSize() >= 4294967295L || this.zip64Mode == Zip64Mode.Always || this.zip64Mode == Zip64Mode.AlwaysWithCompatibility) {
                z64.setCompressedSize(new ZipEightByteInteger(ze.getCompressedSize()));
                z64.setSize(new ZipEightByteInteger(ze.getSize()));
            }
            else {
                z64.setCompressedSize(null);
                z64.setSize(null);
            }
            final boolean needsToEncodeLfhOffset = lfhOffset >= 4294967295L || this.zip64Mode == Zip64Mode.Always;
            final boolean needsToEncodeDiskNumberStart = ze.getDiskNumberStart() >= 65535L || this.zip64Mode == Zip64Mode.Always;
            if (needsToEncodeLfhOffset || needsToEncodeDiskNumberStart) {
                z64.setRelativeHeaderOffset(new ZipEightByteInteger(lfhOffset));
            }
            if (needsToEncodeDiskNumberStart) {
                z64.setDiskStartNumber(new ZipLong(ze.getDiskNumberStart()));
            }
            ze.setExtra();
        }
    }
    
    protected void writeCentralDirectoryEnd() throws IOException {
        if (!this.hasUsedZip64 && this.isSplitZip) {
            ((ZipSplitOutputStream)this.out).prepareToWriteUnsplittableContent(this.eocdLength);
        }
        this.validateIfZip64IsNeededInEOCD();
        this.writeCounted(ZipArchiveOutputStream.EOCD_SIG);
        int numberOfThisDisk = 0;
        if (this.isSplitZip) {
            numberOfThisDisk = ((ZipSplitOutputStream)this.out).getCurrentSplitSegmentIndex();
        }
        this.writeCounted(ZipShort.getBytes(numberOfThisDisk));
        this.writeCounted(ZipShort.getBytes((int)this.cdDiskNumberStart));
        final int numberOfEntries = this.entries.size();
        final int numOfEntriesOnThisDisk = this.isSplitZip ? ((this.numberOfCDInDiskData.get(numberOfThisDisk) == null) ? 0 : this.numberOfCDInDiskData.get(numberOfThisDisk)) : numberOfEntries;
        final byte[] numOfEntriesOnThisDiskData = ZipShort.getBytes(Math.min(numOfEntriesOnThisDisk, 65535));
        this.writeCounted(numOfEntriesOnThisDiskData);
        final byte[] num = ZipShort.getBytes(Math.min(numberOfEntries, 65535));
        this.writeCounted(num);
        this.writeCounted(ZipLong.getBytes(Math.min(this.cdLength, 4294967295L)));
        this.writeCounted(ZipLong.getBytes(Math.min(this.cdOffset, 4294967295L)));
        final ByteBuffer data = this.zipEncoding.encode(this.comment);
        final int dataLen = data.limit() - data.position();
        this.writeCounted(ZipShort.getBytes(dataLen));
        this.streamCompressor.writeCounted(data.array(), data.arrayOffset(), dataLen);
    }
    
    private void validateIfZip64IsNeededInEOCD() throws Zip64RequiredException {
        if (this.zip64Mode != Zip64Mode.Never) {
            return;
        }
        int numberOfThisDisk = 0;
        if (this.isSplitZip) {
            numberOfThisDisk = ((ZipSplitOutputStream)this.out).getCurrentSplitSegmentIndex();
        }
        if (numberOfThisDisk >= 65535) {
            throw new Zip64RequiredException("Number of the disk of End Of Central Directory exceeds the limit of 65535.");
        }
        if (this.cdDiskNumberStart >= 65535L) {
            throw new Zip64RequiredException("Number of the disk with the start of Central Directory exceeds the limit of 65535.");
        }
        final int numOfEntriesOnThisDisk = (this.numberOfCDInDiskData.get(numberOfThisDisk) == null) ? 0 : this.numberOfCDInDiskData.get(numberOfThisDisk);
        if (numOfEntriesOnThisDisk >= 65535) {
            throw new Zip64RequiredException("Number of entries on this disk exceeds the limit of 65535.");
        }
        if (this.entries.size() >= 65535) {
            throw new Zip64RequiredException("Archive contains more than 65535 entries.");
        }
        if (this.cdLength >= 4294967295L) {
            throw new Zip64RequiredException("The size of the entire central directory exceeds the limit of 4GByte.");
        }
        if (this.cdOffset >= 4294967295L) {
            throw new Zip64RequiredException("Archive's size exceeds the limit of 4GByte.");
        }
    }
    
    protected void writeZip64CentralDirectory() throws IOException {
        if (this.zip64Mode == Zip64Mode.Never) {
            return;
        }
        if (!this.hasUsedZip64 && this.shouldUseZip64EOCD()) {
            this.hasUsedZip64 = true;
        }
        if (!this.hasUsedZip64) {
            return;
        }
        long offset = this.streamCompressor.getTotalBytesWritten();
        long diskNumberStart = 0L;
        if (this.isSplitZip) {
            final ZipSplitOutputStream zipSplitOutputStream = (ZipSplitOutputStream)this.out;
            offset = zipSplitOutputStream.getCurrentSplitSegmentBytesWritten();
            diskNumberStart = zipSplitOutputStream.getCurrentSplitSegmentIndex();
        }
        this.writeOut(ZipArchiveOutputStream.ZIP64_EOCD_SIG);
        this.writeOut(ZipEightByteInteger.getBytes(44L));
        this.writeOut(ZipShort.getBytes(45));
        this.writeOut(ZipShort.getBytes(45));
        int numberOfThisDisk = 0;
        if (this.isSplitZip) {
            numberOfThisDisk = ((ZipSplitOutputStream)this.out).getCurrentSplitSegmentIndex();
        }
        this.writeOut(ZipLong.getBytes(numberOfThisDisk));
        this.writeOut(ZipLong.getBytes(this.cdDiskNumberStart));
        final int numOfEntriesOnThisDisk = this.isSplitZip ? ((this.numberOfCDInDiskData.get(numberOfThisDisk) == null) ? 0 : this.numberOfCDInDiskData.get(numberOfThisDisk)) : this.entries.size();
        final byte[] numOfEntriesOnThisDiskData = ZipEightByteInteger.getBytes(numOfEntriesOnThisDisk);
        this.writeOut(numOfEntriesOnThisDiskData);
        final byte[] num = ZipEightByteInteger.getBytes(this.entries.size());
        this.writeOut(num);
        this.writeOut(ZipEightByteInteger.getBytes(this.cdLength));
        this.writeOut(ZipEightByteInteger.getBytes(this.cdOffset));
        if (this.isSplitZip) {
            final int zip64EOCDLOCLength = 20;
            final long unsplittableContentSize = 20L + this.eocdLength;
            ((ZipSplitOutputStream)this.out).prepareToWriteUnsplittableContent(unsplittableContentSize);
        }
        this.writeOut(ZipArchiveOutputStream.ZIP64_EOCD_LOC_SIG);
        this.writeOut(ZipLong.getBytes(diskNumberStart));
        this.writeOut(ZipEightByteInteger.getBytes(offset));
        if (this.isSplitZip) {
            final int totalNumberOfDisks = ((ZipSplitOutputStream)this.out).getCurrentSplitSegmentIndex() + 1;
            this.writeOut(ZipLong.getBytes(totalNumberOfDisks));
        }
        else {
            this.writeOut(ZipArchiveOutputStream.ONE);
        }
    }
    
    private boolean shouldUseZip64EOCD() {
        int numberOfThisDisk = 0;
        if (this.isSplitZip) {
            numberOfThisDisk = ((ZipSplitOutputStream)this.out).getCurrentSplitSegmentIndex();
        }
        final int numOfEntriesOnThisDisk = (this.numberOfCDInDiskData.get(numberOfThisDisk) == null) ? 0 : this.numberOfCDInDiskData.get(numberOfThisDisk);
        return numberOfThisDisk >= 65535 || this.cdDiskNumberStart >= 65535L || numOfEntriesOnThisDisk >= 65535 || this.entries.size() >= 65535 || this.cdLength >= 4294967295L || this.cdOffset >= 4294967295L;
    }
    
    protected final void writeOut(final byte[] data) throws IOException {
        this.streamCompressor.writeOut(data, 0, data.length);
    }
    
    protected final void writeOut(final byte[] data, final int offset, final int length) throws IOException {
        this.streamCompressor.writeOut(data, offset, length);
    }
    
    private GeneralPurposeBit getGeneralPurposeBits(final boolean utfFallback, final boolean usesDataDescriptor) {
        final GeneralPurposeBit b = new GeneralPurposeBit();
        b.useUTF8ForNames(this.useUTF8Flag || utfFallback);
        if (usesDataDescriptor) {
            b.useDataDescriptor(true);
        }
        return b;
    }
    
    private int versionNeededToExtract(final int zipMethod, final boolean zip64, final boolean usedDataDescriptor) {
        if (zip64) {
            return 45;
        }
        if (usedDataDescriptor) {
            return 20;
        }
        return this.versionNeededToExtractMethod(zipMethod);
    }
    
    private boolean usesDataDescriptor(final int zipMethod, final boolean phased) {
        return !phased && zipMethod == 8 && this.channel == null;
    }
    
    private int versionNeededToExtractMethod(final int zipMethod) {
        return (zipMethod == 8) ? 20 : 10;
    }
    
    @Override
    public ArchiveEntry createArchiveEntry(final File inputFile, final String entryName) throws IOException {
        if (this.finished) {
            throw new IOException("Stream has already been finished");
        }
        return new ZipArchiveEntry(inputFile, entryName);
    }
    
    @Override
    public ArchiveEntry createArchiveEntry(final Path inputPath, final String entryName, final LinkOption... options) throws IOException {
        if (this.finished) {
            throw new IOException("Stream has already been finished");
        }
        return new ZipArchiveEntry(inputPath, entryName, new LinkOption[0]);
    }
    
    private Zip64ExtendedInformationExtraField getZip64Extra(final ZipArchiveEntry ze) {
        if (this.entry != null) {
            this.entry.causedUseOfZip64 = !this.hasUsedZip64;
        }
        this.hasUsedZip64 = true;
        final ZipExtraField extra = ze.getExtraField(Zip64ExtendedInformationExtraField.HEADER_ID);
        Zip64ExtendedInformationExtraField z64 = (extra instanceof Zip64ExtendedInformationExtraField) ? ((Zip64ExtendedInformationExtraField)extra) : null;
        if (z64 == null) {
            z64 = new Zip64ExtendedInformationExtraField();
        }
        ze.addAsFirstExtraField(z64);
        return z64;
    }
    
    private boolean hasZip64Extra(final ZipArchiveEntry ze) {
        return ze.getExtraField(Zip64ExtendedInformationExtraField.HEADER_ID) instanceof Zip64ExtendedInformationExtraField;
    }
    
    private Zip64Mode getEffectiveZip64Mode(final ZipArchiveEntry ze) {
        if (this.zip64Mode != Zip64Mode.AsNeeded || this.channel != null || ze.getMethod() != 8 || ze.getSize() != -1L) {
            return this.zip64Mode;
        }
        return Zip64Mode.Never;
    }
    
    private ZipEncoding getEntryEncoding(final ZipArchiveEntry ze) {
        final boolean encodable = this.zipEncoding.canEncode(ze.getName());
        return (!encodable && this.fallbackToUTF8) ? ZipEncodingHelper.UTF8_ZIP_ENCODING : this.zipEncoding;
    }
    
    private ByteBuffer getName(final ZipArchiveEntry ze) throws IOException {
        return this.getEntryEncoding(ze).encode(ze.getName());
    }
    
    void destroy() throws IOException {
        try {
            if (this.channel != null) {
                this.channel.close();
            }
        }
        finally {
            if (this.out != null) {
                this.out.close();
            }
        }
    }
    
    static {
        ZERO = new byte[] { 0, 0 };
        LZERO = new byte[] { 0, 0, 0, 0 };
        ONE = ZipLong.getBytes(1L);
        LFH_SIG = ZipLong.LFH_SIG.getBytes();
        DD_SIG = ZipLong.DD_SIG.getBytes();
        CFH_SIG = ZipLong.CFH_SIG.getBytes();
        EOCD_SIG = ZipLong.getBytes(101010256L);
        ZIP64_EOCD_SIG = ZipLong.getBytes(101075792L);
        ZIP64_EOCD_LOC_SIG = ZipLong.getBytes(117853008L);
    }
    
    public static final class UnicodeExtraFieldPolicy
    {
        public static final UnicodeExtraFieldPolicy ALWAYS;
        public static final UnicodeExtraFieldPolicy NEVER;
        public static final UnicodeExtraFieldPolicy NOT_ENCODEABLE;
        private final String name;
        
        private UnicodeExtraFieldPolicy(final String n) {
            this.name = n;
        }
        
        @Override
        public String toString() {
            return this.name;
        }
        
        static {
            ALWAYS = new UnicodeExtraFieldPolicy("always");
            NEVER = new UnicodeExtraFieldPolicy("never");
            NOT_ENCODEABLE = new UnicodeExtraFieldPolicy("not encodeable");
        }
    }
    
    private static final class CurrentEntry
    {
        private final ZipArchiveEntry entry;
        private long localDataStart;
        private long dataStart;
        private long bytesRead;
        private boolean causedUseOfZip64;
        private boolean hasWritten;
        
        private CurrentEntry(final ZipArchiveEntry entry) {
            this.entry = entry;
        }
    }
    
    private static final class EntryMetaData
    {
        private final long offset;
        private final boolean usesDataDescriptor;
        
        private EntryMetaData(final long offset, final boolean usesDataDescriptor) {
            this.offset = offset;
            this.usesDataDescriptor = usesDataDescriptor;
        }
    }
}
