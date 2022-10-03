package sun.rmi.transport.proxy;

import java.io.OutputStream;
import java.io.InputStream;
import sun.rmi.runtime.Log;
import java.io.BufferedInputStream;
import java.net.Socket;
import java.io.IOException;
import java.net.ServerSocket;

class HttpAwareServerSocket extends ServerSocket
{
    public HttpAwareServerSocket(final int n) throws IOException {
        super(n);
    }
    
    public HttpAwareServerSocket(final int n, final int n2) throws IOException {
        super(n, n2);
    }
    
    @Override
    public Socket accept() throws IOException {
        final Socket accept = super.accept();
        final BufferedInputStream bufferedInputStream = new BufferedInputStream(accept.getInputStream());
        RMIMasterSocketFactory.proxyLog.log(Log.BRIEF, "socket accepted (checking for POST)");
        bufferedInputStream.mark(4);
        final boolean b = bufferedInputStream.read() == 80 && bufferedInputStream.read() == 79 && bufferedInputStream.read() == 83 && bufferedInputStream.read() == 84;
        bufferedInputStream.reset();
        if (RMIMasterSocketFactory.proxyLog.isLoggable(Log.BRIEF)) {
            RMIMasterSocketFactory.proxyLog.log(Log.BRIEF, b ? "POST found, HTTP socket returned" : "POST not found, direct socket returned");
        }
        if (b) {
            return new HttpReceiveSocket(accept, bufferedInputStream, null);
        }
        return new WrappedSocket(accept, bufferedInputStream, null);
    }
    
    @Override
    public String toString() {
        return "HttpAware" + super.toString();
    }
}
