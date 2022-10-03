package jcifs.smb;

import java.io.UnsupportedEncodingException;

class NetShareEnum extends SmbComTransaction
{
    private static final String DESCR = "WrLeh\u0000B13BWz\u0000";
    
    NetShareEnum() {
        this.command = 37;
        this.subCommand = 0;
        this.name = new String("\\PIPE\\LANMAN");
        this.maxParameterCount = 8;
        this.maxSetupCount = 0;
        this.setupCount = 0;
        this.timeout = 5000;
    }
    
    int writeSetupWireFormat(final byte[] dst, final int dstIndex) {
        return 0;
    }
    
    int writeParametersWireFormat(final byte[] dst, int dstIndex) {
        final int start = dstIndex;
        byte[] descr;
        try {
            descr = "WrLeh\u0000B13BWz\u0000".getBytes("ASCII");
        }
        catch (final UnsupportedEncodingException uee) {
            return 0;
        }
        ServerMessageBlock.writeInt2(0L, dst, dstIndex);
        dstIndex += 2;
        System.arraycopy(descr, 0, dst, dstIndex, descr.length);
        dstIndex += descr.length;
        ServerMessageBlock.writeInt2(1L, dst, dstIndex);
        dstIndex += 2;
        ServerMessageBlock.writeInt2(this.maxDataCount, dst, dstIndex);
        dstIndex += 2;
        return dstIndex - start;
    }
    
    int writeDataWireFormat(final byte[] dst, final int dstIndex) {
        return 0;
    }
    
    int readSetupWireFormat(final byte[] buffer, final int bufferIndex, final int len) {
        return 0;
    }
    
    int readParametersWireFormat(final byte[] buffer, final int bufferIndex, final int len) {
        return 0;
    }
    
    int readDataWireFormat(final byte[] buffer, final int bufferIndex, final int len) {
        return 0;
    }
    
    public String toString() {
        return new String("NetShareEnum[" + super.toString() + "]");
    }
}
