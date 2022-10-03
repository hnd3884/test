package com.adventnet.sym.server.mdm.certificates.scep;

public enum PasswordRequestStatus
{
    SUCCESS("mdm.scep.password_request_success"), 
    FAILED("mdm.scep.password_request_failed"), 
    PASSWORD_CACHE_LIMIT_REACHED("mdm.scep.password_cache_limit_reached"), 
    ADCS_UNAUTHORIZED("mdm.scep.unauthorized"), 
    HTTP_NOT_ALLOWED("mdm.scep.http_not_allowed"), 
    RA_CERT_EXPIRED("mdm.scep.ra_certificate_expired"), 
    RA_CERT_REVOKED_IN_PKI_MANAGER("mdm.scep.ra_certificate_revoked"), 
    FAILED_TO_AUTHENTICATE_REQUEST("mdm.scep.authentication_failed"), 
    DIGICERT_WS_INTERNAL_SERVER_ERROR("mdm.scep.digicert_ws_internal_server_error"), 
    SEAT_ID_INVALID("mdm.scep.seat_id_invalid"), 
    CERTIFICATE_OID_INVALID("mdm.scep.certificate_oid_invalid"), 
    CERTIFICATE_PROFILE_INACTIVE("mdm.scep.certificate_profile_inactive"), 
    NUMBER_OF_ENROLLMENTS_EXCEEDED_FOR_CERT_PROFILE("mdm.scep.enrollment_limit_exceeded"), 
    UNKNOWN_ERROR("mdm.scep.unexpected_error");
    
    private final String remarkString;
    
    private PasswordRequestStatus(final String remarkString) {
        this.remarkString = remarkString;
    }
    
    public String getRemarkString() {
        return this.remarkString;
    }
}
