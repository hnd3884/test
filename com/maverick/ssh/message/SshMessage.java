package com.maverick.ssh.message;

import com.maverick.util.ByteArrayReader;

public class SshMessage extends ByteArrayReader implements Message
{
    int c;
    SshMessage d;
    SshMessage e;
    
    SshMessage() {
        super(new byte[0]);
    }
    
    public SshMessage(final byte[] array) {
        super(array);
        this.c = this.read();
    }
    
    public int getMessageId() {
        return this.c;
    }
}
