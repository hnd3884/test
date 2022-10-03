package org.msgpack.unpacker;

import java.io.IOException;
import java.nio.ByteBuffer;

final class ByteArrayAccept extends Accept
{
    byte[] value;
    
    @Override
    void acceptRaw(final byte[] raw) {
        this.value = raw;
    }
    
    @Override
    void acceptEmptyRaw() {
        this.value = new byte[0];
    }
    
    @Override
    public void refer(final ByteBuffer bb, final boolean gift) throws IOException {
        bb.get(this.value = new byte[bb.remaining()]);
    }
}
