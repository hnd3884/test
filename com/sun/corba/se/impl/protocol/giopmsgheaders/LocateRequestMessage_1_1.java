package com.sun.corba.se.impl.protocol.giopmsgheaders;

import java.io.IOException;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.InputStream;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import com.sun.corba.se.spi.ior.ObjectKey;
import com.sun.corba.se.spi.orb.ORB;

public final class LocateRequestMessage_1_1 extends Message_1_1 implements LocateRequestMessage
{
    private ORB orb;
    private int request_id;
    private byte[] object_key;
    private ObjectKey objectKey;
    
    LocateRequestMessage_1_1(final ORB orb) {
        this.orb = null;
        this.request_id = 0;
        this.object_key = null;
        this.objectKey = null;
        this.orb = orb;
    }
    
    LocateRequestMessage_1_1(final ORB orb, final int request_id, final byte[] object_key) {
        super(1195986768, GIOPVersion.V1_1, (byte)0, (byte)3, 0);
        this.orb = null;
        this.request_id = 0;
        this.object_key = null;
        this.objectKey = null;
        this.orb = orb;
        this.request_id = request_id;
        this.object_key = object_key;
    }
    
    @Override
    public int getRequestId() {
        return this.request_id;
    }
    
    @Override
    public ObjectKey getObjectKey() {
        if (this.objectKey == null) {
            this.objectKey = MessageBase.extractObjectKey(this.object_key, this.orb);
        }
        return this.objectKey;
    }
    
    @Override
    public void read(final InputStream inputStream) {
        super.read(inputStream);
        this.request_id = inputStream.read_ulong();
        final int read_long = inputStream.read_long();
        inputStream.read_octet_array(this.object_key = new byte[read_long], 0, read_long);
    }
    
    @Override
    public void write(final OutputStream outputStream) {
        super.write(outputStream);
        outputStream.write_ulong(this.request_id);
        MessageBase.nullCheck(this.object_key);
        outputStream.write_long(this.object_key.length);
        outputStream.write_octet_array(this.object_key, 0, this.object_key.length);
    }
    
    @Override
    public void callback(final MessageHandler messageHandler) throws IOException {
        messageHandler.handleInput(this);
    }
}
