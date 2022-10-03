package com.me.devicemanagement.framework.server.certificate.verifier;

import java.security.cert.CertificateParsingException;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.DistributionPointName;
import org.bouncycastle.asn1.x509.DistributionPoint;
import org.bouncycastle.asn1.DERIA5String;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.CRLDistPoint;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.ASN1InputStream;
import java.util.ArrayList;
import org.bouncycastle.asn1.x509.X509Extensions;
import java.net.MalformedURLException;
import java.net.URL;
import java.io.InputStream;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import java.security.cert.CertificateFactory;
import java.io.ByteArrayInputStream;
import javax.naming.directory.InitialDirContext;
import java.util.Hashtable;
import javax.naming.NamingException;
import java.security.cert.CRLException;
import java.security.cert.CertificateException;
import java.io.IOException;
import java.security.cert.X509CRL;
import java.util.Iterator;
import java.util.List;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;

public class CRLVerifier
{
    public static void verifyCertificateCRLs(final X509Certificate cert) throws CertificateVerificationException {
        try {
            final List<String> crlDistPoints = getCrlDistributionPoints(cert);
            for (final String crlDP : crlDistPoints) {
                final X509CRL crl = downloadCRL(crlDP);
                if (crl.isRevoked(cert)) {
                    throw new CertificateVerificationException("The certificate is revoked by CRL: " + crlDP);
                }
            }
        }
        catch (final Exception ex) {
            if (ex instanceof CertificateVerificationException) {
                throw (CertificateVerificationException)ex;
            }
            throw new CertificateVerificationException("Can not verify CRL for certificate: " + cert.getSubjectX500Principal());
        }
    }
    
    private static X509CRL downloadCRL(final String crlURL) throws IOException, CertificateException, CRLException, CertificateVerificationException, NamingException {
        if (crlURL.startsWith("http://") || crlURL.startsWith("https://") || crlURL.startsWith("ftp://")) {
            final X509CRL crl = downloadCRLFromWeb(crlURL);
            return crl;
        }
        if (crlURL.startsWith("ldap://")) {
            final X509CRL crl = downloadCRLFromLDAP(crlURL);
            return crl;
        }
        throw new CertificateVerificationException("Can not download CRL from certificate distribution point: " + crlURL);
    }
    
    private static X509CRL downloadCRLFromLDAP(final String ldapURL) throws CertificateException, NamingException, CRLException, CertificateVerificationException {
        final Hashtable<String, String> env = new Hashtable<String, String>();
        env.put("java.naming.factory.initial", "com.sun.jndi.ldap.LdapCtxFactory");
        env.put("java.naming.provider.url", ldapURL);
        final DirContext ctx = new InitialDirContext(env);
        final Attributes avals = ctx.getAttributes("");
        final Attribute aval = avals.get("certificateRevocationList;binary");
        final byte[] val = (byte[])aval.get();
        if (val == null || val.length == 0) {
            throw new CertificateVerificationException("Can not download CRL from: " + ldapURL);
        }
        final InputStream inStream = new ByteArrayInputStream(val);
        final CertificateFactory cf = CertificateFactory.getInstance("X.509");
        final X509CRL crl = (X509CRL)cf.generateCRL(inStream);
        return crl;
    }
    
    private static X509CRL downloadCRLFromWeb(final String crlURL) throws MalformedURLException, IOException, CertificateException, CRLException {
        final URL url = new URL(crlURL);
        final InputStream crlStream = url.openStream();
        try {
            final CertificateFactory cf = CertificateFactory.getInstance("X.509");
            final X509CRL crl = (X509CRL)cf.generateCRL(crlStream);
            return crl;
        }
        finally {
            crlStream.close();
        }
    }
    
    public static List<String> getCrlDistributionPoints(final X509Certificate cert) throws CertificateParsingException, IOException {
        final byte[] crldpExt = cert.getExtensionValue(X509Extensions.CRLDistributionPoints.getId());
        if (crldpExt == null) {
            final List<String> emptyList = new ArrayList<String>();
            return emptyList;
        }
        final ASN1InputStream oAsnInStream = new ASN1InputStream((InputStream)new ByteArrayInputStream(crldpExt));
        final DEROctetString dosCrlDP = (DEROctetString)oAsnInStream.readObject();
        final byte[] crldpExtOctets = dosCrlDP.getOctets();
        final ASN1InputStream oAsnInStream2 = new ASN1InputStream((InputStream)new ByteArrayInputStream(crldpExtOctets));
        final ASN1Sequence derObj2 = (ASN1Sequence)oAsnInStream2.readObject();
        final CRLDistPoint distPoint = CRLDistPoint.getInstance((Object)derObj2);
        final List<String> crlUrls = new ArrayList<String>();
        for (final DistributionPoint dp : distPoint.getDistributionPoints()) {
            final DistributionPointName dpn = dp.getDistributionPoint();
            if (dpn != null && dpn.getType() == 0) {
                final GeneralName[] genNames = GeneralNames.getInstance((Object)dpn.getName()).getNames();
                for (int j = 0; j < genNames.length; ++j) {
                    if (genNames[j].getTagNo() == 6) {
                        final String url = DERIA5String.getInstance((Object)genNames[j].getName()).getString();
                        crlUrls.add(url);
                    }
                }
            }
        }
        return crlUrls;
    }
}
