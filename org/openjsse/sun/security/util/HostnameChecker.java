package org.openjsse.sun.security.util;

import java.util.StringTokenizer;
import java.util.Locale;
import java.util.function.Function;
import org.openjsse.sun.security.ssl.SSLLogger;
import java.net.IDN;
import javax.security.auth.x500.X500Principal;
import java.security.Principal;
import java.security.cert.CertificateParsingException;
import sun.security.util.DerValue;
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
    
    public static HostnameChecker getInstance(final byte checkType) {
        if (checkType == 1) {
            return HostnameChecker.INSTANCE_TLS;
        }
        if (checkType == 2) {
            return HostnameChecker.INSTANCE_LDAP;
        }
        throw new IllegalArgumentException("Unknown check type: " + checkType);
    }
    
    public void match(final String expectedName, final X509Certificate cert, final boolean chainsToPublicCA) throws CertificateException {
        if (expectedName == null) {
            throw new CertificateException("Hostname or IP address is undefined.");
        }
        if (isIpAddress(expectedName)) {
            matchIP(expectedName, cert);
        }
        else {
            this.matchDNS(expectedName, cert, chainsToPublicCA);
        }
    }
    
    public void match(final String expectedName, final X509Certificate cert) throws CertificateException {
        this.match(expectedName, cert, false);
    }
    
    private static boolean isIpAddress(final String name) {
        return IPAddressUtil.isIPv4LiteralAddress(name) || IPAddressUtil.isIPv6LiteralAddress(name);
    }
    
    private static void matchIP(final String expectedIP, final X509Certificate cert) throws CertificateException {
        final Collection<List<?>> subjAltNames = cert.getSubjectAlternativeNames();
        if (subjAltNames == null) {
            throw new CertificateException("No subject alternative names present");
        }
        for (final List<?> next : subjAltNames) {
            if ((int)next.get(0) == 7) {
                final String ipAddress = (String)next.get(1);
                if (expectedIP.equalsIgnoreCase(ipAddress)) {
                    return;
                }
                try {
                    if (InetAddress.getByName(expectedIP).equals(InetAddress.getByName(ipAddress))) {
                        return;
                    }
                    continue;
                }
                catch (final UnknownHostException ex) {}
                catch (final SecurityException ex2) {}
            }
        }
        throw new CertificateException("No subject alternative names matching IP address " + expectedIP + " found");
    }
    
    private void matchDNS(final String expectedName, final X509Certificate cert, final boolean chainsToPublicCA) throws CertificateException {
        try {
            final SNIHostName sniHostName = new SNIHostName(expectedName);
        }
        catch (final IllegalArgumentException iae) {
            throw new CertificateException("Illegal given domain name: " + expectedName, iae);
        }
        final Collection<List<?>> subjAltNames = cert.getSubjectAlternativeNames();
        if (subjAltNames != null) {
            boolean foundDNS = false;
            for (final List<?> next : subjAltNames) {
                if ((int)next.get(0) == 2) {
                    foundDNS = true;
                    final String dnsName = (String)next.get(1);
                    if (this.isMatched(expectedName, dnsName, chainsToPublicCA)) {
                        return;
                    }
                    continue;
                }
            }
            if (foundDNS) {
                throw new CertificateException("No subject alternative DNS name matching " + expectedName + " found.");
            }
        }
        final X500Name subjectName = getSubjectX500Name(cert);
        final DerValue derValue = subjectName.findMostSpecificAttribute(X500Name.commonName_oid);
        if (derValue != null) {
            try {
                final String cname = derValue.getAsString();
                if (!Normalizer.isNormalized(cname, Normalizer.Form.NFKC)) {
                    throw new CertificateException("Not a formal name " + cname);
                }
                if (this.isMatched(expectedName, cname, chainsToPublicCA)) {
                    return;
                }
            }
            catch (final IOException ex) {}
        }
        final String msg = "No name matching " + expectedName + " found";
        throw new CertificateException(msg);
    }
    
    public static X500Name getSubjectX500Name(final X509Certificate cert) throws CertificateParsingException {
        try {
            final Principal subjectDN = cert.getSubjectDN();
            if (subjectDN instanceof X500Name) {
                return (X500Name)subjectDN;
            }
            final X500Principal subjectX500 = cert.getSubjectX500Principal();
            return new X500Name(subjectX500.getEncoded());
        }
        catch (final IOException e) {
            throw (CertificateParsingException)new CertificateParsingException().initCause(e);
        }
    }
    
    private boolean isMatched(String name, String template, final boolean chainsToPublicCA) {
        try {
            name = IDN.toUnicode(IDN.toASCII(name));
            template = IDN.toUnicode(IDN.toASCII(template));
        }
        catch (final RuntimeException re) {
            if (SSLLogger.isOn) {
                SSLLogger.fine("Failed to normalize to Unicode: " + re, new Object[0]);
            }
            return false;
        }
        if (hasIllegalWildcard(template, chainsToPublicCA)) {
            return false;
        }
        try {
            new SNIHostName(template.replace('*', 'z'));
        }
        catch (final IllegalArgumentException iae) {
            return false;
        }
        if (this.checkType == 1) {
            return matchAllWildcards(name, template);
        }
        return this.checkType == 2 && matchLeftmostWildcard(name, template);
    }
    
    private static boolean hasIllegalWildcard(final String template, final boolean chainsToPublicCA) {
        if (template.equals("*") || template.equals("*.")) {
            if (SSLLogger.isOn) {
                SSLLogger.fine("Certificate domain name has illegal single wildcard character: " + template, new Object[0]);
            }
            return true;
        }
        final int lastWildcardIndex = template.lastIndexOf("*");
        if (lastWildcardIndex == -1) {
            return false;
        }
        final String afterWildcard = template.substring(lastWildcardIndex);
        final int firstDotIndex = afterWildcard.indexOf(".");
        if (firstDotIndex == -1) {
            if (SSLLogger.isOn) {
                SSLLogger.fine("Certificate domain name has illegal wildcard, no dot after wildcard character: " + template, new Object[0]);
            }
            return true;
        }
        if (!chainsToPublicCA) {
            return false;
        }
        final String wildcardedDomain = afterWildcard.substring(firstDotIndex + 1);
        final String templateDomainSuffix = RegisteredDomain.from("z." + wildcardedDomain).filter(d -> d.type() == RegisteredDomain.Type.ICANN).map((Function<? super RegisteredDomain, ? extends String>)RegisteredDomain::publicSuffix).orElse(null);
        if (templateDomainSuffix == null) {
            return false;
        }
        if (wildcardedDomain.equalsIgnoreCase(templateDomainSuffix)) {
            if (SSLLogger.isOn) {
                SSLLogger.fine("Certificate domain name has illegal wildcard for top-level public suffix: " + template, new Object[0]);
            }
            return true;
        }
        return false;
    }
    
    private static boolean matchAllWildcards(String name, String template) {
        name = name.toLowerCase(Locale.ENGLISH);
        template = template.toLowerCase(Locale.ENGLISH);
        final StringTokenizer nameSt = new StringTokenizer(name, ".");
        final StringTokenizer templateSt = new StringTokenizer(template, ".");
        if (nameSt.countTokens() != templateSt.countTokens()) {
            return false;
        }
        while (nameSt.hasMoreTokens()) {
            if (!matchWildCards(nameSt.nextToken(), templateSt.nextToken())) {
                return false;
            }
        }
        return true;
    }
    
    private static boolean matchLeftmostWildcard(String name, String template) {
        name = name.toLowerCase(Locale.ENGLISH);
        template = template.toLowerCase(Locale.ENGLISH);
        int templateIdx = template.indexOf(".");
        int nameIdx = name.indexOf(".");
        if (templateIdx == -1) {
            templateIdx = template.length();
        }
        if (nameIdx == -1) {
            nameIdx = name.length();
        }
        return matchWildCards(name.substring(0, nameIdx), template.substring(0, templateIdx)) && template.substring(templateIdx).equals(name.substring(nameIdx));
    }
    
    private static boolean matchWildCards(String name, final String template) {
        int wildcardIdx = template.indexOf("*");
        if (wildcardIdx == -1) {
            return name.equals(template);
        }
        boolean isBeginning = true;
        String beforeWildcard = "";
        String afterWildcard;
        for (afterWildcard = template; wildcardIdx != -1; wildcardIdx = afterWildcard.indexOf("*")) {
            beforeWildcard = afterWildcard.substring(0, wildcardIdx);
            afterWildcard = afterWildcard.substring(wildcardIdx + 1);
            final int beforeStartIdx = name.indexOf(beforeWildcard);
            if (beforeStartIdx == -1 || (isBeginning && beforeStartIdx != 0)) {
                return false;
            }
            isBeginning = false;
            name = name.substring(beforeStartIdx + beforeWildcard.length());
        }
        return name.endsWith(afterWildcard);
    }
    
    static {
        INSTANCE_TLS = new HostnameChecker((byte)1);
        INSTANCE_LDAP = new HostnameChecker((byte)2);
    }
}
