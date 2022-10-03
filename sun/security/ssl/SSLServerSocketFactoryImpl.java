package sun.security.ssl;

import java.net.InetAddress;
import java.io.IOException;
import java.net.ServerSocket;
import javax.net.ssl.SSLServerSocketFactory;

public final class SSLServerSocketFactoryImpl extends SSLServerSocketFactory
{
    private static final int DEFAULT_BACKLOG = 50;
    private final SSLContextImpl context;
    
    public SSLServerSocketFactoryImpl() throws Exception {
        this.context = SSLContextImpl.DefaultSSLContext.getDefaultImpl();
    }
    
    SSLServerSocketFactoryImpl(final SSLContextImpl context) {
        this.context = context;
    }
    
    @Override
    public ServerSocket createServerSocket() throws IOException {
        return new SSLServerSocketImpl(this.context);
    }
    
    @Override
    public ServerSocket createServerSocket(final int n) throws IOException {
        return new SSLServerSocketImpl(this.context, n, 50);
    }
    
    @Override
    public ServerSocket createServerSocket(final int n, final int n2) throws IOException {
        return new SSLServerSocketImpl(this.context, n, n2);
    }
    
    @Override
    public ServerSocket createServerSocket(final int n, final int n2, final InetAddress inetAddress) throws IOException {
        return new SSLServerSocketImpl(this.context, n, n2, inetAddress);
    }
    
    @Override
    public String[] getDefaultCipherSuites() {
        return CipherSuite.namesOf(this.context.getDefaultCipherSuites(true));
    }
    
    @Override
    public String[] getSupportedCipherSuites() {
        return CipherSuite.namesOf(this.context.getSupportedCipherSuites());
    }
}
