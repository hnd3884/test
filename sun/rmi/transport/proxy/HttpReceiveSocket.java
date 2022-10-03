package sun.rmi.transport.proxy;

import java.net.SocketException;
import java.io.DataOutputStream;
import java.net.InetAddress;
import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;
import java.net.Socket;

public class HttpReceiveSocket extends WrappedSocket implements RMISocketInfo
{
    private boolean headerSent;
    
    public HttpReceiveSocket(final Socket socket, final InputStream inputStream, final OutputStream outputStream) throws IOException {
        super(socket, inputStream, outputStream);
        this.headerSent = false;
        this.in = new HttpInputStream((inputStream != null) ? inputStream : socket.getInputStream());
        this.out = ((outputStream != null) ? outputStream : socket.getOutputStream());
    }
    
    @Override
    public boolean isReusable() {
        return false;
    }
    
    @Override
    public InetAddress getInetAddress() {
        return null;
    }
    
    @Override
    public OutputStream getOutputStream() throws IOException {
        if (!this.headerSent) {
            final DataOutputStream dataOutputStream = new DataOutputStream(this.out);
            dataOutputStream.writeBytes("HTTP/1.0 200 OK\r\n");
            dataOutputStream.flush();
            this.headerSent = true;
            this.out = new HttpOutputStream(this.out);
        }
        return this.out;
    }
    
    @Override
    public synchronized void close() throws IOException {
        this.getOutputStream().close();
        this.socket.close();
    }
    
    @Override
    public String toString() {
        return "HttpReceive" + this.socket.toString();
    }
}
