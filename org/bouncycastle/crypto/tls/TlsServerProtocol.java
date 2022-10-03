package org.bouncycastle.crypto.tls;

import org.bouncycastle.util.Arrays;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.util.PublicKeyFactory;
import java.util.Vector;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.SecureRandom;
import java.io.OutputStream;
import java.io.InputStream;

public class TlsServerProtocol extends TlsProtocol
{
    protected TlsServer tlsServer;
    TlsServerContextImpl tlsServerContext;
    protected TlsKeyExchange keyExchange;
    protected TlsCredentials serverCredentials;
    protected CertificateRequest certificateRequest;
    protected short clientCertificateType;
    protected TlsHandshakeHash prepareFinishHash;
    
    public TlsServerProtocol(final InputStream inputStream, final OutputStream outputStream, final SecureRandom secureRandom) {
        super(inputStream, outputStream, secureRandom);
        this.tlsServer = null;
        this.tlsServerContext = null;
        this.keyExchange = null;
        this.serverCredentials = null;
        this.certificateRequest = null;
        this.clientCertificateType = -1;
        this.prepareFinishHash = null;
    }
    
    public TlsServerProtocol(final SecureRandom secureRandom) {
        super(secureRandom);
        this.tlsServer = null;
        this.tlsServerContext = null;
        this.keyExchange = null;
        this.serverCredentials = null;
        this.certificateRequest = null;
        this.clientCertificateType = -1;
        this.prepareFinishHash = null;
    }
    
    public void accept(final TlsServer tlsServer) throws IOException {
        if (tlsServer == null) {
            throw new IllegalArgumentException("'tlsServer' cannot be null");
        }
        if (this.tlsServer != null) {
            throw new IllegalStateException("'accept' can only be called once");
        }
        this.tlsServer = tlsServer;
        this.securityParameters = new SecurityParameters();
        this.securityParameters.entity = 0;
        this.tlsServerContext = new TlsServerContextImpl(this.secureRandom, this.securityParameters);
        this.securityParameters.serverRandom = TlsProtocol.createRandomBlock(tlsServer.shouldUseGMTUnixTime(), this.tlsServerContext.getNonceRandomGenerator());
        this.tlsServer.init(this.tlsServerContext);
        this.recordStream.init(this.tlsServerContext);
        this.recordStream.setRestrictReadVersion(false);
        this.blockForHandshake();
    }
    
    @Override
    protected void cleanupHandshake() {
        super.cleanupHandshake();
        this.keyExchange = null;
        this.serverCredentials = null;
        this.certificateRequest = null;
        this.prepareFinishHash = null;
    }
    
    @Override
    protected TlsContext getContext() {
        return this.tlsServerContext;
    }
    
    @Override
    AbstractTlsContext getContextAdmin() {
        return this.tlsServerContext;
    }
    
    @Override
    protected TlsPeer getPeer() {
        return this.tlsServer;
    }
    
    @Override
    protected void handleHandshakeMessage(final short n, final ByteArrayInputStream byteArrayInputStream) throws IOException {
        Label_0979: {
            switch (n) {
                case 1: {
                    switch (this.connection_state) {
                        case 0: {
                            this.receiveClientHelloMessage(byteArrayInputStream);
                            this.connection_state = 1;
                            this.sendServerHelloMessage();
                            this.connection_state = 2;
                            this.recordStream.notifyHelloComplete();
                            final Vector serverSupplementalData = this.tlsServer.getServerSupplementalData();
                            if (serverSupplementalData != null) {
                                this.sendSupplementalDataMessage(serverSupplementalData);
                            }
                            this.connection_state = 3;
                            (this.keyExchange = this.tlsServer.getKeyExchange()).init(this.getContext());
                            this.serverCredentials = this.tlsServer.getCredentials();
                            Certificate certificate = null;
                            if (this.serverCredentials == null) {
                                this.keyExchange.skipServerCredentials();
                            }
                            else {
                                this.keyExchange.processServerCredentials(this.serverCredentials);
                                certificate = this.serverCredentials.getCertificate();
                                this.sendCertificateMessage(certificate);
                            }
                            this.connection_state = 4;
                            if (certificate == null || certificate.isEmpty()) {
                                this.allowCertificateStatus = false;
                            }
                            if (this.allowCertificateStatus) {
                                final CertificateStatus certificateStatus = this.tlsServer.getCertificateStatus();
                                if (certificateStatus != null) {
                                    this.sendCertificateStatusMessage(certificateStatus);
                                }
                            }
                            this.connection_state = 5;
                            final byte[] generateServerKeyExchange = this.keyExchange.generateServerKeyExchange();
                            if (generateServerKeyExchange != null) {
                                this.sendServerKeyExchangeMessage(generateServerKeyExchange);
                            }
                            this.connection_state = 6;
                            if (this.serverCredentials != null) {
                                this.certificateRequest = this.tlsServer.getCertificateRequest();
                                if (this.certificateRequest != null) {
                                    if (TlsUtils.isTLSv12(this.getContext()) != (this.certificateRequest.getSupportedSignatureAlgorithms() != null)) {
                                        throw new TlsFatalAlert((short)80);
                                    }
                                    this.keyExchange.validateCertificateRequest(this.certificateRequest);
                                    this.sendCertificateRequestMessage(this.certificateRequest);
                                    TlsUtils.trackHashAlgorithms(this.recordStream.getHandshakeHash(), this.certificateRequest.getSupportedSignatureAlgorithms());
                                }
                            }
                            this.connection_state = 7;
                            this.sendServerHelloDoneMessage();
                            this.connection_state = 8;
                            this.recordStream.getHandshakeHash().sealHashAlgorithms();
                            break Label_0979;
                        }
                        case 16: {
                            this.refuseRenegotiation();
                            break Label_0979;
                        }
                        default: {
                            throw new TlsFatalAlert((short)10);
                        }
                    }
                    break;
                }
                case 23: {
                    switch (this.connection_state) {
                        case 8: {
                            this.tlsServer.processClientSupplementalData(TlsProtocol.readSupplementalDataMessage(byteArrayInputStream));
                            this.connection_state = 9;
                            break Label_0979;
                        }
                        default: {
                            throw new TlsFatalAlert((short)10);
                        }
                    }
                    break;
                }
                case 11: {
                    switch (this.connection_state) {
                        case 8: {
                            this.tlsServer.processClientSupplementalData(null);
                        }
                        case 9: {
                            if (this.certificateRequest == null) {
                                throw new TlsFatalAlert((short)10);
                            }
                            this.receiveCertificateMessage(byteArrayInputStream);
                            this.connection_state = 10;
                            break Label_0979;
                        }
                        default: {
                            throw new TlsFatalAlert((short)10);
                        }
                    }
                    break;
                }
                case 16: {
                    switch (this.connection_state) {
                        case 8: {
                            this.tlsServer.processClientSupplementalData(null);
                        }
                        case 9:
                            Label_0767: {
                                if (this.certificateRequest == null) {
                                    this.keyExchange.skipClientCredentials();
                                    break Label_0767;
                                }
                                if (TlsUtils.isTLSv12(this.getContext())) {
                                    throw new TlsFatalAlert((short)10);
                                }
                                if (!TlsUtils.isSSL(this.getContext())) {
                                    this.notifyClientCertificate(Certificate.EMPTY_CHAIN);
                                    break Label_0767;
                                }
                                if (this.peerCertificate == null) {
                                    throw new TlsFatalAlert((short)10);
                                }
                                break Label_0767;
                            }
                        case 10: {
                            this.receiveClientKeyExchangeMessage(byteArrayInputStream);
                            this.connection_state = 11;
                            break Label_0979;
                        }
                        default: {
                            throw new TlsFatalAlert((short)10);
                        }
                    }
                    break;
                }
                case 15: {
                    switch (this.connection_state) {
                        case 11: {
                            if (!this.expectCertificateVerifyMessage()) {
                                throw new TlsFatalAlert((short)10);
                            }
                            this.receiveCertificateVerifyMessage(byteArrayInputStream);
                            this.connection_state = 12;
                            break Label_0979;
                        }
                        default: {
                            throw new TlsFatalAlert((short)10);
                        }
                    }
                    break;
                }
                case 20: {
                    switch (this.connection_state) {
                        case 11: {
                            if (this.expectCertificateVerifyMessage()) {
                                throw new TlsFatalAlert((short)10);
                            }
                        }
                        case 12: {
                            this.processFinishedMessage(byteArrayInputStream);
                            this.connection_state = 13;
                            if (this.expectSessionTicket) {
                                this.sendNewSessionTicketMessage(this.tlsServer.getNewSessionTicket());
                            }
                            this.connection_state = 14;
                            this.sendChangeCipherSpecMessage();
                            this.sendFinishedMessage();
                            this.connection_state = 15;
                            this.completeHandshake();
                            break Label_0979;
                        }
                        default: {
                            throw new TlsFatalAlert((short)10);
                        }
                    }
                    break;
                }
                default: {
                    throw new TlsFatalAlert((short)10);
                }
            }
        }
    }
    
    @Override
    protected void handleAlertWarningMessage(final short n) throws IOException {
        super.handleAlertWarningMessage(n);
        switch (n) {
            case 41: {
                if (TlsUtils.isSSL(this.getContext()) && this.certificateRequest != null) {
                    switch (this.connection_state) {
                        case 8: {
                            this.tlsServer.processClientSupplementalData(null);
                        }
                        case 9: {
                            this.notifyClientCertificate(Certificate.EMPTY_CHAIN);
                            this.connection_state = 10;
                            return;
                        }
                    }
                }
                throw new TlsFatalAlert((short)10);
            }
            default: {}
        }
    }
    
    protected void notifyClientCertificate(final Certificate peerCertificate) throws IOException {
        if (this.certificateRequest == null) {
            throw new IllegalStateException();
        }
        if (this.peerCertificate != null) {
            throw new TlsFatalAlert((short)10);
        }
        this.peerCertificate = peerCertificate;
        if (peerCertificate.isEmpty()) {
            this.keyExchange.skipClientCredentials();
        }
        else {
            this.clientCertificateType = TlsUtils.getClientCertificateType(peerCertificate, this.serverCredentials.getCertificate());
            this.keyExchange.processClientCertificate(peerCertificate);
        }
        this.tlsServer.notifyClientCertificate(peerCertificate);
    }
    
    protected void receiveCertificateMessage(final ByteArrayInputStream byteArrayInputStream) throws IOException {
        final Certificate parse = Certificate.parse(byteArrayInputStream);
        TlsProtocol.assertEmpty(byteArrayInputStream);
        this.notifyClientCertificate(parse);
    }
    
    protected void receiveCertificateVerifyMessage(final ByteArrayInputStream byteArrayInputStream) throws IOException {
        if (this.certificateRequest == null) {
            throw new IllegalStateException();
        }
        final DigitallySigned parse = DigitallySigned.parse(this.getContext(), byteArrayInputStream);
        TlsProtocol.assertEmpty(byteArrayInputStream);
        try {
            final SignatureAndHashAlgorithm algorithm = parse.getAlgorithm();
            byte[] array;
            if (TlsUtils.isTLSv12(this.getContext())) {
                TlsUtils.verifySupportedSignatureAlgorithm(this.certificateRequest.getSupportedSignatureAlgorithms(), algorithm);
                array = this.prepareFinishHash.getFinalHash(algorithm.getHash());
            }
            else {
                array = this.securityParameters.getSessionHash();
            }
            final AsymmetricKeyParameter key = PublicKeyFactory.createKey(this.peerCertificate.getCertificateAt(0).getSubjectPublicKeyInfo());
            final TlsSigner tlsSigner = TlsUtils.createTlsSigner(this.clientCertificateType);
            tlsSigner.init(this.getContext());
            if (!tlsSigner.verifyRawSignature(algorithm, parse.getSignature(), key, array)) {
                throw new TlsFatalAlert((short)51);
            }
        }
        catch (final TlsFatalAlert tlsFatalAlert) {
            throw tlsFatalAlert;
        }
        catch (final Exception ex) {
            throw new TlsFatalAlert((short)51, ex);
        }
    }
    
    protected void receiveClientHelloMessage(final ByteArrayInputStream byteArrayInputStream) throws IOException {
        final ProtocolVersion version = TlsUtils.readVersion(byteArrayInputStream);
        this.recordStream.setWriteVersion(version);
        if (version.isDTLS()) {
            throw new TlsFatalAlert((short)47);
        }
        final byte[] fully = TlsUtils.readFully(32, byteArrayInputStream);
        if (TlsUtils.readOpaque8(byteArrayInputStream).length > 32) {
            throw new TlsFatalAlert((short)47);
        }
        final int uint16 = TlsUtils.readUint16(byteArrayInputStream);
        if (uint16 < 2 || (uint16 & 0x1) != 0x0) {
            throw new TlsFatalAlert((short)50);
        }
        this.offeredCipherSuites = TlsUtils.readUint16Array(uint16 / 2, byteArrayInputStream);
        final short uint17 = TlsUtils.readUint8(byteArrayInputStream);
        if (uint17 < 1) {
            throw new TlsFatalAlert((short)47);
        }
        this.offeredCompressionMethods = TlsUtils.readUint8Array(uint17, byteArrayInputStream);
        this.clientExtensions = TlsProtocol.readExtensions(byteArrayInputStream);
        this.securityParameters.extendedMasterSecret = TlsExtensionsUtils.hasExtendedMasterSecretExtension(this.clientExtensions);
        this.getContextAdmin().setClientVersion(version);
        this.tlsServer.notifyClientVersion(version);
        this.tlsServer.notifyFallback(Arrays.contains(this.offeredCipherSuites, 22016));
        this.securityParameters.clientRandom = fully;
        this.tlsServer.notifyOfferedCipherSuites(this.offeredCipherSuites);
        this.tlsServer.notifyOfferedCompressionMethods(this.offeredCompressionMethods);
        if (Arrays.contains(this.offeredCipherSuites, 255)) {
            this.secure_renegotiation = true;
        }
        final byte[] extensionData = TlsUtils.getExtensionData(this.clientExtensions, TlsServerProtocol.EXT_RenegotiationInfo);
        if (extensionData != null) {
            this.secure_renegotiation = true;
            if (!Arrays.constantTimeAreEqual(extensionData, TlsProtocol.createRenegotiationInfo(TlsUtils.EMPTY_BYTES))) {
                throw new TlsFatalAlert((short)40);
            }
        }
        this.tlsServer.notifySecureRenegotiation(this.secure_renegotiation);
        if (this.clientExtensions != null) {
            TlsExtensionsUtils.getPaddingExtension(this.clientExtensions);
            this.tlsServer.processClientExtensions(this.clientExtensions);
        }
    }
    
    protected void receiveClientKeyExchangeMessage(final ByteArrayInputStream byteArrayInputStream) throws IOException {
        this.keyExchange.processClientKeyExchange(byteArrayInputStream);
        TlsProtocol.assertEmpty(byteArrayInputStream);
        if (TlsUtils.isSSL(this.getContext())) {
            TlsProtocol.establishMasterSecret(this.getContext(), this.keyExchange);
        }
        this.prepareFinishHash = this.recordStream.prepareToFinish();
        this.securityParameters.sessionHash = TlsProtocol.getCurrentPRFHash(this.getContext(), this.prepareFinishHash, null);
        if (!TlsUtils.isSSL(this.getContext())) {
            TlsProtocol.establishMasterSecret(this.getContext(), this.keyExchange);
        }
        this.recordStream.setPendingConnectionState(this.getPeer().getCompression(), this.getPeer().getCipher());
    }
    
    protected void sendCertificateRequestMessage(final CertificateRequest certificateRequest) throws IOException {
        final HandshakeMessage handshakeMessage = new HandshakeMessage((short)13);
        certificateRequest.encode(handshakeMessage);
        handshakeMessage.writeToRecordStream();
    }
    
    protected void sendCertificateStatusMessage(final CertificateStatus certificateStatus) throws IOException {
        final HandshakeMessage handshakeMessage = new HandshakeMessage((short)22);
        certificateStatus.encode(handshakeMessage);
        handshakeMessage.writeToRecordStream();
    }
    
    protected void sendNewSessionTicketMessage(final NewSessionTicket newSessionTicket) throws IOException {
        if (newSessionTicket == null) {
            throw new TlsFatalAlert((short)80);
        }
        final HandshakeMessage handshakeMessage = new HandshakeMessage((short)4);
        newSessionTicket.encode(handshakeMessage);
        handshakeMessage.writeToRecordStream();
    }
    
    protected void sendServerHelloMessage() throws IOException {
        final HandshakeMessage handshakeMessage = new HandshakeMessage((short)2);
        final ProtocolVersion serverVersion = this.tlsServer.getServerVersion();
        if (!serverVersion.isEqualOrEarlierVersionOf(this.getContext().getClientVersion())) {
            throw new TlsFatalAlert((short)80);
        }
        this.recordStream.setReadVersion(serverVersion);
        this.recordStream.setWriteVersion(serverVersion);
        this.recordStream.setRestrictReadVersion(true);
        this.getContextAdmin().setServerVersion(serverVersion);
        TlsUtils.writeVersion(serverVersion, handshakeMessage);
        handshakeMessage.write(this.securityParameters.serverRandom);
        TlsUtils.writeOpaque8(TlsUtils.EMPTY_BYTES, handshakeMessage);
        final int selectedCipherSuite = this.tlsServer.getSelectedCipherSuite();
        if (!Arrays.contains(this.offeredCipherSuites, selectedCipherSuite) || selectedCipherSuite == 0 || CipherSuite.isSCSV(selectedCipherSuite) || !TlsUtils.isValidCipherSuiteForVersion(selectedCipherSuite, this.getContext().getServerVersion())) {
            throw new TlsFatalAlert((short)80);
        }
        this.securityParameters.cipherSuite = selectedCipherSuite;
        final short selectedCompressionMethod = this.tlsServer.getSelectedCompressionMethod();
        if (!Arrays.contains(this.offeredCompressionMethods, selectedCompressionMethod)) {
            throw new TlsFatalAlert((short)80);
        }
        this.securityParameters.compressionAlgorithm = selectedCompressionMethod;
        TlsUtils.writeUint16(selectedCipherSuite, handshakeMessage);
        TlsUtils.writeUint8(selectedCompressionMethod, handshakeMessage);
        this.serverExtensions = this.tlsServer.getServerExtensions();
        if (this.secure_renegotiation && null == TlsUtils.getExtensionData(this.serverExtensions, TlsServerProtocol.EXT_RenegotiationInfo)) {
            (this.serverExtensions = TlsExtensionsUtils.ensureExtensionsInitialised(this.serverExtensions)).put(TlsServerProtocol.EXT_RenegotiationInfo, TlsProtocol.createRenegotiationInfo(TlsUtils.EMPTY_BYTES));
        }
        if (this.securityParameters.extendedMasterSecret) {
            TlsExtensionsUtils.addExtendedMasterSecretExtension(this.serverExtensions = TlsExtensionsUtils.ensureExtensionsInitialised(this.serverExtensions));
        }
        if (this.serverExtensions != null) {
            this.securityParameters.encryptThenMAC = TlsExtensionsUtils.hasEncryptThenMACExtension(this.serverExtensions);
            this.securityParameters.maxFragmentLength = this.processMaxFragmentLengthExtension(this.clientExtensions, this.serverExtensions, (short)80);
            this.securityParameters.truncatedHMac = TlsExtensionsUtils.hasTruncatedHMacExtension(this.serverExtensions);
            this.allowCertificateStatus = (!this.resumedSession && TlsUtils.hasExpectedEmptyExtensionData(this.serverExtensions, TlsExtensionsUtils.EXT_status_request, (short)80));
            this.expectSessionTicket = (!this.resumedSession && TlsUtils.hasExpectedEmptyExtensionData(this.serverExtensions, TlsProtocol.EXT_SessionTicket, (short)80));
            TlsProtocol.writeExtensions(handshakeMessage, this.serverExtensions);
        }
        this.securityParameters.prfAlgorithm = TlsProtocol.getPRFAlgorithm(this.getContext(), this.securityParameters.getCipherSuite());
        this.securityParameters.verifyDataLength = 12;
        this.applyMaxFragmentLengthExtension();
        handshakeMessage.writeToRecordStream();
    }
    
    protected void sendServerHelloDoneMessage() throws IOException {
        final byte[] array = new byte[4];
        TlsUtils.writeUint8((short)14, array, 0);
        TlsUtils.writeUint24(0, array, 1);
        this.writeHandshakeMessage(array, 0, array.length);
    }
    
    protected void sendServerKeyExchangeMessage(final byte[] array) throws IOException {
        final HandshakeMessage handshakeMessage = new HandshakeMessage((short)12, array.length);
        handshakeMessage.write(array);
        handshakeMessage.writeToRecordStream();
    }
    
    protected boolean expectCertificateVerifyMessage() {
        return this.clientCertificateType >= 0 && TlsUtils.hasSigningCapability(this.clientCertificateType);
    }
}
