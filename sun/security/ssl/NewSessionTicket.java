package sun.security.ssl;

import java.security.ProviderException;
import java.text.MessageFormat;
import java.util.Locale;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.io.IOException;
import java.security.GeneralSecurityException;
import javax.net.ssl.SSLHandshakeException;
import javax.crypto.SecretKey;

final class NewSessionTicket
{
    private static final int MAX_TICKET_LIFETIME = 604800;
    static final SSLConsumer handshakeConsumer;
    static final SSLProducer kickstartProducer;
    static final HandshakeProducer handshakeProducer;
    
    private static SecretKey derivePreSharedKey(final CipherSuite.HashAlg hashAlg, final SecretKey secretKey, final byte[] array) throws IOException {
        try {
            return new HKDF(hashAlg.name).expand(secretKey, SSLSecretDerivation.createHkdfInfo("tls13 resumption".getBytes(), array, hashAlg.hashLength), hashAlg.hashLength, "TlsPreSharedKey");
        }
        catch (final GeneralSecurityException ex) {
            throw (SSLHandshakeException)new SSLHandshakeException("Could not derive PSK").initCause(ex);
        }
    }
    
    static {
        handshakeConsumer = new NewSessionTicketConsumer();
        kickstartProducer = new NewSessionTicketKickstartProducer();
        handshakeProducer = new NewSessionTicketProducer();
    }
    
    static final class NewSessionTicketMessage extends SSLHandshake.HandshakeMessage
    {
        final int ticketLifetime;
        final int ticketAgeAdd;
        final byte[] ticketNonce;
        final byte[] ticket;
        final SSLExtensions extensions;
        
        NewSessionTicketMessage(final HandshakeContext handshakeContext, final int ticketLifetime, final SecureRandom secureRandom, final byte[] ticketNonce, final byte[] ticket) {
            super(handshakeContext);
            this.ticketLifetime = ticketLifetime;
            this.ticketAgeAdd = secureRandom.nextInt();
            this.ticketNonce = ticketNonce;
            this.ticket = ticket;
            this.extensions = new SSLExtensions(this);
        }
        
        NewSessionTicketMessage(final HandshakeContext handshakeContext, final ByteBuffer byteBuffer) throws IOException {
            super(handshakeContext);
            if (byteBuffer.remaining() < 14) {
                throw handshakeContext.conContext.fatal(Alert.ILLEGAL_PARAMETER, "Invalid NewSessionTicket message: no sufficient data");
            }
            this.ticketLifetime = Record.getInt32(byteBuffer);
            this.ticketAgeAdd = Record.getInt32(byteBuffer);
            this.ticketNonce = Record.getBytes8(byteBuffer);
            if (byteBuffer.remaining() < 5) {
                throw handshakeContext.conContext.fatal(Alert.ILLEGAL_PARAMETER, "Invalid NewSessionTicket message: no sufficient data");
            }
            this.ticket = Record.getBytes16(byteBuffer);
            if (this.ticket.length == 0) {
                throw handshakeContext.conContext.fatal(Alert.ILLEGAL_PARAMETER, "No ticket in the NewSessionTicket handshake message");
            }
            if (byteBuffer.remaining() < 2) {
                throw handshakeContext.conContext.fatal(Alert.ILLEGAL_PARAMETER, "Invalid NewSessionTicket message: no sufficient data");
            }
            this.extensions = new SSLExtensions(this, byteBuffer, handshakeContext.sslConfig.getEnabledExtensions(SSLHandshake.NEW_SESSION_TICKET));
        }
        
        public SSLHandshake handshakeType() {
            return SSLHandshake.NEW_SESSION_TICKET;
        }
        
        public int messageLength() {
            int length = this.extensions.length();
            if (length == 0) {
                length = 2;
            }
            return 8 + this.ticketNonce.length + 1 + this.ticket.length + 2 + length;
        }
        
        public void send(final HandshakeOutStream handshakeOutStream) throws IOException {
            handshakeOutStream.putInt32(this.ticketLifetime);
            handshakeOutStream.putInt32(this.ticketAgeAdd);
            handshakeOutStream.putBytes8(this.ticketNonce);
            handshakeOutStream.putBytes16(this.ticket);
            if (this.extensions.length() == 0) {
                handshakeOutStream.putInt16(0);
            }
            else {
                this.extensions.send(handshakeOutStream);
            }
        }
        
        @Override
        public String toString() {
            return new MessageFormat("\"NewSessionTicket\": '{'\n  \"ticket_lifetime\"      : \"{0}\",\n  \"ticket_age_add\"       : \"{1}\",\n  \"ticket_nonce\"         : \"{2}\",\n  \"ticket\"               : \"{3}\",\n  \"extensions\"           : [\n{4}\n  ]\n'}'", Locale.ENGLISH).format(new Object[] { this.ticketLifetime, "<omitted>", Utilities.toHexString(this.ticketNonce), Utilities.toHexString(this.ticket), Utilities.indent(this.extensions.toString(), "    ") });
        }
    }
    
    private static final class NewSessionTicketKickstartProducer implements SSLProducer
    {
        @Override
        public byte[] produce(final ConnectionContext connectionContext) throws IOException {
            final ServerHandshakeContext serverHandshakeContext = (ServerHandshakeContext)connectionContext;
            if (!serverHandshakeContext.handshakeSession.isRejoinable()) {
                return null;
            }
            final PskKeyExchangeModesExtension.PskKeyExchangeModesSpec pskKeyExchangeModesSpec = serverHandshakeContext.handshakeExtensions.get(SSLExtension.PSK_KEY_EXCHANGE_MODES);
            if (pskKeyExchangeModesSpec == null || !pskKeyExchangeModesSpec.contains(PskKeyExchangeModesExtension.PskKeyExchangeMode.PSK_DHE_KE)) {
                return null;
            }
            final SSLSessionContextImpl sslSessionContextImpl = (SSLSessionContextImpl)serverHandshakeContext.sslContext.engineGetServerSessionContext();
            final SessionId sessionId = new SessionId(true, serverHandshakeContext.sslContext.getSecureRandom());
            final SecretKey resumptionMasterSecret = serverHandshakeContext.handshakeSession.getResumptionMasterSecret();
            if (resumptionMasterSecret == null) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("Session has no resumption secret. No ticket sent.", new Object[0]);
                }
                return null;
            }
            final byte[] byteArray = serverHandshakeContext.handshakeSession.incrTicketNonceCounter().toByteArray();
            final SecretKey access$300 = derivePreSharedKey(serverHandshakeContext.negotiatedCipherSuite.hashAlg, resumptionMasterSecret, byteArray);
            final int sessionTimeout = sslSessionContextImpl.getSessionTimeout();
            if (sessionTimeout > 604800) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("Session timeout is too long. No ticket sent.", new Object[0]);
                }
                return null;
            }
            final NewSessionTicketMessage newSessionTicketMessage = new NewSessionTicketMessage(serverHandshakeContext, sessionTimeout, serverHandshakeContext.sslContext.getSecureRandom(), byteArray, sessionId.getId());
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("Produced NewSessionTicket handshake message", newSessionTicketMessage);
            }
            final SSLSessionImpl sslSessionImpl = new SSLSessionImpl(serverHandshakeContext.handshakeSession, sessionId);
            serverHandshakeContext.handshakeSession.addChild(sslSessionImpl);
            sslSessionImpl.setPreSharedKey(access$300);
            sslSessionImpl.setPskIdentity(sessionId.getId());
            sslSessionImpl.setTicketAgeAdd(newSessionTicketMessage.ticketAgeAdd);
            sslSessionContextImpl.put(sslSessionImpl);
            newSessionTicketMessage.write(serverHandshakeContext.handshakeOutput);
            serverHandshakeContext.handshakeOutput.flush();
            return null;
        }
    }
    
    private static final class NewSessionTicketProducer implements HandshakeProducer
    {
        @Override
        public byte[] produce(final ConnectionContext connectionContext, final SSLHandshake.HandshakeMessage handshakeMessage) throws IOException {
            throw new ProviderException("NewSessionTicket handshake producer not implemented");
        }
    }
    
    private static final class NewSessionTicketConsumer implements SSLConsumer
    {
        @Override
        public void consume(final ConnectionContext connectionContext, final ByteBuffer byteBuffer) throws IOException {
            final HandshakeContext handshakeContext = (HandshakeContext)connectionContext;
            final NewSessionTicketMessage newSessionTicketMessage = new NewSessionTicketMessage(handshakeContext, byteBuffer);
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("Consuming NewSessionTicket message", newSessionTicketMessage);
            }
            if (newSessionTicketMessage.ticketLifetime <= 0 || newSessionTicketMessage.ticketLifetime > 604800) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("Discarding NewSessionTicket with lifetime " + newSessionTicketMessage.ticketLifetime, newSessionTicketMessage);
                }
                return;
            }
            final SSLSessionContextImpl sslSessionContextImpl = (SSLSessionContextImpl)handshakeContext.sslContext.engineGetClientSessionContext();
            if (sslSessionContextImpl.getSessionTimeout() > 604800) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("Session cache lifetime is too long. Discarding ticket.", new Object[0]);
                }
                return;
            }
            final SSLSessionImpl conSession = handshakeContext.conContext.conSession;
            final SecretKey resumptionMasterSecret = conSession.getResumptionMasterSecret();
            if (resumptionMasterSecret == null) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("Session has no resumption master secret. Ignoring ticket.", new Object[0]);
                }
                return;
            }
            final SecretKey access$300 = derivePreSharedKey(conSession.getSuite().hashAlg, resumptionMasterSecret, newSessionTicketMessage.ticketNonce);
            final SSLSessionImpl sslSessionImpl = new SSLSessionImpl(conSession, new SessionId(true, handshakeContext.sslContext.getSecureRandom()));
            conSession.addChild(sslSessionImpl);
            sslSessionImpl.setPreSharedKey(access$300);
            sslSessionImpl.setTicketAgeAdd(newSessionTicketMessage.ticketAgeAdd);
            sslSessionImpl.setPskIdentity(newSessionTicketMessage.ticket);
            sslSessionContextImpl.put(sslSessionImpl);
            handshakeContext.conContext.finishPostHandshake();
        }
    }
}
