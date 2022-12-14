package org.bouncycastle.crypto.tls;

import java.util.Hashtable;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.util.PublicKeyFactory;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import org.bouncycastle.util.Arrays;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import java.util.Vector;
import java.io.IOException;
import java.security.SecureRandom;

public class DTLSServerProtocol extends DTLSProtocol
{
    protected boolean verifyRequests;
    
    public DTLSServerProtocol(final SecureRandom secureRandom) {
        super(secureRandom);
        this.verifyRequests = true;
    }
    
    public boolean getVerifyRequests() {
        return this.verifyRequests;
    }
    
    public void setVerifyRequests(final boolean verifyRequests) {
        this.verifyRequests = verifyRequests;
    }
    
    public DTLSTransport accept(final TlsServer server, final DatagramTransport datagramTransport) throws IOException {
        if (server == null) {
            throw new IllegalArgumentException("'server' cannot be null");
        }
        if (datagramTransport == null) {
            throw new IllegalArgumentException("'transport' cannot be null");
        }
        final SecurityParameters securityParameters = new SecurityParameters();
        securityParameters.entity = 0;
        final ServerHandshakeState serverHandshakeState = new ServerHandshakeState();
        serverHandshakeState.server = server;
        serverHandshakeState.serverContext = new TlsServerContextImpl(this.secureRandom, securityParameters);
        securityParameters.serverRandom = TlsProtocol.createRandomBlock(server.shouldUseGMTUnixTime(), serverHandshakeState.serverContext.getNonceRandomGenerator());
        server.init(serverHandshakeState.serverContext);
        final DTLSRecordLayer dtlsRecordLayer = new DTLSRecordLayer(datagramTransport, serverHandshakeState.serverContext, server, (short)22);
        try {
            return this.serverHandshake(serverHandshakeState, dtlsRecordLayer);
        }
        catch (final TlsFatalAlert tlsFatalAlert) {
            this.abortServerHandshake(serverHandshakeState, dtlsRecordLayer, tlsFatalAlert.getAlertDescription());
            throw tlsFatalAlert;
        }
        catch (final IOException ex) {
            this.abortServerHandshake(serverHandshakeState, dtlsRecordLayer, (short)80);
            throw ex;
        }
        catch (final RuntimeException ex2) {
            this.abortServerHandshake(serverHandshakeState, dtlsRecordLayer, (short)80);
            throw new TlsFatalAlert((short)80, ex2);
        }
        finally {
            securityParameters.clear();
        }
    }
    
    protected void abortServerHandshake(final ServerHandshakeState serverHandshakeState, final DTLSRecordLayer dtlsRecordLayer, final short n) {
        dtlsRecordLayer.fail(n);
        this.invalidateSession(serverHandshakeState);
    }
    
    protected DTLSTransport serverHandshake(final ServerHandshakeState serverHandshakeState, final DTLSRecordLayer dtlsRecordLayer) throws IOException {
        final SecurityParameters securityParameters = serverHandshakeState.serverContext.getSecurityParameters();
        final DTLSReliableHandshake dtlsReliableHandshake = new DTLSReliableHandshake(serverHandshakeState.serverContext, dtlsRecordLayer);
        final DTLSReliableHandshake.Message receiveMessage = dtlsReliableHandshake.receiveMessage();
        if (receiveMessage.getType() != 1) {
            throw new TlsFatalAlert((short)10);
        }
        this.processClientHello(serverHandshakeState, receiveMessage.getBody());
        final byte[] generateServerHello = this.generateServerHello(serverHandshakeState);
        DTLSProtocol.applyMaxFragmentLengthExtension(dtlsRecordLayer, securityParameters.maxFragmentLength);
        final ProtocolVersion serverVersion = serverHandshakeState.serverContext.getServerVersion();
        dtlsRecordLayer.setReadVersion(serverVersion);
        dtlsRecordLayer.setWriteVersion(serverVersion);
        dtlsReliableHandshake.sendMessage((short)2, generateServerHello);
        dtlsReliableHandshake.notifyHelloComplete();
        final Vector serverSupplementalData = serverHandshakeState.server.getServerSupplementalData();
        if (serverSupplementalData != null) {
            dtlsReliableHandshake.sendMessage((short)23, DTLSProtocol.generateSupplementalData(serverSupplementalData));
        }
        (serverHandshakeState.keyExchange = serverHandshakeState.server.getKeyExchange()).init(serverHandshakeState.serverContext);
        serverHandshakeState.serverCredentials = serverHandshakeState.server.getCredentials();
        Certificate certificate = null;
        if (serverHandshakeState.serverCredentials == null) {
            serverHandshakeState.keyExchange.skipServerCredentials();
        }
        else {
            serverHandshakeState.keyExchange.processServerCredentials(serverHandshakeState.serverCredentials);
            certificate = serverHandshakeState.serverCredentials.getCertificate();
            dtlsReliableHandshake.sendMessage((short)11, DTLSProtocol.generateCertificate(certificate));
        }
        if (certificate == null || certificate.isEmpty()) {
            serverHandshakeState.allowCertificateStatus = false;
        }
        if (serverHandshakeState.allowCertificateStatus) {
            final CertificateStatus certificateStatus = serverHandshakeState.server.getCertificateStatus();
            if (certificateStatus != null) {
                dtlsReliableHandshake.sendMessage((short)22, this.generateCertificateStatus(serverHandshakeState, certificateStatus));
            }
        }
        final byte[] generateServerKeyExchange = serverHandshakeState.keyExchange.generateServerKeyExchange();
        if (generateServerKeyExchange != null) {
            dtlsReliableHandshake.sendMessage((short)12, generateServerKeyExchange);
        }
        if (serverHandshakeState.serverCredentials != null) {
            serverHandshakeState.certificateRequest = serverHandshakeState.server.getCertificateRequest();
            if (serverHandshakeState.certificateRequest != null) {
                if (TlsUtils.isTLSv12(serverHandshakeState.serverContext) != (serverHandshakeState.certificateRequest.getSupportedSignatureAlgorithms() != null)) {
                    throw new TlsFatalAlert((short)80);
                }
                serverHandshakeState.keyExchange.validateCertificateRequest(serverHandshakeState.certificateRequest);
                dtlsReliableHandshake.sendMessage((short)13, this.generateCertificateRequest(serverHandshakeState, serverHandshakeState.certificateRequest));
                TlsUtils.trackHashAlgorithms(dtlsReliableHandshake.getHandshakeHash(), serverHandshakeState.certificateRequest.getSupportedSignatureAlgorithms());
            }
        }
        dtlsReliableHandshake.sendMessage((short)14, TlsUtils.EMPTY_BYTES);
        dtlsReliableHandshake.getHandshakeHash().sealHashAlgorithms();
        DTLSReliableHandshake.Message message = dtlsReliableHandshake.receiveMessage();
        if (message.getType() == 23) {
            this.processClientSupplementalData(serverHandshakeState, message.getBody());
            message = dtlsReliableHandshake.receiveMessage();
        }
        else {
            serverHandshakeState.server.processClientSupplementalData(null);
        }
        if (serverHandshakeState.certificateRequest == null) {
            serverHandshakeState.keyExchange.skipClientCredentials();
        }
        else if (message.getType() == 11) {
            this.processClientCertificate(serverHandshakeState, message.getBody());
            message = dtlsReliableHandshake.receiveMessage();
        }
        else {
            if (TlsUtils.isTLSv12(serverHandshakeState.serverContext)) {
                throw new TlsFatalAlert((short)10);
            }
            this.notifyClientCertificate(serverHandshakeState, Certificate.EMPTY_CHAIN);
        }
        if (message.getType() == 16) {
            this.processClientKeyExchange(serverHandshakeState, message.getBody());
            final TlsHandshakeHash prepareToFinish = dtlsReliableHandshake.prepareToFinish();
            securityParameters.sessionHash = TlsProtocol.getCurrentPRFHash(serverHandshakeState.serverContext, prepareToFinish, null);
            TlsProtocol.establishMasterSecret(serverHandshakeState.serverContext, serverHandshakeState.keyExchange);
            dtlsRecordLayer.initPendingEpoch(serverHandshakeState.server.getCipher());
            if (this.expectCertificateVerifyMessage(serverHandshakeState)) {
                this.processCertificateVerify(serverHandshakeState, dtlsReliableHandshake.receiveMessageBody((short)15), prepareToFinish);
            }
            this.processFinished(dtlsReliableHandshake.receiveMessageBody((short)20), TlsUtils.calculateVerifyData(serverHandshakeState.serverContext, "client finished", TlsProtocol.getCurrentPRFHash(serverHandshakeState.serverContext, dtlsReliableHandshake.getHandshakeHash(), null)));
            if (serverHandshakeState.expectSessionTicket) {
                dtlsReliableHandshake.sendMessage((short)4, this.generateNewSessionTicket(serverHandshakeState, serverHandshakeState.server.getNewSessionTicket()));
            }
            dtlsReliableHandshake.sendMessage((short)20, TlsUtils.calculateVerifyData(serverHandshakeState.serverContext, "server finished", TlsProtocol.getCurrentPRFHash(serverHandshakeState.serverContext, dtlsReliableHandshake.getHandshakeHash(), null)));
            dtlsReliableHandshake.finish();
            serverHandshakeState.server.notifyHandshakeComplete();
            return new DTLSTransport(dtlsRecordLayer);
        }
        throw new TlsFatalAlert((short)10);
    }
    
    protected byte[] generateCertificateRequest(final ServerHandshakeState serverHandshakeState, final CertificateRequest certificateRequest) throws IOException {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        certificateRequest.encode(byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }
    
    protected byte[] generateCertificateStatus(final ServerHandshakeState serverHandshakeState, final CertificateStatus certificateStatus) throws IOException {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        certificateStatus.encode(byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }
    
    protected byte[] generateNewSessionTicket(final ServerHandshakeState serverHandshakeState, final NewSessionTicket newSessionTicket) throws IOException {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        newSessionTicket.encode(byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }
    
    protected byte[] generateServerHello(final ServerHandshakeState serverHandshakeState) throws IOException {
        final SecurityParameters securityParameters = serverHandshakeState.serverContext.getSecurityParameters();
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        final ProtocolVersion serverVersion = serverHandshakeState.server.getServerVersion();
        if (!serverVersion.isEqualOrEarlierVersionOf(serverHandshakeState.serverContext.getClientVersion())) {
            throw new TlsFatalAlert((short)80);
        }
        serverHandshakeState.serverContext.setServerVersion(serverVersion);
        TlsUtils.writeVersion(serverHandshakeState.serverContext.getServerVersion(), byteArrayOutputStream);
        byteArrayOutputStream.write(securityParameters.getServerRandom());
        TlsUtils.writeOpaque8(TlsUtils.EMPTY_BYTES, byteArrayOutputStream);
        final int selectedCipherSuite = serverHandshakeState.server.getSelectedCipherSuite();
        if (!Arrays.contains(serverHandshakeState.offeredCipherSuites, selectedCipherSuite) || selectedCipherSuite == 0 || CipherSuite.isSCSV(selectedCipherSuite) || !TlsUtils.isValidCipherSuiteForVersion(selectedCipherSuite, serverHandshakeState.serverContext.getServerVersion())) {
            throw new TlsFatalAlert((short)80);
        }
        DTLSProtocol.validateSelectedCipherSuite(selectedCipherSuite, (short)80);
        securityParameters.cipherSuite = selectedCipherSuite;
        final short selectedCompressionMethod = serverHandshakeState.server.getSelectedCompressionMethod();
        if (!Arrays.contains(serverHandshakeState.offeredCompressionMethods, selectedCompressionMethod)) {
            throw new TlsFatalAlert((short)80);
        }
        securityParameters.compressionAlgorithm = selectedCompressionMethod;
        TlsUtils.writeUint16(selectedCipherSuite, byteArrayOutputStream);
        TlsUtils.writeUint8(selectedCompressionMethod, byteArrayOutputStream);
        serverHandshakeState.serverExtensions = serverHandshakeState.server.getServerExtensions();
        if (serverHandshakeState.secure_renegotiation && null == TlsUtils.getExtensionData(serverHandshakeState.serverExtensions, TlsProtocol.EXT_RenegotiationInfo)) {
            (serverHandshakeState.serverExtensions = TlsExtensionsUtils.ensureExtensionsInitialised(serverHandshakeState.serverExtensions)).put(TlsProtocol.EXT_RenegotiationInfo, TlsProtocol.createRenegotiationInfo(TlsUtils.EMPTY_BYTES));
        }
        if (securityParameters.extendedMasterSecret) {
            TlsExtensionsUtils.addExtendedMasterSecretExtension(serverHandshakeState.serverExtensions = TlsExtensionsUtils.ensureExtensionsInitialised(serverHandshakeState.serverExtensions));
        }
        if (serverHandshakeState.serverExtensions != null) {
            securityParameters.encryptThenMAC = TlsExtensionsUtils.hasEncryptThenMACExtension(serverHandshakeState.serverExtensions);
            securityParameters.maxFragmentLength = DTLSProtocol.evaluateMaxFragmentLengthExtension(serverHandshakeState.resumedSession, serverHandshakeState.clientExtensions, serverHandshakeState.serverExtensions, (short)80);
            securityParameters.truncatedHMac = TlsExtensionsUtils.hasTruncatedHMacExtension(serverHandshakeState.serverExtensions);
            serverHandshakeState.allowCertificateStatus = (!serverHandshakeState.resumedSession && TlsUtils.hasExpectedEmptyExtensionData(serverHandshakeState.serverExtensions, TlsExtensionsUtils.EXT_status_request, (short)80));
            serverHandshakeState.expectSessionTicket = (!serverHandshakeState.resumedSession && TlsUtils.hasExpectedEmptyExtensionData(serverHandshakeState.serverExtensions, TlsProtocol.EXT_SessionTicket, (short)80));
            TlsProtocol.writeExtensions(byteArrayOutputStream, serverHandshakeState.serverExtensions);
        }
        securityParameters.prfAlgorithm = TlsProtocol.getPRFAlgorithm(serverHandshakeState.serverContext, securityParameters.getCipherSuite());
        securityParameters.verifyDataLength = 12;
        return byteArrayOutputStream.toByteArray();
    }
    
    protected void invalidateSession(final ServerHandshakeState serverHandshakeState) {
        if (serverHandshakeState.sessionParameters != null) {
            serverHandshakeState.sessionParameters.clear();
            serverHandshakeState.sessionParameters = null;
        }
        if (serverHandshakeState.tlsSession != null) {
            serverHandshakeState.tlsSession.invalidate();
            serverHandshakeState.tlsSession = null;
        }
    }
    
    protected void notifyClientCertificate(final ServerHandshakeState serverHandshakeState, final Certificate clientCertificate) throws IOException {
        if (serverHandshakeState.certificateRequest == null) {
            throw new IllegalStateException();
        }
        if (serverHandshakeState.clientCertificate != null) {
            throw new TlsFatalAlert((short)10);
        }
        serverHandshakeState.clientCertificate = clientCertificate;
        if (clientCertificate.isEmpty()) {
            serverHandshakeState.keyExchange.skipClientCredentials();
        }
        else {
            serverHandshakeState.clientCertificateType = TlsUtils.getClientCertificateType(clientCertificate, serverHandshakeState.serverCredentials.getCertificate());
            serverHandshakeState.keyExchange.processClientCertificate(clientCertificate);
        }
        serverHandshakeState.server.notifyClientCertificate(clientCertificate);
    }
    
    protected void processClientCertificate(final ServerHandshakeState serverHandshakeState, final byte[] array) throws IOException {
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(array);
        final Certificate parse = Certificate.parse(byteArrayInputStream);
        TlsProtocol.assertEmpty(byteArrayInputStream);
        this.notifyClientCertificate(serverHandshakeState, parse);
    }
    
    protected void processCertificateVerify(final ServerHandshakeState serverHandshakeState, final byte[] array, final TlsHandshakeHash tlsHandshakeHash) throws IOException {
        if (serverHandshakeState.certificateRequest == null) {
            throw new IllegalStateException();
        }
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(array);
        final TlsServerContextImpl serverContext = serverHandshakeState.serverContext;
        final DigitallySigned parse = DigitallySigned.parse(serverContext, byteArrayInputStream);
        TlsProtocol.assertEmpty(byteArrayInputStream);
        try {
            final SignatureAndHashAlgorithm algorithm = parse.getAlgorithm();
            byte[] array2;
            if (TlsUtils.isTLSv12(serverContext)) {
                TlsUtils.verifySupportedSignatureAlgorithm(serverHandshakeState.certificateRequest.getSupportedSignatureAlgorithms(), algorithm);
                array2 = tlsHandshakeHash.getFinalHash(algorithm.getHash());
            }
            else {
                array2 = serverContext.getSecurityParameters().getSessionHash();
            }
            final AsymmetricKeyParameter key = PublicKeyFactory.createKey(serverHandshakeState.clientCertificate.getCertificateAt(0).getSubjectPublicKeyInfo());
            final TlsSigner tlsSigner = TlsUtils.createTlsSigner(serverHandshakeState.clientCertificateType);
            tlsSigner.init(serverContext);
            if (!tlsSigner.verifyRawSignature(algorithm, parse.getSignature(), key, array2)) {
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
    
    protected void processClientHello(final ServerHandshakeState serverHandshakeState, final byte[] array) throws IOException {
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(array);
        final ProtocolVersion version = TlsUtils.readVersion(byteArrayInputStream);
        if (!version.isDTLS()) {
            throw new TlsFatalAlert((short)47);
        }
        final byte[] fully = TlsUtils.readFully(32, byteArrayInputStream);
        if (TlsUtils.readOpaque8(byteArrayInputStream).length > 32) {
            throw new TlsFatalAlert((short)47);
        }
        TlsUtils.readOpaque8(byteArrayInputStream);
        final int uint16 = TlsUtils.readUint16(byteArrayInputStream);
        if (uint16 < 2 || (uint16 & 0x1) != 0x0) {
            throw new TlsFatalAlert((short)50);
        }
        serverHandshakeState.offeredCipherSuites = TlsUtils.readUint16Array(uint16 / 2, byteArrayInputStream);
        final short uint17 = TlsUtils.readUint8(byteArrayInputStream);
        if (uint17 < 1) {
            throw new TlsFatalAlert((short)47);
        }
        serverHandshakeState.offeredCompressionMethods = TlsUtils.readUint8Array(uint17, byteArrayInputStream);
        serverHandshakeState.clientExtensions = TlsProtocol.readExtensions(byteArrayInputStream);
        final TlsServerContextImpl serverContext = serverHandshakeState.serverContext;
        final SecurityParameters securityParameters = serverContext.getSecurityParameters();
        securityParameters.extendedMasterSecret = TlsExtensionsUtils.hasExtendedMasterSecretExtension(serverHandshakeState.clientExtensions);
        serverContext.setClientVersion(version);
        serverHandshakeState.server.notifyClientVersion(version);
        serverHandshakeState.server.notifyFallback(Arrays.contains(serverHandshakeState.offeredCipherSuites, 22016));
        securityParameters.clientRandom = fully;
        serverHandshakeState.server.notifyOfferedCipherSuites(serverHandshakeState.offeredCipherSuites);
        serverHandshakeState.server.notifyOfferedCompressionMethods(serverHandshakeState.offeredCompressionMethods);
        if (Arrays.contains(serverHandshakeState.offeredCipherSuites, 255)) {
            serverHandshakeState.secure_renegotiation = true;
        }
        final byte[] extensionData = TlsUtils.getExtensionData(serverHandshakeState.clientExtensions, TlsProtocol.EXT_RenegotiationInfo);
        if (extensionData != null) {
            serverHandshakeState.secure_renegotiation = true;
            if (!Arrays.constantTimeAreEqual(extensionData, TlsProtocol.createRenegotiationInfo(TlsUtils.EMPTY_BYTES))) {
                throw new TlsFatalAlert((short)40);
            }
        }
        serverHandshakeState.server.notifySecureRenegotiation(serverHandshakeState.secure_renegotiation);
        if (serverHandshakeState.clientExtensions != null) {
            TlsExtensionsUtils.getPaddingExtension(serverHandshakeState.clientExtensions);
            serverHandshakeState.server.processClientExtensions(serverHandshakeState.clientExtensions);
        }
    }
    
    protected void processClientKeyExchange(final ServerHandshakeState serverHandshakeState, final byte[] array) throws IOException {
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(array);
        serverHandshakeState.keyExchange.processClientKeyExchange(byteArrayInputStream);
        TlsProtocol.assertEmpty(byteArrayInputStream);
    }
    
    protected void processClientSupplementalData(final ServerHandshakeState serverHandshakeState, final byte[] array) throws IOException {
        serverHandshakeState.server.processClientSupplementalData(TlsProtocol.readSupplementalDataMessage(new ByteArrayInputStream(array)));
    }
    
    protected boolean expectCertificateVerifyMessage(final ServerHandshakeState serverHandshakeState) {
        return serverHandshakeState.clientCertificateType >= 0 && TlsUtils.hasSigningCapability(serverHandshakeState.clientCertificateType);
    }
    
    protected static class ServerHandshakeState
    {
        TlsServer server;
        TlsServerContextImpl serverContext;
        TlsSession tlsSession;
        SessionParameters sessionParameters;
        SessionParameters.Builder sessionParametersBuilder;
        int[] offeredCipherSuites;
        short[] offeredCompressionMethods;
        Hashtable clientExtensions;
        Hashtable serverExtensions;
        boolean resumedSession;
        boolean secure_renegotiation;
        boolean allowCertificateStatus;
        boolean expectSessionTicket;
        TlsKeyExchange keyExchange;
        TlsCredentials serverCredentials;
        CertificateRequest certificateRequest;
        short clientCertificateType;
        Certificate clientCertificate;
        
        protected ServerHandshakeState() {
            this.server = null;
            this.serverContext = null;
            this.tlsSession = null;
            this.sessionParameters = null;
            this.sessionParametersBuilder = null;
            this.offeredCipherSuites = null;
            this.offeredCompressionMethods = null;
            this.clientExtensions = null;
            this.serverExtensions = null;
            this.resumedSession = false;
            this.secure_renegotiation = false;
            this.allowCertificateStatus = false;
            this.expectSessionTicket = false;
            this.keyExchange = null;
            this.serverCredentials = null;
            this.certificateRequest = null;
            this.clientCertificateType = -1;
            this.clientCertificate = null;
        }
    }
}
