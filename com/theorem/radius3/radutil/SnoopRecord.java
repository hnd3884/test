package com.theorem.radius3.radutil;

import java.io.IOException;
import java.util.Date;
import com.theorem.radius3.AttributeList;
import java.net.InetAddress;
import java.text.SimpleDateFormat;

public class SnoopRecord
{
    private static final SimpleDateFormat a;
    private byte[] b;
    private int c;
    private byte[] d;
    private int e;
    public int originalLength;
    public int includedLength;
    public int packetRecordLength;
    public int cumulativeDrops;
    public int seconds;
    public int microseconds;
    DumpPacket f;
    private byte[] g;
    private byte[] h;
    
    protected SnoopRecord(final int originalLength, final int includedLength, final int packetRecordLength, final int cumulativeDrops, final int seconds, final int microseconds) {
        this.originalLength = originalLength;
        this.includedLength = includedLength;
        this.packetRecordLength = packetRecordLength;
        this.cumulativeDrops = cumulativeDrops;
        this.seconds = seconds;
        this.microseconds = microseconds;
    }
    
    public final InetAddress getSourceIP() {
        return AttributeList.parseIPAttribute(this.b);
    }
    
    public final int getSourcePort() {
        return this.c;
    }
    
    public final InetAddress getDestinationIP() {
        return AttributeList.parseIPAttribute(this.d);
    }
    
    public final int getDestinationPort() {
        return this.e;
    }
    
    public final int getSeconds() {
        return this.seconds;
    }
    
    public final int getMicroseconds() {
        return this.microseconds;
    }
    
    public final long getTimeStamp() {
        return this.seconds * 100000L + this.microseconds;
    }
    
    public final DumpPacket getDumpPacket() {
        return this.f = new DumpPacket(this.h);
    }
    
    public final String toString() {
        final String string = "SnoopRecord:\n    Original Length " + this.originalLength + '\n' + "    Included Length " + this.includedLength + '\n' + "    Packet Record Length " + this.packetRecordLength + '\n' + "    Cumulative Drops " + this.cumulativeDrops + '\n' + "    Timestamp " + SnoopRecord.a.format(new Date(this.seconds)) + "." + this.microseconds + "\n" + "Source " + this.getSourceIP().getHostAddress() + ":" + this.getSourcePort() + "\n" + "Destination " + this.getDestinationIP().getHostAddress() + ":" + this.getDestinationPort() + "\n";
        if (this.h == null) {
            return string;
        }
        return string + this.getDumpPacket().toString() + "Raw Packet:\n" + new ByteIterator(this.h, false).dump(0, this.h.length);
    }
    
    public final byte[] getRADIUSPacket() {
        return this.h;
    }
    
    protected final void a(final byte[] g) throws IOException {
        this.g = g;
        this.a();
    }
    
    private final void a() throws IOException {
        final ByteIterator byteIterator = new ByteIterator(this.g, false);
        byteIterator.seek(14);
        final int unsignedByte = byteIterator.readUnsignedByte();
        if (unsignedByte >>> 4 != 4) {
            throw new IOException("Datagram is not version 4");
        }
        final int n = unsignedByte & 0xF;
        byteIterator.moveByte();
        final int n2 = byteIterator.readShort() & 0xFFFF;
        byteIterator.moveShort();
        byteIterator.moveShort();
        byteIterator.moveByte();
        if (byteIterator.readUnsignedByte() != 17) {
            throw new IOException("Packet is not UDP");
        }
        byteIterator.moveShort();
        byteIterator.read(this.b = new byte[4]);
        byteIterator.read(this.d = new byte[4]);
        byteIterator.seek(14 + n * 4);
        this.c = (byteIterator.readShort() & 0xFFFF);
        this.e = (byteIterator.readShort() & 0xFFFF);
        final int n3 = byteIterator.readShort() & 0xFFFF;
        byteIterator.moveShort();
        byteIterator.read(this.h = new byte[this.includedLength - byteIterator.current()]);
    }
    
    static {
        a = new SimpleDateFormat("HH:mm:ss");
    }
}
