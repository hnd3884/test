package jcifs.smb;

class TransWaitNamedPipe extends SmbComTransaction
{
    TransWaitNamedPipe(final String pipeName) {
        this.name = pipeName;
        this.command = 37;
        this.subCommand = 83;
        this.timeout = -1;
        this.maxParameterCount = 0;
        this.maxDataCount = 0;
        this.maxSetupCount = 0;
        this.setupCount = 2;
    }
    
    int writeSetupWireFormat(final byte[] dst, int dstIndex) {
        dst[dstIndex++] = this.subCommand;
        dst[dstIndex++] = 0;
        dst[dstIndex++] = 0;
        dst[dstIndex++] = 0;
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
        return new String("TransWaitNamedPipe[" + super.toString() + ",pipeName=" + this.name + "]");
    }
}
