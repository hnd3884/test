package org.apache.commons.compress.archivers.zip;

import java.util.zip.ZipException;
import org.apache.commons.compress.utils.ByteUtils;

public final class JarMarker implements ZipExtraField
{
    private static final ZipShort ID;
    private static final ZipShort NULL;
    private static final JarMarker DEFAULT;
    
    public static JarMarker getInstance() {
        return JarMarker.DEFAULT;
    }
    
    @Override
    public ZipShort getHeaderId() {
        return JarMarker.ID;
    }
    
    @Override
    public ZipShort getLocalFileDataLength() {
        return JarMarker.NULL;
    }
    
    @Override
    public ZipShort getCentralDirectoryLength() {
        return JarMarker.NULL;
    }
    
    @Override
    public byte[] getLocalFileDataData() {
        return ByteUtils.EMPTY_BYTE_ARRAY;
    }
    
    @Override
    public byte[] getCentralDirectoryData() {
        return ByteUtils.EMPTY_BYTE_ARRAY;
    }
    
    @Override
    public void parseFromLocalFileData(final byte[] data, final int offset, final int length) throws ZipException {
        if (length != 0) {
            throw new ZipException("JarMarker doesn't expect any data");
        }
    }
    
    @Override
    public void parseFromCentralDirectoryData(final byte[] buffer, final int offset, final int length) throws ZipException {
        this.parseFromLocalFileData(buffer, offset, length);
    }
    
    static {
        ID = new ZipShort(51966);
        NULL = new ZipShort(0);
        DEFAULT = new JarMarker();
    }
}
