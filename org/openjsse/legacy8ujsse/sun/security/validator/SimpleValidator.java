package org.openjsse.legacy8ujsse.sun.security.validator;

import java.security.PublicKey;
import java.security.Principal;
import java.io.IOException;
import sun.security.util.DerValue;
import sun.security.util.DerInputStream;
import sun.security.x509.NetscapeCertTypeExtension;
import sun.security.x509.X509CertImpl;
import java.util.Set;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.security.Timestamp;
import sun.security.provider.certpath.AlgorithmChecker;
import java.security.cert.TrustAnchor;
import java.security.cert.CertPathValidatorException;
import java.security.cert.Certificate;
import sun.security.provider.certpath.UntrustedChecker;
import java.util.Date;
import java.security.cert.CertificateException;
import java.security.AlgorithmConstraints;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Collection;
import java.security.cert.X509Certificate;
import java.util.List;
import javax.security.auth.x500.X500Principal;
import java.util.Map;
import sun.security.util.ObjectIdentifier;

public final class SimpleValidator extends Validator
{
    static final String OID_BASIC_CONSTRAINTS = "2.5.29.19";
    static final String OID_NETSCAPE_CERT_TYPE = "2.16.840.1.113730.1.1";
    static final String OID_KEY_USAGE = "2.5.29.15";
    static final String OID_EXTENDED_KEY_USAGE = "2.5.29.37";
    static final String OID_EKU_ANY_USAGE = "2.5.29.37.0";
    static final ObjectIdentifier OBJID_NETSCAPE_CERT_TYPE;
    private static final String NSCT_SSL_CA = "ssl_ca";
    private static final String NSCT_CODE_SIGNING_CA = "object_signing_ca";
    private final Map<X500Principal, List<X509Certificate>> trustedX500Principals;
    private final Collection<X509Certificate> trustedCerts;
    
    SimpleValidator(final String variant, final Collection<X509Certificate> trustedCerts) {
        super("Simple", variant);
        this.trustedCerts = trustedCerts;
        this.trustedX500Principals = new HashMap<X500Principal, List<X509Certificate>>();
        for (final X509Certificate cert : trustedCerts) {
            final X500Principal principal = cert.getSubjectX500Principal();
            List<X509Certificate> list = this.trustedX500Principals.get(principal);
            if (list == null) {
                list = new ArrayList<X509Certificate>(2);
                this.trustedX500Principals.put(principal, list);
            }
            list.add(cert);
        }
    }
    
    @Override
    public Collection<X509Certificate> getTrustedCertificates() {
        return this.trustedCerts;
    }
    
    @Override
    X509Certificate[] engineValidate(X509Certificate[] chain, final Collection<X509Certificate> otherCerts, final AlgorithmConstraints constraints, final Object parameter) throws CertificateException {
        if (chain == null || chain.length == 0) {
            throw new CertificateException("null or zero-length certificate chain");
        }
        chain = this.buildTrustedChain(chain);
        Date date = this.validationDate;
        if (date == null) {
            date = new Date();
        }
        final UntrustedChecker untrustedChecker = new UntrustedChecker();
        final X509Certificate anchorCert = chain[chain.length - 1];
        try {
            untrustedChecker.check(anchorCert);
        }
        catch (final CertPathValidatorException cpve) {
            throw new ValidatorException("Untrusted certificate: " + anchorCert.getSubjectX500Principal(), ValidatorException.T_UNTRUSTED_CERT, anchorCert, cpve);
        }
        final TrustAnchor anchor = new TrustAnchor(anchorCert, null);
        final AlgorithmChecker defaultAlgChecker = new AlgorithmChecker(anchor, this.variant);
        AlgorithmChecker appAlgChecker = null;
        if (constraints != null) {
            appAlgChecker = new AlgorithmChecker(anchor, constraints, (Date)null, (Timestamp)null, this.variant);
        }
        int maxPathLength = chain.length - 1;
        for (int i = chain.length - 2; i >= 0; --i) {
            final X509Certificate issuerCert = chain[i + 1];
            final X509Certificate cert = chain[i];
            try {
                untrustedChecker.check(cert, (Collection<String>)Collections.emptySet());
            }
            catch (final CertPathValidatorException cpve2) {
                throw new ValidatorException("Untrusted certificate: " + cert.getSubjectX500Principal(), ValidatorException.T_UNTRUSTED_CERT, cert, cpve2);
            }
            try {
                defaultAlgChecker.check(cert, (Collection<String>)Collections.emptySet());
                if (appAlgChecker != null) {
                    appAlgChecker.check(cert, (Collection<String>)Collections.emptySet());
                }
            }
            catch (final CertPathValidatorException cpve2) {
                throw new ValidatorException(ValidatorException.T_ALGORITHM_DISABLED, cert, cpve2);
            }
            if (!this.variant.equals("code signing") && !this.variant.equals("jce signing")) {
                cert.checkValidity(date);
            }
            if (!cert.getIssuerX500Principal().equals(issuerCert.getSubjectX500Principal())) {
                throw new ValidatorException(ValidatorException.T_NAME_CHAINING, cert);
            }
            try {
                cert.verify(issuerCert.getPublicKey());
            }
            catch (final GeneralSecurityException e) {
                throw new ValidatorException(ValidatorException.T_SIGNATURE_ERROR, cert, e);
            }
            if (i != 0) {
                maxPathLength = this.checkExtensions(cert, maxPathLength);
            }
        }
        return chain;
    }
    
    private int checkExtensions(final X509Certificate cert, final int maxPathLen) throws CertificateException {
        Set<String> critSet = cert.getCriticalExtensionOIDs();
        if (critSet == null) {
            critSet = Collections.emptySet();
        }
        final int pathLenConstraint = this.checkBasicConstraints(cert, critSet, maxPathLen);
        this.checkKeyUsage(cert, critSet);
        this.checkNetscapeCertType(cert, critSet);
        if (!critSet.isEmpty()) {
            throw new ValidatorException("Certificate contains unknown critical extensions: " + critSet, ValidatorException.T_CA_EXTENSIONS, cert);
        }
        return pathLenConstraint;
    }
    
    private void checkNetscapeCertType(final X509Certificate cert, final Set<String> critSet) throws CertificateException {
        if (!this.variant.equals("generic")) {
            if (this.variant.equals("tls client") || this.variant.equals("tls server")) {
                if (!getNetscapeCertTypeBit(cert, "ssl_ca")) {
                    throw new ValidatorException("Invalid Netscape CertType extension for SSL CA certificate", ValidatorException.T_CA_EXTENSIONS, cert);
                }
                critSet.remove("2.16.840.1.113730.1.1");
            }
            else {
                if (!this.variant.equals("code signing") && !this.variant.equals("jce signing")) {
                    throw new CertificateException("Unknown variant " + this.variant);
                }
                if (!getNetscapeCertTypeBit(cert, "object_signing_ca")) {
                    throw new ValidatorException("Invalid Netscape CertType extension for code signing CA certificate", ValidatorException.T_CA_EXTENSIONS, cert);
                }
                critSet.remove("2.16.840.1.113730.1.1");
            }
        }
    }
    
    static boolean getNetscapeCertTypeBit(final X509Certificate cert, final String type) {
        try {
            NetscapeCertTypeExtension ext;
            if (cert instanceof X509CertImpl) {
                final X509CertImpl certImpl = (X509CertImpl)cert;
                final ObjectIdentifier oid = SimpleValidator.OBJID_NETSCAPE_CERT_TYPE;
                ext = (NetscapeCertTypeExtension)certImpl.getExtension(oid);
                if (ext == null) {
                    return true;
                }
            }
            else {
                final byte[] extVal = cert.getExtensionValue("2.16.840.1.113730.1.1");
                if (extVal == null) {
                    return true;
                }
                final DerInputStream in = new DerInputStream(extVal);
                byte[] encoded = in.getOctetString();
                encoded = new DerValue(encoded).getUnalignedBitString().toByteArray();
                ext = new NetscapeCertTypeExtension(encoded);
            }
            final Boolean val = ext.get(type);
            return val;
        }
        catch (final IOException e) {
            return false;
        }
    }
    
    private int checkBasicConstraints(final X509Certificate cert, final Set<String> critSet, int maxPathLen) throws CertificateException {
        critSet.remove("2.5.29.19");
        final int constraints = cert.getBasicConstraints();
        if (constraints < 0) {
            throw new ValidatorException("End user tried to act as a CA", ValidatorException.T_CA_EXTENSIONS, cert);
        }
        if (!X509CertImpl.isSelfIssued(cert)) {
            if (maxPathLen <= 0) {
                throw new ValidatorException("Violated path length constraints", ValidatorException.T_CA_EXTENSIONS, cert);
            }
            --maxPathLen;
        }
        if (maxPathLen > constraints) {
            maxPathLen = constraints;
        }
        return maxPathLen;
    }
    
    private void checkKeyUsage(final X509Certificate cert, final Set<String> critSet) throws CertificateException {
        critSet.remove("2.5.29.15");
        critSet.remove("2.5.29.37");
        final boolean[] keyUsageInfo = cert.getKeyUsage();
        if (keyUsageInfo != null && (keyUsageInfo.length < 6 || !keyUsageInfo[5])) {
            throw new ValidatorException("Wrong key usage: expected keyCertSign", ValidatorException.T_CA_EXTENSIONS, cert);
        }
    }
    
    private X509Certificate[] buildTrustedChain(final X509Certificate[] chain) throws CertificateException {
        final List<X509Certificate> c = new ArrayList<X509Certificate>(chain.length);
        for (int i = 0; i < chain.length; ++i) {
            final X509Certificate cert = chain[i];
            final X509Certificate trustedCert = this.getTrustedCertificate(cert);
            if (trustedCert != null) {
                c.add(trustedCert);
                return c.toArray(SimpleValidator.CHAIN0);
            }
            c.add(cert);
        }
        final X509Certificate cert2 = chain[chain.length - 1];
        final X500Principal subject = cert2.getSubjectX500Principal();
        final X500Principal issuer = cert2.getIssuerX500Principal();
        final List<X509Certificate> list = this.trustedX500Principals.get(issuer);
        if (list != null) {
            final X509Certificate trustedCert2 = list.iterator().next();
            c.add(trustedCert2);
            return c.toArray(SimpleValidator.CHAIN0);
        }
        throw new ValidatorException(ValidatorException.T_NO_TRUST_ANCHOR);
    }
    
    private X509Certificate getTrustedCertificate(final X509Certificate cert) {
        final Principal certSubjectName = cert.getSubjectX500Principal();
        final List<X509Certificate> list = this.trustedX500Principals.get(certSubjectName);
        if (list == null) {
            return null;
        }
        final Principal certIssuerName = cert.getIssuerX500Principal();
        final PublicKey certPublicKey = cert.getPublicKey();
        for (final X509Certificate mycert : list) {
            if (mycert.equals(cert)) {
                return cert;
            }
            if (!mycert.getIssuerX500Principal().equals(certIssuerName)) {
                continue;
            }
            if (!mycert.getPublicKey().equals(certPublicKey)) {
                continue;
            }
            return mycert;
        }
        return null;
    }
    
    static {
        OBJID_NETSCAPE_CERT_TYPE = NetscapeCertTypeExtension.NetscapeCertType_Id;
    }
}
