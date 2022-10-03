package org.openjsse.sun.security.ssl;

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
    
    static Alert valueOf(final byte id) {
        for (final Alert al : values()) {
            if (al.id == id) {
                return al;
            }
        }
        return null;
    }
    
    static String nameOf(final byte id) {
        for (final Alert al : values()) {
            if (al.id == id) {
                return al.description;
            }
        }
        return "UNKNOWN ALERT (" + (id & 0xFF) + ")";
    }
    
    SSLException createSSLException(final String reason) {
        return this.createSSLException(reason, null);
    }
    
    SSLException createSSLException(String reason, final Throwable cause) {
        if (reason == null) {
            reason = ((cause != null) ? cause.getMessage() : "");
        }
        SSLException ssle;
        if (cause != null && cause instanceof IOException) {
            ssle = new SSLException(reason);
        }
        else if (this == Alert.UNEXPECTED_MESSAGE) {
            ssle = new SSLProtocolException(reason);
        }
        else if (this.handshakeOnly) {
            ssle = new SSLHandshakeException(reason);
        }
        else {
            ssle = new SSLException(reason);
        }
        if (cause != null) {
            ssle.initCause(cause);
        }
        return ssle;
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
        
        static Level valueOf(final byte level) {
            for (final Level lv : values()) {
                if (lv.level == level) {
                    return lv;
                }
            }
            return null;
        }
        
        static String nameOf(final byte level) {
            for (final Level lv : values()) {
                if (lv.level == level) {
                    return lv.description;
                }
            }
            return "UNKNOWN ALERT LEVEL (" + (level & 0xFF) + ")";
        }
    }
    
    private static final class AlertMessage
    {
        private final byte level;
        private final byte id;
        
        AlertMessage(final TransportContext context, final ByteBuffer m) throws IOException {
            if (m.remaining() != 2) {
                throw context.fatal(Alert.ILLEGAL_PARAMETER, "Invalid Alert message: no sufficient data");
            }
            this.level = m.get();
            this.id = m.get();
        }
        
        @Override
        public String toString() {
            final MessageFormat messageFormat = new MessageFormat("\"Alert\": '{'\n  \"level\"      : \"{0}\",\n  \"description\": \"{1}\"\n'}'", Locale.ENGLISH);
            final Object[] messageFields = { Level.nameOf(this.level), Alert.nameOf(this.id) };
            return messageFormat.format(messageFields);
        }
    }
    
    private static final class AlertConsumer implements SSLConsumer
    {
        @Override
        public void consume(final ConnectionContext context, final ByteBuffer m) throws IOException {
            final TransportContext tc = (TransportContext)context;
            final AlertMessage am = new AlertMessage(tc, m);
            if (SSLLogger.isOn && SSLLogger.isOn("ssl")) {
                SSLLogger.fine("Received alert message", am);
            }
            final Level level = Level.valueOf(am.level);
            Alert alert = Alert.valueOf(am.id);
            if (alert == Alert.CLOSE_NOTIFY) {
                tc.isInputCloseNotified = true;
                tc.closeInbound();
                if (tc.peerUserCanceled) {
                    tc.closeOutbound();
                }
                else if (tc.handshakeContext != null) {
                    throw tc.fatal(Alert.UNEXPECTED_MESSAGE, "Received close_notify during handshake");
                }
            }
            else if (alert == Alert.USER_CANCELED) {
                if (level != Level.WARNING) {
                    throw tc.fatal(alert, "Received fatal close_notify alert", true, null);
                }
                tc.peerUserCanceled = true;
            }
            else {
                if (level != Level.WARNING || alert == null) {
                    String diagnostic;
                    if (alert == null) {
                        alert = Alert.UNEXPECTED_MESSAGE;
                        diagnostic = "Unknown alert description (" + am.id + ")";
                    }
                    else {
                        diagnostic = "Received fatal alert: " + alert.description;
                    }
                    throw tc.fatal(alert, diagnostic, true, null);
                }
                if (alert.handshakeOnly && tc.handshakeContext != null) {
                    if (tc.sslConfig.isClientMode || alert != Alert.NO_CERTIFICATE || tc.sslConfig.clientAuthType != ClientAuthType.CLIENT_AUTH_REQUESTED) {
                        throw tc.fatal(Alert.HANDSHAKE_FAILURE, "received handshake warning: " + alert.description);
                    }
                    tc.handshakeContext.handshakeConsumers.remove(SSLHandshake.CERTIFICATE.id);
                    tc.handshakeContext.handshakeConsumers.remove(SSLHandshake.CERTIFICATE_VERIFY.id);
                }
            }
        }
    }
}
