package jcifs.smb;

import java.util.Date;
import jcifs.util.Hexdump;
import java.io.UnsupportedEncodingException;
import java.io.PrintStream;
import jcifs.util.LogStream;

class SmbComNegotiateResponse extends ServerMessageBlock
{
    int dialectIndex;
    SmbTransport.ServerData server;
    
    SmbComNegotiateResponse(final SmbTransport.ServerData server) {
        this.server = server;
    }
    
    int writeParameterWordsWireFormat(final byte[] dst, final int dstIndex) {
        return 0;
    }
    
    int writeBytesWireFormat(final byte[] dst, final int dstIndex) {
        return 0;
    }
    
    int readParameterWordsWireFormat(final byte[] buffer, int bufferIndex) {
        final int start = bufferIndex;
        this.dialectIndex = ServerMessageBlock.readInt2(buffer, bufferIndex);
        bufferIndex += 2;
        if (this.dialectIndex > 10) {
            return bufferIndex - start;
        }
        this.server.securityMode = (buffer[bufferIndex++] & 0xFF);
        this.server.security = (this.server.securityMode & 0x1);
        this.server.encryptedPasswords = ((this.server.securityMode & 0x2) == 0x2);
        this.server.signaturesEnabled = ((this.server.securityMode & 0x4) == 0x4);
        this.server.signaturesRequired = ((this.server.securityMode & 0x8) == 0x8);
        this.server.maxMpxCount = ServerMessageBlock.readInt2(buffer, bufferIndex);
        bufferIndex += 2;
        this.server.maxNumberVcs = ServerMessageBlock.readInt2(buffer, bufferIndex);
        bufferIndex += 2;
        this.server.maxBufferSize = ServerMessageBlock.readInt4(buffer, bufferIndex);
        bufferIndex += 4;
        this.server.maxRawSize = ServerMessageBlock.readInt4(buffer, bufferIndex);
        bufferIndex += 4;
        this.server.sessionKey = ServerMessageBlock.readInt4(buffer, bufferIndex);
        bufferIndex += 4;
        this.server.capabilities = ServerMessageBlock.readInt4(buffer, bufferIndex);
        bufferIndex += 4;
        this.server.serverTime = ServerMessageBlock.readTime(buffer, bufferIndex);
        bufferIndex += 8;
        this.server.serverTimeZone = ServerMessageBlock.readInt2(buffer, bufferIndex);
        bufferIndex += 2;
        this.server.encryptionKeyLength = (buffer[bufferIndex++] & 0xFF);
        return bufferIndex - start;
    }
    
    int readBytesWireFormat(final byte[] buffer, int bufferIndex) {
        final int start = bufferIndex;
        System.arraycopy(buffer, bufferIndex, this.server.encryptionKey = new byte[this.server.encryptionKeyLength], 0, this.server.encryptionKeyLength);
        bufferIndex += this.server.encryptionKeyLength;
        if (this.byteCount > this.server.encryptionKeyLength) {
            int len = 0;
            try {
                if ((this.flags2 & 0x8000) == 0x8000) {
                    while (buffer[bufferIndex + len] != 0 || buffer[bufferIndex + len + 1] != 0) {
                        len += 2;
                        if (len > 256) {
                            throw new RuntimeException("zero termination not found");
                        }
                    }
                    this.server.oemDomainName = new String(buffer, bufferIndex, len, "UnicodeLittleUnmarked");
                }
                else {
                    while (buffer[bufferIndex + len] != 0) {
                        if (++len > 256) {
                            throw new RuntimeException("zero termination not found");
                        }
                    }
                    this.server.oemDomainName = new String(buffer, bufferIndex, len, SmbConstants.OEM_ENCODING);
                }
            }
            catch (final UnsupportedEncodingException uee) {
                final LogStream log = SmbComNegotiateResponse.log;
                if (LogStream.level > 1) {
                    uee.printStackTrace(SmbComNegotiateResponse.log);
                }
            }
            bufferIndex += len;
        }
        else {
            this.server.oemDomainName = new String();
        }
        return bufferIndex - start;
    }
    
    public String toString() {
        return new String("SmbComNegotiateResponse[" + super.toString() + ",wordCount=" + this.wordCount + ",dialectIndex=" + this.dialectIndex + ",securityMode=0x" + Hexdump.toHexString(this.server.securityMode, 1) + ",security=" + ((this.server.security == 0) ? "share" : "user") + ",encryptedPasswords=" + this.server.encryptedPasswords + ",maxMpxCount=" + this.server.maxMpxCount + ",maxNumberVcs=" + this.server.maxNumberVcs + ",maxBufferSize=" + this.server.maxBufferSize + ",maxRawSize=" + this.server.maxRawSize + ",sessionKey=0x" + Hexdump.toHexString(this.server.sessionKey, 8) + ",capabilities=0x" + Hexdump.toHexString(this.server.capabilities, 8) + ",serverTime=" + new Date(this.server.serverTime) + ",serverTimeZone=" + this.server.serverTimeZone + ",encryptionKeyLength=" + this.server.encryptionKeyLength + ",byteCount=" + this.byteCount + ",encryptionKey=0x" + Hexdump.toHexString(this.server.encryptionKey, 0, this.server.encryptionKeyLength * 2) + ",oemDomainName=" + this.server.oemDomainName + "]");
    }
}
