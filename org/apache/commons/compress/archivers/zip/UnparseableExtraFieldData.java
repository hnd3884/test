package org.apache.commons.compress.archivers.zip;

import java.util.Arrays;

public final class UnparseableExtraFieldData implements ZipExtraField
{
    private static final ZipShort HEADER_ID;
    private byte[] localFileData;
    private byte[] centralDirectoryData;
    
    @Override
    public ZipShort getHeaderId() {
        return UnparseableExtraFieldData.HEADER_ID;
    }
    
    @Override
    public ZipShort getLocalFileDataLength() {
        return new ZipShort((this.localFileData == null) ? 0 : this.localFileData.length);
    }
    
    @Override
    public ZipShort getCentralDirectoryLength() {
        return (this.centralDirectoryData == null) ? this.getLocalFileDataLength() : new ZipShort(this.centralDirectoryData.length);
    }
    
    @Override
    public byte[] getLocalFileDataData() {
        return ZipUtil.copy(this.localFileData);
    }
    
    @Override
    public byte[] getCentralDirectoryData() {
        return (this.centralDirectoryData == null) ? this.getLocalFileDataData() : ZipUtil.copy(this.centralDirectoryData);
    }
    
    @Override
    public void parseFromLocalFileData(final byte[] buffer, final int offset, final int length) {
        this.localFileData = Arrays.copyOfRange(buffer, offset, offset + length);
    }
    
    @Override
    public void parseFromCentralDirectoryData(final byte[] buffer, final int offset, final int length) {
        this.centralDirectoryData = Arrays.copyOfRange(buffer, offset, offset + length);
        if (this.localFileData == null) {
            this.parseFromLocalFileData(buffer, offset, length);
        }
    }
    
    static {
        HEADER_ID = new ZipShort(44225);
    }
}
