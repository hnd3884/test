package jcifs.smb;

import java.io.UnsupportedEncodingException;

class NetServerEnum2 extends SmbComTransaction
{
    static final int SV_TYPE_ALL = -1;
    static final int SV_TYPE_DOMAIN_ENUM = Integer.MIN_VALUE;
    static final String[] DESCR;
    String domain;
    String lastName;
    int serverTypes;
    
    NetServerEnum2(final String domain, final int serverTypes) {
        this.lastName = null;
        this.domain = domain;
        this.serverTypes = serverTypes;
        this.command = 37;
        this.subCommand = 104;
        this.name = "\\PIPE\\LANMAN";
        this.maxParameterCount = 8;
        this.maxDataCount = 16384;
        this.maxSetupCount = 0;
        this.setupCount = 0;
        this.timeout = 5000;
    }
    
    void reset(final int key, final String lastName) {
        super.reset();
        this.lastName = lastName;
    }
    
    int writeSetupWireFormat(final byte[] dst, final int dstIndex) {
        return 0;
    }
    
    int writeParametersWireFormat(final byte[] dst, int dstIndex) {
        final int start = dstIndex;
        final int which = (this.subCommand != 104) ? 1 : 0;
        byte[] descr;
        try {
            descr = NetServerEnum2.DESCR[which].getBytes("ASCII");
        }
        catch (final UnsupportedEncodingException uee) {
            return 0;
        }
        ServerMessageBlock.writeInt2(this.subCommand & 0xFF, dst, dstIndex);
        dstIndex += 2;
        System.arraycopy(descr, 0, dst, dstIndex, descr.length);
        dstIndex += descr.length;
        ServerMessageBlock.writeInt2(1L, dst, dstIndex);
        dstIndex += 2;
        ServerMessageBlock.writeInt2(this.maxDataCount, dst, dstIndex);
        dstIndex += 2;
        ServerMessageBlock.writeInt4(this.serverTypes, dst, dstIndex);
        dstIndex += 4;
        dstIndex += this.writeString(this.domain.toUpperCase(), dst, dstIndex, false);
        if (which == 1) {
            dstIndex += this.writeString(this.lastName.toUpperCase(), dst, dstIndex, false);
        }
        return dstIndex - start;
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
        return 0;
    }
    
    public String toString() {
        return new String("NetServerEnum2[" + super.toString() + ",name=" + this.name + ",serverTypes=" + ((this.serverTypes == -1) ? "SV_TYPE_ALL" : "SV_TYPE_DOMAIN_ENUM") + "]");
    }
    
    static {
        DESCR = new String[] { "WrLehDz\u0000B16BBDz\u0000", "WrLehDzz\u0000B16BBDz\u0000" };
    }
}
