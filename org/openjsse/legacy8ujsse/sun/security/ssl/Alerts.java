package org.openjsse.legacy8ujsse.sun.security.ssl;

import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLException;

final class Alerts
{
    static final byte alert_warning = 1;
    static final byte alert_fatal = 2;
    static final byte alert_close_notify = 0;
    static final byte alert_unexpected_message = 10;
    static final byte alert_bad_record_mac = 20;
    static final byte alert_decryption_failed = 21;
    static final byte alert_record_overflow = 22;
    static final byte alert_decompression_failure = 30;
    static final byte alert_handshake_failure = 40;
    static final byte alert_no_certificate = 41;
    static final byte alert_bad_certificate = 42;
    static final byte alert_unsupported_certificate = 43;
    static final byte alert_certificate_revoked = 44;
    static final byte alert_certificate_expired = 45;
    static final byte alert_certificate_unknown = 46;
    static final byte alert_illegal_parameter = 47;
    static final byte alert_unknown_ca = 48;
    static final byte alert_access_denied = 49;
    static final byte alert_decode_error = 50;
    static final byte alert_decrypt_error = 51;
    static final byte alert_export_restriction = 60;
    static final byte alert_protocol_version = 70;
    static final byte alert_insufficient_security = 71;
    static final byte alert_internal_error = 80;
    static final byte alert_user_canceled = 90;
    static final byte alert_no_renegotiation = 100;
    static final byte alert_unsupported_extension = 110;
    static final byte alert_certificate_unobtainable = 111;
    static final byte alert_unrecognized_name = 112;
    static final byte alert_bad_certificate_status_response = 113;
    static final byte alert_bad_certificate_hash_value = 114;
    static final byte alert_no_application_protocol = 120;
    
    static String alertDescription(final byte code) {
        switch (code) {
            case 0: {
                return "close_notify";
            }
            case 10: {
                return "unexpected_message";
            }
            case 20: {
                return "bad_record_mac";
            }
            case 21: {
                return "decryption_failed";
            }
            case 22: {
                return "record_overflow";
            }
            case 30: {
                return "decompression_failure";
            }
            case 40: {
                return "handshake_failure";
            }
            case 41: {
                return "no_certificate";
            }
            case 42: {
                return "bad_certificate";
            }
            case 43: {
                return "unsupported_certificate";
            }
            case 44: {
                return "certificate_revoked";
            }
            case 45: {
                return "certificate_expired";
            }
            case 46: {
                return "certificate_unknown";
            }
            case 47: {
                return "illegal_parameter";
            }
            case 48: {
                return "unknown_ca";
            }
            case 49: {
                return "access_denied";
            }
            case 50: {
                return "decode_error";
            }
            case 51: {
                return "decrypt_error";
            }
            case 60: {
                return "export_restriction";
            }
            case 70: {
                return "protocol_version";
            }
            case 71: {
                return "insufficient_security";
            }
            case 80: {
                return "internal_error";
            }
            case 90: {
                return "user_canceled";
            }
            case 100: {
                return "no_renegotiation";
            }
            case 110: {
                return "unsupported_extension";
            }
            case 111: {
                return "certificate_unobtainable";
            }
            case 112: {
                return "unrecognized_name";
            }
            case 113: {
                return "bad_certificate_status_response";
            }
            case 114: {
                return "bad_certificate_hash_value";
            }
            case 120: {
                return "no_application_protocol";
            }
            default: {
                return "<UNKNOWN ALERT: " + (code & 0xFF) + ">";
            }
        }
    }
    
    static SSLException getSSLException(final byte description, final String reason) {
        return getSSLException(description, null, reason);
    }
    
    static SSLException getSSLException(final byte description, final Throwable cause, String reason) {
        if (reason == null) {
            if (cause != null) {
                reason = cause.toString();
            }
            else {
                reason = "";
            }
        }
        SSLException e = null;
        switch (description) {
            case 40:
            case 41:
            case 42:
            case 43:
            case 44:
            case 45:
            case 46:
            case 48:
            case 49:
            case 51:
            case 60:
            case 71:
            case 110:
            case 111:
            case 112:
            case 113:
            case 114:
            case 120: {
                e = new SSLHandshakeException(reason);
                break;
            }
            default: {
                e = new SSLException(reason);
                break;
            }
        }
        if (cause != null) {
            e.initCause(cause);
        }
        return e;
    }
}
