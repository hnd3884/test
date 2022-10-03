package jcifs.netbios;

import java.io.IOException;
import java.io.InputStream;

public class SessionRequestPacket extends SessionServicePacket
{
    private Name calledName;
    private Name callingName;
    
    SessionRequestPacket() {
        this.calledName = new Name();
        this.callingName = new Name();
    }
    
    public SessionRequestPacket(final Name calledName, final Name callingName) {
        this.type = 129;
        this.calledName = calledName;
        this.callingName = callingName;
    }
    
    int writeTrailerWireFormat(final byte[] dst, int dstIndex) {
        final int start = dstIndex;
        dstIndex += this.calledName.writeWireFormat(dst, dstIndex);
        dstIndex += this.callingName.writeWireFormat(dst, dstIndex);
        return dstIndex - start;
    }
    
    int readTrailerWireFormat(final InputStream in, final byte[] buffer, int bufferIndex) throws IOException {
        final int start = bufferIndex;
        if (in.read(buffer, bufferIndex, this.length) != this.length) {
            throw new IOException("invalid session request wire format");
        }
        bufferIndex += this.calledName.readWireFormat(buffer, bufferIndex);
        bufferIndex += this.callingName.readWireFormat(buffer, bufferIndex);
        return bufferIndex - start;
    }
}
