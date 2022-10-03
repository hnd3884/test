package javax.rmi.ssl;

import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.util.StringTokenizer;
import javax.net.ssl.SSLSocket;
import java.net.Socket;
import javax.net.SocketFactory;
import java.io.Serializable;
import java.rmi.server.RMIClientSocketFactory;

public class SslRMIClientSocketFactory implements RMIClientSocketFactory, Serializable
{
    private static SocketFactory defaultSocketFactory;
    private static final long serialVersionUID = -8310631444933958385L;
    
    @Override
    public Socket createSocket(final String s, final int n) throws IOException {
        final SSLSocket sslSocket = (SSLSocket)getDefaultClientSocketFactory().createSocket(s, n);
        final String property = System.getProperty("javax.rmi.ssl.client.enabledCipherSuites");
        if (property != null) {
            final StringTokenizer stringTokenizer = new StringTokenizer(property, ",");
            final int countTokens = stringTokenizer.countTokens();
            final String[] enabledCipherSuites = new String[countTokens];
            for (int i = 0; i < countTokens; ++i) {
                enabledCipherSuites[i] = stringTokenizer.nextToken();
            }
            try {
                sslSocket.setEnabledCipherSuites(enabledCipherSuites);
            }
            catch (final IllegalArgumentException ex) {
                throw (IOException)new IOException(ex.getMessage()).initCause(ex);
            }
        }
        final String property2 = System.getProperty("javax.rmi.ssl.client.enabledProtocols");
        if (property2 != null) {
            final StringTokenizer stringTokenizer2 = new StringTokenizer(property2, ",");
            final int countTokens2 = stringTokenizer2.countTokens();
            final String[] enabledProtocols = new String[countTokens2];
            for (int j = 0; j < countTokens2; ++j) {
                enabledProtocols[j] = stringTokenizer2.nextToken();
            }
            try {
                sslSocket.setEnabledProtocols(enabledProtocols);
            }
            catch (final IllegalArgumentException ex2) {
                throw (IOException)new IOException(ex2.getMessage()).initCause(ex2);
            }
        }
        return sslSocket;
    }
    
    @Override
    public boolean equals(final Object o) {
        return o != null && (o == this || this.getClass().equals(o.getClass()));
    }
    
    @Override
    public int hashCode() {
        return this.getClass().hashCode();
    }
    
    private static synchronized SocketFactory getDefaultClientSocketFactory() {
        if (SslRMIClientSocketFactory.defaultSocketFactory == null) {
            SslRMIClientSocketFactory.defaultSocketFactory = SSLSocketFactory.getDefault();
        }
        return SslRMIClientSocketFactory.defaultSocketFactory;
    }
    
    static {
        SslRMIClientSocketFactory.defaultSocketFactory = null;
    }
}
