package com.sun.corba.se.impl.protocol.giopmsgheaders;

import java.io.IOException;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.InputStream;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import com.sun.corba.se.spi.ior.ObjectKey;
import com.sun.corba.se.spi.orb.ORB;

public final class LocateRequestMessage_1_2 extends Message_1_2 implements LocateRequestMessage
{
    private ORB orb;
    private ObjectKey objectKey;
    private TargetAddress target;
    
    LocateRequestMessage_1_2(final ORB orb) {
        this.orb = null;
        this.objectKey = null;
        this.target = null;
        this.orb = orb;
    }
    
    LocateRequestMessage_1_2(final ORB orb, final int request_id, final TargetAddress target) {
        super(1195986768, GIOPVersion.V1_2, (byte)0, (byte)3, 0);
        this.orb = null;
        this.objectKey = null;
        this.target = null;
        this.orb = orb;
        this.request_id = request_id;
        this.target = target;
    }
    
    @Override
    public int getRequestId() {
        return this.request_id;
    }
    
    @Override
    public ObjectKey getObjectKey() {
        if (this.objectKey == null) {
            this.objectKey = MessageBase.extractObjectKey(this.target, this.orb);
        }
        return this.objectKey;
    }
    
    @Override
    public void read(final InputStream inputStream) {
        super.read(inputStream);
        this.request_id = inputStream.read_ulong();
        this.target = TargetAddressHelper.read(inputStream);
        this.getObjectKey();
    }
    
    @Override
    public void write(final OutputStream outputStream) {
        super.write(outputStream);
        outputStream.write_ulong(this.request_id);
        MessageBase.nullCheck(this.target);
        TargetAddressHelper.write(outputStream, this.target);
    }
    
    @Override
    public void callback(final MessageHandler messageHandler) throws IOException {
        messageHandler.handleInput(this);
    }
}
