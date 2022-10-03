package com.sun.corba.se.impl.protocol.giopmsgheaders;

import org.omg.CORBA.portable.OutputStream;
import java.nio.ByteBuffer;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;

public class Message_1_2 extends Message_1_1
{
    protected int request_id;
    
    Message_1_2() {
        this.request_id = 0;
    }
    
    Message_1_2(final int n, final GIOPVersion giopVersion, final byte b, final byte b2, final int n2) {
        super(n, giopVersion, b, b2, n2);
        this.request_id = 0;
    }
    
    public void unmarshalRequestID(final ByteBuffer byteBuffer) {
        int n;
        int n2;
        int n3;
        int n4;
        if (!this.isLittleEndian()) {
            n = (byteBuffer.get(12) << 24 & 0xFF000000);
            n2 = (byteBuffer.get(13) << 16 & 0xFF0000);
            n3 = (byteBuffer.get(14) << 8 & 0xFF00);
            n4 = (byteBuffer.get(15) << 0 & 0xFF);
        }
        else {
            n = (byteBuffer.get(15) << 24 & 0xFF000000);
            n2 = (byteBuffer.get(14) << 16 & 0xFF0000);
            n3 = (byteBuffer.get(13) << 8 & 0xFF00);
            n4 = (byteBuffer.get(12) << 0 & 0xFF);
        }
        this.request_id = (n | n2 | n3 | n4);
    }
    
    @Override
    public void write(final OutputStream outputStream) {
        if (this.encodingVersion == 0) {
            super.write(outputStream);
            return;
        }
        final GIOPVersion giop_version = this.GIOP_version;
        this.GIOP_version = GIOPVersion.getInstance((byte)13, this.encodingVersion);
        super.write(outputStream);
        this.GIOP_version = giop_version;
    }
}
