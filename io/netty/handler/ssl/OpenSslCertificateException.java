package io.netty.handler.ssl;

import io.netty.internal.tcnative.CertificateVerifier;
import java.security.cert.CertificateException;

public final class OpenSslCertificateException extends CertificateException
{
    private static final long serialVersionUID = 5542675253797129798L;
    private final int errorCode;
    
    public OpenSslCertificateException(final int errorCode) {
        this((String)null, errorCode);
    }
    
    public OpenSslCertificateException(final String msg, final int errorCode) {
        super(msg);
        this.errorCode = checkErrorCode(errorCode);
    }
    
    public OpenSslCertificateException(final String message, final Throwable cause, final int errorCode) {
        super(message, cause);
        this.errorCode = checkErrorCode(errorCode);
    }
    
    public OpenSslCertificateException(final Throwable cause, final int errorCode) {
        this(null, cause, errorCode);
    }
    
    public int errorCode() {
        return this.errorCode;
    }
    
    private static int checkErrorCode(final int errorCode) {
        if (OpenSsl.isAvailable() && !CertificateVerifier.isValid(errorCode)) {
            throw new IllegalArgumentException("errorCode '" + errorCode + "' invalid, see https://www.openssl.org/docs/man1.0.2/apps/verify.html.");
        }
        return errorCode;
    }
}
