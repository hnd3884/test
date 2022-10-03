package jcifs.smb;

import java.io.UnsupportedEncodingException;
import java.io.PrintStream;
import jcifs.util.LogStream;

class Kerb5SessionSetupAndXResponse extends AndXServerMessageBlock
{
    private int securityBlobLength;
    private SecurityBlob securityBlob;
    private String nativeOs;
    private String nativeLanMan;
    
    public Kerb5SessionSetupAndXResponse(final ServerMessageBlock andx) {
        super(andx);
        this.securityBlobLength = 0;
        this.securityBlob = new SecurityBlob();
        this.nativeOs = "";
        this.nativeLanMan = "";
    }
    
    SecurityBlob getSecurityBlob() {
        return this.securityBlob;
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
        this.securityBlobLength = ServerMessageBlock.readInt2(buffer, bufferIndex);
        bufferIndex += 2;
        return bufferIndex - start;
    }
    
    int readBytesWireFormat(final byte[] buffer, int bufferIndex) {
        final int start = bufferIndex;
        final byte[] b = new byte[this.securityBlobLength];
        System.arraycopy(buffer, bufferIndex, b, 0, b.length);
        bufferIndex += b.length;
        this.securityBlob.set(b);
        this.nativeOs = this.readString(buffer, bufferIndex);
        bufferIndex += this.stringWireLength(this.nativeOs, bufferIndex);
        if (this.useUnicode) {
            if ((bufferIndex - this.headerStart) % 2 != 0) {
                ++bufferIndex;
            }
            int len = 0;
            while (buffer[bufferIndex + len] != 0) {
                len += 2;
                if (len > 256) {
                    throw new RuntimeException("zero termination not found");
                }
            }
            try {
                this.nativeLanMan = new String(buffer, bufferIndex, len, "UnicodeLittle");
            }
            catch (final UnsupportedEncodingException uee) {
                if (LogStream.level > 1) {
                    uee.printStackTrace(Kerb5SessionSetupAndXResponse.log);
                }
            }
            bufferIndex += len;
        }
        else {
            this.nativeLanMan = this.readString(buffer, bufferIndex);
            bufferIndex += this.stringWireLength(this.nativeLanMan, bufferIndex);
        }
        return bufferIndex - start;
    }
    
    public String toString() {
        final String result = new String("Kerb5SessionSetupAndXResponse[" + super.toString() + ",nativeOs=" + this.nativeOs + ",nativeLanMan=" + this.nativeLanMan + "]");
        return result;
    }
}
