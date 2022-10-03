package org.apache.commons.compress.archivers.cpio;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.nio.file.attribute.FileTime;
import java.util.Date;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.io.IOException;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.io.File;
import org.apache.commons.compress.archivers.ArchiveEntry;

public class CpioArchiveEntry implements CpioConstants, ArchiveEntry
{
    private final short fileFormat;
    private final int headerSize;
    private final int alignmentBoundary;
    private long chksum;
    private long filesize;
    private long gid;
    private long inode;
    private long maj;
    private long min;
    private long mode;
    private long mtime;
    private String name;
    private long nlink;
    private long rmaj;
    private long rmin;
    private long uid;
    
    public CpioArchiveEntry(final short format) {
        switch (format) {
            case 1: {
                this.headerSize = 110;
                this.alignmentBoundary = 4;
                break;
            }
            case 2: {
                this.headerSize = 110;
                this.alignmentBoundary = 4;
                break;
            }
            case 4: {
                this.headerSize = 76;
                this.alignmentBoundary = 0;
                break;
            }
            case 8: {
                this.headerSize = 26;
                this.alignmentBoundary = 2;
                break;
            }
            default: {
                throw new IllegalArgumentException("Unknown header type");
            }
        }
        this.fileFormat = format;
    }
    
    public CpioArchiveEntry(final String name) {
        this((short)1, name);
    }
    
    public CpioArchiveEntry(final short format, final String name) {
        this(format);
        this.name = name;
    }
    
    public CpioArchiveEntry(final String name, final long size) {
        this(name);
        this.setSize(size);
    }
    
    public CpioArchiveEntry(final short format, final String name, final long size) {
        this(format, name);
        this.setSize(size);
    }
    
    public CpioArchiveEntry(final File inputFile, final String entryName) {
        this((short)1, inputFile, entryName);
    }
    
    public CpioArchiveEntry(final Path inputPath, final String entryName, final LinkOption... options) throws IOException {
        this((short)1, inputPath, entryName, options);
    }
    
    public CpioArchiveEntry(final short format, final File inputFile, final String entryName) {
        this(format, entryName, inputFile.isFile() ? inputFile.length() : 0L);
        if (inputFile.isDirectory()) {
            this.setMode(16384L);
        }
        else {
            if (!inputFile.isFile()) {
                throw new IllegalArgumentException("Cannot determine type of file " + inputFile.getName());
            }
            this.setMode(32768L);
        }
        this.setTime(inputFile.lastModified() / 1000L);
    }
    
    public CpioArchiveEntry(final short format, final Path inputPath, final String entryName, final LinkOption... options) throws IOException {
        this(format, entryName, Files.isRegularFile(inputPath, options) ? Files.size(inputPath) : 0L);
        if (Files.isDirectory(inputPath, options)) {
            this.setMode(16384L);
        }
        else {
            if (!Files.isRegularFile(inputPath, options)) {
                throw new IllegalArgumentException("Cannot determine type of file " + inputPath);
            }
            this.setMode(32768L);
        }
        this.setTime(Files.getLastModifiedTime(inputPath, options));
    }
    
    private void checkNewFormat() {
        if ((this.fileFormat & 0x3) == 0x0) {
            throw new UnsupportedOperationException();
        }
    }
    
    private void checkOldFormat() {
        if ((this.fileFormat & 0xC) == 0x0) {
            throw new UnsupportedOperationException();
        }
    }
    
    public long getChksum() {
        this.checkNewFormat();
        return this.chksum & 0xFFFFFFFFL;
    }
    
    public long getDevice() {
        this.checkOldFormat();
        return this.min;
    }
    
    public long getDeviceMaj() {
        this.checkNewFormat();
        return this.maj;
    }
    
    public long getDeviceMin() {
        this.checkNewFormat();
        return this.min;
    }
    
    @Override
    public long getSize() {
        return this.filesize;
    }
    
    public short getFormat() {
        return this.fileFormat;
    }
    
    public long getGID() {
        return this.gid;
    }
    
    public int getHeaderSize() {
        return this.headerSize;
    }
    
    public int getAlignmentBoundary() {
        return this.alignmentBoundary;
    }
    
    @Deprecated
    public int getHeaderPadCount() {
        return this.getHeaderPadCount(null);
    }
    
    public int getHeaderPadCount(final Charset charset) {
        if (this.name == null) {
            return 0;
        }
        if (charset == null) {
            return this.getHeaderPadCount(this.name.length());
        }
        return this.getHeaderPadCount(this.name.getBytes(charset).length);
    }
    
    public int getHeaderPadCount(final long namesize) {
        if (this.alignmentBoundary == 0) {
            return 0;
        }
        int size = this.headerSize + 1;
        if (this.name != null) {
            size += (int)namesize;
        }
        final int remain = size % this.alignmentBoundary;
        if (remain > 0) {
            return this.alignmentBoundary - remain;
        }
        return 0;
    }
    
    public int getDataPadCount() {
        if (this.alignmentBoundary == 0) {
            return 0;
        }
        final long size = this.filesize;
        final int remain = (int)(size % this.alignmentBoundary);
        if (remain > 0) {
            return this.alignmentBoundary - remain;
        }
        return 0;
    }
    
    public long getInode() {
        return this.inode;
    }
    
    public long getMode() {
        return (this.mode == 0L && !"TRAILER!!!".equals(this.name)) ? 32768L : this.mode;
    }
    
    @Override
    public String getName() {
        return this.name;
    }
    
    public long getNumberOfLinks() {
        return (this.nlink == 0L) ? (this.isDirectory() ? 2L : 1L) : this.nlink;
    }
    
    public long getRemoteDevice() {
        this.checkOldFormat();
        return this.rmin;
    }
    
    public long getRemoteDeviceMaj() {
        this.checkNewFormat();
        return this.rmaj;
    }
    
    public long getRemoteDeviceMin() {
        this.checkNewFormat();
        return this.rmin;
    }
    
    public long getTime() {
        return this.mtime;
    }
    
    @Override
    public Date getLastModifiedDate() {
        return new Date(1000L * this.getTime());
    }
    
    public long getUID() {
        return this.uid;
    }
    
    public boolean isBlockDevice() {
        return CpioUtil.fileType(this.mode) == 24576L;
    }
    
    public boolean isCharacterDevice() {
        return CpioUtil.fileType(this.mode) == 8192L;
    }
    
    @Override
    public boolean isDirectory() {
        return CpioUtil.fileType(this.mode) == 16384L;
    }
    
    public boolean isNetwork() {
        return CpioUtil.fileType(this.mode) == 36864L;
    }
    
    public boolean isPipe() {
        return CpioUtil.fileType(this.mode) == 4096L;
    }
    
    public boolean isRegularFile() {
        return CpioUtil.fileType(this.mode) == 32768L;
    }
    
    public boolean isSocket() {
        return CpioUtil.fileType(this.mode) == 49152L;
    }
    
    public boolean isSymbolicLink() {
        return CpioUtil.fileType(this.mode) == 40960L;
    }
    
    public void setChksum(final long chksum) {
        this.checkNewFormat();
        this.chksum = (chksum & 0xFFFFFFFFL);
    }
    
    public void setDevice(final long device) {
        this.checkOldFormat();
        this.min = device;
    }
    
    public void setDeviceMaj(final long maj) {
        this.checkNewFormat();
        this.maj = maj;
    }
    
    public void setDeviceMin(final long min) {
        this.checkNewFormat();
        this.min = min;
    }
    
    public void setSize(final long size) {
        if (size < 0L || size > 4294967295L) {
            throw new IllegalArgumentException("Invalid entry size <" + size + ">");
        }
        this.filesize = size;
    }
    
    public void setGID(final long gid) {
        this.gid = gid;
    }
    
    public void setInode(final long inode) {
        this.inode = inode;
    }
    
    public void setMode(final long mode) {
        final long maskedMode = mode & 0xF000L;
        switch ((int)maskedMode) {
            case 4096:
            case 8192:
            case 16384:
            case 24576:
            case 32768:
            case 36864:
            case 40960:
            case 49152: {
                this.mode = mode;
                return;
            }
            default: {
                throw new IllegalArgumentException("Unknown mode. Full: " + Long.toHexString(mode) + " Masked: " + Long.toHexString(maskedMode));
            }
        }
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public void setNumberOfLinks(final long nlink) {
        this.nlink = nlink;
    }
    
    public void setRemoteDevice(final long device) {
        this.checkOldFormat();
        this.rmin = device;
    }
    
    public void setRemoteDeviceMaj(final long rmaj) {
        this.checkNewFormat();
        this.rmaj = rmaj;
    }
    
    public void setRemoteDeviceMin(final long rmin) {
        this.checkNewFormat();
        this.rmin = rmin;
    }
    
    public void setTime(final long time) {
        this.mtime = time;
    }
    
    public void setTime(final FileTime time) {
        this.mtime = time.to(TimeUnit.SECONDS);
    }
    
    public void setUID(final long uid) {
        this.uid = uid;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(this.name);
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }
        final CpioArchiveEntry other = (CpioArchiveEntry)obj;
        if (this.name == null) {
            return other.name == null;
        }
        return this.name.equals(other.name);
    }
}
