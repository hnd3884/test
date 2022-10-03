package com.unboundid.util.ssl;

import java.util.Iterator;
import java.util.Collection;
import com.unboundid.ldap.sdk.RDN;
import java.net.InetAddress;
import com.unboundid.ldap.sdk.LDAPConnectionOptions;
import java.net.URI;
import java.util.List;
import com.unboundid.ldap.sdk.DN;
import java.security.cert.Certificate;
import javax.net.ssl.SSLSession;
import com.unboundid.util.StaticUtils;
import com.unboundid.util.Debug;
import java.security.cert.X509Certificate;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import javax.net.ssl.SSLSocket;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class HostNameSSLSocketVerifier extends SSLSocketVerifier
{
    private final boolean allowWildcards;
    
    public HostNameSSLSocketVerifier(final boolean allowWildcards) {
        this.allowWildcards = allowWildcards;
    }
    
    @Override
    public void verifySSLSocket(final String host, final int port, final SSLSocket sslSocket) throws LDAPException {
        try {
            final SSLSession sslSession = sslSocket.getSession();
            if (sslSession == null) {
                throw new LDAPException(ResultCode.CONNECT_ERROR, SSLMessages.ERR_HOST_NAME_SSL_SOCKET_VERIFIER_NO_SESSION.get(host, port));
            }
            final Certificate[] peerCertificates = sslSession.getPeerCertificates();
            if (peerCertificates == null || peerCertificates.length == 0) {
                throw new LDAPException(ResultCode.CONNECT_ERROR, SSLMessages.ERR_HOST_NAME_SSL_SOCKET_VERIFIER_NO_PEER_CERTS.get(host, port));
            }
            if (!(peerCertificates[0] instanceof X509Certificate)) {
                throw new LDAPException(ResultCode.CONNECT_ERROR, SSLMessages.ERR_HOST_NAME_SSL_SOCKET_VERIFIER_PEER_NOT_X509.get(host, port, peerCertificates[0].getType()));
            }
            final StringBuilder certInfo = new StringBuilder();
            if (!certificateIncludesHostname(host, (X509Certificate)peerCertificates[0], this.allowWildcards, certInfo)) {
                throw new LDAPException(ResultCode.CONNECT_ERROR, SSLMessages.ERR_HOST_NAME_SSL_SOCKET_VERIFIER_HOSTNAME_NOT_FOUND.get(host, certInfo.toString()));
            }
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            throw le;
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new LDAPException(ResultCode.CONNECT_ERROR, SSLMessages.ERR_HOST_NAME_SSL_SOCKET_VERIFIER_EXCEPTION.get(host, port, StaticUtils.getExceptionMessage(e)), e);
        }
    }
    
    static boolean certificateIncludesHostname(final String host, final X509Certificate certificate, final boolean allowWildcards, final StringBuilder certInfo) {
        final String lowerHost = StaticUtils.toLowerCase(host);
        final String subjectDN = certificate.getSubjectX500Principal().getName("RFC2253");
        certInfo.append("subject='");
        certInfo.append(subjectDN);
        certInfo.append('\'');
        try {
            final DN dn = new DN(subjectDN);
            for (final RDN rdn : dn.getRDNs()) {
                final String[] names = rdn.getAttributeNames();
                final String[] values = rdn.getAttributeValues();
                for (int i = 0; i < names.length; ++i) {
                    final String lowerName = StaticUtils.toLowerCase(names[i]);
                    if (lowerName.equals("cn") || lowerName.equals("commonname") || lowerName.equals("2.5.4.3")) {
                        final String lowerValue = StaticUtils.toLowerCase(values[i]);
                        if (lowerHost.equals(lowerValue)) {
                            return true;
                        }
                        if (allowWildcards && lowerValue.startsWith("*.")) {
                            final String withoutWildcard = lowerValue.substring(1);
                            if (lowerHost.endsWith(withoutWildcard)) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        catch (final Exception e) {
            Debug.debugException(e);
        }
        Collection<List<?>> subjectAltNames;
        try {
            subjectAltNames = certificate.getSubjectAlternativeNames();
        }
        catch (final Exception e2) {
            Debug.debugException(e2);
            return false;
        }
        if (subjectAltNames != null) {
            for (final List<?> l : subjectAltNames) {
                try {
                    final Integer type = (Integer)l.get(0);
                    switch (type) {
                        case 2: {
                            final String dnsName = (String)l.get(1);
                            certInfo.append(" dNSName='");
                            certInfo.append(dnsName);
                            certInfo.append('\'');
                            final String lowerDNSName = StaticUtils.toLowerCase(dnsName);
                            if (lowerHost.equals(lowerDNSName)) {
                                return true;
                            }
                            if (!allowWildcards || !lowerDNSName.startsWith("*.")) {
                                continue;
                            }
                            final String withoutWildcard2 = lowerDNSName.substring(1);
                            if (lowerHost.endsWith(withoutWildcard2)) {
                                return true;
                            }
                            continue;
                        }
                        case 6: {
                            final String uriString = (String)l.get(1);
                            certInfo.append(" uniformResourceIdentifier='");
                            certInfo.append(uriString);
                            certInfo.append('\'');
                            final URI uri = new URI(uriString);
                            if (lowerHost.equals(StaticUtils.toLowerCase(uri.getHost()))) {
                                return true;
                            }
                            continue;
                        }
                        case 7: {
                            final String ipAddressString = (String)l.get(1);
                            certInfo.append(" iPAddress='");
                            certInfo.append(ipAddressString);
                            certInfo.append('\'');
                            final InetAddress inetAddress = LDAPConnectionOptions.DEFAULT_NAME_RESOLVER.getByName(ipAddressString);
                            if (!Character.isDigit(host.charAt(0)) && host.indexOf(58) < 0) {
                                continue;
                            }
                            final InetAddress a = InetAddress.getByName(host);
                            if (inetAddress.equals(a)) {
                                return true;
                            }
                            continue;
                        }
                    }
                }
                catch (final Exception e3) {
                    Debug.debugException(e3);
                }
            }
        }
        return false;
    }
}
