package sun.security.ssl;

import java.util.Collection;
import java.security.cert.CertificateException;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import sun.security.provider.certpath.OCSPResponse;
import java.util.Iterator;
import sun.security.util.DerValue;
import sun.security.util.DerInputStream;
import java.util.ArrayList;
import java.security.cert.Extension;
import sun.security.provider.certpath.ResponderId;
import java.util.List;
import sun.misc.HexDumpEncoder;
import java.text.MessageFormat;
import java.util.Locale;
import java.io.IOException;
import javax.net.ssl.SSLProtocolException;
import java.nio.ByteBuffer;

final class CertStatusExtension
{
    static final HandshakeProducer chNetworkProducer;
    static final SSLExtension.ExtensionConsumer chOnLoadConsumer;
    static final HandshakeProducer shNetworkProducer;
    static final SSLExtension.ExtensionConsumer shOnLoadConsumer;
    static final HandshakeProducer ctNetworkProducer;
    static final SSLExtension.ExtensionConsumer ctOnLoadConsumer;
    static final SSLStringizer certStatusReqStringizer;
    static final HandshakeProducer chV2NetworkProducer;
    static final SSLExtension.ExtensionConsumer chV2OnLoadConsumer;
    static final HandshakeProducer shV2NetworkProducer;
    static final SSLExtension.ExtensionConsumer shV2OnLoadConsumer;
    static final SSLStringizer certStatusReqV2Stringizer;
    static final SSLStringizer certStatusRespStringizer;
    
    static {
        chNetworkProducer = new CHCertStatusReqProducer();
        chOnLoadConsumer = new CHCertStatusReqConsumer();
        shNetworkProducer = new SHCertStatusReqProducer();
        shOnLoadConsumer = new SHCertStatusReqConsumer();
        ctNetworkProducer = new CTCertStatusResponseProducer();
        ctOnLoadConsumer = new CTCertStatusResponseConsumer();
        certStatusReqStringizer = new CertStatusRequestStringizer();
        chV2NetworkProducer = new CHCertStatusReqV2Producer();
        chV2OnLoadConsumer = new CHCertStatusReqV2Consumer();
        shV2NetworkProducer = new SHCertStatusReqV2Producer();
        shV2OnLoadConsumer = new SHCertStatusReqV2Consumer();
        certStatusReqV2Stringizer = new CertStatusRequestsStringizer();
        certStatusRespStringizer = new CertStatusRespStringizer();
    }
    
    static final class CertStatusRequestSpec implements SSLExtension.SSLExtensionSpec
    {
        static final CertStatusRequestSpec DEFAULT;
        final CertStatusRequest statusRequest;
        
        private CertStatusRequestSpec(final CertStatusRequest statusRequest) {
            this.statusRequest = statusRequest;
        }
        
        private CertStatusRequestSpec(final ByteBuffer byteBuffer) throws IOException {
            if (byteBuffer.remaining() == 0) {
                this.statusRequest = null;
                return;
            }
            if (byteBuffer.remaining() < 1) {
                throw new SSLProtocolException("Invalid status_request extension: insufficient data");
            }
            final byte b = (byte)Record.getInt8(byteBuffer);
            final byte[] array = new byte[byteBuffer.remaining()];
            if (array.length != 0) {
                byteBuffer.get(array);
            }
            if (b == CertStatusRequestType.OCSP.id) {
                this.statusRequest = new OCSPStatusRequest(b, array);
            }
            else {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.info("Unknown certificate status request (status type: " + b + ")", new Object[0]);
                }
                this.statusRequest = new CertStatusRequest(b, array);
            }
        }
        
        @Override
        public String toString() {
            return (this.statusRequest == null) ? "<empty>" : this.statusRequest.toString();
        }
        
        static {
            DEFAULT = new CertStatusRequestSpec(OCSPStatusRequest.EMPTY_OCSP);
        }
    }
    
    static final class CertStatusResponseSpec implements SSLExtension.SSLExtensionSpec
    {
        final CertStatusResponse statusResponse;
        
        private CertStatusResponseSpec(final CertStatusResponse statusResponse) {
            this.statusResponse = statusResponse;
        }
        
        private CertStatusResponseSpec(final ByteBuffer byteBuffer) throws IOException {
            if (byteBuffer.remaining() < 2) {
                throw new SSLProtocolException("Invalid status_request extension: insufficient data");
            }
            final byte b = (byte)Record.getInt8(byteBuffer);
            final byte[] bytes24 = Record.getBytes24(byteBuffer);
            if (b == CertStatusRequestType.OCSP.id) {
                this.statusResponse = new OCSPStatusResponse(b, bytes24);
            }
            else {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.info("Unknown certificate status response (status type: " + b + ")", new Object[0]);
                }
                this.statusResponse = new CertStatusResponse(b, bytes24);
            }
        }
        
        @Override
        public String toString() {
            return (this.statusResponse == null) ? "<empty>" : this.statusResponse.toString();
        }
    }
    
    private static final class CertStatusRequestStringizer implements SSLStringizer
    {
        @Override
        public String toString(final ByteBuffer byteBuffer) {
            try {
                return new CertStatusRequestSpec(byteBuffer).toString();
            }
            catch (final IOException ex) {
                return ex.getMessage();
            }
        }
    }
    
    private static final class CertStatusRespStringizer implements SSLStringizer
    {
        @Override
        public String toString(final ByteBuffer byteBuffer) {
            try {
                return new CertStatusResponseSpec(byteBuffer).toString();
            }
            catch (final IOException ex) {
                return ex.getMessage();
            }
        }
    }
    
    enum CertStatusRequestType
    {
        OCSP((byte)1, "ocsp"), 
        OCSP_MULTI((byte)2, "ocsp_multi");
        
        final byte id;
        final String name;
        
        private CertStatusRequestType(final byte id, final String name) {
            this.id = id;
            this.name = name;
        }
        
        static CertStatusRequestType valueOf(final byte b) {
            for (final CertStatusRequestType certStatusRequestType : values()) {
                if (certStatusRequestType.id == b) {
                    return certStatusRequestType;
                }
            }
            return null;
        }
        
        static String nameOf(final byte b) {
            for (final CertStatusRequestType certStatusRequestType : values()) {
                if (certStatusRequestType.id == b) {
                    return certStatusRequestType.name;
                }
            }
            return "UNDEFINED-CERT-STATUS-TYPE(" + b + ")";
        }
    }
    
    static class CertStatusRequest
    {
        final byte statusType;
        final byte[] encodedRequest;
        
        protected CertStatusRequest(final byte statusType, final byte[] encodedRequest) {
            this.statusType = statusType;
            this.encodedRequest = encodedRequest;
        }
        
        @Override
        public String toString() {
            return new MessageFormat("\"certificate status type\": {0}\n\"encoded certificate status\": '{'\n{1}\n'}'", Locale.ENGLISH).format(new Object[] { CertStatusRequestType.nameOf(this.statusType), Utilities.indent(new HexDumpEncoder().encodeBuffer(this.encodedRequest)) });
        }
    }
    
    static final class OCSPStatusRequest extends CertStatusRequest
    {
        static final OCSPStatusRequest EMPTY_OCSP;
        static final OCSPStatusRequest EMPTY_OCSP_MULTI;
        final List<ResponderId> responderIds;
        final List<Extension> extensions;
        private final int ridListLen;
        private final int extListLen;
        
        private OCSPStatusRequest(final byte b, final byte[] array) throws IOException {
            super(b, array);
            if (array == null || array.length < 4) {
                throw new SSLProtocolException("Invalid OCSP status request: insufficient data");
            }
            final ArrayList responderIds = new ArrayList();
            final ArrayList extensions = new ArrayList();
            final ByteBuffer wrap = ByteBuffer.wrap(array);
            this.ridListLen = Record.getInt16(wrap);
            if (wrap.remaining() < this.ridListLen + 2) {
                throw new SSLProtocolException("Invalid OCSP status request: insufficient data");
            }
            int i;
            byte[] bytes16;
            for (i = this.ridListLen; i >= 2; i -= bytes16.length + 2) {
                bytes16 = Record.getBytes16(wrap);
                try {
                    responderIds.add(new ResponderId(bytes16));
                }
                catch (final IOException ex) {
                    throw new SSLProtocolException("Invalid OCSP status request: invalid responder ID");
                }
            }
            if (i != 0) {
                throw new SSLProtocolException("Invalid OCSP status request: incomplete data");
            }
            final byte[] bytes17 = Record.getBytes16(wrap);
            this.extListLen = bytes17.length;
            if (this.extListLen > 0) {
                try {
                    final DerValue[] sequence = new DerInputStream(bytes17).getSequence(bytes17.length);
                    for (int length = sequence.length, j = 0; j < length; ++j) {
                        extensions.add(new sun.security.x509.Extension(sequence[j]));
                    }
                }
                catch (final IOException ex2) {
                    throw new SSLProtocolException("Invalid OCSP status request: invalid extension");
                }
            }
            this.responderIds = responderIds;
            this.extensions = extensions;
        }
        
        @Override
        public String toString() {
            final MessageFormat messageFormat = new MessageFormat("\"certificate status type\": {0}\n\"OCSP status request\": '{'\n{1}\n'}'", Locale.ENGLISH);
            final MessageFormat messageFormat2 = new MessageFormat("\"responder_id\": {0}\n\"request extensions\": '{'\n{1}\n'}'", Locale.ENGLISH);
            String string = "<empty>";
            if (!this.responderIds.isEmpty()) {
                string = this.responderIds.toString();
            }
            String string2 = "<empty>";
            if (!this.extensions.isEmpty()) {
                final StringBuilder sb = new StringBuilder(512);
                int n = 1;
                for (final Extension extension : this.extensions) {
                    if (n != 0) {
                        n = 0;
                    }
                    else {
                        sb.append(",\n");
                    }
                    sb.append("{\n").append(Utilities.indent(extension.toString())).append("}");
                }
                string2 = sb.toString();
            }
            return messageFormat.format(new Object[] { CertStatusRequestType.nameOf(this.statusType), Utilities.indent(messageFormat2.format(new Object[] { string, Utilities.indent(string2) })) });
        }
        
        static {
            OCSPStatusRequest empty_OCSP = null;
            OCSPStatusRequest empty_OCSP_MULTI = null;
            try {
                empty_OCSP = new OCSPStatusRequest(CertStatusRequestType.OCSP.id, new byte[] { 0, 0, 0, 0 });
                empty_OCSP_MULTI = new OCSPStatusRequest(CertStatusRequestType.OCSP_MULTI.id, new byte[] { 0, 0, 0, 0 });
            }
            catch (final IOException ex) {}
            EMPTY_OCSP = empty_OCSP;
            EMPTY_OCSP_MULTI = empty_OCSP_MULTI;
        }
    }
    
    static class CertStatusResponse
    {
        final byte statusType;
        final byte[] encodedResponse;
        
        protected CertStatusResponse(final byte statusType, final byte[] encodedResponse) {
            this.statusType = statusType;
            this.encodedResponse = encodedResponse;
        }
        
        byte[] toByteArray() throws IOException {
            final ByteBuffer wrap = ByteBuffer.wrap(new byte[this.encodedResponse.length + 4]);
            Record.putInt8(wrap, this.statusType);
            Record.putBytes24(wrap, this.encodedResponse);
            return wrap.array();
        }
        
        @Override
        public String toString() {
            return new MessageFormat("\"certificate status response type\": {0}\n\"encoded certificate status\": '{'\n{1}\n'}'", Locale.ENGLISH).format(new Object[] { CertStatusRequestType.nameOf(this.statusType), Utilities.indent(new HexDumpEncoder().encodeBuffer(this.encodedResponse)) });
        }
    }
    
    static final class OCSPStatusResponse extends CertStatusResponse
    {
        final OCSPResponse ocspResponse;
        
        private OCSPStatusResponse(final byte b, final byte[] array) throws IOException {
            super(b, array);
            if (array == null || array.length < 1) {
                throw new SSLProtocolException("Invalid OCSP status response: insufficient data");
            }
            this.ocspResponse = new OCSPResponse(array);
        }
        
        @Override
        public String toString() {
            return new MessageFormat("\"certificate status response type\": {0}\n\"OCSP status response\": '{'\n{1}\n'}'", Locale.ENGLISH).format(new Object[] { CertStatusRequestType.nameOf(this.statusType), Utilities.indent(this.ocspResponse.toString()) });
        }
    }
    
    private static final class CHCertStatusReqProducer implements HandshakeProducer
    {
        @Override
        public byte[] produce(final ConnectionContext connectionContext, final SSLHandshake.HandshakeMessage handshakeMessage) throws IOException {
            final ClientHandshakeContext clientHandshakeContext = (ClientHandshakeContext)connectionContext;
            if (!clientHandshakeContext.sslContext.isStaplingEnabled(true)) {
                return null;
            }
            if (!clientHandshakeContext.sslConfig.isAvailable(SSLExtension.CH_STATUS_REQUEST)) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("Ignore unavailable extension: " + SSLExtension.CH_STATUS_REQUEST.name, new Object[0]);
                }
                return null;
            }
            final byte[] array = { 1, 0, 0, 0, 0 };
            clientHandshakeContext.handshakeExtensions.put(SSLExtension.CH_STATUS_REQUEST, CertStatusRequestSpec.DEFAULT);
            return array;
        }
    }
    
    private static final class CHCertStatusReqConsumer implements SSLExtension.ExtensionConsumer
    {
        @Override
        public void consume(final ConnectionContext connectionContext, final SSLHandshake.HandshakeMessage handshakeMessage, final ByteBuffer byteBuffer) throws IOException {
            final ServerHandshakeContext serverHandshakeContext = (ServerHandshakeContext)connectionContext;
            if (!serverHandshakeContext.sslConfig.isAvailable(SSLExtension.CH_STATUS_REQUEST)) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("Ignore unavailable extension: " + SSLExtension.CH_STATUS_REQUEST.name, new Object[0]);
                }
                return;
            }
            CertStatusRequestSpec certStatusRequestSpec;
            try {
                certStatusRequestSpec = new CertStatusRequestSpec(byteBuffer);
            }
            catch (final IOException ex) {
                throw serverHandshakeContext.conContext.fatal(Alert.UNEXPECTED_MESSAGE, ex);
            }
            serverHandshakeContext.handshakeExtensions.put(SSLExtension.CH_STATUS_REQUEST, certStatusRequestSpec);
            if (!serverHandshakeContext.isResumption && !serverHandshakeContext.negotiatedProtocol.useTLS13PlusSpec()) {
                serverHandshakeContext.handshakeProducers.put(SSLHandshake.CERTIFICATE_STATUS.id, SSLHandshake.CERTIFICATE_STATUS);
            }
        }
    }
    
    private static final class SHCertStatusReqProducer implements HandshakeProducer
    {
        @Override
        public byte[] produce(final ConnectionContext connectionContext, final SSLHandshake.HandshakeMessage handshakeMessage) throws IOException {
            final ServerHandshakeContext serverHandshakeContext = (ServerHandshakeContext)connectionContext;
            if (serverHandshakeContext.stapleParams == null || serverHandshakeContext.stapleParams.statusRespExt != SSLExtension.CH_STATUS_REQUEST) {
                return null;
            }
            if (serverHandshakeContext.handshakeExtensions.get(SSLExtension.CH_STATUS_REQUEST) == null) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.finest("Ignore unavailable extension: " + SSLExtension.CH_STATUS_REQUEST.name, new Object[0]);
                }
                return null;
            }
            if (serverHandshakeContext.isResumption) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.finest("No status_request response for session resuming", new Object[0]);
                }
                return null;
            }
            final byte[] array = new byte[0];
            serverHandshakeContext.handshakeExtensions.put(SSLExtension.SH_STATUS_REQUEST, CertStatusRequestSpec.DEFAULT);
            return array;
        }
    }
    
    private static final class SHCertStatusReqConsumer implements SSLExtension.ExtensionConsumer
    {
        @Override
        public void consume(final ConnectionContext connectionContext, final SSLHandshake.HandshakeMessage handshakeMessage, final ByteBuffer byteBuffer) throws IOException {
            final ClientHandshakeContext clientHandshakeContext = (ClientHandshakeContext)connectionContext;
            if (clientHandshakeContext.handshakeExtensions.get(SSLExtension.CH_STATUS_REQUEST) == null) {
                throw clientHandshakeContext.conContext.fatal(Alert.UNEXPECTED_MESSAGE, "Unexpected status_request extension in ServerHello");
            }
            if (byteBuffer.hasRemaining()) {
                throw clientHandshakeContext.conContext.fatal(Alert.UNEXPECTED_MESSAGE, "Invalid status_request extension in ServerHello message: the extension data must be empty");
            }
            clientHandshakeContext.handshakeExtensions.put(SSLExtension.SH_STATUS_REQUEST, CertStatusRequestSpec.DEFAULT);
            clientHandshakeContext.staplingActive = clientHandshakeContext.sslContext.isStaplingEnabled(true);
            if (clientHandshakeContext.staplingActive) {
                clientHandshakeContext.handshakeConsumers.put(SSLHandshake.CERTIFICATE_STATUS.id, SSLHandshake.CERTIFICATE_STATUS);
            }
        }
    }
    
    static final class CertStatusRequestV2Spec implements SSLExtension.SSLExtensionSpec
    {
        static final CertStatusRequestV2Spec DEFAULT;
        final CertStatusRequest[] certStatusRequests;
        
        private CertStatusRequestV2Spec(final CertStatusRequest[] certStatusRequests) {
            this.certStatusRequests = certStatusRequests;
        }
        
        private CertStatusRequestV2Spec(final ByteBuffer byteBuffer) throws IOException {
            if (byteBuffer.remaining() == 0) {
                this.certStatusRequests = new CertStatusRequest[0];
                return;
            }
            if (byteBuffer.remaining() < 5) {
                throw new SSLProtocolException("Invalid status_request_v2 extension: insufficient data");
            }
            final int int16 = Record.getInt16(byteBuffer);
            if (int16 <= 0) {
                throw new SSLProtocolException("certificate_status_req_list length must be positive (received length: " + int16 + ")");
            }
            int i = int16;
            final ArrayList list = new ArrayList();
            while (i > 0) {
                final byte b = (byte)Record.getInt8(byteBuffer);
                final int int17 = Record.getInt16(byteBuffer);
                if (byteBuffer.remaining() < int17) {
                    throw new SSLProtocolException("Invalid status_request_v2 extension: insufficient data (request_length=" + int17 + ", remining=" + byteBuffer.remaining() + ")");
                }
                final byte[] array = new byte[int17];
                if (array.length != 0) {
                    byteBuffer.get(array);
                }
                i -= 3;
                i -= int17;
                if (b == CertStatusRequestType.OCSP.id || b == CertStatusRequestType.OCSP_MULTI.id) {
                    if (array.length < 4) {
                        throw new SSLProtocolException("Invalid status_request_v2 extension: insufficient data");
                    }
                    list.add(new OCSPStatusRequest(b, array));
                }
                else {
                    if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                        SSLLogger.info("Unknown certificate status request (status type: " + b + ")", new Object[0]);
                    }
                    list.add(new CertStatusRequest(b, array));
                }
            }
            this.certStatusRequests = (CertStatusRequest[])list.toArray(new CertStatusRequest[0]);
        }
        
        @Override
        public String toString() {
            if (this.certStatusRequests == null || this.certStatusRequests.length == 0) {
                return "<empty>";
            }
            final MessageFormat messageFormat = new MessageFormat("\"cert status request\": '{'\n{0}\n'}'", Locale.ENGLISH);
            final StringBuilder sb = new StringBuilder(512);
            int n = 1;
            for (final CertStatusRequest certStatusRequest : this.certStatusRequests) {
                if (n != 0) {
                    n = 0;
                }
                else {
                    sb.append(", ");
                }
                sb.append(messageFormat.format(new Object[] { Utilities.indent(certStatusRequest.toString()) }));
            }
            return sb.toString();
        }
        
        static {
            DEFAULT = new CertStatusRequestV2Spec(new CertStatusRequest[] { OCSPStatusRequest.EMPTY_OCSP_MULTI });
        }
    }
    
    private static final class CertStatusRequestsStringizer implements SSLStringizer
    {
        @Override
        public String toString(final ByteBuffer byteBuffer) {
            try {
                return new CertStatusRequestV2Spec(byteBuffer).toString();
            }
            catch (final IOException ex) {
                return ex.getMessage();
            }
        }
    }
    
    private static final class CHCertStatusReqV2Producer implements HandshakeProducer
    {
        @Override
        public byte[] produce(final ConnectionContext connectionContext, final SSLHandshake.HandshakeMessage handshakeMessage) throws IOException {
            final ClientHandshakeContext clientHandshakeContext = (ClientHandshakeContext)connectionContext;
            if (!clientHandshakeContext.sslContext.isStaplingEnabled(true)) {
                return null;
            }
            if (!clientHandshakeContext.sslConfig.isAvailable(SSLExtension.CH_STATUS_REQUEST_V2)) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.finest("Ignore unavailable status_request_v2 extension", new Object[0]);
                }
                return null;
            }
            final byte[] array = { 0, 7, 2, 0, 4, 0, 0, 0, 0 };
            clientHandshakeContext.handshakeExtensions.put(SSLExtension.CH_STATUS_REQUEST_V2, CertStatusRequestV2Spec.DEFAULT);
            return array;
        }
    }
    
    private static final class CHCertStatusReqV2Consumer implements SSLExtension.ExtensionConsumer
    {
        @Override
        public void consume(final ConnectionContext connectionContext, final SSLHandshake.HandshakeMessage handshakeMessage, final ByteBuffer byteBuffer) throws IOException {
            final ServerHandshakeContext serverHandshakeContext = (ServerHandshakeContext)connectionContext;
            if (!serverHandshakeContext.sslConfig.isAvailable(SSLExtension.CH_STATUS_REQUEST_V2)) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.finest("Ignore unavailable status_request_v2 extension", new Object[0]);
                }
                return;
            }
            CertStatusRequestV2Spec certStatusRequestV2Spec;
            try {
                certStatusRequestV2Spec = new CertStatusRequestV2Spec(byteBuffer);
            }
            catch (final IOException ex) {
                throw serverHandshakeContext.conContext.fatal(Alert.UNEXPECTED_MESSAGE, ex);
            }
            serverHandshakeContext.handshakeExtensions.put(SSLExtension.CH_STATUS_REQUEST_V2, certStatusRequestV2Spec);
            if (!serverHandshakeContext.isResumption) {
                serverHandshakeContext.handshakeProducers.putIfAbsent(SSLHandshake.CERTIFICATE_STATUS.id, SSLHandshake.CERTIFICATE_STATUS);
            }
        }
    }
    
    private static final class SHCertStatusReqV2Producer implements HandshakeProducer
    {
        @Override
        public byte[] produce(final ConnectionContext connectionContext, final SSLHandshake.HandshakeMessage handshakeMessage) throws IOException {
            final ServerHandshakeContext serverHandshakeContext = (ServerHandshakeContext)connectionContext;
            if (serverHandshakeContext.stapleParams == null || serverHandshakeContext.stapleParams.statusRespExt != SSLExtension.CH_STATUS_REQUEST_V2) {
                return null;
            }
            if (serverHandshakeContext.handshakeExtensions.get(SSLExtension.CH_STATUS_REQUEST_V2) == null) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.finest("Ignore unavailable status_request_v2 extension", new Object[0]);
                }
                return null;
            }
            if (serverHandshakeContext.isResumption) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.finest("No status_request_v2 response for session resumption", new Object[0]);
                }
                return null;
            }
            final byte[] array = new byte[0];
            serverHandshakeContext.handshakeExtensions.put(SSLExtension.SH_STATUS_REQUEST_V2, CertStatusRequestV2Spec.DEFAULT);
            return array;
        }
    }
    
    private static final class SHCertStatusReqV2Consumer implements SSLExtension.ExtensionConsumer
    {
        @Override
        public void consume(final ConnectionContext connectionContext, final SSLHandshake.HandshakeMessage handshakeMessage, final ByteBuffer byteBuffer) throws IOException {
            final ClientHandshakeContext clientHandshakeContext = (ClientHandshakeContext)connectionContext;
            if (clientHandshakeContext.handshakeExtensions.get(SSLExtension.CH_STATUS_REQUEST_V2) == null) {
                throw clientHandshakeContext.conContext.fatal(Alert.UNEXPECTED_MESSAGE, "Unexpected status_request_v2 extension in ServerHello");
            }
            if (byteBuffer.hasRemaining()) {
                throw clientHandshakeContext.conContext.fatal(Alert.UNEXPECTED_MESSAGE, "Invalid status_request_v2 extension in ServerHello: the extension data must be empty");
            }
            clientHandshakeContext.handshakeExtensions.put(SSLExtension.SH_STATUS_REQUEST_V2, CertStatusRequestV2Spec.DEFAULT);
            clientHandshakeContext.staplingActive = clientHandshakeContext.sslContext.isStaplingEnabled(true);
            if (clientHandshakeContext.staplingActive) {
                clientHandshakeContext.handshakeConsumers.put(SSLHandshake.CERTIFICATE_STATUS.id, SSLHandshake.CERTIFICATE_STATUS);
            }
        }
    }
    
    private static final class CTCertStatusResponseProducer implements HandshakeProducer
    {
        @Override
        public byte[] produce(final ConnectionContext connectionContext, final SSLHandshake.HandshakeMessage handshakeMessage) throws IOException {
            final ServerHandshakeContext serverHandshakeContext = (ServerHandshakeContext)connectionContext;
            if (serverHandshakeContext.stapleParams == null) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.finest("Stapling is disabled for this connection", new Object[0]);
                }
                return null;
            }
            if (serverHandshakeContext.currentCertEntry == null) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.finest("Found null CertificateEntry in context", new Object[0]);
                }
                return null;
            }
            byte[] byteArray;
            try {
                final X509Certificate x509Certificate = (X509Certificate)CertificateFactory.getInstance("X.509").generateCertificate(new ByteArrayInputStream(serverHandshakeContext.currentCertEntry.encoded));
                final byte[] array = serverHandshakeContext.stapleParams.responseMap.get(x509Certificate);
                if (array == null) {
                    if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake,verbose")) {
                        SSLLogger.finest("No status response found for " + x509Certificate.getSubjectX500Principal(), new Object[0]);
                    }
                    serverHandshakeContext.currentCertEntry = null;
                    return null;
                }
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake,verbose")) {
                    SSLLogger.finest("Found status response for " + x509Certificate.getSubjectX500Principal() + ", response length: " + array.length, new Object[0]);
                }
                byteArray = ((serverHandshakeContext.stapleParams.statReqType == CertStatusRequestType.OCSP) ? new OCSPStatusResponse(serverHandshakeContext.stapleParams.statReqType.id, array) : new CertStatusResponse(serverHandshakeContext.stapleParams.statReqType.id, array)).toByteArray();
            }
            catch (final CertificateException ex) {
                throw serverHandshakeContext.conContext.fatal(Alert.BAD_CERTIFICATE, "Failed to parse server certificates", ex);
            }
            catch (final IOException ex2) {
                throw serverHandshakeContext.conContext.fatal(Alert.BAD_CERT_STATUS_RESPONSE, "Failed to parse certificate status response", ex2);
            }
            serverHandshakeContext.currentCertEntry = null;
            return byteArray;
        }
    }
    
    private static final class CTCertStatusResponseConsumer implements SSLExtension.ExtensionConsumer
    {
        @Override
        public void consume(final ConnectionContext connectionContext, final SSLHandshake.HandshakeMessage handshakeMessage, final ByteBuffer byteBuffer) throws IOException {
            final ClientHandshakeContext clientHandshakeContext = (ClientHandshakeContext)connectionContext;
            CertStatusResponseSpec certStatusResponseSpec;
            try {
                certStatusResponseSpec = new CertStatusResponseSpec(byteBuffer);
            }
            catch (final IOException ex) {
                throw clientHandshakeContext.conContext.fatal(Alert.DECODE_ERROR, ex);
            }
            if (clientHandshakeContext.sslContext.isStaplingEnabled(true)) {
                clientHandshakeContext.staplingActive = true;
                if (clientHandshakeContext.handshakeSession != null && !clientHandshakeContext.isResumption) {
                    final ArrayList statusResponses = new ArrayList(clientHandshakeContext.handshakeSession.getStatusResponses());
                    statusResponses.add((Object)certStatusResponseSpec.statusResponse.encodedResponse);
                    clientHandshakeContext.handshakeSession.setStatusResponses((List<byte[]>)statusResponses);
                }
                else if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake,verbose")) {
                    SSLLogger.finest("Ignoring stapled data on resumed session", new Object[0]);
                }
            }
        }
    }
}
