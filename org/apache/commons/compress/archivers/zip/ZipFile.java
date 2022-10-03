package org.apache.commons.compress.archivers.zip;

import org.apache.commons.compress.utils.InputStreamStatistics;
import org.apache.commons.compress.utils.CountingInputStream;
import org.apache.commons.compress.utils.BoundedSeekableByteChannelInputStream;
import java.nio.channels.FileChannel;
import org.apache.commons.compress.utils.BoundedArchiveInputStream;
import java.util.Iterator;
import java.util.zip.ZipException;
import java.io.EOFException;
import java.nio.channels.ReadableByteChannel;
import org.apache.commons.compress.compressors.deflate64.Deflate64CompressorInputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import java.io.SequenceInputStream;
import java.io.ByteArrayInputStream;
import java.util.zip.Inflater;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import org.apache.commons.compress.utils.IOUtils;
import java.util.HashMap;
import java.nio.file.OpenOption;
import java.util.Set;
import java.nio.file.Files;
import java.nio.file.attribute.FileAttribute;
import java.util.EnumSet;
import java.nio.file.StandardOpenOption;
import java.io.IOException;
import java.io.File;
import java.util.Comparator;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.util.LinkedList;
import java.util.Map;
import java.util.List;
import java.io.Closeable;

public class ZipFile implements Closeable
{
    private static final int HASH_SIZE = 509;
    static final int NIBLET_MASK = 15;
    static final int BYTE_SHIFT = 8;
    private static final int POS_0 = 0;
    private static final int POS_1 = 1;
    private static final int POS_2 = 2;
    private static final int POS_3 = 3;
    private static final byte[] ONE_ZERO_BYTE;
    private final List<ZipArchiveEntry> entries;
    private final Map<String, LinkedList<ZipArchiveEntry>> nameMap;
    private final String encoding;
    private final ZipEncoding zipEncoding;
    private final String archiveName;
    private final SeekableByteChannel archive;
    private final boolean useUnicodeExtraFields;
    private volatile boolean closed;
    private final boolean isSplitZipArchive;
    private final byte[] dwordBuf;
    private final byte[] wordBuf;
    private final byte[] cfhBuf;
    private final byte[] shortBuf;
    private final ByteBuffer dwordBbuf;
    private final ByteBuffer wordBbuf;
    private final ByteBuffer cfhBbuf;
    private final ByteBuffer shortBbuf;
    private long centralDirectoryStartDiskNumber;
    private long centralDirectoryStartRelativeOffset;
    private long centralDirectoryStartOffset;
    private static final int CFH_LEN = 42;
    private static final long CFH_SIG;
    static final int MIN_EOCD_SIZE = 22;
    private static final int MAX_EOCD_SIZE = 65557;
    private static final int CFD_LOCATOR_OFFSET = 16;
    private static final int CFD_DISK_OFFSET = 6;
    private static final int CFD_LOCATOR_RELATIVE_OFFSET = 8;
    private static final int ZIP64_EOCDL_LENGTH = 20;
    private static final int ZIP64_EOCDL_LOCATOR_OFFSET = 8;
    private static final int ZIP64_EOCD_CFD_LOCATOR_OFFSET = 48;
    private static final int ZIP64_EOCD_CFD_DISK_OFFSET = 20;
    private static final int ZIP64_EOCD_CFD_LOCATOR_RELATIVE_OFFSET = 24;
    private static final long LFH_OFFSET_FOR_FILENAME_LENGTH = 26L;
    private final Comparator<ZipArchiveEntry> offsetComparator;
    
    public ZipFile(final File f) throws IOException {
        this(f, "UTF8");
    }
    
    public ZipFile(final String name) throws IOException {
        this(new File(name), "UTF8");
    }
    
    public ZipFile(final String name, final String encoding) throws IOException {
        this(new File(name), encoding, true);
    }
    
    public ZipFile(final File f, final String encoding) throws IOException {
        this(f, encoding, true);
    }
    
    public ZipFile(final File f, final String encoding, final boolean useUnicodeExtraFields) throws IOException {
        this(f, encoding, useUnicodeExtraFields, false);
    }
    
    public ZipFile(final File f, final String encoding, final boolean useUnicodeExtraFields, final boolean ignoreLocalFileHeader) throws IOException {
        this(Files.newByteChannel(f.toPath(), EnumSet.of(StandardOpenOption.READ), (FileAttribute<?>[])new FileAttribute[0]), f.getAbsolutePath(), encoding, useUnicodeExtraFields, true, ignoreLocalFileHeader);
    }
    
    public ZipFile(final SeekableByteChannel channel) throws IOException {
        this(channel, "unknown archive", "UTF8", true);
    }
    
    public ZipFile(final SeekableByteChannel channel, final String encoding) throws IOException {
        this(channel, "unknown archive", encoding, true);
    }
    
    public ZipFile(final SeekableByteChannel channel, final String archiveName, final String encoding, final boolean useUnicodeExtraFields) throws IOException {
        this(channel, archiveName, encoding, useUnicodeExtraFields, false, false);
    }
    
    public ZipFile(final SeekableByteChannel channel, final String archiveName, final String encoding, final boolean useUnicodeExtraFields, final boolean ignoreLocalFileHeader) throws IOException {
        this(channel, archiveName, encoding, useUnicodeExtraFields, false, ignoreLocalFileHeader);
    }
    
    private ZipFile(final SeekableByteChannel channel, final String archiveName, final String encoding, final boolean useUnicodeExtraFields, final boolean closeOnError, final boolean ignoreLocalFileHeader) throws IOException {
        this.entries = new LinkedList<ZipArchiveEntry>();
        this.nameMap = new HashMap<String, LinkedList<ZipArchiveEntry>>(509);
        this.closed = true;
        this.dwordBuf = new byte[8];
        this.wordBuf = new byte[4];
        this.cfhBuf = new byte[42];
        this.shortBuf = new byte[2];
        this.dwordBbuf = ByteBuffer.wrap(this.dwordBuf);
        this.wordBbuf = ByteBuffer.wrap(this.wordBuf);
        this.cfhBbuf = ByteBuffer.wrap(this.cfhBuf);
        this.shortBbuf = ByteBuffer.wrap(this.shortBuf);
        this.offsetComparator = Comparator.comparingLong(ZipArchiveEntry::getDiskNumberStart).thenComparingLong(ZipArchiveEntry::getLocalHeaderOffset);
        this.isSplitZipArchive = (channel instanceof ZipSplitReadOnlySeekableByteChannel);
        this.archiveName = archiveName;
        this.encoding = encoding;
        this.zipEncoding = ZipEncodingHelper.getZipEncoding(encoding);
        this.useUnicodeExtraFields = useUnicodeExtraFields;
        this.archive = channel;
        boolean success = false;
        try {
            final Map<ZipArchiveEntry, NameAndComment> entriesWithoutUTF8Flag = this.populateFromCentralDirectory();
            if (!ignoreLocalFileHeader) {
                this.resolveLocalFileHeaderData(entriesWithoutUTF8Flag);
            }
            this.fillNameMap();
            success = true;
        }
        catch (final IOException e) {
            throw new IOException("Error on ZipFile " + archiveName, e);
        }
        finally {
            this.closed = !success;
            if (!success && closeOnError) {
                IOUtils.closeQuietly(this.archive);
            }
        }
    }
    
    public String getEncoding() {
        return this.encoding;
    }
    
    @Override
    public void close() throws IOException {
        this.closed = true;
        this.archive.close();
    }
    
    public static void closeQuietly(final ZipFile zipfile) {
        IOUtils.closeQuietly(zipfile);
    }
    
    public Enumeration<ZipArchiveEntry> getEntries() {
        return Collections.enumeration(this.entries);
    }
    
    public Enumeration<ZipArchiveEntry> getEntriesInPhysicalOrder() {
        final ZipArchiveEntry[] allEntries = this.entries.toArray(ZipArchiveEntry.EMPTY_ZIP_ARCHIVE_ENTRY_ARRAY);
        Arrays.sort(allEntries, this.offsetComparator);
        return Collections.enumeration(Arrays.asList(allEntries));
    }
    
    public ZipArchiveEntry getEntry(final String name) {
        final LinkedList<ZipArchiveEntry> entriesOfThatName = this.nameMap.get(name);
        return (entriesOfThatName != null) ? entriesOfThatName.getFirst() : null;
    }
    
    public Iterable<ZipArchiveEntry> getEntries(final String name) {
        final List<ZipArchiveEntry> entriesOfThatName = this.nameMap.get(name);
        return (entriesOfThatName != null) ? entriesOfThatName : Collections.emptyList();
    }
    
    public Iterable<ZipArchiveEntry> getEntriesInPhysicalOrder(final String name) {
        ZipArchiveEntry[] entriesOfThatName = ZipArchiveEntry.EMPTY_ZIP_ARCHIVE_ENTRY_ARRAY;
        if (this.nameMap.containsKey(name)) {
            entriesOfThatName = (ZipArchiveEntry[])this.nameMap.get(name).toArray(entriesOfThatName);
            Arrays.sort(entriesOfThatName, this.offsetComparator);
        }
        return Arrays.asList(entriesOfThatName);
    }
    
    public boolean canReadEntryData(final ZipArchiveEntry ze) {
        return ZipUtil.canHandleEntryData(ze);
    }
    
    public InputStream getRawInputStream(final ZipArchiveEntry ze) {
        if (!(ze instanceof Entry)) {
            return null;
        }
        final long start = ze.getDataOffset();
        if (start == -1L) {
            return null;
        }
        return this.createBoundedInputStream(start, ze.getCompressedSize());
    }
    
    public void copyRawEntries(final ZipArchiveOutputStream target, final ZipArchiveEntryPredicate predicate) throws IOException {
        final Enumeration<ZipArchiveEntry> src = this.getEntriesInPhysicalOrder();
        while (src.hasMoreElements()) {
            final ZipArchiveEntry entry = src.nextElement();
            if (predicate.test(entry)) {
                target.addRawArchiveEntry(entry, this.getRawInputStream(entry));
            }
        }
    }
    
    public InputStream getInputStream(final ZipArchiveEntry ze) throws IOException {
        if (!(ze instanceof Entry)) {
            return null;
        }
        ZipUtil.checkRequestedFeatures(ze);
        final long start = this.getDataOffset(ze);
        final InputStream is = new BufferedInputStream(this.createBoundedInputStream(start, ze.getCompressedSize()));
        switch (ZipMethod.getMethodByCode(ze.getMethod())) {
            case STORED: {
                return new StoredStatisticsStream(is);
            }
            case UNSHRINKING: {
                return new UnshrinkingInputStream(is);
            }
            case IMPLODING: {
                try {
                    return new ExplodingInputStream(ze.getGeneralPurposeBit().getSlidingDictionarySize(), ze.getGeneralPurposeBit().getNumberOfShannonFanoTrees(), is);
                }
                catch (final IllegalArgumentException ex) {
                    throw new IOException("bad IMPLODE data", ex);
                }
            }
            case DEFLATED: {
                final Inflater inflater = new Inflater(true);
                return new InflaterInputStreamWithStatistics(new SequenceInputStream(is, new ByteArrayInputStream(ZipFile.ONE_ZERO_BYTE)), inflater) {
                    @Override
                    public void close() throws IOException {
                        try {
                            super.close();
                        }
                        finally {
                            inflater.end();
                        }
                    }
                };
            }
            case BZIP2: {
                return new BZip2CompressorInputStream(is);
            }
            case ENHANCED_DEFLATED: {
                return new Deflate64CompressorInputStream(is);
            }
            default: {
                throw new UnsupportedZipFeatureException(ZipMethod.getMethodByCode(ze.getMethod()), ze);
            }
        }
    }
    
    public String getUnixSymlink(final ZipArchiveEntry entry) throws IOException {
        if (entry != null && entry.isUnixSymlink()) {
            try (final InputStream in = this.getInputStream(entry)) {
                return this.zipEncoding.decode(IOUtils.toByteArray(in));
            }
        }
        return null;
    }
    
    @Override
    protected void finalize() throws Throwable {
        try {
            if (!this.closed) {
                System.err.println("Cleaning up unclosed ZipFile for archive " + this.archiveName);
                this.close();
            }
        }
        finally {
            super.finalize();
        }
    }
    
    private Map<ZipArchiveEntry, NameAndComment> populateFromCentralDirectory() throws IOException {
        final HashMap<ZipArchiveEntry, NameAndComment> noUTF8Flag = new HashMap<ZipArchiveEntry, NameAndComment>();
        this.positionAtCentralDirectory();
        this.centralDirectoryStartOffset = this.archive.position();
        this.wordBbuf.rewind();
        IOUtils.readFully(this.archive, this.wordBbuf);
        long sig = ZipLong.getValue(this.wordBuf);
        if (sig != ZipFile.CFH_SIG && this.startsWithLocalFileHeader()) {
            throw new IOException("Central directory is empty, can't expand corrupt archive.");
        }
        while (sig == ZipFile.CFH_SIG) {
            this.readCentralDirectoryEntry(noUTF8Flag);
            this.wordBbuf.rewind();
            IOUtils.readFully(this.archive, this.wordBbuf);
            sig = ZipLong.getValue(this.wordBuf);
        }
        return noUTF8Flag;
    }
    
    private void readCentralDirectoryEntry(final Map<ZipArchiveEntry, NameAndComment> noUTF8Flag) throws IOException {
        this.cfhBbuf.rewind();
        IOUtils.readFully(this.archive, this.cfhBbuf);
        int off = 0;
        final Entry ze = new Entry();
        final int versionMadeBy = ZipShort.getValue(this.cfhBuf, off);
        off += 2;
        ze.setVersionMadeBy(versionMadeBy);
        ze.setPlatform(versionMadeBy >> 8 & 0xF);
        ze.setVersionRequired(ZipShort.getValue(this.cfhBuf, off));
        off += 2;
        final GeneralPurposeBit gpFlag = GeneralPurposeBit.parse(this.cfhBuf, off);
        final boolean hasUTF8Flag = gpFlag.usesUTF8ForNames();
        final ZipEncoding entryEncoding = hasUTF8Flag ? ZipEncodingHelper.UTF8_ZIP_ENCODING : this.zipEncoding;
        if (hasUTF8Flag) {
            ze.setNameSource(ZipArchiveEntry.NameSource.NAME_WITH_EFS_FLAG);
        }
        ze.setGeneralPurposeBit(gpFlag);
        ze.setRawFlag(ZipShort.getValue(this.cfhBuf, off));
        off += 2;
        ze.setMethod(ZipShort.getValue(this.cfhBuf, off));
        off += 2;
        final long time = ZipUtil.dosToJavaTime(ZipLong.getValue(this.cfhBuf, off));
        ze.setTime(time);
        off += 4;
        ze.setCrc(ZipLong.getValue(this.cfhBuf, off));
        off += 4;
        long size = ZipLong.getValue(this.cfhBuf, off);
        if (size < 0L) {
            throw new IOException("broken archive, entry with negative compressed size");
        }
        ze.setCompressedSize(size);
        off += 4;
        size = ZipLong.getValue(this.cfhBuf, off);
        if (size < 0L) {
            throw new IOException("broken archive, entry with negative size");
        }
        ze.setSize(size);
        off += 4;
        final int fileNameLen = ZipShort.getValue(this.cfhBuf, off);
        off += 2;
        if (fileNameLen < 0) {
            throw new IOException("broken archive, entry with negative fileNameLen");
        }
        final int extraLen = ZipShort.getValue(this.cfhBuf, off);
        off += 2;
        if (extraLen < 0) {
            throw new IOException("broken archive, entry with negative extraLen");
        }
        final int commentLen = ZipShort.getValue(this.cfhBuf, off);
        off += 2;
        if (commentLen < 0) {
            throw new IOException("broken archive, entry with negative commentLen");
        }
        ze.setDiskNumberStart(ZipShort.getValue(this.cfhBuf, off));
        off += 2;
        ze.setInternalAttributes(ZipShort.getValue(this.cfhBuf, off));
        off += 2;
        ze.setExternalAttributes(ZipLong.getValue(this.cfhBuf, off));
        off += 4;
        final byte[] fileName = IOUtils.readRange(this.archive, fileNameLen);
        if (fileName.length < fileNameLen) {
            throw new EOFException();
        }
        ze.setName(entryEncoding.decode(fileName), fileName);
        ze.setLocalHeaderOffset(ZipLong.getValue(this.cfhBuf, off));
        this.entries.add(ze);
        final byte[] cdExtraData = IOUtils.readRange(this.archive, extraLen);
        if (cdExtraData.length < extraLen) {
            throw new EOFException();
        }
        try {
            ze.setCentralDirectoryExtra(cdExtraData);
        }
        catch (final RuntimeException ex) {
            final ZipException z = new ZipException("Invalid extra data in entry " + ze.getName());
            z.initCause(ex);
            throw z;
        }
        this.setSizesAndOffsetFromZip64Extra(ze);
        this.sanityCheckLFHOffset(ze);
        final byte[] comment = IOUtils.readRange(this.archive, commentLen);
        if (comment.length < commentLen) {
            throw new EOFException();
        }
        ze.setComment(entryEncoding.decode(comment));
        if (!hasUTF8Flag && this.useUnicodeExtraFields) {
            noUTF8Flag.put(ze, new NameAndComment(fileName, comment));
        }
        ze.setStreamContiguous(true);
    }
    
    private void sanityCheckLFHOffset(final ZipArchiveEntry ze) throws IOException {
        if (ze.getDiskNumberStart() < 0L) {
            throw new IOException("broken archive, entry with negative disk number");
        }
        if (ze.getLocalHeaderOffset() < 0L) {
            throw new IOException("broken archive, entry with negative local file header offset");
        }
        if (this.isSplitZipArchive) {
            if (ze.getDiskNumberStart() > this.centralDirectoryStartDiskNumber) {
                throw new IOException("local file header for " + ze.getName() + " starts on a later disk than central directory");
            }
            if (ze.getDiskNumberStart() == this.centralDirectoryStartDiskNumber && ze.getLocalHeaderOffset() > this.centralDirectoryStartRelativeOffset) {
                throw new IOException("local file header for " + ze.getName() + " starts after central directory");
            }
        }
        else if (ze.getLocalHeaderOffset() > this.centralDirectoryStartOffset) {
            throw new IOException("local file header for " + ze.getName() + " starts after central directory");
        }
    }
    
    private void setSizesAndOffsetFromZip64Extra(final ZipArchiveEntry ze) throws IOException {
        final ZipExtraField extra = ze.getExtraField(Zip64ExtendedInformationExtraField.HEADER_ID);
        if (extra != null && !(extra instanceof Zip64ExtendedInformationExtraField)) {
            throw new ZipException("archive contains unparseable zip64 extra field");
        }
        final Zip64ExtendedInformationExtraField z64 = (Zip64ExtendedInformationExtraField)extra;
        if (z64 != null) {
            final boolean hasUncompressedSize = ze.getSize() == 4294967295L;
            final boolean hasCompressedSize = ze.getCompressedSize() == 4294967295L;
            final boolean hasRelativeHeaderOffset = ze.getLocalHeaderOffset() == 4294967295L;
            final boolean hasDiskStart = ze.getDiskNumberStart() == 65535L;
            z64.reparseCentralDirectoryData(hasUncompressedSize, hasCompressedSize, hasRelativeHeaderOffset, hasDiskStart);
            if (hasUncompressedSize) {
                final long size = z64.getSize().getLongValue();
                if (size < 0L) {
                    throw new IOException("broken archive, entry with negative size");
                }
                ze.setSize(size);
            }
            else if (hasCompressedSize) {
                z64.setSize(new ZipEightByteInteger(ze.getSize()));
            }
            if (hasCompressedSize) {
                final long size = z64.getCompressedSize().getLongValue();
                if (size < 0L) {
                    throw new IOException("broken archive, entry with negative compressed size");
                }
                ze.setCompressedSize(size);
            }
            else if (hasUncompressedSize) {
                z64.setCompressedSize(new ZipEightByteInteger(ze.getCompressedSize()));
            }
            if (hasRelativeHeaderOffset) {
                ze.setLocalHeaderOffset(z64.getRelativeHeaderOffset().getLongValue());
            }
            if (hasDiskStart) {
                ze.setDiskNumberStart(z64.getDiskStartNumber().getValue());
            }
        }
    }
    
    private void positionAtCentralDirectory() throws IOException {
        this.positionAtEndOfCentralDirectoryRecord();
        boolean found = false;
        final boolean searchedForZip64EOCD = this.archive.position() > 20L;
        if (searchedForZip64EOCD) {
            this.archive.position(this.archive.position() - 20L);
            this.wordBbuf.rewind();
            IOUtils.readFully(this.archive, this.wordBbuf);
            found = Arrays.equals(ZipArchiveOutputStream.ZIP64_EOCD_LOC_SIG, this.wordBuf);
        }
        if (!found) {
            if (searchedForZip64EOCD) {
                this.skipBytes(16);
            }
            this.positionAtCentralDirectory32();
        }
        else {
            this.positionAtCentralDirectory64();
        }
    }
    
    private void positionAtCentralDirectory64() throws IOException {
        if (this.isSplitZipArchive) {
            this.wordBbuf.rewind();
            IOUtils.readFully(this.archive, this.wordBbuf);
            final long diskNumberOfEOCD = ZipLong.getValue(this.wordBuf);
            this.dwordBbuf.rewind();
            IOUtils.readFully(this.archive, this.dwordBbuf);
            final long relativeOffsetOfEOCD = ZipEightByteInteger.getLongValue(this.dwordBuf);
            ((ZipSplitReadOnlySeekableByteChannel)this.archive).position(diskNumberOfEOCD, relativeOffsetOfEOCD);
        }
        else {
            this.skipBytes(4);
            this.dwordBbuf.rewind();
            IOUtils.readFully(this.archive, this.dwordBbuf);
            this.archive.position(ZipEightByteInteger.getLongValue(this.dwordBuf));
        }
        this.wordBbuf.rewind();
        IOUtils.readFully(this.archive, this.wordBbuf);
        if (!Arrays.equals(this.wordBuf, ZipArchiveOutputStream.ZIP64_EOCD_SIG)) {
            throw new ZipException("Archive's ZIP64 end of central directory locator is corrupt.");
        }
        if (this.isSplitZipArchive) {
            this.skipBytes(16);
            this.wordBbuf.rewind();
            IOUtils.readFully(this.archive, this.wordBbuf);
            this.centralDirectoryStartDiskNumber = ZipLong.getValue(this.wordBuf);
            this.skipBytes(24);
            this.dwordBbuf.rewind();
            IOUtils.readFully(this.archive, this.dwordBbuf);
            this.centralDirectoryStartRelativeOffset = ZipEightByteInteger.getLongValue(this.dwordBuf);
            ((ZipSplitReadOnlySeekableByteChannel)this.archive).position(this.centralDirectoryStartDiskNumber, this.centralDirectoryStartRelativeOffset);
        }
        else {
            this.skipBytes(44);
            this.dwordBbuf.rewind();
            IOUtils.readFully(this.archive, this.dwordBbuf);
            this.centralDirectoryStartDiskNumber = 0L;
            this.centralDirectoryStartRelativeOffset = ZipEightByteInteger.getLongValue(this.dwordBuf);
            this.archive.position(this.centralDirectoryStartRelativeOffset);
        }
    }
    
    private void positionAtCentralDirectory32() throws IOException {
        if (this.isSplitZipArchive) {
            this.skipBytes(6);
            this.shortBbuf.rewind();
            IOUtils.readFully(this.archive, this.shortBbuf);
            this.centralDirectoryStartDiskNumber = ZipShort.getValue(this.shortBuf);
            this.skipBytes(8);
            this.wordBbuf.rewind();
            IOUtils.readFully(this.archive, this.wordBbuf);
            this.centralDirectoryStartRelativeOffset = ZipLong.getValue(this.wordBuf);
            ((ZipSplitReadOnlySeekableByteChannel)this.archive).position(this.centralDirectoryStartDiskNumber, this.centralDirectoryStartRelativeOffset);
        }
        else {
            this.skipBytes(16);
            this.wordBbuf.rewind();
            IOUtils.readFully(this.archive, this.wordBbuf);
            this.centralDirectoryStartDiskNumber = 0L;
            this.centralDirectoryStartRelativeOffset = ZipLong.getValue(this.wordBuf);
            this.archive.position(this.centralDirectoryStartRelativeOffset);
        }
    }
    
    private void positionAtEndOfCentralDirectoryRecord() throws IOException {
        final boolean found = this.tryToLocateSignature(22L, 65557L, ZipArchiveOutputStream.EOCD_SIG);
        if (!found) {
            throw new ZipException("Archive is not a ZIP archive");
        }
    }
    
    private boolean tryToLocateSignature(final long minDistanceFromEnd, final long maxDistanceFromEnd, final byte[] sig) throws IOException {
        boolean found = false;
        long off = this.archive.size() - minDistanceFromEnd;
        final long stopSearching = Math.max(0L, this.archive.size() - maxDistanceFromEnd);
        if (off >= 0L) {
            while (off >= stopSearching) {
                this.archive.position(off);
                try {
                    this.wordBbuf.rewind();
                    IOUtils.readFully(this.archive, this.wordBbuf);
                    this.wordBbuf.flip();
                }
                catch (final EOFException ex) {
                    break;
                }
                int curr = this.wordBbuf.get();
                if (curr == sig[0]) {
                    curr = this.wordBbuf.get();
                    if (curr == sig[1]) {
                        curr = this.wordBbuf.get();
                        if (curr == sig[2]) {
                            curr = this.wordBbuf.get();
                            if (curr == sig[3]) {
                                found = true;
                                break;
                            }
                        }
                    }
                }
                --off;
            }
        }
        if (found) {
            this.archive.position(off);
        }
        return found;
    }
    
    private void skipBytes(final int count) throws IOException {
        final long currentPosition = this.archive.position();
        final long newPosition = currentPosition + count;
        if (newPosition > this.archive.size()) {
            throw new EOFException();
        }
        this.archive.position(newPosition);
    }
    
    private void resolveLocalFileHeaderData(final Map<ZipArchiveEntry, NameAndComment> entriesWithoutUTF8Flag) throws IOException {
        for (final ZipArchiveEntry zipArchiveEntry : this.entries) {
            final Entry ze = (Entry)zipArchiveEntry;
            final int[] lens = this.setDataOffset(ze);
            final int fileNameLen = lens[0];
            final int extraFieldLen = lens[1];
            this.skipBytes(fileNameLen);
            final byte[] localExtraData = IOUtils.readRange(this.archive, extraFieldLen);
            if (localExtraData.length < extraFieldLen) {
                throw new EOFException();
            }
            try {
                ze.setExtra(localExtraData);
            }
            catch (final RuntimeException ex) {
                final ZipException z = new ZipException("Invalid extra data in entry " + ze.getName());
                z.initCause(ex);
                throw z;
            }
            if (!entriesWithoutUTF8Flag.containsKey(ze)) {
                continue;
            }
            final NameAndComment nc = entriesWithoutUTF8Flag.get(ze);
            ZipUtil.setNameAndCommentFromExtraFields(ze, nc.name, nc.comment);
        }
    }
    
    private void fillNameMap() {
        for (final ZipArchiveEntry ze : this.entries) {
            final String name = ze.getName();
            final LinkedList<ZipArchiveEntry> entriesOfThatName = this.nameMap.computeIfAbsent(name, k -> new LinkedList());
            entriesOfThatName.addLast(ze);
        }
    }
    
    private int[] setDataOffset(final ZipArchiveEntry ze) throws IOException {
        long offset = ze.getLocalHeaderOffset();
        if (this.isSplitZipArchive) {
            ((ZipSplitReadOnlySeekableByteChannel)this.archive).position(ze.getDiskNumberStart(), offset + 26L);
            offset = this.archive.position() - 26L;
        }
        else {
            this.archive.position(offset + 26L);
        }
        this.wordBbuf.rewind();
        IOUtils.readFully(this.archive, this.wordBbuf);
        this.wordBbuf.flip();
        this.wordBbuf.get(this.shortBuf);
        final int fileNameLen = ZipShort.getValue(this.shortBuf);
        this.wordBbuf.get(this.shortBuf);
        final int extraFieldLen = ZipShort.getValue(this.shortBuf);
        ze.setDataOffset(offset + 26L + 2L + 2L + fileNameLen + extraFieldLen);
        if (ze.getDataOffset() + ze.getCompressedSize() > this.centralDirectoryStartOffset) {
            throw new IOException("data for " + ze.getName() + " overlaps with central directory.");
        }
        return new int[] { fileNameLen, extraFieldLen };
    }
    
    private long getDataOffset(final ZipArchiveEntry ze) throws IOException {
        final long s = ze.getDataOffset();
        if (s == -1L) {
            this.setDataOffset(ze);
            return ze.getDataOffset();
        }
        return s;
    }
    
    private boolean startsWithLocalFileHeader() throws IOException {
        this.archive.position(0L);
        this.wordBbuf.rewind();
        IOUtils.readFully(this.archive, this.wordBbuf);
        return Arrays.equals(this.wordBuf, ZipArchiveOutputStream.LFH_SIG);
    }
    
    private BoundedArchiveInputStream createBoundedInputStream(final long start, final long remaining) {
        if (start < 0L || remaining < 0L || start + remaining < start) {
            throw new IllegalArgumentException("Corrupted archive, stream boundaries are out of range");
        }
        return (this.archive instanceof FileChannel) ? new BoundedFileChannelInputStream(start, remaining) : new BoundedSeekableByteChannelInputStream(start, remaining, this.archive);
    }
    
    static {
        ONE_ZERO_BYTE = new byte[1];
        CFH_SIG = ZipLong.getValue(ZipArchiveOutputStream.CFH_SIG);
    }
    
    private class BoundedFileChannelInputStream extends BoundedArchiveInputStream
    {
        private final FileChannel archive;
        
        BoundedFileChannelInputStream(final long start, final long remaining) {
            super(start, remaining);
            this.archive = (FileChannel)ZipFile.this.archive;
        }
        
        @Override
        protected int read(final long pos, final ByteBuffer buf) throws IOException {
            final int read = this.archive.read(buf, pos);
            buf.flip();
            return read;
        }
    }
    
    private static final class NameAndComment
    {
        private final byte[] name;
        private final byte[] comment;
        
        private NameAndComment(final byte[] name, final byte[] comment) {
            this.name = name;
            this.comment = comment;
        }
    }
    
    private static class Entry extends ZipArchiveEntry
    {
        Entry() {
        }
        
        @Override
        public int hashCode() {
            return 3 * super.hashCode() + (int)this.getLocalHeaderOffset() + (int)(this.getLocalHeaderOffset() >> 32);
        }
        
        @Override
        public boolean equals(final Object other) {
            if (super.equals(other)) {
                final Entry otherEntry = (Entry)other;
                return this.getLocalHeaderOffset() == otherEntry.getLocalHeaderOffset() && super.getDataOffset() == otherEntry.getDataOffset() && super.getDiskNumberStart() == otherEntry.getDiskNumberStart();
            }
            return false;
        }
    }
    
    private static class StoredStatisticsStream extends CountingInputStream implements InputStreamStatistics
    {
        StoredStatisticsStream(final InputStream in) {
            super(in);
        }
        
        @Override
        public long getCompressedCount() {
            return super.getBytesRead();
        }
        
        @Override
        public long getUncompressedCount() {
            return this.getCompressedCount();
        }
    }
}
