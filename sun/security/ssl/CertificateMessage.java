package sun.security.ssl;

import java.util.Arrays;
import java.security.cert.CertPathValidatorException;
import javax.net.ssl.X509TrustManager;
import java.net.Socket;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.X509ExtendedTrustManager;
import java.util.HashSet;
import javax.security.auth.x500.X500Principal;
import java.util.Collection;
import java.security.cert.CertificateParsingException;
import java.security.PrivateKey;
import java.text.MessageFormat;
import java.util.Locale;
import java.security.cert.CertificateException;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.Iterator;
import java.io.IOException;
import java.util.Collections;
import javax.net.ssl.SSLProtocolException;
import java.util.LinkedList;
import java.nio.ByteBuffer;
import javax.net.ssl.SSLException;
import java.security.cert.CertificateEncodingException;
import java.util.ArrayList;
import java.security.cert.X509Certificate;
import java.util.List;

final class CertificateMessage
{
    static final SSLConsumer t12HandshakeConsumer;
    static final HandshakeProducer t12HandshakeProducer;
    static final SSLConsumer t13HandshakeConsumer;
    static final HandshakeProducer t13HandshakeProducer;
    
    static {
        t12HandshakeConsumer = new T12CertificateConsumer();
        t12HandshakeProducer = new T12CertificateProducer();
        t13HandshakeConsumer = new T13CertificateConsumer();
        t13HandshakeProducer = new T13CertificateProducer();
    }
    
    static final class T12CertificateMessage extends SSLHandshake.HandshakeMessage
    {
        final List<byte[]> encodedCertChain;
        
        T12CertificateMessage(final HandshakeContext handshakeContext, final X509Certificate[] array) throws SSLException {
            super(handshakeContext);
            final ArrayList encodedCertChain = new ArrayList(array.length);
            for (final X509Certificate x509Certificate : array) {
                try {
                    encodedCertChain.add(x509Certificate.getEncoded());
                }
                catch (final CertificateEncodingException ex) {
                    throw handshakeContext.conContext.fatal(Alert.INTERNAL_ERROR, "Could not encode certificate (" + x509Certificate.getSubjectX500Principal() + ")", ex);
                }
            }
            this.encodedCertChain = encodedCertChain;
        }
        
        T12CertificateMessage(final HandshakeContext handshakeContext, final ByteBuffer byteBuffer) throws IOException {
            super(handshakeContext);
            int i = Record.getInt24(byteBuffer);
            if (i > byteBuffer.remaining()) {
                throw handshakeContext.conContext.fatal(Alert.ILLEGAL_PARAMETER, "Error parsing certificate message:no sufficient data");
            }
            if (i > 0) {
                final LinkedList encodedCertChain = new LinkedList();
                while (i > 0) {
                    final byte[] bytes24 = Record.getBytes24(byteBuffer);
                    i -= 3 + bytes24.length;
                    encodedCertChain.add(bytes24);
                    if (encodedCertChain.size() > SSLConfiguration.maxCertificateChainLength) {
                        throw new SSLProtocolException("The certificate chain length (" + encodedCertChain.size() + ") exceeds the maximum allowed length (" + SSLConfiguration.maxCertificateChainLength + ")");
                    }
                }
                this.encodedCertChain = encodedCertChain;
            }
            else {
                this.encodedCertChain = Collections.emptyList();
            }
        }
        
        public SSLHandshake handshakeType() {
            return SSLHandshake.CERTIFICATE;
        }
        
        public int messageLength() {
            int n = 3;
            final Iterator<byte[]> iterator = this.encodedCertChain.iterator();
            while (iterator.hasNext()) {
                n += iterator.next().length + 3;
            }
            return n;
        }
        
        public void send(final HandshakeOutStream handshakeOutStream) throws IOException {
            int n = 0;
            final Iterator<byte[]> iterator = this.encodedCertChain.iterator();
            while (iterator.hasNext()) {
                n += iterator.next().length + 3;
            }
            handshakeOutStream.putInt24(n);
            final Iterator<byte[]> iterator2 = this.encodedCertChain.iterator();
            while (iterator2.hasNext()) {
                handshakeOutStream.putBytes24(iterator2.next());
            }
        }
        
        @Override
        public String toString() {
            if (this.encodedCertChain.isEmpty()) {
                return "\"Certificates\": <empty list>";
            }
            final Object[] array = new Object[this.encodedCertChain.size()];
            try {
                final CertificateFactory instance = CertificateFactory.getInstance("X.509");
                int n = 0;
                for (final byte[] array2 : this.encodedCertChain) {
                    Object o;
                    try {
                        o = instance.generateCertificate(new ByteArrayInputStream(array2));
                    }
                    catch (final CertificateException ex) {
                        o = array2;
                    }
                    array[n++] = o;
                }
            }
            catch (final CertificateException ex2) {
                int n2 = 0;
                final Iterator<byte[]> iterator2 = this.encodedCertChain.iterator();
                while (iterator2.hasNext()) {
                    array[n2++] = iterator2.next();
                }
            }
            return new MessageFormat("\"Certificates\": [\n{0}\n]", Locale.ENGLISH).format(new Object[] { SSLLogger.toString(array) });
        }
    }
    
    private static final class T12CertificateProducer implements HandshakeProducer
    {
        @Override
        public byte[] produce(final ConnectionContext connectionContext, final SSLHandshake.HandshakeMessage handshakeMessage) throws IOException {
            if (((HandshakeContext)connectionContext).sslConfig.isClientMode) {
                return this.onProduceCertificate((ClientHandshakeContext)connectionContext, handshakeMessage);
            }
            return this.onProduceCertificate((ServerHandshakeContext)connectionContext, handshakeMessage);
        }
        
        private byte[] onProduceCertificate(final ServerHandshakeContext serverHandshakeContext, final SSLHandshake.HandshakeMessage handshakeMessage) throws IOException {
            X509Authentication.X509Possession x509Possession = null;
            for (final SSLPossession sslPossession : serverHandshakeContext.handshakePossessions) {
                if (sslPossession instanceof X509Authentication.X509Possession) {
                    x509Possession = (X509Authentication.X509Possession)sslPossession;
                    break;
                }
            }
            if (x509Possession == null) {
                throw serverHandshakeContext.conContext.fatal(Alert.INTERNAL_ERROR, "No expected X.509 certificate for server authentication");
            }
            serverHandshakeContext.handshakeSession.setLocalPrivateKey(x509Possession.popPrivateKey);
            serverHandshakeContext.handshakeSession.setLocalCertificates(x509Possession.popCerts);
            final T12CertificateMessage t12CertificateMessage = new T12CertificateMessage(serverHandshakeContext, x509Possession.popCerts);
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("Produced server Certificate handshake message", t12CertificateMessage);
            }
            t12CertificateMessage.write(serverHandshakeContext.handshakeOutput);
            serverHandshakeContext.handshakeOutput.flush();
            return null;
        }
        
        private byte[] onProduceCertificate(final ClientHandshakeContext clientHandshakeContext, final SSLHandshake.HandshakeMessage handshakeMessage) throws IOException {
            X509Authentication.X509Possession x509Possession = null;
            for (final SSLPossession sslPossession : clientHandshakeContext.handshakePossessions) {
                if (sslPossession instanceof X509Authentication.X509Possession) {
                    x509Possession = (X509Authentication.X509Possession)sslPossession;
                    break;
                }
            }
            if (x509Possession == null) {
                if (!clientHandshakeContext.negotiatedProtocol.useTLS10PlusSpec()) {
                    if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                        SSLLogger.fine("No X.509 certificate for client authentication, send a no_certificate alert", new Object[0]);
                    }
                    clientHandshakeContext.conContext.warning(Alert.NO_CERTIFICATE);
                    return null;
                }
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("No X.509 certificate for client authentication, use empty Certificate message instead", new Object[0]);
                }
                x509Possession = new X509Authentication.X509Possession(null, new X509Certificate[0]);
            }
            clientHandshakeContext.handshakeSession.setLocalPrivateKey(x509Possession.popPrivateKey);
            if (x509Possession.popCerts != null && x509Possession.popCerts.length != 0) {
                clientHandshakeContext.handshakeSession.setLocalCertificates(x509Possession.popCerts);
            }
            else {
                clientHandshakeContext.handshakeSession.setLocalCertificates(null);
            }
            final T12CertificateMessage t12CertificateMessage = new T12CertificateMessage(clientHandshakeContext, x509Possession.popCerts);
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("Produced client Certificate handshake message", t12CertificateMessage);
            }
            t12CertificateMessage.write(clientHandshakeContext.handshakeOutput);
            clientHandshakeContext.handshakeOutput.flush();
            return null;
        }
    }
    
    static final class T12CertificateConsumer implements SSLConsumer
    {
        private T12CertificateConsumer() {
        }
        
        @Override
        public void consume(final ConnectionContext connectionContext, final ByteBuffer byteBuffer) throws IOException {
            final HandshakeContext handshakeContext = (HandshakeContext)connectionContext;
            handshakeContext.handshakeConsumers.remove(SSLHandshake.CERTIFICATE.id);
            final T12CertificateMessage t12CertificateMessage = new T12CertificateMessage(handshakeContext, byteBuffer);
            if (handshakeContext.sslConfig.isClientMode) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("Consuming server Certificate handshake message", t12CertificateMessage);
                }
                this.onCertificate((ClientHandshakeContext)connectionContext, t12CertificateMessage);
            }
            else {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("Consuming client Certificate handshake message", t12CertificateMessage);
                }
                this.onCertificate((ServerHandshakeContext)connectionContext, t12CertificateMessage);
            }
        }
        
        private void onCertificate(final ServerHandshakeContext serverHandshakeContext, final T12CertificateMessage t12CertificateMessage) throws IOException {
            final List<byte[]> encodedCertChain = t12CertificateMessage.encodedCertChain;
            if (encodedCertChain != null && !encodedCertChain.isEmpty()) {
                final X509Certificate[] peerCertificates = new X509Certificate[encodedCertChain.size()];
                try {
                    final CertificateFactory instance = CertificateFactory.getInstance("X.509");
                    int n = 0;
                    final Iterator iterator = encodedCertChain.iterator();
                    while (iterator.hasNext()) {
                        peerCertificates[n++] = (X509Certificate)instance.generateCertificate(new ByteArrayInputStream((byte[])iterator.next()));
                    }
                }
                catch (final CertificateException ex) {
                    throw serverHandshakeContext.conContext.fatal(Alert.BAD_CERTIFICATE, "Failed to parse server certificates", ex);
                }
                checkClientCerts(serverHandshakeContext, peerCertificates);
                serverHandshakeContext.handshakeCredentials.add(new X509Authentication.X509Credentials(peerCertificates[0].getPublicKey(), peerCertificates));
                serverHandshakeContext.handshakeSession.setPeerCertificates(peerCertificates);
                return;
            }
            serverHandshakeContext.handshakeConsumers.remove(SSLHandshake.CERTIFICATE_VERIFY.id);
            if (serverHandshakeContext.sslConfig.clientAuthType != ClientAuthType.CLIENT_AUTH_REQUESTED) {
                throw serverHandshakeContext.conContext.fatal(Alert.BAD_CERTIFICATE, "Empty server certificate chain");
            }
        }
        
        private void onCertificate(final ClientHandshakeContext clientHandshakeContext, final T12CertificateMessage t12CertificateMessage) throws IOException {
            final List<byte[]> encodedCertChain = t12CertificateMessage.encodedCertChain;
            if (encodedCertChain == null || encodedCertChain.isEmpty()) {
                throw clientHandshakeContext.conContext.fatal(Alert.BAD_CERTIFICATE, "Empty server certificate chain");
            }
            final X509Certificate[] array = new X509Certificate[encodedCertChain.size()];
            try {
                final CertificateFactory instance = CertificateFactory.getInstance("X.509");
                int n = 0;
                final Iterator iterator = encodedCertChain.iterator();
                while (iterator.hasNext()) {
                    array[n++] = (X509Certificate)instance.generateCertificate(new ByteArrayInputStream((byte[])iterator.next()));
                }
            }
            catch (final CertificateException ex) {
                throw clientHandshakeContext.conContext.fatal(Alert.BAD_CERTIFICATE, "Failed to parse server certificates", ex);
            }
            if (clientHandshakeContext.reservedServerCerts != null && !clientHandshakeContext.handshakeSession.useExtendedMasterSecret) {
                final String identificationProtocol = clientHandshakeContext.sslConfig.identificationProtocol;
                if ((identificationProtocol == null || identificationProtocol.isEmpty()) && !isIdentityEquivalent(array[0], clientHandshakeContext.reservedServerCerts[0])) {
                    throw clientHandshakeContext.conContext.fatal(Alert.BAD_CERTIFICATE, "server certificate change is restricted during renegotiation");
                }
            }
            if (clientHandshakeContext.staplingActive) {
                clientHandshakeContext.deferredCerts = array;
            }
            else {
                checkServerCerts(clientHandshakeContext, array);
            }
            clientHandshakeContext.handshakeCredentials.add(new X509Authentication.X509Credentials(array[0].getPublicKey(), array));
            clientHandshakeContext.handshakeSession.setPeerCertificates(array);
        }
        
        private static boolean isIdentityEquivalent(final X509Certificate x509Certificate, final X509Certificate x509Certificate2) {
            if (x509Certificate.equals(x509Certificate2)) {
                return true;
            }
            Collection<List<?>> subjectAlternativeNames = null;
            try {
                subjectAlternativeNames = x509Certificate.getSubjectAlternativeNames();
            }
            catch (final CertificateParsingException ex) {
                if (SSLLogger.isOn && SSLLogger.isOn("handshake")) {
                    SSLLogger.fine("Attempt to obtain subjectAltNames extension failed!", new Object[0]);
                }
            }
            Collection<List<?>> subjectAlternativeNames2 = null;
            try {
                subjectAlternativeNames2 = x509Certificate2.getSubjectAlternativeNames();
            }
            catch (final CertificateParsingException ex2) {
                if (SSLLogger.isOn && SSLLogger.isOn("handshake")) {
                    SSLLogger.fine("Attempt to obtain subjectAltNames extension failed!", new Object[0]);
                }
            }
            if (subjectAlternativeNames != null && subjectAlternativeNames2 != null) {
                final Collection<String> subjectAltNames = getSubjectAltNames(subjectAlternativeNames, 7);
                final Collection<String> subjectAltNames2 = getSubjectAltNames(subjectAlternativeNames2, 7);
                if (subjectAltNames != null && subjectAltNames2 != null && isEquivalent(subjectAltNames, subjectAltNames2)) {
                    return true;
                }
                final Collection<String> subjectAltNames3 = getSubjectAltNames(subjectAlternativeNames, 2);
                final Collection<String> subjectAltNames4 = getSubjectAltNames(subjectAlternativeNames2, 2);
                if (subjectAltNames3 != null && subjectAltNames4 != null && isEquivalent(subjectAltNames3, subjectAltNames4)) {
                    return true;
                }
            }
            final X500Principal subjectX500Principal = x509Certificate.getSubjectX500Principal();
            final X500Principal subjectX500Principal2 = x509Certificate2.getSubjectX500Principal();
            final X500Principal issuerX500Principal = x509Certificate.getIssuerX500Principal();
            final X500Principal issuerX500Principal2 = x509Certificate2.getIssuerX500Principal();
            return !subjectX500Principal.getName().isEmpty() && !subjectX500Principal2.getName().isEmpty() && subjectX500Principal.equals(subjectX500Principal2) && issuerX500Principal.equals(issuerX500Principal2);
        }
        
        private static Collection<String> getSubjectAltNames(final Collection<List<?>> collection, final int n) {
            HashSet<String> set = null;
            for (final List list : collection) {
                if ((int)list.get(0) == n) {
                    final String s = (String)list.get(1);
                    if (s == null || s.isEmpty()) {
                        continue;
                    }
                    if (set == null) {
                        set = new HashSet<String>(collection.size());
                    }
                    set.add(s);
                }
            }
            return set;
        }
        
        private static boolean isEquivalent(final Collection<String> collection, final Collection<String> collection2) {
            for (final String s : collection) {
                final Iterator<String> iterator2 = collection2.iterator();
                while (iterator2.hasNext()) {
                    if (s.equalsIgnoreCase(iterator2.next())) {
                        return true;
                    }
                }
            }
            return false;
        }
        
        static void checkServerCerts(final ClientHandshakeContext clientHandshakeContext, final X509Certificate[] peerCertificates) throws IOException {
            final X509TrustManager x509TrustManager = clientHandshakeContext.sslContext.getX509TrustManager();
            String s;
            if (clientHandshakeContext.negotiatedCipherSuite.keyExchange == CipherSuite.KeyExchange.K_RSA_EXPORT || clientHandshakeContext.negotiatedCipherSuite.keyExchange == CipherSuite.KeyExchange.K_DHE_RSA_EXPORT) {
                s = CipherSuite.KeyExchange.K_RSA.name;
            }
            else {
                s = clientHandshakeContext.negotiatedCipherSuite.keyExchange.name;
            }
            try {
                if (!(x509TrustManager instanceof X509ExtendedTrustManager)) {
                    throw new CertificateException("Improper X509TrustManager implementation");
                }
                if (clientHandshakeContext.conContext.transport instanceof SSLEngine) {
                    ((X509ExtendedTrustManager)x509TrustManager).checkServerTrusted(peerCertificates.clone(), s, (SSLEngine)clientHandshakeContext.conContext.transport);
                }
                else {
                    ((X509ExtendedTrustManager)x509TrustManager).checkServerTrusted(peerCertificates.clone(), s, (Socket)clientHandshakeContext.conContext.transport);
                }
                clientHandshakeContext.handshakeSession.setPeerCertificates(peerCertificates);
            }
            catch (final CertificateException ex) {
                throw clientHandshakeContext.conContext.fatal(getCertificateAlert(clientHandshakeContext, ex), ex);
            }
        }
        
        private static void checkClientCerts(final ServerHandshakeContext serverHandshakeContext, final X509Certificate[] array) throws IOException {
            final X509TrustManager x509TrustManager = serverHandshakeContext.sslContext.getX509TrustManager();
            final String algorithm;
            final String s = algorithm = array[0].getPublicKey().getAlgorithm();
            String s2 = null;
            switch (algorithm) {
                case "RSA":
                case "DSA":
                case "EC":
                case "RSASSA-PSS": {
                    s2 = s;
                    break;
                }
                default: {
                    s2 = "UNKNOWN";
                    break;
                }
            }
            try {
                if (!(x509TrustManager instanceof X509ExtendedTrustManager)) {
                    throw new CertificateException("Improper X509TrustManager implementation");
                }
                if (serverHandshakeContext.conContext.transport instanceof SSLEngine) {
                    ((X509ExtendedTrustManager)x509TrustManager).checkClientTrusted(array.clone(), s2, (SSLEngine)serverHandshakeContext.conContext.transport);
                }
                else {
                    ((X509ExtendedTrustManager)x509TrustManager).checkClientTrusted(array.clone(), s2, (Socket)serverHandshakeContext.conContext.transport);
                }
            }
            catch (final CertificateException ex) {
                throw serverHandshakeContext.conContext.fatal(Alert.CERTIFICATE_UNKNOWN, ex);
            }
        }
        
        private static Alert getCertificateAlert(final ClientHandshakeContext clientHandshakeContext, final CertificateException ex) {
            Alert alert = Alert.CERTIFICATE_UNKNOWN;
            final Throwable cause = ex.getCause();
            if (cause instanceof CertPathValidatorException) {
                final CertPathValidatorException.Reason reason = ((CertPathValidatorException)cause).getReason();
                if (reason == CertPathValidatorException.BasicReason.REVOKED) {
                    alert = (clientHandshakeContext.staplingActive ? Alert.BAD_CERT_STATUS_RESPONSE : Alert.CERTIFICATE_REVOKED);
                }
                else if (reason == CertPathValidatorException.BasicReason.UNDETERMINED_REVOCATION_STATUS) {
                    alert = (clientHandshakeContext.staplingActive ? Alert.BAD_CERT_STATUS_RESPONSE : Alert.CERTIFICATE_UNKNOWN);
                }
                else if (reason == CertPathValidatorException.BasicReason.ALGORITHM_CONSTRAINED) {
                    alert = Alert.UNSUPPORTED_CERTIFICATE;
                }
                else if (reason == CertPathValidatorException.BasicReason.EXPIRED) {
                    alert = Alert.CERTIFICATE_EXPIRED;
                }
                else if (reason == CertPathValidatorException.BasicReason.INVALID_SIGNATURE || reason == CertPathValidatorException.BasicReason.NOT_YET_VALID) {
                    alert = Alert.BAD_CERTIFICATE;
                }
            }
            return alert;
        }
    }
    
    static final class CertificateEntry
    {
        final byte[] encoded;
        private final SSLExtensions extensions;
        
        CertificateEntry(final byte[] encoded, final SSLExtensions extensions) {
            this.encoded = encoded;
            this.extensions = extensions;
        }
        
        private int getEncodedSize() {
            int length = this.extensions.length();
            if (length == 0) {
                length = 2;
            }
            return 3 + this.encoded.length + length;
        }
        
        @Override
        public String toString() {
            final MessageFormat messageFormat = new MessageFormat("\n'{'\n{0}\n  \"extensions\": '{'\n{1}\n  '}'\n'}',", Locale.ENGLISH);
            Object o;
            try {
                o = CertificateFactory.getInstance("X.509").generateCertificate(new ByteArrayInputStream(this.encoded));
            }
            catch (final CertificateException ex) {
                o = this.encoded;
            }
            return messageFormat.format(new Object[] { SSLLogger.toString(o), Utilities.indent(this.extensions.toString(), "    ") });
        }
    }
    
    static final class T13CertificateMessage extends SSLHandshake.HandshakeMessage
    {
        private final byte[] requestContext;
        private final List<CertificateEntry> certEntries;
        
        T13CertificateMessage(final HandshakeContext handshakeContext, final byte[] array, final X509Certificate[] array2) throws SSLException, CertificateException {
            super(handshakeContext);
            this.requestContext = array.clone();
            this.certEntries = new LinkedList<CertificateEntry>();
            for (int length = array2.length, i = 0; i < length; ++i) {
                this.certEntries.add(new CertificateEntry(array2[i].getEncoded(), new SSLExtensions(this)));
            }
        }
        
        T13CertificateMessage(final HandshakeContext handshakeContext, final byte[] array, final List<CertificateEntry> certEntries) {
            super(handshakeContext);
            this.requestContext = array.clone();
            this.certEntries = certEntries;
        }
        
        T13CertificateMessage(final HandshakeContext handshakeContext, final ByteBuffer byteBuffer) throws IOException {
            super(handshakeContext);
            if (byteBuffer.remaining() < 4) {
                throw new SSLProtocolException("Invalid Certificate message: insufficient data (length=" + byteBuffer.remaining() + ")");
            }
            this.requestContext = Record.getBytes8(byteBuffer);
            if (byteBuffer.remaining() < 3) {
                throw new SSLProtocolException("Invalid Certificate message: insufficient certificate entries data (length=" + byteBuffer.remaining() + ")");
            }
            final int int24 = Record.getInt24(byteBuffer);
            if (int24 != byteBuffer.remaining()) {
                throw new SSLProtocolException("Invalid Certificate message: incorrect list length (length=" + int24 + ")");
            }
            final SSLExtension[] enabledExtensions = handshakeContext.sslConfig.getEnabledExtensions(SSLHandshake.CERTIFICATE);
            final LinkedList list = new LinkedList();
            while (byteBuffer.hasRemaining()) {
                final byte[] bytes24 = Record.getBytes24(byteBuffer);
                if (bytes24.length == 0) {
                    throw new SSLProtocolException("Invalid Certificate message: empty cert_data");
                }
                list.add(new CertificateEntry(bytes24, new SSLExtensions(this, byteBuffer, enabledExtensions)));
                if (list.size() > SSLConfiguration.maxCertificateChainLength) {
                    throw new SSLProtocolException("The certificate chain length (" + list.size() + ") exceeds the maximum allowed length (" + SSLConfiguration.maxCertificateChainLength + ")");
                }
            }
            this.certEntries = (List<CertificateEntry>)Collections.unmodifiableList((List<?>)list);
        }
        
        public SSLHandshake handshakeType() {
            return SSLHandshake.CERTIFICATE;
        }
        
        public int messageLength() {
            int n = 4 + this.requestContext.length;
            final Iterator<CertificateEntry> iterator = this.certEntries.iterator();
            while (iterator.hasNext()) {
                n += iterator.next().getEncodedSize();
            }
            return n;
        }
        
        public void send(final HandshakeOutStream handshakeOutStream) throws IOException {
            int n = 0;
            final Iterator<CertificateEntry> iterator = this.certEntries.iterator();
            while (iterator.hasNext()) {
                n += iterator.next().getEncodedSize();
            }
            handshakeOutStream.putBytes8(this.requestContext);
            handshakeOutStream.putInt24(n);
            for (final CertificateEntry certificateEntry : this.certEntries) {
                handshakeOutStream.putBytes24(certificateEntry.encoded);
                if (certificateEntry.extensions.length() == 0) {
                    handshakeOutStream.putInt16(0);
                }
                else {
                    certificateEntry.extensions.send(handshakeOutStream);
                }
            }
        }
        
        @Override
        public String toString() {
            final MessageFormat messageFormat = new MessageFormat("\"Certificate\": '{'\n  \"certificate_request_context\": \"{0}\",\n  \"certificate_list\": [{1}\n]\n'}'", Locale.ENGLISH);
            final StringBuilder sb = new StringBuilder(512);
            final Iterator<CertificateEntry> iterator = this.certEntries.iterator();
            while (iterator.hasNext()) {
                sb.append(iterator.next().toString());
            }
            return messageFormat.format(new Object[] { Utilities.toHexString(this.requestContext), Utilities.indent(sb.toString()) });
        }
    }
    
    private static final class T13CertificateProducer implements HandshakeProducer
    {
        @Override
        public byte[] produce(final ConnectionContext connectionContext, final SSLHandshake.HandshakeMessage handshakeMessage) throws IOException {
            if (((HandshakeContext)connectionContext).sslConfig.isClientMode) {
                return this.onProduceCertificate((ClientHandshakeContext)connectionContext, handshakeMessage);
            }
            return this.onProduceCertificate((ServerHandshakeContext)connectionContext, handshakeMessage);
        }
        
        private byte[] onProduceCertificate(final ServerHandshakeContext serverHandshakeContext, final SSLHandshake.HandshakeMessage handshakeMessage) throws IOException {
            final SSLPossession choosePossession = choosePossession(serverHandshakeContext, (ClientHello.ClientHelloMessage)handshakeMessage);
            if (choosePossession == null) {
                throw serverHandshakeContext.conContext.fatal(Alert.HANDSHAKE_FAILURE, "No available authentication scheme");
            }
            if (!(choosePossession instanceof X509Authentication.X509Possession)) {
                throw serverHandshakeContext.conContext.fatal(Alert.HANDSHAKE_FAILURE, "No X.509 certificate for server authentication");
            }
            final X509Authentication.X509Possession x509Possession = (X509Authentication.X509Possession)choosePossession;
            final X509Certificate[] popCerts = x509Possession.popCerts;
            if (popCerts == null || popCerts.length == 0) {
                throw serverHandshakeContext.conContext.fatal(Alert.HANDSHAKE_FAILURE, "No X.509 certificate for server authentication");
            }
            serverHandshakeContext.handshakePossessions.add(x509Possession);
            serverHandshakeContext.handshakeSession.setLocalPrivateKey(x509Possession.popPrivateKey);
            serverHandshakeContext.handshakeSession.setLocalCertificates(popCerts);
            T13CertificateMessage t13CertificateMessage;
            try {
                t13CertificateMessage = new T13CertificateMessage(serverHandshakeContext, new byte[0], popCerts);
            }
            catch (final SSLException | CertificateException ex) {
                throw serverHandshakeContext.conContext.fatal(Alert.HANDSHAKE_FAILURE, "Failed to produce server Certificate message", (Throwable)ex);
            }
            serverHandshakeContext.stapleParams = StatusResponseManager.processStapling(serverHandshakeContext);
            serverHandshakeContext.staplingActive = (serverHandshakeContext.stapleParams != null);
            final SSLExtension[] enabledExtensions = serverHandshakeContext.sslConfig.getEnabledExtensions(SSLHandshake.CERTIFICATE, Arrays.asList(ProtocolVersion.PROTOCOLS_OF_13));
            for (final CertificateEntry currentCertEntry : t13CertificateMessage.certEntries) {
                serverHandshakeContext.currentCertEntry = currentCertEntry;
                currentCertEntry.extensions.produce(serverHandshakeContext, enabledExtensions);
            }
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("Produced server Certificate message", t13CertificateMessage);
            }
            t13CertificateMessage.write(serverHandshakeContext.handshakeOutput);
            serverHandshakeContext.handshakeOutput.flush();
            return null;
        }
        
        private static SSLPossession choosePossession(final HandshakeContext handshakeContext, final ClientHello.ClientHelloMessage clientHelloMessage) throws IOException {
            if (handshakeContext.peerRequestedCertSignSchemes == null || handshakeContext.peerRequestedCertSignSchemes.isEmpty()) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.warning("No signature_algorithms(_cert) in ClientHello", new Object[0]);
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
        
        private byte[] onProduceCertificate(final ClientHandshakeContext clientHandshakeContext, final SSLHandshake.HandshakeMessage handshakeMessage) throws IOException {
            final SSLPossession choosePossession = choosePossession(clientHandshakeContext, (ClientHello.ClientHelloMessage)handshakeMessage);
            X509Certificate[] popCerts;
            if (choosePossession == null) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("No available client authentication scheme", new Object[0]);
                }
                popCerts = new X509Certificate[0];
            }
            else {
                clientHandshakeContext.handshakePossessions.add(choosePossession);
                if (!(choosePossession instanceof X509Authentication.X509Possession)) {
                    if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                        SSLLogger.fine("No X.509 certificate for client authentication", new Object[0]);
                    }
                    popCerts = new X509Certificate[0];
                }
                else {
                    final X509Authentication.X509Possession x509Possession = (X509Authentication.X509Possession)choosePossession;
                    popCerts = x509Possession.popCerts;
                    clientHandshakeContext.handshakeSession.setLocalPrivateKey(x509Possession.popPrivateKey);
                }
            }
            if (popCerts != null && popCerts.length != 0) {
                clientHandshakeContext.handshakeSession.setLocalCertificates(popCerts);
            }
            else {
                clientHandshakeContext.handshakeSession.setLocalCertificates(null);
            }
            T13CertificateMessage t13CertificateMessage;
            try {
                t13CertificateMessage = new T13CertificateMessage(clientHandshakeContext, clientHandshakeContext.certRequestContext, popCerts);
            }
            catch (final SSLException | CertificateException ex) {
                throw clientHandshakeContext.conContext.fatal(Alert.HANDSHAKE_FAILURE, "Failed to produce client Certificate message", (Throwable)ex);
            }
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("Produced client Certificate message", t13CertificateMessage);
            }
            t13CertificateMessage.write(clientHandshakeContext.handshakeOutput);
            clientHandshakeContext.handshakeOutput.flush();
            return null;
        }
    }
    
    private static final class T13CertificateConsumer implements SSLConsumer
    {
        @Override
        public void consume(final ConnectionContext connectionContext, final ByteBuffer byteBuffer) throws IOException {
            final HandshakeContext handshakeContext = (HandshakeContext)connectionContext;
            handshakeContext.handshakeConsumers.remove(SSLHandshake.CERTIFICATE.id);
            final T13CertificateMessage t13CertificateMessage = new T13CertificateMessage(handshakeContext, byteBuffer);
            if (handshakeContext.sslConfig.isClientMode) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("Consuming server Certificate handshake message", t13CertificateMessage);
                }
                this.onConsumeCertificate((ClientHandshakeContext)connectionContext, t13CertificateMessage);
            }
            else {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("Consuming client Certificate handshake message", t13CertificateMessage);
                }
                this.onConsumeCertificate((ServerHandshakeContext)connectionContext, t13CertificateMessage);
            }
        }
        
        private void onConsumeCertificate(final ServerHandshakeContext serverHandshakeContext, final T13CertificateMessage t13CertificateMessage) throws IOException {
            if (t13CertificateMessage.certEntries != null && !t13CertificateMessage.certEntries.isEmpty()) {
                final X509Certificate[] checkClientCerts = checkClientCerts(serverHandshakeContext, t13CertificateMessage.certEntries);
                serverHandshakeContext.handshakeCredentials.add(new X509Authentication.X509Credentials(checkClientCerts[0].getPublicKey(), checkClientCerts));
                serverHandshakeContext.handshakeSession.setPeerCertificates(checkClientCerts);
                return;
            }
            serverHandshakeContext.handshakeConsumers.remove(SSLHandshake.CERTIFICATE_VERIFY.id);
            if (serverHandshakeContext.sslConfig.clientAuthType == ClientAuthType.CLIENT_AUTH_REQUIRED) {
                throw serverHandshakeContext.conContext.fatal(Alert.BAD_CERTIFICATE, "Empty client certificate chain");
            }
        }
        
        private void onConsumeCertificate(final ClientHandshakeContext clientHandshakeContext, final T13CertificateMessage t13CertificateMessage) throws IOException {
            if (t13CertificateMessage.certEntries == null || t13CertificateMessage.certEntries.isEmpty()) {
                throw clientHandshakeContext.conContext.fatal(Alert.BAD_CERTIFICATE, "Empty server certificate chain");
            }
            final SSLExtension[] enabledExtensions = clientHandshakeContext.sslConfig.getEnabledExtensions(SSLHandshake.CERTIFICATE);
            final Iterator iterator = t13CertificateMessage.certEntries.iterator();
            while (iterator.hasNext()) {
                ((CertificateEntry)iterator.next()).extensions.consumeOnLoad(clientHandshakeContext, enabledExtensions);
            }
            final X509Certificate[] checkServerCerts = checkServerCerts(clientHandshakeContext, t13CertificateMessage.certEntries);
            clientHandshakeContext.handshakeCredentials.add(new X509Authentication.X509Credentials(checkServerCerts[0].getPublicKey(), checkServerCerts));
            clientHandshakeContext.handshakeSession.setPeerCertificates(checkServerCerts);
        }
        
        private static X509Certificate[] checkClientCerts(final ServerHandshakeContext serverHandshakeContext, final List<CertificateEntry> list) throws IOException {
            final X509Certificate[] peerCertificates = new X509Certificate[list.size()];
            try {
                final CertificateFactory instance = CertificateFactory.getInstance("X.509");
                int n = 0;
                final Iterator iterator = list.iterator();
                while (iterator.hasNext()) {
                    peerCertificates[n++] = (X509Certificate)instance.generateCertificate(new ByteArrayInputStream(((CertificateEntry)iterator.next()).encoded));
                }
            }
            catch (final CertificateException ex) {
                throw serverHandshakeContext.conContext.fatal(Alert.BAD_CERTIFICATE, "Failed to parse server certificates", ex);
            }
            final String algorithm;
            final String s = algorithm = peerCertificates[0].getPublicKey().getAlgorithm();
            String s2 = null;
            switch (algorithm) {
                case "RSA":
                case "DSA":
                case "EC":
                case "RSASSA-PSS": {
                    s2 = s;
                    break;
                }
                default: {
                    s2 = "UNKNOWN";
                    break;
                }
            }
            try {
                final X509TrustManager x509TrustManager = serverHandshakeContext.sslContext.getX509TrustManager();
                if (!(x509TrustManager instanceof X509ExtendedTrustManager)) {
                    throw new CertificateException("Improper X509TrustManager implementation");
                }
                if (serverHandshakeContext.conContext.transport instanceof SSLEngine) {
                    ((X509ExtendedTrustManager)x509TrustManager).checkClientTrusted(peerCertificates.clone(), s2, (SSLEngine)serverHandshakeContext.conContext.transport);
                }
                else {
                    ((X509ExtendedTrustManager)x509TrustManager).checkClientTrusted(peerCertificates.clone(), s2, (Socket)serverHandshakeContext.conContext.transport);
                }
                serverHandshakeContext.handshakeSession.setPeerCertificates(peerCertificates);
            }
            catch (final CertificateException ex2) {
                throw serverHandshakeContext.conContext.fatal(Alert.CERTIFICATE_UNKNOWN, ex2);
            }
            return peerCertificates;
        }
        
        private static X509Certificate[] checkServerCerts(final ClientHandshakeContext clientHandshakeContext, final List<CertificateEntry> list) throws IOException {
            final X509Certificate[] peerCertificates = new X509Certificate[list.size()];
            try {
                final CertificateFactory instance = CertificateFactory.getInstance("X.509");
                int n = 0;
                final Iterator iterator = list.iterator();
                while (iterator.hasNext()) {
                    peerCertificates[n++] = (X509Certificate)instance.generateCertificate(new ByteArrayInputStream(((CertificateEntry)iterator.next()).encoded));
                }
            }
            catch (final CertificateException ex) {
                throw clientHandshakeContext.conContext.fatal(Alert.BAD_CERTIFICATE, "Failed to parse server certificates", ex);
            }
            final String s = "UNKNOWN";
            try {
                final X509TrustManager x509TrustManager = clientHandshakeContext.sslContext.getX509TrustManager();
                if (!(x509TrustManager instanceof X509ExtendedTrustManager)) {
                    throw new CertificateException("Improper X509TrustManager implementation");
                }
                if (clientHandshakeContext.conContext.transport instanceof SSLEngine) {
                    ((X509ExtendedTrustManager)x509TrustManager).checkServerTrusted(peerCertificates.clone(), s, (SSLEngine)clientHandshakeContext.conContext.transport);
                }
                else {
                    ((X509ExtendedTrustManager)x509TrustManager).checkServerTrusted(peerCertificates.clone(), s, (Socket)clientHandshakeContext.conContext.transport);
                }
                clientHandshakeContext.handshakeSession.setPeerCertificates(peerCertificates);
            }
            catch (final CertificateException ex2) {
                throw clientHandshakeContext.conContext.fatal(getCertificateAlert(clientHandshakeContext, ex2), ex2);
            }
            return peerCertificates;
        }
        
        private static Alert getCertificateAlert(final ClientHandshakeContext clientHandshakeContext, final CertificateException ex) {
            Alert certificate_UNKNOWN = Alert.CERTIFICATE_UNKNOWN;
            final Throwable cause = ex.getCause();
            if (cause instanceof CertPathValidatorException) {
                final CertPathValidatorException.Reason reason = ((CertPathValidatorException)cause).getReason();
                if (reason == CertPathValidatorException.BasicReason.REVOKED) {
                    certificate_UNKNOWN = (clientHandshakeContext.staplingActive ? Alert.BAD_CERT_STATUS_RESPONSE : Alert.CERTIFICATE_REVOKED);
                }
                else if (reason == CertPathValidatorException.BasicReason.UNDETERMINED_REVOCATION_STATUS) {
                    certificate_UNKNOWN = (clientHandshakeContext.staplingActive ? Alert.BAD_CERT_STATUS_RESPONSE : Alert.CERTIFICATE_UNKNOWN);
                }
            }
            return certificate_UNKNOWN;
        }
    }
}
