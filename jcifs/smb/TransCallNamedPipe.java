package jcifs.smb;

import jcifs.util.LogStream;

class TransCallNamedPipe extends SmbComTransaction
{
    private byte[] pipeData;
    private int pipeDataOff;
    private int pipeDataLen;
    
    TransCallNamedPipe(final String pipeName, final byte[] data, final int off, final int len) {
        this.name = pipeName;
        this.pipeData = data;
        this.pipeDataOff = off;
        this.pipeDataLen = len;
        this.command = 37;
        this.subCommand = 84;
        this.timeout = -1;
        this.maxParameterCount = 0;
        this.maxDataCount = 65535;
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
        if (dst.length - dstIndex < this.pipeDataLen) {
            final LogStream log = TransCallNamedPipe.log;
            if (LogStream.level >= 3) {
                TransCallNamedPipe.log.println("TransCallNamedPipe data too long for buffer");
            }
            return 0;
        }
        System.arraycopy(this.pipeData, this.pipeDataOff, dst, dstIndex, this.pipeDataLen);
        return this.pipeDataLen;
    }
    
    int readParametersWireFormat(final byte[] buffer, final int bufferIndex, final int len) {
        return 0;
    }
    
    int readDataWireFormat(final byte[] buffer, final int bufferIndex, final int len) {
        return 0;
    }
    
    public String toString() {
        return new String("TransCallNamedPipe[" + super.toString() + ",pipeName=" + this.name + "]");
    }
}
