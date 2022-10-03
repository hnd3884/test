package com.me.ems.onpremise.security.certificate.api.Exception;

import javax.ws.rs.core.Response;
import com.me.ems.framework.common.api.utils.APIException;

public class CertificateAPIException extends APIException
{
    public CertificateAPIException(final Throwable cause) {
        super(cause);
    }
    
    public CertificateAPIException(final Throwable cause, final String errorCode, final String errorMsg, final String... msgArgs) {
        super(cause, errorCode, errorMsg, msgArgs);
    }
    
    public CertificateAPIException(final String errorCode, final String errorMsg, final String... msgArgs) {
        super(errorCode, (String)null, msgArgs);
    }
    
    public CertificateAPIException(final String errorCode, final String errorMsg, final Throwable cause, final String... msgArgs) {
        super(errorCode, (String)null, msgArgs);
    }
    
    public CertificateAPIException(final Response.Status status, final String errorCode, final String errorMsg, final String... msgArgs) {
        super(status, errorCode, errorMsg, msgArgs);
    }
    
    public CertificateAPIException(final String errorCode) {
        super(errorCode);
    }
    
    public CertificateAPIException(final String errorCode, final Throwable cause) {
        super(errorCode);
    }
    
    public CertificateAPIException(final String errorCode, final String errorMessage) {
        super(errorCode);
    }
    
    public CertificateAPIException(final String errorCode, final String errorMessage, final Throwable cause) {
        super(errorCode);
    }
    
    public CertificateAPIException(final String errorCode, final boolean isReferenceURIAvailable, final String referenceURI) {
        super(errorCode, isReferenceURIAvailable, referenceURI);
    }
}
