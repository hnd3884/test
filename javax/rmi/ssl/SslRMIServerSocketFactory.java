package javax.rmi.ssl;

import java.io.IOException;
import java.net.Socket;
import java.net.ServerSocket;
import java.util.Arrays;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLContext;
import java.util.List;
import javax.net.ssl.SSLSocketFactory;
import java.rmi.server.RMIServerSocketFactory;

public class SslRMIServerSocketFactory implements RMIServerSocketFactory
{
    private static SSLSocketFactory defaultSSLSocketFactory;
    private final String[] enabledCipherSuites;
    private final String[] enabledProtocols;
    private final boolean needClientAuth;
    private List<String> enabledCipherSuitesList;
    private List<String> enabledProtocolsList;
    private SSLContext context;
    
    public SslRMIServerSocketFactory() {
        this(null, null, false);
    }
    
    public SslRMIServerSocketFactory(final String[] array, final String[] array2, final boolean b) throws IllegalArgumentException {
        this(null, array, array2, b);
    }
    
    public SslRMIServerSocketFactory(final SSLContext context, final String[] array, final String[] array2, final boolean needClientAuth) throws IllegalArgumentException {
        this.enabledCipherSuites = (String[])((array == null) ? null : ((String[])array.clone()));
        this.enabledProtocols = (String[])((array2 == null) ? null : ((String[])array2.clone()));
        this.needClientAuth = needClientAuth;
        this.context = context;
        final SSLSocketFactory sslSocketFactory = (context == null) ? getDefaultSSLSocketFactory() : context.getSocketFactory();
        SSLSocket sslSocket = null;
        Label_0119: {
            if (this.enabledCipherSuites == null) {
                if (this.enabledProtocols == null) {
                    break Label_0119;
                }
            }
            try {
                sslSocket = (SSLSocket)sslSocketFactory.createSocket();
            }
            catch (final Exception ex) {
                throw (IllegalArgumentException)new IllegalArgumentException("Unable to check if the cipher suites and protocols to enable are supported").initCause(ex);
            }
        }
        if (this.enabledCipherSuites != null) {
            sslSocket.setEnabledCipherSuites(this.enabledCipherSuites);
            this.enabledCipherSuitesList = Arrays.asList(this.enabledCipherSuites);
        }
        if (this.enabledProtocols != null) {
            sslSocket.setEnabledProtocols(this.enabledProtocols);
            this.enabledProtocolsList = Arrays.asList(this.enabledProtocols);
        }
    }
    
    public final String[] getEnabledCipherSuites() {
        return (String[])((this.enabledCipherSuites == null) ? null : ((String[])this.enabledCipherSuites.clone()));
    }
    
    public final String[] getEnabledProtocols() {
        return (String[])((this.enabledProtocols == null) ? null : ((String[])this.enabledProtocols.clone()));
    }
    
    public final boolean getNeedClientAuth() {
        return this.needClientAuth;
    }
    
    @Override
    public ServerSocket createServerSocket(final int n) throws IOException {
        return new ServerSocket(n) {
            final /* synthetic */ SSLSocketFactory val$sslSocketFactory = (SslRMIServerSocketFactory.this.context == null) ? getDefaultSSLSocketFactory() : SslRMIServerSocketFactory.this.context.getSocketFactory();
            
            @Override
            public Socket accept() throws IOException {
                final Socket accept = super.accept();
                final SSLSocket sslSocket = (SSLSocket)this.val$sslSocketFactory.createSocket(accept, accept.getInetAddress().getHostName(), accept.getPort(), true);
                sslSocket.setUseClientMode(false);
                if (SslRMIServerSocketFactory.this.enabledCipherSuites != null) {
                    sslSocket.setEnabledCipherSuites(SslRMIServerSocketFactory.this.enabledCipherSuites);
                }
                if (SslRMIServerSocketFactory.this.enabledProtocols != null) {
                    sslSocket.setEnabledProtocols(SslRMIServerSocketFactory.this.enabledProtocols);
                }
                sslSocket.setNeedClientAuth(SslRMIServerSocketFactory.this.needClientAuth);
                return sslSocket;
            }
        };
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == null) {
            return false;
        }
        if (o == this) {
            return true;
        }
        if (!(o instanceof SslRMIServerSocketFactory)) {
            return false;
        }
        final SslRMIServerSocketFactory sslRMIServerSocketFactory = (SslRMIServerSocketFactory)o;
        return this.getClass().equals(sslRMIServerSocketFactory.getClass()) && this.checkParameters(sslRMIServerSocketFactory);
    }
    
    private boolean checkParameters(final SslRMIServerSocketFactory sslRMIServerSocketFactory) {
        if (this.context == null) {
            if (sslRMIServerSocketFactory.context == null) {
                return this.needClientAuth == sslRMIServerSocketFactory.needClientAuth && (this.enabledCipherSuites != null || sslRMIServerSocketFactory.enabledCipherSuites == null) && (this.enabledCipherSuites == null || sslRMIServerSocketFactory.enabledCipherSuites != null) && (this.enabledCipherSuites == null || sslRMIServerSocketFactory.enabledCipherSuites == null || this.enabledCipherSuitesList.equals(Arrays.asList(sslRMIServerSocketFactory.enabledCipherSuites))) && (this.enabledProtocols != null || sslRMIServerSocketFactory.enabledProtocols == null) && (this.enabledProtocols == null || sslRMIServerSocketFactory.enabledProtocols != null) && (this.enabledProtocols == null || sslRMIServerSocketFactory.enabledProtocols == null || this.enabledProtocolsList.equals(Arrays.asList(sslRMIServerSocketFactory.enabledProtocols)));
            }
        }
        else if (this.context.equals(sslRMIServerSocketFactory.context)) {
            return this.needClientAuth == sslRMIServerSocketFactory.needClientAuth && (this.enabledCipherSuites != null || sslRMIServerSocketFactory.enabledCipherSuites == null) && (this.enabledCipherSuites == null || sslRMIServerSocketFactory.enabledCipherSuites != null) && (this.enabledCipherSuites == null || sslRMIServerSocketFactory.enabledCipherSuites == null || this.enabledCipherSuitesList.equals(Arrays.asList(sslRMIServerSocketFactory.enabledCipherSuites))) && (this.enabledProtocols != null || sslRMIServerSocketFactory.enabledProtocols == null) && (this.enabledProtocols == null || sslRMIServerSocketFactory.enabledProtocols != null) && (this.enabledProtocols == null || sslRMIServerSocketFactory.enabledProtocols == null || this.enabledProtocolsList.equals(Arrays.asList(sslRMIServerSocketFactory.enabledProtocols)));
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return this.getClass().hashCode() + ((this.context == null) ? 0 : this.context.hashCode()) + (this.needClientAuth ? Boolean.TRUE.hashCode() : Boolean.FALSE.hashCode()) + ((this.enabledCipherSuites == null) ? 0 : this.enabledCipherSuitesList.hashCode()) + ((this.enabledProtocols == null) ? 0 : this.enabledProtocolsList.hashCode());
    }
    
    private static synchronized SSLSocketFactory getDefaultSSLSocketFactory() {
        if (SslRMIServerSocketFactory.defaultSSLSocketFactory == null) {
            SslRMIServerSocketFactory.defaultSSLSocketFactory = (SSLSocketFactory)SSLSocketFactory.getDefault();
        }
        return SslRMIServerSocketFactory.defaultSSLSocketFactory;
    }
    
    static {
        SslRMIServerSocketFactory.defaultSSLSocketFactory = null;
    }
}
