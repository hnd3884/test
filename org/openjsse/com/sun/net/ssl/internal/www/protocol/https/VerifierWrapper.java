package org.openjsse.com.sun.net.ssl.internal.www.protocol.https;

import sun.security.util.DerValue;
import java.util.Iterator;
import java.util.Collection;
import java.security.cert.CertificateException;
import java.io.IOException;
import sun.security.x509.X500Name;
import sun.security.util.HostnameChecker;
import java.util.List;
import java.security.cert.Certificate;
import javax.net.ssl.SSLPeerUnverifiedException;
import java.security.cert.X509Certificate;
import javax.net.ssl.SSLSession;
import javax.net.ssl.HostnameVerifier;

class VerifierWrapper implements HostnameVerifier
{
    private org.openjsse.com.sun.net.ssl.HostnameVerifier verifier;
    
    VerifierWrapper(final org.openjsse.com.sun.net.ssl.HostnameVerifier verifier) {
        this.verifier = verifier;
    }
    
    @Override
    public boolean verify(final String hostname, final SSLSession session) {
        try {
            final Certificate[] serverChain = session.getPeerCertificates();
            if (serverChain == null || serverChain.length == 0) {
                return false;
            }
            if (!(serverChain[0] instanceof X509Certificate)) {
                return false;
            }
            final X509Certificate serverCert = (X509Certificate)serverChain[0];
            final String serverName = getServername(serverCert);
            return serverName != null && this.verifier.verify(hostname, serverName);
        }
        catch (final SSLPeerUnverifiedException e) {
            return false;
        }
    }
    
    private static String getServername(final X509Certificate peerCert) {
        try {
            final Collection<List<?>> subjAltNames = peerCert.getSubjectAlternativeNames();
            if (subjAltNames != null) {
                for (final List<?> next : subjAltNames) {
                    if ((int)next.get(0) == 2) {
                        final String dnsName = (String)next.get(1);
                        return dnsName;
                    }
                }
            }
            final X500Name subject = HostnameChecker.getSubjectX500Name(peerCert);
            final DerValue derValue = subject.findMostSpecificAttribute(X500Name.commonName_oid);
            if (derValue != null) {
                try {
                    final String name = derValue.getAsString();
                    return name;
                }
                catch (final IOException ex) {}
            }
        }
        catch (final CertificateException ex2) {}
        return null;
    }
}
