package jcifs.smb;

class TransCallNamedPipeResponse extends SmbComTransactionResponse
{
    private SmbNamedPipe pipe;
    
    TransCallNamedPipeResponse(final SmbNamedPipe pipe) {
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
    
    int readParametersWireFormat(final byte[] buffer, final int bufferIndex, final int len) {
        return 0;
    }
    
    int readDataWireFormat(final byte[] buffer, final int bufferIndex, final int len) {
        if (this.pipe.pipeIn != null) {
            final TransactNamedPipeInputStream in = (TransactNamedPipeInputStream)this.pipe.pipeIn;
            synchronized (in.lock) {
                in.receive(buffer, bufferIndex, len);
                in.lock.notify();
            }
        }
        return len;
    }
    
    public String toString() {
        return new String("TransCallNamedPipeResponse[" + super.toString() + "]");
    }
}
