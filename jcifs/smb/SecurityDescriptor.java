package jcifs.smb;

public class SecurityDescriptor
{
    public int type;
    public ACE[] aces;
    
    public SecurityDescriptor() {
    }
    
    public SecurityDescriptor(final byte[] buffer, final int bufferIndex, final int len) {
        this.decode(buffer, bufferIndex, len);
    }
    
    public int decode(final byte[] buffer, int bufferIndex, final int len) {
        final int start = bufferIndex;
        ++bufferIndex;
        ++bufferIndex;
        this.type = ServerMessageBlock.readInt2(buffer, bufferIndex);
        bufferIndex += 2;
        ServerMessageBlock.readInt4(buffer, bufferIndex);
        bufferIndex += 4;
        ServerMessageBlock.readInt4(buffer, bufferIndex);
        bufferIndex += 4;
        ServerMessageBlock.readInt4(buffer, bufferIndex);
        bufferIndex += 4;
        final int daclOffset = ServerMessageBlock.readInt4(buffer, bufferIndex);
        bufferIndex = start + daclOffset;
        ++bufferIndex;
        ++bufferIndex;
        final int size = ServerMessageBlock.readInt2(buffer, bufferIndex);
        bufferIndex += 2;
        final int numAces = ServerMessageBlock.readInt4(buffer, bufferIndex);
        bufferIndex += 4;
        if (numAces > 4096) {
            throw new RuntimeException("Invalid SecurityDescriptor");
        }
        this.aces = new ACE[numAces];
        for (int i = 0; i < numAces; ++i) {
            this.aces[i] = new ACE();
            bufferIndex += this.aces[i].decode(buffer, bufferIndex);
        }
        return bufferIndex - start;
    }
    
    public String toString() {
        String ret = "SecurityDescriptor:\n";
        for (int ai = 0; ai < this.aces.length; ++ai) {
            ret = ret + this.aces[ai].toString() + "\n";
        }
        return ret;
    }
}
