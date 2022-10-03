package sun.security.util;

import java.util.StringTokenizer;
import java.util.Locale;
import java.util.Optional;
import sun.security.ssl.SSLLogger;
import java.security.cert.CertificateParsingException;
import java.io.IOException;
import java.text.Normalizer;
import sun.security.x509.X500Name;
import javax.net.ssl.SNIHostName;
import java.util.Iterator;
import java.util.Collection;
import java.net.UnknownHostException;
import java.net.InetAddress;
import java.util.List;
import sun.net.util.IPAddressUtil;
import sun.security.ssl.Krb5Helper;
import java.security.Principal;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class HostnameChecker
{
    public static final byte TYPE_TLS = 1;
    private static final HostnameChecker INSTANCE_TLS;
    public static final byte TYPE_LDAP = 2;
    private static final HostnameChecker INSTANCE_LDAP;
    private static final int ALTNAME_DNS = 2;
    private static final int ALTNAME_IP = 7;
    private final byte checkType;
    
    private HostnameChecker(final byte checkType) {
        this.checkType = checkType;
    }
    
    public static HostnameChecker getInstance(final byte b) {
        if (b == 1) {
            return HostnameChecker.INSTANCE_TLS;
        }
        if (b == 2) {
            return HostnameChecker.INSTANCE_LDAP;
        }
        throw new IllegalArgumentException("Unknown check type: " + b);
    }
    
    public void match(final String s, final X509Certificate x509Certificate, final boolean b) throws CertificateException {
        if (s == null) {
            throw new CertificateException("Hostname or IP address is undefined.");
        }
        if (isIpAddress(s)) {
            matchIP(s, x509Certificate);
        }
        else {
            this.matchDNS(s, x509Certificate, b);
        }
    }
    
    public void match(final String s, final X509Certificate x509Certificate) throws CertificateException {
        this.match(s, x509Certificate, false);
    }
    
    public static boolean match(final String s, final Principal principal) {
        return s.equalsIgnoreCase(getServerName(principal));
    }
    
    public static String getServerName(final Principal principal) {
        return Krb5Helper.getPrincipalHostName(principal);
    }
    
    private static boolean isIpAddress(final String s) {
        return IPAddressUtil.isIPv4LiteralAddress(s) || IPAddressUtil.isIPv6LiteralAddress(s);
    }
    
    private static void matchIP(final String s, final X509Certificate x509Certificate) throws CertificateException {
        final Collection<List<?>> subjectAlternativeNames = x509Certificate.getSubjectAlternativeNames();
        if (subjectAlternativeNames == null) {
            throw new CertificateException("No subject alternative names present");
        }
        for (final List list : subjectAlternativeNames) {
            if ((int)list.get(0) == 7) {
                final String s2 = (String)list.get(1);
                if (s.equalsIgnoreCase(s2)) {
                    return;
                }
                try {
                    if (InetAddress.getByName(s).equals(InetAddress.getByName(s2))) {
                        return;
                    }
                    continue;
                }
                catch (final UnknownHostException ex) {}
                catch (final SecurityException ex2) {}
            }
        }
        throw new CertificateException("No subject alternative names matching IP address " + s + " found");
    }
    
    private void matchDNS(final String s, final X509Certificate x509Certificate, final boolean b) throws CertificateException {
        try {
            final SNIHostName sniHostName = new SNIHostName(s);
        }
        catch (final IllegalArgumentException ex) {
            throw new CertificateException("Illegal given domain name: " + s, ex);
        }
        final Collection<List<?>> subjectAlternativeNames = x509Certificate.getSubjectAlternativeNames();
        if (subjectAlternativeNames != null) {
            boolean b2 = false;
            for (final List list : subjectAlternativeNames) {
                if ((int)list.get(0) == 2) {
                    b2 = true;
                    if (this.isMatched(s, (String)list.get(1), b)) {
                        return;
                    }
                    continue;
                }
            }
            if (b2) {
                throw new CertificateException("No subject alternative DNS name matching " + s + " found.");
            }
        }
        final DerValue mostSpecificAttribute = getSubjectX500Name(x509Certificate).findMostSpecificAttribute(X500Name.commonName_oid);
        if (mostSpecificAttribute != null) {
            try {
                final String asString = mostSpecificAttribute.getAsString();
                if (!Normalizer.isNormalized(asString, Normalizer.Form.NFKC)) {
                    throw new CertificateException("Not a formal name " + asString);
                }
                if (this.isMatched(s, asString, b)) {
                    return;
                }
            }
            catch (final IOException ex2) {}
        }
        throw new CertificateException("No name matching " + s + " found");
    }
    
    public static X500Name getSubjectX500Name(final X509Certificate x509Certificate) throws CertificateParsingException {
        try {
            final Principal subjectDN = x509Certificate.getSubjectDN();
            if (subjectDN instanceof X500Name) {
                return (X500Name)subjectDN;
            }
            return new X500Name(x509Certificate.getSubjectX500Principal().getEncoded());
        }
        catch (final IOException ex) {
            throw (CertificateParsingException)new CertificateParsingException().initCause(ex);
        }
    }
    
    private boolean isMatched(final String s, final String s2, final boolean b) {
        if (hasIllegalWildcard(s, s2, b)) {
            return false;
        }
        try {
            final SNIHostName sniHostName = new SNIHostName(s2.replace('*', 'z'));
        }
        catch (final IllegalArgumentException ex) {
            return false;
        }
        if (this.checkType == 1) {
            return matchAllWildcards(s, s2);
        }
        return this.checkType == 2 && matchLeftmostWildcard(s, s2);
    }
    
    private static boolean hasIllegalWildcard(final String s, final String s2, final boolean b) {
        if (s2.equals("*") || s2.equals("*.")) {
            if (SSLLogger.isOn) {
                SSLLogger.fine("Certificate domain name has illegal single wildcard character: " + s2, new Object[0]);
            }
            return true;
        }
        final int lastIndex = s2.lastIndexOf("*");
        if (lastIndex == -1) {
            return false;
        }
        final String substring = s2.substring(lastIndex);
        final int index = substring.indexOf(".");
        if (index == -1) {
            if (SSLLogger.isOn) {
                SSLLogger.fine("Certificate domain name has illegal wildcard, no dot after wildcard character: " + s2, new Object[0]);
            }
            return true;
        }
        if (!b) {
            return false;
        }
        final Optional<RegisteredDomain> filter = RegisteredDomain.from(s).filter(registeredDomain -> registeredDomain.type() == RegisteredDomain.Type.ICANN);
        if (filter.isPresent() && filter.get().publicSuffix().equalsIgnoreCase(substring.substring(index + 1))) {
            if (SSLLogger.isOn) {
                SSLLogger.fine("Certificate domain name has illegal wildcard for public suffix: " + s2, new Object[0]);
            }
            return true;
        }
        return false;
    }
    
    private static boolean matchAllWildcards(String lowerCase, String lowerCase2) {
        lowerCase = lowerCase.toLowerCase(Locale.ENGLISH);
        lowerCase2 = lowerCase2.toLowerCase(Locale.ENGLISH);
        final StringTokenizer stringTokenizer = new StringTokenizer(lowerCase, ".");
        final StringTokenizer stringTokenizer2 = new StringTokenizer(lowerCase2, ".");
        if (stringTokenizer.countTokens() != stringTokenizer2.countTokens()) {
            return false;
        }
        while (stringTokenizer.hasMoreTokens()) {
            if (!matchWildCards(stringTokenizer.nextToken(), stringTokenizer2.nextToken())) {
                return false;
            }
        }
        return true;
    }
    
    private static boolean matchLeftmostWildcard(String lowerCase, String lowerCase2) {
        lowerCase = lowerCase.toLowerCase(Locale.ENGLISH);
        lowerCase2 = lowerCase2.toLowerCase(Locale.ENGLISH);
        int n = lowerCase2.indexOf(".");
        int n2 = lowerCase.indexOf(".");
        if (n == -1) {
            n = lowerCase2.length();
        }
        if (n2 == -1) {
            n2 = lowerCase.length();
        }
        return matchWildCards(lowerCase.substring(0, n2), lowerCase2.substring(0, n)) && lowerCase2.substring(n).equals(lowerCase.substring(n2));
    }
    
    private static boolean matchWildCards(String substring, final String s) {
        int i = s.indexOf("*");
        if (i == -1) {
            return substring.equals(s);
        }
        int n = 1;
        String substring2;
        for (substring2 = s; i != -1; i = substring2.indexOf("*")) {
            final String substring3 = substring2.substring(0, i);
            substring2 = substring2.substring(i + 1);
            final int index = substring.indexOf(substring3);
            if (index == -1 || (n != 0 && index != 0)) {
                return false;
            }
            n = 0;
            substring = substring.substring(index + substring3.length());
        }
        return substring.endsWith(substring2);
    }
    
    static {
        INSTANCE_TLS = new HostnameChecker((byte)1);
        INSTANCE_LDAP = new HostnameChecker((byte)2);
    }
}
