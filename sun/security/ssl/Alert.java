package sun.security.ssl;

import java.text.MessageFormat;
import java.util.Locale;
import java.nio.ByteBuffer;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLProtocolException;
import java.io.IOException;
import javax.net.ssl.SSLException;

enum Alert
{
    CLOSE_NOTIFY((byte)0, "close_notify", false), 
    UNEXPECTED_MESSAGE((byte)10, "unexpected_message", false), 
    BAD_RECORD_MAC((byte)20, "bad_record_mac", false), 
    DECRYPTION_FAILED((byte)21, "decryption_failed", false), 
    RECORD_OVERFLOW((byte)22, "record_overflow", false), 
    DECOMPRESSION_FAILURE((byte)30, "decompression_failure", false), 
    HANDSHAKE_FAILURE((byte)40, "handshake_failure", true), 
    NO_CERTIFICATE((byte)41, "no_certificate", true), 
    BAD_CERTIFICATE((byte)42, "bad_certificate", true), 
    UNSUPPORTED_CERTIFICATE((byte)43, "unsupported_certificate", true), 
    CERTIFICATE_REVOKED((byte)44, "certificate_revoked", true), 
    CERTIFICATE_EXPIRED((byte)45, "certificate_expired", true), 
    CERTIFICATE_UNKNOWN((byte)46, "certificate_unknown", true), 
    ILLEGAL_PARAMETER((byte)47, "illegal_parameter", true), 
    UNKNOWN_CA((byte)48, "unknown_ca", true), 
    ACCESS_DENIED((byte)49, "access_denied", true), 
    DECODE_ERROR((byte)50, "decode_error", true), 
    DECRYPT_ERROR((byte)51, "decrypt_error", true), 
    EXPORT_RESTRICTION((byte)60, "export_restriction", true), 
    PROTOCOL_VERSION((byte)70, "protocol_version", true), 
    INSUFFICIENT_SECURITY((byte)71, "insufficient_security", true), 
    INTERNAL_ERROR((byte)80, "internal_error", false), 
    INAPPROPRIATE_FALLBACK((byte)86, "inappropriate_fallback", false), 
    USER_CANCELED((byte)90, "user_canceled", false), 
    NO_RENEGOTIATION((byte)100, "no_renegotiation", true), 
    MISSING_EXTENSION((byte)109, "missing_extension", true), 
    UNSUPPORTED_EXTENSION((byte)110, "unsupported_extension", true), 
    CERT_UNOBTAINABLE((byte)111, "certificate_unobtainable", true), 
    UNRECOGNIZED_NAME((byte)112, "unrecognized_name", true), 
    BAD_CERT_STATUS_RESPONSE((byte)113, "bad_certificate_status_response", true), 
    BAD_CERT_HASH_VALUE((byte)114, "bad_certificate_hash_value", true), 
    UNKNOWN_PSK_IDENTITY((byte)115, "unknown_psk_identity", true), 
    CERTIFICATE_REQUIRED((byte)116, "certificate_required", true), 
    NO_APPLICATION_PROTOCOL((byte)120, "no_application_protocol", true);
    
    final byte id;
    final String description;
    final boolean handshakeOnly;
    static final SSLConsumer alertConsumer;
    
    private Alert(final byte id, final String description, final boolean handshakeOnly) {
        this.id = id;
        this.description = description;
        this.handshakeOnly = handshakeOnly;
    }
    
    static Alert valueOf(final byte b) {
        for (final Alert alert : values()) {
            if (alert.id == b) {
                return alert;
            }
        }
        return null;
    }
    
    static String nameOf(final byte b) {
        for (final Alert alert : values()) {
            if (alert.id == b) {
                return alert.description;
            }
        }
        return "UNKNOWN ALERT (" + (b & 0xFF) + ")";
    }
    
    SSLException createSSLException(final String s) {
        return this.createSSLException(s, null);
    }
    
    SSLException createSSLException(String s, final Throwable t) {
        if (s == null) {
            s = ((t != null) ? t.getMessage() : "");
        }
        SSLException ex;
        if (t != null && t instanceof IOException) {
            ex = new SSLException(s);
        }
        else if (this == Alert.UNEXPECTED_MESSAGE) {
            ex = new SSLProtocolException(s);
        }
        else if (this.handshakeOnly) {
            ex = new SSLHandshakeException(s);
        }
        else {
            ex = new SSLException(s);
        }
        if (t != null) {
            ex.initCause(t);
        }
        return ex;
    }
    
    static {
        alertConsumer = new AlertConsumer();
    }
    
    enum Level
    {
        WARNING((byte)1, "warning"), 
        FATAL((byte)2, "fatal");
        
        final byte level;
        final String description;
        
        private Level(final byte level, final String description) {
            this.level = level;
            this.description = description;
        }
        
        static Level valueOf(final byte b) {
            for (final Level level : values()) {
                if (level.level == b) {
                    return level;
                }
            }
            return null;
        }
        
        static String nameOf(final byte b) {
            for (final Level level : values()) {
                if (level.level == b) {
                    return level.description;
                }
            }
            return "UNKNOWN ALERT LEVEL (" + (b & 0xFF) + ")";
        }
    }
    
    private static final class AlertMessage
    {
        private final byte level;
        private final byte id;
        
        AlertMessage(final TransportContext transportContext, final ByteBuffer byteBuffer) throws IOException {
            if (byteBuffer.remaining() != 2) {
                throw transportContext.fatal(Alert.ILLEGAL_PARAMETER, "Invalid Alert message: no sufficient data");
            }
            this.level = byteBuffer.get();
            this.id = byteBuffer.get();
        }
        
        @Override
        public String toString() {
            return new MessageFormat("\"Alert\": '{'\n  \"level\"      : \"{0}\",\n  \"description\": \"{1}\"\n'}'", Locale.ENGLISH).format(new Object[] { Level.nameOf(this.level), Alert.nameOf(this.id) });
        }
    }
    
    private static final class AlertConsumer implements SSLConsumer
    {
        @Override
        public void consume(final ConnectionContext connectionContext, final ByteBuffer byteBuffer) throws IOException {
            final TransportContext transportContext = (TransportContext)connectionContext;
            final AlertMessage alertMessage = new AlertMessage(transportContext, byteBuffer);
            if (SSLLogger.isOn && SSLLogger.isOn("ssl")) {
                SSLLogger.fine("Received alert message", alertMessage);
            }
            final Level value = Level.valueOf(alertMessage.level);
            Alert alert = Alert.valueOf(alertMessage.id);
            if (alert == Alert.CLOSE_NOTIFY) {
                transportContext.isInputCloseNotified = true;
                transportContext.closeInbound();
                if (transportContext.peerUserCanceled) {
                    transportContext.closeOutbound();
                }
                else if (transportContext.handshakeContext != null) {
                    throw transportContext.fatal(Alert.UNEXPECTED_MESSAGE, "Received close_notify during handshake");
                }
            }
            else if (alert == Alert.USER_CANCELED) {
                if (value != Level.WARNING) {
                    throw transportContext.fatal(alert, "Received fatal close_notify alert", true, null);
                }
                transportContext.peerUserCanceled = true;
            }
            else {
                if (value != Level.WARNING || alert == null) {
                    String s;
                    if (alert == null) {
                        alert = Alert.UNEXPECTED_MESSAGE;
                        s = "Unknown alert description (" + alertMessage.id + ")";
                    }
                    else {
                        s = "Received fatal alert: " + alert.description;
                    }
                    throw transportContext.fatal(alert, s, true, null);
                }
                if (alert.handshakeOnly && transportContext.handshakeContext != null) {
                    if (transportContext.sslConfig.isClientMode || alert != Alert.NO_CERTIFICATE || transportContext.sslConfig.clientAuthType != ClientAuthType.CLIENT_AUTH_REQUESTED) {
                        throw transportContext.fatal(Alert.HANDSHAKE_FAILURE, "received handshake warning: " + alert.description);
                    }
                    transportContext.handshakeContext.handshakeConsumers.remove(SSLHandshake.CERTIFICATE.id);
                    transportContext.handshakeContext.handshakeConsumers.remove(SSLHandshake.CERTIFICATE_VERIFY.id);
                }
            }
        }
    }
}
