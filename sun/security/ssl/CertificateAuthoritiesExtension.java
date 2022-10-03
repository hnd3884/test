package sun.security.ssl;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.Iterator;
import javax.security.auth.x500.X500Principal;
import java.util.Collections;
import java.util.ArrayList;
import java.security.cert.X509Certificate;
import java.io.IOException;
import java.util.LinkedList;
import javax.net.ssl.SSLProtocolException;
import java.nio.ByteBuffer;
import java.util.List;

final class CertificateAuthoritiesExtension
{
    static final HandshakeProducer chNetworkProducer;
    static final SSLExtension.ExtensionConsumer chOnLoadConsumer;
    static final HandshakeProducer crNetworkProducer;
    static final SSLExtension.ExtensionConsumer crOnLoadConsumer;
    static final SSLStringizer ssStringizer;
    
    static {
        chNetworkProducer = new CHCertificateAuthoritiesProducer();
        chOnLoadConsumer = new CHCertificateAuthoritiesConsumer();
        crNetworkProducer = new CRCertificateAuthoritiesProducer();
        crOnLoadConsumer = new CRCertificateAuthoritiesConsumer();
        ssStringizer = new CertificateAuthoritiesStringizer();
    }
    
    static final class CertificateAuthoritiesSpec implements SSLExtension.SSLExtensionSpec
    {
        final List<byte[]> authorities;
        
        private CertificateAuthoritiesSpec(final List<byte[]> authorities) {
            this.authorities = authorities;
        }
        
        private CertificateAuthoritiesSpec(final HandshakeContext handshakeContext, final ByteBuffer byteBuffer) throws IOException {
            if (byteBuffer.remaining() < 3) {
                if (handshakeContext != null) {
                    throw handshakeContext.conContext.fatal(Alert.DECODE_ERROR, new SSLProtocolException("Invalid certificate_authorities extension: insufficient data"));
                }
                throw new SSLProtocolException("Invalid certificate_authorities extension: insufficient data");
            }
            else {
                int i = Record.getInt16(byteBuffer);
                if (i == 0) {
                    if (handshakeContext != null) {
                        throw handshakeContext.conContext.fatal(Alert.DECODE_ERROR, "Invalid certificate_authorities extension: no certificate authorities");
                    }
                    throw new SSLProtocolException("Invalid certificate_authorities extension: no certificate authorities");
                }
                else {
                    if (i <= byteBuffer.remaining()) {
                        this.authorities = new LinkedList<byte[]>();
                        while (i > 0) {
                            final byte[] bytes16 = Record.getBytes16(byteBuffer);
                            i -= 2 + bytes16.length;
                            this.authorities.add(bytes16);
                        }
                        return;
                    }
                    if (handshakeContext != null) {
                        throw handshakeContext.conContext.fatal(Alert.DECODE_ERROR, "Invalid certificate_authorities extension: insufficient data");
                    }
                    throw new SSLProtocolException("Invalid certificate_authorities extension: insufficient data");
                }
            }
        }
        
        private static List<byte[]> getEncodedAuthorities(final X509Certificate[] array) {
            final ArrayList list = new ArrayList(array.length);
            int n = 0;
            for (int length = array.length, i = 0; i < length; ++i) {
                final byte[] encoded = array[i].getSubjectX500Principal().getEncoded();
                n += encoded.length;
                if (n > 65535) {
                    return Collections.emptyList();
                }
                if (encoded.length != 0) {
                    list.add(encoded);
                }
            }
            return list;
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
        
        @Override
        public String toString() {
            final MessageFormat messageFormat = new MessageFormat("\"certificate authorities\": '['\n{0}']'", Locale.ENGLISH);
            final StringBuilder sb = new StringBuilder(512);
            final Iterator<byte[]> iterator = this.authorities.iterator();
            while (iterator.hasNext()) {
                sb.append(new X500Principal(iterator.next()).toString());
                sb.append("\n");
            }
            return messageFormat.format(new Object[] { Utilities.indent(sb.toString()) });
        }
    }
    
    private static final class CertificateAuthoritiesStringizer implements SSLStringizer
    {
        @Override
        public String toString(final ByteBuffer byteBuffer) {
            try {
                return new CertificateAuthoritiesSpec((HandshakeContext)null, byteBuffer).toString();
            }
            catch (final IOException ex) {
                return ex.getMessage();
            }
        }
    }
    
    private static final class CHCertificateAuthoritiesProducer implements HandshakeProducer
    {
        @Override
        public byte[] produce(final ConnectionContext connectionContext, final SSLHandshake.HandshakeMessage handshakeMessage) throws IOException {
            final ClientHandshakeContext clientHandshakeContext = (ClientHandshakeContext)connectionContext;
            if (!clientHandshakeContext.sslConfig.isAvailable(SSLExtension.CH_CERTIFICATE_AUTHORITIES)) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("Ignore unavailable certificate_authorities extension", new Object[0]);
                }
                return null;
            }
            final X509Certificate[] acceptedIssuers = clientHandshakeContext.sslContext.getX509TrustManager().getAcceptedIssuers();
            if (acceptedIssuers.length == 0) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("No available certificate authorities", new Object[0]);
                }
                return null;
            }
            final List access$600 = getEncodedAuthorities(acceptedIssuers);
            if (access$600.isEmpty()) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.warning("The number of CAs exceeds the maximum sizeof the certificate_authorities extension", new Object[0]);
                }
                return null;
            }
            final CertificateAuthoritiesSpec certificateAuthoritiesSpec = new CertificateAuthoritiesSpec(access$600);
            int n = 0;
            final Iterator<byte[]> iterator = certificateAuthoritiesSpec.authorities.iterator();
            while (iterator.hasNext()) {
                n += iterator.next().length + 2;
            }
            final byte[] array = new byte[n + 2];
            final ByteBuffer wrap = ByteBuffer.wrap(array);
            Record.putInt16(wrap, n);
            final Iterator<byte[]> iterator2 = certificateAuthoritiesSpec.authorities.iterator();
            while (iterator2.hasNext()) {
                Record.putBytes16(wrap, iterator2.next());
            }
            clientHandshakeContext.handshakeExtensions.put(SSLExtension.CH_CERTIFICATE_AUTHORITIES, certificateAuthoritiesSpec);
            return array;
        }
    }
    
    private static final class CHCertificateAuthoritiesConsumer implements SSLExtension.ExtensionConsumer
    {
        @Override
        public void consume(final ConnectionContext connectionContext, final SSLHandshake.HandshakeMessage handshakeMessage, final ByteBuffer byteBuffer) throws IOException {
            final ServerHandshakeContext serverHandshakeContext = (ServerHandshakeContext)connectionContext;
            if (!serverHandshakeContext.sslConfig.isAvailable(SSLExtension.CH_CERTIFICATE_AUTHORITIES)) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("Ignore unavailable certificate_authorities extension", new Object[0]);
                }
                return;
            }
            final CertificateAuthoritiesSpec certificateAuthoritiesSpec = new CertificateAuthoritiesSpec((HandshakeContext)serverHandshakeContext, byteBuffer);
            serverHandshakeContext.peerSupportedAuthorities = certificateAuthoritiesSpec.getAuthorities();
            serverHandshakeContext.handshakeExtensions.put(SSLExtension.CH_CERTIFICATE_AUTHORITIES, certificateAuthoritiesSpec);
        }
    }
    
    private static final class CRCertificateAuthoritiesProducer implements HandshakeProducer
    {
        @Override
        public byte[] produce(final ConnectionContext connectionContext, final SSLHandshake.HandshakeMessage handshakeMessage) throws IOException {
            final ServerHandshakeContext serverHandshakeContext = (ServerHandshakeContext)connectionContext;
            if (!serverHandshakeContext.sslConfig.isAvailable(SSLExtension.CR_CERTIFICATE_AUTHORITIES)) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("Ignore unavailable certificate_authorities extension", new Object[0]);
                }
                return null;
            }
            final X509Certificate[] acceptedIssuers = serverHandshakeContext.sslContext.getX509TrustManager().getAcceptedIssuers();
            if (acceptedIssuers.length == 0) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("No available certificate authorities", new Object[0]);
                }
                return null;
            }
            final List access$600 = getEncodedAuthorities(acceptedIssuers);
            if (access$600.isEmpty()) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.warning("Too many certificate authorities to use the certificate_authorities extension", new Object[0]);
                }
                return null;
            }
            final CertificateAuthoritiesSpec certificateAuthoritiesSpec = new CertificateAuthoritiesSpec(access$600);
            int n = 0;
            final Iterator<byte[]> iterator = certificateAuthoritiesSpec.authorities.iterator();
            while (iterator.hasNext()) {
                n += iterator.next().length + 2;
            }
            final byte[] array = new byte[n + 2];
            final ByteBuffer wrap = ByteBuffer.wrap(array);
            Record.putInt16(wrap, n);
            final Iterator<byte[]> iterator2 = certificateAuthoritiesSpec.authorities.iterator();
            while (iterator2.hasNext()) {
                Record.putBytes16(wrap, iterator2.next());
            }
            serverHandshakeContext.handshakeExtensions.put(SSLExtension.CR_CERTIFICATE_AUTHORITIES, certificateAuthoritiesSpec);
            return array;
        }
    }
    
    private static final class CRCertificateAuthoritiesConsumer implements SSLExtension.ExtensionConsumer
    {
        @Override
        public void consume(final ConnectionContext connectionContext, final SSLHandshake.HandshakeMessage handshakeMessage, final ByteBuffer byteBuffer) throws IOException {
            final ClientHandshakeContext clientHandshakeContext = (ClientHandshakeContext)connectionContext;
            if (!clientHandshakeContext.sslConfig.isAvailable(SSLExtension.CR_CERTIFICATE_AUTHORITIES)) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("Ignore unavailable certificate_authorities extension", new Object[0]);
                }
                return;
            }
            final CertificateAuthoritiesSpec certificateAuthoritiesSpec = new CertificateAuthoritiesSpec((HandshakeContext)clientHandshakeContext, byteBuffer);
            clientHandshakeContext.peerSupportedAuthorities = certificateAuthoritiesSpec.getAuthorities();
            clientHandshakeContext.handshakeExtensions.put(SSLExtension.CR_CERTIFICATE_AUTHORITIES, certificateAuthoritiesSpec);
        }
    }
}
