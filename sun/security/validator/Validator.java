package sun.security.validator;

import java.util.List;
import java.security.AlgorithmConstraints;
import java.util.Collections;
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
    
    public static Validator getInstance(final String s, final String s2, final KeyStore keyStore) {
        return getInstance(s, s2, TrustStoreUtil.getTrustedCerts(keyStore));
    }
    
    public static Validator getInstance(final String s, final String s2, final Collection<X509Certificate> collection) {
        if (s.equals("Simple")) {
            return new SimpleValidator(s2, collection);
        }
        if (s.equals("PKIX")) {
            return new PKIXValidator(s2, collection);
        }
        throw new IllegalArgumentException("Unknown validator type: " + s);
    }
    
    public static Validator getInstance(final String s, final String s2, final PKIXBuilderParameters pkixBuilderParameters) {
        if (!s.equals("PKIX")) {
            throw new IllegalArgumentException("getInstance(PKIXBuilderParameters) can only be used with PKIX validator");
        }
        return new PKIXValidator(s2, pkixBuilderParameters);
    }
    
    public final X509Certificate[] validate(final X509Certificate[] array) throws CertificateException {
        return this.validate(array, null, null);
    }
    
    public final X509Certificate[] validate(final X509Certificate[] array, final Collection<X509Certificate> collection) throws CertificateException {
        return this.validate(array, collection, null);
    }
    
    public final X509Certificate[] validate(final X509Certificate[] array, final Collection<X509Certificate> collection, final Object o) throws CertificateException {
        return this.validate(array, collection, Collections.emptyList(), null, o);
    }
    
    public final X509Certificate[] validate(X509Certificate[] engineValidate, final Collection<X509Certificate> collection, final List<byte[]> list, final AlgorithmConstraints algorithmConstraints, final Object o) throws CertificateException {
        engineValidate = this.engineValidate(engineValidate, collection, list, algorithmConstraints, o);
        if (engineValidate.length > 1) {
            this.endEntityChecker.check(engineValidate, o, this.type != "PKIX");
        }
        return engineValidate;
    }
    
    abstract X509Certificate[] engineValidate(final X509Certificate[] p0, final Collection<X509Certificate> p1, final List<byte[]> p2, final AlgorithmConstraints p3, final Object p4) throws CertificateException;
    
    public abstract Collection<X509Certificate> getTrustedCertificates();
    
    @Deprecated
    public void setValidationDate(final Date validationDate) {
        this.validationDate = validationDate;
    }
    
    static {
        CHAIN0 = new X509Certificate[0];
    }
}
