package sun.security.ssl;

import java.text.MessageFormat;
import java.util.Locale;
import sun.security.provider.certpath.OCSPResponse;
import java.util.Iterator;
import javax.net.ssl.SSLHandshakeException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

final class CertificateStatus
{
    static final SSLConsumer handshakeConsumer;
    static final HandshakeProducer handshakeProducer;
    static final HandshakeAbsence handshakeAbsence;
    
    static {
        handshakeConsumer = new CertificateStatusConsumer();
        handshakeProducer = new CertificateStatusProducer();
        handshakeAbsence = new CertificateStatusAbsence();
    }
    
    static final class CertificateStatusMessage extends SSLHandshake.HandshakeMessage
    {
        final CertStatusExtension.CertStatusRequestType statusType;
        int encodedResponsesLen;
        int messageLength;
        final List<byte[]> encodedResponses;
        
        CertificateStatusMessage(final HandshakeContext handshakeContext) {
            super(handshakeContext);
            this.encodedResponsesLen = 0;
            this.messageLength = -1;
            this.encodedResponses = new ArrayList<byte[]>();
            final ServerHandshakeContext serverHandshakeContext = (ServerHandshakeContext)handshakeContext;
            final StatusResponseManager.StaplingParameters stapleParams = serverHandshakeContext.stapleParams;
            if (stapleParams == null) {
                throw new IllegalArgumentException("Unexpected null stapling parameters");
            }
            final X509Certificate[] array = (X509Certificate[])serverHandshakeContext.handshakeSession.getLocalCertificates();
            if (array == null) {
                throw new IllegalArgumentException("Unexpected null certificate chain");
            }
            this.statusType = stapleParams.statReqType;
            if (this.statusType == CertStatusExtension.CertStatusRequestType.OCSP) {
                byte[] array2 = stapleParams.responseMap.get(array[0]);
                if (array2 == null) {
                    array2 = new byte[0];
                }
                this.encodedResponses.add(array2);
                this.encodedResponsesLen += array2.length + 3;
            }
            else {
                if (this.statusType != CertStatusExtension.CertStatusRequestType.OCSP_MULTI) {
                    throw new IllegalArgumentException("Unsupported StatusResponseType: " + this.statusType);
                }
                final X509Certificate[] array3 = array;
                for (int length = array3.length, i = 0; i < length; ++i) {
                    byte[] array4 = stapleParams.responseMap.get(array3[i]);
                    if (array4 == null) {
                        array4 = new byte[0];
                    }
                    this.encodedResponses.add(array4);
                    this.encodedResponsesLen += array4.length + 3;
                }
            }
            this.messageLength = this.messageLength();
        }
        
        CertificateStatusMessage(final HandshakeContext handshakeContext, final ByteBuffer byteBuffer) throws IOException {
            super(handshakeContext);
            this.encodedResponsesLen = 0;
            this.messageLength = -1;
            this.encodedResponses = new ArrayList<byte[]>();
            this.statusType = CertStatusExtension.CertStatusRequestType.valueOf((byte)Record.getInt8(byteBuffer));
            if (this.statusType == CertStatusExtension.CertStatusRequestType.OCSP) {
                final byte[] bytes24 = Record.getBytes24(byteBuffer);
                if (bytes24.length <= 0) {
                    throw handshakeContext.conContext.fatal(Alert.HANDSHAKE_FAILURE, "Zero-length OCSP Response");
                }
                this.encodedResponses.add(bytes24);
                this.encodedResponsesLen = 3 + bytes24.length;
            }
            else {
                if (this.statusType != CertStatusExtension.CertStatusRequestType.OCSP_MULTI) {
                    throw handshakeContext.conContext.fatal(Alert.HANDSHAKE_FAILURE, "Unsupported StatusResponseType: " + this.statusType);
                }
                int i = Record.getInt24(byteBuffer);
                this.encodedResponsesLen = i;
                while (i > 0) {
                    final byte[] bytes25 = Record.getBytes24(byteBuffer);
                    this.encodedResponses.add(bytes25);
                    i -= bytes25.length + 3;
                }
                if (i != 0) {
                    throw handshakeContext.conContext.fatal(Alert.INTERNAL_ERROR, "Bad OCSP response list length");
                }
            }
            this.messageLength = this.messageLength();
        }
        
        public SSLHandshake handshakeType() {
            return SSLHandshake.CERTIFICATE_STATUS;
        }
        
        public int messageLength() {
            int messageLength = 1;
            if (this.messageLength == -1) {
                if (this.statusType == CertStatusExtension.CertStatusRequestType.OCSP) {
                    messageLength += this.encodedResponsesLen;
                }
                else if (this.statusType == CertStatusExtension.CertStatusRequestType.OCSP_MULTI) {
                    messageLength += 3 + this.encodedResponsesLen;
                }
                this.messageLength = messageLength;
            }
            return this.messageLength;
        }
        
        public void send(final HandshakeOutStream handshakeOutStream) throws IOException {
            handshakeOutStream.putInt8(this.statusType.id);
            if (this.statusType == CertStatusExtension.CertStatusRequestType.OCSP) {
                handshakeOutStream.putBytes24(this.encodedResponses.get(0));
            }
            else {
                if (this.statusType != CertStatusExtension.CertStatusRequestType.OCSP_MULTI) {
                    throw new SSLHandshakeException("Unsupported status_type: " + this.statusType.id);
                }
                handshakeOutStream.putInt24(this.encodedResponsesLen);
                for (final byte[] array : this.encodedResponses) {
                    if (array != null) {
                        handshakeOutStream.putBytes24(array);
                    }
                    else {
                        handshakeOutStream.putBytes24(null);
                    }
                }
            }
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            for (final byte[] array : this.encodedResponses) {
                if (array.length > 0) {
                    try {
                        sb.append(new OCSPResponse(array).toString()).append("\n");
                    }
                    catch (final IOException ex) {
                        sb.append("OCSP Response Exception: ").append(ex).append("\n");
                    }
                }
                else {
                    sb.append("<Zero-length entry>\n");
                }
            }
            return new MessageFormat("\"CertificateStatus\": '{'\n  \"type\"                : \"{0}\",\n  \"responses \"          : [\n{1}\n  ]\n'}'", Locale.ENGLISH).format(new Object[] { this.statusType.name, Utilities.indent(Utilities.indent(sb.toString())) });
        }
    }
    
    private static final class CertificateStatusConsumer implements SSLConsumer
    {
        @Override
        public void consume(final ConnectionContext connectionContext, final ByteBuffer byteBuffer) throws IOException {
            final ClientHandshakeContext clientHandshakeContext = (ClientHandshakeContext)connectionContext;
            final CertificateStatusMessage certificateStatusMessage = new CertificateStatusMessage(clientHandshakeContext, byteBuffer);
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("Consuming server CertificateStatus handshake message", certificateStatusMessage);
            }
            clientHandshakeContext.handshakeSession.setStatusResponses(certificateStatusMessage.encodedResponses);
            CertificateMessage.T12CertificateConsumer.checkServerCerts(clientHandshakeContext, clientHandshakeContext.deferredCerts);
            clientHandshakeContext.handshakeConsumers.remove(SSLHandshake.CERTIFICATE_STATUS.id);
        }
    }
    
    private static final class CertificateStatusProducer implements HandshakeProducer
    {
        @Override
        public byte[] produce(final ConnectionContext connectionContext, final SSLHandshake.HandshakeMessage handshakeMessage) throws IOException {
            final ServerHandshakeContext serverHandshakeContext = (ServerHandshakeContext)connectionContext;
            if (!serverHandshakeContext.staplingActive) {
                return null;
            }
            final CertificateStatusMessage certificateStatusMessage = new CertificateStatusMessage(serverHandshakeContext);
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("Produced server CertificateStatus handshake message", certificateStatusMessage);
            }
            certificateStatusMessage.write(serverHandshakeContext.handshakeOutput);
            serverHandshakeContext.handshakeOutput.flush();
            return null;
        }
    }
    
    private static final class CertificateStatusAbsence implements HandshakeAbsence
    {
        @Override
        public void absent(final ConnectionContext connectionContext, final SSLHandshake.HandshakeMessage handshakeMessage) throws IOException {
            final ClientHandshakeContext clientHandshakeContext = (ClientHandshakeContext)connectionContext;
            if (clientHandshakeContext.staplingActive) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("Server did not send CertificateStatus, checking cert chain without status info.", new Object[0]);
                }
                CertificateMessage.T12CertificateConsumer.checkServerCerts(clientHandshakeContext, clientHandshakeContext.deferredCerts);
            }
        }
    }
}
