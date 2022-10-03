package org.openjsse.sun.security.ssl;

import java.security.cert.X509Certificate;
import java.text.MessageFormat;
import java.util.Locale;
import java.io.IOException;
import java.util.ArrayList;
import javax.net.ssl.SSLProtocolException;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.List;
import javax.security.auth.x500.X500Principal;

final class CertificateAuthorityExtension
{
    static final HandshakeProducer chNetworkProducer;
    static final SSLExtension.ExtensionConsumer chOnLoadConsumer;
    static final HandshakeConsumer chOnTradeConsumer;
    static final HandshakeProducer crNetworkProducer;
    static final SSLExtension.ExtensionConsumer crOnLoadConsumer;
    static final HandshakeConsumer crOnTradeConsumer;
    static final SSLStringizer ssStringizer;
    
    static {
        chNetworkProducer = new CHCertificateAuthoritiesProducer();
        chOnLoadConsumer = new CHCertificateAuthoritiesConsumer();
        chOnTradeConsumer = new CHCertificateAuthoritiesUpdate();
        crNetworkProducer = new CRCertificateAuthoritiesProducer();
        crOnLoadConsumer = new CRCertificateAuthoritiesConsumer();
        crOnTradeConsumer = new CRCertificateAuthoritiesUpdate();
        ssStringizer = new CertificateAuthoritiesStringizer();
    }
    
    static final class CertificateAuthoritiesSpec implements SSLExtension.SSLExtensionSpec
    {
        final X500Principal[] authorities;
        
        CertificateAuthoritiesSpec(final List<X500Principal> authorities) {
            if (authorities != null) {
                this.authorities = new X500Principal[authorities.size()];
                int i = 0;
                for (final X500Principal name : authorities) {
                    this.authorities[i++] = name;
                }
            }
            else {
                this.authorities = new X500Principal[0];
            }
        }
        
        CertificateAuthoritiesSpec(final ByteBuffer buffer) throws IOException {
            if (buffer.remaining() < 2) {
                throw new SSLProtocolException("Invalid signature_algorithms: insufficient data");
            }
            final int caLength = Record.getInt16(buffer);
            if (buffer.remaining() != caLength) {
                throw new SSLProtocolException("Invalid certificate_authorities: incorrect data size");
            }
            final ArrayList<X500Principal> dnList = new ArrayList<X500Principal>();
            while (buffer.remaining() > 0) {
                final byte[] dn = Record.getBytes16(buffer);
                final X500Principal ca = new X500Principal(dn);
                dnList.add(ca);
            }
            this.authorities = dnList.toArray(new X500Principal[dnList.size()]);
        }
        
        X500Principal[] getAuthorities() {
            return this.authorities;
        }
        
        @Override
        public String toString() {
            final MessageFormat messageFormat = new MessageFormat("\"certificate authorities\": '['{0}']'", Locale.ENGLISH);
            if (this.authorities == null || this.authorities.length == 0) {
                final Object[] messageFields = { "<no supported certificate authorities specified>" };
                return messageFormat.format(messageFields);
            }
            final StringBuilder builder = new StringBuilder(512);
            boolean isFirst = true;
            for (final X500Principal ca : this.authorities) {
                if (isFirst) {
                    isFirst = false;
                }
                else {
                    builder.append("]; [");
                }
                builder.append(ca);
            }
            final Object[] messageFields2 = { builder.toString() };
            return messageFormat.format(messageFields2);
        }
    }
    
    private static final class CertificateAuthoritiesStringizer implements SSLStringizer
    {
        @Override
        public String toString(final ByteBuffer buffer) {
            try {
                return new CertificateAuthoritiesSpec(buffer).toString();
            }
            catch (final IOException ioe) {
                return ioe.getMessage();
            }
        }
    }
    
    private static final class CHCertificateAuthoritiesProducer implements HandshakeProducer
    {
        private final boolean enableCAExtension;
        private final int maxCAExtensionSize;
        
        private CHCertificateAuthoritiesProducer() {
            this.enableCAExtension = Utilities.getBooleanProperty("org.openjsse.client.enableCAExtension", false);
            this.maxCAExtensionSize = Utilities.getUIntProperty("org.openjsse.client.maxCAExtensionSize", 8192);
        }
        
        @Override
        public byte[] produce(final ConnectionContext context, final SSLHandshake.HandshakeMessage message) throws IOException {
            final ClientHandshakeContext chc = (ClientHandshakeContext)context;
            if (!chc.sslConfig.isAvailable(SSLExtension.CH_CERTIFICATE_AUTHORITIES)) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("Ignore unavailable certificate_authorities extension", new Object[0]);
                }
                return null;
            }
            if (!this.enableCAExtension) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("Ignore disabled certificate_authorities extension", new Object[0]);
                }
                return null;
            }
            if (chc.localSupportedAuthorities == null) {
                final X509Certificate[] caCerts = chc.sslContext.getX509TrustManager().getAcceptedIssuers();
                final ArrayList<X500Principal> authList = new ArrayList<X500Principal>(caCerts.length);
                for (final X509Certificate cert : caCerts) {
                    authList.add(cert.getSubjectX500Principal());
                }
                if (!authList.isEmpty()) {
                    chc.localSupportedAuthorities = authList;
                }
            }
            if (chc.localSupportedAuthorities == null) {
                return null;
            }
            int vectorLen = 0;
            final List<byte[]> authorities = new ArrayList<byte[]>();
            for (final X500Principal ca : chc.localSupportedAuthorities) {
                final byte[] enc = ca.getEncoded();
                final int len = enc.length + 2;
                if (vectorLen + len <= this.maxCAExtensionSize) {
                    vectorLen += len;
                    authorities.add(enc);
                }
            }
            final byte[] extData = new byte[vectorLen + 2];
            final ByteBuffer m = ByteBuffer.wrap(extData);
            Record.putInt16(m, vectorLen);
            for (final byte[] enc2 : authorities) {
                Record.putBytes16(m, enc2);
            }
            chc.handshakeExtensions.put(SSLExtension.CH_CERTIFICATE_AUTHORITIES, new CertificateAuthoritiesSpec(chc.localSupportedAuthorities));
            return extData;
        }
    }
    
    private static final class CHCertificateAuthoritiesConsumer implements SSLExtension.ExtensionConsumer
    {
        @Override
        public void consume(final ConnectionContext context, final SSLHandshake.HandshakeMessage message, final ByteBuffer buffer) throws IOException {
            final ServerHandshakeContext shc = (ServerHandshakeContext)context;
            if (!shc.sslConfig.isAvailable(SSLExtension.CH_CERTIFICATE_AUTHORITIES)) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("Ignore unavailable certificate_authorities extension", new Object[0]);
                }
                return;
            }
            CertificateAuthoritiesSpec spec;
            try {
                spec = new CertificateAuthoritiesSpec(buffer);
            }
            catch (final IOException ioe) {
                throw shc.conContext.fatal(Alert.UNEXPECTED_MESSAGE, ioe);
            }
            shc.handshakeExtensions.put(SSLExtension.CH_CERTIFICATE_AUTHORITIES, spec);
        }
    }
    
    private static final class CHCertificateAuthoritiesUpdate implements HandshakeConsumer
    {
        @Override
        public void consume(final ConnectionContext context, final SSLHandshake.HandshakeMessage message) throws IOException {
            final ServerHandshakeContext shc = (ServerHandshakeContext)context;
            final CertificateAuthoritiesSpec spec = shc.handshakeExtensions.get(SSLExtension.CH_CERTIFICATE_AUTHORITIES);
            if (spec == null) {
                return;
            }
            shc.peerSupportedAuthorities = spec.getAuthorities();
        }
    }
    
    private static final class CRCertificateAuthoritiesProducer implements HandshakeProducer
    {
        private final boolean enableCAExtension;
        private final int maxCAExtensionSize;
        
        private CRCertificateAuthoritiesProducer() {
            this.enableCAExtension = Utilities.getBooleanProperty("org.openjsse.server.enableCAExtension", true);
            this.maxCAExtensionSize = Utilities.getUIntProperty("org.openjsse.server.maxCAExtensionSize", 8192);
        }
        
        @Override
        public byte[] produce(final ConnectionContext context, final SSLHandshake.HandshakeMessage message) throws IOException {
            final ServerHandshakeContext shc = (ServerHandshakeContext)context;
            if (!shc.sslConfig.isAvailable(SSLExtension.CR_CERTIFICATE_AUTHORITIES)) {
                throw shc.conContext.fatal(Alert.MISSING_EXTENSION, "No available certificate_authority extension for client certificate authentication");
            }
            if (!this.enableCAExtension) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("Ignore disabled certificate_authorities extension", new Object[0]);
                }
                return null;
            }
            if (shc.localSupportedAuthorities == null) {
                final X509Certificate[] caCerts = shc.sslContext.getX509TrustManager().getAcceptedIssuers();
                final ArrayList<X500Principal> authList = new ArrayList<X500Principal>(caCerts.length);
                for (final X509Certificate cert : caCerts) {
                    authList.add(cert.getSubjectX500Principal());
                }
                if (!authList.isEmpty()) {
                    shc.localSupportedAuthorities = authList;
                }
            }
            if (shc.localSupportedAuthorities == null) {
                return null;
            }
            int vectorLen = 0;
            final List<byte[]> authorities = new ArrayList<byte[]>();
            for (final X500Principal ca : shc.localSupportedAuthorities) {
                final byte[] enc = ca.getEncoded();
                final int len = enc.length + 2;
                if (vectorLen + len <= this.maxCAExtensionSize) {
                    vectorLen += len;
                    authorities.add(enc);
                }
            }
            final byte[] extData = new byte[vectorLen + 2];
            final ByteBuffer m = ByteBuffer.wrap(extData);
            Record.putInt16(m, vectorLen);
            for (final byte[] enc2 : authorities) {
                Record.putBytes16(m, enc2);
            }
            shc.handshakeExtensions.put(SSLExtension.CR_CERTIFICATE_AUTHORITIES, new CertificateAuthoritiesSpec(shc.localSupportedAuthorities));
            return extData;
        }
    }
    
    private static final class CRCertificateAuthoritiesConsumer implements SSLExtension.ExtensionConsumer
    {
        @Override
        public void consume(final ConnectionContext context, final SSLHandshake.HandshakeMessage message, final ByteBuffer buffer) throws IOException {
            final ClientHandshakeContext chc = (ClientHandshakeContext)context;
            if (!chc.sslConfig.isAvailable(SSLExtension.CR_CERTIFICATE_AUTHORITIES)) {
                throw chc.conContext.fatal(Alert.HANDSHAKE_FAILURE, "No available certificate_authority extension for client certificate authentication");
            }
            CertificateAuthoritiesSpec spec;
            try {
                spec = new CertificateAuthoritiesSpec(buffer);
            }
            catch (final IOException ioe) {
                throw chc.conContext.fatal(Alert.UNEXPECTED_MESSAGE, ioe);
            }
            chc.handshakeExtensions.put(SSLExtension.CR_CERTIFICATE_AUTHORITIES, spec);
        }
    }
    
    private static final class CRCertificateAuthoritiesUpdate implements HandshakeConsumer
    {
        @Override
        public void consume(final ConnectionContext context, final SSLHandshake.HandshakeMessage message) throws IOException {
            final ClientHandshakeContext chc = (ClientHandshakeContext)context;
            final CertificateAuthoritiesSpec spec = chc.handshakeExtensions.get(SSLExtension.CR_CERTIFICATE_AUTHORITIES);
            if (spec == null) {
                return;
            }
            chc.peerSupportedAuthorities = spec.getAuthorities();
        }
    }
}
