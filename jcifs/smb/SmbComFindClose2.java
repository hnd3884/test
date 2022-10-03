package jcifs.smb;

class SmbComFindClose2 extends ServerMessageBlock
{
    private int sid;
    
    SmbComFindClose2(final int sid) {
        this.sid = sid;
        this.command = 52;
    }
    
    int writeParameterWordsWireFormat(final byte[] dst, final int dstIndex) {
        ServerMessageBlock.writeInt2(this.sid, dst, dstIndex);
        return 2;
    }
    
    int writeBytesWireFormat(final byte[] dst, final int dstIndex) {
        return 0;
    }
    
    int readParameterWordsWireFormat(final byte[] buffer, final int bufferIndex) {
        return 0;
    }
    
    int readBytesWireFormat(final byte[] buffer, final int bufferIndex) {
        return 0;
    }
    
    public String toString() {
        return new String("SmbComFindClose2[" + super.toString() + ",sid=" + this.sid + "]");
    }
}
