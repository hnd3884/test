package com.adventnet.sym.server.mdm.certificates.integrations.certificateauthority.digicert.v1;

import com.adventnet.sym.server.mdm.certificates.scep.PasswordRequestStatus;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.adventnet.sym.server.mdm.certificates.scep.PasswordResponse;
import java.util.Map;

public class DigicertResponseHandler
{
    private DigicertResponseHandler() {
    }
    
    public static PasswordResponse getPasswordResponse(final Map<String, String> digicertPasswordResponse) {
        final String password = digicertPasswordResponse.get("PASSCODE");
        if (!MDMStringUtils.isEmpty(password)) {
            return new PasswordResponse(PasswordRequestStatus.SUCCESS, password);
        }
        final PasswordRequestStatus passwordRequestStatus = getReasonForFailure(digicertPasswordResponse);
        return new PasswordResponse(passwordRequestStatus, null);
    }
    
    private static PasswordRequestStatus getReasonForFailure(final Map<String, String> passcodeResponse) {
        final String status = passcodeResponse.get("STATUS");
        PasswordRequestStatus passwordRequestStatus = PasswordRequestStatus.UNKNOWN_ERROR;
        if (status != null) {
            final String s = status;
            switch (s) {
                case "A300": {
                    passwordRequestStatus = PasswordRequestStatus.FAILED_TO_AUTHENTICATE_REQUEST;
                    break;
                }
                case "A301": {
                    passwordRequestStatus = PasswordRequestStatus.RA_CERT_EXPIRED;
                    break;
                }
                case "A302": {
                    passwordRequestStatus = PasswordRequestStatus.SEAT_ID_INVALID;
                    break;
                }
                case "A515": {
                    passwordRequestStatus = PasswordRequestStatus.RA_CERT_REVOKED_IN_PKI_MANAGER;
                    break;
                }
                case "A600":
                case "A603": {
                    passwordRequestStatus = PasswordRequestStatus.DIGICERT_WS_INTERNAL_SERVER_ERROR;
                    break;
                }
                case "A505": {
                    passwordRequestStatus = PasswordRequestStatus.CERTIFICATE_OID_INVALID;
                    break;
                }
                case "A201": {
                    passwordRequestStatus = PasswordRequestStatus.CERTIFICATE_PROFILE_INACTIVE;
                    break;
                }
                case "A605":
                case "AD01":
                case "A30E": {
                    passwordRequestStatus = PasswordRequestStatus.NUMBER_OF_ENROLLMENTS_EXCEEDED_FOR_CERT_PROFILE;
                    break;
                }
                default: {
                    passwordRequestStatus = PasswordRequestStatus.UNKNOWN_ERROR;
                    break;
                }
            }
        }
        return passwordRequestStatus;
    }
}
