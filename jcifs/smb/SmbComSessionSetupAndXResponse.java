package jcifs.smb;

import java.io.UnsupportedEncodingException;
import java.io.PrintStream;
import jcifs.util.LogStream;

class SmbComSessionSetupAndXResponse extends AndXServerMessageBlock
{
    private String nativeOs;
    private String nativeLanMan;
    private String primaryDomain;
    boolean isLoggedInAsGuest;
    
    SmbComSessionSetupAndXResponse(final ServerMessageBlock andx) {
        super(andx);
        this.nativeOs = "";
        this.nativeLanMan = "";
        this.primaryDomain = "";
    }
    
    int writeParameterWordsWireFormat(final byte[] dst, final int dstIndex) {
        return 0;
    }
    
    int writeBytesWireFormat(final byte[] dst, final int dstIndex) {
        return 0;
    }
    
    int readParameterWordsWireFormat(final byte[] buffer, final int bufferIndex) {
        this.isLoggedInAsGuest = ((buffer[bufferIndex] & 0x1) == 0x1);
        return 2;
    }
    
    int readBytesWireFormat(final byte[] buffer, int bufferIndex) {
        final int start = bufferIndex;
        this.nativeOs = this.readString(buffer, bufferIndex);
        bufferIndex += this.stringWireLength(this.nativeOs, bufferIndex);
        this.nativeLanMan = this.readString(buffer, bufferIndex);
        bufferIndex += this.stringWireLength(this.nativeLanMan, bufferIndex);
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
                this.primaryDomain = new String(buffer, bufferIndex, len, "UnicodeLittle");
            }
            catch (final UnsupportedEncodingException uee) {
                final LogStream log = SmbComSessionSetupAndXResponse.log;
                if (LogStream.level > 1) {
                    uee.printStackTrace(SmbComSessionSetupAndXResponse.log);
                }
            }
            bufferIndex += len;
        }
        else {
            this.primaryDomain = this.readString(buffer, bufferIndex);
            bufferIndex += this.stringWireLength(this.primaryDomain, bufferIndex);
        }
        return bufferIndex - start;
    }
    
    public String toString() {
        final String result = new String("SmbComSessionSetupAndXResponse[" + super.toString() + ",isLoggedInAsGuest=" + this.isLoggedInAsGuest + ",nativeOs=" + this.nativeOs + ",nativeLanMan=" + this.nativeLanMan + ",primaryDomain=" + this.primaryDomain + "]");
        return result;
    }
}
