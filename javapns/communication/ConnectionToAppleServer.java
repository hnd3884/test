package javapns.communication;

import java.security.Provider;
import java.security.Security;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.LoggerFactory;
import javax.net.ssl.HandshakeCompletedEvent;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.io.IOException;
import java.net.Socket;
import javapns.communication.exceptions.CommunicationException;
import javax.net.ssl.SSLSocket;
import java.security.SecureRandom;
import javax.net.ssl.SSLContext;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManager;
import javapns.communication.exceptions.KeystoreException;
import javax.net.ssl.SSLSocketFactory;
import java.security.KeyStore;
import org.slf4j.Logger;

public abstract class ConnectionToAppleServer
{
    public static final String KEYSTORE_TYPE_PKCS12 = "PKCS12";
    public static final String KEYSTORE_TYPE_JKS = "JKS";
    private static final Logger logger;
    private static final String ALGORITHM;
    private static final String PROTOCOL = "TLS";
    private final AppleServer server;
    private KeyStore keyStore;
    private SSLSocketFactory socketFactory;
    
    protected ConnectionToAppleServer(final AppleServer server) throws KeystoreException {
        this.server = server;
        this.keyStore = KeystoreManager.loadKeystore(server);
    }
    
    protected ConnectionToAppleServer(final AppleServer server, final KeyStore keystore) {
        this.server = server;
        this.keyStore = keystore;
    }
    
    public AppleServer getServer() {
        return this.server;
    }
    
    private KeyStore getKeystore() {
        return this.keyStore;
    }
    
    public void setKeystore(final KeyStore ks) {
        this.keyStore = ks;
    }
    
    private SSLSocketFactory createSSLSocketFactoryWithTrustManagers(final TrustManager[] trustManagers) throws KeystoreException {
        ConnectionToAppleServer.logger.debug("Creating SSLSocketFactory");
        try {
            final KeyStore keystore = this.getKeystore();
            final KeyManagerFactory kmf = KeyManagerFactory.getInstance(ConnectionToAppleServer.ALGORITHM);
            try {
                final char[] password = KeystoreManager.getKeystorePasswordForSSL(this.server);
                kmf.init(keystore, password);
            }
            catch (Exception e) {
                e = KeystoreManager.wrapKeystoreException(e);
                throw e;
            }
            final SSLContext sslc = SSLContext.getInstance("TLS");
            sslc.init(kmf.getKeyManagers(), trustManagers, null);
            return sslc.getSocketFactory();
        }
        catch (final Exception e2) {
            throw new KeystoreException("Keystore exception: " + e2.getMessage(), e2);
        }
    }
    
    public abstract String getServerHost();
    
    protected abstract int getServerPort();
    
    private SSLSocketFactory createSSLSocketFactory() throws KeystoreException {
        return this.createSSLSocketFactoryWithTrustManagers(new TrustManager[] { new ServerTrustingTrustManager() });
    }
    
    private SSLSocketFactory getSSLSocketFactory() throws KeystoreException {
        if (this.socketFactory == null) {
            this.socketFactory = this.createSSLSocketFactory();
        }
        return this.socketFactory;
    }
    
    public SSLSocket getSSLSocket() throws KeystoreException, CommunicationException {
        final SSLSocketFactory socketFactory = this.getSSLSocketFactory();
        ConnectionToAppleServer.logger.debug("Creating SSLSocket to " + this.getServerHost() + ":" + this.getServerPort());
        try {
            if (ProxyManager.isUsingProxy(this.server)) {
                return this.tunnelThroughProxy(socketFactory);
            }
            return (SSLSocket)socketFactory.createSocket(this.getServerHost(), this.getServerPort());
        }
        catch (final Exception e) {
            throw new CommunicationException("Communication exception: " + e, e);
        }
    }
    
    private SSLSocket tunnelThroughProxy(final SSLSocketFactory socketFactory) throws IOException {
        final String tunnelHost = ProxyManager.getProxyHost(this.server);
        final Integer tunnelPort = ProxyManager.getProxyPort(this.server);
        final Socket tunnel = new Socket(tunnelHost, tunnelPort);
        this.doTunnelHandshake(tunnel, this.getServerHost(), this.getServerPort());
        final SSLSocket socket = (SSLSocket)socketFactory.createSocket(tunnel, this.getServerHost(), this.getServerPort(), true);
        socket.addHandshakeCompletedListener(event -> {
            ConnectionToAppleServer.logger.debug("Handshake finished!");
            ConnectionToAppleServer.logger.debug("\t CipherSuite:" + event.getCipherSuite());
            ConnectionToAppleServer.logger.debug("\t SessionId " + event.getSession());
            ConnectionToAppleServer.logger.debug("\t PeerHost " + event.getSession().getPeerHost());
            return;
        });
        return socket;
    }
    
    private void doTunnelHandshake(final Socket tunnel, final String host, final int port) throws IOException {
        final OutputStream out = tunnel.getOutputStream();
        final String msg = "CONNECT " + host + ":" + port + " HTTP/1.0\n" + "User-Agent: BoardPad Server" + "\r\n\r\n";
        byte[] b;
        try {
            b = msg.getBytes("ASCII7");
        }
        catch (final UnsupportedEncodingException ignored) {
            b = msg.getBytes();
        }
        out.write(b);
        out.flush();
        final byte[] reply = new byte[200];
        int replyLen = 0;
        int newlinesSeen = 0;
        boolean headerDone = false;
        final InputStream in = tunnel.getInputStream();
        while (newlinesSeen < 2) {
            final int i = in.read();
            if (i < 0) {
                throw new IOException("Unexpected EOF from proxy");
            }
            if (i == 10) {
                headerDone = true;
                ++newlinesSeen;
            }
            else {
                if (i == 13) {
                    continue;
                }
                newlinesSeen = 0;
                if (headerDone || replyLen >= reply.length) {
                    continue;
                }
                reply[replyLen++] = (byte)i;
            }
        }
        String replyStr;
        try {
            replyStr = new String(reply, 0, replyLen, "ASCII7");
        }
        catch (final UnsupportedEncodingException ignored2) {
            replyStr = new String(reply, 0, replyLen);
        }
        if (!replyStr.toLowerCase().contains("200 connection established")) {
            throw new IOException("Unable to tunnel through. Proxy returns \"" + replyStr + "\"");
        }
    }
    
    static {
        logger = LoggerFactory.getLogger((Class)ConnectionToAppleServer.class);
        ALGORITHM = KeyManagerFactory.getDefaultAlgorithm();
        Security.addProvider((Provider)new BouncyCastleProvider());
    }
}
