package jcifs.smb;

class SmbComReadAndXResponse extends AndXServerMessageBlock
{
    byte[] b;
    int off;
    int dataCompactionMode;
    int dataLength;
    int dataOffset;
    
    SmbComReadAndXResponse() {
    }
    
    SmbComReadAndXResponse(final byte[] b, final int off) {
        this.b = b;
        this.off = off;
    }
    
    void setParam(final byte[] b, final int off) {
        this.b = b;
        this.off = off;
    }
    
    int writeParameterWordsWireFormat(final byte[] dst, final int dstIndex) {
        return 0;
    }
    
    int writeBytesWireFormat(final byte[] dst, final int dstIndex) {
        return 0;
    }
    
    int readParameterWordsWireFormat(final byte[] buffer, int bufferIndex) {
        final int start = bufferIndex;
        bufferIndex += 2;
        this.dataCompactionMode = ServerMessageBlock.readInt2(buffer, bufferIndex);
        bufferIndex += 4;
        this.dataLength = ServerMessageBlock.readInt2(buffer, bufferIndex);
        bufferIndex += 2;
        this.dataOffset = ServerMessageBlock.readInt2(buffer, bufferIndex);
        bufferIndex += 12;
        return bufferIndex - start;
    }
    
    int readBytesWireFormat(final byte[] buffer, final int bufferIndex) {
        return 0;
    }
    
    public String toString() {
        return new String("SmbComReadAndXResponse[" + super.toString() + ",dataCompactionMode=" + this.dataCompactionMode + ",dataLength=" + this.dataLength + ",dataOffset=" + this.dataOffset + "]");
    }
}
