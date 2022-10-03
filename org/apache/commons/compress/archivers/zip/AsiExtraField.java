package org.apache.commons.compress.archivers.zip;

import java.util.zip.ZipException;
import java.util.zip.CRC32;

public class AsiExtraField implements ZipExtraField, UnixStat, Cloneable
{
    private static final ZipShort HEADER_ID;
    private static final int MIN_SIZE = 14;
    private int mode;
    private int uid;
    private int gid;
    private String link;
    private boolean dirFlag;
    private CRC32 crc;
    
    public AsiExtraField() {
        this.link = "";
        this.crc = new CRC32();
    }
    
    @Override
    public ZipShort getHeaderId() {
        return AsiExtraField.HEADER_ID;
    }
    
    @Override
    public ZipShort getLocalFileDataLength() {
        return new ZipShort(14 + this.getLinkedFile().getBytes().length);
    }
    
    @Override
    public ZipShort getCentralDirectoryLength() {
        return this.getLocalFileDataLength();
    }
    
    @Override
    public byte[] getLocalFileDataData() {
        final byte[] data = new byte[this.getLocalFileDataLength().getValue() - 4];
        System.arraycopy(ZipShort.getBytes(this.getMode()), 0, data, 0, 2);
        final byte[] linkArray = this.getLinkedFile().getBytes();
        System.arraycopy(ZipLong.getBytes(linkArray.length), 0, data, 2, 4);
        System.arraycopy(ZipShort.getBytes(this.getUserId()), 0, data, 6, 2);
        System.arraycopy(ZipShort.getBytes(this.getGroupId()), 0, data, 8, 2);
        System.arraycopy(linkArray, 0, data, 10, linkArray.length);
        this.crc.reset();
        this.crc.update(data);
        final long checksum = this.crc.getValue();
        final byte[] result = new byte[data.length + 4];
        System.arraycopy(ZipLong.getBytes(checksum), 0, result, 0, 4);
        System.arraycopy(data, 0, result, 4, data.length);
        return result;
    }
    
    @Override
    public byte[] getCentralDirectoryData() {
        return this.getLocalFileDataData();
    }
    
    public void setUserId(final int uid) {
        this.uid = uid;
    }
    
    public int getUserId() {
        return this.uid;
    }
    
    public void setGroupId(final int gid) {
        this.gid = gid;
    }
    
    public int getGroupId() {
        return this.gid;
    }
    
    public void setLinkedFile(final String name) {
        this.link = name;
        this.mode = this.getMode(this.mode);
    }
    
    public String getLinkedFile() {
        return this.link;
    }
    
    public boolean isLink() {
        return !this.getLinkedFile().isEmpty();
    }
    
    public void setMode(final int mode) {
        this.mode = this.getMode(mode);
    }
    
    public int getMode() {
        return this.mode;
    }
    
    public void setDirectory(final boolean dirFlag) {
        this.dirFlag = dirFlag;
        this.mode = this.getMode(this.mode);
    }
    
    public boolean isDirectory() {
        return this.dirFlag && !this.isLink();
    }
    
    @Override
    public void parseFromLocalFileData(final byte[] data, final int offset, final int length) throws ZipException {
        if (length < 14) {
            throw new ZipException("The length is too short, only " + length + " bytes, expected at least " + 14);
        }
        final long givenChecksum = ZipLong.getValue(data, offset);
        final byte[] tmp = new byte[length - 4];
        System.arraycopy(data, offset + 4, tmp, 0, length - 4);
        this.crc.reset();
        this.crc.update(tmp);
        final long realChecksum = this.crc.getValue();
        if (givenChecksum != realChecksum) {
            throw new ZipException("Bad CRC checksum, expected " + Long.toHexString(givenChecksum) + " instead of " + Long.toHexString(realChecksum));
        }
        final int newMode = ZipShort.getValue(tmp, 0);
        final int linkArrayLength = (int)ZipLong.getValue(tmp, 2);
        if (linkArrayLength < 0 || linkArrayLength > tmp.length - 10) {
            throw new ZipException("Bad symbolic link name length " + linkArrayLength + " in ASI extra field");
        }
        this.uid = ZipShort.getValue(tmp, 6);
        this.gid = ZipShort.getValue(tmp, 8);
        if (linkArrayLength == 0) {
            this.link = "";
        }
        else {
            final byte[] linkArray = new byte[linkArrayLength];
            System.arraycopy(tmp, 10, linkArray, 0, linkArrayLength);
            this.link = new String(linkArray);
        }
        this.setDirectory((newMode & 0x4000) != 0x0);
        this.setMode(newMode);
    }
    
    @Override
    public void parseFromCentralDirectoryData(final byte[] buffer, final int offset, final int length) throws ZipException {
        this.parseFromLocalFileData(buffer, offset, length);
    }
    
    protected int getMode(final int mode) {
        int type = 32768;
        if (this.isLink()) {
            type = 40960;
        }
        else if (this.isDirectory()) {
            type = 16384;
        }
        return type | (mode & 0xFFF);
    }
    
    public Object clone() {
        try {
            final AsiExtraField cloned = (AsiExtraField)super.clone();
            cloned.crc = new CRC32();
            return cloned;
        }
        catch (final CloneNotSupportedException cnfe) {
            throw new RuntimeException(cnfe);
        }
    }
    
    static {
        HEADER_ID = new ZipShort(30062);
    }
}
