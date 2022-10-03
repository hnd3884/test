package org.apache.tika.fork;

import java.io.IOException;
import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.InputStream;

class InputStreamProxy extends InputStream implements ForkProxy
{
    private static final long serialVersionUID = 4350939227765568438L;
    private final int resource;
    private transient DataInputStream input;
    private transient DataOutputStream output;
    
    public InputStreamProxy(final int resource) {
        this.resource = resource;
    }
    
    @Override
    public void init(final DataInputStream input, final DataOutputStream output) {
        this.input = input;
        this.output = output;
    }
    
    @Override
    public int read() throws IOException {
        this.output.writeByte(3);
        this.output.writeByte(this.resource);
        this.output.writeInt(1);
        this.output.flush();
        final int n = this.input.readInt();
        if (n == 1) {
            return this.input.readUnsignedByte();
        }
        return n;
    }
    
    @Override
    public int read(final byte[] b, final int off, final int len) throws IOException {
        this.output.writeByte(3);
        this.output.writeByte(this.resource);
        this.output.writeInt(len);
        this.output.flush();
        final int n = this.input.readInt();
        if (n > 0) {
            this.input.readFully(b, off, n);
        }
        return n;
    }
}
