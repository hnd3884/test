package org.bouncycastle.crypto.tls;

import java.util.Hashtable;
import java.util.Enumeration;
import org.bouncycastle.util.Arrays;
import java.util.Vector;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.SecureRandom;
import java.io.OutputStream;
import java.io.InputStream;

public class TlsClientProtocol extends TlsProtocol
{
    protected TlsClient tlsClient;
    TlsClientContextImpl tlsClientContext;
    protected byte[] selectedSessionID;
    protected TlsKeyExchange keyExchange;
    protected TlsAuthentication authentication;
    protected CertificateStatus certificateStatus;
    protected CertificateRequest certificateRequest;
    
    public TlsClientProtocol(final InputStream inputStream, final OutputStream outputStream, final SecureRandom secureRandom) {
        super(inputStream, outputStream, secureRandom);
        this.tlsClient = null;
        this.tlsClientContext = null;
        this.selectedSessionID = null;
        this.keyExchange = null;
        this.authentication = null;
        this.certificateStatus = null;
        this.certificateRequest = null;
    }
    
    public TlsClientProtocol(final SecureRandom secureRandom) {
        super(secureRandom);
        this.tlsClient = null;
        this.tlsClientContext = null;
        this.selectedSessionID = null;
        this.keyExchange = null;
        this.authentication = null;
        this.certificateStatus = null;
        this.certificateRequest = null;
    }
    
    public void connect(final TlsClient tlsClient) throws IOException {
        if (tlsClient == null) {
            throw new IllegalArgumentException("'tlsClient' cannot be null");
        }
        if (this.tlsClient != null) {
            throw new IllegalStateException("'connect' can only be called once");
        }
        this.tlsClient = tlsClient;
        this.securityParameters = new SecurityParameters();
        this.securityParameters.entity = 1;
        this.tlsClientContext = new TlsClientContextImpl(this.secureRandom, this.securityParameters);
        this.securityParameters.clientRandom = TlsProtocol.createRandomBlock(tlsClient.shouldUseGMTUnixTime(), this.tlsClientContext.getNonceRandomGenerator());
        this.tlsClient.init(this.tlsClientContext);
        this.recordStream.init(this.tlsClientContext);
        final TlsSession sessionToResume = tlsClient.getSessionToResume();
        if (sessionToResume != null && sessionToResume.isResumable()) {
            final SessionParameters exportSessionParameters = sessionToResume.exportSessionParameters();
            if (exportSessionParameters != null) {
                this.tlsSession = sessionToResume;
                this.sessionParameters = exportSessionParameters;
            }
        }
        this.sendClientHelloMessage();
        this.connection_state = 1;
        this.blockForHandshake();
    }
    
    @Override
    protected void cleanupHandshake() {
        super.cleanupHandshake();
        this.selectedSessionID = null;
        this.keyExchange = null;
        this.authentication = null;
        this.certificateStatus = null;
        this.certificateRequest = null;
    }
    
    @Override
    protected TlsContext getContext() {
        return this.tlsClientContext;
    }
    
    @Override
    AbstractTlsContext getContextAdmin() {
        return this.tlsClientContext;
    }
    
    @Override
    protected TlsPeer getPeer() {
        return this.tlsClient;
    }
    
    @Override
    protected void handleHandshakeMessage(final short n, final ByteArrayInputStream byteArrayInputStream) throws IOException {
        if (!this.resumedSession) {
            Label_1374: {
                switch (n) {
                    case 11: {
                        switch (this.connection_state) {
                            case 2: {
                                this.handleSupplementalData(null);
                            }
                            case 3: {
                                this.peerCertificate = Certificate.parse(byteArrayInputStream);
                                TlsProtocol.assertEmpty(byteArrayInputStream);
                                if (this.peerCertificate == null || this.peerCertificate.isEmpty()) {
                                    this.allowCertificateStatus = false;
                                }
                                this.keyExchange.processServerCertificate(this.peerCertificate);
                                (this.authentication = this.tlsClient.getAuthentication()).notifyServerCertificate(this.peerCertificate);
                                this.connection_state = 4;
                                break Label_1374;
                            }
                            default: {
                                throw new TlsFatalAlert((short)10);
                            }
                        }
                        break;
                    }
                    case 22: {
                        switch (this.connection_state) {
                            case 4: {
                                if (!this.allowCertificateStatus) {
                                    throw new TlsFatalAlert((short)10);
                                }
                                this.certificateStatus = CertificateStatus.parse(byteArrayInputStream);
                                TlsProtocol.assertEmpty(byteArrayInputStream);
                                this.connection_state = 5;
                                break Label_1374;
                            }
                            default: {
                                throw new TlsFatalAlert((short)10);
                            }
                        }
                        break;
                    }
                    case 20: {
                        switch (this.connection_state) {
                            case 13: {
                                if (this.expectSessionTicket) {
                                    throw new TlsFatalAlert((short)10);
                                }
                            }
                            case 14: {
                                this.processFinishedMessage(byteArrayInputStream);
                                this.connection_state = 15;
                                this.completeHandshake();
                                break Label_1374;
                            }
                            default: {
                                throw new TlsFatalAlert((short)10);
                            }
                        }
                        break;
                    }
                    case 2: {
                        switch (this.connection_state) {
                            case 1: {
                                this.receiveServerHelloMessage(byteArrayInputStream);
                                this.connection_state = 2;
                                this.recordStream.notifyHelloComplete();
                                this.applyMaxFragmentLengthExtension();
                                if (this.resumedSession) {
                                    this.securityParameters.masterSecret = Arrays.clone(this.sessionParameters.getMasterSecret());
                                    this.recordStream.setPendingConnectionState(this.getPeer().getCompression(), this.getPeer().getCipher());
                                    break Label_1374;
                                }
                                this.invalidateSession();
                                if (this.selectedSessionID.length > 0) {
                                    this.tlsSession = new TlsSessionImpl(this.selectedSessionID, null);
                                    break Label_1374;
                                }
                                break Label_1374;
                            }
                            default: {
                                throw new TlsFatalAlert((short)10);
                            }
                        }
                        break;
                    }
                    case 23: {
                        switch (this.connection_state) {
                            case 2: {
                                this.handleSupplementalData(TlsProtocol.readSupplementalDataMessage(byteArrayInputStream));
                                break Label_1374;
                            }
                            default: {
                                throw new TlsFatalAlert((short)10);
                            }
                        }
                        break;
                    }
                    case 14: {
                        switch (this.connection_state) {
                            case 2: {
                                this.handleSupplementalData(null);
                            }
                            case 3: {
                                this.keyExchange.skipServerCredentials();
                                this.authentication = null;
                            }
                            case 4:
                            case 5: {
                                this.keyExchange.skipServerKeyExchange();
                            }
                            case 6:
                            case 7: {
                                TlsProtocol.assertEmpty(byteArrayInputStream);
                                this.connection_state = 8;
                                this.recordStream.getHandshakeHash().sealHashAlgorithms();
                                final Vector clientSupplementalData = this.tlsClient.getClientSupplementalData();
                                if (clientSupplementalData != null) {
                                    this.sendSupplementalDataMessage(clientSupplementalData);
                                }
                                this.connection_state = 9;
                                TlsCredentials clientCredentials = null;
                                if (this.certificateRequest == null) {
                                    this.keyExchange.skipClientCredentials();
                                }
                                else {
                                    clientCredentials = this.authentication.getClientCredentials(this.certificateRequest);
                                    if (clientCredentials == null) {
                                        this.keyExchange.skipClientCredentials();
                                        this.sendCertificateMessage(Certificate.EMPTY_CHAIN);
                                    }
                                    else {
                                        this.keyExchange.processClientCredentials(clientCredentials);
                                        this.sendCertificateMessage(clientCredentials.getCertificate());
                                    }
                                }
                                this.connection_state = 10;
                                this.sendClientKeyExchangeMessage();
                                this.connection_state = 11;
                                if (TlsUtils.isSSL(this.getContext())) {
                                    TlsProtocol.establishMasterSecret(this.getContext(), this.keyExchange);
                                }
                                final TlsHandshakeHash prepareToFinish = this.recordStream.prepareToFinish();
                                this.securityParameters.sessionHash = TlsProtocol.getCurrentPRFHash(this.getContext(), prepareToFinish, null);
                                if (!TlsUtils.isSSL(this.getContext())) {
                                    TlsProtocol.establishMasterSecret(this.getContext(), this.keyExchange);
                                }
                                this.recordStream.setPendingConnectionState(this.getPeer().getCompression(), this.getPeer().getCipher());
                                if (clientCredentials != null && clientCredentials instanceof TlsSignerCredentials) {
                                    final TlsSignerCredentials tlsSignerCredentials = (TlsSignerCredentials)clientCredentials;
                                    final SignatureAndHashAlgorithm signatureAndHashAlgorithm = TlsUtils.getSignatureAndHashAlgorithm(this.getContext(), tlsSignerCredentials);
                                    byte[] array;
                                    if (signatureAndHashAlgorithm == null) {
                                        array = this.securityParameters.getSessionHash();
                                    }
                                    else {
                                        array = prepareToFinish.getFinalHash(signatureAndHashAlgorithm.getHash());
                                    }
                                    this.sendCertificateVerifyMessage(new DigitallySigned(signatureAndHashAlgorithm, tlsSignerCredentials.generateCertificateSignature(array)));
                                    this.connection_state = 12;
                                }
                                this.sendChangeCipherSpecMessage();
                                this.sendFinishedMessage();
                                this.connection_state = 13;
                                break Label_1374;
                            }
                            default: {
                                throw new TlsFatalAlert((short)10);
                            }
                        }
                        break;
                    }
                    case 12: {
                        switch (this.connection_state) {
                            case 2: {
                                this.handleSupplementalData(null);
                            }
                            case 3: {
                                this.keyExchange.skipServerCredentials();
                                this.authentication = null;
                            }
                            case 4:
                            case 5: {
                                this.keyExchange.processServerKeyExchange(byteArrayInputStream);
                                TlsProtocol.assertEmpty(byteArrayInputStream);
                                this.connection_state = 6;
                                break Label_1374;
                            }
                            default: {
                                throw new TlsFatalAlert((short)10);
                            }
                        }
                        break;
                    }
                    case 13: {
                        switch (this.connection_state) {
                            case 4:
                            case 5: {
                                this.keyExchange.skipServerKeyExchange();
                            }
                            case 6: {
                                if (this.authentication == null) {
                                    throw new TlsFatalAlert((short)40);
                                }
                                this.certificateRequest = CertificateRequest.parse(this.getContext(), byteArrayInputStream);
                                TlsProtocol.assertEmpty(byteArrayInputStream);
                                this.keyExchange.validateCertificateRequest(this.certificateRequest);
                                TlsUtils.trackHashAlgorithms(this.recordStream.getHandshakeHash(), this.certificateRequest.getSupportedSignatureAlgorithms());
                                this.connection_state = 7;
                                break Label_1374;
                            }
                            default: {
                                throw new TlsFatalAlert((short)10);
                            }
                        }
                        break;
                    }
                    case 4: {
                        switch (this.connection_state) {
                            case 13: {
                                if (!this.expectSessionTicket) {
                                    throw new TlsFatalAlert((short)10);
                                }
                                this.invalidateSession();
                                this.receiveNewSessionTicketMessage(byteArrayInputStream);
                                this.connection_state = 14;
                                break Label_1374;
                            }
                            default: {
                                throw new TlsFatalAlert((short)10);
                            }
                        }
                        break;
                    }
                    case 0: {
                        TlsProtocol.assertEmpty(byteArrayInputStream);
                        if (this.connection_state == 16) {
                            this.refuseRenegotiation();
                            break;
                        }
                        break;
                    }
                    default: {
                        throw new TlsFatalAlert((short)10);
                    }
                }
            }
            return;
        }
        if (n != 20 || this.connection_state != 2) {
            throw new TlsFatalAlert((short)10);
        }
        this.processFinishedMessage(byteArrayInputStream);
        this.connection_state = 15;
        this.sendChangeCipherSpecMessage();
        this.sendFinishedMessage();
        this.connection_state = 13;
        this.completeHandshake();
    }
    
    protected void handleSupplementalData(final Vector vector) throws IOException {
        this.tlsClient.processServerSupplementalData(vector);
        this.connection_state = 3;
        (this.keyExchange = this.tlsClient.getKeyExchange()).init(this.getContext());
    }
    
    protected void receiveNewSessionTicketMessage(final ByteArrayInputStream byteArrayInputStream) throws IOException {
        final NewSessionTicket parse = NewSessionTicket.parse(byteArrayInputStream);
        TlsProtocol.assertEmpty(byteArrayInputStream);
        this.tlsClient.notifyNewSessionTicket(parse);
    }
    
    protected void receiveServerHelloMessage(final ByteArrayInputStream byteArrayInputStream) throws IOException {
        final ProtocolVersion version = TlsUtils.readVersion(byteArrayInputStream);
        if (version.isDTLS()) {
            throw new TlsFatalAlert((short)47);
        }
        if (!version.equals(this.recordStream.getReadVersion())) {
            throw new TlsFatalAlert((short)47);
        }
        if (!version.isEqualOrEarlierVersionOf(this.getContext().getClientVersion())) {
            throw new TlsFatalAlert((short)47);
        }
        this.recordStream.setWriteVersion(version);
        this.getContextAdmin().setServerVersion(version);
        this.tlsClient.notifyServerVersion(version);
        this.securityParameters.serverRandom = TlsUtils.readFully(32, byteArrayInputStream);
        this.selectedSessionID = TlsUtils.readOpaque8(byteArrayInputStream);
        if (this.selectedSessionID.length > 32) {
            throw new TlsFatalAlert((short)47);
        }
        this.tlsClient.notifySessionID(this.selectedSessionID);
        this.resumedSession = (this.selectedSessionID.length > 0 && this.tlsSession != null && Arrays.areEqual(this.selectedSessionID, this.tlsSession.getSessionID()));
        final int uint16 = TlsUtils.readUint16(byteArrayInputStream);
        if (!Arrays.contains(this.offeredCipherSuites, uint16) || uint16 == 0 || CipherSuite.isSCSV(uint16) || !TlsUtils.isValidCipherSuiteForVersion(uint16, this.getContext().getServerVersion())) {
            throw new TlsFatalAlert((short)47);
        }
        this.tlsClient.notifySelectedCipherSuite(uint16);
        final short uint17 = TlsUtils.readUint8(byteArrayInputStream);
        if (!Arrays.contains(this.offeredCompressionMethods, uint17)) {
            throw new TlsFatalAlert((short)47);
        }
        this.tlsClient.notifySelectedCompressionMethod(uint17);
        this.serverExtensions = TlsProtocol.readExtensions(byteArrayInputStream);
        if (this.serverExtensions != null) {
            final Enumeration keys = this.serverExtensions.keys();
            while (keys.hasMoreElements()) {
                final Integer n = (Integer)keys.nextElement();
                if (n.equals(TlsClientProtocol.EXT_RenegotiationInfo)) {
                    continue;
                }
                if (null == TlsUtils.getExtensionData(this.clientExtensions, n)) {
                    throw new TlsFatalAlert((short)110);
                }
                if (this.resumedSession) {}
            }
        }
        final byte[] extensionData = TlsUtils.getExtensionData(this.serverExtensions, TlsClientProtocol.EXT_RenegotiationInfo);
        if (extensionData != null) {
            this.secure_renegotiation = true;
            if (!Arrays.constantTimeAreEqual(extensionData, TlsProtocol.createRenegotiationInfo(TlsUtils.EMPTY_BYTES))) {
                throw new TlsFatalAlert((short)40);
            }
        }
        this.tlsClient.notifySecureRenegotiation(this.secure_renegotiation);
        Hashtable clientExtensions = this.clientExtensions;
        Hashtable hashtable = this.serverExtensions;
        if (this.resumedSession) {
            if (uint16 != this.sessionParameters.getCipherSuite() || uint17 != this.sessionParameters.getCompressionAlgorithm()) {
                throw new TlsFatalAlert((short)47);
            }
            clientExtensions = null;
            hashtable = this.sessionParameters.readServerExtensions();
        }
        this.securityParameters.cipherSuite = uint16;
        this.securityParameters.compressionAlgorithm = uint17;
        if (hashtable != null) {
            final boolean hasEncryptThenMACExtension = TlsExtensionsUtils.hasEncryptThenMACExtension(hashtable);
            if (hasEncryptThenMACExtension && !TlsUtils.isBlockCipherSuite(uint16)) {
                throw new TlsFatalAlert((short)47);
            }
            this.securityParameters.encryptThenMAC = hasEncryptThenMACExtension;
            this.securityParameters.extendedMasterSecret = TlsExtensionsUtils.hasExtendedMasterSecretExtension(hashtable);
            this.securityParameters.maxFragmentLength = this.processMaxFragmentLengthExtension(clientExtensions, hashtable, (short)47);
            this.securityParameters.truncatedHMac = TlsExtensionsUtils.hasTruncatedHMacExtension(hashtable);
            this.allowCertificateStatus = (!this.resumedSession && TlsUtils.hasExpectedEmptyExtensionData(hashtable, TlsExtensionsUtils.EXT_status_request, (short)47));
            this.expectSessionTicket = (!this.resumedSession && TlsUtils.hasExpectedEmptyExtensionData(hashtable, TlsProtocol.EXT_SessionTicket, (short)47));
        }
        if (clientExtensions != null) {
            this.tlsClient.processServerExtensions(hashtable);
        }
        this.securityParameters.prfAlgorithm = TlsProtocol.getPRFAlgorithm(this.getContext(), this.securityParameters.getCipherSuite());
        this.securityParameters.verifyDataLength = 12;
    }
    
    protected void sendCertificateVerifyMessage(final DigitallySigned digitallySigned) throws IOException {
        final HandshakeMessage handshakeMessage = new HandshakeMessage((short)15);
        digitallySigned.encode(handshakeMessage);
        handshakeMessage.writeToRecordStream();
    }
    
    protected void sendClientHelloMessage() throws IOException {
        this.recordStream.setWriteVersion(this.tlsClient.getClientHelloRecordLayerVersion());
        final ProtocolVersion clientVersion = this.tlsClient.getClientVersion();
        if (clientVersion.isDTLS()) {
            throw new TlsFatalAlert((short)80);
        }
        this.getContextAdmin().setClientVersion(clientVersion);
        byte[] array = TlsUtils.EMPTY_BYTES;
        if (this.tlsSession != null) {
            array = this.tlsSession.getSessionID();
            if (array == null || array.length > 32) {
                array = TlsUtils.EMPTY_BYTES;
            }
        }
        final boolean fallback = this.tlsClient.isFallback();
        this.offeredCipherSuites = this.tlsClient.getCipherSuites();
        this.offeredCompressionMethods = this.tlsClient.getCompressionMethods();
        if (array.length > 0 && this.sessionParameters != null && (!Arrays.contains(this.offeredCipherSuites, this.sessionParameters.getCipherSuite()) || !Arrays.contains(this.offeredCompressionMethods, this.sessionParameters.getCompressionAlgorithm()))) {
            array = TlsUtils.EMPTY_BYTES;
        }
        this.clientExtensions = this.tlsClient.getClientExtensions();
        final HandshakeMessage handshakeMessage = new HandshakeMessage((short)1);
        TlsUtils.writeVersion(clientVersion, handshakeMessage);
        handshakeMessage.write(this.securityParameters.getClientRandom());
        TlsUtils.writeOpaque8(array, handshakeMessage);
        final boolean b = null == TlsUtils.getExtensionData(this.clientExtensions, TlsClientProtocol.EXT_RenegotiationInfo);
        final boolean b2 = !Arrays.contains(this.offeredCipherSuites, 255);
        if (b && b2) {
            this.offeredCipherSuites = Arrays.append(this.offeredCipherSuites, 255);
        }
        if (fallback && !Arrays.contains(this.offeredCipherSuites, 22016)) {
            this.offeredCipherSuites = Arrays.append(this.offeredCipherSuites, 22016);
        }
        TlsUtils.writeUint16ArrayWithUint16Length(this.offeredCipherSuites, handshakeMessage);
        TlsUtils.writeUint8ArrayWithUint8Length(this.offeredCompressionMethods, handshakeMessage);
        if (this.clientExtensions != null) {
            TlsProtocol.writeExtensions(handshakeMessage, this.clientExtensions);
        }
        handshakeMessage.writeToRecordStream();
    }
    
    protected void sendClientKeyExchangeMessage() throws IOException {
        final HandshakeMessage handshakeMessage = new HandshakeMessage((short)16);
        this.keyExchange.generateClientKeyExchange(handshakeMessage);
        handshakeMessage.writeToRecordStream();
    }
}
