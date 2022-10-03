package mdm.integrations.certificateauthority.digicert.v1;

import org.apache.commons.httpclient.params.HttpConnectionParams;
import java.io.IOException;
import java.net.Socket;
import java.net.InetAddress;
import javax.net.ssl.SSLContext;
import org.apache.commons.httpclient.protocol.SSLProtocolSocketFactory;

public class CustomSocketFactory extends SSLProtocolSocketFactory
{
    private final SSLContext sslContext;
    
    public CustomSocketFactory(final SSLContext sslContext) {
        this.sslContext = sslContext;
    }
    
    public Socket createSocket(final String host, final int port, final InetAddress clientHost, final int clientPort) throws IOException {
        return this.sslContext.getSocketFactory().createSocket(host, port, clientHost, clientPort);
    }
    
    public Socket createSocket(final String host, final int port, final InetAddress clientHost, final int clientPort, final HttpConnectionParams httpConnectionParams) throws IOException {
        return this.sslContext.getSocketFactory().createSocket(host, port, clientHost, clientPort);
    }
    
    public Socket createSocket(final String host, final int port) throws IOException {
        return this.sslContext.getSocketFactory().createSocket(host, port);
    }
    
    public Socket createSocket(final Socket socket, final String host, final int port, final boolean autoClose) throws IOException {
        return this.sslContext.getSocketFactory().createSocket(socket, host, port, autoClose);
    }
}
