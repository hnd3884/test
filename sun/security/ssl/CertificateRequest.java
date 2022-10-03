package sun.security.ssl;

import java.util.HashSet;
import java.util.Collection;
import java.security.PrivateKey;
import javax.net.ssl.X509ExtendedKeyManager;
import javax.net.ssl.SSLEngine;
import java.net.Socket;
import java.security.Principal;
import javax.net.ssl.SSLSocket;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.Iterator;
import javax.security.auth.x500.X500Principal;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.nio.ByteBuffer;
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
        
        private ClientCertificateType(final byte b, final String s2) {
            this(b, s2, null, false);
        }
        
        private ClientCertificateType(final byte id, final String name, final String keyAlgorithm, final boolean isAvailable) {
            this.id = id;
            this.name = name;
            this.keyAlgorithm = keyAlgorithm;
            this.isAvailable = isAvailable;
        }
        
        private static String nameOf(final byte b) {
            for (final ClientCertificateType clientCertificateType : values()) {
                if (clientCertificateType.id == b) {
                    return clientCertificateType.name;
                }
            }
            return "UNDEFINED-CLIENT-CERTIFICATE-TYPE(" + b + ")";
        }
        
        private static ClientCertificateType valueOf(final byte b) {
            for (final ClientCertificateType clientCertificateType : values()) {
                if (clientCertificateType.id == b) {
                    return clientCertificateType;
                }
            }
            return null;
        }
        
        private static String[] getKeyTypes(final byte[] array) {
            final ArrayList list = new ArrayList(3);
            for (int length = array.length, i = 0; i < length; ++i) {
                final ClientCertificateType value = valueOf(array[i]);
                if (value.isAvailable) {
                    list.add(value.keyAlgorithm);
                }
            }
            return list.toArray(new String[0]);
        }
        
        static {
            CERT_TYPES = (JsseJce.isEcAvailable() ? new byte[] { ClientCertificateType.ECDSA_SIGN.id, ClientCertificateType.RSA_SIGN.id, ClientCertificateType.DSS_SIGN.id } : new byte[] { ClientCertificateType.RSA_SIGN.id, ClientCertificateType.DSS_SIGN.id });
        }
    }
    
    static final class T10CertificateRequestMessage extends SSLHandshake.HandshakeMessage
    {
        final byte[] types;
        final List<byte[]> authorities;
        
        T10CertificateRequestMessage(final HandshakeContext handshakeContext, final X509Certificate[] array, final CipherSuite.KeyExchange keyExchange) {
            super(handshakeContext);
            this.authorities = new ArrayList<byte[]>(array.length);
            for (int length = array.length, i = 0; i < length; ++i) {
                this.authorities.add(array[i].getSubjectX500Principal().getEncoded());
            }
            this.types = ClientCertificateType.CERT_TYPES;
        }
        
        T10CertificateRequestMessage(final HandshakeContext handshakeContext, final ByteBuffer byteBuffer) throws IOException {
            super(handshakeContext);
            if (byteBuffer.remaining() < 4) {
                throw handshakeContext.conContext.fatal(Alert.ILLEGAL_PARAMETER, "Incorrect CertificateRequest message: no sufficient data");
            }
            this.types = Record.getBytes8(byteBuffer);
            int i = Record.getInt16(byteBuffer);
            if (i > byteBuffer.remaining()) {
                throw handshakeContext.conContext.fatal(Alert.ILLEGAL_PARAMETER, "Incorrect CertificateRequest message:no sufficient data");
            }
            if (i > 0) {
                this.authorities = new LinkedList<byte[]>();
                while (i > 0) {
                    final byte[] bytes16 = Record.getBytes16(byteBuffer);
                    i -= 2 + bytes16.length;
                    this.authorities.add(bytes16);
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
            final X500Principal[] array = new X500Principal[this.authorities.size()];
            int n = 0;
            final Iterator<byte[]> iterator = this.authorities.iterator();
            while (iterator.hasNext()) {
                array[n++] = new X500Principal(iterator.next());
            }
            return array;
        }
        
        public SSLHandshake handshakeType() {
            return SSLHandshake.CERTIFICATE_REQUEST;
        }
        
        public int messageLength() {
            int n = 1 + this.types.length + 2;
            final Iterator<byte[]> iterator = this.authorities.iterator();
            while (iterator.hasNext()) {
                n += iterator.next().length + 2;
            }
            return n;
        }
        
        public void send(final HandshakeOutStream handshakeOutStream) throws IOException {
            handshakeOutStream.putBytes8(this.types);
            int n = 0;
            final Iterator<byte[]> iterator = this.authorities.iterator();
            while (iterator.hasNext()) {
                n += iterator.next().length + 2;
            }
            handshakeOutStream.putInt16(n);
            final Iterator<byte[]> iterator2 = this.authorities.iterator();
            while (iterator2.hasNext()) {
                handshakeOutStream.putBytes16(iterator2.next());
            }
        }
        
        @Override
        public String toString() {
            final MessageFormat messageFormat = new MessageFormat("\"CertificateRequest\": '{'\n  \"certificate types\": {0}\n  \"certificate authorities\": {1}\n'}'", Locale.ENGLISH);
            final ArrayList list = new ArrayList(this.types.length);
            final byte[] types = this.types;
            for (int length = types.length, i = 0; i < length; ++i) {
                list.add(nameOf(types[i]));
            }
            final ArrayList list2 = new ArrayList(this.authorities.size());
            final Iterator<byte[]> iterator = this.authorities.iterator();
            while (iterator.hasNext()) {
                list2.add(new X500Principal(iterator.next()).toString());
            }
            return messageFormat.format(new Object[] { list, list2 });
        }
    }
    
    private static final class T10CertificateRequestProducer implements HandshakeProducer
    {
        @Override
        public byte[] produce(final ConnectionContext connectionContext, final SSLHandshake.HandshakeMessage handshakeMessage) throws IOException {
            final ServerHandshakeContext serverHandshakeContext = (ServerHandshakeContext)connectionContext;
            final T10CertificateRequestMessage t10CertificateRequestMessage = new T10CertificateRequestMessage(serverHandshakeContext, serverHandshakeContext.sslContext.getX509TrustManager().getAcceptedIssuers(), serverHandshakeContext.negotiatedCipherSuite.keyExchange);
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("Produced CertificateRequest handshake message", t10CertificateRequestMessage);
            }
            t10CertificateRequestMessage.write(serverHandshakeContext.handshakeOutput);
            serverHandshakeContext.handshakeOutput.flush();
            serverHandshakeContext.handshakeConsumers.put(SSLHandshake.CERTIFICATE.id, SSLHandshake.CERTIFICATE);
            serverHandshakeContext.handshakeConsumers.put(SSLHandshake.CERTIFICATE_VERIFY.id, SSLHandshake.CERTIFICATE_VERIFY);
            return null;
        }
    }
    
    private static final class T10CertificateRequestConsumer implements SSLConsumer
    {
        @Override
        public void consume(final ConnectionContext connectionContext, final ByteBuffer byteBuffer) throws IOException {
            final ClientHandshakeContext clientHandshakeContext = (ClientHandshakeContext)connectionContext;
            clientHandshakeContext.handshakeConsumers.remove(SSLHandshake.CERTIFICATE_REQUEST.id);
            if (clientHandshakeContext.handshakeConsumers.remove(SSLHandshake.CERTIFICATE_STATUS.id) != null) {
                CertificateStatus.handshakeAbsence.absent(connectionContext, null);
            }
            final T10CertificateRequestMessage t10CertificateRequestMessage = new T10CertificateRequestMessage(clientHandshakeContext, byteBuffer);
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("Consuming CertificateRequest handshake message", t10CertificateRequestMessage);
            }
            clientHandshakeContext.handshakeProducers.put(SSLHandshake.CERTIFICATE.id, SSLHandshake.CERTIFICATE);
            final X509ExtendedKeyManager x509KeyManager = clientHandshakeContext.sslContext.getX509KeyManager();
            String s = null;
            if (clientHandshakeContext.conContext.transport instanceof SSLSocketImpl) {
                s = x509KeyManager.chooseClientAlias(t10CertificateRequestMessage.getKeyTypes(), t10CertificateRequestMessage.getAuthorities(), (Socket)clientHandshakeContext.conContext.transport);
            }
            else if (clientHandshakeContext.conContext.transport instanceof SSLEngineImpl) {
                s = x509KeyManager.chooseEngineClientAlias(t10CertificateRequestMessage.getKeyTypes(), t10CertificateRequestMessage.getAuthorities(), (SSLEngine)clientHandshakeContext.conContext.transport);
            }
            if (s == null) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.warning("No available client authentication", new Object[0]);
                }
                return;
            }
            final PrivateKey privateKey = x509KeyManager.getPrivateKey(s);
            if (privateKey == null) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.warning("No available client private key", new Object[0]);
                }
                return;
            }
            final X509Certificate[] certificateChain = x509KeyManager.getCertificateChain(s);
            if (certificateChain == null || certificateChain.length == 0) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.warning("No available client certificate", new Object[0]);
                }
                return;
            }
            clientHandshakeContext.handshakePossessions.add(new X509Authentication.X509Possession(privateKey, certificateChain));
            clientHandshakeContext.handshakeProducers.put(SSLHandshake.CERTIFICATE_VERIFY.id, SSLHandshake.CERTIFICATE_VERIFY);
        }
    }
    
    static final class T12CertificateRequestMessage extends SSLHandshake.HandshakeMessage
    {
        final byte[] types;
        final int[] algorithmIds;
        final List<byte[]> authorities;
        
        T12CertificateRequestMessage(final HandshakeContext handshakeContext, final X509Certificate[] array, final CipherSuite.KeyExchange keyExchange, final List<SignatureScheme> list) throws IOException {
            super(handshakeContext);
            this.types = ClientCertificateType.CERT_TYPES;
            if (list == null || list.isEmpty()) {
                throw handshakeContext.conContext.fatal(Alert.ILLEGAL_PARAMETER, "No signature algorithms specified for CertificateRequest hanshake message");
            }
            this.algorithmIds = new int[list.size()];
            int n = 0;
            final Iterator iterator = list.iterator();
            while (iterator.hasNext()) {
                this.algorithmIds[n++] = ((SignatureScheme)iterator.next()).id;
            }
            this.authorities = new ArrayList<byte[]>(array.length);
            for (int length = array.length, i = 0; i < length; ++i) {
                this.authorities.add(array[i].getSubjectX500Principal().getEncoded());
            }
        }
        
        T12CertificateRequestMessage(final HandshakeContext handshakeContext, final ByteBuffer byteBuffer) throws IOException {
            super(handshakeContext);
            if (byteBuffer.remaining() < 8) {
                throw handshakeContext.conContext.fatal(Alert.ILLEGAL_PARAMETER, "Invalid CertificateRequest handshake message: no sufficient data");
            }
            this.types = Record.getBytes8(byteBuffer);
            if (byteBuffer.remaining() < 6) {
                throw handshakeContext.conContext.fatal(Alert.ILLEGAL_PARAMETER, "Invalid CertificateRequest handshake message: no sufficient data");
            }
            final byte[] bytes16 = Record.getBytes16(byteBuffer);
            if (bytes16 == null || bytes16.length == 0 || (bytes16.length & 0x1) != 0x0) {
                throw handshakeContext.conContext.fatal(Alert.ILLEGAL_PARAMETER, "Invalid CertificateRequest handshake message: incomplete signature algorithms");
            }
            this.algorithmIds = new int[bytes16.length >> 1];
            for (int i = 0, n = 0; i < bytes16.length; this.algorithmIds[n++] = ((bytes16[i++] & 0xFF) << 8 | (bytes16[i++] & 0xFF))) {}
            if (byteBuffer.remaining() < 2) {
                throw handshakeContext.conContext.fatal(Alert.ILLEGAL_PARAMETER, "Invalid CertificateRequest handshake message: no sufficient data");
            }
            int j = Record.getInt16(byteBuffer);
            if (j > byteBuffer.remaining()) {
                throw handshakeContext.conContext.fatal(Alert.ILLEGAL_PARAMETER, "Invalid CertificateRequest message: no sufficient data");
            }
            if (j > 0) {
                this.authorities = new LinkedList<byte[]>();
                while (j > 0) {
                    final byte[] bytes17 = Record.getBytes16(byteBuffer);
                    j -= 2 + bytes17.length;
                    this.authorities.add(bytes17);
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
            final X500Principal[] array = new X500Principal[this.authorities.size()];
            int n = 0;
            final Iterator<byte[]> iterator = this.authorities.iterator();
            while (iterator.hasNext()) {
                array[n++] = new X500Principal(iterator.next());
            }
            return array;
        }
        
        public SSLHandshake handshakeType() {
            return SSLHandshake.CERTIFICATE_REQUEST;
        }
        
        public int messageLength() {
            int n = 1 + this.types.length + 2 + (this.algorithmIds.length << 1) + 2;
            final Iterator<byte[]> iterator = this.authorities.iterator();
            while (iterator.hasNext()) {
                n += iterator.next().length + 2;
            }
            return n;
        }
        
        public void send(final HandshakeOutStream handshakeOutStream) throws IOException {
            handshakeOutStream.putBytes8(this.types);
            int n = 0;
            final Iterator<byte[]> iterator = this.authorities.iterator();
            while (iterator.hasNext()) {
                n += iterator.next().length + 2;
            }
            handshakeOutStream.putInt16(this.algorithmIds.length << 1);
            final int[] algorithmIds = this.algorithmIds;
            for (int length = algorithmIds.length, i = 0; i < length; ++i) {
                handshakeOutStream.putInt16(algorithmIds[i]);
            }
            handshakeOutStream.putInt16(n);
            final Iterator<byte[]> iterator2 = this.authorities.iterator();
            while (iterator2.hasNext()) {
                handshakeOutStream.putBytes16(iterator2.next());
            }
        }
        
        @Override
        public String toString() {
            final MessageFormat messageFormat = new MessageFormat("\"CertificateRequest\": '{'\n  \"certificate types\": {0}\n  \"supported signature algorithms\": {1}\n  \"certificate authorities\": {2}\n'}'", Locale.ENGLISH);
            final ArrayList list = new ArrayList(this.types.length);
            final byte[] types = this.types;
            for (int length = types.length, i = 0; i < length; ++i) {
                list.add(nameOf(types[i]));
            }
            final ArrayList list2 = new ArrayList(this.algorithmIds.length);
            final int[] algorithmIds = this.algorithmIds;
            for (int length2 = algorithmIds.length, j = 0; j < length2; ++j) {
                list2.add(SignatureScheme.nameOf(algorithmIds[j]));
            }
            final ArrayList list3 = new ArrayList(this.authorities.size());
            final Iterator<byte[]> iterator = this.authorities.iterator();
            while (iterator.hasNext()) {
                list3.add(new X500Principal(iterator.next()).toString());
            }
            return messageFormat.format(new Object[] { list, list2, list3 });
        }
    }
    
    private static final class T12CertificateRequestProducer implements HandshakeProducer
    {
        @Override
        public byte[] produce(final ConnectionContext connectionContext, final SSLHandshake.HandshakeMessage handshakeMessage) throws IOException {
            final ServerHandshakeContext serverHandshakeContext = (ServerHandshakeContext)connectionContext;
            if (serverHandshakeContext.localSupportedSignAlgs == null) {
                serverHandshakeContext.localSupportedSignAlgs = SignatureScheme.getSupportedAlgorithms(serverHandshakeContext.sslConfig, serverHandshakeContext.algorithmConstraints, serverHandshakeContext.activeProtocols);
            }
            if (serverHandshakeContext.localSupportedSignAlgs == null || serverHandshakeContext.localSupportedSignAlgs.isEmpty()) {
                throw serverHandshakeContext.conContext.fatal(Alert.HANDSHAKE_FAILURE, "No supported signature algorithm");
            }
            final T12CertificateRequestMessage t12CertificateRequestMessage = new T12CertificateRequestMessage(serverHandshakeContext, serverHandshakeContext.sslContext.getX509TrustManager().getAcceptedIssuers(), serverHandshakeContext.negotiatedCipherSuite.keyExchange, serverHandshakeContext.localSupportedSignAlgs);
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("Produced CertificateRequest handshake message", t12CertificateRequestMessage);
            }
            t12CertificateRequestMessage.write(serverHandshakeContext.handshakeOutput);
            serverHandshakeContext.handshakeOutput.flush();
            serverHandshakeContext.handshakeConsumers.put(SSLHandshake.CERTIFICATE.id, SSLHandshake.CERTIFICATE);
            serverHandshakeContext.handshakeConsumers.put(SSLHandshake.CERTIFICATE_VERIFY.id, SSLHandshake.CERTIFICATE_VERIFY);
            return null;
        }
    }
    
    private static final class T12CertificateRequestConsumer implements SSLConsumer
    {
        @Override
        public void consume(final ConnectionContext connectionContext, final ByteBuffer byteBuffer) throws IOException {
            final ClientHandshakeContext clientHandshakeContext = (ClientHandshakeContext)connectionContext;
            clientHandshakeContext.handshakeConsumers.remove(SSLHandshake.CERTIFICATE_REQUEST.id);
            if (clientHandshakeContext.handshakeConsumers.remove(SSLHandshake.CERTIFICATE_STATUS.id) != null) {
                CertificateStatus.handshakeAbsence.absent(connectionContext, null);
            }
            final T12CertificateRequestMessage t12CertificateRequestMessage = new T12CertificateRequestMessage(clientHandshakeContext, byteBuffer);
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("Consuming CertificateRequest handshake message", t12CertificateRequestMessage);
            }
            clientHandshakeContext.handshakeProducers.put(SSLHandshake.CERTIFICATE.id, SSLHandshake.CERTIFICATE);
            final LinkedList peerSupportedSignatureAlgorithms = new LinkedList();
            final int[] algorithmIds = t12CertificateRequestMessage.algorithmIds;
            for (int length = algorithmIds.length, i = 0; i < length; ++i) {
                final SignatureScheme value = SignatureScheme.valueOf(algorithmIds[i]);
                if (value != null) {
                    peerSupportedSignatureAlgorithms.add(value);
                }
            }
            clientHandshakeContext.peerRequestedSignatureSchemes = peerSupportedSignatureAlgorithms;
            clientHandshakeContext.peerRequestedCertSignSchemes = peerSupportedSignatureAlgorithms;
            clientHandshakeContext.handshakeSession.setPeerSupportedSignatureAlgorithms(peerSupportedSignatureAlgorithms);
            clientHandshakeContext.peerSupportedAuthorities = t12CertificateRequestMessage.getAuthorities();
            final SSLPossession choosePossession = choosePossession(clientHandshakeContext);
            if (choosePossession == null) {
                return;
            }
            clientHandshakeContext.handshakePossessions.add(choosePossession);
            clientHandshakeContext.handshakeProducers.put(SSLHandshake.CERTIFICATE_VERIFY.id, SSLHandshake.CERTIFICATE_VERIFY);
        }
        
        private static SSLPossession choosePossession(final HandshakeContext handshakeContext) throws IOException {
            if (handshakeContext.peerRequestedCertSignSchemes == null || handshakeContext.peerRequestedCertSignSchemes.isEmpty()) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.warning("No signature and hash algorithms in CertificateRequest", new Object[0]);
                }
                return null;
            }
            final HashSet set = new HashSet();
            for (final SignatureScheme signatureScheme : handshakeContext.peerRequestedCertSignSchemes) {
                if (set.contains(signatureScheme.keyAlgorithm)) {
                    if (!SSLLogger.isOn || !SSLLogger.isOn("ssl,handshake")) {
                        continue;
                    }
                    SSLLogger.warning("Unsupported authentication scheme: " + signatureScheme.name, new Object[0]);
                }
                else if (SignatureScheme.getPreferableAlgorithm(handshakeContext.algorithmConstraints, handshakeContext.peerRequestedSignatureSchemes, signatureScheme, handshakeContext.negotiatedProtocol) == null) {
                    if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                        SSLLogger.warning("Unable to produce CertificateVerify for signature scheme: " + signatureScheme.name, new Object[0]);
                    }
                    set.add(signatureScheme.keyAlgorithm);
                }
                else {
                    final X509Authentication value = X509Authentication.valueOf(signatureScheme);
                    if (value == null) {
                        if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                            SSLLogger.warning("Unsupported authentication scheme: " + signatureScheme.name, new Object[0]);
                        }
                        set.add(signatureScheme.keyAlgorithm);
                    }
                    else {
                        final SSLPossession possession = value.createPossession(handshakeContext);
                        if (possession != null) {
                            return possession;
                        }
                        if (!SSLLogger.isOn || !SSLLogger.isOn("ssl,handshake")) {
                            continue;
                        }
                        SSLLogger.warning("Unavailable authentication scheme: " + signatureScheme.name, new Object[0]);
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
        
        T13CertificateRequestMessage(final HandshakeContext handshakeContext, final ByteBuffer byteBuffer) throws IOException {
            super(handshakeContext);
            if (byteBuffer.remaining() < 5) {
                throw handshakeContext.conContext.fatal(Alert.ILLEGAL_PARAMETER, "Invalid CertificateRequest handshake message: no sufficient data");
            }
            this.requestContext = Record.getBytes8(byteBuffer);
            if (byteBuffer.remaining() < 4) {
                throw handshakeContext.conContext.fatal(Alert.ILLEGAL_PARAMETER, "Invalid CertificateRequest handshake message: no sufficient extensions data");
            }
            this.extensions = new SSLExtensions(this, byteBuffer, handshakeContext.sslConfig.getEnabledExtensions(SSLHandshake.CERTIFICATE_REQUEST));
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
        void send(final HandshakeOutStream handshakeOutStream) throws IOException {
            handshakeOutStream.putBytes8(this.requestContext);
            this.extensions.send(handshakeOutStream);
        }
        
        @Override
        public String toString() {
            return new MessageFormat("\"CertificateRequest\": '{'\n  \"certificate_request_context\": \"{0}\",\n  \"extensions\": [\n{1}\n  ]\n'}'", Locale.ENGLISH).format(new Object[] { Utilities.toHexString(this.requestContext), Utilities.indent(Utilities.indent(this.extensions.toString())) });
        }
    }
    
    private static final class T13CertificateRequestProducer implements HandshakeProducer
    {
        @Override
        public byte[] produce(final ConnectionContext connectionContext, final SSLHandshake.HandshakeMessage handshakeMessage) throws IOException {
            final ServerHandshakeContext serverHandshakeContext = (ServerHandshakeContext)connectionContext;
            final T13CertificateRequestMessage t13CertificateRequestMessage = new T13CertificateRequestMessage(serverHandshakeContext);
            t13CertificateRequestMessage.extensions.produce(serverHandshakeContext, serverHandshakeContext.sslConfig.getEnabledExtensions(SSLHandshake.CERTIFICATE_REQUEST, serverHandshakeContext.negotiatedProtocol));
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("Produced CertificateRequest message", t13CertificateRequestMessage);
            }
            t13CertificateRequestMessage.write(serverHandshakeContext.handshakeOutput);
            serverHandshakeContext.handshakeOutput.flush();
            serverHandshakeContext.certRequestContext = t13CertificateRequestMessage.requestContext.clone();
            serverHandshakeContext.handshakeConsumers.put(SSLHandshake.CERTIFICATE.id, SSLHandshake.CERTIFICATE);
            serverHandshakeContext.handshakeConsumers.put(SSLHandshake.CERTIFICATE_VERIFY.id, SSLHandshake.CERTIFICATE_VERIFY);
            return null;
        }
    }
    
    private static final class T13CertificateRequestConsumer implements SSLConsumer
    {
        @Override
        public void consume(final ConnectionContext connectionContext, final ByteBuffer byteBuffer) throws IOException {
            final ClientHandshakeContext clientHandshakeContext = (ClientHandshakeContext)connectionContext;
            clientHandshakeContext.handshakeConsumers.remove(SSLHandshake.CERTIFICATE_REQUEST.id);
            final T13CertificateRequestMessage t13CertificateRequestMessage = new T13CertificateRequestMessage(clientHandshakeContext, byteBuffer);
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("Consuming CertificateRequest handshake message", t13CertificateRequestMessage);
            }
            final SSLExtension[] enabledExtensions = clientHandshakeContext.sslConfig.getEnabledExtensions(SSLHandshake.CERTIFICATE_REQUEST);
            t13CertificateRequestMessage.extensions.consumeOnLoad(clientHandshakeContext, enabledExtensions);
            t13CertificateRequestMessage.extensions.consumeOnTrade(clientHandshakeContext, enabledExtensions);
            clientHandshakeContext.certRequestContext = t13CertificateRequestMessage.requestContext.clone();
            clientHandshakeContext.handshakeProducers.put(SSLHandshake.CERTIFICATE.id, SSLHandshake.CERTIFICATE);
            clientHandshakeContext.handshakeProducers.put(SSLHandshake.CERTIFICATE_VERIFY.id, SSLHandshake.CERTIFICATE_VERIFY);
        }
    }
}
