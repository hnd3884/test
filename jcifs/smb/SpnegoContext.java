package jcifs.smb;

import java.io.IOException;
import jcifs.spnego.SpnegoToken;
import jcifs.spnego.NegTokenTarg;
import jcifs.spnego.NegTokenInit;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.Oid;
import org.ietf.jgss.GSSContext;

class SpnegoContext
{
    private GSSContext context;
    private Oid[] mechs;
    
    SpnegoContext(final GSSContext source) throws GSSException {
        this(source, new Oid[] { source.getMech() });
    }
    
    SpnegoContext(final GSSContext source, final Oid[] mech) {
        this.context = source;
        this.mechs = mech;
    }
    
    Oid[] getMechs() {
        return this.mechs;
    }
    
    void setMechs(final Oid[] mechs) {
        this.mechs = mechs;
    }
    
    GSSContext getGssContext() {
        return this.context;
    }
    
    byte[] initSecContext(final byte[] inputBuf, final int offset, final int len) throws GSSException {
        byte[] ret = null;
        if (len == 0) {
            final byte[] mechToken = this.context.initSecContext(inputBuf, offset, len);
            int contextFlags = 0;
            if (this.context.getCredDelegState()) {
                contextFlags |= 0x40;
            }
            if (this.context.getMutualAuthState()) {
                contextFlags |= 0x20;
            }
            if (this.context.getReplayDetState()) {
                contextFlags |= 0x10;
            }
            if (this.context.getSequenceDetState()) {
                contextFlags |= 0x8;
            }
            if (this.context.getAnonymityState()) {
                contextFlags |= 0x4;
            }
            if (this.context.getConfState()) {
                contextFlags |= 0x2;
            }
            if (this.context.getIntegState()) {
                contextFlags |= 0x1;
            }
            ret = new NegTokenInit(new String[] { this.context.getMech().toString() }, contextFlags, mechToken, null).toByteArray();
        }
        else {
            final SpnegoToken spToken = this.getToken(inputBuf, offset, len);
            byte[] mechToken2 = spToken.getMechanismToken();
            mechToken2 = this.context.initSecContext(mechToken2, 0, mechToken2.length);
            if (mechToken2 != null) {
                int result = 1;
                if (this.context.isEstablished()) {
                    result = 0;
                }
                ret = new NegTokenTarg(result, this.context.getMech().toString(), mechToken2, null).toByteArray();
            }
        }
        return ret;
    }
    
    public boolean isEstablished() {
        return this.context.isEstablished();
    }
    
    private SpnegoToken getToken(final byte[] token, final int off, final int len) throws GSSException {
        byte[] b = new byte[len];
        if (off == 0 && token.length == len) {
            b = token;
        }
        else {
            System.arraycopy(token, off, b, 0, len);
        }
        return this.getToken(b);
    }
    
    private SpnegoToken getToken(final byte[] token) throws GSSException {
        SpnegoToken spnegoToken = null;
        try {
            switch (token[0]) {
                case 96: {
                    spnegoToken = new NegTokenInit(token);
                    break;
                }
                case -95: {
                    spnegoToken = new NegTokenTarg(token);
                    break;
                }
                default: {
                    throw new GSSException(10);
                }
            }
            return spnegoToken;
        }
        catch (final IOException e) {
            throw new GSSException(11);
        }
    }
}
