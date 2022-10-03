package org.openjsse.sun.security.ssl;

import java.util.Collection;
import java.security.cert.CertificateException;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.security.cert.X509Certificate;
import java.security.cert.CertificateFactory;
import sun.security.provider.certpath.OCSPResponse;
import java.util.Iterator;
import sun.security.util.DerValue;
import sun.security.util.DerInputStream;
import java.util.ArrayList;
import java.security.cert.Extension;
import sun.security.provider.certpath.ResponderId;
import java.util.List;
import org.openjsse.sun.security.util.HexDumpEncoder;
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
        
        private CertStatusRequestSpec(final ByteBuffer buffer) throws IOException {
            if (buffer.remaining() == 0) {
                this.statusRequest = null;
                return;
            }
            if (buffer.remaining() < 1) {
                throw new SSLProtocolException("Invalid status_request extension: insufficient data");
            }
            final byte statusType = (byte)Record.getInt8(buffer);
            final byte[] encoded = new byte[buffer.remaining()];
            if (encoded.length != 0) {
                buffer.get(encoded);
            }
            if (statusType == CertStatusRequestType.OCSP.id) {
                this.statusRequest = new OCSPStatusRequest(statusType, encoded);
            }
            else {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.info("Unknown certificate status request (status type: " + statusType + ")", new Object[0]);
                }
                this.statusRequest = new CertStatusRequest(statusType, encoded);
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
        
        private CertStatusResponseSpec(final CertStatusResponse resp) {
            this.statusResponse = resp;
        }
        
        private CertStatusResponseSpec(final ByteBuffer buffer) throws IOException {
            if (buffer.remaining() < 2) {
                throw new SSLProtocolException("Invalid status_request extension: insufficient data");
            }
            final byte type = (byte)Record.getInt8(buffer);
            final byte[] respData = Record.getBytes24(buffer);
            if (type == CertStatusRequestType.OCSP.id) {
                this.statusResponse = new OCSPStatusResponse(type, respData);
            }
            else {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.info("Unknown certificate status response (status type: " + type + ")", new Object[0]);
                }
                this.statusResponse = new CertStatusResponse(type, respData);
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
        public String toString(final ByteBuffer buffer) {
            try {
                return new CertStatusRequestSpec(buffer).toString();
            }
            catch (final IOException ioe) {
                return ioe.getMessage();
            }
        }
    }
    
    private static final class CertStatusRespStringizer implements SSLStringizer
    {
        @Override
        public String toString(final ByteBuffer buffer) {
            try {
                return new CertStatusResponseSpec(buffer).toString();
            }
            catch (final IOException ioe) {
                return ioe.getMessage();
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
        
        static CertStatusRequestType valueOf(final byte id) {
            for (final CertStatusRequestType srt : values()) {
                if (srt.id == id) {
                    return srt;
                }
            }
            return null;
        }
        
        static String nameOf(final byte id) {
            for (final CertStatusRequestType srt : values()) {
                if (srt.id == id) {
                    return srt.name;
                }
            }
            return "UNDEFINED-CERT-STATUS-TYPE(" + id + ")";
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
            final MessageFormat messageFormat = new MessageFormat("\"certificate status type\": {0}\n\"encoded certificate status\": '{'\n{1}\n'}'", Locale.ENGLISH);
            final HexDumpEncoder hexEncoder = new HexDumpEncoder();
            final String encoded = hexEncoder.encodeBuffer(this.encodedRequest);
            final Object[] messageFields = { CertStatusRequestType.nameOf(this.statusType), Utilities.indent(encoded) };
            return messageFormat.format(messageFields);
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
        
        private OCSPStatusRequest(final byte statusType, final byte[] encoded) throws IOException {
            super(statusType, encoded);
            if (encoded == null || encoded.length < 4) {
                throw new SSLProtocolException("Invalid OCSP status request: insufficient data");
            }
            final List<ResponderId> rids = new ArrayList<ResponderId>();
            final List<Extension> exts = new ArrayList<Extension>();
            final ByteBuffer m = ByteBuffer.wrap(encoded);
            this.ridListLen = Record.getInt16(m);
            if (m.remaining() < this.ridListLen + 2) {
                throw new SSLProtocolException("Invalid OCSP status request: insufficient data");
            }
            int ridListBytesRemaining;
            byte[] ridBytes;
            for (ridListBytesRemaining = this.ridListLen; ridListBytesRemaining >= 2; ridListBytesRemaining -= ridBytes.length + 2) {
                ridBytes = Record.getBytes16(m);
                try {
                    rids.add(new ResponderId(ridBytes));
                }
                catch (final IOException ioe) {
                    throw new SSLProtocolException("Invalid OCSP status request: invalid responder ID");
                }
            }
            if (ridListBytesRemaining != 0) {
                throw new SSLProtocolException("Invalid OCSP status request: incomplete data");
            }
            final byte[] extListBytes = Record.getBytes16(m);
            this.extListLen = extListBytes.length;
            if (this.extListLen > 0) {
                try {
                    final DerInputStream dis = new DerInputStream(extListBytes);
                    final DerValue[] sequence;
                    final DerValue[] extSeqContents = sequence = dis.getSequence(extListBytes.length);
                    for (final DerValue extDerVal : sequence) {
                        exts.add(new sun.security.x509.Extension(extDerVal));
                    }
                }
                catch (final IOException ioe) {
                    throw new SSLProtocolException("Invalid OCSP status request: invalid extension");
                }
            }
            this.responderIds = rids;
            this.extensions = exts;
        }
        
        @Override
        public String toString() {
            final MessageFormat messageFormat = new MessageFormat("\"certificate status type\": {0}\n\"OCSP status request\": '{'\n{1}\n'}'", Locale.ENGLISH);
            final MessageFormat requestFormat = new MessageFormat("\"responder_id\": {0}\n\"request extensions\": '{'\n{1}\n'}'", Locale.ENGLISH);
            String ridStr = "<empty>";
            if (!this.responderIds.isEmpty()) {
                ridStr = this.responderIds.toString();
            }
            String extsStr = "<empty>";
            if (!this.extensions.isEmpty()) {
                final StringBuilder extBuilder = new StringBuilder(512);
                boolean isFirst = true;
                for (final Extension ext : this.extensions) {
                    if (isFirst) {
                        isFirst = false;
                    }
                    else {
                        extBuilder.append(",\n");
                    }
                    extBuilder.append("{\n").append(Utilities.indent(ext.toString())).append("}");
                }
                extsStr = extBuilder.toString();
            }
            final Object[] requestFields = { ridStr, Utilities.indent(extsStr) };
            final String ocspStatusRequest = requestFormat.format(requestFields);
            final Object[] messageFields = { CertStatusRequestType.nameOf(this.statusType), Utilities.indent(ocspStatusRequest) };
            return messageFormat.format(messageFields);
        }
        
        static {
            OCSPStatusRequest ocspReq = null;
            OCSPStatusRequest multiReq = null;
            try {
                ocspReq = new OCSPStatusRequest(CertStatusRequestType.OCSP.id, new byte[] { 0, 0, 0, 0 });
                multiReq = new OCSPStatusRequest(CertStatusRequestType.OCSP_MULTI.id, new byte[] { 0, 0, 0, 0 });
            }
            catch (final IOException ex) {}
            EMPTY_OCSP = ocspReq;
            EMPTY_OCSP_MULTI = multiReq;
        }
    }
    
    static class CertStatusResponse
    {
        final byte statusType;
        final byte[] encodedResponse;
        
        protected CertStatusResponse(final byte statusType, final byte[] respDer) {
            this.statusType = statusType;
            this.encodedResponse = respDer;
        }
        
        byte[] toByteArray() throws IOException {
            final byte[] outData = new byte[this.encodedResponse.length + 4];
            final ByteBuffer buf = ByteBuffer.wrap(outData);
            Record.putInt8(buf, this.statusType);
            Record.putBytes24(buf, this.encodedResponse);
            return buf.array();
        }
        
        @Override
        public String toString() {
            final MessageFormat messageFormat = new MessageFormat("\"certificate status response type\": {0}\n\"encoded certificate status\": '{'\n{1}\n'}'", Locale.ENGLISH);
            final HexDumpEncoder hexEncoder = new HexDumpEncoder();
            final String encoded = hexEncoder.encodeBuffer(this.encodedResponse);
            final Object[] messageFields = { CertStatusRequestType.nameOf(this.statusType), Utilities.indent(encoded) };
            return messageFormat.format(messageFields);
        }
    }
    
    static final class OCSPStatusResponse extends CertStatusResponse
    {
        final OCSPResponse ocspResponse;
        
        private OCSPStatusResponse(final byte statusType, final byte[] encoded) throws IOException {
            super(statusType, encoded);
            if (encoded == null || encoded.length < 1) {
                throw new SSLProtocolException("Invalid OCSP status response: insufficient data");
            }
            this.ocspResponse = new OCSPResponse(encoded);
        }
        
        @Override
        public String toString() {
            final MessageFormat messageFormat = new MessageFormat("\"certificate status response type\": {0}\n\"OCSP status response\": '{'\n{1}\n'}'", Locale.ENGLISH);
            final Object[] messageFields = { CertStatusRequestType.nameOf(this.statusType), Utilities.indent(this.ocspResponse.toString()) };
            return messageFormat.format(messageFields);
        }
    }
    
    private static final class CHCertStatusReqProducer implements HandshakeProducer
    {
        @Override
        public byte[] produce(final ConnectionContext context, final SSLHandshake.HandshakeMessage message) throws IOException {
            final ClientHandshakeContext chc = (ClientHandshakeContext)context;
            if (!chc.sslContext.isStaplingEnabled(true)) {
                return null;
            }
            if (!chc.sslConfig.isAvailable(SSLExtension.CH_STATUS_REQUEST)) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("Ignore unavailable extension: " + SSLExtension.CH_STATUS_REQUEST.name, new Object[0]);
                }
                return null;
            }
            final byte[] extData = { 1, 0, 0, 0, 0 };
            chc.handshakeExtensions.put(SSLExtension.CH_STATUS_REQUEST, CertStatusRequestSpec.DEFAULT);
            return extData;
        }
    }
    
    private static final class CHCertStatusReqConsumer implements SSLExtension.ExtensionConsumer
    {
        @Override
        public void consume(final ConnectionContext context, final SSLHandshake.HandshakeMessage message, final ByteBuffer buffer) throws IOException {
            final ServerHandshakeContext shc = (ServerHandshakeContext)context;
            if (!shc.sslConfig.isAvailable(SSLExtension.CH_STATUS_REQUEST)) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("Ignore unavailable extension: " + SSLExtension.CH_STATUS_REQUEST.name, new Object[0]);
                }
                return;
            }
            CertStatusRequestSpec spec;
            try {
                spec = new CertStatusRequestSpec(buffer);
            }
            catch (final IOException ioe) {
                throw shc.conContext.fatal(Alert.UNEXPECTED_MESSAGE, ioe);
            }
            shc.handshakeExtensions.put(SSLExtension.CH_STATUS_REQUEST, spec);
            if (!shc.isResumption && !shc.negotiatedProtocol.useTLS13PlusSpec()) {
                shc.handshakeProducers.put(SSLHandshake.CERTIFICATE_STATUS.id, SSLHandshake.CERTIFICATE_STATUS);
            }
        }
    }
    
    private static final class SHCertStatusReqProducer implements HandshakeProducer
    {
        @Override
        public byte[] produce(final ConnectionContext context, final SSLHandshake.HandshakeMessage message) throws IOException {
            final ServerHandshakeContext shc = (ServerHandshakeContext)context;
            if (shc.stapleParams == null || shc.stapleParams.statusRespExt != SSLExtension.CH_STATUS_REQUEST) {
                return null;
            }
            final CertStatusRequestSpec spec = shc.handshakeExtensions.get(SSLExtension.CH_STATUS_REQUEST);
            if (spec == null) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.finest("Ignore unavailable extension: " + SSLExtension.CH_STATUS_REQUEST.name, new Object[0]);
                }
                return null;
            }
            if (shc.isResumption) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.finest("No status_request response for session resuming", new Object[0]);
                }
                return null;
            }
            final byte[] extData = new byte[0];
            shc.handshakeExtensions.put(SSLExtension.SH_STATUS_REQUEST, CertStatusRequestSpec.DEFAULT);
            return extData;
        }
    }
    
    private static final class SHCertStatusReqConsumer implements SSLExtension.ExtensionConsumer
    {
        @Override
        public void consume(final ConnectionContext context, final SSLHandshake.HandshakeMessage message, final ByteBuffer buffer) throws IOException {
            final ClientHandshakeContext chc = (ClientHandshakeContext)context;
            final CertStatusRequestSpec requestedCsr = chc.handshakeExtensions.get(SSLExtension.CH_STATUS_REQUEST);
            if (requestedCsr == null) {
                throw chc.conContext.fatal(Alert.UNEXPECTED_MESSAGE, "Unexpected status_request extension in ServerHello");
            }
            if (buffer.hasRemaining()) {
                throw chc.conContext.fatal(Alert.UNEXPECTED_MESSAGE, "Invalid status_request extension in ServerHello message: the extension data must be empty");
            }
            chc.handshakeExtensions.put(SSLExtension.SH_STATUS_REQUEST, CertStatusRequestSpec.DEFAULT);
            chc.staplingActive = chc.sslContext.isStaplingEnabled(true);
            if (chc.staplingActive) {
                chc.handshakeConsumers.put(SSLHandshake.CERTIFICATE_STATUS.id, SSLHandshake.CERTIFICATE_STATUS);
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
        
        private CertStatusRequestV2Spec(final ByteBuffer message) throws IOException {
            if (message.remaining() == 0) {
                this.certStatusRequests = new CertStatusRequest[0];
                return;
            }
            if (message.remaining() < 5) {
                throw new SSLProtocolException("Invalid status_request_v2 extension: insufficient data");
            }
            final int listLen = Record.getInt16(message);
            if (listLen <= 0) {
                throw new SSLProtocolException("certificate_status_req_list length must be positive (received length: " + listLen + ")");
            }
            int remaining = listLen;
            final List<CertStatusRequest> statusRequests = new ArrayList<CertStatusRequest>();
            while (remaining > 0) {
                final byte statusType = (byte)Record.getInt8(message);
                final int requestLen = Record.getInt16(message);
                if (message.remaining() < requestLen) {
                    throw new SSLProtocolException("Invalid status_request_v2 extension: insufficient data (request_length=" + requestLen + ", remining=" + message.remaining() + ")");
                }
                final byte[] encoded = new byte[requestLen];
                if (encoded.length != 0) {
                    message.get(encoded);
                }
                remaining -= 3;
                remaining -= requestLen;
                if (statusType == CertStatusRequestType.OCSP.id || statusType == CertStatusRequestType.OCSP_MULTI.id) {
                    if (encoded.length < 4) {
                        throw new SSLProtocolException("Invalid status_request_v2 extension: insufficient data");
                    }
                    statusRequests.add(new OCSPStatusRequest(statusType, encoded));
                }
                else {
                    if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                        SSLLogger.info("Unknown certificate status request (status type: " + statusType + ")", new Object[0]);
                    }
                    statusRequests.add(new CertStatusRequest(statusType, encoded));
                }
            }
            this.certStatusRequests = statusRequests.toArray(new CertStatusRequest[0]);
        }
        
        @Override
        public String toString() {
            if (this.certStatusRequests == null || this.certStatusRequests.length == 0) {
                return "<empty>";
            }
            final MessageFormat messageFormat = new MessageFormat("\"cert status request\": '{'\n{0}\n'}'", Locale.ENGLISH);
            final StringBuilder builder = new StringBuilder(512);
            boolean isFirst = true;
            for (final CertStatusRequest csr : this.certStatusRequests) {
                if (isFirst) {
                    isFirst = false;
                }
                else {
                    builder.append(", ");
                }
                final Object[] messageFields = { Utilities.indent(csr.toString()) };
                builder.append(messageFormat.format(messageFields));
            }
            return builder.toString();
        }
        
        static {
            DEFAULT = new CertStatusRequestV2Spec(new CertStatusRequest[] { OCSPStatusRequest.EMPTY_OCSP_MULTI });
        }
    }
    
    private static final class CertStatusRequestsStringizer implements SSLStringizer
    {
        @Override
        public String toString(final ByteBuffer buffer) {
            try {
                return new CertStatusRequestV2Spec(buffer).toString();
            }
            catch (final IOException ioe) {
                return ioe.getMessage();
            }
        }
    }
    
    private static final class CHCertStatusReqV2Producer implements HandshakeProducer
    {
        @Override
        public byte[] produce(final ConnectionContext context, final SSLHandshake.HandshakeMessage message) throws IOException {
            final ClientHandshakeContext chc = (ClientHandshakeContext)context;
            if (!chc.sslContext.isStaplingEnabled(true)) {
                return null;
            }
            if (!chc.sslConfig.isAvailable(SSLExtension.CH_STATUS_REQUEST_V2)) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.finest("Ignore unavailable status_request_v2 extension", new Object[0]);
                }
                return null;
            }
            final byte[] extData = { 0, 7, 2, 0, 4, 0, 0, 0, 0 };
            chc.handshakeExtensions.put(SSLExtension.CH_STATUS_REQUEST_V2, CertStatusRequestV2Spec.DEFAULT);
            return extData;
        }
    }
    
    private static final class CHCertStatusReqV2Consumer implements SSLExtension.ExtensionConsumer
    {
        @Override
        public void consume(final ConnectionContext context, final SSLHandshake.HandshakeMessage message, final ByteBuffer buffer) throws IOException {
            final ServerHandshakeContext shc = (ServerHandshakeContext)context;
            if (!shc.sslConfig.isAvailable(SSLExtension.CH_STATUS_REQUEST_V2)) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.finest("Ignore unavailable status_request_v2 extension", new Object[0]);
                }
                return;
            }
            CertStatusRequestV2Spec spec;
            try {
                spec = new CertStatusRequestV2Spec(buffer);
            }
            catch (final IOException ioe) {
                throw shc.conContext.fatal(Alert.UNEXPECTED_MESSAGE, ioe);
            }
            shc.handshakeExtensions.put(SSLExtension.CH_STATUS_REQUEST_V2, spec);
            if (!shc.isResumption) {
                shc.handshakeProducers.putIfAbsent(SSLHandshake.CERTIFICATE_STATUS.id, SSLHandshake.CERTIFICATE_STATUS);
            }
        }
    }
    
    private static final class SHCertStatusReqV2Producer implements HandshakeProducer
    {
        @Override
        public byte[] produce(final ConnectionContext context, final SSLHandshake.HandshakeMessage message) throws IOException {
            final ServerHandshakeContext shc = (ServerHandshakeContext)context;
            if (shc.stapleParams == null || shc.stapleParams.statusRespExt != SSLExtension.CH_STATUS_REQUEST_V2) {
                return null;
            }
            final CertStatusRequestV2Spec spec = shc.handshakeExtensions.get(SSLExtension.CH_STATUS_REQUEST_V2);
            if (spec == null) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.finest("Ignore unavailable status_request_v2 extension", new Object[0]);
                }
                return null;
            }
            if (shc.isResumption) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.finest("No status_request_v2 response for session resumption", new Object[0]);
                }
                return null;
            }
            final byte[] extData = new byte[0];
            shc.handshakeExtensions.put(SSLExtension.SH_STATUS_REQUEST_V2, CertStatusRequestV2Spec.DEFAULT);
            return extData;
        }
    }
    
    private static final class SHCertStatusReqV2Consumer implements SSLExtension.ExtensionConsumer
    {
        @Override
        public void consume(final ConnectionContext context, final SSLHandshake.HandshakeMessage message, final ByteBuffer buffer) throws IOException {
            final ClientHandshakeContext chc = (ClientHandshakeContext)context;
            final CertStatusRequestV2Spec requestedCsr = chc.handshakeExtensions.get(SSLExtension.CH_STATUS_REQUEST_V2);
            if (requestedCsr == null) {
                throw chc.conContext.fatal(Alert.UNEXPECTED_MESSAGE, "Unexpected status_request_v2 extension in ServerHello");
            }
            if (buffer.hasRemaining()) {
                throw chc.conContext.fatal(Alert.UNEXPECTED_MESSAGE, "Invalid status_request_v2 extension in ServerHello: the extension data must be empty");
            }
            chc.handshakeExtensions.put(SSLExtension.SH_STATUS_REQUEST_V2, CertStatusRequestV2Spec.DEFAULT);
            chc.staplingActive = chc.sslContext.isStaplingEnabled(true);
            if (chc.staplingActive) {
                chc.handshakeConsumers.put(SSLHandshake.CERTIFICATE_STATUS.id, SSLHandshake.CERTIFICATE_STATUS);
            }
        }
    }
    
    private static final class CTCertStatusResponseProducer implements HandshakeProducer
    {
        @Override
        public byte[] produce(final ConnectionContext context, final SSLHandshake.HandshakeMessage message) throws IOException {
            final ServerHandshakeContext shc = (ServerHandshakeContext)context;
            byte[] producedData = null;
            if (shc.stapleParams == null) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.finest("Stapling is disabled for this connection", new Object[0]);
                }
                return null;
            }
            if (shc.currentCertEntry == null) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.finest("Found null CertificateEntry in context", new Object[0]);
                }
                return null;
            }
            try {
                final CertificateFactory cf = CertificateFactory.getInstance("X.509");
                final X509Certificate x509Cert = (X509Certificate)cf.generateCertificate(new ByteArrayInputStream(shc.currentCertEntry.encoded));
                final byte[] respBytes = shc.stapleParams.responseMap.get(x509Cert);
                if (respBytes == null) {
                    if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake,verbose")) {
                        SSLLogger.finest("No status response found for " + x509Cert.getSubjectX500Principal(), new Object[0]);
                    }
                    shc.currentCertEntry = null;
                    return null;
                }
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake,verbose")) {
                    SSLLogger.finest("Found status response for " + x509Cert.getSubjectX500Principal() + ", response length: " + respBytes.length, new Object[0]);
                }
                final CertStatusResponse certResp = (shc.stapleParams.statReqType == CertStatusRequestType.OCSP) ? new OCSPStatusResponse(shc.stapleParams.statReqType.id, respBytes) : new CertStatusResponse(shc.stapleParams.statReqType.id, respBytes);
                producedData = certResp.toByteArray();
            }
            catch (final CertificateException ce) {
                throw shc.conContext.fatal(Alert.BAD_CERTIFICATE, "Failed to parse server certificates", ce);
            }
            catch (final IOException ioe) {
                throw shc.conContext.fatal(Alert.BAD_CERT_STATUS_RESPONSE, "Failed to parse certificate status response", ioe);
            }
            shc.currentCertEntry = null;
            return producedData;
        }
    }
    
    private static final class CTCertStatusResponseConsumer implements SSLExtension.ExtensionConsumer
    {
        @Override
        public void consume(final ConnectionContext context, final SSLHandshake.HandshakeMessage message, final ByteBuffer buffer) throws IOException {
            final ClientHandshakeContext chc = (ClientHandshakeContext)context;
            CertStatusResponseSpec spec;
            try {
                spec = new CertStatusResponseSpec(buffer);
            }
            catch (final IOException ioe) {
                throw chc.conContext.fatal(Alert.DECODE_ERROR, ioe);
            }
            if (chc.sslContext.isStaplingEnabled(true)) {
                chc.staplingActive = true;
                if (chc.handshakeSession != null && !chc.isResumption) {
                    final List<byte[]> respList = new ArrayList<byte[]>(chc.handshakeSession.getStatusResponses());
                    respList.add(spec.statusResponse.encodedResponse);
                    chc.handshakeSession.setStatusResponses(respList);
                }
                else if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake,verbose")) {
                    SSLLogger.finest("Ignoring stapled data on resumed session", new Object[0]);
                }
            }
        }
    }
}
