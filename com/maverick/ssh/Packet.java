package com.maverick.ssh;

import java.io.IOException;
import com.maverick.util.ByteArrayWriter;

public class Packet extends ByteArrayWriter
{
    int b;
    
    public Packet() throws IOException {
        this(35000);
    }
    
    public Packet(final int n) throws IOException {
        super(n + 4);
        this.b = -1;
        this.writeInt(0);
    }
    
    public int setPosition(final int count) {
        final int count2 = super.count;
        super.count = count;
        return count2;
    }
    
    public int position() {
        return super.count;
    }
    
    public void finish() {
        super.buf[0] = (byte)(super.count - 4 >> 24);
        super.buf[1] = (byte)(super.count - 4 >> 16);
        super.buf[2] = (byte)(super.count - 4 >> 8);
        super.buf[3] = (byte)(super.count - 4);
    }
    
    public void reset() {
        super.reset();
        try {
            this.writeInt(0);
        }
        catch (final IOException ex) {}
    }
}
