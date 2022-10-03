package org.openjsse.sun.security.ssl;

import java.util.HashSet;
import java.util.Collection;
import java.security.PrivateKey;
import javax.net.ssl.X509ExtendedKeyManager;
import org.openjsse.javax.net.ssl.SSLEngine;
import java.net.Socket;
import java.security.Principal;
import org.openjsse.javax.net.ssl.SSLSocket;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.Iterator;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.nio.ByteBuffer;
import javax.security.auth.x500.X500Principal;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.ArrayList;

final class CertificateRequest
{
    static final SSLConsumer t10HandshakeConsumer;
    static final HandshakeProducer t10HandshakeProducer;
    static final SSLConsumer t12HandshakeConsumer;
    static final HandshakeProducer t12HandshakeProducer;
    static final SSLConsumer t13HandshakeConsumer;
    static final HandshakeProducer t13HandshakeProducer;
    
    static {
        t10HandshakeConsumer = new T10CertificateRequestConsumer();
        t10HandshakeProducer = new T10CertificateRequestProducer();
        t12HandshakeConsumer = new T12CertificateRequestConsumer();
        t12HandshakeProducer = new T12CertificateRequestProducer();
        t13HandshakeConsumer = new T13CertificateRequestConsumer();
        t13HandshakeProducer = new T13CertificateRequestProducer();
    }
    
    private enum ClientCertificateType
    {
        RSA_SIGN((byte)1, "rsa_sign", "RSA", true), 
        DSS_SIGN((byte)2, "dss_sign", "DSA", true), 
        RSA_FIXED_DH((byte)3, "rsa_fixed_dh"), 
        DSS_FIXED_DH((byte)4, "dss_fixed_dh"), 
        RSA_EPHEMERAL_DH((byte)5, "rsa_ephemeral_dh"), 
        DSS_EPHEMERAL_DH((byte)6, "dss_ephemeral_dh"), 
        FORTEZZA_DMS((byte)20, "fortezza_dms"), 
        ECDSA_SIGN((byte)64, "ecdsa_sign", "EC", JsseJce.isEcAvailable()), 
        RSA_FIXED_ECDH((byte)65, "rsa_fixed_ecdh"), 
        ECDSA_FIXED_ECDH((byte)66, "ecdsa_fixed_ecdh");
        
        private static final byte[] CERT_TYPES;
        final byte id;
        final String name;
        final String keyAlgorithm;
        final boolean isAvailable;
        
        private ClientCertificateType(final byte id, final String name) {
            this(id, name, null, false);
        }
        
        private ClientCertificateType(final byte id, final String name, final String keyAlgorithm, final boolean isAvailable) {
            this.id = id;
            this.name = name;
            this.keyAlgorithm = keyAlgorithm;
            this.isAvailable = isAvailable;
        }
        
        private static String nameOf(final byte id) {
            for (final ClientCertificateType cct : values()) {
                if (cct.id == id) {
                    return cct.name;
                }
            }
            return "UNDEFINED-CLIENT-CERTIFICATE-TYPE(" + id + ")";
        }
        
        private static ClientCertificateType valueOf(final byte id) {
            for (final ClientCertificateType cct : values()) {
                if (cct.id == id) {
                    return cct;
                }
            }
            return null;
        }
        
        private static String[] getKeyTypes(final byte[] ids) {
            final ArrayList<String> keyTypes = new ArrayList<String>(3);
            for (final byte id : ids) {
                final ClientCertificateType cct = valueOf(id);
                if (cct.isAvailable) {
                    keyTypes.add(cct.keyAlgorithm);
                }
            }
            return keyTypes.toArray(new String[0]);
        }
        
        static {
            CERT_TYPES = (JsseJce.isEcAvailable() ? new byte[] { ClientCertificateType.ECDSA_SIGN.id, ClientCertificateType.RSA_SIGN.id, ClientCertificateType.DSS_SIGN.id } : new byte[] { ClientCertificateType.RSA_SIGN.id, ClientCertificateType.DSS_SIGN.id });
        }
    }
    
    static final class T10CertificateRequestMessage extends SSLHandshake.HandshakeMessage
    {
        final byte[] types;
        final List<byte[]> authorities;
        
        T10CertificateRequestMessage(final HandshakeContext handshakeContext, final X509Certificate[] trustedCerts, final CipherSuite.KeyExchange keyExchange) {
            super(handshakeContext);
            this.authorities = new ArrayList<byte[]>(trustedCerts.length);
            for (final X509Certificate cert : trustedCerts) {
                final X500Principal x500Principal = cert.getSubjectX500Principal();
                this.authorities.add(x500Principal.getEncoded());
            }
            this.types = ClientCertificateType.CERT_TYPES;
        }
        
        T10CertificateRequestMessage(final HandshakeContext handshakeContext, final ByteBuffer m) throws IOException {
            super(handshakeContext);
            if (m.remaining() < 4) {
                throw handshakeContext.conContext.fatal(Alert.ILLEGAL_PARAMETER, "Incorrect CertificateRequest message: no sufficient data");
            }
            this.types = Record.getBytes8(m);
            int listLen = Record.getInt16(m);
            if (listLen > m.remaining()) {
                throw handshakeContext.conContext.fatal(Alert.ILLEGAL_PARAMETER, "Incorrect CertificateRequest message:no sufficient data");
            }
            if (listLen > 0) {
                this.authorities = new LinkedList<byte[]>();
                while (listLen > 0) {
                    final byte[] encoded = Record.getBytes16(m);
                    listLen -= 2 + encoded.length;
                    this.authorities.add(encoded);
                }
            }
            else {
                this.authorities = Collections.emptyList();
            }
        }
        
        String[] getKeyTypes() {
            return getKeyTypes(this.types);
        }
        
        X500Principal[] getAuthorities() {
            final List<X500Principal> principals = new ArrayList<X500Principal>(this.authorities.size());
            for (final byte[] encoded : this.authorities) {
                final X500Principal principal = new X500Principal(encoded);
                principals.add(principal);
            }
            return principals.toArray(new X500Principal[0]);
        }
        
        public SSLHandshake handshakeType() {
            return SSLHandshake.CERTIFICATE_REQUEST;
        }
        
        public int messageLength() {
            int len = 1 + this.types.length + 2;
            for (final byte[] encoded : this.authorities) {
                len += encoded.length + 2;
            }
            return len;
        }
        
        public void send(final HandshakeOutStream hos) throws IOException {
            hos.putBytes8(this.types);
            int listLen = 0;
            for (final byte[] encoded : this.authorities) {
                listLen += encoded.length + 2;
            }
            hos.putInt16(listLen);
            for (final byte[] encoded : this.authorities) {
                hos.putBytes16(encoded);
            }
        }
        
        @Override
        public String toString() {
            final MessageFormat messageFormat = new MessageFormat("\"CertificateRequest\": '{'\n  \"certificate types\": {0}\n  \"certificate authorities\": {1}\n'}'", Locale.ENGLISH);
            final List<String> typeNames = new ArrayList<String>(this.types.length);
            for (final byte type : this.types) {
                typeNames.add(nameOf(type));
            }
            final List<String> authorityNames = new ArrayList<String>(this.authorities.size());
            for (final byte[] encoded : this.authorities) {
                final X500Principal principal = new X500Principal(encoded);
                authorityNames.add(principal.toString());
            }
            final Object[] messageFields = { typeNames, authorityNames };
            return messageFormat.format(messageFields);
        }
    }
    
    private static final class T10CertificateRequestProducer implements HandshakeProducer
    {
        @Override
        public byte[] produce(final ConnectionContext context, final SSLHandshake.HandshakeMessage message) throws IOException {
            final ServerHandshakeContext shc = (ServerHandshakeContext)context;
            final X509Certificate[] caCerts = shc.sslContext.getX509TrustManager().getAcceptedIssuers();
            final T10CertificateRequestMessage crm = new T10CertificateRequestMessage(shc, caCerts, shc.negotiatedCipherSuite.keyExchange);
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("Produced CertificateRequest handshake message", crm);
            }
            crm.write(shc.handshakeOutput);
            shc.handshakeOutput.flush();
            shc.handshakeConsumers.put(SSLHandshake.CERTIFICATE.id, SSLHandshake.CERTIFICATE);
            shc.handshakeConsumers.put(SSLHandshake.CERTIFICATE_VERIFY.id, SSLHandshake.CERTIFICATE_VERIFY);
            return null;
        }
    }
    
    private static final class T10CertificateRequestConsumer implements SSLConsumer
    {
        @Override
        public void consume(final ConnectionContext context, final ByteBuffer message) throws IOException {
            final ClientHandshakeContext chc = (ClientHandshakeContext)context;
            chc.handshakeConsumers.remove(SSLHandshake.CERTIFICATE_REQUEST.id);
            final SSLConsumer certStatCons = chc.handshakeConsumers.remove(SSLHandshake.CERTIFICATE_STATUS.id);
            if (certStatCons != null) {
                CertificateStatus.handshakeAbsence.absent(context, null);
            }
            final T10CertificateRequestMessage crm = new T10CertificateRequestMessage(chc, message);
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("Consuming CertificateRequest handshake message", crm);
            }
            chc.handshakeProducers.put(SSLHandshake.CERTIFICATE.id, SSLHandshake.CERTIFICATE);
            final X509ExtendedKeyManager km = chc.sslContext.getX509KeyManager();
            String clientAlias = null;
            if (chc.conContext.transport instanceof SSLSocketImpl) {
                clientAlias = km.chooseClientAlias(crm.getKeyTypes(), crm.getAuthorities(), (Socket)chc.conContext.transport);
            }
            else if (chc.conContext.transport instanceof SSLEngineImpl) {
                clientAlias = km.chooseEngineClientAlias(crm.getKeyTypes(), crm.getAuthorities(), (javax.net.ssl.SSLEngine)chc.conContext.transport);
            }
            if (clientAlias == null) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.warning("No available client authentication", new Object[0]);
                }
                return;
            }
            final PrivateKey clientPrivateKey = km.getPrivateKey(clientAlias);
            if (clientPrivateKey == null) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.warning("No available client private key", new Object[0]);
                }
                return;
            }
            final X509Certificate[] clientCerts = km.getCertificateChain(clientAlias);
            if (clientCerts == null || clientCerts.length == 0) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.warning("No available client certificate", new Object[0]);
                }
                return;
            }
            chc.handshakePossessions.add(new X509Authentication.X509Possession(clientPrivateKey, clientCerts));
            chc.handshakeProducers.put(SSLHandshake.CERTIFICATE_VERIFY.id, SSLHandshake.CERTIFICATE_VERIFY);
        }
    }
    
    static final class T12CertificateRequestMessage extends SSLHandshake.HandshakeMessage
    {
        final byte[] types;
        final int[] algorithmIds;
        final List<byte[]> authorities;
        
        T12CertificateRequestMessage(final HandshakeContext handshakeContext, final X509Certificate[] trustedCerts, final CipherSuite.KeyExchange keyExchange, final List<SignatureScheme> signatureSchemes) throws IOException {
            super(handshakeContext);
            this.types = ClientCertificateType.CERT_TYPES;
            if (signatureSchemes == null || signatureSchemes.isEmpty()) {
                throw handshakeContext.conContext.fatal(Alert.ILLEGAL_PARAMETER, "No signature algorithms specified for CertificateRequest hanshake message");
            }
            this.algorithmIds = new int[signatureSchemes.size()];
            int i = 0;
            for (final SignatureScheme scheme : signatureSchemes) {
                this.algorithmIds[i++] = scheme.id;
            }
            this.authorities = new ArrayList<byte[]>(trustedCerts.length);
            for (final X509Certificate cert : trustedCerts) {
                final X500Principal x500Principal = cert.getSubjectX500Principal();
                this.authorities.add(x500Principal.getEncoded());
            }
        }
        
        T12CertificateRequestMessage(final HandshakeContext handshakeContext, final ByteBuffer m) throws IOException {
            super(handshakeContext);
            if (m.remaining() < 8) {
                throw handshakeContext.conContext.fatal(Alert.ILLEGAL_PARAMETER, "Invalid CertificateRequest handshake message: no sufficient data");
            }
            this.types = Record.getBytes8(m);
            if (m.remaining() < 6) {
                throw handshakeContext.conContext.fatal(Alert.ILLEGAL_PARAMETER, "Invalid CertificateRequest handshake message: no sufficient data");
            }
            final byte[] algs = Record.getBytes16(m);
            if (algs == null || algs.length == 0 || (algs.length & 0x1) != 0x0) {
                throw handshakeContext.conContext.fatal(Alert.ILLEGAL_PARAMETER, "Invalid CertificateRequest handshake message: incomplete signature algorithms");
            }
            this.algorithmIds = new int[algs.length >> 1];
            byte hash;
            byte sign;
            for (int i = 0, j = 0; i < algs.length; hash = algs[i++], sign = algs[i++], this.algorithmIds[j++] = ((hash & 0xFF) << 8 | (sign & 0xFF))) {}
            if (m.remaining() < 2) {
                throw handshakeContext.conContext.fatal(Alert.ILLEGAL_PARAMETER, "Invalid CertificateRequest handshake message: no sufficient data");
            }
            int listLen = Record.getInt16(m);
            if (listLen > m.remaining()) {
                throw handshakeContext.conContext.fatal(Alert.ILLEGAL_PARAMETER, "Invalid CertificateRequest message: no sufficient data");
            }
            if (listLen > 0) {
                this.authorities = new LinkedList<byte[]>();
                while (listLen > 0) {
                    final byte[] encoded = Record.getBytes16(m);
                    listLen -= 2 + encoded.length;
                    this.authorities.add(encoded);
                }
            }
            else {
                this.authorities = Collections.emptyList();
            }
        }
        
        String[] getKeyTypes() {
            return getKeyTypes(this.types);
        }
        
        X500Principal[] getAuthorities() {
            final List<X500Principal> principals = new ArrayList<X500Principal>(this.authorities.size());
            for (final byte[] encoded : this.authorities) {
                final X500Principal principal = new X500Principal(encoded);
                principals.add(principal);
            }
            return principals.toArray(new X500Principal[0]);
        }
        
        public SSLHandshake handshakeType() {
            return SSLHandshake.CERTIFICATE_REQUEST;
        }
        
        public int messageLength() {
            int len = 1 + this.types.length + 2 + (this.algorithmIds.length << 1) + 2;
            for (final byte[] encoded : this.authorities) {
                len += encoded.length + 2;
            }
            return len;
        }
        
        public void send(final HandshakeOutStream hos) throws IOException {
            hos.putBytes8(this.types);
            int listLen = 0;
            for (final byte[] encoded : this.authorities) {
                listLen += encoded.length + 2;
            }
            hos.putInt16(this.algorithmIds.length << 1);
            for (final int algorithmId : this.algorithmIds) {
                hos.putInt16(algorithmId);
            }
            hos.putInt16(listLen);
            for (final byte[] encoded : this.authorities) {
                hos.putBytes16(encoded);
            }
        }
        
        @Override
        public String toString() {
            final MessageFormat messageFormat = new MessageFormat("\"CertificateRequest\": '{'\n  \"certificate types\": {0}\n  \"supported signature algorithms\": {1}\n  \"certificate authorities\": {2}\n'}'", Locale.ENGLISH);
            final List<String> typeNames = new ArrayList<String>(this.types.length);
            for (final byte type : this.types) {
                typeNames.add(nameOf(type));
            }
            final List<String> algorithmNames = new ArrayList<String>(this.algorithmIds.length);
            for (final int algorithmId : this.algorithmIds) {
                algorithmNames.add(SignatureScheme.nameOf(algorithmId));
            }
            final List<String> authorityNames = new ArrayList<String>(this.authorities.size());
            for (final byte[] encoded : this.authorities) {
                final X500Principal principal = new X500Principal(encoded);
                authorityNames.add(principal.toString());
            }
            final Object[] messageFields = { typeNames, algorithmNames, authorityNames };
            return messageFormat.format(messageFields);
        }
    }
    
    private static final class T12CertificateRequestProducer implements HandshakeProducer
    {
        @Override
        public byte[] produce(final ConnectionContext context, final SSLHandshake.HandshakeMessage message) throws IOException {
            final ServerHandshakeContext shc = (ServerHandshakeContext)context;
            if (shc.localSupportedSignAlgs == null) {
                shc.localSupportedSignAlgs = SignatureScheme.getSupportedAlgorithms(shc.sslConfig, shc.algorithmConstraints, shc.activeProtocols);
            }
            if (shc.localSupportedSignAlgs == null || shc.localSupportedSignAlgs.isEmpty()) {
                throw shc.conContext.fatal(Alert.HANDSHAKE_FAILURE, "No supported signature algorithm");
            }
            final X509Certificate[] caCerts = shc.sslContext.getX509TrustManager().getAcceptedIssuers();
            final T12CertificateRequestMessage crm = new T12CertificateRequestMessage(shc, caCerts, shc.negotiatedCipherSuite.keyExchange, shc.localSupportedSignAlgs);
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("Produced CertificateRequest handshake message", crm);
            }
            crm.write(shc.handshakeOutput);
            shc.handshakeOutput.flush();
            shc.handshakeConsumers.put(SSLHandshake.CERTIFICATE.id, SSLHandshake.CERTIFICATE);
            shc.handshakeConsumers.put(SSLHandshake.CERTIFICATE_VERIFY.id, SSLHandshake.CERTIFICATE_VERIFY);
            return null;
        }
    }
    
    private static final class T12CertificateRequestConsumer implements SSLConsumer
    {
        @Override
        public void consume(final ConnectionContext context, final ByteBuffer message) throws IOException {
            final ClientHandshakeContext chc = (ClientHandshakeContext)context;
            chc.handshakeConsumers.remove(SSLHandshake.CERTIFICATE_REQUEST.id);
            final SSLConsumer certStatCons = chc.handshakeConsumers.remove(SSLHandshake.CERTIFICATE_STATUS.id);
            if (certStatCons != null) {
                CertificateStatus.handshakeAbsence.absent(context, null);
            }
            final T12CertificateRequestMessage crm = new T12CertificateRequestMessage(chc, message);
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("Consuming CertificateRequest handshake message", crm);
            }
            chc.handshakeProducers.put(SSLHandshake.CERTIFICATE.id, SSLHandshake.CERTIFICATE);
            final List<SignatureScheme> sss = new LinkedList<SignatureScheme>();
            for (final int id : crm.algorithmIds) {
                final SignatureScheme ss = SignatureScheme.valueOf(id);
                if (ss != null) {
                    sss.add(ss);
                }
            }
            chc.peerRequestedSignatureSchemes = sss;
            chc.peerRequestedCertSignSchemes = sss;
            chc.handshakeSession.setPeerSupportedSignatureAlgorithms(sss);
            chc.peerSupportedAuthorities = crm.getAuthorities();
            final SSLPossession pos = choosePossession(chc);
            if (pos == null) {
                return;
            }
            chc.handshakePossessions.add(pos);
            chc.handshakeProducers.put(SSLHandshake.CERTIFICATE_VERIFY.id, SSLHandshake.CERTIFICATE_VERIFY);
        }
        
        private static SSLPossession choosePossession(final HandshakeContext hc) throws IOException {
            if (hc.peerRequestedCertSignSchemes == null || hc.peerRequestedCertSignSchemes.isEmpty()) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.warning("No signature and hash algorithms in CertificateRequest", new Object[0]);
                }
                return null;
            }
            final Collection<String> checkedKeyTypes = new HashSet<String>();
            for (final SignatureScheme ss : hc.peerRequestedCertSignSchemes) {
                if (checkedKeyTypes.contains(ss.keyAlgorithm)) {
                    if (!SSLLogger.isOn || !SSLLogger.isOn("ssl,handshake")) {
                        continue;
                    }
                    SSLLogger.warning("Unsupported authentication scheme: " + ss.name, new Object[0]);
                }
                else if (SignatureScheme.getPreferableAlgorithm(hc.peerRequestedSignatureSchemes, ss, hc.negotiatedProtocol) == null) {
                    if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                        SSLLogger.warning("Unable to produce CertificateVerify for signature scheme: " + ss.name, new Object[0]);
                    }
                    checkedKeyTypes.add(ss.keyAlgorithm);
                }
                else {
                    final SSLAuthentication ka = X509Authentication.valueOf(ss);
                    if (ka == null) {
                        if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                            SSLLogger.warning("Unsupported authentication scheme: " + ss.name, new Object[0]);
                        }
                        checkedKeyTypes.add(ss.keyAlgorithm);
                    }
                    else {
                        final SSLPossession pos = ka.createPossession(hc);
                        if (pos != null) {
                            return pos;
                        }
                        if (!SSLLogger.isOn || !SSLLogger.isOn("ssl,handshake")) {
                            continue;
                        }
                        SSLLogger.warning("Unavailable authentication scheme: " + ss.name, new Object[0]);
                    }
                }
            }
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.warning("No available authentication scheme", new Object[0]);
            }
            return null;
        }
    }
    
    static final class T13CertificateRequestMessage extends SSLHandshake.HandshakeMessage
    {
        private final byte[] requestContext;
        private final SSLExtensions extensions;
        
        T13CertificateRequestMessage(final HandshakeContext handshakeContext) throws IOException {
            super(handshakeContext);
            this.requestContext = new byte[0];
            this.extensions = new SSLExtensions(this);
        }
        
        T13CertificateRequestMessage(final HandshakeContext handshakeContext, final ByteBuffer m) throws IOException {
            super(handshakeContext);
            if (m.remaining() < 5) {
                throw handshakeContext.conContext.fatal(Alert.ILLEGAL_PARAMETER, "Invalid CertificateRequest handshake message: no sufficient data");
            }
            this.requestContext = Record.getBytes8(m);
            if (m.remaining() < 4) {
                throw handshakeContext.conContext.fatal(Alert.ILLEGAL_PARAMETER, "Invalid CertificateRequest handshake message: no sufficient extensions data");
            }
            final SSLExtension[] enabledExtensions = handshakeContext.sslConfig.getEnabledExtensions(SSLHandshake.CERTIFICATE_REQUEST);
            this.extensions = new SSLExtensions(this, m, enabledExtensions);
        }
        
        @Override
        SSLHandshake handshakeType() {
            return SSLHandshake.CERTIFICATE_REQUEST;
        }
        
        @Override
        int messageLength() {
            return 1 + this.requestContext.length + this.extensions.length();
        }
        
        @Override
        void send(final HandshakeOutStream hos) throws IOException {
            hos.putBytes8(this.requestContext);
            this.extensions.send(hos);
        }
        
        @Override
        public String toString() {
            final MessageFormat messageFormat = new MessageFormat("\"CertificateRequest\": '{'\n  \"certificate_request_context\": \"{0}\",\n  \"extensions\": [\n{1}\n  ]\n'}'", Locale.ENGLISH);
            final Object[] messageFields = { Utilities.toHexString(this.requestContext), Utilities.indent(Utilities.indent(this.extensions.toString())) };
            return messageFormat.format(messageFields);
        }
    }
    
    private static final class T13CertificateRequestProducer implements HandshakeProducer
    {
        @Override
        public byte[] produce(final ConnectionContext context, final SSLHandshake.HandshakeMessage message) throws IOException {
            final ServerHandshakeContext shc = (ServerHandshakeContext)context;
            final T13CertificateRequestMessage crm = new T13CertificateRequestMessage(shc);
            final SSLExtension[] extTypes = shc.sslConfig.getEnabledExtensions(SSLHandshake.CERTIFICATE_REQUEST, shc.negotiatedProtocol);
            crm.extensions.produce(shc, extTypes);
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("Produced CertificateRequest message", crm);
            }
            crm.write(shc.handshakeOutput);
            shc.handshakeOutput.flush();
            shc.certRequestContext = crm.requestContext.clone();
            shc.handshakeConsumers.put(SSLHandshake.CERTIFICATE.id, SSLHandshake.CERTIFICATE);
            shc.handshakeConsumers.put(SSLHandshake.CERTIFICATE_VERIFY.id, SSLHandshake.CERTIFICATE_VERIFY);
            return null;
        }
    }
    
    private static final class T13CertificateRequestConsumer implements SSLConsumer
    {
        @Override
        public void consume(final ConnectionContext context, final ByteBuffer message) throws IOException {
            final ClientHandshakeContext chc = (ClientHandshakeContext)context;
            chc.handshakeConsumers.remove(SSLHandshake.CERTIFICATE_REQUEST.id);
            final T13CertificateRequestMessage crm = new T13CertificateRequestMessage(chc, message);
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("Consuming CertificateRequest handshake message", crm);
            }
            final SSLExtension[] extTypes = chc.sslConfig.getEnabledExtensions(SSLHandshake.CERTIFICATE_REQUEST);
            crm.extensions.consumeOnLoad(chc, extTypes);
            crm.extensions.consumeOnTrade(chc, extTypes);
            chc.certRequestContext = crm.requestContext.clone();
            chc.handshakeProducers.put(SSLHandshake.CERTIFICATE.id, SSLHandshake.CERTIFICATE);
            chc.handshakeProducers.put(SSLHandshake.CERTIFICATE_VERIFY.id, SSLHandshake.CERTIFICATE_VERIFY);
        }
    }
}
