package org.bouncycastle.crypto.tls;

import java.util.Hashtable;
import java.util.Enumeration;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import java.util.Vector;
import org.bouncycastle.util.Arrays;
import java.io.IOException;
import java.security.SecureRandom;

public class DTLSClientProtocol extends DTLSProtocol
{
    public DTLSClientProtocol(final SecureRandom secureRandom) {
        super(secureRandom);
    }
    
    public DTLSTransport connect(final TlsClient client, final DatagramTransport datagramTransport) throws IOException {
        if (client == null) {
            throw new IllegalArgumentException("'client' cannot be null");
        }
        if (datagramTransport == null) {
            throw new IllegalArgumentException("'transport' cannot be null");
        }
        final SecurityParameters securityParameters = new SecurityParameters();
        securityParameters.entity = 1;
        final ClientHandshakeState clientHandshakeState = new ClientHandshakeState();
        clientHandshakeState.client = client;
        clientHandshakeState.clientContext = new TlsClientContextImpl(this.secureRandom, securityParameters);
        securityParameters.clientRandom = TlsProtocol.createRandomBlock(client.shouldUseGMTUnixTime(), clientHandshakeState.clientContext.getNonceRandomGenerator());
        client.init(clientHandshakeState.clientContext);
        final DTLSRecordLayer dtlsRecordLayer = new DTLSRecordLayer(datagramTransport, clientHandshakeState.clientContext, client, (short)22);
        final TlsSession sessionToResume = clientHandshakeState.client.getSessionToResume();
        Label_0178: {
            if (sessionToResume == null || !sessionToResume.isResumable()) {
                break Label_0178;
            }
            final SessionParameters exportSessionParameters = sessionToResume.exportSessionParameters();
            if (exportSessionParameters == null) {
                break Label_0178;
            }
            clientHandshakeState.tlsSession = sessionToResume;
            clientHandshakeState.sessionParameters = exportSessionParameters;
            try {
                return this.clientHandshake(clientHandshakeState, dtlsRecordLayer);
            }
            catch (final TlsFatalAlert tlsFatalAlert) {
                this.abortClientHandshake(clientHandshakeState, dtlsRecordLayer, tlsFatalAlert.getAlertDescription());
                throw tlsFatalAlert;
            }
            catch (final IOException ex) {
                this.abortClientHandshake(clientHandshakeState, dtlsRecordLayer, (short)80);
                throw ex;
            }
            catch (final RuntimeException ex2) {
                this.abortClientHandshake(clientHandshakeState, dtlsRecordLayer, (short)80);
                throw new TlsFatalAlert((short)80, ex2);
            }
            finally {
                securityParameters.clear();
            }
        }
    }
    
    protected void abortClientHandshake(final ClientHandshakeState clientHandshakeState, final DTLSRecordLayer dtlsRecordLayer, final short n) {
        dtlsRecordLayer.fail(n);
        this.invalidateSession(clientHandshakeState);
    }
    
    protected DTLSTransport clientHandshake(final ClientHandshakeState clientHandshakeState, final DTLSRecordLayer dtlsRecordLayer) throws IOException {
        final SecurityParameters securityParameters = clientHandshakeState.clientContext.getSecurityParameters();
        final DTLSReliableHandshake dtlsReliableHandshake = new DTLSReliableHandshake(clientHandshakeState.clientContext, dtlsRecordLayer);
        final byte[] generateClientHello = this.generateClientHello(clientHandshakeState, clientHandshakeState.client);
        dtlsRecordLayer.setWriteVersion(ProtocolVersion.DTLSv10);
        dtlsReliableHandshake.sendMessage((short)1, generateClientHello);
        DTLSReliableHandshake.Message message;
        for (message = dtlsReliableHandshake.receiveMessage(); message.getType() == 3; message = dtlsReliableHandshake.receiveMessage()) {
            if (!dtlsRecordLayer.getReadVersion().isEqualOrEarlierVersionOf(clientHandshakeState.clientContext.getClientVersion())) {
                throw new TlsFatalAlert((short)47);
            }
            dtlsRecordLayer.setReadVersion(null);
            final byte[] patchClientHelloWithCookie = patchClientHelloWithCookie(generateClientHello, this.processHelloVerifyRequest(clientHandshakeState, message.getBody()));
            dtlsReliableHandshake.resetHandshakeMessagesDigest();
            dtlsReliableHandshake.sendMessage((short)1, patchClientHelloWithCookie);
        }
        if (message.getType() != 2) {
            throw new TlsFatalAlert((short)10);
        }
        final ProtocolVersion readVersion = dtlsRecordLayer.getReadVersion();
        this.reportServerVersion(clientHandshakeState, readVersion);
        dtlsRecordLayer.setWriteVersion(readVersion);
        this.processServerHello(clientHandshakeState, message.getBody());
        dtlsReliableHandshake.notifyHelloComplete();
        DTLSProtocol.applyMaxFragmentLengthExtension(dtlsRecordLayer, securityParameters.maxFragmentLength);
        if (clientHandshakeState.resumedSession) {
            securityParameters.masterSecret = Arrays.clone(clientHandshakeState.sessionParameters.getMasterSecret());
            dtlsRecordLayer.initPendingEpoch(clientHandshakeState.client.getCipher());
            this.processFinished(dtlsReliableHandshake.receiveMessageBody((short)20), TlsUtils.calculateVerifyData(clientHandshakeState.clientContext, "server finished", TlsProtocol.getCurrentPRFHash(clientHandshakeState.clientContext, dtlsReliableHandshake.getHandshakeHash(), null)));
            dtlsReliableHandshake.sendMessage((short)20, TlsUtils.calculateVerifyData(clientHandshakeState.clientContext, "client finished", TlsProtocol.getCurrentPRFHash(clientHandshakeState.clientContext, dtlsReliableHandshake.getHandshakeHash(), null)));
            dtlsReliableHandshake.finish();
            clientHandshakeState.clientContext.setResumableSession(clientHandshakeState.tlsSession);
            clientHandshakeState.client.notifyHandshakeComplete();
            return new DTLSTransport(dtlsRecordLayer);
        }
        this.invalidateSession(clientHandshakeState);
        if (clientHandshakeState.selectedSessionID.length > 0) {
            clientHandshakeState.tlsSession = new TlsSessionImpl(clientHandshakeState.selectedSessionID, null);
        }
        DTLSReliableHandshake.Message message2 = dtlsReliableHandshake.receiveMessage();
        if (message2.getType() == 23) {
            this.processServerSupplementalData(clientHandshakeState, message2.getBody());
            message2 = dtlsReliableHandshake.receiveMessage();
        }
        else {
            clientHandshakeState.client.processServerSupplementalData(null);
        }
        (clientHandshakeState.keyExchange = clientHandshakeState.client.getKeyExchange()).init(clientHandshakeState.clientContext);
        Certificate processServerCertificate = null;
        if (message2.getType() == 11) {
            processServerCertificate = this.processServerCertificate(clientHandshakeState, message2.getBody());
            message2 = dtlsReliableHandshake.receiveMessage();
        }
        else {
            clientHandshakeState.keyExchange.skipServerCredentials();
        }
        if (processServerCertificate == null || processServerCertificate.isEmpty()) {
            clientHandshakeState.allowCertificateStatus = false;
        }
        if (message2.getType() == 22) {
            this.processCertificateStatus(clientHandshakeState, message2.getBody());
            message2 = dtlsReliableHandshake.receiveMessage();
        }
        if (message2.getType() == 12) {
            this.processServerKeyExchange(clientHandshakeState, message2.getBody());
            message2 = dtlsReliableHandshake.receiveMessage();
        }
        else {
            clientHandshakeState.keyExchange.skipServerKeyExchange();
        }
        if (message2.getType() == 13) {
            this.processCertificateRequest(clientHandshakeState, message2.getBody());
            TlsUtils.trackHashAlgorithms(dtlsReliableHandshake.getHandshakeHash(), clientHandshakeState.certificateRequest.getSupportedSignatureAlgorithms());
            message2 = dtlsReliableHandshake.receiveMessage();
        }
        if (message2.getType() != 14) {
            throw new TlsFatalAlert((short)10);
        }
        if (message2.getBody().length != 0) {
            throw new TlsFatalAlert((short)50);
        }
        dtlsReliableHandshake.getHandshakeHash().sealHashAlgorithms();
        final Vector clientSupplementalData = clientHandshakeState.client.getClientSupplementalData();
        if (clientSupplementalData != null) {
            dtlsReliableHandshake.sendMessage((short)23, DTLSProtocol.generateSupplementalData(clientSupplementalData));
        }
        if (clientHandshakeState.certificateRequest != null) {
            clientHandshakeState.clientCredentials = clientHandshakeState.authentication.getClientCredentials(clientHandshakeState.certificateRequest);
            Certificate certificate = null;
            if (clientHandshakeState.clientCredentials != null) {
                certificate = clientHandshakeState.clientCredentials.getCertificate();
            }
            if (certificate == null) {
                certificate = Certificate.EMPTY_CHAIN;
            }
            dtlsReliableHandshake.sendMessage((short)11, DTLSProtocol.generateCertificate(certificate));
        }
        if (clientHandshakeState.clientCredentials != null) {
            clientHandshakeState.keyExchange.processClientCredentials(clientHandshakeState.clientCredentials);
        }
        else {
            clientHandshakeState.keyExchange.skipClientCredentials();
        }
        dtlsReliableHandshake.sendMessage((short)16, this.generateClientKeyExchange(clientHandshakeState));
        final TlsHandshakeHash prepareToFinish = dtlsReliableHandshake.prepareToFinish();
        securityParameters.sessionHash = TlsProtocol.getCurrentPRFHash(clientHandshakeState.clientContext, prepareToFinish, null);
        TlsProtocol.establishMasterSecret(clientHandshakeState.clientContext, clientHandshakeState.keyExchange);
        dtlsRecordLayer.initPendingEpoch(clientHandshakeState.client.getCipher());
        if (clientHandshakeState.clientCredentials != null && clientHandshakeState.clientCredentials instanceof TlsSignerCredentials) {
            final TlsSignerCredentials tlsSignerCredentials = (TlsSignerCredentials)clientHandshakeState.clientCredentials;
            final SignatureAndHashAlgorithm signatureAndHashAlgorithm = TlsUtils.getSignatureAndHashAlgorithm(clientHandshakeState.clientContext, tlsSignerCredentials);
            byte[] array;
            if (signatureAndHashAlgorithm == null) {
                array = securityParameters.getSessionHash();
            }
            else {
                array = prepareToFinish.getFinalHash(signatureAndHashAlgorithm.getHash());
            }
            dtlsReliableHandshake.sendMessage((short)15, this.generateCertificateVerify(clientHandshakeState, new DigitallySigned(signatureAndHashAlgorithm, tlsSignerCredentials.generateCertificateSignature(array))));
        }
        dtlsReliableHandshake.sendMessage((short)20, TlsUtils.calculateVerifyData(clientHandshakeState.clientContext, "client finished", TlsProtocol.getCurrentPRFHash(clientHandshakeState.clientContext, dtlsReliableHandshake.getHandshakeHash(), null)));
        if (clientHandshakeState.expectSessionTicket) {
            final DTLSReliableHandshake.Message receiveMessage = dtlsReliableHandshake.receiveMessage();
            if (receiveMessage.getType() != 4) {
                throw new TlsFatalAlert((short)10);
            }
            this.processNewSessionTicket(clientHandshakeState, receiveMessage.getBody());
        }
        this.processFinished(dtlsReliableHandshake.receiveMessageBody((short)20), TlsUtils.calculateVerifyData(clientHandshakeState.clientContext, "server finished", TlsProtocol.getCurrentPRFHash(clientHandshakeState.clientContext, dtlsReliableHandshake.getHandshakeHash(), null)));
        dtlsReliableHandshake.finish();
        if (clientHandshakeState.tlsSession != null) {
            clientHandshakeState.sessionParameters = new SessionParameters.Builder().setCipherSuite(securityParameters.getCipherSuite()).setCompressionAlgorithm(securityParameters.getCompressionAlgorithm()).setMasterSecret(securityParameters.getMasterSecret()).setPeerCertificate(processServerCertificate).setPSKIdentity(securityParameters.getPSKIdentity()).setSRPIdentity(securityParameters.getSRPIdentity()).setServerExtensions(clientHandshakeState.serverExtensions).build();
            clientHandshakeState.tlsSession = TlsUtils.importSession(clientHandshakeState.tlsSession.getSessionID(), clientHandshakeState.sessionParameters);
            clientHandshakeState.clientContext.setResumableSession(clientHandshakeState.tlsSession);
        }
        clientHandshakeState.client.notifyHandshakeComplete();
        return new DTLSTransport(dtlsRecordLayer);
    }
    
    protected byte[] generateCertificateVerify(final ClientHandshakeState clientHandshakeState, final DigitallySigned digitallySigned) throws IOException {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        digitallySigned.encode(byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }
    
    protected byte[] generateClientHello(final ClientHandshakeState clientHandshakeState, final TlsClient tlsClient) throws IOException {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        final ProtocolVersion clientVersion = tlsClient.getClientVersion();
        if (!clientVersion.isDTLS()) {
            throw new TlsFatalAlert((short)80);
        }
        final TlsClientContextImpl clientContext = clientHandshakeState.clientContext;
        clientContext.setClientVersion(clientVersion);
        TlsUtils.writeVersion(clientVersion, byteArrayOutputStream);
        byteArrayOutputStream.write(clientContext.getSecurityParameters().getClientRandom());
        byte[] array = TlsUtils.EMPTY_BYTES;
        if (clientHandshakeState.tlsSession != null) {
            array = clientHandshakeState.tlsSession.getSessionID();
            if (array == null || array.length > 32) {
                array = TlsUtils.EMPTY_BYTES;
            }
        }
        TlsUtils.writeOpaque8(array, byteArrayOutputStream);
        TlsUtils.writeOpaque8(TlsUtils.EMPTY_BYTES, byteArrayOutputStream);
        final boolean fallback = tlsClient.isFallback();
        clientHandshakeState.offeredCipherSuites = tlsClient.getCipherSuites();
        clientHandshakeState.clientExtensions = tlsClient.getClientExtensions();
        final boolean b = null == TlsUtils.getExtensionData(clientHandshakeState.clientExtensions, TlsProtocol.EXT_RenegotiationInfo);
        final boolean b2 = !Arrays.contains(clientHandshakeState.offeredCipherSuites, 255);
        if (b && b2) {
            clientHandshakeState.offeredCipherSuites = Arrays.append(clientHandshakeState.offeredCipherSuites, 255);
        }
        if (fallback && !Arrays.contains(clientHandshakeState.offeredCipherSuites, 22016)) {
            clientHandshakeState.offeredCipherSuites = Arrays.append(clientHandshakeState.offeredCipherSuites, 22016);
        }
        TlsUtils.writeUint16ArrayWithUint16Length(clientHandshakeState.offeredCipherSuites, byteArrayOutputStream);
        TlsUtils.writeUint8ArrayWithUint8Length(clientHandshakeState.offeredCompressionMethods = new short[] { 0 }, byteArrayOutputStream);
        if (clientHandshakeState.clientExtensions != null) {
            TlsProtocol.writeExtensions(byteArrayOutputStream, clientHandshakeState.clientExtensions);
        }
        return byteArrayOutputStream.toByteArray();
    }
    
    protected byte[] generateClientKeyExchange(final ClientHandshakeState clientHandshakeState) throws IOException {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        clientHandshakeState.keyExchange.generateClientKeyExchange(byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }
    
    protected void invalidateSession(final ClientHandshakeState clientHandshakeState) {
        if (clientHandshakeState.sessionParameters != null) {
            clientHandshakeState.sessionParameters.clear();
            clientHandshakeState.sessionParameters = null;
        }
        if (clientHandshakeState.tlsSession != null) {
            clientHandshakeState.tlsSession.invalidate();
            clientHandshakeState.tlsSession = null;
        }
    }
    
    protected void processCertificateRequest(final ClientHandshakeState clientHandshakeState, final byte[] array) throws IOException {
        if (clientHandshakeState.authentication == null) {
            throw new TlsFatalAlert((short)40);
        }
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(array);
        clientHandshakeState.certificateRequest = CertificateRequest.parse(clientHandshakeState.clientContext, byteArrayInputStream);
        TlsProtocol.assertEmpty(byteArrayInputStream);
        clientHandshakeState.keyExchange.validateCertificateRequest(clientHandshakeState.certificateRequest);
    }
    
    protected void processCertificateStatus(final ClientHandshakeState clientHandshakeState, final byte[] array) throws IOException {
        if (!clientHandshakeState.allowCertificateStatus) {
            throw new TlsFatalAlert((short)10);
        }
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(array);
        clientHandshakeState.certificateStatus = CertificateStatus.parse(byteArrayInputStream);
        TlsProtocol.assertEmpty(byteArrayInputStream);
    }
    
    protected byte[] processHelloVerifyRequest(final ClientHandshakeState clientHandshakeState, final byte[] array) throws IOException {
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(array);
        final ProtocolVersion version = TlsUtils.readVersion(byteArrayInputStream);
        final byte[] opaque8 = TlsUtils.readOpaque8(byteArrayInputStream);
        TlsProtocol.assertEmpty(byteArrayInputStream);
        if (!version.isEqualOrEarlierVersionOf(clientHandshakeState.clientContext.getClientVersion())) {
            throw new TlsFatalAlert((short)47);
        }
        if (!ProtocolVersion.DTLSv12.isEqualOrEarlierVersionOf(version) && opaque8.length > 32) {
            throw new TlsFatalAlert((short)47);
        }
        return opaque8;
    }
    
    protected void processNewSessionTicket(final ClientHandshakeState clientHandshakeState, final byte[] array) throws IOException {
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(array);
        final NewSessionTicket parse = NewSessionTicket.parse(byteArrayInputStream);
        TlsProtocol.assertEmpty(byteArrayInputStream);
        clientHandshakeState.client.notifyNewSessionTicket(parse);
    }
    
    protected Certificate processServerCertificate(final ClientHandshakeState clientHandshakeState, final byte[] array) throws IOException {
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(array);
        final Certificate parse = Certificate.parse(byteArrayInputStream);
        TlsProtocol.assertEmpty(byteArrayInputStream);
        clientHandshakeState.keyExchange.processServerCertificate(parse);
        (clientHandshakeState.authentication = clientHandshakeState.client.getAuthentication()).notifyServerCertificate(parse);
        return parse;
    }
    
    protected void processServerHello(final ClientHandshakeState clientHandshakeState, final byte[] array) throws IOException {
        final SecurityParameters securityParameters = clientHandshakeState.clientContext.getSecurityParameters();
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(array);
        this.reportServerVersion(clientHandshakeState, TlsUtils.readVersion(byteArrayInputStream));
        securityParameters.serverRandom = TlsUtils.readFully(32, byteArrayInputStream);
        clientHandshakeState.selectedSessionID = TlsUtils.readOpaque8(byteArrayInputStream);
        if (clientHandshakeState.selectedSessionID.length > 32) {
            throw new TlsFatalAlert((short)47);
        }
        clientHandshakeState.client.notifySessionID(clientHandshakeState.selectedSessionID);
        clientHandshakeState.resumedSession = (clientHandshakeState.selectedSessionID.length > 0 && clientHandshakeState.tlsSession != null && Arrays.areEqual(clientHandshakeState.selectedSessionID, clientHandshakeState.tlsSession.getSessionID()));
        final int uint16 = TlsUtils.readUint16(byteArrayInputStream);
        if (!Arrays.contains(clientHandshakeState.offeredCipherSuites, uint16) || uint16 == 0 || CipherSuite.isSCSV(uint16) || !TlsUtils.isValidCipherSuiteForVersion(uint16, clientHandshakeState.clientContext.getServerVersion())) {
            throw new TlsFatalAlert((short)47);
        }
        DTLSProtocol.validateSelectedCipherSuite(uint16, (short)47);
        clientHandshakeState.client.notifySelectedCipherSuite(uint16);
        final short uint17 = TlsUtils.readUint8(byteArrayInputStream);
        if (!Arrays.contains(clientHandshakeState.offeredCompressionMethods, uint17)) {
            throw new TlsFatalAlert((short)47);
        }
        clientHandshakeState.client.notifySelectedCompressionMethod(uint17);
        clientHandshakeState.serverExtensions = TlsProtocol.readExtensions(byteArrayInputStream);
        if (clientHandshakeState.serverExtensions != null) {
            final Enumeration keys = clientHandshakeState.serverExtensions.keys();
            while (keys.hasMoreElements()) {
                final Integer n = (Integer)keys.nextElement();
                if (n.equals(TlsProtocol.EXT_RenegotiationInfo)) {
                    continue;
                }
                if (null == TlsUtils.getExtensionData(clientHandshakeState.clientExtensions, n)) {
                    throw new TlsFatalAlert((short)110);
                }
                if (clientHandshakeState.resumedSession) {}
            }
        }
        final byte[] extensionData = TlsUtils.getExtensionData(clientHandshakeState.serverExtensions, TlsProtocol.EXT_RenegotiationInfo);
        if (extensionData != null) {
            clientHandshakeState.secure_renegotiation = true;
            if (!Arrays.constantTimeAreEqual(extensionData, TlsProtocol.createRenegotiationInfo(TlsUtils.EMPTY_BYTES))) {
                throw new TlsFatalAlert((short)40);
            }
        }
        clientHandshakeState.client.notifySecureRenegotiation(clientHandshakeState.secure_renegotiation);
        Hashtable clientExtensions = clientHandshakeState.clientExtensions;
        Hashtable hashtable = clientHandshakeState.serverExtensions;
        if (clientHandshakeState.resumedSession) {
            if (uint16 != clientHandshakeState.sessionParameters.getCipherSuite() || uint17 != clientHandshakeState.sessionParameters.getCompressionAlgorithm()) {
                throw new TlsFatalAlert((short)47);
            }
            clientExtensions = null;
            hashtable = clientHandshakeState.sessionParameters.readServerExtensions();
        }
        securityParameters.cipherSuite = uint16;
        securityParameters.compressionAlgorithm = uint17;
        if (hashtable != null) {
            final boolean hasEncryptThenMACExtension = TlsExtensionsUtils.hasEncryptThenMACExtension(hashtable);
            if (hasEncryptThenMACExtension && !TlsUtils.isBlockCipherSuite(securityParameters.getCipherSuite())) {
                throw new TlsFatalAlert((short)47);
            }
            securityParameters.encryptThenMAC = hasEncryptThenMACExtension;
            securityParameters.extendedMasterSecret = TlsExtensionsUtils.hasExtendedMasterSecretExtension(hashtable);
            securityParameters.maxFragmentLength = DTLSProtocol.evaluateMaxFragmentLengthExtension(clientHandshakeState.resumedSession, clientExtensions, hashtable, (short)47);
            securityParameters.truncatedHMac = TlsExtensionsUtils.hasTruncatedHMacExtension(hashtable);
            clientHandshakeState.allowCertificateStatus = (!clientHandshakeState.resumedSession && TlsUtils.hasExpectedEmptyExtensionData(hashtable, TlsExtensionsUtils.EXT_status_request, (short)47));
            clientHandshakeState.expectSessionTicket = (!clientHandshakeState.resumedSession && TlsUtils.hasExpectedEmptyExtensionData(hashtable, TlsProtocol.EXT_SessionTicket, (short)47));
        }
        if (clientExtensions != null) {
            clientHandshakeState.client.processServerExtensions(hashtable);
        }
        securityParameters.prfAlgorithm = TlsProtocol.getPRFAlgorithm(clientHandshakeState.clientContext, securityParameters.getCipherSuite());
        securityParameters.verifyDataLength = 12;
    }
    
    protected void processServerKeyExchange(final ClientHandshakeState clientHandshakeState, final byte[] array) throws IOException {
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(array);
        clientHandshakeState.keyExchange.processServerKeyExchange(byteArrayInputStream);
        TlsProtocol.assertEmpty(byteArrayInputStream);
    }
    
    protected void processServerSupplementalData(final ClientHandshakeState clientHandshakeState, final byte[] array) throws IOException {
        clientHandshakeState.client.processServerSupplementalData(TlsProtocol.readSupplementalDataMessage(new ByteArrayInputStream(array)));
    }
    
    protected void reportServerVersion(final ClientHandshakeState clientHandshakeState, final ProtocolVersion serverVersion) throws IOException {
        final TlsClientContextImpl clientContext = clientHandshakeState.clientContext;
        final ProtocolVersion serverVersion2 = clientContext.getServerVersion();
        if (null == serverVersion2) {
            clientContext.setServerVersion(serverVersion);
            clientHandshakeState.client.notifyServerVersion(serverVersion);
        }
        else if (!serverVersion2.equals(serverVersion)) {
            throw new TlsFatalAlert((short)47);
        }
    }
    
    protected static byte[] patchClientHelloWithCookie(final byte[] array, final byte[] array2) throws IOException {
        final int n = 34;
        final int n2 = n + 1 + TlsUtils.readUint8(array, n);
        final int n3 = n2 + 1;
        final byte[] array3 = new byte[array.length + array2.length];
        System.arraycopy(array, 0, array3, 0, n2);
        TlsUtils.checkUint8(array2.length);
        TlsUtils.writeUint8(array2.length, array3, n2);
        System.arraycopy(array2, 0, array3, n3, array2.length);
        System.arraycopy(array, n3, array3, n3 + array2.length, array.length - n3);
        return array3;
    }
    
    protected static class ClientHandshakeState
    {
        TlsClient client;
        TlsClientContextImpl clientContext;
        TlsSession tlsSession;
        SessionParameters sessionParameters;
        SessionParameters.Builder sessionParametersBuilder;
        int[] offeredCipherSuites;
        short[] offeredCompressionMethods;
        Hashtable clientExtensions;
        Hashtable serverExtensions;
        byte[] selectedSessionID;
        boolean resumedSession;
        boolean secure_renegotiation;
        boolean allowCertificateStatus;
        boolean expectSessionTicket;
        TlsKeyExchange keyExchange;
        TlsAuthentication authentication;
        CertificateStatus certificateStatus;
        CertificateRequest certificateRequest;
        TlsCredentials clientCredentials;
        
        protected ClientHandshakeState() {
            this.client = null;
            this.clientContext = null;
            this.tlsSession = null;
            this.sessionParameters = null;
            this.sessionParametersBuilder = null;
            this.offeredCipherSuites = null;
            this.offeredCompressionMethods = null;
            this.clientExtensions = null;
            this.serverExtensions = null;
            this.selectedSessionID = null;
            this.resumedSession = false;
            this.secure_renegotiation = false;
            this.allowCertificateStatus = false;
            this.expectSessionTicket = false;
            this.keyExchange = null;
            this.authentication = null;
            this.certificateStatus = null;
            this.certificateRequest = null;
            this.clientCredentials = null;
        }
    }
}
