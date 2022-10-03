package sun.security.jgss.spnego;

import java.io.IOException;
import sun.security.util.DerValue;
import sun.security.util.DerOutputStream;
import org.ietf.jgss.GSSException;
import sun.security.util.ObjectIdentifier;
import sun.security.jgss.GSSToken;

abstract class SpNegoToken extends GSSToken
{
    static final int NEG_TOKEN_INIT_ID = 0;
    static final int NEG_TOKEN_TARG_ID = 1;
    private int tokenType;
    static final boolean DEBUG;
    public static ObjectIdentifier OID;
    
    protected SpNegoToken(final int tokenType) {
        this.tokenType = tokenType;
    }
    
    abstract byte[] encode() throws GSSException;
    
    byte[] getEncoded() throws IOException, GSSException {
        final DerOutputStream derOutputStream = new DerOutputStream();
        derOutputStream.write(this.encode());
        switch (this.tokenType) {
            case 0: {
                final DerOutputStream derOutputStream2 = new DerOutputStream();
                derOutputStream2.write(DerValue.createTag((byte)(-128), true, (byte)0), derOutputStream);
                return derOutputStream2.toByteArray();
            }
            case 1: {
                final DerOutputStream derOutputStream3 = new DerOutputStream();
                derOutputStream3.write(DerValue.createTag((byte)(-128), true, (byte)1), derOutputStream);
                return derOutputStream3.toByteArray();
            }
            default: {
                return derOutputStream.toByteArray();
            }
        }
    }
    
    final int getType() {
        return this.tokenType;
    }
    
    static String getTokenName(final int n) {
        switch (n) {
            case 0: {
                return "SPNEGO NegTokenInit";
            }
            case 1: {
                return "SPNEGO NegTokenTarg";
            }
            default: {
                return "SPNEGO Mechanism Token";
            }
        }
    }
    
    static NegoResult getNegoResultType(final int n) {
        switch (n) {
            case 0: {
                return NegoResult.ACCEPT_COMPLETE;
            }
            case 1: {
                return NegoResult.ACCEPT_INCOMPLETE;
            }
            case 2: {
                return NegoResult.REJECT;
            }
            default: {
                return NegoResult.ACCEPT_COMPLETE;
            }
        }
    }
    
    static String getNegoResultString(final int n) {
        switch (n) {
            case 0: {
                return "Accept Complete";
            }
            case 1: {
                return "Accept InComplete";
            }
            case 2: {
                return "Reject";
            }
            default: {
                return "Unknown Negotiated Result: " + n;
            }
        }
    }
    
    static int checkNextField(final int n, final int n2) throws GSSException {
        if (n < n2) {
            return n2;
        }
        throw new GSSException(10, -1, "Invalid SpNegoToken token : wrong order");
    }
    
    static {
        DEBUG = SpNegoContext.DEBUG;
        try {
            SpNegoToken.OID = new ObjectIdentifier(SpNegoMechFactory.GSS_SPNEGO_MECH_OID.toString());
        }
        catch (final IOException ex) {}
    }
    
    enum NegoResult
    {
        ACCEPT_COMPLETE, 
        ACCEPT_INCOMPLETE, 
        REJECT;
    }
}
