package sun.management.jdp;

import java.util.Collections;
import java.util.HashMap;
import java.io.UnsupportedEncodingException;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.util.Map;
import java.io.DataInputStream;

public final class JdpPacketReader
{
    private final DataInputStream pkt;
    private Map<String, String> pmap;
    
    public JdpPacketReader(final byte[] array) throws JdpException {
        this.pmap = null;
        this.pkt = new DataInputStream(new ByteArrayInputStream(array));
        try {
            JdpGenericPacket.checkMagic(this.pkt.readInt());
        }
        catch (final IOException ex) {
            throw new JdpException("Invalid JDP packet received, bad magic");
        }
        try {
            JdpGenericPacket.checkVersion(this.pkt.readShort());
        }
        catch (final IOException ex2) {
            throw new JdpException("Invalid JDP packet received, bad protocol version");
        }
    }
    
    public String getEntry() throws EOFException, JdpException {
        try {
            final short short1 = this.pkt.readShort();
            if (short1 < 1 && short1 > this.pkt.available()) {
                throw new JdpException("Broken JDP packet. Invalid entry length field.");
            }
            final byte[] array = new byte[short1];
            if (this.pkt.read(array) != short1) {
                throw new JdpException("Broken JDP packet. Unable to read entry.");
            }
            return new String(array, "UTF-8");
        }
        catch (final EOFException ex) {
            throw ex;
        }
        catch (final UnsupportedEncodingException ex2) {
            throw new JdpException("Broken JDP packet. Unable to decode entry.");
        }
        catch (final IOException ex3) {
            throw new JdpException("Broken JDP packet. Unable to read entry.");
        }
    }
    
    public Map<String, String> getDiscoveryDataAsMap() throws JdpException {
        if (this.pmap != null) {
            return this.pmap;
        }
        String entry = null;
        Object entry2 = null;
        final HashMap hashMap = new HashMap();
        try {
            while (true) {
                entry = this.getEntry();
                entry2 = this.getEntry();
                hashMap.put(entry, entry2);
            }
        }
        catch (final EOFException ex) {
            if (entry2 == null) {
                throw new JdpException("Broken JDP packet. Key without value." + entry);
            }
            return this.pmap = (Map<String, String>)Collections.unmodifiableMap((Map<?, ?>)hashMap);
        }
    }
}
