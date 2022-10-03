package jcifs.smb;

import java.io.UnsupportedEncodingException;

class SmbComTreeConnectAndXResponse extends AndXServerMessageBlock
{
    private static final int SMB_SUPPORT_SEARCH_BITS = 1;
    private static final int SMB_SHARE_IS_IN_DFS = 2;
    boolean supportSearchBits;
    boolean shareIsInDfs;
    String service;
    String nativeFileSystem;
    
    SmbComTreeConnectAndXResponse(final ServerMessageBlock andx) {
        super(andx);
        this.nativeFileSystem = "";
    }
    
    int writeParameterWordsWireFormat(final byte[] dst, final int dstIndex) {
        return 0;
    }
    
    int writeBytesWireFormat(final byte[] dst, final int dstIndex) {
        return 0;
    }
    
    int readParameterWordsWireFormat(final byte[] buffer, final int bufferIndex) {
        this.supportSearchBits = ((buffer[bufferIndex] & 0x1) == 0x1);
        this.shareIsInDfs = ((buffer[bufferIndex] & 0x2) == 0x2);
        return 2;
    }
    
    int readBytesWireFormat(final byte[] buffer, int bufferIndex) {
        final int start = bufferIndex;
        final int len = this.readStringLength(buffer, bufferIndex, 32);
        try {
            this.service = new String(buffer, bufferIndex, len, "ASCII");
        }
        catch (final UnsupportedEncodingException uee) {
            return 0;
        }
        bufferIndex += len + 1;
        if (this.byteCount > bufferIndex - start) {
            this.nativeFileSystem = this.readString(buffer, bufferIndex);
            bufferIndex += this.stringWireLength(this.nativeFileSystem, bufferIndex);
        }
        return bufferIndex - start;
    }
    
    public String toString() {
        final String result = new String("SmbComTreeConnectAndXResponse[" + super.toString() + ",supportSearchBits=" + this.supportSearchBits + ",shareIsInDfs=" + this.shareIsInDfs + ",service=" + this.service + ",nativeFileSystem=" + this.nativeFileSystem + "]");
        return result;
    }
}
