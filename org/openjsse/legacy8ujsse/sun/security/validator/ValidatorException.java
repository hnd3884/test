package org.openjsse.legacy8ujsse.sun.security.validator;

import java.security.cert.X509Certificate;
import java.security.cert.CertificateException;

public class ValidatorException extends CertificateException
{
    private static final long serialVersionUID = -2836879718282292155L;
    public static final Object T_NO_TRUST_ANCHOR;
    public static final Object T_EE_EXTENSIONS;
    public static final Object T_CA_EXTENSIONS;
    public static final Object T_CERT_EXPIRED;
    public static final Object T_SIGNATURE_ERROR;
    public static final Object T_NAME_CHAINING;
    public static final Object T_ALGORITHM_DISABLED;
    public static final Object T_UNTRUSTED_CERT;
    private Object type;
    private X509Certificate cert;
    
    public ValidatorException(final String msg) {
        super(msg);
    }
    
    public ValidatorException(final String msg, final Throwable cause) {
        super(msg);
        this.initCause(cause);
    }
    
    public ValidatorException(final Object type) {
        this(type, null);
    }
    
    public ValidatorException(final Object type, final X509Certificate cert) {
        super((String)type);
        this.type = type;
        this.cert = cert;
    }
    
    public ValidatorException(final Object type, final X509Certificate cert, final Throwable cause) {
        this(type, cert);
        this.initCause(cause);
    }
    
    public ValidatorException(final String msg, final Object type, final X509Certificate cert) {
        super(msg);
        this.type = type;
        this.cert = cert;
    }
    
    public ValidatorException(final String msg, final Object type, final X509Certificate cert, final Throwable cause) {
        this(msg, type, cert);
        this.initCause(cause);
    }
    
    public Object getErrorType() {
        return this.type;
    }
    
    public X509Certificate getErrorCertificate() {
        return this.cert;
    }
    
    static {
        T_NO_TRUST_ANCHOR = "No trusted certificate found";
        T_EE_EXTENSIONS = "End entity certificate extension check failed";
        T_CA_EXTENSIONS = "CA certificate extension check failed";
        T_CERT_EXPIRED = "Certificate expired";
        T_SIGNATURE_ERROR = "Certificate signature validation failed";
        T_NAME_CHAINING = "Certificate chaining error";
        T_ALGORITHM_DISABLED = "Certificate signature algorithm disabled";
        T_UNTRUSTED_CERT = "Untrusted certificate";
    }
}
