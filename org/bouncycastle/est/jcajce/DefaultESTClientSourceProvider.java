package org.bouncycastle.est.jcajce;

import java.util.Iterator;
import org.bouncycastle.util.Strings;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import javax.net.ssl.SSLSocket;
import org.bouncycastle.est.Source;
import java.security.GeneralSecurityException;
import java.util.Set;
import javax.net.ssl.SSLSocketFactory;
import org.bouncycastle.est.ESTClientSourceProvider;

class DefaultESTClientSourceProvider implements ESTClientSourceProvider
{
    private final SSLSocketFactory sslSocketFactory;
    private final JsseHostnameAuthorizer hostNameAuthorizer;
    private final int timeout;
    private final ChannelBindingProvider bindingProvider;
    private final Set<String> cipherSuites;
    private final Long absoluteLimit;
    private final boolean filterSupportedSuites;
    
    public DefaultESTClientSourceProvider(final SSLSocketFactory sslSocketFactory, final JsseHostnameAuthorizer hostNameAuthorizer, final int timeout, final ChannelBindingProvider bindingProvider, final Set<String> cipherSuites, final Long absoluteLimit, final boolean filterSupportedSuites) throws GeneralSecurityException {
        this.sslSocketFactory = sslSocketFactory;
        this.hostNameAuthorizer = hostNameAuthorizer;
        this.timeout = timeout;
        this.bindingProvider = bindingProvider;
        this.cipherSuites = cipherSuites;
        this.absoluteLimit = absoluteLimit;
        this.filterSupportedSuites = filterSupportedSuites;
    }
    
    public Source makeSource(final String s, final int n) throws IOException {
        final SSLSocket sslSocket = (SSLSocket)this.sslSocketFactory.createSocket(s, n);
        sslSocket.setSoTimeout(this.timeout);
        if (this.cipherSuites != null && !this.cipherSuites.isEmpty()) {
            if (this.filterSupportedSuites) {
                final HashSet set = new HashSet();
                final String[] supportedCipherSuites = sslSocket.getSupportedCipherSuites();
                for (int i = 0; i != supportedCipherSuites.length; ++i) {
                    set.add(supportedCipherSuites[i]);
                }
                final ArrayList list = new ArrayList();
                for (final String s2 : this.cipherSuites) {
                    if (set.contains(s2)) {
                        list.add(s2);
                    }
                }
                if (list.isEmpty()) {
                    throw new IllegalStateException("No supplied cipher suite is supported by the provider.");
                }
                sslSocket.setEnabledCipherSuites((String[])list.toArray(new String[list.size()]));
            }
            else {
                sslSocket.setEnabledCipherSuites(this.cipherSuites.toArray(new String[this.cipherSuites.size()]));
            }
        }
        sslSocket.startHandshake();
        if (this.hostNameAuthorizer != null && !this.hostNameAuthorizer.verified(s, sslSocket.getSession())) {
            throw new IOException("Host name could not be verified.");
        }
        final String lowerCase = Strings.toLowerCase(sslSocket.getSession().getCipherSuite());
        if (lowerCase.contains("_des_") || lowerCase.contains("_des40_") || lowerCase.contains("_3des_")) {
            throw new IOException("EST clients must not use DES ciphers");
        }
        if (Strings.toLowerCase(sslSocket.getSession().getCipherSuite()).contains("null")) {
            throw new IOException("EST clients must not use NULL ciphers");
        }
        if (Strings.toLowerCase(sslSocket.getSession().getCipherSuite()).contains("anon")) {
            throw new IOException("EST clients must not use anon ciphers");
        }
        if (Strings.toLowerCase(sslSocket.getSession().getCipherSuite()).contains("export")) {
            throw new IOException("EST clients must not use export ciphers");
        }
        if (sslSocket.getSession().getProtocol().equalsIgnoreCase("tlsv1")) {
            try {
                sslSocket.close();
            }
            catch (final Exception ex) {}
            throw new IOException("EST clients must not use TLSv1");
        }
        if (this.hostNameAuthorizer != null && !this.hostNameAuthorizer.verified(s, sslSocket.getSession())) {
            throw new IOException("Hostname was not verified: " + s);
        }
        return new LimitedSSLSocketSource(sslSocket, this.bindingProvider, this.absoluteLimit);
    }
}
