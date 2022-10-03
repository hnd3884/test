package javax.net.ssl;

import java.net.InetAddress;
import java.io.IOException;
import java.net.SocketException;
import java.net.ServerSocket;

class DefaultSSLServerSocketFactory extends SSLServerSocketFactory
{
    private final Exception reason;
    
    DefaultSSLServerSocketFactory(final Exception reason) {
        this.reason = reason;
    }
    
    private ServerSocket throwException() throws SocketException {
        throw (SocketException)new SocketException(this.reason.toString()).initCause(this.reason);
    }
    
    @Override
    public ServerSocket createServerSocket() throws IOException {
        return this.throwException();
    }
    
    @Override
    public ServerSocket createServerSocket(final int n) throws IOException {
        return this.throwException();
    }
    
    @Override
    public ServerSocket createServerSocket(final int n, final int n2) throws IOException {
        return this.throwException();
    }
    
    @Override
    public ServerSocket createServerSocket(final int n, final int n2, final InetAddress inetAddress) throws IOException {
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
