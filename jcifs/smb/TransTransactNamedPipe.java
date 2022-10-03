package jcifs.smb;

import jcifs.util.LogStream;

class TransTransactNamedPipe extends SmbComTransaction
{
    private byte[] pipeData;
    private int pipeFid;
    private int pipeDataOff;
    private int pipeDataLen;
    
    TransTransactNamedPipe(final int fid, final byte[] data, final int off, final int len) {
        this.pipeFid = fid;
        this.pipeData = data;
        this.pipeDataOff = off;
        this.pipeDataLen = len;
        this.command = 37;
        this.subCommand = 38;
        this.maxParameterCount = 0;
        this.maxDataCount = 65535;
        this.maxSetupCount = 0;
        this.setupCount = 2;
        this.name = "\\PIPE\\";
    }
    
    int writeSetupWireFormat(final byte[] dst, int dstIndex) {
        dst[dstIndex++] = this.subCommand;
        dst[dstIndex++] = 0;
        ServerMessageBlock.writeInt2(this.pipeFid, dst, dstIndex);
        dstIndex += 2;
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
            final LogStream log = TransTransactNamedPipe.log;
            if (LogStream.level >= 3) {
                TransTransactNamedPipe.log.println("TransTransactNamedPipe data too long for buffer");
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
        return new String("TransTransactNamedPipe[" + super.toString() + ",pipeFid=" + this.pipeFid + "]");
    }
}
