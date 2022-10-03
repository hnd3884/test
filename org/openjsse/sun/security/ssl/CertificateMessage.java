package org.openjsse.sun.security.ssl;

import java.util.Arrays;
import java.security.cert.CertPathValidatorException;
import java.security.PublicKey;
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
        
        T12CertificateMessage(final HandshakeContext handshakeContext, final X509Certificate[] certChain) throws SSLException {
            super(handshakeContext);
            final List<byte[]> encodedCerts = new ArrayList<byte[]>(certChain.length);
            for (final X509Certificate cert : certChain) {
                try {
                    encodedCerts.add(cert.getEncoded());
                }
                catch (final CertificateEncodingException cee) {
                    throw handshakeContext.conContext.fatal(Alert.INTERNAL_ERROR, "Could not encode certificate (" + cert.getSubjectX500Principal() + ")", cee);
                }
            }
            this.encodedCertChain = encodedCerts;
        }
        
        T12CertificateMessage(final HandshakeContext handshakeContext, final ByteBuffer m) throws IOException {
            super(handshakeContext);
            int listLen = Record.getInt24(m);
            if (listLen > m.remaining()) {
                throw handshakeContext.conContext.fatal(Alert.ILLEGAL_PARAMETER, "Error parsing certificate message:no sufficient data");
            }
            if (listLen > 0) {
                final List<byte[]> encodedCerts = new LinkedList<byte[]>();
                while (listLen > 0) {
                    final byte[] encodedCert = Record.getBytes24(m);
                    listLen -= 3 + encodedCert.length;
                    encodedCerts.add(encodedCert);
                    if (encodedCerts.size() > SSLConfiguration.maxCertificateChainLength) {
                        throw new SSLProtocolException("The certificate chain length (" + encodedCerts.size() + ") exceeds the maximum allowed length (" + SSLConfiguration.maxCertificateChainLength + ")");
                    }
                }
                this.encodedCertChain = encodedCerts;
            }
            else {
                this.encodedCertChain = Collections.emptyList();
            }
        }
        
        public SSLHandshake handshakeType() {
            return SSLHandshake.CERTIFICATE;
        }
        
        public int messageLength() {
            int msgLen = 3;
            for (final byte[] encodedCert : this.encodedCertChain) {
                msgLen += encodedCert.length + 3;
            }
            return msgLen;
        }
        
        public void send(final HandshakeOutStream hos) throws IOException {
            int listLen = 0;
            for (final byte[] encodedCert : this.encodedCertChain) {
                listLen += encodedCert.length + 3;
            }
            hos.putInt24(listLen);
            for (final byte[] encodedCert : this.encodedCertChain) {
                hos.putBytes24(encodedCert);
            }
        }
        
        @Override
        public String toString() {
            if (this.encodedCertChain.isEmpty()) {
                return "\"Certificates\": <empty list>";
            }
            final Object[] x509Certs = new Object[this.encodedCertChain.size()];
            try {
                final CertificateFactory cf = CertificateFactory.getInstance("X.509");
                int i = 0;
                for (final byte[] encodedCert : this.encodedCertChain) {
                    Object obj;
                    try {
                        obj = cf.generateCertificate(new ByteArrayInputStream(encodedCert));
                    }
                    catch (final CertificateException ce) {
                        obj = encodedCert;
                    }
                    x509Certs[i++] = obj;
                }
            }
            catch (final CertificateException ce2) {
                int i = 0;
                for (final byte[] encodedCert : this.encodedCertChain) {
                    x509Certs[i++] = encodedCert;
                }
            }
            final MessageFormat messageFormat = new MessageFormat("\"Certificates\": [\n{0}\n]", Locale.ENGLISH);
            final Object[] messageFields = { SSLLogger.toString(x509Certs) };
            return messageFormat.format(messageFields);
        }
    }
    
    private static final class T12CertificateProducer implements HandshakeProducer
    {
        @Override
        public byte[] produce(final ConnectionContext context, final SSLHandshake.HandshakeMessage message) throws IOException {
            final HandshakeContext hc = (HandshakeContext)context;
            if (hc.sslConfig.isClientMode) {
                return this.onProduceCertificate((ClientHandshakeContext)context, message);
            }
            return this.onProduceCertificate((ServerHandshakeContext)context, message);
        }
        
        private byte[] onProduceCertificate(final ServerHandshakeContext shc, final SSLHandshake.HandshakeMessage message) throws IOException {
            X509Authentication.X509Possession x509Possession = null;
            for (final SSLPossession possession : shc.handshakePossessions) {
                if (possession instanceof X509Authentication.X509Possession) {
                    x509Possession = (X509Authentication.X509Possession)possession;
                    break;
                }
            }
            if (x509Possession == null) {
                throw shc.conContext.fatal(Alert.INTERNAL_ERROR, "No expected X.509 certificate for server authentication");
            }
            shc.handshakeSession.setLocalPrivateKey(x509Possession.popPrivateKey);
            shc.handshakeSession.setLocalCertificates(x509Possession.popCerts);
            final T12CertificateMessage cm = new T12CertificateMessage(shc, x509Possession.popCerts);
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("Produced server Certificate handshake message", cm);
            }
            cm.write(shc.handshakeOutput);
            shc.handshakeOutput.flush();
            return null;
        }
        
        private byte[] onProduceCertificate(final ClientHandshakeContext chc, final SSLHandshake.HandshakeMessage message) throws IOException {
            X509Authentication.X509Possession x509Possession = null;
            for (final SSLPossession possession : chc.handshakePossessions) {
                if (possession instanceof X509Authentication.X509Possession) {
                    x509Possession = (X509Authentication.X509Possession)possession;
                    break;
                }
            }
            if (x509Possession == null) {
                if (!chc.negotiatedProtocol.useTLS10PlusSpec()) {
                    if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                        SSLLogger.fine("No X.509 certificate for client authentication, send a no_certificate alert", new Object[0]);
                    }
                    chc.conContext.warning(Alert.NO_CERTIFICATE);
                    return null;
                }
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("No X.509 certificate for client authentication, use empty Certificate message instead", new Object[0]);
                }
                x509Possession = new X509Authentication.X509Possession(null, new X509Certificate[0]);
            }
            chc.handshakeSession.setLocalPrivateKey(x509Possession.popPrivateKey);
            if (x509Possession.popCerts != null && x509Possession.popCerts.length != 0) {
                chc.handshakeSession.setLocalCertificates(x509Possession.popCerts);
            }
            else {
                chc.handshakeSession.setLocalCertificates(null);
            }
            final T12CertificateMessage cm = new T12CertificateMessage(chc, x509Possession.popCerts);
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("Produced client Certificate handshake message", cm);
            }
            cm.write(chc.handshakeOutput);
            chc.handshakeOutput.flush();
            return null;
        }
    }
    
    static final class T12CertificateConsumer implements SSLConsumer
    {
        private T12CertificateConsumer() {
        }
        
        @Override
        public void consume(final ConnectionContext context, final ByteBuffer message) throws IOException {
            final HandshakeContext hc = (HandshakeContext)context;
            hc.handshakeConsumers.remove(SSLHandshake.CERTIFICATE.id);
            final T12CertificateMessage cm = new T12CertificateMessage(hc, message);
            if (hc.sslConfig.isClientMode) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("Consuming server Certificate handshake message", cm);
                }
                this.onCertificate((ClientHandshakeContext)context, cm);
            }
            else {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("Consuming client Certificate handshake message", cm);
                }
                this.onCertificate((ServerHandshakeContext)context, cm);
            }
        }
        
        private void onCertificate(final ServerHandshakeContext shc, final T12CertificateMessage certificateMessage) throws IOException {
            final List<byte[]> encodedCerts = certificateMessage.encodedCertChain;
            if (encodedCerts != null && !encodedCerts.isEmpty()) {
                final X509Certificate[] x509Certs = new X509Certificate[encodedCerts.size()];
                try {
                    final CertificateFactory cf = CertificateFactory.getInstance("X.509");
                    int i = 0;
                    for (final byte[] encodedCert : encodedCerts) {
                        x509Certs[i++] = (X509Certificate)cf.generateCertificate(new ByteArrayInputStream(encodedCert));
                    }
                }
                catch (final CertificateException ce) {
                    throw shc.conContext.fatal(Alert.BAD_CERTIFICATE, "Failed to parse server certificates", ce);
                }
                checkClientCerts(shc, x509Certs);
                shc.handshakeCredentials.add(new X509Authentication.X509Credentials(x509Certs[0].getPublicKey(), x509Certs));
                shc.handshakeSession.setPeerCertificates(x509Certs);
                return;
            }
            shc.handshakeConsumers.remove(SSLHandshake.CERTIFICATE_VERIFY.id);
            if (shc.sslConfig.clientAuthType != ClientAuthType.CLIENT_AUTH_REQUESTED) {
                throw shc.conContext.fatal(Alert.BAD_CERTIFICATE, "Empty server certificate chain");
            }
        }
        
        private void onCertificate(final ClientHandshakeContext chc, final T12CertificateMessage certificateMessage) throws IOException {
            final List<byte[]> encodedCerts = certificateMessage.encodedCertChain;
            if (encodedCerts == null || encodedCerts.isEmpty()) {
                throw chc.conContext.fatal(Alert.BAD_CERTIFICATE, "Empty server certificate chain");
            }
            final X509Certificate[] x509Certs = new X509Certificate[encodedCerts.size()];
            try {
                final CertificateFactory cf = CertificateFactory.getInstance("X.509");
                int i = 0;
                for (final byte[] encodedCert : encodedCerts) {
                    x509Certs[i++] = (X509Certificate)cf.generateCertificate(new ByteArrayInputStream(encodedCert));
                }
            }
            catch (final CertificateException ce) {
                throw chc.conContext.fatal(Alert.BAD_CERTIFICATE, "Failed to parse server certificates", ce);
            }
            if (chc.reservedServerCerts != null && !chc.handshakeSession.useExtendedMasterSecret) {
                final String identityAlg = chc.sslConfig.identificationProtocol;
                if ((identityAlg == null || identityAlg.length() == 0) && !isIdentityEquivalent(x509Certs[0], chc.reservedServerCerts[0])) {
                    throw chc.conContext.fatal(Alert.BAD_CERTIFICATE, "server certificate change is restricted during renegotiation");
                }
            }
            if (chc.staplingActive) {
                chc.deferredCerts = x509Certs;
            }
            else {
                checkServerCerts(chc, x509Certs);
            }
            chc.handshakeCredentials.add(new X509Authentication.X509Credentials(x509Certs[0].getPublicKey(), x509Certs));
            chc.handshakeSession.setPeerCertificates(x509Certs);
        }
        
        private static boolean isIdentityEquivalent(final X509Certificate thisCert, final X509Certificate prevCert) {
            if (thisCert.equals(prevCert)) {
                return true;
            }
            Collection<List<?>> thisSubjectAltNames = null;
            try {
                thisSubjectAltNames = thisCert.getSubjectAlternativeNames();
            }
            catch (final CertificateParsingException cpe) {
                if (SSLLogger.isOn && SSLLogger.isOn("handshake")) {
                    SSLLogger.fine("Attempt to obtain subjectAltNames extension failed!", new Object[0]);
                }
            }
            Collection<List<?>> prevSubjectAltNames = null;
            try {
                prevSubjectAltNames = prevCert.getSubjectAlternativeNames();
            }
            catch (final CertificateParsingException cpe2) {
                if (SSLLogger.isOn && SSLLogger.isOn("handshake")) {
                    SSLLogger.fine("Attempt to obtain subjectAltNames extension failed!", new Object[0]);
                }
            }
            if (thisSubjectAltNames != null && prevSubjectAltNames != null) {
                final Collection<String> thisSubAltIPAddrs = getSubjectAltNames(thisSubjectAltNames, 7);
                final Collection<String> prevSubAltIPAddrs = getSubjectAltNames(prevSubjectAltNames, 7);
                if (thisSubAltIPAddrs != null && prevSubAltIPAddrs != null && isEquivalent(thisSubAltIPAddrs, prevSubAltIPAddrs)) {
                    return true;
                }
                final Collection<String> thisSubAltDnsNames = getSubjectAltNames(thisSubjectAltNames, 2);
                final Collection<String> prevSubAltDnsNames = getSubjectAltNames(prevSubjectAltNames, 2);
                if (thisSubAltDnsNames != null && prevSubAltDnsNames != null && isEquivalent(thisSubAltDnsNames, prevSubAltDnsNames)) {
                    return true;
                }
            }
            final X500Principal thisSubject = thisCert.getSubjectX500Principal();
            final X500Principal prevSubject = prevCert.getSubjectX500Principal();
            final X500Principal thisIssuer = thisCert.getIssuerX500Principal();
            final X500Principal prevIssuer = prevCert.getIssuerX500Principal();
            return !thisSubject.getName().isEmpty() && !prevSubject.getName().isEmpty() && thisSubject.equals(prevSubject) && thisIssuer.equals(prevIssuer);
        }
        
        private static Collection<String> getSubjectAltNames(final Collection<List<?>> subjectAltNames, final int type) {
            HashSet<String> subAltDnsNames = null;
            for (final List<?> subjectAltName : subjectAltNames) {
                final int subjectAltNameType = (int)subjectAltName.get(0);
                if (subjectAltNameType == type) {
                    final String subAltDnsName = (String)subjectAltName.get(1);
                    if (subAltDnsName == null || subAltDnsName.isEmpty()) {
                        continue;
                    }
                    if (subAltDnsNames == null) {
                        subAltDnsNames = new HashSet<String>(subjectAltNames.size());
                    }
                    subAltDnsNames.add(subAltDnsName);
                }
            }
            return subAltDnsNames;
        }
        
        private static boolean isEquivalent(final Collection<String> thisSubAltNames, final Collection<String> prevSubAltNames) {
            for (final String thisSubAltName : thisSubAltNames) {
                for (final String prevSubAltName : prevSubAltNames) {
                    if (thisSubAltName.equalsIgnoreCase(prevSubAltName)) {
                        return true;
                    }
                }
            }
            return false;
        }
        
        static void checkServerCerts(final ClientHandshakeContext chc, final X509Certificate[] certs) throws IOException {
            final X509TrustManager tm = chc.sslContext.getX509TrustManager();
            String keyExchangeString;
            if (chc.negotiatedCipherSuite.keyExchange == CipherSuite.KeyExchange.K_RSA_EXPORT || chc.negotiatedCipherSuite.keyExchange == CipherSuite.KeyExchange.K_DHE_RSA_EXPORT) {
                keyExchangeString = CipherSuite.KeyExchange.K_RSA.name;
            }
            else {
                keyExchangeString = chc.negotiatedCipherSuite.keyExchange.name;
            }
            try {
                if (!(tm instanceof X509ExtendedTrustManager)) {
                    throw new CertificateException("Improper X509TrustManager implementation");
                }
                if (chc.conContext.transport instanceof SSLEngine) {
                    final SSLEngine engine = (SSLEngine)chc.conContext.transport;
                    ((X509ExtendedTrustManager)tm).checkServerTrusted(certs.clone(), keyExchangeString, engine);
                }
                else {
                    final SSLSocket socket = (SSLSocket)chc.conContext.transport;
                    ((X509ExtendedTrustManager)tm).checkServerTrusted(certs.clone(), keyExchangeString, socket);
                }
                chc.handshakeSession.setPeerCertificates(certs);
            }
            catch (final CertificateException ce) {
                throw chc.conContext.fatal(getCertificateAlert(chc, ce), ce);
            }
        }
        
        private static void checkClientCerts(final ServerHandshakeContext shc, final X509Certificate[] certs) throws IOException {
            final X509TrustManager tm = shc.sslContext.getX509TrustManager();
            final PublicKey key = certs[0].getPublicKey();
            final String algorithm;
            final String keyAlgorithm = algorithm = key.getAlgorithm();
            String authType = null;
            switch (algorithm) {
                case "RSA":
                case "DSA":
                case "EC":
                case "RSASSA-PSS": {
                    authType = keyAlgorithm;
                    break;
                }
                default: {
                    authType = "UNKNOWN";
                    break;
                }
            }
            try {
                if (!(tm instanceof X509ExtendedTrustManager)) {
                    throw new CertificateException("Improper X509TrustManager implementation");
                }
                if (shc.conContext.transport instanceof SSLEngine) {
                    final SSLEngine engine = (SSLEngine)shc.conContext.transport;
                    ((X509ExtendedTrustManager)tm).checkClientTrusted(certs.clone(), authType, engine);
                }
                else {
                    final SSLSocket socket = (SSLSocket)shc.conContext.transport;
                    ((X509ExtendedTrustManager)tm).checkClientTrusted(certs.clone(), authType, socket);
                }
            }
            catch (final CertificateException ce) {
                throw shc.conContext.fatal(Alert.CERTIFICATE_UNKNOWN, ce);
            }
        }
        
        private static Alert getCertificateAlert(final ClientHandshakeContext chc, final CertificateException cexc) {
            Alert alert = Alert.CERTIFICATE_UNKNOWN;
            final Throwable baseCause = cexc.getCause();
            if (baseCause instanceof CertPathValidatorException) {
                final CertPathValidatorException cpve = (CertPathValidatorException)baseCause;
                final CertPathValidatorException.Reason reason = cpve.getReason();
                if (reason == CertPathValidatorException.BasicReason.REVOKED) {
                    alert = (chc.staplingActive ? Alert.BAD_CERT_STATUS_RESPONSE : Alert.CERTIFICATE_REVOKED);
                }
                else if (reason == CertPathValidatorException.BasicReason.UNDETERMINED_REVOCATION_STATUS) {
                    alert = (chc.staplingActive ? Alert.BAD_CERT_STATUS_RESPONSE : Alert.CERTIFICATE_UNKNOWN);
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
            int extLen = this.extensions.length();
            if (extLen == 0) {
                extLen = 2;
            }
            return 3 + this.encoded.length + extLen;
        }
        
        @Override
        public String toString() {
            final MessageFormat messageFormat = new MessageFormat("\n'{'\n{0}\n  \"extensions\": '{'\n{1}\n  '}'\n'}',", Locale.ENGLISH);
            Object x509Certs;
            try {
                final CertificateFactory cf = CertificateFactory.getInstance("X.509");
                x509Certs = cf.generateCertificate(new ByteArrayInputStream(this.encoded));
            }
            catch (final CertificateException ce) {
                x509Certs = this.encoded;
            }
            final Object[] messageFields = { SSLLogger.toString(x509Certs), Utilities.indent(this.extensions.toString(), "    ") };
            return messageFormat.format(messageFields);
        }
    }
    
    static final class T13CertificateMessage extends SSLHandshake.HandshakeMessage
    {
        private final byte[] requestContext;
        private final List<CertificateEntry> certEntries;
        
        T13CertificateMessage(final HandshakeContext context, final byte[] requestContext, final X509Certificate[] certificates) throws SSLException, CertificateException {
            super(context);
            this.requestContext = requestContext.clone();
            this.certEntries = new LinkedList<CertificateEntry>();
            for (final X509Certificate cert : certificates) {
                final byte[] encoded = cert.getEncoded();
                final SSLExtensions extensions = new SSLExtensions(this);
                this.certEntries.add(new CertificateEntry(encoded, extensions));
            }
        }
        
        T13CertificateMessage(final HandshakeContext handshakeContext, final byte[] requestContext, final List<CertificateEntry> certificates) {
            super(handshakeContext);
            this.requestContext = requestContext.clone();
            this.certEntries = certificates;
        }
        
        T13CertificateMessage(final HandshakeContext handshakeContext, final ByteBuffer m) throws IOException {
            super(handshakeContext);
            if (m.remaining() < 4) {
                throw new SSLProtocolException("Invalid Certificate message: insufficient data (length=" + m.remaining() + ")");
            }
            this.requestContext = Record.getBytes8(m);
            if (m.remaining() < 3) {
                throw new SSLProtocolException("Invalid Certificate message: insufficient certificate entries data (length=" + m.remaining() + ")");
            }
            final int listLen = Record.getInt24(m);
            if (listLen != m.remaining()) {
                throw new SSLProtocolException("Invalid Certificate message: incorrect list length (length=" + listLen + ")");
            }
            final SSLExtension[] enabledExtensions = handshakeContext.sslConfig.getEnabledExtensions(SSLHandshake.CERTIFICATE);
            final List<CertificateEntry> certList = new LinkedList<CertificateEntry>();
            while (m.hasRemaining()) {
                final byte[] encodedCert = Record.getBytes24(m);
                if (encodedCert.length == 0) {
                    throw new SSLProtocolException("Invalid Certificate message: empty cert_data");
                }
                final SSLExtensions extensions = new SSLExtensions(this, m, enabledExtensions);
                certList.add(new CertificateEntry(encodedCert, extensions));
                if (certList.size() > SSLConfiguration.maxCertificateChainLength) {
                    throw new SSLProtocolException("The certificate chain length (" + certList.size() + ") exceeds the maximum allowed length (" + SSLConfiguration.maxCertificateChainLength + ")");
                }
            }
            this.certEntries = Collections.unmodifiableList((List<? extends CertificateEntry>)certList);
        }
        
        public SSLHandshake handshakeType() {
            return SSLHandshake.CERTIFICATE;
        }
        
        public int messageLength() {
            int msgLen = 4 + this.requestContext.length;
            for (final CertificateEntry entry : this.certEntries) {
                msgLen += entry.getEncodedSize();
            }
            return msgLen;
        }
        
        public void send(final HandshakeOutStream hos) throws IOException {
            int entryListLen = 0;
            for (final CertificateEntry entry : this.certEntries) {
                entryListLen += entry.getEncodedSize();
            }
            hos.putBytes8(this.requestContext);
            hos.putInt24(entryListLen);
            for (final CertificateEntry entry : this.certEntries) {
                hos.putBytes24(entry.encoded);
                if (entry.extensions.length() == 0) {
                    hos.putInt16(0);
                }
                else {
                    entry.extensions.send(hos);
                }
            }
        }
        
        @Override
        public String toString() {
            final MessageFormat messageFormat = new MessageFormat("\"Certificate\": '{'\n  \"certificate_request_context\": \"{0}\",\n  \"certificate_list\": [{1}\n]\n'}'", Locale.ENGLISH);
            final StringBuilder builder = new StringBuilder(512);
            for (final CertificateEntry entry : this.certEntries) {
                builder.append(entry.toString());
            }
            final Object[] messageFields = { Utilities.toHexString(this.requestContext), Utilities.indent(builder.toString()) };
            return messageFormat.format(messageFields);
        }
    }
    
    private static final class T13CertificateProducer implements HandshakeProducer
    {
        @Override
        public byte[] produce(final ConnectionContext context, final SSLHandshake.HandshakeMessage message) throws IOException {
            final HandshakeContext hc = (HandshakeContext)context;
            if (hc.sslConfig.isClientMode) {
                return this.onProduceCertificate((ClientHandshakeContext)context, message);
            }
            return this.onProduceCertificate((ServerHandshakeContext)context, message);
        }
        
        private byte[] onProduceCertificate(final ServerHandshakeContext shc, final SSLHandshake.HandshakeMessage message) throws IOException {
            final ClientHello.ClientHelloMessage clientHello = (ClientHello.ClientHelloMessage)message;
            final SSLPossession pos = choosePossession(shc, clientHello);
            if (pos == null) {
                throw shc.conContext.fatal(Alert.HANDSHAKE_FAILURE, "No available authentication scheme");
            }
            if (!(pos instanceof X509Authentication.X509Possession)) {
                throw shc.conContext.fatal(Alert.HANDSHAKE_FAILURE, "No X.509 certificate for server authentication");
            }
            final X509Authentication.X509Possession x509Possession = (X509Authentication.X509Possession)pos;
            final X509Certificate[] localCerts = x509Possession.popCerts;
            if (localCerts == null || localCerts.length == 0) {
                throw shc.conContext.fatal(Alert.HANDSHAKE_FAILURE, "No X.509 certificate for server authentication");
            }
            shc.handshakePossessions.add(x509Possession);
            shc.handshakeSession.setLocalPrivateKey(x509Possession.popPrivateKey);
            shc.handshakeSession.setLocalCertificates(localCerts);
            T13CertificateMessage cm;
            try {
                cm = new T13CertificateMessage(shc, new byte[0], localCerts);
            }
            catch (final SSLException | CertificateException ce) {
                throw shc.conContext.fatal(Alert.HANDSHAKE_FAILURE, "Failed to produce server Certificate message", ce);
            }
            shc.stapleParams = StatusResponseManager.processStapling(shc);
            shc.staplingActive = (shc.stapleParams != null);
            final SSLExtension[] enabledCTExts = shc.sslConfig.getEnabledExtensions(SSLHandshake.CERTIFICATE, Arrays.asList(ProtocolVersion.PROTOCOLS_OF_13));
            for (final CertificateEntry certEnt : cm.certEntries) {
                shc.currentCertEntry = certEnt;
                certEnt.extensions.produce(shc, enabledCTExts);
            }
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("Produced server Certificate message", cm);
            }
            cm.write(shc.handshakeOutput);
            shc.handshakeOutput.flush();
            return null;
        }
        
        private static SSLPossession choosePossession(final HandshakeContext hc, final ClientHello.ClientHelloMessage clientHello) throws IOException {
            if (hc.peerRequestedCertSignSchemes == null || hc.peerRequestedCertSignSchemes.isEmpty()) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.warning("No signature_algorithms(_cert) in ClientHello", new Object[0]);
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
        
        private byte[] onProduceCertificate(final ClientHandshakeContext chc, final SSLHandshake.HandshakeMessage message) throws IOException {
            final ClientHello.ClientHelloMessage clientHello = (ClientHello.ClientHelloMessage)message;
            final SSLPossession pos = choosePossession(chc, clientHello);
            X509Certificate[] localCerts;
            if (pos == null) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("No available client authentication scheme", new Object[0]);
                }
                localCerts = new X509Certificate[0];
            }
            else {
                chc.handshakePossessions.add(pos);
                if (!(pos instanceof X509Authentication.X509Possession)) {
                    if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                        SSLLogger.fine("No X.509 certificate for client authentication", new Object[0]);
                    }
                    localCerts = new X509Certificate[0];
                }
                else {
                    final X509Authentication.X509Possession x509Possession = (X509Authentication.X509Possession)pos;
                    localCerts = x509Possession.popCerts;
                    chc.handshakeSession.setLocalPrivateKey(x509Possession.popPrivateKey);
                }
            }
            if (localCerts != null && localCerts.length != 0) {
                chc.handshakeSession.setLocalCertificates(localCerts);
            }
            else {
                chc.handshakeSession.setLocalCertificates(null);
            }
            T13CertificateMessage cm;
            try {
                cm = new T13CertificateMessage(chc, chc.certRequestContext, localCerts);
            }
            catch (final SSLException | CertificateException ce) {
                throw chc.conContext.fatal(Alert.HANDSHAKE_FAILURE, "Failed to produce client Certificate message", ce);
            }
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("Produced client Certificate message", cm);
            }
            cm.write(chc.handshakeOutput);
            chc.handshakeOutput.flush();
            return null;
        }
    }
    
    private static final class T13CertificateConsumer implements SSLConsumer
    {
        @Override
        public void consume(final ConnectionContext context, final ByteBuffer message) throws IOException {
            final HandshakeContext hc = (HandshakeContext)context;
            hc.handshakeConsumers.remove(SSLHandshake.CERTIFICATE.id);
            final T13CertificateMessage cm = new T13CertificateMessage(hc, message);
            if (hc.sslConfig.isClientMode) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("Consuming server Certificate handshake message", cm);
                }
                this.onConsumeCertificate((ClientHandshakeContext)context, cm);
            }
            else {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("Consuming client Certificate handshake message", cm);
                }
                this.onConsumeCertificate((ServerHandshakeContext)context, cm);
            }
        }
        
        private void onConsumeCertificate(final ServerHandshakeContext shc, final T13CertificateMessage certificateMessage) throws IOException {
            if (certificateMessage.certEntries != null && !certificateMessage.certEntries.isEmpty()) {
                final X509Certificate[] cliCerts = checkClientCerts(shc, certificateMessage.certEntries);
                shc.handshakeCredentials.add(new X509Authentication.X509Credentials(cliCerts[0].getPublicKey(), cliCerts));
                shc.handshakeSession.setPeerCertificates(cliCerts);
                return;
            }
            shc.handshakeConsumers.remove(SSLHandshake.CERTIFICATE_VERIFY.id);
            if (shc.sslConfig.clientAuthType == ClientAuthType.CLIENT_AUTH_REQUIRED) {
                throw shc.conContext.fatal(Alert.BAD_CERTIFICATE, "Empty client certificate chain");
            }
        }
        
        private void onConsumeCertificate(final ClientHandshakeContext chc, final T13CertificateMessage certificateMessage) throws IOException {
            if (certificateMessage.certEntries == null || certificateMessage.certEntries.isEmpty()) {
                throw chc.conContext.fatal(Alert.BAD_CERTIFICATE, "Empty server certificate chain");
            }
            final SSLExtension[] enabledExtensions = chc.sslConfig.getEnabledExtensions(SSLHandshake.CERTIFICATE);
            for (final CertificateEntry certEnt : certificateMessage.certEntries) {
                certEnt.extensions.consumeOnLoad(chc, enabledExtensions);
            }
            final X509Certificate[] srvCerts = checkServerCerts(chc, certificateMessage.certEntries);
            chc.handshakeCredentials.add(new X509Authentication.X509Credentials(srvCerts[0].getPublicKey(), srvCerts));
            chc.handshakeSession.setPeerCertificates(srvCerts);
        }
        
        private static X509Certificate[] checkClientCerts(final ServerHandshakeContext shc, final List<CertificateEntry> certEntries) throws IOException {
            final X509Certificate[] certs = new X509Certificate[certEntries.size()];
            try {
                final CertificateFactory cf = CertificateFactory.getInstance("X.509");
                int i = 0;
                for (final CertificateEntry entry : certEntries) {
                    certs[i++] = (X509Certificate)cf.generateCertificate(new ByteArrayInputStream(entry.encoded));
                }
            }
            catch (final CertificateException ce) {
                throw shc.conContext.fatal(Alert.BAD_CERTIFICATE, "Failed to parse server certificates", ce);
            }
            final String algorithm;
            final String keyAlgorithm = algorithm = certs[0].getPublicKey().getAlgorithm();
            String authType = null;
            switch (algorithm) {
                case "RSA":
                case "DSA":
                case "EC":
                case "RSASSA-PSS": {
                    authType = keyAlgorithm;
                    break;
                }
                default: {
                    authType = "UNKNOWN";
                    break;
                }
            }
            try {
                final X509TrustManager tm = shc.sslContext.getX509TrustManager();
                if (!(tm instanceof X509ExtendedTrustManager)) {
                    throw new CertificateException("Improper X509TrustManager implementation");
                }
                if (shc.conContext.transport instanceof SSLEngine) {
                    final SSLEngine engine = (SSLEngine)shc.conContext.transport;
                    ((X509ExtendedTrustManager)tm).checkClientTrusted(certs.clone(), authType, engine);
                }
                else {
                    final SSLSocket socket = (SSLSocket)shc.conContext.transport;
                    ((X509ExtendedTrustManager)tm).checkClientTrusted(certs.clone(), authType, socket);
                }
                shc.handshakeSession.setPeerCertificates(certs);
            }
            catch (final CertificateException ce2) {
                throw shc.conContext.fatal(Alert.CERTIFICATE_UNKNOWN, ce2);
            }
            return certs;
        }
        
        private static X509Certificate[] checkServerCerts(final ClientHandshakeContext chc, final List<CertificateEntry> certEntries) throws IOException {
            final X509Certificate[] certs = new X509Certificate[certEntries.size()];
            try {
                final CertificateFactory cf = CertificateFactory.getInstance("X.509");
                int i = 0;
                for (final CertificateEntry entry : certEntries) {
                    certs[i++] = (X509Certificate)cf.generateCertificate(new ByteArrayInputStream(entry.encoded));
                }
            }
            catch (final CertificateException ce) {
                throw chc.conContext.fatal(Alert.BAD_CERTIFICATE, "Failed to parse server certificates", ce);
            }
            final String authType = "UNKNOWN";
            try {
                final X509TrustManager tm = chc.sslContext.getX509TrustManager();
                if (!(tm instanceof X509ExtendedTrustManager)) {
                    throw new CertificateException("Improper X509TrustManager implementation");
                }
                if (chc.conContext.transport instanceof SSLEngine) {
                    final SSLEngine engine = (SSLEngine)chc.conContext.transport;
                    ((X509ExtendedTrustManager)tm).checkServerTrusted(certs.clone(), authType, engine);
                }
                else {
                    final SSLSocket socket = (SSLSocket)chc.conContext.transport;
                    ((X509ExtendedTrustManager)tm).checkServerTrusted(certs.clone(), authType, socket);
                }
                chc.handshakeSession.setPeerCertificates(certs);
            }
            catch (final CertificateException ce2) {
                throw chc.conContext.fatal(getCertificateAlert(chc, ce2), ce2);
            }
            return certs;
        }
        
        private static Alert getCertificateAlert(final ClientHandshakeContext chc, final CertificateException cexc) {
            Alert alert = Alert.CERTIFICATE_UNKNOWN;
            final Throwable baseCause = cexc.getCause();
            if (baseCause instanceof CertPathValidatorException) {
                final CertPathValidatorException cpve = (CertPathValidatorException)baseCause;
                final CertPathValidatorException.Reason reason = cpve.getReason();
                if (reason == CertPathValidatorException.BasicReason.REVOKED) {
                    alert = (chc.staplingActive ? Alert.BAD_CERT_STATUS_RESPONSE : Alert.CERTIFICATE_REVOKED);
                }
                else if (reason == CertPathValidatorException.BasicReason.UNDETERMINED_REVOCATION_STATUS) {
                    alert = (chc.staplingActive ? Alert.BAD_CERT_STATUS_RESPONSE : Alert.CERTIFICATE_UNKNOWN);
                }
            }
            return alert;
        }
    }
}
