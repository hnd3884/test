package com.sun.security.sasl.gsskerb;

import org.ietf.jgss.GSSException;
import java.util.logging.Level;
import org.ietf.jgss.MessageProp;
import javax.security.sasl.SaslException;
import java.util.Map;
import org.ietf.jgss.GSSContext;
import org.ietf.jgss.Oid;
import com.sun.security.sasl.util.AbstractSaslImpl;

abstract class GssKrb5Base extends AbstractSaslImpl
{
    private static final String KRB5_OID_STR = "1.2.840.113554.1.2.2";
    protected static Oid KRB5_OID;
    protected static final byte[] EMPTY;
    protected GSSContext secCtx;
    protected static final int JGSS_QOP = 0;
    
    protected GssKrb5Base(final Map<String, ?> map, final String s) throws SaslException {
        super(map, s);
        this.secCtx = null;
    }
    
    public String getMechanismName() {
        return "GSSAPI";
    }
    
    public byte[] unwrap(final byte[] array, final int n, final int n2) throws SaslException {
        if (!this.completed) {
            throw new IllegalStateException("GSSAPI authentication not completed");
        }
        if (!this.integrity) {
            throw new IllegalStateException("No security layer negotiated");
        }
        try {
            final MessageProp messageProp = new MessageProp(0, false);
            final byte[] unwrap = this.secCtx.unwrap(array, n, n2, messageProp);
            if (this.privacy && !messageProp.getPrivacy()) {
                throw new SaslException("Privacy not protected");
            }
            this.checkMessageProp("", messageProp);
            if (GssKrb5Base.logger.isLoggable(Level.FINEST)) {
                AbstractSaslImpl.traceOutput(this.myClassName, "KRB501:Unwrap", "incoming: ", array, n, n2);
                AbstractSaslImpl.traceOutput(this.myClassName, "KRB502:Unwrap", "unwrapped: ", unwrap, 0, unwrap.length);
            }
            return unwrap;
        }
        catch (final GSSException ex) {
            throw new SaslException("Problems unwrapping SASL buffer", ex);
        }
    }
    
    public byte[] wrap(final byte[] array, final int n, final int n2) throws SaslException {
        if (!this.completed) {
            throw new IllegalStateException("GSSAPI authentication not completed");
        }
        if (!this.integrity) {
            throw new IllegalStateException("No security layer negotiated");
        }
        try {
            final byte[] wrap = this.secCtx.wrap(array, n, n2, new MessageProp(0, this.privacy));
            if (GssKrb5Base.logger.isLoggable(Level.FINEST)) {
                AbstractSaslImpl.traceOutput(this.myClassName, "KRB503:Wrap", "outgoing: ", array, n, n2);
                AbstractSaslImpl.traceOutput(this.myClassName, "KRB504:Wrap", "wrapped: ", wrap, 0, wrap.length);
            }
            return wrap;
        }
        catch (final GSSException ex) {
            throw new SaslException("Problem performing GSS wrap", ex);
        }
    }
    
    public void dispose() throws SaslException {
        if (this.secCtx != null) {
            try {
                this.secCtx.dispose();
            }
            catch (final GSSException ex) {
                throw new SaslException("Problem disposing GSS context", ex);
            }
            this.secCtx = null;
        }
    }
    
    @Override
    protected void finalize() throws Throwable {
        this.dispose();
    }
    
    void checkMessageProp(final String s, final MessageProp messageProp) throws SaslException {
        if (messageProp.isDuplicateToken()) {
            throw new SaslException(s + "Duplicate token");
        }
        if (messageProp.isGapToken()) {
            throw new SaslException(s + "Gap token");
        }
        if (messageProp.isOldToken()) {
            throw new SaslException(s + "Old token");
        }
        if (messageProp.isUnseqToken()) {
            throw new SaslException(s + "Token not in sequence");
        }
    }
    
    static {
        EMPTY = new byte[0];
        try {
            GssKrb5Base.KRB5_OID = new Oid("1.2.840.113554.1.2.2");
        }
        catch (final GSSException ex) {}
    }
}
