package jcifs.smb;

import jcifs.util.Hexdump;

class NtTransQuerySecurityDesc extends SmbComNtTransaction
{
    int fid;
    int securityInformation;
    
    NtTransQuerySecurityDesc(final int fid, final int securityInformation) {
        this.fid = fid;
        this.securityInformation = securityInformation;
        this.command = -96;
        this.function = 6;
        this.setupCount = 0;
        this.totalDataCount = 0;
        this.maxParameterCount = 4;
        this.maxDataCount = 32768;
        this.maxSetupCount = 0;
    }
    
    int writeSetupWireFormat(final byte[] dst, final int dstIndex) {
        return 0;
    }
    
    int writeParametersWireFormat(final byte[] dst, int dstIndex) {
        final int start = dstIndex;
        ServerMessageBlock.writeInt2(this.fid, dst, dstIndex);
        dstIndex += 2;
        dst[dstIndex++] = 0;
        dst[dstIndex++] = 0;
        ServerMessageBlock.writeInt4(this.securityInformation, dst, dstIndex);
        dstIndex += 4;
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
        return new String("NtTransGetSecurityDesc[" + super.toString() + ",fid=0x" + Hexdump.toHexString(this.fid, 4) + ",securityInformation=0x" + Hexdump.toHexString(this.securityInformation, 8) + "]");
    }
}
