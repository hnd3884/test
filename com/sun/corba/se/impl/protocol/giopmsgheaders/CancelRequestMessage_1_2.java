package com.sun.corba.se.impl.protocol.giopmsgheaders;

import java.io.IOException;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.InputStream;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;

public final class CancelRequestMessage_1_2 extends Message_1_1 implements CancelRequestMessage
{
    private int request_id;
    
    CancelRequestMessage_1_2() {
        this.request_id = 0;
    }
    
    CancelRequestMessage_1_2(final int request_id) {
        super(1195986768, GIOPVersion.V1_2, (byte)0, (byte)2, 4);
        this.request_id = 0;
        this.request_id = request_id;
    }
    
    @Override
    public int getRequestId() {
        return this.request_id;
    }
    
    @Override
    public void read(final InputStream inputStream) {
        super.read(inputStream);
        this.request_id = inputStream.read_ulong();
    }
    
    @Override
    public void write(final OutputStream outputStream) {
        super.write(outputStream);
        outputStream.write_ulong(this.request_id);
    }
    
    @Override
    public void callback(final MessageHandler messageHandler) throws IOException {
        messageHandler.handleInput(this);
    }
}
