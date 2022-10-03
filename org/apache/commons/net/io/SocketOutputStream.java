package org.apache.commons.net.io;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.io.FilterOutputStream;

public class SocketOutputStream extends FilterOutputStream
{
    private final Socket __socket;
    
    public SocketOutputStream(final Socket socket, final OutputStream stream) {
        super(stream);
        this.__socket = socket;
    }
    
    @Override
    public void write(final byte[] buffer, final int offset, final int length) throws IOException {
        this.out.write(buffer, offset, length);
    }
    
    @Override
    public void close() throws IOException {
        super.close();
        this.__socket.close();
    }
}
