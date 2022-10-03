package jcifs.smb;

import jcifs.util.Hexdump;

class Trans2FindNext2 extends SmbComTransaction
{
    private int sid;
    private int informationLevel;
    private int resumeKey;
    private int flags;
    private String filename;
    
    Trans2FindNext2(final int sid, final int resumeKey, final String filename) {
        this.sid = sid;
        this.resumeKey = resumeKey;
        this.filename = filename;
        this.command = 50;
        this.subCommand = 2;
        this.informationLevel = 260;
        this.flags = 0;
        this.maxParameterCount = 8;
        this.maxDataCount = Trans2FindFirst2.LIST_SIZE;
        this.maxSetupCount = 0;
    }
    
    void reset(final int resumeKey, final String lastName) {
        super.reset();
        this.resumeKey = resumeKey;
        this.filename = lastName;
        this.flags2 = 0;
    }
    
    int writeSetupWireFormat(final byte[] dst, int dstIndex) {
        dst[dstIndex++] = this.subCommand;
        dst[dstIndex++] = 0;
        return 2;
    }
    
    int writeParametersWireFormat(final byte[] dst, int dstIndex) {
        final int start = dstIndex;
        ServerMessageBlock.writeInt2(this.sid, dst, dstIndex);
        dstIndex += 2;
        ServerMessageBlock.writeInt2(Trans2FindFirst2.LIST_COUNT, dst, dstIndex);
        dstIndex += 2;
        ServerMessageBlock.writeInt2(this.informationLevel, dst, dstIndex);
        dstIndex += 2;
        ServerMessageBlock.writeInt4(this.resumeKey, dst, dstIndex);
        dstIndex += 4;
        ServerMessageBlock.writeInt2(this.flags, dst, dstIndex);
        dstIndex += 2;
        dstIndex += this.writeString(this.filename, dst, dstIndex);
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
        return new String("Trans2FindNext2[" + super.toString() + ",sid=" + this.sid + ",searchCount=" + Trans2FindFirst2.LIST_SIZE + ",informationLevel=0x" + Hexdump.toHexString(this.informationLevel, 3) + ",resumeKey=0x" + Hexdump.toHexString(this.resumeKey, 4) + ",flags=0x" + Hexdump.toHexString(this.flags, 2) + ",filename=" + this.filename + "]");
    }
}
