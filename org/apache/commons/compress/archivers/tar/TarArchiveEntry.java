package org.apache.commons.compress.archivers.tar;

import org.apache.commons.compress.utils.ArchiveUtils;
import java.util.Locale;
import java.util.Collection;
import java.nio.file.DirectoryStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.Comparator;
import java.util.Collections;
import java.util.concurrent.TimeUnit;
import java.nio.file.attribute.FileTime;
import java.util.Date;
import org.apache.commons.compress.archivers.zip.ZipEncoding;
import java.util.Set;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.DosFileAttributes;
import java.nio.file.Files;
import java.nio.file.attribute.PosixFileAttributes;
import java.io.IOException;
import java.io.File;
import org.apache.commons.compress.utils.IOUtils;
import java.util.HashMap;
import java.util.Map;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.List;
import org.apache.commons.compress.archivers.EntryStreamOffsets;
import org.apache.commons.compress.archivers.ArchiveEntry;

public class TarArchiveEntry implements ArchiveEntry, TarConstants, EntryStreamOffsets
{
    private static final TarArchiveEntry[] EMPTY_TAR_ARCHIVE_ENTRY_ARRAY;
    public static final long UNKNOWN = -1L;
    private String name;
    private final boolean preserveAbsolutePath;
    private int mode;
    private long userId;
    private long groupId;
    private long size;
    private long modTime;
    private boolean checkSumOK;
    private byte linkFlag;
    private String linkName;
    private String magic;
    private String version;
    private String userName;
    private String groupName;
    private int devMajor;
    private int devMinor;
    private List<TarArchiveStructSparse> sparseHeaders;
    private boolean isExtended;
    private long realSize;
    private boolean paxGNUSparse;
    private boolean paxGNU1XSparse;
    private boolean starSparse;
    private final Path file;
    private final LinkOption[] linkOptions;
    private final Map<String, String> extraPaxHeaders;
    public static final int MAX_NAMELEN = 31;
    public static final int DEFAULT_DIR_MODE = 16877;
    public static final int DEFAULT_FILE_MODE = 33188;
    public static final int MILLIS_PER_SECOND = 1000;
    private long dataOffset;
    
    private TarArchiveEntry(final boolean preserveAbsolutePath) {
        this.name = "";
        this.linkName = "";
        this.magic = "ustar\u0000";
        this.version = "00";
        this.groupName = "";
        this.extraPaxHeaders = new HashMap<String, String>();
        this.dataOffset = -1L;
        String user = System.getProperty("user.name", "");
        if (user.length() > 31) {
            user = user.substring(0, 31);
        }
        this.userName = user;
        this.file = null;
        this.linkOptions = IOUtils.EMPTY_LINK_OPTIONS;
        this.preserveAbsolutePath = preserveAbsolutePath;
    }
    
    public TarArchiveEntry(final String name) {
        this(name, false);
    }
    
    public TarArchiveEntry(String name, final boolean preserveAbsolutePath) {
        this(preserveAbsolutePath);
        name = normalizeFileName(name, preserveAbsolutePath);
        final boolean isDir = name.endsWith("/");
        this.name = name;
        this.mode = (isDir ? 16877 : 33188);
        this.linkFlag = (byte)(isDir ? 53 : 48);
        this.modTime = System.currentTimeMillis() / 1000L;
        this.userName = "";
    }
    
    public TarArchiveEntry(final String name, final byte linkFlag) {
        this(name, linkFlag, false);
    }
    
    public TarArchiveEntry(final String name, final byte linkFlag, final boolean preserveAbsolutePath) {
        this(name, preserveAbsolutePath);
        this.linkFlag = linkFlag;
        if (linkFlag == 76) {
            this.magic = "ustar ";
            this.version = " \u0000";
        }
    }
    
    public TarArchiveEntry(final File file) {
        this(file, file.getPath());
    }
    
    public TarArchiveEntry(final Path file) throws IOException {
        this(file, file.toString(), new LinkOption[0]);
    }
    
    public TarArchiveEntry(final File file, final String fileName) {
        this.name = "";
        this.linkName = "";
        this.magic = "ustar\u0000";
        this.version = "00";
        this.groupName = "";
        this.extraPaxHeaders = new HashMap<String, String>();
        this.dataOffset = -1L;
        final String normalizedName = normalizeFileName(fileName, false);
        this.file = file.toPath();
        this.linkOptions = IOUtils.EMPTY_LINK_OPTIONS;
        try {
            this.readFileMode(this.file, normalizedName, new LinkOption[0]);
        }
        catch (final IOException e) {
            if (!file.isDirectory()) {
                this.size = file.length();
            }
        }
        this.userName = "";
        try {
            this.readOsSpecificProperties(this.file, new LinkOption[0]);
        }
        catch (final IOException e) {
            this.modTime = file.lastModified() / 1000L;
        }
        this.preserveAbsolutePath = false;
    }
    
    public TarArchiveEntry(final Path file, final String fileName, final LinkOption... linkOptions) throws IOException {
        this.name = "";
        this.linkName = "";
        this.magic = "ustar\u0000";
        this.version = "00";
        this.groupName = "";
        this.extraPaxHeaders = new HashMap<String, String>();
        this.dataOffset = -1L;
        final String normalizedName = normalizeFileName(fileName, false);
        this.file = file;
        this.linkOptions = ((linkOptions == null) ? IOUtils.EMPTY_LINK_OPTIONS : linkOptions);
        this.readFileMode(file, normalizedName, linkOptions);
        this.userName = "";
        this.readOsSpecificProperties(file, new LinkOption[0]);
        this.preserveAbsolutePath = false;
    }
    
    private void readOsSpecificProperties(final Path file, final LinkOption... options) throws IOException {
        final Set<String> availableAttributeViews = file.getFileSystem().supportedFileAttributeViews();
        if (availableAttributeViews.contains("posix")) {
            final PosixFileAttributes posixFileAttributes = Files.readAttributes(file, PosixFileAttributes.class, options);
            this.setModTime(posixFileAttributes.lastModifiedTime());
            this.userName = posixFileAttributes.owner().getName();
            this.groupName = posixFileAttributes.group().getName();
            if (availableAttributeViews.contains("unix")) {
                this.userId = ((Number)Files.getAttribute(file, "unix:uid", options)).longValue();
                this.groupId = ((Number)Files.getAttribute(file, "unix:gid", options)).longValue();
            }
        }
        else if (availableAttributeViews.contains("dos")) {
            final DosFileAttributes dosFileAttributes = Files.readAttributes(file, DosFileAttributes.class, options);
            this.setModTime(dosFileAttributes.lastModifiedTime());
            this.userName = Files.getOwner(file, options).getName();
        }
        else {
            final BasicFileAttributes basicFileAttributes = Files.readAttributes(file, BasicFileAttributes.class, options);
            this.setModTime(basicFileAttributes.lastModifiedTime());
            this.userName = Files.getOwner(file, options).getName();
        }
    }
    
    private void readFileMode(final Path file, final String normalizedName, final LinkOption... options) throws IOException {
        if (Files.isDirectory(file, options)) {
            this.mode = 16877;
            this.linkFlag = 53;
            final int nameLength = normalizedName.length();
            if (nameLength == 0 || normalizedName.charAt(nameLength - 1) != '/') {
                this.name = normalizedName + "/";
            }
            else {
                this.name = normalizedName;
            }
        }
        else {
            this.mode = 33188;
            this.linkFlag = 48;
            this.name = normalizedName;
            this.size = Files.size(file);
        }
    }
    
    public TarArchiveEntry(final byte[] headerBuf) {
        this(false);
        this.parseTarHeader(headerBuf);
    }
    
    public TarArchiveEntry(final byte[] headerBuf, final ZipEncoding encoding) throws IOException {
        this(headerBuf, encoding, false);
    }
    
    public TarArchiveEntry(final byte[] headerBuf, final ZipEncoding encoding, final boolean lenient) throws IOException {
        this(false);
        this.parseTarHeader(headerBuf, encoding, false, lenient);
    }
    
    public TarArchiveEntry(final byte[] headerBuf, final ZipEncoding encoding, final boolean lenient, final long dataOffset) throws IOException {
        this(headerBuf, encoding, lenient);
        this.setDataOffset(dataOffset);
    }
    
    public boolean equals(final TarArchiveEntry it) {
        return it != null && this.getName().equals(it.getName());
    }
    
    @Override
    public boolean equals(final Object it) {
        return it != null && this.getClass() == it.getClass() && this.equals((TarArchiveEntry)it);
    }
    
    @Override
    public int hashCode() {
        return this.getName().hashCode();
    }
    
    public boolean isDescendent(final TarArchiveEntry desc) {
        return desc.getName().startsWith(this.getName());
    }
    
    @Override
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = normalizeFileName(name, this.preserveAbsolutePath);
    }
    
    public void setMode(final int mode) {
        this.mode = mode;
    }
    
    public String getLinkName() {
        return this.linkName;
    }
    
    public void setLinkName(final String link) {
        this.linkName = link;
    }
    
    @Deprecated
    public int getUserId() {
        return (int)(this.userId & -1L);
    }
    
    public void setUserId(final int userId) {
        this.setUserId((long)userId);
    }
    
    public long getLongUserId() {
        return this.userId;
    }
    
    public void setUserId(final long userId) {
        this.userId = userId;
    }
    
    @Deprecated
    public int getGroupId() {
        return (int)(this.groupId & -1L);
    }
    
    public void setGroupId(final int groupId) {
        this.setGroupId((long)groupId);
    }
    
    public long getLongGroupId() {
        return this.groupId;
    }
    
    public void setGroupId(final long groupId) {
        this.groupId = groupId;
    }
    
    public String getUserName() {
        return this.userName;
    }
    
    public void setUserName(final String userName) {
        this.userName = userName;
    }
    
    public String getGroupName() {
        return this.groupName;
    }
    
    public void setGroupName(final String groupName) {
        this.groupName = groupName;
    }
    
    public void setIds(final int userId, final int groupId) {
        this.setUserId(userId);
        this.setGroupId(groupId);
    }
    
    public void setNames(final String userName, final String groupName) {
        this.setUserName(userName);
        this.setGroupName(groupName);
    }
    
    public void setModTime(final long time) {
        this.modTime = time / 1000L;
    }
    
    public void setModTime(final Date time) {
        this.modTime = time.getTime() / 1000L;
    }
    
    public void setModTime(final FileTime time) {
        this.modTime = time.to(TimeUnit.SECONDS);
    }
    
    public Date getModTime() {
        return new Date(this.modTime * 1000L);
    }
    
    @Override
    public Date getLastModifiedDate() {
        return this.getModTime();
    }
    
    public boolean isCheckSumOK() {
        return this.checkSumOK;
    }
    
    public File getFile() {
        if (this.file == null) {
            return null;
        }
        return this.file.toFile();
    }
    
    public Path getPath() {
        return this.file;
    }
    
    public int getMode() {
        return this.mode;
    }
    
    @Override
    public long getSize() {
        return this.size;
    }
    
    public void setSparseHeaders(final List<TarArchiveStructSparse> sparseHeaders) {
        this.sparseHeaders = sparseHeaders;
    }
    
    public List<TarArchiveStructSparse> getSparseHeaders() {
        return this.sparseHeaders;
    }
    
    public List<TarArchiveStructSparse> getOrderedSparseHeaders() throws IOException {
        if (this.sparseHeaders == null || this.sparseHeaders.isEmpty()) {
            return Collections.emptyList();
        }
        final List<TarArchiveStructSparse> orderedAndFiltered = this.sparseHeaders.stream().filter(s -> s.getOffset() > 0L || s.getNumbytes() > 0L).sorted(Comparator.comparingLong(TarArchiveStructSparse::getOffset)).collect((Collector<? super Object, ?, List<TarArchiveStructSparse>>)Collectors.toList());
        final int numberOfHeaders = orderedAndFiltered.size();
        for (int i = 0; i < numberOfHeaders; ++i) {
            final TarArchiveStructSparse str = orderedAndFiltered.get(i);
            if (i + 1 < numberOfHeaders && str.getOffset() + str.getNumbytes() > orderedAndFiltered.get(i + 1).getOffset()) {
                throw new IOException("Corrupted TAR archive. Sparse blocks for " + this.getName() + " overlap each other.");
            }
            if (str.getOffset() + str.getNumbytes() < 0L) {
                throw new IOException("Unreadable TAR archive. Offset and numbytes for sparse block in " + this.getName() + " too large.");
            }
        }
        if (!orderedAndFiltered.isEmpty()) {
            final TarArchiveStructSparse last = orderedAndFiltered.get(numberOfHeaders - 1);
            if (last.getOffset() + last.getNumbytes() > this.getRealSize()) {
                throw new IOException("Corrupted TAR archive. Sparse block extends beyond real size of the entry");
            }
        }
        return orderedAndFiltered;
    }
    
    public boolean isPaxGNU1XSparse() {
        return this.paxGNU1XSparse;
    }
    
    public void setSize(final long size) {
        if (size < 0L) {
            throw new IllegalArgumentException("Size is out of range: " + size);
        }
        this.size = size;
    }
    
    public int getDevMajor() {
        return this.devMajor;
    }
    
    public void setDevMajor(final int devNo) {
        if (devNo < 0) {
            throw new IllegalArgumentException("Major device number is out of range: " + devNo);
        }
        this.devMajor = devNo;
    }
    
    public int getDevMinor() {
        return this.devMinor;
    }
    
    public void setDevMinor(final int devNo) {
        if (devNo < 0) {
            throw new IllegalArgumentException("Minor device number is out of range: " + devNo);
        }
        this.devMinor = devNo;
    }
    
    public boolean isExtended() {
        return this.isExtended;
    }
    
    public long getRealSize() {
        if (!this.isSparse()) {
            return this.getSize();
        }
        return this.realSize;
    }
    
    public boolean isGNUSparse() {
        return this.isOldGNUSparse() || this.isPaxGNUSparse();
    }
    
    public boolean isOldGNUSparse() {
        return this.linkFlag == 83;
    }
    
    public boolean isPaxGNUSparse() {
        return this.paxGNUSparse;
    }
    
    public boolean isStarSparse() {
        return this.starSparse;
    }
    
    public boolean isGNULongLinkEntry() {
        return this.linkFlag == 75;
    }
    
    public boolean isGNULongNameEntry() {
        return this.linkFlag == 76;
    }
    
    public boolean isPaxHeader() {
        return this.linkFlag == 120 || this.linkFlag == 88;
    }
    
    public boolean isGlobalPaxHeader() {
        return this.linkFlag == 103;
    }
    
    @Override
    public boolean isDirectory() {
        if (this.file != null) {
            return Files.isDirectory(this.file, this.linkOptions);
        }
        return this.linkFlag == 53 || (!this.isPaxHeader() && !this.isGlobalPaxHeader() && this.getName().endsWith("/"));
    }
    
    public boolean isFile() {
        if (this.file != null) {
            return Files.isRegularFile(this.file, this.linkOptions);
        }
        return this.linkFlag == 0 || this.linkFlag == 48 || !this.getName().endsWith("/");
    }
    
    public boolean isSymbolicLink() {
        return this.linkFlag == 50;
    }
    
    public boolean isLink() {
        return this.linkFlag == 49;
    }
    
    public boolean isCharacterDevice() {
        return this.linkFlag == 51;
    }
    
    public boolean isBlockDevice() {
        return this.linkFlag == 52;
    }
    
    public boolean isFIFO() {
        return this.linkFlag == 54;
    }
    
    public boolean isSparse() {
        return this.isGNUSparse() || this.isStarSparse();
    }
    
    @Override
    public long getDataOffset() {
        return this.dataOffset;
    }
    
    public void setDataOffset(final long dataOffset) {
        if (dataOffset < 0L) {
            throw new IllegalArgumentException("The offset can not be smaller than 0");
        }
        this.dataOffset = dataOffset;
    }
    
    @Override
    public boolean isStreamContiguous() {
        return true;
    }
    
    public Map<String, String> getExtraPaxHeaders() {
        return Collections.unmodifiableMap((Map<? extends String, ? extends String>)this.extraPaxHeaders);
    }
    
    public void clearExtraPaxHeaders() {
        this.extraPaxHeaders.clear();
    }
    
    public void addPaxHeader(final String name, final String value) {
        try {
            this.processPaxHeader(name, value);
        }
        catch (final IOException ex) {
            throw new IllegalArgumentException("Invalid input", ex);
        }
    }
    
    public String getExtraPaxHeader(final String name) {
        return this.extraPaxHeaders.get(name);
    }
    
    void updateEntryFromPaxHeaders(final Map<String, String> headers) throws IOException {
        for (final Map.Entry<String, String> ent : headers.entrySet()) {
            final String key = ent.getKey();
            final String val = ent.getValue();
            this.processPaxHeader(key, val, headers);
        }
    }
    
    private void processPaxHeader(final String key, final String val) throws IOException {
        this.processPaxHeader(key, val, this.extraPaxHeaders);
    }
    
    private void processPaxHeader(final String key, final String val, final Map<String, String> headers) throws IOException {
        switch (key) {
            case "path": {
                this.setName(val);
                break;
            }
            case "linkpath": {
                this.setLinkName(val);
                break;
            }
            case "gid": {
                this.setGroupId(Long.parseLong(val));
                break;
            }
            case "gname": {
                this.setGroupName(val);
                break;
            }
            case "uid": {
                this.setUserId(Long.parseLong(val));
                break;
            }
            case "uname": {
                this.setUserName(val);
                break;
            }
            case "size": {
                final long size = Long.parseLong(val);
                if (size < 0L) {
                    throw new IOException("Corrupted TAR archive. Entry size is negative");
                }
                this.setSize(size);
                break;
            }
            case "mtime": {
                this.setModTime((long)(Double.parseDouble(val) * 1000.0));
                break;
            }
            case "SCHILY.devminor": {
                final int devMinor = Integer.parseInt(val);
                if (devMinor < 0) {
                    throw new IOException("Corrupted TAR archive. Dev-Minor is negative");
                }
                this.setDevMinor(devMinor);
                break;
            }
            case "SCHILY.devmajor": {
                final int devMajor = Integer.parseInt(val);
                if (devMajor < 0) {
                    throw new IOException("Corrupted TAR archive. Dev-Major is negative");
                }
                this.setDevMajor(devMajor);
                break;
            }
            case "GNU.sparse.size": {
                this.fillGNUSparse0xData(headers);
                break;
            }
            case "GNU.sparse.realsize": {
                this.fillGNUSparse1xData(headers);
                break;
            }
            case "SCHILY.filetype": {
                if ("sparse".equals(val)) {
                    this.fillStarSparseData(headers);
                    break;
                }
                break;
            }
            default: {
                this.extraPaxHeaders.put(key, val);
                break;
            }
        }
    }
    
    public TarArchiveEntry[] getDirectoryEntries() {
        if (this.file == null || !this.isDirectory()) {
            return TarArchiveEntry.EMPTY_TAR_ARCHIVE_ENTRY_ARRAY;
        }
        final List<TarArchiveEntry> entries = new ArrayList<TarArchiveEntry>();
        try (final DirectoryStream<Path> dirStream = Files.newDirectoryStream(this.file)) {
            for (final Path p : dirStream) {
                entries.add(new TarArchiveEntry(p));
            }
        }
        catch (final IOException e) {
            return TarArchiveEntry.EMPTY_TAR_ARCHIVE_ENTRY_ARRAY;
        }
        return entries.toArray(TarArchiveEntry.EMPTY_TAR_ARCHIVE_ENTRY_ARRAY);
    }
    
    public void writeEntryHeader(final byte[] outbuf) {
        try {
            this.writeEntryHeader(outbuf, TarUtils.DEFAULT_ENCODING, false);
        }
        catch (final IOException ex) {
            try {
                this.writeEntryHeader(outbuf, TarUtils.FALLBACK_ENCODING, false);
            }
            catch (final IOException ex2) {
                throw new RuntimeException(ex2);
            }
        }
    }
    
    public void writeEntryHeader(final byte[] outbuf, final ZipEncoding encoding, final boolean starMode) throws IOException {
        int offset = 0;
        offset = TarUtils.formatNameBytes(this.name, outbuf, offset, 100, encoding);
        offset = this.writeEntryHeaderField(this.mode, outbuf, offset, 8, starMode);
        offset = this.writeEntryHeaderField(this.userId, outbuf, offset, 8, starMode);
        offset = this.writeEntryHeaderField(this.groupId, outbuf, offset, 8, starMode);
        offset = this.writeEntryHeaderField(this.size, outbuf, offset, 12, starMode);
        final int csOffset;
        offset = (csOffset = this.writeEntryHeaderField(this.modTime, outbuf, offset, 12, starMode));
        for (int c = 0; c < 8; ++c) {
            outbuf[offset++] = 32;
        }
        outbuf[offset++] = this.linkFlag;
        for (offset = TarUtils.formatNameBytes(this.linkName, outbuf, offset, 100, encoding), offset = TarUtils.formatNameBytes(this.magic, outbuf, offset, 6), offset = TarUtils.formatNameBytes(this.version, outbuf, offset, 2), offset = TarUtils.formatNameBytes(this.userName, outbuf, offset, 32, encoding), offset = TarUtils.formatNameBytes(this.groupName, outbuf, offset, 32, encoding), offset = this.writeEntryHeaderField(this.devMajor, outbuf, offset, 8, starMode), offset = this.writeEntryHeaderField(this.devMinor, outbuf, offset, 8, starMode); offset < outbuf.length; outbuf[offset++] = 0) {}
        final long chk = TarUtils.computeCheckSum(outbuf);
        TarUtils.formatCheckSumOctalBytes(chk, outbuf, csOffset, 8);
    }
    
    private int writeEntryHeaderField(final long value, final byte[] outbuf, final int offset, final int length, final boolean starMode) {
        if (!starMode && (value < 0L || value >= 1L << 3 * (length - 1))) {
            return TarUtils.formatLongOctalBytes(0L, outbuf, offset, length);
        }
        return TarUtils.formatLongOctalOrBinaryBytes(value, outbuf, offset, length);
    }
    
    public void parseTarHeader(final byte[] header) {
        try {
            this.parseTarHeader(header, TarUtils.DEFAULT_ENCODING);
        }
        catch (final IOException ex) {
            try {
                this.parseTarHeader(header, TarUtils.DEFAULT_ENCODING, true, false);
            }
            catch (final IOException ex2) {
                throw new RuntimeException(ex2);
            }
        }
    }
    
    public void parseTarHeader(final byte[] header, final ZipEncoding encoding) throws IOException {
        this.parseTarHeader(header, encoding, false, false);
    }
    
    private void parseTarHeader(final byte[] header, final ZipEncoding encoding, final boolean oldStyle, final boolean lenient) throws IOException {
        try {
            this.parseTarHeaderUnwrapped(header, encoding, oldStyle, lenient);
        }
        catch (final IllegalArgumentException ex) {
            throw new IOException("Corrupted TAR archive.", ex);
        }
    }
    
    private void parseTarHeaderUnwrapped(final byte[] header, final ZipEncoding encoding, final boolean oldStyle, final boolean lenient) throws IOException {
        int offset = 0;
        this.name = (oldStyle ? TarUtils.parseName(header, offset, 100) : TarUtils.parseName(header, offset, 100, encoding));
        offset += 100;
        this.mode = (int)this.parseOctalOrBinary(header, offset, 8, lenient);
        offset += 8;
        this.userId = (int)this.parseOctalOrBinary(header, offset, 8, lenient);
        offset += 8;
        this.groupId = (int)this.parseOctalOrBinary(header, offset, 8, lenient);
        offset += 8;
        this.size = TarUtils.parseOctalOrBinary(header, offset, 12);
        if (this.size < 0L) {
            throw new IOException("broken archive, entry with negative size");
        }
        offset += 12;
        this.modTime = this.parseOctalOrBinary(header, offset, 12, lenient);
        offset += 12;
        this.checkSumOK = TarUtils.verifyCheckSum(header);
        offset += 8;
        this.linkFlag = header[offset++];
        this.linkName = (oldStyle ? TarUtils.parseName(header, offset, 100) : TarUtils.parseName(header, offset, 100, encoding));
        offset += 100;
        this.magic = TarUtils.parseName(header, offset, 6);
        offset += 6;
        this.version = TarUtils.parseName(header, offset, 2);
        offset += 2;
        this.userName = (oldStyle ? TarUtils.parseName(header, offset, 32) : TarUtils.parseName(header, offset, 32, encoding));
        offset += 32;
        this.groupName = (oldStyle ? TarUtils.parseName(header, offset, 32) : TarUtils.parseName(header, offset, 32, encoding));
        offset += 32;
        if (this.linkFlag == 51 || this.linkFlag == 52) {
            this.devMajor = (int)this.parseOctalOrBinary(header, offset, 8, lenient);
            offset += 8;
            this.devMinor = (int)this.parseOctalOrBinary(header, offset, 8, lenient);
            offset += 8;
        }
        else {
            offset += 16;
        }
        final int type = this.evaluateType(header);
        switch (type) {
            case 2: {
                offset += 12;
                offset += 12;
                offset += 12;
                offset += 4;
                ++offset;
                this.sparseHeaders = new ArrayList<TarArchiveStructSparse>(TarUtils.readSparseStructs(header, offset, 4));
                offset += 96;
                this.isExtended = TarUtils.parseBoolean(header, offset);
                ++offset;
                this.realSize = TarUtils.parseOctal(header, offset, 12);
                offset += 12;
                break;
            }
            case 4: {
                final String xstarPrefix = oldStyle ? TarUtils.parseName(header, offset, 131) : TarUtils.parseName(header, offset, 131, encoding);
                if (!xstarPrefix.isEmpty()) {
                    this.name = xstarPrefix + "/" + this.name;
                    break;
                }
                break;
            }
            default: {
                final String prefix = oldStyle ? TarUtils.parseName(header, offset, 155) : TarUtils.parseName(header, offset, 155, encoding);
                if (this.isDirectory() && !this.name.endsWith("/")) {
                    this.name += "/";
                }
                if (!prefix.isEmpty()) {
                    this.name = prefix + "/" + this.name;
                    break;
                }
                break;
            }
        }
    }
    
    private long parseOctalOrBinary(final byte[] header, final int offset, final int length, final boolean lenient) {
        if (lenient) {
            try {
                return TarUtils.parseOctalOrBinary(header, offset, length);
            }
            catch (final IllegalArgumentException ex) {
                return -1L;
            }
        }
        return TarUtils.parseOctalOrBinary(header, offset, length);
    }
    
    private static String normalizeFileName(String fileName, final boolean preserveAbsolutePath) {
        if (!preserveAbsolutePath) {
            final String osname = System.getProperty("os.name").toLowerCase(Locale.ENGLISH);
            if (osname != null) {
                if (osname.startsWith("windows")) {
                    if (fileName.length() > 2) {
                        final char ch1 = fileName.charAt(0);
                        final char ch2 = fileName.charAt(1);
                        if (ch2 == ':' && ((ch1 >= 'a' && ch1 <= 'z') || (ch1 >= 'A' && ch1 <= 'Z'))) {
                            fileName = fileName.substring(2);
                        }
                    }
                }
                else if (osname.contains("netware")) {
                    final int colon = fileName.indexOf(58);
                    if (colon != -1) {
                        fileName = fileName.substring(colon + 1);
                    }
                }
            }
        }
        for (fileName = fileName.replace(File.separatorChar, '/'); !preserveAbsolutePath && fileName.startsWith("/"); fileName = fileName.substring(1)) {}
        return fileName;
    }
    
    private int evaluateType(final byte[] header) {
        if (ArchiveUtils.matchAsciiBuffer("ustar ", header, 257, 6)) {
            return 2;
        }
        if (!ArchiveUtils.matchAsciiBuffer("ustar\u0000", header, 257, 6)) {
            return 0;
        }
        if (ArchiveUtils.matchAsciiBuffer("tar\u0000", header, 508, 4)) {
            return 4;
        }
        return 3;
    }
    
    void fillGNUSparse0xData(final Map<String, String> headers) {
        this.paxGNUSparse = true;
        this.realSize = Integer.parseInt(headers.get("GNU.sparse.size"));
        if (headers.containsKey("GNU.sparse.name")) {
            this.name = headers.get("GNU.sparse.name");
        }
    }
    
    void fillGNUSparse1xData(final Map<String, String> headers) throws IOException {
        this.paxGNUSparse = true;
        this.paxGNU1XSparse = true;
        if (headers.containsKey("GNU.sparse.name")) {
            this.name = headers.get("GNU.sparse.name");
        }
        if (headers.containsKey("GNU.sparse.realsize")) {
            try {
                this.realSize = Integer.parseInt(headers.get("GNU.sparse.realsize"));
            }
            catch (final NumberFormatException ex) {
                throw new IOException("Corrupted TAR archive. GNU.sparse.realsize header for " + this.name + " contains non-numeric value");
            }
        }
    }
    
    void fillStarSparseData(final Map<String, String> headers) throws IOException {
        this.starSparse = true;
        if (headers.containsKey("SCHILY.realsize")) {
            try {
                this.realSize = Long.parseLong(headers.get("SCHILY.realsize"));
            }
            catch (final NumberFormatException ex) {
                throw new IOException("Corrupted TAR archive. SCHILY.realsize header for " + this.name + " contains non-numeric value");
            }
        }
    }
    
    static {
        EMPTY_TAR_ARCHIVE_ENTRY_ARRAY = new TarArchiveEntry[0];
    }
}
