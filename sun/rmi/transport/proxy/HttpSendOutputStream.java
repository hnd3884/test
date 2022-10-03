package sun.rmi.transport.proxy;

import java.io.IOException;
import java.io.OutputStream;
import java.io.FilterOutputStream;

class HttpSendOutputStream extends FilterOutputStream
{
    HttpSendSocket owner;
    
    public HttpSendOutputStream(final OutputStream outputStream, final HttpSendSocket owner) throws IOException {
        super(outputStream);
        this.owner = owner;
    }
    
    public void deactivate() {
        this.out = null;
    }
    
    @Override
    public void write(final int n) throws IOException {
        if (this.out == null) {
            this.out = this.owner.writeNotify();
        }
        this.out.write(n);
    }
    
    @Override
    public void write(final byte[] array, final int n, final int n2) throws IOException {
        if (n2 == 0) {
            return;
        }
        if (this.out == null) {
            this.out = this.owner.writeNotify();
        }
        this.out.write(array, n, n2);
    }
    
    @Override
    public void flush() throws IOException {
        if (this.out != null) {
            this.out.flush();
        }
    }
    
    @Override
    public void close() throws IOException {
        this.flush();
        this.owner.close();
    }
}
