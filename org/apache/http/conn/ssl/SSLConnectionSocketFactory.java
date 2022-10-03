package org.apache.http.conn.ssl;

import java.util.Collections;
import java.util.Collection;
import javax.security.auth.x500.X500Principal;
import java.security.cert.Certificate;
import java.io.InputStream;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLPeerUnverifiedException;
import java.security.cert.X509Certificate;
import javax.net.ssl.SSLHandshakeException;
import java.util.Arrays;
import java.util.ArrayList;
import java.net.SocketAddress;
import java.net.InetSocketAddress;
import org.apache.http.HttpHost;
import javax.net.SocketFactory;
import java.net.Socket;
import org.apache.http.protocol.HttpContext;
import java.io.IOException;
import javax.net.ssl.SSLSocket;
import org.apache.commons.logging.LogFactory;
import org.apache.http.util.Args;
import javax.net.ssl.SSLContext;
import org.apache.http.util.TextUtils;
import java.util.Iterator;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.conn.util.PublicSuffixMatcherLoader;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;
import org.apache.commons.logging.Log;
import java.util.regex.Pattern;
import java.util.List;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.annotation.Contract;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;

@Contract(threading = ThreadingBehavior.SAFE)
public class SSLConnectionSocketFactory implements LayeredConnectionSocketFactory
{
    public static final String TLS = "TLS";
    public static final String SSL = "SSL";
    public static final String SSLV2 = "SSLv2";
    @Deprecated
    public static final X509HostnameVerifier ALLOW_ALL_HOSTNAME_VERIFIER;
    @Deprecated
    public static final X509HostnameVerifier BROWSER_COMPATIBLE_HOSTNAME_VERIFIER;
    @Deprecated
    public static final X509HostnameVerifier STRICT_HOSTNAME_VERIFIER;
    private static final String WEAK_KEY_EXCHANGES = "^(TLS|SSL)_(NULL|ECDH_anon|DH_anon|DH_anon_EXPORT|DHE_RSA_EXPORT|DHE_DSS_EXPORT|DSS_EXPORT|DH_DSS_EXPORT|DH_RSA_EXPORT|RSA_EXPORT|KRB5_EXPORT)_(.*)";
    private static final String WEAK_CIPHERS = "^(TLS|SSL)_(.*)_WITH_(NULL|DES_CBC|DES40_CBC|DES_CBC_40|3DES_EDE_CBC|RC4_128|RC4_40|RC2_CBC_40)_(.*)";
    private static final List<Pattern> WEAK_CIPHER_SUITE_PATTERNS;
    private final Log log;
    private final SSLSocketFactory socketfactory;
    private final HostnameVerifier hostnameVerifier;
    private final String[] supportedProtocols;
    private final String[] supportedCipherSuites;
    
    public static HostnameVerifier getDefaultHostnameVerifier() {
        return new DefaultHostnameVerifier(PublicSuffixMatcherLoader.getDefault());
    }
    
    public static SSLConnectionSocketFactory getSocketFactory() throws SSLInitializationException {
        return new SSLConnectionSocketFactory(SSLContexts.createDefault(), getDefaultHostnameVerifier());
    }
    
    static boolean isWeakCipherSuite(final String cipherSuite) {
        for (final Pattern pattern : SSLConnectionSocketFactory.WEAK_CIPHER_SUITE_PATTERNS) {
            if (pattern.matcher(cipherSuite).matches()) {
                return true;
            }
        }
        return false;
    }
    
    private static String[] split(final String s) {
        if (TextUtils.isBlank((CharSequence)s)) {
            return null;
        }
        return s.split(" *, *");
    }
    
    public static SSLConnectionSocketFactory getSystemSocketFactory() throws SSLInitializationException {
        return new SSLConnectionSocketFactory((SSLSocketFactory)SSLSocketFactory.getDefault(), split(System.getProperty("https.protocols")), split(System.getProperty("https.cipherSuites")), getDefaultHostnameVerifier());
    }
    
    public SSLConnectionSocketFactory(final SSLContext sslContext) {
        this(sslContext, getDefaultHostnameVerifier());
    }
    
    @Deprecated
    public SSLConnectionSocketFactory(final SSLContext sslContext, final X509HostnameVerifier hostnameVerifier) {
        this(((SSLContext)Args.notNull((Object)sslContext, "SSL context")).getSocketFactory(), null, null, hostnameVerifier);
    }
    
    @Deprecated
    public SSLConnectionSocketFactory(final SSLContext sslContext, final String[] supportedProtocols, final String[] supportedCipherSuites, final X509HostnameVerifier hostnameVerifier) {
        this(((SSLContext)Args.notNull((Object)sslContext, "SSL context")).getSocketFactory(), supportedProtocols, supportedCipherSuites, hostnameVerifier);
    }
    
    @Deprecated
    public SSLConnectionSocketFactory(final SSLSocketFactory socketfactory, final X509HostnameVerifier hostnameVerifier) {
        this(socketfactory, null, null, hostnameVerifier);
    }
    
    @Deprecated
    public SSLConnectionSocketFactory(final SSLSocketFactory socketfactory, final String[] supportedProtocols, final String[] supportedCipherSuites, final X509HostnameVerifier hostnameVerifier) {
        this(socketfactory, supportedProtocols, supportedCipherSuites, (HostnameVerifier)hostnameVerifier);
    }
    
    public SSLConnectionSocketFactory(final SSLContext sslContext, final HostnameVerifier hostnameVerifier) {
        this(((SSLContext)Args.notNull((Object)sslContext, "SSL context")).getSocketFactory(), null, null, hostnameVerifier);
    }
    
    public SSLConnectionSocketFactory(final SSLContext sslContext, final String[] supportedProtocols, final String[] supportedCipherSuites, final HostnameVerifier hostnameVerifier) {
        this(((SSLContext)Args.notNull((Object)sslContext, "SSL context")).getSocketFactory(), supportedProtocols, supportedCipherSuites, hostnameVerifier);
    }
    
    public SSLConnectionSocketFactory(final SSLSocketFactory socketfactory, final HostnameVerifier hostnameVerifier) {
        this(socketfactory, null, null, hostnameVerifier);
    }
    
    public SSLConnectionSocketFactory(final SSLSocketFactory socketfactory, final String[] supportedProtocols, final String[] supportedCipherSuites, final HostnameVerifier hostnameVerifier) {
        this.log = LogFactory.getLog((Class)this.getClass());
        this.socketfactory = (SSLSocketFactory)Args.notNull((Object)socketfactory, "SSL socket factory");
        this.supportedProtocols = supportedProtocols;
        this.supportedCipherSuites = supportedCipherSuites;
        this.hostnameVerifier = ((hostnameVerifier != null) ? hostnameVerifier : getDefaultHostnameVerifier());
    }
    
    protected void prepareSocket(final SSLSocket socket) throws IOException {
    }
    
    @Override
    public Socket createSocket(final HttpContext context) throws IOException {
        return SocketFactory.getDefault().createSocket();
    }
    
    @Override
    public Socket connectSocket(final int connectTimeout, final Socket socket, final HttpHost host, final InetSocketAddress remoteAddress, final InetSocketAddress localAddress, final HttpContext context) throws IOException {
        Args.notNull((Object)host, "HTTP host");
        Args.notNull((Object)remoteAddress, "Remote address");
        final Socket sock = (socket != null) ? socket : this.createSocket(context);
        if (localAddress != null) {
            sock.bind(localAddress);
        }
        try {
            if (connectTimeout > 0 && sock.getSoTimeout() == 0) {
                sock.setSoTimeout(connectTimeout);
            }
            if (this.log.isDebugEnabled()) {
                this.log.debug((Object)("Connecting socket to " + remoteAddress + " with timeout " + connectTimeout));
            }
            sock.connect(remoteAddress, connectTimeout);
        }
        catch (final IOException ex) {
            try {
                sock.close();
            }
            catch (final IOException ex2) {}
            throw ex;
        }
        if (sock instanceof SSLSocket) {
            final SSLSocket sslsock = (SSLSocket)sock;
            this.log.debug((Object)"Starting handshake");
            sslsock.startHandshake();
            this.verifyHostname(sslsock, host.getHostName());
            return sock;
        }
        return this.createLayeredSocket(sock, host.getHostName(), remoteAddress.getPort(), context);
    }
    
    @Override
    public Socket createLayeredSocket(final Socket socket, final String target, final int port, final HttpContext context) throws IOException {
        final SSLSocket sslsock = (SSLSocket)this.socketfactory.createSocket(socket, target, port, true);
        if (this.supportedProtocols != null) {
            sslsock.setEnabledProtocols(this.supportedProtocols);
        }
        else {
            final String[] allProtocols = sslsock.getEnabledProtocols();
            final List<String> enabledProtocols = new ArrayList<String>(allProtocols.length);
            for (final String protocol : allProtocols) {
                if (!protocol.startsWith("SSL")) {
                    enabledProtocols.add(protocol);
                }
            }
            if (!enabledProtocols.isEmpty()) {
                sslsock.setEnabledProtocols(enabledProtocols.toArray(new String[enabledProtocols.size()]));
            }
        }
        if (this.supportedCipherSuites != null) {
            sslsock.setEnabledCipherSuites(this.supportedCipherSuites);
        }
        else {
            final String[] allCipherSuites = sslsock.getEnabledCipherSuites();
            final List<String> enabledCipherSuites = new ArrayList<String>(allCipherSuites.length);
            for (final String cipherSuite : allCipherSuites) {
                if (!isWeakCipherSuite(cipherSuite)) {
                    enabledCipherSuites.add(cipherSuite);
                }
            }
            if (!enabledCipherSuites.isEmpty()) {
                sslsock.setEnabledCipherSuites(enabledCipherSuites.toArray(new String[enabledCipherSuites.size()]));
            }
        }
        if (this.log.isDebugEnabled()) {
            this.log.debug((Object)("Enabled protocols: " + Arrays.asList(sslsock.getEnabledProtocols())));
            this.log.debug((Object)("Enabled cipher suites:" + Arrays.asList(sslsock.getEnabledCipherSuites())));
        }
        this.prepareSocket(sslsock);
        this.log.debug((Object)"Starting handshake");
        sslsock.startHandshake();
        this.verifyHostname(sslsock, target);
        return sslsock;
    }
    
    private void verifyHostname(final SSLSocket sslsock, final String hostname) throws IOException {
        try {
            SSLSession session = sslsock.getSession();
            if (session == null) {
                final InputStream in = sslsock.getInputStream();
                in.available();
                session = sslsock.getSession();
                if (session == null) {
                    sslsock.startHandshake();
                    session = sslsock.getSession();
                }
            }
            if (session == null) {
                throw new SSLHandshakeException("SSL session not available");
            }
            if (this.log.isDebugEnabled()) {
                this.log.debug((Object)"Secure session established");
                this.log.debug((Object)(" negotiated protocol: " + session.getProtocol()));
                this.log.debug((Object)(" negotiated cipher suite: " + session.getCipherSuite()));
                try {
                    final Certificate[] certs = session.getPeerCertificates();
                    final X509Certificate x509 = (X509Certificate)certs[0];
                    final X500Principal peer = x509.getSubjectX500Principal();
                    this.log.debug((Object)(" peer principal: " + peer.toString()));
                    final Collection<List<?>> altNames1 = x509.getSubjectAlternativeNames();
                    if (altNames1 != null) {
                        final List<String> altNames2 = new ArrayList<String>();
                        for (final List<?> aC : altNames1) {
                            if (!aC.isEmpty()) {
                                altNames2.add((String)aC.get(1));
                            }
                        }
                        this.log.debug((Object)(" peer alternative names: " + altNames2));
                    }
                    final X500Principal issuer = x509.getIssuerX500Principal();
                    this.log.debug((Object)(" issuer principal: " + issuer.toString()));
                    final Collection<List<?>> altNames3 = x509.getIssuerAlternativeNames();
                    if (altNames3 != null) {
                        final List<String> altNames4 = new ArrayList<String>();
                        for (final List<?> aC2 : altNames3) {
                            if (!aC2.isEmpty()) {
                                altNames4.add((String)aC2.get(1));
                            }
                        }
                        this.log.debug((Object)(" issuer alternative names: " + altNames4));
                    }
                }
                catch (final Exception ex) {}
            }
            if (!this.hostnameVerifier.verify(hostname, session)) {
                final Certificate[] certs = session.getPeerCertificates();
                final X509Certificate x509 = (X509Certificate)certs[0];
                final List<SubjectName> subjectAlts = DefaultHostnameVerifier.getSubjectAltNames(x509);
                throw new SSLPeerUnverifiedException("Certificate for <" + hostname + "> doesn't match any " + "of the subject alternative names: " + subjectAlts);
            }
        }
        catch (final IOException iox) {
            try {
                sslsock.close();
            }
            catch (final Exception ex2) {}
            throw iox;
        }
    }
    
    static {
        ALLOW_ALL_HOSTNAME_VERIFIER = AllowAllHostnameVerifier.INSTANCE;
        BROWSER_COMPATIBLE_HOSTNAME_VERIFIER = BrowserCompatHostnameVerifier.INSTANCE;
        STRICT_HOSTNAME_VERIFIER = StrictHostnameVerifier.INSTANCE;
        WEAK_CIPHER_SUITE_PATTERNS = Collections.unmodifiableList((List<? extends Pattern>)Arrays.asList(Pattern.compile("^(TLS|SSL)_(NULL|ECDH_anon|DH_anon|DH_anon_EXPORT|DHE_RSA_EXPORT|DHE_DSS_EXPORT|DSS_EXPORT|DH_DSS_EXPORT|DH_RSA_EXPORT|RSA_EXPORT|KRB5_EXPORT)_(.*)", 2), Pattern.compile("^(TLS|SSL)_(.*)_WITH_(NULL|DES_CBC|DES40_CBC|DES_CBC_40|3DES_EDE_CBC|RC4_128|RC4_40|RC2_CBC_40)_(.*)", 2)));
    }
}
