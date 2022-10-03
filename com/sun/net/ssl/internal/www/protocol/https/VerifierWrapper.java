package com.sun.net.ssl.internal.www.protocol.https;

import sun.security.util.DerValue;
import java.util.Iterator;
import java.util.Collection;
import java.security.cert.CertificateException;
import java.io.IOException;
import sun.security.x509.X500Name;
import java.util.List;
import java.security.Principal;
import java.security.cert.Certificate;
import javax.net.ssl.SSLPeerUnverifiedException;
import java.security.cert.X509Certificate;
import sun.security.util.HostnameChecker;
import javax.net.ssl.SSLSession;
import javax.net.ssl.HostnameVerifier;

class VerifierWrapper implements HostnameVerifier
{
    private com.sun.net.ssl.HostnameVerifier verifier;
    
    VerifierWrapper(final com.sun.net.ssl.HostnameVerifier verifier) {
        this.verifier = verifier;
    }
    
    @Override
    public boolean verify(final String s, final SSLSession sslSession) {
        try {
            String s2;
            if (sslSession.getCipherSuite().startsWith("TLS_KRB5")) {
                s2 = HostnameChecker.getServerName(this.getPeerPrincipal(sslSession));
            }
            else {
                final Certificate[] peerCertificates = sslSession.getPeerCertificates();
                if (peerCertificates == null || peerCertificates.length == 0) {
                    return false;
                }
                if (!(peerCertificates[0] instanceof X509Certificate)) {
                    return false;
                }
                s2 = getServername((X509Certificate)peerCertificates[0]);
            }
            return s2 != null && this.verifier.verify(s, s2);
        }
        catch (final SSLPeerUnverifiedException ex) {
            return false;
        }
    }
    
    private Principal getPeerPrincipal(final SSLSession sslSession) throws SSLPeerUnverifiedException {
        Principal peerPrincipal;
        try {
            peerPrincipal = sslSession.getPeerPrincipal();
        }
        catch (final AbstractMethodError abstractMethodError) {
            peerPrincipal = null;
        }
        return peerPrincipal;
    }
    
    private static String getServername(final X509Certificate x509Certificate) {
        try {
            final Collection<List<?>> subjectAlternativeNames = x509Certificate.getSubjectAlternativeNames();
            if (subjectAlternativeNames != null) {
                for (final List list : subjectAlternativeNames) {
                    if ((int)list.get(0) == 2) {
                        return (String)list.get(1);
                    }
                }
            }
            final DerValue mostSpecificAttribute = HostnameChecker.getSubjectX500Name(x509Certificate).findMostSpecificAttribute(X500Name.commonName_oid);
            if (mostSpecificAttribute != null) {
                try {
                    return mostSpecificAttribute.getAsString();
                }
                catch (final IOException ex) {}
            }
        }
        catch (final CertificateException ex2) {}
        return null;
    }
}
