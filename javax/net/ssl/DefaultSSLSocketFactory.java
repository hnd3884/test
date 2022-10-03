package javax.net.ssl;

import java.net.InetAddress;
import java.io.IOException;
import java.net.SocketException;
import java.net.Socket;

class DefaultSSLSocketFactory extends SSLSocketFactory
{
    private Exception reason;
    
    DefaultSSLSocketFactory(final Exception reason) {
        this.reason = reason;
    }
    
    private Socket throwException() throws SocketException {
        throw (SocketException)new SocketException(this.reason.toString()).initCause(this.reason);
    }
    
    @Override
    public Socket createSocket() throws IOException {
        return this.throwException();
    }
    
    @Override
    public Socket createSocket(final String s, final int n) throws IOException {
        return this.throwException();
    }
    
    @Override
    public Socket createSocket(final Socket socket, final String s, final int n, final boolean b) throws IOException {
        return this.throwException();
    }
    
    @Override
    public Socket createSocket(final InetAddress inetAddress, final int n) throws IOException {
        return this.throwException();
    }
    
    @Override
    public Socket createSocket(final String s, final int n, final InetAddress inetAddress, final int n2) throws IOException {
        return this.throwException();
    }
    
    @Override
    public Socket createSocket(final InetAddress inetAddress, final int n, final InetAddress inetAddress2, final int n2) throws IOException {
        return this.throwException();
    }
    
    @Override
    public String[] getDefaultCipherSuites() {
        return new String[0];
    }
    
    @Override
    public String[] getSupportedCipherSuites() {
        return new String[0];
    }
}
