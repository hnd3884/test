package com.sun.corba.se.impl.protocol.giopmsgheaders;

import java.io.IOException;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.InputStream;

public final class FragmentMessage_1_1 extends Message_1_1 implements FragmentMessage
{
    FragmentMessage_1_1() {
    }
    
    FragmentMessage_1_1(final Message_1_1 message_1_1) {
        this.magic = message_1_1.magic;
        this.GIOP_version = message_1_1.GIOP_version;
        this.flags = message_1_1.flags;
        this.message_type = 7;
        this.message_size = 0;
    }
    
    @Override
    public int getRequestId() {
        return -1;
    }
    
    @Override
    public int getHeaderLength() {
        return 12;
    }
    
    @Override
    public void read(final InputStream inputStream) {
        super.read(inputStream);
    }
    
    @Override
    public void write(final OutputStream outputStream) {
        super.write(outputStream);
    }
    
    @Override
    public void callback(final MessageHandler messageHandler) throws IOException {
        messageHandler.handleInput(this);
    }
}
