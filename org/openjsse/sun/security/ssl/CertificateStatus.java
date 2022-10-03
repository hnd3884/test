package org.openjsse.sun.security.ssl;

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
            final ServerHandshakeContext shc = (ServerHandshakeContext)handshakeContext;
            final StatusResponseManager.StaplingParameters stapleParams = shc.stapleParams;
            if (stapleParams == null) {
                throw new IllegalArgumentException("Unexpected null stapling parameters");
            }
            final X509Certificate[] certChain = (X509Certificate[])shc.handshakeSession.getLocalCertificates();
            if (certChain == null) {
                throw new IllegalArgumentException("Unexpected null certificate chain");
            }
            this.statusType = stapleParams.statReqType;
            if (this.statusType == CertStatusExtension.CertStatusRequestType.OCSP) {
                byte[] resp = stapleParams.responseMap.get(certChain[0]);
                if (resp == null) {
                    resp = new byte[0];
                }
                this.encodedResponses.add(resp);
                this.encodedResponsesLen += resp.length + 3;
            }
            else {
                if (this.statusType != CertStatusExtension.CertStatusRequestType.OCSP_MULTI) {
                    throw new IllegalArgumentException("Unsupported StatusResponseType: " + this.statusType);
                }
                for (final X509Certificate cert : certChain) {
                    byte[] resp2 = stapleParams.responseMap.get(cert);
                    if (resp2 == null) {
                        resp2 = new byte[0];
                    }
                    this.encodedResponses.add(resp2);
                    this.encodedResponsesLen += resp2.length + 3;
                }
            }
            this.messageLength = this.messageLength();
        }
        
        CertificateStatusMessage(final HandshakeContext handshakeContext, final ByteBuffer m) throws IOException {
            super(handshakeContext);
            this.encodedResponsesLen = 0;
            this.messageLength = -1;
            this.encodedResponses = new ArrayList<byte[]>();
            this.statusType = CertStatusExtension.CertStatusRequestType.valueOf((byte)Record.getInt8(m));
            if (this.statusType == CertStatusExtension.CertStatusRequestType.OCSP) {
                final byte[] respDER = Record.getBytes24(m);
                if (respDER.length <= 0) {
                    throw handshakeContext.conContext.fatal(Alert.HANDSHAKE_FAILURE, "Zero-length OCSP Response");
                }
                this.encodedResponses.add(respDER);
                this.encodedResponsesLen = 3 + respDER.length;
            }
            else {
                if (this.statusType != CertStatusExtension.CertStatusRequestType.OCSP_MULTI) {
                    throw handshakeContext.conContext.fatal(Alert.HANDSHAKE_FAILURE, "Unsupported StatusResponseType: " + this.statusType);
                }
                int respListLen = Record.getInt24(m);
                this.encodedResponsesLen = respListLen;
                while (respListLen > 0) {
                    final byte[] respDER2 = Record.getBytes24(m);
                    this.encodedResponses.add(respDER2);
                    respListLen -= respDER2.length + 3;
                }
                if (respListLen != 0) {
                    throw handshakeContext.conContext.fatal(Alert.INTERNAL_ERROR, "Bad OCSP response list length");
                }
            }
            this.messageLength = this.messageLength();
        }
        
        public SSLHandshake handshakeType() {
            return SSLHandshake.CERTIFICATE_STATUS;
        }
        
        public int messageLength() {
            int len = 1;
            if (this.messageLength == -1) {
                if (this.statusType == CertStatusExtension.CertStatusRequestType.OCSP) {
                    len += this.encodedResponsesLen;
                }
                else if (this.statusType == CertStatusExtension.CertStatusRequestType.OCSP_MULTI) {
                    len += 3 + this.encodedResponsesLen;
                }
                this.messageLength = len;
            }
            return this.messageLength;
        }
        
        public void send(final HandshakeOutStream s) throws IOException {
            s.putInt8(this.statusType.id);
            if (this.statusType == CertStatusExtension.CertStatusRequestType.OCSP) {
                s.putBytes24(this.encodedResponses.get(0));
            }
            else {
                if (this.statusType != CertStatusExtension.CertStatusRequestType.OCSP_MULTI) {
                    throw new SSLHandshakeException("Unsupported status_type: " + this.statusType.id);
                }
                s.putInt24(this.encodedResponsesLen);
                for (final byte[] respBytes : this.encodedResponses) {
                    if (respBytes != null) {
                        s.putBytes24(respBytes);
                    }
                    else {
                        s.putBytes24(null);
                    }
                }
            }
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            for (final byte[] respDER : this.encodedResponses) {
                if (respDER.length > 0) {
                    try {
                        final OCSPResponse oResp = new OCSPResponse(respDER);
                        sb.append(oResp.toString()).append("\n");
                    }
                    catch (final IOException ioe) {
                        sb.append("OCSP Response Exception: ").append(ioe).append("\n");
                    }
                }
                else {
                    sb.append("<Zero-length entry>\n");
                }
            }
            final MessageFormat messageFormat = new MessageFormat("\"CertificateStatus\": '{'\n  \"type\"                : \"{0}\",\n  \"responses \"          : [\n{1}\n  ]\n'}'", Locale.ENGLISH);
            final Object[] messageFields = { this.statusType.name, Utilities.indent(Utilities.indent(sb.toString())) };
            return messageFormat.format(messageFields);
        }
    }
    
    private static final class CertificateStatusConsumer implements SSLConsumer
    {
        @Override
        public void consume(final ConnectionContext context, final ByteBuffer message) throws IOException {
            final ClientHandshakeContext chc = (ClientHandshakeContext)context;
            final CertificateStatusMessage cst = new CertificateStatusMessage(chc, message);
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("Consuming server CertificateStatus handshake message", cst);
            }
            chc.handshakeSession.setStatusResponses(cst.encodedResponses);
            CertificateMessage.T12CertificateConsumer.checkServerCerts(chc, chc.deferredCerts);
            chc.handshakeConsumers.remove(SSLHandshake.CERTIFICATE_STATUS.id);
        }
    }
    
    private static final class CertificateStatusProducer implements HandshakeProducer
    {
        @Override
        public byte[] produce(final ConnectionContext context, final SSLHandshake.HandshakeMessage message) throws IOException {
            final ServerHandshakeContext shc = (ServerHandshakeContext)context;
            if (!shc.staplingActive) {
                return null;
            }
            final CertificateStatusMessage csm = new CertificateStatusMessage(shc);
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("Produced server CertificateStatus handshake message", csm);
            }
            csm.write(shc.handshakeOutput);
            shc.handshakeOutput.flush();
            return null;
        }
    }
    
    private static final class CertificateStatusAbsence implements HandshakeAbsence
    {
        @Override
        public void absent(final ConnectionContext context, final SSLHandshake.HandshakeMessage message) throws IOException {
            final ClientHandshakeContext chc = (ClientHandshakeContext)context;
            if (chc.staplingActive) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("Server did not send CertificateStatus, checking cert chain without status info.", new Object[0]);
                }
                CertificateMessage.T12CertificateConsumer.checkServerCerts(chc, chc.deferredCerts);
            }
        }
    }
}
