package org.apache.tika.fork;

import java.io.IOException;
import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.InputStream;

class InputStreamResource implements ForkResource
{
    private final InputStream stream;
    
    public InputStreamResource(final InputStream stream) {
        this.stream = stream;
    }
    
    @Override
    public Throwable process(final DataInputStream input, final DataOutputStream output) throws IOException {
        final int n = input.readInt();
        final byte[] buffer = new byte[n];
        int m;
        try {
            m = this.stream.read(buffer);
        }
        catch (final IOException e) {
            e.printStackTrace();
            m = -1;
        }
        output.writeInt(m);
        if (m > 0) {
            output.write(buffer, 0, m);
        }
        output.flush();
        return null;
    }
}
