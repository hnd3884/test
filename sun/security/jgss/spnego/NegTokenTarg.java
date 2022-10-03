package sun.security.jgss.spnego;

import sun.security.jgss.GSSToken;
import java.io.IOException;
import sun.security.jgss.GSSUtil;
import sun.security.util.DerValue;
import sun.security.util.DerOutputStream;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.Oid;

public class NegTokenTarg extends SpNegoToken
{
    private int negResult;
    private Oid supportedMech;
    private byte[] responseToken;
    private byte[] mechListMIC;
    
    NegTokenTarg(final int negResult, final Oid supportedMech, final byte[] responseToken, final byte[] mechListMIC) {
        super(1);
        this.negResult = 0;
        this.supportedMech = null;
        this.responseToken = null;
        this.mechListMIC = null;
        this.negResult = negResult;
        this.supportedMech = supportedMech;
        this.responseToken = responseToken;
        this.mechListMIC = mechListMIC;
    }
    
    public NegTokenTarg(final byte[] array) throws GSSException {
        super(1);
        this.negResult = 0;
        this.supportedMech = null;
        this.responseToken = null;
        this.mechListMIC = null;
        this.parseToken(array);
    }
    
    @Override
    final byte[] encode() throws GSSException {
        try {
            final DerOutputStream derOutputStream = new DerOutputStream();
            final DerOutputStream derOutputStream2 = new DerOutputStream();
            derOutputStream2.putEnumerated(this.negResult);
            derOutputStream.write(DerValue.createTag((byte)(-128), true, (byte)0), derOutputStream2);
            if (this.supportedMech != null) {
                final DerOutputStream derOutputStream3 = new DerOutputStream();
                derOutputStream3.write(this.supportedMech.getDER());
                derOutputStream.write(DerValue.createTag((byte)(-128), true, (byte)1), derOutputStream3);
            }
            if (this.responseToken != null) {
                final DerOutputStream derOutputStream4 = new DerOutputStream();
                derOutputStream4.putOctetString(this.responseToken);
                derOutputStream.write(DerValue.createTag((byte)(-128), true, (byte)2), derOutputStream4);
            }
            if (this.mechListMIC != null) {
                if (NegTokenTarg.DEBUG) {
                    System.out.println("SpNegoToken NegTokenTarg: sending MechListMIC");
                }
                final DerOutputStream derOutputStream5 = new DerOutputStream();
                derOutputStream5.putOctetString(this.mechListMIC);
                derOutputStream.write(DerValue.createTag((byte)(-128), true, (byte)3), derOutputStream5);
            }
            else if (GSSUtil.useMSInterop() && this.responseToken != null) {
                if (NegTokenTarg.DEBUG) {
                    System.out.println("SpNegoToken NegTokenTarg: sending additional token for MS Interop");
                }
                final DerOutputStream derOutputStream6 = new DerOutputStream();
                derOutputStream6.putOctetString(this.responseToken);
                derOutputStream.write(DerValue.createTag((byte)(-128), true, (byte)3), derOutputStream6);
            }
            final DerOutputStream derOutputStream7 = new DerOutputStream();
            derOutputStream7.write((byte)48, derOutputStream);
            return derOutputStream7.toByteArray();
        }
        catch (final IOException ex) {
            throw new GSSException(10, -1, "Invalid SPNEGO NegTokenTarg token : " + ex.getMessage());
        }
    }
    
    private void parseToken(final byte[] array) throws GSSException {
        try {
            final DerValue derValue = new DerValue(array);
            if (!derValue.isContextSpecific((byte)1)) {
                throw new IOException("SPNEGO NegoTokenTarg : did not have the right token type");
            }
            final DerValue derValue2 = derValue.data.getDerValue();
            if (derValue2.tag != 48) {
                throw new IOException("SPNEGO NegoTokenTarg : did not have the Sequence tag");
            }
            int n = -1;
            while (derValue2.data.available() > 0) {
                final DerValue derValue3 = derValue2.data.getDerValue();
                if (derValue3.isContextSpecific((byte)0)) {
                    n = SpNegoToken.checkNextField(n, 0);
                    this.negResult = derValue3.data.getEnumerated();
                    if (!NegTokenTarg.DEBUG) {
                        continue;
                    }
                    System.out.println("SpNegoToken NegTokenTarg: negotiated result = " + SpNegoToken.getNegoResultString(this.negResult));
                }
                else if (derValue3.isContextSpecific((byte)1)) {
                    n = SpNegoToken.checkNextField(n, 1);
                    this.supportedMech = new Oid(derValue3.data.getOID().toString());
                    if (!NegTokenTarg.DEBUG) {
                        continue;
                    }
                    System.out.println("SpNegoToken NegTokenTarg: supported mechanism = " + this.supportedMech);
                }
                else if (derValue3.isContextSpecific((byte)2)) {
                    n = SpNegoToken.checkNextField(n, 2);
                    this.responseToken = derValue3.data.getOctetString();
                }
                else {
                    if (!derValue3.isContextSpecific((byte)3)) {
                        continue;
                    }
                    n = SpNegoToken.checkNextField(n, 3);
                    if (GSSUtil.useMSInterop()) {
                        continue;
                    }
                    this.mechListMIC = derValue3.data.getOctetString();
                    if (!NegTokenTarg.DEBUG) {
                        continue;
                    }
                    System.out.println("SpNegoToken NegTokenTarg: MechListMIC Token = " + GSSToken.getHexBytes(this.mechListMIC));
                }
            }
        }
        catch (final IOException ex) {
            throw new GSSException(10, -1, "Invalid SPNEGO NegTokenTarg token : " + ex.getMessage());
        }
    }
    
    int getNegotiatedResult() {
        return this.negResult;
    }
    
    public Oid getSupportedMech() {
        return this.supportedMech;
    }
    
    byte[] getResponseToken() {
        return this.responseToken;
    }
    
    byte[] getMechListMIC() {
        return this.mechListMIC;
    }
}
