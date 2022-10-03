package jcifs.smb;

class Kerb5SessionSetupAndX extends AndXServerMessageBlock
{
    private int sessionKey;
    private SmbSession session;
    private SecurityBlob securityBlob;
    
    Kerb5SessionSetupAndX(final SmbSession session, final ServerMessageBlock andx) throws SmbException {
        super(andx);
        this.sessionKey = 0;
        this.securityBlob = new SecurityBlob();
        this.command = 115;
        this.session = session;
    }
    
    SecurityBlob getSecurityBlob() {
        return this.securityBlob;
    }
    
    int writeParameterWordsWireFormat(final byte[] dst, int dstIndex) {
        final int start = dstIndex;
        ServerMessageBlock.writeInt2(this.session.transport.snd_buf_size, dst, dstIndex);
        dstIndex += 2;
        ServerMessageBlock.writeInt2(SmbConstants.CAPABILITIES, dst, dstIndex);
        dstIndex += 2;
        ServerMessageBlock.writeInt2(1L, dst, dstIndex);
        dstIndex += 2;
        ServerMessageBlock.writeInt4(this.sessionKey, dst, dstIndex);
        dstIndex += 4;
        ServerMessageBlock.writeInt2(this.securityBlob.length(), dst, dstIndex);
        dstIndex += 2;
        dst[dstIndex++] = 0;
        dst[dstIndex++] = 0;
        dst[dstIndex++] = 0;
        dst[dstIndex++] = 0;
        ServerMessageBlock.writeInt4(this.session.transport.capabilities, dst, dstIndex);
        dstIndex += 4;
        return dstIndex - start;
    }
    
    int writeBytesWireFormat(final byte[] dst, int dstIndex) {
        final int start = dstIndex;
        System.arraycopy(this.securityBlob.get(), 0, dst, dstIndex, this.securityBlob.length());
        dstIndex += this.securityBlob.length();
        dstIndex += this.writeString((SmbConstants.NATIVE_OS == null) ? "" : SmbConstants.NATIVE_OS, dst, dstIndex);
        dstIndex += this.writeString((SmbConstants.NATIVE_LANMAN == null) ? "" : SmbConstants.NATIVE_LANMAN, dst, dstIndex);
        return dstIndex - start;
    }
    
    public String toString() {
        final String result = new String("Kerb5SessionSetupAndX[" + super.toString() + ",snd_buf_size=" + this.session.transport.snd_buf_size + ",maxMpxCount=" + this.session.transport.maxMpxCount + ",VC_NUMBER=" + 1 + ",sessionKey=" + this.sessionKey + ",securityBlobLength=" + this.securityBlob.length() + ",capabilities=" + SmbConstants.CAPABILITIES + ",securityBlob=" + this.securityBlob.toString() + ",os=" + SmbConstants.NATIVE_OS + ",lanman=" + SmbConstants.NATIVE_LANMAN);
        return result;
    }
    
    int readBytesWireFormat(final byte[] buffer, final int bufferIndex) {
        return 0;
    }
    
    int readParameterWordsWireFormat(final byte[] buffer, final int bufferIndex) {
        return 0;
    }
}
