package sun.security.ssl;

import java.net.InetAddress;
import java.io.InputStream;
import java.net.UnknownHostException;
import java.io.IOException;
import java.net.Socket;
import javax.net.ssl.SSLSocketFactory;

public final class SSLSocketFactoryImpl extends SSLSocketFactory
{
    private final SSLContextImpl context;
    
    public SSLSocketFactoryImpl() throws Exception {
        this.context = SSLContextImpl.DefaultSSLContext.getDefaultImpl();
    }
    
    SSLSocketFactoryImpl(final SSLContextImpl context) {
        this.context = context;
    }
    
    @Override
    public Socket createSocket() {
        return new SSLSocketImpl(this.context);
    }
    
    @Override
    public Socket createSocket(final String s, final int n) throws IOException, UnknownHostException {
        return new SSLSocketImpl(this.context, s, n);
    }
    
    @Override
    public Socket createSocket(final Socket socket, final String s, final int n, final boolean b) throws IOException {
        return new SSLSocketImpl(this.context, socket, s, n, b);
    }
    
    @Override
    public Socket createSocket(final Socket socket, final InputStream inputStream, final boolean b) throws IOException {
        if (socket == null) {
            throw new NullPointerException("the existing socket cannot be null");
        }
        return new SSLSocketImpl(this.context, socket, inputStream, b);
    }
    
    @Override
    public Socket createSocket(final InetAddress inetAddress, final int n) throws IOException {
        return new SSLSocketImpl(this.context, inetAddress, n);
    }
    
    @Override
    public Socket createSocket(final String s, final int n, final InetAddress inetAddress, final int n2) throws IOException {
        return new SSLSocketImpl(this.context, s, n, inetAddress, n2);
    }
    
    @Override
    public Socket createSocket(final InetAddress inetAddress, final int n, final InetAddress inetAddress2, final int n2) throws IOException {
        return new SSLSocketImpl(this.context, inetAddress, n, inetAddress2, n2);
    }
    
    @Override
    public String[] getDefaultCipherSuites() {
        return CipherSuite.namesOf(this.context.getDefaultCipherSuites(false));
    }
    
    @Override
    public String[] getSupportedCipherSuites() {
        return CipherSuite.namesOf(this.context.getSupportedCipherSuites());
    }
}
