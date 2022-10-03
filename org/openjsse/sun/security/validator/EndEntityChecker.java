package org.openjsse.sun.security.validator;

import java.util.Arrays;
import sun.security.validator.ValidatorException;
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
    
    static EndEntityChecker getInstance(final String type, final String variant) {
        return new EndEntityChecker(type, variant);
    }
    
    void check(final X509Certificate[] chain, final Object parameter, final boolean checkUnresolvedCritExts) throws CertificateException {
        if (this.variant.equals("generic")) {
            return;
        }
        final Set<String> exts = this.getCriticalExtensions(chain[0]);
        if (this.variant.equals("tls server")) {
            this.checkTLSServer(chain[0], (String)parameter, exts);
        }
        else if (this.variant.equals("tls client")) {
            this.checkTLSClient(chain[0], exts);
        }
        else if (this.variant.equals("code signing")) {
            this.checkCodeSigning(chain[0], exts);
        }
        else if (this.variant.equals("jce signing")) {
            this.checkCodeSigning(chain[0], exts);
        }
        else if (this.variant.equals("plugin code signing")) {
            this.checkCodeSigning(chain[0], exts);
        }
        else {
            if (!this.variant.equals("tsa server")) {
                throw new CertificateException("Unknown variant: " + this.variant);
            }
            this.checkTSAServer(chain[0], exts);
        }
        if (checkUnresolvedCritExts) {
            this.checkRemainingExtensions(exts);
        }
        for (final CADistrustPolicy policy : CADistrustPolicy.POLICIES) {
            policy.checkDistrust(this.variant, chain);
        }
    }
    
    private Set<String> getCriticalExtensions(final X509Certificate cert) {
        Set<String> exts = cert.getCriticalExtensionOIDs();
        if (exts == null) {
            exts = Collections.emptySet();
        }
        return exts;
    }
    
    private void checkRemainingExtensions(final Set<String> exts) throws CertificateException {
        exts.remove("2.5.29.19");
        exts.remove("2.5.29.17");
        if (!exts.isEmpty()) {
            throw new CertificateException("Certificate contains unsupported critical extensions: " + exts);
        }
    }
    
    private boolean checkEKU(final X509Certificate cert, final Set<String> exts, final String expectedEKU) throws CertificateException {
        final List<String> eku = cert.getExtendedKeyUsage();
        return eku == null || eku.contains(expectedEKU) || eku.contains("2.5.29.37.0");
    }
    
    private boolean checkKeyUsage(final X509Certificate cert, final int bit) throws CertificateException {
        final boolean[] keyUsage = cert.getKeyUsage();
        return keyUsage == null || (keyUsage.length > bit && keyUsage[bit]);
    }
    
    private void checkTLSClient(final X509Certificate cert, final Set<String> exts) throws CertificateException {
        if (!this.checkKeyUsage(cert, 0)) {
            throw new ValidatorException("KeyUsage does not allow digital signatures", ValidatorException.T_EE_EXTENSIONS, cert);
        }
        if (!this.checkEKU(cert, exts, "1.3.6.1.5.5.7.3.2")) {
            throw new ValidatorException("Extended key usage does not permit use for TLS client authentication", ValidatorException.T_EE_EXTENSIONS, cert);
        }
        if (!SimpleValidator.getNetscapeCertTypeBit(cert, "ssl_client")) {
            throw new ValidatorException("Netscape cert type does not permit use for SSL client", ValidatorException.T_EE_EXTENSIONS, cert);
        }
        exts.remove("2.5.29.15");
        exts.remove("2.5.29.37");
        exts.remove("2.16.840.1.113730.1.1");
    }
    
    private void checkTLSServer(final X509Certificate cert, final String parameter, final Set<String> exts) throws CertificateException {
        if (EndEntityChecker.KU_SERVER_ENCRYPTION.contains(parameter)) {
            if (!this.checkKeyUsage(cert, 2)) {
                throw new ValidatorException("KeyUsage does not allow key encipherment", ValidatorException.T_EE_EXTENSIONS, cert);
            }
        }
        else if (EndEntityChecker.KU_SERVER_SIGNATURE.contains(parameter)) {
            if (!this.checkKeyUsage(cert, 0)) {
                throw new ValidatorException("KeyUsage does not allow digital signatures", ValidatorException.T_EE_EXTENSIONS, cert);
            }
        }
        else {
            if (!EndEntityChecker.KU_SERVER_KEY_AGREEMENT.contains(parameter)) {
                throw new CertificateException("Unknown authType: " + parameter);
            }
            if (!this.checkKeyUsage(cert, 4)) {
                throw new ValidatorException("KeyUsage does not allow key agreement", ValidatorException.T_EE_EXTENSIONS, cert);
            }
        }
        if (!this.checkEKU(cert, exts, "1.3.6.1.5.5.7.3.1") && !this.checkEKU(cert, exts, "1.3.6.1.4.1.311.10.3.3") && !this.checkEKU(cert, exts, "2.16.840.1.113730.4.1")) {
            throw new ValidatorException("Extended key usage does not permit use for TLS server authentication", ValidatorException.T_EE_EXTENSIONS, cert);
        }
        if (!SimpleValidator.getNetscapeCertTypeBit(cert, "ssl_server")) {
            throw new ValidatorException("Netscape cert type does not permit use for SSL server", ValidatorException.T_EE_EXTENSIONS, cert);
        }
        exts.remove("2.5.29.15");
        exts.remove("2.5.29.37");
        exts.remove("2.16.840.1.113730.1.1");
    }
    
    private void checkCodeSigning(final X509Certificate cert, final Set<String> exts) throws CertificateException {
        if (!this.checkKeyUsage(cert, 0)) {
            throw new ValidatorException("KeyUsage does not allow digital signatures", ValidatorException.T_EE_EXTENSIONS, cert);
        }
        if (!this.checkEKU(cert, exts, "1.3.6.1.5.5.7.3.3")) {
            throw new ValidatorException("Extended key usage does not permit use for code signing", ValidatorException.T_EE_EXTENSIONS, cert);
        }
        if (!this.variant.equals("jce signing")) {
            if (!SimpleValidator.getNetscapeCertTypeBit(cert, "object_signing")) {
                throw new ValidatorException("Netscape cert type does not permit use for code signing", ValidatorException.T_EE_EXTENSIONS, cert);
            }
            exts.remove("2.16.840.1.113730.1.1");
        }
        exts.remove("2.5.29.15");
        exts.remove("2.5.29.37");
    }
    
    private void checkTSAServer(final X509Certificate cert, final Set<String> exts) throws CertificateException {
        if (!this.checkKeyUsage(cert, 0)) {
            throw new ValidatorException("KeyUsage does not allow digital signatures", ValidatorException.T_EE_EXTENSIONS, cert);
        }
        if (cert.getExtendedKeyUsage() == null) {
            throw new ValidatorException("Certificate does not contain an extended key usage extension required for a TSA server", ValidatorException.T_EE_EXTENSIONS, cert);
        }
        if (!this.checkEKU(cert, exts, "1.3.6.1.5.5.7.3.8")) {
            throw new ValidatorException("Extended key usage does not permit use for TSA server", ValidatorException.T_EE_EXTENSIONS, cert);
        }
        exts.remove("2.5.29.15");
        exts.remove("2.5.29.37");
    }
    
    static {
        KU_SERVER_SIGNATURE = Arrays.asList("DHE_DSS", "DHE_RSA", "ECDHE_ECDSA", "ECDHE_RSA", "RSA_EXPORT", "UNKNOWN");
        KU_SERVER_ENCRYPTION = Arrays.asList("RSA");
        KU_SERVER_KEY_AGREEMENT = Arrays.asList("DH_DSS", "DH_RSA", "ECDH_ECDSA", "ECDH_RSA");
    }
}
