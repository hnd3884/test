package sun.management.jdp;

import java.io.IOException;
import java.io.OutputStream;
import java.io.DataOutputStream;
import java.io.ByteArrayOutputStream;

public final class JdpPacketWriter
{
    private final ByteArrayOutputStream baos;
    private final DataOutputStream pkt;
    
    public JdpPacketWriter() throws IOException {
        this.baos = new ByteArrayOutputStream();
        (this.pkt = new DataOutputStream(this.baos)).writeInt(JdpGenericPacket.getMagic());
        this.pkt.writeShort(JdpGenericPacket.getVersion());
    }
    
    public void addEntry(final String s) throws IOException {
        this.pkt.writeUTF(s);
    }
    
    public void addEntry(final String s, final String s2) throws IOException {
        if (s2 != null) {
            this.addEntry(s);
            this.addEntry(s2);
        }
    }
    
    public byte[] getPacketBytes() {
        return this.baos.toByteArray();
    }
}
