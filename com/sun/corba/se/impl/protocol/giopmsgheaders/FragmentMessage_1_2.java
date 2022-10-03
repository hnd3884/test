package com.sun.corba.se.impl.protocol.giopmsgheaders;

import java.io.IOException;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.InputStream;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;

public final class FragmentMessage_1_2 extends Message_1_2 implements FragmentMessage
{
    FragmentMessage_1_2() {
    }
    
    FragmentMessage_1_2(final int request_id) {
        super(1195986768, GIOPVersion.V1_2, (byte)0, (byte)7, 0);
        this.message_type = 7;
        this.request_id = request_id;
    }
    
    FragmentMessage_1_2(final Message_1_1 message_1_1) {
        this.magic = message_1_1.magic;
        this.GIOP_version = message_1_1.GIOP_version;
        this.flags = message_1_1.flags;
        this.message_type = 7;
        this.message_size = 0;
        switch (message_1_1.message_type) {
            case 0: {
                this.request_id = ((RequestMessage)message_1_1).getRequestId();
                break;
            }
            case 1: {
                this.request_id = ((ReplyMessage)message_1_1).getRequestId();
                break;
            }
            case 3: {
                this.request_id = ((LocateRequestMessage)message_1_1).getRequestId();
                break;
            }
            case 4: {
                this.request_id = ((LocateReplyMessage)message_1_1).getRequestId();
                break;
            }
            case 7: {
                this.request_id = ((FragmentMessage)message_1_1).getRequestId();
                break;
            }
        }
    }
    
    @Override
    public int getRequestId() {
        return this.request_id;
    }
    
    @Override
    public int getHeaderLength() {
        return 16;
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
