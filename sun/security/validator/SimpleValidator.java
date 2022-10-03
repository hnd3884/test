package sun.security.validator;

import java.security.PublicKey;
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
    
    SimpleValidator(final String s, final Collection<X509Certificate> trustedCerts) {
        super("Simple", s);
        this.trustedCerts = trustedCerts;
        this.trustedX500Principals = new HashMap<X500Principal, List<X509Certificate>>();
        for (final X509Certificate x509Certificate : trustedCerts) {
            final X500Principal subjectX500Principal = x509Certificate.getSubjectX500Principal();
            List list = this.trustedX500Principals.get(subjectX500Principal);
            if (list == null) {
                list = new ArrayList(2);
                this.trustedX500Principals.put(subjectX500Principal, list);
            }
            list.add(x509Certificate);
        }
    }
    
    @Override
    public Collection<X509Certificate> getTrustedCertificates() {
        return this.trustedCerts;
    }
    
    @Override
    X509Certificate[] engineValidate(X509Certificate[] buildTrustedChain, final Collection<X509Certificate> collection, final List<byte[]> list, final AlgorithmConstraints algorithmConstraints, final Object o) throws CertificateException {
        if (buildTrustedChain == null || buildTrustedChain.length == 0) {
            throw new CertificateException("null or zero-length certificate chain");
        }
        buildTrustedChain = this.buildTrustedChain(buildTrustedChain);
        Date validationDate = this.validationDate;
        if (validationDate == null) {
            validationDate = new Date();
        }
        final UntrustedChecker untrustedChecker = new UntrustedChecker();
        final X509Certificate x509Certificate = buildTrustedChain[buildTrustedChain.length - 1];
        try {
            untrustedChecker.check(x509Certificate);
        }
        catch (final CertPathValidatorException ex) {
            throw new ValidatorException("Untrusted certificate: " + x509Certificate.getSubjectX500Principal(), ValidatorException.T_UNTRUSTED_CERT, x509Certificate, ex);
        }
        final TrustAnchor trustAnchor = new TrustAnchor(x509Certificate, null);
        final AlgorithmChecker algorithmChecker = new AlgorithmChecker(trustAnchor, this.variant);
        AlgorithmChecker algorithmChecker2 = null;
        if (algorithmConstraints != null) {
            algorithmChecker2 = new AlgorithmChecker(trustAnchor, algorithmConstraints, null, null, this.variant);
        }
        int checkExtensions = buildTrustedChain.length - 1;
        for (int i = buildTrustedChain.length - 2; i >= 0; --i) {
            final X509Certificate x509Certificate2 = buildTrustedChain[i + 1];
            final X509Certificate x509Certificate3 = buildTrustedChain[i];
            try {
                untrustedChecker.check(x509Certificate3, (Collection<String>)Collections.emptySet());
            }
            catch (final CertPathValidatorException ex2) {
                throw new ValidatorException("Untrusted certificate: " + x509Certificate3.getSubjectX500Principal(), ValidatorException.T_UNTRUSTED_CERT, x509Certificate3, ex2);
            }
            try {
                algorithmChecker.check(x509Certificate3, (Collection<String>)Collections.emptySet());
                if (algorithmChecker2 != null) {
                    algorithmChecker2.check(x509Certificate3, (Collection<String>)Collections.emptySet());
                }
            }
            catch (final CertPathValidatorException ex3) {
                throw new ValidatorException(ValidatorException.T_ALGORITHM_DISABLED, x509Certificate3, ex3);
            }
            if (!this.variant.equals("code signing") && !this.variant.equals("jce signing")) {
                x509Certificate3.checkValidity(validationDate);
            }
            if (!x509Certificate3.getIssuerX500Principal().equals(x509Certificate2.getSubjectX500Principal())) {
                throw new ValidatorException(ValidatorException.T_NAME_CHAINING, x509Certificate3);
            }
            try {
                x509Certificate3.verify(x509Certificate2.getPublicKey());
            }
            catch (final GeneralSecurityException ex4) {
                throw new ValidatorException(ValidatorException.T_SIGNATURE_ERROR, x509Certificate3, ex4);
            }
            if (i != 0) {
                checkExtensions = this.checkExtensions(x509Certificate3, checkExtensions);
            }
        }
        return buildTrustedChain;
    }
    
    private int checkExtensions(final X509Certificate x509Certificate, final int n) throws CertificateException {
        Object o = x509Certificate.getCriticalExtensionOIDs();
        if (o == null) {
            o = Collections.emptySet();
        }
        final int checkBasicConstraints = this.checkBasicConstraints(x509Certificate, (Set<String>)o, n);
        this.checkKeyUsage(x509Certificate, (Set<String>)o);
        this.checkNetscapeCertType(x509Certificate, (Set<String>)o);
        if (!((Set)o).isEmpty()) {
            throw new ValidatorException("Certificate contains unknown critical extensions: " + o, ValidatorException.T_CA_EXTENSIONS, x509Certificate);
        }
        return checkBasicConstraints;
    }
    
    private void checkNetscapeCertType(final X509Certificate x509Certificate, final Set<String> set) throws CertificateException {
        if (!this.variant.equals("generic")) {
            if (this.variant.equals("tls client") || this.variant.equals("tls server")) {
                if (!getNetscapeCertTypeBit(x509Certificate, "ssl_ca")) {
                    throw new ValidatorException("Invalid Netscape CertType extension for SSL CA certificate", ValidatorException.T_CA_EXTENSIONS, x509Certificate);
                }
                set.remove("2.16.840.1.113730.1.1");
            }
            else {
                if (!this.variant.equals("code signing") && !this.variant.equals("jce signing")) {
                    throw new CertificateException("Unknown variant " + this.variant);
                }
                if (!getNetscapeCertTypeBit(x509Certificate, "object_signing_ca")) {
                    throw new ValidatorException("Invalid Netscape CertType extension for code signing CA certificate", ValidatorException.T_CA_EXTENSIONS, x509Certificate);
                }
                set.remove("2.16.840.1.113730.1.1");
            }
        }
    }
    
    static boolean getNetscapeCertTypeBit(final X509Certificate x509Certificate, final String s) {
        try {
            NetscapeCertTypeExtension netscapeCertTypeExtension;
            if (x509Certificate instanceof X509CertImpl) {
                netscapeCertTypeExtension = (NetscapeCertTypeExtension)((X509CertImpl)x509Certificate).getExtension(SimpleValidator.OBJID_NETSCAPE_CERT_TYPE);
                if (netscapeCertTypeExtension == null) {
                    return true;
                }
            }
            else {
                final byte[] extensionValue = x509Certificate.getExtensionValue("2.16.840.1.113730.1.1");
                if (extensionValue == null) {
                    return true;
                }
                netscapeCertTypeExtension = new NetscapeCertTypeExtension(new DerValue(new DerInputStream(extensionValue).getOctetString()).getUnalignedBitString().toByteArray());
            }
            return netscapeCertTypeExtension.get(s);
        }
        catch (final IOException ex) {
            return false;
        }
    }
    
    private int checkBasicConstraints(final X509Certificate x509Certificate, final Set<String> set, int n) throws CertificateException {
        set.remove("2.5.29.19");
        final int basicConstraints = x509Certificate.getBasicConstraints();
        if (basicConstraints < 0) {
            throw new ValidatorException("End user tried to act as a CA", ValidatorException.T_CA_EXTENSIONS, x509Certificate);
        }
        if (!X509CertImpl.isSelfIssued(x509Certificate)) {
            if (n <= 0) {
                throw new ValidatorException("Violated path length constraints", ValidatorException.T_CA_EXTENSIONS, x509Certificate);
            }
            --n;
        }
        if (n > basicConstraints) {
            n = basicConstraints;
        }
        return n;
    }
    
    private void checkKeyUsage(final X509Certificate x509Certificate, final Set<String> set) throws CertificateException {
        set.remove("2.5.29.15");
        set.remove("2.5.29.37");
        final boolean[] keyUsage = x509Certificate.getKeyUsage();
        if (keyUsage != null && (keyUsage.length < 6 || !keyUsage[5])) {
            throw new ValidatorException("Wrong key usage: expected keyCertSign", ValidatorException.T_CA_EXTENSIONS, x509Certificate);
        }
    }
    
    private X509Certificate[] buildTrustedChain(final X509Certificate[] array) throws CertificateException {
        final ArrayList list = new ArrayList(array.length);
        for (int i = 0; i < array.length; ++i) {
            final X509Certificate x509Certificate = array[i];
            final X509Certificate trustedCertificate = this.getTrustedCertificate(x509Certificate);
            if (trustedCertificate != null) {
                list.add(trustedCertificate);
                return (X509Certificate[])list.toArray(SimpleValidator.CHAIN0);
            }
            list.add(x509Certificate);
        }
        final X509Certificate x509Certificate2 = array[array.length - 1];
        x509Certificate2.getSubjectX500Principal();
        final List list2 = this.trustedX500Principals.get(x509Certificate2.getIssuerX500Principal());
        if (list2 != null) {
            list.add(list2.iterator().next());
            return (X509Certificate[])list.toArray(SimpleValidator.CHAIN0);
        }
        throw new ValidatorException(ValidatorException.T_NO_TRUST_ANCHOR);
    }
    
    private X509Certificate getTrustedCertificate(final X509Certificate x509Certificate) {
        final List list = this.trustedX500Principals.get(x509Certificate.getSubjectX500Principal());
        if (list == null) {
            return null;
        }
        final X500Principal issuerX500Principal = x509Certificate.getIssuerX500Principal();
        final PublicKey publicKey = x509Certificate.getPublicKey();
        for (final X509Certificate x509Certificate2 : list) {
            if (x509Certificate2.equals(x509Certificate)) {
                return x509Certificate;
            }
            if (!x509Certificate2.getIssuerX500Principal().equals(issuerX500Principal)) {
                continue;
            }
            if (!x509Certificate2.getPublicKey().equals(publicKey)) {
                continue;
            }
            return x509Certificate2;
        }
        return null;
    }
    
    static {
        OBJID_NETSCAPE_CERT_TYPE = NetscapeCertTypeExtension.NetscapeCertType_Id;
    }
}
