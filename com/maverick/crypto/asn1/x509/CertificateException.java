package com.maverick.crypto.asn1.x509;

public class CertificateException extends Exception
{
    public static final int CERTIFICATE_EXPIRED = 1;
    public static final int CERTIFICATE_NOT_YET_VALID = 2;
    public static final int CERTIFICATE_ENCODING_ERROR = 3;
    public static final int CERTIFICATE_GENERAL_ERROR = 4;
    public static final int CERTIFICATE_UNSUPPORTED_ALGORITHM = 5;
    int b;
    
    public CertificateException(final int b, final String s) {
        super(s);
        this.b = b;
    }
    
    public int getStatus() {
        return this.b;
    }
}
