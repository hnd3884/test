package sun.rmi.transport.proxy;

import java.io.IOException;
import java.io.DataOutputStream;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;

class HttpOutputStream extends ByteArrayOutputStream
{
    protected OutputStream out;
    boolean responseSent;
    private static byte[] emptyData;
    
    public HttpOutputStream(final OutputStream out) {
        this.responseSent = false;
        this.out = out;
    }
    
    @Override
    public synchronized void close() throws IOException {
        if (!this.responseSent) {
            if (this.size() == 0) {
                this.write(HttpOutputStream.emptyData);
            }
            final DataOutputStream dataOutputStream = new DataOutputStream(this.out);
            dataOutputStream.writeBytes("Content-type: application/octet-stream\r\n");
            dataOutputStream.writeBytes("Content-length: " + this.size() + "\r\n");
            dataOutputStream.writeBytes("\r\n");
            this.writeTo(dataOutputStream);
            dataOutputStream.flush();
            this.reset();
            this.responseSent = true;
        }
    }
    
    static {
        HttpOutputStream.emptyData = new byte[] { 0 };
    }
}
