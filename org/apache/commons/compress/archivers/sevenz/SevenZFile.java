package org.apache.commons.compress.archivers.sevenz;

import org.apache.commons.compress.MemoryLimitException;
import java.nio.CharBuffer;
import java.nio.channels.ReadableByteChannel;
import org.apache.commons.compress.utils.InputStreamStatistics;
import java.io.ByteArrayInputStream;
import org.apache.commons.compress.utils.ByteUtils;
import java.io.FilterInputStream;
import java.io.BufferedInputStream;
import org.apache.commons.compress.utils.BoundedInputStream;
import java.util.Map;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.BitSet;
import java.util.List;
import java.util.LinkedList;
import java.util.Iterator;
import org.apache.commons.compress.utils.IOUtils;
import java.io.DataInputStream;
import org.apache.commons.compress.utils.CRC32VerifyingInputStream;
import java.util.zip.CRC32;
import java.io.EOFException;
import java.nio.ByteOrder;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Arrays;
import java.nio.file.OpenOption;
import java.util.Set;
import java.nio.file.Files;
import java.nio.file.attribute.FileAttribute;
import java.util.EnumSet;
import java.nio.file.StandardOpenOption;
import java.io.IOException;
import java.io.File;
import java.nio.charset.CharsetEncoder;
import java.util.ArrayList;
import java.io.InputStream;
import java.nio.channels.SeekableByteChannel;
import java.io.Closeable;

public class SevenZFile implements Closeable
{
    static final int SIGNATURE_HEADER_SIZE = 32;
    private static final String DEFAULT_FILE_NAME = "unknown archive";
    private final String fileName;
    private SeekableByteChannel channel;
    private final Archive archive;
    private int currentEntryIndex;
    private int currentFolderIndex;
    private InputStream currentFolderInputStream;
    private byte[] password;
    private final SevenZFileOptions options;
    private long compressedBytesReadFromCurrentEntry;
    private long uncompressedBytesReadFromCurrentEntry;
    private final ArrayList<InputStream> deferredBlockStreams;
    static final byte[] sevenZSignature;
    private static final CharsetEncoder PASSWORD_ENCODER;
    
    public SevenZFile(final File fileName, final char[] password) throws IOException {
        this(fileName, password, SevenZFileOptions.DEFAULT);
    }
    
    public SevenZFile(final File fileName, final char[] password, final SevenZFileOptions options) throws IOException {
        this(Files.newByteChannel(fileName.toPath(), EnumSet.of(StandardOpenOption.READ), (FileAttribute<?>[])new FileAttribute[0]), fileName.getAbsolutePath(), utf16Decode(password), true, options);
    }
    
    @Deprecated
    public SevenZFile(final File fileName, final byte[] password) throws IOException {
        this(Files.newByteChannel(fileName.toPath(), EnumSet.of(StandardOpenOption.READ), (FileAttribute<?>[])new FileAttribute[0]), fileName.getAbsolutePath(), password, true, SevenZFileOptions.DEFAULT);
    }
    
    public SevenZFile(final SeekableByteChannel channel) throws IOException {
        this(channel, SevenZFileOptions.DEFAULT);
    }
    
    public SevenZFile(final SeekableByteChannel channel, final SevenZFileOptions options) throws IOException {
        this(channel, "unknown archive", null, options);
    }
    
    public SevenZFile(final SeekableByteChannel channel, final char[] password) throws IOException {
        this(channel, password, SevenZFileOptions.DEFAULT);
    }
    
    public SevenZFile(final SeekableByteChannel channel, final char[] password, final SevenZFileOptions options) throws IOException {
        this(channel, "unknown archive", password, options);
    }
    
    public SevenZFile(final SeekableByteChannel channel, final String fileName, final char[] password) throws IOException {
        this(channel, fileName, password, SevenZFileOptions.DEFAULT);
    }
    
    public SevenZFile(final SeekableByteChannel channel, final String fileName, final char[] password, final SevenZFileOptions options) throws IOException {
        this(channel, fileName, utf16Decode(password), false, options);
    }
    
    public SevenZFile(final SeekableByteChannel channel, final String fileName) throws IOException {
        this(channel, fileName, SevenZFileOptions.DEFAULT);
    }
    
    public SevenZFile(final SeekableByteChannel channel, final String fileName, final SevenZFileOptions options) throws IOException {
        this(channel, fileName, null, false, options);
    }
    
    @Deprecated
    public SevenZFile(final SeekableByteChannel channel, final byte[] password) throws IOException {
        this(channel, "unknown archive", password);
    }
    
    @Deprecated
    public SevenZFile(final SeekableByteChannel channel, final String fileName, final byte[] password) throws IOException {
        this(channel, fileName, password, false, SevenZFileOptions.DEFAULT);
    }
    
    private SevenZFile(final SeekableByteChannel channel, final String filename, final byte[] password, final boolean closeOnError, final SevenZFileOptions options) throws IOException {
        this.currentEntryIndex = -1;
        this.currentFolderIndex = -1;
        this.deferredBlockStreams = new ArrayList<InputStream>();
        boolean succeeded = false;
        this.channel = channel;
        this.fileName = filename;
        this.options = options;
        try {
            this.archive = this.readHeaders(password);
            if (password != null) {
                this.password = Arrays.copyOf(password, password.length);
            }
            else {
                this.password = null;
            }
            succeeded = true;
        }
        finally {
            if (!succeeded && closeOnError) {
                this.channel.close();
            }
        }
    }
    
    public SevenZFile(final File fileName) throws IOException {
        this(fileName, SevenZFileOptions.DEFAULT);
    }
    
    public SevenZFile(final File fileName, final SevenZFileOptions options) throws IOException {
        this(fileName, null, options);
    }
    
    @Override
    public void close() throws IOException {
        if (this.channel != null) {
            try {
                this.channel.close();
            }
            finally {
                this.channel = null;
                if (this.password != null) {
                    Arrays.fill(this.password, (byte)0);
                }
                this.password = null;
            }
        }
    }
    
    public SevenZArchiveEntry getNextEntry() throws IOException {
        if (this.currentEntryIndex >= this.archive.files.length - 1) {
            return null;
        }
        ++this.currentEntryIndex;
        final SevenZArchiveEntry entry = this.archive.files[this.currentEntryIndex];
        if (entry.getName() == null && this.options.getUseDefaultNameForUnnamedEntries()) {
            entry.setName(this.getDefaultName());
        }
        this.buildDecodingStream(this.currentEntryIndex, false);
        final long n = 0L;
        this.compressedBytesReadFromCurrentEntry = n;
        this.uncompressedBytesReadFromCurrentEntry = n;
        return entry;
    }
    
    public Iterable<SevenZArchiveEntry> getEntries() {
        return new ArrayList<SevenZArchiveEntry>(Arrays.asList(this.archive.files));
    }
    
    private Archive readHeaders(final byte[] password) throws IOException {
        final ByteBuffer buf = ByteBuffer.allocate(12).order(ByteOrder.LITTLE_ENDIAN);
        this.readFully(buf);
        final byte[] signature = new byte[6];
        buf.get(signature);
        if (!Arrays.equals(signature, SevenZFile.sevenZSignature)) {
            throw new IOException("Bad 7z signature");
        }
        final byte archiveVersionMajor = buf.get();
        final byte archiveVersionMinor = buf.get();
        if (archiveVersionMajor != 0) {
            throw new IOException(String.format("Unsupported 7z version (%d,%d)", archiveVersionMajor, archiveVersionMinor));
        }
        boolean headerLooksValid = false;
        final long startHeaderCrc = 0xFFFFFFFFL & (long)buf.getInt();
        if (startHeaderCrc == 0L) {
            final long currentPosition = this.channel.position();
            final ByteBuffer peekBuf = ByteBuffer.allocate(20);
            this.readFully(peekBuf);
            this.channel.position(currentPosition);
            while (peekBuf.hasRemaining()) {
                if (peekBuf.get() != 0) {
                    headerLooksValid = true;
                    break;
                }
            }
        }
        else {
            headerLooksValid = true;
        }
        if (headerLooksValid) {
            final StartHeader startHeader = this.readStartHeader(startHeaderCrc);
            return this.initializeArchive(startHeader, password, true);
        }
        if (this.options.getTryToRecoverBrokenArchives()) {
            return this.tryToLocateEndHeader(password);
        }
        throw new IOException("archive seems to be invalid.\nYou may want to retry and enable the tryToRecoverBrokenArchives if the archive could be a multi volume archive that has been closed prematurely.");
    }
    
    private Archive tryToLocateEndHeader(final byte[] password) throws IOException {
        final ByteBuffer nidBuf = ByteBuffer.allocate(1);
        final long searchLimit = 1048576L;
        final long previousDataSize = this.channel.position() + 20L;
        long minPos;
        if (this.channel.position() + 1048576L > this.channel.size()) {
            minPos = this.channel.position();
        }
        else {
            minPos = this.channel.size() - 1048576L;
        }
        long pos = this.channel.size() - 1L;
        while (pos > minPos) {
            --pos;
            this.channel.position(pos);
            nidBuf.rewind();
            if (this.channel.read(nidBuf) < 1) {
                throw new EOFException();
            }
            final int nid = nidBuf.array()[0];
            if (nid != 23) {
                if (nid != 1) {
                    continue;
                }
            }
            try {
                final StartHeader startHeader = new StartHeader();
                startHeader.nextHeaderOffset = pos - previousDataSize;
                startHeader.nextHeaderSize = this.channel.size() - pos;
                final Archive result = this.initializeArchive(startHeader, password, false);
                if (result.packSizes.length > 0 && result.files.length > 0) {
                    return result;
                }
                continue;
            }
            catch (final Exception ex) {}
        }
        throw new IOException("Start header corrupt and unable to guess end header");
    }
    
    private Archive initializeArchive(final StartHeader startHeader, final byte[] password, final boolean verifyCrc) throws IOException {
        assertFitsIntoNonNegativeInt("nextHeaderSize", startHeader.nextHeaderSize);
        final int nextHeaderSizeInt = (int)startHeader.nextHeaderSize;
        this.channel.position(32L + startHeader.nextHeaderOffset);
        ByteBuffer buf = ByteBuffer.allocate(nextHeaderSizeInt).order(ByteOrder.LITTLE_ENDIAN);
        this.readFully(buf);
        if (verifyCrc) {
            final CRC32 crc = new CRC32();
            crc.update(buf.array());
            if (startHeader.nextHeaderCrc != crc.getValue()) {
                throw new IOException("NextHeader CRC mismatch");
            }
        }
        Archive archive = new Archive();
        int nid = getUnsignedByte(buf);
        if (nid == 23) {
            buf = this.readEncodedHeader(buf, archive, password);
            archive = new Archive();
            nid = getUnsignedByte(buf);
        }
        if (nid != 1) {
            throw new IOException("Broken or unsupported archive: no Header");
        }
        this.readHeader(buf, archive);
        archive.subStreamsInfo = null;
        return archive;
    }
    
    private StartHeader readStartHeader(final long startHeaderCrc) throws IOException {
        final StartHeader startHeader = new StartHeader();
        try (final DataInputStream dataInputStream = new DataInputStream(new CRC32VerifyingInputStream(new BoundedSeekableByteChannelInputStream(this.channel, 20L), 20L, startHeaderCrc))) {
            startHeader.nextHeaderOffset = Long.reverseBytes(dataInputStream.readLong());
            if (startHeader.nextHeaderOffset < 0L || startHeader.nextHeaderOffset + 32L > this.channel.size()) {
                throw new IOException("nextHeaderOffset is out of bounds");
            }
            startHeader.nextHeaderSize = Long.reverseBytes(dataInputStream.readLong());
            final long nextHeaderEnd = startHeader.nextHeaderOffset + startHeader.nextHeaderSize;
            if (nextHeaderEnd < startHeader.nextHeaderOffset || nextHeaderEnd + 32L > this.channel.size()) {
                throw new IOException("nextHeaderSize is out of bounds");
            }
            startHeader.nextHeaderCrc = (0xFFFFFFFFL & (long)Integer.reverseBytes(dataInputStream.readInt()));
            return startHeader;
        }
    }
    
    private void readHeader(final ByteBuffer header, final Archive archive) throws IOException {
        final int pos = header.position();
        final ArchiveStatistics stats = this.sanityCheckAndCollectStatistics(header);
        stats.assertValidity(this.options.getMaxMemoryLimitInKb());
        header.position(pos);
        int nid = getUnsignedByte(header);
        if (nid == 2) {
            this.readArchiveProperties(header);
            nid = getUnsignedByte(header);
        }
        if (nid == 3) {
            throw new IOException("Additional streams unsupported");
        }
        if (nid == 4) {
            this.readStreamsInfo(header, archive);
            nid = getUnsignedByte(header);
        }
        if (nid == 5) {
            this.readFilesInfo(header, archive);
            nid = getUnsignedByte(header);
        }
    }
    
    private ArchiveStatistics sanityCheckAndCollectStatistics(final ByteBuffer header) throws IOException {
        final ArchiveStatistics stats = new ArchiveStatistics();
        int nid = getUnsignedByte(header);
        if (nid == 2) {
            this.sanityCheckArchiveProperties(header);
            nid = getUnsignedByte(header);
        }
        if (nid == 3) {
            throw new IOException("Additional streams unsupported");
        }
        if (nid == 4) {
            this.sanityCheckStreamsInfo(header, stats);
            nid = getUnsignedByte(header);
        }
        if (nid == 5) {
            this.sanityCheckFilesInfo(header, stats);
            nid = getUnsignedByte(header);
        }
        if (nid != 0) {
            throw new IOException("Badly terminated header, found " + nid);
        }
        return stats;
    }
    
    private void readArchiveProperties(final ByteBuffer input) throws IOException {
        for (int nid = getUnsignedByte(input); nid != 0; nid = getUnsignedByte(input)) {
            final long propertySize = readUint64(input);
            final byte[] property = new byte[(int)propertySize];
            get(input, property);
        }
    }
    
    private void sanityCheckArchiveProperties(final ByteBuffer header) throws IOException {
        for (int nid = getUnsignedByte(header); nid != 0; nid = getUnsignedByte(header)) {
            final int propertySize = assertFitsIntoNonNegativeInt("propertySize", readUint64(header));
            if (skipBytesFully(header, propertySize) < propertySize) {
                throw new IOException("invalid property size");
            }
        }
    }
    
    private ByteBuffer readEncodedHeader(final ByteBuffer header, final Archive archive, final byte[] password) throws IOException {
        final int pos = header.position();
        final ArchiveStatistics stats = new ArchiveStatistics();
        this.sanityCheckStreamsInfo(header, stats);
        stats.assertValidity(this.options.getMaxMemoryLimitInKb());
        header.position(pos);
        this.readStreamsInfo(header, archive);
        if (archive.folders == null || archive.folders.length == 0) {
            throw new IOException("no folders, can't read encoded header");
        }
        if (archive.packSizes == null || archive.packSizes.length == 0) {
            throw new IOException("no packed streams, can't read encoded header");
        }
        final Folder folder = archive.folders[0];
        final int firstPackStreamIndex = 0;
        final long folderOffset = 32L + archive.packPos + 0L;
        this.channel.position(folderOffset);
        InputStream inputStreamStack = new BoundedSeekableByteChannelInputStream(this.channel, archive.packSizes[0]);
        for (final Coder coder : folder.getOrderedCoders()) {
            if (coder.numInStreams != 1L || coder.numOutStreams != 1L) {
                throw new IOException("Multi input/output stream coders are not yet supported");
            }
            inputStreamStack = Coders.addDecoder(this.fileName, inputStreamStack, folder.getUnpackSizeForCoder(coder), coder, password, this.options.getMaxMemoryLimitInKb());
        }
        if (folder.hasCrc) {
            inputStreamStack = new CRC32VerifyingInputStream(inputStreamStack, folder.getUnpackSize(), folder.crc);
        }
        final int unpackSize = assertFitsIntoNonNegativeInt("unpackSize", folder.getUnpackSize());
        final byte[] nextHeader = IOUtils.readRange(inputStreamStack, unpackSize);
        if (nextHeader.length < unpackSize) {
            throw new IOException("premature end of stream");
        }
        inputStreamStack.close();
        return ByteBuffer.wrap(nextHeader).order(ByteOrder.LITTLE_ENDIAN);
    }
    
    private void sanityCheckStreamsInfo(final ByteBuffer header, final ArchiveStatistics stats) throws IOException {
        int nid = getUnsignedByte(header);
        if (nid == 6) {
            this.sanityCheckPackInfo(header, stats);
            nid = getUnsignedByte(header);
        }
        if (nid == 7) {
            this.sanityCheckUnpackInfo(header, stats);
            nid = getUnsignedByte(header);
        }
        if (nid == 8) {
            this.sanityCheckSubStreamsInfo(header, stats);
            nid = getUnsignedByte(header);
        }
        if (nid != 0) {
            throw new IOException("Badly terminated StreamsInfo");
        }
    }
    
    private void readStreamsInfo(final ByteBuffer header, final Archive archive) throws IOException {
        int nid = getUnsignedByte(header);
        if (nid == 6) {
            this.readPackInfo(header, archive);
            nid = getUnsignedByte(header);
        }
        if (nid == 7) {
            this.readUnpackInfo(header, archive);
            nid = getUnsignedByte(header);
        }
        else {
            archive.folders = Folder.EMPTY_FOLDER_ARRAY;
        }
        if (nid == 8) {
            this.readSubStreamsInfo(header, archive);
            nid = getUnsignedByte(header);
        }
    }
    
    private void sanityCheckPackInfo(final ByteBuffer header, final ArchiveStatistics stats) throws IOException {
        final long packPos = readUint64(header);
        if (packPos < 0L || 32L + packPos > this.channel.size() || 32L + packPos < 0L) {
            throw new IOException("packPos (" + packPos + ") is out of range");
        }
        final long numPackStreams = readUint64(header);
        stats.numberOfPackedStreams = assertFitsIntoNonNegativeInt("numPackStreams", numPackStreams);
        int nid = getUnsignedByte(header);
        if (nid == 9) {
            long totalPackSizes = 0L;
            for (int i = 0; i < stats.numberOfPackedStreams; ++i) {
                final long packSize = readUint64(header);
                totalPackSizes += packSize;
                final long endOfPackStreams = 32L + packPos + totalPackSizes;
                if (packSize < 0L || endOfPackStreams > this.channel.size() || endOfPackStreams < packPos) {
                    throw new IOException("packSize (" + packSize + ") is out of range");
                }
            }
            nid = getUnsignedByte(header);
        }
        if (nid == 10) {
            final int crcsDefined = this.readAllOrBits(header, stats.numberOfPackedStreams).cardinality();
            if (skipBytesFully(header, 4 * crcsDefined) < 4 * crcsDefined) {
                throw new IOException("invalid number of CRCs in PackInfo");
            }
            nid = getUnsignedByte(header);
        }
        if (nid != 0) {
            throw new IOException("Badly terminated PackInfo (" + nid + ")");
        }
    }
    
    private void readPackInfo(final ByteBuffer header, final Archive archive) throws IOException {
        archive.packPos = readUint64(header);
        final int numPackStreamsInt = (int)readUint64(header);
        int nid = getUnsignedByte(header);
        if (nid == 9) {
            archive.packSizes = new long[numPackStreamsInt];
            for (int i = 0; i < archive.packSizes.length; ++i) {
                archive.packSizes[i] = readUint64(header);
            }
            nid = getUnsignedByte(header);
        }
        if (nid == 10) {
            archive.packCrcsDefined = this.readAllOrBits(header, numPackStreamsInt);
            archive.packCrcs = new long[numPackStreamsInt];
            for (int i = 0; i < numPackStreamsInt; ++i) {
                if (archive.packCrcsDefined.get(i)) {
                    archive.packCrcs[i] = (0xFFFFFFFFL & (long)getInt(header));
                }
            }
            nid = getUnsignedByte(header);
        }
    }
    
    private void sanityCheckUnpackInfo(final ByteBuffer header, final ArchiveStatistics stats) throws IOException {
        int nid = getUnsignedByte(header);
        if (nid != 11) {
            throw new IOException("Expected kFolder, got " + nid);
        }
        final long numFolders = readUint64(header);
        stats.numberOfFolders = assertFitsIntoNonNegativeInt("numFolders", numFolders);
        final int external = getUnsignedByte(header);
        if (external != 0) {
            throw new IOException("External unsupported");
        }
        final List<Integer> numberOfOutputStreamsPerFolder = new LinkedList<Integer>();
        for (int i = 0; i < stats.numberOfFolders; ++i) {
            numberOfOutputStreamsPerFolder.add(this.sanityCheckFolder(header, stats));
        }
        final long totalNumberOfBindPairs = stats.numberOfOutStreams - stats.numberOfFolders;
        final long packedStreamsRequiredByFolders = stats.numberOfInStreams - totalNumberOfBindPairs;
        if (packedStreamsRequiredByFolders < stats.numberOfPackedStreams) {
            throw new IOException("archive doesn't contain enough packed streams");
        }
        nid = getUnsignedByte(header);
        if (nid != 12) {
            throw new IOException("Expected kCodersUnpackSize, got " + nid);
        }
        for (final int numberOfOutputStreams : numberOfOutputStreamsPerFolder) {
            for (int j = 0; j < numberOfOutputStreams; ++j) {
                final long unpackSize = readUint64(header);
                if (unpackSize < 0L) {
                    throw new IllegalArgumentException("negative unpackSize");
                }
            }
        }
        nid = getUnsignedByte(header);
        if (nid == 10) {
            stats.folderHasCrc = this.readAllOrBits(header, stats.numberOfFolders);
            final int crcsDefined = stats.folderHasCrc.cardinality();
            if (skipBytesFully(header, 4 * crcsDefined) < 4 * crcsDefined) {
                throw new IOException("invalid number of CRCs in UnpackInfo");
            }
            nid = getUnsignedByte(header);
        }
        if (nid != 0) {
            throw new IOException("Badly terminated UnpackInfo");
        }
    }
    
    private void readUnpackInfo(final ByteBuffer header, final Archive archive) throws IOException {
        int nid = getUnsignedByte(header);
        final int numFoldersInt = (int)readUint64(header);
        final Folder[] folders = new Folder[numFoldersInt];
        archive.folders = folders;
        getUnsignedByte(header);
        for (int i = 0; i < numFoldersInt; ++i) {
            folders[i] = this.readFolder(header);
        }
        nid = getUnsignedByte(header);
        for (final Folder folder : folders) {
            assertFitsIntoNonNegativeInt("totalOutputStreams", folder.totalOutputStreams);
            folder.unpackSizes = new long[(int)folder.totalOutputStreams];
            for (int j = 0; j < folder.totalOutputStreams; ++j) {
                folder.unpackSizes[j] = readUint64(header);
            }
        }
        nid = getUnsignedByte(header);
        if (nid == 10) {
            final BitSet crcsDefined = this.readAllOrBits(header, numFoldersInt);
            for (int k = 0; k < numFoldersInt; ++k) {
                if (crcsDefined.get(k)) {
                    folders[k].hasCrc = true;
                    folders[k].crc = (0xFFFFFFFFL & (long)getInt(header));
                }
                else {
                    folders[k].hasCrc = false;
                }
            }
            nid = getUnsignedByte(header);
        }
    }
    
    private void sanityCheckSubStreamsInfo(final ByteBuffer header, final ArchiveStatistics stats) throws IOException {
        int nid = getUnsignedByte(header);
        final List<Integer> numUnpackSubStreamsPerFolder = new LinkedList<Integer>();
        if (nid == 13) {
            for (int i = 0; i < stats.numberOfFolders; ++i) {
                numUnpackSubStreamsPerFolder.add(assertFitsIntoNonNegativeInt("numStreams", readUint64(header)));
            }
            stats.numberOfUnpackSubStreams = numUnpackSubStreamsPerFolder.stream().collect(Collectors.summingLong(Integer::longValue));
            nid = getUnsignedByte(header);
        }
        else {
            stats.numberOfUnpackSubStreams = stats.numberOfFolders;
        }
        assertFitsIntoNonNegativeInt("totalUnpackStreams", stats.numberOfUnpackSubStreams);
        if (nid == 9) {
            for (final int numUnpackSubStreams : numUnpackSubStreamsPerFolder) {
                if (numUnpackSubStreams == 0) {
                    continue;
                }
                for (int j = 0; j < numUnpackSubStreams - 1; ++j) {
                    final long size = readUint64(header);
                    if (size < 0L) {
                        throw new IOException("negative unpackSize");
                    }
                }
            }
            nid = getUnsignedByte(header);
        }
        int numDigests = 0;
        if (numUnpackSubStreamsPerFolder.isEmpty()) {
            numDigests = ((stats.folderHasCrc == null) ? stats.numberOfFolders : (stats.numberOfFolders - stats.folderHasCrc.cardinality()));
        }
        else {
            int folderIdx = 0;
            for (final int numUnpackSubStreams2 : numUnpackSubStreamsPerFolder) {
                if (numUnpackSubStreams2 != 1 || stats.folderHasCrc == null || !stats.folderHasCrc.get(folderIdx++)) {
                    numDigests += numUnpackSubStreams2;
                }
            }
        }
        if (nid == 10) {
            assertFitsIntoNonNegativeInt("numDigests", numDigests);
            final int missingCrcs = this.readAllOrBits(header, numDigests).cardinality();
            if (skipBytesFully(header, 4 * missingCrcs) < 4 * missingCrcs) {
                throw new IOException("invalid number of missing CRCs in SubStreamInfo");
            }
            nid = getUnsignedByte(header);
        }
        if (nid != 0) {
            throw new IOException("Badly terminated SubStreamsInfo");
        }
    }
    
    private void readSubStreamsInfo(final ByteBuffer header, final Archive archive) throws IOException {
        for (final Folder folder : archive.folders) {
            folder.numUnpackSubStreams = 1;
        }
        long unpackStreamsCount = archive.folders.length;
        int nid = getUnsignedByte(header);
        if (nid == 13) {
            unpackStreamsCount = 0L;
            for (final Folder folder2 : archive.folders) {
                final long numStreams = readUint64(header);
                folder2.numUnpackSubStreams = (int)numStreams;
                unpackStreamsCount += numStreams;
            }
            nid = getUnsignedByte(header);
        }
        final int totalUnpackStreams = (int)unpackStreamsCount;
        final SubStreamsInfo subStreamsInfo = new SubStreamsInfo();
        subStreamsInfo.unpackSizes = new long[totalUnpackStreams];
        subStreamsInfo.hasCrc = new BitSet(totalUnpackStreams);
        subStreamsInfo.crcs = new long[totalUnpackStreams];
        int nextUnpackStream = 0;
        for (final Folder folder3 : archive.folders) {
            if (folder3.numUnpackSubStreams != 0) {
                long sum = 0L;
                if (nid == 9) {
                    for (int i = 0; i < folder3.numUnpackSubStreams - 1; ++i) {
                        final long size = readUint64(header);
                        subStreamsInfo.unpackSizes[nextUnpackStream++] = size;
                        sum += size;
                    }
                }
                if (sum > folder3.getUnpackSize()) {
                    throw new IOException("sum of unpack sizes of folder exceeds total unpack size");
                }
                subStreamsInfo.unpackSizes[nextUnpackStream++] = folder3.getUnpackSize() - sum;
            }
        }
        if (nid == 9) {
            nid = getUnsignedByte(header);
        }
        int numDigests = 0;
        for (final Folder folder4 : archive.folders) {
            if (folder4.numUnpackSubStreams != 1 || !folder4.hasCrc) {
                numDigests += folder4.numUnpackSubStreams;
            }
        }
        if (nid == 10) {
            final BitSet hasMissingCrc = this.readAllOrBits(header, numDigests);
            final long[] missingCrcs = new long[numDigests];
            for (int j = 0; j < numDigests; ++j) {
                if (hasMissingCrc.get(j)) {
                    missingCrcs[j] = (0xFFFFFFFFL & (long)getInt(header));
                }
            }
            int nextCrc = 0;
            int nextMissingCrc = 0;
            for (final Folder folder5 : archive.folders) {
                if (folder5.numUnpackSubStreams == 1 && folder5.hasCrc) {
                    subStreamsInfo.hasCrc.set(nextCrc, true);
                    subStreamsInfo.crcs[nextCrc] = folder5.crc;
                    ++nextCrc;
                }
                else {
                    for (int k = 0; k < folder5.numUnpackSubStreams; ++k) {
                        subStreamsInfo.hasCrc.set(nextCrc, hasMissingCrc.get(nextMissingCrc));
                        subStreamsInfo.crcs[nextCrc] = missingCrcs[nextMissingCrc];
                        ++nextCrc;
                        ++nextMissingCrc;
                    }
                }
            }
            nid = getUnsignedByte(header);
        }
        archive.subStreamsInfo = subStreamsInfo;
    }
    
    private int sanityCheckFolder(final ByteBuffer header, final ArchiveStatistics stats) throws IOException {
        final int numCoders = assertFitsIntoNonNegativeInt("numCoders", readUint64(header));
        if (numCoders == 0) {
            throw new IOException("Folder without coders");
        }
        stats.numberOfCoders += numCoders;
        long totalOutStreams = 0L;
        long totalInStreams = 0L;
        for (int i = 0; i < numCoders; ++i) {
            final int bits = getUnsignedByte(header);
            final int idSize = bits & 0xF;
            get(header, new byte[idSize]);
            final boolean isSimple = (bits & 0x10) == 0x0;
            final boolean hasAttributes = (bits & 0x20) != 0x0;
            final boolean moreAlternativeMethods = (bits & 0x80) != 0x0;
            if (moreAlternativeMethods) {
                throw new IOException("Alternative methods are unsupported, please report. The reference implementation doesn't support them either.");
            }
            if (isSimple) {
                ++totalInStreams;
                ++totalOutStreams;
            }
            else {
                totalInStreams += assertFitsIntoNonNegativeInt("numInStreams", readUint64(header));
                totalOutStreams += assertFitsIntoNonNegativeInt("numOutStreams", readUint64(header));
            }
            if (hasAttributes) {
                final int propertiesSize = assertFitsIntoNonNegativeInt("propertiesSize", readUint64(header));
                if (skipBytesFully(header, propertiesSize) < propertiesSize) {
                    throw new IOException("invalid propertiesSize in folder");
                }
            }
        }
        assertFitsIntoNonNegativeInt("totalInStreams", totalInStreams);
        assertFitsIntoNonNegativeInt("totalOutStreams", totalOutStreams);
        stats.numberOfOutStreams += totalOutStreams;
        stats.numberOfInStreams += totalInStreams;
        if (totalOutStreams == 0L) {
            throw new IOException("Total output streams can't be 0");
        }
        final int numBindPairs = assertFitsIntoNonNegativeInt("numBindPairs", totalOutStreams - 1L);
        if (totalInStreams < numBindPairs) {
            throw new IOException("Total input streams can't be less than the number of bind pairs");
        }
        final BitSet inStreamsBound = new BitSet((int)totalInStreams);
        for (int j = 0; j < numBindPairs; ++j) {
            final int inIndex = assertFitsIntoNonNegativeInt("inIndex", readUint64(header));
            if (totalInStreams <= inIndex) {
                throw new IOException("inIndex is bigger than number of inStreams");
            }
            inStreamsBound.set(inIndex);
            final int outIndex = assertFitsIntoNonNegativeInt("outIndex", readUint64(header));
            if (totalOutStreams <= outIndex) {
                throw new IOException("outIndex is bigger than number of outStreams");
            }
        }
        final int numPackedStreams = assertFitsIntoNonNegativeInt("numPackedStreams", totalInStreams - numBindPairs);
        if (numPackedStreams == 1) {
            if (inStreamsBound.nextClearBit(0) == -1) {
                throw new IOException("Couldn't find stream's bind pair index");
            }
        }
        else {
            for (int k = 0; k < numPackedStreams; ++k) {
                final int packedStreamIndex = assertFitsIntoNonNegativeInt("packedStreamIndex", readUint64(header));
                if (packedStreamIndex >= totalInStreams) {
                    throw new IOException("packedStreamIndex is bigger than number of totalInStreams");
                }
            }
        }
        return (int)totalOutStreams;
    }
    
    private Folder readFolder(final ByteBuffer header) throws IOException {
        final Folder folder = new Folder();
        final long numCoders = readUint64(header);
        final Coder[] coders = new Coder[(int)numCoders];
        long totalInStreams = 0L;
        long totalOutStreams = 0L;
        for (int i = 0; i < coders.length; ++i) {
            coders[i] = new Coder();
            final int bits = getUnsignedByte(header);
            final int idSize = bits & 0xF;
            final boolean isSimple = (bits & 0x10) == 0x0;
            final boolean hasAttributes = (bits & 0x20) != 0x0;
            final boolean moreAlternativeMethods = (bits & 0x80) != 0x0;
            get(header, coders[i].decompressionMethodId = new byte[idSize]);
            if (isSimple) {
                coders[i].numInStreams = 1L;
                coders[i].numOutStreams = 1L;
            }
            else {
                coders[i].numInStreams = readUint64(header);
                coders[i].numOutStreams = readUint64(header);
            }
            totalInStreams += coders[i].numInStreams;
            totalOutStreams += coders[i].numOutStreams;
            if (hasAttributes) {
                final long propertiesSize = readUint64(header);
                get(header, coders[i].properties = new byte[(int)propertiesSize]);
            }
            if (moreAlternativeMethods) {
                throw new IOException("Alternative methods are unsupported, please report. The reference implementation doesn't support them either.");
            }
        }
        folder.coders = coders;
        folder.totalInputStreams = totalInStreams;
        folder.totalOutputStreams = totalOutStreams;
        final long numBindPairs = totalOutStreams - 1L;
        final BindPair[] bindPairs = new BindPair[(int)numBindPairs];
        for (int j = 0; j < bindPairs.length; ++j) {
            bindPairs[j] = new BindPair();
            bindPairs[j].inIndex = readUint64(header);
            bindPairs[j].outIndex = readUint64(header);
        }
        folder.bindPairs = bindPairs;
        final long numPackedStreams = totalInStreams - numBindPairs;
        final long[] packedStreams = new long[(int)numPackedStreams];
        if (numPackedStreams == 1L) {
            int k;
            for (k = 0; k < (int)totalInStreams && folder.findBindPairForInStream(k) >= 0; ++k) {}
            packedStreams[0] = k;
        }
        else {
            for (int k = 0; k < (int)numPackedStreams; ++k) {
                packedStreams[k] = readUint64(header);
            }
        }
        folder.packedStreams = packedStreams;
        return folder;
    }
    
    private BitSet readAllOrBits(final ByteBuffer header, final int size) throws IOException {
        final int areAllDefined = getUnsignedByte(header);
        BitSet bits;
        if (areAllDefined != 0) {
            bits = new BitSet(size);
            for (int i = 0; i < size; ++i) {
                bits.set(i, true);
            }
        }
        else {
            bits = this.readBits(header, size);
        }
        return bits;
    }
    
    private BitSet readBits(final ByteBuffer header, final int size) throws IOException {
        final BitSet bits = new BitSet(size);
        int mask = 0;
        int cache = 0;
        for (int i = 0; i < size; ++i) {
            if (mask == 0) {
                mask = 128;
                cache = getUnsignedByte(header);
            }
            bits.set(i, (cache & mask) != 0x0);
            mask >>>= 1;
        }
        return bits;
    }
    
    private void sanityCheckFilesInfo(final ByteBuffer header, final ArchiveStatistics stats) throws IOException {
        stats.numberOfEntries = assertFitsIntoNonNegativeInt("numFiles", readUint64(header));
        int emptyStreams = -1;
        while (true) {
            final int propertyType = getUnsignedByte(header);
            if (propertyType == 0) {
                stats.numberOfEntriesWithStream = stats.numberOfEntries - ((emptyStreams > 0) ? emptyStreams : 0);
                return;
            }
            final long size = readUint64(header);
            switch (propertyType) {
                case 14: {
                    emptyStreams = this.readBits(header, stats.numberOfEntries).cardinality();
                    continue;
                }
                case 15: {
                    if (emptyStreams == -1) {
                        throw new IOException("Header format error: kEmptyStream must appear before kEmptyFile");
                    }
                    this.readBits(header, emptyStreams);
                    continue;
                }
                case 16: {
                    if (emptyStreams == -1) {
                        throw new IOException("Header format error: kEmptyStream must appear before kAnti");
                    }
                    this.readBits(header, emptyStreams);
                    continue;
                }
                case 17: {
                    final int external = getUnsignedByte(header);
                    if (external != 0) {
                        throw new IOException("Not implemented");
                    }
                    final int namesLength = assertFitsIntoNonNegativeInt("file names length", size - 1L);
                    if ((namesLength & 0x1) != 0x0) {
                        throw new IOException("File names length invalid");
                    }
                    int filesSeen = 0;
                    for (int i = 0; i < namesLength; i += 2) {
                        final char c = getChar(header);
                        if (c == '\0') {
                            ++filesSeen;
                        }
                    }
                    if (filesSeen != stats.numberOfEntries) {
                        throw new IOException("Invalid number of file names (" + filesSeen + " instead of " + stats.numberOfEntries + ")");
                    }
                    continue;
                }
                case 18: {
                    final int timesDefined = this.readAllOrBits(header, stats.numberOfEntries).cardinality();
                    final int external2 = getUnsignedByte(header);
                    if (external2 != 0) {
                        throw new IOException("Not implemented");
                    }
                    if (skipBytesFully(header, 8 * timesDefined) < 8 * timesDefined) {
                        throw new IOException("invalid creation dates size");
                    }
                    continue;
                }
                case 19: {
                    final int timesDefined = this.readAllOrBits(header, stats.numberOfEntries).cardinality();
                    final int external2 = getUnsignedByte(header);
                    if (external2 != 0) {
                        throw new IOException("Not implemented");
                    }
                    if (skipBytesFully(header, 8 * timesDefined) < 8 * timesDefined) {
                        throw new IOException("invalid access dates size");
                    }
                    continue;
                }
                case 20: {
                    final int timesDefined = this.readAllOrBits(header, stats.numberOfEntries).cardinality();
                    final int external2 = getUnsignedByte(header);
                    if (external2 != 0) {
                        throw new IOException("Not implemented");
                    }
                    if (skipBytesFully(header, 8 * timesDefined) < 8 * timesDefined) {
                        throw new IOException("invalid modification dates size");
                    }
                    continue;
                }
                case 21: {
                    final int attributesDefined = this.readAllOrBits(header, stats.numberOfEntries).cardinality();
                    final int external2 = getUnsignedByte(header);
                    if (external2 != 0) {
                        throw new IOException("Not implemented");
                    }
                    if (skipBytesFully(header, 4 * attributesDefined) < 4 * attributesDefined) {
                        throw new IOException("invalid windows attributes size");
                    }
                    continue;
                }
                case 24: {
                    throw new IOException("kStartPos is unsupported, please report");
                }
                case 25: {
                    if (skipBytesFully(header, size) < size) {
                        throw new IOException("Incomplete kDummy property");
                    }
                    continue;
                }
                default: {
                    if (skipBytesFully(header, size) < size) {
                        throw new IOException("Incomplete property of type " + propertyType);
                    }
                    continue;
                }
            }
        }
    }
    
    private void readFilesInfo(final ByteBuffer header, final Archive archive) throws IOException {
        final int numFilesInt = (int)readUint64(header);
        final Map<Integer, SevenZArchiveEntry> fileMap = new HashMap<Integer, SevenZArchiveEntry>();
        BitSet isEmptyStream = null;
        BitSet isEmptyFile = null;
        BitSet isAnti = null;
        while (true) {
            final int propertyType = getUnsignedByte(header);
            if (propertyType == 0) {
                int nonEmptyFileCounter = 0;
                int emptyFileCounter = 0;
                for (int i = 0; i < numFilesInt; ++i) {
                    final SevenZArchiveEntry entryAtIndex = fileMap.get(i);
                    if (entryAtIndex != null) {
                        entryAtIndex.setHasStream(isEmptyStream == null || !isEmptyStream.get(i));
                        if (entryAtIndex.hasStream()) {
                            if (archive.subStreamsInfo == null) {
                                throw new IOException("Archive contains file with streams but no subStreamsInfo");
                            }
                            entryAtIndex.setDirectory(false);
                            entryAtIndex.setAntiItem(false);
                            entryAtIndex.setHasCrc(archive.subStreamsInfo.hasCrc.get(nonEmptyFileCounter));
                            entryAtIndex.setCrcValue(archive.subStreamsInfo.crcs[nonEmptyFileCounter]);
                            entryAtIndex.setSize(archive.subStreamsInfo.unpackSizes[nonEmptyFileCounter]);
                            if (entryAtIndex.getSize() < 0L) {
                                throw new IOException("broken archive, entry with negative size");
                            }
                            ++nonEmptyFileCounter;
                        }
                        else {
                            entryAtIndex.setDirectory(isEmptyFile == null || !isEmptyFile.get(emptyFileCounter));
                            entryAtIndex.setAntiItem(isAnti != null && isAnti.get(emptyFileCounter));
                            entryAtIndex.setHasCrc(false);
                            entryAtIndex.setSize(0L);
                            ++emptyFileCounter;
                        }
                    }
                }
                final List<SevenZArchiveEntry> entries = new ArrayList<SevenZArchiveEntry>();
                for (final SevenZArchiveEntry e : fileMap.values()) {
                    if (e != null) {
                        entries.add(e);
                    }
                }
                archive.files = entries.toArray(SevenZArchiveEntry.EMPTY_SEVEN_Z_ARCHIVE_ENTRY_ARRAY);
                this.calculateStreamMap(archive);
                return;
            }
            final long size = readUint64(header);
            switch (propertyType) {
                case 14: {
                    isEmptyStream = this.readBits(header, numFilesInt);
                    continue;
                }
                case 15: {
                    isEmptyFile = this.readBits(header, isEmptyStream.cardinality());
                    continue;
                }
                case 16: {
                    isAnti = this.readBits(header, isEmptyStream.cardinality());
                    continue;
                }
                case 17: {
                    getUnsignedByte(header);
                    final byte[] names = new byte[(int)(size - 1L)];
                    final int namesLength = names.length;
                    get(header, names);
                    int nextFile = 0;
                    int nextName = 0;
                    for (int j = 0; j < namesLength; j += 2) {
                        if (names[j] == 0 && names[j + 1] == 0) {
                            this.checkEntryIsInitialized(fileMap, nextFile);
                            fileMap.get(nextFile).setName(new String(names, nextName, j - nextName, StandardCharsets.UTF_16LE));
                            nextName = j + 2;
                            ++nextFile;
                        }
                    }
                    if (nextName != namesLength || nextFile != numFilesInt) {
                        throw new IOException("Error parsing file names");
                    }
                    continue;
                }
                case 18: {
                    final BitSet timesDefined = this.readAllOrBits(header, numFilesInt);
                    getUnsignedByte(header);
                    for (int k = 0; k < numFilesInt; ++k) {
                        this.checkEntryIsInitialized(fileMap, k);
                        final SevenZArchiveEntry entryAtIndex2 = fileMap.get(k);
                        entryAtIndex2.setHasCreationDate(timesDefined.get(k));
                        if (entryAtIndex2.getHasCreationDate()) {
                            entryAtIndex2.setCreationDate(getLong(header));
                        }
                    }
                    continue;
                }
                case 19: {
                    final BitSet timesDefined = this.readAllOrBits(header, numFilesInt);
                    getUnsignedByte(header);
                    for (int k = 0; k < numFilesInt; ++k) {
                        this.checkEntryIsInitialized(fileMap, k);
                        final SevenZArchiveEntry entryAtIndex2 = fileMap.get(k);
                        entryAtIndex2.setHasAccessDate(timesDefined.get(k));
                        if (entryAtIndex2.getHasAccessDate()) {
                            entryAtIndex2.setAccessDate(getLong(header));
                        }
                    }
                    continue;
                }
                case 20: {
                    final BitSet timesDefined = this.readAllOrBits(header, numFilesInt);
                    getUnsignedByte(header);
                    for (int k = 0; k < numFilesInt; ++k) {
                        this.checkEntryIsInitialized(fileMap, k);
                        final SevenZArchiveEntry entryAtIndex2 = fileMap.get(k);
                        entryAtIndex2.setHasLastModifiedDate(timesDefined.get(k));
                        if (entryAtIndex2.getHasLastModifiedDate()) {
                            entryAtIndex2.setLastModifiedDate(getLong(header));
                        }
                    }
                    continue;
                }
                case 21: {
                    final BitSet attributesDefined = this.readAllOrBits(header, numFilesInt);
                    getUnsignedByte(header);
                    for (int k = 0; k < numFilesInt; ++k) {
                        this.checkEntryIsInitialized(fileMap, k);
                        final SevenZArchiveEntry entryAtIndex2 = fileMap.get(k);
                        entryAtIndex2.setHasWindowsAttributes(attributesDefined.get(k));
                        if (entryAtIndex2.getHasWindowsAttributes()) {
                            entryAtIndex2.setWindowsAttributes(getInt(header));
                        }
                    }
                    continue;
                }
                case 25: {
                    skipBytesFully(header, size);
                    continue;
                }
                default: {
                    skipBytesFully(header, size);
                    continue;
                }
            }
        }
    }
    
    private void checkEntryIsInitialized(final Map<Integer, SevenZArchiveEntry> archiveEntries, final int index) {
        if (archiveEntries.get(index) == null) {
            archiveEntries.put(index, new SevenZArchiveEntry());
        }
    }
    
    private void calculateStreamMap(final Archive archive) throws IOException {
        final StreamMap streamMap = new StreamMap();
        int nextFolderPackStreamIndex = 0;
        final int numFolders = (archive.folders != null) ? archive.folders.length : 0;
        streamMap.folderFirstPackStreamIndex = new int[numFolders];
        for (int i = 0; i < numFolders; ++i) {
            streamMap.folderFirstPackStreamIndex[i] = nextFolderPackStreamIndex;
            nextFolderPackStreamIndex += archive.folders[i].packedStreams.length;
        }
        long nextPackStreamOffset = 0L;
        final int numPackSizes = archive.packSizes.length;
        streamMap.packStreamOffsets = new long[numPackSizes];
        for (int j = 0; j < numPackSizes; ++j) {
            streamMap.packStreamOffsets[j] = nextPackStreamOffset;
            nextPackStreamOffset += archive.packSizes[j];
        }
        streamMap.folderFirstFileIndex = new int[numFolders];
        streamMap.fileFolderIndex = new int[archive.files.length];
        int nextFolderIndex = 0;
        int nextFolderUnpackStreamIndex = 0;
        for (int k = 0; k < archive.files.length; ++k) {
            if (!archive.files[k].hasStream() && nextFolderUnpackStreamIndex == 0) {
                streamMap.fileFolderIndex[k] = -1;
            }
            else {
                if (nextFolderUnpackStreamIndex == 0) {
                    while (nextFolderIndex < archive.folders.length) {
                        streamMap.folderFirstFileIndex[nextFolderIndex] = k;
                        if (archive.folders[nextFolderIndex].numUnpackSubStreams > 0) {
                            break;
                        }
                        ++nextFolderIndex;
                    }
                    if (nextFolderIndex >= archive.folders.length) {
                        throw new IOException("Too few folders in archive");
                    }
                }
                streamMap.fileFolderIndex[k] = nextFolderIndex;
                if (archive.files[k].hasStream()) {
                    if (++nextFolderUnpackStreamIndex >= archive.folders[nextFolderIndex].numUnpackSubStreams) {
                        ++nextFolderIndex;
                        nextFolderUnpackStreamIndex = 0;
                    }
                }
            }
        }
        archive.streamMap = streamMap;
    }
    
    private void buildDecodingStream(final int entryIndex, final boolean isRandomAccess) throws IOException {
        if (this.archive.streamMap == null) {
            throw new IOException("Archive doesn't contain stream information to read entries");
        }
        final int folderIndex = this.archive.streamMap.fileFolderIndex[entryIndex];
        if (folderIndex < 0) {
            this.deferredBlockStreams.clear();
            return;
        }
        final SevenZArchiveEntry file = this.archive.files[entryIndex];
        boolean isInSameFolder = false;
        if (this.currentFolderIndex == folderIndex) {
            if (entryIndex > 0) {
                file.setContentMethods(this.archive.files[entryIndex - 1].getContentMethods());
            }
            if (isRandomAccess && file.getContentMethods() == null) {
                final int folderFirstFileIndex = this.archive.streamMap.folderFirstFileIndex[folderIndex];
                final SevenZArchiveEntry folderFirstFile = this.archive.files[folderFirstFileIndex];
                file.setContentMethods(folderFirstFile.getContentMethods());
            }
            isInSameFolder = true;
        }
        else {
            this.reopenFolderInputStream(this.currentFolderIndex = folderIndex, file);
        }
        boolean haveSkippedEntries = false;
        if (isRandomAccess) {
            haveSkippedEntries = this.skipEntriesWhenNeeded(entryIndex, isInSameFolder, folderIndex);
        }
        if (isRandomAccess && this.currentEntryIndex == entryIndex && !haveSkippedEntries) {
            return;
        }
        InputStream fileStream = new BoundedInputStream(this.currentFolderInputStream, file.getSize());
        if (file.getHasCrc()) {
            fileStream = new CRC32VerifyingInputStream(fileStream, file.getSize(), file.getCrcValue());
        }
        this.deferredBlockStreams.add(fileStream);
    }
    
    private void reopenFolderInputStream(final int folderIndex, final SevenZArchiveEntry file) throws IOException {
        this.deferredBlockStreams.clear();
        if (this.currentFolderInputStream != null) {
            this.currentFolderInputStream.close();
            this.currentFolderInputStream = null;
        }
        final Folder folder = this.archive.folders[folderIndex];
        final int firstPackStreamIndex = this.archive.streamMap.folderFirstPackStreamIndex[folderIndex];
        final long folderOffset = 32L + this.archive.packPos + this.archive.streamMap.packStreamOffsets[firstPackStreamIndex];
        this.currentFolderInputStream = this.buildDecoderStack(folder, folderOffset, firstPackStreamIndex, file);
    }
    
    private boolean skipEntriesWhenNeeded(final int entryIndex, final boolean isInSameFolder, final int folderIndex) throws IOException {
        final SevenZArchiveEntry file = this.archive.files[entryIndex];
        if (this.currentEntryIndex == entryIndex && !this.hasCurrentEntryBeenRead()) {
            return false;
        }
        int filesToSkipStartIndex = this.archive.streamMap.folderFirstFileIndex[this.currentFolderIndex];
        if (isInSameFolder) {
            if (this.currentEntryIndex < entryIndex) {
                filesToSkipStartIndex = this.currentEntryIndex + 1;
            }
            else {
                this.reopenFolderInputStream(folderIndex, file);
            }
        }
        for (int i = filesToSkipStartIndex; i < entryIndex; ++i) {
            final SevenZArchiveEntry fileToSkip = this.archive.files[i];
            InputStream fileStreamToSkip = new BoundedInputStream(this.currentFolderInputStream, fileToSkip.getSize());
            if (fileToSkip.getHasCrc()) {
                fileStreamToSkip = new CRC32VerifyingInputStream(fileStreamToSkip, fileToSkip.getSize(), fileToSkip.getCrcValue());
            }
            this.deferredBlockStreams.add(fileStreamToSkip);
            fileToSkip.setContentMethods(file.getContentMethods());
        }
        return true;
    }
    
    private boolean hasCurrentEntryBeenRead() {
        boolean hasCurrentEntryBeenRead = false;
        if (!this.deferredBlockStreams.isEmpty()) {
            final InputStream currentEntryInputStream = this.deferredBlockStreams.get(this.deferredBlockStreams.size() - 1);
            if (currentEntryInputStream instanceof CRC32VerifyingInputStream) {
                hasCurrentEntryBeenRead = (((CRC32VerifyingInputStream)currentEntryInputStream).getBytesRemaining() != this.archive.files[this.currentEntryIndex].getSize());
            }
            if (currentEntryInputStream instanceof BoundedInputStream) {
                hasCurrentEntryBeenRead = (((BoundedInputStream)currentEntryInputStream).getBytesRemaining() != this.archive.files[this.currentEntryIndex].getSize());
            }
        }
        return hasCurrentEntryBeenRead;
    }
    
    private InputStream buildDecoderStack(final Folder folder, final long folderOffset, final int firstPackStreamIndex, final SevenZArchiveEntry entry) throws IOException {
        this.channel.position(folderOffset);
        InputStream inputStreamStack = new FilterInputStream(new BufferedInputStream(new BoundedSeekableByteChannelInputStream(this.channel, this.archive.packSizes[firstPackStreamIndex]))) {
            @Override
            public int read() throws IOException {
                final int r = this.in.read();
                if (r >= 0) {
                    this.count(1);
                }
                return r;
            }
            
            @Override
            public int read(final byte[] b) throws IOException {
                return this.read(b, 0, b.length);
            }
            
            @Override
            public int read(final byte[] b, final int off, final int len) throws IOException {
                if (len == 0) {
                    return 0;
                }
                final int r = this.in.read(b, off, len);
                if (r >= 0) {
                    this.count(r);
                }
                return r;
            }
            
            private void count(final int c) {
                SevenZFile.this.compressedBytesReadFromCurrentEntry += c;
            }
        };
        final LinkedList<SevenZMethodConfiguration> methods = new LinkedList<SevenZMethodConfiguration>();
        for (final Coder coder : folder.getOrderedCoders()) {
            if (coder.numInStreams != 1L || coder.numOutStreams != 1L) {
                throw new IOException("Multi input/output stream coders are not yet supported");
            }
            final SevenZMethod method = SevenZMethod.byId(coder.decompressionMethodId);
            inputStreamStack = Coders.addDecoder(this.fileName, inputStreamStack, folder.getUnpackSizeForCoder(coder), coder, this.password, this.options.getMaxMemoryLimitInKb());
            methods.addFirst(new SevenZMethodConfiguration(method, Coders.findByMethod(method).getOptionsFromCoder(coder, inputStreamStack)));
        }
        entry.setContentMethods(methods);
        if (folder.hasCrc) {
            return new CRC32VerifyingInputStream(inputStreamStack, folder.getUnpackSize(), folder.crc);
        }
        return inputStreamStack;
    }
    
    public int read() throws IOException {
        final int b = this.getCurrentStream().read();
        if (b >= 0) {
            ++this.uncompressedBytesReadFromCurrentEntry;
        }
        return b;
    }
    
    private InputStream getCurrentStream() throws IOException {
        if (this.archive.files[this.currentEntryIndex].getSize() == 0L) {
            return new ByteArrayInputStream(ByteUtils.EMPTY_BYTE_ARRAY);
        }
        if (this.deferredBlockStreams.isEmpty()) {
            throw new IllegalStateException("No current 7z entry (call getNextEntry() first).");
        }
        while (this.deferredBlockStreams.size() > 1) {
            try (final InputStream stream = this.deferredBlockStreams.remove(0)) {
                IOUtils.skip(stream, Long.MAX_VALUE);
            }
            this.compressedBytesReadFromCurrentEntry = 0L;
        }
        return this.deferredBlockStreams.get(0);
    }
    
    public InputStream getInputStream(final SevenZArchiveEntry entry) throws IOException {
        int entryIndex = -1;
        for (int i = 0; i < this.archive.files.length; ++i) {
            if (entry == this.archive.files[i]) {
                entryIndex = i;
                break;
            }
        }
        if (entryIndex < 0) {
            throw new IllegalArgumentException("Can not find " + entry.getName() + " in " + this.fileName);
        }
        this.buildDecodingStream(entryIndex, true);
        this.currentEntryIndex = entryIndex;
        this.currentFolderIndex = this.archive.streamMap.fileFolderIndex[entryIndex];
        return this.getCurrentStream();
    }
    
    public int read(final byte[] b) throws IOException {
        return this.read(b, 0, b.length);
    }
    
    public int read(final byte[] b, final int off, final int len) throws IOException {
        if (len == 0) {
            return 0;
        }
        final int cnt = this.getCurrentStream().read(b, off, len);
        if (cnt > 0) {
            this.uncompressedBytesReadFromCurrentEntry += cnt;
        }
        return cnt;
    }
    
    public InputStreamStatistics getStatisticsForCurrentEntry() {
        return new InputStreamStatistics() {
            @Override
            public long getCompressedCount() {
                return SevenZFile.this.compressedBytesReadFromCurrentEntry;
            }
            
            @Override
            public long getUncompressedCount() {
                return SevenZFile.this.uncompressedBytesReadFromCurrentEntry;
            }
        };
    }
    
    private static long readUint64(final ByteBuffer in) throws IOException {
        final long firstByte = getUnsignedByte(in);
        int mask = 128;
        long value = 0L;
        for (int i = 0; i < 8; ++i) {
            if ((firstByte & (long)mask) == 0x0L) {
                return value | (firstByte & (long)(mask - 1)) << 8 * i;
            }
            final long nextByte = getUnsignedByte(in);
            value |= nextByte << 8 * i;
            mask >>>= 1;
        }
        return value;
    }
    
    private static char getChar(final ByteBuffer buf) throws IOException {
        if (buf.remaining() < 2) {
            throw new EOFException();
        }
        return buf.getChar();
    }
    
    private static int getInt(final ByteBuffer buf) throws IOException {
        if (buf.remaining() < 4) {
            throw new EOFException();
        }
        return buf.getInt();
    }
    
    private static long getLong(final ByteBuffer buf) throws IOException {
        if (buf.remaining() < 8) {
            throw new EOFException();
        }
        return buf.getLong();
    }
    
    private static void get(final ByteBuffer buf, final byte[] to) throws IOException {
        if (buf.remaining() < to.length) {
            throw new EOFException();
        }
        buf.get(to);
    }
    
    private static int getUnsignedByte(final ByteBuffer buf) throws IOException {
        if (!buf.hasRemaining()) {
            throw new EOFException();
        }
        return buf.get() & 0xFF;
    }
    
    public static boolean matches(final byte[] signature, final int length) {
        if (length < SevenZFile.sevenZSignature.length) {
            return false;
        }
        for (int i = 0; i < SevenZFile.sevenZSignature.length; ++i) {
            if (signature[i] != SevenZFile.sevenZSignature[i]) {
                return false;
            }
        }
        return true;
    }
    
    private static long skipBytesFully(final ByteBuffer input, long bytesToSkip) throws IOException {
        if (bytesToSkip < 1L) {
            return 0L;
        }
        final int current = input.position();
        final int maxSkip = input.remaining();
        if (maxSkip < bytesToSkip) {
            bytesToSkip = maxSkip;
        }
        input.position(current + (int)bytesToSkip);
        return bytesToSkip;
    }
    
    private void readFully(final ByteBuffer buf) throws IOException {
        buf.rewind();
        IOUtils.readFully(this.channel, buf);
        buf.flip();
    }
    
    @Override
    public String toString() {
        return this.archive.toString();
    }
    
    public String getDefaultName() {
        if ("unknown archive".equals(this.fileName) || this.fileName == null) {
            return null;
        }
        final String lastSegment = new File(this.fileName).getName();
        final int dotPos = lastSegment.lastIndexOf(".");
        if (dotPos > 0) {
            return lastSegment.substring(0, dotPos);
        }
        return lastSegment + "~";
    }
    
    private static byte[] utf16Decode(final char[] chars) throws IOException {
        if (chars == null) {
            return null;
        }
        final ByteBuffer encoded = SevenZFile.PASSWORD_ENCODER.encode(CharBuffer.wrap(chars));
        if (encoded.hasArray()) {
            return encoded.array();
        }
        final byte[] e = new byte[encoded.remaining()];
        encoded.get(e);
        return e;
    }
    
    private static int assertFitsIntoNonNegativeInt(final String what, final long value) throws IOException {
        if (value > 2147483647L || value < 0L) {
            throw new IOException("Cannot handle " + what + " " + value);
        }
        return (int)value;
    }
    
    static {
        sevenZSignature = new byte[] { 55, 122, -68, -81, 39, 28 };
        PASSWORD_ENCODER = StandardCharsets.UTF_16LE.newEncoder();
    }
    
    private static class ArchiveStatistics
    {
        private int numberOfPackedStreams;
        private long numberOfCoders;
        private long numberOfOutStreams;
        private long numberOfInStreams;
        private long numberOfUnpackSubStreams;
        private int numberOfFolders;
        private BitSet folderHasCrc;
        private int numberOfEntries;
        private int numberOfEntriesWithStream;
        
        @Override
        public String toString() {
            return "Archive with " + this.numberOfEntries + " entries in " + this.numberOfFolders + " folders. Estimated size " + this.estimateSize() / 1024L + " kB.";
        }
        
        long estimateSize() {
            final long lowerBound = 16L * this.numberOfPackedStreams + this.numberOfPackedStreams / 8 + this.numberOfFolders * this.folderSize() + this.numberOfCoders * this.coderSize() + (this.numberOfOutStreams - this.numberOfFolders) * this.bindPairSize() + 8L * (this.numberOfInStreams - this.numberOfOutStreams + this.numberOfFolders) + 8L * this.numberOfOutStreams + this.numberOfEntries * this.entrySize() + this.streamMapSize();
            return 2L * lowerBound;
        }
        
        void assertValidity(final int maxMemoryLimitInKb) throws IOException {
            if (this.numberOfEntriesWithStream > 0 && this.numberOfFolders == 0) {
                throw new IOException("archive with entries but no folders");
            }
            if (this.numberOfEntriesWithStream > this.numberOfUnpackSubStreams) {
                throw new IOException("archive doesn't contain enough substreams for entries");
            }
            final long memoryNeededInKb = this.estimateSize() / 1024L;
            if (maxMemoryLimitInKb < memoryNeededInKb) {
                throw new MemoryLimitException(memoryNeededInKb, maxMemoryLimitInKb);
            }
        }
        
        private long folderSize() {
            return 30L;
        }
        
        private long coderSize() {
            return 22L;
        }
        
        private long bindPairSize() {
            return 16L;
        }
        
        private long entrySize() {
            return 100L;
        }
        
        private long streamMapSize() {
            return 8 * this.numberOfFolders + 8 * this.numberOfPackedStreams + 4 * this.numberOfEntries;
        }
    }
}
