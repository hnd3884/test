package sun.security.ssl;

import java.util.Collections;
import java.util.LinkedList;
import java.util.Collection;
import sun.misc.HexDumpEncoder;
import java.text.MessageFormat;
import java.util.Locale;
import java.nio.ByteBuffer;
import java.io.IOException;

enum SSLExtension implements SSLStringizer
{
    CH_SERVER_NAME(0, "server_name", SSLHandshake.CLIENT_HELLO, ProtocolVersion.PROTOCOLS_TO_13, ServerNameExtension.chNetworkProducer, ServerNameExtension.chOnLoadConsumer, (HandshakeAbsence)null, (HandshakeConsumer)null, (HandshakeAbsence)null, ServerNameExtension.chStringizer), 
    SH_SERVER_NAME(0, "server_name", SSLHandshake.SERVER_HELLO, ProtocolVersion.PROTOCOLS_TO_12, ServerNameExtension.shNetworkProducer, ServerNameExtension.shOnLoadConsumer, (HandshakeAbsence)null, (HandshakeConsumer)null, (HandshakeAbsence)null, ServerNameExtension.shStringizer), 
    EE_SERVER_NAME(0, "server_name", SSLHandshake.ENCRYPTED_EXTENSIONS, ProtocolVersion.PROTOCOLS_OF_13, ServerNameExtension.eeNetworkProducer, ServerNameExtension.eeOnLoadConsumer, (HandshakeAbsence)null, (HandshakeConsumer)null, (HandshakeAbsence)null, ServerNameExtension.shStringizer), 
    CH_MAX_FRAGMENT_LENGTH(1, "max_fragment_length", SSLHandshake.CLIENT_HELLO, ProtocolVersion.PROTOCOLS_TO_13, MaxFragExtension.chNetworkProducer, MaxFragExtension.chOnLoadConsumer, (HandshakeAbsence)null, (HandshakeConsumer)null, (HandshakeAbsence)null, MaxFragExtension.maxFragLenStringizer), 
    SH_MAX_FRAGMENT_LENGTH(1, "max_fragment_length", SSLHandshake.SERVER_HELLO, ProtocolVersion.PROTOCOLS_TO_12, MaxFragExtension.shNetworkProducer, MaxFragExtension.shOnLoadConsumer, (HandshakeAbsence)null, MaxFragExtension.shOnTradeConsumer, (HandshakeAbsence)null, MaxFragExtension.maxFragLenStringizer), 
    EE_MAX_FRAGMENT_LENGTH(1, "max_fragment_length", SSLHandshake.ENCRYPTED_EXTENSIONS, ProtocolVersion.PROTOCOLS_OF_13, MaxFragExtension.eeNetworkProducer, MaxFragExtension.eeOnLoadConsumer, (HandshakeAbsence)null, MaxFragExtension.eeOnTradeConsumer, (HandshakeAbsence)null, MaxFragExtension.maxFragLenStringizer), 
    CLIENT_CERTIFICATE_URL(2, "client_certificate_url"), 
    TRUSTED_CA_KEYS(3, "trusted_ca_keys"), 
    TRUNCATED_HMAC(4, "truncated_hmac"), 
    CH_STATUS_REQUEST(5, "status_request", SSLHandshake.CLIENT_HELLO, ProtocolVersion.PROTOCOLS_TO_13, CertStatusExtension.chNetworkProducer, CertStatusExtension.chOnLoadConsumer, (HandshakeAbsence)null, (HandshakeConsumer)null, (HandshakeAbsence)null, CertStatusExtension.certStatusReqStringizer), 
    SH_STATUS_REQUEST(5, "status_request", SSLHandshake.SERVER_HELLO, ProtocolVersion.PROTOCOLS_TO_12, CertStatusExtension.shNetworkProducer, CertStatusExtension.shOnLoadConsumer, (HandshakeAbsence)null, (HandshakeConsumer)null, (HandshakeAbsence)null, CertStatusExtension.certStatusReqStringizer), 
    CR_STATUS_REQUEST(5, "status_request"), 
    CT_STATUS_REQUEST(5, "status_request", SSLHandshake.CERTIFICATE, ProtocolVersion.PROTOCOLS_OF_13, CertStatusExtension.ctNetworkProducer, CertStatusExtension.ctOnLoadConsumer, (HandshakeAbsence)null, (HandshakeConsumer)null, (HandshakeAbsence)null, CertStatusExtension.certStatusRespStringizer), 
    USER_MAPPING(6, "user_mapping"), 
    CLIENT_AUTHZ(7, "client_authz"), 
    SERVER_AUTHZ(8, "server_authz"), 
    CERT_TYPE(9, "cert_type"), 
    CH_SUPPORTED_GROUPS(10, "supported_groups", SSLHandshake.CLIENT_HELLO, ProtocolVersion.PROTOCOLS_TO_13, SupportedGroupsExtension.chNetworkProducer, SupportedGroupsExtension.chOnLoadConsumer, (HandshakeAbsence)null, (HandshakeConsumer)null, SupportedGroupsExtension.chOnTradAbsence, SupportedGroupsExtension.sgsStringizer), 
    EE_SUPPORTED_GROUPS(10, "supported_groups", SSLHandshake.ENCRYPTED_EXTENSIONS, ProtocolVersion.PROTOCOLS_OF_13, SupportedGroupsExtension.eeNetworkProducer, SupportedGroupsExtension.eeOnLoadConsumer, (HandshakeAbsence)null, (HandshakeConsumer)null, (HandshakeAbsence)null, SupportedGroupsExtension.sgsStringizer), 
    CH_EC_POINT_FORMATS(11, "ec_point_formats", SSLHandshake.CLIENT_HELLO, ProtocolVersion.PROTOCOLS_TO_12, ECPointFormatsExtension.chNetworkProducer, ECPointFormatsExtension.chOnLoadConsumer, (HandshakeAbsence)null, (HandshakeConsumer)null, (HandshakeAbsence)null, ECPointFormatsExtension.epfStringizer), 
    SH_EC_POINT_FORMATS(11, "ec_point_formats", SSLHandshake.SERVER_HELLO, ProtocolVersion.PROTOCOLS_TO_12, (HandshakeProducer)null, ECPointFormatsExtension.shOnLoadConsumer, (HandshakeAbsence)null, (HandshakeConsumer)null, (HandshakeAbsence)null, ECPointFormatsExtension.epfStringizer), 
    SRP(12, "srp"), 
    CH_SIGNATURE_ALGORITHMS(13, "signature_algorithms", SSLHandshake.CLIENT_HELLO, ProtocolVersion.PROTOCOLS_12_13, SignatureAlgorithmsExtension.chNetworkProducer, SignatureAlgorithmsExtension.chOnLoadConsumer, SignatureAlgorithmsExtension.chOnLoadAbsence, SignatureAlgorithmsExtension.chOnTradeConsumer, SignatureAlgorithmsExtension.chOnTradeAbsence, SignatureAlgorithmsExtension.ssStringizer), 
    CR_SIGNATURE_ALGORITHMS(13, "signature_algorithms", SSLHandshake.CERTIFICATE_REQUEST, ProtocolVersion.PROTOCOLS_OF_13, SignatureAlgorithmsExtension.crNetworkProducer, SignatureAlgorithmsExtension.crOnLoadConsumer, SignatureAlgorithmsExtension.crOnLoadAbsence, SignatureAlgorithmsExtension.crOnTradeConsumer, (HandshakeAbsence)null, SignatureAlgorithmsExtension.ssStringizer), 
    CH_SIGNATURE_ALGORITHMS_CERT(50, "signature_algorithms_cert", SSLHandshake.CLIENT_HELLO, ProtocolVersion.PROTOCOLS_12_13, CertSignAlgsExtension.chNetworkProducer, CertSignAlgsExtension.chOnLoadConsumer, (HandshakeAbsence)null, CertSignAlgsExtension.chOnTradeConsumer, (HandshakeAbsence)null, CertSignAlgsExtension.ssStringizer), 
    CR_SIGNATURE_ALGORITHMS_CERT(50, "signature_algorithms_cert", SSLHandshake.CERTIFICATE_REQUEST, ProtocolVersion.PROTOCOLS_OF_13, CertSignAlgsExtension.crNetworkProducer, CertSignAlgsExtension.crOnLoadConsumer, (HandshakeAbsence)null, CertSignAlgsExtension.crOnTradeConsumer, (HandshakeAbsence)null, CertSignAlgsExtension.ssStringizer), 
    USE_SRTP(14, "use_srtp"), 
    HEARTBEAT(14, "heartbeat"), 
    CH_ALPN(16, "application_layer_protocol_negotiation", SSLHandshake.CLIENT_HELLO, ProtocolVersion.PROTOCOLS_TO_13, AlpnExtension.chNetworkProducer, AlpnExtension.chOnLoadConsumer, AlpnExtension.chOnLoadAbsence, (HandshakeConsumer)null, (HandshakeAbsence)null, AlpnExtension.alpnStringizer), 
    SH_ALPN(16, "application_layer_protocol_negotiation", SSLHandshake.SERVER_HELLO, ProtocolVersion.PROTOCOLS_TO_12, AlpnExtension.shNetworkProducer, AlpnExtension.shOnLoadConsumer, AlpnExtension.shOnLoadAbsence, (HandshakeConsumer)null, (HandshakeAbsence)null, AlpnExtension.alpnStringizer), 
    EE_ALPN(16, "application_layer_protocol_negotiation", SSLHandshake.ENCRYPTED_EXTENSIONS, ProtocolVersion.PROTOCOLS_OF_13, AlpnExtension.shNetworkProducer, AlpnExtension.shOnLoadConsumer, AlpnExtension.shOnLoadAbsence, (HandshakeConsumer)null, (HandshakeAbsence)null, AlpnExtension.alpnStringizer), 
    CH_STATUS_REQUEST_V2(17, "status_request_v2", SSLHandshake.CLIENT_HELLO, ProtocolVersion.PROTOCOLS_TO_12, CertStatusExtension.chV2NetworkProducer, CertStatusExtension.chV2OnLoadConsumer, (HandshakeAbsence)null, (HandshakeConsumer)null, (HandshakeAbsence)null, CertStatusExtension.certStatusReqV2Stringizer), 
    SH_STATUS_REQUEST_V2(17, "status_request_v2", SSLHandshake.SERVER_HELLO, ProtocolVersion.PROTOCOLS_TO_12, CertStatusExtension.shV2NetworkProducer, CertStatusExtension.shV2OnLoadConsumer, (HandshakeAbsence)null, (HandshakeConsumer)null, (HandshakeAbsence)null, CertStatusExtension.certStatusReqV2Stringizer), 
    SIGNED_CERT_TIMESTAMP(18, "signed_certificate_timestamp"), 
    CLIENT_CERT_TYPE(19, "padding"), 
    SERVER_CERT_TYPE(20, "server_certificate_type"), 
    PADDING(21, "client_certificate_type"), 
    ENCRYPT_THEN_MAC(22, "encrypt_then_mac"), 
    CH_EXTENDED_MASTER_SECRET(23, "extended_master_secret", SSLHandshake.CLIENT_HELLO, ProtocolVersion.PROTOCOLS_TO_12, ExtendedMasterSecretExtension.chNetworkProducer, ExtendedMasterSecretExtension.chOnLoadConsumer, ExtendedMasterSecretExtension.chOnLoadAbsence, (HandshakeConsumer)null, (HandshakeAbsence)null, ExtendedMasterSecretExtension.emsStringizer), 
    SH_EXTENDED_MASTER_SECRET(23, "extended_master_secret", SSLHandshake.SERVER_HELLO, ProtocolVersion.PROTOCOLS_TO_12, ExtendedMasterSecretExtension.shNetworkProducer, ExtendedMasterSecretExtension.shOnLoadConsumer, ExtendedMasterSecretExtension.shOnLoadAbsence, (HandshakeConsumer)null, (HandshakeAbsence)null, ExtendedMasterSecretExtension.emsStringizer), 
    TOKEN_BINDING(24, "token_binding "), 
    CACHED_INFO(25, "cached_info"), 
    SESSION_TICKET(35, "session_ticket"), 
    CH_EARLY_DATA(42, "early_data"), 
    EE_EARLY_DATA(42, "early_data"), 
    NST_EARLY_DATA(42, "early_data"), 
    CH_SUPPORTED_VERSIONS(43, "supported_versions", SSLHandshake.CLIENT_HELLO, ProtocolVersion.PROTOCOLS_TO_13, SupportedVersionsExtension.chNetworkProducer, SupportedVersionsExtension.chOnLoadConsumer, (HandshakeAbsence)null, (HandshakeConsumer)null, (HandshakeAbsence)null, SupportedVersionsExtension.chStringizer), 
    SH_SUPPORTED_VERSIONS(43, "supported_versions", SSLHandshake.SERVER_HELLO, ProtocolVersion.PROTOCOLS_OF_13, SupportedVersionsExtension.shNetworkProducer, SupportedVersionsExtension.shOnLoadConsumer, (HandshakeAbsence)null, (HandshakeConsumer)null, (HandshakeAbsence)null, SupportedVersionsExtension.shStringizer), 
    HRR_SUPPORTED_VERSIONS(43, "supported_versions", SSLHandshake.HELLO_RETRY_REQUEST, ProtocolVersion.PROTOCOLS_OF_13, SupportedVersionsExtension.hrrNetworkProducer, SupportedVersionsExtension.hrrOnLoadConsumer, (HandshakeAbsence)null, (HandshakeConsumer)null, (HandshakeAbsence)null, SupportedVersionsExtension.hrrStringizer), 
    MH_SUPPORTED_VERSIONS(43, "supported_versions", SSLHandshake.MESSAGE_HASH, ProtocolVersion.PROTOCOLS_OF_13, SupportedVersionsExtension.hrrReproducer, (ExtensionConsumer)null, (HandshakeAbsence)null, (HandshakeConsumer)null, (HandshakeAbsence)null, SupportedVersionsExtension.hrrStringizer), 
    CH_COOKIE(44, "cookie", SSLHandshake.CLIENT_HELLO, ProtocolVersion.PROTOCOLS_OF_13, CookieExtension.chNetworkProducer, CookieExtension.chOnLoadConsumer, (HandshakeAbsence)null, CookieExtension.chOnTradeConsumer, (HandshakeAbsence)null, (SSLStringizer)CookieExtension.cookieStringizer), 
    HRR_COOKIE(44, "cookie", SSLHandshake.HELLO_RETRY_REQUEST, ProtocolVersion.PROTOCOLS_OF_13, CookieExtension.hrrNetworkProducer, CookieExtension.hrrOnLoadConsumer, (HandshakeAbsence)null, (HandshakeConsumer)null, (HandshakeAbsence)null, (SSLStringizer)CookieExtension.cookieStringizer), 
    MH_COOKIE(44, "cookie", SSLHandshake.MESSAGE_HASH, ProtocolVersion.PROTOCOLS_OF_13, CookieExtension.hrrNetworkReproducer, (ExtensionConsumer)null, (HandshakeAbsence)null, (HandshakeConsumer)null, (HandshakeAbsence)null, (SSLStringizer)CookieExtension.cookieStringizer), 
    PSK_KEY_EXCHANGE_MODES(45, "psk_key_exchange_modes", SSLHandshake.CLIENT_HELLO, ProtocolVersion.PROTOCOLS_OF_13, PskKeyExchangeModesExtension.chNetworkProducer, PskKeyExchangeModesExtension.chOnLoadConsumer, PskKeyExchangeModesExtension.chOnLoadAbsence, (HandshakeConsumer)null, PskKeyExchangeModesExtension.chOnTradeAbsence, PskKeyExchangeModesExtension.pkemStringizer), 
    CH_CERTIFICATE_AUTHORITIES(47, "certificate_authorities", SSLHandshake.CLIENT_HELLO, ProtocolVersion.PROTOCOLS_OF_13, CertificateAuthoritiesExtension.chNetworkProducer, CertificateAuthoritiesExtension.chOnLoadConsumer, (HandshakeAbsence)null, (HandshakeConsumer)null, (HandshakeAbsence)null, CertificateAuthoritiesExtension.ssStringizer), 
    CR_CERTIFICATE_AUTHORITIES(47, "certificate_authorities", SSLHandshake.CERTIFICATE_REQUEST, ProtocolVersion.PROTOCOLS_OF_13, CertificateAuthoritiesExtension.crNetworkProducer, CertificateAuthoritiesExtension.crOnLoadConsumer, (HandshakeAbsence)null, (HandshakeConsumer)null, (HandshakeAbsence)null, CertificateAuthoritiesExtension.ssStringizer), 
    OID_FILTERS(48, "oid_filters"), 
    POST_HANDSHAKE_AUTH(48, "post_handshake_auth"), 
    CH_KEY_SHARE(51, "key_share", SSLHandshake.CLIENT_HELLO, ProtocolVersion.PROTOCOLS_OF_13, KeyShareExtension.chNetworkProducer, KeyShareExtension.chOnLoadConsumer, (HandshakeAbsence)null, (HandshakeConsumer)null, KeyShareExtension.chOnTradAbsence, KeyShareExtension.chStringizer), 
    SH_KEY_SHARE(51, "key_share", SSLHandshake.SERVER_HELLO, ProtocolVersion.PROTOCOLS_OF_13, KeyShareExtension.shNetworkProducer, KeyShareExtension.shOnLoadConsumer, KeyShareExtension.shOnLoadAbsence, (HandshakeConsumer)null, (HandshakeAbsence)null, KeyShareExtension.shStringizer), 
    HRR_KEY_SHARE(51, "key_share", SSLHandshake.HELLO_RETRY_REQUEST, ProtocolVersion.PROTOCOLS_OF_13, KeyShareExtension.hrrNetworkProducer, KeyShareExtension.hrrOnLoadConsumer, (HandshakeAbsence)null, (HandshakeConsumer)null, (HandshakeAbsence)null, KeyShareExtension.hrrStringizer), 
    MH_KEY_SHARE(51, "key_share", SSLHandshake.MESSAGE_HASH, ProtocolVersion.PROTOCOLS_OF_13, KeyShareExtension.hrrNetworkReproducer, (ExtensionConsumer)null, (HandshakeAbsence)null, (HandshakeConsumer)null, (HandshakeAbsence)null, KeyShareExtension.hrrStringizer), 
    CH_RENEGOTIATION_INFO(65281, "renegotiation_info", SSLHandshake.CLIENT_HELLO, ProtocolVersion.PROTOCOLS_TO_12, RenegoInfoExtension.chNetworkProducer, RenegoInfoExtension.chOnLoadConsumer, RenegoInfoExtension.chOnLoadAbsence, (HandshakeConsumer)null, (HandshakeAbsence)null, RenegoInfoExtension.rniStringizer), 
    SH_RENEGOTIATION_INFO(65281, "renegotiation_info", SSLHandshake.SERVER_HELLO, ProtocolVersion.PROTOCOLS_TO_12, RenegoInfoExtension.shNetworkProducer, RenegoInfoExtension.shOnLoadConsumer, RenegoInfoExtension.shOnLoadAbsence, (HandshakeConsumer)null, (HandshakeAbsence)null, RenegoInfoExtension.rniStringizer), 
    CH_PRE_SHARED_KEY(41, "pre_shared_key", SSLHandshake.CLIENT_HELLO, ProtocolVersion.PROTOCOLS_OF_13, PreSharedKeyExtension.chNetworkProducer, PreSharedKeyExtension.chOnLoadConsumer, PreSharedKeyExtension.chOnLoadAbsence, PreSharedKeyExtension.chOnTradeConsumer, PreSharedKeyExtension.chOnTradAbsence, PreSharedKeyExtension.chStringizer), 
    SH_PRE_SHARED_KEY(41, "pre_shared_key", SSLHandshake.SERVER_HELLO, ProtocolVersion.PROTOCOLS_OF_13, PreSharedKeyExtension.shNetworkProducer, PreSharedKeyExtension.shOnLoadConsumer, PreSharedKeyExtension.shOnLoadAbsence, (HandshakeConsumer)null, (HandshakeAbsence)null, PreSharedKeyExtension.shStringizer);
    
    final int id;
    final SSLHandshake handshakeType;
    final String name;
    final ProtocolVersion[] supportedProtocols;
    final HandshakeProducer networkProducer;
    final ExtensionConsumer onLoadConsumer;
    final HandshakeAbsence onLoadAbsence;
    final HandshakeConsumer onTradeConsumer;
    final HandshakeAbsence onTradeAbsence;
    final SSLStringizer stringizer;
    
    private SSLExtension(final int id, final String name) {
        this.id = id;
        this.handshakeType = SSLHandshake.NOT_APPLICABLE;
        this.name = name;
        this.supportedProtocols = new ProtocolVersion[0];
        this.networkProducer = null;
        this.onLoadConsumer = null;
        this.onLoadAbsence = null;
        this.onTradeConsumer = null;
        this.onTradeAbsence = null;
        this.stringizer = null;
    }
    
    private SSLExtension(final int id, final String name, final SSLHandshake handshakeType, final ProtocolVersion[] supportedProtocols, final HandshakeProducer networkProducer, final ExtensionConsumer onLoadConsumer, final HandshakeAbsence onLoadAbsence, final HandshakeConsumer onTradeConsumer, final HandshakeAbsence onTradeAbsence, final SSLStringizer stringizer) {
        this.id = id;
        this.handshakeType = handshakeType;
        this.name = name;
        this.supportedProtocols = supportedProtocols;
        this.networkProducer = networkProducer;
        this.onLoadConsumer = onLoadConsumer;
        this.onLoadAbsence = onLoadAbsence;
        this.onTradeConsumer = onTradeConsumer;
        this.onTradeAbsence = onTradeAbsence;
        this.stringizer = stringizer;
    }
    
    static SSLExtension valueOf(final SSLHandshake sslHandshake, final int n) {
        for (final SSLExtension sslExtension : values()) {
            if (sslExtension.id == n && sslExtension.handshakeType == sslHandshake) {
                return sslExtension;
            }
        }
        return null;
    }
    
    static String nameOf(final int n) {
        for (final SSLExtension sslExtension : values()) {
            if (sslExtension.id == n) {
                return sslExtension.name;
            }
        }
        return "unknown extension";
    }
    
    static boolean isConsumable(final int n) {
        for (final SSLExtension sslExtension : values()) {
            if (sslExtension.id == n && sslExtension.onLoadConsumer != null) {
                return true;
            }
        }
        return false;
    }
    
    public byte[] produce(final ConnectionContext connectionContext, final SSLHandshake.HandshakeMessage handshakeMessage) throws IOException {
        if (this.networkProducer != null) {
            return this.networkProducer.produce(connectionContext, handshakeMessage);
        }
        throw new UnsupportedOperationException("Not yet supported extension producing.");
    }
    
    public void consumeOnLoad(final ConnectionContext connectionContext, final SSLHandshake.HandshakeMessage handshakeMessage, final ByteBuffer byteBuffer) throws IOException {
        if (this.onLoadConsumer != null) {
            this.onLoadConsumer.consume(connectionContext, handshakeMessage, byteBuffer);
            return;
        }
        throw new UnsupportedOperationException("Not yet supported extension loading.");
    }
    
    public void consumeOnTrade(final ConnectionContext connectionContext, final SSLHandshake.HandshakeMessage handshakeMessage) throws IOException {
        if (this.onTradeConsumer != null) {
            this.onTradeConsumer.consume(connectionContext, handshakeMessage);
            return;
        }
        throw new UnsupportedOperationException("Not yet supported extension processing.");
    }
    
    void absentOnLoad(final ConnectionContext connectionContext, final SSLHandshake.HandshakeMessage handshakeMessage) throws IOException {
        if (this.onLoadAbsence != null) {
            this.onLoadAbsence.absent(connectionContext, handshakeMessage);
            return;
        }
        throw new UnsupportedOperationException("Not yet supported extension absence processing.");
    }
    
    void absentOnTrade(final ConnectionContext connectionContext, final SSLHandshake.HandshakeMessage handshakeMessage) throws IOException {
        if (this.onTradeAbsence != null) {
            this.onTradeAbsence.absent(connectionContext, handshakeMessage);
            return;
        }
        throw new UnsupportedOperationException("Not yet supported extension absence processing.");
    }
    
    public boolean isAvailable(final ProtocolVersion protocolVersion) {
        for (int i = 0; i < this.supportedProtocols.length; ++i) {
            if (this.supportedProtocols[i] == protocolVersion) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public String toString() {
        return this.name;
    }
    
    @Override
    public String toString(final ByteBuffer byteBuffer) {
        final MessageFormat messageFormat = new MessageFormat("\"{0} ({1})\": '{'\n{2}\n'}'", Locale.ENGLISH);
        String s;
        if (this.stringizer == null) {
            s = new HexDumpEncoder().encode(byteBuffer.duplicate());
        }
        else {
            s = this.stringizer.toString(byteBuffer);
        }
        return messageFormat.format(new Object[] { this.name, this.id, Utilities.indent(s) });
    }
    
    static final class ClientExtensions
    {
        static final Collection<SSLExtension> defaults;
        
        static {
            final LinkedList list = new LinkedList();
            for (final SSLExtension sslExtension : SSLExtension.values()) {
                if (sslExtension.handshakeType != SSLHandshake.NOT_APPLICABLE) {
                    list.add(sslExtension);
                }
            }
            if (!Utilities.getBooleanProperty("jsse.enableSNIExtension", true)) {
                list.remove(SSLExtension.CH_SERVER_NAME);
            }
            if (!Utilities.getBooleanProperty("jsse.enableMFLNExtension", false) && !Utilities.getBooleanProperty("jsse.enableMFLExtension", false)) {
                list.remove(SSLExtension.CH_MAX_FRAGMENT_LENGTH);
            }
            if (!Utilities.getBooleanProperty("jdk.tls.client.enableCAExtension", false)) {
                list.remove(SSLExtension.CH_CERTIFICATE_AUTHORITIES);
            }
            defaults = Collections.unmodifiableCollection((Collection<?>)list);
        }
    }
    
    static final class ServerExtensions
    {
        static final Collection<SSLExtension> defaults;
        
        static {
            final LinkedList list = new LinkedList();
            for (final SSLExtension sslExtension : SSLExtension.values()) {
                if (sslExtension.handshakeType != SSLHandshake.NOT_APPLICABLE) {
                    list.add(sslExtension);
                }
            }
            defaults = Collections.unmodifiableCollection((Collection<?>)list);
        }
    }
    
    interface SSLExtensionSpec
    {
    }
    
    interface ExtensionConsumer
    {
        void consume(final ConnectionContext p0, final SSLHandshake.HandshakeMessage p1, final ByteBuffer p2) throws IOException;
    }
}
