package jcifs.smb;

import java.io.UnsupportedEncodingException;
import jcifs.Config;

class SmbComNegotiate extends ServerMessageBlock
{
    private static final String DIALECTS = "\u0002NT LM 0.12\u0000";
    
    SmbComNegotiate() {
        this.command = 114;
        this.flags2 = Config.getInt("jcifs.smb.client.flags2", SmbConstants.DEFAULT_FLAGS2);
    }
    
    int writeParameterWordsWireFormat(final byte[] dst, final int dstIndex) {
        return 0;
    }
    
    int writeBytesWireFormat(final byte[] dst, final int dstIndex) {
        byte[] dialects;
        try {
            dialects = "\u0002NT LM 0.12\u0000".getBytes("ASCII");
        }
        catch (final UnsupportedEncodingException uee) {
            return 0;
        }
        System.arraycopy(dialects, 0, dst, dstIndex, dialects.length);
        return dialects.length;
    }
    
    int readParameterWordsWireFormat(final byte[] buffer, final int bufferIndex) {
        return 0;
    }
    
    int readBytesWireFormat(final byte[] buffer, final int bufferIndex) {
        return 0;
    }
    
    public String toString() {
        return new String("SmbComNegotiate[" + super.toString() + ",wordCount=" + this.wordCount + ",dialects=NT LM 0.12]");
    }
}
