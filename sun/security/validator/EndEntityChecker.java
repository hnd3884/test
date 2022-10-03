package sun.security.validator;

import java.util.Arrays;
import java.util.List;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Collection;

class EndEntityChecker
{
    private static final String OID_EXTENDED_KEY_USAGE = "2.5.29.37";
    private static final String OID_EKU_TLS_SERVER = "1.3.6.1.5.5.7.3.1";
    private static final String OID_EKU_TLS_CLIENT = "1.3.6.1.5.5.7.3.2";
    private static final String OID_EKU_CODE_SIGNING = "1.3.6.1.5.5.7.3.3";
    private static final String OID_EKU_TIME_STAMPING = "1.3.6.1.5.5.7.3.8";
    private static final String OID_EKU_ANY_USAGE = "2.5.29.37.0";
    private static final String OID_EKU_NS_SGC = "2.16.840.1.113730.4.1";
    private static final String OID_EKU_MS_SGC = "1.3.6.1.4.1.311.10.3.3";
    private static final String OID_SUBJECT_ALT_NAME = "2.5.29.17";
    private static final String NSCT_SSL_CLIENT = "ssl_client";
    private static final String NSCT_SSL_SERVER = "ssl_server";
    private static final String NSCT_CODE_SIGNING = "object_signing";
    private static final int KU_SIGNATURE = 0;
    private static final int KU_KEY_ENCIPHERMENT = 2;
    private static final int KU_KEY_AGREEMENT = 4;
    private static final Collection<String> KU_SERVER_SIGNATURE;
    private static final Collection<String> KU_SERVER_ENCRYPTION;
    private static final Collection<String> KU_SERVER_KEY_AGREEMENT;
    private final String variant;
    private final String type;
    
    private EndEntityChecker(final String type, final String variant) {
        this.type = type;
        this.variant = variant;
    }
    
    static EndEntityChecker getInstance(final String s, final String s2) {
        return new EndEntityChecker(s, s2);
    }
    
    void check(final X509Certificate[] array, final Object o, final boolean b) throws CertificateException {
        if (this.variant.equals("generic")) {
            return;
        }
        final Set<String> criticalExtensions = this.getCriticalExtensions(array[0]);
        if (this.variant.equals("tls server")) {
            this.checkTLSServer(array[0], (String)o, criticalExtensions);
        }
        else if (this.variant.equals("tls client")) {
            this.checkTLSClient(array[0], criticalExtensions);
        }
        else if (this.variant.equals("code signing")) {
            this.checkCodeSigning(array[0], criticalExtensions);
        }
        else if (this.variant.equals("jce signing")) {
            this.checkCodeSigning(array[0], criticalExtensions);
        }
        else if (this.variant.equals("plugin code signing")) {
            this.checkCodeSigning(array[0], criticalExtensions);
        }
        else {
            if (!this.variant.equals("tsa server")) {
                throw new CertificateException("Unknown variant: " + this.variant);
            }
            this.checkTSAServer(array[0], criticalExtensions);
        }
        if (b) {
            this.checkRemainingExtensions(criticalExtensions);
        }
        final Iterator<Object> iterator = CADistrustPolicy.POLICIES.iterator();
        while (iterator.hasNext()) {
            iterator.next().checkDistrust(this.variant, array);
        }
    }
    
    private Set<String> getCriticalExtensions(final X509Certificate x509Certificate) {
        Object o = x509Certificate.getCriticalExtensionOIDs();
        if (o == null) {
            o = Collections.emptySet();
        }
        return (Set<String>)o;
    }
    
    private void checkRemainingExtensions(final Set<String> set) throws CertificateException {
        set.remove("2.5.29.19");
        set.remove("2.5.29.17");
        if (!set.isEmpty()) {
            throw new CertificateException("Certificate contains unsupported critical extensions: " + set);
        }
    }
    
    private boolean checkEKU(final X509Certificate x509Certificate, final Set<String> set, final String s) throws CertificateException {
        final List<String> extendedKeyUsage = x509Certificate.getExtendedKeyUsage();
        return extendedKeyUsage == null || extendedKeyUsage.contains(s) || extendedKeyUsage.contains("2.5.29.37.0");
    }
    
    private boolean checkKeyUsage(final X509Certificate x509Certificate, final int n) throws CertificateException {
        final boolean[] keyUsage = x509Certificate.getKeyUsage();
        return keyUsage == null || (keyUsage.length > n && keyUsage[n]);
    }
    
    private void checkTLSClient(final X509Certificate x509Certificate, final Set<String> set) throws CertificateException {
        if (!this.checkKeyUsage(x509Certificate, 0)) {
            throw new ValidatorException("KeyUsage does not allow digital signatures", ValidatorException.T_EE_EXTENSIONS, x509Certificate);
        }
        if (!this.checkEKU(x509Certificate, set, "1.3.6.1.5.5.7.3.2")) {
            throw new ValidatorException("Extended key usage does not permit use for TLS client authentication", ValidatorException.T_EE_EXTENSIONS, x509Certificate);
        }
        if (!SimpleValidator.getNetscapeCertTypeBit(x509Certificate, "ssl_client")) {
            throw new ValidatorException("Netscape cert type does not permit use for SSL client", ValidatorException.T_EE_EXTENSIONS, x509Certificate);
        }
        set.remove("2.5.29.15");
        set.remove("2.5.29.37");
        set.remove("2.16.840.1.113730.1.1");
    }
    
    private void checkTLSServer(final X509Certificate x509Certificate, final String s, final Set<String> set) throws CertificateException {
        if (EndEntityChecker.KU_SERVER_ENCRYPTION.contains(s)) {
            if (!this.checkKeyUsage(x509Certificate, 2)) {
                throw new ValidatorException("KeyUsage does not allow key encipherment", ValidatorException.T_EE_EXTENSIONS, x509Certificate);
            }
        }
        else if (EndEntityChecker.KU_SERVER_SIGNATURE.contains(s)) {
            if (!this.checkKeyUsage(x509Certificate, 0)) {
                throw new ValidatorException("KeyUsage does not allow digital signatures", ValidatorException.T_EE_EXTENSIONS, x509Certificate);
            }
        }
        else {
            if (!EndEntityChecker.KU_SERVER_KEY_AGREEMENT.contains(s)) {
                throw new CertificateException("Unknown authType: " + s);
            }
            if (!this.checkKeyUsage(x509Certificate, 4)) {
                throw new ValidatorException("KeyUsage does not allow key agreement", ValidatorException.T_EE_EXTENSIONS, x509Certificate);
            }
        }
        if (!this.checkEKU(x509Certificate, set, "1.3.6.1.5.5.7.3.1") && !this.checkEKU(x509Certificate, set, "1.3.6.1.4.1.311.10.3.3") && !this.checkEKU(x509Certificate, set, "2.16.840.1.113730.4.1")) {
            throw new ValidatorException("Extended key usage does not permit use for TLS server authentication", ValidatorException.T_EE_EXTENSIONS, x509Certificate);
        }
        if (!SimpleValidator.getNetscapeCertTypeBit(x509Certificate, "ssl_server")) {
            throw new ValidatorException("Netscape cert type does not permit use for SSL server", ValidatorException.T_EE_EXTENSIONS, x509Certificate);
        }
        set.remove("2.5.29.15");
        set.remove("2.5.29.37");
        set.remove("2.16.840.1.113730.1.1");
    }
    
    private void checkCodeSigning(final X509Certificate x509Certificate, final Set<String> set) throws CertificateException {
        if (!this.checkKeyUsage(x509Certificate, 0)) {
            throw new ValidatorException("KeyUsage does not allow digital signatures", ValidatorException.T_EE_EXTENSIONS, x509Certificate);
        }
        if (!this.checkEKU(x509Certificate, set, "1.3.6.1.5.5.7.3.3")) {
            throw new ValidatorException("Extended key usage does not permit use for code signing", ValidatorException.T_EE_EXTENSIONS, x509Certificate);
        }
        if (!this.variant.equals("jce signing")) {
            if (!SimpleValidator.getNetscapeCertTypeBit(x509Certificate, "object_signing")) {
                throw new ValidatorException("Netscape cert type does not permit use for code signing", ValidatorException.T_EE_EXTENSIONS, x509Certificate);
            }
            set.remove("2.16.840.1.113730.1.1");
        }
        set.remove("2.5.29.15");
        set.remove("2.5.29.37");
    }
    
    private void checkTSAServer(final X509Certificate x509Certificate, final Set<String> set) throws CertificateException {
        if (!this.checkKeyUsage(x509Certificate, 0)) {
            throw new ValidatorException("KeyUsage does not allow digital signatures", ValidatorException.T_EE_EXTENSIONS, x509Certificate);
        }
        if (x509Certificate.getExtendedKeyUsage() == null) {
            throw new ValidatorException("Certificate does not contain an extended key usage extension required for a TSA server", ValidatorException.T_EE_EXTENSIONS, x509Certificate);
        }
        if (!this.checkEKU(x509Certificate, set, "1.3.6.1.5.5.7.3.8")) {
            throw new ValidatorException("Extended key usage does not permit use for TSA server", ValidatorException.T_EE_EXTENSIONS, x509Certificate);
        }
        set.remove("2.5.29.15");
        set.remove("2.5.29.37");
    }
    
    static {
        KU_SERVER_SIGNATURE = Arrays.asList("DHE_DSS", "DHE_RSA", "ECDHE_ECDSA", "ECDHE_RSA", "RSA_EXPORT", "UNKNOWN");
        KU_SERVER_ENCRYPTION = Arrays.asList("RSA");
        KU_SERVER_KEY_AGREEMENT = Arrays.asList("DH_DSS", "DH_RSA", "ECDH_ECDSA", "ECDH_RSA");
    }
}
