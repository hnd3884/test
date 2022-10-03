package org.apache.tika.fork;

import java.io.IOException;
import java.util.Enumeration;
import java.io.InputStream;
import java.net.URL;
import java.io.DataOutputStream;
import java.io.DataInputStream;

class ClassLoaderResource implements ForkResource
{
    private final ClassLoader loader;
    
    public ClassLoaderResource(final ClassLoader loader) {
        this.loader = loader;
    }
    
    @Override
    public Throwable process(final DataInputStream input, final DataOutputStream output) throws IOException {
        final byte type = input.readByte();
        final String name = input.readUTF();
        if (type == 1) {
            final InputStream stream = this.loader.getResourceAsStream(name);
            if (stream != null) {
                output.writeBoolean(true);
                this.writeAndCloseStream(output, stream);
            }
            else {
                output.writeBoolean(false);
            }
        }
        else if (type == 2) {
            final Enumeration<URL> resources = this.loader.getResources(name);
            while (resources.hasMoreElements()) {
                output.writeBoolean(true);
                final InputStream stream2 = resources.nextElement().openStream();
                this.writeAndCloseStream(output, stream2);
            }
            output.writeBoolean(false);
        }
        output.flush();
        return null;
    }
    
    private void writeAndCloseStream(final DataOutputStream output, final InputStream stream) throws IOException {
        try {
            final byte[] buffer = new byte[65535];
            int n;
            while ((n = stream.read(buffer)) != -1) {
                output.writeShort(n);
                output.write(buffer, 0, n);
            }
            output.writeShort(0);
        }
        finally {
            stream.close();
        }
    }
}
