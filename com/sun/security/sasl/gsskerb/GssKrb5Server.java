package com.sun.security.sasl.gsskerb;

import javax.security.auth.callback.UnsupportedCallbackException;
import java.io.IOException;
import javax.security.auth.callback.Callback;
import javax.security.sasl.AuthorizeCallback;
import java.io.UnsupportedEncodingException;
import org.ietf.jgss.MessageProp;
import com.sun.security.sasl.util.AbstractSaslImpl;
import org.ietf.jgss.GSSException;
import javax.security.sasl.SaslException;
import org.ietf.jgss.GSSName;
import org.ietf.jgss.GSSManager;
import java.util.logging.Level;
import java.util.Map;
import javax.security.auth.callback.CallbackHandler;
import javax.security.sasl.SaslServer;

final class GssKrb5Server extends GssKrb5Base implements SaslServer
{
    private static final String MY_CLASS_NAME;
    private int handshakeStage;
    private String peer;
    private String me;
    private String authzid;
    private CallbackHandler cbh;
    private final String protocolSaved;
    
    GssKrb5Server(final String protocolSaved, final String s, final Map<String, ?> map, final CallbackHandler cbh) throws SaslException {
        super(map, GssKrb5Server.MY_CLASS_NAME);
        this.handshakeStage = 0;
        this.cbh = cbh;
        String string;
        if (s == null) {
            this.protocolSaved = protocolSaved;
            string = null;
        }
        else {
            this.protocolSaved = null;
            string = protocolSaved + "@" + s;
        }
        GssKrb5Server.logger.log(Level.FINE, "KRB5SRV01:Using service name: {0}", string);
        try {
            final GSSManager instance = GSSManager.getInstance();
            this.secCtx = instance.createContext(instance.createCredential((string == null) ? null : instance.createName(string, GSSName.NT_HOSTBASED_SERVICE, GssKrb5Server.KRB5_OID), Integer.MAX_VALUE, GssKrb5Server.KRB5_OID, 2));
            if ((this.allQop & 0x2) != 0x0) {
                this.secCtx.requestInteg(true);
            }
            if ((this.allQop & 0x4) != 0x0) {
                this.secCtx.requestConf(true);
            }
        }
        catch (final GSSException ex) {
            throw new SaslException("Failure to initialize security context", ex);
        }
        GssKrb5Server.logger.log(Level.FINE, "KRB5SRV02:Initialization complete");
    }
    
    @Override
    public byte[] evaluateResponse(final byte[] array) throws SaslException {
        if (this.completed) {
            throw new SaslException("SASL authentication already complete");
        }
        if (GssKrb5Server.logger.isLoggable(Level.FINER)) {
            AbstractSaslImpl.traceOutput(GssKrb5Server.MY_CLASS_NAME, "evaluateResponse", "KRB5SRV03:Response [raw]:", array);
        }
        switch (this.handshakeStage) {
            case 1: {
                return this.doHandshake1(array);
            }
            case 2: {
                return this.doHandshake2(array);
            }
            default: {
                try {
                    final byte[] acceptSecContext = this.secCtx.acceptSecContext(array, 0, array.length);
                    if (GssKrb5Server.logger.isLoggable(Level.FINER)) {
                        AbstractSaslImpl.traceOutput(GssKrb5Server.MY_CLASS_NAME, "evaluateResponse", "KRB5SRV04:Challenge: [after acceptSecCtx]", acceptSecContext);
                    }
                    if (this.secCtx.isEstablished()) {
                        this.handshakeStage = 1;
                        this.peer = this.secCtx.getSrcName().toString();
                        this.me = this.secCtx.getTargName().toString();
                        GssKrb5Server.logger.log(Level.FINE, "KRB5SRV05:Peer name is : {0}, my name is : {1}", new Object[] { this.peer, this.me });
                        if (this.protocolSaved != null && !this.protocolSaved.equalsIgnoreCase(this.me.split("[/@]")[0])) {
                            throw new SaslException("GSS context targ name protocol error: " + this.me);
                        }
                        if (acceptSecContext == null) {
                            return this.doHandshake1(GssKrb5Server.EMPTY);
                        }
                    }
                    return acceptSecContext;
                }
                catch (final GSSException ex) {
                    throw new SaslException("GSS initiate failed", ex);
                }
                break;
            }
        }
    }
    
    private byte[] doHandshake1(final byte[] array) throws SaslException {
        try {
            if (array != null && array.length > 0) {
                throw new SaslException("Handshake expecting no response data from server");
            }
            final byte[] array2 = new byte[4];
            array2[0] = this.allQop;
            AbstractSaslImpl.intToNetworkByteOrder(this.recvMaxBufSize, array2, 1, 3);
            if (GssKrb5Server.logger.isLoggable(Level.FINE)) {
                GssKrb5Server.logger.log(Level.FINE, "KRB5SRV06:Supported protections: {0}; recv max buf size: {1}", new Object[] { new Byte(this.allQop), new Integer(this.recvMaxBufSize) });
            }
            this.handshakeStage = 2;
            if (GssKrb5Server.logger.isLoggable(Level.FINER)) {
                AbstractSaslImpl.traceOutput(GssKrb5Server.MY_CLASS_NAME, "doHandshake1", "KRB5SRV07:Challenge [raw]", array2);
            }
            final byte[] wrap = this.secCtx.wrap(array2, 0, array2.length, new MessageProp(0, false));
            if (GssKrb5Server.logger.isLoggable(Level.FINER)) {
                AbstractSaslImpl.traceOutput(GssKrb5Server.MY_CLASS_NAME, "doHandshake1", "KRB5SRV08:Challenge [after wrap]", wrap);
            }
            return wrap;
        }
        catch (final GSSException ex) {
            throw new SaslException("Problem wrapping handshake1", ex);
        }
    }
    
    private byte[] doHandshake2(final byte[] array) throws SaslException {
        try {
            final MessageProp messageProp = new MessageProp(false);
            final byte[] unwrap = this.secCtx.unwrap(array, 0, array.length, messageProp);
            this.checkMessageProp("Handshake failure: ", messageProp);
            if (GssKrb5Server.logger.isLoggable(Level.FINER)) {
                AbstractSaslImpl.traceOutput(GssKrb5Server.MY_CLASS_NAME, "doHandshake2", "KRB5SRV09:Response [after unwrap]", unwrap);
            }
            final byte b = unwrap[0];
            if ((b & this.allQop) == 0x0) {
                throw new SaslException("Client selected unsupported protection: " + b);
            }
            if ((b & 0x4) != 0x0) {
                this.privacy = true;
                this.integrity = true;
            }
            else if ((b & 0x2) != 0x0) {
                this.integrity = true;
            }
            final int networkByteOrderToInt = AbstractSaslImpl.networkByteOrderToInt(unwrap, 1, 3);
            this.sendMaxBufSize = ((this.sendMaxBufSize == 0) ? networkByteOrderToInt : Math.min(this.sendMaxBufSize, networkByteOrderToInt));
            this.rawSendSize = this.secCtx.getWrapSizeLimit(0, this.privacy, this.sendMaxBufSize);
            if (GssKrb5Server.logger.isLoggable(Level.FINE)) {
                GssKrb5Server.logger.log(Level.FINE, "KRB5SRV10:Selected protection: {0}; privacy: {1}; integrity: {2}", new Object[] { new Byte(b), this.privacy, this.integrity });
                GssKrb5Server.logger.log(Level.FINE, "KRB5SRV11:Client max recv size: {0}; server max send size: {1}; rawSendSize: {2}", new Object[] { new Integer(networkByteOrderToInt), new Integer(this.sendMaxBufSize), new Integer(this.rawSendSize) });
            }
            Label_0348: {
                if (unwrap.length > 4) {
                    try {
                        this.authzid = new String(unwrap, 4, unwrap.length - 4, "UTF-8");
                        break Label_0348;
                    }
                    catch (final UnsupportedEncodingException ex) {
                        throw new SaslException("Cannot decode authzid", ex);
                    }
                }
                this.authzid = this.peer;
            }
            GssKrb5Server.logger.log(Level.FINE, "KRB5SRV12:Authzid: {0}", this.authzid);
            final AuthorizeCallback authorizeCallback = new AuthorizeCallback(this.peer, this.authzid);
            this.cbh.handle(new Callback[] { authorizeCallback });
            if (authorizeCallback.isAuthorized()) {
                this.authzid = authorizeCallback.getAuthorizedID();
                this.completed = true;
                return null;
            }
            throw new SaslException(this.peer + " is not authorized to connect as " + this.authzid);
        }
        catch (final GSSException ex2) {
            throw new SaslException("Final handshake step failed", ex2);
        }
        catch (final IOException ex3) {
            throw new SaslException("Problem with callback handler", ex3);
        }
        catch (final UnsupportedCallbackException ex4) {
            throw new SaslException("Problem with callback handler", ex4);
        }
    }
    
    @Override
    public String getAuthorizationID() {
        if (this.completed) {
            return this.authzid;
        }
        throw new IllegalStateException("Authentication incomplete");
    }
    
    @Override
    public Object getNegotiatedProperty(final String s) {
        if (!this.completed) {
            throw new IllegalStateException("Authentication incomplete");
        }
        Object negotiatedProperty = null;
        switch (s) {
            case "javax.security.sasl.bound.server.name": {
                try {
                    negotiatedProperty = this.me.split("[/@]")[1];
                }
                catch (final Exception ex) {
                    negotiatedProperty = null;
                }
                break;
            }
            default: {
                negotiatedProperty = super.getNegotiatedProperty(s);
                break;
            }
        }
        return negotiatedProperty;
    }
    
    static {
        MY_CLASS_NAME = GssKrb5Server.class.getName();
    }
}
