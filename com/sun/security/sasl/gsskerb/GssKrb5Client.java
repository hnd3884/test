package com.sun.security.sasl.gsskerb;

import org.ietf.jgss.MessageProp;
import com.sun.security.sasl.util.AbstractSaslImpl;
import java.io.IOException;
import org.ietf.jgss.GSSException;
import javax.security.sasl.SaslException;
import org.ietf.jgss.ChannelBinding;
import sun.security.jgss.krb5.TlsChannelBindingImpl;
import org.ietf.jgss.GSSCredential;
import org.ietf.jgss.GSSName;
import org.ietf.jgss.GSSManager;
import java.util.logging.Level;
import javax.security.auth.callback.CallbackHandler;
import java.util.Map;
import javax.security.sasl.SaslClient;

final class GssKrb5Client extends GssKrb5Base implements SaslClient
{
    private static final String MY_CLASS_NAME;
    private boolean finalHandshake;
    private boolean mutual;
    private byte[] authzID;
    
    GssKrb5Client(final String s, final String s2, final String s3, final Map<String, ?> map, final CallbackHandler callbackHandler) throws SaslException {
        super(map, GssKrb5Client.MY_CLASS_NAME);
        this.finalHandshake = false;
        this.mutual = false;
        final String string = s2 + "@" + s3;
        GssKrb5Client.logger.log(Level.FINE, "KRB5CLNT01:Requesting service name: {0}", string);
        try {
            final GSSManager instance = GSSManager.getInstance();
            final GSSName name = instance.createName(string, GSSName.NT_HOSTBASED_SERVICE, GssKrb5Client.KRB5_OID);
            GSSCredential gssCredential = null;
            if (map != null) {
                final Object value = map.get("javax.security.sasl.credentials");
                if (value != null && value instanceof GSSCredential) {
                    gssCredential = (GSSCredential)value;
                    GssKrb5Client.logger.log(Level.FINE, "KRB5CLNT01:Using the credentials supplied in javax.security.sasl.credentials");
                }
            }
            this.secCtx = instance.createContext(name, GssKrb5Client.KRB5_OID, gssCredential, Integer.MAX_VALUE);
            if (gssCredential != null) {
                this.secCtx.requestCredDeleg(true);
            }
            if (map != null) {
                final String s4 = (String)map.get("javax.security.sasl.server.authentication");
                if (s4 != null) {
                    this.mutual = "true".equalsIgnoreCase(s4);
                }
            }
            this.secCtx.requestMutualAuth(this.mutual);
            if (map != null) {
                final byte[] array = (Object)map.get("jdk.internal.sasl.tlschannelbinding");
                if (array != null) {
                    this.secCtx.setChannelBinding(new TlsChannelBindingImpl(array));
                }
            }
            this.secCtx.requestConf(true);
            this.secCtx.requestInteg(true);
        }
        catch (final GSSException ex) {
            throw new SaslException("Failure to initialize security context", ex);
        }
        if (s != null && s.length() > 0) {
            try {
                this.authzID = s.getBytes("UTF8");
            }
            catch (final IOException ex2) {
                throw new SaslException("Cannot encode authorization ID", ex2);
            }
        }
    }
    
    @Override
    public boolean hasInitialResponse() {
        return true;
    }
    
    @Override
    public byte[] evaluateChallenge(final byte[] array) throws SaslException {
        if (this.completed) {
            throw new IllegalStateException("GSSAPI authentication already complete");
        }
        if (this.finalHandshake) {
            return this.doFinalHandshake(array);
        }
        try {
            final byte[] initSecContext = this.secCtx.initSecContext(array, 0, array.length);
            if (GssKrb5Client.logger.isLoggable(Level.FINER)) {
                AbstractSaslImpl.traceOutput(GssKrb5Client.MY_CLASS_NAME, "evaluteChallenge", "KRB5CLNT02:Challenge: [raw]", array);
                AbstractSaslImpl.traceOutput(GssKrb5Client.MY_CLASS_NAME, "evaluateChallenge", "KRB5CLNT03:Response: [after initSecCtx]", initSecContext);
            }
            if (this.secCtx.isEstablished()) {
                this.finalHandshake = true;
                if (initSecContext == null) {
                    return GssKrb5Client.EMPTY;
                }
            }
            return initSecContext;
        }
        catch (final GSSException ex) {
            throw new SaslException("GSS initiate failed", ex);
        }
    }
    
    private byte[] doFinalHandshake(final byte[] array) throws SaslException {
        try {
            if (GssKrb5Client.logger.isLoggable(Level.FINER)) {
                AbstractSaslImpl.traceOutput(GssKrb5Client.MY_CLASS_NAME, "doFinalHandshake", "KRB5CLNT04:Challenge [raw]:", array);
            }
            if (array.length == 0) {
                return GssKrb5Client.EMPTY;
            }
            final MessageProp messageProp = new MessageProp(false);
            final byte[] unwrap = this.secCtx.unwrap(array, 0, array.length, messageProp);
            this.checkMessageProp("Handshake failure: ", messageProp);
            if (GssKrb5Client.logger.isLoggable(Level.FINE)) {
                if (GssKrb5Client.logger.isLoggable(Level.FINER)) {
                    AbstractSaslImpl.traceOutput(GssKrb5Client.MY_CLASS_NAME, "doFinalHandshake", "KRB5CLNT05:Challenge [unwrapped]:", unwrap);
                }
                GssKrb5Client.logger.log(Level.FINE, "KRB5CLNT06:Server protections: {0}", new Byte(unwrap[0]));
            }
            final byte preferredMask = AbstractSaslImpl.findPreferredMask(unwrap[0], this.qop);
            if (preferredMask == 0) {
                throw new SaslException("No common protection layer between client and server");
            }
            if ((preferredMask & 0x4) != 0x0) {
                this.privacy = true;
                this.integrity = true;
            }
            else if ((preferredMask & 0x2) != 0x0) {
                this.integrity = true;
            }
            final int networkByteOrderToInt = AbstractSaslImpl.networkByteOrderToInt(unwrap, 1, 3);
            this.sendMaxBufSize = ((this.sendMaxBufSize == 0) ? networkByteOrderToInt : Math.min(this.sendMaxBufSize, networkByteOrderToInt));
            this.rawSendSize = this.secCtx.getWrapSizeLimit(0, this.privacy, this.sendMaxBufSize);
            if (GssKrb5Client.logger.isLoggable(Level.FINE)) {
                GssKrb5Client.logger.log(Level.FINE, "KRB5CLNT07:Client max recv size: {0}; server max recv size: {1}; rawSendSize: {2}", new Object[] { new Integer(this.recvMaxBufSize), new Integer(networkByteOrderToInt), new Integer(this.rawSendSize) });
            }
            int n = 4;
            if (this.authzID != null) {
                n += this.authzID.length;
            }
            final byte[] array2 = new byte[n];
            array2[0] = preferredMask;
            if (GssKrb5Client.logger.isLoggable(Level.FINE)) {
                GssKrb5Client.logger.log(Level.FINE, "KRB5CLNT08:Selected protection: {0}; privacy: {1}; integrity: {2}", new Object[] { new Byte(preferredMask), this.privacy, this.integrity });
            }
            AbstractSaslImpl.intToNetworkByteOrder(this.recvMaxBufSize, array2, 1, 3);
            if (this.authzID != null) {
                System.arraycopy(this.authzID, 0, array2, 4, this.authzID.length);
                GssKrb5Client.logger.log(Level.FINE, "KRB5CLNT09:Authzid: {0}", this.authzID);
            }
            if (GssKrb5Client.logger.isLoggable(Level.FINER)) {
                AbstractSaslImpl.traceOutput(GssKrb5Client.MY_CLASS_NAME, "doFinalHandshake", "KRB5CLNT10:Response [raw]", array2);
            }
            final byte[] wrap = this.secCtx.wrap(array2, 0, array2.length, new MessageProp(0, false));
            if (GssKrb5Client.logger.isLoggable(Level.FINER)) {
                AbstractSaslImpl.traceOutput(GssKrb5Client.MY_CLASS_NAME, "doFinalHandshake", "KRB5CLNT11:Response [after wrap]", wrap);
            }
            this.completed = true;
            return wrap;
        }
        catch (final GSSException ex) {
            throw new SaslException("Final handshake failed", ex);
        }
    }
    
    static {
        MY_CLASS_NAME = GssKrb5Client.class.getName();
    }
}
