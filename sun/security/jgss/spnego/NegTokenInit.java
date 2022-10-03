package sun.security.jgss.spnego;

import sun.security.util.ObjectIdentifier;
import sun.security.util.DerInputStream;
import sun.security.jgss.GSSToken;
import sun.security.jgss.GSSUtil;
import java.io.IOException;
import sun.security.util.DerValue;
import sun.security.util.DerOutputStream;
import org.ietf.jgss.GSSException;
import sun.security.util.BitArray;
import org.ietf.jgss.Oid;

public class NegTokenInit extends SpNegoToken
{
    private byte[] mechTypes;
    private Oid[] mechTypeList;
    private BitArray reqFlags;
    private byte[] mechToken;
    private byte[] mechListMIC;
    
    NegTokenInit(final byte[] mechTypes, final BitArray reqFlags, final byte[] mechToken, final byte[] mechListMIC) {
        super(0);
        this.mechTypes = null;
        this.mechTypeList = null;
        this.reqFlags = null;
        this.mechToken = null;
        this.mechListMIC = null;
        this.mechTypes = mechTypes;
        this.reqFlags = reqFlags;
        this.mechToken = mechToken;
        this.mechListMIC = mechListMIC;
    }
    
    public NegTokenInit(final byte[] array) throws GSSException {
        super(0);
        this.mechTypes = null;
        this.mechTypeList = null;
        this.reqFlags = null;
        this.mechToken = null;
        this.mechListMIC = null;
        this.parseToken(array);
    }
    
    @Override
    final byte[] encode() throws GSSException {
        try {
            final DerOutputStream derOutputStream = new DerOutputStream();
            if (this.mechTypes != null) {
                derOutputStream.write(DerValue.createTag((byte)(-128), true, (byte)0), this.mechTypes);
            }
            if (this.reqFlags != null) {
                final DerOutputStream derOutputStream2 = new DerOutputStream();
                derOutputStream2.putUnalignedBitString(this.reqFlags);
                derOutputStream.write(DerValue.createTag((byte)(-128), true, (byte)1), derOutputStream2);
            }
            if (this.mechToken != null) {
                final DerOutputStream derOutputStream3 = new DerOutputStream();
                derOutputStream3.putOctetString(this.mechToken);
                derOutputStream.write(DerValue.createTag((byte)(-128), true, (byte)2), derOutputStream3);
            }
            if (this.mechListMIC != null) {
                if (NegTokenInit.DEBUG) {
                    System.out.println("SpNegoToken NegTokenInit: sending MechListMIC");
                }
                final DerOutputStream derOutputStream4 = new DerOutputStream();
                derOutputStream4.putOctetString(this.mechListMIC);
                derOutputStream.write(DerValue.createTag((byte)(-128), true, (byte)3), derOutputStream4);
            }
            final DerOutputStream derOutputStream5 = new DerOutputStream();
            derOutputStream5.write((byte)48, derOutputStream);
            return derOutputStream5.toByteArray();
        }
        catch (final IOException ex) {
            throw new GSSException(10, -1, "Invalid SPNEGO NegTokenInit token : " + ex.getMessage());
        }
    }
    
    private void parseToken(final byte[] array) throws GSSException {
        try {
            final DerValue derValue = new DerValue(array);
            if (!derValue.isContextSpecific((byte)0)) {
                throw new IOException("SPNEGO NegoTokenInit : did not have right token type");
            }
            final DerValue derValue2 = derValue.data.getDerValue();
            if (derValue2.tag != 48) {
                throw new IOException("SPNEGO NegoTokenInit : did not have the Sequence tag");
            }
            int n = -1;
            while (derValue2.data.available() > 0) {
                final DerValue derValue3 = derValue2.data.getDerValue();
                if (derValue3.isContextSpecific((byte)0)) {
                    n = SpNegoToken.checkNextField(n, 0);
                    final DerInputStream data = derValue3.data;
                    this.mechTypes = data.toByteArray();
                    final DerValue[] sequence = data.getSequence(0);
                    this.mechTypeList = new Oid[sequence.length];
                    for (int i = 0; i < sequence.length; ++i) {
                        final ObjectIdentifier oid = sequence[i].getOID();
                        if (NegTokenInit.DEBUG) {
                            System.out.println("SpNegoToken NegTokenInit: reading Mechanism Oid = " + oid);
                        }
                        this.mechTypeList[i] = new Oid(oid.toString());
                    }
                }
                else if (derValue3.isContextSpecific((byte)1)) {
                    n = SpNegoToken.checkNextField(n, 1);
                }
                else if (derValue3.isContextSpecific((byte)2)) {
                    n = SpNegoToken.checkNextField(n, 2);
                    if (NegTokenInit.DEBUG) {
                        System.out.println("SpNegoToken NegTokenInit: reading Mech Token");
                    }
                    this.mechToken = derValue3.data.getOctetString();
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
                    if (!NegTokenInit.DEBUG) {
                        continue;
                    }
                    System.out.println("SpNegoToken NegTokenInit: MechListMIC Token = " + GSSToken.getHexBytes(this.mechListMIC));
                }
            }
        }
        catch (final IOException ex) {
            throw new GSSException(10, -1, "Invalid SPNEGO NegTokenInit token : " + ex.getMessage());
        }
    }
    
    byte[] getMechTypes() {
        return this.mechTypes;
    }
    
    public Oid[] getMechTypeList() {
        return this.mechTypeList;
    }
    
    BitArray getReqFlags() {
        return this.reqFlags;
    }
    
    public byte[] getMechToken() {
        return this.mechToken;
    }
    
    byte[] getMechListMIC() {
        return this.mechListMIC;
    }
}
