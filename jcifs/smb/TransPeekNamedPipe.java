package jcifs.smb;

class TransPeekNamedPipe extends SmbComTransaction
{
    private int fid;
    
    TransPeekNamedPipe(final String pipeName, final int fid) {
        this.name = pipeName;
        this.fid = fid;
        this.command = 37;
        this.subCommand = 35;
        this.timeout = -1;
        this.maxParameterCount = 6;
        this.maxDataCount = 1;
        this.maxSetupCount = 0;
        this.setupCount = 2;
    }
    
    int writeSetupWireFormat(final byte[] dst, int dstIndex) {
        dst[dstIndex++] = this.subCommand;
        dst[dstIndex++] = 0;
        ServerMessageBlock.writeInt2(this.fid, dst, dstIndex);
        return 4;
    }
    
    int readSetupWireFormat(final byte[] buffer, final int bufferIndex, final int len) {
        return 0;
    }
    
    int writeParametersWireFormat(final byte[] dst, final int dstIndex) {
        return 0;
    }
    
    int writeDataWireFormat(final byte[] dst, final int dstIndex) {
        return 0;
    }
    
    int readParametersWireFormat(final byte[] buffer, final int bufferIndex, final int len) {
        return 0;
    }
    
    int readDataWireFormat(final byte[] buffer, final int bufferIndex, final int len) {
        return 0;
    }
    
    public String toString() {
        return new String("TransPeekNamedPipe[" + super.toString() + ",pipeName=" + this.name + "]");
    }
}
