package org.openjsse.sun.security.ssl;

import java.security.ProviderException;
import java.math.BigInteger;
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
    
    private static SecretKey derivePreSharedKey(final CipherSuite.HashAlg hashAlg, final SecretKey resumptionMasterSecret, final byte[] nonce) throws IOException {
        try {
            final HKDF hkdf = new HKDF(hashAlg.name);
            final byte[] hkdfInfo = SSLSecretDerivation.createHkdfInfo("tls13 resumption".getBytes(), nonce, hashAlg.hashLength);
            return hkdf.expand(resumptionMasterSecret, hkdfInfo, hashAlg.hashLength, "TlsPreSharedKey");
        }
        catch (final GeneralSecurityException gse) {
            throw (SSLHandshakeException)new SSLHandshakeException("Could not derive PSK").initCause(gse);
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
        
        NewSessionTicketMessage(final HandshakeContext context, final int ticketLifetime, final SecureRandom generator, final byte[] ticketNonce, final byte[] ticket) {
            super(context);
            this.ticketLifetime = ticketLifetime;
            this.ticketAgeAdd = generator.nextInt();
            this.ticketNonce = ticketNonce;
            this.ticket = ticket;
            this.extensions = new SSLExtensions(this);
        }
        
        NewSessionTicketMessage(final HandshakeContext context, final ByteBuffer m) throws IOException {
            super(context);
            if (m.remaining() < 14) {
                throw context.conContext.fatal(Alert.ILLEGAL_PARAMETER, "Invalid NewSessionTicket message: no sufficient data");
            }
            this.ticketLifetime = Record.getInt32(m);
            this.ticketAgeAdd = Record.getInt32(m);
            this.ticketNonce = Record.getBytes8(m);
            if (m.remaining() < 5) {
                throw context.conContext.fatal(Alert.ILLEGAL_PARAMETER, "Invalid NewSessionTicket message: no sufficient data");
            }
            this.ticket = Record.getBytes16(m);
            if (this.ticket.length == 0) {
                throw context.conContext.fatal(Alert.ILLEGAL_PARAMETER, "No ticket in the NewSessionTicket handshake message");
            }
            if (m.remaining() < 2) {
                throw context.conContext.fatal(Alert.ILLEGAL_PARAMETER, "Invalid NewSessionTicket message: no sufficient data");
            }
            final SSLExtension[] supportedExtensions = context.sslConfig.getEnabledExtensions(SSLHandshake.NEW_SESSION_TICKET);
            this.extensions = new SSLExtensions(this, m, supportedExtensions);
        }
        
        public SSLHandshake handshakeType() {
            return SSLHandshake.NEW_SESSION_TICKET;
        }
        
        public int messageLength() {
            int extLen = this.extensions.length();
            if (extLen == 0) {
                extLen = 2;
            }
            return 8 + this.ticketNonce.length + 1 + this.ticket.length + 2 + extLen;
        }
        
        public void send(final HandshakeOutStream hos) throws IOException {
            hos.putInt32(this.ticketLifetime);
            hos.putInt32(this.ticketAgeAdd);
            hos.putBytes8(this.ticketNonce);
            hos.putBytes16(this.ticket);
            if (this.extensions.length() == 0) {
                hos.putInt16(0);
            }
            else {
                this.extensions.send(hos);
            }
        }
        
        @Override
        public String toString() {
            final MessageFormat messageFormat = new MessageFormat("\"NewSessionTicket\": '{'\n  \"ticket_lifetime\"      : \"{0}\",\n  \"ticket_age_add\"       : \"{1}\",\n  \"ticket_nonce\"         : \"{2}\",\n  \"ticket\"               : \"{3}\",\n  \"extensions\"           : [\n{4}\n  ]\n'}'", Locale.ENGLISH);
            final Object[] messageFields = { this.ticketLifetime, "<omitted>", Utilities.toHexString(this.ticketNonce), Utilities.toHexString(this.ticket), Utilities.indent(this.extensions.toString(), "    ") };
            return messageFormat.format(messageFields);
        }
    }
    
    private static final class NewSessionTicketKickstartProducer implements SSLProducer
    {
        @Override
        public byte[] produce(final ConnectionContext context) throws IOException {
            final ServerHandshakeContext shc = (ServerHandshakeContext)context;
            if (!shc.handshakeSession.isRejoinable()) {
                return null;
            }
            final PskKeyExchangeModesExtension.PskKeyExchangeModesSpec pkemSpec = shc.handshakeExtensions.get(SSLExtension.PSK_KEY_EXCHANGE_MODES);
            if (pkemSpec == null || !pkemSpec.contains(PskKeyExchangeModesExtension.PskKeyExchangeMode.PSK_DHE_KE)) {
                return null;
            }
            final SSLSessionContextImpl sessionCache = (SSLSessionContextImpl)shc.sslContext.engineGetServerSessionContext();
            final SessionId newId = new SessionId(true, shc.sslContext.getSecureRandom());
            final SecretKey resumptionMasterSecret = shc.handshakeSession.getResumptionMasterSecret();
            if (resumptionMasterSecret == null) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("Session has no resumption secret. No ticket sent.", new Object[0]);
                }
                return null;
            }
            final BigInteger nonce = shc.handshakeSession.incrTicketNonceCounter();
            final byte[] nonceArr = nonce.toByteArray();
            final SecretKey psk = derivePreSharedKey(shc.negotiatedCipherSuite.hashAlg, resumptionMasterSecret, nonceArr);
            final int sessionTimeoutSeconds = sessionCache.getSessionTimeout();
            if (sessionTimeoutSeconds > 604800) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("Session timeout is too long. No ticket sent.", new Object[0]);
                }
                return null;
            }
            final NewSessionTicketMessage nstm = new NewSessionTicketMessage(shc, sessionTimeoutSeconds, shc.sslContext.getSecureRandom(), nonceArr, newId.getId());
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("Produced NewSessionTicket handshake message", nstm);
            }
            final SSLSessionImpl sessionCopy = new SSLSessionImpl(shc.handshakeSession, newId);
            shc.handshakeSession.addChild(sessionCopy);
            sessionCopy.setPreSharedKey(psk);
            sessionCopy.setPskIdentity(newId.getId());
            sessionCopy.setTicketAgeAdd(nstm.ticketAgeAdd);
            sessionCache.put(sessionCopy);
            nstm.write(shc.handshakeOutput);
            shc.handshakeOutput.flush();
            return null;
        }
    }
    
    private static final class NewSessionTicketProducer implements HandshakeProducer
    {
        @Override
        public byte[] produce(final ConnectionContext context, final SSLHandshake.HandshakeMessage message) throws IOException {
            throw new ProviderException("NewSessionTicket handshake producer not implemented");
        }
    }
    
    private static final class NewSessionTicketConsumer implements SSLConsumer
    {
        @Override
        public void consume(final ConnectionContext context, final ByteBuffer message) throws IOException {
            final HandshakeContext hc = (HandshakeContext)context;
            final NewSessionTicketMessage nstm = new NewSessionTicketMessage(hc, message);
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("Consuming NewSessionTicket message", nstm);
            }
            if (nstm.ticketLifetime <= 0 || nstm.ticketLifetime > 604800) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("Discarding NewSessionTicket with lifetime " + nstm.ticketLifetime, nstm);
                }
                return;
            }
            final SSLSessionContextImpl sessionCache = (SSLSessionContextImpl)hc.sslContext.engineGetClientSessionContext();
            if (sessionCache.getSessionTimeout() > 604800) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("Session cache lifetime is too long. Discarding ticket.", new Object[0]);
                }
                return;
            }
            final SSLSessionImpl sessionToSave = hc.conContext.conSession;
            final SecretKey resumptionMasterSecret = sessionToSave.getResumptionMasterSecret();
            if (resumptionMasterSecret == null) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("Session has no resumption master secret. Ignoring ticket.", new Object[0]);
                }
                return;
            }
            final SecretKey psk = derivePreSharedKey(sessionToSave.getSuite().hashAlg, resumptionMasterSecret, nstm.ticketNonce);
            final SessionId newId = new SessionId(true, hc.sslContext.getSecureRandom());
            final SSLSessionImpl sessionCopy = new SSLSessionImpl(sessionToSave, newId);
            sessionToSave.addChild(sessionCopy);
            sessionCopy.setPreSharedKey(psk);
            sessionCopy.setTicketAgeAdd(nstm.ticketAgeAdd);
            sessionCopy.setPskIdentity(nstm.ticket);
            sessionCache.put(sessionCopy);
            hc.conContext.finishPostHandshake();
        }
    }
}
