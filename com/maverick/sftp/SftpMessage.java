package com.maverick.sftp;

import java.io.IOException;
import com.maverick.ssh.message.Message;
import com.maverick.util.ByteArrayReader;

public class SftpMessage extends ByteArrayReader implements Message
{
    int g;
    int h;
    
    SftpMessage(final byte[] array) throws IOException {
        super(array);
        this.g = this.read();
        this.h = (int)this.readInt();
    }
    
    public int getType() {
        return this.g;
    }
    
    public int getMessageId() {
        return this.h;
    }
}
