package org.bouncycastle.est.jcajce;

import org.bouncycastle.util.Strings;
import org.bouncycastle.asn1.x500.AttributeTypeAndValue;
import org.bouncycastle.asn1.x500.RDN;
import java.util.Iterator;
import java.util.Collection;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x500.X500Name;
import java.net.InetAddress;
import java.util.List;
import java.io.IOException;
import org.bouncycastle.est.ESTException;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import javax.net.ssl.SSLSession;
import java.util.Set;

public class JsseDefaultHostnameAuthorizer implements JsseHostnameAuthorizer
{
    private final Set<String> knownSuffixes;
    
    public JsseDefaultHostnameAuthorizer(final Set<String> knownSuffixes) {
        this.knownSuffixes = knownSuffixes;
    }
    
    public boolean verified(final String s, final SSLSession sslSession) throws IOException {
        try {
            return this.verify(s, (X509Certificate)CertificateFactory.getInstance("X509").generateCertificate(new ByteArrayInputStream(sslSession.getPeerCertificates()[0].getEncoded())));
        }
        catch (final Exception ex) {
            if (ex instanceof ESTException) {
                throw (ESTException)ex;
            }
            throw new ESTException(ex.getMessage(), ex);
        }
    }
    
    public boolean verify(final String s, final X509Certificate x509Certificate) throws IOException {
        try {
            final Collection<List<?>> subjectAlternativeNames = x509Certificate.getSubjectAlternativeNames();
            if (subjectAlternativeNames != null) {
                for (final List list : subjectAlternativeNames) {
                    switch (((Number)list.get(0)).intValue()) {
                        case 2: {
                            if (isValidNameMatch(s, list.get(1).toString(), this.knownSuffixes)) {
                                return true;
                            }
                            continue;
                        }
                        case 7: {
                            if (InetAddress.getByName(s).equals(InetAddress.getByName(list.get(1).toString()))) {
                                return true;
                            }
                            continue;
                        }
                        default: {
                            throw new RuntimeException("Unable to handle ");
                        }
                    }
                }
                return false;
            }
        }
        catch (final Exception ex) {
            throw new ESTException(ex.getMessage(), ex);
        }
        if (x509Certificate.getSubjectX500Principal() == null) {
            return false;
        }
        final RDN[] rdNs = X500Name.getInstance((Object)x509Certificate.getSubjectX500Principal().getEncoded()).getRDNs();
        for (int i = 0; i != rdNs.length; ++i) {
            final RDN rdn = rdNs[i];
            final AttributeTypeAndValue[] typesAndValues = rdn.getTypesAndValues();
            for (int j = 0; j != typesAndValues.length; ++j) {
                if (typesAndValues[j].getType().equals((Object)BCStyle.CN)) {
                    return isValidNameMatch(s, rdn.getFirst().getValue().toString(), this.knownSuffixes);
                }
            }
        }
        return false;
    }
    
    public static boolean isValidNameMatch(final String s, final String s2, final Set<String> set) throws IOException {
        if (!s2.contains("*")) {
            return s.equalsIgnoreCase(s2);
        }
        final int index = s2.indexOf(42);
        if (index != s2.lastIndexOf("*")) {
            return false;
        }
        if (s2.contains("..") || s2.charAt(s2.length() - 1) == '*') {
            return false;
        }
        final int index2 = s2.indexOf(46, index);
        if (set != null && set.contains(Strings.toLowerCase(s2.substring(index2)))) {
            throw new IOException("Wildcard `" + s2 + "` matches known public suffix.");
        }
        final String lowerCase = Strings.toLowerCase(s2.substring(index + 1));
        final String lowerCase2 = Strings.toLowerCase(s);
        if (lowerCase2.equals(lowerCase)) {
            return false;
        }
        if (lowerCase.length() > lowerCase2.length()) {
            return false;
        }
        if (index > 0) {
            return lowerCase2.startsWith(s2.substring(0, index - 1)) && lowerCase2.endsWith(lowerCase) && lowerCase2.substring(index, lowerCase2.length() - lowerCase.length()).indexOf(46) < 0;
        }
        return lowerCase2.substring(0, lowerCase2.length() - lowerCase.length()).indexOf(46) <= 0 && lowerCase2.endsWith(lowerCase);
    }
}
