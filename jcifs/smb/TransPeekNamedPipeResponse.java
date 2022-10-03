package jcifs.smb;

class TransPeekNamedPipeResponse extends SmbComTransactionResponse
{
    private SmbNamedPipe pipe;
    private int head;
    static final int STATUS_DISCONNECTED = 1;
    static final int STATUS_LISTENING = 2;
    static final int STATUS_CONNECTION_OK = 3;
    static final int STATUS_SERVER_END_CLOSED = 4;
    int status;
    int available;
    
    TransPeekNamedPipeResponse(final SmbNamedPipe pipe) {
        this.pipe = pipe;
    }
    
    int writeSetupWireFormat(final byte[] dst, final int dstIndex) {
        return 0;
    }
    
    int writeParametersWireFormat(final byte[] dst, final int dstIndex) {
        return 0;
    }
    
    int writeDataWireFormat(final byte[] dst, final int dstIndex) {
        return 0;
    }
    
    int readSetupWireFormat(final byte[] buffer, final int bufferIndex, final int len) {
        return 0;
    }
    
    int readParametersWireFormat(final byte[] buffer, int bufferIndex, final int len) {
        this.available = ServerMessageBlock.readInt2(buffer, bufferIndex);
        bufferIndex += 2;
        this.head = ServerMessageBlock.readInt2(buffer, bufferIndex);
        bufferIndex += 2;
        this.status = ServerMessageBlock.readInt2(buffer, bufferIndex);
        return 6;
    }
    
    int readDataWireFormat(final byte[] buffer, final int bufferIndex, final int len) {
        return 0;
    }
    
    public String toString() {
        return new String("TransPeekNamedPipeResponse[" + super.toString() + "]");
    }
}
