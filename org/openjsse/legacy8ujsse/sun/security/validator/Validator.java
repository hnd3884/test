package org.openjsse.legacy8ujsse.sun.security.validator;

import java.security.AlgorithmConstraints;
import java.security.cert.CertificateException;
import java.security.cert.PKIXBuilderParameters;
import java.util.Collection;
import java.security.KeyStore;
import java.util.Date;
import java.security.cert.X509Certificate;

public abstract class Validator
{
    static final X509Certificate[] CHAIN0;
    public static final String TYPE_SIMPLE = "Simple";
    public static final String TYPE_PKIX = "PKIX";
    public static final String VAR_GENERIC = "generic";
    public static final String VAR_CODE_SIGNING = "code signing";
    public static final String VAR_JCE_SIGNING = "jce signing";
    public static final String VAR_TLS_CLIENT = "tls client";
    public static final String VAR_TLS_SERVER = "tls server";
    public static final String VAR_TSA_SERVER = "tsa server";
    public static final String VAR_PLUGIN_CODE_SIGNING = "plugin code signing";
    private final String type;
    final EndEntityChecker endEntityChecker;
    final String variant;
    @Deprecated
    volatile Date validationDate;
    
    Validator(final String type, final String variant) {
        this.type = type;
        this.variant = variant;
        this.endEntityChecker = EndEntityChecker.getInstance(type, variant);
    }
    
    public static Validator getInstance(final String type, final String variant, final KeyStore ks) {
        return getInstance(type, variant, TrustStoreUtil.getTrustedCerts(ks));
    }
    
    public static Validator getInstance(final String type, final String variant, final Collection<X509Certificate> trustedCerts) {
        if (type.equals("Simple")) {
            return new SimpleValidator(variant, trustedCerts);
        }
        if (type.equals("PKIX")) {
            return new PKIXValidator(variant, trustedCerts);
        }
        throw new IllegalArgumentException("Unknown validator type: " + type);
    }
    
    public static Validator getInstance(final String type, final String variant, final PKIXBuilderParameters params) {
        if (!type.equals("PKIX")) {
            throw new IllegalArgumentException("getInstance(PKIXBuilderParameters) can only be used with PKIX validator");
        }
        return new PKIXValidator(variant, params);
    }
    
    public final X509Certificate[] validate(final X509Certificate[] chain) throws CertificateException {
        return this.validate(chain, null, null);
    }
    
    public final X509Certificate[] validate(final X509Certificate[] chain, final Collection<X509Certificate> otherCerts) throws CertificateException {
        return this.validate(chain, otherCerts, null);
    }
    
    public final X509Certificate[] validate(final X509Certificate[] chain, final Collection<X509Certificate> otherCerts, final Object parameter) throws CertificateException {
        return this.validate(chain, otherCerts, null, parameter);
    }
    
    public final X509Certificate[] validate(X509Certificate[] chain, final Collection<X509Certificate> otherCerts, final AlgorithmConstraints constraints, final Object parameter) throws CertificateException {
        chain = this.engineValidate(chain, otherCerts, constraints, parameter);
        if (chain.length > 1) {
            final boolean checkUnresolvedCritExts = this.type != "PKIX";
            this.endEntityChecker.check(chain, parameter, checkUnresolvedCritExts);
        }
        return chain;
    }
    
    abstract X509Certificate[] engineValidate(final X509Certificate[] p0, final Collection<X509Certificate> p1, final AlgorithmConstraints p2, final Object p3) throws CertificateException;
    
    public abstract Collection<X509Certificate> getTrustedCertificates();
    
    @Deprecated
    public void setValidationDate(final Date validationDate) {
        this.validationDate = validationDate;
    }
    
    static {
        CHAIN0 = new X509Certificate[0];
    }
}
